package com.moulliet.metro;

import com.moulliet.common.ClientCreator;
import com.sun.jersey.api.client.Client;
import org.apache.commons.io.FileUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import java.io.File;
import java.io.IOException;

/**
 *
 */
@Path("/file")
public class FileResource {
    private static final Logger logger = LoggerFactory.getLogger(FileResource.class);
    private static final ObjectMapper mapper = new ObjectMapper();
    @Context
    UriInfo uriInfo;
    private Client client = ClientCreator.cached();

    @GET
    @Path("{path:.*}")
    public String get(@PathParam("path") String path) throws IOException {
        //todo - gfm - this is only for local testing - pull out path as config
        File file = new File("/Users/greg/code/crash-data/public/" + path);

        return FileUtils.readFileToString(file);
    }
}
