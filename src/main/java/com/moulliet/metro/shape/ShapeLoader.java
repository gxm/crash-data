package com.moulliet.metro.shape;

import com.esri.core.geometry.Envelope;
import com.esri.core.geometry.Point;
import com.esri.core.geometry.Polygon;
import com.esri.dbf.DBFReader;
import com.esri.shp.ShpReader;
import org.apache.commons.io.FileUtils;
import org.geotools.referencing.CRS;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.*;

public class ShapeLoader {

    private static final Logger logger = LoggerFactory.getLogger(ShapeLoader.class);

    private String path;
    private String fileRoot;

    private static MathTransform transform;

    public ShapeLoader(String path, String fileRoot) {
        this.path = path;
        this.fileRoot = fileRoot;
    }

    public List<Shape> loadPolygons() throws IOException {
        parseWellKnownText();
        List<Shape> shapes = new ArrayList<>();
        List<Polygon> polygons = loadShapes();
        List<Map<String, Object>> descriptions = loadDescriptions();
        for (int i = 0; i < polygons.size(); i++) {
            Polygon polygon = polygons.get(i);
            Map<String, Object> desc = descriptions.get(i);

            Shape shape = new Shape(desc);
            int pointCount = polygon.getPointCount();
            for (int j  = 0; j < pointCount; j++) {
                Point point = polygon.getPoint(j);
                double[] transformed = transform(point.getX(), point.getY());
                shape.getPoints().add(new com.moulliet.metro.crash.Point(transformed[0], transformed[1]));
            }

            shapes.add(shape);
        }
        return shapes;
    }

    private static double[] transform(Double lon, Double lat) {
        double[] longLat = {lon, lat};
        if (transform == null) {
            return longLat;
        }
        try {
            double[] transformedLongLat = {lon, lat};
            transform.transform(longLat, 0, transformedLongLat, 0, 1);
            return transformedLongLat;
        } catch (TransformException e) {
            logger.warn("unable to transform " + Arrays.toString(longLat), e);
            return longLat;
        }
    }

    public List<Polygon> loadShapes() throws IOException {
        List<Polygon> polygons = new ArrayList<>();
        final File file = new File(path + fileRoot +  ".shp");
        try (FileInputStream fileInputStream = new FileInputStream(file)) {
            int count = 0;
            Envelope envelope = new Envelope();
            ShpReader shpReader = new ShpReader(new DataInputStream(new BufferedInputStream(fileInputStream)));
            while (shpReader.hasMore()) {
                Polygon polygon = new Polygon();
                shpReader.queryPolygon(polygon);
                polygon.queryEnvelope(envelope);
                count++;
                polygons.add(polygon);
            }
            logger.info("points count " + count);
        }
        return polygons;
    }

    public List<Map<String, Object>> loadDescriptions() throws IOException {
        List<Map<String, Object>> descriptions = new ArrayList<>();
        File file = new File(path + fileRoot + ".dbf");
        try (FileInputStream fileInputStream = new FileInputStream(file)) {
            int count = 0;
            DBFReader dbfReader = new DBFReader(new DataInputStream(new BufferedInputStream(fileInputStream)));
            Map<String, Object> map = new HashMap<>();
            while (dbfReader.readRecordAsMap(map) != null) {
                descriptions.add(map);
                map = new HashMap<>();
                count++;
            }
            logger.info("dbf count " + count);
        }
        return descriptions;
    }

    public void parseWellKnownText() {
        try {
            File file = new File(path + fileRoot + ".prj");
            String wkt = FileUtils.readFileToString(file);
            logger.info("using wkt " + wkt);
            CoordinateReferenceSystem coordinateReferenceSystem = CRS.parseWKT(wkt);
            transform = CRS.findMathTransform(coordinateReferenceSystem, DefaultGeographicCRS.WGS84, true);
        } catch (Exception e) {
            logger.warn("unable to parse transform ", e);
        } 
    }

}