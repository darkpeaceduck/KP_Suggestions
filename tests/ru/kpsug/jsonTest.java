package ru.kpsug;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import javax.management.RuntimeErrorException;

import org.json.simple.*;
import org.json.simple.parser.ContainerFactory;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
public class jsonTest {
    public static class fig{
        fig(){
            throw new RuntimeException();
        }
    }
    public static fig func(){
        try{
            return new fig();
        }catch(Exception e){
            System.out.println("here");
        }
        return null;
    }
    public static void main(String[] args) {
//        ContainerFactory containerFactory = new ContainerFactory(){
//            public List creatArrayContainer() {
//              return new ArrayList<>();
//            }
//
//            public Map createObjectContainer() {
//              return new TreeMap<>();
//            }
//                                
//          };
//
        TreeMap<String, ArrayList<String>> map = new TreeMap<String, ArrayList<String>>();
        map.put("12", new ArrayList<String>());
        map.get("12").add("abacaba");
        TreeMap<String, Integer> mmap = new TreeMap<String, Integer>();
        mmap.put("df", 2);
        map =(TreeMap<String, ArrayList<String>>) (JSONValue.parse(JSONValue.toJSONString(mmap)));
        for(Entry<String, ArrayList<String>>key : map.entrySet()){
//            key.getValue().add(3);
            System.out.println(key.getValue());
        }
    }
}
