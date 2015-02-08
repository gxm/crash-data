package com.moulliet.metro;

import com.moulliet.metro.filter.Sinks;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

@Path("/sinks")
public class SinkResource {

    @GET
    public Response get() throws Exception {
        return Response.status(200).entity(Sinks.getSinks().toString()).build();
    }

}
