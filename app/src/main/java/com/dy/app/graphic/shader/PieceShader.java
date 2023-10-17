package com.dy.app.graphic.shader;

import android.opengl.GLES20;
import android.opengl.GLES30;

import com.dy.app.common.maths.Mat4;
import com.dy.app.common.maths.Vec3;
import com.dy.app.common.maths.Vec4;
import com.dy.app.core.GameCore;
import com.dy.app.gameplay.piece.Piece;
import com.dy.app.graphic.Light;
import com.dy.app.graphic.camera.Camera;
import com.dy.app.utils.GLHelper;
import com.dy.app.utils.ObjLoader;

public class PieceShader extends Obj3DShader{
    public static final String TAG = "Shader";
    private Piece mPiece = null;

    public PieceShader(String verCode, String fragCode){
        super(verCode, fragCode);
    }

    public void setPiece(Piece piece){
        mPiece = piece;
        super.setObj3D(piece.getObj());
    }

    @Override
    public PieceShader clone(){
        PieceShader shader = new PieceShader(mVerCode, mFragCode);
        shader.setPiece(null);
        return shader;
    }

    @Override
    public void loadUniforms(){
        //load uniforms
        super.loadUniforms();
        loadLight(GameCore.getInstance().getGameSetting().getLight());
        loadShineDamper(mPiece.getObj().getMaterial().getLightDamper());
        loadReflectivity(mPiece.getObj().getMaterial().getReflectivity());
        loadAmbient(GameCore.getInstance().getGameSetting().getAmbientFactor());
        loadHighlightColor(mPiece.getObj().getHighlightColor());
    }

    @Override
    public void getAllUniLocations(){
        super.getAllUniLocations();
        mLightModelMatLoc = GLHelper.getUniLocation(mProgram, LIGHT_MODEL_MAT_NAME);
        mLightPosLoc = GLHelper.getUniLocation(mProgram, LIGHT_POS_NAME);
        mLightColorLoc = GLHelper.getUniLocation(mProgram, LIGHT_COLOR_NAME);

        mShineDamperLoc = GLHelper.getUniLocation(mProgram, SHINE_Damper_NAME);
        mReflectivityLoc = GLHelper.getUniLocation(mProgram, REFLECTIVITY_NAME);

        mAmbientFactorLoc = GLHelper.getUniLocation(mProgram, AMBIENT_FACTOR_NAME);
        //mAttenConstLoc = GLHelper.getUniLocation(mProgram, ATTEN_CONSTANT_NAME);
        //mAttenLinearLoc = GLHelper.getUniLocation(mProgram, ATTEN_LINEAR_NAME);
        //mAttenQuadraticLoc = GLHelper.getUniLocation(mProgram, ATTEN_QUADRATIC_NAME);
        mHighlightColorLoc = GLHelper.getUniLocation(mProgram, HIGHLIGHT_COLOR_NAME);
    }

    public void loadLight(Light light){
        loadMat4(mLightModelMatLoc, light.getModelMat());
        loadVec3(mLightPosLoc, light.getPos());
        loadVec3(mLightColorLoc, light.getColor());
        //load light attenuation (aka natural light) factors
        //GLES30.glUniform1f(mAttenConstLoc, light.getAttenuationConstant());
        //GLES30.glUniform1f(mAttenLinearLoc, light.getAttenuationLinear());
        //GLES30.glUniform1f(mAttenQuadraticLoc, light.getAttenuationQuadratic());
    }

    public void loadShineDamper(float Damper){
        GLES30.glUniform1f(mShineDamperLoc, Damper);
    }

    public void loadReflectivity(float reflectivity){
        GLES30.glUniform1f(mReflectivityLoc, reflectivity);
    }

    public void loadAmbient(float ambientFactor){
        GLES30.glUniform1f(mAmbientFactorLoc, ambientFactor);
    }

    public void loadHighlightColor(Vec4 color){
        GLES30.glUniform4f(mHighlightColorLoc, color.x, color.y, color.z, color.w);
    }

    private final String LIGHT_POS_NAME = "uLightPos";
    private int mLightPosLoc;
    private final String LIGHT_MODEL_MAT_NAME = "uLightModelMat";
    private int mLightModelMatLoc;
    private final String LIGHT_COLOR_NAME = "uLightColor";
    private int mLightColorLoc;
    private final String SHINE_Damper_NAME = "uShineDamper";
    private int mShineDamperLoc;
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

}
