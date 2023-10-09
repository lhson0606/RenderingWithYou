package com.dy.startinganimation.parser;

import org.w3c.dom.Node;

import java.util.HashMap;
import java.util.List;
import java.util.Vector;

public class DyNode {
    private Node node;
    HashMap<String, String> attributes = new HashMap<>();
    List<DyNode> children;
    String name;
    public DyNode(Node node){
        this.node = node;
        children = new Vector<>();
        attributes = new HashMap<>();
        name = node.getNodeName();
        parseAttributes();
        parseChildren();
    }

    void parseAttributes(){
        for(int i = 0; i<node.getAttributes().getLength(); ++i){
            Node attribute = node.getAttributes().item(i);
            attributes.put(attribute.getNodeName(), attribute.getNodeValue());
        }
    }

    void parseChildren(){
        for(int i = 0; i<node.getChildNodes().getLength(); ++i){
            Node child = node.getChildNodes().item(i);
            if(child.getNodeType() == Node.ELEMENT_NODE){
                children.add(new DyNode(child));
            }
        }
    }

    public String getAttribute(String name){
        return attributes.get(name);
    }

    public List<DyNode> getChildren(){
        return children;
    }

    public List<DyNode> getChildren(String type){
        List<DyNode> ret = new Vector<>();
        for(DyNode child : children){

            if(child.getName().equals(type)){
                ret.add(child);
            }

        }
        return ret;
    }

    public String getName(){
        return name;
    }

    public String getContent(){
        return node.getTextContent();
    }

    public DyNode findContainedNodeByID(String ID){
        if(getAttribute("id") != null){

            if(getAttribute("id").equals(ID)){
                return this;
            }

        }

        for(DyNode child : children){
            DyNode ret = child.findContainedNodeByID(ID);

            if(ret != null){
                return ret;
            }

        }

        return null;
    }

    boolean hasAttribute(String name){
        return attributes.containsKey(name);
    }

    public List<DyNode> getChildrenHavingAttribute(String attribName){
        List<DyNode> ret = new Vector<>();

        for(DyNode child : children){

            if(child.hasAttribute(attribName)){
                ret.add(child);
            }

        }

        return ret;
    }

    public DyNode getFirstChild(){
        return children.get(0);
    }

    public DyNode getFirstChildHasType(String type){
        for(DyNode child : children){

            if(child.getName().equals(type)){
                return child;
            }

        }

        return null;
    }

    public boolean hasChildType(String type){
        for(DyNode child : children){

            if(child.getName().equals(type)){
                return true;
            }

        }

        return false;
    }
}
