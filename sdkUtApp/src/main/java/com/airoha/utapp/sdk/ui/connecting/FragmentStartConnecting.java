package com.airoha.utapp.sdk.ui.connecting;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.airoha.utapp.sdk.databinding.FragmentStartConnectingBinding;
import com.airoha.utapp.sdk.ui.base.BaseFragment;

public class FragmentStartConnecting extends BaseFragment<FragmentStartConnectingBinding> {

    @Override
    protected void initBinding(LayoutInflater inflater, ViewGroup container) {
        binding = FragmentStartConnectingBinding.inflate(inflater, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.backButton.setOnClickListener(view1 -> getBaseActivity().onBackPressed());
        binding.startConnectBtn.setOnClickListener(view12 -> {
            ((SelectDeviceActivity) getBaseActivity()).openConnectingInstructionFragment();
        });
    }
}
