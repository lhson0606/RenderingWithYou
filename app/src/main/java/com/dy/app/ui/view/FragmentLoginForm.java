package com.dy.app.ui.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.airbnb.lottie.LottieAnimationView;
import com.dy.app.R;
import com.dy.app.activity.FragmentHubActivity;
import com.dy.app.activity.MainActivity;
import com.dy.app.core.FragmentCallback;

public class FragmentLoginForm extends Fragment
        implements View.OnClickListener, FragmentCallback {
    private LottieAnimationView btnClose;
    private Button btnLogin, btnCreateAccount, btnContinueWith;
    private EditText etUsername, etPassword;

    public final static String TAG = "FragmentLoginForm";
    private FragmentHubActivity main;

    public static FragmentLoginForm newInstance(){
        FragmentLoginForm fragment = new FragmentLoginForm();
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        main = (FragmentHubActivity)getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login_form, container, false);
        btnClose = view.findViewById(R.id.btnClose);
        btnClose.setOnClickListener(this);
        btnLogin = view.findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(this);
        btnCreateAccount = view.findViewById(R.id.btnCreateAccount);
        btnCreateAccount.setOnClickListener(this);
        etUsername = view.findViewById(R.id.etUsername);
        etPassword = view.findViewById(R.id.etPassword);
        btnContinueWith = view.findViewById(R.id.btnContinueWith);
        btnContinueWith.setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.btnClose){
            btnClose.playAnimation();
            main.onMsgToMain(TAG, 0, null, null);
        }else if(v.getId() == R.id.btnLogin){
            main.onMsgToMain(TAG, 1, etUsername.getText().toString(), etPassword.getText().toString());
        }else if (v.getId() == R.id.btnCreateAccount){
            main.onMsgToMain(TAG, 2, null, null);
        }else if(v.getId() == R.id.btnContinueWith){
            main.onMsgToMain(TAG, 3, null, null);
        }
    }

    @Override
    public void onResume() {
        etPassword.setText("");
        super.onResume();
    }

    @Override
    public void onMsgFromMain(String TAG, int type, Object o1, Object o2) {
        switch (type){
            case 0:
                break;
            case 1:
                break;
        }
    }
}
