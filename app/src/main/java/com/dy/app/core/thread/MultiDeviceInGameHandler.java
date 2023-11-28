package com.dy.app.core.thread;

import android.util.Log;

import com.dy.app.activity.GameActivity;
import com.dy.app.gameplay.board.Board;
import com.dy.app.gameplay.player.Rival;
import com.dy.app.graphic.listener.TilePicker;
import com.dy.app.manager.ConnectionManager;
import com.dy.app.network.IMessageHandler;
import com.dy.app.network.Message;
import com.dy.app.network.MessageCode;
import com.dy.app.utils.MessageFactory;

import java.util.Vector;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class MultiDeviceInGameHandler extends Thread
        implements IMessageHandler, TilePicker.TilePickerListener {
    private final Board board;
    private final ConnectionManager connectionManager;
    private boolean isRunning = false;
    private final GameActivity gameActivity;
    private final ReentrantLock lock = new ReentrantLock();
    private final Condition condition = lock.newCondition();
    private final Vector<Message> messages = new Vector<>();
    public static final String TAG = "MultiDeviceInGameHandler";
    private TilePicker tilePicker;

    public MultiDeviceInGameHandler(GameActivity gameActivity, Board board, ConnectionManager connectionManager, TilePicker tilePicker){
        this.board = board;
        this.connectionManager = connectionManager;
        this.gameActivity = gameActivity;
        connectionManager.startReceiving(this);
        this.tilePicker = tilePicker;
        tilePicker.setListener(this);
    }

    @Override
    public void onMoveDetected(String moveNotation) {
        //#todo
        Log.d(TAG, "onMoveDetected: moveNotation: " + moveNotation);
        //send move to other device
        sendMove(moveNotation);
    }

    private void handleMessage(Message msg) {
        //#todo
        final int code = msg.getCode();
        final byte[] data = msg.getData();

        String moveNotation = new String(data);
        Log.d(TAG, "handleMessage: moveNotation: " + moveNotation);
        try {
            board.moveByNotation(moveNotation, Rival.getInstance().isWhitePiece());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void sendMove(String moveNotation){
        //#todo
        Message moveMessage = MessageFactory.getInstance().createDataMessage(moveNotation.getBytes(), MessageCode.ON_PIECE_MOVE_REQUEST);
        connectionManager.postMessage(moveMessage);
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
                //use while instead of if to guard against spurious wake ups
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
