package com.moulliet.metro;

import com.moulliet.metro.arterial.Arterials;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/arterial")
public class ArterialResource {

    @GET
    @Path("/multiline")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getLines() throws Exception {
        return Response.status(200).entity(Arterials.getMultiLine().toString()).build();
    }

    @Path("/multipolygon")
    @Produces(MediaType.APPLICATION_JSON)
    @GET
    public Response getPolygon() throws Exception {
        return Response.status(200).entity(Arterials.getMultiPolygon().toString()).build();
    }

}
