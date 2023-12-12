package com.dy.app.activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

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

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.Semaphore;

public class ReplayActivity extends FragmentHubActivity
        implements View.OnClickListener, SeekBar.OnSeekBarChangeListener, ScriptsRunner.IScriptRunnerCallback {
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
    }

    private void attachListener(){
        btnClose.setOnClickListener(this);
        sbProgress.setOnSeekBarChangeListener(this);
        btnPlay.setOnClickListener(this);
        btnPrev.setOnClickListener(this);
        btnNext.setOnClickListener(this);
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
    public void exitWithError(String error) {
        runOnUiThread(()->{
            showErrorDialog(error);
        });
    }

    @Override
    public void onClick(View v) {
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
        }
    }
}
