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

public class ShapePlay {

    private static final Logger logger = LoggerFactory.getLogger(ShapePlay.class);

    public static final String SHAPE_FILES = "/Users/greg/code/rlis/Feb2015/arterial/";
    private static MathTransform transform;

    public static void main(String[] args) throws IOException {
        parseWellKnownText();
        List<Polygon> polygons = loadShapes();
        List<Map<String, Object>> descriptions = loadDescriptions();
        for (int i = 0; i < polygons.size(); i++) {
            Polygon polygon = polygons.get(i);
            Map<String, Object> desc = descriptions.get(i);

            String streetname = (String) desc.get("STREETNAME");
            Double length = (Double) desc.get("LENGTH");
            //Short type = (Short) desc.get("TYPE");
            if (streetname.startsWith("MORRISON BRG") && length > 334 && length < 335) {
                logger.info(polygon.toString() + " " + desc);
                int pointCount = polygon.getPointCount();
                for (int j  = 0; j < pointCount; j++) {
                    Point point = polygon.getPoint(j);
                    double[] transformed = transform(point.getX(), point.getY());
                    System.out.println(point.getX() + "," + point.getY() + "," + transformed[0] + "," + transformed[1]);
                }
            }
        }
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

    public static List<Polygon> loadShapes() throws IOException {
        List<Polygon> polygons = new ArrayList<>();
        final File file = new File(SHAPE_FILES + "arterial.shp");
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
            logger.info("polygon count " + count);
        }
        return polygons;
    }

    public static List<Map<String, Object>> loadDescriptions() throws IOException {
        List<Map<String, Object>> descriptions = new ArrayList<>();
        File file = new File(SHAPE_FILES + "arterial.dbf");
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

    public static void parseWellKnownText() {
        try {
            File file = new File(SHAPE_FILES + "arterial.prj");
            String wkt = FileUtils.readFileToString(file);
            logger.info("using wkt " + wkt);
            CoordinateReferenceSystem coordinateReferenceSystem = CRS.parseWKT(wkt);
            transform = CRS.findMathTransform(coordinateReferenceSystem, DefaultGeographicCRS.WGS84, true);
        } catch (Exception e) {
            logger.warn("unable to parse transform ", e);
        } 
    }

}