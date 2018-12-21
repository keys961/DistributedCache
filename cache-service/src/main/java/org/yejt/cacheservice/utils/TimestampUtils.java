package org.yejt.cacheservice.utils;

import org.apache.commons.io.FileUtils;

import java.io.*;

public class TimestampUtils
{
    private static final File TIMESTAMP_FILE = new File("timestamp.file");

    public static long getTimestamp() throws IOException
    {
        if(!TIMESTAMP_FILE.exists())
        {
            if(TIMESTAMP_FILE.createNewFile())
                return writeTimestamp(0);
            throw new IOException("Record timestamp file failed.");
        }
        BufferedReader reader = new BufferedReader(new FileReader(TIMESTAMP_FILE));
        long timestamp = Long.parseLong(reader.readLine());
        reader.close();

        FileUtils.forceDelete(TIMESTAMP_FILE);
        if(TIMESTAMP_FILE.createNewFile())
            return writeTimestamp(++timestamp);

        throw new IOException("Record timestamp file failed.");
    }

    public static File getTimestampFile()
    {
        return TIMESTAMP_FILE;
    }

    private static long writeTimestamp(long timestamp) throws IOException
    {
        PrintWriter writer = new PrintWriter(TIMESTAMP_FILE);
        writer.print(timestamp);
        writer.flush();
        writer.close();

        return timestamp;
    }
}
