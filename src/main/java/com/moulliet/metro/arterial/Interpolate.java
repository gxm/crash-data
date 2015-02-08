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
            int difference = - one.getLatitiudeDifference(incoming.get(i + 1));
            for (int j = 1; j <= difference; j++) {
                interpolated.add(one.offset(0, j));
            }
        }
        return interpolated;
    }
}
