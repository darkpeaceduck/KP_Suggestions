package ru.kpsug.utils;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.TreeMap;

public class ConfigParser {
    public static Map<String, String> parseConfig(String path) throws IOException{
        BufferedReader reader = new BufferedReader(new InputStreamReader(
                new FileInputStream(path)));
        String line = null;
        TreeMap<String, String> result = new TreeMap<String, String>();
        while ((line = reader.readLine()) != null) {
            int split_index = line.indexOf("=");
            String key = line.substring(0, split_index);    
            String value = line.substring(split_index + 1);
            result.put(key, value);
        }
        return result;
    }
}
