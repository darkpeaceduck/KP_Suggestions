package ru.kpsug.utils;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.json.simple.parser.ContainerFactory;
import org.json.simple.parser.JSONParser;

public class ConfigParser {
    public static Map<String, String> parseConfigInp(InputStream input) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(input));
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
    
    public static Map<String, String> parseConfig(String path) throws IOException{
        return parseConfigInp(new FileInputStream(path));
    }
    

    private static ContainerFactory json_factory = new ContainerFactory() {
        public List creatArrayContainer() {
            return new ArrayList<>();
        }

        public Map createObjectContainer() {
            return new TreeMap<>();
        }

    };
    
    private static JSONParser json_parser = new JSONParser();

    public static ContainerFactory getContainerFactory() {
        return json_factory;
    }

    public static JSONParser getJSONParser() {
        return json_parser;
    }
}
