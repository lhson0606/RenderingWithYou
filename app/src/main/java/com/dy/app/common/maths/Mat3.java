package com.dy.app.common.maths;

import android.opengl.Matrix;

public class Mat3 {
    public Mat3(float[] data){
        mData = data;
    }

    public Mat3(){
        mData = new float[9];
        Matrix.setIdentityM(mData, mOffset);
    }

    public Mat3(Mat3 m){
        mData = m.mData;
        mOffset = m.mOffset;
    }

    public void setIdentityMat(){
        Matrix.setIdentityM(mData, mOffset);
    }

    public float[] mData;
    public int mOffset = 0;
}
