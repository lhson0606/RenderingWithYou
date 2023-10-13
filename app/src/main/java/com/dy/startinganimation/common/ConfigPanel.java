package com.dy.startinganimation.common;

import android.content.Context;
import android.opengl.GLES30;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.dy.startinganimation.activities.DemoGLActivity;
import com.dy.startinganimation.activities.MainActivity;
import com.example.startinganimation.R;
import com.google.android.material.slider.Slider;

public class ConfigPanel extends Fragment
        implements View.OnClickListener, AdapterView.OnItemSelectedListener, TextWatcher, Slider.OnChangeListener {
    DemoGLActivity main;
    Context context = null;
    Button btnClose;
    EditText etPlaySpeed;
    Spinner spnGLDrawMode;
    Slider sldSensorSensitivity;

    public ConfigPanel() {
        super();
    }

    @Override
    public void onAttach(Context context){
        super.onAttach(context);
        this.context = context;
        main = (DemoGLActivity) context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View v = inflater.inflate(R.layout.config_panel, container, false);

        btnClose = v.findViewById(R.id.btnClose);
        btnClose.setOnClickListener(this);

        spnGLDrawMode = v.findViewById(R.id.spnGLDrawMode);
        spnGLDrawMode.setOnItemSelectedListener(this);
        //https://stackoverflow.com/questions/4029261/populating-spinner-directly-in-the-layout-xml
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, GL_DRAW_MODE_NAMES);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnGLDrawMode.setAdapter(adapter);
        spnGLDrawMode.setBackgroundColor(0x00000000);

        etPlaySpeed = v.findViewById(R.id.etPlaySpeed);
        etPlaySpeed.setText("0.015");
        etPlaySpeed.addTextChangedListener(this);

        sldSensorSensitivity = v.findViewById(R.id.sldSensorSensitivity);
        sldSensorSensitivity.setValue(50);
        sldSensorSensitivity.addOnChangeListener(this);

        return v;
    }

    @Override
    public void onClick(View view) {
        if(view == btnClose){
            main.closeConfigPanel();
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        int drawMode = GL_DRAW_MODES[i];
        main.changeDrawMode(drawMode);
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    public final static int GL_DRAW_MODES[] = {
            GLES30.GL_TRIANGLES,
            GLES30.GL_LINES,
            GLES30.GL_POINTS,
            GLES30.GL_LINE_STRIP,
            GLES30.GL_LINE_LOOP,
            GLES30.GL_TRIANGLE_STRIP,
            GLES30.GL_TRIANGLE_FAN,
    };

    public final static String GL_DRAW_MODE_NAMES[] = {
            "GL_TRIANGLE",
            "GL_LINE",
            "GL_POINT",
            "GL_LINE_STRIP",
            "GL_LINE_LOOP",
            "GL_TRIANGLE_STRIP",
            "GL_TRIANGLE_FAN",
    };

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void afterTextChanged(Editable editable) {
        try{
            float playSpeed = Float.parseFloat(editable.toString());
            main.changePlaySpeed(playSpeed);
        }catch (Exception e){
            main.displayToast("Invalid play speed");
        }



    }

    @Override
    public void onValueChange(@NonNull Slider slider, float value, boolean fromUser) {
        main.changeSensorSensitivity(value);
    }
}
