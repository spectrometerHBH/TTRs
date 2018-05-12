package com.example.jzm.ttrs;

import android.content.Intent;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Message;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

public class UserQuery extends AppCompatActivity
        implements View.OnClickListener{

    private EditText editUserid;
    private Button queryButton;
    private JSONObject myInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_management);

        Toolbar toolbar = findViewById(R.id.toolbar_user_management);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        initializeWidgets();
        Intent intent = getIntent();
        try {
            myInfo = new JSONObject(intent.getStringExtra("info"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        queryButton.setOnClickListener(this);
    }

    private void initializeWidgets(){
        editUserid = findViewById(R.id.userid_Edit);
        queryButton = findViewById(R.id.contain_userid_query_button);
        ImageView useridCLear = findViewById(R.id.userid_query_Clear);
        EditTextClearTools.addClearListener(editUserid, useridCLear);
    }


    @Override
    public void onClick(View view){
        switch (view.getId()) {
            case R.id.contain_userid_query_button:{
                String useridQuery = editUserid.getText().toString();
                try {
                    if (!useridCheck(useridQuery)) break;
                    sendRequest();
                } catch (Exception e){
                    e.printStackTrace();
                }
                break;
            }
            default: break;
        }
    }

    private void sendRequest(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String useridQuery = editUserid.getText().toString();
                    HttpClient client = new HttpClient();
                    JSONObjectStringCreate jsonobjcetcreate = new JSONObjectStringCreate();
                    jsonobjcetcreate.addStringPair("type", "query_profile");
                    jsonobjcetcreate.addStringPair("id", useridQuery);
                    client.setCommand(jsonobjcetcreate.getResult());
                    JSONObject jsonObject = new JSONObject(client.run());
                    String success = jsonObject.getString("success");
                    if (success.equals("true")) {
                        Intent intent = new Intent(UserQuery.this, ModifyUserInfoAdmin.class);
                        intent.putExtra("myInfo", myInfo.toString());
                        intent.putExtra("userInfo", jsonObject.toString());
                        intent.putExtra("userID", useridQuery);
                        startActivity(intent);
                    }
                    else showResponse("不知道为什么获取不到信息~QAQ~");
                } catch (Exception e){
                    e.printStackTrace();
                }
            }
        }).start();
    }

    @Override
    public void onBackPressed() {
        finish();
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

    private boolean useridCheck(String s) throws UnsupportedEncodingException {
        if (empty(s, "id")) return false;
        if (tooLong(s, "id")) return false;
        if (checkWhiteSpace(s, "id")) return false;
        return true;
    }

    private void showWarning(final String message){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(UserQuery.this, message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showResponse(final String message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(UserQuery.this, message, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
