package com.dy.startinganimation.maths;

import android.opengl.Matrix;

public class Mat4 {

    public Mat4(float[] data){
        mData = data;
    }

    public Mat4(float val){
        mData = new float[16];

        setVal(val);

    }

    public void setVal(float val){
        for(int i = 0; i<16; ++i){
            mData[i] = val;
        }
    }

    public Mat4(){
        mData = new float[16];
        setVal(0);
    }

    public Mat4(Mat4 mat4) {
        mData = new float[16];
        for(int i = 0; i<16; ++i){
            mData[i] = mat4.mData[i];
        }
        mOffset = mat4.mOffset;
    }

    public void setIdentityMat(){
        Matrix.setIdentityM(mData, mOffset);
    }

    public Mat4 multiplyMM(Mat4 m){
        Mat4 ret = new Mat4(this);
        Matrix.multiplyMM(ret.mData, ret.mOffset, ret.mData, ret.mOffset, m.mData, m.mOffset);
        return ret;
    }

    public float[] mData;
    public int mOffset = 0;

    public Vec3 getTranslateVec() {
        return new Vec3(
                mData[0*4+3],
                mData[1*4+3],
                mData[2*4+3]
        );
    }
}
