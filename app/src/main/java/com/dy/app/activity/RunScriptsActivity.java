package com.dy.app.activity;

import android.app.ProgressDialog;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;

import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.dy.app.R;
import com.dy.app.core.GameCore;
import com.dy.app.core.thread.GameLoop;
import com.dy.app.core.thread.ScriptsRunner;
import com.dy.app.graphic.display.GameFragment;

import java.io.IOException;
import java.io.InputStream;

public class RunScriptsActivity extends FragmentHubActivity{
    private ProgressDialog progressDialog;
    private Handler mainHandler;
    private GameFragment gameFragment;
    private GameLoop gameLoop;
    private GameCore gameCore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        setContentView(R.layout.game_activity);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);//https://stackoverflow.com/questions/6922878/how-to-remove-the-battery-icon-in-android-status-bar
        mainHandler = new Handler(getMainLooper());
        initCore();
        //runScript("scripts/carlsen_kasparov_2004.pgn");
        //carlsen_nakamura_2009.pgn
        runScript("scripts/carlsen_nakamura_2009.pgn");
    }

    private void runScript(String src) {

        InputStream is = null;
        try {
            is = getAssets().open(src);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        ScriptsRunner runner = new ScriptsRunner(is, gameCore.getBoard());
        runner.start();
    }

    @Override
    protected void onDestroy() {
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
            case SET_GAME_SURFACE:
                FragmentManager fm = getSupportFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();
                gameFragment = new GameFragment(this, gameCore.getEntityManger(), gameCore.getBoard());
                ft.replace(R.id.fl_game_surface, gameFragment);
                ft.commit();
                break;
            case SET_GAME_BACKGROUND:
                gameFragment.onMsgFromMain(TAG, t, o1, o2);
                break;
            case START_GAME:
                gameLoop = new GameLoop(gameFragment.getSurfaceView(), gameCore.getEntityManger());
                gameLoop.start();
                break;
        }
    }
}
