package com.op.crush;

import android.app.Dialog;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.view.Window;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.SetOptions;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.op.crush.Room.ProgressState;
import com.op.crush.Room.ProgressViewModel;
import com.op.crush.background.FlwService;
import com.op.crush.background.LoadFollower;
import com.op.crush.background.LoadFollowing;
import com.op.crush.menu.DownloaderBottomFragment;
import com.op.crush.menu.ExploreBottomFragment;
import com.op.crush.menu.FollowBottomFragment;
import com.op.crush.menu.HomeBottomFragment;
import com.op.crush.menu.ThemeDialog;
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
import androidx.fragment.app.DialogFragment;
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class MainMenu extends AppCompatActivity {


    private TwitterSession session;




    SharedPreferences preferences ;
    BottomNavigationView bottomNavigationView;
    ImageView profile, banner;
    TextView fc,fwc,tid,tname;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private NavigationView navigationView;
    public static DrawerLayout donav;

    Fragment fragment1 = new HomeBottomFragment();
    Fragment fragment2 = new ExploreBottomFragment();
    Fragment fragment3 = new TwittsBottomFragment();
    Fragment fragment4 = new FollowBottomFragment();
    Fragment fragment5 = new DownloaderBottomFragment(); //downloader
    FragmentManager fm = getSupportFragmentManager();
    Fragment active = fragment1;
    private FirebaseFirestore firestore;
    boolean f2 = true;
    boolean f3 = true;
    boolean f4 = true;
    boolean f5 = true;

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


        new CountDownTimer(5000, 1000) {
            @Override
            public void onTick(long l) {

            }

            @Override
            public void onFinish() {
                startService(new Intent(MainMenu.this, FlwService.class));
            }
        }.start();




        bottomNavigationView = findViewById(R.id.bottom_nav);






        preferences = getSharedPreferences("Courser", Context.MODE_PRIVATE);
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.navigation_view);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        // getSupportActionBar().setDisplayHomeAsUpEnabled(true); //for toolbar
        banner = navigationView.getHeaderView(0).findViewById(R.id.profile_banner);
        profile = navigationView.getHeaderView(0).findViewById(R.id.nav_profile);
        fc = navigationView.getHeaderView(0).findViewById(R.id.f_count);
        fwc = navigationView.getHeaderView(0).findViewById(R.id.fw_count);
        tid = navigationView.getHeaderView(0).findViewById(R.id.header_twitter_id);
        tname = navigationView.getHeaderView(0).findViewById(R.id.header_twitter_name);

/*

        hamberger.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });
*/


        progressViewModel.getState().observe(this, new Observer<List<ProgressState>>() {
            @Override
            public void onChanged(List<ProgressState> progressStates) {
                if (progressStates != null && progressStates.size() > 0) {
                //    text.setText(String.valueOf(progressStates.get(progressStates.size() - 1).getState()));
                    Log.i("vm", String.valueOf(progressStates.get(progressStates.size() - 1).getState()));
                }
            }
        });


        session = TwitterCore.getInstance().getSessionManager().getActiveSession();

        user_info(session, MainMenu.this);

        Map<String, String> map = new HashMap<>();

        firestore = FirebaseFirestore.getInstance();
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder().build();
        firestore.setFirestoreSettings(settings);


//
//        MyTwitterApiClient myTwitterApiClient = new MyTwitterApiClient(session);
//        myTwitterApiClient.getCustomTwitterService().getid(1994321L,1694138268288000421L,5000).enqueue(new retrofit2.Callback<JsonObject>() {
//            @Override
//            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
//
//                if (response.body() != null) {
//                    try {
//
//                        JsonObject jsonObject = (JsonObject) response.body();
//                        JsonArray jsonObject1 = jsonObject.getAsJsonArray("ids");
//                        for (int i=15000; i < jsonObject1.size()+15000; i++){
//                            map.put(String.valueOf(i), jsonObject1.get(i-15000).toString());
//                        }
//
//
//                        firestore.collection("SugestUser")
//                                .document("FR").set(map, SetOptions.merge())
//                                .addOnSuccessListener(new OnSuccessListener<Void>() {
//                                    @Override
//                                    public void onSuccess(Void aVoid) {
//                                        Log.i("id", "saved");
//
//                                    }
//                                }).addOnFailureListener(new OnFailureListener() {
//                            @Override
//                            public void onFailure(@NonNull Exception e) {
//                                Log.i("id", "not saved" + e.getMessage());
//
//                            }
//                        });
//
//                    } catch (Exception e) {
//                        Log.i("id", e.getMessage());
//                    }
//                } else {
//                    Log.i("id", "3");
//
//                }
//            }
//
//            @Override
//            public void onFailure(Call<JsonObject> call, Throwable t) {
//
//                Log.i("id", "error");
//            }
//        });



        ////////////////////Night Mode

        Menu menu = navigationView.getMenu();
     //   MenuItem menuItem = menu.findItem(R.id.nav_switch);
    //    View actionView = MenuItemCompat.getActionView(menuItem);
    //    nightswitch = (SwitchCompat) actionView.findViewById(R.id.nightswitch);
    //    night_preferences = getSharedPreferences("night" , 0);
    //    Boolean aBoolean = night_preferences.getBoolean("night_mode",true);

        /*if (aBoolean){
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            nightswitch.setChecked(true);
        }

        nightswitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if(isChecked){
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                    nightswitch.setChecked(true);
                    SharedPreferences.Editor editor = night_preferences.edit();
                    editor.putBoolean("night_mode",true);
                    editor.commit();
                }
                else {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                    nightswitch.setChecked(false);
                    SharedPreferences.Editor editor = night_preferences.edit();
                    editor.putBoolean("night_mode",false);
                    editor.commit();
                }
            }
        });

*/
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem drawItem) {
                int id = drawItem.getItemId();
                switch (id) {
                    case R.id.about_us:

                        Dialog about_us=new Dialog(MainMenu.this,android.R.style.Theme_NoTitleBar_Fullscreen);
                        about_us.setTitle("select color");
                        about_us.setCancelable(true);
                        about_us.setContentView(R.layout.about_us);
                        about_us.show();
                        break;
                    case R.id.policy :
                        String url = "https://twittwetools.w3spaces.com/";
                        Intent i = new Intent(Intent.ACTION_VIEW);
                        i.setData(Uri.parse(url));
                        startActivity(i);
                        break;
                    case R.id.twitter:
                        Intent launchIntent = getPackageManager().getLaunchIntentForPackage("com.twitter.android");
                        if (launchIntent != null) {
                            startActivity(launchIntent);//null pointer check in case package name was not found
                        }
                        break;
                    case R.id.exit :
                        SharedPreferences preferences;
                        preferences = getSharedPreferences("login", Context.MODE_PRIVATE);
                        preferences.edit().putString("log", "logout").apply();
                        Intent intent = new Intent(MainMenu.this,MainActivity.class);
                        startActivity(intent);
                        finish();
                        break;
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
                     //       bar.setVisibility(View.VISIBLE);
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

                        case R.id.item_5:

                            if (f5) {
                                fm.beginTransaction().add(R.id.fragment_container, fragment5, "5").hide(fragment5).commit();
                                f5 = false;
                            }
                            fm.beginTransaction().hide(active).show(fragment5).commit();
                            active = fragment5;
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
//                    Toast.makeText(MainMenu.this, "" + show.getProfile_name() + "\n"
//                            + show.getProfile_image_url() + "\n" + show.getFollowers_count(), Toast.LENGTH_SHORT).show();
                    int cf = show.getFollowers_count() + show.getFollowings_count();
                    preferences.edit().putInt("CP", cf).apply();

                    String purl = show.getProfile_image_url();
                    String burl = show.getProfile_banner_url();
                    // follower_num.setText("Follower\n" + String.valueOf(show.getFollowers_count()));
                    //  following_num.setText("Following\n" + String.valueOf(show.getFollowings_count()));
                    String url = geturlpic(purl);
                    fc.setText(String.valueOf(show.getFollowers_count()));
                    fwc.setText(String.valueOf(show.getFollowings_count()));
                    tid.setText(show.getScreen_name());
                    tname.setText(show.getProfile_name());

                    Picasso.with(context).load(burl).into(banner);
                    Picasso.with(context).load(url).into(profile);
                }
            }

            @Override
            public void onFailure(Call<UserShow> call, Throwable t) {
                Toast.makeText(context, "Check your connection", Toast.LENGTH_SHORT).show();
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