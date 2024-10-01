package com.airoha.utapp.sdk.ui.onboarding;

import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.airoha.utapp.sdk.R;
import com.airoha.utapp.sdk.databinding.ActivityOnboardingBinding;
import com.airoha.utapp.sdk.ui.base.BaseActivity;
import com.airoha.utapp.sdk.ui.main.LayfictoneMainActivity;
import com.airoha.utapp.sdk.ui.splash.SplashActivity;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

public class OnBoardingActivity extends BaseActivity<ActivityOnboardingBinding> {

    private PagerAdaper mAdapter;

    @Override
    protected void initBinding() {
        binding = ActivityOnboardingBinding.inflate(getLayoutInflater());
    }

    @Override
    protected boolean isStatusBarLightMode() {
        return false;
    }

    @Override
    protected void initView() {
        super.initView();
        initViewPager();

        binding.tvNext.setOnClickListener(view -> {
            int currentIndex = binding.viewPager.getCurrentItem();
            if (currentIndex < mAdapter.getCount() - 1) {
                binding.viewPager.setCurrentItem(currentIndex + 1);
            } else {
                getShared().edit().putBoolean(SplashActivity.SHARED_KEY_ONBOARDING, true).apply();
                startActivity(new Intent(this, LayfictoneMainActivity.class));
                finish();
            }
        });
    }

    private void initViewPager() {
        List<OnBoardingFragment> fragments = new ArrayList<>();
        fragments.add(OnBoardingFragment.getNewInstance(R.drawable.slider_image1, R.string.onboarding_title_1, R.string.onboarding_des_1));
        fragments.add(OnBoardingFragment.getNewInstance(R.drawable.slider_image2, R.string.onboarding_title_2, R.string.onboarding_des_2));
        fragments.add(OnBoardingFragment.getNewInstance(R.drawable.slider_image3, R.string.onboarding_title_3, R.string.onboarding_des_3));
        mAdapter = new PagerAdaper(getSupportFragmentManager(), fragments);

        binding.viewPager.setAdapter(mAdapter);
        binding.tabLayout.setupWithViewPager(binding.viewPager);
        for (int i = 0; i < fragments.size(); i++) {
            TabLayout.Tab tab = binding.tabLayout.getTabAt(i);
            tab.setCustomView(R.layout.tab_item_onboarding);
        }
        binding.viewPager.setCurrentItem(0);
        binding.tvNext.setText(mAdapter.getPageTitle(0));

        binding.viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                binding.tvNext.setText(mAdapter.getPageTitle(position));
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    public class PagerAdaper extends FragmentPagerAdapter {

        private List<OnBoardingFragment> fragments;

        public PagerAdaper(@NonNull FragmentManager fm, List<OnBoardingFragment> fragments) {
            super(fm);
            this.fragments = fragments;
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return position == fragments.size() - 1 ? getString(R.string.start) : getString(R.string.next);
        }
    }
}
