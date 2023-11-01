package com.dy.app.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.airbnb.lottie.LottieAnimationView;
import com.dy.app.R;
import com.dy.app.gameplay.Player;
import com.dy.app.manager.SoundManager;

import java.text.SimpleDateFormat;
import java.util.Date;

public class GamepassActivity extends Activity
implements View.OnClickListener {
    int[] items = {
            0, 1, 2, 3, 1, 1, 0, 1, 1, 3,
            3, 2, 0, 1, 1, 0, 3, 1, 1, 3,
            3, 2, 0, 1, 1, 0, 3, 1, 1, 3,
            3, 2, 0, 1, 1, 0, 3, 1, 1, 3,
            3, 2, 0, 1, 1, 0, 3, 1, 1, 3,
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.game_pass_activity);

        init();
        exqListener();
        displayItems();
    }

    private void displayItems() {
        final int player_lvl = Player.getInstance().getPassLvl();
        for(int i = 0; i<=player_lvl/5; i+=1){
            View v = LayoutInflater.from(this).inflate(R.layout.lvl_milestone, llMilestones, false);
            ImageView ivRoad = v.findViewById(R.id.ivRoad);
            ImageView ivItem = v.findViewById(R.id.ivItem);
            LottieAnimationView lvCelebration = v.findViewById(R.id.lvCelebration);
            TextView tvLvl = v.findViewById(R.id.tvLvl);
            tvLvl.setText(String.valueOf(i*5));
            ivItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    lvCelebration.playAnimation();
                    ivItem.setEnabled(false);
                    ivItem.setImageResource(R.drawable.item_collected);
                    SoundManager.getInstance().playSound(getApplicationContext(), SoundManager.SoundType.COIN_CLINK);
                }
            });

            if(i == 0){
                ivRoad.setImageResource(R.drawable.road_start_active);
            }else if(i == items.length/5){
                ivRoad.setImageResource(R.drawable.road_end_active);
            }else {
                ivRoad.setImageResource(R.drawable.road_mid_active);
            }

            switch (items[i]){
                case 0:
                    ivItem.setImageResource(R.drawable.game_item_chest);
                    break;
                case 1:
                    ivItem.setImageResource(R.drawable.game_item_coins_x1);
                    break;
                case 2:
                    ivItem.setImageResource(R.drawable.game_item_coins_x2);
                    break;
                case 3:
                    ivItem.setImageResource(R.drawable.game_item_coins_x3);
                    break;
            }

            llMilestones.addView(v);
        }

        for(int i = player_lvl/5 + 1; i<=items.length/5; i+=1){
            View v = getLayoutInflater().inflate(R.layout.lvl_milestone, llMilestones, false);
            ImageView ivRoad = v.findViewById(R.id.ivRoad);
            ImageView ivItem = v.findViewById(R.id.ivItem);
            LottieAnimationView lvCelebration = v.findViewById(R.id.lvCelebration);
            TextView tvLvl = v.findViewById(R.id.tvLvl);
            tvLvl.setText(String.valueOf(i*5));

            if(i == 0){
                ivRoad.setImageResource(R.drawable.road_start_inactive);
            }else if(i == items.length/5){
                ivRoad.setImageResource(R.drawable.road_end_inactive);
            }else {
                ivRoad.setImageResource(R.drawable.road_mid_inactive);
            }

            switch (items[i]){
                case 0:
                    ivItem.setImageResource(R.drawable.game_item_chest);
                    break;
                case 1:
                    ivItem.setImageResource(R.drawable.game_item_coins_x1);
                    break;
                case 2:
                    ivItem.setImageResource(R.drawable.game_item_coins_x2);
                    break;
                case 3:
                    ivItem.setImageResource(R.drawable.game_item_coins_x3);
                    break;
            }

            llMilestones.addView(v);
        }

    }

    private void init(){
        btnClose = findViewById(R.id.btnClose);
        llMilestones = findViewById(R.id.llMilestones);
        tvLvl = findViewById(R.id.tvLvl);
        tvLvl.setText("Wins: " + String.valueOf(Player.getInstance().getPassLvl()));
        tvSeasonTimeRemaining = findViewById(R.id.tvSeasonTimeRemaining);
        isSeasonTimerRunning = true;
        seasonTimer = new Thread(new Runnable() {
            @Override
            public void run() {
                while (isSeasonTimerRunning){
                    try {
                        Thread.sleep(1000);
                        Date now = new Date();
                        long diff = seasonEnd.getTime() - now.getTime();
                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd'd:'HH'h:'mm'm:'ss's'");
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                tvSeasonTimeRemaining.setText(simpleDateFormat.format(diff));
                            }
                        });
                    } catch (InterruptedException e) {
                        finish();
                    }
                }
            }
        });

        seasonTimer.start();
    }

    private void exqListener(){
        btnClose.setOnClickListener(this);
    }

    @Override
    protected void onPause() {
        //prevent memory leak
        if(seasonTimer.isAlive()){
            isSeasonTimerRunning = false;
            seasonTimer.interrupt();
        }
        super.onPause();
    }

    @Override
    public void onClick(View v) {
        if(v == btnClose){
            btnClose.playAnimation();
            finish();
        }
    }

    private TextView tvSeasonTimeRemaining;
    private LottieAnimationView btnClose;
    private LinearLayout llMilestones;
    private TextView tvLvl;
    private final Date seasonEnd = new Date(2023, 11, 30, 0, 0, 0);
    private Thread seasonTimer;
    private boolean isSeasonTimerRunning = false;
}