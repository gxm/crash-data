package com.moulliet.metro;

import com.google.common.io.Files;
import com.moulliet.metro.crash.Crashes;
import com.sun.jersey.core.header.FormDataContentDisposition;
import com.sun.jersey.multipart.FormDataParam;
import org.codehaus.jackson.node.ArrayNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

@Path("/datasets")
public class DatasetResource {
    private static final Logger logger = LoggerFactory.getLogger(DatasetResource.class);


    @GET
    @Produces("application/json")
    public Response getDatasets() {
        ArrayNode datasets = Statics.datasetService.get();
        return Response.status(200).entity(datasets.toString()).build();
    }

    @PUT
    @Consumes("application/json")
    @Produces("application/json")
    public Response putDatasets(String data) throws IOException {
        logger.info("posted data {}", data);
        Statics.datasetService.update(data);
        Crashes.loadAll();
        return getDatasets();
    }

    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response uploadFile(
            @FormDataParam("file") InputStream fileInputStream,
            @FormDataParam("file") FormDataContentDisposition contentDispositionHeader) {
        logger.info("posting...");
        File tempDir = Files.createTempDir();
        String filePath = tempDir + contentDispositionHeader.getFileName();

        // save the file to the server
        saveFile(fileInputStream, filePath);

        String output = "File saved to server location : " + filePath;

        logger.info("posted " + output);
        return Response.status(200).entity(output).build();

    }

    // save uploaded file to a defined location on the server
    private void saveFile(InputStream uploadedInputStream,
                          String serverLocation) {

        try {
            OutputStream outpuStream = new FileOutputStream(new File(serverLocation));
            int read = 0;
            byte[] bytes = new byte[1024];

            outpuStream = new FileOutputStream(new File(serverLocation));
            while ((read = uploadedInputStream.read(bytes)) != -1) {
                outpuStream.write(bytes, 0, read);
            }
            outpuStream.flush();
            outpuStream.close();
        } catch (IOException e) {

            e.printStackTrace();
        }

    }
}
