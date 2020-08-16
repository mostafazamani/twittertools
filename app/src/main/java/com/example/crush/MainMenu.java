package com.example.crush;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.crush.menu.ExploreBottomFragment;
import com.example.crush.menu.HomeBottomFragment;
import com.example.crush.menu.TwittsBottomFragment;
import com.example.crush.models.followmodel;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.twitter.sdk.android.core.Twitter;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterSession;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.view.GravityCompat;
import androidx.core.view.MenuItemCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import retrofit2.Call;
import retrofit2.Response;

public class MainMenu extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private TwitterSession session;


    ImageButton hamberger;
    LinearLayout bar;

    SwitchCompat nightswitch;
    boolean night;

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

    private DbFollowers dbHelper;
    public long nextCursor = -1L;
    private DbFollowings db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Twitter.initialize(this);
        setContentView(R.layout.content_main);


        hamberger = findViewById(R.id.hamberger_btn);
        bar = findViewById(R.id.toolbar_lin);
        bottomNavigationView = findViewById(R.id.bottom_nav);
       // toolbar = findViewById(R.id.m_toolbar);
      //  setSupportActionBar(toolbar); //toolbar

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.navigation_view);

        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
       // getSupportActionBar().setDisplayHomeAsUpEnabled(true); //for toolbar

        hamberger.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawerLayout. openDrawer(GravityCompat.START);
            }
        });

        session = TwitterCore.getInstance().getSessionManager().getActiveSession();


        dbHelper = new DbFollowers(MainMenu.this);
        db = new DbFollowings(MainMenu.this);
        loadFollowers(session,nextCursor);
        loadFollowings(session,nextCursor);



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

                    Fragment selectedFragment = null;

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



    public void loadFollowers(final TwitterSession twitterSession, long next) {
        dbHelper = new DbFollowers(MainMenu.this);
         dbHelper.getWritableDatabase();
         dbHelper.getReadableDatabase();
        MyTwitterApiClient myTwitterApiClient = new MyTwitterApiClient(twitterSession);
        myTwitterApiClient.getCustomTwitterService().FollowersList(twitterSession.getId(), next, 200).enqueue(new retrofit2.Callback() {
            @Override
            public void onResponse(Call call, @NonNull Response response) {
                if (response.body() != null) {
                    followmodel fol = (followmodel) response.body();
                    if (fol.getResults() != null)
                        for (int i = 0; i < fol.getResults().size(); i++) {

                            dbHelper.AddItem(fol.getResults().get(i));
                        }
                    dbHelper.close();

                    if (fol.getNextCursor() != 0) {
                        loadFollowers(twitterSession, fol.getNextCursor());
                    } else {

                    }

                }
            }

            @Override
            public void onFailure(Call call, Throwable t) {


            }
        });


    }


    public void loadFollowings(final TwitterSession twitterSession, long next) {
        db = new DbFollowings(MainMenu.this);
        db.getWritableDatabase();
        db.getReadableDatabase();
        MyTwitterApiClient myTwitterApiClient = new MyTwitterApiClient(twitterSession);
        myTwitterApiClient.getCustomTwitterService().FollowingList(twitterSession.getId(), next, 200).enqueue(new retrofit2.Callback() {
            @Override
            public void onResponse(Call call, @NonNull Response response) {
                if (response.body() != null) {
                    followmodel fol = (followmodel) response.body();
                    if (fol.getResults() != null)
                        for (int i = 0; i < fol.getResults().size(); i++) {

                            db.AddItem(fol.getResults().get(i));
                        }

                    db.close();

                    if (fol.getNextCursor() != 0) {
                        loadFollowings(twitterSession, fol.getNextCursor());
                    } else {

                    }

                }
            }

            @Override
            public void onFailure(Call call, Throwable t) {


            }
        });


    }



}