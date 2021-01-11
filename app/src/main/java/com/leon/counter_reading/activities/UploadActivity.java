package com.leon.counter_reading.activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Debug;
import android.view.View;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.leon.counter_reading.R;
import com.leon.counter_reading.adapters.ViewPagerAdapterTab;
import com.leon.counter_reading.base_items.BaseActivity;
import com.leon.counter_reading.databinding.ActivityUploadBinding;
import com.leon.counter_reading.fragments.UploadFragment;
import com.leon.counter_reading.tables.TrackingDto;
import com.leon.counter_reading.utils.CustomProgressBar;
import com.leon.counter_reading.utils.DepthPageTransformer;
import com.leon.counter_reading.utils.MyDatabaseClient;

import java.util.ArrayList;

public class UploadActivity extends BaseActivity {
    ActivityUploadBinding binding;
    Activity activity;
    int previousState, currentState;
    ArrayList<TrackingDto> trackingDtos = new ArrayList<>();

    @Override
    protected void initialize() {
        binding = ActivityUploadBinding.inflate(getLayoutInflater());
        View childLayout = binding.getRoot();
        ConstraintLayout parentLayout = findViewById(R.id.base_Content);
        parentLayout.addView(childLayout);
        activity = this;
        new GetDBData().execute();
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
            binding.textViewUploadOff.setBackground(
                    ContextCompat.getDrawable(getApplicationContext(), R.drawable.border_white_2));
            setPadding();
            binding.viewPager.setCurrentItem(1);
        });
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    void textViewUploadMultimedia() {
        binding.textViewUploadMultimedia.setOnClickListener(view -> {
            setColor();
            binding.textViewUploadMultimedia.setBackground(
                    ContextCompat.getDrawable(getApplicationContext(), R.drawable.border_white_2));
            setPadding();
            binding.viewPager.setCurrentItem(2);
        });
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    void textViewUploadNormal() {
        binding.textViewUpload.setOnClickListener(view -> {
            setColor();
            binding.textViewUpload.setBackground(
                    ContextCompat.getDrawable(getApplicationContext(), R.drawable.border_white_2));
            setPadding();
            binding.viewPager.setCurrentItem(0);
        });
    }

    private void setColor() {
        binding.textViewUploadOff.setBackgroundColor(Color.TRANSPARENT);
        binding.textViewUploadOff.setTextColor(
                ContextCompat.getColor(getApplicationContext(), R.color.text_color_light));
        binding.textViewUpload.setBackgroundColor(Color.TRANSPARENT);
        binding.textViewUpload.setTextColor(
                ContextCompat.getColor(getApplicationContext(), R.color.text_color_light));
        binding.textViewUploadMultimedia.setBackgroundColor(Color.TRANSPARENT);
        binding.textViewUploadMultimedia.setTextColor(
                ContextCompat.getColor(getApplicationContext(), R.color.text_color_light));
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
        ViewPagerAdapterTab adapter = new ViewPagerAdapterTab(getSupportFragmentManager(),
                FragmentStatePagerAdapter.POSITION_NONE);
        adapter.addFragment(UploadFragment.newInstance(1, trackingDtos), "بارگذاری");
        adapter.addFragment(UploadFragment.newInstance(2, trackingDtos), "بارگذاری مجدد");
        adapter.addFragment(UploadFragment.newInstance(3, new ArrayList<>()), "بارگذاری چند رسانه");
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

    @SuppressLint("StaticFieldLeak")
    class GetDBData extends AsyncTask<Integer, Integer, Integer> {
        CustomProgressBar customProgressBar;

        public GetDBData() {
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
            trackingDtos.addAll(MyDatabaseClient.getInstance(activity).getMyDatabase().
                    trackingDao().getTrackingDtoNotArchive(false));
            runOnUiThread(() -> {
                setupViewPager();
                initializeTextViews();
            });
            return null;
        }
    }
}