package com.leon.counter_reading.fragments;

import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;

import androidx.fragment.app.DialogFragment;

import com.leon.counter_reading.R;
import com.leon.counter_reading.activities.ReadingActivity;
import com.leon.counter_reading.adapters.SpinnerCustomAdapter;
import com.leon.counter_reading.databinding.FragmentSearchBinding;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

public class SearchFragment extends DialogFragment {
    FragmentSearchBinding binding;
    int type;

    public SearchFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentSearchBinding.inflate(inflater, container, false);
        initialize();
        return binding.getRoot();
    }

    void initialize() {
        initializeSpinner();
        setOnButtonSearchClickListener();
    }

    void setOnButtonSearchClickListener() {
        binding.buttonSearch.setOnClickListener(v -> {
            if (type == 5) {
                ((ReadingActivity) Objects.requireNonNull(getActivity())).search(type, null);
                dismiss();
            } else {
                String key = binding.editTextSearch.getText().toString();
                if (key.isEmpty()) {
                    View view = binding.editTextSearch;
                    binding.editTextSearch.setError(getString(R.string.error_empty));
                    view.requestFocus();
                } else {
                    ((ReadingActivity) Objects.requireNonNull(getActivity())).search(type, key);
                    dismiss();
                }
            }
        });
    }

    void initializeSpinner() {
        ArrayList<String> items = new ArrayList<>(
                Arrays.asList(getResources().getStringArray(R.array.search_option)));
        SpinnerCustomAdapter adapter = new SpinnerCustomAdapter(getActivity(), items);
        binding.spinnerSearch.setAdapter(adapter);
        binding.spinnerSearch.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                type = position;
                if (position == 3)
                    binding.editTextSearch.setInputType(InputType.TYPE_CLASS_TEXT);
                else
                    binding.editTextSearch.setInputType(InputType.TYPE_CLASS_NUMBER);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    @Override
    public void onResume() {
        WindowManager.LayoutParams params = Objects.requireNonNull(getDialog()).getWindow().getAttributes();
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
        params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        getDialog().getWindow().setAttributes(params);
        super.onResume();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}