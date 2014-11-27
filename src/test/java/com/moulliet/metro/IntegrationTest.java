package com.moulliet.metro;

import com.moulliet.common.ClientCreator;
import com.moulliet.common.Timer;
import com.moulliet.metro.crash.CrashFactory;
import com.moulliet.metro.mongo.MongoDaoImpl;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import junit.framework.TestCase;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.core.MediaType;
import java.io.IOException;

/**
 * Full end to end integration test of server, through the Http interface and using Mongo
 */
public class IntegrationTest extends TestCase
{
    private static final Logger logger = LoggerFactory.getLogger(IntegrationTest.class);

    public static final String URL = "http://localhost:7070/odot/45.55823/45.48174/-122.56125/-122.77874";
    private Client client = ClientCreator.cached();
    private final ObjectMapper mapper = new ObjectMapper();


    protected void setUp() throws Exception
    {
        CrashFactory.reset();
        CrashFactory.setMongoDao(new MongoDaoImpl());
        CrashServiceMain.startResources(7070);
    }

    protected void tearDown() throws Exception
    {
        CrashServiceMain.stop();
    }

    public void testGetPoints() throws Exception
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
    }

    public void testBugWithNoCarsInjuryAndFatality() throws Exception
    {
        String url = "http://localhost:7070/odot/45.51562477936899/45.50245195864057/-122.6588138473034/-122.68600071787836"
                + "?bikes=true&cars=false&fatality=true&injury=true&peds=true&zoom=16";
        ClientResponse response = client.resource(url).get(ClientResponse.class);
        assertEquals(200, response.getStatus());
        String entity = response.getEntity(String.class);
        JsonNode node = mapper.readTree(entity);
        assertEquals(12, node.get("max").asInt());
    }

    public void testBugWithCarsAndFatality() throws Exception
    {
        int cars = makeCall("bikes=false&cars=true&fatality=true&injury=false&peds=false");
        int peds = makeCall("bikes=false&cars=false&fatality=true&injury=false&peds=true");
        int carsAndPeds = makeCall("bikes=false&cars=true&fatality=true&injury=false&peds=true");

        assertEquals(cars + peds, carsAndPeds);
    }

    private int makeCall(String params) throws IOException
    {
        String url = "http://localhost:7070/odot/45.539541504633966/45.50045170602349/-122.61562625885011/-122.72437374114992" +
                "?zoom=16&" + params;
        ClientResponse response = client.resource(url).get(ClientResponse.class);
        assertEquals(200, response.getStatus());
        String entity = response.getEntity(String.class);
        JsonNode node = mapper.readTree(entity);
        JsonNode total = node.get("total");
        return total.getIntValue();
    }

    public void testCsv() {
        ClientResponse response = client.resource("http://localhost:7070/odot/45.51562477936899/45.50245195864057/-122.6588138473034/-122.68600071787836"
                + "?bikes=true&cars=false&fatality=true&injury=true&peds=true&zoom=16&download=true").get(ClientResponse.class);

        assertEquals(200, response.getStatus());
        String entity = response.getEntity(String.class);
        System.out.println(entity);

    }
    /*public void testPerformance() {

        Timer timer = new Timer();
        //typical response 1500-1700ms with multnomahInjury2
        //typical response 2000-2800ms with multnomahComplete
        for (int i = 0; i < 100; i++)
        {
            ClientResponse response = client.resource(URL).get(ClientResponse.class);
            logger.info("response in " + timer.reset());
        }
    }*/

}
