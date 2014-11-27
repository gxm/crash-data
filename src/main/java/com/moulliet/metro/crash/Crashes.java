package com.moulliet.metro.crash;

import com.mongodb.DBObject;
import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class Crashes {
    private static final Logger logger = LoggerFactory.getLogger(Crashes.class);
    private final List<Crash> crashes = new ArrayList<>();
    private final Map<Point, AtomicInteger> pointMap = new TreeMap<Point, AtomicInteger>();
    private CrashTotals crashTotals = new CrashTotals();
    //todo - gfm - put this in a web page
    public static final String SUMMARY = "ODOT Data Summary - https://zigzag.odot.state.or.us/uniquesig8efc049cfb97eaafba98df6f7ee94468b7dd48e598ddaa2c5206da1b7765dd84fe1ba2942e431c37dcacffc1dd7fc9c9/uniquesig0/tad/TVC/documents/CDS501_DataExtract_Layout.doc";
    public static final String MANUAL = "ODOT Full Data Manual - http://www.oregon.gov/ODOT/TD/TDATA/car/docs/2007codemanualversion2.0.pdf";
    public static final String COLUMN_HEADERS = "Crash ID,Record Type,Vehicle ID,Participant ID,Participant Display Seq#,Vehicle Coded Seq#,Participant Vehicle Seq#,Serial #,Crash Month,Crash Day,Crash Year,Week Day Code,Crash Hour,County Code,City Section ID,Urban Area Code,Functional Class Code,NHS Flag,Highway Number,Highway Suffix,Roadway Number,Highway Component,Mileage Type,Connection Number,Linear Reference System (LRS),Latitude Degrees,Latitude Minutes,Latitude Seconds,Longitude Degrees,Longitude Minutes,Longitude Seconds,Special Jurisdiction,Jurisdiction Group,Street Number,Nearest Intersecting Street Number,Intersection Sequence Number,Distance from Intersection,Direction From Intersection,Milepoint,Posted Speed Limit,Road Character,Off Roadway Flag,Intersection Type,Intersection Related Flag,Roundabout Flag,Driveway Related Flag,Number of Lanes,Number of Turning Legs,Median Type,Impact Location,Crash Type,Collision Type,Crash Severity,Weather Condition,Road Surface Condition,Light Condition,Traffic Control Device (TCD),TCD Functional Flag,Investigating Agency,Crash Level Event 1 Code,Crash Level Event 2 Code,Crash Level Event 3 Code,Crash Level Cause 1 Code,Crash Level Cause 2 Code,Crash Level Cause 3 Code,School Zone Indicator,Work Zone Indicator,Alcohol-Involved Flag,Drugs Involved Flag,Speed Involved Flag,Crash Level Hit & Run Flag,Population Range Code,Road Control,Route Type,Route Number,Region ID,District ID,Segment Marker ID,Segment Point LRS Measure,Unlocatable Flag,Total Vehicle Count,Total Fatality Count,Total Serious Injury (Inj-A) Count,Total Moderate Injury (Inj-B) Count,Total Minor Injury (Inj-C) Count,Total Non-Fatal Injury Count,Total Count of Un-Injured  Children Age 00-04,Total Count of Un-Injured Persons,Total Pedestrian Count,Total Pedestrian Fatality Count,Total Pedestrian Non-Fatal Injury Count,Total Pedalcyclist Count,Total Pedalcyclist Fatality Count,Total Pedalcyclist Non-Fatal Injury Count,Total Unknown Non-Motorist Count,Total Unknown Non-Motorist Fatality Count,Total Unknown Non-Motorist Injury Count,Total Vehicle Occupant Count,Total Count of Persons Involved,Total Quantity of Persons Using Safety Equipment  ,Total Quantity of Persons Not Using Safety Equipment,\"Total Quantity of Persons Safety Equipment \"\"Use Unknown\"\"\",Vehicle Ownership Code,Vehicle Special Use Code,Vehicle Type Code,Emergency Use Flag,Number of Trailers,Vehicle Movement Code,Vehicle Travel Direction From,Vehicle Travel Direction To,Vehicle Action Code,Vehicle Cause 1 Code,Vehicle Cause 2 Code,Vehicle Cause 3 Code,Vehicle Event 1 Code,Vehicle Event 2 Code,Vehicle Event 3 Code,Vehicle Exceeded Posted Speed Flag,Vehicle Hit & Run Flag,Safety Equipment Used Quantity,Safety Equipment Un-used Quantity,Safety Equipment Use Unknown Quantity,Vehicle Occupant Count,Vehicle Striking Flag,Participant Type Code,Participant Hit & Run Flag,Public Employee Flag,Sex,Age,Driver License Status,Driver Residence,Injury Severity,Participant Safety Equipment Use Code,Airbag Deployment,Non-Motorist Movement Code,Non-Motorist Travel Direction From,Non-Motorist Travel Direction To,Non-Motorist Location,Participant Action,Participant Error 1 Code,Participant Error 2 Code,Participant Error 3 Code,Participant Cause 1 Code,Participant Cause 2 Code,Participant Cause 3 Code,Participant Event 1 Code,Participant Event 2 Code,Participant Event 3 Code,BAC Test Results Code,Alcohol Use Reported,Drug Use Reported,Participant Striker Flag";

    public void aggregatedCrashes(OutputStream stream) throws IOException {
        consolidatePoints();
        int max = 0;
        JsonFactory jsonFactory = new JsonFactory();
        JsonGenerator json = jsonFactory.createJsonGenerator(stream);
        json.writeStartObject();
        json.writeArrayFieldStart("data");
        for (Map.Entry<Point, AtomicInteger> entry : pointMap.entrySet()) {
            Point key = entry.getKey();
            int value = entry.getValue().get();
            max = Math.max(max, value);
            json.writeStartObject();
            json.writeStringField("lat", key.getY());
            json.writeStringField("lng", key.getX());
            json.writeNumberField("count", value);
            json.writeEndObject();
        }
        json.writeEndArray();
        json.writeNumberField("max", max);
        json.writeNumberField("total", crashTotals.getTotal());
        crashTotals.write(json);
        json.writeEndObject();
        json.flush();
        logger.debug("returned " + pointMap.entrySet().size() + " elements with max of " + max);
    }

    public void csvCrashes(OutputStream stream) throws IOException {
        writeLine(COLUMN_HEADERS, stream);
        System.out.println(crashes.size());
        for (Crash crash : crashes) {
            List<String> records = ((Crash) crash).getRecords();
            for (String record : records) {
                writeLine(record, stream);
            }
            stream.flush();
        }

    }

    private void writeLine(String line, OutputStream stream) throws IOException {
        stream.write(line.getBytes());
        stream.write("\r\n".getBytes());
    }

    public void loadResults(Iterator<DBObject> dbObjectIterator, DecimalFormat format) {
        while (dbObjectIterator.hasNext()) {
            DBObject next = dbObjectIterator.next();
            crashes.add(new Crash(next, format));
        }
        System.out.println(crashes.size());
    }

    private void consolidatePoints() {
        for (Crash crash : crashes) {
            addPointToMap(crash.getPoint());
            crashTotals.addCrash(crash);
        }
    }

    private void addPointToMap(Point point) {
        AtomicInteger integer = pointMap.get(point);
        if (null == integer) {
            pointMap.put(point, new AtomicInteger(1));
        } else {
            integer.incrementAndGet();
        }
    }

}
