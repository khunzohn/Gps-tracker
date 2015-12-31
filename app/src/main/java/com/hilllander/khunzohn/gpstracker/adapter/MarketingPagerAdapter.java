package com.hilllander.khunzohn.gpstracker.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.hilllander.khunzohn.gpstracker.MarketActivity;
import com.hilllander.khunzohn.gpstracker.fragment.MarketingFragments;

public class MarketingPagerAdapter extends FragmentStatePagerAdapter {


    public MarketingPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        return MarketingFragments.newInstance(position);
    }

    @Override
    public int getCount() {
        return MarketActivity.NUM_PAGES;
    }
}
