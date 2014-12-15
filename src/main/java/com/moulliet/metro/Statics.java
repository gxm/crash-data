package com.moulliet.metro;

import com.moulliet.metro.load.DataService;
import com.moulliet.metro.mongo.MongoDao;

public class Statics {
    public static final MongoDao mongoDao = new MongoDao(Config.getConfig().getString("database", "metro"));
    public static final DataService datasetService = new DataService();
}
N