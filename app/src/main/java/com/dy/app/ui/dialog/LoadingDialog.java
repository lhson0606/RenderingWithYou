package com.dy.app.ui.dialog;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.dy.app.R;

public class LoadingDialog extends DialogFragment {
    public LoadingDialog() {
        setStyle(DialogFragment.STYLE_NO_FRAME, R.style.TransparentDialog);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Set the dialog style to the transparent style
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_pagman_loading, container, false);
    }
}
