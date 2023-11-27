package com.dy.app.gameplay.board;

import com.dy.app.common.maths.Vec2i;
import com.dy.app.common.maths.Vec3;
import com.dy.app.gameplay.algebraicNotation.ChessNotation;
import com.dy.app.gameplay.piece.Piece;
import com.dy.app.graphic.Material;
import com.dy.app.graphic.Skin;
import com.dy.app.graphic.model.Obj3D;
import com.dy.app.graphic.model.Texture;
import com.dy.app.manager.AssetManger;
import com.dy.app.utils.DyConst;

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

    public boolean hasPiece(){
        synchronized (this){
            return piece != null;
        }
    }

    public Piece getPiece(){
        if(!hasPiece())
            return null;

        return piece;
    }

    public Tile setPiece(Piece piece){
        this.piece = piece;
        return this;
    }

    public Vec3 getWorldPos(){
        return getOffSet(pos.x, pos.y);
    }

    public Obj3D getObj(){
        return obj;
    }


    public void init(AssetManger assetManger){
        Skin skin = null;
        if((pos.x + pos.y)%2 == 0){
            color = TileColor.WHITE;
            skin = assetManger.getSkin(AssetManger.SkinType.WHITE_TILE);
        }else{
            color = TileColor.BLACK;
            skin = assetManger.getSkin(AssetManger.SkinType.BLACK_TILE);
        }

        Texture tex = new Texture(skin.getBitmap());
        Material material = skin.getMaterial();

        obj.setTex(tex);
        obj.setMaterial(material);

        obj.init();
    }

    public static Vec3 getOffSet(int x, int y){
        //mobile x value is left to right, z value is top to bottom
        return new Vec3(DyConst.tile_size*(x - 3.5f) , 0, DyConst.tile_size*(y - 3.5f));
    }

    public static Vec2i getTilePos(Vec3 pos){
        //mobile x value is left to right, z value is top to bottom
        //we have use Math.floor() here, say we picked a tile at (-4.9, -4.9), we want to get (-5, -5) instead of (-4, -4)
        //explain when we don't use floor(), if we pick a tile at (-4.9, -4.9) => (-4, -4), offset by (4, 4) => (-0, -0) => (0, 0) which is a valid tile => wrong since our first tile is from (-3.0, 3.0) -> (-3.999, -3.999)
        //there is no tile at (-4.9, -4.9)
        return new Vec2i((int)Math.floor(pos.x/DyConst.tile_size + 4), (int)Math.floor(pos.z/DyConst.tile_size + 4));
    }

    public String getNotation(){
        return ChessNotation.BoardPositions[pos.x][pos.y];
    }
}
