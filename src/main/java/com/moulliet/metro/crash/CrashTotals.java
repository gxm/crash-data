package com.moulliet.metro.crash;

import org.codehaus.jackson.JsonGenerator;

import java.io.IOException;

/**
 *
 */
public class CrashTotals {

    private int total = 0;
    private int cars = 0;
    private int bikes = 0;
    private int peds = 0;
    private int alcohol = 0;
    private int injury = 0;
    private int fatality = 0;
    private int day = 0;
    private int night = 0;
    private int twilight = 0;
    private int dry = 0;
    private int wet = 0;
    private int snowIce = 0;
    private int angle = 0;
    private int headOn = 0;
    private int rearEnd = 0;
    private int sideSwipe = 0;
    private int turning = 0;
    private int other = 0;

    public void addCrash(Crash crash) {
        total++;
        if (crash.getBike() > 0) bikes++;
        if (crash.getPed() > 0) peds++;
        if ((crash.getBike() + crash.getPed()) == 0) {
            cars++;
        }
        if (crash.isAlcohol()) alcohol++;
        if (crash.getInjury() > 0) injury++;
        if (crash.getFatality() > 0) fatality++;
        surface(crash);
        light(crash);
        type(crash);
    }

    public void write(JsonGenerator json) throws IOException {
        json.writeObjectFieldStart("summary");
        json.writeNumberField("total", total);
        json.writeNumberField("cars", cars);
        json.writeNumberField("bikes", bikes);
        json.writeNumberField("peds", peds);
        json.writeNumberField("alcohol", alcohol);
        json.writeNumberField("injury", injury);
        json.writeNumberField("fatality", fatality);
        json.writeNumberField("day", day);
        json.writeNumberField("night", night);
        json.writeNumberField("twilight", twilight);
        json.writeNumberField("dry", dry);
        json.writeNumberField("wet", wet);
        json.writeNumberField("snowIce", snowIce);
        json.writeNumberField("angle", angle);
        json.writeNumberField("headOn", headOn);
        json.writeNumberField("rearEnd", rearEnd);
        json.writeNumberField("sideSwipe", sideSwipe);
        json.writeNumberField("turning", turning);
        json.writeNumberField("other", other);
        json.writeEndObject();

    }

    private void surface(Crash crash) {
        switch (crash.getSurface()) {
            case 0:
            case 1:
                dry++;
                break;
            case 2:
                wet++;
                break;
            case 3:
            case 4:
                snowIce++;
                break;
            default:
                dry++;
                break;
        }
    }

    private void light(Crash crash) {
        switch (crash.getLight()) {
            case 0:
            case 1:
                day++;
                break;
            case 2:
            case 3:
                night++;
                break;
            case 4:
            case 5:
                twilight++;
                break;
            default:
                day++;
                break;
        }
    }

    private void type(Crash crash) {
        if ("1".equals(crash.getType())) {
            angle++;
        } else if ("2".equals(crash.getType())) {
            headOn++;
        } else if ("3".equals(crash.getType())) {
            rearEnd++;
        } else if ("4".equals(crash.getType()) || "5".equals(crash.getType())) {
            sideSwipe++;
        } else if ("6".equals(crash.getType())) {
            turning++;
        } else {
            other++;
        }
    }

    public int getTotal() {
        return total;
    }
}
