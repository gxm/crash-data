package com.moulliet.metro.mongo;

import com.mongodb.DBObject;

/**
 *
 */
public interface MongoDao {

    void query(String name, DBObject query, MongoQueryCallback callback);

}
