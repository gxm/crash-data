package com.moulliet.metro.arterial;

import com.moulliet.metro.crash.Point;
import com.moulliet.metro.shape.Shape;
import com.moulliet.metro.shape.ShapeLoader;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.ObjectNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;

public class Arterials {

    private static final Logger logger = LoggerFactory.getLogger(Arterials.class);

    private static final ObjectMapper mapper = new ObjectMapper();
    private static ArrayNode rootNode;

    public static void loadArterials() throws IOException {
        logger.info("loading arterials");
        ShapeLoader shapeLoader = new ShapeLoader("/Users/greg/code/rlis/Feb2015/arterial/", "arterial");
        List<Shape> shapes = shapeLoader.loadPolygons();
        logger.info("loaded {} arterial shapes ", shapes.size());
        rootNode = mapper.createArrayNode();
        for (Shape shape : shapes) {
            ArrayNode shapesPoints = rootNode.addArray();
            List<Point> points = shape.getPoints();
            for (Point point : points) {
                ObjectNode node = shapesPoints.addObject();
                node.put("lng", point.getLongitude());
                node.put("lat", point.getLatitude());
            }
        }
        logger.info("loaded {} arterial points ", rootNode.size());
    }

    public static JsonNode getPoints() throws IOException {
        return rootNode;
    }
}
