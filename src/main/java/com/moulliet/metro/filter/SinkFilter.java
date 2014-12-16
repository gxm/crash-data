package com.moulliet.metro.filter;

import com.moulliet.metro.Config;
import com.moulliet.metro.crash.Point;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class SinkFilter {
    private static final Logger logger = LoggerFactory.getLogger(SinkFilter.class);
    private static final ObjectMapper mapper = new ObjectMapper();
    private static Map<String, Point> filterMap = new HashMap<>();
    private static final float SINK_DELTA = 0.0002f;
    private static final float SINK_RADIUS = 0.0001f;

    public static void loadSinkPoints() throws IOException {
        Iterator<JsonNode> elements = getRootNode().getElements();
        while (elements.hasNext()) {
            JsonNode node = elements.next();
            String lng = node.get("lng").asText();
            String lat = node.get("lat").asText();
            addSinkPoints(lng, lat);
        }
        logger.trace("sinks {}", filterMap);
        logger.info("loaded {} sinks", filterMap.size());
    }

    private static JsonNode getRootNode() throws IOException {
        return mapper.readTree(new File(Config.getConfig().getString("sinks.file")));
    }

    private static void addSinkPoints(String lng, String lat) {
        float x = Float.parseFloat(lng);
        float y = Float.parseFloat(lat);
        Point sinkPoint = new Point(x, y);
        for (float i = -SINK_RADIUS; i <= SINK_RADIUS; i += SINK_RADIUS) {
            String hash = new Point(x + i, y).createHash();
            filterMap.put(hash, sinkPoint);
            for (float j = -SINK_RADIUS; j <= SINK_RADIUS; j += SINK_RADIUS) {
                String hash1 = new Point(x + i, y + j).createHash();
                filterMap.put(hash1, sinkPoint);
            }
        }
    }

    public static boolean isSink(Point point) {
        String hash = point.createHash();
        Point sinkPoint = filterMap.get(hash);
        if (sinkPoint == null) {
            logger.trace("point {} not in hash map {} ", point, hash);
            return false;
        }
        boolean within = point.isWithin(sinkPoint, SINK_DELTA);
        logger.trace("point: {} withing? {} sink: {}", point, within, sinkPoint);
        return within;
    }

    public static JsonNode getSinks() throws IOException {
        return getRootNode();
    }


}
