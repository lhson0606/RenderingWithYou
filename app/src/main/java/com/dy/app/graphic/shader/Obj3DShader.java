package com.dy.app.graphic.shader;

import com.dy.app.common.maths.Mat4;
import com.dy.app.common.maths.Vec3;
import com.dy.app.gameplay.board.Tile;
import com.dy.app.graphic.camera.Camera;
import com.dy.app.graphic.model.Obj3D;
import com.dy.app.utils.GLHelper;

public class Obj3DShader extends BaseShader implements Cloneable{
    private Obj3D obj3D;

    public Obj3DShader(String verCode, String fragCode) {
        super(verCode, fragCode);
    }

    public void setObj3D(Obj3D obj3D){
        this.obj3D = obj3D;
    }

    @Override
    public Obj3DShader clone(){
        Obj3DShader shader = new Obj3DShader(mVerCode, mFragCode);
        shader.setObj3D(null);
        return shader;
    }

    @Override
    public void loadUniforms() {
        super.loadUniforms();
        loadModelMat(obj3D.getModelMat().mData);
        loadViewMat(Camera.getInstance().mViewMat.mData);
        loadProjectionMat(Camera.getInstance().mProjMat.mData);

    }

    public void loadExtraTexture(){};

    public void loadProjectionMat(float[] proMat){
        loadMat4(mProMatLoc, proMat);
    }

    public void loadViewMat(float[] viewMat){
        loadMat4(mViewMatLoc, viewMat);
    }

    public void loadModelMat(float[] modelMat){
        loadMat4(mModelMatLoc, modelMat);
    }

    @Override
    public void getAllUniLocations() {
        super.getAllUniLocations();
        mModelMatLoc = GLHelper.getUniLocation(mProgram, MODEL_MAT_NAME);
        mViewMatLoc = GLHelper.getUniLocation(mProgram, VIEW_MAT_NAME);
        mProMatLoc = GLHelper.getUniLocation(mProgram, PRO_MAT_NAME);
    }

    private final String MODEL_MAT_NAME = "uModelMat";
    private int mProMatLoc;
    private final String VIEW_MAT_NAME = "uViewMat";
    private int mViewMatLoc;
    private final String PRO_MAT_NAME = "uProMat";
    private int mModelMatLoc;

    public static final int VERTEX_INDEX = 0;
    public static final int TEXTURE_COORD_INDEX = 1;
    public static final int NORMAL_INDEX = 2;
}
