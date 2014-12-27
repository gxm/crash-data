package com.moulliet.metro;

import com.moulliet.common.ClientCreator;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.core.header.FormDataContentDisposition;
import com.sun.jersey.multipart.FormDataBodyPart;
import com.sun.jersey.multipart.FormDataMultiPart;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ArrayNode;
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
import static org.junit.Assert.fail;

/**
 * Full end to end integration test of server, through the Http interface and using Mongo
 */
public class IntegrationTest {
    private static final Logger logger = LoggerFactory.getLogger(IntegrationTest.class);
    private static final ObjectMapper mapper = new ObjectMapper();
    private static Client client = ClientCreator.cached();
    public static final String ROOT_URL = "http://localhost:7070/";
    public static final String DATASETS_URL = ROOT_URL + "datasets";
    public static final String CRASH_URL = ROOT_URL + "crashes/47/44/-121/-124?callback=stuff";

    @BeforeClass
    public static void beforeClass() throws Exception {
        System.setProperty("config.properties", "/Users/greg/code/crash-data/config/test/crash-data.properties");
        CrashServiceMain.startResources(7070);
        deleteExisting();
        loadFile();
        setActive();
    }

    @AfterClass
    public static void tearDown() throws Exception {
        CrashServiceMain.stop();
    }

    private static void setActive() throws IOException {
        ObjectNode dataset = (ObjectNode) getDatasets().get(0);
        dataset.put("active", true);
        logger.info("dataset active {}", dataset);

        ClientResponse put = client.resource(DATASETS_URL)
                .accept(MediaType.APPLICATION_JSON)
                .entity(dataset.toString(), MediaType.APPLICATION_JSON)
                .put(ClientResponse.class);
        logger.info("response {}", put);
        assertEquals(200, put.getStatus());
    }

    private static void deleteExisting() throws IOException {
        for (JsonNode node : getDatasets()) {
            client.resource(DATASETS_URL + "/" + node.get("name").asText()).delete();
        }
    }

    private static JsonNode getDatasets() throws IOException {
        return mapper.readTree(client.resource(DATASETS_URL).get(String.class));
    }

    private static String loadFile() throws IOException {
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

    private JsonNode getJsonNode(String params) throws IOException {
        ClientResponse response = client.resource(CRASH_URL + params).get(ClientResponse.class);
        assertEquals(200, response.getStatus());
        String entity = trimEntity(response.getEntity(String.class));
        System.out.println(trimEntity(entity));
        return mapper.readTree(entity);
    }

    private void checkDataPoint(String lat, String lng, int count, JsonNode rootNode) {
        ArrayNode data = (ArrayNode) rootNode.get("data");
        for (JsonNode node : data) {
            if (lat.equals(node.get("lat").asText()) && lng.equals(node.get("lng").asText())) {
                assertEquals("point " + lat + " " + lng, count, node.get("count").asInt());
                return;
            }
        }
        fail("missing point " + lat + " " + lng);
    }

    @Test
    public void testAll() throws IOException {
        JsonNode rootNode = getJsonNode("");
        assertEquals(30, rootNode.get("max").asInt());
        assertEquals(953, rootNode.get("total").asInt());
        checkDataPoint("45.56", "-122.6615", 2, rootNode);
        checkDataPoint("45.5552", "-122.6297", 9, rootNode);
        checkDataPoint("45.5591", "-122.6615", 30, rootNode);
    }

    //todo - gfm - 12/27/14 - add test for scope window & regional

    @Test
    public void testAlcohol() throws IOException {
        JsonNode rootNode = getJsonNode("&alcohol=true");
        assertEquals(2, rootNode.get("max").asInt());
        assertEquals(52, rootNode.get("total").asInt());
        assertEquals(52, rootNode.get("summary").get("alcohol").asInt());
        checkDataPoint("45.5627", "-122.658", 2, rootNode);
    }

    @Test
    public void testPeds() throws IOException {
        JsonNode rootNode = getJsonNode("&peds=true&cars=false&bikes=false");
        assertEquals(3, rootNode.get("max").asInt());
        assertEquals(31, rootNode.get("total").asInt());
        assertEquals(31, rootNode.get("summary").get("peds").asInt());
        assertEquals(0, rootNode.get("summary").get("bikes").asInt());
    }

    @Test
    public void testBikes() throws IOException {
        JsonNode rootNode = getJsonNode("&peds=false&cars=false&bikes=true");
        assertEquals(2, rootNode.get("max").asInt());
        assertEquals(40, rootNode.get("total").asInt());
        assertEquals(0, rootNode.get("summary").get("peds").asInt());
        assertEquals(40, rootNode.get("summary").get("bikes").asInt());
        assertEquals(0, rootNode.get("summary").get("cars").asInt());
    }

    @Test
    public void testPedsAndBikes() throws IOException {
        JsonNode rootNode = getJsonNode("&peds=true&cars=false&bikes=true");
        assertEquals(5, rootNode.get("max").asInt());
        assertEquals(71, rootNode.get("total").asInt());
        assertEquals(31, rootNode.get("summary").get("peds").asInt());
        assertEquals(40, rootNode.get("summary").get("bikes").asInt());
    }

    @Test
    public void testWet() throws IOException {
        JsonNode rootNode = getJsonNode("&wet=true&dry=false&snowIce=false");
        assertEquals(6, rootNode.get("max").asInt());
        assertEquals(213, rootNode.get("total").asInt());
        assertEquals(213, rootNode.get("summary").get("wet").asInt());
        assertEquals(0, rootNode.get("summary").get("snowIce").asInt());
        assertEquals(0, rootNode.get("summary").get("dry").asInt());
    }

    @Test
    public void testIce() throws IOException {
        JsonNode rootNode = getJsonNode("&wet=false&dry=false&snowIce=true");
        assertEquals(1, rootNode.get("max").asInt());
        assertEquals(8, rootNode.get("total").asInt());
        assertEquals(0, rootNode.get("summary").get("wet").asInt());
        assertEquals(0, rootNode.get("summary").get("dry").asInt());
        assertEquals(8, rootNode.get("summary").get("snowIce").asInt());
    }

    @Test
    public void testNight() throws IOException {
        JsonNode rootNode = getJsonNode("&day=false&twilight=false&night=true");
        assertEquals(11, rootNode.get("max").asInt());
        assertEquals(218, rootNode.get("total").asInt());
        assertEquals(218, rootNode.get("summary").get("night").asInt());
        assertEquals(0, rootNode.get("summary").get("day").asInt());
        assertEquals(0, rootNode.get("summary").get("twilight").asInt());
    }

    @Test
    public void testTwilightNight() throws IOException {
        JsonNode rootNode = getJsonNode("&day=false&twilight=true&night=true");
        assertEquals(12, rootNode.get("max").asInt());
        assertEquals(288, rootNode.get("total").asInt());
        assertEquals(218, rootNode.get("summary").get("night").asInt());
        assertEquals(0, rootNode.get("summary").get("day").asInt());
        assertEquals(70, rootNode.get("summary").get("twilight").asInt());
    }

    @Test
    public void testYear() throws IOException {
        JsonNode rootNode = getJsonNode("&y2007=false&y2008=true&y2009=false&y2010=true&y2011=false&y2012=false&y2013=false");
        assertEquals(12, rootNode.get("max").asInt());
        assertEquals(208, rootNode.get("total").asInt());
    }

    @Test
    public void testTypeMost() throws IOException {
        JsonNode rootNode = getJsonNode("&angle=true&headOn=false&rearEnd=true&sideSwipe=false&turning=true&other=true");
        assertEquals(853, rootNode.get("total").asInt());
    }

    @Test
    public void testTypeSome() throws IOException {
        JsonNode rootNode = getJsonNode("&angle=false&headOn=false&rearEnd=true&sideSwipe=false&turning=true&other=false");
        assertEquals(532, rootNode.get("total").asInt());
    }

    @Test
    public void testSeverity() throws IOException {
        JsonNode rootNode = getJsonNode("&fatal=true&injuryA=false&injuryB=true&injuryC=false&property=true");
        assertEquals(583, rootNode.get("total").asInt());
        assertEquals(1, rootNode.get("summary").get("fatal").asInt());
        assertEquals(0, rootNode.get("summary").get("injuryA").asInt());
        assertEquals(118, rootNode.get("summary").get("injuryB").asInt());
        assertEquals(0, rootNode.get("summary").get("injuryC").asInt());
        assertEquals(464, rootNode.get("summary").get("property").asInt());
    }

    @Test
    public void testSeveritySome() throws IOException {
        JsonNode rootNode = getJsonNode("&fatal=false&injuryA=true&injuryB=false&injuryC=true&property=false");
        assertEquals(370, rootNode.get("total").asInt());
        assertEquals(0, rootNode.get("summary").get("fatal").asInt());
        assertEquals(15, rootNode.get("summary").get("injuryA").asInt());
        assertEquals(0, rootNode.get("summary").get("injuryB").asInt());
        assertEquals(355, rootNode.get("summary").get("injuryC").asInt());
        assertEquals(0, rootNode.get("summary").get("property").asInt());
    }


    private String trimEntity(String entity) {
        return StringUtils.removeEnd(StringUtils.removeStart(entity, "stuff("), ")");
    }

}
