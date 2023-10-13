package com.dy.startinganimation.parser;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Paint;
import android.opengl.Matrix;
import android.util.Log;
import android.widget.Toast;

import com.dy.startinganimation.activities.DyGLSurfaceView;
import com.dy.startinganimation.animation.AnimRenderer;
import com.dy.startinganimation.animation.AnimatedModel;
import com.dy.startinganimation.animation.Animator;
import com.dy.startinganimation.animation.Joint;
import com.dy.startinganimation.animation.JointTransform;
import com.dy.startinganimation.animation.KeyFrame;
import com.dy.startinganimation.camera.Camera;
import com.dy.startinganimation.gl.Vertex;
import com.dy.startinganimation.maths.Mat4;
import com.dy.startinganimation.maths.Vec2;
import com.dy.startinganimation.maths.Vec3;
import com.dy.startinganimation.model.Mesh;
import com.dy.startinganimation.model.Texture;
import com.dy.startinganimation.shader.Shader;
import com.dy.startinganimation.shader.ShaderHelper;
import com.dy.startinganimation.utils.GLHelper;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class AnimParser {
    static Context mContext;
    static String mLocalPath = "models/dae/";

    public static Animator parse(Context context, InputStream inputStream) throws ParserConfigurationException, IOException, SAXException {

        Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(inputStream);
        DyXml dyXml = new DyXml(doc);
        DyNode dyRoot = dyXml.root;
        ImagesParser imagesParser = new ImagesParser(dyRoot.getFirstChildHasType("library_images"));
        GeometryParser geometryParser = new GeometryParser(dyRoot.getFirstChildHasType("library_geometries"));
        ControllerParser controllerParser = new ControllerParser(dyRoot.getFirstChildHasType("library_controllers"));
        AnimationParser animationParser = new AnimationParser(dyRoot.getFirstChildHasType("library_animations"));
        VisualScenesParser visualSceneParser = new VisualScenesParser(dyRoot.getFirstChildHasType("library_visual_scenes"));

        assembleKeyframes(visualSceneParser.root, animationParser.jointKeyFrames);

        Joint[] joints = new Joint[controllerParser.jointNames.length];
        for(int i = 0; i<joints.length; ++i){
            joints[i] = visualSceneParser.joints.get(controllerParser.jointNames[i]);
        }

        Mesh mesh = geometryParser.getGeometries().firstElement().mesh;

        if(imagesParser.textures.size()>0){
            String path = imagesParser.textures.get(imagesParser.getIDs().firstElement());
            Bitmap bitmap = GLHelper.loadBitmap(context.getAssets().open(mLocalPath + path));
            Texture texture = new Texture(bitmap);
            mesh.mTexture = texture;
        }

        int jointIDs[] = new int[mesh.mIndices.length*4];
        float weights[] = new float[mesh.mIndices.length*4];

        for(int i = 0; i<mesh.mIndices.length; ++i){
            float sum = 0;//for normalizing weights
            int boneIndx = geometryParser.bonesIndicesData[i];
            for(int j = 0; j<4; ++j){
                jointIDs[i*4+j] = controllerParser.skins.get(mesh.mID).jointIDs.get(boneIndx*4 + j);
                weights[i*4+j] = controllerParser.skins.get(mesh.mID).weights.get(boneIndx*4 + j);
                sum += weights[i*4+j];
            }
            for(int j = 0; j<4; ++j){
                weights[i*4+j] /= sum;
            }
        }

        mesh.mBoneIDs = jointIDs;
        mesh.mWeights = weights;

        AnimatedModel animatedModel = new AnimatedModel(
                mesh,
                controllerParser.skins.get(mesh.mID).BSM,
                joints,
                visualSceneParser.root
        );

        Shader shader = ShaderHelper.getInstance().createShader(
                context.getAssets().open("shaders/anim/ver.glsl"),
                context.getAssets().open("shaders/anim/frag.glsl")
        );

        Animator animator = new Animator(
                animatedModel,
                Camera.getInstance().getInstance().mViewMat,
                Camera.getInstance().getInstance().mProjMat,
                shader
        );

        return animator;
    }

    private static void assembleKeyframes(Joint root, HashMap<String, KeyFrame[]> frames) {
        root.mKeyFrames = frames.get(root.mID);
        for(Joint child : root.mChildren){
            assembleKeyframes(child, frames);
        }
    }
}