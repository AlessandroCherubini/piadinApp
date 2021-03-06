package com.example.android.home;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.example.android.fragments.TabCreaPiadina;
import com.example.android.fragments.TabLeMiePiadine;
import com.example.android.fragments.TabMenu;

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
                TabLeMiePiadine tab3 = new TabLeMiePiadine();
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