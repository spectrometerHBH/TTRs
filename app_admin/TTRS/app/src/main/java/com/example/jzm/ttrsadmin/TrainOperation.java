package com.example.jzm.ttrsadmin;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class TrainOperation extends AppCompatActivity
    implements NavigationView.OnNavigationItemSelectedListener{

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

    List<String> titles = new ArrayList<>();
    TabLayout tabLayout;
    ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_train_operation);
        Toolbar toolbar = findViewById(R.id.toolbar_train_operation);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.activity_train_operation);
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

        View headerLayout = navigationView.getHeaderView(0);
        CircleImageView logo = headerLayout.findViewById(R.id.nav_logo);
        logo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(TrainOperation.this, ModifyUserInfo.class);
                intent.putExtra("info", userInfo.toString());
                startActivity(intent);
            }
        });

        intentFilter = new IntentFilter("usertrans");
        myBroadCastReceiver = new TrainOperation.MyBroadCastReceiver();
        registerReceiver(myBroadCastReceiver, intentFilter);

        Intent intent = getIntent();
        try {
            userInfo = new JSONObject(intent.getStringExtra("info"));
            refreshNav();
        }catch (JSONException e){
            e.printStackTrace();
        }

        titles.add("车次添加");
        titles.add("车次修改/公开/删除");
        initializeViewPager();
    }

    private void initializeViewPager(){
        tabLayout = findViewById(R.id.tablayout_train_operation);
        viewPager = findViewById(R.id.view_pager_train_operation);
        viewPager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                Fragment fragment = null;
                String userId;
                if (position == 0){
                    fragment = new ContentFragment_train_add();
                }else{
                    fragment = new ContentFragment_train_other_operation();
                }
                return fragment;
            }
            @Override
            public int getCount() {
                return titles.size();
            }
            @Override
            public CharSequence getPageTitle(int position){
                return titles.get(position);
            }
        });
        tabLayout.setupWithViewPager(viewPager);
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
        DrawerLayout drawer = findViewById(R.id.activity_train_query);
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
            Intent intent = new Intent(TrainOperation.this, MainActivity.class);
            intent.putExtra("info", userInfo.toString());
            startActivity(intent);
        } else if (id == R.id.nav_train) {
            Intent intent = new Intent(TrainOperation.this, TrainQuery.class);
            intent.putExtra("info", userInfo.toString());
            startActivity(intent);
        } else if (id == R.id.nav_user_management) {
            Intent intent = new Intent(TrainOperation.this, UserQuery.class);
            intent.putExtra("info", userInfo.toString());
            startActivity(intent);
        } else if (id == R.id.nav_settings) {
                Toast.makeText(TrainOperation.this, "你已经在车次管理页面了哦~w(ﾟДﾟ)w", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.nav_info) {

        }

        DrawerLayout drawer = findViewById(R.id.activity_train_operation);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
