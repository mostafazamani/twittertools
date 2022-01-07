package com.opteam.tools.menu;


import android.app.ProgressDialog;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.DialogFragment;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.opteam.tools.MyTwitterApiClient;
import com.opteam.tools.R;
import com.opteam.tools.adapter.CircularItemAdapter;
import com.opteam.tools.adapter.ListSearchAdapter;
import com.opteam.tools.models.UserCrushSearch;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterSession;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class CrushSearch extends DialogFragment {

    private TextView txt_search;
    private ImageButton btn_search;
    private ListView listView;
    private TwitterSession session;
    private CircularItemAdapter adapte;
    private LayoutInflater inflate;
    private ProgressDialog dialog;


    public CrushSearch(CircularItemAdapter adapter,LayoutInflater inflater) {
        this.adapte = adapter;
        this.inflate = inflater;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NORMAL, R.style.DialogAnimation_up_down);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.crush_search_dialog, container, false);
        // Dialog dialog=new Dialog(this,android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        txt_search = v.findViewById(R.id.crush_search_edittext);
        btn_search = v.findViewById(R.id.crush_search_button);
        listView = v.findViewById(R.id.crush_search_listview);

        dialog = new ProgressDialog(v.getContext());
        dialog.setMessage("wait...");
        dialog.setCancelable(false);

        session = TwitterCore.getInstance().getSessionManager().getActiveSession();


        // Do all the stuff to initialize your custom view
        btn_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.show();
                MyTwitterApiClient apiClient = new MyTwitterApiClient(session);
                apiClient.getCustomTwitterService().SearchUser(txt_search.getText().toString()).enqueue(new Callback<JsonArray>() {
                    @Override
                    public void onResponse(Call<JsonArray> call, Response<JsonArray> response) {
                                dialog.dismiss();
                        if (response.body() != null) {
                            try {
                                JsonArray elements = (JsonArray) response.body();
                                List<UserCrushSearch> crushSearchList = new ArrayList<>();
                                for (int i = 0; i < elements.size(); i++) {
                                    JsonObject jsonObject = (JsonObject) elements.get(i);
                                    UserCrushSearch users = new UserCrushSearch(jsonObject.get("id").getAsLong(),
                                            jsonObject.get("name").getAsString(), jsonObject.get("screen_name").getAsString(),
                                            jsonObject.get("profile_image_url").getAsString());

                                    crushSearchList.add(users);
//                                    Log.i("search",String.valueOf(i));
                                }
                                ListSearchAdapter adapter = new ListSearchAdapter(v.getContext(), crushSearchList, adapte,inflate,getDialog());
                                listView.setAdapter(adapter);
//                                Log.i("search","end");

                            } catch (Exception e) {
                                Toast.makeText(getContext(), "error", Toast.LENGTH_SHORT).show();
                            }


                        }
                    }

                    @Override
                    public void onFailure(Call<JsonArray> call, Throwable t) {
                        dialog.dismiss();
                        Toast.makeText(getContext(), "try again", Toast.LENGTH_SHORT).show();
                    }
                });


            }
        });
        return v;
    }
}
