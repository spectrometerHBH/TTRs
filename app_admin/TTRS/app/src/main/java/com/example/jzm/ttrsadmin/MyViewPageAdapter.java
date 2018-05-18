package com.example.jzm.ttrsadmin;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

public class MyViewPageAdapter extends FragmentPagerAdapter{
    private final List<Fragment> fragments = new ArrayList<>();
    private final List<String> fragmentsTitles = new ArrayList<>();
    public MyViewPageAdapter(FragmentManager fragmentManager){
        super(fragmentManager);
    }
    public void addFragment(Fragment fragment, String fragmentTitle){
        fragments.add(fragment);
        fragmentsTitles.add(fragmentTitle);
    }
    @Override
    public Fragment getItem(int position){
        return fragments.get(position);
    }
    @Override
    public int getCount(){
        return fragments.size();
    }
    @Override
    public CharSequence getPageTitle(int position){
        return fragmentsTitles.get(position);
    }
}
