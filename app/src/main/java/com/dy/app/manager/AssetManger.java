package com.dy.app.manager;

import android.content.Context;

import com.dy.app.gameplay.player.Player;
import com.dy.app.gameplay.player.Rival;
import com.dy.app.graphic.Material;
import com.dy.app.graphic.Skin;
import com.dy.app.setting.GameSetting;
import com.dy.app.utils.DyConst;
import com.dy.app.utils.GLHelper;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class AssetManger {
    private Map<SkinType, Skin> skins;
    Context context;
    public enum SkinType {
        PLAYER,
        RIVAL,
        BOARD,
        BLACK_TILE,
        WHITE_TILE,
        BACKGROUND,
        TERRAIN_TEXTURE,
        BLACK_PIECE_SKIN,
        WHITE_PIECE_SKIN
    }

    public AssetManger(Context context){
        this.context = context;
        skins = new HashMap<>();
    }

    public void loadSkin () throws IOException {
        GameSetting gameSetting = GameSetting.getInstance();
        String player_piece_skin_path = Player.getInstance().getPieceSkinPath();
        String rival_piece_skin_path = Rival.getInstance().getPieceSkinPath();
        String board_skin_path = Player.getInstance().getBoardSkinPath();
        String black_tile_skin_path = Player.getInstance().getBlackTileSkinPath();
        String white_tile_skin_path = Player.getInstance().getWhiteTileSkinPath();
        String terrain_texture_path = Player.getInstance().getTerrainTexPath();
        String whitePieceSkinPath =  Player.getInstance().isWhitePiece()? player_piece_skin_path : rival_piece_skin_path;
        String blackPieceSkinPath =  Player.getInstance().isWhitePiece()? rival_piece_skin_path : player_piece_skin_path;
        Skin player_skin = new Skin(
                gameSetting.getPieceMaterial(),
                GLHelper.loadBitmap(context.getAssets().open(player_piece_skin_path))
        );

        Skin rival_skin = new Skin(
                gameSetting.getPieceMaterial(),
                GLHelper.loadBitmap(context.getAssets().open(rival_piece_skin_path))
        );

        Skin board_skin = new Skin(
                gameSetting.getTileMaterial(),
                GLHelper.loadBitmap(context.getAssets().open(board_skin_path))
        );

        Skin black_tile_skin = new Skin(
                gameSetting.getTileMaterial(),
                GLHelper.loadBitmap(context.getAssets().open(black_tile_skin_path))
        );

        Skin white_tile_skin = new Skin(
                gameSetting.getTileMaterial(),
                GLHelper.loadBitmap(context.getAssets().open(white_tile_skin_path))
        );

        Skin terrain_texture = new Skin(
                gameSetting.getTerrainMaterial(),
                GLHelper.loadBitmap(context.getAssets().open(terrain_texture_path))
        );

        Skin white_piece_skin = new Skin(
                gameSetting.getPieceMaterial(),
                GLHelper.loadBitmap(context.getAssets().open(whitePieceSkinPath))
        );

        Skin black_piece_skin = new Skin(
                gameSetting.getPieceMaterial(),
                GLHelper.loadBitmap(context.getAssets().open(blackPieceSkinPath))
        );

        skins.put(SkinType.PLAYER, player_skin);
        skins.put(SkinType.RIVAL, rival_skin);
        skins.put(SkinType.BOARD, board_skin);
        skins.put(SkinType.BLACK_TILE, black_tile_skin);
        skins.put(SkinType.WHITE_TILE, white_tile_skin);
        skins.put(SkinType.TERRAIN_TEXTURE, terrain_texture);
        skins.put(SkinType.BLACK_PIECE_SKIN, black_piece_skin);
        skins.put(SkinType.WHITE_PIECE_SKIN, white_piece_skin);

    }

    public Skin getSkin (SkinType type) {
        return skins.get(type);
    }

    private AssetManger() {
        skins = new HashMap<>();
    }
}
