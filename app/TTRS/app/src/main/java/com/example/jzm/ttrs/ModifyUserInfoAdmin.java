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
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

public class ModifyUserInfoAdmin extends AppCompatActivity
        implements View.OnClickListener {

    private EditText editTextusername;
    private EditText editTextpassword;
    private EditText editTextconfirmpassword;
    private EditText editTextemail;
    private EditText editTextphone;
    private RadioButton radioButtonUser;
    private RadioButton radioButtonAdmin;
    private Button buttonModify;
    private String userid;
    private String useridNow;
    private String privilegeNow;
    private String usernameNow;
    private String emailNow;
    private String phoneNow;
    private JSONObject myInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_user_info_admin);

        Toolbar toolbar = findViewById(R.id.toolbar_modify_user_info_admin);
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
            JSONObject jsonObject = new JSONObject(intent.getStringExtra("userInfo"));
            useridNow = intent.getStringExtra("userID");
            usernameNow = jsonObject.getString("name");
            emailNow = jsonObject.getString("email");
            phoneNow = jsonObject.getString("phone");
            privilegeNow = jsonObject.getString("privilege");
            myInfo = new JSONObject(intent.getStringExtra("myInfo"));
            userid = myInfo.getString("id");
            refreshProfile();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        buttonModify.setOnClickListener(this);
    }

    private void refreshProfile(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    editTextusername.setText(usernameNow);
                    editTextemail.setText(emailNow);
                    editTextphone.setText(phoneNow);
                    editTextpassword.setText("");
                    editTextconfirmpassword.setText("");
                    radioButtonUser.setChecked(privilegeNow == "1");
                    radioButtonAdmin.setChecked(privilegeNow == "2");
                    if (privilegeNow == "2") {

                        editTextusername.setFocusableInTouchMode(false);
                        editTextusername.setFocusable(false);

                        editTextemail.setFocusableInTouchMode(false);
                        editTextemail.setFocusable(false);

                        editTextphone.setFocusableInTouchMode(false);
                        editTextphone.setFocusable(false);

                        editTextpassword.setFocusableInTouchMode(false);
                        editTextpassword.setFocusable(false);

                        editTextconfirmpassword.setFocusableInTouchMode(false);
                        editTextconfirmpassword.setFocusable(false);

                        radioButtonUser.setEnabled(false);
                        radioButtonAdmin.setEnabled(true);


                        ImageView usernameClear = findViewById(R.id.modify_user_info_admin_Username_Clear);
                        ImageView emailClear = findViewById(R.id.modify_user_info_admin_Email_Clear);
                        ImageView phoneClear = findViewById(R.id.modify_user_info_admin_Phone_Clear);
                        usernameClear.setClickable(false);
                        usernameClear.setVisibility(View.INVISIBLE);
                        emailClear.setClickable(false);
                        emailClear.setVisibility(View.INVISIBLE);
                        phoneClear.setClickable(false);
                        phoneClear.setVisibility(View.INVISIBLE);
                        buttonModify.setClickable(false);
                        buttonModify.setVisibility(View.INVISIBLE);

                    }
                } catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
    }

    private void initializeWidgets(){
        editTextusername = findViewById(R.id.modify_user_info_admin_Username_Edit);
        editTextpassword = findViewById(R.id.modify_user_info_admin_Password_Edit);
        editTextconfirmpassword = findViewById(R.id.modify_user_info_admin_ConfirmPassword_Edit);
        editTextemail = findViewById(R.id.modify_user_info_admin_Email_Edit);
        editTextphone = findViewById(R.id.modify_user_info_admin_Phone_Edit);
        buttonModify = findViewById(R.id.modify_user_info_admin_Button);
        radioButtonUser = findViewById(R.id.radiobutton_user);
        radioButtonAdmin = findViewById(R.id.radiobutton_admin);
        ImageView usernameClear = findViewById(R.id.modify_user_info_admin_Username_Clear);
        ImageView passwordClear = findViewById(R.id.modify_user_info_admin_Password_Clear);
        ImageView confirmpasswordClear = findViewById(R.id.modify_user_info_admin_ConfirmPassword_Clear);
        ImageView emailClear = findViewById(R.id.modify_user_info_admin_Email_Clear);
        ImageView phoneClear = findViewById(R.id.modify_user_info_admin_Phone_Clear);
        EditTextClearTools.addClearListener(editTextusername, usernameClear);
        EditTextClearTools.addClearListener(editTextpassword, passwordClear);
        EditTextClearTools.addClearListener(editTextconfirmpassword, confirmpasswordClear);
        EditTextClearTools.addClearListener(editTextemail, emailClear);
        EditTextClearTools.addClearListener(editTextphone, phoneClear);
    }

    @Override
    public void onClick(View view){
        switch (view.getId()) {
            case R.id.modify_user_info_admin_Button:{
                String username = editTextusername.getText().toString();
                String password = editTextpassword.getText().toString();
                String confirmpassword = editTextconfirmpassword.getText().toString();
                String email = editTextemail.getText().toString();
                String phone = editTextphone.getText().toString();
                try {
                    if (!usernameCheck(username)) break;
                    if (!passwordCheck(password)) break;
                    if (!confirmpasswordCheck(confirmpassword)) break;
                    if (!emailCheck(email)) break;
                    if (!phoneCheck(phone)) break;
                    if (!password.equals(confirmpassword)) {
                        showWarning("两次密码不一样呀~QAQ~");
                        break;
                    }
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
                    String username = editTextusername.getText().toString();
                    String newpassword = editTextpassword.getText().toString();
                    String email = editTextemail.getText().toString();
                    String phone = editTextphone.getText().toString();
                    HttpClient client = new HttpClient();
                    JSONObjectStringCreate jsonobjcetcreate = new JSONObjectStringCreate();
                    if (newpassword.equals("")) {
                        jsonobjcetcreate.addStringPair("type", "modify_profile2");
                    } else {
                        jsonobjcetcreate.addStringPair("type", "modify_profile");
                    }
                    jsonobjcetcreate.addStringPair("id", useridNow);
                    jsonobjcetcreate.addStringPair("name", username);
                    if (!newpassword.equals("")) {
                        jsonobjcetcreate.addStringPair("password", newpassword);
                    }
                    jsonobjcetcreate.addStringPair("email", email);
                    jsonobjcetcreate.addStringPair("phone", phone);
                    client.setCommand(jsonobjcetcreate.getResult());
                    JSONObject jsonObject = new JSONObject(client.run());
                    String success = jsonObject.getString("success");
                    if (success.equals("true")){
                        String privilege = radioButtonUser.isChecked() ? "1" : "2";
                        HttpClient client2 = new HttpClient();
                        JSONObjectStringCreate jsonobjcetcreate2 = new JSONObjectStringCreate();
                        jsonobjcetcreate2.addStringPair("type", "modify_privilege");
                        jsonobjcetcreate2.addStringPair("id1", userid);
                        jsonobjcetcreate2.addStringPair("id2", useridNow);
                        jsonobjcetcreate2.addIntPair("privilege", privilege);
                        client.setCommand(jsonobjcetcreate.getResult());
                        JSONObject jsonObject2 = new JSONObject(client.run());
                        String success2 = jsonObject2.getString("success");
                        if (success2.equals("true")){
                            usernameNow = username;
                            emailNow = email;
                            phoneNow = phone;
                            privilegeNow = privilege;
                            refreshProfile();
                            showResponse("修改成功了呢O(∩_∩)O");
                        }
                    }else{
                        showWarning("不知道为什么修改失败了~QAQ~");
                    }
                } catch (Exception e) {
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

    private boolean usernameCheck(String s) throws UnsupportedEncodingException {
        if (empty(s, "用户名")) return false;
        if (tooLong(s, "用户名")) return false;
        if (checkWhiteSpace(s, "用户名")) return false;
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
                Toast.makeText(ModifyUserInfoAdmin.this, message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showResponse(final String message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(ModifyUserInfoAdmin.this, message, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
