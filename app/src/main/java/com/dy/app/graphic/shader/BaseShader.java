package com.dy.app.graphic.shader;

import android.opengl.GLES20;
import android.opengl.GLES30;

import com.dy.app.common.maths.Mat4;
import com.dy.app.common.maths.Vec3;
import com.dy.app.utils.GLHelper;

public class BaseShader implements Cloneable{
    public static final String TAG = "BaseShader";
    protected int mProgram;
    protected String mVerCode;
    protected String mFragCode;
    public BaseShader(String verCode, String fragCode){
        mVerCode = verCode;
        mFragCode = fragCode;
    }

    @Override
    public BaseShader clone(){
        BaseShader shader = new BaseShader(mVerCode, mFragCode);
        return shader;
    }

    public void init(){
        int verID = ShaderHelper.getInstance().compileShader(mVerCode, GLES20.GL_VERTEX_SHADER);
        int fragID = ShaderHelper.getInstance().compileShader(mFragCode, GLES20.GL_FRAGMENT_SHADER);
        int programID = GLES30.glCreateProgram();
        GLES20.glAttachShader(programID, verID);
        GLES20.glAttachShader(programID, fragID);
        GLES20.glLinkProgram(programID);
        GLES20.glValidateProgram(programID);

        int[] compiled = new int[1];
        compiled[0] = -1;
        GLES20.glGetProgramiv(programID, GLES20.GL_VALIDATE_STATUS, compiled, 0);

        if(compiled[0] == GLES20.GL_FALSE){
            String errorMsg = GLES20.glGetProgramInfoLog(programID);
            GLHelper.handleException(TAG, errorMsg);
        }

        mProgram = programID;

        GLES20.glDeleteShader(verID);
        GLES20.glDeleteShader(fragID);

        getAllUniLocations();

    }

    protected void loadMat4(int location, Mat4 mat4){
        GLES30.glUniformMatrix4fv(location, 1, false, mat4.mData, mat4.mOffset);
    }
    protected void loadMat4(int location, float[] mat4){
        if(mat4.length != 16){
            GLHelper.handleException(TAG, "loading invalid mat4");
        }

        GLES30.glUniformMatrix4fv(location, 1, false, mat4, 0);
    }

    protected void loadVec3(int location, Vec3 vec){
        GLES30.glUniform3f(location, vec.x, vec.y, vec.z);
    }

    protected void loadFloat( int location, float val){
        GLES30.glUniform1f(location, val);
    }

    protected void getAllUniLocations() {
    }

    public void loadUniforms(){}
    public void start(){
        GLES30.glUseProgram(mProgram);
    }
    public void stop(){
        GLES30.glUseProgram(0);
    }
    public void destroy(){}

    public String getVerCode() {
        return mVerCode;
    }

    public String getFragCode() {
        return mFragCode;
    }
}
