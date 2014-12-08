package com.moulliet.metro.filter;

import com.moulliet.metro.crash.Crash;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("Convert2Lambda")
public class Filters implements Filter {
    private boolean returnFalse = false;
    private List<Filter> filters = new ArrayList<>();

    public void add(Filter filter) {
        if (filter != null) {
            filters.add(filter);
        }
    }

    @Override
    public boolean include(Crash crash) {
        if (returnFalse) {
            return false;
        }
        for (Filter filter : filters) {
            if (!filter.include(crash)) {
                return false;
            }
        }
        return true;
    }

    public void alcohol(boolean alcohol) {
        if (alcohol) {
            add(new Filter() {
                @Override
                public boolean include(Crash crash) {
                    return crash.isAlcohol();
                }
            });
        }
    }

    public void vehicle(boolean cars, boolean bikes, boolean peds) {
        if (cars && bikes && peds) {
            //do nothing
        } else if (!cars && !bikes && !peds) {
            returnFalse = true;
        } else  {
            add(new Filter() {
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
            });
        }
    }

    public void light(boolean day, boolean night, boolean twilight) {
        if (day && night && twilight) {
            return;
        } else if (!day && !night && !twilight) {
            returnFalse = true;
        } else {
            add(new Filter() {
                @Override
                public boolean include(Crash crash) {
                    switch (crash.getLight()) {
                        case 0:
                        case 1:
                            return day;
                        case 2:
                        case 3:
                            return night;
                        case 4:
                        case 5:
                            return twilight;
                    }
                    return false;
                }
            });
        }

    }
}
