package com.airoha.utapp.sdk.ui.connecting;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothProfile;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import com.airoha.liblinker.constant.UuidTable;
import com.airoha.sdk.AirohaConnector;
import com.airoha.sdk.AirohaSDK;
import com.airoha.sdk.api.device.AirohaDevice;
import com.airoha.sdk.api.message.AirohaBaseMsg;
import com.airoha.sdk.api.utils.ConnectionProtocol;
import com.airoha.sdk.api.utils.ConnectionUUID;
import com.airoha.utapp.sdk.R;
import com.airoha.utapp.sdk.constant.SharedKey;
import com.airoha.utapp.sdk.databinding.ActivityConnectingBinding;
import com.airoha.utapp.sdk.model.TestDeviceStrategy;
import com.airoha.utapp.sdk.tools.LogUtils;
import com.airoha.utapp.sdk.ui.base.BaseActivity;
import com.airoha.utapp.sdk.ui.home.HomeFragment;
import com.airoha.utapp.sdk.util.Common;

import java.util.HashSet;
import java.util.UUID;
import java.util.Set;

@SuppressLint("MissingPermission")
public class SelectDeviceActivity extends BaseActivity<ActivityConnectingBinding> implements AirohaConnector.AirohaConnectionListener {
    private BluetoothDevice mSelectedDevice;

    @Override
    protected void initBinding() {
        binding = ActivityConnectingBinding.inflate(getLayoutInflater());
    }

    @Override
    protected void initView() {
        super.initView();
        LogUtils.ENTER_FUNC_LOG();
        // init bluetoothAdapter
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        mBluetoothAdapter.getProfileProxy(this, new BluetoothProfileListener(), BluetoothProfile.A2DP);
        replaceFragment(R.id.frContainer, new ListDeviceFragment(), false);
        AirohaSDK.getInst().init(this);
        mAirohaConnector = AirohaSDK.getInst().getAirohaDeviceConnector();
        mAirohaConnector.registerConnectionListener(this);
      LogUtils.LEAVE_FUNC_LOG("END INIT VIEW SELECT ACTIVITY");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
     //   mAirohaConnector.unregisterConnectionListener(this);
    }

    /** Connect device that was selected from list device.
     * @param device device was seleceted.
     *        mSelectedDevice hold reference of user selected device.
     */
    // TODO: update put selected device info
    public void openStartConnectingFragment(String deviceName) {
      //  mSelectedDevice = device;
        addFragment(R.id.frContainer, new FragmentStartConnecting(), true);
    }

    // TODO: update put selected device info
    public void openConnectingInstructionFragment() {
        addFragment(R.id.frContainer, new FragmentConnecting1stInstruction(), true);
    }

    public void openConnecting2ndInstructionFragment() {
        addFragment(R.id.frContainer, new FragmentConnecting2ndInstruction(), true);
    }

    /** Method is used to connect to device for the first time, when the user select device from list device on ListDeviceFragment.
     * Notify bluetooth connect status change to onStatusChanged.
     * @param
     * @retun Nothing.
     */

    public void connect(BluetoothDevice bluetoothDevice) {
        if (isA2dpConnected(bluetoothDevice)) {
            LogUtils.LOG("befre show dialog");
            showProgressDialog();
            AirohaDevice airohaDevice = new AirohaDevice();
            airohaDevice.setApiStrategy(new TestDeviceStrategy());
            airohaDevice.setTargetAddr(bluetoothDevice.getAddress());
            airohaDevice.setDeviceName(bluetoothDevice.getName());
            airohaDevice.setDeviceMAC(bluetoothDevice.getAddress());
            airohaDevice.setPreferredProtocol(ConnectionProtocol.PROTOCOL_SPP);
            ConnectionUUID connectionUUID = new ConnectionUUID(UUID.fromString(UuidTable.AIROHA_SPP_UUID.toString()));
            mAirohaConnector.connect(airohaDevice, connectionUUID);
            SaveConnectTarget(bluetoothDevice.getAddress(),Common.DEVICE_CONNECTED);
        } else {
            Toast.makeText(this, "Please turn on the headphones first.", Toast.LENGTH_SHORT).show();
            // TODO: update later.
        }
        LogUtils.LEAVE_FUNC_LOG();
    }


    /** Handler result airoha bluetooth connect.
     * @param i status Airoha bluetooth
     *          CONNECTED: put device informtion to bundle, notify start activity to MainActivity.
     */
    @Override
    public void onStatusChanged(int i) {
        switch (i) {
            case AirohaConnector.CONNECTED:
                LogUtils.LOG("AirohaConnector.CONNECTED");

                hideProgressDialog();
                Bundle bundle = new Bundle();
                bundle.putSerializable("DEVICE", mAirohaConnector.getDevice());
                Intent intent = new Intent();
                intent.putExtras(bundle);
                setResult(Activity.RESULT_OK, intent);
                finish();
                break;
            case AirohaConnector.CONNECTION_ERROR:
            case AirohaConnector.DISCONNECTED:
            case AirohaConnector.INITIALIZATION_FAILED:
                runOnUiThread(() -> {
                    hideProgressDialog();
                   // Toast.makeText(SelectDeviceActivity.this, "Connection failed, please try again.", Toast.LENGTH_SHORT).show();
                });
                break;
        }
    }

    @Override
    public void onDataReceived(AirohaBaseMsg airohaBaseMsg) {
    }
}
