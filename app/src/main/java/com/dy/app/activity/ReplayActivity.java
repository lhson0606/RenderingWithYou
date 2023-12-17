package com.dy.app.activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.airbnb.lottie.LottieAnimationView;
import com.dy.app.R;
import com.dy.app.core.GameCore;
import com.dy.app.core.thread.GameLoop;
import com.dy.app.core.thread.ScriptsRunner;
import com.dy.app.gameplay.pgn.PGNFile;
import com.dy.app.gameplay.pgn.PGNParseException;
import com.dy.app.gameplay.player.Player;
import com.dy.app.gameplay.player.Rival;
import com.dy.app.graphic.display.GameFragment;
import com.dy.app.manager.SoundManager;
import com.dy.app.ui.dialog.MoveControlPanel;
import com.dy.app.ui.view.FragmentSetting;
import com.dy.app.utils.Utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.Semaphore;

public class ReplayActivity extends FragmentHubActivity
        implements View.OnClickListener, SeekBar.OnSeekBarChangeListener, ScriptsRunner.IScriptRunnerCallback {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        setContentView(R.layout.run_script_activity);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);//https://stackoverflow.com/questions/6922878/how-to-remove-the-battery-icon-in-android-status-bar
        mainHandler = new Handler(getMainLooper());
        Intent callerIntent = getIntent();
        pgnFile = (PGNFile) callerIntent.getSerializableExtra("pgn");
        moveControlPanel = MoveControlPanel.newInstance(pgnFile);
        Player.getInstance().setWhitePiece(true);
        Rival.getInstance().setWhitePiece(false);
        initCore();
        init();
        attachListener();
    }

    private void updateDisplay(PGNFile pgnFile){
        runOnUiThread(()->{
            ((TextView)findViewById(R.id.tvEventTitle)).setText(pgnFile.getTitle());
            ((TextView)findViewById(R.id.tvBlackPlayerName)).setText(pgnFile.getBlackPlayerName());
            ((TextView)findViewById(R.id.tvWhitePlayerName)).setText(pgnFile.getWhitePlayerName());
            ((TextView)findViewById(R.id.tvBlackPlayerElo)).setText(pgnFile.getBlackPlayerElo());
            ((TextView)findViewById(R.id.tvWhitePlayerElo)).setText(pgnFile.getWhitePlayerElo());
            sbProgress.setMax(pgnFile.getBothSideMoveCount());
            sbProgress.setProgress(0);
            btnPlay.setEnabled(true);
            btnPrev.setEnabled(true);
            btnNext.setEnabled(true);
        });
    }

    public void updateProgress(int progress){
        runOnUiThread(()->{
            sbProgress.setProgress(progress);
            if(!moveControlPanel.isVisible()){
                return;
            }
            if(progress>0){
                moveControlPanel.updateMoveIndex(progress-1);
            }else{
                moveControlPanel.unhighlight();
            }

        });

    }

    private void init(){
        btnClose = findViewById(R.id.btnClose);
        sbProgress = findViewById(R.id.sbProgress);
        btnPlay = findViewById(R.id.btnPlay);
        btnPrev = findViewById(R.id.btnPrev);
        btnNext = findViewById(R.id.btnNext);
        //disable the buttons for now, until we load the script, we will enable them in updateDisplay()
        btnPlay.setEnabled(false);
        btnPrev.setEnabled(false);
        btnNext.setEnabled(false);

        btnConfig = findViewById(R.id.btnConfig);
        btnSpeaker = findViewById(R.id.btnSpeaker);
        btnMovePanel = findViewById(R.id.btnMovePanel);

        fragmentSetting = new FragmentSetting();
    }

    private void attachListener(){
        btnClose.setOnClickListener(this);
        sbProgress.setOnSeekBarChangeListener(this);
        btnPlay.setOnClickListener(this);
        btnPrev.setOnClickListener(this);
        btnNext.setOnClickListener(this);
        btnConfig.setOnClickListener(this);
        btnSpeaker.setOnClickListener(this);
        btnMovePanel.setOnClickListener(this);
    }

    private void runScript(PGNFile pgnFile) {
        updateDisplay(pgnFile);
        runner = new ScriptsRunner(this, pgnFile, gameCore.getBoard());
        runner.start();
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
                handleFragmentSettingMsg(TAG, type, o1, o2);
                break;
            case MoveControlPanel.TAG:
                handleMoveControlPanelMsg(TAG, type, o1, o2);
                break;
        }
    }

    private void handleMoveControlPanelMsg(String tag, int type, Object o1, Object o2) {
        switch (type){
            case MoveControlPanel.JUMP_TO_MOVE:
                int moveIndex = (int) o1;
                runner.jumpToMove(moveIndex + 1);
                break;
            case MoveControlPanel.SHARE_GAME_IMAGE:
                Bitmap bitmap = (Bitmap) o1;
                Utils.shareBitmap(this, bitmap);
                bitmap.recycle();
                break;
        }
    }

    private void handleFragmentSettingMsg(String tag, int type, Object o1, Object o2) {
        switch (type){
            case FragmentSetting.CLOSE_PANEL:
                removeFragment(fragmentSetting);
                break;
        }
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
                    runScript(pgnFile);
                    gameLoop.start();
                });

                worker.start();
                break;
        }
    }

    private void showErrorDialog(String msg){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Error");
        builder.setMessage(msg);
        builder.setPositiveButton("OK", (dialog, which) -> {
            quit();
        });
    }

    @Override
    public void onBackPressed() {
        showQuitDialog();

        if(false){
            //stop the IDE from complaining
            super.onBackPressed();
        }
    }

    private void showQuitDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Quit");
        builder.setMessage("Are you sure to quit?");
        builder.setPositiveButton("Yes", (dialog, which) -> {
            quit();
        });
        builder.setNegativeButton("No", (dialog, which) -> {
            dialog.dismiss();
        });
        builder.show();
    }

    private void quit(){
        runner.close();
        finish();
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if(fromUser){
            Log.d("Debug concurrent", "on user progress change: " + progress);
            runner.jumpToMove(progress);
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        Log.d("Debug concurrent", "<start touching progress bar");
        //runner.pausePlaying();
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        //runner.resumePlaying();
        Log.d("Debug concurrent", "stop touching progress bar>");
    }

    public void changePlayButtonToPause(){
        runOnUiThread(()->{
            btnPlay.setImageResource(R.drawable.ic_pause_playing);
        });
    }

    public void changePlayButtonToContinue(){
        runOnUiThread(()->{
            btnPlay.setImageResource(R.drawable.ic_continue_playing);
        });
    }

    @Override
    public void exitWithError(String message) {
        runOnUiThread(()->{
            showErrorDialog(message);
        });
    }

    @Override
    public void onClick(View v) {
        SoundManager.getInstance().playSound(this, SoundManager.SoundType.BTN_BLOP);
        if(v == btnClose){
            btnClose.playAnimation();
            showQuitDialog();
        }else if(v == btnPlay){
            if(runner.isPaused()){
                runner.resumePlaying();
                btnPlay.setImageResource(R.drawable.ic_pause_playing);
            }else{
                runner.pausePlaying();
                btnPlay.setImageResource(R.drawable.ic_continue_playing);
            }
        }else if(v == btnPrev){
            runner.prevMove();
        }else if(v == btnNext){
            runner.nextMove();
        }else if(v == btnSpeaker){
            if(SoundManager.getInstance().isSoundOn()){
                SoundManager.getInstance().setSoundOn(false);
                setButtonSpeakerDisableIcon();
            }else{
                SoundManager.getInstance().setSoundOn(true);
                setButtonSpeakerEnableIcon();
            }
            btnSpeaker.playAnimation();
        } else if (v == btnConfig) {
            if(isShowingFragment(fragmentSetting)){
                removeFragment(fragmentSetting);
            }else{
                showFragment(fragmentSetting);
            }
        }else if(v == btnMovePanel){
            btnMovePanel.playAnimation();
            showMoveControlPanel();
        }
    }

    private void setButtonSpeakerEnableIcon(){
        btnSpeaker.setAnimation("animated_ui/btn_speaker_enable.json");
    }

    private void setButtonSpeakerDisableIcon(){
        btnSpeaker.setAnimation("animated_ui/btn_speaker_mute.json");
    }

    private void showMoveControlPanel() {
        if(moveControlPanel == null){
            return;
        }

        moveControlPanel.show(getSupportFragmentManager(), MoveControlPanel.TAG);
    }

    private boolean isShowingFragment(Fragment fragment){
        if(currenFragment == null){
            return false;
        }else {
            return currenFragment == fragment;
        }
    }

    private void showFragment(Fragment fragment){
        if(isShowingFragment(fragment)){
            return;
        }
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.flStage, fragment);
        ft.commit();
        currenFragment = fragment;
    }

    private void removeFragment(Fragment fragment){
        if(isShowingFragment(fragment)){
            FragmentManager fm = getSupportFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            ft.remove(fragment);
            ft.commit();
            currenFragment = null;
        }
    }

    public ScriptsRunner getRunner(){
        return runner;
    }

    public PGNFile getRunningPGNFile(){
        return pgnFile;
    }

    private ProgressDialog progressDialog;
    private Handler mainHandler;
    private GameFragment gameFragment;
    private GameLoop gameLoop;
    private GameCore gameCore;
    private LottieAnimationView btnClose;
    private PGNFile pgnFile = null;
    private SeekBar sbProgress;
    private ScriptsRunner runner = null;
    private ImageView btnPlay, btnPrev, btnNext;
    private MoveControlPanel moveControlPanel = null;
    private LottieAnimationView btnConfig, btnSpeaker, btnMovePanel;
    private FragmentSetting fragmentSetting;
    private Fragment currenFragment = null;
}
