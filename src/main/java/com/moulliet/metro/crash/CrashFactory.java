package com.moulliet.metro.crash;

import com.moulliet.common.Config;
import com.moulliet.metro.mongo.MongoDao;
import com.moulliet.metro.mongo.MongoDaoImpl;

/**
 *
 */
public class CrashFactory {

    private static MongoDao mongoDao;
    private static Config config;

    public static void setMongoDao(MongoDao mongoDao) {
        CrashFactory.mongoDao = mongoDao;
    }

    public static MongoDao getMongoDao() {
        if (null == mongoDao) {
            mongoDao = new MongoDaoImpl();
        }
        return mongoDao;
    }

    public static Config getConfig() {
        if (config == null) {
            config = new Config();
        }
        return config;
    }

    public static void reset() {
        mongoDao = null;
        config = null;
    }
}
