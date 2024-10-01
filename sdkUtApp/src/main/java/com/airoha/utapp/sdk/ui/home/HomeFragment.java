package com.airoha.utapp.sdk.ui.home;

import android.Manifest;
import android.annotation.SuppressLint;

import android.content.pm.PackageManager;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.widget.TextViewCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.PagerAdapter;

import com.airoha.sdk.api.device.AirohaDevice;
import com.airoha.utapp.sdk.R;
import com.airoha.utapp.sdk.constant.Arg;
import com.airoha.utapp.sdk.databinding.FragmentHomeBinding;
import com.airoha.utapp.sdk.databinding.ItemHomeSliderBinding;
import com.airoha.utapp.sdk.model.EQSetting;
import com.airoha.utapp.sdk.model.GainData;
import com.airoha.utapp.sdk.tools.LogUtils;
import com.airoha.utapp.sdk.ui.base.BaseFWDialogFragment;
import com.airoha.utapp.sdk.ui.base.BaseFragment;

import com.airoha.utapp.sdk.ui.home.fwupdate.UpdatePopUpFragment;
import com.airoha.utapp.sdk.ui.home.fwupdate.UpdateResultPopUpFragment;
import com.airoha.utapp.sdk.ui.home.fwupdate.UpdatingPopUpFragment;
import com.airoha.utapp.sdk.ui.main.LayfictoneMainActivity;
import com.airoha.utapp.sdk.util.BluetoothDeviceUtil;
import com.google.android.material.tabs.TabLayout;
import com.h6ah4i.android.widget.verticalseekbar.VerticalSeekBar;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import android.content.Context;
import it.sephiroth.android.library.tooltip.Tooltip;
import android.content.SharedPreferences;
import android.graphics.Color;


public class HomeFragment extends BaseFragment<FragmentHomeBinding> implements BaseFWDialogFragment.FWDialogListener {

    private HomeMusicFragment homeMusicFragment;
    private AirohaDevice mAirohaDevice;
    private final List<TextView> gainLabels = new ArrayList<>();
    private final List<TextView> gainStates = new ArrayList<>();
    private final List<TextView> gainStatesHigtlight = new ArrayList<>();
    private final List<VerticalSeekBar> seekBars = new ArrayList<>();
    private EQSetting currentEQSetting;
    private boolean isDisableProgressChange = false;
    private final ArrayList<Integer> deviceImages = new ArrayList<>();
    private Handler enableProgressChangeHandler = new Handler();
    private Runnable enableProgressChangeRunnable = () -> isDisableProgressChange = false;
    private Handler updateCustomGainsHandler = new Handler();
    private static boolean isAiroha = false;
    private static final String PREF_NAME = "MyPrefs";
    private static final String IS_SHOW_TOOL_TIP_KEY = "isShowToolTip";
    private static final int delayToolTip = 3000;
    private Handler handler = new Handler();
    private int currentIndex = 1;
    private boolean isChangeEQMode = false;
    private Runnable updateCustomGainsRunnable = () -> {
        float valueLo = (float) binding.seekBarLo.getProgress() / 10f;
        float valueLoM = (float) binding.seekBarLoM.getProgress() /  10.0f;
        float valueHiM = (float) binding.seekBarHiM.getProgress() / 10.0f;
        float valueHi = (float) binding.seekBarHi.getProgress() / 10.0f;
        if (currentEQSetting != null && currentEQSetting.isCustomMode()) {
            LayfictoneMainActivity mainActivity = (LayfictoneMainActivity) getBaseActivity();
            ArrayList<GainData> baseEQSettingGains = EQSetting.getBaseEQSetting(mAirohaDevice.getDeviceName()).getGains();
            currentEQSetting.getGains().get(0).setGain(valueLo + baseEQSettingGains.get(0).getGain());
            currentEQSetting.getGains().get(1).setGain(valueLoM + baseEQSettingGains.get(1).getGain());
            currentEQSetting.getGains().get(2).setGain(valueHiM + baseEQSettingGains.get(2).getGain());
            currentEQSetting.getGains().get(3).setGain(valueHi + baseEQSettingGains.get(3).getGain());
            mainActivity.applyNewEQSetting(currentEQSetting);
        }
    };
    Timer mTimer = new Timer();

    public static HomeFragment newInstance(AirohaDevice airohaDevice) {
        LogUtils.ENTER_FUNC_LOG();
        isAiroha = true;
        HomeFragment instance = new HomeFragment();
        Bundle args = new Bundle();
        args.putSerializable(Arg.ARG_DEVICE, airohaDevice);
        instance.setArguments(args);
        return instance;
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public void openUpdateDialog() {
        UpdatePopUpFragment dialogFragment = new UpdatePopUpFragment();
        dialogFragment.setListener(HomeFragment.this);
        dialogFragment.show(getChildFragmentManager(), UpdatingPopUpFragment.class.getName());
    }

    @Override
    public void openInstallingDialog() {
        UpdatingPopUpFragment dialogFragment = new UpdatingPopUpFragment();
        dialogFragment.setListener(HomeFragment.this);
        dialogFragment.show(getChildFragmentManager(), UpdatingPopUpFragment.class.getName());
    }

    @Override
    public void openInstallResultDialog(UpdatingPopUpFragment.UpdateResult result) {
        UpdateResultPopUpFragment dialogFragment = UpdateResultPopUpFragment.getNewInstance(result);
        dialogFragment.show(getChildFragmentManager(), UpdateResultPopUpFragment.class.getName());
    }

    enum TypeSelected {
        MUSIC, WORK, CHILL, THEATER, CUSTOM1, CUSTOM2;

        public static TypeSelected getType(String name) {
            for (TypeSelected typeSelected : TypeSelected.values()) {
                if (typeSelected.name().equals(name)) {
                    return typeSelected;
                }
            }
            return null;
        }
    }

    enum TypeEQGain {
        LO, LOM, HIM, HI
    }

    @Override
    protected void initBinding(LayoutInflater inflater, ViewGroup container) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void initView() {
        super.initView();
        gainLabels.clear();
        gainStates.clear();
        seekBars.clear();
        gainLabels.add(binding.tvLGain);
        gainLabels.add(binding.tvLMGain);
        gainLabels.add(binding.tvHMGain);
        gainLabels.add(binding.tvHGain);
        gainStates.add(binding.tvLState);
        gainStates.add(binding.tvLMState);
        gainStates.add(binding.tvHMState);
        gainStates.add(binding.tvHState);
        gainStatesHigtlight.add(binding.tvLState1);
        gainStatesHigtlight.add(binding.tvLMState1);
        gainStatesHigtlight.add(binding.tvHMState1);
        gainStatesHigtlight.add(binding.tvHState1);
        seekBars.add(binding.seekBarLo);
        seekBars.add(binding.seekBarLoM);
        seekBars.add(binding.seekBarHiM);
        seekBars.add(binding.seekBarHi);
        if (isAiroha) {
            mAirohaDevice = (AirohaDevice) getArguments().getSerializable(Arg.ARG_DEVICE);
            binding.tvDeviceName.setText(mAirohaDevice.getDeviceName());
        }

        homeMusicFragment = new HomeMusicFragment();
        for (VerticalSeekBar seekBar : seekBars) {
            seekBar.setOnTouchListener((view, motionEvent) -> {
                if (!seekBar.isActivated()) {
                    return true;
                } else {
                    if (motionEvent.getAction() == MotionEvent.ACTION_UP || motionEvent.getAction() == MotionEvent.ACTION_CANCEL) {
                        updateCustomGainsHandler.removeCallbacks(updateCustomGainsRunnable);
                        updateCustomGainsHandler.postDelayed(updateCustomGainsRunnable, 300);
                        return true;
                    }
                    return false;
                }
            });
            seekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener());
        }
        binding.tvModeMusic.setOnClickListener(view -> applyEQSetting(EQSetting.D01v_GMU_MUSIC, TypeSelected.MUSIC));

        binding.tvModeWork.setOnClickListener(view -> applyEQSetting(EQSetting.D01v_GMU_WORK, TypeSelected.WORK));

        binding.tvModeChill.setOnClickListener(view -> applyEQSetting(EQSetting.USE_GMU_CHILL, TypeSelected.CHILL));

        binding.tvModeTheater.setOnClickListener(view -> applyEQSetting(EQSetting.D01v_GMU_THEATER, TypeSelected.THEATER));

        binding.tvModeCustom1.setOnClickListener(view -> applyEQSetting(EQSetting.D01v_GMU_CUSTOM1, TypeSelected.CUSTOM1));

        binding.tvModeCustom2.setOnClickListener(view -> applyEQSetting(EQSetting.D01v_GMU_CUSTOM2, TypeSelected.CUSTOM2));

        binding.updateBtn.setOnClickListener(view -> openUpdateDialog());
        binding.tvAllReset.setOnClickListener(view -> {
            for (SeekBar seekBar : seekBars) {
                seekBar.setProgress(0);
                seekBar.setActivated(true);
                changeSBState(seekBar);
            }
            for (TextView textView : gainStates) {
                textView.setText(getString(R.string.on));
                textView.setEnabled(true);
                textView.setActivated(true);
            }
            updateCustomGainsHandler.removeCallbacks(updateCustomGainsRunnable);
            updateCustomGainsHandler.postDelayed(updateCustomGainsRunnable, 300);
        });
        initImageSlider();
        displayFragment();
        changeTVState(binding.tvLState, binding.seekBarLo);
        changeTVState(binding.tvLMState, binding.seekBarLoM);
        changeTVState(binding.tvHMState, binding.seekBarHiM);
        changeTVState(binding.tvHState, binding.seekBarHi);
        changeTVStateHighlight(binding.tvLState1);
        changeTVStateHighlight(binding.tvLMState1);
        changeTVStateHighlight(binding.tvHMState1);
        changeTVStateHighlight(binding.tvHState1);
        binding.tvLState.setOnClickListener(view -> resetTVState(binding.tvLState, binding.seekBarLo, TypeEQGain.LO));
        binding.tvLMState.setOnClickListener(view -> resetTVState(binding.tvLMState, binding.seekBarLoM, TypeEQGain.LOM));
        binding.tvHMState.setOnClickListener(view -> resetTVState(binding.tvHMState, binding.seekBarHiM, TypeEQGain.HIM));
        binding.tvHState.setOnClickListener(view -> resetTVState(binding.tvHState, binding.seekBarHi, TypeEQGain.HI));
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mTimer.cancel();
    }

    public void updateEQData(EQSetting eqSetting) {
        currentEQSetting = eqSetting;
        List<GainData> diffGains = eqSetting.getDiffGains();
        homeMusicFragment.updateReceivedData(diffGains.get(0).getGain(), diffGains.get(1).getGain(), diffGains.get(2).getGain(), diffGains.get(3).getGain());
        isDisableProgressChange = true;
        if (eqSetting.isCustomMode()) {
            binding.seekBarLo.setProgress((int) (diffGains.get(0).getGain() * 10));
            binding.seekBarLoM.setProgress((int) (diffGains.get(1).getGain() * 10));
            binding.seekBarHiM.setProgress((int) (diffGains.get(2).getGain() * 10));
            binding.seekBarHi.setProgress((int) (diffGains.get(3).getGain() * 10));
        }
        binding.tvAllReset.setEnabled(eqSetting.isCustomMode());
        updateGainLabels(eqSetting);
        enableProgressChangeHandler.removeCallbacks(enableProgressChangeRunnable);
        enableProgressChangeHandler.postDelayed(enableProgressChangeRunnable, 300);
        setOnClick(TypeSelected.getType(eqSetting.getCategoryName()));
        showToolTip();
    }

    private void showToolTip() {
        SharedPreferences sharedPreferences = getContext().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        boolean isShowToolTip = sharedPreferences.getBoolean(IS_SHOW_TOOL_TIP_KEY, true);
        if (isShowToolTip) {
            startShowToolTip();
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean(IS_SHOW_TOOL_TIP_KEY, false);
            editor.apply();
        }
    }

    public void updateFirmwareVersion(String version) {

        binding.tvFirmware.setText(getString(R.string.firmware_version, version));
        checkNewVersion();
    }

    private void checkNewVersion() {
        // TODO: handle later.
    }

    private void applyEQSetting(EQSetting eqSetting, TypeSelected typeEQModes) {
        switch (typeEQModes) {
            case CUSTOM1:
            case CUSTOM2:
                isChangeEQMode = true;
                break;
            default:
                isChangeEQMode = false;
                break;
        }
        LogUtils.LOG_UI("eqSetting Name ::" + eqSetting.getCategoryName());
        LayfictoneMainActivity mainActivity = (LayfictoneMainActivity) getBaseActivity();
        mainActivity.applyNewEQSetting(eqSetting);
    }

    private void updateGainLabels(EQSetting eqSetting) {
        if (eqSetting.isCustomMode()) {
            int index = 0;
            for (TextView tv : gainLabels) {
                float value = (float) seekBars.get(index).getProgress() / 10.0f;
                tv.setText(getGainLabel(value));
                index++;
                tv.setEnabled(true);
            }
            if (isChangeEQMode) {
                for (TextView tv : gainStates) {
                    tv.setText(getString(R.string.on));
                    tv.setEnabled(true);
                    tv.setActivated(true);
                }
                for (SeekBar sb : seekBars) {
                    if (isChangeEQMode) {
                        sb.setActivated(true);
                    }
                }
                for (TextView tv: gainStatesHigtlight) {
                    tv.setText(getString(R.string.on));
                    tv.setEnabled(true);
                    tv.setActivated(true);
                }
                isChangeEQMode = false;
            }
            for (SeekBar sb : seekBars) {
                changeSBState(sb);
            }
        } else {
            for (TextView tv : gainLabels) {
                tv.setText("--");
                tv.setEnabled(false);
            }
            for (TextView tv : gainStates) {
                tv.setText(getString(R.string.on));
                tv.setEnabled(false);
            }
            for (TextView tv: gainStatesHigtlight) {
                tv.setText(getString(R.string.on));
                tv.setEnabled(false);
            }
        }
    }

    private void setOnClick(TypeSelected typeSelected) {
        resetImage();
        switch (typeSelected) {
            case MUSIC:
                binding.tvModeMusic.setActivated(true);
                binding.tvModeMusic1.setActivated(true);
                binding.llEqControl.setVisibility(View.INVISIBLE);
                break;
            case WORK:
                binding.tvModeWork.setActivated(true);
                binding.tvModeWork1.setActivated(true);
                binding.llEqControl.setVisibility(View.INVISIBLE);
                break;
            case CHILL:
                binding.tvModeChill.setActivated(true);
                binding.tvModeChill1.setActivated(true);
                binding.llEqControl.setVisibility(View.INVISIBLE);
                break;
            case THEATER:
                binding.tvModeTheater.setActivated(true);
                binding.tvModeTheater1.setActivated(true);
                binding.llEqControl.setVisibility(View.INVISIBLE);
                break;
            case CUSTOM1:
                binding.tvModeCustom1.setActivated(true);
                binding.tvModeCustomA.setActivated(true);
                binding.llEqControl.setVisibility(View.VISIBLE);
                break;
            case CUSTOM2:
                binding.tvModeCustom2.setActivated(true);
                binding.tvModeCustomB.setActivated(true);
                binding.llEqControl.setVisibility(View.VISIBLE);
                break;
        }
    }

    private void resetImage() {
        binding.tvModeMusic.setActivated(false);
        binding.tvModeWork.setActivated(false);
        binding.tvModeChill.setActivated(false);
        binding.tvModeTheater.setActivated(false);
        binding.tvModeCustom1.setActivated(false);
        binding.tvModeCustom2.setActivated(false);

        binding.tvModeMusic1.setActivated(false);
        binding.tvModeWork1.setActivated(false);
        binding.tvModeChill1.setActivated(false);
        binding.tvModeTheater1.setActivated(false);
        binding.tvModeCustomA.setActivated(false);
        binding.tvModeCustomB.setActivated(false);
    }

    private void displayFragment() {
        FragmentManager fragmentManager = getChildFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.contentView, homeMusicFragment);
        fragmentTransaction.commit();
    }

    private void changeTVState(TextView tv, VerticalSeekBar vs) {
        tv.setActivated(!tv.isActivated());
        tv.setText(tv.isActivated() ? getString(R.string.on) : getString(R.string.off));
        vs.setActivated(!vs.isActivated());
        changeSBState(vs);
    }

    private void resetTVState(TextView tv, VerticalSeekBar vs, TypeEQGain typeEQGain) {
        boolean updateIfNeeded = false;
        if (tv.isActivated() && currentEQSetting != null && currentEQSetting.isCustomMode()) {
            LayfictoneMainActivity mainActivity = (LayfictoneMainActivity) getBaseActivity();
            ArrayList<GainData> baseEQSettingGains = EQSetting.getBaseEQSetting(mAirohaDevice.getDeviceName()).getGains();
            float valueLo = (float) binding.seekBarLo.getProgress() / 10f;
            float valueLoM = (float) binding.seekBarLoM.getProgress() /  10.0f;
            float valueHiM = (float) binding.seekBarHiM.getProgress() / 10.0f;
            float valueHi = (float) binding.seekBarHi.getProgress() / 10.0f;
            currentEQSetting.getGains().get(0).setGain(valueLo + baseEQSettingGains.get(0).getGain());
            currentEQSetting.getGains().get(1).setGain(valueLoM + baseEQSettingGains.get(1).getGain());
            currentEQSetting.getGains().get(2).setGain(valueHiM + baseEQSettingGains.get(2).getGain());
            currentEQSetting.getGains().get(3).setGain(valueHi + baseEQSettingGains.get(3).getGain());
            switch (typeEQGain) {
                case LO:
                    currentEQSetting.getGains().get(0).setGain(baseEQSettingGains.get(0).getGain());
                    updateIfNeeded = binding.seekBarLo.getProgress() == 0 ? false : true;
                    break;
                case LOM:
                    currentEQSetting.getGains().get(1).setGain(baseEQSettingGains.get(1).getGain());
                    updateIfNeeded = binding.seekBarLoM.getProgress() == 0 ? false : true;
                    break;
                case HIM:
                    currentEQSetting.getGains().get(2).setGain(baseEQSettingGains.get(2).getGain());
                    updateIfNeeded = binding.seekBarHiM.getProgress() == 0 ? false : true;
                    break;
                case HI:
                    currentEQSetting.getGains().get(3).setGain(baseEQSettingGains.get(3).getGain());
                    updateIfNeeded = binding.seekBarHi.getProgress() == 0 ? false : true;
                    break;
            }
            if (updateIfNeeded) {
                mainActivity.applyNewEQSetting(currentEQSetting);
            }
        }
        tv.setActivated(!tv.isActivated());
        tv.setText(tv.isActivated() ? getString(R.string.on) : getString(R.string.off));
        vs.setActivated(!vs.isActivated());
        changeSBState(vs);
    }

    private void changeTVStateHighlight(TextView tv) {
        tv.setActivated(!tv.isActivated());
        tv.setText(tv.isActivated() ? getString(R.string.on) : getString(R.string.off));
    }

    public void changeSBState(SeekBar sb) {
        Drawable drawable1 = getResources().getDrawable(R.drawable.thumb);
        Drawable drawable2 = getResources().getDrawable(R.drawable.thumb_transparent);
        sb.setThumb(sb.isActivated() ? drawable1 : drawable2);
    }

    private void updateFillViewHeight(View view, SeekBar seekBar) {
        ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
        float valueLo = ((float)seekBar.getProgress() / 10f);
        int height = seekBar.getWidth() - seekBar.getPaddingLeft() * 2;
        layoutParams.height = (int) (1f * Math.abs(valueLo) / 20 * height);
        if (seekBar.getProgress() < 0) {
            layoutParams.bottomMargin = layoutParams.height / 2;
            layoutParams.topMargin = 0;
        } else if (seekBar.getProgress() > 0) {
            layoutParams.topMargin = layoutParams.height / 2;
            layoutParams.bottomMargin = 0;
        } else {
            layoutParams.height = 1;
        }
        view.setActivated(seekBar.getProgress() < 0);
        view.setEnabled(layoutParams.height > 1);
        view.requestLayout();
    }

    private class OnSeekBarChangeListener implements SeekBar.OnSeekBarChangeListener {
        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
            float valueLo = ((float)binding.seekBarLo.getProgress() / 10f);
            float valueLoM = ((float)binding.seekBarLoM.getProgress() / 10f);
            float valueHiM = ((float)binding.seekBarHiM.getProgress() / 10f);
            float valueHi = ((float)binding.seekBarHi.getProgress() / 10f);
            homeMusicFragment.updateReceivedData(
                    valueLo,
                    valueLoM,
                    valueHiM,
                    valueHi);
            updateFillViewHeight(binding.fillLo, binding.seekBarLo);
            updateFillViewHeight(binding.fillLoM, binding.seekBarLoM);
            updateFillViewHeight(binding.fillHiM, binding.seekBarHiM);
            updateFillViewHeight(binding.fillHi, binding.seekBarHi);
            updateGainLabels(currentEQSetting);
            if (isDisableProgressChange) {
                return;
            }
//            updateCustomGainsHandler.removeCallbacks(updateCustomGainsRunnable);
//            updateCustomGainsHandler.postDelayed(updateCustomGainsRunnable, 500);
        }
    }

    private String getGainLabel(float value) {
        if (value > 0) {
            return "+" + value;
        } else if (value < 0) {
            return String.valueOf(value);
        } else {
            return "±0";
        }
    }

    private class ImageSliderAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return deviceImages.size();
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
            return view == object;
        }

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {
            ItemHomeSliderBinding binding = ItemHomeSliderBinding.inflate(getLayoutInflater(), container, false);
            binding.imageView.setImageResource(deviceImages.get(position));
            container.addView(binding.getRoot());
            return binding.getRoot();
        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            container.removeView((ImageView) object);
        }
    }

    private void initImageSlider() {
        deviceImages.clear();
        deviceImages.addAll(BluetoothDeviceUtil.getDeviceSliderImages(mAirohaDevice));
        binding.viewPager.setAdapter(new ImageSliderAdapter());
        binding.viewPager.getAdapter().notifyDataSetChanged();
        binding.tabLayout.setupWithViewPager(binding.viewPager);
        for (int i = 0; i < deviceImages.size(); i++) {
            TabLayout.Tab tab = binding.tabLayout.getTabAt(i);
            tab.setCustomView(R.layout.tab_item_home_slider);
        }
        binding.viewPager.setCurrentItem(0);
    }

    private void setToolTip(View view, String titleString, Tooltip.Gravity gravity) {
        Tooltip.make(getContext(), new Tooltip.Builder(101)
                .anchor(view, gravity)
                .closePolicy(new Tooltip.ClosePolicy()
                        .insidePolicy(true, false)
                        .outsidePolicy(true, false), delayToolTip)
                .activateDelay(800)
                .showDelay(delayToolTip / 10)
                .text(titleString)
                .maxWidth(1000)
                .withArrow(true)
                .withOverlay(true)
                .floatingAnimation(Tooltip.AnimationBuilder.DEFAULT)
                .withStyleId(R.style.ToolTipLayoutCustomStyle)
                .withOverlay(false)
                .build()).show();
    }

    public void startShowToolTip() {
        binding.backgroundOpacityView.setVisibility(View.VISIBLE);
        binding.transparentView.setVisibility(View.VISIBLE);
        binding.updateContainer.setBackgroundColor(Color.parseColor("#AEAEAE"));
        binding.updateBtn.setCardBackgroundColor(Color.parseColor("#AEAEAE"));
        binding.tvAllReset.setBackgroundResource(R.drawable.bq_eq_all_reset_none);
        hiddenHighlightTextView(View.VISIBLE);
        binding.scrollView.fullScroll(View.FOCUS_UP);
        visibleButtonState(View.GONE, View.VISIBLE);
        disableChartWhenShowTooltip(R.drawable.thumb_highlight, true);
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                hiddenHighlightTextView(View.VISIBLE);
                switch (currentIndex) {
                    case 1:
                        setToolTip(binding.tabLayout, getString(R.string.tooltip_title1), Tooltip.Gravity.BOTTOM);
                        currentIndex = currentIndex + 1;
                        handler.postDelayed(this, delayToolTip);
                        break;
                    case 2:
                        setToolTip(binding.EQModeTitle, getString(R.string.tooltip_title2), Tooltip.Gravity.TOP);
                        binding.tvModeMusic1.setVisibility(View.GONE);
                        binding.tvModeChill1.setVisibility(View.GONE);
                        binding.tvModeTheater1.setVisibility(View.GONE);
                        binding.tvModeWork1.setVisibility(View.GONE);
                        binding.tvModeCustomA.setVisibility(View.VISIBLE);
                        binding.tvModeCustomB.setVisibility(View.VISIBLE);
                        currentIndex = currentIndex + 1;
                        handler.postDelayed(this, delayToolTip);
                        break;
                    case 3:
                        setToolTip(binding.containerEQMode, getString(R.string.tooltip_title3), Tooltip.Gravity.BOTTOM);
                        binding.tvModeMusic1.setVisibility(View.VISIBLE);
                        binding.tvModeChill1.setVisibility(View.VISIBLE);
                        binding.tvModeTheater1.setVisibility(View.VISIBLE);
                        binding.tvModeWork1.setVisibility(View.VISIBLE);
                        binding.tvModeCustomA.setVisibility(View.GONE);
                        binding.tvModeCustomB.setVisibility(View.GONE);
                        currentIndex = currentIndex + 1;
                        handler.postDelayed(this, delayToolTip);
                        break;
                    case 4:
                        binding.scrollView.fullScroll(View.FOCUS_DOWN);
                        currentIndex = currentIndex + 1;
                        handler.postDelayed(this, 400);
                        break;
                    case 5:
                        setToolTip(binding.switchButton, getString(R.string.tooltip_title4), Tooltip.Gravity.TOP);
                        currentIndex = currentIndex + 1;
                        handler.postDelayed(this, delayToolTip);
                        break;
                    case 6:
                        binding.scrollView.fullScroll(View.FOCUS_UP);
                        binding.updateContainer.setBackgroundColor(Color.parseColor("#EDEDED"));
                        binding.updateBtn.setCardBackgroundColor(Color.WHITE);
                        binding.backgroundOpacityView.setVisibility(View.GONE);
                        binding.transparentView.setVisibility(View.GONE);
                        binding.tvAllReset.setBackgroundResource(R.drawable.bg_eq_all_reset);
                        disableChartWhenShowTooltip(R.drawable.thumb, false);
                        hiddenHighlightTextView(View.GONE);
                        visibleButtonState(View.VISIBLE, View.GONE);
                        handler.removeCallbacks(this);
                        currentIndex = 1;
                        break;
                }
            }
        };
        handler.postDelayed(runnable, 300);
    }

    private void visibleButtonState(int stateNormal, int stateHighlight) {
        binding.tvLState.setVisibility(stateNormal);
        binding.tvLMState.setVisibility(stateNormal);
        binding.tvHMState.setVisibility(stateNormal);
        binding.tvHState.setVisibility(stateNormal);
        binding.tvLState1.setVisibility(stateHighlight);
        binding.tvLMState1.setVisibility(stateHighlight);
        binding.tvHMState1.setVisibility(stateHighlight);
        binding.tvHState1.setVisibility(stateHighlight);
    }

    private void hiddenHighlightTextView(int visible) {
        binding.tvModeMusic1.setVisibility(visible);
        binding.tvModeChill1.setVisibility(visible);
        binding.tvModeTheater1.setVisibility(visible);
        binding.tvModeWork1.setVisibility(visible);
        binding.tvModeCustomA.setVisibility(visible);
        binding.tvModeCustomB.setVisibility(visible);
    }

    private void disableChartWhenShowTooltip(int drawableId, boolean selected) {
        binding.seekBarHi.setThumb(ContextCompat.getDrawable(getContext(), drawableId));
        binding.seekBarHiM.setThumb(ContextCompat.getDrawable(getContext(), drawableId));
        binding.seekBarLo.setThumb(ContextCompat.getDrawable(getContext(), drawableId));
        binding.seekBarLoM.setThumb(ContextCompat.getDrawable(getContext(), drawableId));
        binding.fillHiM.setSelected(selected);
        binding.fillHiM.setSelected(selected);
        binding.fillLo.setSelected(selected);
        binding.fillLoM.setSelected(selected);
    }
}
