package com.moulliet.metro.load;

import com.mongodb.*;
import com.mongodb.util.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class LoadShapefile {

    private static final Logger logger = LoggerFactory.getLogger(LoadShapefile.class);

    public static void main(String[] args) throws IOException {
        logger.info("starting load");
        String file = "/Users/greg/code/import/Crashes_07_11.out";
        int count = load(file, "metro", "crashes");
        logger.info("completed load of {} items", count);
    }

    public static int load(String file, String database, String collection) throws IOException {
        MongoClient mongo = new MongoClient();
        DB metro = mongo.getDB(database);
        DBCollection crashes = metro.getCollection(collection);
        DBObject index2d = BasicDBObjectBuilder.start("loc", "2dsphere").get();
        crashes.createIndex(index2d);
        int count = 0;
        BufferedReader br = new BufferedReader(new FileReader(file));
        String line = br.readLine();
        while (line != null) {
            DBObject dbObject = (DBObject) JSON.parse(line);
            mapFields(dbObject);
            crashes.save(dbObject);
            count++;
            line = br.readLine();
        }
        br.close();
        return count;
    }

    public static void mapFields(DBObject dbObject) {
        //OBJECTID=106947, CRASH_SVRT="2", CRASH_SV_1="FAT", CRASH_SV_T="FATAL", TOT_FATAL_=1, TOT_INJ_LV=0, TOT_INJ__1=0, TOT_INJ__2=0, TOT_INJ_CN=0,
        //OBJECTID=106953, CRASH_SVRT="4", CRASH_SV_1="INJ", CRASH_SV_T="INJURY A", TOT_FATAL_=0, TOT_INJ_LV=1, TOT_INJ__1=0, TOT_INJ__2=0, TOT_INJ_CN=1,
        //OBJECTID=106951, CRASH_SVRT="4", CRASH_SV_1="INJ", CRASH_SV_T="INJURY B", TOT_FATAL_=0, TOT_INJ_LV=0, TOT_INJ__1=1, TOT_INJ__2=0, TOT_INJ_CN=1,
        //OBJECTID=106952, CRASH_SVRT="4", CRASH_SV_1="INJ", CRASH_SV_T="INJURY C", TOT_FATAL_=0, TOT_INJ_LV=0, TOT_INJ__1=0, TOT_INJ__2=2, TOT_INJ_CN=2,
        dbObject.put("injury", dbObject.get("TOT_INJ_CN"));
        dbObject.put("fatality", dbObject.get("TOT_FATAL_"));
        dbObject.put("alcohol", (int) dbObject.get("ALCHL_INVL") > 0);
        dbObject.put("ped", dbObject.get("TOT_PED_CN"));
        dbObject.put("bike", dbObject.get("TOT_PEDCYC"));
        //"RD_SURF_CO": string 0, 1 = dry, 2 wet, 3 snow, 4 ice
        dbObject.put("surface", Integer.parseInt((String) dbObject.get("RD_SURF_CO")));


    }
}
