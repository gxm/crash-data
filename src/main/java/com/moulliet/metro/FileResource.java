package com.moulliet.metro;

import org.apache.commons.io.FileUtils;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;
import java.io.File;
import java.io.IOException;
import java.net.URI;

@Path("/")
public class FileResource {

    @GET
    public Response rootRedirect() throws IOException {
        return redirect();
    }

    @GET
    @Path("/file")
    public Response fileRedirect() throws IOException {
        return redirect();
    }

    @GET
    @Path("/file/{path:.*}")
    public Response get(@PathParam("path") String path) throws IOException {
        File file = new File(Config.getConfig().getString("public.dir") + path);
        return Response.ok(FileUtils.readFileToByteArray(file)).build();
    }

    private Response redirect() {
        return Response.temporaryRedirect(URI.create("/file/index.html")).build();
    }
}
