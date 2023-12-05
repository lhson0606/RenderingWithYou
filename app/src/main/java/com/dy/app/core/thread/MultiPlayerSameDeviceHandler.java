package com.dy.app.core.thread;

import com.dy.app.activity.MultiPlayerOnSameDeviceActivity;
import com.dy.app.gameplay.algebraicNotation.AlgebraicChessInterpreter;
import com.dy.app.gameplay.board.Board;
import com.dy.app.gameplay.board.Tile;
import com.dy.app.gameplay.move.ChessMove;
import com.dy.app.gameplay.player.Player;
import com.dy.app.gameplay.player.Rival;
import com.dy.app.graphic.listener.TilePicker;
import com.dy.app.manager.ConnectionManager;

public class MultiPlayerSameDeviceHandler extends Thread
implements TilePicker.TilePickerListener{
    final Player player = Player.getInstance();
    final Board board;
    private boolean isRunning = false;
    //15 mins
    private long blackTimeRemainingMS = 3000;
    //private long blackTimeRemainingMS = 15 * 60 * 1000;
    private long whiteTimeRemainingMS = 15 * 60 * 1000;
    private MultiPlayerOnSameDeviceActivity activity;
    private Boolean gameEnd = false;
    private TilePicker tilePicker;

    @Override
    public void run() {
        isRunning = true;

        while(isRunning){
            if(player.isWhitePiece()){
                whiteTimeRemainingMS -= 1000;
                activity.updateTimeRemain(player.isWhitePiece(), whiteTimeRemainingMS);
            } else {
                blackTimeRemainingMS -= 1000;
                activity.updateTimeRemain(player.isWhitePiece(), blackTimeRemainingMS);
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                //throw new RuntimeException(e);
            }

            checkForTimeWin();
        }
    }

    private void handleFinalMove(String moveNotation){
        try {
            board.moveByNotation(moveNotation, player.isWhitePiece());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        int testResult = board.testForCheck(!player.isWhitePiece());
        if(testResult == Board.IS_CHECK){
            moveNotation += "+";
        }else if(testResult == Board.IS_CHECKMATE){
            moveNotation += "#";
        }
        //check for checkmate
        if(moveNotation.contains("#")){
            synchronized (gameEnd){
                if(gameEnd){
                    return;
                }
                endGame(player.isWhitePiece());
            }
            return;
        }
        player.setWhitePiece(!player.isWhitePiece());
    }

    private boolean checkForSpecialMove(String moveNotation) {
        final String promotionMoveNotation = moveNotation;
        //promotion move
        if(moveNotation.contains("=")){
            //call main thread to show promotion dialog, note that we are on the ui thread
            activity.showPromotionDialog((pieceName) -> {
                String tempMoveNotation = promotionMoveNotation.replace("?", pieceName);
                handleFinalMove(tempMoveNotation);
            });
            return true;
        }
        return false;
    }

    public MultiPlayerSameDeviceHandler(MultiPlayerOnSameDeviceActivity multiPlayerOnSameDeviceActivity, Board board, TilePicker tilePicker) {
        player.setInTurn(true);
        player.setWhitePiece(true);
        this.board = board;
        tilePicker.setListener(this);
        this.activity = multiPlayerOnSameDeviceActivity;
        this.tilePicker = tilePicker;
        init();
    }

    public void init(){
        activity.updateTimeRemain(true, whiteTimeRemainingMS);
        activity.updateTimeRemain(false, blackTimeRemainingMS);
    }

    @Override
    public void onMoveDetected(Tile src, Tile dst) {
        String moveNotation = AlgebraicChessInterpreter.convertToAlgebraicNotation(board, src, dst);
        if(checkForSpecialMove(moveNotation)){
            return;
        }
        handleFinalMove(moveNotation);
    }

    private void checkForTimeWin(){
        if(whiteTimeRemainingMS <= 0){
            endGame(false);
        }else if(blackTimeRemainingMS <= 0){
            endGame(true);
        }
    }

    public void endGame(boolean isWhiteWin){
        synchronized (gameEnd) {
            if (gameEnd) {
                return;
            }
            gameEnd = true;
            isRunning = false;
            activity.onGameResult(isWhiteWin);
            tilePicker.setListener(null);
        }
    }
}
