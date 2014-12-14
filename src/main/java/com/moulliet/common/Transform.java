package com.moulliet.common;

import com.vividsolutions.jts.geom.Point;
import org.geotools.data.FileDataStore;
import org.geotools.data.FileDataStoreFinder;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.geometry.jts.JTS;
import org.geotools.referencing.CRS;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;

import java.io.File;
import java.io.IOException;

public class Transform {

    public static void main(String[] args) throws IOException, FactoryException, TransformException {
        // epsg 2992 to 4326

        /*
        PROJCS["NAD_1983_Oregon_Statewide_Lambert_Feet_Intl",GEOGCS["GCS_North_American_1983",DATUM["D_North_American_1983",SPHEROID["GRS_1980",6378137.0,298.257222101]],PRIMEM["Greenwich",0.0],UNIT["Degree",0.0174532925199433]],PROJECTION["Lambert_Conformal_Conic"],PARAMETER["False_Easting",1312335.958005249],PARAMETER["False_Northing",0.0],PARAMETER["Central_Meridian",-120.5],PARAMETER["Standard_Parallel_1",43.0],PARAMETER["Standard_Parallel_2",45.5],PARAMETER["Latitude_Of_Origin",41.75],UNIT["Foot",0.3048]]

        {"x":776252.7670898438,"y":1372831.180480957} -122.59074 45.49744
        {"x":753744.2113037109,"y":1412394.1489257812} -122.68259 45.6043
        {"x":749037.4249267578,"y":1389433.2451171875} -122.69857 45.541004
        {"x":711005.5098876953,"y":1430853.6298828125} -122.85156 45.651657
        {"x":748852.5103149414,"y":1402618.329711914} -122.70067 45.577137
        {"x":753075.2366943359,"y":1368303.5167236328} -122.680626 45.48338
        {"x":782778.7202758789,"y":1380547.955078125} -122.56606 45.519047
        {"x":756754.3327026367,"y":1379004.4918823242} -122.66738 45.512978
        {"x":754635.2000732422,"y":1394357.2000732422} -122.677185 45.555298
        {"x":740484.397277832,"y":1378001.2814941406} -122.73072 45.509033
        */

        FileDataStore store = FileDataStoreFinder.getDataStore(new File("/Users/greg/Google Drive/metro/Crash_07_12/Crashes_2012.shp"));

        SimpleFeatureSource featureSource = store.getFeatureSource();
        System.out.println("getBounds " + featureSource.getBounds());
        System.out.println("getInfo " + featureSource.getInfo());
        System.out.println("getInfo.getCRS " + featureSource.getInfo().getCRS());
        System.out.println("getName " + featureSource.getName());
        System.out.println("getSchema " + featureSource.getSchema());
        System.out.println("getSupportedHints " + featureSource.getSupportedHints());
        //System.out.println(featureSource.);
        SimpleFeatureCollection features = featureSource.getFeatures();

        int count = 0;
        MathTransform transform = CRS.findMathTransform(featureSource.getInfo().getCRS(), DefaultGeographicCRS.WGS84, true);
        try (SimpleFeatureIterator iterator = features.features()) {
            while (iterator.hasNext()) {
                SimpleFeature feature = iterator.next();
                count++;

                //System.out.println(feature.getAttribute("the_geom"));
                Point point = (Point) feature.getDefaultGeometry();
                Point xformed = (Point) JTS.transform(point, transform);

                Double odotLng = (Double) feature.getAttribute("LONGTD_DD");
                Double odotLat = (Double) feature.getAttribute("LAT_DD");
                double lngDiff = xformed.getX() - odotLng;
                if (lngDiff > 0.0001) {
                    System.out.println(xformed.getY() + " " + xformed.getX() + " lat " + odotLat + " long " + odotLng  + " " + lngDiff + " " + feature) ;
                }

            }
        }
        System.out.println("count " + count);
    }
}
