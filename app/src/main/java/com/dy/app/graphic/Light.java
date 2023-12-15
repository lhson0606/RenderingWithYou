package com.dy.app.graphic;

import androidx.annotation.NonNull;

import com.dy.app.common.maths.Mat4;
import com.dy.app.common.maths.Vec3;
import com.dy.app.utils.DyConst;

public class Light implements Cloneable{
    private Vec3 color;
    private Vec3 pos;
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
        return new Light(color.clone(), pos.clone(), intensity);
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
}
