package com.dy.app.gameplay.move;

import com.dy.app.common.maths.Vec2i;
import com.dy.app.gameplay.board.Board;
import com.dy.app.gameplay.board.Tile;

public class ChessMove {
    private String move;
    private Board board;
    private Vec2i srcTilePos;
    private Vec2i desTilePos;
    private boolean isWhiteMove;

    public ChessMove(boolean isWhiteMove, String move, Board board){
        this.move = move;
        this.board = board;
        this.isWhiteMove = isWhiteMove;
        interpretMove();
    }

    private void interpretMove() {
        if(isWhiteMove){
            interpretWhiteMove();
        }else{
            interpretBlackMove();
        }
    }

    private void interpretWhiteMove(){
        if(move.length() == 2) {
            desTilePos = new Vec2i(7 - (move.charAt(0) - 'a'), move.charAt(1) - '1');
            srcTilePos = new Vec2i(7 - (move.charAt(0) - 'a'), 1);
        }
    }

    private void interpretBlackMove(){
        if(move.length() == 2) {
            desTilePos = new Vec2i(7 - (move.charAt(0) - 'a'), move.charAt(1) - '1');
            srcTilePos = new Vec2i(7 - (move.charAt(0) - 'a'), 6);
        }
    }

    public Tile getSrcTile(){
        return board.getTile(srcTilePos);
    }

    public Tile getDesTile(){
        return board.getTile(desTilePos);
    }
}
