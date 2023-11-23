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
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.dy.app.R;
import com.dy.app.gameplay.player.Player;
import com.dy.app.gameplay.player.PlayerInventory;
import com.dy.app.gameplay.player.PlayerProfile;
import com.dy.app.gameplay.player.Rival;
import com.dy.app.manager.ConnectionManager;
import com.dy.app.manager.UIManager;
import com.dy.app.network.IMessageHandler;
import com.dy.app.network.Message;
import com.dy.app.network.MessageCode;
import com.dy.app.network.PlayerInitialInfo;
import com.dy.app.ui.view.FragmentChatLobby;
import com.dy.app.ui.view.FragmentSetting;
import com.dy.app.ui.view.FragmentSkinSelection;
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
    }

    private void attachFragment() {
        FragmentTransaction ft = fm.beginTransaction();

        ft.replace(R.id.flChatWindow,fragmentChatLobby, FragmentChatLobby.TAG);
        ft.show(fragmentChatLobby);
        //#todo
        ft.replace(R.id.flSkinSelection, fragmentSetting, FragmentSkinSelection.TAG);
        ft.show(fragmentSetting);

        ft.commit();
    }

    private void init(){
        uiManager = UIManager.getInstance();
        fragmentChatLobby = (FragmentChatLobby) UIManager.getInstance().getUI(UIManager.UIType.CHAT);
        fragmentSetting = (FragmentSetting) UIManager.getInstance().getUI(UIManager.UIType.CONFIG);
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
            case MessageCode.PLAYER_INITIAL_MESSAGE_CODE:
                //deserialize data
                PlayerInitialInfo info = (PlayerInitialInfo) Utils.deserialize(data);
                Rival rival = Rival.getInstance();
                rival.setName(info.getName());
                rival.setPieceSkinIndex(info.getPieceSkinIndex());
                rival.setBoardSkinIndex(info.getBoardSkinIndex());
                rival.setTileSkinIndex(info.getTileSkinIndex());
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
            case MessageCode.PLAYER_READY_STATUS_CODE:
                //deserialize data
                Boolean isReady = (Boolean) Utils.deserialize(data);
                Rival.getInstance().setReady(isReady);
                //update UI
                if(isReady){
                    runOnUiThread(()-> {
                        fragmentChatLobby.onMsgFromMain(ConnectionManager.TAG, FragmentChatLobby.ADD_SYSTEM_MESSAGE, Rival.getInstance().getName() + " is ready", null);
                    });
                }else{
                    runOnUiThread(()-> {
                        fragmentChatLobby.onMsgFromMain(ConnectionManager.TAG, FragmentChatLobby.ADD_SYSTEM_MESSAGE, Rival.getInstance().getName() + " is not ready", null);
                    });
                }
                //check if both players are ready
                if(Player.getInstance().isReady() && Rival.getInstance().isReady()){
                    //let host start the game
                    if(Player.getInstance().isHost()) {
                        startCountDown();
                    }else{//else request host to start the game
                        Message o = MessageFactory.getInstance().createDataMessage(null, MessageCode.REQUEST_START_GAME_COUNTDOWN_CODE);
                        ConnectionManager.getInstance().postMessage(o);
                    }
                }else {
                    //if count down has started, stop it
                    if(Player.getInstance().isHost()) {
                        countDownStarted = false;
                    }else{
                        //if we are not host, we should send a message to host to stop the count down
                        Message stopRequestMsg = MessageFactory.getInstance().createDataMessage(null, MessageCode.REQUEST_STOP_GAME_COUNTDOWN_CODE);
                        ConnectionManager.getInstance().postMessage(stopRequestMsg);
                    }

                }
                break;
            case MessageCode.REQUEST_START_GAME_COUNTDOWN_CODE:
                    //check if both players are ready
                    if(Player.getInstance().isReady() && Rival.getInstance().isReady()){
                        startCountDown();
                    }else if(countDownStarted){
                        //if count down has started, stop it
                        countDownStarted = false;
                    }
                    break;
            case MessageCode.REQUEST_STOP_GAME_COUNTDOWN_CODE:
                    //if count down has started, stop it
                    countDownStarted = false;
                    break;
            case MessageCode.START_GAME_CODE:
                    //start game
                    startMatch();
                    break;
        }
    }

    private void startCountDown() {
        if(countDownStarted) return;
        countDownStarted = true;
        CountDownThread thread = new CountDownThread();
        thread.start();
    }

    private class CountDownThread extends Thread{
        int countDownValue = 10;
        @Override
        public void run() {
            while (countDownValue > 0){

                if(!countDownStarted) {
                    addSystemMessageToPlayerAndPeer("Count down stopped");
                    break;
                }

                try {
                    Thread.sleep(1000);
                    countDownValue--;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                addSystemMessageToPlayerAndPeer("Game starts in " + countDownValue + " seconds");
            }

            if(countDownValue == 0) {
                //inform peer(client) to start the game
                Message startGameMsg = MessageFactory.getInstance().createDataMessage(null, MessageCode.START_GAME_CODE);
                ConnectionManager.getInstance().postMessage(startGameMsg);
                startMatch();
            }
        }
    }

    private void addSystemMessageToPlayerAndPeer(String message){
        //add message to chat window
        runOnUiThread(()->{
            fragmentChatLobby.onMsgFromMain(ConnectionManager.TAG, FragmentChatLobby.ADD_SYSTEM_MESSAGE, message, null);
        });
        //send message to peer
        Message msg = MessageFactory.getInstance().createDataMessage(message.getBytes(), MessageCode.SYSTEM_MESSAGE_CODE);
        ConnectionManager.getInstance().postMessage(msg);
    }

    private void sendPlayerInitialData(){
        //create player initial info object
        PlayerInitialInfo info = new PlayerInitialInfo();
        Player player = Player.getInstance();
        info.setName(player.profile.get(PlayerProfile.KEY_USERNAME).toString());
        info.setPieceSkinIndex((long)player.inventory.get(PlayerInventory.KEY_PIECE_SKIN_INDEX));
        info.setBoardSkinIndex((long)player.inventory.get(PlayerInventory.KEY_BOARD_SKIN_INDEX));
        info.setTileSkinIndex((long)player.inventory.get(PlayerInventory.KEY_TILE_SKIN_INDEX));
        //convert to byte array
        byte[] data = Utils.serialize(info);
        //create message
        Message msg = MessageFactory.getInstance().createDataMessage(data, MessageCode.PLAYER_INITIAL_MESSAGE_CODE);
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
                    isRunning = false;
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
        attachFragment();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.btnQuit){
            showConfirmQuitDialog();
        }else if (v.getId() == R.id.btnReady){
            boolean isReady = Player.getInstance().isReady();
            if(!isReady){
                btnReady.setPadding(0, 0, 0, 0);
                btnReady.setText("We are ready!");
                btnReady.setTextSize(16);
            }else{
                btnReady.setText("Ready!!!");
                btnReady.setPadding(30, 30, 30, 30);
                btnReady.setTextSize(30);
            }

            Player.getInstance().setReady(!isReady);
            informReadyStatus();
            //update UI
            String msg = Player.getInstance().isReady()?ready_msg:not_ready_msg;
            fragmentChatLobby.onMsgFromMain(TAG, FragmentChatLobby.ADD_SYSTEM_MESSAGE, msg, null);
        }
    }

    private void informReadyStatus() {
        Boolean isReady = Player.getInstance().isReady();
        byte[] data = Utils.serialize(isReady);
        Message msg = MessageFactory.getInstance().createDataMessage(data, MessageCode.PLAYER_READY_STATUS_CODE);
        ConnectionManager.getInstance().postMessage(msg);
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

    private UIManager uiManager;
    private FragmentManager fm;
    private Button btnQuit, btnReady;
    private FragmentChatLobby fragmentChatLobby;
    private FragmentSetting fragmentSetting;
    private final static String TAG = "GameLobbyActivity";
    private final String ready_msg = "You are ready";
    private final String not_ready_msg = "You are not ready";
    private MessageHandler messageHandler;
    private boolean countDownStarted = false;

}
