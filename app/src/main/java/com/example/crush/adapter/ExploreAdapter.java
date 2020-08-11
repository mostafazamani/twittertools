package com.example.crush.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.BaseAdapter;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.crush.MyTwitterApiClient;
import com.example.crush.R;
import com.example.crush.menu.HomeBottomFragment;
import com.example.crush.models.UserShow;
import com.example.crush.models.following;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterSession;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ExploreAdapter extends BaseAdapter {

    private final Context mContext;
    private final List<following> ex;
    private TwitterSession session;

    // 1
    public ExploreAdapter(Context context) {
        ex = new ArrayList<>();
        this.mContext = context;
    }

    public void AddItemToList(List<following> l) {
        ex.addAll(l);
        this.notifyDataSetChanged();

    }

    // 2
    @Override
    public int getCount() {
        return ex.size();
    }

    // 3
    @Override
    public long getItemId(int position) {
        return 0;
    }

    // 4
    @Override
    public Object getItem(int position) {
        return ex.get(position);
    }

    // 5
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // 1
        //   final Book book = books[position];

        // 2
        if (convertView == null) {
            final LayoutInflater layoutInflater = LayoutInflater.from(mContext);
            convertView = layoutInflater.inflate(R.layout.explore_item, null);
        }
        session = TwitterCore.getInstance().getSessionManager().getActiveSession();

        final ImageView profilePic = (ImageView) convertView.findViewById(R.id.imageview_cover_art);
        final TextView textname = (TextView) convertView.findViewById(R.id.textview_book_name);
        final TextView idname = (TextView) convertView.findViewById(R.id.textview_book_author);
        //final ImageView imageViewFavorite = (ImageView)convertView.findViewById(R.id.imageview_favorite);


        String purl = ex.get(position).getProfilePictureUrl();

        textname.setText(ex.get(position).getName());
        idname.setText(ex.get(position).getScreenName());

        new DownloadImageTask(profilePic)
                .execute(purl);


        return convertView;
    }


    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bannerImage;

        public DownloadImageTask(ImageView bannerImage) {
            this.bannerImage = bannerImage;
            //   this.profileImage = profilImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("خطا در بارگیری عکس", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bannerImage.setImageBitmap(result);
            //profileImage.setImageBitmap(result);
        }
    }

}
