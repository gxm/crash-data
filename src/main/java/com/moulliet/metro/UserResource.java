package com.moulliet.metro;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ObjectNode;
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
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response login(String json) throws Exception {
        JsonNode node = mapper.readTree(json);
        String user = node.get("user").asText();
        String password = node.get("password").asText();
        logger.info("got post! {} {}", user, password);
        ObjectNode root = mapper.createObjectNode();
        root.put("type", user);
        if ("admin".equals(user)) {
            root.put("key", "ABC");
        }
        return Response.ok(root.toString()).build();
    }
}
