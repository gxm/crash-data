package com.moulliet.metro.arterial;

import com.moulliet.metro.crash.Point;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class Interpolate {

    private static final Logger logger = LoggerFactory.getLogger(Interpolate.class);

    public static List<Point> interpolate(List<Point> incoming) {
        Set<Point> interpolated = new TreeSet<>(incoming);
        for (int i = 0; i <= incoming.size() - 2; i++) {
            Point one = incoming.get(i);
            Point two = incoming.get(i + 1);
            int latDiff = - one.getLatitiudeDifference(two);
            int lngDiff = - one.getLongitudeDifference(two);
            if (lngDiff == 0) {
                for (int j = 1; j <= latDiff; j++) {
                    interpolated.add(one.offset(0, j));
                }
            } else if (latDiff == 0) {
                for (int j = 1; j <= lngDiff; j++) {
                    interpolated.add(one.offset(j, 0));
                }
            } else {
                int steps = Math.min(Math.abs(latDiff), Math.abs(lngDiff));
                int lngStep = (int) ((float) lngDiff / (float) steps);
                int latStep = (int) ((float) latDiff / (float) steps);
                for (int j = 0; j < steps; j++) {
                    interpolated.add(one.offset(lngStep , latStep));
                }
            }
        }
        return new ArrayList<>(interpolated);
    }
}
