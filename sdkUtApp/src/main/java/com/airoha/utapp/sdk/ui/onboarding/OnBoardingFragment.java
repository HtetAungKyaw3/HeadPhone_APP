package com.airoha.utapp.sdk.ui.onboarding;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.airoha.utapp.sdk.databinding.FragmentOnboardingBinding;
import com.airoha.utapp.sdk.ui.base.BaseFragment;

public class OnBoardingFragment extends BaseFragment<FragmentOnboardingBinding> {

    private static final String ARG_IMAGE_ID = "ARG_IMAGE_ID";
    private static final String ARG_TITLE_TEXT_ID = "ARG_TITLE_TEXT_ID";
    private static final String ARG_DES_TEXT_ID = "ARG_DES_TEXT_ID";

    public static OnBoardingFragment getNewInstance(int imageId, int titleTextId, int descriptionTextId) {
        OnBoardingFragment instance = new OnBoardingFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(ARG_IMAGE_ID, imageId);
        bundle.putInt(ARG_TITLE_TEXT_ID, titleTextId);
        bundle.putInt(ARG_DES_TEXT_ID, descriptionTextId);
        instance.setArguments(bundle);
        return instance;
    }

    @Override
    protected void initBinding(LayoutInflater inflater, ViewGroup container) {
        binding = FragmentOnboardingBinding.inflate(inflater, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null) {
            binding.imgBackGround.setImageResource(bundle.getInt(ARG_IMAGE_ID));
            binding.tvTitle.setText(bundle.getInt(ARG_TITLE_TEXT_ID));
            binding.tvDescription.setText(bundle.getInt(ARG_DES_TEXT_ID));
        }
    }
}
