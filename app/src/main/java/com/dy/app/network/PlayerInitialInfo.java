package com.dy.app.network;

import java.io.Serializable;

public class PlayerInitialInfo implements Serializable {
    private String name = "";
    private long pieceSkinIndex = -1;
    private long boardSkinIndex = -1;
    private long tileSkinIndex = -1;

    public PlayerInitialInfo(){
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getPieceSkinIndex() {
        return pieceSkinIndex;
    }

    public void setPieceSkinIndex(long pieceSkinIndex) {
        this.pieceSkinIndex = pieceSkinIndex;
    }

    public long getBoardSkinIndex() {
        return boardSkinIndex;
    }

    public void setBoardSkinIndex(long boardSkinIndex) {
        this.boardSkinIndex = boardSkinIndex;
    }

    public long getTileSkinIndex() {
        return tileSkinIndex;
    }

    public void setTileSkinIndex(long tileSkinIndex) {
        this.tileSkinIndex = tileSkinIndex;
    }
}
