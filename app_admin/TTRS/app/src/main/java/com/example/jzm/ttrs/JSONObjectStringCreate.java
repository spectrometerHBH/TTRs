package com.example.jzm.ttrs;

public class JSONObjectStringCreate {
    private String result;

    public JSONObjectStringCreate() { result = ""; }
    public void addStringPair(String key, String value){
        result = result + "\"" + key + "\":\"" + value + "\",";
    }
    public void addIntPair(String key, String value){
        result = result + "\"" + key + "\":" + value + ",";
    }
    public String getResult(){
        String ans = result.substring(0, result.length() - 1);
        return "{" + ans + "}";
    }
}
