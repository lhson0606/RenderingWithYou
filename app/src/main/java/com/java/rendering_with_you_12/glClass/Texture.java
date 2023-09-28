package com.java.rendering_with_you_12.glClass;

import android.opengl.GLES30;

public class Texture {


    private int m_ID;
    private float m_AmbientFactor;
    private float m_Reflectivity;
    private float m_LightDampener;

    public Texture(int ID, float ambientFactor, float reflectivity, float dampener) {
        m_ID = ID;
        m_AmbientFactor = ambientFactor;
        m_Reflectivity = reflectivity;
        m_LightDampener = dampener;
    }

    public int getID() {
        return m_ID;
    }

    public float getAmbientFactor() {
        return m_AmbientFactor;
    }

    public float getReflectivity() {
        return m_Reflectivity;
    }

    public void destroy(){
        int temp[] = {m_ID};
        GLES30.glDeleteTextures(0, temp, 1);
    }

    public float getLightDampener() {
        return m_LightDampener;
    }
}
