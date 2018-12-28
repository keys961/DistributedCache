package org.yejt.cacheservice.utils;

import com.netflix.appinfo.InstanceInfo;
import org.apache.commons.io.FileUtils;

import java.io.*;

/**
 * @author keys961
 */
public class TimestampUtils {

    public static long getTimestamp(InstanceInfo instanceInfo) throws IOException {
        File timestampFile = getTimestampFile(instanceInfo);
        if (!timestampFile.exists()) {
            if (timestampFile.createNewFile()) {
                return writeTimestamp(timestampFile, 0);
            }
            throw new IOException("Record timestamp file failed.");
        }
        BufferedReader reader = new BufferedReader(new FileReader(timestampFile));
        long timestamp = Long.parseLong(reader.readLine());
        reader.close();

        FileUtils.forceDelete(timestampFile);
        if (timestampFile.createNewFile()) {
            return writeTimestamp(timestampFile, ++timestamp);
        }

        throw new IOException("Record timestamp file failed.");
    }

    public static File getTimestampFile(InstanceInfo info) {
        if (info == null) {
            return new File("timestamp.file");
        }
        return new File(info.getId());
    }

    private static long writeTimestamp(File timestampFile, long timestamp) throws IOException {
        PrintWriter writer = new PrintWriter(timestampFile);
        writer.print(timestamp);
        writer.flush();
        writer.close();

        return timestamp;
    }
}
