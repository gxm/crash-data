package com.moulliet.metro.crash;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class Point implements Comparable<Point> {
    private static final Logger logger = LoggerFactory.getLogger(Point.class);
    private static final int FACTOR = 10000;

    private float x;
    private float y;

    private static DecimalFormat POINT_FORMAT = new DecimalFormat("#######0.0000");
    private static final float SINK_RADIUS = 0.0001f;

    public Point(Number x, Number y) {
        this(x, y, POINT_FORMAT);
    }

    public Point(Number x, Number y, DecimalFormat format) {
        this.x = Float.parseFloat(format.format(x));
        this.y = Float.parseFloat(format.format(y));
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
        if (deltaY(other) > delta) {
            return false;
        }
        if (deltaX(other) > delta) {
            return false;
        }
        return true;
    }

    private float deltaX(Point other) {
        return Math.abs(x - other.x);
    }

    private float deltaY(Point other) {
        return Math.abs(y - other.y);
    }

    public String createHash() {
        return POINT_FORMAT.format(y) + POINT_FORMAT.format(x);
    }

    public int getLatitiudeDifference(Point other) {
        return Math.round(FACTOR * (y - other.y));
    }

    public int getLongitudeDifference(Point other) {
        return Math.round(FACTOR * (x - other.x));
    }

    public Point offset(int lng, int lat) {
        return new Point(x + (float) lng / (float) FACTOR, y + (float) lat / (float) FACTOR);
    }

    public List<Point> star() {
        ArrayList<Point> points = new ArrayList<>();
        points.add(this);
        for (float i = -SINK_RADIUS; i <= SINK_RADIUS; i += SINK_RADIUS) {
            points.add(new Point(x + i, y));
            for (float j = -SINK_RADIUS; j <= SINK_RADIUS; j += SINK_RADIUS) {
                points.add(new Point(x + i, y + j));
            }
        }
        return points;
    }

    public double distance(Point other) {
        double deltaX = 36.516 * deltaX(other);
        double deltaY = 25.82 * deltaY(other);
        return 10000 * Math.sqrt(deltaX * deltaX  + deltaY * deltaY);
    }
}
