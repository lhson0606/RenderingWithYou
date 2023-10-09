package com.dy.startinganimation.parser;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.InputStream;
import java.util.Vector;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class DyXml {
    Document doc;
    DyNode root;
    public DyXml(InputStream inputStream) throws ParserConfigurationException, IOException, SAXException {
        doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(inputStream);
        root = new DyNode(doc.getDocumentElement());
    }

    public DyXml(Document doc) {
        this.doc = doc;
        root = new DyNode(doc.getDocumentElement());
    }

    public static float[] parseFloats(String str, String separator){
        String[] strs = str.split(separator);
        float[] ret = new float[strs.length];

        for(int i = 0; i<strs.length; ++i){
            ret[i] = Float.parseFloat(strs[i]);
        }

        return ret;
    }

    public Vector<Float> parseFloats(String str, String separator, Vector<Float> ret){
        String[] strs = str.split(separator);

        for(int i = 0; i<strs.length; ++i){
            ret.add(Float.parseFloat(strs[i]));
        }

        return ret;
    }

    public static int[] parseInts(String str, String separator){
        String[] strs = str.split(separator);
        int[] ret = new int[strs.length];

        for(int i = 0; i<strs.length; ++i){
            ret[i] = Integer.parseInt(strs[i]);
        }

        return ret;
    }

    public Vector<Integer> parseInts(String str, String separator, Vector<Integer> ret){
        String[] strs = str.split(separator);

        for(int i = 0; i<strs.length; ++i){
            ret.add(Integer.parseInt(strs[i]));
        }

        return ret;
    }
}
