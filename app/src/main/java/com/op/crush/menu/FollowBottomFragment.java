package com.op.crush.menu;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.op.crush.R;
import com.op.crush.adapter.FollowTabsAdapter;

public class FollowBottomFragment extends Fragment {
    private FollowTabsAdapter adapter;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private FragmentActivity myContext;




    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.follow_fragment, container, false);



        tabLayout = view.findViewById(R.id.follow_tab_layout);
        tabLayout.addTab(tabLayout.newTab().setText("Follower"));
        tabLayout.addTab(tabLayout.newTab().setText("Following"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        final ViewPager viewPager = view.findViewById(R.id.follow_view_pager);
        final FollowTabsAdapter adapter = new FollowTabsAdapter
                ( getFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

               // FragmentManager fragManager = myContext.getSupportFragmentManager();
                }
            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });











        return view;
    }

}

