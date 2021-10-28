package com.leon.counter_reading.fragments;

import static com.leon.counter_reading.helpers.Constants.POSITION;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.leon.counter_reading.R;
import com.leon.counter_reading.activities.ReadingActivity;
import com.leon.counter_reading.databinding.FragmentReportTotalBinding;
import com.leon.counter_reading.enums.BundleEnum;
import com.leon.counter_reading.enums.HighLowStateEnum;
import com.leon.counter_reading.enums.ReadStatusEnum;

import org.eazegraph.lib.models.PieModel;
import org.jetbrains.annotations.NotNull;

public class ReportTotalFragment extends Fragment {
    private FragmentReportTotalBinding binding;
    private int zero, normal, high, low;
    private Activity activity;
    @SuppressLint("NonConstantResourceId")
    View.OnClickListener onClickListener = v -> {
        Intent intent = new Intent(getActivity(), ReadingActivity.class);
        intent.putExtra(BundleEnum.READ_STATUS.getValue(), ReadStatusEnum.STATE.getValue());
        switch (v.getId()) {
            case R.id.linear_layout_normal:
                intent.putExtra(BundleEnum.TYPE.getValue(), HighLowStateEnum.NORMAL.getValue());
                break;
            case R.id.linear_layout_zero:
                intent.putExtra(BundleEnum.TYPE.getValue(), HighLowStateEnum.ZERO.getValue());
                break;
            case R.id.linear_layout_high:
                intent.putExtra(BundleEnum.TYPE.getValue(), HighLowStateEnum.HIGH.getValue());
                break;
            case R.id.linear_layout_low:
                intent.putExtra(BundleEnum.TYPE.getValue(), HighLowStateEnum.LOW.getValue());
                break;
            default:
                intent.putExtra(BundleEnum.READ_STATUS.getValue(), ReadStatusEnum.READ.getValue());
        }
        POSITION = 1;
        startActivity(intent);
        activity.finish();
    };

    public static ReportTotalFragment newInstance(int zero, int normal, int high, int low) {
        ReportTotalFragment fragment = new ReportTotalFragment();
        fragment.setArguments(putBundle(zero, normal, high, low));
        return fragment;
    }

    static Bundle putBundle(int zero, int normal, int high,
                            int low) {
        Bundle args = new Bundle();
        args.putInt(BundleEnum.ZERO.getValue(), zero);
        args.putInt(BundleEnum.HIGH.getValue(), high);
        args.putInt(BundleEnum.LOW.getValue(), low);
        args.putInt(BundleEnum.NORMAL.getValue(), normal);
        return args;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getBundle();
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentReportTotalBinding.inflate(inflater, container, false);
        activity = getActivity();
        initialize();
        return binding.getRoot();
    }

    void initialize() {
        setupChart();
        initializeTextViews();
        setOnLinearLayoutClickListener();
    }

    void setupChart() {
        binding.pieChart.addPieSlice(new PieModel(getString(R.string.zero), zero, ContextCompat.getColor(activity, R.color.blue)));
        binding.pieChart.addPieSlice(new PieModel(getString(R.string.normal), normal, ContextCompat.getColor(activity, R.color.green)));
        binding.pieChart.addPieSlice(new PieModel(getString(R.string.down), low, ContextCompat.getColor(activity, R.color.yellow)));
        binding.pieChart.addPieSlice(new PieModel(getString(R.string.up), high, ContextCompat.getColor(activity, R.color.red)));
        binding.pieChart.startAnimation();
    }

    void initializeTextViews() {
        binding.textViewHigh.setText(String.valueOf(high));
        binding.textViewLow.setText(String.valueOf(low));
        binding.textViewZero.setText(String.valueOf(zero));
        binding.textViewNormal.setText(String.valueOf(normal));
        binding.textViewTotal.setText(String.valueOf(normal + low + high + zero));
    }

    void setOnLinearLayoutClickListener() {
        binding.linearLayoutHigh.setOnClickListener(onClickListener);
        binding.linearLayoutLow.setOnClickListener(onClickListener);
        binding.linearLayoutZero.setOnClickListener(onClickListener);
        binding.linearLayoutNormal.setOnClickListener(onClickListener);
        binding.linearLayoutTotal.setOnClickListener(onClickListener);
    }

    void getBundle() {
        if (getArguments() != null) {
            zero = getArguments().getInt(BundleEnum.ZERO.getValue());
            low = getArguments().getInt(BundleEnum.LOW.getValue());
            high = getArguments().getInt(BundleEnum.HIGH.getValue());
            normal = getArguments().getInt(BundleEnum.NORMAL.getValue());
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}