package com.moulliet.metro.mongo;

import com.mongodb.DB;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MongoDao {

    private static final Logger logger = LoggerFactory.getLogger(MongoDao.class);
    private MongoClient mongoClient = null;
    private final String database;
    private DB db;

    public MongoDao(String database) {
        this.database = database;
        try {
            //todo - gfm - this presumes running Mongo on the local machine on the default port.
            mongoClient = new MongoClient();
        } catch (Exception e) {
            logger.error("unable to create MongoClient", e);
            throw new RuntimeException(e);
        }
        db = mongoClient.getDB(database);
    }

    public void insert(DBObject dbObject, String collection) {
        db.getCollection(collection).insert(dbObject);
    }

    public void query(String collection, DBObject query, MongoQueryCallback callback) {
        DBCursor cursor = null;
        try {
            DB db = mongoClient.getDB(database);
            cursor = db.getCollection(collection).find(query);
            cursor.batchSize(100);

            callback.callback(cursor);
        } catch (Exception e) {
            logger.warn("unable to crash " + query, e);
        } finally {
            if (null != cursor) {
                cursor.close();
            }
        }
    }


}