package com.airoha.utapp.sdk.ui.connecting;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothDevice;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.recyclerview.widget.LinearLayoutManager;

import com.airoha.utapp.sdk.R;
import com.airoha.utapp.sdk.bluetooth.BluetoothPermissionAdapter;
import com.airoha.utapp.sdk.databinding.SelectConnectedDeviceBinding;
import com.airoha.utapp.sdk.receiver.BluetoothDeviceStateReceiver;
import com.airoha.utapp.sdk.tools.LogUtils;
import com.airoha.utapp.sdk.ui.base.BaseFragment;
import com.airoha.utapp.sdk.ui.connecting.devListRecyclerView.DeviceListAdapter;
import com.airoha.utapp.sdk.util.BluetoothDeviceUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class ListDeviceFragment extends BaseFragment<SelectConnectedDeviceBinding> implements DeviceListAdapter.OnItemClickListener, BluetoothDeviceStateReceiver.BluetoothDevicesListener {

    private BluetoothDeviceStateReceiver mReceiver;
    private List<String> mDevices = new ArrayList<>();

    private List<BluetoothDevice> mAirohaDevice = new ArrayList<>();
    private DeviceListAdapter mAdapter;

    @Override
    protected void initBinding(LayoutInflater inflater, ViewGroup container) {
        binding = SelectConnectedDeviceBinding.inflate(inflater, container, false);
    }


    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    @Override
    protected void initView() {
        super.initView();
        LogUtils.ENTER_FUNC_LOG();
        binding.recyclerListDevice.setLayoutManager(new LinearLayoutManager(getContext()));
        mDevices.add(getString(R.string.broadband));
        mAdapter = new DeviceListAdapter(mDevices, this);
        binding.recyclerListDevice.setAdapter(mAdapter);
        binding.backButton.setOnClickListener(view1 -> getBaseActivity().finish());
        if (BluetoothPermissionAdapter.shared().isPermissionFullGranted()) {
            mReceiver = new BluetoothDeviceStateReceiver(this);
            getBaseActivity().registerReceiver(mReceiver, BluetoothDeviceStateReceiver.getIntentFilter());
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        getBaseActivity().unregisterReceiver(mReceiver);
    }

    @Override
    public void onItemClick(String deviceName) {
        // TODO: put selected device info here
        LogUtils.LOG("put device :" + deviceName);
        ((SelectDeviceActivity) getBaseActivity()).openStartConnectingFragment(deviceName);
    }

  @Override
    public void onDevicesListChanged(Set<BluetoothDevice> devices) {
      mAirohaDevice.clear();
        for (BluetoothDevice device : devices) {
            if (BluetoothDeviceUtil.isDeviceSupported(device))
                mAirohaDevice.add(device);
        }
//        mAdapter.notifyDataSetChanged();
    }
}
