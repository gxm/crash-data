package com.moulliet.metro.load;

import com.google.common.io.Files;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class GdbService {
    private static final Logger logger = LoggerFactory.getLogger(GdbService.class);

    public static void main(String[] args) throws IOException {
        unzip(new File("/Users/greg/code/import/TestCrash.gdb.zip"));
    }

    public static File unzip(File file) throws IOException {
        File tempDir = Files.createTempDir();
        logger.info("using temp dir " + tempDir);
        ZipFile zipFile = new ZipFile(file);
        Enumeration<? extends ZipEntry> entries = zipFile.entries();
        while (entries.hasMoreElements()) {
            ZipEntry zipEntry = entries.nextElement();
            File temp = new File(tempDir + "/" + zipEntry.getName());
            if (zipEntry.isDirectory()) {
                logger.info("created dir " + temp + " " + temp.mkdirs());
            } else {
                logger.debug("writing file " + temp + " " + temp.createNewFile());
                IOUtils.copy(zipFile.getInputStream(zipEntry), new FileOutputStream(temp));
            }
        }
        return tempDir;
    }
}
