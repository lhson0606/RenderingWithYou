package com.dy.app.graphic.model;


public class Mesh {
    public String name = "not implemented";
    public String ID = "not implemented";
    public float mPositions[];
    public float mNormals[];
    public float mTexCoords[];
    public int mIndices[];

    public Mesh(float[] positions, float[] texCoords, float[] normals, int indices[]){
        mPositions = positions;
        mTexCoords = texCoords;
        mNormals = normals;
        mIndices = indices;
    }

    public Mesh(Mesh mesh) {
        this.name = mesh.name;
        this.ID = mesh.ID;
        this.mPositions = mesh.mPositions;
        this.mNormals = mesh.mNormals;
        this.mTexCoords = mesh.mTexCoords;
        this.mIndices = mesh.mIndices;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setID(String ID) {
        this.ID = ID;
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


}
