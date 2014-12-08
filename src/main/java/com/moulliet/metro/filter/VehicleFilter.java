package com.moulliet.metro.filter;

import com.moulliet.metro.crash.Crash;

public class VehicleFilter implements Filter {
    private final boolean cars;
    private final boolean bikes;
    private final boolean peds;

    public VehicleFilter(boolean cars, boolean bikes, boolean peds) {

        this.cars = cars;
        this.bikes = bikes;
        this.peds = peds;
    }

    public static Filter create(boolean cars, boolean bikes, boolean peds) {
        if (cars && bikes && peds) {
            return null;
        }
        if (!cars && !bikes && !peds) {
            return new FalseFilter();
        }
        return new VehicleFilter(cars, bikes, peds);
    }

    @Override
    public boolean include(Crash crash) {
        if (!cars) {
            if (bikes && peds) {
                return crash.getBike() > 0 || crash.getPed() > 0;
            } else if (bikes) {
                return crash.getBike() > 0;
            } else if (peds) {
                return crash.getPed() > 0;
            }
        } else {
            if (bikes) {
                return crash.getPed() == 0;
            } else if (peds) {
                return crash.getBike() == 0;
            } else {
                return crash.getPed() == 0 && crash.getBike() == 0;
            }
        }
        return false;
    }
}
