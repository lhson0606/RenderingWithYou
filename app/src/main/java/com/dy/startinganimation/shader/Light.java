package com.dy.startinganimation.shader;

import android.opengl.Matrix;

import com.dy.startinganimation.engine.Entity;
import com.dy.startinganimation.maths.Mat4;
import com.dy.startinganimation.maths.Vec3;

public class Light implements Entity {

    public Light(Vec3 m_Color, Vec3 m_Position) {
        this.mColor = mColor;
        this.mPosition = mPosition;
        mModelMat = new Mat4();
        mModelMat.setIdentityMat();
    }

    public Vec3 getColor() {
        return mColor;
    }

    public Vec3 getPosition() {
        return mPosition;
    }

    public float getAttenuationConstant() {
        return mAttenuationConstant;
    }

    public float getAttenuationLinear() {
        return mAttenuationLinear;
    }

    public float getAttenuationQuadratic() {
        return mAttenuationQuadratic;
    }

    public Mat4 getModelMat() {
        return mModelMat;
    }

    public void setColor(Vec3 mColor) {
        this.mColor = mColor;
    }

    public void setPosition(Vec3 mPosition) {
        this.mPosition = mPosition;
    }

    public void setAttenuationConstant(float mAttenuationConstant) {
        this.mAttenuationConstant = mAttenuationConstant;
    }

    public void setAttenuationLinear(float mAttenuationLinear) {
        this.mAttenuationLinear = mAttenuationLinear;
    }

    public void setAttenuationQuadratic(float mAttenuationQuadratic) {
        this.mAttenuationQuadratic = mAttenuationQuadratic;
    }

    public void setModelMat(Mat4 modelMat) {
        this.mModelMat = modelMat;
    }

    private Vec3 mColor = new Vec3(1,1,1);
    private Vec3 mPosition = new Vec3(0,0,0);
    private float mAttenuationConstant = 0.f;
    private float mAttenuationLinear = 0.5f;
    private float mAttenuationQuadratic = 0.5f;
    private Mat4 mModelMat;


    @Override
    public void init() {

    }

    @Override
    public void update(float dt) {

    }

    @Override
    public void draw() {

    }

    @Override
    public void destroy() {

    }
}
