package com.airoha.utapp.sdk.receiver;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import java.util.Set;

@SuppressLint("MissingPermission")
public class BluetoothDeviceStateReceiver extends BroadcastReceiver {

    private BluetoothDevicesListener mListener;

    public BluetoothDeviceStateReceiver(BluetoothDevicesListener listener) {
        mListener = listener;
        BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
        Set<BluetoothDevice> devices = adapter.getBondedDevices();
        mListener.onDevicesListChanged(devices);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED.equals(intent.getAction())
                || BluetoothAdapter.ACTION_STATE_CHANGED.equals(intent.getAction())) {
            BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
            Set<BluetoothDevice> devices = adapter.getBondedDevices();
            mListener.onDevicesListChanged(devices);
        }
    }

    public static IntentFilter getIntentFilter() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED);
        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        return filter;
    }

    public interface BluetoothDevicesListener {
        void onDevicesListChanged(Set<BluetoothDevice> devices);
    }
}
