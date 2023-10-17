package com.dy.app.setting;

import com.dy.app.graphic.Light;
import com.dy.app.utils.DyConst;

public class GameSetting {
    private Light light;
    private float ambientFactor;

    public GameSetting(){
        light = DyConst.defulat_light;
        ambientFactor = DyConst.default_ambient_factor;
    }

    public Light getLight() {
        return light;
    }

    public float getAmbientFactor() {
        return ambientFactor;
    }
}
