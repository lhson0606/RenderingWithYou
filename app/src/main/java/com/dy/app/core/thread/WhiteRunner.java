package com.dy.app.core.thread;

import android.util.Log;

import com.dy.app.gameplay.board.Board;
import com.dy.app.gameplay.board.Tile;
import com.dy.app.gameplay.move.ChessMove;
import com.dy.app.gameplay.piece.Piece;
import com.dy.app.utils.Utils;

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

//        try {
//            Thread.sleep(2000);
//        } catch (InterruptedException e) {
//            throw new RuntimeException(e);
//        }

        for(Piece piece : board.getPieceManager().getAllPieces()){
            if(!piece.isInitialized){
                throw new RuntimeException("Piece not initialized");
            }
        }

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

        int moveCount = board.getMoveCount();

        for(Piece piece : board.getPieceManager().getAllPieces()){
            if(!piece.isInitialized){
                throw new RuntimeException("Piece not initialized");
            }
        }

        board.goToMove(0);
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
//        for(int k = 0; k<moveCount; k++){
//            board.goToMove(k);
//            try {
//                Thread.sleep(1000);
//            } catch (InterruptedException e) {
//                throw new RuntimeException(e);
//            }
//        }
        //board.goToMove(150);
//        for(int k = moveCount; k >= 0; k--){
//            board.goToMove(k);
//        }
        //board.goToMove(67*2+2);
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
