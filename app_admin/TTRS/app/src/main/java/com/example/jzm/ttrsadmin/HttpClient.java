package com.example.jzm.ttrsadmin;

import android.content.Context;
import android.widget.Toast;

import java.util.concurrent.TimeUnit;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class HttpClient {
    private String command;
    private String responseData;
    private Context context;

    public HttpClient(){
        this.context = context;
    }
    public void setCommand(String command) {
        this.command = command;
    }

    public String run() {
        try {
            OkHttpClient client = new OkHttpClient.Builder().connectTimeout(100, TimeUnit.SECONDS).readTimeout(200, TimeUnit.SECONDS).build();
            RequestBody requestBody = new FormBody.Builder().add("input", command).build();
            Request request = new Request.Builder().url("http://120.79.236.3:5000/action/post").post(requestBody).build();
            Response response = client.newCall(request).execute();
            responseData = response.body().string();
        }catch (Exception e){

            e.printStackTrace();
        }
        return responseData;
    }
}