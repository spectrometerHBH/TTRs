package com.example.jzm.ttrs;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatImageButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.haibin.calendarview.CalendarView;

import org.json.JSONObject;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import static android.app.Activity.RESULT_OK;

public class ContentFragment_train_query extends Fragment {
    private View view;
    private EditText departureTextview;
    private EditText destinationTextview;
    private AppCompatImageButton exchangeButton;
    private Button calenderButton;
    private TextView monthTextview;
    private TextView dayTextview;
    private TextView yearTextview;
    private List<CheckBox> checkBoxes = new ArrayList<>();
    private CheckBox transferCheckBox;
    private Button queryButton;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.contain_train_query, container, false);
        initializeWidgets(view);
        exchangeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String departure = departureTextview.getText().toString();
                String destination = destinationTextview.getText().toString();
                departureTextview.setText(destination);
                destinationTextview.setText(departure);
            }
        });
        calenderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String day = dayTextview.getText().toString();
                String month = monthTextview.getText().toString();
                String year = yearTextview.getText().toString();
                Intent intent = new Intent(getActivity(), Calender.class);
                intent.putExtra("day", day);
                intent.putExtra("month", month);
                intent.putExtra("year", year);
                startActivityForResult(intent, 1);
            }
        });
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
        yearTextview.setText(String.valueOf(calendar.get(Calendar.YEAR)));
        monthTextview.setText(String.valueOf(calendar.get(Calendar.MONTH) + 1));
        dayTextview.setText(String.valueOf(calendar.get(Calendar.DATE)));
        CheckBox checkBoxAll = checkBoxes.get(0);
        checkBoxAll.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    for (CheckBox checkBox : checkBoxes)
                        checkBox.setChecked(true);
                } else {
                    for (CheckBox checkBox : checkBoxes)
                        checkBox.setChecked(false);
                }
            }
        });
        queryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String loc1 = departureTextview.getText().toString();
                String loc2 = destinationTextview.getText().toString();
                String year = yearTextview.getText().toString();
                String month = monthTextview.getText().toString();
                String day = dayTextview.getText().toString();
                if (Integer.valueOf(month) < 10) month = "0" + month;
                if (Integer.valueOf(day) < 10) day = "0" + day;
                String time = year + "-" + month + "-" + day;
                String catalog = "";
                for (int i = 1; i < 8; i++) {
                    CheckBox checkBox = checkBoxes.get(i);
                    if (checkBox.isChecked())
                        catalog = catalog + checkBox.getText().toString().substring(0, 1);
                }
                JSONObjectStringCreate jsonObjectStringCreate = new JSONObjectStringCreate();
                if (transferCheckBox.isChecked()) {
                    jsonObjectStringCreate.addStringPair("type", "query_transfer");
                }else{
                    jsonObjectStringCreate.addStringPair("type", "query_ticket");
                }
                jsonObjectStringCreate.addStringPair("loc1", loc1);
                jsonObjectStringCreate.addStringPair("loc2", loc2);
                jsonObjectStringCreate.addStringPair("date", time);
                jsonObjectStringCreate.addStringPair("catalog", catalog);
                String command = jsonObjectStringCreate.getResult();
                sendRequest(command);
            }
        });
        return view;
    }

    private void initializeWidgets(View view) {
        departureTextview = view.findViewById(R.id.contain_train_query_departure);
        destinationTextview = view.findViewById(R.id.contain_train_query_destination);
        exchangeButton = view.findViewById(R.id.contain_train_query_exchange);
        calenderButton = view.findViewById(R.id.calendar_enter);
        yearTextview = view.findViewById(R.id.contain_train_query_year);
        monthTextview = view.findViewById(R.id.contain_train_query_month);
        dayTextview = view.findViewById(R.id.contain_train_query_day);
        transferCheckBox = view.findViewById(R.id.contain_train_query_checkBox_transfer);
        queryButton = view.findViewById(R.id.contain_train_query_button);
        checkBoxes.add((CheckBox) view.findViewById(R.id.contain_train_query_checkBox_all));
        checkBoxes.add((CheckBox) view.findViewById(R.id.contain_train_query_checkBox_T));
        checkBoxes.add((CheckBox) view.findViewById(R.id.contain_train_query_checkBox_Z));
        checkBoxes.add((CheckBox) view.findViewById(R.id.contain_train_query_checkBox_C));
        checkBoxes.add((CheckBox) view.findViewById(R.id.contain_train_query_checkBox_O));
        checkBoxes.add((CheckBox) view.findViewById(R.id.contain_train_query_checkBox_G));
        checkBoxes.add((CheckBox) view.findViewById(R.id.contain_train_query_checkBox_D));
        checkBoxes.add((CheckBox) view.findViewById(R.id.contain_train_query_checkBox_K));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 1: {
                if (resultCode == RESULT_OK) {
                    String day = data.getStringExtra("day");
                    String month = data.getStringExtra("month");
                    String year = data.getStringExtra("year");
                    dayTextview.setText(day);
                    monthTextview.setText(month);
                    yearTextview.setText(year);
                }
                break;
            }
            default: {
                break;
            }
        }
    }

    private void sendRequest(final String command){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    HttpClient client = new HttpClient();
                    client.setCommand(command);
                    JSONObject jsonObject = new JSONObject(client.run());
                    String num = jsonObject.getString("num");
                    if (!num.equals("0")) {
                        Intent intent = new Intent(getActivity(), TicketManifest.class);
                        intent.putExtra("data", jsonObject.toString());
                        startActivity(intent);
                    }else{
                        showResponse("没有这样的车票呀( ⊙ o ⊙ )！");
                    }
                } catch (Exception e){
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void showResponse(final String message){
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getActivity().getApplicationContext(), message, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
