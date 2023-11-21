package com.dy.app.ui.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.emoji.widget.EmojiTextView;
import androidx.fragment.app.Fragment;

import com.dy.app.R;
import com.dy.app.activity.FragmentHubActivity;
import com.dy.app.core.FragmentCallback;
import com.dy.app.gameplay.Rival;
import com.dy.app.manager.ConnectionManager;
import com.dy.app.network.Message;
import com.dy.app.network.MessageCode;
import com.dy.app.utils.MessageFactory;
import com.vanniktech.emoji.EmojiPopup;

public class FragmentChatLobby extends Fragment
        implements FragmentCallback, View.OnClickListener {


    public final static String TAG = "FragmentChatLobby";
    private FragmentHubActivity main;

    private EditText etMsg;
    private Button btnEmoji, btnSend;
    private EmojiPopup popup;
    private LinearLayout llChat;
    private ScrollView svChat;
    public static final int ADD_PLAYER_MESSAGE = 0;
    public static final int ADD_SYSTEM_MESSAGE = 1;

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
        btnEmoji = view.findViewById(R.id.btnEmoji);
        btnSend = view.findViewById(R.id.btnSend);
        llChat = view.findViewById(R.id.llChatContent);
        svChat = view.findViewById(R.id.svChat);
        popup = EmojiPopup.Builder.fromRootView(
            view.findViewById(R.id.root_view)
        ).build(etMsg);

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
            if(etMsg.getText().toString().length() > 0)
                addMessage(etMsg.getText().toString());
            etMsg.setText("");
        }
        else if(v.getId() == R.id.btnEmoji){
            popup.toggle();
        }
    }

    @Override
    public void onMsgFromMain(String TAG, int type, Object o1, Object o2) {
        switch (type){
            case ADD_PLAYER_MESSAGE:
                final String playerMsg = (String)o1;
                addMessageFromPeer(playerMsg);
                break;
            case ADD_SYSTEM_MESSAGE:
                final String systemMsg = (String)o1;
                addSystemMessage(systemMsg);
                break;
            case 2:
                //connection lost
                break;
        }
    }

    private void addMessage(String chatText){
        androidx.emoji.widget.EmojiTextView tv = LayoutInflater.from(getContext()).inflate(R.layout.emoji_text_view, llChat, false).findViewById(R.id.emojiView);
        tv.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_END);
        tv.setText(etMsg.getText().toString());
        llChat.addView(tv);
        //scroll down to the last message
        scrollDown();
        //send message to the other player
        Message o = MessageFactory.getInstance().createDataMessage(chatText.getBytes(), MessageCode.PLAYER_CHAT_MESSAGE_CODE);
        ConnectionManager.getInstance().postMessage(o);
    }

    private void addMessageFromPeer(String chatText){
        androidx.emoji.widget.EmojiTextView tv = LayoutInflater.from(getContext()).inflate(R.layout.emoji_text_view, llChat, false).findViewById(R.id.emojiView);
        tv.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
        final String text = "["+Rival.getInstance().getName() +"] " +chatText;
        tv.setText(text);
        llChat.addView(tv);
        //scroll down to the latest message
        scrollDown();
    }

    private void scrollDown() {
        svChat.post(new Runnable() {
            @Override
            public void run() {
                svChat.fullScroll(View.FOCUS_DOWN);
            }
        });
    }

    private void addSystemMessage(String s) {
        TextView tv = LayoutInflater.from(getContext()).inflate(R.layout.system_text_view, llChat, false).findViewById(R.id.tvMsg);
        tv.setText(s);
        llChat.addView(tv);
        //scroll down to the last message
        scrollDown();
    }
}
