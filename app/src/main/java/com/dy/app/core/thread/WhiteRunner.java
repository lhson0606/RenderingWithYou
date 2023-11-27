package com.dy.app.core.thread;

import android.util.Log;

import com.dy.app.gameplay.board.Board;
import com.dy.app.gameplay.board.Tile;
import com.dy.app.gameplay.move.ChessMove;
import com.dy.app.gameplay.piece.Piece;

import java.util.Locale;
import java.util.Vector;
import java.util.concurrent.Semaphore;

public class WhiteRunner extends Thread{
    private Vector<ScriptsRunner.Move> moves;
    private boolean isRunning = true;
    private Board board;
    private Semaphore whiteSem;
    private Semaphore blackSem;

    public WhiteRunner(Vector<ScriptsRunner.Move> moves, Board board, Semaphore whiteSem, Semaphore blackSem){
        this.moves = moves;
        this.board = board;
        this.whiteSem = whiteSem;
        this.blackSem = blackSem;
    }

    @Override
    public void run() {
        ScriptsRunner.Move currentMove = null;
        int i = 0;

        while(isRunning){
            try{
                whiteSem.acquire();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            if(i == moves.size()){
                blackSem.release();
                break;
            }

            currentMove = moves.get(i);
            i++;
            String moveData = currentMove.white;
            performMove(moveData);

            //release black
            blackSem.release();
        }

        int moveCount = board.getMoveCount()-1;
        for(int k = moveCount; k >= 0; k--){
            try {
                Thread.sleep(0);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            board.goToMove(k);
        }
        board.goToMove(500);
        board.goToMove(0);
        board.goToMove(60);
        board.goToMove(50);
        board.goToMove(80);
    }

    private void performMove(String moveData) {
        ChessMove move = null;
        try {
            move = new ChessMove(true, moveData, board);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        Tile srcTile = move.getSrcTile();
        Piece piece = srcTile.getPiece();
        try {
            Thread.sleep(0);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        piece.pickUp();
        try {
            Thread.sleep(0);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        piece.putDown();
        //piece.move(desTile.pos);
        try {
            board.moveByNotation(moveData, true);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void stopRunning(){
        isRunning = false;
    }
}
