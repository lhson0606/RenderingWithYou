package com.dy.app.gameplay.piece;

import com.dy.app.gameplay.board.Tile;
import com.dy.app.graphic.model.Obj3D;

public class King extends Piece{
    boolean hasMoved = false;
    boolean inCheck = false;
    boolean inCheckMate = false;
    boolean inStaleMate = false;

    public King(Tile tile, Obj3D obj, boolean onPlayerSide) {
        super(tile, obj, onPlayerSide);
    }

    public void performCastle() {}
}
