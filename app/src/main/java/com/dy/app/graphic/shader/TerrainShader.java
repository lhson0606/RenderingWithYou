package com.dy.app.graphic.shader;

import com.dy.app.gameplay.terrain.Terrain;
import com.dy.app.graphic.model.Obj3D;

public class TerrainShader extends Obj3DShader{
    private Terrain terrain;
    public TerrainShader(String verCode, String fragCode) {
        super(verCode, fragCode);
    }

    @Override
    public TerrainShader clone(){
        TerrainShader shader = new TerrainShader(mVerCode, mFragCode);
        shader.setTerrain(null);
        return shader;
    }

    public void setTerrain(Terrain terrain){
        this.terrain = terrain;
        super.setObj3D(terrain.getObj());
    }
}
