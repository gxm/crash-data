package com.moulliet.metro.mongo;

import com.mongodb.DBObject;

import java.util.Iterator;

public interface MongoQueryCallback {
    void callback(Iterator<DBObject> dbObjectIterator);
}
