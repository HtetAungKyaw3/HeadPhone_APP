package com.airoha.utapp.sdk.model;

import com.airoha.sdk.api.message.AirohaEQPayload;
import com.airoha.utapp.sdk.constant.EQFreq;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public enum EQSetting {
    D01v_GMU_MUSIC("AVIOT TE-D01v-GMU", 1, 1, "MUSIC", Arrays.asList(
            new GainData(EQFreq.LOW, 3, 0.8f),
            new GainData(EQFreq.LOW_M, 5, 1.6f),
            new GainData(EQFreq.HIGH_M, 5, 2.2f),
            new GainData(EQFreq.HIGH, 3, 1.4f)
    ), false),
    D01v_GMU_WORK("AVIOT TE-D01v-GMU", 2, 2, "WORK", Arrays.asList(
            new GainData(EQFreq.LOW, 2, 1.4f),
            new GainData(EQFreq.LOW_M, 6, 1.4f),
            new GainData(EQFreq.HIGH_M, 7, 1.4f),
            new GainData(EQFreq.HIGH, 5, 1.4f)
    ), false),
    USE_GMU_CHILL("AVIOT TE-D01v-GMU", 3, 3, "CHILL", Arrays.asList(
            new GainData(EQFreq.LOW, -1, 0.8f),
            new GainData(EQFreq.LOW_M, 3, 1.2f),
            new GainData(EQFreq.HIGH_M, 3, 1.8f),
            new GainData(EQFreq.HIGH, 9, 2.2f)
    ), false),
    D01v_GMU_CHILL("AVIOT TE-D01v-GMU", 101, 3, "CHILL", Arrays.asList(
            new GainData(EQFreq.LOW, -1, 0.8f),
            new GainData(EQFreq.LOW_M, 3, 1.2f),
            new GainData(EQFreq.HIGH_M, 3, 1.8f),
            new GainData(EQFreq.HIGH, 9, 2.2f)
    ), false),
    D01v_GMU_THEATER("AVIOT TE-D01v-GMU", 102, 4, "THEATER", Arrays.asList(
            new GainData(EQFreq.LOW, -6, 0.8f),
            new GainData(EQFreq.LOW_M, 8, 1.2f),
            new GainData(EQFreq.HIGH_M, 11, 1.8f),
            new GainData(EQFreq.HIGH, 2, 2.2f)
    ), false),
    D01v_GMU_CUSTOM1("AVIOT TE-D01v-GMU", 103, 5, "CUSTOM1", Arrays.asList(
            new GainData(EQFreq.LOW, 3, 0.8f),
            new GainData(EQFreq.LOW_M, 5, 1.2f),
            new GainData(EQFreq.HIGH_M, 5, 1.8f),
            new GainData(EQFreq.HIGH, 3, 2.2f)
    ), true),
    D01v_GMU_CUSTOM2("AVIOT TE-D01v-GMU", 104, 6, "CUSTOM2", Arrays.asList(
            new GainData(EQFreq.LOW, 3, 0.8f),
            new GainData(EQFreq.LOW_M, 5, 1.2f),
            new GainData(EQFreq.HIGH_M, 5, 1.8f),
            new GainData(EQFreq.HIGH, 3, 2.2f)
    ), true);

    private String deviceName;
    private int categoryId;
    private int index;
    private String categoryName;
    private ArrayList<GainData> gains;
    private boolean isCustom;

    EQSetting(String deviceName, int categoryId, int index, String categoryName, List<GainData> gains, boolean isCustom) {
        this.deviceName = deviceName;
        this.categoryId = categoryId;
        this.index = index;
        this.categoryName = categoryName;
        this.gains = new ArrayList<>();
        this.gains.addAll(gains);
        this.isCustom = isCustom;
    }

    public static EQSetting getEQSetting(String deviceName, int categoryId) {
        for (EQSetting eqSetting : EQSetting.values()) {
//            if (eqSetting.getDeviceName().equals(deviceName) && eqSetting.getCategoryId() == categoryId) {
//                return eqSetting;
//            }
            // TODO: update and use above code later.
            if (eqSetting.getCategoryId() == categoryId) {

                return eqSetting;
            }
        }
        return null;
    }

    public static EQSetting getBaseEQSetting(String deviceName) {
        for (EQSetting eqSetting : EQSetting.values()) {
//            if (eqSetting.getDeviceName().equals(deviceName) && eqSetting.getIndex() == 1) {
//                return eqSetting;
//            }
            // TODO: update and use above code later.
            if (eqSetting.getIndex() == 1) {
                return eqSetting;
            }
        }
        return null;
    }

    public static EQSetting getCustom1EQSetting(String deviceName) {
        for (EQSetting eqSetting : EQSetting.values()) {
            if (eqSetting.getDeviceName().equals(deviceName) && eqSetting.name().contains("CUSTOM1")) {
                return eqSetting;
            }
        }
        return null;
    }

    public static EQSetting getCustom2EQSetting(String deviceName) {
        for (EQSetting eqSetting : EQSetting.values()) {
            if (eqSetting.getDeviceName().equals(deviceName) && eqSetting.name().contains("CUSTOM2")) {
                return eqSetting;
            }
        }
        return null;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public ArrayList<GainData> getGains() {
        return gains;
    }

    public List<GainData> getDiffGains() {
        List<GainData> diffGain = new ArrayList<>();
        EQSetting baseEQSetting = getBaseEQSetting(getDeviceName());
        for (int i = 0; i < Math.min(getGains().size(), baseEQSetting.getGains().size()); i++) {
            diffGain.add(new GainData(getGains().get(i).getFreq(),
                    getGains().get(i).getGain() - baseEQSetting.getGains().get(i).getGain(),
                    getGains().get(i).getQValue()));
        }
        return diffGain;
    }

    public boolean isCustomMode() {
        return isCustom;
    }

    public int getIndex() {
        return index;
    }

    public AirohaEQPayload getEQPayload() {
        if (getCategoryId() < 100) {
            return null;
        }
        AirohaEQPayload payload = new AirohaEQPayload();
        payload.setAllSampleRates(new int[]{44100, 48000});
        payload.setBandCount(getGains().size());
        payload.setIndex(getIndex());
        LinkedList<AirohaEQPayload.EQIDParam> params = new LinkedList<>();
        payload.setLeftGain(0);
        for (GainData gain : getGains()) {
            AirohaEQPayload.EQIDParam param = new AirohaEQPayload.EQIDParam();
            param.setBandType(2);
            param.setFrequency(gain.getFreq());
            param.setGainValue(gain.getGain());
            param.setQValue(gain.getQValue());
            params.add(param);
        }
        payload.setIirParams(params);
        return payload;
    }

    public static boolean isIrParamsValid(LinkedList<AirohaEQPayload.EQIDParam> params) {
        if (params.size() == 4) {
            return params.get(0).getFrequency() == EQFreq.LOW &&
                    params.get(1).getFrequency() == EQFreq.LOW_M &&
                    params.get(2).getFrequency() == EQFreq.HIGH_M &&
                    params.get(3).getFrequency() == EQFreq.HIGH;
        }
        return false;
    }
}
