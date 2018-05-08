package com.example.jzm.ttrs;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class TimeTable extends AppCompatActivity {

    private List<Station> stationList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_table);
        View view = findViewById(R.id.activity_time_table);
        Intent intent = getIntent();
        try {
            JSONArray stations = new JSONArray(intent.getStringExtra("station"));
            int ticket_type = intent.getIntExtra("ticket_type", 0);
            for (int i = 0; i < stations.length(); i++){
                JSONObject jsonObject = stations.getJSONObject(i);
                JSONArray prices = jsonObject.getJSONArray("ticket");
                Object object = prices.get(ticket_type);
                Double haha = new Double(object.toString());
                Station station = new Station(jsonObject.getString("name"),
                        jsonObject.getString("timearriv"),
                        jsonObject.getString("timestart"),
                        haha);
                stationList.add(station);
            }
            RecyclerView recyclerView = findViewById(R.id.time_table_recyclerview);
            recyclerView.setBackgroundColor(Color.parseColor("#90000000"));
            LinearLayoutManager layoutManager = new LinearLayoutManager(this);
            recyclerView.setLayoutManager(layoutManager);
            StationAdapter adapter = new StationAdapter(stationList);
            recyclerView.setAdapter(adapter);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

}

/*
{"type":"add_train",
 "train_id":"c100",
 "name":"和谐号",
 "stationnum":2,
 "pricenum":2,
 "ticket":["一等座","二等座"],
 "station":[
    {"name":"Hangzhou",
    "timearriv":"xx:xx",
    "timestart":"08:12"
    "timestopover":"00:00",
    "ticket":[0.0,0.0],
    },
    {"name":"Suzhou",
    "timearriv":"12:00",
    "timestart":"xx:xx"
    "timestopover":"00:00",
    "ticket":[756.00,432.00],
    }
 ]
 }
 */
