package com.moulliet.metro.arterial;

import com.moulliet.metro.shape.ShapeLoader;
import org.geotools.referencing.CRS;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Transform {
    private static final Logger logger = LoggerFactory.getLogger(Transform.class);

    private static MathTransform toOregon;
    private static MathTransform toWSG84;

    static {
        try {
            CoordinateReferenceSystem crs = ShapeLoader.getCoordinateReferenceSystem("/Users/greg/code/rlis/Feb2015/arterial/arterial.prj");
            toOregon = CRS.findMathTransform(DefaultGeographicCRS.WGS84, crs, true);
            toWSG84 = CRS.findMathTransform(crs, DefaultGeographicCRS.WGS84, true);
        } catch (Exception e) {
            logger.warn("unable to load transforms", e);
        }
    }

    public static double[] toWSG84(double x, double y) {
        return transform(x, y, toWSG84);
    }

    public static double[] toOregon(double lng, double lat) {
        return transform(lng, lat, toOregon);
    }

    private static double[] transform(double x, double y, MathTransform transform) {
        double[] xy = {x, y};
        double[] transformed = {x, y};
        try {
            transform.transform(xy, 0, transformed, 0, 1);
            return transformed;
        } catch (TransformException e) {
            logger.warn("unexpected transform exception " + x + " " + y, e);
            return xy;
        }
    }
}
