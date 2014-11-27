package com.moulliet.common;

import junit.framework.TestCase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ConfigTest extends TestCase
{

    private Config config;

    public void testSetProperty() throws Exception
    {
        String key = "abc";
        String value = "qwerty";
        config.setVerbose(true);
        assertEquals(value, config.get(key, value));
        config.set(key, value);
        assertEquals(value, config.get(key, null));
    }

    public void testSetIntProperty() throws Exception
    {
        String key = "abc";
        int value = 10;
        assertEquals(value, config.get(key, value));
        config.set(key, value);
        assertEquals(value, config.get(key, 0));
    }

    public void testSetBooleanProperty() throws Exception
    {
        String key = "boolena";
        boolean value = true;
        assertEquals(value, config.get(key, value));
        config.set(key, value);
        assertEquals(value, config.get(key, false));
    }

    public void testFile() throws Exception
    {
        loadFromFile();
        assertEquals("valueX", config.get("nameOne", null));
        assertEquals("value3", config.get("nameThree", null));
        assertEquals("valueTwo", config.get("nameTwo", null));
    }

    private void loadFromFile()
    {
        config.loadFromFile(config.getBaseDir() + "src/test/java/com/moulliet/common/test.properties");
    }

    public void testList() throws Exception
    {
        assertEquals("one,two,three", config.get("list", null));
        List<String> expected = Arrays.asList("one", "two", "three");
        assertEquals(expected, config.getValues("list", new ArrayList<String>()));
        assertEquals(expected, config.getValues("no-list", expected));
    }

    public void setUp() throws Exception
    {
        System.setProperty("config.properties", "/src/test/java/com/moulliet/common/test.properties");
        config = new Config();
        config.setVerbose(false);
    }

    public void tearDown() throws Exception
    {
        System.setProperty("config.properties", "");
    }

}
