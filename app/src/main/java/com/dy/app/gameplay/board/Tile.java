package com.dy.app.gameplay.board;

import android.graphics.Bitmap;

import com.dy.app.common.maths.Mat4;
import com.dy.app.common.maths.Vec2i;
import com.dy.app.common.maths.Vec3;
import com.dy.app.core.GameCore;
import com.dy.app.gameplay.piece.Piece;
import com.dy.app.graphic.Material;
import com.dy.app.graphic.Skin;
import com.dy.app.graphic.model.Obj3D;
import com.dy.app.graphic.model.Texture;
import com.dy.app.graphic.shader.ShaderHelper;
import com.dy.app.graphic.shader.TileShader;
import com.dy.app.manager.AssetManger;
import com.dy.app.utils.DyConst;

import java.io.IOException;

public class Tile {
    public Vec2i pos;
    private Piece piece;

    private Obj3D obj;

    enum TileColor{
        WHITE,
        BLACK
    }

    private TileColor color;

    public Tile(Obj3D obj, Vec2i pos, Piece piece){
        this.obj = obj;
        this.pos = pos;
        this.piece = piece;
        //set the position of the tile
        obj.translate(getOffSet(pos.x, pos.y));
    }

    boolean hasPiece(){
        return piece != null;
    }

    public Piece getPiece(){
        if(!hasPiece())
            throw new RuntimeException("Tile has no piece");

        return piece;
    }

    public void setPiece(Piece piece){
        this.piece = piece;
    }

    public Vec3 getWorldPos(){
        return new Vec3(pos.x, DyConst.board_height, pos.y);
    }

    public Obj3D getObj(){
        return obj;
    }


    public void init(){
        Skin skin = null;
        if((pos.x + pos.y)%2 == 0){
            color = TileColor.WHITE;
            skin = AssetManger.getInstance().getSkin(AssetManger.SkinType.WHITE_TILE);
        }else{
            color = TileColor.BLACK;
            skin = AssetManger.getInstance().getSkin(AssetManger.SkinType.BLACK_TILE);
        }

        Texture tex = new Texture(skin.getBitmap());
        Material material = skin.getMaterial();

        obj.setTex(tex);
        obj.setMaterial(material);

        obj.init();
    }

    public static Vec3 getOffSet(int x, int y){
        return new Vec3(DyConst.tile_size*(x -4+ 0.5f) , 0, DyConst.tile_size*(y -4+ 0.5f));
    }
}
