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
import com.leon.counter_reading.databinding.ActivityReportBinding;
import com.leon.counter_reading.enums.HighLowStateEnum;
import com.leon.counter_reading.fragments.ReportNotReadingFragment;
import com.leon.counter_reading.fragments.ReportTemporaryFragment;
import com.leon.counter_reading.fragments.ReportTotalFragment;
import com.leon.counter_reading.tables.CounterStateDto;
import com.leon.counter_reading.tables.TrackingDto;
import com.leon.counter_reading.utils.CustomProgressBar;
import com.leon.counter_reading.utils.DepthPageTransformer;
import com.leon.counter_reading.utils.MyDatabaseClient;

import java.util.ArrayList;

public class ReportActivity extends BaseActivity {
    ActivityReportBinding binding;
    Activity activity;
    int previousState, currentState;
    int zero, normal, high, low, unread, total, isMane;
    ArrayList<CounterStateDto> counterStateDtos = new ArrayList<>();
    ArrayList<TrackingDto> trackingDtos = new ArrayList<>();

    @Override
    protected void initialize() {
        binding = ActivityReportBinding.inflate(getLayoutInflater());
        View childLayout = binding.getRoot();
        ConstraintLayout parentLayout = findViewById(R.id.base_Content);
        parentLayout.addView(childLayout);
        activity = this;
        new GetDBData().execute();
        initializeTextViews();
    }

    void initializeTextViews() {
        textViewTotalNormal();
        textViewTemporary();
        textViewNotRead();
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    void textViewTotalNormal() {
        binding.textViewTotal.setOnClickListener(view -> {
            setColor();
            binding.textViewTotal.setBackground(
                    ContextCompat.getDrawable(activity, R.drawable.border_white_2));
            setPadding();
            binding.viewPager.setCurrentItem(0);
        });
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    void textViewNotRead() {
        binding.textViewNotRead.setOnClickListener(view -> {
            setColor();
            binding.textViewNotRead.setBackground(
                    ContextCompat.getDrawable(activity, R.drawable.border_white_2));
            setPadding();
            binding.viewPager.setCurrentItem(1);
        });
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    void textViewTemporary() {
        binding.textViewTemporary.setOnClickListener(view -> {
            setColor();
            binding.textViewTemporary.setBackground(
                    ContextCompat.getDrawable(activity, R.drawable.border_white_2));
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

    private void setupViewPager() {
        //TODO
        ViewPagerAdapterTab adapter = new ViewPagerAdapterTab(getSupportFragmentManager());
        adapter.addFragment(ReportTotalFragment.newInstance(zero, normal, high, low), "آمار کلی");
        adapter.addFragment(ReportNotReadingFragment.newInstance(total, unread), "قرائت نشده");
        adapter.addFragment(ReportTemporaryFragment.newInstance(counterStateDtos, total, isMane), "علی الحساب");
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
            super.onPostExecute(integer);
            customProgressBar.getDialog().dismiss();
        }

        @Override
        protected Integer doInBackground(Integer... integers) {
            trackingDtos.addAll(MyDatabaseClient.getInstance(activity).getMyDatabase().
                    trackingDao().getTrackingDtosIsActiveNotArchive(true, false));
            counterStateDtos.addAll(MyDatabaseClient.getInstance(activity).getMyDatabase().
                    counterStateDao().getCounterStateDtos());
            ArrayList<Integer> isManes = new ArrayList<>(MyDatabaseClient.getInstance(activity).
                    getMyDatabase().counterStateDao().getCounterStateDtosIsMane(true));

            for (TrackingDto trackingDto : trackingDtos) {
                for (int i = 0; i < isManes.size(); i++) {
                    isMane = isMane + MyDatabaseClient.getInstance(activity).getMyDatabase().
                            onOffLoadDao().getOnOffLoadIsManeCount(isManes.get(i), trackingDto.id);
                }
                zero = zero + MyDatabaseClient.getInstance(activity).getMyDatabase().
                        onOffLoadDao().getOnOffLoadReadCountByStatus(trackingDto.id,
                        HighLowStateEnum.ZERO.getValue());
                high = high + MyDatabaseClient.getInstance(activity).getMyDatabase().
                        onOffLoadDao().getOnOffLoadReadCountByStatus(trackingDto.id,
                        HighLowStateEnum.HIGH.getValue());
                low = low + MyDatabaseClient.getInstance(activity).getMyDatabase().
                        onOffLoadDao().getOnOffLoadReadCountByStatus(trackingDto.id,
                        HighLowStateEnum.LOW.getValue());
                normal = normal + MyDatabaseClient.getInstance(activity).getMyDatabase().
                        onOffLoadDao().getOnOffLoadReadCountByStatus(trackingDto.id,
                        HighLowStateEnum.NORMAL.getValue());
                unread = unread + MyDatabaseClient.getInstance(activity).getMyDatabase().
                        onOffLoadDao().getOnOffLoadReadCount(0, trackingDto.id);
                total = total + MyDatabaseClient.getInstance(activity).getMyDatabase().
                        onOffLoadDao().getOnOffLoadCount(trackingDto.id);
            }
            runOnUiThread(ReportActivity.this::setupViewPager);
            return null;
        }
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