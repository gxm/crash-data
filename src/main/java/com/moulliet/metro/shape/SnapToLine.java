package com.moulliet.metro.shape;

import com.moulliet.metro.crash.Crash;
import com.moulliet.metro.crash.Crashes;
import com.vividsolutions.jts.geom.*;
import com.vividsolutions.jts.index.SpatialIndex;
import com.vividsolutions.jts.index.strtree.STRtree;
import com.vividsolutions.jts.linearref.LinearLocation;
import com.vividsolutions.jts.linearref.LocationIndexedLine;
import org.geotools.data.FeatureSource;
import org.geotools.data.FileDataStore;
import org.geotools.data.FileDataStoreFinder;
import org.geotools.feature.FeatureCollection;
import org.geotools.referencing.CRS;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.geotools.util.NullProgressListener;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class SnapToLine {

    public static void main(String[] args) throws Exception {

        /*
         * Open a shapefile. You should choose one with line features
         * (LineString or MultiLineString geometry)
         *
         */
        File file = new File("/Users/greg/code/rlis/Feb2015/arterial/arterial.shp");

        FileDataStore store = FileDataStoreFinder.getDataStore(file);
        FeatureSource source = store.getFeatureSource();

        // Check that we have line features
        Class<?> geomBinding = source.getSchema().getGeometryDescriptor().getType().getBinding();
        boolean isLine = geomBinding != null
                && (LineString.class.isAssignableFrom(geomBinding) ||
                MultiLineString.class.isAssignableFrom(geomBinding));

        if (!isLine) {
            System.out.println("This example needs a shapefile with line features");
            return;
        }

        final SpatialIndex rtree = new STRtree();
        FeatureCollection features = source.getFeatures();
        System.out.println("Slurping in features ...");
        features.accepts(feature -> {
            SimpleFeature simpleFeature = (SimpleFeature) feature;
            Geometry geom = (MultiLineString) simpleFeature.getDefaultGeometry();
            // Just in case: check for  null or empty geometry
            if (geom != null) {
                Envelope env = geom.getEnvelopeInternal();
                if (!env.isNull()) {
                    rtree.insert(env, new LocationIndexedLine(geom));
                }
            }
        }, new NullProgressListener());

        //todo - gfm - 2/14/15 - figure out how to load real Oregon Lambert data points
        CoordinateReferenceSystem crs = ShapeLoader.getCoordinateReferenceSystem("/Users/greg/code/rlis/Feb2015/arterial/arterial.prj");
        MathTransform transform = CRS.findMathTransform(DefaultGeographicCRS.WGS84, crs, true);
        List<Coordinate> points = new ArrayList<>();
        List<Crash> crashes = Crashes.loadCrashes();

        for (Crash crash : crashes) {
            double lat = (double) crash.getLat();
            double lng = (double) crash.getLng();
            double[] longLat = {lng, lat};
            double[] transformedLongLat = {lng, lat};
            transform.transform(longLat, 0, transformedLongLat, 0, 1);
            points.add(new Coordinate(transformedLongLat[0], transformedLongLat[1]));
        }

        /*
         * We defined the maximum distance that a line can be from a point
         * to be a candidate for snapping (1% of the width of the feature
         * bounds for this example).
         */
        final double MAX_SEARCH_DISTANCE = 26;

        int pointsProcessed = 0;
        int pointsSnapped = 0;

        for (Coordinate coordinate : points) {
            pointsProcessed++;
            // Get point and create search envelope
            Envelope search = new Envelope(coordinate);
            search.expandBy(MAX_SEARCH_DISTANCE);

            /*
             * Query the spatial index for objects within the search envelope.
             * Note that this just compares the point envelope to the line envelopes
             * so it is possible that the point is actually more distant than
             * MAX_SEARCH_DISTANCE from a line.
             */
            List<LocationIndexedLine> lines = rtree.query(search);

            // Initialize the minimum distance found to our maximum acceptable
            // distance plus a little bit
            double minDist = MAX_SEARCH_DISTANCE + 1.0e-6;
            Coordinate minDistPoint = null;

            for (LocationIndexedLine line : lines) {
                LinearLocation here = line.project(coordinate);
                Coordinate extractPoint = line.extractPoint(here);
                double dist = extractPoint.distance(coordinate);
                if (dist < minDist) {
                    minDist = dist;
                    minDistPoint = extractPoint;
                }
            }


            if (minDistPoint == null) {
                // No line close enough to snap the point to
                System.out.println(coordinate + "- X");

            } else {
                System.out.printf("%s - snapped by moving %.4f\n",
                        coordinate.toString(), minDist);
                pointsSnapped++;
            }
        }

        System.out.println("found crashes " + crashes.size());
        System.out.printf("Processed %d points. \nSnapped %d points.\n\n", pointsProcessed, pointsSnapped);

        System.out.println("MAX_SEARCH_DISTANCE " + MAX_SEARCH_DISTANCE);
    }
}