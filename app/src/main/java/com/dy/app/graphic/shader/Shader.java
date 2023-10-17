package com.dy.app.graphic.shader;

import android.opengl.GLES20;
import android.opengl.GLES30;

import com.dy.app.common.maths.Mat4;
import com.dy.app.common.maths.Vec3;
import com.dy.app.common.maths.Vec4;
import com.dy.app.graphic.Light;
import com.dy.app.utils.GLHelper;

public class Shader {
    public static final String TAG = "Shader";
    private int mProgram;

    String mVerCode;
    String mFragCode;

    public Shader(String verCode, String fragCode){
        mVerCode = verCode;
        mFragCode = fragCode;
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

    public void loadProjectionMat(float[] proMat){

        loadMat4(mProMatLoc, proMat);
    }

    public void loadViewMat(float[] viewMat){

        loadMat4(mViewMatLoc, viewMat);
    }

    public void loadModelMat(float[] modelMat){
        loadMat4(mModelMatLoc, modelMat);
    }

    public void loadLight(Light light){
        loadVec3(mLightColorLoc, light.getColor());
        loadVec3(mLightPosLoc, light.getPos());
        loadMat4(mLightModelMatLoc, light.getModelMat());
        //load light attenuation (aka natural light) factors
        GLES30.glUniform1f(mAttenConstLoc, light.getAttenuationConstant());
        GLES30.glUniform1f(mAttenLinearLoc, light.getAttenuationLinear());
        GLES30.glUniform1f(mAttenQuadraticLoc, light.getAttenuationQuadratic());
    }

    public void loadShineDampener(float dampener){
        GLES30.glUniform1f(mShineDampenerLoc, dampener);
    }

    public void loadReflectivity(float reflectivity){
        GLES30.glUniform1f(mReflectivityLoc, reflectivity);
    }

    public void loadAmbient(float ambient){
        GLES30.glUniform1f(mAmbientFactorLoc, ambient);
    }

    public void start(){
        GLES30.glUseProgram(mProgram);
    }

    public void stop(){
        GLES30.glUseProgram(0);
    }

    public int getProgram() {
        return mProgram;
    }

    public void destroy(){
        GLES30.glDeleteProgram(mProgram);
    }

    private void loadVec3(int location, Vec3 vec){
        GLES30.glUniform3f(location, vec.x, vec.y, vec.z);
    }

    private void loadMat4(int location, Mat4 mat4){
        GLES30.glUniformMatrix4fv(location, 1, false, mat4.mData, mat4.mOffset);
    }
    private void loadMat4(int location, float[] mat4){
        if(mat4.length != 16){
            GLHelper.handleException(TAG, "loading invalid mat4");
        }

        GLES30.glUniformMatrix4fv(location, 1, false, mat4, 0);
    }

    public void loadHighlightColor(Vec4 color){
        GLES30.glUniform4f(mHighlightColorLoc, color.x, color.y, color.z, color.w);
    }

    private final String MODEL_MAT_NAME = "uModelMat";
    private int mProMatLoc;
    private final String VIEW_MAT_NAME = "uViewMat";
    private int mViewMatLoc;
    private final String PRO_MAT_NAME = "uProMat";
    private int mModelMatLoc;
    private final String LIGHT_POS_NAME = "uLightPos";
    private int mLightPosLoc;
    private final String LIGHT_MODEL_MAT_NAME = "uLightModelMat";
    private int mLightModelMatLoc;
    private final String LIGHT_COLOR_NAME = "uLightColor";
    private int mLightColorLoc;
    private final String SHINE_DAMPENER_NAME = "uShineDampener";
    private int mShineDampenerLoc;
    private final String REFLECTIVITY_NAME = "uReflectivity";
    private int mReflectivityLoc;
    private  final String AMBIENT_FACTOR_NAME = "uAmbientFactor";
    private int mAmbientFactorLoc;
    private final String ATTEN_CONSTANT_NAME = "uLightAttenuationConstant";
    private int mAttenConstLoc;
    private final String ATTEN_LINEAR_NAME = "uLightAttenuationLinear";
    private int mAttenLinearLoc;
    private final String ATTEN_QUADRATIC_NAME = "uLightAttenuationQuadratic";
    private int mAttenQuadraticLoc;
    private final String HIGHLIGHT_COLOR_NAME = "uHighlightColor";
    private int mHighlightColorLoc;

    public void getAllUniLocations(){
        mModelMatLoc = GLHelper.getUniLocation(mProgram, MODEL_MAT_NAME);
        mViewMatLoc = GLHelper.getUniLocation(mProgram, VIEW_MAT_NAME);
        mProMatLoc = GLHelper.getUniLocation(mProgram, PRO_MAT_NAME);
        mLightPosLoc = GLHelper.getUniLocation(mProgram, LIGHT_POS_NAME);
        mLightModelMatLoc = GLHelper.getUniLocation(mProgram, LIGHT_MODEL_MAT_NAME);
        mLightColorLoc = GLHelper.getUniLocation(mProgram, LIGHT_COLOR_NAME);
        mShineDampenerLoc = GLHelper.getUniLocation(mProgram, SHINE_DAMPENER_NAME);
        mReflectivityLoc = GLHelper.getUniLocation(mProgram, REFLECTIVITY_NAME);
        mAmbientFactorLoc = GLHelper.getUniLocation(mProgram, AMBIENT_FACTOR_NAME);
        mAttenConstLoc = GLHelper.getUniLocation(mProgram, ATTEN_CONSTANT_NAME);
        mAttenLinearLoc = GLHelper.getUniLocation(mProgram, ATTEN_LINEAR_NAME);
        mAttenQuadraticLoc = GLHelper.getUniLocation(mProgram, ATTEN_QUADRATIC_NAME);
        mHighlightColorLoc = GLHelper.getUniLocation(mProgram, HIGHLIGHT_COLOR_NAME);
    }

    public String getVerCode() {
        return mVerCode;
    }

    public String getFragCode() {
        return mFragCode;
    }

    public static final int VERTEX_INDEX = 0;
    public static final int TEXTURE_COORD_INDEX = 1;
    public static final int NORMAL_INDEX = 2;

}
