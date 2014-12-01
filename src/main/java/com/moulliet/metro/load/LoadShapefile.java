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
        dbObject.put("injury", dbObject.get("TOT_INJ_CN"));
        dbObject.put("fatality", dbObject.get("TOT_FATAL_"));
        dbObject.put("alcohol", (int) dbObject.get("ALCHL_INVL") > 0);
        dbObject.put("ped", dbObject.get("TOT_PED_CN"));
        dbObject.put("bike", dbObject.get("TOT_PEDCYC"));
        dbObject.put("surface", Integer.parseInt((String) dbObject.get("RD_SURF_CO")));
        dbObject.put("light", Integer.parseInt((String) dbObject.get("LGT_COND_C")));
        dbObject.put("type", dbObject.get("COLLIS_TYP"));
        dbObject.put("year", Integer.parseInt((String) dbObject.get("CRASH_YR_N")));
    }
}
