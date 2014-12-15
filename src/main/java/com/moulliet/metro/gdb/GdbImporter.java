package com.moulliet.metro.gdb;

import org.opensextant.giscore.DocumentType;
import org.opensextant.giscore.GISFactory;
import org.opensextant.giscore.events.Feature;
import org.opensextant.giscore.events.IGISObject;
import org.opensextant.giscore.events.Row;
import org.opensextant.giscore.input.IGISInputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

public class GdbImporter {
    private static final Logger logger = LoggerFactory.getLogger(GdbImporter.class);

    public static void main(String[] args) {

        /**
         * https://github.com/OpenSextant/giscore
         * This works using the un-zipped gdb folder.
         * The filegdb lib is the default
         * The ESRI File Geodatabase API is on DYLD_LIBRARY_PATH
         * export DYLD_LIBRARY_PATH=/Users/greg/code/FileGDB_API/lib
         * http://www.esri.com/apps/products/download/#File_Geodatabase_API_1.3
         */
        //String file = "/Users/greg/code/metro/OregonMetro_Crashes_2013.gdb";
        String file = "/Users/greg/code/metro/TestCrash.gdb";

        IGISInputStream stream = null;
        try {
            stream = GISFactory.getInputStream(DocumentType.FileGDB, new File(file));
            logger.info("result is " + stream);
            IGISObject read = stream.read();
            int objects = 0;
            int rows = 0;
            while (read != null) {
                objects++;
                if (read instanceof Row) {
                    rows++;
                    logger.info("row " + rows + " " + read);
                } else {
                    logger.info("other " + read.getClass() + " " + read);
                }
                read = stream.read();
            }
            logger.info("end: objects: " + objects + " rows: " + rows);
        } catch (IOException e) {
            logger.warn("unable to parse " + file, e);
        } finally {
            if (stream != null) {
                stream.close();
            }
        }

    }
}