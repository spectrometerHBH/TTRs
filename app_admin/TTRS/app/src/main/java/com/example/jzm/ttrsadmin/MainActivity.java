package com.example.jzm.ttrsadmin;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.os.UserManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private JSONObject userInfo;
    private String userId;
    private String userCatalog;
    private NavigationView navigationView;
    private IntentFilter intentFilter;
    private TextView monthTextview;
    private TextView dayTextview;
    private TextView yearTextview;
    private Button calenderButton;
    private Button queryButton;
    private List<CheckBox> checkBoxes = new ArrayList<>();

    ProgressbarFragment progressbarFragment = new ProgressbarFragment();

    public class MyBroadCastReceiver extends BroadcastReceiver{
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar_main);
        setSupportActionBar(toolbar);
        DrawerLayout drawer =  findViewById(R.id.activity_main);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        initializeWidgets();
        Toast.makeText(MainActivity.this, "登录成功~♪（＾∀＾●）", Toast.LENGTH_SHORT).show();

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
        yearTextview.setText(String.valueOf(calendar.get(Calendar.YEAR)));
        monthTextview.setText(String.valueOf(calendar.get(Calendar.MONTH) + 1));
        dayTextview.setText(String.valueOf(calendar.get(Calendar.DATE)));
        calenderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String day = dayTextview.getText().toString();
                String month = monthTextview.getText().toString();
                String year = yearTextview.getText().toString();
                Intent intent = new Intent(MainActivity.this, Calender.class);
                intent.putExtra("day", day);
                intent.putExtra("month", month);
                intent.putExtra("year", year);
                startActivityForResult(intent, 1);
            }
        });

        Intent intent = getIntent();
        try {
            userInfo = new JSONObject(intent.getStringExtra("info"));
            userId = userInfo.getString("id");
            refreshNav();
        }catch (JSONException e){
            e.printStackTrace();
        }

        View headerLayout = navigationView.getHeaderView(0);
        CircleImageView logo = headerLayout.findViewById(R.id.nav_logo);
        logo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, ModifyUserInfo.class);
                intent.putExtra("info", userInfo.toString());
                startActivity(intent);
            }
        });

        intentFilter = new IntentFilter("usertrans");
        myBroadCastReceiver = new MyBroadCastReceiver();
        registerReceiver(myBroadCastReceiver, intentFilter);
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
                    Toast.makeText(MainActivity.this, "还没选要看的类型啊~QAQ~", Toast.LENGTH_SHORT).show();
                    return;
                }
                JSONObjectStringCreate jsonObjectStringCreate = new JSONObjectStringCreate();
                jsonObjectStringCreate.addStringPair("type", "query_order");
                jsonObjectStringCreate.addStringPair("id", userId);
                jsonObjectStringCreate.addStringPair("date", time);
                jsonObjectStringCreate.addStringPair("catalog", userCatalog);
                String command = jsonObjectStringCreate.getResult();
                try{

                    progressbarFragment.setCancelable(false);
                    progressbarFragment.show(getFragmentManager());
                }catch (Exception e){
                    e.printStackTrace();
                }
                sendRequest(command);
            }
        });
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
                        showResponse("你还一张票都没买呢( ⊙ o ⊙ )！");
                        return;
                    }
                    String num = jsonObject.getString("num");
                    if (!num.equals("0")) {
                        Intent intent = new Intent(MainActivity.this, OrderManifest.class);
                        intent.putExtra("data", jsonObject.toString());
                        intent.putExtra("id", userId);
                        intent.putExtra("catalog", userCatalog);
                        progressbarFragment.dismiss();
                        startActivity(intent);
                    }else{
                        progressbarFragment.dismiss();
                        showResponse("你还一张票都没买呢( ⊙ o ⊙ )！");
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }).start();
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

    private void initializeWidgets(){
        yearTextview = findViewById(R.id.contain_order_query_year);
        monthTextview = findViewById(R.id.contain_order_query_month);
        dayTextview = findViewById(R.id.contain_order_query_day);
        calenderButton = findViewById(R.id.calendar_enter_order_query);
        queryButton = findViewById(R.id.contain_order_query_button);        
        checkBoxes.add((CheckBox) findViewById(R.id.contain_order_query_checkBox_all));
        checkBoxes.add((CheckBox) findViewById(R.id.contain_order_query_checkBox_T));
        checkBoxes.add((CheckBox) findViewById(R.id.contain_order_query_checkBox_Z));
        checkBoxes.add((CheckBox) findViewById(R.id.contain_order_query_checkBox_C));
        checkBoxes.add((CheckBox) findViewById(R.id.contain_order_query_checkBox_O));
        checkBoxes.add((CheckBox) findViewById(R.id.contain_order_query_checkBox_G));
        checkBoxes.add((CheckBox) findViewById(R.id.contain_order_query_checkBox_D));
        checkBoxes.add((CheckBox) findViewById(R.id.contain_order_query_checkBox_K));
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
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.activity_main);
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
            Toast.makeText(MainActivity.this, "你已经在首页了哦~w(ﾟДﾟ)w", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.nav_train) {
            Intent intent = new Intent(MainActivity.this, TrainQuery.class);
            intent.putExtra("info", userInfo.toString());
            startActivity(intent);
        } else if (id == R.id.nav_user_management){
            Intent intent = new Intent(MainActivity.this, UserQuery.class);
            intent.putExtra("info", userInfo.toString());
            startActivity(intent);
        } else if (id == R.id.nav_settings) {
            Intent intent = new Intent(MainActivity.this, TrainOperation.class);
            intent.putExtra("info", userInfo.toString());
            startActivity(intent);
        } else if (id == R.id.nav_info) {

        }

        DrawerLayout drawer = findViewById(R.id.activity_main);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    private void showResponse(final String message){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
            }
        });
    }
}

