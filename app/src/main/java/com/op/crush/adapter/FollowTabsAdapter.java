package com.op.crush.adapter;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.op.crush.menu.FollowerYouNotFollow;
import com.op.crush.menu.FollowingNotFollowYou;

import java.util.ArrayList;
import java.util.List;

public class FollowTabsAdapter extends FragmentStatePagerAdapter {
    int mNumOfTabs;
    public FollowTabsAdapter(FragmentManager fm, int NumOfTabs) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
    }
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                FollowingNotFollowYou notFollowYou = new FollowingNotFollowYou();
                return notFollowYou;
            case 1:
                FollowerYouNotFollow youNotFollow = new FollowerYouNotFollow();
                return youNotFollow;
            default:
                return null;
        }
    }
    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}
