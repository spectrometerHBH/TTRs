package com.example.jzm.ttrsadmin;

public class JSONArrayStringCreate {
    private String result;

    public JSONArrayStringCreate() { result = ""; }
    public void addString(String item){
        result = result + "\"" + item + "\",";
    }
    public void addInt(String jsonObject){
        result = result + jsonObject + ",";
    }
    public void addJSONObject(String jsonObject){
        result = result + jsonObject + ",";
    }
    public String getResult(){
        String ans = result.substring(0, result.length() - 1);
        return "[" + ans + "]";
    }
}
