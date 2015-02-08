package com.moulliet.metro.arterial;

import com.moulliet.metro.crash.Point;
import com.moulliet.metro.shape.Shape;
import junit.framework.TestCase;

public class ArterialsTest extends TestCase {

    public void testFilter() {
        Shape shape = new Shape(null);
        Point start = new Point(-122.0000, 45.0000);
        shape.getPoints().add(start);
        Point end = new Point(-122.0010, 45.0000);
        shape.getPoints().add(end);

        Arterials.addPoints(shape);

        assertTrue(Arterials.isArterial(start));
        assertTrue(Arterials.isArterial(end));
        assertTrue(Arterials.isArterial(new Point(-122.0005, 45.0000)));

    }

    public void testFilterY() {
        Shape shape = new Shape(null);
        shape.getPoints().addAll(InterpolateTest.getYPoints());

        Arterials.addPoints(shape);

        assertTrue(Arterials.isArterial(new Point(-122.6614, 45.5634)));
        assertTrue(Arterials.isArterial(new Point(-122.6614, 45.5635)));
        assertTrue(Arterials.isArterial(new Point(-122.6614, 45.5678)));
    }
}