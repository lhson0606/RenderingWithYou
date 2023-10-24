package com.dy.app.activity;

import androidx.fragment.app.FragmentActivity;

import com.dy.app.core.MainCallback;

public class FragmentHubActivity extends FragmentActivity
implements MainCallback {
    @Override
    public void onMsgToMain(String TAG, int type, Object o1, Object o2) {

    }
}
