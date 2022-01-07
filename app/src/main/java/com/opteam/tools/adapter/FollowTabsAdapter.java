package com.opteam.tools.adapter;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.opteam.tools.menu.FollowerYouNotFollow;
import com.opteam.tools.menu.FollowingNotFollowYou;

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
