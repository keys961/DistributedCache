package org.yejt.cacheservice.store.container;

import java.io.Serializable;
import java.util.*;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.locks.ReentrantLock;

public class ConcurrentLRUHashMap<K, V> extends AbstractMap<K, V>
        implements ConcurrentMap<K, V>, Serializable
{
    private static final int DEFAULT_INITIAL_CAPACITY = 16;

    private static final int DEFAULT_CONCURRENCY_LEVEL = 512;

    private static final int MAX_SEGMENTS = 1 << 16;

    private static final int RETRIES_BEFORE_LOCK = 2;

    private static final long serialVersionUID = -71218921488561397L;

    private final int maxSize;

    private final int segmentMask;

    private final int segmentShift;

    private final Segment<K, V>[] segments;

    private transient Set<Map.Entry<K, V>> entrySet;

    public ConcurrentLRUHashMap()
    {
        this(DEFAULT_INITIAL_CAPACITY, DEFAULT_CONCURRENCY_LEVEL);
    }

    public ConcurrentLRUHashMap(int maxSize, int concurrencyLevel)
    {
        if (maxSize < 0 || concurrencyLevel <= 0)
            throw new IllegalArgumentException();

        if (concurrencyLevel > MAX_SEGMENTS)
            concurrencyLevel = MAX_SEGMENTS;

        int sshift = 0;
        int ssize = 1;
        while (ssize < concurrencyLevel)
        {
            ++sshift;
            ssize <<= 1;
        }
        segmentShift = 32 - sshift;
        segmentMask = ssize - 1;
        this.segments = newArray(ssize);

        int c = maxSize / ssize;
        this.maxSize = c;
        if (c * ssize < this.maxSize)
            ++c;
        int cap = 1;
        while (cap < c)
            cap <<= 1;

        for (int i = 0; i < this.segments.length; ++i)
            this.segments[i] = new Segment<K, V>(cap);

    }

    public ConcurrentLRUHashMap(int maxSize)
    {
        this(maxSize, DEFAULT_CONCURRENCY_LEVEL);
    }

    private static int hash(int h)
    {
        h += (h << 15) ^ 0xffffcd7d;
        h ^= (h >>> 10);
        h += (h << 3);
        h ^= (h >>> 6);
        h += (h << 2) + (h << 14);
        return h ^ (h >>> 16);
    }

    @SuppressWarnings({"unchecked", "hiding"})
    private Segment<K, V>[] newArray(int i)
    {
        return new Segment[i];
    }

    private Segment<K, V> segmentFor(int hash)
    {
        return segments[(hash >>> segmentShift) & segmentMask];
    }

    public boolean isEmpty()
    {
        final Segment<K, V>[] segments = this.segments;

        int[] mc = new int[segments.length];
        int mcsum = 0;
        for (int i = 0; i < segments.length; ++i)
        {
            if (segments[i].count != 0)
                return false;
            else mcsum += mc[i] = segments[i].modCount;
        }

        if (mcsum != 0)
            for (int i = 0; i < segments.length; ++i)
                if (segments[i].count != 0 || mc[i] != segments[i].modCount)
                    return false;
        return true;
    }

    public int size()
    {
        final Segment<K, V>[] segments = this.segments;
        long sum = 0;
        long check = 0;
        int[] mc = new int[segments.length];

        for (int k = 0; k < RETRIES_BEFORE_LOCK; ++k)
        {
            check = 0;
            sum = 0;
            int mcsum = 0;
            for (int i = 0; i < segments.length; ++i)
            {
                sum += segments[i].count;
                mcsum += mc[i] = segments[i].modCount;
            }
            if (mcsum != 0)
            {
                for (int i = 0; i < segments.length; ++i)
                {
                    check += segments[i].count;
                    if (mc[i] != segments[i].modCount)
                    {
                        check = -1; // force retry
                        break;
                    }
                }
            }
            if (check == sum)
                break;
        }

        if (check != sum)
        {
            // Resort to locking all segments
            sum = 0;
            for (Segment<K, V> segment : segments) segment.lock();
            for (Segment<K, V> segment : segments) sum += segment.count;
            for (Segment<K, V> segment : segments) segment.unlock();
        }
        if (sum > Integer.MAX_VALUE)
            return Integer.MAX_VALUE;
        else
            return (int) sum;
    }

    public V get(Object key)
    {
        int hash = hash(key.hashCode());
        return segmentFor(hash).get(key, hash);
    }

    public boolean containsKey(Object key)
    {
        int hash = hash(key.hashCode());
        return segmentFor(hash).containsKey(key, hash);
    }


    @SuppressWarnings("unused")
    public boolean containsValue(Object value)
    {
        if (value == null) throw new NullPointerException();
        final Segment<K, V>[] segments = this.segments;
        int[] mc = new int[segments.length];

        for (int k = 0; k < RETRIES_BEFORE_LOCK; ++k)
        {
            int sum = 0;
            int mcsum = 0;
            for (int i = 0; i < segments.length; ++i)
            {
                int c = segments[i].count;
                mcsum += mc[i] = segments[i].modCount;
                if (segments[i].containsValue(value))
                    return true;
            }
            boolean cleanSweep = true;
            if (mcsum != 0)
            {
                for (int i = 0; i < segments.length; ++i)
                {
                    int c = segments[i].count;
                    if (mc[i] != segments[i].modCount)
                    {
                        cleanSweep = false;
                        break;
                    }
                }
            }
            if (cleanSweep) return false;
        }

        for (Segment<K, V> segment1 : segments)
            segment1.lock();
        boolean found = false;
        try
        {
            for (Segment<K, V> segment : segments)
            {
                if (segment.containsValue(value))
                {
                    found = true;
                    break;
                }
            }
        }
        finally
        {
            for (Segment<K, V> segment : segments)
                segment.unlock();
        }
        return found;
    }

    public V put(K key, V value)
    {
        if (value == null) throw new NullPointerException();
        int hash = hash(key.hashCode());
        return segmentFor(hash).put(key, hash, value, false);
    }

    public V putIfAbsent(K key, V value)
    {
        if (value == null)
            throw new NullPointerException();
        int hash = hash(key.hashCode());
        return segmentFor(hash).put(key, hash, value, true);
    }

    public void putAll(Map<? extends K, ? extends V> m)
    {
        for (Map.Entry<? extends K, ? extends V> e : m.entrySet())
            put(e.getKey(), e.getValue());
    }

    public V remove(Object key)
    {
        int hash = hash(key.hashCode());
        return segmentFor(hash).remove(key, hash, null);
    }


    public boolean remove(Object key, Object value)
    {
        int hash = hash(key.hashCode());
        if (value == null)
            return false;
        return segmentFor(hash).remove(key, hash, value) != null;
    }


    public boolean replace(K key, V oldValue, V newValue)
    {
        if (oldValue == null || newValue == null)
            throw new NullPointerException();
        int hash = hash(key.hashCode());
        return segmentFor(hash).replace(key, hash, oldValue, newValue);
    }

    public V replace(K key, V value)
    {
        if (value == null)
            throw new NullPointerException();
        int hash = hash(key.hashCode());
        return segmentFor(hash).replace(key, hash, value);
    }

    public void clear()
    {
        for (Segment<K, V> segment : segments)
            segment.clear();
    }

    @Override
    public Set<Entry<K, V>> entrySet()
    {
        Set<Map.Entry<K, V>> es = entrySet;
        return (es != null) ?
                es : (entrySet = new EntrySet());
    }


    static final class HashEntry<K, V>
    {
        final K key;

        final int hash;

        final HashEntry<K, V> next;

        volatile V value;

        HashEntry<K, V> before;

        HashEntry<K, V> after;

        HashEntry(K key, int hash, HashEntry<K, V> next, V value)
        {
            this.key = key;
            this.hash = hash;
            this.next = next;
            this.value = value;
        }

        @SuppressWarnings("unchecked")
        static <K, V> HashEntry<K, V>[] newArray(int i)
        {
            return new HashEntry[i];
        }

        void remove()
        {
            before.after = after;
            after.before = before;
            before = after = null;
        }


        void addBefore(HashEntry<K, V> entry)
        {
            before = entry.before;
            after = entry;
            before.after = this;
            after.before = this;
        }

        void reuseLRU(HashEntry<K, V> entry)
        {
            entry.before.after = this;
            entry.after.before = this;
            before = entry.before;
            after = entry.after;
            entry.before = null;
            entry.after = null;
        }
    }


    @SuppressWarnings("hiding")
    final class Segment<K, V> extends ReentrantLock implements Serializable
    {
        private static final long serialVersionUID = 7568413189932886369L;

        transient final HashEntry<K, V> header;

        transient volatile int count;

        transient int modCount;

        transient volatile HashEntry<K, V>[] table;

        Segment(int initialCapacity)
        {
            setTable(HashEntry.newArray(initialCapacity));
            header = new HashEntry<>(null, -1, null, null);
            header.before = header.after = header;
        }

        void setTable(HashEntry<K, V>[] newTable)
        {
            table = newTable;
        }

        HashEntry<K, V> getFirst(int hash)
        {
            HashEntry<K, V>[] tab = table;
            return tab[hash & (tab.length - 1)];
        }

        V readValueUnderLock(HashEntry<K, V> e)
        {
            lock();
            try
            {
                e.remove();
                e.addBefore(header);
                return e.value;
            }
            finally
            {
                unlock();
            }
        }

        V get(Object key, int hash)
        {
            if (count != 0)
            {
                // read-volatile
                HashEntry<K, V> e = getFirst(hash);
                while (e != null)
                {
                    if (e.hash == hash && key.equals(e.key))
                    {
                        V v = e.value;
                        if (v != null)
                            return readValueUnderLock(e); // recheck
                        return null;
                    }
                    e = e.next;
                }
            }
            return null;
        }

        boolean containsKey(Object key, int hash)
        {
            if (count != 0)
            {
                // read-volatile
                HashEntry<K, V> e = getFirst(hash);
                while (e != null)
                {
                    if (e.hash == hash && key.equals(e.key)) return true;
                    e = e.next;
                }
            }
            return false;
        }

        boolean containsValue(Object value)
        {
            if (count != 0)
            {
                // read-volatile
                HashEntry<K, V>[] tab = table;
                int len = tab.length;
                for (HashEntry<K, V> aTab : tab)
                {
                    for (HashEntry<K, V> e = aTab; e != null; e = e.next)
                    {
                        V v = e.value;
                        if (v == null) // recheck
                            v = readValueUnderLock(e);
                        if (value.equals(v))
                            return true;
                    }
                }
            }
            return false;
        }

        boolean replace(K key, int hash, V oldValue, V newValue)
        {
            lock();
            try
            {
                HashEntry<K, V> e = getFirst(hash);
                while (e != null && (e.hash != hash || !key.equals(e.key)))
                    e = e.next;

                boolean replaced = false;
                if (e != null && oldValue.equals(e.value))
                {
                    replaced = true;
                    e.value = newValue;
                    e.remove();
                    e.addBefore(header);
                }
                return replaced;
            }
            finally
            {
                unlock();
            }
        }


        V replace(K key, int hash, V newValue)
        {
            lock();
            try
            {
                HashEntry<K, V> e = getFirst(hash);
                while (e != null && (e.hash != hash || !key.equals(e.key)))
                    e = e.next;

                V oldValue = null;
                if (e != null)
                {
                    oldValue = e.value;
                    e.value = newValue;
                    e.remove();
                    e.addBefore(header);
                }
                return oldValue;
            }
            finally
            {
                unlock();
            }
        }

        V put(K key, int hash, V value, boolean onlyIfAbsent)
        {
            return addEntry(key, hash, value, onlyIfAbsent);
        }

        V addEntry(K key, int hash, V value, boolean onlyIfAbsent)
        {
            lock();
            try
            {
                checkFull();
                int c = count;
                c++;
                HashEntry<K, V>[] tab = table;
                int index = hash & (tab.length - 1);
                HashEntry<K, V> first = tab[index];
                HashEntry<K, V> e = first;
                while (e != null && (e.hash != hash || !key.equals(e.key)))
                    e = e.next;

                V oldValue;
                if (e != null)
                {
                    oldValue = e.value;
                    if (!onlyIfAbsent)
                        e.value = value;
                    e.remove();
                    e.addBefore(header);
                }
                else
                {
                    oldValue = null;
                    ++modCount;
                    e = tab[index] = new HashEntry<>(key, hash, first, value);
                    e.addBefore(header);
                    count = c; // write-volatile
                }
                return oldValue;
            }
            finally
            {
                unlock();
            }
        }


        void checkFull()
        {
            if (isFull())
            {
                HashEntry<K, V> removal = header.after;
                remove(removal.key, removal.hash, removal.value);

                removal = null;
            }
        }


        boolean isFull()
        {
            return count >= maxSize;
        }

        V remove(Object key, int hash, Object value)
        {
            lock();
            try
            {
                int c = count;
                c--;
                HashEntry<K, V>[] tab = table;
                int index = hash & (tab.length - 1);
                HashEntry<K, V> first = tab[index];
                HashEntry<K, V> e = first;
                while (e != null && (e.hash != hash || !key.equals(e.key)))
                    e = e.next;


                V oldValue = null;
                if (e != null)
                {
                    V v = e.value;
                    if (value == null || value.equals(v))
                    {
                        oldValue = v;

                        ++modCount;
                        HashEntry<K, V> newFirst = e.next;
                        for (HashEntry<K, V> p = first; p != e; p = p.next)
                        {
                            newFirst = new HashEntry<>(p.key, p.hash, newFirst, p.value);
                            newFirst.reuseLRU(p);
                        }
                        tab[index] = newFirst;
                        count = c; // write-volatile
                        e.remove();
                    }
                }

                return oldValue;
            }
            finally
            {
                unlock();
            }
        }

        void clear()
        {
            if (count != 0)
            {
                lock();
                try
                {
                    HashEntry<K, V>[] tab = table;
                    for (int i = 0; i < tab.length; i++)
                        tab[i] = null;
                    ++modCount;
                    header.before = header.after = header;
                    count = 0; // write-volatile
                }
                finally
                {
                    unlock();
                }
            }
        }
    }

    public class EntryIterator implements Iterator<Entry<K, V>>
    {
        int nextSegmentIndex;

        int nextTableIndex;

        HashEntry<K, V>[] currentTable;

        HashEntry<K, V> nextEntry;

        HashEntry<K, V> lastReturned;

        EntryIterator()
        {
            nextSegmentIndex = segments.length - 1;
            nextTableIndex = -1;
            advance();
        }

        public boolean hasMoreElements()
        {
            return hasNext();
        }

        final void advance()
        {
            if (nextEntry != null && (nextEntry = nextEntry.next) != null)
                return;

            while (nextTableIndex >= 0)
            {
                if ((nextEntry = currentTable[nextTableIndex--]) != null)
                    return;
            }

            while (nextSegmentIndex >= 0)
            {
                Segment<K, V> seg = segments[nextSegmentIndex--];
                if (seg.count != 0)
                {
                    currentTable = seg.table;
                    for (int j = currentTable.length - 1; j >= 0; --j)
                    {
                        if ((nextEntry = currentTable[j]) != null)
                        {
                            nextTableIndex = j - 1;
                            return;
                        }
                    }
                }
            }
        }

        public boolean hasNext()
        {
            return nextEntry != null;
        }

        HashEntry<K, V> nextEntry()
        {
            if (nextEntry == null)
                throw new NoSuchElementException();
            lastReturned = nextEntry;
            advance();
            return lastReturned;
        }

        public void remove()
        {
            if (lastReturned == null)
                throw new IllegalStateException();
            ConcurrentLRUHashMap.this.remove(lastReturned.key);
            lastReturned = null;
        }

        public Map.Entry<K, V> next()
        {
            HashEntry<K, V> e = nextEntry();
            return new WriteThroughEntry(e.key, e.value);
        }
    }

    @SuppressWarnings("serial")
    final class WriteThroughEntry extends AbstractMap.SimpleEntry<K, V>
    {

        WriteThroughEntry(K k, V v)
        {
            super(k, v);
        }

        public V setValue(V value)
        {
            if (value == null)
                throw new NullPointerException();
            V v = super.setValue(value);
            ConcurrentLRUHashMap.this.put(getKey(), value);
            return v;
        }
    }

    final class EntrySet extends AbstractSet<Entry<K, V>>
    {

        public Iterator<Map.Entry<K, V>> iterator()
        {
            return new EntryIterator();
        }

        public boolean contains(Object o)
        {
            if (!(o instanceof Map.Entry))
                return false;
            Map.Entry<?, ?> e = (Map.Entry<?, ?>) o;
            V v = ConcurrentLRUHashMap.this.get(e.getKey());
            return v != null && v.equals(e.getValue());
        }

        public boolean remove(Object o)
        {
            if (!(o instanceof Map.Entry))
                return false;
            Map.Entry<?, ?> e = (Map.Entry<?, ?>) o;
            return ConcurrentLRUHashMap.this.remove(e.getKey(), e.getValue());
        }

        public int size()
        {
            return ConcurrentLRUHashMap.this.size();
        }

        public void clear()
        {
            ConcurrentLRUHashMap.this.clear();
        }
    }
}

