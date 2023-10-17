package com.dy.app.graphic;

public class Material {
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
}
