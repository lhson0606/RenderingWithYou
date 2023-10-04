package com.dy.startinganimation.animation;

import android.graphics.Paint;
import android.opengl.Matrix;

import com.dy.startinganimation.maths.Mat4;
import com.dy.startinganimation.maths.Quat;
import com.dy.startinganimation.maths.Vec3;

import java.util.List;
import java.util.Vector;

public class Joint {
    public String mName;
    public List<Joint> mChildren;
    //index
    public int mID;

    //inverse bind shape matrix bind shape matrix
    //see https://www.khronos.org/files/collada_spec_1_4.pdf page 33
    Mat4 mIBPM;
    // aka interpolated Transform matrix (Translate * Quaternion Rotate)
    Mat4 mTransformMat;
    KeyFrame[] mKeyFrames;

    public Joint(String name, int id, Mat4 IBPM, KeyFrame keyFrames[]){
        mName = name;
        mID = id;
        mIBPM = IBPM;
        mKeyFrames = keyFrames;
        update(0);
    }

    private int mCurrentFrameIndx = 0;
    private int mNextFrameIndx;
    private float mProgress;

    public void update(float dt){
        mProgress += dt;
        updateAnim();
    }
    public void updateAnim(){
        //case
        //First------------------------>LastTimeStamp-------------->Progress------->
        //reset process to zero if this happened
        if(mProgress>mKeyFrames[mKeyFrames.length-1].mTimeStamp){

            while(mProgress>mKeyFrames[mKeyFrames.length-1].mTimeStamp)
            {
                mProgress -= mKeyFrames[mKeyFrames.length-1].mTimeStamp;
            }

            mCurrentFrameIndx = 0;
        }
        //First--------->currentFrame----------->progress---------->nextFrame------>LastTimeStamp
        //update current frame index
        for(int i = mCurrentFrameIndx; i < mKeyFrames.length; ++i){

            if(mProgress>=mKeyFrames[i].mTimeStamp){
                mCurrentFrameIndx = i;
                break;
            }

        }
        //calculate next frame index
        mNextFrameIndx = (mCurrentFrameIndx+1)%mKeyFrames.length;

        //Start interpolating
        //currentFrame--------alpha------>progress--------beta------->nextFrame
        //the duration between two frames
        float duration = mKeyFrames[mNextFrameIndx].mTimeStamp - mKeyFrames[mCurrentFrameIndx].mTimeStamp;
        float alpha = mProgress - mKeyFrames[mCurrentFrameIndx].mTimeStamp;
        float beta = mKeyFrames[mNextFrameIndx].mTimeStamp - mProgress;
        alpha /= duration;
        beta /= duration;

        /*mTransformMat = interpolateTransformMat(
                mKeyFrames[mCurrentFrameIndx].mJointTransforms, alpha,
                mKeyFrames[mNextFrameIndx].mJointTransforms, beta
                );*/
       mTransformMat = new Mat4();
       mTransformMat.setIdentityMat();
    }

    private Mat4 interpolateTransformMat(JointTransform jointTransformA, float alpha,JointTransform jointTransformB, float beta){

        Mat4 interpolatedTranslateMat = interpolateMat(jointTransformA.getTranslateVec(), 1-alpha, jointTransformB.getTranslateVec(), 1-beta);
        Quat interpolatedRotateQuat = Quat.interpolate(jointTransformA.getQuat(), 1-alpha, jointTransformB.getQuat(),  1-beta);

        return interpolatedTranslateMat.multiplyMM(interpolatedRotateQuat.toMat());
    }

    private Mat4 interpolateMat(Vec3 mA, float alpha, Vec3 mB, float beta){
        Vec3 interpolatedVec = new Vec3(
                mA.x*alpha + mB.x*beta,
                mA.y*alpha + mB.y*beta,
                mA.z*alpha + mB.z*beta
        );

        Mat4 ret = new Mat4();
        ret.setIdentityMat();
        ret.mData[0*4 + 3] = interpolatedVec.x;
        ret.mData[1*4 + 3] = interpolatedVec.y;
        ret.mData[2*4 + 3] = interpolatedVec.z;

        return ret;
    }

    public Mat4 getSpaceTransformMat(){
        //
        //return mIBPM.multiplyMM(mTransformMat);
        return mTransformMat;
    }
}
