package com.leon.counter_reading.activities;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Debug;

import androidx.appcompat.app.AppCompatActivity;

import com.leon.counter_reading.MyApplication;
import com.leon.counter_reading.R;
import com.leon.counter_reading.adapters.ReadingReportCustomAdapter;
import com.leon.counter_reading.databinding.ActivityReadingReportBinding;
import com.leon.counter_reading.enums.BundleEnum;
import com.leon.counter_reading.enums.SharedReferenceKeys;
import com.leon.counter_reading.enums.SharedReferenceNames;
import com.leon.counter_reading.infrastructure.ISharedPreferenceManager;
import com.leon.counter_reading.tables.CounterReportDto;
import com.leon.counter_reading.tables.OffLoadReport;
import com.leon.counter_reading.utils.CustomToast;
import com.leon.counter_reading.utils.SharedPreferenceManager;
import com.leon.counter_reading.utils.reporting.GetReadingReportDBData;

import java.util.ArrayList;

public class ReadingReportActivity extends AppCompatActivity {
    private ActivityReadingReportBinding binding;
    private Activity activity;
    private String uuid;
    private int position, trackNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ISharedPreferenceManager sharedPreferenceManager = new SharedPreferenceManager(getApplicationContext(),
                SharedReferenceNames.ACCOUNT.getValue());
        int theme = sharedPreferenceManager.getIntData(SharedReferenceKeys.THEME_STABLE.getValue());
        MyApplication.onActivitySetTheme(this, theme, true);
        binding = ActivityReadingReportBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        activity = this;
        initialize();
    }

    void initialize() {
        if (getIntent().getExtras() != null) {
            uuid = getIntent().getExtras().getString(BundleEnum.BILL_ID.getValue());
            trackNumber = getIntent().getExtras().getInt(BundleEnum.TRACKING.getValue());
            position = getIntent().getExtras().getInt(BundleEnum.POSITION.getValue());
        }
        new GetReadingReportDBData(activity, trackNumber, uuid).execute(activity);
        binding.buttonSubmit.setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.putExtra(BundleEnum.POSITION.getValue(), position);
            intent.putExtra(BundleEnum.BILL_ID.getValue(), uuid);
            setResult(RESULT_OK, intent);
            finish();
        });
    }

    public void setupRecyclerView(ArrayList<CounterReportDto> counterReportDtos,
                                  ArrayList<OffLoadReport> offLoadReports) {
        ReadingReportCustomAdapter readingReportCustomAdapter =
                new ReadingReportCustomAdapter(activity, uuid, trackNumber,
                        counterReportDtos, offLoadReports);
        activity.runOnUiThread(() -> binding.listViewReports.setAdapter(readingReportCustomAdapter));
    }

    @Override
    public void onBackPressed() {
        new CustomToast().warning(getString(R.string.submit_for_back));
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