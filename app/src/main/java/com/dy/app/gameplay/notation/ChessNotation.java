package com.dy.app.gameplay.notation;

import com.dy.app.common.maths.Vec2i;
import com.dy.app.gameplay.piece.King;

public class ChessNotation {
    public static final String King = "K";
    public static final String Queen = "Q";
    public static final String Bishop = "B";
    public static final String Knight = "N";
    public static final String Rook = "R";
    public static final String Pawn = "";
    public static final String BoardPositions[][]={
            {"a1", "b1", "c1", "d1", "e1", "f1", "g1", "h1"},
            {"a2", "b2", "c2", "d2", "e2", "f2", "g2", "h2"},
            {"a3", "b3", "c3", "d3", "e3", "f3", "g3", "h3"},
            {"a4", "b4", "c4", "d4", "e4", "f4", "g4", "h4"},
            {"a5", "b5", "c5", "d5", "e5", "f5", "g5", "h5"},
            {"a6", "b6", "c6", "d6", "e6", "f6", "g6", "h6"},
            {"a7", "b7", "c7", "d7", "e7", "f7", "g7", "h7"},
            {"a8", "b8", "c8", "d8", "e8", "f8", "g8", "h8"},
    };

    public static Vec2i getPos(String pos){
        pos = pos.trim();
        if(pos.length()!=2){
            throw new RuntimeException("invalid pos");
        }

        int x = pos.charAt(0) - 'a';
        int y = pos.charAt(1) - '1';

        return new Vec2i(x, y);
    };
}
