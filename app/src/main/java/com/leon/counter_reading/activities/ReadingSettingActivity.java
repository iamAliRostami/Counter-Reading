package com.leon.counter_reading.activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Debug;
import android.view.View;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;

import com.leon.counter_reading.R;
import com.leon.counter_reading.adapters.ViewPagerAdapterTab;
import com.leon.counter_reading.base_items.BaseActivity;
import com.leon.counter_reading.databinding.ActivityReadingSettingBinding;
import com.leon.counter_reading.fragments.ReadingPossibleSettingFragment;
import com.leon.counter_reading.fragments.ReadingSettingDeleteFragment;
import com.leon.counter_reading.fragments.ReadingSettingFragment;
import com.leon.counter_reading.tables.TrackingDto;
import com.leon.counter_reading.utils.CustomProgressBar;
import com.leon.counter_reading.utils.DepthPageTransformer;
import com.leon.counter_reading.utils.MyDatabaseClient;

import java.util.ArrayList;

public class ReadingSettingActivity extends BaseActivity {
    ActivityReadingSettingBinding binding;
    int previousState, currentState;
    ArrayList<TrackingDto> trackingDtos = new ArrayList<>();
    Activity activity;

    @Override
    protected void initialize() {
        binding = ActivityReadingSettingBinding.inflate(getLayoutInflater());
        View childLayout = binding.getRoot();
        ConstraintLayout parentLayout = findViewById(R.id.base_Content);
        parentLayout.addView(childLayout);
        activity = this;
        new getDBData().execute();
        initializeTextViews();
    }

    void initializeTextViews() {
        textViewRead();
        textViewDelete();
        textViewNavigation();
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    void textViewRead() {
        binding.textViewRead.setOnClickListener(view -> {
            setColor();
            binding.textViewRead.setBackground(
                    ContextCompat.getDrawable(activity, R.drawable.border_white_2));
            setPadding();
            binding.viewPager.setCurrentItem(0);
        });
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    void textViewNavigation() {
        binding.textViewNavigation.setOnClickListener(view -> {
            setColor();
            binding.textViewNavigation.setBackground(
                    ContextCompat.getDrawable(activity, R.drawable.border_white_2));
            setPadding();
            binding.viewPager.setCurrentItem(1);
        });
    }

    @SuppressLint("UseCompatLoadingForDrawables")
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
//        adapter.addFragment(UploadFragment.newInstance(1, trackingDtos), "بارگذاری");
        adapter.addFragment(ReadingSettingFragment.newInstance(trackingDtos), "تنظیمات قرائت");
        adapter.addFragment(new ReadingPossibleSettingFragment(), "پیمایش پویا");
        adapter.addFragment(ReadingSettingDeleteFragment.newInstance(trackingDtos), "حذف");
//        adapter.addFragment(DownloadFragment.newInstance(2), "بارگذاری");

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

    @SuppressLint("StaticFieldLeak")
    class getDBData extends AsyncTask<Integer, Integer, Integer> {
        CustomProgressBar customProgressBar;

        public getDBData() {
            super();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            customProgressBar = new CustomProgressBar();
            customProgressBar.show(activity, false);
        }

        @Override
        protected void onPostExecute(Integer integer) {
            customProgressBar.getDialog().dismiss();
            super.onPostExecute(integer);
        }

        @Override
        protected Integer doInBackground(Integer... integers) {
            //TODO
            trackingDtos.addAll(MyDatabaseClient.getInstance(activity).getMyDatabase().
                    trackingDao().getTrackingDtoNotArchive(false));
            runOnUiThread(ReadingSettingActivity.this::setupViewPager);
            return null;
        }
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