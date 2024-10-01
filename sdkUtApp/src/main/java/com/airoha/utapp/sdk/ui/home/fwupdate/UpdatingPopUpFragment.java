package com.airoha.utapp.sdk.ui.home.fwupdate;

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.airoha.utapp.sdk.databinding.FragmentUpdatingPopupBinding;
import com.airoha.utapp.sdk.ui.base.BaseFWDialogFragment;


public class UpdatingPopUpFragment extends BaseFWDialogFragment<FragmentUpdatingPopupBinding> {

    private int currentProgress;
    private CountDownTimer countDownTimer;

    @Override
    protected void initBinding(LayoutInflater inflater, @Nullable ViewGroup container) {
        mBinding = FragmentUpdatingPopupBinding.inflate(inflater, container, false);
        mBinding.tvCancel.setOnClickListener(view1 -> dismiss());
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        startCountdown();
    }

    private void startCountdown() {
        // Thời gian đếm ngược là 10 giây (10000 milliseconds)
        long millisInFuture = 10000;
        // Khoảng thời gian giữa các tick là 1000 milliseconds (1 giây)
        long countDownInterval = 1000;

        countDownTimer = new CountDownTimer(millisInFuture, countDownInterval) {
            @Override
            public void onTick(long millisUntilFinished) {
                currentProgress += 10;
                mBinding.updateProgressBar.setProgress(currentProgress);
            }

            @Override
            public void onFinish() {
                mListener.openInstallResultDialog(UpdateResult.SUCCESS);
                new Handler().postDelayed(() -> dismiss(), 150);
            }
        }.start();
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
        countDownTimer.cancel();
    }

    public enum UpdateResult {
        SUCCESS, FAIL
    }
}
