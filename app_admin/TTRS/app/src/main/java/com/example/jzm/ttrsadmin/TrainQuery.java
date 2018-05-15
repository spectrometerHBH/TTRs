package com.example.jzm.ttrsadmin;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
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
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.haibin.calendarview.CalendarView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class TrainQuery extends AppCompatActivity
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
        setContentView(R.layout.activity_train_query);
        Toolbar toolbar = findViewById(R.id.toolbar_train_query);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.activity_train_query);
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

        Intent intent = getIntent();
        try {
            userInfo = new JSONObject(intent.getStringExtra("info"));
            refreshNav();
        }catch (JSONException e){
            e.printStackTrace();
        }

        titles.add("车次筛选");
        titles.add("车次详细");
        initializeViewPager();

        View headerLayout = navigationView.getHeaderView(0);
        CircleImageView logo = headerLayout.findViewById(R.id.nav_logo);
        logo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(TrainQuery.this, ModifyUserInfo.class);
                intent.putExtra("info", userInfo.toString());
                startActivity(intent);
            }
        });

        intentFilter = new IntentFilter("usertrans");
        myBroadCastReceiver = new MyBroadCastReceiver();
        registerReceiver(myBroadCastReceiver, intentFilter);
    }

    private void initializeViewPager(){
        tabLayout = findViewById(R.id.tablayout_train_query);
        viewPager = findViewById(R.id.view_pager_train_query);
        viewPager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                Fragment fragment = null;
                String userId;
                try {
                    userId = userInfo.getString("id");
                    if (position == 0){
                        fragment = new ContentFragment_train_query();
                        Bundle bundle = new Bundle();
                        bundle.putString("id", userId);
                        fragment.setArguments(bundle);
                    }else{
                        fragment = new ContentFragment_train_detail();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
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
            Intent intent = new Intent(TrainQuery.this, MainActivity.class);
            intent.putExtra("info", userInfo.toString());
            startActivity(intent);
        } else if (id == R.id.nav_train) {
            Toast.makeText(TrainQuery.this, "你已经在车次查询页面了哦~w(ﾟДﾟ)w", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.nav_user_management) {
            Intent intent = new Intent(TrainQuery.this, UserQuery.class);
            intent.putExtra("info", userInfo.toString());
            startActivity(intent);
        } else if (id == R.id.nav_settings) {
            Intent intent = new Intent(TrainQuery.this, TrainOperation.class);
            intent.putExtra("info", userInfo.toString());
            startActivity(intent);
        } else if (id == R.id.nav_info) {

        }

        DrawerLayout drawer = findViewById(R.id.activity_train_query);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
