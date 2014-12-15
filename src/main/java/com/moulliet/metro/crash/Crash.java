package com.moulliet.metro.crash;

import com.mongodb.BasicDBList;
import com.mongodb.DBObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

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
    private int ped;
    private int bike;
    private int surface;
    private int light;
    private String type;
    private int year;
    private int severity;
    private int crashId;
    private BasicDBList coordinates;

    public static final String[] fieldNames = {
            "ALCHL_INVLV_FLG",
            "TOT_PED_CNT",
            "TOT_PEDCYCL_CNT",
            "RD_SURF_COND_CD",
            "LGT_COND_CD",
            "COLLIS_TYP_CD",
            "CRASH_YR_NO",
            "TOT_FATAL_CNT",
            "TOT_INJ_LVL_A_CNT",
            "TOT_INJ_LVL_B_CNT",
            "TOT_INJ_LVL_C_CNT",
            "CRASH_ID"
    };

    public Crash(DBObject dbObject) {
        alcohol = (int) dbObject.get(fieldNames[0]) > 0;
        ped =  (int) dbObject.get(fieldNames[1]);
        bike = (int) dbObject.get(fieldNames[2]);
        surface = Integer.parseInt((String) dbObject.get(fieldNames[3]));
        light = Integer.parseInt((String) dbObject.get(fieldNames[4]));
        type = (String) dbObject.get(fieldNames[5]);
        year = Integer.parseInt((String) dbObject.get(fieldNames[6]));

        if ((int) dbObject.get(fieldNames[7]) > 0) {
            severity = 4;
        } else if ((int) dbObject.get(fieldNames[8]) > 0) {
            severity = 3;
        } else if ((int) dbObject.get(fieldNames[9]) > 0) {
            severity = 2;
        } else if ((int) dbObject.get(fieldNames[10]) > 0) {
            severity = 1;
        } else {
            severity = 0;
        }
        crashId = (int) dbObject.get(fieldNames[11]);

        DBObject loc = (DBObject) dbObject.get("loc");
        coordinates = (BasicDBList) loc.get("coordinates");


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
