package com.dy.app.ui.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.dy.app.R;
import com.dy.app.activity.FragmentHubActivity;
import com.dy.app.activity.MainActivity;
import com.dy.app.core.FragmentCallback;

public class FragmentLogoutForm extends Fragment
        implements View.OnClickListener, FragmentCallback {
    private Button btnClose, btnLogout;
    private TextView tvUsername;

    public final static String TAG = "FragmentLogoutForm";
    private FragmentHubActivity main;

    public static FragmentLogoutForm newInstance(){
        FragmentLogoutForm fragment = new FragmentLogoutForm();
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        main = (FragmentHubActivity)getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_logout_form, container, false);

        btnClose = view.findViewById(R.id.btnClose);
        tvUsername = view.findViewById(R.id.tvUsername);
        btnLogout = view.findViewById(R.id.btnLogout);

        btnClose.setOnClickListener(this);
        btnLogout.setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.btnClose){
            main.onMsgToMain(TAG, 0, null, null);
        }else if (v.getId() == R.id.btnLogout){
            main.onMsgToMain(TAG, 1, null, null);
        }
    }

    @Override
    public void onMsgFromMain(String TAG, int type, Object o1, Object o2) {
        switch (type){
            case 0:
                tvUsername.setText((String)o1);
                break;
            case 1:
                break;
        }
    }
}
