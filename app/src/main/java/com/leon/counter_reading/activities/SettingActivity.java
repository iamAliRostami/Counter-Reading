package com.leon.counter_reading.activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Color;
import android.os.Debug;
import android.view.View;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.leon.counter_reading.R;
import com.leon.counter_reading.adapters.ViewPagerAdapterTab;
import com.leon.counter_reading.base_items.BaseActivity;
import com.leon.counter_reading.databinding.ActivitySettingBinding;
import com.leon.counter_reading.enums.SharedReferenceNames;
import com.leon.counter_reading.fragments.SettingChangePasswordFragment;
import com.leon.counter_reading.fragments.SettingChangeThemeFragment;
import com.leon.counter_reading.fragments.SettingUpdateFragment;
import com.leon.counter_reading.infrastructure.ISharedPreferenceManager;
import com.leon.counter_reading.utils.DepthPageTransformer;
import com.leon.counter_reading.utils.SharedPreferenceManager;

public class SettingActivity extends BaseActivity {
    ActivitySettingBinding binding;
    ISharedPreferenceManager sharedPreferenceManager;
    Activity activity;
    private int previousState, currentState;

    @Override
    protected void initialize() {
        sharedPreferenceManager = new SharedPreferenceManager(getApplicationContext(),
                SharedReferenceNames.ACCOUNT.getValue());
        binding = ActivitySettingBinding.inflate(getLayoutInflater());
        View childLayout = binding.getRoot();
        ConstraintLayout parentLayout = findViewById(R.id.base_Content);
        parentLayout.addView(childLayout);
        activity = this;
        setupViewPager();
        initializeTextViews();
    }

    void initializeTextViews() {
        textViewChangeTheme();
        textViewChangePassword();
        textViewUpdate();
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    void textViewChangeTheme() {
        binding.textViewChangeTheme.setOnClickListener(view -> {
            setColor();
            binding.textViewChangeTheme.setBackground(
                    ContextCompat.getDrawable(activity, R.drawable.border_white_2));
            setPadding();
            binding.viewPager.setCurrentItem(0);
        });
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    void textViewChangePassword() {
        binding.textViewChangePassword.setOnClickListener(view -> {
            setColor();
            binding.textViewChangePassword.setBackground(
                    ContextCompat.getDrawable(activity, R.drawable.border_white_2));
            setPadding();
            binding.viewPager.setCurrentItem(1);
        });
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    void textViewUpdate() {
        binding.textViewUpdate.setOnClickListener(view -> {
            setColor();
            binding.textViewUpdate.setBackground(
                    ContextCompat.getDrawable(activity, R.drawable.border_white_2));
            setPadding();
            binding.viewPager.setCurrentItem(2);
        });
    }

    private void setColor() {
        binding.textViewUpdate.setBackgroundColor(Color.TRANSPARENT);
        binding.textViewUpdate.setTextColor(
                ContextCompat.getColor(activity, R.color.text_color_light));
        binding.textViewChangeTheme.setBackgroundColor(Color.TRANSPARENT);
        binding.textViewChangeTheme.setTextColor(
                ContextCompat.getColor(activity, R.color.text_color_light));
        binding.textViewChangePassword.setBackgroundColor(Color.TRANSPARENT);
        binding.textViewChangePassword.setTextColor(
                ContextCompat.getColor(activity, R.color.text_color_light));
    }

    private void setPadding() {
        binding.textViewChangeTheme.setPadding(0,
                (int) getResources().getDimension(R.dimen.medium_dp), 0,
                (int) getResources().getDimension(R.dimen.medium_dp));
        binding.textViewUpdate.setPadding(0,
                (int) getResources().getDimension(R.dimen.medium_dp), 0,
                (int) getResources().getDimension(R.dimen.medium_dp));
        binding.textViewChangePassword.setPadding(0,
                (int) getResources().getDimension(R.dimen.medium_dp), 0,
                (int) getResources().getDimension(R.dimen.medium_dp));
    }

    private void setupViewPager() {
        ViewPagerAdapterTab adapter = new ViewPagerAdapterTab(getSupportFragmentManager(),
                FragmentStatePagerAdapter.POSITION_NONE);
        adapter.addFragment(new SettingChangeThemeFragment(), "تغییر پوسته");
        adapter.addFragment(new SettingChangePasswordFragment(), "تغییر گذرواژه");
        adapter.addFragment(new SettingUpdateFragment(), "به روز رسانی");
        binding.viewPager.setAdapter(adapter);
        binding.viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                if (position == 0) {
                    binding.textViewChangeTheme.callOnClick();
                } else if (position == 1) {
                    binding.textViewChangePassword.callOnClick();
                } else if (position == 2) {
                    binding.textViewUpdate.callOnClick();
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                int currentPage = binding.viewPager.getCurrentItem();
                if (currentPage == 2 || currentPage == 0) {
                    previousState = currentState;
                    currentState = state;
                    if (previousState == 1 && currentState == 0) {
                        binding.viewPager.setCurrentItem(currentPage == 0 ? 2 : 0);
                    }
                }
            }
        });
        binding.viewPager.setPageTransformer(true, new DepthPageTransformer());
    }

    @Override
    protected void onStop() {
        super.onStop();
        Runtime.getRuntime().totalMemory();
        Runtime.getRuntime().freeMemory();
        Runtime.getRuntime().maxMemory();
        Debug.getNativeHeapAllocatedSize();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Runtime.getRuntime().totalMemory();
        Runtime.getRuntime().freeMemory();
        Runtime.getRuntime().maxMemory();
        Debug.getNativeHeapAllocatedSize();
    }
}