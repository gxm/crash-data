package com.moulliet.metro.arterial;

import com.moulliet.metro.crash.Point;
import junit.framework.TestCase;

import java.util.ArrayList;
import java.util.List;

public class InterpolateTest extends TestCase {

    public void testInterpolateX() {
        List<Point> points = getXPoints();
        List<Point> interpolated = Interpolate.interpolate(points);

        for (Point point : interpolated) {
            System.out.println(point);
        }
        assertTrue(interpolated.size() >= 27);

    }

    public static List<Point> getXPoints() {
        List<Point> points = new ArrayList<>();
        points.add(new Point(-122.6614, 45.5627));
        points.add(new Point(-122.6596, 45.5627));
        points.add(new Point(-122.6587, 45.5627));
        return points;
    }

    public void testInterpolateY() {
        List<Point> interpolated = Interpolate.interpolate(getYPoints());
        for (Point point : interpolated) {
            System.out.println(point);
        }

        assertTrue(interpolated.size() >= 73);

    }

    public static List<Point> getYPoints() {
        List<Point> points = new ArrayList<>();
        points.add(new Point(-122.6614, 45.5627));
        points.add(new Point(-122.6614, 45.5634));
        points.add(new Point(-122.6614, 45.5639));
        points.add(new Point(-122.6614, 45.5641));
        points.add(new Point(-122.6614, 45.5648));
        points.add(new Point(-122.6614, 45.5651));
        points.add(new Point(-122.6614, 45.5655));
        points.add(new Point(-122.6614, 45.5664));
        points.add(new Point(-122.6614, 45.5674));
        points.add(new Point(-122.6614, 45.5677));
        points.add(new Point(-122.6614, 45.5682));
        points.add(new Point(-122.6614, 45.5690));
        points.add(new Point(-122.6614, 45.57));
        return points;
    }

    public void testInterpolateBoth() {

    }

}