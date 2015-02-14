package com.moulliet.metro.arterial;

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
        ShapeLoader shapeLoader = new ShapeLoader("/Users/greg/code/rlis/Feb2015/arterial/", "arterial");
        List<Shape> shapes = shapeLoader.loadPolygons();

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
}


