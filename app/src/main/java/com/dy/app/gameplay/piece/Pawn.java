package com.dy.app.gameplay.piece;

import android.util.Log;

import com.dy.app.common.maths.Vec2i;
import com.dy.app.gameplay.board.Board;
import com.dy.app.gameplay.board.Tile;
import com.dy.app.graphic.model.Obj3D;

import java.util.Vector;

public class Pawn extends Piece{
    boolean hasMoved = false;

    public Pawn(Tile tile, Obj3D obj, boolean onPlayerSide, PieceColor pieceColor){
        super(tile, obj, onPlayerSide, pieceColor);
    }

    @Override
    public Tile move(Vec2i pos){
        hasMoved = true;
        return super.move(pos);
    }

    @Override
    protected void updatePossibleMoves(){
        super.updatePossibleMoves();
       if(isBlack()){
           getPossibleMovesBlack();
       }else{
           getPossibleMovesWhite();
       }

    }

    private void getPossibleMovesBlack(){
        Vec2i pos = tile.pos;
        Board board = Board.getInstance();
        Tile tile = null;

        //check if pawn is on the edge of the board
        if(pos.y<1){
            return;
        }

        //forward
        //check if first step is possible
        tile = board.getTile(new Vec2i(pos.x, pos.y - 1));
        if(!tile.hasPiece()){
            possibleMoves.add(tile);
            if(!hasMoved){
                tile = board.getTile(new Vec2i(pos.x, pos.y - 2));
                if(!tile.hasPiece()){
                    possibleMoves.add(tile);
                }
            }
        }

        //possible capturing moves
        if(pos.x != 0){
            tile = board.getTile(new Vec2i(pos.x - 1, pos.y - 1));
            if(tile.hasPiece() && !tile.getPiece().isTheSameColor(this)){
                possibleMoves.add(tile);
            }
        }

        if(pos.x != 7){
            tile = board.getTile(new Vec2i(pos.x + 1, pos.y - 1));
            if(tile.hasPiece() && !tile.getPiece().isTheSameColor(this)){
                possibleMoves.add(tile);
            }
        }

    }

    private void getPossibleMovesWhite(){
        Vec2i pos = tile.pos;
        Board board = Board.getInstance();
        Tile tile = null;

        if(pos.y>6){
            return;
        }

        //forward
        //check if first step is possible
        tile = board.getTile(new Vec2i(pos.x, pos.y + 1));
        if(!tile.hasPiece()){
            possibleMoves.add(tile);
            if(!hasMoved){
                tile = board.getTile(new Vec2i(pos.x, pos.y + 2));
                if(!tile.hasPiece()){
                    possibleMoves.add(tile);
                }
            }
        }

        //possible capturing moves
        if(pos.x != 0){
            tile = board.getTile(new Vec2i(pos.x - 1, pos.y + 1));
            if(tile.hasPiece() && !tile.getPiece().isTheSameColor(this)){
                possibleMoves.add(tile);
            }
        }

        if(pos.x != 7){
            tile = board.getTile(new Vec2i(pos.x + 1, pos.y + 1));
            if(tile.hasPiece() && !tile.getPiece().isTheSameColor(this)){
                possibleMoves.add(tile);
            }
        }
    }
}
