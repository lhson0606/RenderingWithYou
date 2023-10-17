package com.dy.app.graphic.gl;

import android.opengl.GLES20;
import android.opengl.GLES30;

import com.dy.app.utils.GLHelper;

public class VBO{
    public VBO(int ID, float[] data, int sizePerStride, int stride, boolean normalized){
        mID = ID;
        mData = data;
        mSize = sizePerStride;
        mStride = stride;
        mNormalized = normalized;
        //put data
        put();
    }
    public VBO(float[] data, int sizePerStride, int stride, boolean normalized){
        mID = GLHelper.genBuffer();
        mData = data;
        mSize = sizePerStride;
        mStride = stride;
        mNormalized = normalized;
        //put data
        put();
    }
    public void put(){
        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, mID);
        GLES30.glBufferData(GLES30.GL_ARRAY_BUFFER,
                mData.length*Float.BYTES,
                GLHelper.createFloatBuffer(mData),
                GLES20.GL_STATIC_DRAW
        );
        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, 0);
    }

    public void bind(){
        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, mID);
    }

    public void unbind(){
        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, 0);
    }

    public void destroy(){
        int temp[] = new int[mID];
        GLES30.glDeleteBuffers(0, temp, 1);
    }

    public int getID(){
        return mID;
    }

    public float[] getData() {
        return mData;
    }

    public int getType(){
        return GLES30.GL_FLOAT;
    }

    public int getSize() {
        return mSize;
    }

    public int getStride() {
        return mStride;
    }

    public boolean isNormalized() {
        return mNormalized;
    }

    private int mID;
    private float[] mData;
    private int mSize;
    private int mStride;
    private boolean mNormalized;
}
