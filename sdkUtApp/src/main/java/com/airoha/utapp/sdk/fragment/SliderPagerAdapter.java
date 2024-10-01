package com.airoha.utapp.sdk.fragment;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.ArrayList;

public class SliderPagerAdapter extends FragmentPagerAdapter {

    public static ArrayList<Fragment> fragArr = new ArrayList<>();

    public SliderPagerAdapter(@NonNull FragmentManager fm) {
        super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        return fragArr.get(position);
    }

    @Override
    public int getCount() {
        return fragArr.size();
    }
}