package com.annsp.myapplication;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;


public class PagerAdapter extends  FragmentPagerAdapter {
    List<Fragment> fragments = new ArrayList<>();
//    int fragments;

    public PagerAdapter(FragmentManager fm, List<Fragment> fragments) {
        super(fm);
        this.fragments = fragments;
    }

    @Override
    public Fragment getItem(int position) {
//        switch(position)
//        {
//            case 0:
//                event tab1=new event();
//                return tab1;
//            case 1:
//                artist tab2=new artist();
//                return tab2;
//            case 2:
//                venue tab3=new venue();
//                return tab3;
//            case 3:
//                upcoming tab4=new upcoming();
//                return tab4;
//            default:
//                return null;
//        }

        return fragments.get(position);

    }


    @Override
    public int getCount() {
        int i = fragments != null ? fragments.size() : 0;
        String s = Integer.toString(i);
        Log.i("fragmentsize",s);
        return fragments != null ? fragments.size() : 0;
//        return fragments;
    }

}
