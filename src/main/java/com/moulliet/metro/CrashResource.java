package com.moulliet.metro;

import com.mongodb.DBObject;
import com.moulliet.common.Timer;
import com.moulliet.metro.crash.CrashFactory;
import com.moulliet.metro.crash.CrashQuery;
import com.moulliet.metro.crash.Crashes;
import com.moulliet.metro.mongo.MongoQueryCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;
import java.io.IOException;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.util.Iterator;

@Path("/metro")
public class CrashResource {

    private static final Logger logger = LoggerFactory.getLogger(CrashResource.class);

    @GET
    public String get() throws Exception {
        return "Crash!";
    }

    @GET
    @Path("/{north}/{south}/{east}/{west}")
    public Response getBox(@PathParam("north") String north,
                           @PathParam("south") String south,
                           @PathParam("east") String east,
                           @PathParam("west") String west,
                           @QueryParam("callback") final String callback,
                           @DefaultValue("true") @QueryParam("cars") boolean cars,
                           @DefaultValue("true") @QueryParam("peds") boolean peds,
                           @DefaultValue("true") @QueryParam("bikes") boolean bikes,
                           @DefaultValue("false") @QueryParam("alcohol") boolean alcohol,
                           @DefaultValue("false") @QueryParam("injury") boolean injury,
                           @DefaultValue("false") @QueryParam("fatality") boolean fatality,
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
                           @DefaultValue("true") @QueryParam("y2013") boolean y2013
    ) throws Exception {
        try {
            final Timer timer = new Timer();

            CrashQuery query = new CrashQuery();
            query.location(north, south, east, west);

            query.vehicle(cars, bikes, peds);
            query.alcohol(alcohol);
            query.hurt(injury, fatality);
            query.light(day, night, twilight);
            query.surface(dry, wet, snowIce);
            query.years(y2007, y2008, y2009, y2010, y2011, y2012, y2013);

            logger.debug(query.toString());

            final Crashes crashes = new Crashes();

            CrashFactory.getMongoDao().query(query.getQuery(), new MongoQueryCallback() {
                public void callback(Iterator<DBObject> dbObjectIterator) {
                    logger.debug("crash call in " + timer.reset() + " millis.");
                    crashes.loadResults(dbObjectIterator, getDecimalFormat(zoom));
                    logger.debug("loaded results in " + timer.reset() + " millis.");
                }
            });

            Response.ResponseBuilder builder = Response.ok(new StreamingOutput() {
                public void write(OutputStream outputStream) throws IOException, WebApplicationException {
                    try {
                        outputStream.write((callback + "(").getBytes());
                        crashes.aggregatedCrashes(outputStream);
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

    //todo - gfm - this may need to change
    private DecimalFormat getDecimalFormat(int zoom) {
        if (zoom >= 17) {
            return new DecimalFormat("####.#####");
        } else if (zoom >= 15) {
            return new DecimalFormat("####.####");
        } else if (zoom >= 12) {
            return new DecimalFormat("####.###");
        } else if (zoom >= 10) {
            return new DecimalFormat("####.##");
        }
        return new DecimalFormat("####.#");
    }

}
