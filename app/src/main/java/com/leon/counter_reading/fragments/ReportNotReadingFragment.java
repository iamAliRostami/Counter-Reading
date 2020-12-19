package com.leon.counter_reading.fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.leon.counter_reading.MyApplication;
import com.leon.counter_reading.R;
import com.leon.counter_reading.activities.ReadingActivity;
import com.leon.counter_reading.databinding.FragmentReportNotReadingBinding;
import com.leon.counter_reading.enums.BundleEnum;
import com.leon.counter_reading.enums.ReadStatusEnum;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class ReportNotReadingFragment extends Fragment {
    FragmentReportNotReadingBinding binding;
    int unread, total;

    public ReportNotReadingFragment() {
    }

    public static ReportNotReadingFragment newInstance(int total, int unread) {
        ReportNotReadingFragment fragment = new ReportNotReadingFragment();
        fragment.setArguments(putBundle(total, unread));
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getBundle();
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentReportNotReadingBinding.inflate(inflater, container, false);
        initialize();
        return binding.getRoot();
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    void initialize() {
        binding.textViewNotRead.setText(String.valueOf(unread));
        binding.textViewTotal.setText(String.valueOf(total));
        binding.imageViewNotRead.setImageDrawable(getResources().getDrawable(R.drawable.img_not_read));
        binding.buttonContinue.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), ReadingActivity.class);
            intent.putExtra(BundleEnum.READ_STATUS.getValue(), ReadStatusEnum.UNREAD.getValue());
            MyApplication.POSITION = 1;
            Objects.requireNonNull(getActivity()).startActivity(intent);
        });
    }

    static Bundle putBundle(int total, int unread) {
        Bundle args = new Bundle();
        args.putInt(BundleEnum.UNREAD.getValue(), unread);
        args.putInt(BundleEnum.TOTAL.getValue(), total);
        return args;
    }

    void getBundle() {
        if (getArguments() != null) {
            total = getArguments().getInt(BundleEnum.TOTAL.getValue());
            unread = getArguments().getInt(BundleEnum.UNREAD.getValue());
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding.imageViewNotRead.setImageDrawable(null);
    }
}