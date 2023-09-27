package com.java.rendering_with_you_12.glClass;

import android.annotation.SuppressLint;
import android.opengl.GLES20;
import android.opengl.GLES30;

import com.java.rendering_with_you_12.utils.GLHelper;

import java.nio.FloatBuffer;

public class VBO{
    int m_ID;
    float[] m_Data;
    int m_Size;
    int m_Stride;
    boolean m_Normalized;
    public VBO(int ID, float[] data, int sizePerStride, int stride, boolean normalized){
        m_ID = ID;
        m_Data = data;
        m_Size = sizePerStride;
        m_Stride = stride;
        m_Normalized = normalized;
        //put data
        put();
    }
    public VBO(float[] data, int sizePerStride, int stride, boolean normalized){
        m_ID = GLHelper.genBuffer();
        m_Data = data;
        m_Size = sizePerStride;
        m_Stride = stride;
        m_Normalized = normalized;
        //put data
        put();
    }
    public void put(){
        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, m_ID);
        GLES30.glBufferData(GLES30.GL_ARRAY_BUFFER,
                m_Data.length*Float.BYTES,
                GLHelper.createFloatBuffer(m_Data),
                GLES20.GL_STATIC_DRAW
        );
        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, 0);
    }

    public void bind(){
        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, m_ID);
    }

    public void unbind(){
        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, 0);
    }

    public void destroy(){
        int temp[] = new int[m_ID];
        GLES30.glDeleteBuffers(0, temp, 1);
    }

    public int getID(){
        return m_ID;
    }

    public float[] getData() {
        return m_Data;
    }

    public int getType(){
        return GLES30.GL_FLOAT;
    }

    public int getSize() {
        return m_Size;
    }

    public int getStride() {
        return m_Stride;
    }

    public boolean isNormalized() {
        return m_Normalized;
    }
}
