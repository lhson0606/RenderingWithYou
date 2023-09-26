package com.java.rendering_with_you_12.utils;

import android.content.Context;

public class EntityFractory {

    public static EntityFractory getInstance(){
        return s_Instance = (s_Instance != null)? s_Instance : new EntityFractory();
    }

    public void CreateInterpolatedTriangle(Context context){

    }
    EntityFractory(){};
    static EntityFractory s_Instance;
}
