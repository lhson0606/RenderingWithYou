package com.dy.startinganimation.camera;

import android.opengl.Matrix;

import com.dy.startinganimation.maths.Mat4;
import com.dy.startinganimation.maths.Vec3;

public class Camera {
    public Camera(int width, int height){
        mWidth = width; mHeight = height;

        //Set up View matrix

        Matrix.setLookAtM(mViewMat.mData, mViewMat.mOffset,
                mPos.x, mPos.y, mPos.z,
                mCenter.x, mCenter.y, mCenter.z,
                mUp.x, mUp.y, mUp.z);

        //Set up Projection matrix
        float aspectRatio = (float)width/(float)height;
        Matrix.frustumM(mProjMat.mData, mProjMat.mOffset,-aspectRatio, aspectRatio, mBottom, mTop, mNear, mFar);
    }

    public void updateViewMatrix(){
        Matrix.setLookAtM(mViewMat.mData, mViewMat.mOffset,
                mPos.x, mPos.y, mPos.z,
                mCenter.x, mCenter.y, mCenter.z,
                mUp.x, mUp.y, mUp.z);
    }

    private int mWidth;
    private int mHeight;
    private float mBottom = -1;
    private float mTop = 1;
    private float mNear = 3f;
    private float mFar = 50;
    public Mat4 mProjMat = new Mat4();
    public Mat4 mViewMat = new Mat4();
    public Vec3 mPos = new Vec3(0, 20, 15);
    public Vec3 mUp = new Vec3(0,1,0);
    public Vec3 mCenter = new Vec3(0,0,0);

    //Constants
    public static final Vec3 Y_AXIS = new Vec3(0,1,0);
    final float sensitivity = 0.1f;
    final float MAX_MOVE_LENGTH = 20;
    final float MAX_DX = MAX_MOVE_LENGTH;
    final float MAX_DY = MAX_MOVE_LENGTH;
    final float MIN_DX = -MAX_MOVE_LENGTH;
    final float MIN_DY = -MAX_MOVE_LENGTH;
    final float MIN_ANGLE_TO_Y = (float)Math.toRadians(5.0f);
    public void scaleR(float s){
        //#TODO
    }
}
