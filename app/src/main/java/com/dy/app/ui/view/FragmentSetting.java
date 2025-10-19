package com.dy.app.ui.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.airbnb.lottie.LottieAnimationView;
import com.dy.app.R;
import com.dy.app.activity.FragmentHubActivity;
import com.dy.app.core.FragmentCallback;
import com.dy.app.gameplay.viewport.ViewPort;
import com.dy.app.graphic.camera.Camera;
import com.dy.app.setting.GameSetting;
import com.dy.app.utils.DyConst;
import com.google.android.material.slider.Slider;
import com.google.android.material.switchmaterial.SwitchMaterial;

public class FragmentSetting extends Fragment
        implements FragmentCallback, View.OnClickListener, Slider.OnSliderTouchListener,
        CompoundButton.OnCheckedChangeListener{

    public static final int CLOSE_PANEL = 0;
    public static final int CINEMATIC_CAMERA = 1;
    public static final int ENABLE_DRAG_MODE = 2;
    private LottieAnimationView btnClose;

    public final static String TAG = "FragmentSetting";
    private FragmentHubActivity main;
    private final GameSetting gameSetting = GameSetting.getInstance();
    private Slider sldSensorSensitivity, sldDirectionalLightRed, sldDirectionalLightGreen, sldDirectionalLightBlue,
            sldAmbientLight, sldSpectacularLightIntensity, sldSpectacularLightShineDamper, sldSpectacularLightReflectivity,
            slPlaySpeed;
    private Spinner spnGLDrawMode, spnViewPort;
    private SwitchMaterial swBlockViewPort, swCinematicCamera, swEnableDragMode;

    public static FragmentSetting newInstance(){
        FragmentSetting fragment = new FragmentSetting();
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        main = (FragmentHubActivity)getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_setting, container, false);

        btnClose = view.findViewById(R.id.btnClose);
        spnGLDrawMode = view.findViewById(R.id.spnGLDrawMode);
        sldSensorSensitivity = view.findViewById(R.id.sldSensorSensitivity);
        sldDirectionalLightRed = view.findViewById(R.id.sldDirectionalLightRed);
        sldDirectionalLightGreen = view.findViewById(R.id.sldDirectionalLightGreen);
        sldDirectionalLightBlue = view.findViewById(R.id.sldDirectionalLightBlue);
        sldAmbientLight = view.findViewById(R.id.sldAmbientLight);
        sldSpectacularLightIntensity = view.findViewById(R.id.sldSpectacularLightIntensity);
        sldSpectacularLightShineDamper = view.findViewById(R.id.sldSpectacularLightShineDamper);
        sldSpectacularLightReflectivity = view.findViewById(R.id.sldSpectacularLightReflectivity);
        slPlaySpeed = view.findViewById(R.id.slPlaySpeed);
        spnViewPort = view.findViewById(R.id.spnViewPort);
        swBlockViewPort = view.findViewById(R.id.swBlockViewPort);
        swCinematicCamera = view.findViewById(R.id.swCinematicCamera);
        swEnableDragMode = view.findViewById(R.id.swEnableDragMode);

        ArrayAdapter glDrawModeAdapter = new ArrayAdapter<String>(main, android.R.layout.simple_list_item_1, GameSetting.DRAW_MODES);
        spnGLDrawMode.setAdapter(glDrawModeAdapter);
        ArrayAdapter viewPortAdapter = new ArrayAdapter<String>(main, android.R.layout.simple_list_item_1, ViewPort.VIEW_PORT_NAMES);
        spnViewPort.setAdapter(viewPortAdapter);

        btnClose.setOnClickListener(this);
        spnGLDrawMode.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                gameSetting.setDrawModeIndex(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        slPlaySpeed.setValue(gameSetting.getPlaybackSpeed());

        sldSensorSensitivity.addOnSliderTouchListener(this);
        sldDirectionalLightRed.addOnSliderTouchListener(this);
        sldDirectionalLightGreen.addOnSliderTouchListener(this);
        sldDirectionalLightBlue.addOnSliderTouchListener(this);
        sldAmbientLight.addOnSliderTouchListener(this);
        sldSpectacularLightIntensity.addOnSliderTouchListener(this);
        sldSpectacularLightShineDamper.addOnSliderTouchListener(this);
        sldSpectacularLightReflectivity.addOnSliderTouchListener(this);
        slPlaySpeed.addOnSliderTouchListener(this);
        spnViewPort.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                gameSetting.setSelectedViewPortIndex(position);
                Camera.getInstance().setPos(DyConst.viewPorts[position].getPos());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        swBlockViewPort.setOnCheckedChangeListener(this);
        swCinematicCamera.setOnCheckedChangeListener(this);
        swEnableDragMode.setOnCheckedChangeListener(this);

        return view;
    }

    private void updateChanges(){
        spnGLDrawMode.setSelection(gameSetting.getDrawModeIndex());
        spnViewPort.setSelection(gameSetting.getSelectedViewPortIndex());
        Camera.getInstance().setPos(DyConst.viewPorts[gameSetting.getSelectedViewPortIndex()].getPos());
        sldSensorSensitivity.setValue(Camera.getInstance().getSensitivity());
        sldDirectionalLightRed.setValue(gameSetting.getLight().getRed());
        sldDirectionalLightGreen.setValue(gameSetting.getLight().getGreen());
        sldDirectionalLightBlue.setValue(gameSetting.getLight().getBlue());
        sldAmbientLight.setValue(gameSetting.getAmbientFactor());
        sldSpectacularLightIntensity.setValue(0);
        sldSpectacularLightShineDamper.setValue(gameSetting.getPieceMaterial().getLightDamper());
        sldSpectacularLightReflectivity.setValue(gameSetting.getPieceMaterial().getReflectivity());
        slPlaySpeed.setValue(gameSetting.getPlaybackSpeed());
        swBlockViewPort.setChecked(Camera.getInstance().isMovementLocked());
        swCinematicCamera.setChecked(gameSetting.isCinematicCameraEnabled);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        updateChanges();
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.btnClose){
            btnClose.playAnimation();
            main.onMsgToMain(TAG, CLOSE_PANEL, null, null);
        }
    }

    @Override
    public void onMsgFromMain(String TAG, int type, Object o1, Object o2) {

    }

    @Override
    public void onStartTrackingTouch(@NonNull Slider slider) {

    }

    @Override
    public void onStopTrackingTouch(@NonNull Slider slider) {
        if(slider == sldSensorSensitivity){
            float progress = (float) sldSensorSensitivity.getValue();
            Camera.getInstance().setSensitivity(progress);
            gameSetting.setSensitivity(progress);
        }else if(slider == sldDirectionalLightRed){
            float progress = (float) sldDirectionalLightRed.getValue();
            gameSetting.getLight().setRed(progress);
        }else if(slider == sldDirectionalLightGreen){
            float progress = (float) sldDirectionalLightGreen.getValue();
            gameSetting.getLight().setGreen(progress);
        }else if(slider == sldDirectionalLightBlue) {
            float progress = (float) sldDirectionalLightBlue.getValue();
            gameSetting.getLight().setBlue(progress);
        }else if(slider == sldAmbientLight){
            float progress = (float) sldAmbientLight.getValue();
            gameSetting.setAmbientFactor(progress);
        }else if(slider == sldSpectacularLightIntensity){
            float progress = (float) sldSpectacularLightIntensity.getValue();
            //#TODO
        }else if(slider == sldSpectacularLightShineDamper){
            float progress = (float) sldSpectacularLightShineDamper.getValue();
            gameSetting.getPieceMaterial().setLightDamper(progress);
        }else if(slider == sldSpectacularLightReflectivity){
            float progress = (float) sldSpectacularLightReflectivity.getValue();
            gameSetting.getPieceMaterial().setReflectivity(progress);
        }else if(slider == sldSpectacularLightReflectivity){
            float progress = (float) sldSpectacularLightReflectivity.getValue();
            //#TODO
        }else if(slider == slPlaySpeed){
            int progress = (int) slPlaySpeed.getValue();
            gameSetting.setPlaybackSpeed(progress);
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if(buttonView == swBlockViewPort){
            if(isChecked){
                Camera.getInstance().lockMovement();
            }else{
                Camera.getInstance().unlockMovement();
            }
        }else if(swCinematicCamera == buttonView){
            if(isChecked){
                main.onMsgToMain(TAG, FragmentSetting.CINEMATIC_CAMERA, true, null);
            }else{
                main.onMsgToMain(TAG, FragmentSetting.CINEMATIC_CAMERA, false, null);
            }
        }else if(swEnableDragMode == buttonView){
            if(isChecked){
                main.onMsgToMain(TAG, FragmentSetting.ENABLE_DRAG_MODE, true, null);
            }else{
                main.onMsgToMain(TAG, FragmentSetting.ENABLE_DRAG_MODE, false, null);
            }
        }
    }
}
