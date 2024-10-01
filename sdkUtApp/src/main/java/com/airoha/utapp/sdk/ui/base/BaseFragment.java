package com.airoha.utapp.sdk.ui.base;

import android.bluetooth.BluetoothA2dp;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewbinding.ViewBinding;

import com.airoha.sdk.AirohaConnector;
import com.airoha.utapp.sdk.tools.LogUtils;

import java.util.List;

public abstract class BaseFragment<T extends ViewBinding> extends Fragment {

    protected T binding;

    protected BluetoothAdapter mBluetoothAdapter;
    protected BluetoothA2dp mBluetoothProfileA2DP;
    protected AirohaConnector mAirohaConnector;
    protected BluetoothDevice bluetoothDevice;
    protected static boolean hasDeviceConnected = false;


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
   //     initBluetoothWithPermissionOK();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        initBinding(inflater, container);

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView();
    }

    protected abstract void initBinding(LayoutInflater inflater, ViewGroup container);

    protected void initView() {
      //  initBluetoothWithPermissionOK();
    }



    protected BaseActivity getBaseActivity() {
        return (BaseActivity) getActivity();
    }


}
