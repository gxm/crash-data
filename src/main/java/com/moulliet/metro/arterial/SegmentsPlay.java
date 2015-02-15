package com.moulliet.metro.arterial;

import com.esri.core.geometry.OperatorDensifyByLength;
import com.esri.core.geometry.Polygon;
import com.moulliet.metro.crash.Point;
import com.moulliet.metro.shape.Shape;
import com.moulliet.metro.shape.ShapeLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class SegmentsPlay {
    private static final Logger logger = LoggerFactory.getLogger(SegmentsPlay.class);

    public static void main(String[] args) throws IOException {
        OperatorDensifyByLength densifyByLength = OperatorDensifyByLength.local();
        ShapeLoader shapeLoader = new ShapeLoader("/Users/greg/code/rlis/Feb2015/arterial/", "arterial");
        List<Shape> shapes = shapeLoader.loadPolygons();
        Tracker tracker = new Tracker("SegmentsPlay");
        for (int i = 0; i < 10; i++) {
            Shape shape = shapes.get(i);
            Polygon polygon = shape.getPolygon();

            Polygon geometry = (Polygon) densifyByLength.execute(polygon, 52.0, tracker);

            logger.info("found geometry {}", geometry);
            logger.info("found geometry type {}", geometry.getType());

            logger.info("from {} to {} points", polygon.getPointCount(), geometry.getPointCount());

            //int pointCount = polygon.getPointCount();
            //System.out.println  ("poly:");
           /* for (int j  = 0; j < pointCount; j++) {
                com.esri.core.geometry.Point point = polygon.getPoint(j);
                System.out.println(point.getX() + "," + point.getY());

            }*/
        }
        //write(shapes);
    }

    private static void write(List<Shape> shapes) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter("/Users/greg/code/rlis/Feb2015/arterial/segs.csv"));
        logger.info("loaded {} arterial shapes ", shapes.size());
        for (Shape shape : shapes) {

            List<Point> points = shape.getPoints();
            Point point = points.get(0);

            Point last = points.get(points.size() - 1);
            writer.write("" + point.distance(last));
            writer.newLine();

        }
        System.out.println("written");
        writer.close();
    }

    //todo - gfm - 2/14/15 - try to use OperatorDensifyByLength

}


