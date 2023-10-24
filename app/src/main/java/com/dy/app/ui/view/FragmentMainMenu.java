package com.dy.app.ui.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.dy.app.R;
import com.dy.app.activity.FragmentHubActivity;
import com.dy.app.activity.MainActivity;
import com.dy.app.core.FragmentCallback;
import com.dy.app.ui.adapter.AdapterMenuItems;
import com.dy.app.utils.ImageLoader;

public class FragmentMainMenu extends Fragment
        implements FragmentCallback, AdapterView.OnItemClickListener {
    public final static String TAG = "FragmentMainMenu";
    private FragmentHubActivity main;
    private ListView lvMenuItems;
    private AdapterMenuItems adapterMenuItems;

    public static FragmentMainMenu newInstance(){
        FragmentMainMenu fragment = new FragmentMainMenu();
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        main = (FragmentHubActivity)getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View mainMenuView = inflater.inflate(R.layout.fragment_menu_items, container, false);
        lvMenuItems = (ListView)mainMenuView.findViewById(R.id.lvMenuItems);
        adapterMenuItems = new AdapterMenuItems();

        lvMenuItems.setAdapter(adapterMenuItems);
        lvMenuItems.setOnItemClickListener(this);
        return mainMenuView;
    }

    @Override
    public void onMsgFromMain(String TAG, int type, Object o1, Object o2) {

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        final String selected_item = (String)parent.getItemAtPosition(position);
        if(selected_item.equals("Find room")) {
            main.onMsgToMain(TAG, 0, null, null);
        } else if(selected_item.equals("Credits")) {
            main.onMsgToMain(TAG, 1, null, null);
        } else if(selected_item.equals("Quit")) {
            main.onMsgToMain(TAG, 2, null, null);
        }
    }
}
