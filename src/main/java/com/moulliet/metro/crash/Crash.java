package com.moulliet.metro.crash;

import com.mongodb.BasicDBList;
import com.mongodb.DBObject;

import java.text.DecimalFormat;

/**
 * This class represents a crash as displayed by the heatmap.
 * { "_id" : { "$oid" : "51e07dc30364a5f3a7cc7a75"} ,
 * "loc" : { "type" : "Point" ,
 * "coordinates" : [ -122.6671371459961 , 45.511680603027344]} ,
 * "count" : 1 ,
 * "alcohol" : false ,
 * "injury" : 1 ,
 * "fatality" : 0 ,
 * "ped" : 0 ,
 * "bike" : 0 ,
 * "surface" : 2 ,
 * "light" : 1 ,
 * "year" : 2012 , "month" : 10 , "day" : 30}
 */
public class Crash  {
    private static DecimalFormat format = new DecimalFormat("####.####");
    private final Point point;

    private boolean alcohol;
    private int severity;
    private int ped;
    private int bike;
    private int surface;
    private int light;
    private int year;
    private String type;
    private BasicDBList coordinates;

    public Crash(DBObject dbObject) {
        alcohol = (int) dbObject.get("ALCHL_INVL") > 0;
        ped =  (int) dbObject.get("TOT_PED_CN");
        bike = (int) dbObject.get("TOT_PEDCYC");
        surface = Integer.parseInt((String) dbObject.get("RD_SURF_CO"));
        light = Integer.parseInt((String) dbObject.get("LGT_COND_C"));
        type = (String) dbObject.get("COLLIS_TYP");
        year = Integer.parseInt((String) dbObject.get("CRASH_YR_N"));
        DBObject loc = (DBObject) dbObject.get("loc");
        coordinates = (BasicDBList) loc.get("coordinates");

        if ((int) dbObject.get("TOT_FATAL_") > 0) {
            severity = 4;
        } else if ((int) dbObject.get("TOT_INJ_LV") > 0) {
            severity = 3;
        } else if ((int) dbObject.get("TOT_INJ__1") > 0) {
            severity = 2;
        } else if ((int) dbObject.get("TOT_INJ__2") > 0) {
            severity = 1;
        } else {
            severity = 0;
        }
        point = new Point(getLng(), getLat(), format);
    }

    public boolean isCrash() {
        return true;
    }

    public Point getPoint() {
        return point;
    }

    public Number getLng() {
        return (Number) coordinates.get(0);
    }

    public Number getLat() {
        return (Number) coordinates.get(1);
    }

    public boolean isAlcohol() {
        return alcohol;
    }

    public int getSeverity() {
        return severity;
    }

    public int getPed() {
        return ped;
    }

    public int getBike() {
        return bike;
    }

    public int getSurface() {
        return surface;
    }

    public int getLight() {
        return light;
    }

    public String getType() {
        return type;
    }

    public int getYear() {
        return year;
    }
}
