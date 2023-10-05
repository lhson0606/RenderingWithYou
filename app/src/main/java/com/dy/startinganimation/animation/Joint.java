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

    //index
    public int mID;

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

    public Joint(String name, int id, KeyFrame keyFrames[]){
        mName = name;
        mID = id;
        mKeyFrames = keyFrames;
        mChildren = new Vector<>();
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
        mProgress += dt;
        updateAnim(dt);
    }
    public void updateAnim(float dt){
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
        for(int i = mKeyFrames.length-1; i >=mCurrentFrameIndx ; --i){

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

       /* mBoneInstantAnimatedTransform = interpolateTransformMat(
                mKeyFrames[mCurrentFrameIndx].mJointTransforms, alpha,
                mKeyFrames[mNextFrameIndx].mJointTransforms, beta
                );*/
        if(mCurrentFrameIndx == mKeyFrames.length-1)
            mCurrentFrameIndx = 0;
        mBoneInstantAnimatedTransform = mKeyFrames[mCurrentFrameIndx++].mJointTransforms.mTransform;
        //Animation_Bone = Parent_World_Matrix * Animation_Parent * Local_Transformation * Bone_Instant_Animation * Inverse_Bind_Pose_Matrix

        /*if(isRoot()){
            mFinalAnimatedTransform = mLocalBindTransform.
                    multiplyMM(mBoneInstantAnimatedTransform).
                    multiplyMM(mInverseBindPoseMatrix);
        }else{
            mFinalAnimatedTransform = mParent.getWorldTransform().
                    multiplyMM(mParent.getAnimationTransform()).
                    multiplyMM(mLocalBindTransform).
                    multiplyMM(mBoneInstantAnimatedTransform).
                    multiplyMM(mInverseBindPoseMatrix);
        }*/

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

    private Mat4 interpolateTransformMat(JointTransform jointTransformA, float alpha,JointTransform jointTransformB, float beta){

        Mat4 interpolatedTranslateMat = interpolateMat(jointTransformA.getTranslateVec(), 1-alpha, jointTransformB.getTranslateVec(), 1-beta);
        Quat interpolatedRotateQuat = Quat.interpolate(jointTransformA.getQuat(), alpha, jointTransformB.getQuat(),  beta);

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

    public Mat4 getAnimationTransform(){
        //return mIBPM.multiplyMM(mKeyFrames[0].mJointTransforms.mTransform);
        //return mKeyFrames[0].mJointTransforms.mTransform.multiplyMM(mIBPM);
        /*Mat4 identity = new Mat4();
        identity.setIdentityMat();
        return  identity;*/
        return mFinalAnimatedTransform;
    }

    public void calculateInverseBindPoseMatrices(Mat4 parentBindTransform){
        Mat4 bindTransform = parentBindTransform.multiplyMM(mLocalBindTransform);
        mInverseBindPoseMatrix = bindTransform.invert();

        for(Joint child : mChildren){
            child.calculateInverseBindPoseMatrices(bindTransform);
        }
    }
}
