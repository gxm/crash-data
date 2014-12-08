package com.moulliet.metro.load;

import com.mongodb.DBObject;
import com.mongodb.util.JSON;
import com.moulliet.metro.crash.Crash;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class LoadShapefile {

    private static final Logger logger = LoggerFactory.getLogger(LoadShapefile.class);


    public static List<Crash> load(String file) throws IOException {
        logger.info("loading {}", file);
        List<Crash> allCrashes = new ArrayList<>();
        BufferedReader br = new BufferedReader(new FileReader(file));
        String line = br.readLine();
        while (line != null) {
            DBObject dbObject = (DBObject) JSON.parse(line);
            Object sink = dbObject.get("Sink");
            if (sink == null || (int) sink != 1) {
                allCrashes.add(new Crash(dbObject));
            }
            line = br.readLine();
        }
        br.close();
        return allCrashes;
    }


}
