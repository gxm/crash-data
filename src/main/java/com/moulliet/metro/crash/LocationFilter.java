package com.moulliet.metro.crash;

public class LocationFilter implements Filter {

    private final double north;
    private final double south;
    private final double east;
    private final double west;

    public LocationFilter(double north, double south, double east, double west) {
        this.north = north;
        this.south = south;
        this.east = east;
        this.west = west;
    }

    @Override
    public boolean include(Crash crash) {
        double lat = crash.getLat().doubleValue();
        if (lat < north && lat > south) {
            double lng = crash.getLng().doubleValue();
            if (lng < east && lng > west) {
                return true;
            }
        }
        return false;
    }
}
