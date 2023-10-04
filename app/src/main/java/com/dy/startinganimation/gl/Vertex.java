package com.dy.startinganimation.gl;

import com.dy.startinganimation.maths.Vec2;
import com.dy.startinganimation.maths.Vec3;

public class Vertex {
    public Vec3 mPos;
    public Vec2 mTexCoords;
    public Vec3 mNormal;
    public int mTextureSet;
    public boolean mHasLine = false;

    public Vertex(Vec3 pos){
        mPos = pos;
    }

    public Vertex(Vec3 pos, Vec2 texCoords, Vec3 normal){
        mPos = pos;
        mTexCoords = texCoords;
        mNormal = normal;
    }


    public void setWeightsNormalized(){
        float sum = 0;

        for(int i = 0; i<Vertex.MAX_JOINTS; ++i){
            sum += mWeights[i];
        }

        for(int i = 0; i<Vertex.MAX_JOINTS; ++i){
            mWeights[i] /= sum;
        }
    }
    public int[] mJointIDs = new int[MAX_JOINTS];
    public float[] mWeights = new float[MAX_JOINTS];
    public static final int MAX_JOINTS = 4;
}
