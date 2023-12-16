package com.dy.app.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.dy.app.R;

public class EndGameOptionAdapter extends BaseAdapter {
    private String[] options;

    public EndGameOptionAdapter(String[] options){
        this.options = options;
    }

    @Override
    public int getCount() {
        return options.length;
    }

    @Override
    public Object getItem(int position) {
        return options[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater layoutInflater=LayoutInflater.from(parent.getContext());
        View v=layoutInflater.inflate(R.layout.end_game_options_item,null);
        TextView tvOptionName = v.findViewById(R.id.tvOptionName);
        tvOptionName.setText(options[position]);
        return v;
    }
}
