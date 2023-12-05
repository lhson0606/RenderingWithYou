package com.dy.app.activity;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.app.ProgressDialog;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.dy.app.R;
import com.dy.app.core.GameCore;
import com.dy.app.core.MainCallback;
import com.dy.app.core.thread.GameLoop;
import com.dy.app.core.thread.MultiDeviceInGameHandler;
import com.dy.app.gameplay.player.Player;
import com.dy.app.gameplay.player.PlayerProfile;
import com.dy.app.gameplay.player.Rival;
import com.dy.app.graphic.display.GameFragment;
import com.dy.app.manager.ConnectionManager;
import com.dy.app.manager.SoundManager;
import com.dy.app.manager.UIManager;
import com.dy.app.ui.dialog.PromotionSelectionDialog;
import com.dy.app.ui.view.FragmentChatLobby;
import com.dy.app.ui.view.FragmentSetting;

import java.util.concurrent.Semaphore;

public class GameActivity extends FragmentHubActivity
implements MainCallback, View.OnClickListener {

    private static final String TAG = "GameActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        setContentView(R.layout.game_activity);
        //https://stackoverflow.com/questions/6922878/how-to-remove-the-battery-icon-in-android-status-bar
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        mainHandler = new Handler(getMainLooper());
        init();
        attachFragment();
        attachListener();
        initCore();
    }

    private void init(){
        btnOpenChat = findViewById(R.id.btnOpenChat);
        btnConfig = findViewById(R.id.btnConfig);
        btnSpeaker = findViewById(R.id.btnSpeaker);
        fragmentSetting = FragmentSetting.newInstance();
        fragmentChatLobby = FragmentChatLobby.newInstance();
        TextView tvBlackPlayerName = findViewById(R.id.tvBlackPlayerName);
        TextView tvWhitePlayerName = findViewById(R.id.tvWhitePlayerName);
        TextView tvBlackPlayerElo = findViewById(R.id.tvBlackPlayerElo);
        TextView tvWhitePlayerElo = findViewById(R.id.tvWhitePlayerElo);

        if(Player.getInstance().isWhitePiece()) {
            tvWhitePlayerName.setText((String)Player.getInstance().profile.get(PlayerProfile.KEY_USERNAME));
            tvBlackPlayerName.setText(Rival.getInstance().getName());
            tvBlackPlayerElo.setText(String.valueOf(Rival.getInstance().getElo()));
            tvWhitePlayerElo.setText(String.valueOf(Player.getInstance().profile.get(PlayerProfile.KEY_ELO)));
        }else{
            tvWhitePlayerName.setText(Rival.getInstance().getName());
            tvBlackPlayerName.setText((String)Player.getInstance().profile.get(PlayerProfile.KEY_USERNAME));
            tvWhitePlayerElo.setText(String.valueOf(Rival.getInstance().getElo()));
            tvBlackPlayerElo.setText(String.valueOf(Player.getInstance().profile.get(PlayerProfile.KEY_ELO)));

        }
    }

    private void attachFragment() {
        FragmentTransaction ft = fm.beginTransaction();
        ft.add(R.id.flStage, fragmentSetting);
        ft.add(R.id.flStage, fragmentChatLobby);
        ft.show(fragmentSetting);
        ft.show(fragmentChatLobby);
        ft.hide(fragmentSetting);
        ft.hide(fragmentChatLobby);
        ft.commit();
    }

    private void attachListener(){
        btnOpenChat.setOnClickListener(this);
        btnConfig.setOnClickListener(this);
        btnSpeaker.setOnClickListener(this);
    }

    @Override
    protected void onDestroy() {
        gameLoop.shutDown();
        super.onDestroy();
    }

    private void initCore() {
        gameCore = new GameCore(this);
        gameCore.init();
    }

    @Override
    public void onMsgToMain(String TAG, int type, Object o1, Object o2) {
        mainHandler.post(new Runnable() {
            @Override
            public void run() {
                handle(TAG, type, o1, o2);
            }
        });
    }

    private void handle(String TAG, int type, Object o1, Object o2){
        switch (TAG){
            case GameCore.TAG:
                handleGameCoreMsg(TAG, type, o1, o2);
                break;
            case FragmentSetting.TAG:
            {
                handleMsgFromSetting(type, o1, o2);
            }
        }
    }

    private void handleMsgFromSetting(int type, Object o1, Object o2) {
        switch (type){
            case 0:
                hideFragment(fragmentSetting);
                break;
        }
    }

    public void addPeerMessage(String msg){
        if(fragmentChatLobby.isHidden()){
            runOnUiThread(()->{
                btnOpenChat.setAnimation("animated_ui/btn_open_chat_with_red_dot.json");
            });
            hasUnreadMessage = true;
        }
        runOnUiThread(()->{
            fragmentChatLobby.onMsgFromMain(GameActivity.TAG, FragmentChatLobby.ADD_PLAYER_MESSAGE, msg, null);
        });
    }

    private void handleGameCoreMsg(String TAG,int t, Object o1, Object o2) {
        GameCore.TaskType taskType = (GameCore.TaskType) o1;
        switch (taskType){
            case SET_LOADING_BACKGROUND:
                findViewById(R.id.loadingBackground).setBackground((BitmapDrawable) o2);
                break;
            case SET_GAME_BACKGROUND:
                gameFragment.onMsgFromMain(TAG, t, o1, o2);
                break;
            case START_GAME:
                FragmentManager fm = getSupportFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();
                //Semaphore semaphore = new Semaphore(0);
                gameFragment = new GameFragment(this, gameCore.getEntityManger(), gameCore.getBoard());
                ft.replace(R.id.fl_game_surface, gameFragment);
                ft.commit();
                gameLoop = new GameLoop(gameFragment.getSurfaceView(), gameCore.getEntityManger());
                Thread worker = new Thread(()->{
                    gameFragment.getSurfaceView().getRenderer().waitForGLInit();
                    gameLoop.start();
                    multiDeviceInGameHandler = new MultiDeviceInGameHandler(this, gameCore.getBoard(), ConnectionManager.getInstance(), gameFragment.getSurfaceView().getRenderer().getTilePicker());
                    multiDeviceInGameHandler.start();
                });
                worker.start();
                break;
        }
    }

    @Override
    public void onClick(View v) {
        SoundManager.getInstance().playSound(this, SoundManager.SoundType.BTN_BLOP);

        if(v == btnOpenChat){
            btnOpenChat.playAnimation();
            if(fragmentChatLobby.isHidden()){
                showFragment(fragmentChatLobby);
                if(hasUnreadMessage){
                    btnOpenChat.setAnimation("animated_ui/btn_open_chat_default.json");
                    hasUnreadMessage = false;
                }
            }else{
                hideFragment(fragmentChatLobby);
            }
        }else if(v == btnConfig){
            btnConfig.playAnimation();
            if(fragmentSetting.isHidden()){
                showFragment(fragmentSetting);
            }else{
                hideFragment(fragmentSetting);
            }
        }else if(v == btnSpeaker) {
            if(SoundManager.getInstance().isSoundOn()) {
                SoundManager.getInstance().setSoundOn(false);
                btnSpeaker.setAnimation("animated_ui/btn_speaker_mute.json");
            }else{
                SoundManager.getInstance().setSoundOn(true);
                btnSpeaker.setAnimation("animated_ui/btn_speaker_enable.json");
            }
        }
    }

    public void hideFragment(Fragment fragment){
        if(fragment.isHidden()) return;

        FragmentTransaction ft = fm.beginTransaction();
        ft.hide(fragment);
        ft.commit();
        currentFragment = null;
    }

    public void showFragment(Fragment fragment){
        if(currentFragment == fragment) return;

        FragmentTransaction ft = fm.beginTransaction();
        if(currentFragment != null) ft.hide(currentFragment);
        ft.show(fragment);
        ft.commit();

        currentFragment = fragment;
    }

    public void showPromotionDialog(PromotionSelectionDialog.PromotionSelectionDialogListener listener) {
        PromotionSelectionDialog dialog = new PromotionSelectionDialog(listener);
        dialog.show(getSupportFragmentManager(), "PromotionSelectionDialog");
    }

    private Fragment currentFragment;
    private final FragmentManager fm = getSupportFragmentManager();
    private ProgressDialog progressDialog;
    private Handler mainHandler;
    private GameFragment gameFragment;
    private GameLoop gameLoop;
    private GameCore gameCore;
    private MultiDeviceInGameHandler multiDeviceInGameHandler;
    private LottieAnimationView btnOpenChat, btnConfig, btnSpeaker;
    private FragmentSetting fragmentSetting;
    private FragmentChatLobby fragmentChatLobby;
    private boolean hasUnreadMessage = false;
}