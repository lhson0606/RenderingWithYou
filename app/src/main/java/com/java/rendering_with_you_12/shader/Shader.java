package com.java.rendering_with_you_12.shader;

import android.opengl.GLES20;
import android.opengl.GLES30;

import com.java.rendering_with_you_12.Model.Light;
import com.java.rendering_with_you_12.maths.Vec3;
import com.java.rendering_with_you_12.utils.GLHelper;

public class Shader {
    public static final String TAG = "Shader";
    private int m_Program;
        public Shader(String verCode, String fragCode){
        int verID = ShaderHelper.getInstance().compileShader(verCode, GLES20.GL_VERTEX_SHADER);
        int fragID = ShaderHelper.getInstance().compileShader(fragCode, GLES20.GL_FRAGMENT_SHADER);
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

        m_Program = programID;

        GLES20.glDeleteShader(verID);
        GLES20.glDeleteShader(fragID);

        getAllUniLocations();
    }

    public void loadProjectionMat(float[] proMat){

            loadMat4(m_ProMatLoc, proMat);
    }

    public void loadViewMat(float[] viewMat){

            loadMat4(m_ViewMatLoc, viewMat);
    }

    public void loadModelMat(float[] modelMat){
        loadMat4(m_ModelMatLoc, modelMat);
    }

    public void loadLight(Light light){
        loadVec3(m_LightColorLoc, light.getColor());
        loadVec3(m_LightPosLoc, light.getPosition());
        loadMat4(m_LightModelMatLoc, light.getModelMat());
    }

    public void loadShineDampener(float dampener){
        GLES30.glUniform1f(m_ShineDampenerLoc, dampener);
    }

    public void loadReflectivity(float reflectivity){
        GLES30.glUniform1f(m_ReflectivityLoc, reflectivity);
    }

    public void loadAmbient(float ambient){
        GLES30.glUniform1f(m_AmbientFactorLoc, ambient);
    }

    public void start(){
        GLES30.glUseProgram(m_Program);
    }

    public void stop(){
        GLES30.glUseProgram(0);
    }

    public int getProgram() {
        return m_Program;
    }

    public void destroy(){
        GLES30.glDeleteProgram(m_Program);
    }

    private void loadVec3(int location, Vec3 vec){
        GLES30.glUniform3f(location, vec.x, vec.y, vec.z);
    }
    private void loadMat4(int location, float[] mat4){
        if(mat4.length != 16){
            GLHelper.handleException(TAG, "loading invalid mat4");
        }

        GLES30.glUniformMatrix4fv(location, 1, false, mat4, 0);
    }

    private final String MODEL_MAT_NAME = "uModelMat";
    private int m_ProMatLoc;
    private final String VIEW_MAT_NAME = "uViewMat";
    private int m_ViewMatLoc;
    private final String PRO_MAT_NAME = "uProMat";
    private int m_ModelMatLoc;
    private final String LIGHT_POS_NAME = "uLightPos";
    private int m_LightPosLoc;
    private final String LIGHT_MODEL_MAT_NAME = "uLightModelMat";
    private int m_LightModelMatLoc;
    private final String LIGHT_COLOR_NAME = "uLightColor";
    private int m_LightColorLoc;
    private final String SHINE_DAMPENER_NAME = "uShineDampener";
    private int m_ShineDampenerLoc;
    private final String REFLECTIVITY_NAME = "uReflectivity";
    private int m_ReflectivityLoc;
    private  final String AMBIENT_FACTOR_NAME = "uAmbientFactor";
    private int m_AmbientFactorLoc;

    public void getAllUniLocations(){
        m_ModelMatLoc = GLHelper.getUniLocation(m_Program, MODEL_MAT_NAME);
        m_ViewMatLoc = GLHelper.getUniLocation(m_Program, VIEW_MAT_NAME);
        m_ProMatLoc = GLHelper.getUniLocation(m_Program, PRO_MAT_NAME);
        m_LightPosLoc = GLHelper.getUniLocation(m_Program, LIGHT_POS_NAME);
        m_LightModelMatLoc = GLHelper.getUniLocation(m_Program, LIGHT_MODEL_MAT_NAME);
        m_LightColorLoc = GLHelper.getUniLocation(m_Program, LIGHT_COLOR_NAME);
        m_ShineDampenerLoc = GLHelper.getUniLocation(m_Program, SHINE_DAMPENER_NAME);
        m_ReflectivityLoc = GLHelper.getUniLocation(m_Program, REFLECTIVITY_NAME);
        m_AmbientFactorLoc = GLHelper.getUniLocation(m_Program, AMBIENT_FACTOR_NAME);
    }

    public static final int VERTEX_INDEX = 0;
    public static final int TEXTURE_COORD_INDEX = 1;
    public static final int NORMAL_INDEX = 2;

}
