package com.moulliet.metro;

import com.moulliet.metro.arterial.Arterials;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

@Path("/arterial")
public class ArterialResource {

    @GET
    public Response get() throws Exception {
        return Response.status(200).entity(Arterials.getPoints().toString()).build();
    }

}
