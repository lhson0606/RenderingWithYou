package com.dy.app.ui.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.TextSliderView;
import com.daimajia.slider.library.Tricks.ViewPagerEx;
import com.dy.app.R;
import com.dy.app.gameplay.player.Player;
import com.dy.app.gameplay.player.PlayerInventory;
import com.dy.app.manager.SoundManager;
import com.dy.app.utils.DyConst;
import com.dy.app.utils.Utils;

import java.util.List;

public class FragmentTerrainSelection extends Fragment
        implements View.OnClickListener, ViewPagerEx.OnPageChangeListener {

    public final static String TAG = "FragmentTerrainSelection";
    private SliderLayout slider;
    private Button btnNext, btnPrev;
    private final SoundManager soundManager = SoundManager.getInstance();
    private Player player;

    public static FragmentTerrainSelection newInstance(){
        FragmentTerrainSelection fragment = new FragmentTerrainSelection();
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
        List<Long> id = (List<Long>)player.inventory.get(PlayerInventory.KEY_TERRAIN_SKIN);
        slider.removeAllSliders();

        for(int i = 0; i<id.size(); i++){
            int skinId = id.get(i).intValue();
            TextSliderView textSliderView = new TextSliderView(slider.getContext());
            int res = DyConst.terrain_skin_thumbnails[skinId];
            String name =  Utils.toLocalCapitalize(DyConst.terrain_models[skinId].substring(0, DyConst.terrain_models[skinId].length()-4).replace("_", " ").toUpperCase());
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
        updateUI();
        return view;
    }

    private void updateUI() {
        int index = Utils.long2int((long)player.inventory.get(PlayerInventory.KEY_TERRAIN_SKIN_INDEX));
        slider.setCurrentPosition(index);
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
        Integer intTemp = position;
        Long longTemp = intTemp.longValue();
        player.inventory.set(PlayerInventory.KEY_TERRAIN_SKIN_INDEX, longTemp);
        soundManager.playSound(getContext(), SoundManager.SoundType.BTN_SKIN_PICKING);
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
}
