package com.dy.startinganimation.parser;

import com.dy.startinganimation.maths.Mat4;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

public class Skin {
    Mat4 BSM;
    Vector<Integer> jointIDs;
    Vector<String> jointNames;
    Vector<Float> weights;

    public Skin() {
        jointIDs = new Vector<>();
        jointNames = new Vector<>();
        weights = new Vector<>();
    }
}
