package com.leon.counter_reading.activities;

import android.app.Activity;
import android.graphics.Color;
import android.os.Debug;
import android.view.View;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;

import com.leon.counter_reading.helpers.MyApplication;
import com.leon.counter_reading.R;
import com.leon.counter_reading.adapters.ViewPagerAdapterTab;
import com.leon.counter_reading.base_items.BaseActivity;
import com.leon.counter_reading.databinding.ActivityReadingSettingBinding;
import com.leon.counter_reading.fragments.ReadingPossibleSettingFragment;
import com.leon.counter_reading.fragments.ReadingSettingDeleteFragment;
import com.leon.counter_reading.fragments.ReadingSettingFragment;
import com.leon.counter_reading.tables.TrackingDto;
import com.leon.counter_reading.utils.DepthPageTransformer;
import com.leon.counter_reading.utils.DifferentCompanyManager;

import java.util.ArrayList;

public class ReadingSettingActivity extends BaseActivity {
    private ActivityReadingSettingBinding binding;
    private int previousState, currentState;
    private ArrayList<TrackingDto> trackingDtos = new ArrayList<>();
    private Activity activity;

    @Override
    protected void initialize() {
        binding = ActivityReadingSettingBinding.inflate(getLayoutInflater());
        View childLayout = binding.getRoot();
        ConstraintLayout parentLayout = findViewById(R.id.base_Content);
        parentLayout.addView(childLayout);

        TextView textViewCompanyName = findViewById(R.id.text_view_company_name);
        textViewCompanyName.setText(DifferentCompanyManager.getCompanyName(DifferentCompanyManager.getActiveCompanyName()));

        activity = this;
        trackingDtos.addAll(MyApplication.getApplicationComponent().MyDatabase().
                trackingDao().getTrackingDtoNotArchive(false));
        setupViewPager();
        initializeTextViews();
    }

    void initializeTextViews() {
        textViewRead();
        textViewDelete();
        textViewNavigation();
    }

    void textViewRead() {
        binding.textViewRead.setOnClickListener(view -> {
            setColor();
            binding.textViewRead.setBackground(
                    ContextCompat.getDrawable(activity, R.drawable.border_white_2));
            setPadding();
            binding.viewPager.setCurrentItem(0);
        });
    }

    void textViewNavigation() {
        binding.textViewNavigation.setOnClickListener(view -> {
            setColor();
            binding.textViewNavigation.setBackground(
                    ContextCompat.getDrawable(activity, R.drawable.border_white_2));
            setPadding();
            binding.viewPager.setCurrentItem(1);
        });
    }

    void textViewDelete() {
        binding.textViewDelete.setOnClickListener(view -> {
            setColor();
            binding.textViewDelete.setBackground(
                    ContextCompat.getDrawable(activity, R.drawable.border_white_2));
            setPadding();
            binding.viewPager.setCurrentItem(2);
        });
    }

    private void setColor() {
        binding.textViewRead.setBackgroundColor(Color.TRANSPARENT);
        binding.textViewRead.setTextColor(ContextCompat.getColor(activity, R.color.text_color_light));
        binding.textViewDelete.setBackgroundColor(Color.TRANSPARENT);
        binding.textViewDelete.setTextColor(ContextCompat.getColor(activity, R.color.text_color_light));
        binding.textViewNavigation.setBackgroundColor(Color.TRANSPARENT);
        binding.textViewNavigation.setTextColor(ContextCompat.getColor(activity, R.color.text_color_light));
    }

    private void setPadding() {
        binding.textViewRead.setPadding(0,
                (int) getResources().getDimension(R.dimen.medium_dp), 0,
                (int) getResources().getDimension(R.dimen.medium_dp));
        binding.textViewDelete.setPadding(0,
                (int) getResources().getDimension(R.dimen.medium_dp), 0,
                (int) getResources().getDimension(R.dimen.medium_dp));
        binding.textViewNavigation.setPadding(0,
                (int) getResources().getDimension(R.dimen.medium_dp), 0,
                (int) getResources().getDimension(R.dimen.medium_dp));
    }

    private void setupViewPager() {
        ViewPagerAdapterTab adapter = new ViewPagerAdapterTab(getSupportFragmentManager());
        adapter.addFragment(ReadingSettingFragment.newInstance(trackingDtos));
        adapter.addFragment(new ReadingPossibleSettingFragment());
        adapter.addFragment(ReadingSettingDeleteFragment.newInstance(trackingDtos));

        binding.viewPager.setAdapter(adapter);
        binding.viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                if (position == 0) {
                    binding.textViewRead.callOnClick();
                } else if (position == 1) {
                    binding.textViewNavigation.callOnClick();
                } else if (position == 2) {
                    binding.textViewDelete.callOnClick();
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
        trackingDtos = null;
        binding = null;
//        MyDatabaseClient.getInstance(MyApplication.getContext()).destroyDatabase();
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