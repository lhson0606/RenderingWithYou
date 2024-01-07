package com.dy.app.graphic.shadow;

import android.content.Context;
import android.opengl.Matrix;

import com.dy.app.common.maths.Mat4;
import com.dy.app.common.maths.Vec3;
import com.dy.app.graphic.Light;
import com.dy.app.graphic.shader.ShaderHelper;
import com.dy.app.graphic.shader.ShadowMapShader;

public class ShadowMapTechnique {
    public static final String SHADOW_MAP_VER = "glsl/shadow_map/ver.glsl";
    public static final String SHADOW_MAP_FRAG = "glsl/shadow_map/frag.glsl";
    private Light lightSrc;
    private Mat4 lightViewPortMat = new Mat4();
    private Mat4 lightProMat = new Mat4();
    private float mBottom = -1;
    private float mTop = 1;
    private float mNear = 1f;
    private float mFar = 50;
    public ShadowMapTechnique(Light lightSrc){
        this.lightSrc = lightSrc;
    }

    public void updateView(int width, int height){
        float aspect_ratio = 16f/9f;
        if(height != 0){
            aspect_ratio = (float)width/(float)height;
        }
        Vec3 mUp = lightSrc.getUp();
        Vec3 mPos = lightSrc.getPos();
        Vec3 mCenter = new Vec3();//[0,0,0]
        Matrix.setLookAtM(lightViewPortMat.mData, lightViewPortMat.mOffset,
                mPos.x, mPos.y, mPos.z,
                mCenter.x, mCenter.y, mCenter.z,
                mUp.x, mUp.y, mUp.z);
        Matrix.frustumM(lightProMat.mData, lightProMat.mOffset,-aspect_ratio, aspect_ratio, mBottom, mTop, mNear, mFar);
    }

    public Mat4 getLightTransformMatrix() {
        return lightProMat.multiplyMM(lightViewPortMat);
    }
}
