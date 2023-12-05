package com.dy.app.gameplay.player;

import android.content.Context;
import android.graphics.drawable.Drawable;

import com.dy.app.R;
import com.dy.app.utils.DyConst;
import com.dy.app.utils.ImageLoader;
import com.dy.app.utils.Utils;

import java.io.IOException;

public class Player {
    private boolean inTurn = true;
    private boolean whitePiece = true;
    private boolean hasLogin = false;
    private boolean isHost;
    private float xp = 0;
    private int elo = 0;
    public PlayerProfile profile;
    public PlayerInventory inventory;
    public BattlePass battlePass;
    public PlayerStatistics statistics;
    public PlayerPreferences preferences;
    public PlayerPurchase purchase;
    private boolean isReady = false;

    private Player(){
        reset();
    }

    public String getDisplayName() {
        String name = (String) profile.get(PlayerProfile.KEY_USERNAME);
        if(name == null || name == "")
            name = (String) profile.get(PlayerProfile.KEY_EMAIL);
        return name;
    }

    public void reset(){
        inTurn = true;
        whitePiece = true;
        hasLogin = false;
        profile = new PlayerProfile();
        inventory = new PlayerInventory();
        battlePass = new BattlePass();
        statistics = new PlayerStatistics();
        preferences = new PlayerPreferences();
        purchase = new PlayerPurchase();
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
        Long temp = (Long)inventory.get(PlayerInventory.KEY_PIECE_SKIN_INDEX);
        int pieceSkinIndex = temp.intValue();

        if(isWhitePiece()){
            return DyConst.white_piece_tex_path + DyConst.piece_skins[pieceSkinIndex];
        }
        else{
            return DyConst.black_piece_tex_path + DyConst.piece_skins[pieceSkinIndex];
        }
    }

    public String getBoardSkinPath(){
        int boardSkinIndex = Utils.long2int((long)inventory.get(PlayerInventory.KEY_BOARD_SKIN_INDEX));
        return DyConst.board_tex_path + DyConst.board_skins[boardSkinIndex];
    }

    public String getBlackTileSkinPath(){
        int tileSkinIndex = Utils.long2int((long)inventory.get(PlayerInventory.KEY_TILE_SKIN_INDEX));
        return DyConst.black_tile_tex_path + DyConst.tile_skins[tileSkinIndex];
    }

    public String getWhiteTileSkinPath(){
        int tileSkinIndex = Utils.long2int((long)inventory.get(PlayerInventory.KEY_TILE_SKIN_INDEX));
        return DyConst.white_tile_tex_path + DyConst.tile_skins[tileSkinIndex];
    }

    public String getTerrainTexPath(){
        int terrainSkinIndex = Utils.long2int((long)inventory.get(PlayerInventory.KEY_TERRAIN_SKIN_INDEX));
        return DyConst.terrain_model_path + DyConst.terrain_tex[terrainSkinIndex];
    }

    public String getTerrainModelPath(){
        int terrainSkinIndex = Utils.long2int((long)inventory.get(PlayerInventory.KEY_TERRAIN_SKIN_INDEX));
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

    public Drawable getDrawableAvatar(Context context) throws IOException {
        String avatarPath = (String)profile.get(PlayerProfile.KEY_PHOTO_URL);
        Drawable res = null;

        if(avatarPath == "" || avatarPath == null) {
            //create drawable from resource ic_no_image
            res = context.getDrawable(R.drawable.ic_no_image);
        }else{
            //create drawable from url
            res = ImageLoader.drawableFromUrl(avatarPath);
        }

        return res;
    }

    public boolean isReady() {
        return isReady;
    }

    public void setReady(boolean ready) {
        isReady = ready;
    }
}
