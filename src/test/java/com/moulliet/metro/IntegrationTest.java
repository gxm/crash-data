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
        int load = LoadShapefile.load("/Users/greg/code/crash-data/data/testDataCycle.json", DATABASE, collection);
        assertEquals(5, load);
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
        //{"data":[{"lat":"45.55","lng":"-122.826","count":3},{"lat":"45.592","lng":"-123.12","count":2}],"max":3,"total":5,
        assertEquals(3, rootNode.get("max").asInt());
        assertEquals(5, rootNode.get("total").asInt());
        assertTrue(entity.contains("{\"data\":[{\"lat\":\"45.55\",\"lng\":\"-122.826\",\"count\":3},{\"lat\":\"45.592\",\"lng\":\"-123.12\",\"count\":2}]"));
        // "summary":{"total":5,"cars":5,"bikes":0,"peds":0,"alcohol":0,"injury":0,"fatality":0,"day":5,"night":0,"twilight":0,"dry":5,"wet":0,"snowIce":0,"angle":0,"headOn":0,"rearEnd":0,"sideSwipe":0,"turning":0,"other":5}};
    }

    @Test
    public void testInjury() throws IOException {
        ClientResponse response = client.resource(URL + "&injury=true").get(ClientResponse.class);
        assertEquals(200, response.getStatus());
        String entity = trimEntity(response.getEntity(String.class));
        System.out.println(trimEntity(entity));
        JsonNode rootNode = mapper.readTree(entity);
        assertEquals(2, rootNode.get("max").asInt());
        assertEquals(3, rootNode.get("total").asInt());
        assertEquals(3, rootNode.get("summary").get("injury").asInt());
        // "summary":{"total":3,"cars":3,"bikes":0,"peds":0,"alcohol":0,"injury":0,"fatality":0,"day":3,"night":0,"twilight":0,"dry":3,"wet":0,"snowIce":0,"angle":0,"headOn":0,"rearEnd":0,"sideSwipe":0,"turning":0,"other":3}});
    }

    @Test
    public void testFatality() throws IOException {
        ClientResponse response = client.resource(URL + "&fatality=true").get(ClientResponse.class);
        assertEquals(200, response.getStatus());
        String entity = trimEntity(response.getEntity(String.class));
        System.out.println(trimEntity(entity));
        JsonNode rootNode = mapper.readTree(entity);
        assertEquals(1, rootNode.get("max").asInt());
        assertEquals(1, rootNode.get("total").asInt());
        assertEquals(1, rootNode.get("summary").get("fatality").asInt());
        // "summary":{"total":3,"cars":3,"bikes":0,"peds":0,"alcohol":0,"injury":0,"fatality":0,"day":3,"night":0,"twilight":0,"dry":3,"wet":0,"snowIce":0,"angle":0,"headOn":0,"rearEnd":0,"sideSwipe":0,"turning":0,"other":3}});
    }

    private String trimEntity(String entity) {
        return StringUtils.removeEnd(StringUtils.removeStart(entity, "stuff("), ")");
    }

}
