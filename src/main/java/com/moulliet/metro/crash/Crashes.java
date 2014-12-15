package com.moulliet.metro.crash;

import com.mongodb.DBObject;
import com.moulliet.metro.filter.Filter;
import com.moulliet.metro.filter.SinkFilter;
import com.moulliet.metro.mongo.MongoDao;
import com.moulliet.metro.mongo.MongoQueryCallback;
import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicInteger;

public class Crashes {
    private static final Logger logger = LoggerFactory.getLogger(Crashes.class);
    private static List<Crash> allCrashes;

    private final Map<Point, AtomicInteger> pointMap = new TreeMap<>();
    private CrashTotals crashTotals = new CrashTotals();

    public int aggregatedCrashes(Filter filter, OutputStream stream, int radius) throws IOException {
        consolidatePoints(filter);
        int max = 0;
        JsonFactory jsonFactory = new JsonFactory();
        JsonGenerator json = jsonFactory.createJsonGenerator(stream);
        json.writeStartObject();
        json.writeArrayFieldStart("data");
        for (Map.Entry<Point, AtomicInteger> entry : pointMap.entrySet()) {
            Point key = entry.getKey();
            int value = entry.getValue().get();
            max = Math.max(max, value);
            json.writeStartObject();
            json.writeStringField("lat", key.getY());
            json.writeStringField("lng", key.getX());
            json.writeNumberField("count", value);
            json.writeNumberField("radius", radius);
            json.writeEndObject();
        }
        json.writeEndArray();
        json.writeNumberField("max", max);
        json.writeNumberField("total", crashTotals.getTotal());
        crashTotals.write(json);
        json.writeEndObject();
        json.flush();
        return pointMap.entrySet().size();
    }

    public static void loadAll() throws IOException {
        logger.info("loading sinks");
        SinkFilter.loadSinkPoints();
        logger.info("loading crashes");
        List<Crash> crashes = new ArrayList<>();
        //todo - gfm - get the collection name dynamically
        MongoDao mongoDao = new MongoDao("crashes");
        //MongoDao mongoDao = new MongoDao("crashes", "TestCrash");
        mongoDao.query("Crashes_2013", null, new MongoQueryCallback() {
            @Override
            public void callback(Iterator<DBObject> iterator) {
               while (iterator.hasNext()) {
                   Crash crash = new Crash(iterator.next());
                   if (!SinkFilter.isSink(crash.getPoint())) {
                       crashes.add(crash);
                   }

               }
            }
        });
        allCrashes = Collections.unmodifiableList(crashes);
        logger.info("loaded {} crashes", allCrashes.size());
    }

    private void consolidatePoints(Filter filter) {
        for (Crash crash : allCrashes) {
            if (filter.include(crash)) {
                addPointToMap(crash.getPoint());
                crashTotals.addCrash(crash);
            }
        }
    }

    private void addPointToMap(Point point) {
        AtomicInteger integer = pointMap.get(point);
        if (null == integer) {
            pointMap.put(point, new AtomicInteger(1));
        } else {
            integer.incrementAndGet();
        }
    }

}
