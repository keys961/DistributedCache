package org.yejt.cacheservice.utils;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

public class TimestampUtilsTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(TimestampUtilsTest.class);

    private File timestampFile;

    @Before
    public void init() {
        timestampFile = TimestampUtils.getTimestampFile();
    }

    @After
    public void clear() throws IOException {
        FileUtils.forceDelete(timestampFile);
    }


    @Test
    public void testGetTimestamp() throws IOException {
        for (long i = 0; i < 10; i++) {
            long timestamp = TimestampUtils.getTimestamp();
            LOGGER.info("Timestamp: {}.", timestamp);
            Assert.assertEquals(i, timestamp);
        }
    }

}
