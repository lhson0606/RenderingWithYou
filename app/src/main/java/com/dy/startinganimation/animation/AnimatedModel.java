package com.dy.startinganimation.animation;

import com.dy.startinganimation.maths.Mat4;
import com.dy.startinganimation.model.Mesh;

import java.util.Vector;

public class AnimatedModel {
    public Mesh mMesh;
    //bind pose matrix see https://www.khronos.org/files/collada_spec_1_4.pdf page 33
    private Mat4 mBPM;
    public Joint[] mJoints;
    public AnimatedModel(Mesh mesh, Mat4 BPM, Joint[] joints){
        mMesh = mesh;
        mBPM = BPM;
        mJoints = joints;
    }
    void update(float dt){
        for(Joint joint:mJoints){
            joint.update(dt);
        }
    }

    public Mat4[] getCurrentJointsTransformData(){
        Mat4[] ret = new Mat4[mJoints.length];

        for(int i = 0; i<mJoints.length; i++){

            ret[i] = mBPM.multiplyMM(mJoints[i].getSpaceTransformMat());

        }

        return ret;
    }

    public void destroy() {
    }
    //#TODO destroy method
}
