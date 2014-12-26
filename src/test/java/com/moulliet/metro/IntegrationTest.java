package com.moulliet.metro;

import com.moulliet.common.ClientCreator;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.core.header.FormDataContentDisposition;
import com.sun.jersey.multipart.FormDataBodyPart;
import com.sun.jersey.multipart.FormDataMultiPart;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ObjectNode;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.core.MediaType;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import static org.junit.Assert.assertEquals;

/**
 * Full end to end integration test of server, through the Http interface and using Mongo
 */
public class IntegrationTest {
    public static final String URL = "http://localhost:7070/metro/47/44/-121/-124?callback=stuff";
    private static final Logger logger = LoggerFactory.getLogger(IntegrationTest.class);
    private final ObjectMapper mapper = new ObjectMapper();
    private Client client = ClientCreator.cached();
    public static final String ROOT_URL = "http://localhost:7070/";
    public static final String DATASETS_URL = ROOT_URL + "datasets";

    @BeforeClass
    public static void beforeClass() throws Exception {
        System.setProperty("config.properties", "/Users/greg/code/crash-data/config/test/crash-data.properties");
        CrashServiceMain.startResources(7070);
    }

    @AfterClass
    public static void tearDown() throws Exception {
        CrashServiceMain.stop();
    }

    @Test
    public void testUpload() throws IOException {
        deleteExisting();
        loadFile();
        ObjectNode dataset = (ObjectNode) getDatasets().get(0);
        dataset.put("active", true);
        logger.info("dataset active {}", dataset);

        ClientResponse put = client.resource(DATASETS_URL)
                .accept(MediaType.APPLICATION_JSON)
                .entity(dataset.toString(), MediaType.APPLICATION_JSON)
                .put(ClientResponse.class);
        logger.info("response {}", put);
        assertEquals(200, put.getStatus());

        //todo - gfm - 12/26/14 - query crash endpoint
    }

    private void deleteExisting() throws IOException {
        for (JsonNode node : getDatasets()) {
            client.resource(DATASETS_URL + "/" + node.get("name").asText()).delete();
        }
    }

    private JsonNode getDatasets() throws IOException {
        return mapper.readTree(client.resource(DATASETS_URL).get(String.class));
    }

    private String loadFile() throws IOException {
        String name = "test-" + RandomStringUtils.randomAlphabetic(6);
        FormDataMultiPart multiPart = new FormDataMultiPart();
        FormDataContentDisposition contentDisposition = FormDataContentDisposition
                .name("file")
                .fileName("TestCrash.gdb.zip")
                .build();
        File file = new File(Config.getConfig().getString("test.data", "define..."));
        byte[] bytes = IOUtils.toByteArray(new FileInputStream(file));
        FormDataBodyPart bodyPart = new FormDataBodyPart(contentDisposition, bytes, MediaType.WILDCARD_TYPE);
        multiPart.bodyPart(bodyPart);
        multiPart.field("datasetName", name);
        client.resource(DATASETS_URL).type(MediaType.MULTIPART_FORM_DATA)
                .accept(MediaType.APPLICATION_JSON)
                .post(multiPart);
        return name;
    }

    /*

    @Test
    public void testAll() throws IOException {
        ClientResponse response = client.resource(URL).get(ClientResponse.class);
        assertEquals(200, response.getStatus());
        String entity = trimEntity(response.getEntity(String.class));
        System.out.println(entity);
        JsonNode rootNode = mapper.readTree(entity);
        assertEquals(3, rootNode.get("max").asInt());
        assertEquals(5, rootNode.get("total").asInt());
        assertTrue(entity.contains("{\"data\":[{\"lat\":\"45.5496\",\"lng\":\"-122.9256\",\"count\":3,\"radius\":24}," +
                "{\"lat\":\"45.5922\",\"lng\":\"-123.2204\",\"count\":2,\"radius\":24}]"));
    }

    @Test
    public void testAlcohol() throws IOException {
        ClientResponse response = client.resource(URL + "&alcohol=true").get(ClientResponse.class);
        assertEquals(200, response.getStatus());
        String entity = trimEntity(response.getEntity(String.class));
        System.out.println(trimEntity(entity));
        JsonNode rootNode = mapper.readTree(entity);
        assertEquals(1, rootNode.get("max").asInt());
        assertEquals(1, rootNode.get("total").asInt());
        assertEquals(1, rootNode.get("summary").get("alcohol").asInt());
    }

    @Test
    public void testPeds() throws IOException {
        ClientResponse response = client.resource(URL + "&peds=true&cars=false&bikes=false").get(ClientResponse.class);
        assertEquals(200, response.getStatus());
        String entity = trimEntity(response.getEntity(String.class));
        System.out.println(trimEntity(entity));
        JsonNode rootNode = mapper.readTree(entity);
        assertEquals(1, rootNode.get("max").asInt());
        assertEquals(1, rootNode.get("total").asInt());
        assertEquals(1, rootNode.get("summary").get("peds").asInt());
        assertEquals(0, rootNode.get("summary").get("bikes").asInt());
    }

    @Test
    public void testBikes() throws IOException {
        ClientResponse response = client.resource(URL + "&peds=false&cars=false&bikes=true").get(ClientResponse.class);
        assertEquals(200, response.getStatus());
        String entity = trimEntity(response.getEntity(String.class));
        System.out.println(trimEntity(entity));
        JsonNode rootNode = mapper.readTree(entity);
        assertEquals(1, rootNode.get("max").asInt());
        assertEquals(1, rootNode.get("total").asInt());
        assertEquals(0, rootNode.get("summary").get("peds").asInt());
        assertEquals(1, rootNode.get("summary").get("bikes").asInt());
    }

    @Test
    public void testPedsAndBikes() throws IOException {
        ClientResponse response = client.resource(URL + "&peds=true&cars=false&bikes=true").get(ClientResponse.class);
        assertEquals(200, response.getStatus());
        String entity = trimEntity(response.getEntity(String.class));
        System.out.println(trimEntity(entity));
        JsonNode rootNode = mapper.readTree(entity);
        assertEquals(2, rootNode.get("max").asInt());
        assertEquals(2, rootNode.get("total").asInt());
        assertEquals(1, rootNode.get("summary").get("peds").asInt());
        assertEquals(1, rootNode.get("summary").get("bikes").asInt());
    }

    @Test
    public void testWet() throws IOException {
        ClientResponse response = client.resource(URL + "&wet=true&dry=false&snowIce=false").get(ClientResponse.class);
        assertEquals(200, response.getStatus());
        String entity = trimEntity(response.getEntity(String.class));
        System.out.println(trimEntity(entity));
        JsonNode rootNode = mapper.readTree(entity);
        assertEquals(2, rootNode.get("max").asInt());
        assertEquals(2, rootNode.get("total").asInt());
        assertEquals(2, rootNode.get("summary").get("wet").asInt());
        assertEquals(0, rootNode.get("summary").get("snowIce").asInt());
        assertEquals(0, rootNode.get("summary").get("bikes").asInt());
    }

    @Test
    public void testIce() throws IOException {
        ClientResponse response = client.resource(URL + "&wet=false&dry=false&snowIce=true").get(ClientResponse.class);
        assertEquals(200, response.getStatus());
        String entity = trimEntity(response.getEntity(String.class));
        System.out.println(trimEntity(entity));
        JsonNode rootNode = mapper.readTree(entity);
        assertEquals(1, rootNode.get("max").asInt());
        assertEquals(1, rootNode.get("total").asInt());
        assertEquals(0, rootNode.get("summary").get("wet").asInt());
        assertEquals(1, rootNode.get("summary").get("snowIce").asInt());
        assertEquals(0, rootNode.get("summary").get("bikes").asInt());
    }

    @Test
    public void testNight() throws IOException {
        ClientResponse response = client.resource(URL + "&day=false&twilight=false&night=true").get(ClientResponse.class);
        assertEquals(200, response.getStatus());
        String entity = trimEntity(response.getEntity(String.class));
        System.out.println(trimEntity(entity));
        JsonNode rootNode = mapper.readTree(entity);
        assertEquals(2, rootNode.get("max").asInt());
        assertEquals(2, rootNode.get("total").asInt());
        assertEquals(2, rootNode.get("summary").get("night").asInt());
        assertEquals(0, rootNode.get("summary").get("day").asInt());
        assertEquals(0, rootNode.get("summary").get("twilight").asInt());
    }

    @Test
    public void testTwilightNight() throws IOException {
        ClientResponse response = client.resource(URL + "&day=false&twilight=true&night=true").get(ClientResponse.class);
        assertEquals(200, response.getStatus());
        String entity = trimEntity(response.getEntity(String.class));
        System.out.println(trimEntity(entity));
        JsonNode rootNode = mapper.readTree(entity);
        assertEquals(3, rootNode.get("max").asInt());
        assertEquals(3, rootNode.get("total").asInt());
        assertEquals(2, rootNode.get("summary").get("night").asInt());
        assertEquals(0, rootNode.get("summary").get("day").asInt());
        assertEquals(1, rootNode.get("summary").get("twilight").asInt());
    }

    @Test
    public void testYear() throws IOException {
        ClientResponse response = client.resource(URL +
                "&y2007=false&y2008=true&y2009=false&y2010=false&y2011=false&y2012=false&y2013=true").get(ClientResponse.class);
        assertEquals(200, response.getStatus());
        String entity = trimEntity(response.getEntity(String.class));
        System.out.println(trimEntity(entity));
        JsonNode rootNode = mapper.readTree(entity);
        assertEquals(2, rootNode.get("max").asInt());
        assertEquals(2, rootNode.get("total").asInt());
    }

    @Test
    public void testTypeAll() throws IOException {
        ClientResponse response = client.resource(URL +
                "&angle=true&headOn=false&rearEnd=true&sideSwipe=false&turning=true&other=true").get(ClientResponse.class);
        assertEquals(200, response.getStatus());
        String entity = trimEntity(response.getEntity(String.class));
        System.out.println(trimEntity(entity));
        JsonNode rootNode = mapper.readTree(entity);
        assertEquals(5, rootNode.get("total").asInt());
    }

    @Test
    public void testTypeSome() throws IOException {
        ClientResponse response = client.resource(URL +
                "&angle=false&headOn=false&rearEnd=true&sideSwipe=false&turning=true&other=false").get(ClientResponse.class);
        assertEquals(200, response.getStatus());
        String entity = trimEntity(response.getEntity(String.class));
        System.out.println(trimEntity(entity));
        JsonNode rootNode = mapper.readTree(entity);
        assertEquals(2, rootNode.get("total").asInt());
    }

    @Test
    public void testSeverity() throws IOException {
        ClientResponse response = client.resource(URL +
                "&fatal=true&injuryA=false&injuryB=true&injuryC=false&property=true").get(ClientResponse.class);
        assertEquals(200, response.getStatus());
        String entity = trimEntity(response.getEntity(String.class));
        System.out.println(trimEntity(entity));
        JsonNode rootNode = mapper.readTree(entity);
        assertEquals(3, rootNode.get("total").asInt());
        assertEquals(1, rootNode.get("summary").get("fatal").asInt());
        assertEquals(0, rootNode.get("summary").get("injuryA").asInt());
        assertEquals(1, rootNode.get("summary").get("injuryB").asInt());
        assertEquals(0, rootNode.get("summary").get("injuryC").asInt());
        assertEquals(1, rootNode.get("summary").get("property").asInt());
    }

    @Test
    public void testSeveritySome() throws IOException {
        ClientResponse response = client.resource(URL +
                "&fatal=false&injuryA=true&injuryB=false&injuryC=true&property=false").get(ClientResponse.class);
        assertEquals(200, response.getStatus());
        String entity = trimEntity(response.getEntity(String.class));
        System.out.println(trimEntity(entity));
        JsonNode rootNode = mapper.readTree(entity);
        assertEquals(2, rootNode.get("total").asInt());
        assertEquals(0, rootNode.get("summary").get("fatal").asInt());
        assertEquals(1, rootNode.get("summary").get("injuryA").asInt());
        assertEquals(0, rootNode.get("summary").get("injuryB").asInt());
        assertEquals(1, rootNode.get("summary").get("injuryC").asInt());
        assertEquals(0, rootNode.get("summary").get("property").asInt());
    }


    private String trimEntity(String entity) {
        return StringUtils.removeEnd(StringUtils.removeStart(entity, "stuff("), ")");
    }*/

}
