package com.op.crush.adapter;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.SetOptions;
import com.op.crush.R;
import com.op.crush.Room.CircleCrush.UserCrush;
import com.op.crush.Room.CircleCrush.UserCrushDatabase;
import com.op.crush.menu.HomeBottomFragment;
import com.op.crush.models.UserCrushSearch;
import com.op.crush.models.follow;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterSession;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ListSearchAdapter extends BaseAdapter {

    private List<UserCrushSearch> list;
    private Context context;
    private UserCrushDatabase database;
    private CircularItemAdapter adapter;
    private LayoutInflater inflater;
    public FirebaseFirestore firestore;
    private TwitterSession session;
    private Dialog dialog;


    public ListSearchAdapter(Context context, List<UserCrushSearch> list,
                             CircularItemAdapter adapter, LayoutInflater inflater, Dialog dialog) {
        this.context = context;
        this.list = list;
        this.adapter = adapter;
        this.inflater = inflater;
        this.dialog = dialog;
    }


    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int i) {
        return list.get(i);
    }

    @Override
    public long getItemId(int i) {
        return list.get(i).getId();
    }

    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {
        if (convertView == null) {
            final LayoutInflater layoutInflater = LayoutInflater.from(context);
            convertView = layoutInflater.inflate(R.layout.crush_search_adapter, null);
        }
        TextView name = convertView.findViewById(R.id.search_name);
        TextView screen_name = convertView.findViewById(R.id.search_screen_name);
        ImageView img = convertView.findViewById(R.id.search_img);
        Button btn = convertView.findViewById(R.id.search_crush);

        session = TwitterCore.getInstance().getSessionManager().getActiveSession();

        Map<String, String> map = new HashMap<>();
        map.put(String.valueOf(session.getId()), String.valueOf(session.getId()));


        database = UserCrushDatabase.getInstance(convertView.getContext());


        name.setText(list.get(i).getName());
        screen_name.setText(list.get(i).getScreen_name());

        String url = geturlpic(list.get(i).getProfile_pic());
        Picasso.with(convertView.getContext()).load(url).into(img);

        firestore = FirebaseFirestore.getInstance();
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setTimestampsInSnapshotsEnabled(true)
                .build();
        firestore.setFirestoreSettings(settings);


        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Picasso.with(context).load(url).into(new Target() {
                    @Override
                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                        firestore.collection("crush")
                                .document(String.valueOf(list.get(i).getId())).set(map, SetOptions.merge())
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Log.i("firebase", "saved");
                                        Log.i("firebase", String.valueOf(list.get(i).getId()));
//                                        new Ins(database).execute(new UserCrush(list.get(i).getId()));
                                        database.userCrushDao().insert(new UserCrush(list.get(i).getId()));
                                        View v = inflater.inflate(R.layout.circular_adapter, null);
                                        ImageView itemView = v.findViewById(R.id.img_item);
                                        itemView.setImageBitmap(bitmap);
                                        Log.i("ciecle_list", "bit");
                                        adapter.addItem(v);
                                        dialog.dismiss();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.i("firebase", "not saved" + e.getMessage());

                            }
                        });

                    }

                    @Override
                    public void onBitmapFailed(Drawable errorDrawable) {

                    }

                    @Override
                    public void onPrepareLoad(Drawable placeHolderDrawable) {

                    }
                });


            }
        });

        return convertView;
    }

    public String geturlpic(String s) {
        String url = "";
        if (s != null) {
            char[] chars = s.toCharArray();
            for (int i = 0; i < chars.length - 11; i++) {
                url += chars[i];
            }

            url += ".jpg";
        } else
            url = "https://pbs.twimg.com/profile_images/1275172653968633856/V25e9N9E_400x400.jpg";
        return url;
    }

    public void add_crush(Long map, long id) {

    }

    public static class Ins extends AsyncTask<UserCrush, Void, Void> {
        UserCrushDatabase database;

        public Ins(UserCrushDatabase database) {
            this.database = database;
        }

        @Override
        protected Void doInBackground(UserCrush... userCrushes) {
            database.userCrushDao().insert(userCrushes[0]);
            return null;
        }
    }


}
