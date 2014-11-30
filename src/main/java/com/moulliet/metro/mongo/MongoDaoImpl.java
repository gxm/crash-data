package com.moulliet.metro.mongo;

import com.mongodb.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MongoDaoImpl implements MongoDao {

    private static final Logger logger = LoggerFactory.getLogger(MongoDaoImpl.class);
    private MongoClient mongoClient = null;
    private final String database;
    private final String collection;

    public MongoDaoImpl(String database, String collection) {
        this.database = database;
        this.collection = collection;
        try {
            //todo - gfm - this presumes running Mongo on the local machine on the default port.
            mongoClient = new MongoClient();
        } catch (Exception e) {
            logger.error("unable to create MongoClient", e);
            throw new RuntimeException(e);
        }
    }

    public void query(DBObject query, MongoQueryCallback callback) {
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

    public void deleteCollection() {
        DB db = mongoClient.getDB(database);
        db.getCollection(collection).drop();
    }

}
