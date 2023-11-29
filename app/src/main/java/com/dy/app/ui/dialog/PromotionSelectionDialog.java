package com.dy.app.ui.dialog;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.dy.app.R;
import com.dy.app.gameplay.algebraicNotation.ChessNotation;

public class PromotionSelectionDialog extends DialogFragment implements View.OnClickListener {
    private ImageView ivBtnQueenPromotion, ivBtnRookPromotion, ivBtnBishopPromotion, ivBtnKnightPromotion;
    private ImageView autoPromoteQueen, autoPromoteRook, autoPromoteBishop, autoPromoteKnight;

    public interface PromotionSelectionDialogListener{
        void onPromotionSelected(String pieceName);
    }

    private PromotionSelectionDialogListener listener;

    public PromotionSelectionDialog(PromotionSelectionDialogListener listener){
        this.listener = listener;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Set the dialog style to the transparent style
        //setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.dialog_promotion_selection, container, false);
        setCancelable(false);

        ivBtnQueenPromotion = v.findViewById(R.id.ivBtnQueenPromotion);
        ivBtnRookPromotion = v.findViewById(R.id.ivBtnRookPromotion);
        ivBtnBishopPromotion = v.findViewById(R.id.ivBtnBishopPromotion);
        ivBtnKnightPromotion = v.findViewById(R.id.ivBtnKnightPromotion);

        autoPromoteQueen = v.findViewById(R.id.autoPromoteQueen);
        autoPromoteRook = v.findViewById(R.id.autoPromoteRook);
        autoPromoteBishop = v.findViewById(R.id.autoPromoteBishop);
        autoPromoteKnight = v.findViewById(R.id.autoPromoteKnight);

        ivBtnQueenPromotion.setOnClickListener(this);
        ivBtnRookPromotion.setOnClickListener(this);
        ivBtnBishopPromotion.setOnClickListener(this);
        ivBtnKnightPromotion.setOnClickListener(this);

        autoPromoteQueen.setOnClickListener(this);
        autoPromoteRook.setOnClickListener(this);
        autoPromoteBishop.setOnClickListener(this);
        autoPromoteKnight.setOnClickListener(this);

        return v;
    }

    @Override
    public void onClick(View v) {
        if(v == ivBtnQueenPromotion) {
            listener.onPromotionSelected(ChessNotation.QUEEN);
            dismiss();
        }else if(v == ivBtnRookPromotion){
            listener.onPromotionSelected(ChessNotation.ROOK);
            dismiss();
        }else if(v == ivBtnBishopPromotion){
            listener.onPromotionSelected(ChessNotation.BISHOP);
            dismiss();
        } else if(v == ivBtnKnightPromotion){
            listener.onPromotionSelected(ChessNotation.KNIGHT);
            dismiss();
        } else if(v == autoPromoteQueen){
            listener.onPromotionSelected(ChessNotation.QUEEN);
            dismiss();
        } else if(v == autoPromoteRook){
            listener.onPromotionSelected(ChessNotation.ROOK);
            dismiss();
        } else if(v == autoPromoteBishop){
            listener.onPromotionSelected(ChessNotation.BISHOP);
            dismiss();
        } else if(v == autoPromoteKnight){
            listener.onPromotionSelected(ChessNotation.KNIGHT);
            dismiss();
        }
    }
}
