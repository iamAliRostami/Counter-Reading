package com.leon.counter_reading.activities;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Debug;
import android.view.View;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.viewpager.widget.ViewPager;

import com.leon.counter_reading.R;
import com.leon.counter_reading.adapters.ViewPagerAdapterTab;
import com.leon.counter_reading.base_items.BaseActivity;
import com.leon.counter_reading.databinding.ActivityUploadBinding;
import com.leon.counter_reading.fragments.UploadFragment;
import com.leon.counter_reading.utils.DepthPageTransformer;

public class UploadActivity extends BaseActivity {
    ActivityUploadBinding binding;
    private int previousState, currentState;

    @Override
    protected void initialize() {
        binding = ActivityUploadBinding.inflate(getLayoutInflater());
        View childLayout = binding.getRoot();
        ConstraintLayout parentLayout = findViewById(R.id.base_Content);
        parentLayout.addView(childLayout);
        setupViewPager();
        initializeTextViews();
    }

    void initializeTextViews() {
        textViewUploadNormal();
        textViewUploadMultimedia();
        textViewUploadOff();
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    void textViewUploadOff() {
        binding.textViewUploadOff.setOnClickListener(view -> {
            setColor();
            binding.textViewUploadOff.setBackground(getResources().getDrawable(R.drawable.border_white_2));
            setPadding();
            binding.viewPager.setCurrentItem(1);
        });
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    void textViewUploadMultimedia() {
        binding.textViewUploadMultimedia.setOnClickListener(view -> {
            setColor();
            binding.textViewUploadMultimedia.setBackground(getResources().getDrawable(R.drawable.border_white_2));
            setPadding();
            binding.viewPager.setCurrentItem(2);
        });
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    void textViewUploadNormal() {
        binding.textViewUpload.setOnClickListener(view -> {
            setColor();
            binding.textViewUpload.setBackground(getResources().getDrawable(R.drawable.border_white_2));
            setPadding();
            binding.viewPager.setCurrentItem(0);
        });
    }

    private void setColor() {
        binding.textViewUploadOff.setBackgroundColor(Color.TRANSPARENT);
        binding.textViewUploadOff.setTextColor(getResources().getColor(R.color.text_color_light));
        binding.textViewUpload.setBackgroundColor(Color.TRANSPARENT);
        binding.textViewUpload.setTextColor(getResources().getColor(R.color.text_color_light));
        binding.textViewUploadMultimedia.setBackgroundColor(Color.TRANSPARENT);
        binding.textViewUploadMultimedia.setTextColor(getResources().getColor(R.color.text_color_light));
    }

    private void setPadding() {
        binding.textViewUpload.setPadding(0,
                (int) getResources().getDimension(R.dimen.medium_dp), 0,
                (int) getResources().getDimension(R.dimen.medium_dp));
        binding.textViewUploadOff.setPadding(0,
                (int) getResources().getDimension(R.dimen.medium_dp), 0,
                (int) getResources().getDimension(R.dimen.medium_dp));
        binding.textViewUploadMultimedia.setPadding(0,
                (int) getResources().getDimension(R.dimen.medium_dp), 0,
                (int) getResources().getDimension(R.dimen.medium_dp));
    }

    private void setupViewPager() {
        ViewPagerAdapterTab adapter = new ViewPagerAdapterTab(getSupportFragmentManager());
        adapter.addFragment(UploadFragment.newInstance(1), "بارگذاری");
        adapter.addFragment(UploadFragment.newInstance(2), "بارگذاری مجدد");
        adapter.addFragment(UploadFragment.newInstance(3), "بارگذاری چند رسانه");
        binding.viewPager.setAdapter(adapter);
        binding.viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position == 0) {
                    binding.textViewUpload.callOnClick();
                } else if (position == 1) {
                    binding.textViewUploadOff.callOnClick();
                } else if (position == 2) {
                    binding.textViewUploadMultimedia.callOnClick();
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