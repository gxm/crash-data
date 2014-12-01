package com.moulliet.metro.mongo;

import com.mongodb.DBObject;

public interface MongoDao {

    void query(DBObject query, MongoQueryCallback callback);

}
