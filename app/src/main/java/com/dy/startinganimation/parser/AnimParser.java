package com.dy.startinganimation.parser;

import android.content.Context;
import android.graphics.Paint;
import android.opengl.Matrix;
import android.util.Log;
import android.widget.Toast;

import com.dy.startinganimation.animation.Joint;
import com.dy.startinganimation.animation.JointTransform;
import com.dy.startinganimation.animation.KeyFrame;
import com.dy.startinganimation.gl.Vertex;
import com.dy.startinganimation.maths.Mat4;
import com.dy.startinganimation.maths.Vec2;
import com.dy.startinganimation.maths.Vec3;
import com.dy.startinganimation.model.Mesh;
import com.dy.startinganimation.utils.GLHelper;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.Vector;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class AnimParser {
    static Mat4 CORRECTION;
    static Context mContext;
    static String mLocalPath = "models/";
    public static Scene parse(Context context, InputStream inputStream) throws ParserConfigurationException, IOException, SAXException {
        CORRECTION = new Mat4();
        CORRECTION.setIdentityMat();
        Matrix.setRotateM(CORRECTION.mData, 0, -90, 1, 0, 0);

        Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(inputStream);

        Node root = doc.getFirstChild();
        Scene scene = new Scene();

        mContext = context;

        NodeList nodeList = root.getChildNodes();

        for(int i = 0; i<nodeList.getLength(); ++i){
            Node tempNode = nodeList.item(i);

            if(tempNode.getNodeType() != Node.ELEMENT_NODE) {
                continue;
            }

            String name = tempNode.getNodeName();

            if(name.equals("library_images")){
                extractTexture(scene, tempNode);
            }else if(name.equals("library_geometries")){
                extractGeometries(scene, tempNode);
            }else if(name.equals("library_animations")){
                extractAnimations(scene, tempNode);
            }else if(name.equals("library_controllers")){
                extractControllers(scene, tempNode);
            }else if(name.equals("library_visual_scenes")) {
                extractVisualScenes(scene, tempNode);
            }

        }
        //#TODO
        return scene;
    }

    private static void extractVisualScenes(Scene scene, Node tempNode) {
        Node visualSceneNodes = getChildNode("visual_scene", tempNode);
        Node armature_node = getChildNodeByID("node", "Armature", visualSceneNodes);
        Node root_node = getChildNode("node", armature_node);
        extractSingleJoint(scene, null, root_node);
    }

    private static Joint extractSingleJoint(Scene scene, Joint parent, Node joint_node){
        String jointID = getAttribute("id", joint_node);
        Joint joint = scene.getJoints()[scene.getJointIndexByName(jointID)];
        joint.mParent = parent;
        Node matrix_node = getChildNode("matrix", joint_node);
        float matrix_data[] = extractFloatArray(matrix_node, 16);
        joint.mLocalBindTransform = new Mat4(matrix_data);
        joint.mLocalBindTransform = joint.mLocalBindTransform.transpose();

        if(getChildNode("node", joint_node) == null){
            return joint;
        }

        for(Node child_node : getAllChildNodeByType("node", joint_node)){
            joint.mChildren.add(extractSingleJoint(scene, joint, child_node));
        }

        return joint;
    }

    private static void extractControllers(Scene scene, Node parent) {
        //#TODO multiple meshes
        Vector<Node> controllers = getAllChildNodeByType("controller", parent);

        for(Node controller : controllers){

            final String controllerID = getAttribute("id", controller);
            final String controllerName = getAttribute("name", controller);
            final Node skin_node = getChildNode("skin", controller);

            final String meshID = getAttribute("source", skin_node).substring(1);//ignore "#"

            float bind_shape_matrix_data[] = extractFloatArray(getChildNode("bind_shape_matrix", skin_node), 16);
            Mat4 BSM = new Mat4(bind_shape_matrix_data);
            scene.getBindPoseMatrices().add(BSM);

            Node curNode = getChildNodeByID("source", controllerID+"-joints", skin_node);

            String jointsID[] = getChildNodeByID("Name_array",controllerID+ "-joints-array", curNode).getTextContent().split(" ");

            //extract inverse bind pose matrix
            curNode = getChildNodeByID("source", controllerID+"-bind_poses", skin_node);
            curNode = getChildNodeByID("float_array", controllerID+"-bind_poses-array", curNode);
            Mat4[] IBPMs = extractMat4(curNode);

            curNode = getChildNodeByID("source", controllerID + "-weights", skin_node);
            curNode = getChildNodeByID("float_array", controllerID+"-weights-array", curNode);
            float weights_data[] = extractFloatArray(curNode);

            Joint joints[] = new Joint[jointsID.length];

            for(int i =0; i<joints.length; ++i){
                joints[i] = new Joint(
                        jointsID[i],
                        i,
                        //IBPMs[i],
                        scene.getJointKeyFrames().get(jointsID[i]+"/transform")
                );
            }

            scene.setJoints(joints);

            curNode = getChildNode("vertex_weights", skin_node);
            int vcount[] = extractIntArray(getChildNode("vcount", curNode));
            int v[] = extractIntArray(getChildNode("v", curNode));

            final int joint_offset = getAttribValI("offset", getChildNodeByAttribVal("input", "semantic", "JOINT", curNode));
            final int weight_offset = getAttribValI("offset", getChildNodeByAttribVal("input", "semantic", "WEIGHT", curNode));

            //extract joint and weight data
            final int stride = findStride(curNode);
            int vindx = 0;

            Mesh currentMesh = scene.getMeshByID(meshID);
            for(int i = 0; i<vcount.length; ++i){

                for(int j=0;j<vcount[i];++j){
                    int jointIndx = v[vindx+j*stride + joint_offset];
                    int weightIndx = v[vindx+j*stride + weight_offset];

                    if(j>=Vertex.MAX_JOINTS){
                        break;
                    }

                    currentMesh.mVertices[i].mJointIDs[j] = scene.getJointIndexByName(jointsID[jointIndx]);
                    currentMesh.mVertices[i].mWeights[j] = weights_data[weightIndx];

                }
                //normalize
                currentMesh.mVertices[i].setWeightsNormalized();

                vindx += vcount[i]*stride;
            }

        }
    }

    private static int findStride(Node curNode) {
        int max = 0;

        for(Node input : getAllChildNodeByType("input", curNode)){
            if(max<getAttribValI("offset", input)){
                max = getAttribValI("offset", input);
            }
        }

        return max+1;
    }

    private static int[] extractIntArray(Node parent) {

        String[] data = parent.getTextContent().split(" ");
        int ret[] = new int[data.length];

        for(int i = 0; i<ret.length; ++i){
            ret[i] = Integer.parseInt(data[i]);
        }

        return ret;
    }

    private static Mat4[] extractMat4(Node node) {
        String countData = getAttribute("count", node);
        int count = Integer.parseInt(getAttribute("count", node));
        Mat4 ret[] = new Mat4[count/16];

        String data[] = node.getTextContent().split(" ");
        for(int i = 0; i<ret.length; ++i){
            float matData[] = new float[16];

            for(int j = 0; j< 16; ++j){
                matData[j] = Float.parseFloat(data[i*16+j]);
            }

            ret[i] = new Mat4(matData);
        }


        return ret;
    }

    private static void extractAnimations(Scene scene, Node tempNode) {
        Vector<Node> animationNodes = getAllChildNodeByType("animation", tempNode);

        for(Node animNode : animationNodes){

            String animID = getAttribute("id", animNode);

            Node curNode = getChildNodeByID("source", animID+ "-input", animNode);
            curNode = getChildNodeByID("float_array", animID+ "-input-array", curNode);
            float timeStampData[] = extractFloatArray(curNode);

            curNode = getChildNodeByID("source", animID+ "-output", animNode);
            curNode = getChildNodeByID("float_array", animID+ "-output-array", curNode);
            float transformMatData[] = extractFloatArray(curNode);

            curNode = getChildNode("channel", animNode);
            final String boneName = getAttribute("target", curNode);

            KeyFrame keyFrames[] = new KeyFrame[timeStampData.length];

            for(int i = 0; i<keyFrames.length; ++i){
                float matData[] = new float[16];

                for(int j = 0; j< 16; ++j){
                    matData[j] = transformMatData[i*16+j];
                }

                JointTransform jointTransform = new JointTransform(new Mat4(matData));
                jointTransform.mTransform = jointTransform.mTransform.transpose();
                keyFrames[i] = new KeyFrame(
                        jointTransform,
                        timeStampData[i]
                );
            }

            scene.addJointKeyFrames(boneName, keyFrames);

        }

    }

    private static void extractGeometries(Scene scene, Node parent) {
        Vector<Node> geometries = getAllChildNodeByType("geometry", parent);

        for (Node geometry : geometries) {

            String geometryID = getAttribute("id", geometry);
            String geometryName = getAttribute("name", geometry);
            geometry = getChildNode("mesh", geometry);

            //positions
            Node float_array_node = getChildNodeByID("source", geometryID + "-positions", geometry);
            float_array_node = getChildNodeByID("float_array", geometryID + "-positions-array", float_array_node);
            float positionsData[] = extractFloatArray(float_array_node);

            //normals
            float_array_node = getChildNodeByID("source", geometryID + "-normals", geometry);
            float_array_node = getChildNodeByID("float_array", geometryID + "-normals-array", float_array_node);
            float normalsData[] = extractFloatArray(float_array_node);

            //colors-Col
            float_array_node = getChildNodeByID("source", geometryID + "-map-0", geometry);
            float_array_node = getChildNodeByID("float_array", geometryID + "-map-0-array", float_array_node);
            float texCoordsData[] = extractAllTexCoordsData(geometry, geometryID);

            //prepare for extracting vertices
            Node polylist_node = getChildNode("polylist", geometry);
            final int indices_count = getAttribValI("count", polylist_node)*3;

            Node input_node = getChildNodeByAttribVal("input", "semantic", "VERTEX", polylist_node);
            final int vertex_offset = getAttribValI("offset", input_node);

            input_node = getChildNodeByAttribVal("input", "semantic", "NORMAL", polylist_node);
            final int normal_offset = getAttribValI("offset", input_node);
            ;

            //#TODO handle multiple texture
            input_node = getChildNodeByAttribVal("input", "semantic", "TEXCOORD", polylist_node);
            final int texCoord_offset = getAttribValI("offset", input_node);
            final int tex_set = getAttribValI("set", input_node);

            Node p_node = getChildNode("p", polylist_node);
            //String p_data[] = p_node.getTextContent().split(" ");
            /*int[] p = new int[p_data.length];

            for (int i = 0; i < p_data.length; ++i) {
                p[i] = Integer.parseInt(p_data[i]);
            }*/

            int p[] = extractAllP(geometry);

            int count = 0;

            //vertices
            int stride = findPolylistStride(geometry);
            Vertex vertices[] = new Vertex[positionsData.length / 3];
            int indices[] = new int[p.length/stride];




            for (int i = 0; i < p.length/stride; ++i) {

                int verIndx = p[i*(stride) + vertex_offset];
                int texCoordIndx = p[i*stride + texCoord_offset];
                int normIndx = p[i*stride + normal_offset];

                indices[i] = verIndx;

                if(i == p.length/stride-1){
                    Log.d("Hello", "World");
                }

                if(vertices[verIndx] == null) {
                    vertices[verIndx] = new Vertex(
                            new Vec3(positionsData[verIndx * 3 + 0], positionsData[verIndx * 3 + 1], positionsData[verIndx * 3 + 2]),
                            new Vec2(texCoordsData[texCoordIndx * 2 + 0], 1-texCoordsData[texCoordIndx * 2 + 1]),//blender's y is reversed
                            new Vec3(normalsData[normIndx * 3 + 0], normalsData[normIndx * 3 + 1], normalsData[normIndx * 3 + 2]));
                }
            }

            //handle lines (optional)
            if(getChildNode("lines", geometry) != null){
                Node line_node = getChildNode("lines", geometry);
                Node lines_p = getChildNode("p", line_node);
                String lines_p_data[] = lines_p.getTextContent().split(" ");

                for(int i = 0; i<lines_p_data.length ;++i){
                    int verIndx = Integer.parseInt(lines_p_data[i]);
                    vertices[verIndx] = new Vertex(
                            new Vec3(positionsData[verIndx * 3 + 0],
                                    positionsData[verIndx * 3 + 1],
                                    positionsData[verIndx * 3 + 2])
                    );
                    vertices[verIndx].mHasLine = true;
                }

            }


            scene.addMeshes(new Mesh(
                    geometryID,
                    geometryName,
                    vertices,
                    indices,
                    scene.getTexture(tex_set),
                    null));
        }

    }

    private static int findPolylistStride(Node parent) {
        int max = 0;

        for(Node polylist : getAllChildNodeByType("polylist", parent)){
            for(Node input: getAllChildNodeByType("input", polylist)){
                if(max<getAttribValI("offset", input)){
                    max = getAttribValI("offset", input);
                }
            }

        }

        return max+1;
    }

    private static int[] extractAllP(Node parent){
        Vector<Node> all_polylist_nodes = getAllChildNodeByType("polylist", parent);
        int count = 0;
       /* for(Node polylist_node : all_polylist_nodes){
            count += getAttribValI("count", polylist_node);
        }*/

        Vector<int[]> ps = new Vector<>();



        for(Node polylist_node : all_polylist_nodes){
            int data[] = extractIntArray(getChildNode("p", polylist_node));
            ps.add(data);
            /*for(int e: data){
                ret[index++] = e;
            }*/
            count += data.length;

        }
        int index = 0;
        int ret[]  = new int[count];

        for(int i = 0; i< ps.size(); ++i){
            for(int j = 0; j<ps.elementAt(i).length; ++j){
                ret[index++] = ps.elementAt(i)[j];
            }
        }

        return ret;
    }

    private static float[] extractAllTexCoordsData(Node parent, String meshID){
        int count = 0;

        for(int i =0; true; ++i){
            Node curNode = getChildNodeByID("source", meshID + "-map-" + Integer.toString(i), parent);
            if(curNode == null) break;
            curNode = getChildNodeByID("float_array", meshID + "-map-"+Integer.toString(i)+"-array", curNode);
            count += getAttribValI("count",curNode);
        }

        float ret[]=new float[count];
        int indx = 0;
        for(int i =0; true; ++i){
            Node curNode = getChildNodeByID("source", meshID + "-map-" + Integer.toString(i), parent);
            if(curNode == null) break;
            curNode = getChildNodeByID("float_array", meshID + "-map-"+Integer.toString(i)+"-array", curNode);

            for(float e:extractFloatArray(curNode)){
                ret[indx++] = e;
            }
        }

        return ret;
    }

    private static void extractTexture(Scene scene, Node node) throws IOException {

        if(!node.hasChildNodes()){
            return;
        }

        Node image = getChildNode("image", node);
        String imageName = getAttribute("id", image);
        Node init_from =  getChildNode("init_from", image);
        String path = init_from.getTextContent();
        int texID = GLHelper.loadTexture(mContext, mLocalPath + path);
        scene.addTextures(imageName, mLocalPath + path, texID);
    }

    private static String getAttribute(String name, Node node){
        NamedNodeMap namedNodeMap = node.getAttributes();

        for(int i = 0; i<namedNodeMap.getLength(); ++i){

            if(namedNodeMap.item(i).getNodeType() != Node.ATTRIBUTE_NODE){
                continue;
            }

            //String name = namedNodeMap.item(i).getNodeName()
            //Log.i("Testing", name);
            if(namedNodeMap.item(i).getNodeName().equals(name)){
                return namedNodeMap.item(i).getNodeValue();
            }
        }

        return null;
    }

    private static Node getChildNode(String name, Node node){
        NodeList children = node.getChildNodes();

        for(int i = 0; i<children.getLength(); ++i){

            if(children.item(i).getNodeType() != Node.ELEMENT_NODE){
                continue;
            }

            //String name = children.item(i).getNodeName()
            //Log.i("Testing", name);
            if(children.item(i).getNodeName().equals(name)){
                return children.item(i);
            }
        }

        return null;
    }

    private static Node getChildNodeByID(String nodeName, String id, Node node){
        NodeList children = node.getChildNodes();

        for(int i = 0; i<children.getLength(); ++i){

            if(children.item(i).getNodeType() != Node.ELEMENT_NODE){
                continue;
            }

            //String name = children.item(i).getNodeName()
            //Log.i("Testing", name);
            if(!children.item(i).getNodeName().equals(nodeName)){
                continue;
            }

            if(getAttribute("id", children.item(i)).equals(id)){
                return children.item(i);
            }
        }

        return null;
    }

    private static float[] extractFloatArray(Node node){
        String countData = getAttribute("count", node);
        int count = Integer.parseInt(getAttribute("count", node));
        float ret[] = new float[count];

        String[] data = node.getTextContent().split(" ");

        for(int i = 0; i<count; ++i){
            ret[i] = Float.parseFloat(data[i]);
        }

        return ret;
    }

    private static float[] extractFloatArray(Node node, int count){

        float ret[] = new float[count];

        String[] data = node.getTextContent().split(" ");

        for(int i = 0; i<count; ++i){
            ret[i] = Float.parseFloat(data[i]);
        }

        return ret;
    }

    private static Node getChildNodeByAttribVal(String nodeName, String attribName, String attribVal, Node parent){
        NodeList children = parent.getChildNodes();

        for(int i = 0; i<children.getLength(); ++i){

            if(children.item(i).getNodeType() != Node.ELEMENT_NODE){

            }

            //String name = children.item(i).getNodeName()
            //Log.i("Testing", name);
            if(!children.item(i).getNodeName().equals(nodeName)){
                continue;
            }

            if(getAttribute(attribName, children.item(i)).equals(attribVal)){
                return children.item(i);
            }
        }

        return null;
    }

    private static int getAttribValI(String attribName, Node node){
        return Integer.parseInt(getAttribute(attribName, node));
    }

    private static int countChildByType(String type, Node node){
        int count = 0;

        NodeList children = node.getChildNodes();

        for(int i = 0; i<children.getLength(); ++i){

            if(children.item(i).getNodeType() != Node.ELEMENT_NODE){
                continue;
            }

            //String name = children.item(i).getNodeName()
            //Log.i("Testing", name);
            if(children.item(i).getNodeName().equals(type)){
                ++count;
            }
        }

        return count;
    }

    private static Vector<Node> getAllChildNodeByType(String typeName, Node parent){
        Vector<Node> ret = new Vector<>();

        NodeList children = parent.getChildNodes();

        for(int i = 0; i<children.getLength(); ++i){

            if(children.item(i).getNodeType() != Node.ELEMENT_NODE){
                continue;
            }

            //String name = children.item(i).getNodeName()
            //Log.i("Testing", name);
            if(children.item(i).getNodeName().equals(typeName)){
                ret.add(children.item(i));
            }
        }

        return ret;
    }

}
