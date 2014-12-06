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

    private final Point point;
    private boolean alcohol;
    private int injury;
    private int fatality;
    private int ped;
    private int bike;
    private int surface;
    private int light;
    private String type;

    public Crash(DBObject dbObject, DecimalFormat format) {
        DBObject loc = (DBObject) dbObject.get("loc");
        BasicDBList coordinates = (BasicDBList) loc.get("coordinates");
        injury = (Integer) dbObject.get("injury");
        fatality = (Integer) dbObject.get("fatality");
        alcohol = (Boolean) dbObject.get("alcohol");
        ped = (Integer) dbObject.get("ped");
        bike = (Integer) dbObject.get("bike");
        surface = (Integer) dbObject.get("surface");
        light = (Integer) dbObject.get("light");
        type = (String) dbObject.get("type");
        point = new Point((Number) coordinates.get(0), (Number) coordinates.get(1), format);
    }

    public boolean isCrash() {
        return true;
    }

    public Point getPoint() {
        return point;
    }

    public boolean isAlcohol() {
        return alcohol;
    }

    public int getInjury() {
        return injury;
    }

    public int getFatality() {
        return fatality;
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

}
