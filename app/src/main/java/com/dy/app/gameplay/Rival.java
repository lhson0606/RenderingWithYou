package com.dy.app.gameplay;

import com.dy.app.utils.DyConst;

public class Rival {
    private boolean inTurn = true;
    private boolean whitePiece = true;
    private int pieceSkinIndex = 0;
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

    private static Player instance = null;

    public static Player getInstance() {
        if(instance != null) return instance;
        instance = new Player();
        return instance;
    }
}
