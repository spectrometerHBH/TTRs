package com.example.jzm.ttrs;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONObject;

public class ContentFragment_train_detail extends Fragment {
    private View view;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
        view = inflater.inflate(R.layout.contain_trainid_query, container, false);
        final EditText editText = view.findViewById(R.id.trainid_Edit);
        Button button = view.findViewById(R.id.contain_trainid_query_button);
        button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                String trainId = editText.getText().toString();
                if (trainId.equals("")){
                    showResponse("还没有输入列车的id呀");
                }else sendRequest();
            }
        });
        return view;
    }

    private void sendRequest(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    HttpClient client = new HttpClient();
                    EditText editText = view.findViewById(R.id.trainid_Edit);
                    String trainId = editText.getText().toString();
                    client.setCommand("{\"type\":\"query_train\",\"train_id\":\""+trainId+"\"}");
                    String response = client.run();
                    JSONObject jsonObject = new JSONObject(response);
                    String success = jsonObject.getString("success");
                    if (success.equals("true")){
                        Intent intent = new Intent(getActivity(), TrainDetailManifest.class);
                        intent.putExtra("JSON", jsonObject.toString());
                        startActivity(intent);
                    }else{
                        showResponse("这是一辆幽灵列车");
                    }
                }catch (Exception e){
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
