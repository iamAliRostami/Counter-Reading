package com.leon.counter_reading.activities;

import android.app.Activity;
import android.graphics.Color;
import android.os.Debug;
import android.view.View;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;

import com.leon.counter_reading.R;
import com.leon.counter_reading.adapters.ViewPagerAdapterTab;
import com.leon.counter_reading.base_items.BaseActivity;
import com.leon.counter_reading.databinding.ActivitySettingBinding;
import com.leon.counter_reading.fragments.SettingChangeAvatarFragment;
import com.leon.counter_reading.fragments.SettingChangePasswordFragment;
import com.leon.counter_reading.fragments.SettingChangeThemeFragment;
import com.leon.counter_reading.fragments.SettingUpdateFragment;
import com.leon.counter_reading.utils.DepthPageTransformer;
import com.leon.counter_reading.utils.DifferentCompanyManager;

public class SettingActivity extends BaseActivity {
    private ActivitySettingBinding binding;
    private Activity activity;
    private int previousState, currentState;

    @Override
    protected void initialize() {
        binding = ActivitySettingBinding.inflate(getLayoutInflater());
        View childLayout = binding.getRoot();
        ConstraintLayout parentLayout = findViewById(R.id.base_Content);
        parentLayout.addView(childLayout);
        activity = this;
        setupViewPager();
        initializeTextViews();
    }

    void initializeTextViews() {
        TextView textViewCompanyName = findViewById(R.id.text_view_company_name);
        textViewCompanyName.setText(DifferentCompanyManager.getCompanyName(DifferentCompanyManager.getActiveCompanyName()));

        textViewChangeTheme();
        textViewChangePassword();
        textViewUpdate();
        textViewAvatar();
    }

    void textViewChangeTheme() {
        binding.textViewChangeTheme.setOnClickListener(view -> {
            setColor();
            binding.textViewChangeTheme.setBackground(
                    ContextCompat.getDrawable(activity, R.drawable.border_white_2));
            setPadding();
            binding.viewPager.setCurrentItem(0);
        });
    }

    void textViewChangePassword() {
        binding.textViewChangePassword.setOnClickListener(view -> {
            setColor();
            binding.textViewChangePassword.
                    setBackground(ContextCompat.getDrawable(activity, R.drawable.border_white_2));
            setPadding();
            binding.viewPager.setCurrentItem(1);
        });
    }

    void textViewUpdate() {
        binding.textViewUpdate.setOnClickListener(view -> {
            setColor();
            binding.textViewUpdate.
                    setBackground(ContextCompat.getDrawable(activity, R.drawable.border_white_2));
            setPadding();
            binding.viewPager.setCurrentItem(2);
        });
    }

    void textViewAvatar() {
        binding.textViewChangeAvatar.setOnClickListener(view -> {
            setColor();
            binding.textViewChangeAvatar.
                    setBackground(ContextCompat.getDrawable(activity, R.drawable.border_white_2));
            setPadding();
            binding.viewPager.setCurrentItem(3);
        });
    }

    private void setColor() {
        binding.textViewUpdate.setBackgroundColor(Color.TRANSPARENT);
        binding.textViewUpdate.setTextColor(ContextCompat.getColor(activity, R.color.text_color_light));
        binding.textViewChangeTheme.setBackgroundColor(Color.TRANSPARENT);
        binding.textViewChangeTheme.setTextColor(ContextCompat.getColor(activity, R.color.text_color_light));
        binding.textViewChangePassword.setBackgroundColor(Color.TRANSPARENT);
        binding.textViewChangePassword.setTextColor(ContextCompat.getColor(activity, R.color.text_color_light));
        binding.textViewChangeAvatar.setBackgroundColor(Color.TRANSPARENT);
        binding.textViewChangeAvatar.setTextColor(ContextCompat.getColor(activity, R.color.text_color_light));
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
        binding.textViewChangeAvatar.setPadding(0,
                (int) getResources().getDimension(R.dimen.medium_dp), 0,
                (int) getResources().getDimension(R.dimen.medium_dp));
    }

    private void setupViewPager() {
        ViewPagerAdapterTab adapter = new ViewPagerAdapterTab(getSupportFragmentManager());
        adapter.addFragment(new SettingChangeThemeFragment());
        adapter.addFragment(new SettingChangePasswordFragment());
        adapter.addFragment(new SettingUpdateFragment());
        adapter.addFragment(new SettingChangeAvatarFragment());
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
                } else if (position == 3) {
                    binding.textViewChangeAvatar.callOnClick();
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                int currentPage = binding.viewPager.getCurrentItem();
                if (currentPage == 3 || currentPage == 0) {
                    previousState = currentState;
                    currentState = state;
                    if (previousState == 1 && currentState == 0) {
                        binding.viewPager.setCurrentItem(currentPage == 0 ? 3 : 0);
                    }
                }
            }
        });
        binding.viewPager.setPageTransformer(true, new DepthPageTransformer());
    }

    @Override
    protected void onStop() {
        Debug.getNativeHeapAllocatedSize();
        System.runFinalization();
        Runtime.getRuntime().totalMemory();
        Runtime.getRuntime().freeMemory();
        Runtime.getRuntime().maxMemory();
        Runtime.getRuntime().gc();
        System.gc();
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        Debug.getNativeHeapAllocatedSize();
        System.runFinalization();
        Runtime.getRuntime().totalMemory();
        Runtime.getRuntime().freeMemory();
        Runtime.getRuntime().maxMemory();
        Runtime.getRuntime().gc();
        System.gc();
        super.onDestroy();
    }
}