package com.moulliet.metro.arterial;

import com.google.common.util.concurrent.AtomicDouble;
import com.moulliet.metro.crash.Point;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.MultiLineString;
import com.vividsolutions.jts.index.SpatialIndex;
import com.vividsolutions.jts.index.strtree.STRtree;
import com.vividsolutions.jts.linearref.LinearLocation;
import com.vividsolutions.jts.linearref.LocationIndexedLine;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.ObjectNode;
import org.geotools.data.FeatureSource;
import org.geotools.data.FileDataStore;
import org.geotools.data.FileDataStoreFinder;
import org.geotools.feature.FeatureCollection;
import org.geotools.util.NullProgressListener;
import org.opengis.feature.simple.SimpleFeature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class GeoArterials {

    private static final Logger logger = LoggerFactory.getLogger(GeoArterials.class);

    private static final ObjectMapper mapper = new ObjectMapper();
    private static ArrayNode multiLines = mapper.createArrayNode();
    private static final double MAX_SEARCH_DISTANCE = 26;
    private static SpatialIndex rtree;

    public static void main(String[] args) throws IOException {
        GeoArterials.loadArterials();
    }

    public static void loadArterials() throws IOException {
        File file = new File("/Users/greg/code/rlis/Feb2015/arterial/arterial.shp");
        FileDataStore store = FileDataStoreFinder.getDataStore(file);
        FeatureSource source = store.getFeatureSource();
        rtree = new STRtree();
        FeatureCollection features = source.getFeatures();
        AtomicDouble totalLength = new AtomicDouble();
        features.accepts(feature -> {
            SimpleFeature simpleFeature = (SimpleFeature) feature;
            MultiLineString geometry = (MultiLineString) simpleFeature.getDefaultGeometry();
            totalLength.addAndGet((Double) simpleFeature.getAttribute("LENGTH"));
            if (geometry != null) {
                Envelope envelope = geometry.getEnvelopeInternal();
                if (!envelope.isNull()) {
                    rtree.insert(envelope, new LocationIndexedLine(geometry));
                }
                addLines(geometry);
            }
        }, new NullProgressListener());
        logger.info("total length {}", totalLength);
    }

    static void addLines(MultiLineString geometry) {
        ArrayNode point = null;
        Coordinate[] coordinates = geometry.getCoordinates();
        for (Coordinate coordinate : coordinates) {
            if (point == null) {
                point = multiLines.addArray();
            }
            ObjectNode node = point.addObject();
            double[] wsg84 = Transform.toWSG84(coordinate.x, coordinate.y);
            node.put("lng", wsg84[0]);
            node.put("lat", wsg84[1]);
        }
    }

    public static JsonNode getMultiLine() throws IOException {
        return multiLines;
    }

    public static boolean isArterial(Point point) {
        double[] doubles = Transform.toOregon(point.getOriginals()[0], point.getOriginals()[1]);
        Coordinate coordinate = new Coordinate(doubles[0], doubles[1]);
        Envelope envelope = new Envelope();

        Envelope search = new Envelope(coordinate);
        search.expandBy(MAX_SEARCH_DISTANCE);
        /*
             * Query the spatial index for objects within the search envelope.
             * Note that this just compares the point envelope to the line envelopes
             * so it is possible that the point is actually more distant than
             * MAX_SEARCH_DISTANCE from a line.
             */
        List<LocationIndexedLine> lines = rtree.query(search);

        for (LocationIndexedLine indexedLine : lines) {
            LinearLocation linearLocation = indexedLine.project(coordinate);
            Coordinate extractPoint = indexedLine.extractPoint(linearLocation);
            double distance = extractPoint.distance(coordinate);
            if (distance < MAX_SEARCH_DISTANCE) {
                return true;
            }
        }

        return false;
    }


}
