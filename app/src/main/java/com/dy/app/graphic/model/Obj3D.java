package com.dy.app.graphic.model;

import android.graphics.Shader;
import android.opengl.GLES30;
import android.opengl.Matrix;

import com.dy.app.common.maths.Mat4;
import com.dy.app.common.maths.Vec3;
import com.dy.app.common.maths.Vec4;
import com.dy.app.graphic.Material;
import com.dy.app.graphic.gl.EBO;
import com.dy.app.graphic.gl.VAO;
import com.dy.app.graphic.gl.VBO;
import com.dy.app.graphic.shader.BaseShader;
import com.dy.app.graphic.shader.Obj3DShader;
import com.dy.app.graphic.shader.TileShader;
import com.dy.app.setting.GameSetting;

import java.util.Map;
import java.util.concurrent.Semaphore;

public class Obj3D implements Cloneable{

    private VAO VAO;
    private VBO VBOpos;
    private VBO VBOTexCoords;
    private VBO VBONormals;
    private EBO EBOIndices;
    private Obj3DShader shader;
    private Mesh mesh;
    private Mat4 modelMat;
    private Vec3 target;//looking at
    private Texture tex;;
    private Material material;
    private Vec4 highlightColor;
    private final Map<State, Vec4> highlightColors;

    public EBO getEBOIndices() {
        return EBOIndices;
    }

    public BaseShader getShader() {
        return shader;
    }

/*    public Obj3D(Obj3D o) {
        this = (Obj)
    }*/

    public enum State{
        NORMAL,
        HIGHLIGHTED,
        SELECTED,
        SOURCE,
        ENDANGERED
    }

    public Obj3D(Mesh mesh){
        this.mesh = mesh;
        this.modelMat = Mat4.createIdentityMatrix();
        highlightColors = new java.util.HashMap<>();
        highlightColors.put(State.NORMAL, new Vec4(0, 0, 0, 0));
        highlightColors.put(State.HIGHLIGHTED, new Vec4(1, 1, 0, 0.1f));
        highlightColors.put(State.SELECTED, new Vec4(0, 1, 0, 0.1f));
        highlightColors.put(State.SOURCE, new Vec4(0, 0, 1, 0.1f));
        highlightColors.put(State.ENDANGERED, new Vec4(1, 0, 0, 0.1f));
        highlightColor = highlightColors.get(State.NORMAL);
    }

    public void setShader(Obj3DShader shader) {
        this.shader = shader;
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
        VAO.linkBufferAttribute(Obj3DShader.VERTEX_INDEX, VBOpos, 0);
        VAO.linkBufferAttribute(Obj3DShader.TEXTURE_COORD_INDEX, VBOTexCoords, 0);
        VAO.linkBufferAttribute(Obj3DShader.NORMAL_INDEX, VBONormals, 0);
//        GLES30.glEnableVertexAttribArray(Obj3DShader.VERTEX_INDEX);
//        GLES30.glEnableVertexAttribArray(Obj3DShader.TEXTURE_COORD_INDEX);
//        GLES30.glEnableVertexAttribArray(Obj3DShader.NORMAL_INDEX);
        VAO.unbind();

        VAO.enableElements(EBOIndices);
    }

    public void update(float dt) {

    }

    public void draw(Mat4 viewMat, Mat4 projMat) {
        shader.start();
        //load uniforms
        shader.loadUniforms();

        //while(VAO == null);

        VAO.bind();
        GLES30.glEnableVertexAttribArray(Obj3DShader.VERTEX_INDEX);
        GLES30.glEnableVertexAttribArray(Obj3DShader.TEXTURE_COORD_INDEX);
        GLES30.glEnableVertexAttribArray(Obj3DShader.NORMAL_INDEX);

        shader.loadTexUnit();
        GLES30.glActiveTexture(GLES30.GL_TEXTURE0);
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D,  tex.getID());
        shader.loadExtraTexture();

        //GLES30.glDrawElements(GLES30.GL_TRIANGLES, EBOIndices.length(), EBOIndices.getType(), 0)
        GLES30.glDrawElements(GameSetting.getInstance().getDrawMode(), EBOIndices.length(), EBOIndices.getType(), 0);

        GLES30.glDisable(GLES30.GL_TEXTURE_2D);
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, 0);
        GLES30.glDisableVertexAttribArray(Obj3DShader.VERTEX_INDEX);
        GLES30.glDisableVertexAttribArray(Obj3DShader.TEXTURE_COORD_INDEX);
        GLES30.glDisableVertexAttribArray(Obj3DShader.NORMAL_INDEX);
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

    @Override
    public Obj3D clone(){
        Obj3D res = new Obj3D(new Mesh(mesh));
        if(shader!=null){
            res.setShader(shader.clone());
            res.shader.setObj3D(res);
        }
        res.setTex(tex);
        res.setMaterial(material);
        res.setModelMat(new Mat4(modelMat));
        res.setTarget(target);
        return res;
    }

    public void setModelMat(Mat4 modelMat){
        this.modelMat = modelMat;
    }

    public synchronized void changeState(State state){
        highlightColor = highlightColors.get(state);
    }

    public void translate(Vec3 translation){
        Matrix.translateM(modelMat.mData, modelMat.mOffset, translation.x, translation.y, translation.z);
    }

    public void setTranslation(Vec3 translation){
        Matrix.setIdentityM(modelMat.mData, modelMat.mOffset);
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

    public Mat4 getModelMat() {
        return modelMat;
    }

    public Material getMaterial() {
        return material;
    }

    public Mesh getMesh() {
        return mesh;
    }

    public Vec3 getTarget() {
        return target;
    }

    public Texture getTex() {
        return tex;
    }

    public Vec4 getHighlightColor() {
        return highlightColor;
    }

    public Map<State, Vec4> getHighlightColors() {
        return highlightColors;
    }

    public VAO getVAO() {
        return VAO;
    }
}
