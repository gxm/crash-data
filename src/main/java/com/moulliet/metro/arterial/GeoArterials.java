package com.moulliet.metro.arterial;

import com.moulliet.metro.crash.Point;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.MultiLineString;
import com.vividsolutions.jts.index.SpatialIndex;
import com.vividsolutions.jts.index.strtree.STRtree;
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

public class GeoArterials {

    private static final Logger logger = LoggerFactory.getLogger(GeoArterials.class);

    private static final ObjectMapper mapper = new ObjectMapper();
    private static ArrayNode multiLines = mapper.createArrayNode();

    private static Coordinate center;
    //private static final Point center = new Point(-122.66534, 45.52422);
    private static final float DELTA = 0.0002f;

    public static void main(String[] args) throws IOException {
        GeoArterials.loadArterials();
    }

    public static void loadArterials() throws IOException {
        double[] centerTransform = Transform.toOregon(-122.647, 45.55534);
        center = new Coordinate(centerTransform[0], centerTransform[1]);

        File file = new File("/Users/greg/code/rlis/Feb2015/arterial/arterial.shp");
        FileDataStore store = FileDataStoreFinder.getDataStore(file);
        FeatureSource source = store.getFeatureSource();
        SpatialIndex rtree = new STRtree();
        FeatureCollection features = source.getFeatures();
        features.accepts(feature -> {
            SimpleFeature simpleFeature = (SimpleFeature) feature;
            MultiLineString geometry = (MultiLineString) simpleFeature.getDefaultGeometry();
            if (geometry != null) {
                //logger.info("geometry {}", geometry);
                Envelope envelope = geometry.getEnvelopeInternal();
                if (!envelope.isNull()) {
                    rtree.insert(envelope, new LocationIndexedLine(geometry));
                }
                addLines(geometry);
            }
        }, new NullProgressListener());

    }

    static void addLines(MultiLineString geometry) {
        ArrayNode point = null;
        Coordinate[] coordinates = geometry.getCoordinates();
        for (Coordinate coordinate : coordinates) {
            if (coordinate.distance(center) < 5500) {
                if (point == null) {
                    point = multiLines.addArray();
                }
                ObjectNode node = point.addObject();
                double[] wsg84 = Transform.toWSG84(coordinate.x, coordinate.y);
                node.put("lng", wsg84[0]);
                node.put("lat", wsg84[1]);
            }
        }
    }

    public static JsonNode getMultiLine() throws IOException {
        return multiLines;
    }

    public static boolean isArterial(Point point) {
        return false;
    }


}
