package com.leon.counter_reading.activities;

import android.app.Activity;
import android.graphics.Color;
import android.os.Debug;
import android.view.View;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;

import com.leon.counter_reading.R;
import com.leon.counter_reading.adapters.ViewPagerAdapterTab;
import com.leon.counter_reading.base_items.BaseActivity;
import com.leon.counter_reading.databinding.ActivityReportBinding;
import com.leon.counter_reading.fragments.ReportNotReadingFragment;
import com.leon.counter_reading.fragments.ReportTemporaryFragment;
import com.leon.counter_reading.fragments.ReportTotalFragment;
import com.leon.counter_reading.tables.CounterStateDto;
import com.leon.counter_reading.tables.TrackingDto;
import com.leon.counter_reading.utils.DepthPageTransformer;
import com.leon.counter_reading.utils.reporting.GetReportDBData;

import java.util.ArrayList;

public class ReportActivity extends BaseActivity {
    private ActivityReportBinding binding;
    private Activity activity;
    private int previousState;
    private int currentState;
    private ArrayList<CounterStateDto> counterStateDtos = new ArrayList<>();
    private ArrayList<TrackingDto> trackingDtos = new ArrayList<>();

    @Override
    protected void initialize() {
        binding = ActivityReportBinding.inflate(getLayoutInflater());
        View childLayout = binding.getRoot();
        ConstraintLayout parentLayout = findViewById(R.id.base_Content);
        parentLayout.addView(childLayout);
        activity = this;
        new GetReportDBData(activity).execute(activity);
        initializeTextViews();
    }

    void initializeTextViews() {
        textViewTotalNormal();
        textViewTemporary();
        textViewNotRead();
    }

    void textViewTotalNormal() {
        binding.textViewTotal.setOnClickListener(view -> {
            setColor();
            binding.textViewTotal.
                    setBackground(ContextCompat.getDrawable(activity, R.drawable.border_white_2));
            setPadding();
            binding.viewPager.setCurrentItem(0);
        });
    }

    void textViewNotRead() {
        binding.textViewNotRead.setOnClickListener(view -> {
            setColor();
            binding.textViewNotRead.
                    setBackground(ContextCompat.getDrawable(activity, R.drawable.border_white_2));
            setPadding();
            binding.viewPager.setCurrentItem(1);
        });
    }

    void textViewTemporary() {
        binding.textViewTemporary.setOnClickListener(view -> {
            setColor();
            binding.textViewTemporary.
                    setBackground(ContextCompat.getDrawable(activity, R.drawable.border_white_2));
            setPadding();
            binding.viewPager.setCurrentItem(2);
        });
    }

    private void setColor() {
        binding.textViewNotRead.setBackgroundColor(Color.TRANSPARENT);
        binding.textViewNotRead.setTextColor(
                ContextCompat.getColor(activity, R.color.text_color_light));
        binding.textViewTotal.setBackgroundColor(Color.TRANSPARENT);
        binding.textViewTotal.setTextColor(
                ContextCompat.getColor(activity, R.color.text_color_light));
        binding.textViewTemporary.setBackgroundColor(Color.TRANSPARENT);
        binding.textViewTemporary.setTextColor(
                ContextCompat.getColor(activity, R.color.text_color_light));
    }

    private void setPadding() {
        binding.textViewTotal.setPadding(0,
                (int) getResources().getDimension(R.dimen.medium_dp), 0,
                (int) getResources().getDimension(R.dimen.medium_dp));
        binding.textViewNotRead.setPadding(0,
                (int) getResources().getDimension(R.dimen.medium_dp), 0,
                (int) getResources().getDimension(R.dimen.medium_dp));
        binding.textViewTemporary.setPadding(0,
                (int) getResources().getDimension(R.dimen.medium_dp), 0,
                (int) getResources().getDimension(R.dimen.medium_dp));
    }

    public void setupViewPager(ArrayList<CounterStateDto> counterStateDtos,
                               ArrayList<TrackingDto> trackingDtos, int zero,
                               int normal, int high, int low, int total, int isMane, int unread) {
        this.counterStateDtos = new ArrayList<>(counterStateDtos);
        this.trackingDtos = new ArrayList<>(trackingDtos);

        ViewPagerAdapterTab adapter = new ViewPagerAdapterTab(getSupportFragmentManager());
        adapter.addFragment(ReportTotalFragment.newInstance(zero, normal, high, low));
        adapter.addFragment(ReportNotReadingFragment.newInstance(total, unread));
        adapter.addFragment(ReportTemporaryFragment.newInstance(total, isMane));
        binding.viewPager.setAdapter(adapter);
        binding.viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                if (position == 0) {
                    binding.textViewTotal.callOnClick();
                } else if (position == 1) {
                    binding.textViewNotRead.callOnClick();
                } else if (position == 2) {
                    binding.textViewTemporary.callOnClick();
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

    public ArrayList<CounterStateDto> getCounterStateDtos() {
        return counterStateDtos;
    }

    public ArrayList<TrackingDto> getTrackingDtos() {
        return trackingDtos;
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
        binding = null;
        counterStateDtos = null;
        trackingDtos = null;
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