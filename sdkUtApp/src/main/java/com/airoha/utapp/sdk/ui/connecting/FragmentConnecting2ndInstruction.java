package com.airoha.utapp.sdk.ui.connecting;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothProfile;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.airoha.utapp.sdk.R;
import com.airoha.utapp.sdk.databinding.FragmentConnecting2ndInstructionBinding;

import com.airoha.utapp.sdk.tools.LogUtils;
import com.airoha.utapp.sdk.ui.base.BaseFragment;

import java.util.List;

public class FragmentConnecting2ndInstruction extends BaseFragment<FragmentConnecting2ndInstructionBinding> {
    private final static String headPhoneName = "The_Industrial-ist_Bb".toLowerCase();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LogUtils.LOG_UI("onCreate Framgnet 2");
        //   initBluetoothWithPermissionOK();
    }

    @Override
    protected void initBinding(LayoutInflater inflater, ViewGroup container) {
        binding = FragmentConnecting2ndInstructionBinding.inflate(inflater, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.backButton.setEnabled(true);
        binding.openSettingBtn.setOnClickListener(view1 -> {
            Intent intent = new Intent(Settings.ACTION_BLUETOOTH_SETTINGS);
            startActivity(intent);
        });
        binding.backButton.setOnClickListener(view12 -> getBaseActivity().onBackPressed());
        binding.closeBtn.setOnClickListener(view13 -> getBaseActivity().finish());
    }

    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public void onResume() {
        super.onResume();
        LogUtils.LOG_UI("app come back");
        //   if (SplashActivity.isConnected) {
        initBluetoothWithPermissionOK();
        // }
    }

    private void initBluetoothWithPermissionOK() {
        LogUtils.LOG_UI("start read status bt");
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        mBluetoothAdapter.getProfileProxy(getBaseActivity(), new BluetoothProfileListener(), BluetoothProfile.A2DP);
    }

    private class BluetoothProfileListener implements BluetoothProfile.ServiceListener {
        @Override
        public void onServiceConnected(int i, BluetoothProfile bluetoothProfile) {
            if (i == BluetoothProfile.A2DP) {

                List<BluetoothDevice> bluetoothDevices = bluetoothProfile.getConnectedDevices();
                if (bluetoothDevices != null) {
                    LogUtils.LOG_UI("zie" + bluetoothDevices.size());
                }

                for (BluetoothDevice dev : bluetoothDevices) {
                    if (dev.getName().toLowerCase().contains(headPhoneName)) {
                        LogUtils.LOG_UI("dhdjdj");
                    }

                    if (dev.getName().contains("AVIOT") || dev.getName().toLowerCase().contains(headPhoneName)) {
                        if (bluetoothProfile.getConnectionState(dev) == BluetoothProfile.STATE_CONNECTED) {
                            LogUtils.LOG_UI("is connected");
                            hasDeviceConnected = true;
                            binding.imageView2.setImageDrawable(getResources().getDrawable(R.drawable.icon_checked));
                            binding.content.setText(getString(R.string.connect_ble_devices_success));
                            binding.backButton.setEnabled(false);
                            binding.openSettingBtn.setEnabled(false);
                            binding.openSettingBtn.setClickable(false);
                            binding.openSettingBtn.setOnClickListener(null);
                            binding.closeBtn.setOnClickListener(view13 -> {
                                ((SelectDeviceActivity) getBaseActivity()).connect(dev);
                            });
                        }
                    }
                }
            }
        }

        @Override
        public void onServiceDisconnected(int i) {

        }
    }


}