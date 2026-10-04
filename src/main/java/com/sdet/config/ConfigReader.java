package com.sdet.config;

import java.io.InputStream;
import java.util.Properties;

/**
 * Reads settings from src/test/resources/config/{env}.properties.
 * Pick the environment with -Denv=qa (default) or -Denv=staging.
 * Any setting can be overridden from the command line, e.g. -Dheadless=false
 */
public class ConfigReader {

    private static final Properties properties = new Properties();

    static {
        String env = System.getProperty("env", "qa");
        String fileName = "config/" + env + ".properties";
        try (InputStream file = ConfigReader.class.getClassLoader().getResourceAsStream(fileName)) {
            properties.load(file);
        } catch (Exception e) {
            throw new RuntimeException("Could not load config file: " + fileName, e);
        }
    }

    public static String get(String key) {
        // A -D value from the command line wins over the value in the file
        return System.getProperty(key, properties.getProperty(key));
    }

    public static int getInt(String key) {
        return Integer.parseInt(get(key));
    }

    public static boolean getBoolean(String key) {
        return Boolean.parseBoolean(get(key));
    }
}
