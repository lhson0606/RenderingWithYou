package com.dy.startinganimation.animation;

import android.graphics.Paint;

public class KeyFrame {
    public KeyFrame(JointTransform jointTransform, float timeStamp){
        mJointTransforms = jointTransform;
        mTimeStamp = timeStamp;
    }
    public JointTransform mJointTransforms;
    public float mTimeStamp;
}
