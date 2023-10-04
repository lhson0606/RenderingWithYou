package com.dy.startinganimation.parser;

import android.content.Context;

import com.dy.startinganimation.animation.AnimRenderer;
import com.dy.startinganimation.animation.AnimatedModel;
import com.dy.startinganimation.animation.Animator;
import com.dy.startinganimation.animation.Joint;
import com.dy.startinganimation.animation.KeyFrame;
import com.dy.startinganimation.maths.Mat4;
import com.dy.startinganimation.model.Mesh;
import com.dy.startinganimation.model.Texture;
import com.dy.startinganimation.shader.Shader;
import com.dy.startinganimation.shader.ShaderHelper;
import com.dy.startinganimation.utils.GLHelper;

import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;
import java.util.Vector;

public class Scene {
    private Vector<Texture> mTextures;
    private Vector<Mesh> mMeshes;
    private Vector<Mat4> mBindPoseMatrices;
    private Map<String, KeyFrame[]> mJointKeyFrames;
    private Joint[] mJoints;
    Scene(){
        mTextures = new Vector<>();
        mMeshes = new Vector<>();
        mJointKeyFrames = new TreeMap<>();
        mBindPoseMatrices = new Vector<>();
    }
    public void addTextures(String id, String path, int texID){
        mTextures.add(new Texture(texID, path));
    }

    public void addMeshes(Mesh mesh){
        mMeshes.add(mesh);
    }

    public void addJointKeyFrames(String id, KeyFrame[] keyFrames){
        mJointKeyFrames.put(id, keyFrames);
    }

    public Texture getTexture(int index){
        if(mTextures.size() <= index){
            return null;
        }

        return mTextures.elementAt(index);
    }

    public void setJoints(Joint[] joints){
        mJoints = joints;
    }

    public Vector<Texture> getTextures() {
        return mTextures;
    }

    public Vector<Mesh> getMeshes() {
        return mMeshes;
    }

    public Map<String, KeyFrame[]> getJointKeyFrames() {
        return mJointKeyFrames;
    }

    public Joint[] getJoints() {
        return mJoints;
    }
    Mesh getMeshByID(String id){
        for(Mesh mesh : mMeshes){
            if(mesh.mID.equals(id)){
                return mesh;
            }
        }
        return null;
    }

    int getJointIndexByName(String name){
        for(int i = 0; i<mJoints.length; ++i){
            if(mJoints[i].mName.equals(name)){
                return i;
            }
        }
        return -1;
    }

    public Vector<Mat4> getBindPoseMatrices() {
        return mBindPoseMatrices;
    }

    public void setmBindPoseMatrices(Vector<Mat4> mBindPoseMatrices) {
        this.mBindPoseMatrices = mBindPoseMatrices;
    }

    public void destroy() {
        //#TODO
    }

    public Animator createAnimator(Context context) throws IOException {
        //#TODO
        Mesh mesh = mMeshes.firstElement();
        AnimatedModel animatedModel = new AnimatedModel(
                mesh,
                mBindPoseMatrices.firstElement(),
                mJoints
        );

        Shader shader = ShaderHelper.getInstance().createShader(
                context.getAssets().open("shaders/anim/ver.glsl"),
                context.getAssets().open("shaders/anim/frag.glsl")
        );

        Animator animator = new Animator(
                animatedModel,
                AnimRenderer.camera.mViewMat,
                AnimRenderer.camera.mProjMat,
                shader
        );

        return animator;
    }
}
