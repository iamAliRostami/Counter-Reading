package com.leon.counter_reading.fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.gson.Gson;
import com.leon.counter_reading.MyApplication;
import com.leon.counter_reading.R;
import com.leon.counter_reading.activities.ReadingActivity;
import com.leon.counter_reading.activities.ReportActivity;
import com.leon.counter_reading.adapters.SpinnerCustomAdapterNew;
import com.leon.counter_reading.databinding.FragmentReportTemporaryBinding;
import com.leon.counter_reading.enums.BundleEnum;
import com.leon.counter_reading.enums.ReadStatusEnum;
import com.leon.counter_reading.tables.CounterStateDto;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class ReportTemporaryFragment extends Fragment {
    private FragmentReportTemporaryBinding binding;
    private ArrayList<CounterStateDto> counterStateDtos = new ArrayList<>();
    private SpinnerCustomAdapterNew adapter;
    private Activity activity;
    private String[] items;
    private int total, isMane;
    private boolean isFirst = true;

    public static ReportTemporaryFragment newInstance(int total, int isMane) {
        ReportTemporaryFragment fragment = new ReportTemporaryFragment();
        fragment.setArguments(putBundle(total, isMane));
        return fragment;
    }

    static Bundle putBundle(int total, int isMane) {
        Bundle args = new Bundle();
        args.putInt(BundleEnum.TOTAL.getValue(), total);
        args.putInt(BundleEnum.IS_MANE.getValue(), isMane);
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
        binding = FragmentReportTemporaryBinding.inflate(inflater, container, false);
        initialize();
        return binding.getRoot();
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    void initialize() {
        binding.imageViewTemporary.setImageDrawable(
                ContextCompat.getDrawable(activity, R.drawable.img_temporary_report));
        binding.textViewTotal.setText(String.valueOf(total));
        binding.textViewTemporary.setText(String.valueOf(isMane));
        initializeSpinner();
    }

    void initializeSpinner() {
        adapter = new SpinnerCustomAdapterNew(activity, items);
        binding.spinner.setAdapter(adapter);
        binding.spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (isFirst) {
                    isFirst = false;
                    return;
                }
                Intent intent = new Intent(getActivity(), ReadingActivity.class);
                Gson gson = new Gson();
                ArrayList<String> json1 = new ArrayList<>();
                if (position == 0) {
                    for (int i = 0, counterStateDtosSize = counterStateDtos.size(); i < counterStateDtosSize; i++) {
                        CounterStateDto counterStateDto = counterStateDtos.get(i);
                        json1.add(gson.toJson(counterStateDto.id));
                    }
                } else if (position > counterStateDtos.size()) {
                    for (int i = 0, counterStateDtosSize = counterStateDtos.size(); i < counterStateDtosSize; i++) {
                        CounterStateDto counterStateDto = counterStateDtos.get(i);
                        if (counterStateDto.isMane)
                            json1.add(gson.toJson(counterStateDto.id));
                    }
                } else {
                    json1.add(gson.toJson(counterStateDtos.get(position - 1).id));
                }
                if (position == counterStateDtos.size() + 2) {
                    intent.putExtra(BundleEnum.READ_STATUS.getValue(), ReadStatusEnum.ALL_MANE_UNREAD.getValue());
                } else {
                    intent.putExtra(BundleEnum.READ_STATUS.getValue(), ReadStatusEnum.ALL_MANE.getValue());
                }
                intent.putExtra(BundleEnum.IS_MANE.getValue(), json1);
                MyApplication.POSITION = 1;
                startActivity(intent);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    void getBundle() {
        activity = getActivity();
        if (getArguments() != null) {
            total = getArguments().getInt(BundleEnum.TOTAL.getValue());
            isMane = getArguments().getInt(BundleEnum.IS_MANE.getValue());
            counterStateDtos = new ArrayList<>(((ReportActivity) activity).getCounterStateDtos());
//            items = new String[];
            items = CounterStateDto.getCounterStateItems(counterStateDtos, new String[]
                    {getString(R.string.all_items),
                            getString(R.string.all_mane),
                            getString(R.string.all_mane_unread)});
        }
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
        isFirst = true;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding.imageViewTemporary.setImageDrawable(null);
        binding = null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        items = null;
        adapter = null;
        counterStateDtos = null;
    }
}