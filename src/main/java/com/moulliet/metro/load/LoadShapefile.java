package com.moulliet.metro.load;

import com.mongodb.*;
import com.mongodb.util.JSON;
import com.moulliet.metro.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class LoadShapefile {

    private static final Logger logger = LoggerFactory.getLogger(LoadShapefile.class);

    public static void main(String[] args) throws IOException {
        String file = Config.getConfig().getString("data.file");
        String database = Config.getConfig().getString("database");
        String collection = Config.getConfig().getString("collection");
        logger.info("loading items from {} into {} {} ", file, database, collection);
        int count = load(file, database, collection);
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
            DBObject dbObject = mapFields((DBObject) JSON.parse(line));
            if (dbObject != null) {
                crashes.save(dbObject);
            }
            count++;
            line = br.readLine();
        }
        br.close();
        return count;
    }

    public static BasicDBObject mapFields(DBObject dbObject) {
        Object sink = dbObject.get("Sink");
        if (sink != null && (int) sink == 1) {
            return null;
        }
        BasicDBObject object = new BasicDBObject();
        object.put("injury", dbObject.get("TOT_INJ_CN"));
        object.put("fatality", dbObject.get("TOT_FATAL_"));
        object.put("alcohol", (int) dbObject.get("ALCHL_INVL") > 0);
        object.put("ped", dbObject.get("TOT_PED_CN"));
        object.put("bike", dbObject.get("TOT_PEDCYC"));
        object.put("surface", Integer.parseInt((String) dbObject.get("RD_SURF_CO")));
        object.put("light", Integer.parseInt((String) dbObject.get("LGT_COND_C")));
        object.put("type", dbObject.get("COLLIS_TYP"));
        object.put("year", Integer.parseInt((String) dbObject.get("CRASH_YR_N")));
        object.put("loc", dbObject.get("loc"));
        return object;
    }
}
