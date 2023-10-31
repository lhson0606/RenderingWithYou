package com.dy.app.ui.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.airbnb.lottie.LottieAnimationView;
import com.dy.app.R;
import com.dy.app.activity.FragmentHubActivity;
import com.dy.app.activity.MainActivity;

public class FragmentCredits extends Fragment
    implements View.OnClickListener {
    private LottieAnimationView btnClose;

    public final static String TAG = "FragmentCredits";
    private FragmentHubActivity main;

    public static FragmentCredits newInstance(){
        FragmentCredits fragment = new FragmentCredits();
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        main = (FragmentHubActivity)getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_credits, container, false);
        btnClose = view.findViewById(R.id.btnClose);
        btnClose.setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.btnClose){
            btnClose.playAnimation();
            main.onMsgToMain(TAG, 0, null, null);
        }
    }
}
