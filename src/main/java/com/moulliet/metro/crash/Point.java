package com.moulliet.metro.crash;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DecimalFormat;

public class Point implements Comparable<Point> {
    private static final Logger logger = LoggerFactory.getLogger(Point.class);
    private static final int FACTOR = 10000;

    private float x;
    private float y;

    public static DecimalFormat FORMAT = new DecimalFormat("####.####");
    private static DecimalFormat HASH = new DecimalFormat("###0.0000");

    public Point(Number x, Number y) {
        this.x = Float.parseFloat(FORMAT.format(x));
        this.y = Float.parseFloat(FORMAT.format(y));
    }

    public String toString() {
        return "Point{" +
                "y=" + y +
                ", x=" + x +
                '}';
    }

    public float getLongitude() {
        return x;
    }

    public float getLatitude() {
        return y;
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Point point = (Point) o;

        if (x != point.x) return false;
        if (y != point.y) return false;

        return true;
    }

    public int hashCode() {
        return (int) (FACTOR * (31 * y + x));
    }

    public int compareTo(Point point) {
        int diff = (int) (FACTOR * (y - point.y));
        if (diff == 0) {
            diff = (int) (FACTOR * ( x - point.x));
        }
        return diff;
    }

    public boolean isWithin(Point other, float delta) {
        float latDiff = y - other.y;
        if (Math.abs(latDiff) > delta) {
            logger.trace("lat diff {}", latDiff);
            return false;
        }
        float longDiff = x - other.x;
        if (Math.abs(longDiff) > delta) {
            logger.trace("lng diff {}", longDiff);
            return false;
        }
        logger.trace("within {} {}", longDiff, latDiff);
        return true;
    }

    public String createHash() {
        return HASH.format(y) + HASH.format(x);
    }
}
