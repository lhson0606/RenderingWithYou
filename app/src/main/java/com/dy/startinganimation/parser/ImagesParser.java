package com.dy.startinganimation.parser;

import com.dy.startinganimation.model.Texture;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

public class ImagesParser {
    HashMap<String, String> textures;
    Vector<String> IDs;

    public ImagesParser(DyNode libNode) {
        textures = new HashMap<>();
        IDs = new Vector<>();
        DyNode imagesNode = libNode.getFirstChildHasType("image");

        if(imagesNode == null){
            return;
        }

        for(DyNode imageNode : libNode.getChildren("image")){
            String id = imageNode.getAttribute("id");
            String path = imageNode.getFirstChildHasType("init_from").getContent();
            IDs.add(id);
            textures.put(id, path);
        }
    }

    public HashMap<String, String> getTextures() {
        return textures;
    }

    public Vector<String> getIDs() {
        return IDs;
    }
}
