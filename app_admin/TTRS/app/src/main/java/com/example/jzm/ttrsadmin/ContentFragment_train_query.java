package com.example.jzm.ttrsadmin;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatImageButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
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

import es.dmoral.toasty.Toasty;

import static android.app.Activity.RESULT_OK;

public class ContentFragment_train_query extends Fragment {
    private View view;
    private Button departureSelect;
    private Button destinationSelect;
    private AppCompatImageButton exchangeButton;
    private Button calenderButton;
    private TextView monthTextview;
    private TextView dayTextview;
    private TextView yearTextview;
    private List<CheckBox> checkBoxes = new ArrayList<>();
    private CheckBox transferCheckBox;
    private Button queryButton;
    private String userId;
    private String userCatalog;
    private String queryType;

    ProgressbarFragment progressbarFragment = new ProgressbarFragment();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.contain_train_query, container, false);
        initializeWidgets(view);
        exchangeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String departure = departureSelect.getText().toString();
                String destination = destinationSelect.getText().toString();
                departureSelect.setText(destination);
                destinationSelect.setText(departure);
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
                String loc1 = departureSelect.getText().toString();
                String loc2 = destinationSelect.getText().toString();
                String year = yearTextview.getText().toString();
                String month = monthTextview.getText().toString();
                String day = dayTextview.getText().toString();
                if (Integer.valueOf(month) < 10) month = "0" + month;
                if (Integer.valueOf(day) < 10) day = "0" + day;
                String time = year + "-" + month + "-" + day;
                userCatalog = "";
                for (int i = 1; i < 8; i++) {
                    CheckBox checkBox = checkBoxes.get(i);
                    if (checkBox.isChecked())
                        userCatalog = userCatalog + checkBox.getText().toString().substring(0, 1);
                }
                if (userCatalog.equals("")){
                    showResponse("还没选要看的类型啊~QAQ~", "info");
                    return;
                }
                JSONObjectStringCreate jsonObjectStringCreate = new JSONObjectStringCreate();
                if (transferCheckBox.isChecked()) {
                    queryType = "query_transfer";
                    jsonObjectStringCreate.addStringPair("type", "query_transfer");
                }else{
                    queryType = "query_ticket";
                    jsonObjectStringCreate.addStringPair("type", "query_ticket");
                }
                jsonObjectStringCreate.addStringPair("loc1", loc1);
                jsonObjectStringCreate.addStringPair("loc2", loc2);
                jsonObjectStringCreate.addStringPair("date", time);
                jsonObjectStringCreate.addStringPair("catalog", userCatalog);
                String command = jsonObjectStringCreate.getResult();

                try {
                    progressbarFragment.setCancelable(false);
                    progressbarFragment.show(getActivity().getFragmentManager());
                }catch (Exception e){
                    e.printStackTrace();
                }
                sendRequest(command);
            }
        });
        departureSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), SelectStation.class);
                intent.putExtra("type", "depart");
                startActivityForResult(intent, 2);
            }
        });
        destinationSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), SelectStation.class);
                intent.putExtra("type", "destination");
                startActivityForResult(intent, 2);
            }
        });
        return view;
    }

    private void initializeWidgets(View view) {
        departureSelect = view.findViewById(R.id.contain_train_query_departure);
        destinationSelect = view.findViewById(R.id.contain_train_query_destination);
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
            case 2:{
                if (resultCode == RESULT_OK) {
                    String type = data.getStringExtra("type");
                    String station = data.getStringExtra("station");
                    if (type.equals("depart")) {
                        departureSelect.setText(station);
                    } else {
                        destinationSelect.setText(station);
                    }
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
                    if (jsonObject.getString("success").equals("false")){
                        progressbarFragment.dismiss();
                        showResponse("\"没有这样的车票呀( ⊙ o ⊙ )！\"", "error");
                        return;
                    }
                    String num = jsonObject.getString("num");
                    if (!num.equals("0")) {
                        Intent intent = new Intent(getActivity(), TicketManifest.class);
                        intent.putExtra("data", jsonObject.toString());
                        if (getArguments() != null) {
                            userId = getArguments().getString("id");
                        }
                        intent.putExtra("id", userId);
                        intent.putExtra("catalog", userCatalog);
                        intent.putExtra("type", queryType);
                        progressbarFragment.dismiss();
                        startActivity(intent);
                    }else{
                        progressbarFragment.dismiss();
                        showResponse("没有这样的车票呀( ⊙ o ⊙ )！", "error");
                    }
                } catch (Exception e){
                    showResponse("小熊猫联系不上饲养员了，请检查网络连接%>_<%", "warning");
                    try{
                        progressbarFragment.dismiss();
                    }catch (Exception ex){
                        ex.printStackTrace();
                    }
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void showResponse(final String message, final String type){
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    switch (type) {
                        case "error": {
                            Toasty.error(getActivity(), message, Toast.LENGTH_SHORT).show();
                            break;
                        }
                        case "success": {
                            Toasty.success(getActivity(), message, Toast.LENGTH_SHORT).show();
                            break;
                        }
                        case "info": {
                            Toasty.info(getActivity(), message, Toast.LENGTH_SHORT).show();
                            break;
                        }
                        case "warning": {
                            Toasty.warning(getActivity(), message, Toast.LENGTH_SHORT).show();
                            break;
                        }
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
    }
}
