package com.dy.app.ui.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.dy.app.R;
import com.dy.app.activity.FragmentHubActivity;
import com.dy.app.activity.MainActivity;
import com.dy.app.core.FragmentCallback;

public class FragmentCreateAccount extends Fragment
    implements FragmentCallback, View.OnClickListener{
    private Button btnClose, btnCancel, btnCreate;
    private EditText etMail, etUsername, etPassword, etConfirmPassword;


    public final static String TAG = "FragmentCreateCountForm";
    private FragmentHubActivity main;

    public static FragmentCreateAccount newInstance(){
        FragmentCreateAccount fragment = new FragmentCreateAccount();
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        main = (FragmentHubActivity)getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_create_account, container, false);
        btnClose = view.findViewById(R.id.btnClose);
        btnCancel = view.findViewById(R.id.btnCancel);
        btnCreate = view.findViewById(R.id.btnCreate);
        etMail = view.findViewById(R.id.etMail);
        etUsername = view.findViewById(R.id.etUsername);
        etPassword = view.findViewById(R.id.etPassword);
        etConfirmPassword = view.findViewById(R.id.etRetype);

        exqListener();
        return view;
    }

    private void exqListener() {
        btnClose.setOnClickListener(this);
        btnCancel.setOnClickListener(this);
        btnCreate.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.btnClose){
            reset();
            main.onMsgToMain(TAG, 0, null, null);
        }else if (v.getId() == R.id.btnCancel){
            reset();
            main.onMsgToMain(TAG, 1, null, null);
        }else if (v.getId() == R.id.btnCreate){
            reset();
            main.onMsgToMain(TAG, 2, null, null);
        }
    }

    @Override
    public void onMsgFromMain(String TAG, int type, Object o1, Object o2) {

    }

    private void reset(){
        etMail.setText("");
        etUsername.setText("");
        etPassword.setText("");
        etConfirmPassword.setText("");
    }
}
