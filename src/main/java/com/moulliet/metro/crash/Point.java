package com.moulliet.metro.crash;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DecimalFormat;

/**
 *
 */
public class Point implements Comparable<Point> {
    private static final Logger logger = LoggerFactory.getLogger(Point.class);

    private String x;
    private String y;

    public static DecimalFormat FORMAT = new DecimalFormat("####.#####");
    private static DecimalFormat HASH = new DecimalFormat("####.##");

    public Point(String x, String y) {
        this.x = FORMAT.format(Float.parseFloat(x));
        this.y = FORMAT.format(Float.parseFloat(y));
    }

    public Point(Number x, Number y) {
        this.x = FORMAT.format(x);
        this.y = FORMAT.format(y);
    }

    public Point(Number x, Number y, DecimalFormat format) {
        this.x = format.format(x);
        this.y = format.format(y);

    }

    /**
     * 45,28,25.4600000,-122,38,55.4200007
     * Decimal value = Degrees + (Minutes/60) + (Seconds/3600)
     */
    public Point(String longDegrees, String longMinutes, String longSeconds,
                 String latDegrees, String latMinutes, String latSeconds) {
        y = FORMAT.format(Float.parseFloat(longDegrees) + Float.parseFloat(longMinutes) / 60 + Float.parseFloat(longSeconds) / 3600);
        x = FORMAT.format(Float.parseFloat(latDegrees) - Float.parseFloat(latMinutes) / 60 - Float.parseFloat(latSeconds) / 3600);
    }

    public String toString() {
        return "Point{" +
                "y=" + y +
                ", x=" + x +
                '}';
    }

    public String getX() {
        return x;
    }

    public float getLongitude() {
        return Float.parseFloat(x);
    }

    public String getY() {
        return y;
    }

    public float getLatitude() {
        return Float.parseFloat(y);
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Point point = (Point) o;

        if (!x.equals(point.x)) return false;
        if (!y.equals(point.y)) return false;

        return true;
    }

    public int hashCode() {
        int result = x.hashCode();
        result = 31 * result + y.hashCode();
        return result;
    }

    public int compareTo(Point point) {
        return hashCode() - point.hashCode();
    }

    public boolean isWithin(Point other, float delta) {
        if (Math.abs(getLatitude() - other.getLatitude()) > delta) {
            return false;
        }
        if (Math.abs(getLongitude() - other.getLongitude()) > delta) {
            return false;
        }
        return true;
    }

    public String createHash() {
        return HASH.format(getLatitude()) + HASH.format(getLongitude());
    }
}
