package com.moulliet.metro.load;

import com.google.common.io.Files;
import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.moulliet.metro.Statics;
import com.moulliet.metro.crash.Crash;
import com.moulliet.metro.mongo.MongoDao;
import org.apache.commons.io.IOUtils;
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
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class GdbService {
    private static final Logger logger = LoggerFactory.getLogger(GdbService.class);

    public static void main(String[] args) throws IOException {
        unzip(new File("/Users/greg/code/import/TestCrash.gdb.zip"));
    }

    public static int load(String datasetName, File file) throws IOException {
        File unzipTempDir = unzip(file);
        File[] files = unzipTempDir.listFiles();
        logger.info("found " + Arrays.toString(files));
        int records = 0;
        for (File found : files) {
            if (found.isDirectory() && found.getPath().endsWith(".gdb")) {
                records += importData(datasetName, found);
            }
        }
        return records;
    }

    static File unzip(File file) throws IOException {
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

    static int importData(String datasetName, File file) {
        logger.info("starting import {} {}", datasetName, file);
        MongoDao mongoDao = Statics.mongoDao;
        IGISInputStream stream = null;
        try {
            stream = GISFactory.getInputStream(DocumentType.FileGDB, file);
            IGISObject read = stream.read();
            int objects = 0;
            int inserted = 0;
            while (read != null) {
                objects++;
                if (read instanceof ContainerStart) {
                    logger.info("container start " + read);
                } else if (read instanceof Feature) {
                    BasicDBObject dbObject = new BasicDBObject();
                    Feature feature = (Feature) read;
                    for (String fieldName : Crash.fieldNames) {
                        dbObject.put(fieldName, feature.getData(new SimpleField(fieldName)));
                    }
                    dbObject.put("loc", parseLoc(feature));
                    Object year = feature.getData(new SimpleField("CRASH_YR_NO"));
                    if (year != null) {
                        mongoDao.insert(dbObject, datasetName);
                        inserted++;
                    } else {
                        logger.info(" null year " + feature);
                    }
                } else {
                    //do nothing
                }
                if (objects % 1000 == 0) {
                    logger.debug("objects " + objects + " inserted " + inserted);
                }
                read = stream.read();
            }
            logger.info("completed import {} {} {}", datasetName, file, inserted);
            return inserted;
        } catch (IOException e) {
            logger.warn("unable to parse " + file, e);
        } finally {
            if (stream != null) {
                stream.close();
            }
        }
        return 0;
    }

    private static BasicDBObject parseLoc(Feature feature) {
        BasicDBList coordinates = new BasicDBList();
        Geodetic2DPoint center = feature.getGeometry().getCenter();
        coordinates.add(center.getLongitudeAsDegrees());
        coordinates.add(center.getLatitudeAsDegrees());
        BasicDBObject loc = new BasicDBObject("type", "Point");
        loc.put("coordinates", coordinates);
        return loc;
    }
}
