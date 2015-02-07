package com.moulliet.metro.shape;

import com.esri.core.geometry.Envelope;
import com.esri.core.geometry.Point;
import com.esri.core.geometry.Polygon;
import com.esri.dbf.DBFReader;
import com.esri.shp.ShpReader;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ShapePlay {


    public static void main(String[] args) throws IOException {

        List<Polygon> polygons = loadShapes();
        List<Map<String, Object>> descriptions = loadDescriptions();
        for (int i = 0; i < polygons.size(); i++) {
            Polygon polygon = polygons.get(i);
            Map<String, Object> desc = descriptions.get(i);

            String streetname = (String) desc.get("STREETNAME");
            Double length = (Double) desc.get("LENGTH");
            //Short type = (Short) desc.get("TYPE");
            if (streetname.startsWith("MORRISON BRG") && length > 334 && length < 335) {
                System.out.println(polygon.toString() + " " + desc);
                int pointCount = polygon.getPointCount();
                for (int j  = 0; j < pointCount; j++) {
                    Point point = polygon.getPoint(j);
                    System.out.println("point" + point);
                }
                //todo - gfm - 2/7/15 - convert into WSG84
                //todo - gfm - 2/7/15 - how to get access to raw data points?

            }
        }
    }

    public static List<Polygon> loadShapes() throws IOException {
        List<Polygon> polygons = new ArrayList<>();
        final File file = new File("/Users/greg/code/rlis/Feb2015/arterial/arterial.shp");
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
            System.out.println("polygon count " + count);
        }
        return polygons;
    }

    public static List<Map<String, Object>> loadDescriptions() throws IOException {
        List<Map<String, Object>> descriptions = new ArrayList<>();
        final File file = new File("/Users/greg/code/rlis/Feb2015/arterial/arterial.dbf");
        try (FileInputStream fileInputStream = new FileInputStream(file)) {
            int count = 0;
            final DBFReader dbfReader = new DBFReader(new DataInputStream(new BufferedInputStream(fileInputStream)));
            Map<String, Object> map = new HashMap<>();
            while (dbfReader.readRecordAsMap(map) != null) {
                descriptions.add(map);
                map = new HashMap<>();
                count++;

            }
            System.out.println("dbf count " + count);
        }
        return descriptions;
    }


}