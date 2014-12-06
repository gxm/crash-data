package com.moulliet.metro.crash;

import junit.framework.TestCase;

public class CrashQueryTest extends TestCase {

    private CrashQuery crashQuery;

    public void setUp() throws Exception {
        crashQuery = new CrashQuery();
    }

    public void testAllVehicles() {
        crashQuery.vehicle(true, true, true);
        assertEquals("{ }", crashQuery.getQuery().toString());
    }

    public void testBikes() {
        crashQuery.vehicle(false, true, false);
        assertEquals("{ \"bike\" : { \"$gt\" : 0}}", crashQuery.getQuery().toString());
    }

    public void testPeds() {
        crashQuery.vehicle(false, false, true);
        assertEquals("{ \"ped\" : { \"$gt\" : 0}}", crashQuery.getQuery().toString());
    }

    public void testBikesAndPeds() {
        crashQuery.vehicle(false, true, true);
        assertEquals("{ \"$or\" : [ { \"bike\" : { \"$gt\" : 0}} , { \"ped\" : { \"$gt\" : 0}}]}",
                crashQuery.getQuery().toString());
    }

    public void testDay() {
        crashQuery.light(true, false, false);
        assertEquals("{ \"light\" : { \"$in\" : [ 0 , 1]}}", crashQuery.getQuery().toString());
    }

    public void testNight() {
        crashQuery.light(false, true, false);
        assertEquals("{ \"light\" : { \"$in\" : [ 2 , 3]}}", crashQuery.getQuery().toString());
    }

    public void testNightTwilight() {
        crashQuery.light(false, true, true);
        assertEquals("{ \"light\" : { \"$in\" : [ 2 , 3 , 4 , 5]}}", crashQuery.getQuery().toString());
    }

    public void testLightAll() {
        crashQuery.light(true, true, true);
        assertEquals("{ }", crashQuery.getQuery().toString());
    }

    public void testSurfacesAll() {
        crashQuery.surface(true, true, true);
        assertEquals("{ }", crashQuery.getQuery().toString());
    }

    public void testDry() {
        crashQuery.surface(true, false, false);
        assertEquals("{ \"surface\" : { \"$in\" : [ 0 , 1]}}", crashQuery.getQuery().toString());
    }

    public void testWet() {
        crashQuery.surface(false, true, false);
        assertEquals("{ \"surface\" : { \"$in\" : [ 2]}}", crashQuery.getQuery().toString());
    }

    public void testDrySnowIce() {
        crashQuery.surface(true, false, true);
        assertEquals("{ \"surface\" : { \"$in\" : [ 0 , 1 , 3 , 4]}}", crashQuery.getQuery().toString());
    }

    public void testYears() {
        crashQuery.years(true, false, true, false, true, false, false);
        assertEquals("{ \"year\" : { \"$in\" : [ 2007 , 2009 , 2011]}}", crashQuery.getQuery().toString());
    }
    
    public void testTypeAll() {
        crashQuery.type(true, true, true, true, true, true);
        assertEquals("{ }", crashQuery.getQuery().toString());
    }

    public void testTypeNone() {
        crashQuery.type(false, false, false, false, false, false);
        assertEquals("{ \"type\" : \"X\"}", crashQuery.getQuery().toString());
    }

    public void testTypeOther() {
        crashQuery.type(false, false, false, false, false, true);
        assertEquals("{ \"type\" : { \"$in\" : [ \"7\" , \"8\" , \"9\" , \"0\" , \"-\" , \"&\"]}}", crashQuery.getQuery().toString());
    }

    public void testTypeSome() {
        crashQuery.type(true, false, true, false, true, false);
        assertEquals("{ \"type\" : { \"$in\" : [ \"1\" , \"3\" , \"6\"]}}", crashQuery.getQuery().toString());
    }

    public void testTypeNotOther() {
        crashQuery.type(true, true, true, true, true, false);
        assertEquals("{ \"type\" : { \"$in\" : [ \"1\" , \"2\" , \"3\" , \"4\" , \"5\" , \"6\"]}}", crashQuery.getQuery().toString());
    }
}
