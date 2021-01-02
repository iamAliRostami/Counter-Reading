package com.leon.counter_reading.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import androidx.fragment.app.DialogFragment;

import com.leon.counter_reading.R;
import com.leon.counter_reading.databinding.FragmentAhadBinding;
import com.leon.counter_reading.enums.BundleEnum;
import com.leon.counter_reading.utils.MyDatabaseClient;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class AhadFragment extends DialogFragment {

    FragmentAhadBinding binding;
    String uuid;
    Context context;

    public AhadFragment() {
    }

    public static AhadFragment newInstance(String uuid) {
        AhadFragment fragment = new AhadFragment();
        Bundle args = new Bundle();
        args.putString(BundleEnum.BILL_ID.getValue(), uuid);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            uuid = getArguments().getString(BundleEnum.BILL_ID.getValue());
        }
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentAhadBinding.inflate(inflater, container, false);
        context = getActivity();
        initialize();
        return binding.getRoot();
    }

    void initialize() {
        setOnButtonClickListener();
    }

    void setOnButtonClickListener() {
        binding.buttonClose.setOnClickListener(v -> dismiss());
        binding.buttonSubmit.setOnClickListener(v -> {
            String asli = "";
            String fari = "";
            boolean cancel = false;
            if (binding.editTextAhadAsli.getText().toString().isEmpty() &&
                    binding.editTextAhadFari.getText().toString().isEmpty()) {
                binding.editTextAhadFari.setError(getString(R.string.error_empty));
                binding.editTextAhadFari.setError(getString(R.string.error_empty));
                View view = binding.editTextAhadAsli;
                view.requestFocus();
                cancel = true;
            } else if (!binding.editTextAhadAsli.getText().toString().isEmpty() &&
                    !binding.editTextAhadFari.getText().toString().isEmpty()) {
                asli = binding.editTextAhadAsli.getText().toString();
                fari = binding.editTextAhadFari.getText().toString();
            } else {
                if (!binding.editTextAhadAsli.getText().toString().isEmpty()) {
                    asli = binding.editTextAhadAsli.getText().toString();
                } else if (!binding.editTextAhadFari.getText().toString().isEmpty()) {
                    fari = binding.editTextAhadFari.getText().toString();
                }
            }
            if (!cancel) {
                MyDatabaseClient.getInstance(context).getMyDatabase().onOffLoadDao().
                        updateOnOffLoad(asli, fari, uuid);
                dismiss();
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
}