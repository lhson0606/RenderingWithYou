package com.dy.startinganimation.parser;

import com.dy.startinganimation.animation.JointTransform;
import com.dy.startinganimation.animation.KeyFrame;
import com.dy.startinganimation.maths.Mat4;

import java.util.HashMap;

public class AnimationParser {
    HashMap<String, KeyFrame[]> jointKeyFrames;
    public AnimationParser(DyNode libNode) {
        jointKeyFrames = new HashMap<>();
        for(DyNode animation : libNode.getChildren("animation")){
            parseAnimation(animation);
        }
    }

    private void parseAnimation(DyNode animationNode){
        String animationID = animationNode.getAttribute("id");

        DyNode source_input_node = animationNode.findContainedNodeByID(animationID + "-input");
        DyNode source_output_node = animationNode.findContainedNodeByID(animationID + "-output");

        /*if(source_input_node == null || source_output_node == null){
            animationNode = animationNode.getFirstChildHasType("animation");
            animationID = animationNode.getAttribute("id");
            source_input_node = animationNode.findContainedNodeByID(animationID + "-input");
            source_output_node = animationNode.findContainedNodeByID(animationID + "-output");
        }*/
        for(DyNode source : animationNode.getChildren("source")){
            String sourceID = source.getAttribute("id");
            if(sourceID.equals(animationID + "-input")){
                source_input_node = source;
            }else if(sourceID.equals(animationID + "-output")){
                source_output_node = source;
            }
        }

        final float time_stamps[] = DyXml.parseFloats(source_input_node.getFirstChildHasType("float_array").getContent(), " ");
        final float transforms_data[] = DyXml.parseFloats(source_output_node.getFirstChildHasType("float_array").getContent(), " ");
        KeyFrame keyFrames[] = new KeyFrame[time_stamps.length];

        for(int i = 0; i<time_stamps.length; ++i){
            float mat_data[] = new float[16];
            for(int j = 0;j< 16;++j){
                mat_data[j] = transforms_data[i*16+j];
            }
            Mat4 transform = new Mat4(mat_data);
            //transpose before use
            JointTransform jointTransform = new JointTransform(transform.transpose());
            keyFrames[i] = new KeyFrame(jointTransform, time_stamps[i]);
        }

        String jointID = animationNode.getFirstChildHasType("channel").getAttribute("target").split("/")[0];
        jointKeyFrames.put(jointID, keyFrames);
    }
}
