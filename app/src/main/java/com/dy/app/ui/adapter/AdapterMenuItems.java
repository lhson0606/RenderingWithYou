package com.dy.app.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.dy.app.R;

public class AdapterMenuItems extends BaseAdapter {
    private final String TAG = getClass().getSimpleName();
    private final String [] menuItems = {"Find room", "Credits", "Quit"};

    @Override
    public int getCount() {
        return menuItems.length;
    }

    @Override
    public Object getItem(int position) {
        return menuItems[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View v = inflater.inflate(R.layout.item_menu, null);
        TextView tv = v.findViewById(R.id.tvItem);
        tv.setText(menuItems[position]);
        return v;
    }
}
