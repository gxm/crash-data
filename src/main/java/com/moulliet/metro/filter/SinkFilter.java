package com.moulliet.metro.filter;

import com.moulliet.metro.Config;
import com.moulliet.metro.crash.Point;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class SinkFilter {
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
    }

    public static boolean isSink(Point point) {
        Point sinkPoint = filterMap.get(point.createHash());
        if (sinkPoint == null) {
            return false;
        }
        return point.isWithin(sinkPoint, 0.0001F);
    }

}
