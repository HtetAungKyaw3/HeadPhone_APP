package com.airoha.utapp.sdk;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import com.airoha.liblogger.AirohaLogger;
import com.airoha.utapp.sdk.model.IDumpFlow;

public class ProgressDialogUtil {
    private AlertDialog mAlertDialog;
    private View mLoadView;
    private TextView mTvTip;

    /**
     * 彈出耗時對話方塊
     * @param context
     */
    public void showProgressDialog(Context context) {
        if (mAlertDialog == null) {
            mAlertDialog = new AlertDialog.Builder(context, R.style.CustomProgressDialog).create();
        }

        mLoadView = LayoutInflater.from(context).inflate(R.layout.custom_progress_dialog_view, null);
        mAlertDialog.setView(mLoadView, 0, 0, 0, 0);
        mAlertDialog.setCanceledOnTouchOutside(false);

        mTvTip = mLoadView.findViewById(R.id.tvTip);
        mTvTip.setText("Loading...");

        mAlertDialog.show();
    }

    public void setBackKeyListener(final IDumpFlow flow) {
        mAlertDialog.setOnKeyListener(new Dialog.OnKeyListener() {

            @Override
            public boolean onKey(DialogInterface arg0, int keyCode,
                                 KeyEvent event) {
                // TODO Auto-generated method stub
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    flow.cancelDump();
                    dismiss();
                }
                return true;
            }
        });
    }

    public void showProgressDialog(Context context, String tip) {
        if (TextUtils.isEmpty(tip)) {
            tip = "Loading...";
        }

        if (mAlertDialog == null) {
            mAlertDialog = new AlertDialog.Builder(context, R.style.CustomProgressDialog).create();
        }

        mLoadView = LayoutInflater.from(context).inflate(R.layout.custom_progress_dialog_view, null);
        mAlertDialog.setView(mLoadView, 0, 0, 0, 0);
        mAlertDialog.setCanceledOnTouchOutside(false);

        mTvTip = mLoadView.findViewById(R.id.tvTip);
        mTvTip.setText(tip);

        mAlertDialog.show();
    }

    public void updateProgressMsg(String tip) {
        mTvTip.setText(tip);
    }

    /**
     * 隱藏耗時對話方塊
     */
    public void dismiss() {
        if (mAlertDialog != null && mAlertDialog.isShowing()) {
            mAlertDialog.dismiss();

            AirohaLogger gLogger = AirohaLogger.getInstance();
            gLogger.d("ProgressDialogUtil", "dismiss");
        }
    }

    public boolean isShowing() {
        if (mAlertDialog != null) {
            return mAlertDialog.isShowing();
        }
        return false;
    }
}
