package com.airoha.utapp.sdk.ui.home.fwupdate;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.airoha.utapp.sdk.databinding.FragmentUpdatePopupBinding;
import com.airoha.utapp.sdk.ui.base.BaseFWDialogFragment;

public class UpdatePopUpFragment extends BaseFWDialogFragment<FragmentUpdatePopupBinding> {

    @Override
    protected void initBinding(LayoutInflater inflater, @Nullable ViewGroup container) {
        mBinding = FragmentUpdatePopupBinding.inflate(inflater, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mBinding.tvCancel.setOnClickListener(view1 -> dismiss());
        mBinding.tvUpdateNow.setOnClickListener(view12 -> {
            mListener.openInstallingDialog();
            new Handler().postDelayed(() -> dismiss(), 150);
        });
    }
}
