package com.dy.app.graphic.shader;

import android.opengl.GLES20;
import android.opengl.GLES30;

import com.dy.app.common.maths.Vec4;
import com.dy.app.core.GameCore;
import com.dy.app.gameplay.board.Tile;
import com.dy.app.utils.GLHelper;

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

    @Override
    public void loadExtraTexture() {
        tile.getShadowFrameBuffer().bindForReading(GLES30.GL_TEXTURE1);
    }

    @Override
    public void loadUniforms(){
        //load uniforms
        super.loadUniforms();
        loadHighlightColor(tile.getObj().getHighlightColor());
    }

    private void loadHighlightColor(Vec4 highlightColor) {
        GLES20.glUniform4f(mHighlightColorLoc, highlightColor.x, highlightColor.y, highlightColor.z, highlightColor.w);
    }

    @Override
    public void getAllUniLocations(){
        super.getAllUniLocations();
        mHighlightColorLoc = GLHelper.getUniLocation(mProgram, HIGHLIGHT_COLOR_NAME);
        mLightMVPLoc = GLHelper.getUniLocation(mProgram, LIGHT_MVP_MAT_NAME);
    }

    public static final String HIGHLIGHT_COLOR_NAME = "uHighlightColor";
    private int mHighlightColorLoc = -1;
    private final String LIGHT_MVP_MAT_NAME = "uLightMVP";
    private int mLightMVPLoc = -1;
}
