package com.dy.app.gameplay;

import com.dy.app.utils.DyConst;

public class Player {
    private boolean inTurn = true;
    private boolean whitePiece = false;
    private boolean hasLogin = false;
    private boolean isHost;
    private float xp = 0;
    private int elo = 0;
    public PlayerProfile profile;
    public PlayerInventory inventory;
    public BattlePass battlePass;

    private Player(){
        reset();
    }

    public void reset(){
        inTurn = true;
        whitePiece = false;
        hasLogin = false;
        profile = new PlayerProfile();
        inventory = new PlayerInventory();
        battlePass = new BattlePass();
        isHost = false;
        xp = 0;
        elo = 0;
    }

    public boolean isInTurn() {
        return inTurn;
    }

    public boolean isWhitePiece() {
        return whitePiece;
    }

    public void setWhitePiece(boolean whitePiece) {
        this.whitePiece = whitePiece;
    }


    public String getPieceSkinPath(){
        int pieceSkinIndex = (int)inventory.get(PlayerInventory.KEY_PIECE_SKIN_INDEX);

        if(isWhitePiece()){
            return DyConst.white_piece_tex_path + DyConst.piece_skins[pieceSkinIndex];
        }
        else{
            return DyConst.black_piece_tex_path + DyConst.piece_skins[pieceSkinIndex];
        }
    }

    public String getBoardSkinPath(){
        int boardSkinIndex = (int)inventory.get(PlayerInventory.KEY_BOARD_SKIN_INDEX);
        return DyConst.board_tex_path + DyConst.board_skins[boardSkinIndex];
    }

    public String getBlackTileSkinPath(){
        int tileSkinIndex = (int)inventory.get(PlayerInventory.KEY_TILE_SKIN_INDEX);
        return DyConst.black_tile_tex_path + DyConst.tile_skins[tileSkinIndex];
    }

    public String getWhiteTileSkinPath(){
        int tileSkinIndex = (int)inventory.get(PlayerInventory.KEY_TILE_SKIN_INDEX);
        return DyConst.white_tile_tex_path + DyConst.tile_skins[tileSkinIndex];
    }

    public String getBackgroundSkinPath(){
        int backgroundSkinIndex = (int)inventory.get(PlayerInventory.KEY_BOARD_SKIN_INDEX);
        return DyConst.background_tex_path + DyConst.back_ground_skins[backgroundSkinIndex];
    }

    public String getTerrainTexPath(){
        int terrainSkinIndex = (int)inventory.get(PlayerInventory.KEY_TERRAIN_SKIN_INDEX);
        return DyConst.terrain_model_path + DyConst.terrain_tex[terrainSkinIndex];
    }

    public String getTerrainModelPath(){
        int terrainSkinIndex = (int)inventory.get(PlayerInventory.KEY_TERRAIN_SKIN_INDEX);
        return DyConst.terrain_model_path + DyConst.terrain_models[terrainSkinIndex];
    }

    public void setInTurn(boolean inTurn) {
        this.inTurn = inTurn;
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
        //reset if logout
        if(!hasLogin)
            reset();
    }

    public boolean isHost() {
        return isHost;
    }

    public void setHost(boolean host) {
        isHost = host;
    }

}
