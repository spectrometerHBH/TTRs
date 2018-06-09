package com.example.jzm.ttrsadmin;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class TimeTableWithoutPrice extends AppCompatActivity {

    private List<Station> stationList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_table_without_price);
        View view = findViewById(R.id.activity_time_table_without_price);
        view.setBackgroundColor(Color.parseColor("#90000000"));
        Intent intent = getIntent();
        try {
            JSONArray stations = new JSONArray(intent.getStringExtra("station"));
            for (int i = 0; i < stations.length(); i++) {
                JSONObject jsonObject = stations.getJSONObject(i);
                Station station = new Station(jsonObject.getString("name"),
                        jsonObject.getString("timearriv"),
                        jsonObject.getString("timestart"),
                        jsonObject.getString("timestopover"), 666.0);
                stationList.add(station);
            }
            RecyclerView recyclerView = findViewById(R.id.time_table_without_pricerecyclerview);
            LinearLayoutManager layoutManager = new LinearLayoutManager(this);
            recyclerView.setLayoutManager(layoutManager);
            StationWithoutPriceAdapter adapter = new StationWithoutPriceAdapter(stationList);
            recyclerView.setAdapter(adapter);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
