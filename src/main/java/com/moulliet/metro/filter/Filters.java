package com.moulliet.metro.filter;

import com.moulliet.metro.arterial.EsriArterials;
import com.moulliet.metro.crash.Crash;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("Convert2Lambda")
public class Filters implements Filter {
    private boolean returnFalse = false;
    private List<Filter> filters = new ArrayList<>();

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

    private void add(Filter filter) {
        if (filter != null) {
            filters.add(filter);
        }
    }

    public void location(double north, double south, double east, double west) {
        add(new Filter() {
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
        });
    }

    public void sobriety(boolean alcohol, boolean drug, boolean sober) {
        if (alcohol && drug && sober) {
            return;
        } else if (!alcohol && !drug && !sober) {
            returnFalse = true;
        } else {
            add(new Filter() {
                @Override
                public boolean include(Crash crash) {
                    if (crash.isAlcohol() && alcohol) {
                        return true;
                    } else if (crash.isDrug() && drug) {
                        return true;
                    } else if (sober) {
                        return !crash.isDrug() && !crash.isAlcohol();
                    }
                    return false;
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

    public void surface(boolean dry, boolean wet, boolean snowIce) {
        if (dry && wet && snowIce) {
            return;
        } else if (!dry && !wet && !snowIce) {
            returnFalse = true;
        } else {
            add(new Filter() {
                @Override
                public boolean include(Crash crash) {
                    switch (crash.getSurface()) {
                        case 0:
                        case 1:
                            return dry;
                        case 2:
                            return wet;
                        case 3:
                        case 4:
                            return snowIce;
                    }
                    return false;
                }
            });
        }
    }

    public void years(boolean y2007, boolean y2008, boolean y2009,
                      boolean y2010, boolean y2011, boolean y2012, boolean y2013) {
        if (y2007 && y2008 && y2009 && y2010 && y2011 && y2012 && y2013) {
            return;
        } else if (!y2007 && !y2008 && !y2009 && !y2010 && !y2011 && !y2012 && !y2013) {
            returnFalse = true;
        } else {
            add(new Filter() {
                @Override
                public boolean include(Crash crash) {
                    switch (crash.getYear()) {
                        case 2007:
                            return y2007;
                        case 2008:
                            return y2008;
                        case 2009:
                            return y2009;
                        case 2010:
                            return y2010;
                        case 2011:
                            return y2011;
                        case 2012:
                            return y2012;
                        case 2013:
                            return y2013;
                    }
                    return false;
                }
            });
        }
    }

    public void type(boolean angle, boolean headOn, boolean rearEnd, boolean sideSwipe, boolean turning, boolean other) {
        if (angle && headOn && rearEnd && turning && sideSwipe && other) {
            return;
        } else if (!angle && !headOn && !rearEnd && !turning && !sideSwipe && !other) {
            returnFalse = true;
        } else {
            add(new Filter() {
                @Override
                public boolean include(Crash crash) {
                    switch (crash.getType()) {
                        case "1":
                            return angle;
                        case "2":
                            return headOn;
                        case "3":
                            return rearEnd;
                        case "4":
                        case "5":
                            return sideSwipe;
                        case "6":
                            return turning;
                        default:
                            return other;
                    }
                }
            });
        }
    }

    public void severity(boolean fatal, boolean injuryA, boolean injuryB, boolean injuryC, boolean property) {
        if (fatal && injuryA && injuryB && injuryC && property) {
            return;
        } else if (!fatal && !injuryA && !injuryB && !injuryC && !property) {
            returnFalse = true;
        } else {
            add(new Filter() {
                @Override
                public boolean include(Crash crash) {
                    switch (crash.getSeverity()) {
                        case 0:
                            return property;
                        case 1:
                            return injuryC;
                        case 2:
                            return injuryB;
                        case 3:
                            return injuryA;
                        case 4:
                            return fatal;
                    }
                    return false;
                }
            });
        }
    }

    public void arterial(boolean arterial, boolean local) {
        if (arterial && local) {
            return;
        } else if (!arterial && !local) {
            returnFalse = true;
        }
        add(new Filter() {
            @Override
            public boolean include(Crash crash) {
                if (arterial) {
                    return EsriArterials.isArterial(crash.getPoint());
                } else {
                    return !EsriArterials.isArterial(crash.getPoint());
                }
            }
        });
    }
}


