package com.moulliet.metro;

import com.moulliet.common.Timer;
import com.moulliet.metro.crash.Crashes;
import com.moulliet.metro.filter.Filters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;
import java.io.IOException;
import java.io.OutputStream;

//todo - gfm - rename this
@Path("/metro")
public class CrashResource {

    private static final Logger logger = LoggerFactory.getLogger(CrashResource.class);

    @GET
    public String get() throws Exception {
        return "Crash!";
    }

    @GET
    @Path("/{north}/{south}/{east}/{west}")
    public Response getBox(@PathParam("north") double north,
                           @PathParam("south") double south,
                           @PathParam("east") double east,
                           @PathParam("west") double west,
                           @QueryParam("callback") final String callback,
                           @DefaultValue("true") @QueryParam("cars") boolean cars,
                           @DefaultValue("true") @QueryParam("peds") boolean peds,
                           @DefaultValue("true") @QueryParam("bikes") boolean bikes,
                           @DefaultValue("false") @QueryParam("alcohol") boolean alcohol,
                           @DefaultValue("true") @QueryParam("day") boolean day,
                           @DefaultValue("true") @QueryParam("night") boolean night,
                           @DefaultValue("true") @QueryParam("twilight") boolean twilight,
                           @DefaultValue("true") @QueryParam("dry") boolean dry,
                           @DefaultValue("true") @QueryParam("wet") boolean wet,
                           @DefaultValue("true") @QueryParam("snowIce") boolean snowIce,
                           @DefaultValue("14") @QueryParam("zoom") final int zoom,
                           @DefaultValue("true") @QueryParam("y2007") boolean y2007,
                           @DefaultValue("true") @QueryParam("y2008") boolean y2008,
                           @DefaultValue("true") @QueryParam("y2009") boolean y2009,
                           @DefaultValue("true") @QueryParam("y2010") boolean y2010,
                           @DefaultValue("true") @QueryParam("y2011") boolean y2011,
                           @DefaultValue("true") @QueryParam("y2012") boolean y2012,
                           @DefaultValue("true") @QueryParam("y2013") boolean y2013,
                           @DefaultValue("true") @QueryParam("angle") boolean angle,
                           @DefaultValue("true") @QueryParam("headOn") boolean headOn,
                           @DefaultValue("true") @QueryParam("rearEnd") boolean rearEnd,
                           @DefaultValue("true") @QueryParam("sideSwipe") boolean sideSwipe,
                           @DefaultValue("true") @QueryParam("turning") boolean turning,
                           @DefaultValue("true") @QueryParam("other") boolean other,
                           @DefaultValue("true") @QueryParam("fatal") boolean fatal,
                           @DefaultValue("true") @QueryParam("injuryA") boolean injuryA,
                           @DefaultValue("true") @QueryParam("injuryB") boolean injuryB,
                           @DefaultValue("true") @QueryParam("injuryC") boolean injuryC,
                           @DefaultValue("true") @QueryParam("property") boolean property,
                           @DefaultValue("Window") @QueryParam("scale") String scale
    ) throws Exception {
        try {
            final Timer timer = new Timer();

            Filters filters = new Filters();
            if (scale.equals("Window")) {
                filters.location(north, south, east, west);
            }
            filters.vehicle(cars, bikes, peds);
            filters.alcohol(alcohol);
            filters.light(day, night, twilight);
            filters.surface(dry, wet, snowIce);
            filters.years(y2007, y2008, y2009, y2010, y2011, y2012, y2013);
            filters.type(angle, headOn, rearEnd, sideSwipe, turning, other);
            filters.severity(fatal, injuryA, injuryB, injuryC, property);

            final Crashes crashes = new Crashes();
            Response.ResponseBuilder builder = Response.ok(new StreamingOutput() {
                public void write(OutputStream outputStream) throws IOException, WebApplicationException {
                    try {
                        outputStream.write((callback + "(").getBytes());
                        int points = crashes.aggregatedCrashes(filters, outputStream, getRadius(zoom));
                        logger.debug("wrote {} points in {} millis.", points, timer.reset());
                        outputStream.write(");".getBytes());
                    } catch (IOException e) {
                        logger.warn("IOException ", e);
                    }
                }
            });

            builder.type("text/javascript");
            return builder.build();
        } catch (Exception e) {
            logger.warn("unable to handle request", e);
            throw e;
        }
    }

    private int getRadius(int zoom) {
        switch(zoom) {
            case 19:
            case 18:
            case 17:
            case 16:
                return 35;
            case 15:
                return 33;
            case 14:
                return 24;
            case 13:
                return 17;
            case 12:
                return 12;
            case 11:
                return 9;
            default:
                return 6;
        }
    }

}
