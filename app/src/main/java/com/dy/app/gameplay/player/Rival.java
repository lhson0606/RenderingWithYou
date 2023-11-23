package com.dy.app.gameplay.player;

import com.dy.app.utils.DyConst;
import com.dy.app.utils.Utils;

public class Rival {
    private boolean inTurn = true;
    private boolean whitePiece = false;
    private long pieceSkinIndex = 0;
    private long backgroundSkinIndex = 0;
    private long tileSkinIndex = 0;
    private long boardSkinIndex = 0;
    private String name = "Rival";
    private Boolean isReady = false;

    public boolean isInTurn() {
        return inTurn;
    }

    public boolean isWhitePiece() {
        return whitePiece;
    }

    public long getPieceSkinIndex() {
        return pieceSkinIndex;
    }

    public long getBoardSkinIndex() {
        return boardSkinIndex;
    }

    public String getPieceSkinPath(){
        if(isWhitePiece()){
            return DyConst.white_piece_tex_path + DyConst.piece_skins[Utils.long2int(pieceSkinIndex)];
        }
        else{
            return DyConst.black_piece_tex_path + DyConst.piece_skins[Utils.long2int(pieceSkinIndex)];
        }
    }

    public String getBoardSkinPath(){
        return DyConst.board_tex_path + DyConst.board_skins[Utils.long2int(boardSkinIndex)];
    }

    public String getBlackTileSkinPath(){
        return DyConst.black_tile_tex_path + DyConst.tile_skins[Utils.long2int(tileSkinIndex)];
    }

    public String getWhiteTileSkinPath(){
        return DyConst.white_tile_tex_path + DyConst.tile_skins[Utils.long2int(tileSkinIndex)];
    }

    public String getBackgroundSkinPath(){
        return DyConst.background_tex_path + DyConst.back_ground_skins[Utils.long2int(backgroundSkinIndex)];
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
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setReady(Boolean isReady) {
        this.isReady = isReady;
    }

    public boolean isReady() {
        return isReady;
    }

    public void setPieceSkinIndex(long pieceSkinIndex) {
        this.pieceSkinIndex = pieceSkinIndex;
    }

    public void setBoardSkinIndex(long boardSkinIndex) {
        this.boardSkinIndex = boardSkinIndex;
    }

    public void setTileSkinIndex(long tileSkinIndex) {
        this.tileSkinIndex = tileSkinIndex;
    }

    public void setBackgroundSkinIndex(long backgroundSkinIndex) {
        this.backgroundSkinIndex = backgroundSkinIndex;
    }
}
