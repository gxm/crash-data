package com.moulliet.metro;

import org.apache.commons.configuration.*;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Config {
    private static final Logger logger = LoggerFactory.getLogger(Config.class);

    private static Configuration config;

    public static void load() {
        String configPath = System.getProperty("config.properties");
        if (StringUtils.isEmpty(configPath)) {
            throw new RuntimeException("unable to find system property 'config.properties'");
        }
        logger.info("starting with " + configPath);
        try {
            CompositeConfiguration compositeConfiguration = new CompositeConfiguration();
            compositeConfiguration.addConfiguration(new SystemConfiguration());
            compositeConfiguration.addConfiguration(new PropertiesConfiguration(configPath));
            config = compositeConfiguration;
        } catch (Exception e) {
            throw new RuntimeException("unable to load config " + configPath, e);
        }
    }

    public static Configuration getConfig() {
        if (config == null) {
            load();
        }
        return config;
    }
}
