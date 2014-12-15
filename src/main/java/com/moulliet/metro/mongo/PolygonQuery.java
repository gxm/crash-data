package com.moulliet.metro.mongo;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;

public class PolygonQuery {

    private String north = "45.558234864509195";
    private String south = "45.48173913538092";
    private String east = "-122.56125251770021";
    private String west = "-122.77874748229982";

    public PolygonQuery() {
    }

    public PolygonQuery(String north, String south, String east, String west) {
        this.north = north;
        this.south = south;
        this.east = east;
        this.west = west;
    }

    public void getQuery(BasicDBObject query) {
        BasicDBList northEast = new BasicDBList();
        northEast.add(Float.parseFloat(east));
        northEast.add(Float.parseFloat(north));

        BasicDBList southEast = new BasicDBList();
        southEast.add(Float.parseFloat(east));
        southEast.add(Float.parseFloat(south));

        BasicDBList southWest = new BasicDBList();
        southWest.add(Float.parseFloat(west));
        southWest.add(Float.parseFloat(south));

        BasicDBList northWest = new BasicDBList();
        northWest.add(Float.parseFloat(west));
        northWest.add(Float.parseFloat(north));

        BasicDBList geoParams = new BasicDBList();
        geoParams.add(northEast);
        geoParams.add(southEast);
        geoParams.add(southWest);
        geoParams.add(northWest);
        geoParams.add(northEast);

        BasicDBList coordinates = new BasicDBList();
        coordinates.add(geoParams);

        BasicDBObject polygon = new BasicDBObject("type", "Polygon");
        polygon.append("coordinates", coordinates);

        query.append("loc", new BasicDBObject("$geoWithin",
                new BasicDBObject("$geometry", polygon)));

    }

}