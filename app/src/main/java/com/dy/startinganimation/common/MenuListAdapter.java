package com.dy.startinganimation.common;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.dy.startinganimation.activities.MainActivity;
import com.example.startinganimation.R;

public class MenuListAdapter extends BaseAdapter {
    private MainActivity main;
    private String[] menuItems;
    public MenuListAdapter(MainActivity mainActivity, String[] menuItems) {
        this.main = mainActivity;
        this.menuItems = menuItems;
    }

    @Override
    public int getCount() {
        return menuItems.length;
    }

    @Override
    public Object getItem(int i) {
        return menuItems[i];
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        LayoutInflater inflater = main.getLayoutInflater();
        View v = inflater.inflate(R.layout.menu_single_item, null);
        TextView tv = v.findViewById(R.id.tvItem);
        tv.setText(menuItems[i]);
        return v;
    }
}
