package com.moulliet.metro.load;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.moulliet.metro.Statics;
import com.moulliet.metro.crash.Crash;
import com.moulliet.metro.mongo.MongoDao;
import org.opensextant.geodesy.Geodetic2DPoint;
import org.opensextant.giscore.DocumentType;
import org.opensextant.giscore.GISFactory;
import org.opensextant.giscore.events.ContainerStart;
import org.opensextant.giscore.events.Feature;
import org.opensextant.giscore.events.IGISObject;
import org.opensextant.giscore.events.SimpleField;
import org.opensextant.giscore.input.IGISInputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

public class LoadGdb {
    private static final Logger logger = LoggerFactory.getLogger(LoadGdb.class);

    /**
     * https://github.com/OpenSextant/giscore
     * This works using the un-zipped gdb folder.
     * The filegdb lib is the default
     * The ESRI File Geodatabase API is on DYLD_LIBRARY_PATH
     * export DYLD_LIBRARY_PATH=/Users/greg/code/FileGDB_API/lib
     * http://www.esri.com/apps/products/download/#File_Geodatabase_API_1.3
     */
    public static void main(String[] args) {
        //String file = "/Users/greg/code/metro/OregonMetro_Crashes_09_11.gdb";

        /**
         * 2012 appears to contain \MetroCrashes_2007_2011, which has a different set of fields
         */
        //String file = "/Users/greg/code/metro/OregonMetro_Crashes_2012.gdb";
        String file = "/Users/greg/code/metro/OregonMetro_Crashes_2013.gdb";

        String collection = "Crashes_2013";
        MongoDao mongoDao = Statics.mongoDao;
        //String file = "/Users/greg/code/metro/TestCrash.gdb";
        IGISInputStream stream = null;
        try {
            stream = GISFactory.getInputStream(DocumentType.FileGDB, new File(file));
            logger.info("result is " + stream);
            IGISObject read = stream.read();
            int objects = 0;
            int rows = 0;
            while (read != null) {
                objects++;
                if (read instanceof ContainerStart) {
                    logger.info("container start " + read);
                } else if (read instanceof Feature) {
                    rows++;
                    BasicDBObject dbObject = new BasicDBObject();
                    Feature feature = (Feature) read;
                    for (String fieldName : Crash.fieldNamesLong) {
                        dbObject.put(fieldName, feature.getData(new SimpleField(fieldName)));
                    }

                    BasicDBList coordinates = new BasicDBList();
                    Geodetic2DPoint center = feature.getGeometry().getCenter();
                    coordinates.add(center.getLongitudeAsDegrees());
                    coordinates.add(center.getLatitudeAsDegrees());
                    BasicDBObject loc = new BasicDBObject("type", "Point");
                    loc.put("coordinates", coordinates);
                    dbObject.put("loc", loc);
                    mongoDao.insert(dbObject, collection);
                    if (feature.getData(new SimpleField("CRASH_YR_NO")) == null) {
                        logger.info("row " + rows + " null year " + feature);
                    }
                } else {
                    //do nothing
                }
                if (objects % 1000 == 0) {
                    logger.info("objects " + objects);
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
