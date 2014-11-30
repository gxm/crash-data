package com.moulliet.metro;

import com.moulliet.common.ClientCreator;
import com.moulliet.metro.crash.CrashFactory;
import com.moulliet.metro.load.LoadShapefile;
import com.moulliet.metro.mongo.MongoDaoImpl;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import junit.framework.TestCase;
import org.apache.commons.lang3.RandomStringUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Full end to end integration test of server, through the Http interface and using Mongo
 */
public class IntegrationTest extends TestCase {
    private static final Logger logger = LoggerFactory.getLogger(IntegrationTest.class);

    public static final String URL = "http://localhost:7070/metro/46/45/-122/-123?callback=stuff";
    public static final String DATABASE = "test";
    private Client client = ClientCreator.cached();
    private final ObjectMapper mapper = new ObjectMapper();
    private String collection;
    private MongoDaoImpl mongoDao;


    protected void setUp() throws Exception {
        CrashFactory.reset();
        collection = RandomStringUtils.random(6);
        mongoDao = new MongoDaoImpl(DATABASE, collection);
        CrashFactory.setMongoDao(mongoDao);
        CrashServiceMain.startResources(7070);
    }

    protected void tearDown() throws Exception {
        CrashServiceMain.stop();
        mongoDao.deleteCollection();
    }

    public void testDataCycle() throws IOException {
        int load = LoadShapefile.load("/Users/greg/code/crash-data/data/testDataCycle.json", DATABASE, collection);
        assertEquals(4, load);
        ClientResponse response = client.resource(URL).get(ClientResponse.class);
        assertEquals(200, response.getStatus());
        String entity = response.getEntity(String.class);
        System.out.println(entity);

        //todo - gfm - query by params
    }

    /*public void testGetPoints() throws Exception
    {
        Timer timer = new Timer();

        ClientResponse response = client.resource(URL).get(ClientResponse.class);
        logger.info("response in " + timer.reset());
        assertEquals(200, response.getStatus());
        String entity = response.getEntity(String.class);
        assertEquals(MediaType.APPLICATION_JSON_TYPE, response.getType());
        JsonNode node = mapper.readTree(entity);
        assertEquals(2656, node.get("data").size());
        assertEquals(34547, node.get("total").asInt());
        assertEquals(195, node.get("max").asInt());
        JsonNode summary = node.get("summary");
        logger.info(summary.toString());
        assertEquals(32563, summary.get("cars").asInt());
        assertEquals(1179, summary.get("bikes").asInt());
        assertEquals(805, summary.get("peds").asInt());
        assertEquals(1360, summary.get("alcohol").asInt());
        //todo - gfm - this value still seems very high, slightly lower now
        assertEquals(14880, summary.get("injury").asInt());
        assertEquals(698, summary.get("fatality").asInt());
        assertEquals(25010, summary.get("day").asInt());
        assertEquals(7247, summary.get("night").asInt());
        assertEquals(2290, summary.get("twilight").asInt());
        assertEquals(25721, summary.get("dry").asInt());
        assertEquals(8373, summary.get("wet").asInt());
        assertEquals(453, summary.get("snowIce").asInt());
    }*/
}
