package com.moulliet.metro.arterial;

import com.esri.core.geometry.Polygon;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Arterials {

    private static final Logger logger = LoggerFactory.getLogger(Arterials.class);

    private static final ObjectMapper mapper = new ObjectMapper();
    private static ArrayNode multiLines = mapper.createArrayNode();
    private static ArrayNode multiPolygons = mapper.createArrayNode();
    private static Map<String, Point> filterMap = new HashMap<>();
    //todo - gfm - 2/14/15 - &lat=45.5553406&lng=-122.645869
    private static final Point center = new Point(-122.645869, 45.55534);
    //private static final Point center = new Point(-122.66534, 45.52422);
    private static final float DELTA = 0.0002f;

    public static void loadArterials() throws IOException {
        logger.info("loading arterials");
        ShapeLoader shapeLoader = new ShapeLoader("/Users/greg/code/rlis/Feb2015/arterial/", "arterial");
        List<Shape> shapes = shapeLoader.loadPolygons();
        logger.info("loaded {} arterial shapes ", shapes.size());
        for (Shape shape : shapes) {
            //debug(shape);
            addPoints(shape);
        }
        logger.info("loaded {} arterial map ", filterMap.size());
    }

    private static void debug(Shape shape) {
        //debug("KILLINGSWORTH", 7882, shape);
        //debug("MARTIN", 2640, shape);
        //debug("DIVISION", 1884, shape);
        //debug("SUNSET", 940, shape);
    }

/*    private static void debug(String name, int len, Shape shape) {
        String streetname = (String) shape.getDescriptions().get("STREETNAME");
        Double length = (Double) shape.getDescriptions().get("LENGTH");
        if (streetname.startsWith(name)) {
            if (length > len && length < len + 1) {
                for (Point point : shape.getMultiLine()) {
                    System.out.println(point.getLongitude() + "," + point.getLatitude());
                }
            }
        }
    }*/

    static void addPoints(Shape shape) {
        //List<Point> points = Interpolate.interpolate(shape.getMultiLine());
        List<Point> points = shape.getPoints();
        ArrayNode shapesPoints = null;

        for (Point point : points) {
            //todo - gfm - 2/14/15 -
            if (point.isWithin(center, (float) 0.02)) {
                //filterMap.put(point.createHash(), point);
                logger.trace("adding {} {}", point.createHash(), point);
                if (shapesPoints == null) {
                    shapesPoints = multiLines.addArray();
                }
                ObjectNode node = shapesPoints.addObject();
                node.put("lng", point.getLongitude());
                node.put("lat", point.getLatitude());
            }
        }
        //todo - gfm - 2/14/15 - create segment polygons
        Polygon polygon = shape.getPolygon();
        double length2D = polygon.calculateLength2D();
        //polygon.interpolateAttributes();
        //todo - gfm - 2/14/15 - look for connections among multi-lines
        //todo - gfm - 2/14/15 - add to multiPolygons

    }

    public static JsonNode getMultiLine() throws IOException {
        return multiLines;
    }

    public static JsonNode getMultiPolygon() throws IOException {
        //todo - gfm - 2/14/15 - return an array of 8
        return multiPolygons;
    }

    public static boolean isArterial(Point point) {
        String hash = point.createHash();
        Point sinkPoint = filterMap.get(hash);
        if (sinkPoint == null) {
            logger.trace("point {} not in hash map {} ", point, hash);
            return false;
        }
        boolean within = point.isWithin(sinkPoint, DELTA);
        logger.trace("point: {} withing? {} sink: {}", point, within, sinkPoint);
        return within;
    }
}
