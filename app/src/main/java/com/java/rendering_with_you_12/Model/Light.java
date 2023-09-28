package com.java.rendering_with_you_12.Model;

import android.opengl.Matrix;

import com.java.rendering_with_you_12.maths.Vec3;

public class Light implements Entity{

    public Light(Vec3 m_Color, Vec3 m_Position) {
        this.m_Color = m_Color;
        this.m_Position = m_Position;
        m_ModelMat = new float[16];
        Matrix.setIdentityM(m_ModelMat,0);
    }

    public Vec3 getColor() {
        return m_Color;
    }

    public Vec3 getPosition() {
        return m_Position;
    }

    public float[] getModelMat() {
        return m_ModelMat;
    }

    private Vec3 m_Color;
    private Vec3 m_Position;
    private float[] m_ModelMat;


    @Override
    public void draw() {

    }

    @Override
    public void destroy() {

    }
}
