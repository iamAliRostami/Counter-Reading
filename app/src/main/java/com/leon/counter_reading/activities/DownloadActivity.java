package com.leon.counter_reading.activities;

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
import com.leon.counter_reading.databinding.ActivityDownloadBinding;
import com.leon.counter_reading.fragments.DownloadFragment;
import com.leon.counter_reading.utils.DepthPageTransformer;
import com.leon.counter_reading.utils.DifferentCompanyManager;

public class DownloadActivity extends BaseActivity {
    private ActivityDownloadBinding binding;
    private int previousState, currentState;

    @Override
    protected void initialize() {
        binding = ActivityDownloadBinding.inflate(getLayoutInflater());
        View childLayout = binding.getRoot();
        ConstraintLayout parentLayout = findViewById(R.id.base_Content);
        parentLayout.addView(childLayout);
        setupViewPager();
        initializeTextViews();
    }

    void initializeTextViews() {
        TextView textViewCompanyName = findViewById(R.id.text_view_company_name);
        textViewCompanyName.setText(DifferentCompanyManager.getCompanyName(DifferentCompanyManager.getActiveCompanyName()));

        textViewDownloadNormal();
        textViewDownloadSpecial();
        textViewDownloadOff();
        textViewDownloadRetry();
    }

    void textViewDownloadOff() {
        binding.textViewDownloadOff.setOnClickListener(view -> {
            setColor();
            binding.textViewDownloadOff.setBackground(
                    ContextCompat.getDrawable(getApplicationContext(), R.drawable.border_white_2));
            setPadding();
            binding.viewPager.setCurrentItem(2);
        });
    }

    void textViewDownloadRetry() {
        binding.textViewDownloadRetry.setOnClickListener(view -> {
            setColor();
            binding.textViewDownloadRetry.setBackground(
                    ContextCompat.getDrawable(getApplicationContext(), R.drawable.border_white_2));
            setPadding();
            binding.viewPager.setCurrentItem(1);
        });
    }

    void textViewDownloadSpecial() {
        binding.textViewDownloadSpecial.setOnClickListener(view -> {
            setColor();
            binding.textViewDownloadSpecial.setBackground(ContextCompat.getDrawable(
                    getApplicationContext(), R.drawable.border_white_2));
            setPadding();
            binding.viewPager.setCurrentItem(3);
        });
    }

    void textViewDownloadNormal() {
        binding.textViewDownloadNormal.setOnClickListener(view -> {
            setColor();
            binding.textViewDownloadNormal.setBackground(ContextCompat.getDrawable(
                    getApplicationContext(), R.drawable.border_white_2));
            setPadding();
            binding.viewPager.setCurrentItem(0);
        });
    }

    private void setColor() {
        binding.textViewDownloadOff.setBackgroundColor(Color.TRANSPARENT);
        binding.textViewDownloadOff.setTextColor(
                ContextCompat.getColor(getApplicationContext(), R.color.text_color_light));
        binding.textViewDownloadNormal.setBackgroundColor(Color.TRANSPARENT);
        binding.textViewDownloadNormal.setTextColor(
                ContextCompat.getColor(getApplicationContext(), R.color.text_color_light));
        binding.textViewDownloadRetry.setBackgroundColor(Color.TRANSPARENT);
        binding.textViewDownloadRetry.setTextColor(
                ContextCompat.getColor(getApplicationContext(), R.color.text_color_light));
        binding.textViewDownloadSpecial.setBackgroundColor(Color.TRANSPARENT);
        binding.textViewDownloadSpecial.setTextColor(
                ContextCompat.getColor(getApplicationContext(), R.color.text_color_light));
    }

    private void setPadding() {
        binding.textViewDownloadNormal.setPadding(0,
                (int) getResources().getDimension(R.dimen.medium_dp), 0,
                (int) getResources().getDimension(R.dimen.medium_dp));
        binding.textViewDownloadOff.setPadding(0,
                (int) getResources().getDimension(R.dimen.medium_dp), 0,
                (int) getResources().getDimension(R.dimen.medium_dp));
        binding.textViewDownloadRetry.setPadding(0,
                (int) getResources().getDimension(R.dimen.medium_dp), 0,
                (int) getResources().getDimension(R.dimen.medium_dp));
        binding.textViewDownloadSpecial.setPadding(0,
                (int) getResources().getDimension(R.dimen.medium_dp), 0,
                (int) getResources().getDimension(R.dimen.medium_dp));
    }

    private void setupViewPager() {
        ViewPagerAdapterTab adapter = new ViewPagerAdapterTab(getSupportFragmentManager()
        );
        adapter.addFragment(DownloadFragment.newInstance(1));
        adapter.addFragment(DownloadFragment.newInstance(2));
        adapter.addFragment(DownloadFragment.newInstance(3));
        adapter.addFragment(DownloadFragment.newInstance(4));
        binding.viewPager.setAdapter(adapter);
        binding.viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                if (position == 0) {
                    binding.textViewDownloadNormal.callOnClick();
                } else if (position == 1) {
                    binding.textViewDownloadRetry.callOnClick();
                } else if (position == 2) {
                    binding.textViewDownloadOff.callOnClick();
                } else if (position == 3) {
                    binding.textViewDownloadSpecial.callOnClick();
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