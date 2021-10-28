package com.leon.counter_reading.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.leon.counter_reading.databinding.FragmentReportPerformanceBinding;
import com.leon.counter_reading.tables.PerformanceResponse;
import com.leon.counter_reading.utils.performance.GetPerformance;
import com.sardari.daterangepicker.customviews.DateRangeCalendarView;
import com.sardari.daterangepicker.dialog.DatePickerDialog;
import com.sardari.daterangepicker.utils.PersianCalendar;

public class ReportPerformanceFragment extends Fragment {
    private FragmentReportPerformanceBinding binding;
    private Activity activity;
    private boolean visible = true;

    public ReportPerformanceFragment() {
    }

    public static ReportPerformanceFragment newInstance() {
        return new ReportPerformanceFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentReportPerformanceBinding.inflate(inflater, container, false);
        activity = requireActivity();
        initialize();
        return binding.getRoot();
    }

    private void initialize() {
        setupTextViews();
    }

    private void setupTextViews() {
        binding.textViewFrom.setText(new PersianCalendar().getPersianShortDate());
        binding.textViewEnd.setText(new PersianCalendar().getPersianShortDate());
        binding.relativeLayoutDetail.setVisibility(View.GONE);
        setOnTextViewFromOnClickListener();
        setOnTextViewEndOnClickListener();
        setSubmitButtonOnClickListener();
    }

    private void setSubmitButtonOnClickListener() {
//        binding.buttonSubmit.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                new GetPerformance(activity, this, binding.textViewFrom.getText().toString(),
//                        binding.textViewEnd.getText().toString()).execute(activity);
//            }
//        });
        binding.buttonSubmit.setOnClickListener(v -> {
            if (visible)
                new GetPerformance(activity, this, binding.textViewFrom.getText().toString(),
                        binding.textViewEnd.getText().toString()).execute(activity);
            else {
                binding.linearLayoutFrom.setVisibility(View.VISIBLE);
                binding.linearLayoutEnd.setVisibility(View.VISIBLE);
                visible = true;
            }
        });
    }

    private void setOnTextViewFromOnClickListener() {
        binding.textViewFrom.setOnClickListener(v -> {
            DatePickerDialog datePickerDialog = new DatePickerDialog(activity);
            datePickerDialog.setSelectionMode(DateRangeCalendarView.SelectionMode.Single);
            datePickerDialog.setDisableDaysAgo(false);
            datePickerDialog.setTextSizeTitle(10.0f);
            datePickerDialog.setTextSizeWeek(12.0f);
            datePickerDialog.setTextSizeDate(14.0f);
            datePickerDialog.setCanceledOnTouchOutside(true);
//            datePickerDialog.setOnSingleDateSelectedListener(date ->
//                    );
            datePickerDialog.setOnSingleDateSelectedListener(date -> {
//                startDate = date.getPersianShortDate();
                binding.relativeLayoutDetail.setVisibility(View.GONE);
                binding.textViewFrom.setText(date.getPersianShortDate());
            });
            datePickerDialog.showDialog();
        });
    }

    private void setOnTextViewEndOnClickListener() {
        binding.textViewEnd.setOnClickListener(v -> {
            DatePickerDialog datePickerDialog = new DatePickerDialog(activity);
            datePickerDialog.setSelectionMode(DateRangeCalendarView.SelectionMode.Single);
            datePickerDialog.setDisableDaysAgo(false);
            datePickerDialog.setTextSizeTitle(10.0f);
            datePickerDialog.setTextSizeWeek(12.0f);
            datePickerDialog.setTextSizeDate(14.0f);
            datePickerDialog.setCanceledOnTouchOutside(true);
            datePickerDialog.setOnSingleDateSelectedListener(date -> {
                binding.relativeLayoutDetail.setVisibility(View.GONE);
//                endDate = date.getPersianShortDate();
                binding.textViewEnd.setText(date.getPersianShortDate());
            });

            datePickerDialog.showDialog();
        });
    }

    public void setTextViewTextSetter(PerformanceResponse performanceResponse) {
        activity.runOnUiThread(() -> {
            binding.linearLayoutEnd.setVisibility(View.GONE);
            binding.linearLayoutFrom.setVisibility(View.GONE);
            binding.relativeLayoutDetail.setVisibility(View.VISIBLE);
            visible = false;
            binding.textViewAdiCount.setText(String.valueOf(performanceResponse.adiCount));
            binding.textViewTotal.setText(String.valueOf(performanceResponse.overalCount));
            binding.textViewMedia.setText(String.valueOf(performanceResponse.mediaCount));
            binding.textViewForbid.setText(String.valueOf(performanceResponse.forbiddenCount));
            binding.textViewMane.setText(String.valueOf(performanceResponse.maneCount));
            binding.textViewXarab.setText(String.valueOf(performanceResponse.xarabCount));
            binding.textViewTaviz.setText(String.valueOf(performanceResponse.tavizCount));
            binding.textViewFaqed.setText(String.valueOf(performanceResponse.faqedCount));
        });
    }
}