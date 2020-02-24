package com.flyco.tablayout.utils;

import android.widget.ListView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by tlol20 on 2017/6/2
 */
public class FragmentAdapter extends FragmentPagerAdapter {
    private List<Fragment> fragments;
    private List<String> mTabTitles = new ArrayList<>();
    private List<Integer> icons= new ArrayList<>();

    public FragmentAdapter(FragmentManager fm, List<Fragment> fragments, List<String> mTabTitles, List<Integer> mIcon) {
        super(fm);
        this.fragments = fragments;
        this.mTabTitles = mTabTitles;
        this.icons = mIcon;
    }

    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    @Override
    public int getCount() {
        return fragments.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        if (mTabTitles != null) {
            return mTabTitles.get(position);
        }
        return super.getPageTitle(position);
    }

    public int getIcon(int position) {
        if (icons != null) {
            return icons.get(position);
        }
        return 0;

    }
}
