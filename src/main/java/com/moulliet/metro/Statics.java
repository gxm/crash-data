package com.moulliet.metro;

import com.moulliet.metro.load.DataService;
import com.moulliet.metro.mongo.MongoDao;

public class Statics {
    public static final MongoDao mongoDao = new MongoDao("crashes");
    public static final DataService datasetService = new DataService();
}
