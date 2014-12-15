package com.moulliet.metro.load;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;

import org.opensextant.geodesy.Geodetic2DPoint;
import org.opensextant.giscore.DocumentType;
import org.opensextant.giscore.GISFactory;
import org.opensextant.giscore.events.ContainerStart;
import org.opensextant.giscore.events.IGISObject;
import org.opensextant.giscore.events.Feature;
import org.opensextant.giscore.events.SimpleField;
import org.opensextant.giscore.geometry.Geometry;
import org.opensextant.giscore.input.IGISInputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

public class LoadGdb {
    private static final Logger logger = LoggerFactory.getLogger(LoadGdb.class);

    public static void main(String[] args) {
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
                if (read instanceof ContainerStart) {
                    logger.info("container start " + read);
                } else if (read instanceof Feature) {
                    rows++;
                    BasicDBObject dbObject = new BasicDBObject();
                    Feature feature = (Feature) read;
                    dbObject.put("ALCHL_INVL", feature.getData(new SimpleField("ALCHL_INVLV_FLG")));
                    dbObject.put("TOT_PED_CN", feature.getData(new SimpleField("TOT_PED_CNT")));
                    dbObject.put("TOT_PEDCYCL", feature.getData(new SimpleField("TOT_PEDCYCL_CNT")));
                    dbObject.put("RD_SURF_CO", feature.getData(new SimpleField("RD_SURF_COND_CD")));
                    dbObject.put("LGT_COND_C", feature.getData(new SimpleField("LGT_COND_CD")));
                    dbObject.put("COLLIS_TYP", feature.getData(new SimpleField("COLLIS_TYP_CD")));
                    dbObject.put("CRASH_YR_N", feature.getData(new SimpleField("CRASH_YR_NO")));
                    BasicDBList coords = new BasicDBList();
                    Geodetic2DPoint center = feature.getGeometry().getCenter();
                    coords.add(center.getLongitudeAsDegrees());
                    coords.add(center.getLatitudeAsDegrees());
                    BasicDBObject loc = new BasicDBObject("type", "Point");
                    loc.put("coordinates", coords);
                    dbObject.put("loc", loc);

                    logger.info("row " + rows + " " + dbObject);
                } else {
                    //do nothing
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
