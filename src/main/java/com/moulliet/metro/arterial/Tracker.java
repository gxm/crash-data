package com.moulliet.metro.arterial;

import com.esri.core.geometry.ProgressTracker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Tracker extends ProgressTracker {
    private static final Logger logger = LoggerFactory.getLogger(Tracker.class);

    private String name;

    public Tracker(String name) {
        this.name = name;
    }

    @Override
    public boolean progress(int step, int totalExpectedSteps) {
        logger.debug("progress {} step {} expected {}", name, step, totalExpectedSteps);
        return true;
    }
}
