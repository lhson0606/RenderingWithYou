package com.dy.app.gameplay.piece;

import com.dy.app.common.maths.Vec2i;
import com.dy.app.gameplay.board.Board;
import com.dy.app.gameplay.board.Tile;
import com.dy.app.graphic.model.Obj3D;

public class King extends Piece{
    boolean hasMoved = false;
    boolean inCheck = false;
    boolean inCheckMate = false;
    boolean inStaleMate = false;

    public King(Tile tile, Obj3D obj, boolean onPlayerSide, PieceColor pieceColor){
        super(tile, obj, onPlayerSide, pieceColor);
    }

    @Override
    public void updatePossibleMoves(){
        super.updatePossibleMoves();
        Vec2i pos = tile.pos;

        for(int i = -1; i < 2; i++){
            for(int j = -1; j < 2; j++){
                if(pos.x + i < 0 || pos.x + i > 7 || pos.y + j < 0 || pos.y + j > 7) continue;
                Tile tile = Board.getInstance().getTile(new Vec2i(pos.x + i, pos.y + j));
                if(tile.hasPiece()){
                    if(!tile.getPiece().isTheSameColor(this))
                        possibleMoves.add(tile);
                    continue;
                }
                possibleMoves.add(tile);
            }
        }
    }

    public void performCastle() {

    }
}
