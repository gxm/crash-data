package com.moulliet.metro;

import com.moulliet.metro.dataset.DatasetService;
import com.moulliet.metro.mongo.MongoDao;

public class Statics {
    public static final MongoDao mongoDao = new MongoDao("crashes");
    public static final DatasetService datasetService = new DatasetService();
}
