package iot.mdp.weather_app;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

public class TabViewPagerAdapter extends FragmentPagerAdapter {
    private final List<Fragment> mFgragmentList = new ArrayList<>();
    private final List<String> mFgragmentTitleList = new ArrayList<>();

    public TabViewPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        return mFgragmentList.get(position);
    }

    @Override
    public int getCount() {
        return mFgragmentList.size();
    }

    public void addFragment(Fragment fragment, String title){
        mFgragmentList.add(fragment);
        mFgragmentTitleList.add(title);
    }
    @Override
    public CharSequence getPageTitle(int position){
        return mFgragmentTitleList.get(position);
    }
}
