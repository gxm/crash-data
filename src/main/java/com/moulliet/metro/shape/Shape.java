package com.moulliet.metro.shape;

import com.moulliet.metro.crash.Point;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Shape {
    List<Point> points;
    Map<String, Object> descriptions;

    public Shape(Map<String, Object> descriptions) {
        this.points = new ArrayList<>();
        this.descriptions = descriptions;
    }

    public List<Point> getPoints() {
        return points;
    }

    public Map<String, Object> getDescriptions() {
        return descriptions;
    }
}
