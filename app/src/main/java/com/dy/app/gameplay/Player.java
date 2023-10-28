package com.dy.app.gameplay;

import com.dy.app.utils.DyConst;

public class Player {
    private boolean inTurn = true;
    private boolean whitePiece = false;
    private int pieceSkinIndex = 0; private int[] piece_skins = {0,1,2};
    private int backgroundSkinIndex = 0; private int[] back_ground_skins = {0};
    private int tileSkinIndex = 0; private int[] tile_skins = {0, 1};
    private int boardSkinIndex = 0; private int[] board_skins = {0};
    private int terrainSkinIndex = 0;private int[] terrain_skins = {0,1,2,3};
    private boolean hasLogin = false;
    private String name = "Player";
    private boolean isHost;
    private float xp = 0;

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

    public String getTerrainTexPath(){
        return DyConst.terrain_model_path + DyConst.terrain_tex[terrainSkinIndex];
    }

    public String getTerrainModelPath(){
        return DyConst.terrain_model_path + DyConst.terrain_models[terrainSkinIndex];
    }

    private static Player instance = null;

    public static Player getInstance() {
        if(instance != null) return instance;
        instance = new Player();
        return instance;
    }

    public boolean hasLogin() {
        return hasLogin;
    }

    public void setLoginStatus(boolean hasLogin) {
        this.hasLogin = hasLogin;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public float getXp() {
        return xp;
    }

    public void setXp(float xp) {
        this.xp = xp;
    }

    public int getTileSkinIndex() {
        return tileSkinIndex;
    }

    public void setTileSkinIndex(int tileSkinIndex) {
        this.tileSkinIndex = tileSkinIndex;
    }

    public void setBoardSkinIndex(int boardSkinIndex) {
        this.boardSkinIndex = boardSkinIndex;
    }

    public int getTerrainSkinIndex() {
        return terrainSkinIndex;
    }

    public void setTerrainSkinIndex(int terrainSkinIndex) {
        this.terrainSkinIndex = terrainSkinIndex;
    }

    public void setPieceSkinIndex(int pieceSkinIndex) {
        this.pieceSkinIndex = pieceSkinIndex;
    }

    public int getBackgroundSkinIndex() {
        return backgroundSkinIndex;
    }

    public void setBackgroundSkinIndex(int backgroundSkinIndex) {
        this.backgroundSkinIndex = backgroundSkinIndex;
    }

    public int[] getPiece_skins() {
        return piece_skins;
    }

    public void setPiece_skins(int[] piece_skins) {
        this.piece_skins = piece_skins;
    }

    public int[] getBack_ground_skins() {
        return back_ground_skins;
    }

    public void setBack_ground_skins(int[] back_ground_skins) {
        this.back_ground_skins = back_ground_skins;
    }

    public int[] getTile_skins() {
        return tile_skins;
    }

    public void setTile_skins(int[] tile_skins) {
        this.tile_skins = tile_skins;
    }

    public int[] getBoard_skins() {
        return board_skins;
    }

    public void setBoard_skins(int[] board_skins) {
        this.board_skins = board_skins;
    }

    public int[] getTerrain_skins() {
        return terrain_skins;
    }

    public void setTerrain_skins(int[] terrain_skins) {
        this.terrain_skins = terrain_skins;
    }

    public boolean isHost() {
        return isHost;
    }

    public void setHost(boolean host) {
        isHost = host;
    }
}
