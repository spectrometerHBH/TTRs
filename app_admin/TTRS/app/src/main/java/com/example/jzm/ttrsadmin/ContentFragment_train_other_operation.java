package com.example.jzm.ttrsadmin;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONObject;

import es.dmoral.toasty.Toasty;

public class ContentFragment_train_other_operation extends Fragment
    implements View.OnClickListener{
    private View view;
    private EditText trainId;
    private Button sale;
    private Button delete;
    ProgressbarFragment progressbarFragment = new ProgressbarFragment();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
        view = inflater.inflate(R.layout.contain_train_other_operation, container, false);
        initializeWidgets(view);
        sale.setOnClickListener(this);
        delete.setOnClickListener(this);
        return view;
    }

    private void initializeWidgets(View view){
        trainId = view.findViewById(R.id.train_other_operation_Edit);
        sale = view.findViewById(R.id.contain_train_publicize_button);
        delete = view.findViewById(R.id.contain_train_delete_button);
    }

    @Override
    public void onClick(View view){
        switch (view.getId()){
            case R.id.contain_train_publicize_button : {
                String train = trainId.getText().toString();
                JSONObjectStringCreate jsonObjectStringCreate = new JSONObjectStringCreate();
                jsonObjectStringCreate.addStringPair("type", "sale_train");
                jsonObjectStringCreate.addStringPair("train_id", train);
                try {

                    progressbarFragment.setCancelable(false);
                    progressbarFragment.show(getActivity().getFragmentManager());
                }catch (Exception e){
                    e.printStackTrace();
                }
                sale(jsonObjectStringCreate.getResult());
                break;
            }
            case R.id.contain_train_delete_button :{
                String train = trainId.getText().toString();
                JSONObjectStringCreate jsonObjectStringCreate = new JSONObjectStringCreate();
                jsonObjectStringCreate.addStringPair("type", "delete_train");
                jsonObjectStringCreate.addStringPair("train_id", train);
                try{

                    progressbarFragment.setCancelable(false);
                    progressbarFragment.show(getActivity().getFragmentManager());
                }catch (Exception e){
                    e.printStackTrace();
                }
                delete(jsonObjectStringCreate.getResult());
                break;
            }
            default:{
                break;
            }
        }
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

    private void sale(final String command){
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpClient client = new HttpClient();
                client.setCommand(command);
                try {
                    JSONObject jsonObject = new JSONObject(client.run());
                    if (jsonObject.getString("success").equals("true")){
                        progressbarFragment.dismiss();
                        showResponse("公开车次成功O(∩_∩)O", "success");
                    }else{
                        progressbarFragment.dismiss();
                        showResponse("公开车次失败( ⊙ o ⊙ )", "error");
                    }
                }catch (Exception e){
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

    private void delete(final String command){
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpClient client = new HttpClient();
                client.setCommand(command);
                try{
                    JSONObject jsonObject = new JSONObject(client.run());
                    if (jsonObject.getString("success").equals("true")){
                        progressbarFragment.dismiss();
                        showResponse("删除车次成功O(∩_∩)O", "success");
                    }else{
                        progressbarFragment.dismiss();
                        showResponse("删除车次失败(*@ο@*) ", "error");
                    }
                }catch (Exception e){
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
}
