package com.dy.app.activity;

import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.dy.app.R;
import com.dy.app.manager.ConnectionManager;
import com.dy.app.manager.UIManager;
import com.dy.app.ui.view.FragmentChatLobby;
import com.dy.app.ui.view.FragmentSkinSelection;
import com.dy.app.utils.ImageLoader;

import java.io.InputStream;
import java.io.OutputStream;

public class GameLobbyActivity extends FragmentHubActivity
implements View.OnClickListener {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_lobby_activity);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        fm = getSupportFragmentManager();

        init();
        exqListener();
        attachFragment();
    }

    private void attachFragment() {
        FragmentTransaction ft = fm.beginTransaction();

//        fragmentSkinSelection = (FragmentSkinSelection) uiManager.getUI(UIManager.UIType.SKIN_SELECTION);
//        ft.replace(R.id.flSkinSelection, fragmentSkinSelection, FragmentSkinSelection.TAG);
//        ft.show(fragmentSkinSelection);


        ft.replace(R.id.flChatWindow,fragmentChatLobby, FragmentChatLobby.TAG);
        ft.show(fragmentChatLobby);



        ft.commit();
        //fragmentSkinSelection.setFragmentManager(getSupportFragmentManager());
    }

    private void exqListener() {
    }


    private void init(){
        resources = getResources();
        uiManager = UIManager.getInstance();
        screenView = (View) findViewById(R.id.fullScreen);
        screenView.setBackground(ImageLoader.loadImage(getResources().openRawResource(R.raw.chess_wallpaper)));
        fragmentChatLobby = FragmentChatLobby.newInstance();
        btnQuit = (Button)findViewById(R.id.btnQuit);
        btnQuit.setOnClickListener(this);
        btnReady = (Button)findViewById(R.id.btnReady);
        btnReady.setOnClickListener(this);
        btnReady.setText("Ready!!!");
        btnReady.setPadding(30, 30, 30, 30);
        btnReady.setTextSize(30);
        isReady = false;

    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.btnQuit){
            finish();
            ConnectionManager.getInstance().closeConnection();
        }else if (v.getId() == R.id.btnReady){
            if(!isReady){
                btnReady.setPadding(0, 0, 0, 0);
                btnReady.setText("We are ready!");
                btnReady.setTextSize(16);
                isReady = true;
            }else{
                btnReady.setText("Ready!!!");
                btnReady.setPadding(30, 30, 30, 30);
                btnReady.setTextSize(30);
                isReady = false;
            }

        }
    }

    private InputStream is;
    private OutputStream os;
    private View screenView;
    private FragmentSkinSelection fragmentSkinSelection;
    private UIManager uiManager;
    private FragmentManager fm;
    private Button btnQuit, btnReady;
    private FragmentChatLobby fragmentChatLobby;
    private Resources resources;
    private boolean isReady = false;

}
