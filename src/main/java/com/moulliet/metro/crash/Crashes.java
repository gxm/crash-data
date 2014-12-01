package com.moulliet.metro.crash;

import com.mongodb.DBObject;
import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class Crashes {
    private static final Logger logger = LoggerFactory.getLogger(Crashes.class);
    private final List<Crash> crashes = new ArrayList<>();
    private final Map<Point, AtomicInteger> pointMap = new TreeMap<>();
    private CrashTotals crashTotals = new CrashTotals();

    public void aggregatedCrashes(OutputStream stream) throws IOException {
        consolidatePoints();
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
            json.writeEndObject();
        }
        json.writeEndArray();
        json.writeNumberField("max", max);
        json.writeNumberField("total", crashTotals.getTotal());
        crashTotals.write(json);
        json.writeEndObject();
        json.flush();
        logger.debug("returned " + pointMap.entrySet().size() + " elements with max of " + max);
    }

    public void loadResults(Iterator<DBObject> dbObjectIterator, DecimalFormat format) {
        while (dbObjectIterator.hasNext()) {
            DBObject next = dbObjectIterator.next();
            crashes.add(new Crash(next, format));
        }
        System.out.println(crashes.size());
    }

    private void consolidatePoints() {
        for (Crash crash : crashes) {
            addPointToMap(crash.getPoint());
            crashTotals.addCrash(crash);
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
