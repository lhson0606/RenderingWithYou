package com.dy.app.core.thread;

import android.util.Log;

import com.dy.app.activity.GameActivity;
import com.dy.app.gameplay.algebraicNotation.AlgebraicChessInterpreter;
import com.dy.app.gameplay.board.Board;
import com.dy.app.gameplay.board.Tile;
import com.dy.app.gameplay.player.Player;
import com.dy.app.gameplay.player.PlayerProfile;
import com.dy.app.gameplay.player.Rival;
import com.dy.app.graphic.listener.TilePicker;
import com.dy.app.manager.ConnectionManager;
import com.dy.app.network.IMessageHandler;
import com.dy.app.network.Message;
import com.dy.app.network.MessageCode;
import com.dy.app.ui.dialog.P2pGameResultDialog;
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
    private final Player player = Player.getInstance();
    private final long startDuration = 15 * 60 * 1000;//15 mins
    private long duration = startDuration;//15 mins
    private Timer timer;
    private long playerPromotionCount = 0;

    public MultiDeviceInGameHandler(GameActivity gameActivity, Board board, ConnectionManager connectionManager, TilePicker tilePicker){
        this.board = board;
        this.connectionManager = connectionManager;
        this.gameActivity = gameActivity;
        connectionManager.startReceiving(this);
        this.tilePicker = tilePicker;
        tilePicker.setListener(this);

        if(player.isWhitePiece()){
            player.setInTurn(true);
        }else{
            player.setInTurn(false);
        }

        timer = new Timer(duration, new Timer.OnTimerTickListener() {
            @Override
            public void onTimeUpdate(long timeRemaining) {
                gameActivity.updateTimeRemain(player.isWhitePiece(), timeRemaining);
                informRemainTime(timeRemaining);
                gameActivity.updatePlayerTimeRemain(timeRemaining);
            }

            @Override
            public void onTimeOut() {
                endGame(P2pGameResultDialog.LOSE);
                informLoss();
            }
        });
    }

    @Override
    public void onMoveDetected(Tile src, Tile dest) {
        //#todo
        String moveNotation = AlgebraicChessInterpreter.convertToAlgebraicNotation(board, src, dest);
        Log.d(TAG, "onMoveDetected: moveNotation: " + moveNotation);
        if(checkForSpecialMove(moveNotation)){
            return;
        }
        player.setInTurn(false);
        try {
            board.moveByNotation(moveNotation, player.isWhitePiece());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        //we need to add whether it's a check or checkmate
        int testResult = board.testForCheck(Rival.getInstance().isWhitePiece());
        if(testResult == Board.IS_CHECK){
            moveNotation += "+";
        }else if(testResult == Board.IS_CHECKMATE){
            moveNotation += "#";
        }
        //handle move by player
        handlePlayerMove(moveNotation);
    }

    private boolean checkForSpecialMove(String moveNotation) {
        final String promotionMoveNotation = moveNotation;
        //promotion move
        if(moveNotation.contains("=")){
            //call main thread to show promotion dialog, note that we are on the ui thread
            gameActivity.showPromotionDialog((pieceName) -> {
                String tempMoveNotation = promotionMoveNotation.replace("?", pieceName);
                try {
                    board.moveByNotation(tempMoveNotation, player.isWhitePiece());
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                int testResult = board.testForCheck(Rival.getInstance().isWhitePiece());
                if(testResult == Board.IS_CHECK){
                    tempMoveNotation += "+";
                }else if(testResult == Board.IS_CHECKMATE){
                    tempMoveNotation += "#";
                }
                playerPromotionCount++;
                //send move to other device
                handlePlayerMove(tempMoveNotation);
            });
            return true;
        }
        return false;
    }

    private void handleMessage(Message msg) {
        //#todo
        final int code = msg.getCode();
        final byte[] data = msg.getData();

        switch (code){
            case MessageCode.ON_PIECE_MOVE_REQUEST:
                handleMoveRequest(data);
                break;
            case MessageCode.PLAYER_CHAT_MESSAGE_CODE:
                handlePlayerChatMessage(data);
                break;
            case MessageCode.ON_TIME_REMAIN_INFO:
                long remainTime = Long.parseLong(new String(data));
                gameActivity.updateTimeRemain(Rival.getInstance().isWhitePiece(), remainTime);
                break;
            case MessageCode.ON_GAME_LOST_INFORM:
                //we are the winner
                endGame(P2pGameResultDialog.WIN);
                break;
            case MessageCode.ON_REMATCH_REQUEST:
                gameActivity.showRematchConfirmDialog();
                break;
            case MessageCode.ON_REMATCH_ACCEPTED:
                gameActivity.onRematchAccepted();
                break;
            case MessageCode.ON_REMATCH_REJECTED:
                gameActivity.onRematchRejected();
            break;
            case MessageCode.ON_DRAW_ACCEPTED:
                endGame(P2pGameResultDialog.DRAW);
                break;
            case MessageCode.ON_DRAW_REJECTED:
                gameActivity.onDrawRejected();
            break;
            case MessageCode.ON_DRAW_REQUEST:
                gameActivity.showDrawConfirmDialog();
                break;
            case MessageCode.ON_UNDO_REQUEST:
                if(board.isUndoAllowed(Rival.getInstance().isWhitePiece())) {
                    gameActivity.showUndoConfirmDialog();
                }else{
                    addSystemMsgBoth("Undo not allowed");
                }
            break;
            case MessageCode.ON_UNDO_ACCEPTED:
                try {
                    board.undoMove();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                //set in turn
                player.setInTurn(true);
                //resume timer
                timer.resumeTimer();
                addSystemMsgBoth("Undo accepted");
                gameActivity.onUndoAccepted();
            break;
            case MessageCode.ON_UNDO_REJECTED:
                gameActivity.onUndoRejected();
                addSystemMsgBoth("Undo rejected");
            break;
            case MessageCode.SYSTEM_MESSAGE_CODE:
                gameActivity.addSystemMessage(new String(data));
                break;
            default:
                throw new RuntimeException("Unknown message code: " + code);
        }
    }

    private void handlePlayerChatMessage(byte[] data) {
        gameActivity.addPeerMessage(new String(data));
    }

    private void handleMoveRequest(byte[] data) {
        String moveNotation = new String(data);
        player.setInTurn(true);
        Log.d(TAG, "handleMessage: moveNotation: " + moveNotation);
        try {
            board.moveByNotation(moveNotation, Rival.getInstance().isWhitePiece());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        //check for checkmate
        if(isCheckMate(moveNotation)){
            endGame(P2pGameResultDialog.LOSE);
            return;
        }
        //resume timer
        timer.resumeTimer();
    }

    private void handlePlayerMove(String moveNotation){
        //#todo
        //stop timer
        timer.pauseTimer();
        Message moveMessage = MessageFactory.getInstance().createDataMessage(moveNotation.getBytes(), MessageCode.ON_PIECE_MOVE_REQUEST);
        connectionManager.postMessage(moveMessage);
        player.setInTurn(false);

        //check for checkmate
        if(isCheckMate(moveNotation)){
            endGame(P2pGameResultDialog.WIN);
        }
    }

    private boolean isCheckMate(String moveNotation){
        return moveNotation.contains("#");
    }

    @Override
    public void run() {
        isRunning = true;
        Vector<Message> processingMessages = new Vector<>();
        timer.start();
        timer.pauseTimer();

        //update UI on start
        gameActivity.updateTimeRemain(player.isWhitePiece(), duration);
        gameActivity.updatePlayerTimeRemain(duration);
        informRemainTime(duration);

        if(player.isWhitePiece()) {
            timer.resumeTimer();
        }

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

    public void informRemainTime(long remainTime){
        Message remainTimeMessage = MessageFactory.getInstance().createDataMessage(String.valueOf(remainTime).getBytes(), MessageCode.ON_TIME_REMAIN_INFO);
        connectionManager.postMessage(remainTimeMessage);
    }

    public void informLoss(){
        Message loseMessage = MessageFactory.getInstance().createDataMessage("".getBytes(), MessageCode.ON_GAME_LOST_INFORM);
        connectionManager.postMessage(loseMessage);
    }

    public void shutDown(){
        interrupt();
        timer.stopTimer();
    }

    public void resign(){
        endGame(P2pGameResultDialog.LOSE);
        informLoss();
    }

    public void requestDraw(){
        Message drawMessage = MessageFactory.getInstance().createDataMessage("".getBytes(), MessageCode.ON_DRAW_REQUEST);
        connectionManager.postMessage(drawMessage);
    }

    public void acceptDraw() {
        Message drawMessage = MessageFactory.getInstance().createDataMessage("".getBytes(), MessageCode.ON_DRAW_ACCEPTED);
        connectionManager.postMessage(drawMessage);
        endGame(P2pGameResultDialog.DRAW);
    }

    public void rejectDraw() {
        Message drawMessage = MessageFactory.getInstance().createDataMessage("".getBytes(), MessageCode.ON_DRAW_REJECTED);
        connectionManager.postMessage(drawMessage);
    }

    private void endGame(int result){
        timer.stopTimer();
        gameActivity.onGameResult(result);
    }

    public void checkAndRequestUndo() {
        boolean isWhitePiece = player.isWhitePiece();
        boolean isUndoAllowed = board.isUndoAllowed(isWhitePiece);

        //if undo is not allowed, return
        if(!isUndoAllowed){
            gameActivity.informMessage("Undo not allowed right now");
            addSystemMsgBoth("Undo not allowed right now");
            return;
        }

        //else we send undo request to other device and wait for approval
        Message undoRequestMessage = MessageFactory.getInstance().createDataMessage("".getBytes(), MessageCode.ON_UNDO_REQUEST);
        connectionManager.postMessage(undoRequestMessage);
        addSystemMsgBoth(Player.getInstance().profile.get(PlayerProfile.KEY_USERNAME) + " requested undo");
    }

    public void acceptUndo() {
        //we need to undo the move and then send the undo accepted message
        try {
            board.undoMove();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        //set in turn to false
        player.setInTurn(false);
        tilePicker.putDownPiece();
        //pause timer
        timer.pauseTimer();
        Message undoAcceptedMessage = MessageFactory.getInstance().createDataMessage("".getBytes(), MessageCode.ON_UNDO_ACCEPTED);
        connectionManager.postMessage(undoAcceptedMessage);
    }

    public void rejectUndo() {
        Message undoRejectedMessage = MessageFactory.getInstance().createDataMessage("".getBytes(), MessageCode.ON_UNDO_REJECTED);
        connectionManager.postMessage(undoRejectedMessage);
    }

    public void addSystemMsgBoth(String msg){
        gameActivity.addSystemMessage(msg);
        Message systemMessage = MessageFactory.getInstance().createDataMessage(msg.getBytes(), MessageCode.SYSTEM_MESSAGE_CODE);
        connectionManager.postMessage(systemMessage);
    }

    public long getGameDuration() {
        return startDuration - duration;
    }

    public long getPromotionCount() {
        return playerPromotionCount;
    }
}
