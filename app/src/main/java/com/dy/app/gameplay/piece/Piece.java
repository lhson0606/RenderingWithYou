package com.dy.app.gameplay.piece;

import com.dy.app.common.maths.Mat4;
import com.dy.app.common.maths.Vec2i;
import com.dy.app.common.maths.Vec3;
import com.dy.app.core.GameEntity;
import com.dy.app.gameplay.board.Tile;
import com.dy.app.graphic.camera.Camera;
import com.dy.app.graphic.model.Obj3D;
import com.dy.app.manager.EntityManger;
import com.dy.app.utils.DyConst;

import java.util.Map;

public class Piece implements GameEntity {
    private Tile tile;
    private boolean onPlayerSide;
    private Obj3D obj;

    enum PieceColor{
        BLACK,
        WHITE
    }

    private PieceColor pieceColor;

    @Override
    public void init() {
        obj.init();
    }

    @Override
    public void update(float dt) {

    }

    @Override
    public void draw() {
        obj.draw(Camera.getInstance().mViewMat, Camera.getInstance().mViewMat);
    }

    @Override
    public void destroy() {
        obj.destroy();
    }

    public Vec2i tilePos(){
        return tile.pos;
    }

    public Obj3D getObj(){
        return obj;
    }

    public Piece(Tile tile, Obj3D obj, boolean onPlayerSide){
        this.tile = tile;
        this.obj = obj;
        this.onPlayerSide = onPlayerSide;
        EntityManger.getInstance().newEntity(this);
    }

    public boolean isOnPlayerSide(){
        return onPlayerSide;
    }

    public void setOnPlayerSide(boolean onPlayerSide){
        this.onPlayerSide = onPlayerSide;
    }

    public Tile getTile(){
        return tile;
    }

    public void move(int x, int y){
        Vec3 translation = new Vec3(x* DyConst.tile_size, 0, y*DyConst.tile_size);
        obj.translate(translation);
    }
}
