package com.example.ale.piadinapp.home;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.example.ale.piadinapp.home.TabCreaPiadina;
import com.example.ale.piadinapp.home.TabLeTuePiadine;
import com.example.ale.piadinapp.home.TabMenu;

public class PagerAdapter extends FragmentStatePagerAdapter {
    int mNumOfTabs;

    public PagerAdapter(FragmentManager fm, int NumOfTabs) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                TabMenu tab1 = new TabMenu();
                return tab1;
            case 1:
                TabCreaPiadina tab2 = new TabCreaPiadina();
                return tab2;
            case 2:
                TabLeTuePiadine tab3 = new TabLeTuePiadine();
                return tab3;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}