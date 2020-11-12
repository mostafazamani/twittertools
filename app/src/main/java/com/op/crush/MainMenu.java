package com.op.crush;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.op.crush.Room.ProgressState;
import com.op.crush.Room.ProgressViewModel;
import com.op.crush.background.LoadFollower;
import com.op.crush.background.LoadFollowing;
import com.op.crush.menu.ExploreBottomFragment;
import com.op.crush.menu.FollowBottomFragment;
import com.op.crush.menu.HomeBottomFragment;
import com.op.crush.menu.TwittsBottomFragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.op.crush.models.UserShow;
import com.squareup.picasso.Picasso;
import com.twitter.sdk.android.core.Twitter;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterSession;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SwitchCompat;
import androidx.constraintlayout.motion.widget.MotionLayout;
import androidx.core.view.GravityCompat;
import androidx.core.view.MenuItemCompat;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Observer;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.work.BackoffPolicy;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import androidx.work.WorkRequest;

import java.util.List;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class MainMenu extends AppCompatActivity {


    private TwitterSession session;

    ImageButton hamberger;
    LinearLayout bar;

    SwitchCompat nightswitch;
    boolean night;


    SharedPreferences preferences;
    BottomNavigationView bottomNavigationView;
    ImageView profile, banner;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private NavigationView navigationView;

    Fragment fragment1 = new HomeBottomFragment();
    Fragment fragment2 = new ExploreBottomFragment();
    Fragment fragment3 = new TwittsBottomFragment();
    Fragment fragment4 = new FollowBottomFragment();
    FragmentManager fm = getSupportFragmentManager();
    Fragment active = fragment1;

    boolean f2 = true;
    boolean f3 = true;
    boolean f4 = true;
    TextView text;
    TextView t;

    private ProgressViewModel progressViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Twitter.initialize(this);
        setContentView(R.layout.content_main);
        progressViewModel = new ViewModelProvider(this).get(ProgressViewModel.class);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        final MotionLayout motionLayout = findViewById(R.id.view);


        drawer.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {
                motionLayout.setProgress(slideOffset / 2);
            }

            @Override
            public void onDrawerOpened(@NonNull View drawerView) {

            }

            @Override
            public void onDrawerClosed(@NonNull View drawerView) {

            }

            @Override
            public void onDrawerStateChanged(int newState) {

            }
        });


        hamberger = findViewById(R.id.hamberger_btn);
        bar = findViewById(R.id.toolbar_lin);
        bottomNavigationView = findViewById(R.id.bottom_nav);
        text = findViewById(R.id.txt_status);
        t = findViewById(R.id.txt_sta);
        // toolbar = findViewById(R.id.m_toolbar);
        //  setSupportActionBar(toolbar); //toolbar

        preferences = getSharedPreferences("Courser", Context.MODE_PRIVATE);

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.navigation_view);

        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        // getSupportActionBar().setDisplayHomeAsUpEnabled(true); //for toolbar


        banner = navigationView.getHeaderView(0).findViewById(R.id.profile_banner);
        profile = navigationView.getHeaderView(0).findViewById(R.id.nav_profile);


        hamberger.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });


        WorkRequest workRequest = new OneTimeWorkRequest.Builder(LoadFollowing.class)
                .build();

        WorkRequest workRequest1 = new OneTimeWorkRequest.Builder(LoadFollower.class).addTag("ab").setBackoffCriteria(
                BackoffPolicy.LINEAR,
                OneTimeWorkRequest.MIN_BACKOFF_MILLIS,
                TimeUnit.MILLISECONDS)
                .build();


        WorkManager.getInstance(MainMenu.this).enqueue(workRequest);

        WorkManager.getInstance(MainMenu.this).enqueue(workRequest1);


        progressViewModel.getState().observe(this, new Observer<List<ProgressState>>() {
            @Override
            public void onChanged(List<ProgressState> progressStates) {
                if (progressStates != null && progressStates.size() > 0) {
                    text.setText(String.valueOf(progressStates.get(progressStates.size() - 1).getState()));
                    Log.i("vm", String.valueOf(progressStates.get(progressStates.size() - 1).getState()));
                }
            }
        });


        session = TwitterCore.getInstance().getSessionManager().getActiveSession();


        user_info(session, MainMenu.this);





        ////////////////////Night Mode
        Menu menu = navigationView.getMenu();
        MenuItem menuItem = menu.findItem(R.id.nav_switch);
        View actionView = MenuItemCompat.getActionView(menuItem);

        nightswitch = (SwitchCompat) actionView.findViewById(R.id.nightswitch);
        if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES) {
            setTheme(R.style.DarkTheme);

            nightswitch.setChecked(true);
            night = true;
        } else {
            setTheme(R.style.AppTheme);
            night = false;
            nightswitch.setChecked(false);
        }
        nightswitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                {
                    if (night == true) {
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                        restartApp();
                    } else if (night == false) {
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                        restartApp();
                    }
                }
            }
        });
        ////////////////////Night Mode End//////////////////////
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem drawItem) {
                int id = drawItem.getItemId();
                switch (id) {
                    case R.id.about_us:
                        Toast.makeText(MainMenu.this, "My Account", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.settings:
                        Toast.makeText(MainMenu.this, "Settings", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.mycart: {
                        Toast.makeText(MainMenu.this, "My Cart", Toast.LENGTH_SHORT).show();
                        break;
                    }
                    case R.id.nav_switch: {
                        if (night == true) {
                            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                            nightswitch.setChecked(true);
                            restartApp();
                        } else if (!night) {
                            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                            nightswitch.setChecked(false);
                            restartApp();
                        }
                    }

                    default:
                        return true;
                }
                return true;
            }
        });


        fm.beginTransaction().add(R.id.fragment_container, fragment1, "4").commit();

        ViewCompat.setLayoutDirection(bottomNavigationView, ViewCompat.LAYOUT_DIRECTION_RTL);
        bottomNavigationView.setOnNavigationItemSelectedListener(bottomListener);
        bottomNavigationView.setSelectedItemId(R.id.item_1);
        bottomNavigationView.setItemIconTintList(null); //baraye selectas



    }

    // open drawer for toolbar icon
    /*@Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.hamberger_btn:
                drawerLayout. openDrawer(GravityCompat.START); ;  // OPEN DRAWER
                return true;

        }
        return super.onOptionsItemSelected(item);
    }*/

    public void restartApp() {
        Intent intent = new Intent(getApplicationContext(), MainMenu.class);
        startActivity(intent);
        finish();
    }

    private BottomNavigationView.OnNavigationItemSelectedListener bottomListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {



                    switch (menuItem.getItemId()) {
                        case R.id.item_1:
                            fm.beginTransaction().hide(active).show(fragment1).commit();
                            bar.setVisibility(View.VISIBLE);
                            active = fragment1;
                            return true;
                        //selectedFragment = new HomeBottomFragment();

                        //break;
                        case R.id.item_2:
                            if (f2) {
                                fm.beginTransaction().add(R.id.fragment_container, fragment2, "2").hide(fragment2).commit();
                                f2 = false;
                            }

                            fm.beginTransaction().hide(active).show(fragment2).commit();
                            active = fragment2;
                            return true;
                        // selectedFragment = new ExploreBottomFragment();
                        // break;
                        case R.id.item_4:
                            if (f4) {
                                fm.beginTransaction().add(R.id.fragment_container, fragment4, "4").hide(fragment4).commit();
                                f4 = false;
                            }
                            fm.beginTransaction().hide(active).show(fragment4).commit();
                            active = fragment4;
                            return true;
                        case R.id.item_3:
                            if (f3) {
                                fm.beginTransaction().add(R.id.fragment_container, fragment3, "3").hide(fragment3).commit();
                                f3 = false;
                            }
                            fm.beginTransaction().hide(active).show(fragment3).commit();
                            active = fragment3;
                            return true;
                        //   selectedFragment = new TwittsBottomFragment();
                        //   break;


                    }
                    return true;
                    // return false; //for 2
                }
            };

    public void user_info(TwitterSession session, final Context context) {

        MyTwitterApiClient myTwitterApiClient = new MyTwitterApiClient(session);
        myTwitterApiClient.getCustomTwitterService().User(session.getUserId(), session.getUserName()).enqueue(new Callback<UserShow>() {
            @Override
            public void onResponse(Call<UserShow> call, Response<UserShow> response) {


                if (response.body() != null) {
                    UserShow show = response.body();
                    Toast.makeText(MainMenu.this, "" + show.getProfile_name() + "\n"
                            + show.getProfile_image_url() + "\n" + show.getFollowers_count(), Toast.LENGTH_SHORT).show();
                    int cf = show.getFollowers_count() + show.getFollowings_count();
                    preferences.edit().putInt("CP", cf).apply();

                    String purl = show.getProfile_image_url();
                    String burl = show.getProfile_banner_url();
                    // follower_num.setText("Follower\n" + String.valueOf(show.getFollowers_count()));
                    //  following_num.setText("Following\n" + String.valueOf(show.getFollowings_count()));
                    String url = geturlpic(purl);


                    Picasso.with(context).load(burl).into(banner);
                    Picasso.with(context).load(url).into(profile);


                }


            }

            @Override
            public void onFailure(Call<UserShow> call, Throwable t) {

            }
        });

    }

    public String geturlpic(String s) {
        char[] chars = s.toCharArray();
        String url = "";
        for (int i = 0; i < chars.length - 11; i++) {
            url += chars[i];
        }

        url += ".jpg";

        return url;
    }



}