package com.airoha.utapp.sdk.ui.connecting;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.airoha.utapp.sdk.databinding.FragmentConnectingInstructionBinding;
import com.airoha.utapp.sdk.ui.base.BaseFragment;

public class FragmentConnecting1stInstruction extends BaseFragment<FragmentConnectingInstructionBinding> {
    @Override
    protected void initBinding(LayoutInflater inflater, ViewGroup container) {
        binding = FragmentConnectingInstructionBinding.inflate(inflater, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.nextBtn.setOnClickListener(view1 -> {
            ((SelectDeviceActivity) getBaseActivity()).openConnecting2ndInstructionFragment();
        });
        binding.backButton.setOnClickListener(view12 -> getBaseActivity().onBackPressed());
    }
}
