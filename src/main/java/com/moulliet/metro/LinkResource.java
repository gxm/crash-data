package com.moulliet.metro;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.ObjectNode;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import java.io.IOException;

@Path("/links")
public class LinkResource {

    private static final ObjectMapper mapper = new ObjectMapper();

    @GET
    @Produces("application/json")
    public Response rootRedirect(@QueryParam("callback") final String callback) throws IOException {
        ArrayNode root = mapper.createArrayNode();
        addLink("Crash Map", "crashes.html", root);
        addLink("Data Sets", "datasets.html", root);
        String entity = callback + "(" + root.toString() + ");";
        return Response.ok(entity).build();
    }

    private void addLink(String name, String ref, ArrayNode root) {
        ObjectNode object = root.addObject();
        object.put("name", name);
        object.put("href", ref);
    }

}
