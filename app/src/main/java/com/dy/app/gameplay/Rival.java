package com.dy.app.gameplay;

import com.dy.app.utils.DyConst;

public class Rival {
    private boolean inTurn = true;
    private boolean whitePiece = true;
    private int pieceSkinIndex = 1;
    private int backgroundSkinIndex = 0;
    private int tileSkinIndex = 0;
    private int boardSkinIndex = 0;

    public boolean isInTurn() {
        return inTurn;
    }

    public boolean isWhitePiece() {
        return whitePiece;
    }

    public int getPieceSkinIndex() {
        return pieceSkinIndex;
    }

    public int getBoardSkinIndex() {
        return boardSkinIndex;
    }

    public String getPieceSkinPath(){
        if(isWhitePiece()){
            return DyConst.white_piece_tex_path + DyConst.piece_skins[pieceSkinIndex];
        }
        else{
            return DyConst.black_piece_tex_path + DyConst.piece_skins[pieceSkinIndex];
        }
    }

    public String getBoardSkinPath(){
        return DyConst.board_tex_path + DyConst.board_skins[boardSkinIndex];
    }

    public String getBlackTileSkinPath(){
        return DyConst.black_tile_tex_path + DyConst.tile_skins[tileSkinIndex];
    }

    public String getWhiteTileSkinPath(){
        return DyConst.white_tile_tex_path + DyConst.tile_skins[tileSkinIndex];
    }

    public String getBackgroundSkinPath(){
        return DyConst.background_tex_path + DyConst.back_ground_skins[backgroundSkinIndex];
    }

    private static Rival instance = null;

    public static Rival getInstance() {
        if(instance != null) return instance;
        instance = new Rival();
        return instance;
    }


    public void setWhitePiece(boolean whitePiece) {
        this.whitePiece = whitePiece;
    }

    public void setInTurn(boolean inTurn) {
        this.inTurn = inTurn;
    }

    public String getName() {
        return "Rival";
    }
}
