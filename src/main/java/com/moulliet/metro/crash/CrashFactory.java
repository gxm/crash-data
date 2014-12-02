package com.moulliet.metro.crash;

import com.moulliet.metro.Config;
import com.moulliet.metro.mongo.MongoDao;
import com.moulliet.metro.mongo.MongoDaoImpl;
import org.apache.commons.configuration.Configuration;

public class CrashFactory {

    private static MongoDao mongoDao;

    public static void setMongoDao(MongoDao mongoDao) {
        CrashFactory.mongoDao = mongoDao;
    }

    public static MongoDao getMongoDao() {
        if (null == mongoDao) {
            Configuration config = Config.getConfig();
            mongoDao = new MongoDaoImpl(config.getString("database"), config.getString("collection"));
        }
        return mongoDao;
    }

    public static void reset() {
        mongoDao = null;
    }
}
