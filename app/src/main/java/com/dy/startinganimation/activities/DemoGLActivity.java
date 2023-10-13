package com.dy.startinganimation.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.dy.startinganimation.animation.Animator;
import com.dy.startinganimation.camera.Camera;
import com.dy.startinganimation.common.ConfigListAdapter;
import com.dy.startinganimation.common.ConfigPanel;
import com.dy.startinganimation.common.DemoDAE;
import com.dy.startinganimation.common.GLFragmentView;
import com.dy.startinganimation.parser.AnimParser;
import com.example.startinganimation.R;

import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.Serializable;
import java.util.Vector;

import javax.xml.parsers.ParserConfigurationException;

public class DemoGLActivity extends FragmentActivity implements Serializable,
    View.OnClickListener {
    GLFragmentView fStage;
    Button btnClose, btnConfig;
    FragmentManager fm;
    ConfigPanel fConfigPanel;
    Intent myCallerIntent;
    boolean isConfigPanelShown = false;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.demo_activity);

        fm = getSupportFragmentManager();

        FragmentTransaction ft = fm.beginTransaction();
        fStage = GLFragmentView.newInstance();
        myCallerIntent = getIntent();
        Bundle b = myCallerIntent.getExtras();
        DemoDAE dae = (DemoDAE) b.getSerializable("dae");

        fConfigPanel = new ConfigPanel();

        btnClose = findViewById(R.id.btnClose);
        btnClose.setOnClickListener(this);
        btnConfig = findViewById(R.id.btnConfig);
        btnConfig.setOnClickListener(this);

        //restore state if any
        if(savedInstanceState != null){
            isConfigPanelShown = savedInstanceState.getBoolean("isConfigPanelShown");
        }

        if(isConfigPanelShown){
            showConfigPanel();
        }

        new AsyncLoad(ft).execute(dae.path);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("isConfigPanelShown", isConfigPanelShown);

    }

    @Override
    public void onPause() {
        super.onPause();
        FragmentTransaction ft = fm.beginTransaction();
        //remove fragment to prevent memory leak
        ft.remove(fConfigPanel);
        ft.remove(fStage);
        ft.commit();
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.btnClose){
            finish();
        } else if (view.getId() == R.id.btnConfig){
            if(isConfigPanelShown){
                closeConfigPanel();
                isConfigPanelShown = false;
            } else {
                showConfigPanel();
                isConfigPanelShown = true;
            }
        }
    }

    public void showConfigPanel(){
        FragmentTransaction ft = fm.beginTransaction();
        ft.add(R.id.flConfigMenu, fConfigPanel);
        ft.addToBackStack(null);
        //ft.remove(fStage);
        ft.commit();
    }

    public void closeConfigPanel() {
        FragmentTransaction ft = fm.beginTransaction();
        ft.remove(fConfigPanel);
        //ft.add(R.id.flStage, fStage);
        ft.commit();
    }

    public void changeDrawMode(int drawMode) {
        fStage.getGlSurfaceView().getRenderer().setDrawMode(drawMode);
    }

    public void changePlaySpeed(float playSpeed) {
        fStage.getGlSurfaceView().getRenderer().setPlaySpeed(playSpeed);
    }

    public void displayToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    public void changeSensorSensitivity(float value) {
        Camera.getInstance().getInstance().setSensitivity(value);
    }

    private class AsyncLoad extends AsyncTask<String, Integer, Vector<Animator>>{
        ProgressDialog progressDialog = new ProgressDialog(DemoGLActivity.this);
        String waitMessage = "Loading...";
        FragmentTransaction ft;

        public AsyncLoad(FragmentTransaction ft){
            this.ft = ft;
        }

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
