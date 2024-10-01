package com.airoha.utapp.sdk.ui.base;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.viewbinding.ViewBinding;

import com.airoha.utapp.sdk.ui.home.fwupdate.UpdatingPopUpFragment;

public abstract class BaseFWDialogFragment<T extends ViewBinding> extends DialogFragment {

    protected T mBinding;
    protected FWDialogListener mListener;

    @Override
    public void onStart() {
        super.onStart();
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        Dialog dialog = getDialog();
        if (dialog != null) {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.MATCH_PARENT;
            dialog.getWindow().setLayout(width, height);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        initBinding(inflater, container);
        return mBinding.getRoot();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        setCancelable(false);
        return dialog;
    }

    protected abstract void initBinding(LayoutInflater inflater, @Nullable ViewGroup container);

    public void setListener(FWDialogListener listener) {
        this.mListener = listener;
    }

    public interface FWDialogListener {
        void openUpdateDialog();

        void openInstallingDialog();

        void openInstallResultDialog(UpdatingPopUpFragment.UpdateResult result);
    }
}
