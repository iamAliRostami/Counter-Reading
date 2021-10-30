package com.leon.counter_reading.fragments;

import static com.leon.counter_reading.utils.MakeNotification.makeRing;

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
import com.leon.counter_reading.enums.NotificationType;
import com.leon.counter_reading.helpers.MyApplication;
import com.leon.counter_reading.utils.DifferentCompanyManager;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class AhadFragment extends DialogFragment {

    private FragmentAhadBinding binding;
    private String uuid;
    private Context context;

    public AhadFragment() {
    }

    public static AhadFragment newInstance(String uuid) {
        AhadFragment fragment = new AhadFragment();
        Bundle args = new Bundle();
        args.putString(BundleEnum.BILL_ID.getValue(), uuid);
        fragment.setArguments(args);
        fragment.setCancelable(false);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            uuid = getArguments().getString(BundleEnum.BILL_ID.getValue());
            getArguments().clear();
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
        makeRing(context, NotificationType.OTHER);
        setOnButtonClickListener();
        binding.editTextAhad1.setHint(DifferentCompanyManager.getAhad1(DifferentCompanyManager.getActiveCompanyName()));
        binding.editTextAhad2.setHint(DifferentCompanyManager.getAhad2(DifferentCompanyManager.getActiveCompanyName()));
    }

    void setOnButtonClickListener() {
        binding.buttonClose.setOnClickListener(v -> dismiss());
        binding.buttonSubmit.setOnClickListener(v -> {
            int asli = 0, fari = 0;
            boolean cancel = false;
            if (binding.editTextAhad1.getText().toString().isEmpty() &&
                    binding.editTextAhad2.getText().toString().isEmpty()) {
                binding.editTextAhad2.setError(getString(R.string.error_empty));
                binding.editTextAhad2.setError(getString(R.string.error_empty));
                View view = binding.editTextAhad1;
                view.requestFocus();
                cancel = true;
            } else if (!binding.editTextAhad1.getText().toString().isEmpty() &&
                    !binding.editTextAhad2.getText().toString().isEmpty()) {
                asli = Integer.parseInt(binding.editTextAhad1.getText().toString());
                fari = Integer.parseInt(binding.editTextAhad2.getText().toString());
            } else {
                if (!binding.editTextAhad1.getText().toString().isEmpty()) {
                    asli = Integer.parseInt(binding.editTextAhad1.getText().toString());
                } else if (!binding.editTextAhad2.getText().toString().isEmpty()) {
                    fari = Integer.parseInt(binding.editTextAhad2.getText().toString());
                }
            }
            if (!cancel) {
                MyApplication.getApplicationComponent().MyDatabase()
                        .onOffLoadDao().updateOnOffLoad(asli, fari, uuid);
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