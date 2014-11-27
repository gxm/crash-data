package com.moulliet.metro.mongo;

import com.mongodb.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MongoDaoImpl implements MongoDao {

    private static final Logger logger = LoggerFactory.getLogger(MongoDaoImpl.class);
    private MongoClient mongoClient = null;

    public MongoDaoImpl() {
        try {
            //todo - gfm - this presumes running Mongo on the local machine on the default port.
            mongoClient = new MongoClient();
        } catch (Exception e) {
            logger.error("unable to create MongoClient", e);
            throw new RuntimeException(e);
        }
    }

    private DBCollection getCollection(String name) {
        //todo - gfm - pull db name out as a config parameter
        DB db = mongoClient.getDB("metro");
        return db.getCollection(name);
    }

    public void query(String name, DBObject query, MongoQueryCallback callback) {
        DBCursor cursor = null;
        try {
            cursor = getCollection(name).find(query);
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
