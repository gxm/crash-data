package com.moulliet.metro.crash;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.moulliet.metro.mongo.PolygonQuery;

public class CrashQuery {

    private BasicDBObject query;

    public CrashQuery() {
        query = new BasicDBObject();
    }

    public BasicDBObject getQuery() {
        return query;
    }

    public void location(String north, String south, String east, String west) {
        new PolygonQuery(north, south, east, west).getQuery(query);
    }

    public void location() {
        new PolygonQuery().getQuery(query);
    }

    public void vehicle(boolean cars, boolean bikes, boolean peds) {
        if (cars && bikes && peds) {
            return;
        }
        if (!cars && !bikes && !peds) {
            query.append("bike", new BasicDBObject("$lt", 0));
        }
        if (!cars) {
            if (bikes && peds) {
                //bikes or peds > 0
                BasicDBList list = new BasicDBList();
                list.add(new BasicDBObject("bike", new BasicDBObject("$gt", 0)));
                list.add(new BasicDBObject("ped", new BasicDBObject("$gt", 0)));
                addOr(list);

            } else if (bikes) {
                //bikes > 0
                query.append("bike", new BasicDBObject("$gt", 0));
            } else if (peds) {
                //peds > 0
                query.append("ped", new BasicDBObject("$gt", 0));
            }
        } else {
            if (bikes) {
                query.append("ped", 0);
            } else if (peds) {
                query.append("bike", 0);
            } else {
                BasicDBList list = new BasicDBList();
                list.add(new BasicDBObject("bike", 0));
                list.add(new BasicDBObject("ped", 0));
                //todo this could cause an '$and' collision later
                query.append("$and", list);
            }
        }

    }

    public void alcohol(boolean alcohol) {
        if (alcohol) {
            query.append("alcohol", alcohol);
        }
    }

    public void hurt(boolean injury, boolean fatality) {
        if (injury && fatality) {
            BasicDBList list = new BasicDBList();
            list.add(new BasicDBObject("injury", new BasicDBObject("$gt", 0)));
            list.add(new BasicDBObject("fatality", new BasicDBObject("$gt", 0)));
            addOr(list);
        } else if (injury) {
            query.append("injury", new BasicDBObject("$gt", 0));
        } else if (fatality) {
            query.append("fatality", new BasicDBObject("$gt", 0));
        }
    }

    private void addOr(DBObject dbObject) {
        if (query.containsField("$or")) {
            BasicDBList andList = new BasicDBList();
            andList.add(new BasicDBObject("$or", dbObject));
            andList.add(new BasicDBObject("$or", query.get("$or")));
            query.remove("$or");
            //todo this could cause an '$and' collision later
            query.append("$and", andList);
        } else {

            query.append("$or", dbObject);
        }
    }

    public void light(boolean day, boolean night, boolean twilight) {
        if (day && night && twilight) {
            return;
        } else if (!day && !night && !twilight) {
            query.append("light", new BasicDBObject("$lt", 0));
        } else {
            BasicDBList list = new BasicDBList();
            if (day) {
                list.add(0);
                list.add(1);
            }
            if (night) {
                list.add(2);
                list.add(3);
            }
            if (twilight) {
                list.add(4);
                list.add(5);
            }
            query.append("light", new BasicDBObject("$in", list));
        }
    }

    public void surface(boolean dry, boolean wet, boolean snowIce) {
        if (dry && wet && snowIce) {
            return;
        } else if (!dry && !wet && !snowIce) {
            query.append("surface", new BasicDBObject("$lt", 0));
        } else {
            BasicDBList list = new BasicDBList();
            if (dry) {
                list.add(0);
                list.add(1);
            }
            if (wet) {
                list.add(2);
            }
            if (snowIce) {
                list.add(3);
                list.add(4);
            }
            query.append("surface", new BasicDBObject("$in", list));
        }
    }

    public void years(boolean y2007, boolean y2008, boolean y2009, boolean y2010, boolean y2011, boolean y2012) {
        if (y2007 && y2008 && y2009 && y2010 && y2011 && y2012) {
            return;
        } else if (!y2007 && !y2008 && !y2009 && !y2010 && !y2011 && !y2012) {
            query.append("year", new BasicDBObject("$lt", 0));
        } else {
            BasicDBList list = new BasicDBList();
            if (y2007) {
                list.add(2007);
            }
            if (y2008) {
                list.add(2008);
            }
            if (y2009) {
                list.add(2009);
            }
            if (y2010) {
                list.add(2010);
            }
            if (y2011) {
                list.add(2011);
            }
            if (y2012) {
                list.add(2012);
            }
            //todo - gfm - add 2013 and ...


            query.append("year", new BasicDBObject("$in", list));
        }
    }


    @Override
    public String toString() {
        return "CrashQuery{" +
                "crash=" + query +
                '}';
    }
}
