/*
 * Created by dotrinh on 5/17/23, 5:50 PM
 * Copyright (c) 2023. USE Inc. All rights reserved.
 */

package com.airoha.utapp.sdk.fragment.slider;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.airoha.utapp.sdk.databinding.SliderLayout4Binding;

public class Slider4Frag extends Fragment implements View.OnClickListener {

    SliderLayout4Binding self;


    public static Slider4Frag newInstance() {
        return new Slider4Frag();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup fragment, Bundle savedInstanceState) {
        self = SliderLayout4Binding.inflate(inflater, fragment, false);
        return self.getRoot();
    }

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
