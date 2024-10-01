/*
 * Created by dotrinh on 5/17/23, 5:50 PM
 * Copyright (c) 2023. USE Inc. All rights reserved.
 */

package com.airoha.utapp.sdk.fragment.slider;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.airoha.utapp.sdk.databinding.SliderLayout1Binding;


public class Slider1Frag extends Fragment implements View.OnClickListener {

    SliderLayout1Binding self;

    TextView scrollBtn;
    Button okLicense;

    //region INIT
    public static Slider1Frag newInstance() {
        return new Slider1Frag();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup fragment, Bundle savedInstanceState) {
        self = SliderLayout1Binding.inflate(inflater, fragment, false);
        return self.getRoot();
    }
    //endregion

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }
}
