package com.dy.app.gameplay.piece;

import com.dy.app.common.maths.Vec2i;
import com.dy.app.gameplay.board.Board;
import com.dy.app.gameplay.board.Tile;
import com.dy.app.gameplay.notation.ChessNotation;
import com.dy.app.graphic.model.Obj3D;

public class Rook extends Piece{

    public Rook(Tile tile, Obj3D obj, boolean onPlayerSide, PieceColor pieceColor, Board board){
        super(tile, obj, onPlayerSide, pieceColor, board);
    }

    @Override
    protected void updatePossibleMoves(){
        super.updatePossibleMoves();
        updatePossibleMovesStraight();
    }

    private void updatePossibleMovesStraight() {
        Vec2i pos = tile.pos;

        //x axis
        for(int i = 1; i < 8; i++){
            if(pos.x + i > 7) break;
            Tile tile = board.getTile(new Vec2i(pos.x + i, pos.y));
            if(tile.hasPiece()){
                if(!tile.getPiece().isTheSameColor(this))
                    possibleMoves.add(tile);
                break;
            }
            possibleMoves.add(tile);
        }
        for(int i = 1; i < 8; i++){
            if(pos.x - i < 0) break;
            Tile tile = board.getTile(new Vec2i(pos.x - i, pos.y));
            if(tile.hasPiece()){
                if(!tile.getPiece().isTheSameColor(this))
                    possibleMoves.add(tile);
                break;
            }
            possibleMoves.add(tile);
        }

        //y axis
        for(int i = 1; i < 8; i++){
            if(pos.y + i > 7) break;
            Tile tile = board.getTile(new Vec2i(pos.x, pos.y + i));
            if(tile.hasPiece()){
                if(!tile.getPiece().isTheSameColor(this))
                    possibleMoves.add(tile);
                break;
            }
            possibleMoves.add(tile);
        }
        for(int i = 1; i < 8; i++){
            if(pos.y - i <0) break;
            Tile tile = board.getTile(new Vec2i(pos.x, pos.y - i));
            if(tile.hasPiece()){
                if(!tile.getPiece().isTheSameColor(this))
                    possibleMoves.add(tile);
                break;
            }
            possibleMoves.add(tile);
        }
    }

    @Override
    public String getNotation(){
        return ChessNotation.ROOK;
    }
}
