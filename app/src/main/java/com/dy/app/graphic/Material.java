package com.dy.app.graphic;

public class Material {
    float lightDampener;
    float reflectivity;

    public Material(float lightDampener, float reflectivity) {
        this.lightDampener = lightDampener;
        this.reflectivity = reflectivity;
    }

    public Material(Material material) {
        this.lightDampener = material.lightDampener;
        this.reflectivity = material.reflectivity;
    }

    public float getLightDampener() {
        return lightDampener;
    }

    public float getReflectivity() {
        return reflectivity;
    }
}
