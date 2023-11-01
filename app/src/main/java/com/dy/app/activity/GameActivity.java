package com.dy.app.activity;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.app.ProgressDialog;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.WindowManager;

import com.dy.app.R;
import com.dy.app.core.GameCore;
import com.dy.app.core.MainCallback;
import com.dy.app.core.TaskManager;
import com.dy.app.graphic.display.GameFragment;
import com.dy.app.utils.ImageLoader;

public class GameActivity extends FragmentHubActivity
implements MainCallback {
    private ProgressDialog progressDialog;
    private Handler mainHandler;
    private GameFragment gameFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        setContentView(R.layout.game_activity);
        TaskManager.getInstance().setActivity(this);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);//https://stackoverflow.com/questions/6922878/how-to-remove-the-battery-icon-in-android-status-bar
        mainHandler = new Handler(getMainLooper());
        initCore();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void initCore() {
        TaskManager.getInstance().addTask(new Runnable() {
            @Override
            public void run() {
                GameCore.getInstance().setActivity(GameActivity.this);
            }
        }, "initializing core");
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
            case TaskManager.TAG:
                handleTaskMangerMsg(TAG, type, o1, o2);
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
                gameFragment = GameFragment.newInstance();
                ft.add(R.id.fl_game_surface, gameFragment);
                ft.addToBackStack(GameFragment.TAG);
                ft.commit();
                break;
            case SET_GAME_BACKGROUND:
                gameFragment.onMsgFromMain(TAG, t, o1, o2);
                break;
        }
    }

    private void handleTaskMangerMsg(String TAG, int t, Object o1, Object o2){
        TaskManager.TaskType taskType = (TaskManager.TaskType) o1;
        switch (taskType){
            case INIT:
                break;
            case START:
                break;
            case UPDATE:
                progressDialog.setMessage((String) o2);
                break;
            case END:
                break;
            case SHOW_DIALOG:
                if(!progressDialog.isShowing()){
                    //progressDialog.show();
                }
                break;
            case DISMISS_DIALOG:
                progressDialog.dismiss();
                break;
        }
    }

}