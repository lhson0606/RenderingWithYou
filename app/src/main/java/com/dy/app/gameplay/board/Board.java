package com.dy.app.gameplay.board;

import com.dy.app.common.maths.Mat4;
import com.dy.app.common.maths.Vec2i;
import com.dy.app.common.maths.Vec3;
import com.dy.app.core.GameEntity;
import com.dy.app.graphic.camera.Camera;
import com.dy.app.manager.AssetManger;
import com.dy.app.manager.EntityManger;
import com.dy.app.manager.ObjManager;
import com.dy.app.utils.DyConst;

public class Board implements GameEntity {
    private Tile tiles[][];

    private Board(){
        tiles = new Tile[DyConst.row_count][DyConst.col_count];
        for(int i = 0; i < DyConst.row_count; i++){
            for(int j = 0; j < DyConst.col_count; j++)
                tiles[i][j] = new Tile(ObjManager.getInstance().getObj(DyConst.tile), new Vec2i(i, j), null);
        }
        EntityManger.getInstance().newEntity(this);
    }

    public Tile getTile(float x, float y){
        Vec2i pos = new Vec2i((int)(x / DyConst.tile_size), (int)(y / DyConst.tile_size));
        return tiles[pos.x][pos.y];
    }

    public Tile getTile(Vec2i pos){
        return tiles[pos.x][pos.y];
    }

    public static Board getInstance(){
        return instance = (instance == null) ? new Board() : instance;
    }

    private static Board instance = null;

    @Override
    public void init() {
        for(int i = 0; i < DyConst.row_count; i++){
            for(int j = 0; j < DyConst.col_count; j++){
                tiles[i][j].init();
            }
        }
        //tiles[0][0].init();
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
