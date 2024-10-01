package com.airoha.utapp.sdk.ui.home.fwupdate;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.airoha.utapp.sdk.databinding.FragmentUpdateResultPopupBinding;
import com.airoha.utapp.sdk.ui.base.BaseFWDialogFragment;

public class UpdateResultPopUpFragment extends BaseFWDialogFragment<FragmentUpdateResultPopupBinding> {

    public static UpdateResultPopUpFragment getNewInstance(UpdatingPopUpFragment.UpdateResult result) {
        UpdateResultPopUpFragment instance = new UpdateResultPopUpFragment();
        // TODO put result to arguments - SUCESS true FAIL false
        return instance;
    }

    @Override
    protected void initBinding(LayoutInflater inflater, @Nullable ViewGroup container) {
        mBinding = FragmentUpdateResultPopupBinding.inflate(inflater, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mBinding.tvOK.setOnClickListener(view1 -> dismiss());
    }
}
