package com.moulliet.metro.arterial;

import com.moulliet.metro.crash.Point;
import junit.framework.TestCase;

import java.util.ArrayList;
import java.util.List;

public class InterpolateTest extends TestCase {

    /*public void testInterpolateX() {
        List<Point> points = new ArrayList<>();
        points.add(new Point(-122.0000, 45.0000));
        points.add(new Point(-122.0010, 45.0000));
        List<Point> interpolated = Interpolate.interpolate(points);
        assertEquals(11, interpolated.size());
        for (Point point : interpolated) {
            System.out.println(point);
        }

    }*/

    public void testInterpolateY() {
        /**
         * -122.6614,45.5627
         -122.6614,45.5634
         -122.6614,45.5639
         -122.6614,45.5641
         -122.6614,45.5648
         -122.6614,45.5651
         -122.6614,45.5655
         -122.6614,45.5664
         -122.6614,45.5674
         -122.6614,45.5677
         -122.6614,45.5682
         -122.6614,45.569
         -122.6614,45.57
         */
        List<Point> points = getYPoints();

        List<Point> interpolated = Interpolate.interpolate(points);
        for (Point point : interpolated) {
            System.out.println(point);
        }

        assertEquals(80, interpolated.size());

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