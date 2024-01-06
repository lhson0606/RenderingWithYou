package com.dy.app.graphic;

import androidx.annotation.NonNull;

import com.dy.app.common.maths.Mat4;
import com.dy.app.common.maths.Vec3;
import com.dy.app.utils.DyConst;

public class Light implements Cloneable{
    private Vec3 color;
    private Vec3 pos;
    private Vec3 up;
    private float intensity;
    private Mat4 modelMat;

    float attenuationConstant = DyConst.default_attenuation_constant;
    float attenuationLinear = DyConst.default_attenuation_linear;
    float attenuationQuadratic = DyConst.default_attenuation_quadratic;

    public Light(Vec3 color, Vec3 pos, float intensity) {
        this.color = color;
        this.pos = pos;
        this.intensity = intensity;
        modelMat = Mat4.createIdentityMatrix();
        up = new Vec3(0, 1, 0);
    }

    public Vec3 getColor() {
        return color;
    }

    public Vec3 getPos() {
        return pos;
    }

    public float getIntensity() {
        return intensity;
    }

    public Mat4 getModelMat() {
        return modelMat;
    }

    public float getAttenuationConstant() {
        return attenuationConstant;
    }

    public float getAttenuationLinear() {
        return attenuationLinear;
    }

    public float getAttenuationQuadratic() {
        return attenuationQuadratic;
    }

    @NonNull
    @Override
    public Light clone() throws CloneNotSupportedException {
        Light res = new Light(color.clone(), pos.clone(), intensity);
        res.setUp(this.up.clone());
        return res;
    }

    public void setRed(float val) {
        color.x = val;
    }

    public void setGreen(float val) {
        color.y = val;
    }

    public void setBlue(float val) {
        color.z = val;
    }

    public void setIntensity(float val) {
        intensity = val;
    }

    public float getRed() {
        return color.x;
    }

    public float getGreen() {
        return color.y;
    }

    public float getBlue() {
        return color.z;
    }

    public Vec3 getUp() {
        return up;
    }

    public void setUp(Vec3 up) {
        this.up = up;
    }
}
