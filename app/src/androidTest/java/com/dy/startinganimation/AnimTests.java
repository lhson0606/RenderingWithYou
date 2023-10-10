package com.dy.startinganimation;

import android.content.Context;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.xml.sax.SAXException;

import com.dy.startinganimation.animation.Animator;
import com.dy.startinganimation.parser.AnimParser;
import com.dy.startinganimation.utils.GLHelper;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class AnimTests {
    @Test
    public void parseModel() {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        String file = "models/model.dae";

        try {
            Animator animator=AnimParser.parse(appContext, appContext.getAssets().open(file));
            //assert(testNullVertex(scene));
        } catch (ParserConfigurationException e) {
            GLHelper.handleException("Test Model", e.getMessage());
            assert(false);
        } catch (IOException e) {
            GLHelper.handleException("Test Model", e.getMessage());
            assert(false);
        } catch (SAXException e) {
            GLHelper.handleException("Test Model", e.getMessage());
            assert(false);
        }
        assert (true);
    }

    @Test
    public void parseDragon() {
        final String TAG = "Test dragon Model";
        String file = "models/dragon/dragon.dae";
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        try {
            Animator animator=AnimParser.parse(appContext, appContext.getAssets().open(file));
        } catch (ParserConfigurationException e) {
            GLHelper.handleException(TAG, e.getMessage());
            assert(false);
        } catch (IOException e) {
            GLHelper.handleException(TAG, e.getMessage());
            assert(false);
        } catch (SAXException e) {
            GLHelper.handleException(TAG, e.getMessage());
            assert(false);
        }

        assert(true);
    }

    @Test
    public void parseWolf() {
        final String TAG = "Test Wolf Model";
        // Context of the app under test.
        String file = "models/wolf/wolf.dae";
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        try {
            Animator animator=AnimParser.parse(appContext, appContext.getAssets().open(file));
        } catch (ParserConfigurationException e) {
            GLHelper.handleException(TAG, e.getMessage());
            assert(false);
        } catch (IOException e) {
            GLHelper.handleException(TAG, e.getMessage());
            assert(false);
        } catch (SAXException e) {
            GLHelper.handleException(TAG, e.getMessage());
            assert(false);
        }

        assert(true);
    }

    @Test
    public void parseSpider() {
        final String TAG = "Test spider Model";
        // Context of the app under test.
        String file = "models/spider/spider.dae";
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        try {
            Animator animator=AnimParser.parse(appContext, appContext.getAssets().open(file));
        } catch (ParserConfigurationException e) {
            GLHelper.handleException(TAG, e.getMessage());
            assert(false);
        } catch (IOException e) {
            GLHelper.handleException(TAG, e.getMessage());
            assert(false);
        } catch (SAXException e) {
            GLHelper.handleException(TAG, e.getMessage());
            assert(false);
        }

        assert(true);
    }
}