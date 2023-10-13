package com.dy.startinganimation.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;

import com.dy.startinganimation.animation.Animator;
import com.dy.startinganimation.common.DemoDAE;
import com.dy.startinganimation.common.GLFragmentView;
import com.dy.startinganimation.parser.AnimParser;
import com.example.startinganimation.R;

import org.xml.sax.SAXException;

import java.io.IOException;
import java.util.Vector;

import javax.xml.parsers.ParserConfigurationException;

public class DemoGLActivity extends FragmentActivity implements
    View.OnClickListener {
    GLFragmentView fStage;
    Button btnClose;
    FragmentTransaction ft;
    Intent myCallerIntent;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.demo_activity);
        btnClose = findViewById(R.id.btnClose);
        btnClose.setOnClickListener(this);
        ft = getSupportFragmentManager().beginTransaction();
        fStage = new GLFragmentView(this);
        myCallerIntent = getIntent();
        Bundle b = myCallerIntent.getExtras();
        DemoDAE dae = (DemoDAE) b.getSerializable("dae");
        /*try {
            Animator animator = AnimParser.parse(DemoGLActivity.this, DemoGLActivity.this.getAssets().open(dae.path));
            animator.init();
            fStage.getGlSurfaceView().getRenderer().render(animator);
        } catch (ParserConfigurationException e) {
            Log.e("AnimParser", e.getMessage());
        } catch (IOException e) {
            Log.e("AnimParser", e.getMessage());
        } catch (SAXException e) {
            Log.e("AnimParser", e.getMessage());
        }*/
        new AsyncLoad().execute(dae.path);
    }

    @Override
    public void onClick(View view) {
        finish();
    }

    private class AsyncLoad extends AsyncTask<String, Integer, Vector<Animator>>{
        ProgressDialog progressDialog = new ProgressDialog(DemoGLActivity.this);
        String waitMessage = "Loading...";
        @Override
        protected void onPreExecute() {
            ft.add(R.id.flStage, fStage);
            ft.commit();
            progressDialog.setMessage(waitMessage);
            progressDialog.show();
            progressDialog.setCancelable(false);
        }

        @Override
        protected void onPostExecute(Vector<Animator> animators) {
            progressDialog.dismiss();

            for(Animator animator : animators){
                fStage.getGlSurfaceView().getRenderer().render(animator);
            }

        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            progressDialog.setMessage(waitMessage);
        }

        @Override
        protected Vector<Animator> doInBackground(String... strings) {
            Vector<Animator> animators = new Vector<Animator>();

            for(String path : strings){
                Animator animator = null;

                try {
                    animator = AnimParser.parse(DemoGLActivity.this, DemoGLActivity.this.getAssets().open(path));
                    animators.add(animator);
                } catch (ParserConfigurationException e) {
                    Log.e("AnimParser", e.getMessage());
                } catch (IOException e) {
                    Log.e("AnimParser", e.getMessage());
                } catch (SAXException e) {
                    Log.e("AnimParser", e.getMessage());
                }
            }


            return animators;
        }
    }
}
