package com.dy.app.ui.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.dy.app.R;
import com.dy.app.activity.FragmentHubActivity;
import com.dy.app.core.FragmentCallback;

public class FragmentChatLobby extends Fragment
        implements FragmentCallback, View.OnClickListener {


    public final static String TAG = "FragmentChatLobby";
    private FragmentHubActivity main;

    private EditText etMsg;
    private TextView tvChat;
    private Button btnEmoji, btnSend;

    public static FragmentChatLobby newInstance() {
        FragmentChatLobby fragment = new FragmentChatLobby();
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        main = (FragmentHubActivity) getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat_window, container, false);
        etMsg = view.findViewById(R.id.etMsg);
        tvChat = view.findViewById(R.id.tvChat);
        btnEmoji = view.findViewById(R.id.btnEmoji);
        btnSend = view.findViewById(R.id.btnSend);
        eqListener();
        return view;
    }

    private void eqListener() {
        btnSend.setOnClickListener(this);
        btnEmoji.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.btnSend){

        }
        else if(v.getId() == R.id.btnEmoji){

        }
    }

    @Override
    public void onMsgFromMain(String TAG, int type, Object o1, Object o2) {

    }
}
