package com.moulliet.metro.crash;

import junit.framework.TestCase;

import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.assertNotEquals;

/**
 *
 */
public class PointTest extends TestCase
{
    public void testFormat()
    {
        Point point = new Point(45.55555, -122.44444);
        assertEquals("45.5555", point.getX());
        //todo - gfm - this seems wrong
        //assertEquals("45.5556", point.getX());
        assertEquals("-122.4444", point.getY());
    }

    public void testIsWithin() {
        Point point = new Point(45.493, -122.5733);
        assertTrue(point.isWithin(point, 0.00001f));
        assertTrue(point.isWithin(new Point(45.4929, -122.5733), 0.0001f));
    }

    public void test2009Bug() {
        //incorrect destination point
        //2015-01-16 07:28:46,382 TRACE [Crashes.java:95] adding point Point{y=45.5123, x=-122.5657}
        Point one = new Point(-122.5657, 45.5123);
        //2015-01-16 07:28:46,387 TRACE [Crashes.java:95] adding point Point{y=45.5263, x=-122.5643}
        Point two = new Point(-122.5643, 45.5263);
        System.out.println(one);
        System.out.println(two);
        assertNotEquals(one, two);
        assertNotEquals(0, one.compareTo(two));

        Map<Point, AtomicInteger> pointMap = new TreeMap<>();
        pointMap.put(one, new AtomicInteger(1));
        AtomicInteger found = pointMap.get(two);
        assertNull(found);

    }
}
