package com.dy.app.ui.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.dy.app.R;
import com.dy.app.activity.MainActivity;
import com.dy.app.core.FragmentCallback;
import com.dy.app.ui.adapter.AdapterMenuItems;

public class FragmentSetting extends Fragment
        implements FragmentCallback, View.OnClickListener {

    Button btnClose;

    public final static String TAG = "FragmentSetting";
    private MainActivity main;

    public static FragmentSetting newInstance(){
        FragmentSetting fragment = new FragmentSetting();
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        main = (MainActivity)getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_setting, container, false);
        btnClose = view.findViewById(R.id.btnClose);
        btnClose.setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.btnClose){
            main.onMsgToMain(TAG, 0, null, null);
        }
    }

    @Override
    public void onMsgFromMain(String TAG, int type, Object o1, Object o2) {

    }
}