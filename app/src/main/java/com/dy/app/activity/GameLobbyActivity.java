package com.dy.app.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.dy.app.R;
import com.dy.app.gameplay.Player;
import com.dy.app.gameplay.PlayerProfile;
import com.dy.app.gameplay.Rival;
import com.dy.app.manager.ConnectionManager;
import com.dy.app.manager.UIManager;
import com.dy.app.network.IMessageHandler;
import com.dy.app.network.Message;
import com.dy.app.network.MessageCode;
import com.dy.app.network.PlayerInitialInfo;
import com.dy.app.ui.view.FragmentChatLobby;
import com.dy.app.utils.MessageFactory;
import com.dy.app.utils.Utils;

import java.util.Vector;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

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

        ft.add(R.id.flChatWindow,fragmentChatLobby, FragmentChatLobby.TAG);
        ft.show(fragmentChatLobby);

        ft.commit();

    }

    private void init(){
        uiManager = UIManager.getInstance();
        fragmentChatLobby = (FragmentChatLobby) UIManager.getInstance().getUI(UIManager.UIType.CHAT);
        btnQuit = (Button)findViewById(R.id.btnQuit);
        btnQuit.setOnClickListener(this);
        btnReady = (Button)findViewById(R.id.btnReady);
        btnReady.setOnClickListener(this);
        btnReady.setText("Ready!!!");
        btnReady.setPadding(30, 30, 30, 30);
        btnReady.setTextSize(30);
        messageHandler = new MessageHandler();
        //send player initial data
        sendPlayerInitialData();
    }

    private void startMatch() {
        //simulate match
        //finish();
        Intent intent = new Intent(this, GameActivity.class);
        startActivity(intent);
    }

    private void exqListener() {
        // start listening to incoming messages
        ConnectionManager.getInstance().startReceiving(messageHandler);
        messageHandler.start();
    }

    private void handleMessage(Message msg) {
        final int code = msg.getCode();
        final byte[] data = msg.getData();

        switch (code){
            case MessageCode.PLAYER_INITIAL_MESSAGE:
                //deserialize data
                PlayerInitialInfo info = (PlayerInitialInfo) Utils.deserialize(data);
                Rival.getInstance().setName(info.getName());
                break;
            case MessageCode.PLAYER_CHAT_MESSAGE_CODE:
                //get string message and send it to add it to chat window
                String strnMessage = new String(data, 0, msg.getLength());
                //make sure that we change UI in UI thread
                runOnUiThread(()-> {
                    fragmentChatLobby.onMsgFromMain(ConnectionManager.TAG, FragmentChatLobby.ADD_PLAYER_MESSAGE, strnMessage, null);
                });

                break;
            case MessageCode.SYSTEM_MESSAGE_CODE:
                //get string message and send it to add it to chat window
                String strMessage = new String(data, 0, msg.getLength());
                //make sure that we change UI in UI thread
                runOnUiThread(()-> {
                    fragmentChatLobby.onMsgFromMain(ConnectionManager.TAG, FragmentChatLobby.ADD_SYSTEM_MESSAGE, strMessage, null);
                });

                break;
        }
    }

    private void sendPlayerInitialData(){
        //create player initial info object
        PlayerInitialInfo info = new PlayerInitialInfo();
        info.setName(Player.getInstance().profile.get(PlayerProfile.KEY_USERNAME).toString());
        //convert to byte array
        byte[] data = Utils.serialize(info);
        //create message
        Message msg = MessageFactory.getInstance().createDataMessage(data, MessageCode.PLAYER_INITIAL_MESSAGE);
        //send message
        ConnectionManager.getInstance().postMessage(msg);
    }

    private class MessageHandler extends Thread implements IMessageHandler{
        private final ReentrantLock lock = new ReentrantLock();
        private final Condition condition = lock.newCondition();
        private boolean isRunning = false;
        private Vector<Message> messages;
        public MessageHandler(){
            messages = new Vector<>();
        }

        @Override
        public void run() {
            isRunning = true;
            Vector<Message> processingMessages = new Vector<>();

            while(isRunning){
                //check if there is any message
                lock.lock();
                processingMessages.clear();
                try{
                    //use while instead of if to guard against spurious wake up
                    while(messages.size() == 0 && isRunning){
                        condition.await();
                    }

                    //copy messages to processingMessages
                    processingMessages.addAll(messages);
                    messages.clear();

                } catch (InterruptedException e) {
                    //#todo
                } finally {
                    lock.unlock();
                }

                //process messages
                for (Message msg:processingMessages){
                    handleMessage(msg);
                }
            }

            Log.d(TAG, "run: message handler thread closed");
        }

        @Override
        public void onNewMessageArrive(Message msg) {
            lock.lock();
            try{
                messages.add(msg);
                condition.signal();
            }finally {
                lock.unlock();
            }
        }

        public void close(){
            isRunning = false;
            interrupt();
        }
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
        messageHandler.close();
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.btnQuit){
            showConfirmQuitDialog();
        }else if (v.getId() == R.id.btnReady){
            if(!Player.getInstance().isReady()){
                btnReady.setPadding(0, 0, 0, 0);
                btnReady.setText("We are ready!");
                btnReady.setTextSize(16);
                Player.getInstance().setReady(true);
            }else{
                btnReady.setText("Ready!!!");
                btnReady.setPadding(30, 30, 30, 30);
                btnReady.setTextSize(30);
                Player.getInstance().setReady(false);
            }

        }
    }

    private void quit(){
        ConnectionManager.getInstance().closeConnection();
        //close message handler
        messageHandler.close();
        finish();
    }

    private void showConfirmQuitDialog(){
        new AlertDialog.Builder(this)
                .setTitle("Confirmation")
                .setMessage("Do you want to exit this lobby?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Perform any necessary actions here before exiting
                        // For example, you can call super.onBackPressed() to navigate back
                        quit();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // User chose not to exit, so do nothing
                    }
                })
                .show();
    }

    @Override
    public void onBackPressed() {

        showConfirmQuitDialog();

        //to prevent compiler from complaining
        if(false)
            super.onBackPressed();
    }

    private Fragment fragmentSkinSelection;
    private UIManager uiManager;
    private FragmentManager fm;
    private Button btnQuit, btnReady;
    private FragmentChatLobby fragmentChatLobby;
    private final static String TAG = "GameLobbyActivity";
    private final String ready_msg = "I am ready";
    private final String not_ready_sg = "I am not ready";
    private MessageHandler messageHandler;

}
