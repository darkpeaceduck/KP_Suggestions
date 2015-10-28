package ru.kpsug.utils;

import org.json.simple.JSONAware;
import org.json.simple.JSONObject;

public interface JSONParceble extends JSONAware{
    public JSONObject toJSONObject();
    
    //refreshing if no parse error, else => oldstate
    public boolean refreshStateFromJSONString(String s);
    public boolean refreshStateFromObject(Object object);
}
