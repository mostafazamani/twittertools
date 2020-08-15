package com.example.crush;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.crush.menu.ExploreBottomFragment;
import com.example.crush.menu.HomeBottomFragment;
import com.example.crush.menu.TwittsBottomFragment;
import com.example.crush.models.SuggestUser;
import com.example.crush.models.UserShow;
import com.example.crush.models.followingmodel;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;
import com.twitter.sdk.android.core.Twitter;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterSession;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.view.MenuItemCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.io.InputStream;
import java.util.List;
import java.util.Random;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainMenu extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private TwitterSession session;

    DbFollowers db;
    DbFollowers li;
    DbSuggest dbSuggest;

    SwitchCompat nightswitch;
    boolean night;
    ImageView profile, banner;
    TextView follower_num, following_num, twitts_num;
    BottomNavigationView bottomNavigationView;
    Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private NavigationView navigationView;

    Fragment fragment1 = new HomeBottomFragment();
    Fragment fragment2 = new ExploreBottomFragment();
    Fragment fragment3 = new TwittsBottomFragment();
    FragmentManager fm = getSupportFragmentManager();
    Fragment active = fragment1;

    boolean f2 = true;
    boolean f3 = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Twitter.initialize(this);
        setContentView(R.layout.content_main);


        bottomNavigationView = findViewById(R.id.bottom_nav);
        toolbar = findViewById(R.id.m_toolbar);
        setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.navigation_view);

        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        session = TwitterCore.getInstance().getSessionManager().getActiveSession();

        db = new DbFollowers(this);
        li = new DbFollowers(this);
        dbSuggest = new DbSuggest(this);
        li.getReadableDatabase();
        List<Long> list = li.getItem();

        loadSuggest(session, -1L, 964565868, this);

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


        fm.beginTransaction().add(R.id.fragment_container, fragment1, "1").commit();


        bottomNavigationView.setOnNavigationItemSelectedListener(bottomListener);
        bottomNavigationView.setItemIconTintList(null); //baraye selectas

        // getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new HomeBottomFragment()).commit();
        //user_info(session);

    }

    public void restartApp() {
        Intent intent = new Intent(getApplicationContext(), MainMenu.class);
        startActivity(intent);
        finish();
    }

    private BottomNavigationView.OnNavigationItemSelectedListener bottomListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                    Fragment selectedFragment = null;

                    switch (menuItem.getItemId()) {
                        case R.id.item_1:
                            fm.beginTransaction().hide(active).show(fragment1).commit();
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
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment).commit();
                    return true;
                    // return false; //for 2
                }
            };


    public void loadSuggest(final TwitterSession twitterSession, long next, final long id, final Context context) {
        db.getReadableDatabase();
        dbSuggest.getWritableDatabase();
        MyTwitterApiClient myTwitterApiClient = new MyTwitterApiClient(twitterSession);
        myTwitterApiClient.getCustomTwitterService().FollowersList(id, next, 200).enqueue(new retrofit2.Callback() {
            @Override
            public void onResponse(Call call, @NonNull Response response) {
                if (response.body() != null) {
                    followingmodel fol = (followingmodel) response.body();

                    if (fol.getResults() != null)
                        for (int i = 0; i < fol.getResults().size(); i++) {
                            SuggestUser fl = new SuggestUser();
                            if (!db.CheckItem(fol.getResults().get(i).getId()) && session.getId() != fol.getResults().get(i).getId()) {
                                fl.setId(fol.getResults().get(i).getId());
                                fl.setName(fol.getResults().get(i).getName());
                                fl.setScreenName(fol.getResults().get(i).getScreenName());
                                fl.setProfilePictureUrl(fol.getResults().get(i).getProfilePictureUrl());
                                dbSuggest.AddItem(fl);


                            }


                        }

                    Toast.makeText(context, "ok!!", Toast.LENGTH_SHORT).show();

                    if (fol.getNextCursor() != 0)
                        loadSuggest(twitterSession, fol.getNextCursor(), id, context);
                }
            }

            @Override
            public void onFailure(Call call, Throwable t) {
                Toast.makeText(context, "" + t.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });


    }


}