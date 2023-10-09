package com.dy.startinganimation.parser;

import com.dy.startinganimation.animation.Joint;
import com.dy.startinganimation.gl.Vertex;
import com.dy.startinganimation.maths.Mat4;

import java.util.HashMap;
import java.util.List;
import java.util.Vector;

public class ControllerParser {
    HashMap<String, Skin> skins;
    String[] jointNames = null;
    public ControllerParser(DyNode libNode) {
        skins = new HashMap<>();

        for(DyNode controller : libNode.getChildren("controller")) {
            String controllerID = controller.getAttribute("id");
            DyNode skinNode = controller.getFirstChildHasType("skin");
            parseSkin(skinNode, controllerID);
        }
    }

    private void parseSkin(DyNode skinNode, String controllerID) {
        Skin skin = new Skin();
        String skinSource = skinNode.getAttribute("source").substring(1);
        List<DyNode> sourceNodes = skinNode.getChildren("source");

        DyNode bindShapeMatrixNode = skinNode.getFirstChildHasType("bind_shape_matrix");
        float[] bindShapeMatrixData = DyXml.parseFloats(bindShapeMatrixNode.getContent(), " ");
        Mat4 bindShapeMatrix = new Mat4(bindShapeMatrixData);

        jointNames = null;
        float[] weights = null;
        for(DyNode sourceNode : sourceNodes) {
            String sourceID = sourceNode.getAttribute("id");
            if(sourceID.equals(controllerID + "-joints")) {
                jointNames = sourceNode.getFirstChildHasType("Name_array").getContent().split(" ");
            }            else if(sourceID.equals(controllerID+"-weights")){
                weights = DyXml.parseFloats(sourceNode.getFirstChildHasType("float_array").getContent(), " ");
            }
        }

        DyNode vertex_weights_node = skinNode.getFirstChildHasType("vertex_weights");

        skin.BSM = bindShapeMatrix;

        processSkin(vertex_weights_node, skin, jointNames, weights);
        skins.put(skinSource, skin);
    }

    private void processSkin(DyNode vertex_weights_node, Skin skin, String[] jointNames, float[] weights) {
        int vcount[] = DyXml.parseInts(vertex_weights_node.getFirstChildHasType("vcount").getContent(), " ");
        int v[] = DyXml.parseInts(vertex_weights_node.getFirstChildHasType("v").getContent(), " ");

        DyNode input_joint = null;
        DyNode input_weight = null;

        for(DyNode input: vertex_weights_node.getChildren("input")){
            String semantic = input.getAttribute("semantic");
            if(semantic.equals("JOINT")){
                input_joint = input;
            }else if(semantic.equals("WEIGHT")){
                input_weight = input;
            }
        }

        final int stride = 2;
        final int joint_offset = Integer.parseInt(input_joint.attributes.get("offset"));
        final int weight_offset = Integer.parseInt(input_weight.attributes.get("offset"));
        int v_index = 0;
        for(int i = 0; i<vcount.length; ++i){
            int jointCount = vcount[i];

            for(int j = 0; j<jointCount; ++j){
                final int jointIndex = v[v_index+ joint_offset];
                final int weightIndex = v[v_index + weight_offset];

                if(j<4){
                    skin.jointIDs.add(jointIndex);
                    skin.jointNames.add(jointNames[jointIndex]);
                    skin.weights.add(weights[weightIndex]);
                }

                v_index+= stride;
            }

            for(int j = jointCount; j<4; ++j){
                skin.jointIDs.add(0);
                skin.weights.add(0f);
                skin.jointNames.add("");
            }

        }
    }
}
