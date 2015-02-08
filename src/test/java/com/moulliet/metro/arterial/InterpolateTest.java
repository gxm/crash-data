package com.moulliet.metro.arterial;

import com.moulliet.metro.crash.Point;
import junit.framework.TestCase;
import org.junit.Assert;

import java.util.ArrayList;
import java.util.List;

public class InterpolateTest extends TestCase {

    public void testInterpolateX() {
        List<Point> points = getXPoints();
        List<Point> interpolated = Interpolate.interpolate(points);

        for (Point point : interpolated) {
            System.out.println(point);
        }
        Assert.assertEquals(28, interpolated.size());
        assertTrue(interpolated.contains(new Point(-122.6613, 45.5627)));
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
        Assert.assertEquals(74, interpolated.size());
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
        List<Point> interpolated = Interpolate.interpolate(getXYPoints());
        for (Point point : interpolated) {
            System.out.println(point);
        }

        //assertTrue(interpolated.size() >= 73);
        /*assertTrue(interpolated.contains(new Point(-122.6607, 45.5074)));
        assertTrue(interpolated.contains(new Point(-122.6606, 45.5074)));
        assertTrue(interpolated.contains(new Point(-122.6605, 45.5074)));
        assertTrue(interpolated.contains(new Point(-122.6605, 45.5073)));
        assertTrue(interpolated.contains(new Point(-122.6604, 45.5073)));
        assertTrue(interpolated.contains(new Point(-122.6604, 45.5072)));
        assertTrue(interpolated.contains(new Point(-122.6603, 45.5072)));*/
        System.out.println(interpolated.size());
    }

    public static List<Point> getXYPoints() {
        //debug("DIVISION", 1884, shape);
        /*List<Point> points = new ArrayList<>();
        points.add(new Point(-122.6608,45.5075));
        points.add(new Point(-122.6607,45.5074));
        points.add(new Point(-122.6603,45.5072));
        points.add(new Point(-122.6598,45.5069));
        points.add(new Point(-122.6591,45.5065));
        points.add(new Point(-122.6587,45.5063));
        points.add(new Point(-122.6578,45.5058));
        points.add(new Point(-122.6572,45.5055));
        points.add(new Point(-122.6568,45.5052));
        points.add(new Point(-122.6565,45.5051));
        points.add(new Point(-122.6563,45.505 ));
        points.add(new Point(-122.6562,45.5049));
        points.add(new Point(-122.6561,45.5049));
        points.add(new Point(-122.656,45.5049 ));
        points.add(new Point(-122.6558,45.5049));
        points.add(new Point(-122.6547,45.5049));*/

        //debug("SUNSET", 940, shape);
        List<Point> points = new ArrayList<>();
        points.add(new Point(-122.6968,45.5158));
        points.add(new Point(-122.6956,45.5156));
        points.add(new Point(-122.6952,45.5156));
        points.add(new Point(-122.6944,45.5154));
        points.add(new Point(-122.6938,45.5153));
        points.add(new Point(-122.6932,45.5152));
        return points;
    }


}