package com.airoha.utapp.sdk.ui.main;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.airoha.liblinker.constant.UuidTable;
import com.airoha.sdk.AirohaConnector;
import com.airoha.sdk.AirohaSDK;
import com.airoha.sdk.api.control.AirohaDeviceListener;
import com.airoha.sdk.api.device.AirohaDevice;
import com.airoha.sdk.api.message.AirohaBaseMsg;
import com.airoha.sdk.api.message.AirohaDeviceInfoMsg;
import com.airoha.sdk.api.message.AirohaEQPayload;
import com.airoha.sdk.api.message.AirohaEQSettings;
import com.airoha.sdk.api.message.AirohaEQStatusMsg;
import com.airoha.sdk.api.utils.AirohaStatusCode;
import com.airoha.sdk.api.utils.ConnectionProtocol;
import com.airoha.sdk.api.utils.ConnectionUUID;
import com.airoha.utapp.sdk.BuildConfig;
import com.airoha.utapp.sdk.R;
import com.airoha.utapp.sdk.bluetooth.BluetoothPermissionAdapter;
import com.airoha.utapp.sdk.constant.SharedKey;
import com.airoha.utapp.sdk.databinding.ActivityMainLayfictoneBinding;
import com.airoha.utapp.sdk.model.EQSetting;
import com.airoha.utapp.sdk.model.GainData;
import com.airoha.utapp.sdk.model.TestDeviceStrategy;
import com.airoha.utapp.sdk.receiver.BluetoothDeviceStateReceiver;
import com.airoha.utapp.sdk.tools.LogUtils;
import com.airoha.utapp.sdk.tools.SingleToast;
import com.airoha.utapp.sdk.ui.base.BaseActivity;
import com.airoha.utapp.sdk.ui.connecting.SelectDeviceActivity;
import com.airoha.utapp.sdk.ui.home.HomeFragment;
import com.airoha.utapp.sdk.ui.splash.SplashActivity;
import com.airoha.utapp.sdk.util.Common;
import com.google.gson.Gson;

import java.util.LinkedList;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

public class LayfictoneMainActivity extends BaseActivity<ActivityMainLayfictoneBinding> implements AirohaConnector.AirohaConnectionListener, AirohaDeviceListener, BluetoothDeviceStateReceiver.BluetoothDevicesListener {

    private String TAG = LayfictoneMainActivity.class.getSimpleName();
    private AirohaConnector mAirohaConnector;
    private AirohaDevice mAirohaDevice;
    private HomeFragment mHomeFragment;
    private BluetoothDeviceStateReceiver mReceiver;
    private boolean doubleBackToExitPressedOnce = false;

    @Override
    protected void initBinding() {
        binding = ActivityMainLayfictoneBinding.inflate(getLayoutInflater());
    }

    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    @Override
    protected void initView() {
        super.initView();
        LogUtils.ENTER_FUNC_LOG();
            binding.imgMenuIcon.setOnClickListener(view -> binding.drawerLayout.openDrawer(Gravity.LEFT));
            binding.tvShowTooltip.setOnClickListener(view -> {
                binding.drawerLayout.closeDrawer(Gravity.LEFT);
                mHomeFragment.startShowToolTip();
            });
            isShowMainActivity = true;
            deviceState = DeviceState.DISCONNECT;
            binding.imgX.setOnClickListener(view -> openSocialLink(getString(R.string.x_link)));
            binding.imgInstagram.setOnClickListener(view -> openSocialLink(getString(R.string.instagram_link)));
            binding.imgSpotify.setOnClickListener(view -> openSocialLink(getString(R.string.spotify_link)));
            binding.verNum.setText("Ver " + BuildConfig.VERSION_NAME);
            AirohaSDK.getInst().init(this);
            appRequestPermisson(new Runnable() {
            @Override public void run() {
                requestBT();
                initAirohaConnectorWithPermisson();
                if (SplashActivity.hasDeviceConnected) {
                    binding.tvShowTooltip.setVisibility(View.VISIBLE);
                    replaceFragment(R.id.frContainer, new HomeFragment(), false);
                }
                else {
                    binding.tvShowTooltip.setVisibility(View.GONE);
                    replaceFragment(R.id.frContainer, new NoDeviceFragment(), false);
                }

                binding.tvAddDevice.setOnClickListener(view -> {
                    startActivityForResult(new Intent(LayfictoneMainActivity.this, SelectDeviceActivity.class), 1234);
                    binding.drawerLayout.closeDrawer(Gravity.LEFT);
                });
            }});
//        if (SplashActivity.isConnected) {
//            replaceFragment(R.id.frContainer, new HomeFragment(), false);
//        }
//        else {
//            LogUtils.LOG("no device connected 111 ");
//            replaceFragment(R.id.frContainer, new NoDeviceFragment(), false);
//        }
       // SplashActivity.isConnected = false;
    }

    /**
     *  This method is used replace fragment after granted location and bluetooth permisson.
     *  If app has ever connected to a device, MyDevice screen has been show.
     *  app never connected device, allow user add a new device.
     *  Airoha Device initialization.
     *  Register a listener event for event change state of Bluetooth Device.
     *
     */
    private void initAirohaConnectorWithPermisson() {
        mAirohaConnector = AirohaSDK.getInst().getAirohaDeviceConnector();
        mAirohaConnector.registerConnectionListener(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {

        if (binding.drawerLayout.isDrawerOpen(binding.constraintDrawer)) {
            binding.drawerLayout.closeDrawer(Gravity.LEFT);
            return;
        }

        if (doubleBackToExitPressedOnce) {
            finishAffinity();
            System.exit(0);
            return;
        }

        doubleBackToExitPressedOnce = true;
        SingleToast.getInstance().showShortToast(getBaseContext(), getString(R.string.exit));
        new Handler().postDelayed(() -> doubleBackToExitPressedOnce = false, 2000);

    }

    /** Handle the result of an intent launched from SelecteDeviceActivity.
     * @param requestCode The integer request code originally supplied to
     *                    startActivityForResult(), allowing you to identify who this
     *                    result came from.
     * @param resultCode  The integer result code returned by the child activity
     *                    through its setResult().
     * @param data        An Intent, which can return result data to the caller
     *                    (various data can be attached to Intent "extras").
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK && data != null && data.getExtras() != null) {
            LogUtils.LOG("Start activity result refer airoha deevice");
            mAirohaDevice = (AirohaDevice) data.getExtras().getSerializable("DEVICE");
            mHomeFragment = HomeFragment.newInstance(mAirohaDevice);
            replaceFragment(R.id.frContainer, mHomeFragment, false);
            showProgressDialog();
            binding.imgMenuIcon.setEnabled(false);
            AirohaSDK.getInst().getAirohaEQControl().getAllEQSettings(this);
            AirohaSDK.getInst().getAirohaDeviceControl().getDeviceInfo(this);
            binding.tvAddDevice.setEnabled(false);
        }

        if (requestCode == Common.REQUEST_CODE_BT) {
            if(resultCode == RESULT_OK) {
                LogUtils.LOG_UI("bluetooth is OK");
               // initBluetoothWithPermissionOK();
            }
            else {
                requestBT();
            }
        }
    }
    /**
     * This method is used show My Device activity.
     * Check list has a value or null with mutuable value connectedDeviceList.
     * This is reference variable obtained from result when started the intent.
     *                      It is used when app connect to device for the first time.
     * @return Nothing.
     */
    public void showMyDevice() {
        binding.tvDisConnect.setVisibility(View.GONE);
        binding.tvShowTooltip.setVisibility(View.VISIBLE);
        binding.tvAddDevice.setEnabled(false);
        mAirohaDevice = mAirohaConnector.getDevice();
        mHomeFragment = HomeFragment.newInstance(mAirohaDevice);
        replaceFragment(R.id.frContainer, mHomeFragment, false);
        showProgressDialog();
        binding.imgMenuIcon.setEnabled(false);
        AirohaSDK.getInst().getAirohaEQControl().getAllEQSettings(this);
        AirohaSDK.getInst().getAirohaDeviceControl().getDeviceInfo(this);
    }

    private boolean isShowHome() {
        return getShared().getBoolean(SharedKey.SHOW_HOME, false);
    }

    public void showAddDevice() {
        replaceFragment(R.id.frContainer, new NoDeviceFragment(), false);
    }

    /** Connect to device when it is already connected and is saved in shared preferences.
     *
     */
    public void connect() {
        if (isA2dpConnected(bluetoothDevice)) {
            deviceState = DeviceState.CONNECT;
            showProgressDialog();
            AirohaDevice airohaDevice = new AirohaDevice();
            airohaDevice.setApiStrategy(new TestDeviceStrategy());
            airohaDevice.setTargetAddr(bluetoothDevice.getAddress());
            airohaDevice.setDeviceName(bluetoothDevice.getName());
            airohaDevice.setDeviceMAC(bluetoothDevice.getAddress());
            airohaDevice.setPreferredProtocol(ConnectionProtocol.PROTOCOL_SPP);
            ConnectionUUID connectionUUID = new ConnectionUUID(UUID.fromString(UuidTable.AIROHA_SPP_UUID.toString()));
            mAirohaConnector.connect(airohaDevice, connectionUUID);
            LogUtils.LOG("mac addrs ::::  " +this+ bluetoothDevice.getAddress());
        }
//        } else {
//            Toast.makeText(this, "Please turn on the headphones first.[Spash   connect]", Toast.LENGTH_SHORT).show();
//            // TODO: update later.
//        }
    }

    protected void onResume() {
        super.onResume();
        LogUtils.LOG_UI("APP WENT COMEBACK");
        if (BluetoothPermissionAdapter.shared().isPermissionFullGranted()) {
            LogUtils.LOG_UI("Has permission");
            mReceiver = new BluetoothDeviceStateReceiver(this);
            registerReceiver(mReceiver, BluetoothDeviceStateReceiver.getIntentFilter());
        }
        appState = AppState.RESUME;
        mAirohaConnector = AirohaSDK.getInst().getAirohaDeviceConnector();
        mAirohaConnector.registerConnectionListener(this);

        if (deviceState.equals(DeviceState.DISCONNECT)
                && LoadConnectTarget(Common.DEVICE_CONNECTED) != null
                && BluetoothPermissionAdapter.shared().isPermissionFullGranted()) {
            LogUtils.LOG_UI("onResume device State still disconnect");
            initBluetoothWithPermissionOK();
        }
        LogUtils.LOG_UI("is Show main" + isShowMainActivity);
    }

    @Override
    protected void onPause() {
        super.onPause();
        LogUtils.LOG_UI("APP WENT BACKGROUND");
        appState = AppState.BACKGROUND;
        mAirohaConnector.disconnect();
        deviceState = DeviceState.DISCONNECT;
        if (BluetoothPermissionAdapter.shared().isPermissionFullGranted() && mReceiver != null) {
            mAirohaConnector.unregisterConnectionListener(this);
            unregisterReceiver(mReceiver);
        }
    }
    private void handleWhenBtOff() {

        showScreenDeviceNotConnect("Do you want to try connecting again?", "Retry", new Runnable() {
            @Override
            public void run() {
                if (bluetoothState.equals(BluetoothState.BT_OFF)) {
                    showScreenDeviceNotConnectCount2("Please turn it on to countinue. Would you like to go the Bluetooth screen", "Settings", new Runnable() {
                        @Override
                        public void run() {
                            Intent intent = new Intent(Settings.ACTION_BLUETOOTH_SETTINGS);
                            startActivity(intent);
                        }
                    });
                } else {
                }


            }

        });
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onDevicesListChanged(Set<BluetoothDevice> devices) {

        if (mAirohaDevice == null) {
            return;
        }

        if (mBluetoothAdapter.getState() == BluetoothAdapter.STATE_OFF) {
            mAirohaConnector.disconnect();
            binding.tvDisConnect.setVisibility(View.VISIBLE);
            binding.tvShowTooltip.setVisibility(View.GONE);
            LogUtils.LOG_UI("BLE offf");
            con = false;
            handleWhenBtOff();
            return;
        }

        for (BluetoothDevice device : devices) {
            if (device.getAddress().equals(mAirohaDevice.getDeviceMAC())) {
                if (!isA2dpConnected(device)) {
                    binding.tvDisConnect.setVisibility(View.VISIBLE);
                    binding.tvShowTooltip.setVisibility(View.GONE);
                }
            }
        }
    }

    @Override
    public void onStatusChanged(int i) {
        switch (i) {
            case AirohaConnector.CONNECTED:
                deviceState = DeviceState.CONNECT;
             //   binding.tvAddDevice.setEnabled(false);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        binding.tvAddDevice.setEnabled(false);
                    }
                });
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        showMyDevice();
                    }
                });
                if (bluetoothState.equals(BluetoothState.BT_ON) && dialogNotify != null){
                    dialogNotify.dismiss();
                }
                break;
            case AirohaConnector.DISCONNECTED:
                LogUtils.LOG_UI("Device Disconnect");
                deviceState = DeviceState.DISCONNECT;
                break;
        }
    }

    @Override
    public void onDataReceived(AirohaBaseMsg airohaBaseMsg) {

    }

    @Override
    public void onRead(AirohaStatusCode airohaStatusCode, AirohaBaseMsg airohaBaseMsg) {

        switch (airohaStatusCode){
            case STATUS_SUCCESS:
                hideProgressDialog();
                enableMenuButton();
                if (airohaBaseMsg instanceof AirohaEQStatusMsg) {
                    AirohaEQStatusMsg airohaEQStatusMsg = (AirohaEQStatusMsg) airohaBaseMsg;
                    LogUtils.LOG_UI("size is : " + airohaEQStatusMsg.getMsgContent().size());
                    for (AirohaEQSettings settings : airohaEQStatusMsg.getMsgContent()) {
                        Log.d("EQDATA", "===========================================================>");
                        Log.d("EQDATA", "CATEGORY: " + settings.getCategoryId() + " --- STATUS: " + settings.getStatus());
                        Log.d("EQDATA", new Gson().toJson(settings));
                        Log.d("EQDATA", "===========================================================>");
                        int categoryId = settings.getCategoryId();
                        EQSetting eqSetting = EQSetting.getEQSetting(mAirohaDevice.getDeviceName(), categoryId);
                        if (categoryId == 103 || categoryId == 104) {
                            eqSetting.getGains().get(0).setGain(settings.getEqPayload().getIirParams().get(0).getGainValue());
                            eqSetting.getGains().get(1).setGain(settings.getEqPayload().getIirParams().get(1).getGainValue());
                            eqSetting.getGains().get(2).setGain(settings.getEqPayload().getIirParams().get(2).getGainValue());
                            eqSetting.getGains().get(3).setGain(settings.getEqPayload().getIirParams().get(3).getGainValue());
                        }
                        try {
                            if (eqSetting != null && settings.getStatus() == 1) {
                                runOnUiThread(() -> mHomeFragment.updateEQData(eqSetting));
                            }
                        }
                        catch (Exception exception) {

                        }
                    }
                }
                final AirohaDeviceInfoMsg deviceInfoMessage = (AirohaDeviceInfoMsg) airohaBaseMsg;
                LinkedList<AirohaDevice> content = (LinkedList<AirohaDevice>) deviceInfoMessage.getMsgContent();
                if (content.size() > 0) {
                    AirohaDevice airohaDevice = content.get(0);
                    runOnUiThread(() -> mHomeFragment.updateFirmwareVersion(airohaDevice.getFirmwareVer()));
                }
                break;
            case STATUS_FAIL:
                hideProgressDialog();
                enableMenuButton();
                break;
            case STATUS_TIMEOUT:
                hideProgressDialog();
                enableMenuButton();
                // turn of dialog
                // notify error get
                break;
            case STATUS_CANCEL:
                hideProgressDialog();
                enableMenuButton();
                break;
            case STATUS_UNKNOWN:
                hideProgressDialog();
                enableMenuButton();
                break;
        }
    }

    private void enableMenuButton() {
        runOnUiThread(() -> {
            binding.imgMenuIcon.setEnabled(true);
        });
    }

    @Override
    public void onChanged(AirohaStatusCode airohaStatusCode, AirohaBaseMsg airohaBaseMsg) {
        switch (airohaStatusCode){
            case STATUS_SUCCESS:
                hideProgressDialog();
                enableMenuButton();
                LogUtils.LOG_UI("[SetEQ] success");
                AirohaSDK.getInst().getAirohaEQControl().getRunningEQSetting(LayfictoneMainActivity.this);
                break;
            case STATUS_FAIL:
                hideProgressDialog();
                enableMenuButton();
                break;
            case STATUS_TIMEOUT:
                hideProgressDialog();
                enableMenuButton();
              //  Toast.makeText(LayfictoneMainActivity.this, "Could not change EQ, device timeout!", Toast.LENGTH_LONG).show();
                break;
            case STATUS_CANCEL:
                hideProgressDialog();
                enableMenuButton();
              //  Toast.makeText(LayfictoneMainActivity.this, "Could not change EQ, play music first please!", Toast.LENGTH_LONG).show();
                LogUtils.LOG_UI("[SetEQ] cancel ::");
                break;
            case STATUS_UNKNOWN:
                hideProgressDialog();
                enableMenuButton();
                break;
        }
    }

    public void applyNewEQSetting(EQSetting eqSetting) {
        showProgressDialog();
        binding.imgMenuIcon.setEnabled(false);
        if (eqSetting.getCategoryId() < 100) {
            AirohaSDK.getInst().getAirohaEQControl().setEQSetting(eqSetting.getCategoryId(), null, false, LayfictoneMainActivity.this);
        } else {
            AirohaEQPayload airohaEQPayload = eqSetting.getEQPayload();
            AirohaSDK.getInst().getAirohaEQControl().setEQSetting(eqSetting.getCategoryId(), airohaEQPayload, true, LayfictoneMainActivity.this);
        }
    }
}
