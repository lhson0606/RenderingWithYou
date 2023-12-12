package com.dy.app.ui.dialog;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager2.widget.ViewPager2;

import com.airbnb.lottie.LottieAnimationView;
import com.dy.app.R;
import com.dy.app.gameplay.pgn.PGNFile;
import com.dy.app.ui.adapter.PlayerHistoryPagerAdapter;
import com.dy.app.ui.adapter.PlayerMatchHistoryAdapter;
import com.dy.app.ui.view.FragmentAllGamePlayHistory;
import com.dy.app.ui.view.FragmentPvEHistory;
import com.dy.app.ui.view.FragmentPvPHistory;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.Vector;

public class PlayerHistoryDialog extends DialogFragment
implements View.OnClickListener{
    public View currentSelectedView = null;

    public void setCurrentSelectedView(View view) {
        if(view == currentSelectedView){
            return;
        }

        if(currentSelectedView != null) {
            currentSelectedView.setBackgroundColor(Color.parseColor("#00000000"));
        }

        currentSelectedView = view;
        currentSelectedView.setBackgroundColor(Color.parseColor("#A7C1FC"));
    }

    public interface PlayerHistoryDialogListener{
        void onBtnReplayClick(PGNFile selectedFile);
        void onBtnSaveClick(PGNFile selectedFile);
    }

    private PlayerHistoryDialogListener listener;

    public PlayerHistoryDialog(FragmentActivity activity, PlayerHistoryDialogListener listener){
        this.listener = listener;
        this.activity = activity;
        setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.dialog_player_game_history, container, false);
        setCancelable(true);
        adapter = new PlayerHistoryPagerAdapter(activity, this);

        btnClose = v.findViewById(R.id.btnClose);
        btnReplay = v.findViewById(R.id.btnReplay);
        btnSave = v.findViewById(R.id.btnSave);
        tabLayout = v.findViewById(R.id.tabLayout);
        viewPager = v.findViewById(R.id.viewPager);

        btnClose.setOnClickListener(this);
        btnReplay.setOnClickListener(this);
        btnSave.setOnClickListener(this);
        viewPager.setAdapter(adapter);

        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            // Customize tabs based on position if needed
            tab.setText(pagerTabs[position]);
        }).attach();

        //set on fragment change listener
        viewPager.registerOnPageChangeCallback(callback);

        return v;
    }

    @Override
    public void show(@NonNull FragmentManager manager, @Nullable String tag) {
        super.show(manager, tag);
        //we need to update player history view after showing
        updatePlayerHistoryView();
    }

    private void updatePlayerHistoryView() {
        //update player history in case it is changed for all fragments
        //run on ui thread so that it can update view (otherwise, user have to touch it to update view)
        if(fragmentAllGamePlayHistory.isAdded()){
            fragmentAllGamePlayHistory.updateView();
        }

        if(fragmentPvPHistory.isAdded()){
            fragmentPvPHistory.updateView();
        }

        if(fragmentPvEHistory.isAdded()){
            fragmentPvEHistory.updateView();
        }

    }

    @Override
    public void onClick(View v) {
        if(v == btnClose) {
            btnClose.playAnimation();
            dismiss();
        } else if(v == btnReplay){
            listener.onBtnReplayClick(replayFile);
        }else if(v == btnSave){
            listener.onBtnSaveClick(replayFile);
        }
    }

    @Override
    public void dismiss() {
        replayFile = null;
        super.dismiss();
    }

    public void setReplayFile(PGNFile replayFile){
        this.replayFile = replayFile;
    }

    public PGNFile getReplayFile(){
        return replayFile;
    }

    @Override
    public void onDestroy() {
        viewPager.unregisterOnPageChangeCallback(callback);
        super.onDestroy();
    }

    private LottieAnimationView btnClose;
    private Button btnReplay, btnSave;
    private ViewPager2 viewPager;
    private final String[] pagerTabs = {"All", "PvP", "PvE"};
    private FragmentActivity activity;
    private TabLayout tabLayout;
    public String[] getPagerTabs(){
        return pagerTabs;
    }
    private PGNFile replayFile = null;
    private final FragmentAllGamePlayHistory fragmentAllGamePlayHistory = new FragmentAllGamePlayHistory(this);
    private final FragmentPvPHistory fragmentPvPHistory = new FragmentPvPHistory(this);
    private final FragmentPvEHistory fragmentPvEHistory = new FragmentPvEHistory(this);
    PlayerHistoryPagerAdapter adapter;
    final ViewPager2.OnPageChangeCallback callback = new ViewPager2.OnPageChangeCallback() {
        @Override
        public void onPageSelected(int position) {
            //reset replay file
            if(currentSelectedView != null) {
                currentSelectedView.setBackgroundColor(Color.parseColor("#00000000"));
                currentSelectedView = null;
                replayFile = null;
            }
            //do something when page is selected
            super.onPageSelected(position);
        }
    };

    public FragmentAllGamePlayHistory getFragmentAllGamePlayHistory() {
        return fragmentAllGamePlayHistory;
    }

    public FragmentPvPHistory getFragmentPvPHistory() {
        return fragmentPvPHistory;
    }

    public FragmentPvEHistory getFragmentPvEHistory() {
        return fragmentPvEHistory;
    }
}
