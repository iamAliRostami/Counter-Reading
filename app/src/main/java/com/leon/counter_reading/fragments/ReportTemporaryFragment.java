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
import com.leon.counter_reading.adapters.SpinnerCustomAdapter;
import com.leon.counter_reading.databinding.FragmentReportTemporaryBinding;
import com.leon.counter_reading.enums.BundleEnum;
import com.leon.counter_reading.enums.ReadStatusEnum;
import com.leon.counter_reading.tables.CounterStateDto;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class ReportTemporaryFragment extends Fragment {
    FragmentReportTemporaryBinding binding;
    ArrayList<CounterStateDto> counterStateDtos = new ArrayList<>();
    SpinnerCustomAdapter adapter;
    Activity activity;
    ArrayList<String> items = new ArrayList<>();
    int total, isMane;
    boolean isFirst = true;

    public ReportTemporaryFragment() {
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
        activity = getActivity();
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
        items.add(0, getString(R.string.all_items));
        adapter = new SpinnerCustomAdapter(activity, items);
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
                    for (CounterStateDto counterStateDto : counterStateDtos) {
                        String jsonTemp = gson.toJson(counterStateDto.moshtarakinId);
                        json1.add(jsonTemp);
                    }
                } else {
                    String jsonTemp = gson.toJson(counterStateDtos.get(position - 1).moshtarakinId);
                    json1.add(jsonTemp);
                }
                intent.putExtra(BundleEnum.IS_MANE.getValue(), json1);
                intent.putExtra(BundleEnum.READ_STATUS.getValue(), ReadStatusEnum.ALL_MANE.getValue());
                MyApplication.POSITION = 1;
                startActivity(intent);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    public static ReportTemporaryFragment newInstance(
            ArrayList<CounterStateDto> counterStateDtos, int total, int isMane) {
        ReportTemporaryFragment fragment = new ReportTemporaryFragment();
        fragment.setArguments(putBundle(counterStateDtos, total, isMane));
        return fragment;
    }

    void getBundle() {
        if (getArguments() != null) {
            Gson gson = new Gson();
            ArrayList<String> json1 = getArguments().getStringArrayList(
                    BundleEnum.COUNTER_STATE.getValue());
            for (String s : json1) {
                counterStateDtos.add(gson.fromJson(s, CounterStateDto.class));
                items.add(counterStateDtos.get(counterStateDtos.size() - 1).title);
            }
            total = getArguments().getInt(BundleEnum.TOTAL.getValue());
            isMane = getArguments().getInt(BundleEnum.IS_MANE.getValue());
        }
    }

    static Bundle putBundle(ArrayList<CounterStateDto> counterStateDtos, int total, int isMane) {
        Bundle args = new Bundle();
        Gson gson = new Gson();
        ArrayList<String> json1 = new ArrayList<>();
        for (CounterStateDto counterStateDto : counterStateDtos) {
            String jsonTemp = gson.toJson(counterStateDto);
            json1.add(jsonTemp);
        }
        args.putStringArrayList(BundleEnum.COUNTER_STATE.getValue(), json1);
        args.putInt(BundleEnum.TOTAL.getValue(), total);
        args.putInt(BundleEnum.IS_MANE.getValue(), isMane);
        return args;
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