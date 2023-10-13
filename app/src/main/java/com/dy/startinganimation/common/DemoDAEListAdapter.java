package com.dy.startinganimation.common;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.dy.startinganimation.activities.MainActivity;
import com.example.startinganimation.R;

import java.util.Vector;

public class DemoDAEListAdapter extends BaseAdapter {
    Vector<DemoDAE> daes;
    Activity activity;
    public DemoDAEListAdapter(Activity activity, Vector<DemoDAE> daes){
        this.activity = activity;
        this.daes = daes;
    }
    @Override
    public int getCount() {
        return daes.size();
    }

    @Override
    public Object getItem(int i) {
        return daes.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        DemoDAE demoDAE = daes.get(i);
        LayoutInflater inflater = activity.getLayoutInflater();
        View v = inflater.inflate(R.layout.demo_single_item, null);
        ImageView thumbnail = v.findViewById(R.id.ivThumbnail);
        TextView tv = v.findViewById(R.id.tvTitle);
        tv.setText(demoDAE.title);
        thumbnail.setImageResource(demoDAE.thumbnail);
        return v;
    }
}
