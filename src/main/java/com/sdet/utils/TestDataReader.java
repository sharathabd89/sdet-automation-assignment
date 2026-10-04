package com.sdet.utils;

import io.restassured.path.json.JsonPath;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

/**
 * Reads test data from JSON files in src/test/resources/testdata.
 * Uses REST Assured's JsonPath, so values are read with a simple path:
 * TestDataReader.read("testdata/ui-testdata.json").getString("validUser.username")
 */
public class TestDataReader {

    public static JsonPath read(String fileName) {
        InputStream file = TestDataReader.class.getClassLoader().getResourceAsStream(fileName);
        if (file == null) {
            throw new RuntimeException("Test data file not found: " + fileName);
        }
        return JsonPath.from(file);
    }

    /** Turns a JSON array into the Object[][] that a TestNG @DataProvider returns (one row per test run). */
    public static Object[][] getDataProvider(String fileName, String key) {
        List<Map<String, Object>> rows = read(fileName).getList(key);
        Object[][] data = new Object[rows.size()][1];
        for (int i = 0; i < rows.size(); i++) {
            data[i][0] = rows.get(i);
        }
        return data;
    }
}
