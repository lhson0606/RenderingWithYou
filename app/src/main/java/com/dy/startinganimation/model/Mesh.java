package com.dy.startinganimation.model;

import android.opengl.GLES30;

import com.dy.startinganimation.gl.Vertex;

import java.util.Vector;

public class Mesh {
    public String mName;
    public String mID;
    public Vertex mVertices[];
    public int mIndices[];
    public Texture mTexture;
    public Material mMaterial;

    public Mesh(String ID, String name, Vertex[] vertices, int[] indices, Texture texture, Material material){
        //#TODO multiple textures and materials?
        mID = ID;
        mName= name;
        mVertices= vertices;
        mIndices = indices;
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
        float[] ret = new float[mVertices.length*3];

        for(int i = 0; i<mVertices.length; ++i){
            if(mVertices[i].mHasLine)
            {
                continue;
            }
            ret[i*3 + 0] = mVertices[i].mPos.x;
            ret[i*3 + 1] = mVertices[i].mPos.y;
            ret[i*3 + 2] = mVertices[i].mPos.z;
        }

        return ret;
    }

    public float[] getNormalsData(){
        float[] ret = new float[mVertices.length*3];

        for(int i = 0; i<mVertices.length; ++i){
            if(mVertices[i].mHasLine)
            {
                continue;
            }
            ret[i*3 + 0] = mVertices[i].mNormal.x;
            ret[i*3 + 1] = mVertices[i].mNormal.y;
            ret[i*3 + 2] = mVertices[i].mNormal.z;
        }

        return ret;
    }

    public float[] getTexCoordsData(){
        float[] ret = new float[mVertices.length*2];

        for(int i = 0; i<mVertices.length; ++i){
            if(mVertices[i].mHasLine)
            {
                continue;
            }
            ret[i * 2 + 0] = mVertices[i].mTexCoords.x;
            ret[i * 2 + 1] = mVertices[i].mTexCoords.y;
        }

        return ret;
    }

    public int[] getBonesIndicesData(){
        int ret[] = new int[mVertices.length*Vertex.MAX_JOINTS];

        for(int i = 0; i<mVertices.length; ++i){

            for(int j = 0; j<Vertex.MAX_JOINTS; ++j){
                ret[i*Vertex.MAX_JOINTS+j] = mVertices[i].mJointIDs[j];
            }

        }

        return ret;
    }

    public float[] getBonesWeightsData(){
        float ret[] = new float[mVertices.length*Vertex.MAX_JOINTS];

        for(int i = 0; i<mVertices.length; ++i){

            for(int j = 0; j<Vertex.MAX_JOINTS; ++j){
                ret[i*Vertex.MAX_JOINTS+j] = mVertices[i].mWeights[j];
            }

        }

        return ret;
    }
    //#TODO destroy method
}
