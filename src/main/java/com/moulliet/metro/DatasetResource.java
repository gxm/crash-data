package com.moulliet.metro;

import com.google.common.io.Files;
import com.moulliet.metro.crash.Crashes;
import com.moulliet.metro.load.GdbService;
import com.sun.jersey.core.header.FormDataContentDisposition;
import com.sun.jersey.multipart.FormDataParam;
import org.apache.commons.io.IOUtils;
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
    @Produces("application/json")
    public Response uploadFile(
            @FormDataParam("datasetName") String datasetName,
            @FormDataParam("file") InputStream inputStream,
            @FormDataParam("file") FormDataContentDisposition contentDispositionHeader
            )
            throws Exception {
        String fileName = contentDispositionHeader.getFileName();
        //todo - gfm - check for valid file name
        logger.info("posting file {} as {}", fileName, datasetName);
        File file = new File(Files.createTempDir() + fileName);
        OutputStream outpuStream = new FileOutputStream(file);
        IOUtils.copy(inputStream, outpuStream);
        int rows = GdbService.load(datasetName, file);
        Statics.datasetService.insert(datasetName, rows);
        return getDatasets();
    }

}
