package com.dy.app.gameplay.piece;

import com.dy.app.common.maths.Vec2i;
import com.dy.app.gameplay.board.Board;
import com.dy.app.gameplay.board.Tile;
import com.dy.app.graphic.model.Obj3D;

public class Bishop extends Piece{

    public Bishop(Tile tile, Obj3D obj, boolean onPlayerSide, PieceColor pieceColor){
        super(tile, obj, onPlayerSide, pieceColor);
    }
    @Override
    protected void updatePossibleMoves(){
        super.updatePossibleMoves();
        Vec2i pos = tile.pos;

        for(int i = 1; i < 8; i++){
            if(pos.x + i > 7 || pos.y + i > 7) break;
            Tile tile = Board.getInstance().getTile(new Vec2i(pos.x + i, pos.y + i));
            if(tile.hasPiece()){
                if(!tile.getPiece().isTheSameColor(this))
                    possibleMoves.add(tile);
                break;
            }
            possibleMoves.add(tile);
        }

        for(int i = 1; i<8; i++){
            if(pos.x - i < 0 || pos.y - i < 0) break;
            Tile tile = Board.getInstance().getTile(new Vec2i(pos.x - i, pos.y - i));
            if(tile.hasPiece()){
                if(!tile.getPiece().isTheSameColor(this))
                    possibleMoves.add(tile);
                break;
            }
            possibleMoves.add(tile);
        }

        for(int i = 1; i < 8; i++){
            if(pos.x + i > 7 || pos.y - i < 0) break;
            Tile tile = Board.getInstance().getTile(new Vec2i(pos.x + i, pos.y - i));
            if(tile.hasPiece()){
                if(!tile.getPiece().isTheSameColor(this))
                    possibleMoves.add(tile);
                break;
            }
            possibleMoves.add(tile);
        }

        for(int i = 1; i < 8; i++){
            if(pos.x - i < 0 || pos.y + i > 7) break;
            Tile tile = Board.getInstance().getTile(new Vec2i(pos.x - i, pos.y + i));
            if(tile.hasPiece()){
                if(!tile.getPiece().isTheSameColor(this))
                    possibleMoves.add(tile);
                break;
            }
            possibleMoves.add(tile);
        }
    }
}
