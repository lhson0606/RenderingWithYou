package com.dy.startinganimation.animation;

import android.graphics.Paint;
import android.opengl.Matrix;
import android.util.Log;

import com.dy.startinganimation.maths.Mat4;
import com.dy.startinganimation.maths.Quat;
import com.dy.startinganimation.maths.Vec3;

import java.util.List;
import java.util.Vector;

public class Joint {
    public String mName;

    //index
    public String mID;

    //inverse bind shape matrix bind shape matrix
    //see https://www.khronos.org/files/collada_spec_1_4.pdf page 33
    public Mat4 mInverseBindPoseMatrix;
    public Mat4 mLocalBindTransform;
    public Joint mParent;
    public List<Joint> mChildren;
    public Mat4 mFinalAnimatedTransform;
    public Mat4 mBoneInstantAnimatedTransform;
    public Mat4 mWorldTransform;
    public KeyFrame[] mKeyFrames;

    public Joint(String ID, String name){
        mName = name;
        mID = ID;
        mChildren = new Vector<>();
        mCurrentFrameIndx = 0;
        mNextFrameIndx = 1;
    }

    public void setInverseBindPoseMatrix(Mat4 IBPM){
        mInverseBindPoseMatrix = IBPM;
    }

    public boolean isRoot(){
        return mParent == null;
    }

    private Mat4 initWorldTransform(){
        if(isRoot()){
            Mat4 ret = new Mat4();
            ret.setIdentityMat();
            return ret;
        }else{
            return mLocalBindTransform.multiplyMM(mParent.getWorldTransform());
        }
    }

    public Mat4 getWorldTransform(){
        if(mWorldTransform == null){
            mWorldTransform = initWorldTransform();
        }

        return mWorldTransform;
    }

    private int mCurrentFrameIndx = 0;
    private int mNextFrameIndx;
    private float mProgress;

    public void update(float dt){
        updateAnim(dt);
    }
    public void updateAnim(float dt){
        mProgress += dt;
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
        if(mProgress >= mKeyFrames[mNextFrameIndx].mTimeStamp){
            mCurrentFrameIndx++;
        }
        //calculate next frame index
        mNextFrameIndx = (mCurrentFrameIndx+1)%mKeyFrames.length;
        //case
        //Start interpolating
        //currentFrame--------alpha------>progress--------beta------->nextFrame
        //the duration between two frames
        float t = (mProgress - mKeyFrames[mCurrentFrameIndx].mTimeStamp)/(mKeyFrames[mNextFrameIndx].mTimeStamp - mKeyFrames[mCurrentFrameIndx].mTimeStamp);

        if(t<0) t= -t;

        mBoneInstantAnimatedTransform = interpolateTransformMat(
                mKeyFrames[mCurrentFrameIndx].mJointTransforms,
                mKeyFrames[mNextFrameIndx].mJointTransforms, t
                );
        //mBoneInstantAnimatedTransform  = mKeyFrames[0].mJointTransforms.mTransform;


        if(isRoot()){
            //Animation_Bone = Local_Transformation * Bone_Instant_Animation * Inverse_Bind_Pose_Matrix
            mFinalAnimatedTransform =
                    mBoneInstantAnimatedTransform;
        }else{
            mFinalAnimatedTransform =
                    mParent.getAnimationTransform().
                    multiplyMM(mBoneInstantAnimatedTransform);
        }

        for(Joint child : mChildren){
            child.updateAnim(dt);
        }
    }

    private Mat4 interpolateTransformMat(JointTransform jointTransformA, JointTransform jointTransformB, float t){

        Mat4 interpolatedTranslateMat = Mat4.interpolateTranslateMat(jointTransformA.mTransform.transpose().getTranslateVec(), jointTransformB.mTransform.transpose().getTranslateVec(), t);
        Quat interpolatedRotateQuat = Quat.slerp(jointTransformA.getQuat().normalize(), jointTransformB.getQuat().normalize(), t);
        Mat4 interpolatedRotateMat = interpolatedRotateQuat.toMat();
        Mat4 interpolatedMat = interpolatedTranslateMat.transpose().multiplyMM(interpolatedRotateMat);
        return interpolatedMat;
    }

    public Mat4 getAnimationTransform(){
        return mFinalAnimatedTransform;
    }

    public Mat4 getWorldAnimatedTransform(){
        return mFinalAnimatedTransform.multiplyMM(mInverseBindPoseMatrix);
    }

    public void calculateInverseBindPoseMatrices(Mat4 parentBindTransform){
        Mat4 bindTransform = parentBindTransform.multiplyMM(mLocalBindTransform);
        mInverseBindPoseMatrix = bindTransform.invert();

        for(Joint child : mChildren){
            child.calculateInverseBindPoseMatrices(bindTransform);
        }
    }
}
