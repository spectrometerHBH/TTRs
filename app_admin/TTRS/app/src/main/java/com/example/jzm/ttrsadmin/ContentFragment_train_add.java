package com.example.jzm.ttrsadmin;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ContentFragment_train_add extends Fragment {
    private View view;
    private EditText trainIdEditText;
    private EditText trainNameEditText;
    private EditText trainCatalogEditText;
    private CheckBox checkBoxAll;
    private List<CheckBox> checkBoxList = new ArrayList<>();
    private Map<Integer, String> seatType = new HashMap<>();
    private ArrayList<String> seatTypes = new ArrayList<>();
    private Button addTrain;
    private Button modifyTrain;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
        view = inflater.inflate(R.layout.contain_train_add, container, false);
        initializeWidgets(view);

        checkBoxAll.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b){
                    for (CheckBox checkBox : checkBoxList)
                        checkBox.setChecked(true);
                }else{
                    for (CheckBox checkBox : checkBoxList)
                        checkBox.setChecked(false);
                }
            }
        });
        addTrain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String trainId = trainIdEditText.getText().toString();
                String trainName = trainNameEditText.getText().toString();
                String trainCatalog = trainCatalogEditText.getText().toString();
                try {
                    if (!trainIdCheck(trainId)) return;
                    if (!trainNameCheck(trainName)) return;
                    if (!trainCatalogCheck(trainCatalog)) return;
                    seatTypes.clear();
                    for (int i = 0; i < 11; i++){
                        CheckBox checkBox = checkBoxList.get(i);
                        if (checkBox.isChecked()) seatTypes.add(seatType.get(i));
                    }
                    if (seatTypes.isEmpty()){
                        showWarning("这是一辆没有座位的列车+_+");
                        return;
                    }
                    Intent intent = new Intent(getActivity().getApplicationContext(), GetStation.class);
                    intent.putExtra("trainId", trainId);
                    intent.putExtra("trainName", trainName);
                    intent.putStringArrayListExtra("seats", seatTypes);
                    intent.putExtra("trainCatalog", trainCatalog);
                    intent.putExtra("type", "add_train");
                    startActivity(intent);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        });
        modifyTrain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String trainId = trainIdEditText.getText().toString();
                String trainName = trainNameEditText.getText().toString();
                String trainCatalog = trainCatalogEditText.getText().toString();
                try {
                    if (!trainIdCheck(trainId)) return;
                    if (!trainNameCheck(trainName)) return;
                    if (!trainCatalogCheck(trainCatalog)) return;
                    seatTypes.clear();
                    for (int i = 0; i < 11; i++){
                        CheckBox checkBox = checkBoxList.get(i);
                        if (checkBox.isChecked()) seatTypes.add(seatType.get(i));
                    }
                    if (seatTypes.isEmpty()){
                        showWarning("这是一辆没有座位的列车+_+");
                        return;
                    }
                    Intent intent = new Intent(getActivity().getApplicationContext(), GetStation.class);
                    intent.putExtra("trainId", trainId);
                    intent.putExtra("trainName", trainName);
                    intent.putStringArrayListExtra("seats", seatTypes);
                    intent.putExtra("trainCatalog", trainCatalog);
                    intent.putExtra("type", "modify_train");
                    startActivity(intent);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        });
        return view;
    }

    private void initializeWidgets(View view){
        trainIdEditText = view.findViewById(R.id.trainid_add_Edit);
        trainNameEditText = view.findViewById(R.id.trainname_add_Edit);
        trainCatalogEditText = view.findViewById(R.id.traincatalog_add_Edit);
        checkBoxAll = view.findViewById(R.id.checkBox_all);
        checkBoxList.add((CheckBox) view.findViewById(R.id.checkBox_swz));
        checkBoxList.add((CheckBox) view.findViewById(R.id.checkBox_yz));
        checkBoxList.add((CheckBox) view.findViewById(R.id.checkBox_yw));
        checkBoxList.add((CheckBox) view.findViewById(R.id.checkBox_ydz));
        checkBoxList.add((CheckBox) view.findViewById(R.id.checkBox_rz));
        checkBoxList.add((CheckBox) view.findViewById(R.id.checkBox_rw));
        checkBoxList.add((CheckBox) view.findViewById(R.id.checkBox_edz));
        checkBoxList.add((CheckBox) view.findViewById(R.id.checkBox_wz));
        checkBoxList.add((CheckBox) view.findViewById(R.id.checkBox_dw));
        checkBoxList.add((CheckBox) view.findViewById(R.id.checkBox_tdz));
        checkBoxList.add((CheckBox) view.findViewById(R.id.checkBox_gjrw));
        addTrain = view.findViewById(R.id.contain_train_add_button);
        modifyTrain = view.findViewById(R.id.contain_train_modify_button);
        seatType.put(0, "商务座");
        seatType.put(1, "硬座");
        seatType.put(2, "硬卧");
        seatType.put(3, "一等座");
        seatType.put(4, "软座");
        seatType.put(5, "软卧");
        seatType.put(6, "二等座");
        seatType.put(7, "无座");
        seatType.put(8, "动卧");
        seatType.put(9, "特等座");
        seatType.put(10, "高级软卧");
    }

    private void showWarning(final String message){
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean empty(String s, String message){
        if (s.equals("")) {
            showWarning("未输入" + message + "呀~QAQ~");
            return true;
        }else return false;
    }

    private boolean tooLong(String s, String message) throws UnsupportedEncodingException {
        if (s.getBytes("UTF-8").length > 20){
            showWarning(message + "太长了呀~QAQ");
            return true;
        }else return false;
    }

    private boolean checkWhiteSpace(String s, String message){
        if (s.contains(" ")) {
            showWarning(message + "不能有空格呀~QAQ~");
            return true;
        }else return false;
    }

    private boolean single(String s, String message) throws UnsupportedEncodingException {
        if (s.getBytes("UTF-8").length != 1){
            showWarning(message + "只能有一种呀~QAQ~");
            return false;
        }else return true;
    }

    private boolean trainIdCheck(String s) throws UnsupportedEncodingException {
        if (empty(s, "火车ID")) return false;
        if (tooLong(s, "火车ID")) return false;
        if (checkWhiteSpace(s, "火车ID")) return false;
        return true;
    }


    private boolean trainCatalogCheck(String s) throws UnsupportedEncodingException {
        if (empty(s, "火车类别")) return false;
        if (!single(s, "火车类别")) return false;
        if (checkWhiteSpace(s, "火车类别")) return false;
        return true;
    }

    private boolean trainNameCheck(String s) throws UnsupportedEncodingException {
        if (empty(s, "火车名字")) return false;
        if (tooLong(s, "火车名字")) return false;
        if (checkWhiteSpace(s, "火车名字")) return false;
        return true;
    }
}
