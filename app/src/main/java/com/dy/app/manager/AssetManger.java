package com.dy.app.manager;

import android.content.Context;

import com.dy.app.gameplay.player.Player;
import com.dy.app.gameplay.player.Rival;
import com.dy.app.graphic.Material;
import com.dy.app.graphic.Skin;
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
    }

    public AssetManger(Context context){
        this.context = context;
        skins = new HashMap<>();
    }

    public void loadSkin () throws IOException {
        String player_piece_skin_path = Player.getInstance().getPieceSkinPath();
        String rival_piece_skin_path = Rival.getInstance().getPieceSkinPath();
        String board_skin_path = Player.getInstance().getBoardSkinPath();
        String black_tile_skin_path = Player.getInstance().getBlackTileSkinPath();
        String white_tile_skin_path = Player.getInstance().getWhiteTileSkinPath();
        String terrain_texture_path = Player.getInstance().getTerrainTexPath();

        Skin player_skin = new Skin(
                DyConst.default_material,
                GLHelper.loadBitmap(context.getAssets().open(player_piece_skin_path))
        );

        Skin rival_skin = new Skin(
                DyConst.default_material,
                GLHelper.loadBitmap(context.getAssets().open(rival_piece_skin_path))
        );

        Skin board_skin = new Skin(
                DyConst.default_material,
                GLHelper.loadBitmap(context.getAssets().open(board_skin_path))
        );

        Skin black_tile_skin = new Skin(
                DyConst.default_material,
                GLHelper.loadBitmap(context.getAssets().open(black_tile_skin_path))
        );

        Skin white_tile_skin = new Skin(
                DyConst.default_material,
                GLHelper.loadBitmap(context.getAssets().open(white_tile_skin_path))
        );

        Material terrain_mtl = new Material(
          0, 0
        );

        Skin terrain_texture = new Skin(
                terrain_mtl,
                GLHelper.loadBitmap(context.getAssets().open(terrain_texture_path))
        );

        skins.put(SkinType.PLAYER, player_skin);
        skins.put(SkinType.RIVAL, rival_skin);
        skins.put(SkinType.BOARD, board_skin);
        skins.put(SkinType.BLACK_TILE, black_tile_skin);
        skins.put(SkinType.WHITE_TILE, white_tile_skin);
        skins.put(SkinType.TERRAIN_TEXTURE, terrain_texture);

    }

    public Skin getSkin (SkinType type) {
        return skins.get(type);
    }

    private AssetManger() {
        skins = new HashMap<>();
    }
}
