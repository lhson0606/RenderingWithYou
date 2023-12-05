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

import com.airbnb.lottie.LottieAnimationView;
import com.dy.app.R;
import com.dy.app.activity.FragmentHubActivity;
import com.dy.app.activity.MainActivity;
import com.dy.app.core.FragmentCallback;
import com.dy.app.ui.adapter.AdapterMenuItems;
import com.dy.app.utils.ImageLoader;

public class FragmentMainMenu extends Fragment
        implements FragmentCallback, View.OnClickListener {
    public final static String TAG = "FragmentMainMenu";
    private FragmentHubActivity main;
    private LottieAnimationView ltvFindRoom;
    private LottieAnimationView ltvTwoPlayersOnSameDevice;
    private LottieAnimationView ltvRunScripts;
    private LottieAnimationView ltvCredits;
    private LottieAnimationView ltvQuit;

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
        ltvFindRoom = mainMenuView.findViewById(R.id.ltvFindRoom);
        ltvTwoPlayersOnSameDevice = mainMenuView.findViewById(R.id.ltvTwoPlayersOnSameDevice);
        ltvCredits = mainMenuView.findViewById(R.id.ltvCredits);
        ltvQuit = mainMenuView.findViewById(R.id.ltvQuit);
        ltvRunScripts = mainMenuView.findViewById(R.id.ltvRunScripts);
        ltvFindRoom.setOnClickListener(this);
        ltvTwoPlayersOnSameDevice.setOnClickListener(this);
        ltvCredits.setOnClickListener(this);
        ltvQuit.setOnClickListener(this);
        ltvRunScripts.setOnClickListener(this);
        return mainMenuView;
    }

    @Override
    public void onMsgFromMain(String TAG, int type, Object o1, Object o2) {

    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.ltvFindRoom) {
            ltvFindRoom.playAnimation();
            main.onMsgToMain(TAG, 0, null, null);
        }else if(v.getId() == R.id.ltvTwoPlayersOnSameDevice) {
            ltvTwoPlayersOnSameDevice.playAnimation();
            main.onMsgToMain(TAG, 4, null, null);
        }
        else if (v.getId() == R.id.ltvRunScripts) {
            ltvRunScripts.playAnimation();
            main.onMsgToMain(TAG, 3, null, null);
        }
        else if(v.getId() == R.id.ltvCredits) {
            ltvCredits.playAnimation();
            main.onMsgToMain(TAG, 1, null, null);
        } else if(v.getId() == R.id.ltvQuit) {
            ltvQuit.playAnimation();
            main.onMsgToMain(TAG, 2, null, null);
        }
    }
}
