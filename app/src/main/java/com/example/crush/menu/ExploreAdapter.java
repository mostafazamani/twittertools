package com.example.crush.menu;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.BaseAdapter;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ImageView;
import android.widget.TextView;

import com.example.crush.R;

public class ExploreAdapter extends BaseAdapter {

    private final Context mContext;
    private final String[] ex;

    // 1
    public ExploreAdapter(Context context, String[] ex) {
        this.mContext = context;
        this.ex = ex;
    }

    // 2
    @Override
    public int getCount() {
        return ex.length;
    }

    // 3
    @Override
    public long getItemId(int position) {
        return 0;
    }

    // 4
    @Override
    public Object getItem(int position) {
        return null;
    }

    // 5
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // 1
     //   final Book book = books[position];

        // 2
        if (convertView == null) {
            final LayoutInflater layoutInflater = LayoutInflater.from(mContext);
            convertView = layoutInflater.inflate(R.layout.explore_item, null);
        }

        // 3
        final ImageView profilePic = (ImageView)convertView.findViewById(R.id.imageview_cover_art);
        final TextView textname = (TextView)convertView.findViewById(R.id.textview_book_name);
        final TextView idname = (TextView)convertView.findViewById(R.id.textview_book_author);
        //final ImageView imageViewFavorite = (ImageView)convertView.findViewById(R.id.imageview_favorite);

        // 4
      /*  profilePic.setImageResource(.getImageResource());
        nameTextView.setText(mContext.getString(.getName()));
        authorTextView.setText(mContext.getString(.getAuthor()));*/

        return convertView;
    }

}
