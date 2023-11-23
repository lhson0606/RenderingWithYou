package com.dy.app.gameplay.piece;

import com.dy.app.common.maths.Vec2i;
import com.dy.app.gameplay.board.Board;
import com.dy.app.gameplay.board.Tile;
import com.dy.app.gameplay.notation.ChessNotation;
import com.dy.app.graphic.model.Obj3D;

public class Knight extends Piece{

    public Knight(Tile tile, Obj3D obj, boolean onPlayerSide, PieceColor pieceColor, Board board){
        super(tile, obj, onPlayerSide, pieceColor, board);
    }

    public static final Vec2i[] possibleTranslation = {
            new Vec2i(1, 2),
            new Vec2i(2, 1),
            new Vec2i(-1, 2),
            new Vec2i(-2, 1),
            new Vec2i(1, -2),
            new Vec2i(2, -1),
            new Vec2i(-1, -2),
            new Vec2i(-2, -1)
    };

    @Override
    public void updatePossibleMoves(){
        super.updatePossibleMoves();
        Vec2i pos = tile.pos;

        for(Vec2i translation : possibleTranslation){
            if(pos.x + translation.x < 0 || pos.x + translation.x > 7 || pos.y + translation.y < 0 || pos.y + translation.y > 7) continue;
            Tile tile = board.getTile(new Vec2i(pos.x + translation.x, pos.y + translation.y));
            if(tile.hasPiece()){
                if(!tile.getPiece().isOnPlayerSide())
                    possibleMoves.add(tile);
                continue;
            }
            possibleMoves.add(tile);
        }
    }

    @Override
    public String getNotation(){
        return ChessNotation.KNIGHT;
    }
}
