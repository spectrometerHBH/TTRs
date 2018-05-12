package com.example.jzm.ttrs;

import android.content.Intent;
import android.graphics.Color;
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
import android.widget.TableLayout;
import android.widget.Toast;

import com.haibin.calendarview.CalendarView;

import java.util.ArrayList;
import java.util.List;

public class TrainQuery extends AppCompatActivity
    implements NavigationView.OnNavigationItemSelectedListener{

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

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        titles.add("车次筛选");
        titles.add("车次详细");
        initializeViewPager();
    }

    private void initializeViewPager(){
        tabLayout = findViewById(R.id.tablayout_train_query);
        viewPager = findViewById(R.id.view_pager_train_query);
        viewPager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                Fragment fragment;
                if (position == 0){
                    fragment = new ContentFragment_train_query();
                }else{
                    fragment = new ContentFragment_train_detail();
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
            startActivity(intent);
        } else if (id == R.id.nav_train) {
            Toast.makeText(TrainQuery.this, "你已经在车次查询页面了哦~w(ﾟДﾟ)w", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.nav_settings) {

        } else if (id == R.id.nav_info) {

        }

        DrawerLayout drawer = findViewById(R.id.activity_train_query);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


}
