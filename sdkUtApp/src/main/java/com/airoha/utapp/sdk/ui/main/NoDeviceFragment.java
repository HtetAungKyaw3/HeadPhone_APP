package com.airoha.utapp.sdk.ui.main;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.airoha.utapp.sdk.R;
import com.airoha.utapp.sdk.databinding.FragmentNoDeviceConnectedBinding;
import com.airoha.utapp.sdk.ui.base.BaseFragment;
import com.airoha.utapp.sdk.ui.connecting.SelectDeviceActivity;

public class NoDeviceFragment extends BaseFragment<FragmentNoDeviceConnectedBinding> {
    @Override
    protected void initBinding(LayoutInflater inflater, ViewGroup container) {
        binding = FragmentNoDeviceConnectedBinding.inflate(inflater, container, false);
    }

    @Override
    protected void initView() {
        super.initView();
        binding.tvAddDevice.setOnClickListener(view -> {
            getBaseActivity().startActivityForResult(new Intent(getBaseActivity(), SelectDeviceActivity.class), 1234);
        });
        binding.imgX.setOnClickListener(view -> getBaseActivity().openSocialLink(getString(R.string.x_link)));
        binding.imgInstagram.setOnClickListener(view -> getBaseActivity().openSocialLink(getString(R.string.instagram_link)));
        binding.imgSpotify.setOnClickListener(view -> getBaseActivity().openSocialLink(getString(R.string.spotify_link)));
    }
}
