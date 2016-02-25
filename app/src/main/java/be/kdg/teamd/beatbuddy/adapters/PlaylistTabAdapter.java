package be.kdg.teamd.beatbuddy.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ignace on 23/02/2016.
 */

public class PlaylistTabAdapter extends FragmentStatePagerAdapter
{
    private final List<Fragment> mFragmentList = new ArrayList<>();
    private final List<String> mFragmentTitleList = new ArrayList<>();

    public PlaylistTabAdapter(FragmentManager manager) {
        super(manager);
    }
    @Override
    public android.support.v4.app.Fragment getItem(int position) {
        return mFragmentList.get(position);
    }
    @Override
    public int getCount() {
        return mFragmentList.size();
    }
    public void addFrag(android.support.v4.app.Fragment fragment, String title) {
        mFragmentList.add(fragment);
        mFragmentTitleList.add(title);
    }
    @Override
    public CharSequence getPageTitle(int position) {
        return mFragmentTitleList.get(position);
    }
}