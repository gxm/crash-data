package com.moulliet.metro.arterial;

import com.moulliet.metro.crash.Point;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class Interpolate {

    private static final Logger logger = LoggerFactory.getLogger(Interpolate.class);

    public static List<Point> interpolate(List<Point> incoming) {
        List<Point> interpolated = new ArrayList<>(incoming);
        for (int i = 0; i <= incoming.size() - 2; i++) {
            Point one = incoming.get(i);
            Point two = incoming.get(i + 1);
            int latDiff = - one.getLatitiudeDifference(two);
            for (int j = 1; j <= latDiff; j++) {
                interpolated.add(one.offset(0, j));
            }
            int lngDiff = - one.getLongitudeDifference(two);
            for (int j = 1; j <= lngDiff; j++) {
                interpolated.add(one.offset(j, 0));
            }
        }
        return interpolated;
    }
}
