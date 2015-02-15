package com.moulliet.metro.arterial;

import com.moulliet.metro.crash.Point;
import com.moulliet.metro.shape.Shape;
import junit.framework.TestCase;

public class EsriArterialsTest extends TestCase {

    public void testFilterX() {
        Shape shape = new Shape(null);
        shape.getPoints().addAll(InterpolateTest.getXPoints());

        EsriArterials.addPoints(shape);

        assertTrue(EsriArterials.isArterial(new Point(-122.6614, 45.5627)));
        assertTrue(EsriArterials.isArterial(new Point(-122.6600, 45.5627)));
        assertTrue(EsriArterials.isArterial(new Point(-122.6588, 45.5627)));
    }

    public void testFilterY() {
        Shape shape = new Shape(null);
        shape.getPoints().addAll(InterpolateTest.getYPoints());

        EsriArterials.addPoints(shape);

        assertTrue(EsriArterials.isArterial(new Point(-122.6614, 45.5634)));
        assertTrue(EsriArterials.isArterial(new Point(-122.6614, 45.5635)));
        assertTrue(EsriArterials.isArterial(new Point(-122.6614, 45.5678)));
    }

    public void testFilterXY() {
        Shape shape = new Shape(null);
        shape.getPoints().addAll(InterpolateTest.getXYPoints());

        EsriArterials.addPoints(shape);

        assertTrue(EsriArterials.isArterial(new Point(-122.6968, 45.5158)));
        assertTrue(EsriArterials.isArterial(new Point(-122.6956, 45.5156)));
        assertTrue(EsriArterials.isArterial(new Point(-122.6937, 45.5153)));
        assertTrue(EsriArterials.isArterial(new Point(-122.6939, 45.5153)));
        assertTrue(EsriArterials.isArterial(new Point(-122.6935, 45.5153)));
        assertTrue(EsriArterials.isArterial(new Point(-122.6933, 45.5153)));
        /**
         -122.6968,45.5158
         -122.6956,45.5156
         -122.6952,45.5156
         -122.6944,45.5154
         -122.6938,45.5153
         -122.6932,45.5152
         */
    }
}