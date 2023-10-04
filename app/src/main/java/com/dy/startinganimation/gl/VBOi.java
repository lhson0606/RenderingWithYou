package com.dy.startinganimation.gl;

import android.opengl.GLES20;
import android.opengl.GLES30;

import com.dy.startinganimation.utils.GLHelper;

public class VBOi {

    public VBOi(int ID, int[] data, int sizePerStride, int stride){
        mID = ID;
        mData = data;
        mSize = sizePerStride;
        mStride = stride;
        //put data
        put();
    }
    public VBOi(int[] data, int sizePerStride, int stride){
        mID = GLHelper.genBuffer();
        mData = data;
        mSize = sizePerStride;
        mStride = stride;
        //put data
        put();
    }
    public void put(){
        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, mID);
        GLES30.glBufferData(GLES30.GL_ARRAY_BUFFER,
                mData.length*Integer.BYTES,
                GLHelper.createIntBuffer(mData),
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

    public int[] getData() {
        return mData;
    }

    public int getType(){
        return GLES30.GL_INT;
    }

    public int getSize() {
        return mSize;
    }

    public int getStride() {
        return mStride;
    }

    private int mID;
    private int[] mData;
    private int mSize;
    private int mStride;

}
