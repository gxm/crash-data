package com.moulliet.metro.filter;

import com.moulliet.metro.crash.Point;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class SinkFilterTest {

    @BeforeClass
    public static void setUpClass() throws Exception {
        SinkFilter.loadSinkPoints();
    }

    @Test
    public void testSimple() {
        assertTrue(SinkFilter.isSink(new Point("-122.57478", "45.49284")));
        assertTrue(SinkFilter.isSink(new Point("-122.57077", "45.55688")));
        assertFalse(SinkFilter.isSink(new Point("-122.5705", "45.55688")));
        assertFalse(SinkFilter.isSink(new Point("-122.57077", "45.5567")));
    }

    @Test
    public void testUnfiltered() {
        Point sinkPoint = new Point("-122.80298", "45.47047");
        assertTrue(SinkFilter.isSink(sinkPoint));
        Point unfiltered = new Point("-122.8030", "45.4705");
        assertTrue(SinkFilter.isSink(unfiltered));
    }

    @Test
    public void testFiltration() {
        assertTrue(SinkFilter.isSink(new Point("-122.5733", "45.4929")));
    }
}