package com.dy.app.ui.view;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.TextSliderView;
import com.daimajia.slider.library.Tricks.ViewPagerEx;
import com.dy.app.R;
import com.dy.app.gameplay.Player;
import com.dy.app.manager.SoundManager;
import com.dy.app.utils.DyConst;
import com.dy.app.utils.Utils;

import java.util.HashMap;
import java.util.Locale;

public class FragmentPieceSelection extends Fragment
implements View.OnClickListener, ViewPagerEx.OnPageChangeListener {

    public final static String TAG = "FragmentPieceSelection";
    private SliderLayout slider;
    private Button btnNext, btnPrev;
    private final SoundManager soundManager = SoundManager.getInstance();
    private int selected_index = 0;
    private Player player;

    public static FragmentPieceSelection newInstance(){
        FragmentPieceSelection fragment = new FragmentPieceSelection();
        return fragment;
    }

    private void createCarousel(SliderLayout slider) {
        updateSlides();

        slider.setPresetTransformer(SliderLayout.Transformer.Tablet);
        slider.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom);
        slider.stopAutoCycle();
        slider.addOnPageChangeListener(this);
    }

    public void updateSlides(){
        int id[] = player.getPiece_skins();
        slider.removeAllSliders();

        for(int i = 0; i<id.length; i++){
            TextSliderView textSliderView = new TextSliderView(slider.getContext());
            int res = DyConst.piece_skin_thumbnails[id[i]];
            String name =  Utils.toLocalCapitalize(DyConst.piece_skins[id[i]].substring(0, DyConst.piece_skins[id[i]].length()-4).replace("_", " ").toUpperCase());
            textSliderView
                    .description(name)
                    .image(res)
                    .setScaleType(TextSliderView.ScaleType.FitCenterCrop);

            slider.addSlider(textSliderView);
        }

        slider.setCurrentPosition(0);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_choose_piece_skin_page, container, false);
        slider = view.findViewById(R.id.slider);
        btnNext = view.findViewById(R.id.btnNext);
        btnPrev = view.findViewById(R.id.btnPrev);
        player = Player.getInstance();
        exqListener();
        createCarousel(slider);
        return view;
    }

    private void exqListener() {
        btnNext.setOnClickListener(this);
        btnPrev.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.btnNext){
            //soundManager.playSound(getContext(), SoundManager.SoundType.BTN_SKIN_PICKING);
            slider.moveNextPosition();
        }else if (v.getId() == R.id.btnPrev){
            //soundManager.playSound(getContext(), SoundManager.SoundType.BTN_SKIN_PICKING);
            slider.movePrevPosition();
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        player.setPieceSkinIndex(position);
        soundManager.playSound(getContext(), SoundManager.SoundType.BTN_SKIN_PICKING);
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
}
