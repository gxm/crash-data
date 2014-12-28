package com.moulliet.metro;

import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/user")
public class UserResource {

    private static final Logger logger = LoggerFactory.getLogger(UserResource.class);
    private static final ObjectMapper mapper = new ObjectMapper();

    @GET
    public Response get() {
        logger.info("got get!");
        return Response.ok().build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response login(@FormParam("user") String user, @FormParam("password") String password) {
        logger.info("got post! {} {}", user, password);
        return Response.ok().build();
    }
}
