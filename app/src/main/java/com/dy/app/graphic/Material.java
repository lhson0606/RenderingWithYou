package com.dy.app.graphic;

import androidx.annotation.NonNull;

public class Material implements Cloneable{
    float lightDamper = 1f;
    float reflectivity = 0f;

    public Material(float lightDampener, float reflectivity) {
        this.lightDamper = lightDampener;
        this.reflectivity = reflectivity;
    }

    public Material(Material material) {
        this.lightDamper = material.lightDamper;
        this.reflectivity = material.reflectivity;
    }

    public float getLightDamper() {
        return lightDamper;
    }

    public float getReflectivity() {
        return reflectivity;
    }

    @NonNull
    @Override
    public Material clone() throws CloneNotSupportedException {
        return new Material(this);
    }

    public void setLightDamper(float val) {
        this.lightDamper = val;
    }

    public void setReflectivity(float val) {
        this.reflectivity = val;
    }
}
