package com.dy.startinganimation.parser;

import android.graphics.Paint;

import com.dy.startinganimation.animation.Joint;
import com.dy.startinganimation.animation.JointTransform;
import com.dy.startinganimation.maths.Mat4;

import java.util.HashMap;

public class VisualScenesParser {
    Joint root;
    HashMap<String, Joint> joints;
    public VisualScenesParser(DyNode libNode) {
        joints = new HashMap<>();
        DyNode root_node = findRootJoint(libNode.getFirstChildHasType("visual_scene"));
        root = extractSingleJoint(null, root_node);
    }

    private Joint extractSingleJoint(Joint parent, DyNode node){
        String jointID = node.getAttribute("id");
        String jointName = node.getAttribute("name");
        Joint joint = new Joint(jointID, jointName);

        joint.mParent = parent;
        Mat4 transform = new Mat4(DyXml.parseFloats(node.getFirstChildHasType("matrix").getContent(), " "));
        joint.mLocalBindTransform = transform.transpose();

        for(DyNode child : node.getChildren("node")){
            Joint childJoint = extractSingleJoint(joint, child);
            joint.mChildren.add(childJoint);
        }

        joints.put(jointID, joint);
        return joint;
    }

    DyNode findRootJoint(DyNode node){
        if(node.attributes.get("type") != null){
            if(node.attributes.get("type").equals("JOINT")){
                return node;
            }
        }

        for(DyNode child : node.children){
            DyNode result = findRootJoint(child);
            if(result != null){
                return result;
            }
        }

        return null;
    }
}
