package com.dy.app.common.maths;

import android.opengl.Matrix;
import android.renderscript.Matrix4f;

public class Mat4 implements Cloneable{

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
        Matrix.multiplyMM(ret.mData, ret.mOffset, mData, mOffset, m.mData, m.mOffset);
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

    public Mat4 invert(){
        Mat4 ret = new Mat4();
        Matrix.invertM(ret.mData, ret.mOffset, mData, mOffset);
        return ret;
    }

    public Mat4 transpose(){
        Mat4 ret = new Mat4();
        Matrix.transposeM(ret.mData, ret.mOffset, mData, mOffset);
        return ret;
    }

    public Mat4 translate(Vec3 v){
        Mat4 ret = new Mat4();
        Matrix.translateM(ret.mData, ret.mOffset, mData, mOffset, v.x, v.y, v.z);
        return ret;
    }

    public static Mat4 createIdentityMatrix(){
        Mat4 ret = new Mat4();
        ret.setIdentityMat();
        return ret;
    }

    public static Mat4 interpolateTranslateMat(Vec3 mA, Vec3 mB, float t){
        Vec3 interpolatedVec =mA.interpolate(mB, t);
        Mat4 ret =Mat4.createIdentityMatrix();
        ret.mData[0*4+3] = interpolatedVec.x;
        ret.mData[1*4+3] = interpolatedVec.y;
        ret.mData[2*4+3] = interpolatedVec.z;
        return ret;
    }

    @Override
    public Mat4 clone(){
        return new Mat4(this);
    }
}
