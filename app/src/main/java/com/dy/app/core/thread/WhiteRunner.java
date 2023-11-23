package com.dy.app.core.thread;

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

            if(i>1) break;
            currentMove = moves.get(i);
            i++;
            String moveData = currentMove.white;
            performMove(moveData);

            //release black
            blackSem.release();
        }
    }

    private void performMove(String moveData) {
        ChessMove move = new ChessMove(true, moveData, board);
        Tile srcTile = move.getSrcTile();
        Piece piece = srcTile.getPiece();
        piece.pickUp();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        Tile desTile = move.getDesTile();
        piece.putDown();
        piece.move(desTile.pos);
    }

    public void stopRunning(){
        isRunning = false;
    }
}
