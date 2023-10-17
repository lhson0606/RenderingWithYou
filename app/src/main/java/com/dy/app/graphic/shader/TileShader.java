package com.dy.app.graphic.shader;

import com.dy.app.gameplay.board.Tile;

public class TileShader extends Obj3DShader{
    private Tile tile;

    @Override
    public TileShader clone(){
        TileShader shader = new TileShader(mVerCode, mFragCode);
        shader.setTile(null);
        return shader;
    }

    public TileShader(String verCode, String fragCode) {
        super(verCode, fragCode);
    }

    public void setTile(Tile tile){
        this.tile = tile;
        super.setObj3D(tile.getObj());
    }
}
