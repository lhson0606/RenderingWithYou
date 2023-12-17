package com.dy.app.core.thread;

import android.util.Log;
import android.widget.RelativeLayout;

import com.dy.app.activity.RunScriptsActivity;
import com.dy.app.gameplay.board.Board;
import com.dy.app.gameplay.board.Tile;
import com.dy.app.gameplay.move.ChessMove;
import com.dy.app.gameplay.pgn.PGNFile;
import com.dy.app.gameplay.pgn.PGNMove;
import com.dy.app.gameplay.piece.Piece;
import com.dy.app.setting.GameSetting;
import com.dy.app.ui.dialog.MoveControlPanel;

import java.util.Vector;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class ScriptsRunner extends Thread{

    public static final String TAG = "ScriptsRunner";
    private Board board;
    private PGNFile pgnFile;
    boolean isRunning = false;
    IScriptRunnerCallback activity;
    private boolean isPaused = false;
    private int currentMove = 0;
    private final ReentrantLock moveLock = new ReentrantLock();
    private final Condition resumeCondition = moveLock.newCondition();

    public PGNFile getRunningPGNFile() {
        return pgnFile;
    }

    public interface IScriptRunnerCallback{
        void updateProgress(int progress);
        void changePlayButtonToContinue();
        void exitWithError(String message);
    }

    public ScriptsRunner(IScriptRunnerCallback activity, PGNFile pgnFile, Board board){
        this.activity = activity;
        this.pgnFile = pgnFile;
        this.board = board;
    }

    @Override
    public void run() {
        isRunning = true;

        try {
            loadAllMove();
        } catch (Exception e) {
            //throw new RuntimeException(e);
            activity.exitWithError("Cannot load moves");
            return;
        }
        Vector<PGNMove> moves = pgnFile.getMoves();

        jumpToMove(0);

        while(isRunning){
            while(currentMove < pgnFile.getBothSideMoveCount() && isRunning) {
                try{
                    Log.d("Debug concurrent", "worker is waiting for mutex");
                    moveLock.lock();
                    Log.d("Debug concurrent", "Mutex acquired!");
                    Log.d("Debug concurrent", "<");
                    currentMove++;
                    activity.updateProgress(currentMove);
                    boolean isWhite = currentMove%2 == 1;
                    PGNMove move =isWhite? moves.get((currentMove-1)/2) : moves.get(currentMove/2 - 1);
                    ChessMove chessMove = null;
                    String moveNotation = isWhite? move.white : move.black;
                    Log.d("Debug concurrent", "move " + currentMove + " " + moveNotation);
                    try {
                        chessMove = new ChessMove(isWhite, moveNotation, board);
                    } catch (Exception e) {
                        Log.d(TAG, "at move " + currentMove + " " + moveNotation);
                        Log.d(TAG, board.toString());
                        //throw new RuntimeException(e);
                        activity.exitWithError("Invalid move notation: " + moveNotation);
                        isPaused = true;
                        break;
                    }
                    Piece piece = chessMove.getSrcTile().getPiece();
                    try {
                        board.moveByNotation(moveNotation, isWhite);
                        Log.d("Debug concurrent", "Worker: on board state update: ");
                        Log.d("Debug concurrent", board.toString());
                    } catch (Exception e) {
                        Log.d(TAG, "at move " + currentMove + " " + moveNotation);
                        Log.d(TAG, board.toString());
                        //throw new RuntimeException(e);
                        activity.exitWithError("Invalid move notation: " + moveNotation);
                        isPaused = true;
                        break;
                    }
                    Log.d("Debug concurrent", ">");

                    if(isPaused){
                        try {
                            resumeCondition.await();
                        } catch (InterruptedException e) {
                            //no need to handle
                        }
                    }

                }finally {
                    if(isPaused){
                        Log.d(TAG, "Worker: paused and released mutex");
                    }
                    moveLock.unlock();
                }

                try {
                    Thread.sleep(GameSetting.getInstance().getPlaybackSpeed());
                } catch (InterruptedException e) {
                    //no need to handle
                }
            }

            try{
                moveLock.lock();
                Log.d(TAG, "run: waiting for resume");
                activity.changePlayButtonToContinue();
                resumeCondition.await();
            } catch (InterruptedException e) {
                //no need to handle
            } finally {
                moveLock.unlock();
            }
        }

        Log.d(TAG, " run: stopped");
    }

    public void resumePlaying(){
        try {
            moveLock.lock();
            isPaused = false;
            //if it's the end of the game, jump to the beginning for replay
            if(currentMove == pgnFile.getBothSideMoveCount()){
                jumpToMove(0);
            }

            resumeCondition.signal();
        }finally {
            moveLock.unlock();
        }
    }

    public void pausePlaying(){
        moveLock.lock();
        isPaused = true;
        moveLock.unlock();
    }

    private void loadAllMove() throws Exception {
        Vector<PGNMove> moves = pgnFile.getMoves();
        for(PGNMove move : moves){
            loadSingleMove(move.white, true);
            loadSingleMove(move.black, false);
        }
    }

    private void loadSingleMove(String moveData, boolean isWhite) throws Exception {
        if(moveData.equals("")) return;
        board.moveByNotation(moveData, isWhite);
    }

    public void jumpToMove(int move){
        try {
            moveLock.lock();
            Log.d("Debug concurrent", "[");
            try {
                board.goToMove(move);
            } catch (Exception e) {
                activity.exitWithError("Cannot jump to move " + move);
                //throw new RuntimeException(e);
            }
            Log.d("Debug concurrent", "User: on board state reset: ");
            Log.d("Debug concurrent", board.toString());
            Log.d(TAG, "received jump to move " + move);
            Log.d(TAG, "jumpToMove: from "+ currentMove + " to " + move);
            currentMove = move;
            Log.d(TAG, board.toString());
            Log.d("Debug concurrent", "]");
            activity.updateProgress(currentMove);
        }finally {
            moveLock.unlock();
        }
    }

    public void prevMove(){
        try {
            moveLock.lock();
            if(currentMove == 0){
                jumpToMove(pgnFile.getBothSideMoveCount());
                return;
            }
            jumpToMove(currentMove-1);
        }finally {
            moveLock.unlock();
        }
    }

    public void nextMove(){
        try {
            moveLock.lock();
            if(currentMove == pgnFile.getBothSideMoveCount()){
                jumpToMove(0);
                return;
            }
            jumpToMove(currentMove+1);
        }finally {
            moveLock.unlock();
        }
    }

    public boolean isPaused(){
        try{
            moveLock.lock();
            return isPaused;
        }finally {
            moveLock.unlock();
        }
    }

    public void close(){
        try{
            moveLock.lock();
            isRunning = false;
            resumeCondition.signal();
        }
        finally {
            moveLock.unlock();
        }
    }
}
