package com.example.crush.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.crush.R;
import com.example.crush.models.follow;

import java.util.ArrayList;
import java.util.List;

public class ListAdapter extends BaseAdapter {

    private List<follow> list = new ArrayList<>();
    private Context context;


    public ListAdapter(Context context) {
        this.context = context;


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
    public View getView(int i, View view, ViewGroup viewGroup) {

        View v = View.inflate(context, R.layout.simplerow,null);
        if (list!=null){
            TextView textView = v.findViewById(R.id.rowTextView);
             textView.setText(list.get(i).getName());
        }

        return v;
    }
}
