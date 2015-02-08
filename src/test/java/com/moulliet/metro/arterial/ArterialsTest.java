package com.moulliet.metro.arterial;

import com.moulliet.metro.crash.Point;
import com.moulliet.metro.shape.Shape;
import junit.framework.TestCase;

public class ArterialsTest extends TestCase {

    public void testFilterX() {
        Shape shape = new Shape(null);
        shape.getPoints().addAll(InterpolateTest.getXPoints());

        Arterials.addPoints(shape);

        assertTrue(Arterials.isArterial(new Point(-122.6614, 45.5627)));
        assertTrue(Arterials.isArterial(new Point(-122.6600, 45.5627)));
        assertTrue(Arterials.isArterial(new Point(-122.6588, 45.5627)));
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