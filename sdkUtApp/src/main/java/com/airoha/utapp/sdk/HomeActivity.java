package com.airoha.utapp.sdk;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

import com.airoha.utapp.sdk.tools.LogUtils;
import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;

import java.util.ArrayList;

public class HomeActivity extends AppCompatActivity {
    private String TAG = HomeActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        LogUtils.ENTER_FUNC_LOG();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        ImageSlider imageSlider = findViewById(R.id.imageSlider);
        ArrayList<SlideModel> slideModels = new ArrayList<>();
        slideModels.add(new SlideModel(R.drawable.broadband_s, ScaleTypes.CENTER_INSIDE));
        slideModels.add(new SlideModel(R.drawable.broadband_s, ScaleTypes.CENTER_INSIDE));
        slideModels.add(new SlideModel(R.drawable.broadband_s, ScaleTypes.CENTER_INSIDE));

        imageSlider.setImageList(slideModels);

    }
}