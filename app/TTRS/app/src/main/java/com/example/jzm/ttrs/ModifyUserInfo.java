package com.example.jzm.ttrs;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

public class ModifyUserInfo extends AppCompatActivity
    implements View.OnClickListener{

    private EditText editTextusername;
    private EditText editOldpassword;
    private EditText editTextpassword;
    private EditText editTextconfirmpassword;
    private EditText editTextemail;
    private EditText editTextphone;
    private Button buttonModify;
    private String userid;
    private String password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_user_info);
        Toolbar toolbar = findViewById(R.id.toolbar_modify_user_info);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.putExtra("userid", userid);
                intent.putExtra("password", password);
                setResult(RESULT_OK, intent);
                finish();
            }
        });

        initializeWidgets();
        Intent intent = getIntent();
        userid = intent.getStringExtra("userid");
        password = intent.getStringExtra("password");
        getProfile();
        buttonModify.setOnClickListener(this);
    }

    private void getProfile(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    HttpClient client = new HttpClient();
                    JSONObjectStringCreate jsonobjcetcreate = new JSONObjectStringCreate();
                    jsonobjcetcreate.addStringPair("type", "query_profile");
                    jsonobjcetcreate.addStringPair("id", userid);
                    client.setCommand(jsonobjcetcreate.getResult());
                    JSONObject jsonObject = new JSONObject(client.run());
                    String success = jsonObject.getString("success");
                    if (success.equals("true")) refreshProfile(jsonObject);
                    else showResponse("不知道为什么获取不到信息~QAQ~");
                } catch (Exception e){
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void refreshProfile(final JSONObject jsonObject){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    editTextusername.setText(jsonObject.getString("name"));
                    editOldpassword.setText(password);
                    editTextemail.setText(jsonObject.getString("email"));
                    editTextphone.setText(jsonObject.getString("phone"));
                    editTextpassword.setText("");
                    editTextconfirmpassword.setText("");
                } catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
    }

    private void initializeWidgets(){
        editTextusername = findViewById(R.id.modify_user_info_Username_Edit);
        editOldpassword = findViewById(R.id.modify_user_info_OldPassword_Edit);
        editTextpassword = findViewById(R.id.modify_user_info_Password_Edit);
        editTextconfirmpassword = findViewById(R.id.modify_user_info_ConfirmPassword_Edit);
        editTextemail = findViewById(R.id.modify_user_info_Email_Edit);
        editTextphone = findViewById(R.id.modify_user_info_Phone_Edit);
        buttonModify = findViewById(R.id.modify_user_info_Button);
        ImageView usernameClear = findViewById(R.id.modify_user_info_Username_Clear);
        ImageView oldpasswordClear = findViewById(R.id.modify_user_info_OldPassword_Clear);
        ImageView passwordClear = findViewById(R.id.modify_user_info_Password_Clear);
        ImageView confirmpasswordClear = findViewById(R.id.modify_user_info_ConfirmPassword_Clear);
        ImageView emailClear = findViewById(R.id.modify_user_info_Email_Clear);
        ImageView phoneClear = findViewById(R.id.modify_user_info_Phone_Clear);
        EditTextClearTools.addClearListener(editTextusername, usernameClear);
        EditTextClearTools.addClearListener(editOldpassword, oldpasswordClear);
        EditTextClearTools.addClearListener(editTextpassword, passwordClear);
        EditTextClearTools.addClearListener(editTextconfirmpassword, confirmpasswordClear);
        EditTextClearTools.addClearListener(editTextemail, emailClear);
        EditTextClearTools.addClearListener(editTextphone, phoneClear);
    }

    @Override
    public void onClick(View view){
        switch (view.getId()) {
            case R.id.modify_user_info_Button:{
                String username = editTextusername.getText().toString();
                String oldpassword = editOldpassword.getText().toString();
                String password = editTextpassword.getText().toString();
                String confirmpassword = editTextconfirmpassword.getText().toString();
                String email = editTextemail.getText().toString();
                String phone = editTextphone.getText().toString();
                try {
                    if (!usernameCheck(username)) break;
                    if (!oldpasswordCheck(username)) break;
                    if (!passwordCheck(password)) break;
                    if (!confirmpasswordCheck(confirmpassword)) break;
                    if (!emailCheck(email)) break;
                    if (!phoneCheck(phone)) break;
                    if (!password.equals(confirmpassword)) {
                        showWarning("两次密码不一样呀~QAQ~");
                        break;
                    }
                    verifyPassword(oldpassword);
                } catch (Exception e){
                    e.printStackTrace();
                }
                break;
            }
            default: break;
        }
    }

    private void verifyPassword(final String password){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    HttpClient client = new HttpClient();
                    JSONObjectStringCreate jsonobjcetcreate = new JSONObjectStringCreate();
                    jsonobjcetcreate.addStringPair("type", "login");
                    jsonobjcetcreate.addStringPair("id", userid);
                    jsonobjcetcreate.addStringPair("password", password);
                    client.setCommand(jsonobjcetcreate.getResult());
                    JSONObject jsonObject = new JSONObject(client.run());
                    String success = jsonObject.getString("success");
                    if (success.equals("true")){
                        sendRequest();
                    }else{
                        showResponse("用户名密码不符~QAQ~");
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void sendRequest(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String username = editTextusername.getText().toString();
                    String newpassword = editTextpassword.getText().toString();
                    String email = editTextemail.getText().toString();
                    String phone = editTextphone.getText().toString();
                    HttpClient client = new HttpClient();
                    JSONObjectStringCreate jsonobjcetcreate = new JSONObjectStringCreate();
                    jsonobjcetcreate.addStringPair("type", "modify_profile");
                    jsonobjcetcreate.addStringPair("id", userid);
                    jsonobjcetcreate.addStringPair("name", username);
                    if (newpassword.equals("")) {
                        jsonobjcetcreate.addStringPair("password", password);
                    }else {
                        jsonobjcetcreate.addStringPair("password", newpassword);
                    }
                    jsonobjcetcreate.addStringPair("email", email);
                    jsonobjcetcreate.addStringPair("phone", phone);
                    client.setCommand(jsonobjcetcreate.getResult());
                    JSONObject jsonObject = new JSONObject(client.run());
                    String success = jsonObject.getString("success");
                    if (success.equals("true")){
                        if (!newpassword.equals("")) password = newpassword;
                        showResponse("修改成功了呢O(∩_∩)O");
                        getProfile();
                    }else{
                        showWarning("不知道为什么修改失败了~QAQ~");
                    }
                } catch (Exception e){
                    e.printStackTrace();
                }
            }
        }).start();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.putExtra("userid", userid);
        intent.putExtra("password", password);
        setResult(RESULT_OK, intent);
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

    private boolean usernameCheck(String s) throws UnsupportedEncodingException {
        if (empty(s, "用户名")) return false;
        if (tooLong(s, "用户名")) return false;
        if (checkWhiteSpace(s, "用户名")) return false;
        return true;
    }

    private boolean oldpasswordCheck(String s) throws UnsupportedEncodingException{
        if (empty(s, "旧密码")) return false;
        if (tooLong(s, "旧密码")) return false;
        if (checkWhiteSpace(s, "旧密码")) return false;
        return true;
    }

    private boolean passwordCheck(String s) throws UnsupportedEncodingException {
        if (tooLong(s, "新密码")) return false;
        if (checkWhiteSpace(s, "新密码")) return false;
        return true;
    }

    private boolean confirmpasswordCheck(String s) throws UnsupportedEncodingException {
        if (tooLong(s, "重复新密码")) return false;
        return true;
    }

    private boolean emailCheck(String s) throws UnsupportedEncodingException {
        if (empty(s, "邮箱")) return false;
        if (tooLong(s, "邮箱")) return false;
        if (checkWhiteSpace(s, "密码")) return false;
        return true;
    }

    private boolean phoneCheck(String s) throws UnsupportedEncodingException {
        if (empty(s, "电话")) return false;
        if (tooLong(s, "电话")) return false;
        if (checkWhiteSpace(s, "电话")) return false;
        return true;
    }

    private void showWarning(final String message){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(ModifyUserInfo.this, message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showResponse(final String message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(ModifyUserInfo.this, message, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
