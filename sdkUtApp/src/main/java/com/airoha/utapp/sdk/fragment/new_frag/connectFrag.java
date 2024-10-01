/*
 * Created by dotrinh on 5/17/23, 5:50 PM
 * Copyright (c) 2023. USE Inc. All rights reserved.
 */

package com.airoha.utapp.sdk.fragment.new_frag;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.airoha.utapp.sdk.BaseFragment;
import com.airoha.utapp.sdk.MainActivity;
import com.airoha.utapp.sdk.MenuFragment;
import com.airoha.utapp.sdk.R;
import com.airoha.utapp.sdk.databinding.ConnectFragBinding;

public class connectFrag extends BaseFragment {
    ImageView connect;

    private String TAG = MenuFragment.class.getSimpleName();

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mActivity = (MainActivity) context;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup fragment, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.connect_frag, fragment, false);
        connect = view.findViewById(R.id.imageView17);
        connect.setOnClickListener(view1 -> {
//            changeFragment(MainActivity.FragmentIndex.CONNECT_DEVICES);
        });
        return view;
    }

    private void changeFragment(MainActivity.FragmentIndex index) {
        String previous_addr = (mActivity.getAirohaService().getFragmentByIndex(index)).getTargetAddr();
        if (previous_addr != null) {
            if (!previous_addr.equalsIgnoreCase(gTargetAddr)) {
                mActivity.getAirohaService().createFragmentByIndex(index);
            }
        }
        mActivity.changeFragment(index);
    }
}
