package com.moulliet.metro;

import com.moulliet.metro.mongo.MongoDao;

public class Statics {
    public static final MongoDao mongoDao = new MongoDao("crashes");
}
