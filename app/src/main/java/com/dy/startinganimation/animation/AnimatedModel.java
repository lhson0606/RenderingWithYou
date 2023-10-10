package com.dy.startinganimation.animation;

import android.graphics.Paint;

import com.dy.startinganimation.maths.Mat4;
import com.dy.startinganimation.model.Mesh;

import java.util.Vector;

public class AnimatedModel {
    public static final int MAX_BONES = 50;
    public Mesh mMesh;
    //bind pose matrix see https://www.khronos.org/files/collada_spec_1_4.pdf page 33
    private Mat4 mBindShapeMatrix;
    public Joint[] mJoints;
    private Joint mRootJoint;
    public AnimatedModel(Mesh mesh, Mat4 BPM, Joint[] joints, Joint root){
        mMesh = mesh;
        mBindShapeMatrix = BPM;
        mJoints = joints;
        mRootJoint = root;
        mRootJoint.calculateInverseBindPoseMatrices(Mat4.createIdentityMatrix());
    }

    void update(float dt){
        mRootJoint.update(dt);
    }

    public Mat4[] getCurrentJointsTransformData(){
        Mat4[] ret = new Mat4[mJoints.length];

        for(int i = 0; i<mJoints.length; i++){

            Mat4 animTransform = mJoints[i].getWorldAnimatedTransform();
            Mat4 currentTransform = mBindShapeMatrix.multiplyMM(animTransform);
            ret[i] = currentTransform;
        }

        return ret;
    }

    public void destroy() {
    }
    //#TODO destroy method
}
