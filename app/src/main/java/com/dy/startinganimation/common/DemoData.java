package com.dy.startinganimation.common;

import com.example.startinganimation.R;

import java.util.Vector;

public class DemoData {
    public Vector<DemoOBJ> obj_demo_list = new Vector<DemoOBJ>();
    public Vector<DemoDAE> dae_demo_list = new Vector<DemoDAE>();
    public static DemoData getInstance(){
        return instance = (instance == null) ? new DemoData() : instance;
    }
    private DemoData(){
        dae_demo_list.add(new DemoDAE(
                "models/dae/model/model.dae",
                "running",
                "https://youtu.be/z0jb1OBw45I?si=4AjuSwrrlac0eru8",
                R.raw.running));
        dae_demo_list.add(new DemoDAE(
                "models/dae/wolf/wolf.dae",
                "wolf",
                "https://free3d.com/3d-model/wolf-rigged-and-game-ready-42808.html",
                R.raw.wolf));
    }
    private static DemoData instance = null;

}
