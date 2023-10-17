package com.dy.app.graphic.camera;

import static java.lang.Math.abs;

import android.opengl.Matrix;

import com.dy.app.common.maths.Mat4;
import com.dy.app.common.maths.Vec3;

public class Camera {
    private Camera(){
        //Set up View matrix
        Matrix.setLookAtM(mViewMat.mData, mViewMat.mOffset,
                mPos.x, mPos.y, mPos.z,
                mCenter.x, mCenter.y, mCenter.z,
                mUp.x, mUp.y, mUp.z);

        //Set up Projection matrix
        float aspectRatio = (float)mWidth/(float)mHeight;
        Matrix.frustumM(mProjMat.mData, mProjMat.mOffset,-aspectRatio, aspectRatio, mBottom, mTop, mNear, mFar);
    }

    public void update(){
        final float aspect_ratio = (float)mWidth/(float)mHeight;
        Matrix.setLookAtM(mViewMat.mData, mViewMat.mOffset,
                mPos.x, mPos.y, mPos.z,
                mCenter.x, mCenter.y, mCenter.z,
                mUp.x, mUp.y, mUp.z);
        Matrix.frustumM(mProjMat.mData, mProjMat.mOffset,-aspect_ratio, aspect_ratio, mBottom, mTop, mNear, mFar);
    }

    private int mWidth = 1260;
    private int mHeight = 840;
    private float mBottom = -1;
    private float mTop = 1;
    private float mNear = 1f;
    private float mFar = 100;
    public Mat4 mProjMat = new Mat4();
    public Mat4 mViewMat = new Mat4();
    public Vec3 mPos = new Vec3(0, 0, 5);
    public Vec3 mUp = new Vec3(0,1,0);
    public Vec3 mCenter = new Vec3(0,0,0);

    //Constants
    public static final Vec3 Y_AXIS = new Vec3(0,1,0);
    float sensitivity = 0.1f;
    final float MAX_MOVE_LENGTH = 1;
    final float MAX_DX = MAX_MOVE_LENGTH;
    final float MAX_DY = MAX_MOVE_LENGTH;
    final float MIN_DX = -MAX_MOVE_LENGTH;
    final float MIN_DY = -MAX_MOVE_LENGTH;
    final float MIN_ANGLE_TO_Y = (float)Math.toRadians(10.0f);



    public void move(float dx, float dy) {
        dx *= sensitivity;
        dy *= sensitivity;

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

        if(abs(dx) < 0.8f) dx = 0;
        if(abs(dy) < 0.8f) dy = 0;

        //Rotate around Y axis
        Vec3 vecFor = (mCenter.sub(mPos)).normalize();
        Vec3 vecRight = (vecFor.cross(Y_AXIS)).scale(-1).normalize();
        Vec3 moveX = vecRight.scale(dx*sensitivity);
        Vec3 moveY = mUp.scale(dy*sensitivity);

        Vec3 newPos = mPos.translate(moveX).translate(moveY).normalize().scale(mPos.length());
        //if new newPos is too close to global y-axis we don't move dy
        if(abs(newPos.dot(Y_AXIS)) > Math.cos(MIN_ANGLE_TO_Y)*newPos.length()){
            dy = 0f;
            //Rotate around Y axis
            vecFor = (mCenter.sub(mPos)).normalize();
            vecRight = (vecFor.cross(Y_AXIS)).scale(-1).normalize();
            moveX = vecRight.scale(dx*sensitivity);
            moveY = mUp.scale(dy*sensitivity);

            newPos = mPos.translate(moveX).translate(moveY).normalize().scale(mPos.length());
        }

        mPos = newPos;
        //update vector up
        mUp = vecFor.cross(Y_AXIS).cross(vecFor).normalize();
        update();
    }

    public void scaleR(float s){
        mPos = mPos.scale(s);
        update();
    }

    public void setWidth(int mWidth) {
        this.mWidth = mWidth;
        update();
    }

    public void setHeight(int mHeight) {
        this.mHeight = mHeight;
        update();
    }

    public static Camera getInstance(){
        return instance = (instance == null) ? new Camera() : instance;
    }

    private static Camera instance = null;

    public void setSensitivity(float value) {
        this.sensitivity = 0.1f*((value + 50f)/100f);
    }

    public void setPos(Vec3 pos){
        mPos = pos;
        update();
    }

    public void setCenter(Vec3 center){
        mCenter = center;
        update();
    }

    public void setUp(Vec3 up){
        mUp = up;
        update();
    }

    public void setBottom(float bottom){
        mBottom = bottom;
    }

}
