package com.dy.app.activity;

import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;

import com.airbnb.lottie.LottieAnimationView;
import com.dy.app.R;
import com.dy.app.gameplay.player.BattlePass;
import com.dy.app.gameplay.player.Player;
import com.dy.app.manager.SoundManager;
import com.dy.app.ui.listener.BattlePassItemListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Vector;

public class GamepassActivity extends FragmentActivity
implements View.OnClickListener {
    int[] items = {
            CHEST, NO_REWARD, NO_REWARD, NO_REWARD, COINS_X1, COINS_X3, NO_REWARD, NO_REWARD, NO_REWARD, NO_REWARD,
            CHEST   , NO_REWARD, NO_REWARD, NO_REWARD, COINS_X1, COINS_X3, NO_REWARD, NO_REWARD, NO_REWARD, NO_REWARD,
            COINS_X3, NO_REWARD, NO_REWARD, NO_REWARD, COINS_X1, COINS_X3, NO_REWARD, NO_REWARD, NO_REWARD, NO_REWARD,
            COINS_X2, NO_REWARD, NO_REWARD, NO_REWARD, COINS_X1, COINS_X1, NO_REWARD, NO_REWARD, NO_REWARD, NO_REWARD,
            COINS_X3, NO_REWARD, NO_REWARD, NO_REWARD, COINS_X1, COINS_X3, NO_REWARD, NO_REWARD, NO_REWARD, NO_REWARD, COINS_X3
    };

    public static int NO_REWARD = -1;
    public static int CHEST = 0;
    public static int COINS_X1 = 1;
    public static int COINS_X2 = 2;
    public static int COINS_X3 = 3;

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
        final BattlePass battlePass = Player.getInstance().battlePass;
        final int player_pass_lvl = ((Long)battlePass.get(BattlePass.KEY_LEVEL)).intValue();
        final int step = 5;
        final int finalLvl = ((int)(items.length-1)/step)*step;

        for(int i = 0; i<items.length; i++){
            int curLvl = i;

            if(curLvl%step != 0){
                continue;
            }

            View v = getLayoutInflater().inflate(R.layout.lvl_milestone, llMilestones, false);
            ImageView ivRoad = v.findViewById(R.id.ivRoad);
            ImageView ivItem = v.findViewById(R.id.ivItem);
            LottieAnimationView lvCelebration = v.findViewById(R.id.lvCelebration);
            TextView tvLvl = v.findViewById(R.id.tvLvl);
            ImageView ivStatus = v.findViewById(R.id.ivStatus);
            //index of items
            tvLvl.setText(String.valueOf(curLvl));
            //get image of item
            switch (items[curLvl]){
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
            //***displaying items first
            //check if it is obtained
            if(battlePass.hasObtained(curLvl)){
                ivStatus.setImageResource(R.drawable.item_collected);
                ivItem.setImageAlpha((int)(255*0.8f));
            //it is not obtained and has index < player_lvl
            }else if(curLvl<=player_pass_lvl){
                BattlePassItemListener onClickListener = new BattlePassItemListener(this, battlePass, lvCelebration, ivStatus, ivItem,items[i], curLvl);
                ivStatus.setOnClickListener(onClickListener);
            }else {//else it's not available yet we don't have to add listener
                ivStatus.setImageResource(R.drawable.item_lock);
                ivStatus.setImageAlpha((int)(255*0.5f));
            }
            //***displaying road
            if(curLvl<=player_pass_lvl) {
                //it is activated
                if(curLvl == 0){
                    ivRoad.setImageResource(R.drawable.road_start_active);
                }else if (curLvl == finalLvl) {
                    ivRoad.setImageResource(R.drawable.road_end_active);
                }else{
                    ivRoad.setImageResource(R.drawable.road_mid_active);
                }
            }else{
                //it is not activated
                if(i == 0){
                    ivRoad.setImageResource(R.drawable.road_start_inactive);
                }else if (curLvl == finalLvl) {
                    ivRoad.setImageResource(R.drawable.road_end_inactive);
                }else {
                    ivRoad.setImageResource(R.drawable.road_mid_inactive);
                }
            }

            //finally we add the view to the layout
            llMilestones.addView(v);
        }
    }

    private void init(){
        fireworks = new Vector<>();
        btnClose = findViewById(R.id.btnClose);
        llMilestones = findViewById(R.id.llMilestones);
        tvLvl = findViewById(R.id.tvLvl);
        tvLvl.setText("Wins: " + String.valueOf(Player.getInstance().battlePass.get(BattlePass.KEY_LEVEL)));
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
                        isSeasonTimerRunning = false;
                    }
                }
            }
        });

        seasonTimer.start();

        LottieAnimationView lvFirework0 = findViewById(R.id.lvFirework0);
        LottieAnimationView lvFirework1 = findViewById(R.id.lvFirework1);
        LottieAnimationView lvFirework2 = findViewById(R.id.lvFirework2);
        LottieAnimationView lvFirework3 = findViewById(R.id.lvFirework3);
        LottieAnimationView lvFirework4 = findViewById(R.id.lvFirework4);

        fireworks.add(lvFirework0);
        fireworks.add(lvFirework1);
        fireworks.add(lvFirework2);
        fireworks.add(lvFirework3);
        fireworks.add(lvFirework4);
    }

    private int randInt(int min, int max){
        return (int)(Math.random() * (max - min + 1) + min);
    }

    private void exqListener(){
        btnClose.setOnClickListener(this);
    }

    @Override
    protected void onPause() {
        if(fireworksTimer.isAlive()){
            fireworksTimer.interrupt();
        }

        SoundManager.getInstance().stopSound(SoundManager.SoundType.FIREWORK_LONG);

        super.onPause();
    }

    @Override
    protected void onResume() {
        fireworksTimer = new Thread(new Runnable() {
            boolean isPlaying = true;
            @Override
            public void run() {
                for(int i = 0; i<fireworks.size(); i+=1){
                    try {
                        Thread.sleep(1000);
                        if(!isPlaying){
                            break;
                        }
                        final LottieAnimationView firework = fireworks.get(i);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                firework.playAnimation();
                            }
                        });
                    } catch (InterruptedException e) {
                        isPlaying = false;
                    }
                }
            }
        });
        fireworksTimer.start();
        SoundManager.getInstance().playSound(getApplicationContext(), SoundManager.SoundType.FIREWORK_LONG);
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        //prevent memory leak
        if(seasonTimer.isAlive()){
            isSeasonTimerRunning = false;
            seasonTimer.interrupt();
        }
        super.onDestroy();
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
    private Thread seasonTimer, fireworksTimer;
    private boolean isSeasonTimerRunning = false;
    private Vector<LottieAnimationView> fireworks;
}
