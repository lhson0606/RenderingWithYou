package com.dy.app.graphic.model;

import android.graphics.Color;
import android.opengl.GLES30;
import android.opengl.Matrix;

import com.dy.app.common.maths.Mat4;
import com.dy.app.common.maths.Vec3;
import com.dy.app.common.maths.Vec4;
import com.dy.app.core.GameCore;
import com.dy.app.graphic.Material;
import com.dy.app.graphic.gl.EBO;
import com.dy.app.graphic.gl.VAO;
import com.dy.app.graphic.gl.VBO;
import com.dy.app.graphic.shader.Shader;

import java.util.Map;

public class Obj3D{

    private VAO VAO;
    private VBO VBOpos;
    private VBO VBOTexCoords;
    private VBO VBONormals;
    private EBO EBOIndices;
    private Shader shader;
    private Mesh mesh;
    private Mat4 modelMat;
    private Vec3 target;//looking at
    private Texture tex;;
    private Material material;
    private Vec4 highlightColor;

    private Map<State, Vec4> highlightColors;

    public Obj3D(Obj3D o) {
        this.mesh = new Mesh(o.mesh);
        this.modelMat = new Mat4(o.modelMat);
        this.shader = new Shader(o.shader.getVerCode(), o.shader.getFragCode());
        this.highlightColors = new java.util.HashMap<>(o.highlightColors);
        this.highlightColor = new Vec4(o.highlightColor);
    }

    public enum State{
        NORMAL,
        HIGHLIGHTED,
        SELECTED,
        SOURCE,
        ENDANGERED
    }

    public Obj3D(Mesh mesh, Shader shader){
        this.mesh = mesh;
        this.modelMat = Mat4.createIdentityMatrix();
        this.shader = shader;
        highlightColors = new java.util.HashMap<>();
        highlightColors.put(State.NORMAL, new Vec4(0, 0, 0, 0));
        highlightColors.put(State.HIGHLIGHTED, new Vec4(1, 1, 0, 0.3f));
        highlightColors.put(State.SELECTED, new Vec4(0, 1, 0, 0.3f));
        highlightColors.put(State.SOURCE, new Vec4(0, 0, 1, 0.3f));
        highlightColors.put(State.ENDANGERED, new Vec4(1, 0, 0, 0.3f));
        highlightColor = highlightColors.get(State.NORMAL);
    }

    //has to be called after GL is initialized
    public void init() {
        shader.init();
        tex.init();
        VAO = new VAO();
        VBOpos = new VBO(mesh.mPositions, 3, 3*Float.BYTES, false);
        VBOTexCoords = new VBO(mesh.mTexCoords, 2, 2*Float.BYTES, false);
        VBONormals = new VBO(mesh.mNormals, 3, 3*Float.BYTES, false);
        EBOIndices = new EBO(mesh.mIndices);
        bindAndEnableAttrib();
    }

    private void bindAndEnableAttrib(){
        VAO.bind();
        VAO.linkBufferAttribute(Shader.VERTEX_INDEX, VBOpos, 0);
        VAO.linkBufferAttribute(Shader.TEXTURE_COORD_INDEX, VBOTexCoords, 0);
        VAO.linkBufferAttribute(Shader.NORMAL_INDEX, VBONormals, 0);
        GLES30.glEnableVertexAttribArray(Shader.VERTEX_INDEX);
        GLES30.glEnableVertexAttribArray(Shader.TEXTURE_COORD_INDEX);
        GLES30.glEnableVertexAttribArray(Shader.NORMAL_INDEX);
        VAO.unbind();

        VAO.enableElements(EBOIndices);
    }

    public void update(float dt) {

    }

    public void draw(Mat4 viewMat, Mat4 projMat) {
        shader.start();
        //load uniforms
        shader.loadLight(GameCore.getInstance().getGameSetting().getLight());
        shader.loadModelMat(modelMat.mData);
        shader.loadViewMat(viewMat.mData);
        shader.loadProjectionMat(projMat.mData);
        shader.loadShineDampener(material.getLightDampener());
        shader.loadReflectivity(material.getReflectivity());
        shader.loadAmbient(GameCore.getInstance().getGameSetting().getAmbientFactor());
        shader.loadHighlightColor(highlightColor);

        VAO.bind();
        GLES30.glEnableVertexAttribArray(Shader.VERTEX_INDEX);
        GLES30.glEnableVertexAttribArray(Shader.TEXTURE_COORD_INDEX);
        GLES30.glEnableVertexAttribArray(Shader.NORMAL_INDEX);

        GLES30.glActiveTexture(GLES30.GL_TEXTURE0);
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D,  tex.getID());
        GLES30.glDrawElements(GLES30.GL_TRIANGLES, EBOIndices.length(), EBOIndices.getType(), 0);

        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, 0);
        GLES30.glDisable(GLES30.GL_TEXTURE_2D);
        shader.stop();
        VAO.unbind();
    }

    public void destroy() {
        VAO.destroy();
        VBOpos.destroy();
        VBOTexCoords.destroy();
        VBONormals.destroy();
        EBOIndices.destroy();
        shader.destroy();
        tex.destroy();
    }

    public void setModelMat(Mat4 modelMat){
        this.modelMat = modelMat;
    }

    public void changeState(State state){
        highlightColor = highlightColors.get(state);
    }

    public void translate(Vec3 translation){
        Matrix.translateM(modelMat.mData, modelMat.mOffset, translation.x, translation.y, translation.z);
    }

    public void setTarget(Vec3 target) {
        this.target = target;
    }

    public void setTex(Texture tex) {
        this.tex = tex;
    }

    public void setMaterial(Material material) {
        this.material = material;
    }
}
