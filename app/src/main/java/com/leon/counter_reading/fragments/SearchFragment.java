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
import com.leon.counter_reading.enums.SearchTypeEnum;
import com.leon.counter_reading.utils.DifferentCompanyManager;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class SearchFragment extends DialogFragment {
    private FragmentSearchBinding binding;
    private int type;

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
        binding.editTextSearch.requestFocus();
    }

    void setOnButtonSearchClickListener() {
        binding.buttonSearch.setOnClickListener(v -> {
            if (type == SearchTypeEnum.All.getValue()) {
                ((ReadingActivity) requireActivity()).search(type, null, false);
                dismiss();
            } else {
                String key = binding.editTextSearch.getText().toString();
                if (key.isEmpty()) {
                    View view = binding.editTextSearch;
                    binding.editTextSearch.setError(getString(R.string.error_empty));
                    view.requestFocus();
                } else {
                    ((ReadingActivity) requireActivity()).search(type, key, binding.checkBoxGoToPage.isChecked());
                    dismiss();
                }
            }
        });
    }

    void initializeSpinner() {
        String[] items = getResources().getStringArray(R.array.search_option);
        items[1] = DifferentCompanyManager.getSecondSearchItem(DifferentCompanyManager.getActiveCompanyName());
        SpinnerCustomAdapter adapter = new SpinnerCustomAdapter(getActivity(), items);
        binding.spinnerSearch.setAdapter(adapter);
        binding.spinnerSearch.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                type = position;
                if (position >= SearchTypeEnum.BARCODE.getValue())
                    binding.editTextSearch.setVisibility(View.GONE);
                else binding.editTextSearch.setVisibility(View.VISIBLE);

                if (position >= SearchTypeEnum.NAME.getValue()/* || position == 4 || position == 5*/)
                    binding.checkBoxGoToPage.setVisibility(View.GONE);
                else binding.checkBoxGoToPage.setVisibility(View.VISIBLE);

                if (position == SearchTypeEnum.NAME.getValue())
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