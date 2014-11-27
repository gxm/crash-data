package com.moulliet.common;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * Contains key-value pairs.
 * Given this properties file:
 * key1=value1
 */
public class Config {
    private static final Logger logger = LoggerFactory.getLogger(Config.class);

    private Map<String, String> properties = new HashMap<String, String>();
    private boolean verbose = false;

    public Config() {
        setVerbose(true);
        String baseDir = getBaseDir();
        String config = System.getProperty("config.properties");
        logger.debug("baseDir " + baseDir);
        logger.debug("config " + config);
        if (StringUtils.isNotBlank(baseDir) && StringUtils.isNotBlank(config)) {
            loadFromFile(baseDir + config);
        }
        setVerbose(false);
    }

    public boolean isVerbose() {
        return verbose;
    }

    public void setVerbose(boolean verbose) {
        this.verbose = verbose;
    }

    public void loadFromFile(String filename) {

        if (isVerbose()) {
            logger.info("loading from file " + filename);
        }
        try {
            FileInputStream inputStream = new FileInputStream(filename);
            loadFromInputStream(inputStream);
        } catch (FileNotFoundException e) {
            logger.error("FileNotFoundException occurred loading properties from " + filename, e);
            //todo - gfm - throw exception
        }
    }

    public void loadFromInputStream(InputStream inStream) {
        Properties properties = new Properties();
        try {
            properties.load(inStream);
        } catch (IOException e) {
            logger.error("IOException occurred loading properties from stream", e);
            //todo - gfm - throw exception
        }

        Set<Map.Entry<Object, Object>> entrySet = properties.entrySet();
        for (Map.Entry<Object, Object> entry : entrySet) {
            String key = (String) entry.getKey();
            String value = (String) entry.getValue();

            int indexOfDot = key.lastIndexOf(".");

            if (indexOfDot != -1) {
                key = key.substring(indexOfDot + 1);
            }
            set(key, value);
        }
    }

    public void set(String propertyName, String propertyValue) {
        if (isVerbose()) {
            logger.info("setting value to '{}' for propertyName '{}'",
                    new Object[]{propertyValue, propertyName});
        }
        properties.put(propertyName, propertyValue);
    }

    public void set(String propertyName, int propertyValue) {
        set(propertyName, Integer.toString(propertyValue));
    }

    public void set(String propertyName, boolean propertyValue) {
        set(propertyName, Boolean.toString(propertyValue));
    }

    public String get(String propertyName, String defaultValue) {
        String value = properties.get(propertyName);
        if (null != value) {
            if (isVerbose()) {
                logger.info("found '{}' for propertyName '{}'", value, propertyName);
            }
            return value;
        }
        if (isVerbose()) {
            logger.info("returning default '{}' for propertyName '{}'", defaultValue, propertyName);
        }
        return defaultValue;
    }

    public int get(String propertyName, int defaultValue) {
        String propertyValue = get(propertyName, Integer.toString(defaultValue));

        if (propertyValue != null) {
            try {
                return Integer.parseInt(propertyValue);
            } catch (NumberFormatException e) {
                logger.error("value for " + propertyName + " was " + propertyValue + ", and not a valid Integer");
            }
        }

        return defaultValue;
    }

    public boolean get(String propertyName, boolean defaultValue) {
        String propertyValue = get(propertyName, Boolean.toString(defaultValue));

        if (propertyValue != null) {
            return Boolean.parseBoolean(propertyValue);
        }

        return defaultValue;
    }

    public List<String> getValues(String propertyName, List<String> defaultValueList) {
        String propertyValue = get(propertyName, "defaultValueList");

        if (propertyValue.equals("defaultValueList")) {
            return defaultValueList;
        }

        return Arrays.asList(StringUtils.split(propertyValue, ","));
    }

    public String getBaseDir() {
        return get("base.dir", System.getProperty("base.dir"));
    }

    public String toString() {
        return "Config{" +
                "properties=" + properties +
                '}';
    }
}
