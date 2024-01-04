package com.dy.app.core.thread;

import com.dy.app.activity.MultiPlayerOnSameDeviceActivity;
import com.dy.app.gameplay.algebraicNotation.AlgebraicChessInterpreter;
import com.dy.app.gameplay.board.Board;
import com.dy.app.gameplay.board.Tile;
import com.dy.app.gameplay.move.ChessMove;
import com.dy.app.gameplay.player.Player;
import com.dy.app.gameplay.player.Rival;
import com.dy.app.graphic.camera.CameraEntity;
import com.dy.app.graphic.listener.TilePicker;
import com.dy.app.manager.ConnectionManager;
import com.dy.app.manager.SoundManager;
import com.dy.app.setting.GameSetting;
import com.dy.app.utils.DyConst;

public class MultiPlayerSameDeviceHandler extends Thread
implements TilePicker.TilePickerListener{
    final Player player = Player.getInstance();
    final Board board;
    private boolean isRunning = false;
    //15 mins
    //private long blackTimeRemainingMS = 3000;
    private long blackTimeRemainingMS = 15 * 60 * 1000;
    private long whiteTimeRemainingMS = 15 * 60 * 1000;
    private MultiPlayerOnSameDeviceActivity activity;
    private Boolean gameEnd = false;
    private TilePicker tilePicker;
    private int continuousWhiteCheckCount = 0;
    private int continuousBlackCheckCount = 0;

    @Override
    public void run() {
        isRunning = true;
        SoundManager.getInstance().playSound(activity, SoundManager.SoundType.GAME_START);

        while(isRunning){
            if(player.isWhitePiece()){
                whiteTimeRemainingMS -= 100;
                activity.updateTimeRemain(player.isWhitePiece(), whiteTimeRemainingMS);
            } else {
                blackTimeRemainingMS -= 100;
                activity.updateTimeRemain(player.isWhitePiece(), blackTimeRemainingMS);
            }
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                //throw new RuntimeException(e);
            }

            checkForTimeWin();
        }
    }

    private void checkForDrawByStaleMate(boolean isWhitePiece) {
        if(!board.hasPossibleMove(isWhitePiece)){
            endGame(DyConst.GAME_DRAW);
        }
    }

    private void handleFinalMove(String moveNotation){
        try {
            board.moveByNotation(moveNotation, player.isWhitePiece());
            board.changeKingColorInCheckState();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        int testResult = board.testForCheck(!player.isWhitePiece());

        if(testResult == Board.NO_CHECK){
            if(Player.getInstance().isWhitePiece()){
                continuousBlackCheckCount = 0;
            }else{
                continuousWhiteCheckCount = 0;
            }
        }
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
                endGame(player.isWhitePiece()? DyConst.GAME_WHITE_WIN:DyConst.GAME_BLACK_WIN);
            }
            return;
        }
        player.setWhitePiece(!player.isWhitePiece());
        checkForDrawByStaleMate(player.isWhitePiece());
        handleMoveSoundEffect(moveNotation);
        //do camera animation if needed
        if(GameSetting.getInstance().isCinematicCameraEnabled){
            CameraEntity.getInstance().setState(
                    player.isWhitePiece()? CameraEntity.CameraState.BLACK_TO_WHITE: CameraEntity.CameraState.WHITE_TO_BLACK
            );
        }
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

    @Override
    public void onNotPossibleMoveDetected() {
        SoundManager.getInstance().playSound(activity, SoundManager.SoundType.NOTIFY);
    }

    private void checkForTimeWin(){
        if(whiteTimeRemainingMS <= 0){
            endGame(DyConst.GAME_BLACK_WIN);
        }else if(blackTimeRemainingMS <= 0){
            endGame(DyConst.GAME_WHITE_WIN);
        }
    }

    public void endGame(int result){
        synchronized (gameEnd) {
            if (gameEnd) {
                return;
            }
            gameEnd = true;
            stopGame();
            SoundManager.getInstance().playSound(activity, SoundManager.SoundType.GAME_OVER);
            activity.onGameResult(result);
        }
    }

    public void stopGame(){
        isRunning = false;
        tilePicker.setListener(null);
    }

    public void handleMoveSoundEffect(String moveNotation){
        if(moveNotation.contains("+")){
            SoundManager.getInstance().playSound(activity, SoundManager.SoundType.MOVE_CHECK);
        } else if(moveNotation.contains("x")){
            SoundManager.getInstance().playSound(activity, SoundManager.SoundType.CAPTURE);
        } else if(moveNotation.contains("O-O")){
            SoundManager.getInstance().playSound(activity, SoundManager.SoundType.CASTLE);
        } else if(moveNotation.contains("=")){
            SoundManager.getInstance().playSound(activity, SoundManager.SoundType.PROMOTE);
        } else {
            SoundManager.getInstance().playSound(activity, SoundManager.SoundType.MOVE_SELF);
        }
    }
}
