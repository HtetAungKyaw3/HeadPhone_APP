package com.airoha.utapp.sdk.model;

public class GainData {
    private float freq;
    private float gain;
    private float qValue;

    public GainData(float freg, float gain, float qValue) {
        this.freq = freg;
        this.gain = gain;
        this.qValue = qValue;
    }

    public float getFreq() {
        return freq;
    }

    public void setFreq(float freq) {
        this.freq = freq;
    }

    public float getGain() {
        return gain;
    }

    public void setGain(float gain) {
        this.gain = gain;
    }

    public float getQValue() {
        return qValue;
    }

    public void setQValue(float qValue) {
        this.qValue = qValue;
    }
}
