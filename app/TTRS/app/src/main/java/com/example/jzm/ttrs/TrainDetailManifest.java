package com.example.jzm.ttrs;

import android.content.Intent;
import android.graphics.Paint;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.icu.lang.UCharacter.GraphemeClusterBreak.T;

public class TrainDetailManifest extends AppCompatActivity implements View.OnClickListener{
    private JSONObject jsonObject;
    private String train_id;
    private String train_name;
    private int stationnum;
    private int pricenum;
    private JSONArray ticket;
    private JSONArray station;
    private Map<String, Integer> map = new HashMap<>();
    private TextView textViewTrainID;
    private TextView textViewTrainName;
    private List<CheckBox> checkBoxes = new ArrayList<>();
    private List<String> ticket_types = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_train_detail_manifest);
        Toolbar toolbar = findViewById(R.id.toolbar_train_detail_manifest);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Intent intent = getIntent();
        initializeMap();
        initializeWidgets();
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        try {
            jsonObject = new JSONObject(intent.getStringExtra("JSON"));
            train_id = jsonObject.getString("train_id");
            train_name = jsonObject.getString("name");
            stationnum = jsonObject.getInt("stationnum");
            pricenum = jsonObject.getInt("pricenum");
            ticket = jsonObject.getJSONArray("ticket");
            transJSONtoList();
            station = jsonObject.getJSONArray("station");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        textViewTrainID.setText(train_id);
        textViewTrainName.setText(train_name);
        for (int i = 0; i < 11; i++) {
            CheckBox checkBox = checkBoxes.get(i);
            checkBox.setEnabled(false);
            checkBox.getPaint().setAntiAlias(true);
            checkBox.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
        }
        for (int i = 0; i < ticket.length(); i++){
            try {
                String seat = (String) ticket.get(i);
                CheckBox checkBox = checkBoxes.get(map.get(seat));
                checkBox.setEnabled(true);
                checkBox.getPaint().setFlags(0);
                checkBox.getPaint().setAntiAlias(true);
            }catch (Exception e){
                e.printStackTrace();
            }
        }

        for (int i = 0; i < ticket.length(); i++){
            try {
                String seat = (String) ticket.get(i);
                final CheckBox checkBox = checkBoxes.get(map.get(seat));
                checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (isChecked){
                            for (int i = 0; i < ticket.length(); i++){
                                try {
                                    String seat = (String) ticket.get(i);
                                    if (seat.equals(checkBox.getText())) continue;
                                    CheckBox checkBox = checkBoxes.get(map.get(seat));
                                    checkBox.setEnabled(false);
                                    checkBox.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
                                    checkBox.getPaint().setAntiAlias(true);
                                }catch (Exception e){
                                    e.printStackTrace();
                                }
                            }
                        }else{
                            for (int i = 0; i < ticket.length(); i++){
                                try {
                                    String seat = (String) ticket.get(i);
                                    CheckBox checkBox = checkBoxes.get(map.get(seat));
                                    checkBox.setEnabled(true);
                                    checkBox.getPaint().setFlags(0);
                                    checkBox.getPaint().setAntiAlias(true);
                                }catch (Exception e){
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                });
            }catch (Exception e){
                e.printStackTrace();
            }
        }

        Button button = findViewById(R.id.contain_train_detail_manifest_button);
        button.setOnClickListener(this);
    }

    private void initializeWidgets(){
        textViewTrainID = findViewById(R.id.contain_train_detail_manifest_trainid);
        textViewTrainName = findViewById(R.id.contain_train_detail_manifest_trainname);
        checkBoxes.add((CheckBox)findViewById(R.id.contain_train_detail_manifest_gaojiruanwo));
        checkBoxes.add((CheckBox)findViewById(R.id.contain_train_detail_manifest_yingwo));
        checkBoxes.add((CheckBox)findViewById(R.id.contain_train_detail_manifest_ruanwo));
        checkBoxes.add((CheckBox)findViewById(R.id.contain_train_detail_manifest_dongwo));
        checkBoxes.add((CheckBox)findViewById(R.id.contain_train_detail_manifest_shangwuzuo));
        checkBoxes.add((CheckBox)findViewById(R.id.contain_train_detail_manifest_tedengzuo));
        checkBoxes.add((CheckBox)findViewById(R.id.contain_train_detail_manifest_yidengzuo));
        checkBoxes.add((CheckBox)findViewById(R.id.contain_train_detail_manifest_erdengzuo));
        checkBoxes.add((CheckBox)findViewById(R.id.contain_train_detail_manifest_ruanzuo));
        checkBoxes.add((CheckBox)findViewById(R.id.contain_train_detail_manifest_yingzuo));
        checkBoxes.add((CheckBox)findViewById(R.id.contain_train_detail_manifest_wuzuo));
    }

    private void initializeMap(){
        map.put("高级软卧",0);
        map.put("硬卧",1);
        map.put("软卧",2);
        map.put("动卧",3);
        map.put("商务座",4);
        map.put("特等座",5);
        map.put("一等座",6);
        map.put("二等座",7);
        map.put("软座",8);
        map.put("硬座",9);
        map.put("无座",10);
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.contain_train_detail_manifest_button: {
                int now_ticket = 0;
                for (; now_ticket < 11; now_ticket++) {
                    if (checkBoxes.get(now_ticket).isEnabled() && checkBoxes.get(now_ticket).isChecked()) break;
                }
                if (now_ticket == 11) {
                    Toast.makeText(TrainDetailManifest.this, "还没选择席别呀", Toast.LENGTH_SHORT).show();
                } else {
                    Intent intent = new Intent(TrainDetailManifest.this, TimeTable.class);
                    intent.putExtra("station", station.toString());
                    intent.putExtra("ticket_type", ticket_types.indexOf(checkBoxes.get(now_ticket).getText().toString()));
                    //Toast.makeText(TrainDetailManifest.this, String.valueOf(ticket_types.indexOf(checkBoxes.get(now_ticket).getText().toString())), Toast.LENGTH_LONG).show();
                    startActivity(intent);
                }
            }
        }
    }

    private void transJSONtoList() throws JSONException {
        for (int i = 0; i < ticket.length(); i++)
            ticket_types.add(ticket.getString(i));
    }
}
