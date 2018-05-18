package com.example.jzm.ttrsadmin;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Rect;
import android.os.Build;
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
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

import de.hdodenhof.circleimageview.CircleImageView;
import es.dmoral.toasty.Toasty;

public class UserQuery extends AppCompatActivity
        implements View.OnClickListener, NavigationView.OnNavigationItemSelectedListener{

    private EditText editUserid;
    private Button queryButton;
    private NavigationView navigationView;
    private JSONObject userInfo;
    private IntentFilter intentFilter;
    public class MyBroadCastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent){
            try {
                userInfo = new JSONObject(intent.getStringExtra("info"));
                refreshNav();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
    private MyBroadCastReceiver myBroadCastReceiver;
    ProgressbarFragment progressbarFragment = new ProgressbarFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_management);
        Toolbar toolbar = findViewById(R.id.toolbar_user_management);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.activity_user_management);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        View headerLayout = navigationView.getHeaderView(0);
        CircleImageView logo = headerLayout.findViewById(R.id.nav_logo);
        logo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(UserQuery.this, ModifyUserInfo.class);
                intent.putExtra("info", userInfo.toString());
                startActivity(intent);
            }
        });

        intentFilter = new IntentFilter("usertrans");
        myBroadCastReceiver = new MyBroadCastReceiver();
        registerReceiver(myBroadCastReceiver, intentFilter);

        initializeWidgets();
        Intent intent = getIntent();
        try {
            userInfo = new JSONObject(intent.getStringExtra("info"));
            if (userInfo.getString("privilege").equals("1")){
                navigationView.getMenu().findItem(R.id.nav_user_management).setVisible(false);
                navigationView.getMenu().findItem(R.id.nav_settings).setVisible(false);
            }
            refreshNav();
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

    private void refreshNav() throws JSONException {
        View headerLayout = navigationView.getHeaderView(0);
        TextView name = headerLayout.findViewById(R.id.nav_user_name);
        TextView email = headerLayout.findViewById(R.id.nav_email);
        TextView phone = headerLayout.findViewById(R.id.nav_phone);
        TextView privilege = headerLayout.findViewById(R.id.nav_privilege);
        name.setText(userInfo.getString("name"));
        email.setText(userInfo.getString("email"));
        phone.setText(userInfo.getString("phone"));
        if (userInfo.getString("privilege").equals("1")){
            privilege.setText("用户爸爸");
        }else{
            privilege.setText("鹳狸猿");
        }
    }

    @Override
    public void onClick(View view){
        switch (view.getId()) {
            case R.id.contain_userid_query_button:{
                String useridQuery = editUserid.getText().toString();
                try {
                    if (!useridCheck(useridQuery)) break;
                    progressbarFragment.setCancelable(false);
                    progressbarFragment.show(getFragmentManager());
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
                        intent.putExtra("myInfo", userInfo.toString());
                        intent.putExtra("userInfo", jsonObject.toString());
                        intent.putExtra("userID", useridQuery);
                        progressbarFragment.dismiss();
                        startActivity(intent);
                    }
                    else{
                        progressbarFragment.dismiss();
                        showResponse("不知道为什么获取不到信息~QAQ~", "warning");
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

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.activity_user_management);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_homepage) {
            try{
                Intent intent = new Intent(UserQuery.this, MainActivity.class);
                intent.putExtra("info", userInfo.toString());
                startActivity(intent);
            } catch (Exception e){
                e.printStackTrace();
            }
        } else if (id == R.id.nav_train) {
            Intent intent = new Intent(UserQuery.this, TrainQuery.class);
            intent.putExtra("info", userInfo.toString());
            startActivity(intent);
        } else if (id == R.id.nav_user_management) {
            Toasty.info(UserQuery.this, "你已经在用户管理页面了哦~w(ﾟДﾟ)w", Toast.LENGTH_SHORT, true).show();
        } else if (id == R.id.nav_settings) {
            Intent intent = new Intent(UserQuery.this, TrainOperation.class);
            intent.putExtra("info", userInfo.toString());
            startActivity(intent);
        } else if (id == R.id.nav_info) {

        }

        DrawerLayout drawer = findViewById(R.id.activity_user_management);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private boolean empty(String s, String message){
        if (s.equals("")) {
            showWarning("未输入" + message + "呀~QAQ~", "info");
            return true;
        }else return false;
    }

    private boolean tooLong(String s, String message) throws UnsupportedEncodingException {
        if (s.getBytes("UTF-8").length > 20){
            showWarning(message + "太长了呀~QAQ", "info");
            return true;
        }else return false;
    }

    private boolean checkWhiteSpace(String s, String message){
        if (s.contains(" ")) {
            showWarning(message + "不能有空格呀~QAQ~", "info");
            return true;
        }else return false;
    }

    private boolean useridCheck(String s) throws UnsupportedEncodingException {
        if (empty(s, "id")) return false;
        if (tooLong(s, "id")) return false;
        if (checkWhiteSpace(s, "id")) return false;
        return true;
    }

    private void showWarning(final String message, final String type){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                switch (type){
                    case "error" : {
                        Toasty.error(UserQuery.this, message, Toast.LENGTH_SHORT).show();
                        break;
                    }
                    case "success" : {
                        Toasty.success(UserQuery.this, message, Toast.LENGTH_SHORT).show();
                        break;
                    }
                    case "info" : {
                        Toasty.info(UserQuery.this, message, Toast.LENGTH_SHORT).show();
                        break;
                    }
                    case "warning" : {
                        Toasty.warning(UserQuery.this, message, Toast.LENGTH_SHORT).show();
                        break;
                    }
                }
            }
        });
    }

    private void showResponse(final String message, final String type) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                switch (type){
                    case "error" : {
                        Toasty.error(UserQuery.this, message, Toast.LENGTH_SHORT).show();
                        break;
                    }
                    case "success" : {
                        Toasty.success(UserQuery.this, message, Toast.LENGTH_SHORT).show();
                        break;
                    }
                    case "info" : {
                        Toasty.info(UserQuery.this, message, Toast.LENGTH_SHORT).show();
                        break;
                    }
                    case "warning" : {
                        Toasty.warning(UserQuery.this, message, Toast.LENGTH_SHORT).show();
                        break;
                    }
                }            
            }
        });
    }
}
