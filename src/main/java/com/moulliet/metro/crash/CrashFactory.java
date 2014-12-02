package com.moulliet.metro.crash;

import com.moulliet.metro.mongo.MongoDao;
import com.moulliet.metro.mongo.MongoDaoImpl;

public class CrashFactory {

    private static MongoDao mongoDao;

    public static void setMongoDao(MongoDao mongoDao) {
        CrashFactory.mongoDao = mongoDao;
    }

    public static MongoDao getMongoDao() {
        if (null == mongoDao) {
            mongoDao = new MongoDaoImpl("metro", "crashes");
        }
        return mongoDao;
    }

    public static void reset() {
        mongoDao = null;
    }
}
