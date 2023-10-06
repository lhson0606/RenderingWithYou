package com.dy.startinganimation.camera;

import android.opengl.Matrix;

import com.dy.startinganimation.maths.Mat4;
import com.dy.startinganimation.maths.Vec3;

public class Camera {
    public Camera(){
        //Set up View matrix
        Matrix.setLookAtM(mViewMat.mData, mViewMat.mOffset,
                mPos.x, mPos.y, mPos.z,
                mCenter.x, mCenter.y, mCenter.z,
                mUp.x, mUp.y, mUp.z);

        //Set up Projection matrix
        float aspectRatio = (float)mWidth/(float)mHeight;
        Matrix.frustumM(mProjMat.mData, mProjMat.mOffset,-aspectRatio, aspectRatio, mBottom, mTop, mNear, mFar);
    }

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

    public void update(){
        float aspectRatio = (float)mWidth/(float)mHeight;
        Matrix.setLookAtM(mViewMat.mData, mViewMat.mOffset,
                mPos.x, mPos.y, mPos.z,
                mCenter.x, mCenter.y, mCenter.z,
                mUp.x, mUp.y, mUp.z);
        Matrix.frustumM(mProjMat.mData, mProjMat.mOffset,-aspectRatio, aspectRatio, mBottom, mTop, mNear, mFar);
    }

    private int mWidth = 500;
    private int mHeight = 500;
    private float mBottom = -1;
    private float mTop = 1;
    private float mNear = 1f;
    private float mFar = 100;
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



    public void move(float dx, float dy) {

        if(dx>MAX_DX){
            dx = MAX_DX;
        } else if(dx<MIN_DX){
            dx = MIN_DX;
        }

        if(dy>MAX_DY){
            dy = MAX_DY;
        } else if(dy<MIN_DY){
            dy = MIN_DY;
        }

        //Rotate around Y axis
        Vec3 vecFor = (mCenter.sub(mPos)).normalize();
        Vec3 vecRight = (vecFor.cross(Y_AXIS)).scale(-1).normalize();
        Vec3 moveX = vecRight.scale(dx*sensitivity);
        Vec3 moveY = mUp.scale(dy*sensitivity);

        Vec3 newPos = mPos.translate(moveX).translate(moveY).normalize().scale(mPos.length());
        //if new newPos is too close to global y-axis we don't move (return)
        if(Math.abs(newPos.dot(Y_AXIS)) < Math.cos(MIN_ANGLE_TO_Y)*newPos.length()){
            mPos = newPos;
        }

        mPos = newPos;
        //update vector up
        mUp = vecFor.cross(Y_AXIS).cross(vecFor).normalize();
    }

    public void scaleR(float s){
        mPos = mPos.scale(s);
    }

    public void setWidth(int mWidth) {
        this.mWidth = mWidth;
    }

    public void setHeight(int mHeight) {
        this.mHeight = mHeight;
    }
}
