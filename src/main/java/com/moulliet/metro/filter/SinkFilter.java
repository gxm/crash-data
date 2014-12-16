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

    public static void loadSinkPoints() throws IOException {
        JsonNode root = mapper.readTree(new File(Config.getConfig().getString("sinks.file")));
        Iterator<JsonNode> elements = root.getElements();
        while (elements.hasNext()) {
            JsonNode node = elements.next();
            String lng = node.get("lng").asText();
            String lat = node.get("lat").asText();
            Point point = new Point(lng, lat);
            filterMap.put(point.createHash(), point);
        }
        logger.debug("sinks{}", filterMap);
    }

    public static boolean isSink(Point point) {
        String hash = point.createHash();
        Point sinkPoint = filterMap.get(hash);
        if (sinkPoint == null) {
            logger.trace("point {} not in hash map {} ", point, hash);
            return false;
        }
        boolean within = point.isWithin(sinkPoint, 0.0001F);
        logger.trace("point: {} withing? {} sink: {}", point, within, sinkPoint);
        return within;
    }

}
