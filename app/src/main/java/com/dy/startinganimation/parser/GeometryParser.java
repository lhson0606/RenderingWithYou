package com.dy.startinganimation.parser;

import android.opengl.GLES30;

import com.dy.startinganimation.maths.Vec2;
import com.dy.startinganimation.maths.Vec3;
import com.dy.startinganimation.model.Mesh;
import com.dy.startinganimation.utils.GLHelper;
import com.google.android.material.color.DynamicColorsOptions;

import java.util.Vector;

public class GeometryParser {
    private Vector<Geometry> geometries;
    int bonesIndicesData[] = null;
    Vector<Integer> bonesIndices = new Vector<>();
    public GeometryParser(DyNode geometryLib){

        geometries = new Vector<>();

        for(DyNode geometry : geometryLib.getChildren()){
            geometries.add(parseGeometry(geometry));
        }

    }

    public Vector<Geometry> getGeometries(){
        return geometries;
    }

    private Geometry parseGeometry(DyNode geometry) {
        Geometry ret = new Geometry();
        ret.ID = geometry.getAttribute("id");
        ret.name = geometry.getAttribute("name");
        DyNode meshNode = geometry.getFirstChildHasType("mesh");
        Vector<Vertex> vertices = new Vector<>();
        Vector<Integer> indices = new Vector<>();
        Vector<Vec3> normals = new Vector<>();
        Vector<Vec2> texCoords = new Vector<>();

        float posData[] = DyXml.parseFloats(meshNode.findContainedNodeByID(ret.name + "-mesh-positions-array").getContent(), " ");
        float normalData[] = DyXml.parseFloats(meshNode.findContainedNodeByID(ret.name + "-mesh-normals-array").getContent(), " ");

        for(int i = 0; i<posData.length/3; ++i){
            Vertex vertex = new Vertex();
            vertex.pos = new Vec3(posData[i*3], posData[i*3 + 1], posData[i*3 + 2]);
            vertex.index = vertices.size();
            vertex.boneNumber = i;
            vertices.add(vertex);
        }

        for(int i = 0; i<normalData.length/3; ++i){
            normals.add(new Vec3(normalData[i*3], normalData[i*3 + 1], normalData[i*3 + 2]));
        }

        for(int i = 0;; ++i){
            DyNode source = meshNode.findContainedNodeByID(ret.name + "-mesh-map-" + i);
            if(source == null){
                break;
            }

            float[] texCoordsData = DyXml.parseFloats(source.findContainedNodeByID(ret.name + "-mesh-map-" + i + "-array").getContent(), " ");

            for(int j = 0 ;j<texCoordsData.length/2; ++j){
                texCoords.add(new Vec2(texCoordsData[j*2], texCoordsData[j*2 + 1]));
            }

        }

        for(DyNode faceNode : meshNode.getChildren("triangles")){
            processFaceNode(faceNode, vertices, indices);
        }

        for(DyNode faceNode : meshNode.getChildren("polylist")){
            processFaceNode(faceNode, vertices, indices);
        }

        float[] positionsData = new float[vertices.size()*3];
        float[] texCoordsData = new float[vertices.size()*2];
        float[] normalsData = new float[vertices.size()*3];
        bonesIndicesData = new int[vertices.size()];

        for(int i = 0; i<vertices.size(); ++i){
            bonesIndicesData[i] = vertices.get(i).boneNumber;
            positionsData[i*3] = vertices.get(i).pos.x;
            positionsData[i*3 + 1] = vertices.get(i).pos.y;
            positionsData[i*3 + 2] = vertices.get(i).pos.z;
            texCoordsData[i*2] = texCoords.get(vertices.get(i).texCoordIndex).x;
            texCoordsData[i*2 + 1] = 1 - texCoords.get(vertices.get(i).texCoordIndex).y;
            normalsData[i*3] = normals.get(vertices.get(i).normalIndex).x;
            normalsData[i*3 + 1] = normals.get(vertices.get(i).normalIndex).y;
            normalsData[i*3 + 2] = normals.get(vertices.get(i).normalIndex).z;
        }

        for(Vertex v : vertices){
            if(!v.isSet()){
                v.texCoordIndex = 0;
                v.normalIndex = 0;
            }
        }

        ret.mesh = new Mesh(ret.ID, ret.name,
                positionsData,
                texCoordsData,
                normalsData,
                GLHelper.toIntArray(indices),
                null,
                null,
                null,
                null);

        return ret;
    }

    private void processFaceNode(DyNode faceNode,Vector<Vertex>vertices, Vector<Integer> indices){
        DyNode input_vertex = null;
        DyNode input_texCoord = null;
        DyNode input_normal = null;

        for(DyNode input : faceNode.getChildren("input")){
            String semantic = input.getAttribute("semantic");
            if(semantic.equals("VERTEX")){
                input_vertex = input;
            }else if(semantic.equals("TEXCOORD")){
                input_texCoord = input;
            }else if(semantic.equals("NORMAL")){
                input_normal = input;
            }
        }

        final int posOffset = Integer.parseInt(input_vertex.getAttribute("offset"));
        final int texCoordOffset = Integer.parseInt(input_texCoord.getAttribute("offset"));
        final int normalOffset = Integer.parseInt(input_normal.getAttribute("offset"));
        final int stride = findStride(faceNode);

        int[] p_data = DyXml.parseInts(faceNode.getFirstChildHasType("p").getContent(), " ");

        //[ThinMatrix] algorithm
        //see https://github.com/TheThinMatrix/OpenGL-Animation
        for(int i = 0; i<p_data.length/stride; ++i){
            final int posIndex = p_data[i*stride + posOffset];
            final int texCoordIndex = p_data[i*stride + texCoordOffset];
            final int normalIndex = p_data[i*stride + normalOffset];
            processVertex(vertices, posIndex, texCoordIndex, normalIndex, indices);
        }
    }

    private void processVertex(Vector<Vertex> vertices, int posIndex, int texCoordIndex, int normalIndex, Vector<Integer> indices){
        Vertex currentVertex = vertices.get(posIndex);
        if(!currentVertex.isSet()){
            currentVertex.texCoordIndex = texCoordIndex;
            currentVertex.normalIndex = normalIndex;
            currentVertex.isSet = true;
            indices.add(posIndex);
            bonesIndices.add(currentVertex.boneNumber);
        }else{
            dealWithAlreadyProcessedVertex(currentVertex, texCoordIndex, normalIndex, indices, vertices);
        }

    }


    //see https://github.com/TheThinMatrix/OpenGL-Animation
    private Vertex dealWithAlreadyProcessedVertex(Vertex currentVertex, int texCoordIndex, int normalIndex, Vector<Integer> indices, Vector<Vertex> vertices) {

        if (currentVertex.hasSameTexCoordsAndNormals(texCoordIndex, normalIndex)) {
            indices.add(currentVertex.index);
            bonesIndices.add(currentVertex.boneNumber);
            return currentVertex;
        } else {
            Vertex anotherVertex = currentVertex.dupVertex;
            if (anotherVertex != null) {
                return dealWithAlreadyProcessedVertex(anotherVertex, texCoordIndex, normalIndex, indices, vertices);
            } else {
                Vertex duplicateVertex = new Vertex();
                duplicateVertex.index = vertices.size();
                duplicateVertex.pos = currentVertex.pos;
                duplicateVertex.texCoordIndex = texCoordIndex;
                duplicateVertex.normalIndex = normalIndex;
                duplicateVertex.hasLine = currentVertex.hasLine;
                duplicateVertex.boneNumber = currentVertex.boneNumber;
                currentVertex.dupVertex = duplicateVertex;
                vertices.add(duplicateVertex);
                indices.add(duplicateVertex.index);
                bonesIndices.add(duplicateVertex.boneNumber);
                return duplicateVertex;
            }
        }
    }

    private int findStride(DyNode faceNode){
        int stride = 0;
        for(DyNode inputNode : faceNode.getChildren("input")){
            int offset = Integer.parseInt(inputNode.getAttribute("offset"));
            if(offset > stride){
                stride = offset;
            }
        }
        return stride + 1;
    }

    class Vertex{
        int index = -1;
        Vec3 pos = new Vec3(0,0,0);
        int texCoordIndex = -1;
        int normalIndex = -1;
        Vertex dupVertex = null;
        boolean hasLine = false;
        int boneNumber;
        public Vertex(){};
        boolean isSet = false;
        boolean isSet(){
            return isSet;
        }
        boolean hasSameTexCoordsAndNormals(int texCoordIndexOther, int normalIndexOther){
            return texCoordIndexOther == texCoordIndex && normalIndexOther == normalIndex;
        }
    }

}
