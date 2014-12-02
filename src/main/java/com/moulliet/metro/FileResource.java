package com.moulliet.metro;

import org.apache.commons.io.FileUtils;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import java.io.File;
import java.io.IOException;

@Path("/file")
public class FileResource {

    @GET
    @Path("{path:.*}")
    public String get(@PathParam("path") String path) throws IOException {
        File file = new File(Config.getConfig().getString("public.dir") + path);
        return FileUtils.readFileToString(file);
    }
}
