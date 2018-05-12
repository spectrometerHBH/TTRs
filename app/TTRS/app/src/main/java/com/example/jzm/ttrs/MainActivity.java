package com.example.jzm.ttrs;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private List<Train> trainList = new ArrayList<>();
    private JSONObject userInfo;
    private NavigationView navigationView;
    private IntentFilter intentFilter;
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

        Toast.makeText(MainActivity.this, "登录成功~♪（＾∀＾●）", Toast.LENGTH_SHORT).show();

        Intent intent = getIntent();
        try {
            userInfo = new JSONObject(intent.getStringExtra("info"));
            refreshNav();
        }catch (JSONException e){
            e.printStackTrace();
        }

        initializeTrains();
        RecyclerView recyclerView = findViewById(R.id.front_page_recyclerview);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        TrainAdapter adapter = new TrainAdapter(trainList);
        recyclerView.setAdapter(adapter);

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

    private void initializeTrains(){
        for (int i = 0; i < 50; i++) {
            Train testTrain = new Train("c101", "fuck", "C", "北京", "夏威夷", "08:00", "08:01",
                    "2018-03-28", "2018-03-28");
            trainList.add(testTrain);
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
        } else if (id == R.id.nav_settings) {

        } else if (id == R.id.nav_info) {

        }

        DrawerLayout drawer = findViewById(R.id.activity_main);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}

