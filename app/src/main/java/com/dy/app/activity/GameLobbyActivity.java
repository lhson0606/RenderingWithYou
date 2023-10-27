package com.dy.app.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.provider.FontRequest;
import androidx.emoji.text.EmojiCompat;
import androidx.emoji.text.FontRequestEmojiCompatConfig;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.dy.app.R;
import com.dy.app.manager.ConnectionManager;
import com.dy.app.manager.UIManager;
import com.dy.app.ui.view.FragmentChatLobby;
import com.dy.app.ui.view.FragmentSkinSelection;
import com.dy.app.utils.ImageLoader;
import com.dy.app.network.MessageType;
import com.dy.app.utils.MessageFactory;

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

        fragmentSkinSelection = (Fragment) uiManager.getUI(UIManager.UIType.CREDITS);
        ft.add(R.id.flSkinSelection, fragmentSkinSelection, null);
        ft.show(fragmentSkinSelection);


        ft.add(R.id.flChatWindow,fragmentChatLobby, FragmentChatLobby.TAG);
        ft.show(fragmentChatLobby);



        ft.commit();
        //fragmentSkinSelection.setFragmentManager(getSupportFragmentManager());
    }

    private void exqListener() {
        // start listening to incoming messages
        Handler handler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(@NonNull Message msg) {
                switch (msg.what){
                    case 0:
                        fragmentChatLobby.onMsgFromMain(ConnectionManager.TAG, 0, msg.obj, null);
                        break;
                }
                return false;
            }
        });

        ConnectionManager.getInstance().startReceiving(handler);
        ConnectionManager.getInstance().setConnectionLostCallback(new ConnectionManager.ConnectionStatusCallback() {
            @Override
            public void onConnectionLost() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (!isFinishing()) {  // Check if the activity is finishing
                            AlertDialog.Builder builder = new AlertDialog.Builder(GameLobbyActivity.this);
                            builder.setTitle("Connection lost");
                            builder.setCancelable(false);  // Prevent the dialog from being canceled by clicking outside
                            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    // Handle the OK button click
                                    dialog.dismiss();
                                    // You can finish the activity or take other actions here
                                    finish();
                                }
                            });
                            AlertDialog dialog = builder.create();
                            dialog.show();
                        }
                    }
                });
            }

            @Override
            public void onWeakConnection() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (!isFinishing()) {  // Check if the activity is finishing
                            AlertDialog.Builder builder = new AlertDialog.Builder(GameLobbyActivity.this);
                            builder.setTitle("Weak connection");
                            builder.setCancelable(false);  // Prevent the dialog from being canceled by clicking outside
                            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    // Handle the OK button click
                                    dialog.dismiss();
                                    // You can finish the activity or take other actions here
                                    //finish();
                                }
                            });
                            AlertDialog dialog = builder.create();
                            dialog.show();
                        }
                    }
                });
            }
        });
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
        // Initialize EmojiCompat
        FontRequest fontRequest = new FontRequest(
                "com.google.android.gms.fonts",
                "com.google.android.gms",
                "Noto Color Emoji Compat",
                R.array.com_google_android_gms_fonts_certs);
        EmojiCompat.Config config = new FontRequestEmojiCompatConfig(this, fontRequest);
        EmojiCompat.init(config);


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
                informPeerReady(true);
                isReady = true;
            }else{
                btnReady.setText("Ready!!!");
                btnReady.setPadding(30, 30, 30, 30);
                btnReady.setTextSize(30);
                isReady = false;
            }

        }
    }
    @Override
    public void onBackPressed() {

        new AlertDialog.Builder(this)
                .setTitle("Confirmation")
                .setMessage("Do you want to exit this lobby?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Perform any necessary actions here before exiting
                        // For example, you can call super.onBackPressed() to navigate back
                        GameLobbyActivity.super.onBackPressed();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // User chose not to exit, so do nothing
                    }
                })
                .show();

        //to prevent compiler from complaining
        if(false)
            super.onBackPressed();
    }

    private void informPeerReady(boolean isReady){
        ConnectionManager.getInstance().postMessage(MessageFactory.getInstance().createSystemMessage("I am ready", 0));
    }

    private View screenView;
    private Fragment fragmentSkinSelection;
    private UIManager uiManager;
    private FragmentManager fm;
    private Button btnQuit, btnReady;
    private FragmentChatLobby fragmentChatLobby;
    private Resources resources;
    private boolean isReady = false;

}
