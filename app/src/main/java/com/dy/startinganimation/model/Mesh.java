package com.dy.startinganimation.model;

import android.opengl.GLES30;

import com.dy.startinganimation.gl.Vertex;

import java.util.Vector;

public class Mesh {
    public String mName;
    public String mID;
    public float mPositions[];
    public float mNormals[];
    public float mTexCoords[];
    public int mIndices[];
    public int mBoneIDs[];
    public float mWeights[];
    public Texture mTexture;
    public Material mMaterial;

    public Mesh(String ID, String name,float[] positions, float[] texCoords, float[] normals,int[] indices, int[] boneIDs, float[] weights,Texture texture, Material material){
        //#TODO multiple textures and materials?
        mID = ID;
        mName= name;
        mPositions = positions;
        mTexCoords = texCoords;
        mNormals = normals;
        mIndices = indices;
        mBoneIDs = boneIDs;
        mWeights = weights;
        mTexture = texture;
        mMaterial = material;
    }

    private void enableAttrib(int...  attributes){
        for(int index : attributes){
            GLES30.glEnableVertexAttribArray(index);
        }
    }
    private void disable(int...  attributes){
        for(int index : attributes){
            GLES30.glDisableVertexAttribArray(index);
        }
    }

    public float[] getVertexPosData(){
        return mPositions;
    }

    public float[] getNormalsData(){
        return mNormals;
    }

    public float[] getTexCoordsData(){
        return mTexCoords;
    }

    public int[] getBonesIndicesData(){
        return mBoneIDs;
    }

    public float[] getBonesWeightsData(){
        return mWeights;
    }
    //#TODO destroy method
    public void init(){
        mTexture.init();
    }
}
