package com.moulliet.common;

/**
 *
 */
public class Timer {

    private long start;

    public Timer() {
        clear();
    }

    public void clear() {
        start = System.currentTimeMillis();
    }

    public long reset() {
        long millis = System.currentTimeMillis() - start;
        clear();
        return millis;
    }
}
