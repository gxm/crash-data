package com.moulliet.metro.load;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.util.JSON;
import com.moulliet.metro.Statics;
import com.moulliet.metro.mongo.MongoQueryCallback;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.ObjectNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

public class DataService {
    private static final Logger logger = LoggerFactory.getLogger(DataService.class);
    private ObjectMapper mapper = new ObjectMapper();

    public ArrayNode getAll() {
        ArrayNode list = mapper.createArrayNode();
        Statics.mongoDao.query("datasets", null, new MongoQueryCallback() {
            @Override
            public void callback(Iterator<DBObject> iterator) {
                while (iterator.hasNext()) {
                    DBObject next = iterator.next();
                    ObjectNode node = mapper.createObjectNode();
                    node.put("name", (String) next.get("name"));
                    node.put("active", (boolean) next.get("active"));
                    node.put("count", (int) next.get("count"));
                    node.put("uploaded", next.get("uploaded").toString());
                    list.add(node);
                }
            }
        });
        return list;
    }

    public List<String> getActiveNames() {
        List<String> datasets = new ArrayList<>();
        Statics.mongoDao.query("datasets", new BasicDBObject("active", true), new MongoQueryCallback() {
            @Override
            public void callback(Iterator<DBObject> iterator) {
                while (iterator.hasNext()) {
                    DBObject next = iterator.next();
                    datasets.add(next.get("name").toString());
                }
            }
        });
        return datasets;
    }

    public void update(String json) {
        DBObject update = (DBObject) JSON.parse(json);
        logger.info("updating {}", update);
        getCollection().update(new BasicDBObject("name", update.get("name")), update);
    }

    public void delete(String name) {
        getCollection().remove(new BasicDBObject("name", name));
    }

    private DBCollection getCollection() {
        return Statics.mongoDao.getDb().getCollection("datasets");
    }

    public void insert(String name, int count) {
        BasicDBObject insert = new BasicDBObject("name", name)
                .append("uploaded", new Date())
                .append("active", false)
                .append("count", count);
        logger.info("inserting {}", insert);
        getCollection().insert(insert);
    }
}
