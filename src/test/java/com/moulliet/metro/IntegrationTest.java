package com.moulliet.metro;

import com.moulliet.common.ClientCreator;
import com.moulliet.metro.crash.CrashFactory;
import com.moulliet.metro.load.LoadShapefile;
import com.moulliet.metro.mongo.MongoDaoImpl;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Full end to end integration test of server, through the Http interface and using Mongo
 */
public class IntegrationTest {
    public static final String URL = "http://localhost:7070/metro/47/44/-121/-124?callback=stuff";
    public static final String DATABASE = "test";
    private static final Logger logger = LoggerFactory.getLogger(IntegrationTest.class);
    private static MongoDaoImpl mongoDao;
    private final ObjectMapper mapper = new ObjectMapper();
    private Client client = ClientCreator.cached();

    @BeforeClass
    public static void beforeClass() throws Exception {
        CrashFactory.reset();
        String collection = RandomStringUtils.random(6);
        mongoDao = new MongoDaoImpl(DATABASE, collection);
        CrashFactory.setMongoDao(mongoDao);
        int load = LoadShapefile.load("/Users/greg/code/crash-data/data/testDataCycle.json.txt", DATABASE, collection);
        assertEquals(6, load);
        CrashServiceMain.startResources(7070);
    }

    @AfterClass
    public static void tearDown() throws Exception {
        CrashServiceMain.stop();
        mongoDao.deleteCollection();
    }

    @Test
    public void testAll() throws IOException {
        ClientResponse response = client.resource(URL).get(ClientResponse.class);
        assertEquals(200, response.getStatus());
        String entity = trimEntity(response.getEntity(String.class));
        System.out.println(entity);
        JsonNode rootNode = mapper.readTree(entity);
        assertEquals(3, rootNode.get("max").asInt());
        assertEquals(5, rootNode.get("total").asInt());
        assertTrue(entity.contains("{\"data\":[{\"lat\":\"45.55\",\"lng\":\"-122.826\",\"count\":3},{\"lat\":\"45.592\",\"lng\":\"-123.12\",\"count\":2}]"));
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
        assertEquals(0, rootNode.get("summary").get("fatality").asInt());
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
    }


    private String trimEntity(String entity) {
        return StringUtils.removeEnd(StringUtils.removeStart(entity, "stuff("), ")");
    }

}
