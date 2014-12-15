package com.moulliet.metro;

import com.google.common.io.Files;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.util.JSON;
import com.moulliet.metro.mongo.MongoQueryCallback;
import com.sun.jersey.core.header.FormDataContentDisposition;
import com.sun.jersey.multipart.FormDataParam;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.ObjectNode;
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
import java.util.Iterator;

@Path("/datasets")
public class DatasetResource {
    private static final Logger logger = LoggerFactory.getLogger(DatasetResource.class);
    private ObjectMapper mapper = new ObjectMapper();

    @GET
    @Produces("application/json")
    public Response getDatasets() {
        ArrayNode list = mapper.createArrayNode();
        Statics.mongoDao.query("datasets", null, new MongoQueryCallback() {
            @Override
            public void callback(Iterator<DBObject> iterator) {
                while (iterator.hasNext()) {
                    DBObject next = iterator.next();
                    ObjectNode node = mapper.createObjectNode();
                    node.put("name", next.get("name").toString());
                    node.put("active", (boolean) next.get("active"));
                    node.put("uploaded", next.get("uploaded").toString());
                    list.add(node);
                }
            }
        });
        return Response.status(200).entity(list.toString()).build();
    }

    @PUT
    @Consumes("application/json")
    @Produces("application/json")
    public Response putDatasets(String data) {
        logger.info("posted data {}" , data);
        DBObject update = (DBObject) JSON.parse(data);
        BasicDBObject query = new BasicDBObject("name", update.get("name"));
        Statics.mongoDao.getDb().getCollection("datasets").update(query, update);
        //todo - gfm - re-load active datasets
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
