package com.dy.startinganimation.animation;

import android.opengl.GLES30;
import android.opengl.Matrix;

import com.dy.startinganimation.engine.Entity;
import com.dy.startinganimation.gl.EBO;
import com.dy.startinganimation.gl.VAO;
import com.dy.startinganimation.gl.VBO;
import com.dy.startinganimation.gl.VBOi;
import com.dy.startinganimation.gl.Vertex;
import com.dy.startinganimation.maths.Mat4;
import com.dy.startinganimation.maths.Vec3;
import com.dy.startinganimation.shader.Light;
import com.dy.startinganimation.shader.Shader;

public class Animator implements Entity {
    String mName;
    private AnimatedModel mAnimatedModel;
    private Shader mShader;
    VAO mVAO;
    VBO mVBOVertices;
    VBO mVBOTexCoords;
    VBO mVBONormals;
    EBO mEBOIndices;
    VBOi mVBOiBonesIndx;
    VBO mVBOBonesWeights;
    Mat4 mModelMat;
    Mat4 mViewMat;
    Mat4 mProjMat;
    int mDrawMode = GLES30.GL_TRIANGLES;
    private boolean mIsInitialized = false;
    public static String VERTEX_SHADER_PATH = "anim/ver.glsl";
    public static String FRAGMENT_SHADER_PATH = "anim/frag.glsl";

    public Animator(AnimatedModel animatedModel, Mat4 viewMat, Mat4 projMat, Shader shader){
        mAnimatedModel = animatedModel;
        mViewMat = viewMat;
        mProjMat = projMat;
        mShader = shader;
    }

    //has to be called after GL is initialized
    @Override
    public void init() {
        mShader.init();
        mAnimatedModel.init();
        mVAO = new VAO();

        mVBOVertices = new VBO(mAnimatedModel.mMesh.getVertexPosData(), 3, 3*Float.BYTES, false);
        mVBOTexCoords = new VBO(mAnimatedModel.mMesh.getTexCoordsData(), 2, 2*Float.BYTES, false);
        mVBONormals = new VBO(mAnimatedModel.mMesh.getNormalsData(), 3, 3*Float.BYTES, false);
        mVBOiBonesIndx = new VBOi(mAnimatedModel.mMesh.getBonesIndicesData(), Vertex.MAX_JOINTS, Vertex.MAX_JOINTS*Integer.BYTES);
        mVBOBonesWeights = new VBO(mAnimatedModel.mMesh.getBonesWeightsData(), Vertex.MAX_JOINTS, Vertex.MAX_JOINTS*Float.BYTES, false);

        mEBOIndices = new EBO(mAnimatedModel.mMesh.mIndices);
        bindAndEnableAttrib();

        mModelMat = new Mat4();
        mModelMat.setIdentityMat();
        Matrix.rotateM(mModelMat.mData, mModelMat.mOffset, -90, 1, 0, 0);

        mIsInitialized = true;
    }

    private void bindAndEnableAttrib(){
        mVAO.bind();
        mVAO.linkBufferAttribute(Shader.VERTEX_INDEX, mVBOVertices, 0);
        mVAO.linkBufferAttribute(Shader.TEXTURE_COORD_INDEX, mVBOTexCoords, 0);
        mVAO.linkBufferAttribute(Shader.NORMAL_INDEX, mVBONormals, 0);
        mVAO.linkBufferAttribute(Shader.JOINT_INDEX, mVBOiBonesIndx, 0);
        mVAO.linkBufferAttribute(Shader.WEIGHT_INDEX, mVBOBonesWeights, 0);
        GLES30.glEnableVertexAttribArray(Shader.VERTEX_INDEX);
        GLES30.glEnableVertexAttribArray(Shader.TEXTURE_COORD_INDEX);
        GLES30.glEnableVertexAttribArray(Shader.NORMAL_INDEX);
        GLES30.glEnableVertexAttribArray(Shader.JOINT_INDEX);
        GLES30.glEnableVertexAttribArray(Shader.WEIGHT_INDEX);
        mVAO.unbind();

        mVAO.enableElements(mEBOIndices);

    }

    @Override
    public void update(float dt) {

        mAnimatedModel.update(dt);

    }

    Light light  = new Light(new Vec3(1,1,1),new Vec3(0,0,10));
    @Override
    public void draw() {
        mShader.start();

        mShader.loadModelMat(mModelMat.mData);
        mShader.loadViewMat(mViewMat.mData);
        mShader.loadProjectionMat(mProjMat.mData);
        mShader.loadLight(light);
        mShader.loadJointsTransform(mAnimatedModel.getCurrentJointsTransformData());

        mVAO.bind();
        GLES30.glEnableVertexAttribArray(Shader.VERTEX_INDEX);
        GLES30.glEnableVertexAttribArray(Shader.TEXTURE_COORD_INDEX);
        GLES30.glEnableVertexAttribArray(Shader.NORMAL_INDEX);
        GLES30.glEnableVertexAttribArray(Shader.JOINT_INDEX);
        GLES30.glEnableVertexAttribArray(Shader.WEIGHT_INDEX);

        if(mAnimatedModel.mMesh.mTexture != null){
            GLES30.glActiveTexture(GLES30.GL_TEXTURE0);
            GLES30.glBindTexture(GLES30.GL_TEXTURE_2D,  mAnimatedModel.mMesh.mTexture.getID());
        }

        GLES30.glDrawElements(mDrawMode, mEBOIndices.length(), mEBOIndices.getType(), 0);

        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, 0);
        GLES30.glDisable(GLES30.GL_TEXTURE_2D);

        GLES30.glDisableVertexAttribArray(Shader.VERTEX_INDEX);
        GLES30.glDisableVertexAttribArray(Shader.TEXTURE_COORD_INDEX);
        GLES30.glDisableVertexAttribArray(Shader.NORMAL_INDEX);
        GLES30.glDisableVertexAttribArray(Shader.JOINT_INDEX);
        GLES30.glDisableVertexAttribArray(Shader.WEIGHT_INDEX);

        mShader.stop();
        mVAO.unbind();
    }

    @Override
    public void destroy() {
        //#TODO
        mVAO.destroy();
        mVBOBonesWeights.destroy();
        mVBOVertices.destroy();
        mVBOiBonesIndx.destroy();
        mVBONormals.destroy();
        mVBOTexCoords.destroy();
        mShader.destroy();
        mAnimatedModel.destroy();
    }

    public boolean isIsInitialized() {
        return mIsInitialized;
    }

    public void setDrawMode(int drawMode) {
        mDrawMode = drawMode;
    }
}
