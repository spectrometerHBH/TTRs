package com.example.jzm.ttrsadmin;

import android.animation.TimeAnimator;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class GetStation extends AppCompatActivity {
    private LinearLayout linearLayout;
    private Button addButton;
    private Button confirmButton;
    private Context context;
    private String trainId;
    private String trainName;
    private String trainCatalog;
    private ArrayList<String> seatTypes = new ArrayList<>();
    private List<View> viewList = new ArrayList<>();
    private List<List<View>> subviewList = new ArrayList<>();
    private String type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_staion);
        initializeWidgets();
        Intent intent = getIntent();
        trainId = intent.getStringExtra("trainId");
        trainName = intent.getStringExtra("trainName");
        seatTypes = intent.getStringArrayListExtra("seats");
        trainCatalog = intent.getStringExtra("trainCatalog");

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                LayoutInflater layoutInflater = LayoutInflater.from(context);
                View newView = layoutInflater.inflate(R.layout.item_for_train_add, null);
                fill(newView);
                newView.setLayoutParams(layoutParams);
                linearLayout.addView(newView);
                viewList.add(newView);

                final TextView arrive = viewList.get(viewList.size() - 1).findViewById(R.id.arrive_time_item);
                arrive.setClickable(true);
                arrive.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final Calendar c = Calendar.getInstance();
                        final int hour = c.get(Calendar.HOUR_OF_DAY);
                        final int minute = c.get(Calendar.MINUTE);
                        new TimePickerDialog(GetStation.this, new TimePickerDialog.OnTimeSetListener() {

                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                arrive.setText((hourOfDay < 10 ? "0" : "")+ hourOfDay + ":" +(minute < 10 ? "0" : "") + minute);
                            }
                        }, hour, minute, true).show();
                    }
                });
                final TextView depart = viewList.get(viewList.size() - 1).findViewById(R.id.depart_time_item);
                depart.setClickable(true);
                depart.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final Calendar c = Calendar.getInstance();
                        final int hour = c.get(Calendar.HOUR_OF_DAY);
                        final int minute = c.get(Calendar.MINUTE);
                        new TimePickerDialog(GetStation.this, new TimePickerDialog.OnTimeSetListener() {

                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                depart.setText((hourOfDay < 10 ? "0" : "") + hourOfDay + ":" +(minute < 10 ? "0" : "") + minute);
                            }
                        }, hour, minute, true).show();
                    }
                });
                final TextView stopover = viewList.get(viewList.size() - 1).findViewById(R.id.stopover_item);
                stopover.setClickable(true);
                stopover.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final Calendar c = Calendar.getInstance();
                        final int hour = c.get(Calendar.HOUR_OF_DAY);
                        final int minute = c.get(Calendar.MINUTE);
                        new TimePickerDialog(GetStation.this, new TimePickerDialog.OnTimeSetListener() {

                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                stopover.setText((hourOfDay < 10 ? "0" : "") + hourOfDay + ":" +(minute < 10 ? "0" : "") + minute);
                            }
                        }, hour, minute, true).show();
                    }
                });

            }
        });


        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                JSONObjectStringCreate commandMaker = new JSONObjectStringCreate();
                commandMaker.addStringPair("type", type);
                commandMaker.addStringPair("train_id", trainId);
                commandMaker.addStringPair("name", trainName);
                commandMaker.addIntPair("stationnum", String.valueOf(viewList.size()));
                commandMaker.addIntPair("pricenum", String.valueOf(seatTypes.size()));
                JSONArrayStringCreate seatTypeString = new JSONArrayStringCreate();
                for (String seat : seatTypes){
                    seatTypeString.addString(seat);
                }
                commandMaker.addJSONArrayPair("ticket", seatTypeString.getResult());
                JSONArrayStringCreate jsonArrayStringCreate = new JSONArrayStringCreate();
                for (int i = 0; i < viewList.size(); i++){
                    jsonArrayStringCreate.addJSONObject(getJSON(viewList.get(i), i));
                }
                commandMaker.addJSONArrayPair("station", jsonArrayStringCreate.getResult());
                commandMaker.addStringPair("catalog", trainCatalog);
                sendRequest(commandMaker.getResult());
            }
        });
    }

    private String getJSON(View view, int parentPos){
        JSONObjectStringCreate jsonObjectStringCreate = new JSONObjectStringCreate();
        EditText stationEditText = view.findViewById(R.id.station_item);
        TextView arriveTimeEditText = view.findViewById(R.id.arrive_time_item);
        TextView startTimeEditText = view.findViewById(R.id.depart_time_item);
        TextView stopoverTimeEditText = view.findViewById(R.id.stopover_item);
        jsonObjectStringCreate.addStringPair("name", stationEditText.getText().toString());
        if (view == viewList.get(0)) jsonObjectStringCreate.addStringPair("timearriv", "xx:xx");
        else jsonObjectStringCreate.addStringPair("timearriv", arriveTimeEditText.getText().toString());
        if (view == viewList.get(viewList.size() - 1)) jsonObjectStringCreate.addStringPair("timestart", "xx:xx");
        else jsonObjectStringCreate.addStringPair("timestart", startTimeEditText.getText().toString());
        jsonObjectStringCreate.addStringPair("timestopover", stopoverTimeEditText.getText().toString());
        JSONArrayStringCreate jsonArrayStringCreate = new JSONArrayStringCreate();
        List<View> templist = subviewList.get(parentPos);
        for (int i = 0; i < templist.size(); i++){
            EditText price = templist.get(i).findViewById(R.id.price_subitem);
            jsonArrayStringCreate.addInt(price.getText().toString());
        }
        jsonObjectStringCreate.addJSONArrayPair("ticket", jsonArrayStringCreate.getResult());
        return jsonObjectStringCreate.getResult();
    }

    private void fill(View view) {
        LinearLayout subLinearlayout = view.findViewById(R.id.item_linearlayout);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        List<View> sublist = new ArrayList<>();
        for (String seat : seatTypes) {
            View newView = layoutInflater.inflate(R.layout.subitem_for_train_add, null);
            TextView seatName = newView.findViewById(R.id.seat_subitem);
            seatName.setText(seat);
            newView.setLayoutParams(layoutParams);
            sublist.add(newView);
            subLinearlayout.addView(newView);
        }
        subviewList.add(sublist);
    }

    private void initializeWidgets(){
        linearLayout = findViewById(R.id.station_linear_layout);
        addButton = findViewById(R.id.add_button);
        confirmButton = findViewById(R.id.confirm_button);
        context = this;
    }


    private void showResponse(final String message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(GetStation.this, message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void sendRequest(final String command){
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpClient client = new HttpClient();
                client.setCommand(command);
                try {
                    JSONObject jsonObject = new JSONObject(client.run());
                    if (jsonObject.getString("success").equals("true")){
                        showResponse("加车成功O(∩_∩)O");
                    }else{
                        showResponse("加车失败( ⊙ o ⊙ )");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
