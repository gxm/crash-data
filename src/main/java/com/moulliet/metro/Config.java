package com.moulliet.metro;

import org.apache.commons.configuration.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Config {
    private static final Logger logger = LoggerFactory.getLogger(Config.class);

    private static Configuration config;

    public static void load() throws ConfigurationException {
        String configPath = System.getProperty("config.properties");
        logger.info("starting with " + configPath);
        CompositeConfiguration compositeConfiguration = new CompositeConfiguration();
        compositeConfiguration.addConfiguration(new SystemConfiguration());
        compositeConfiguration.addConfiguration(new PropertiesConfiguration(configPath));
        config = compositeConfiguration;
        logger.debug("config {}", config);
    }

    public static Configuration getConfig() {
        return config;
    }
}
