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
    private float sensitivity = DyConst.default_sensitivity;
    private int selectedViewPortIndex = 0;
    private int playbackSpeed = DyConst.default_playback_speed;
    public static final String[] DRAW_MODES = {
            "Triangle",
            "Line",
            "Point",
            "Triangle Strip",
            "Triangle Fan",
            "Line Strip",
            "Line Loop"
    };
    private int drawMode = 0;

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

    public int getDrawModeIndex() {
        return drawMode;
    }
    public boolean isCinematicCameraEnabled = false;

    public int getDrawMode() {
        switch (DRAW_MODES[drawMode]){
            case "Triangle":
                return GLES30.GL_TRIANGLES;
            case "Line":
                return GLES30.GL_LINES;
            case "Point":
                return GLES30.GL_POINTS;
            case "Triangle Strip":
                return GLES30.GL_TRIANGLE_STRIP;
            case "Triangle Fan":
                return GLES30.GL_TRIANGLE_FAN;
            case "Line Strip":
                return GLES30.GL_LINE_STRIP;
            case "Line Loop":
                return GLES30.GL_LINE_LOOP;
            default:
                return GLES30.GL_TRIANGLES;
        }
    }

    public void setAmbientFactor(float ambientFactor) {
        this.ambientFactor = ambientFactor;
    }

    public void setDrawModeIndex(int drawMode) {
        this.drawMode = drawMode;
    }

    public void setSensitivity(float progress) {
    }

    public int getSelectedViewPortIndex() {
        return selectedViewPortIndex;
    }

    public void setSelectedViewPortIndex(int position) {
        this.selectedViewPortIndex = position;
    }

    public int getPlaybackSpeed() {
        return playbackSpeed;
    }

    public void setPlaybackSpeed(int playbackSpeed) {
        this.playbackSpeed = playbackSpeed;
    }
}
