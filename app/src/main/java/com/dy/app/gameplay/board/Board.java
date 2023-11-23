package com.dy.app.gameplay.board;

import android.content.Context;
import android.graphics.Shader;

import com.dy.app.common.maths.Mat4;
import com.dy.app.common.maths.Vec2i;
import com.dy.app.common.maths.Vec3;
import com.dy.app.core.GameCore;
import com.dy.app.core.GameEntity;
import com.dy.app.graphic.camera.Camera;
import com.dy.app.graphic.shader.ShaderHelper;
import com.dy.app.graphic.shader.TileShader;
import com.dy.app.manager.AssetManger;
import com.dy.app.manager.EntityManger;
import com.dy.app.manager.ObjManager;
import com.dy.app.utils.DyConst;

import java.io.IOException;

public class Board implements GameEntity {
    private Tile tiles[][];
    private final Context context;
    private final EntityManger entityManger;
    private final ObjManager objManager;
    private final AssetManger assetManger;

    public Board(Context context, EntityManger entityManger, ObjManager objManager, AssetManger assetManger){
        this.context = context;
        this.entityManger = entityManger;
        this.objManager = objManager;
        this.assetManger = assetManger;

    }

    public void load(){
        tiles = new Tile[DyConst.row_count][DyConst.col_count];
        for(int i = 0; i < DyConst.row_count; i++){
            for(int j = 0; j < DyConst.col_count; j++)
            {
                createTile(i, j);
            }

        }
        entityManger.newEntity(this);
    }

    private void createTile(int i, int j){
        tiles[i][j] = new Tile(objManager.getObj(DyConst.tile), new Vec2i(i, j), null);

        try {
            String verCode = null;
            verCode = ShaderHelper.getInstance().readShader(context.getAssets().open(DyConst.tile_ver_glsl_path));
            String fragCode = ShaderHelper.getInstance().readShader(context.getAssets().open(DyConst.tile_frag_glsl_path));
            TileShader shader = new TileShader(verCode, fragCode);
            shader.setTile(tiles[i][j]);
            tiles[i][j].getObj().setShader(shader);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public Tile getTile(Vec3 worldPos){
        Vec2i tilePos = Tile.getTilePos(worldPos);
        if(tilePos.x < 0 || tilePos.x >= DyConst.row_count || tilePos.y < 0 || tilePos.y >= DyConst.col_count)
            return null;
        return tiles[tilePos.x][tilePos.y];
    }


    public Tile getTile(Vec2i pos){
        return tiles[pos.x][pos.y];
    }

    private static Board instance = null;

    @Override
    public void init() {
        for(int i = 0; i < DyConst.row_count; i++){
            for(int j = 0; j < DyConst.col_count; j++){
                tiles[i][j].init(assetManger);
            }
        }
    }

    @Override
    public void update(float dt) {

    }

    @Override
    public void draw() {
        for(int i = 0; i < DyConst.row_count; i++){
            for(int j = 0; j < DyConst.col_count; j++)
                tiles[i][j].getObj().draw(Camera.getInstance().mViewMat, Camera.getInstance().mProjMat);
        }
        //tiles[0][0].getObj().draw(Camera.getInstance().mViewMat, Camera.getInstance().mProjMat);
    }

    @Override
    public void destroy() {
        for(int i = 0; i < DyConst.row_count; i++){
            for(int j = 0; j < DyConst.col_count; j++)
                tiles[i][j].getObj().destroy();
        }
    }
}
