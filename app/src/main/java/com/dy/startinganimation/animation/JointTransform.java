package com.dy.startinganimation.animation;

import com.dy.startinganimation.maths.Mat4;
import com.dy.startinganimation.maths.Quat;
import com.dy.startinganimation.maths.Vec3;

public class JointTransform {
    public Mat4 mTransform;
    public JointTransform(Mat4 transform){
        mTransform = transform;
    }
    public Vec3 getTranslateVec(){
        return mTransform.getTranslateVec();
    }

    public Quat getQuat(){
        return new Quat(mTransform);
    }
}
