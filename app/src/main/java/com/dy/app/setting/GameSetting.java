package com.dy.app.setting;

import android.opengl.GLES30;

import com.dy.app.graphic.Light;
import com.dy.app.graphic.Material;
import com.dy.app.utils.DyConst;

public class GameSetting {
    private Light light;
    private float ambientFactor = DyConst.default_ambient_factor;
    private Material pieceMaterial;
    private Material boardMaterial;
    private Material tileMaterial;
    private Material terrainMaterial;
    public static final String[] DRAW_MODES = {
            "Triangle",
            "Line",
            "Point",
            "Triangle Strip",
            "Triangle Fan",
            "Line Strip",
            "Line Loop"
    };
    private int drawMode = GLES30.GL_TRIANGLES;

    private GameSetting(){
        try {
            light = DyConst.defulat_light.clone();
            pieceMaterial = DyConst.default_material.clone();
            boardMaterial = DyConst.default_material.clone();
            tileMaterial = DyConst.default_material.clone();
            terrainMaterial = new Material(
                    0, 0
            );
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }

    public Light getLight() {
        return light;
    }

    public float getAmbientFactor() {
        return ambientFactor;
    }

    public static GameSetting getInstance(){
        return (instance!=null)? instance: (instance = new GameSetting());
    }

    private static GameSetting instance = null;

    public Material getPieceMaterial() {
        return pieceMaterial;
    }

    public Material getBoardMaterial() {
        return boardMaterial;
    }

    public Material getTileMaterial() {
        return tileMaterial;
    }

    public Material getTerrainMaterial() {
        return terrainMaterial;
    }

    public int getDrawMode() {
        return drawMode;
    }

    public void setAmbientFactor(float ambientFactor) {
        this.ambientFactor = ambientFactor;
    }

    public void setDrawMode(int drawMode) {
        switch (DRAW_MODES[drawMode]){
            case "Triangle":
                this.drawMode = GLES30.GL_TRIANGLES;
                break;
            case "Line":
                this.drawMode = GLES30.GL_LINES;
                break;
            case "Point":
                this.drawMode = GLES30.GL_POINTS;
                break;
            case "Triangle Strip":
                this.drawMode = GLES30.GL_TRIANGLE_STRIP;
                break;
            case "Triangle Fan":
                this.drawMode = GLES30.GL_TRIANGLE_FAN;
                break;
            case "Line Strip":
                this.drawMode = GLES30.GL_LINE_STRIP;
                break;
            case "Line Loop":
                this.drawMode = GLES30.GL_LINE_LOOP;
                break;
        }
    }
}
