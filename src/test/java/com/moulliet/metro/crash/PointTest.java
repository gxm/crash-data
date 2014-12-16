package com.moulliet.metro.crash;

import junit.framework.TestCase;

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

    /*public void testIsWithin() {
        Point point = new Point(45.55555, -122.44444);
        assertTrue(point.isWithin(point, 0.00001f));

        assertTrue(point.isWithin(new Point(45.55554, -122.44444), 0.00002f));
        assertTrue(point.isWithin(new Point(45.55555, -122.44445), 0.00002f));
        assertFalse(point.isWithin(new Point(45.55565, -122.44444), 0.00005f));
        assertFalse(point.isWithin(new Point(45.55555, -122.44434), 0.00005f));

    }*/
}
