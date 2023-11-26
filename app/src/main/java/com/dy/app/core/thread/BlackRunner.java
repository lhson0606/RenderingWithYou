package com.dy.app.core.thread;

import android.util.Log;

import com.dy.app.gameplay.board.Board;
import com.dy.app.gameplay.board.Tile;
import com.dy.app.gameplay.move.ChessMove;
import com.dy.app.gameplay.piece.Piece;
import com.dy.app.graphic.model.Obj3D;

import java.util.Vector;
import java.util.concurrent.Semaphore;

public class BlackRunner extends Thread{
    private Vector<ScriptsRunner.Move> moves;
    private boolean isRunning = true;
    private Board board;
    private Semaphore whiteSem;
    private Semaphore blackSem;


    public BlackRunner(Vector<ScriptsRunner.Move> moves, Board board, Semaphore whiteSem, Semaphore blackSem){
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
                blackSem.acquire();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            if(i == moves.size()){
                whiteSem.release();
                break;
            }

            currentMove = moves.get(i);
            i++;
            String moveData = currentMove.black;
            performMove(moveData);

            //release white
            whiteSem.release();
        }
    }

    private void performMove(String moveData) {
        ChessMove move = null;
        try {
            move = new ChessMove(false, moveData, board);
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
        Tile desTile = move.getDesTile();
        piece.putDown();
        //piece.move(desTile.pos);
        try {
            board.moveByNotation(moveData, false);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void stopRunning(){
        isRunning = false;
    }

}
