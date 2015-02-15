package com.moulliet.metro.crash;

import com.mongodb.DBObject;
import com.moulliet.metro.Statics;
import com.moulliet.metro.arterial.Arterials;
import com.moulliet.metro.filter.Filter;
import com.moulliet.metro.filter.Sinks;
import com.moulliet.metro.mongo.MongoQueryCallback;
import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.OutputStream;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class Crashes {
    private static final Logger logger = LoggerFactory.getLogger(Crashes.class);
    private static List<Crash> allCrashes;

    private Map<Point, AtomicInteger> pointMap = new HashMap<>();
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
            json.writeNumberField("lat", key.getLatitude());
            json.writeNumberField("lng", key.getLongitude());
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

    public static synchronized void loadAll() throws IOException {

        Sinks.loadSinks();
        Arterials.loadArterials();
        allCrashes = Collections.unmodifiableList(loadCrashes());
        logger.info("loaded {} crashes", allCrashes.size());
    }

    public static List<Crash> loadCrashes() {
        logger.info("loading crashes");
        Set<Crash> crashes = new HashSet<>();
        for (String dataset : Statics.datasetService.getActiveNames()) {
            logger.info("loading dataset: {}", dataset);
            Statics.mongoDao.query(dataset, null, new MongoQueryCallback() {
                @Override
                public void callback(Iterator<DBObject> iterator) {
                    while (iterator.hasNext()) {
                        Crash crash = Crash.create(iterator.next());
                        if (crash != null && !Sinks.isSink(crash.getPoint())) {
                            crashes.add(crash);
                        }
                    }
                }
            });
            logger.info("loaded dataset: {}, total items: {}", dataset, crashes.size());
        }
        return new ArrayList<>(crashes);
    }

    private void consolidatePoints(Filter filter) {
        for (Crash crash : allCrashes) {
            if (filter.include(crash)) {
                addPointToMap(crash.getPoint());
                crashTotals.addCrash(crash);
            }
        }
    }

    public static List<Crash> getAllCrashes() {
        return allCrashes;
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
