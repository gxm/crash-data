package com.moulliet.metro;

import com.google.common.io.Files;
import com.moulliet.metro.crash.Crashes;
import com.moulliet.metro.load.GdbService;
import com.sun.jersey.core.header.FormDataContentDisposition;
import com.sun.jersey.multipart.FormDataParam;
import org.apache.commons.io.IOUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.ObjectNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.*;

@Path("/datasets")
public class DatasetResource {
    private static final Logger logger = LoggerFactory.getLogger(DatasetResource.class);
    private static final ObjectMapper mapper = new ObjectMapper();

    @GET
    @Produces("application/json")
    public Response getDatasets() {
        ArrayNode datasets = Statics.datasetService.getAll();
        return Response.status(200).entity(datasets.toString()).build();
    }

    @PUT
    @Consumes("application/json")
    @Produces("application/json")
    public Response putDatasets(@HeaderParam("key") String key, String data) throws IOException {
        logger.info("posted data {} key {}", data, key);
        if (!Statics.userService.isValidKey(key)) {
            return unauthorized();
        }
        Statics.datasetService.update(data);
        Crashes.loadAll();
        return getDatasets();
    }

    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces("application/json")
    public Response uploadFile(
            @HeaderParam("key") String key,
            @FormDataParam("datasetName") String datasetName,
            @FormDataParam("file") InputStream inputStream,
            @FormDataParam("file") FormDataContentDisposition contentDispositionHeader
            )
            throws Exception {
        String fileName = contentDispositionHeader.getFileName();
        //todo - gfm - check for valid file name
        logger.info("posting file {} as {} {}", fileName, datasetName, key);
        if (!Statics.userService.isValidKey(key)) {
            return unauthorized();
        }
        File file = new File(Files.createTempDir() + fileName);
        OutputStream outpuStream = new FileOutputStream(file);
        IOUtils.copy(inputStream, outpuStream);
        int rows = GdbService.load(datasetName, file);
        Statics.datasetService.insert(datasetName, rows);
        return getDatasets();
    }

    private Response unauthorized() {
        ObjectNode node = mapper.createObjectNode();
        node.put("message", "unauthorized access");
        return Response.status(403).entity(node.toString()).build();
    }

    @DELETE
    @Path("/{name}")
    @Produces("application/json")
    public Response deleteDataset(@HeaderParam("key") String key,
                                  @PathParam("name") String name) throws IOException {
        logger.info("deleting {}", name);
        if (!Statics.userService.isValidKey(key)) {
            return unauthorized();
        }
        Statics.mongoDao.getDb().getCollection(name).drop();
        Statics.datasetService.delete(name);
        Crashes.loadAll();
        logger.info("deleted {}", name);
        return getDatasets();
    }

}
