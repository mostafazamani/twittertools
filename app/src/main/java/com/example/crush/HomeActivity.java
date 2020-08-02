package com.example.crush;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.twitter.sdk.android.core.Twitter;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterSession;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import retrofit2.Call;
import retrofit2.Response;

public class HomeActivity extends AppCompatActivity {

    private HomeActivity activity = this;
    public ListView mainListView;
    public ListAdapter adapter;
    public List<following> li;

    public List<following> getLi() {
        return li;
    }

    public void setLi(List<following> li) {
        this.li = li;
    }

    public long nextCursor = -1L;
    public List<following> followings;

    public List<following> getFollowings() {
        return followings;
    }

    public void setFollowings(List<following> followings) {
        this.followings = followings;
    }

    public TwitterSession session;

    public Handler mhandler;
    public View ftview;
    public boolean isloading = false;

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Twitter.initialize(this);
        setContentView(R.layout.activity_home);

        session = TwitterCore.getInstance().getSessionManager().getActiveSession();
        Toast.makeText(activity, "" + session.getUserName() + "    \n" + session.getUserId(), Toast.LENGTH_SHORT).show();
        mainListView = (ListView) findViewById(R.id.mainListView);

        LayoutInflater li = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        ftview = li.inflate(R.layout.footerlist, null);

        mhandler = new myHandler();


        adapter = new ListAdapter(getApplicationContext());
        mainListView.setAdapter(adapter);


            loginMethod(session, nextCursor);


        final Thread thread = new threadGetmoredata();

//        mainListView.setOnScrollListener(new AbsListView.OnScrollListener() {
//            @Override
//            public void onScrollStateChanged(AbsListView absListView, int i) {
//
//            }
//
//            @Override
//            public void onScroll(AbsListView absListView, int i, int i1, int i2) {
//
//                if (absListView.getLastVisiblePosition() == i2 - 1 && mainListView.getCount() >= 20 && isloading == false) {
//                    isloading = true;
//
//                    Toast.makeText(getApplicationContext(), "its true", Toast.LENGTH_SHORT).show();
//
//
//                    thread.start();
//
//
//                }
//
//
//            }
//        });


    }


    public void loginMethod(final TwitterSession twitterSession, long next) {


        MyTwitterApiClient myTwitterApiClient = new MyTwitterApiClient(twitterSession);
        myTwitterApiClient.getCustomTwitterService().list(twitterSession.getId(), next, 200).enqueue(new retrofit2.Callback() {
            @Override
            public void onResponse(Call call, @NonNull Response response) {
                if (response.body() != null) {
                    followingmodel fol = (followingmodel) response.body();


                    adapter.AddItemToList(fol.getResults());
                    adapter.notifyDataSetChanged();

                    Toast.makeText(HomeActivity.this, "" + fol.getNextCursor(), Toast.LENGTH_SHORT).show();

                    if (fol.getNextCursor() !=0)  loginMethod(twitterSession,fol.getNextCursor());
                }
            }

            @Override
            public void onFailure(Call call, Throwable t) {

                Toast.makeText(HomeActivity.this, "" + t, Toast.LENGTH_SHORT).show();

            }
        });
//        }

        Toast.makeText(activity, "end", Toast.LENGTH_SHORT).show();
    }

    public class myHandler extends Handler {

        @Override
        public void handleMessage(@NonNull Message msg) {
            switch (msg.what) {

                case 0:
                    mainListView.addFooterView(ftview);
                    break;
                case 1:
                    getmore(session);
                    mainListView.removeFooterView(ftview);
                    isloading = false;
                    break;
                default:
                    break;
            }
        }
    }

    public List<following> getmore(final TwitterSession twitterSession) {
        li = new ArrayList<>();
        MyTwitterApiClient myTwitterApiClient = new MyTwitterApiClient(twitterSession);
        myTwitterApiClient.getCustomTwitterService().list(twitterSession.getId(), nextCursor, 20).enqueue(new retrofit2.Callback() {
            @Override
            public void onResponse(Call call, Response response) {
                if (response.body() != null) {
                    followingmodel fol = (followingmodel) response.body();
                    if (fol.getResults() != null) {
                        followings = fol.getResults();
                        adapter.AddItemToList(followings);
                        adapter.notifyDataSetChanged();

                        if (Long.parseLong(fol.getNextCursorStr()) == 0) {
                            mainListView.removeFooterView(ftview);
                            isloading = true;
                            Toast.makeText(activity, "end", Toast.LENGTH_SHORT).show();
                        } else {
                            nextCursor = Long.parseLong(fol.getNextCursorStr());
                        }
                        Toast.makeText(HomeActivity.this, "" + fol.getNextCursorStr(), Toast.LENGTH_SHORT).show();

                    }
                }
            }


            @Override
            public void onFailure(Call call, Throwable t) {

                Toast.makeText(HomeActivity.this, "" + t, Toast.LENGTH_SHORT).show();

            }
        });


        return getLi();

    }


    public class threadGetmoredata extends Thread {

        @Override
        public void run() {

            mhandler.sendEmptyMessage(0);


            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }


            Message msg = mhandler.obtainMessage(1);
            mhandler.sendMessage(msg);


        }
    }

}