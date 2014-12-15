package com.moulliet.metro.load;

import com.mongodb.BasicDBObject;
import com.moulliet.metro.mongo.MongoDao;

import java.util.Date;

public class DatasetLoad {
    public static void main(String[] args) {
        MongoDao mongoDao = new MongoDao("crashes");
        insert(mongoDao, "Crashes_2013", true);
        insert(mongoDao, "TestCrash", false);
    }

    private static void insert(MongoDao mongoDao, String name, boolean active) {
        BasicDBObject dbObject = new BasicDBObject();
        dbObject.append("name", name)
                .append("uploaded", new Date())
                .append("active", active);

        mongoDao.insert(dbObject, "datasets");
    }
}
