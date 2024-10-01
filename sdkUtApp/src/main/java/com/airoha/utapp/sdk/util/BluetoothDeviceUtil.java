package com.airoha.utapp.sdk.util;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothDevice;

import androidx.annotation.DrawableRes;

import com.airoha.sdk.api.device.AirohaDevice;
import com.airoha.utapp.sdk.R;

import java.util.ArrayList;

@SuppressLint("MissingPermission")
public class BluetoothDeviceUtil {

    public static boolean isDeviceSupported(BluetoothDevice device) {
        // TODO: update condition later
        return true;
    }

    public static String getDisplayName(BluetoothDevice device) {
        // TODO: update name format later
        return device.getName();
    }

    @DrawableRes
    public static int getDisplayIcon() {
        // TODO: update name format later
        return R.drawable.broadband_s;
    }

    public static ArrayList<Integer> getDeviceSliderImages(AirohaDevice device) {
        ArrayList<Integer> images = new ArrayList<>();
        images.add(R.drawable.broadband_s);
        images.add(R.drawable.broadband_s);
        images.add(R.drawable.broadband_s);
        return images;
    }

}
