package com.dy.app.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.provider.FontRequest;
import androidx.emoji.text.EmojiCompat;
import androidx.emoji.text.FontRequestEmojiCompatConfig;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.dy.app.R;
import com.dy.app.core.dythread.MessageDispatcher;
import com.dy.app.gameplay.Player;
import com.dy.app.gameplay.Rival;
import com.dy.app.manager.ConnectionManager;
import com.dy.app.manager.UIManager;
import com.dy.app.ui.view.FragmentChatLobby;
import com.dy.app.ui.view.FragmentSkinSelection;
import com.dy.app.utils.ImageLoader;
import com.dy.app.network.MessageType;
import com.dy.app.utils.MessageFactory;

import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

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

    private void init(){
        resources = getResources();
        uiManager = UIManager.getInstance();
        screenView = (View) findViewById(R.id.fullScreen);
        screenView.setBackground(ImageLoader.loadImage(getResources().openRawResource(R.raw.chess_wallpaper)));
        fragmentChatLobby = (FragmentChatLobby) UIManager.getInstance().getUI(UIManager.UIType.CHAT);
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

        chatMsgHandler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(@NonNull Message msg) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        fragmentChatLobby.onMsgFromMain(ConnectionManager.TAG, 0, msg.obj, null);
                    }
                });

                return true;
            }
        });

        systemMsgHandler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(@NonNull Message msg) {
                com.dy.app.network.Message message = (com.dy.app.network.Message)msg.obj;
                final String sysMsg = new String(message.getData(), StandardCharsets.UTF_8);
                switch(message.getCode()) {
                    case 0:
                        if (sysMsg.equals(ready_msg)) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    fragmentChatLobby.addSystemMessage(Rival.getInstance().getName() + " is ready");
                                }
                            });

                            if(isReady){
                                if(Player.getInstance().isHost()){
                                    countDown();
                                }
                                else{
                                    //send request count down to host
                                    ConnectionManager.getInstance().postMessage(MessageFactory.getInstance().createSystemMessage("", 5));
                                }
                            }

                        }else{
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    fragmentChatLobby.addSystemMessage(Rival.getInstance().getName() + " is not ready");
                                    startingMatch = false;
                                }
                            });
                        }
                    break;

                    case 1://start match
                        ConnectionManager.getInstance().postMessage(MessageFactory.getInstance().createSystemMessage("", 4));
                        startMatch();
                    break;
                    case 2://count down
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                fragmentChatLobby.addSystemMessage(sysMsg);
                            }
                        });
                    break;
                    case 3:
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                fragmentChatLobby.addSystemMessage(Rival.getInstance().getName() + " left the lobby");
                            }
                        });
                    break;
                    case 4://echo match started
                        startMatch();
                    break;
                    case 5://request count down
                        if(!startingMatch)
                            countDown();
                    break;
                }


                return true;
            }
        });

    }
    private boolean startingMatch = false;

    private void startMatch() {
        fragmentChatLobby.addSystemMessage("Match started");
        //simulate match
        //finish();
        Intent intent = new Intent(this, GameActivity.class);
        startActivity(intent);
    }

    private Thread count_down_thread = null;

    private int count_down = 0;

    private void countDown() {
        startingMatch = true;
        count_down = 0;
        count_down_thread = new Thread(new Runnable() {
            @Override
            public void run() {

                while(startingMatch && count_down < 10){
                    try {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                count_down++;
                                if(count_down != 10) {
                                    fragmentChatLobby.addSystemMessage("start in " + (10-count_down) + " seconds");
                                    ConnectionManager.getInstance().postMessage(MessageFactory.getInstance().createSystemMessage("start in " + (10-count_down) + " seconds", 2));
                                }else{

                                    //signal match starts
                                    ConnectionManager.getInstance().postMessage(MessageFactory.getInstance().createSystemMessage("", 1));
                                }
                            }
                        });

                        Thread.sleep(1000);

                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        count_down_thread.start();
    }

    private void exqListener() {
        // start listening to incoming messages


        if (!MessageDispatcher.getInstance().isAlive()) {
            MessageDispatcher.getInstance().start();
        }

        if (!ConnectionManager.getInstance().isListening()) {
            ConnectionManager.getInstance().startReceiving();
        }

        //bugs
        MessageDispatcher.getInstance().subscribe(chatMsgHandler, 0);
        MessageDispatcher.getInstance().subscribe(systemMsgHandler, 1);

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
                            startingMatch = false;
                        }
                    }
                });
            }

            @Override
            public void onWeakConnection() {
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        if (!isFinishing()) {  // Check if the activity is finishing
//                            AlertDialog.Builder builder = new AlertDialog.Builder(GameLobbyActivity.this);
//                            builder.setTitle("Weak connection");
//                            builder.setCancelable(false);  // Prevent the dialog from being canceled by clicking outside
//                            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
//                                @Override
//                                public void onClick(DialogInterface dialog, int which) {
//                                    // Handle the OK button click
//                                    dialog.dismiss();
//                                    // You can finish the activity or take other actions here
//                                    //finish();
//                                }
//                            });
//                            AlertDialog dialog = builder.create();
//                            dialog.show();
//                        }
//                    }
//                });
            }
        });
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
    protected void onDestroy() {
        MessageDispatcher.getInstance().unsubscribe(chatMsgHandler, 0);
        MessageDispatcher.getInstance().unsubscribe(systemMsgHandler, 1);
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.btnQuit){
            //send left message

            ConnectionManager.getInstance().postMessage(MessageFactory.getInstance().createSystemMessage("", 3));
            if(count_down_thread!=null){
                while(count_down_thread.isAlive()){
                    startingMatch = false;
                    try {
                        //wait for thread to finish
                        Thread.sleep(200);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt(); // Re-interrupt the current thread
                        break;
                    }
                }
            }

            while(ConnectionManager.getInstance().isSending()){
                try {
                    //wait for thread to finish
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt(); // Re-interrupt the current thread
                    break;
                }
            }

            ConnectionManager.getInstance().closeConnection();
            finish();

        }else if (v.getId() == R.id.btnReady){
            if(!isReady){
                btnReady.setPadding(0, 0, 0, 0);
                btnReady.setText("We are ready!");
                btnReady.setTextSize(16);
                informPeerReady(true);
                fragmentChatLobby.addSystemMessage("You are ready");
                isReady = true;
            }else{
                btnReady.setText("Ready!!!");
                btnReady.setPadding(30, 30, 30, 30);
                btnReady.setTextSize(30);
                informPeerReady(false);
                fragmentChatLobby.addSystemMessage("You are not ready");
                isReady = false;
                startingMatch = false;
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
        if(isReady) {
            ConnectionManager.getInstance().postMessage(MessageFactory.getInstance().createSystemMessage(ready_msg, 0));
        }
        else {
            ConnectionManager.getInstance().postMessage(MessageFactory.getInstance().createSystemMessage(not_ready_sg, 0));
        }
    }

    private Handler chatMsgHandler = null;
    private Handler systemMsgHandler = null;
    private View screenView;
    private Fragment fragmentSkinSelection;
    private UIManager uiManager;
    private FragmentManager fm;
    private Button btnQuit, btnReady;
    private FragmentChatLobby fragmentChatLobby;
    private Resources resources;
    private boolean isReady = false;
    private final static String TAG = "GameLobbyActivity";
    private final String ready_msg = "I am ready";
    private final String not_ready_sg = "I am not ready";

}
