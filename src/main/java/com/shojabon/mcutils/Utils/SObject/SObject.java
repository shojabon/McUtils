package com.shojabon.mcutils.Utils.SObject;


import com.google.gson.Gson;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class SObject {

    public SObject(){
    }

    public JSONObject toJSON(){
        try {
            return (JSONObject) new JSONParser().parse(new Gson().toJson(this));
        } catch (ParseException e) {
            return null;
        }
    }

    public String toJSONString(){
        return new Gson().toJson(this);
    }

    public <T> T fromJSON(JSONObject obj, Class<T> classObject){
        return new Gson().fromJson(obj.toJSONString(), classObject);
    }

    public <T> T fromJSONString(String obj, Class<T> classObject){
        return new Gson().fromJson(obj, classObject);
    }

}
