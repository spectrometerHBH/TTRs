package com.example.jzm.ttrs;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

public class Register extends AppCompatActivity implements View.OnClickListener{

    private EditText editTextusername;
    private EditText editTextpassword;
    private EditText editTextconfirmpassword;
    private EditText editTextemail;
    private EditText editTextphone;
    private Button buttonRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        initializeWidgets();
        buttonRegister.setOnClickListener(this);
    }

    private void initializeWidgets(){
        editTextusername = findViewById(R.id.registerUsername_Edit);
        editTextpassword = findViewById(R.id.registerPassword_Edit);
        editTextconfirmpassword = findViewById(R.id.registerConfirmPassword_Edit);
        editTextemail = findViewById(R.id.registerEmail_Edit);
        editTextphone = findViewById(R.id.registerPhone_Edit);
        buttonRegister = findViewById(R.id.register_Button);
        ImageView usernameClear = findViewById(R.id.registerUsername_Clear);
        ImageView passwordClear = findViewById(R.id.registerPassword_Clear);
        ImageView confirmpasswordClear = findViewById(R.id.registerConfirmPassword_Clear);
        ImageView emailClear = findViewById(R.id.registerEmail_Clear);
        ImageView phoneClear = findViewById(R.id.registerPhone_Clear);
        EditTextClearTools.addClearListener(editTextusername, usernameClear);
        EditTextClearTools.addClearListener(editTextpassword, passwordClear);
        EditTextClearTools.addClearListener(editTextconfirmpassword, confirmpasswordClear);
        EditTextClearTools.addClearListener(editTextemail, emailClear);
        EditTextClearTools.addClearListener(editTextphone, phoneClear);
    }

    @Override
    public void onClick(View view){
        switch (view.getId()) {
            case R.id.register_Button:{
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
                } catch (Exception e){
                    e.printStackTrace();
                }
                sendRequest();
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
                    String password = editTextpassword.getText().toString();
                    String email = editTextemail.getText().toString();
                    String phone = editTextphone.getText().toString();
                    HttpClient client = new HttpClient();
                    client.setCommand("{\"type\":\"register\",\"name\":\"" + username + "\",\"password\":\"" + password + "\",\"email\":\"" + email + "\",\"phone\":\"" + phone + "\"}");
                    JSONObject jsonObject = new JSONObject(client.run());
                    String success = jsonObject.getString("success");
                    if (success.equals("true")){
                        String userid = jsonObject.getString("id");
                        Intent intent = new Intent(Register.this, Login.class);
                        intent.putExtra("id", userid);
                        setResult(RESULT_OK, intent);
                        finish();
                    }else{
                        showWarning("不知道为什么注册失败了~QAQ~");
                    }
                } catch (Exception e){
                    e.printStackTrace();
                }
            }
        }).start();
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
        if (empty(s, "密码")) return false;
        if (tooLong(s, "密码")) return false;
        if (checkWhiteSpace(s, "密码")) return false;
        return true;
    }

    private boolean confirmpasswordCheck(String s) throws UnsupportedEncodingException {
        if (empty(s, "重复密码")) return false;
        if (tooLong(s, "重复密码")) return false;
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
                Toast.makeText(Register.this, message, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
