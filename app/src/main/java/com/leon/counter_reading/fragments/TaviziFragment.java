package com.leon.counter_reading.fragments;

import static com.leon.counter_reading.utils.MakeNotification.makeRing;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import androidx.fragment.app.DialogFragment;

import com.leon.counter_reading.helpers.MyApplication;
import com.leon.counter_reading.R;
import com.leon.counter_reading.databinding.FragmentSerialBinding;
import com.leon.counter_reading.enums.BundleEnum;
import com.leon.counter_reading.enums.NotificationType;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class TaviziFragment extends DialogFragment {
    private String uuid;
    private FragmentSerialBinding binding;
    private Context context;

    public static TaviziFragment newInstance(String uuid) {
        TaviziFragment fragment = new TaviziFragment();
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
        binding = FragmentSerialBinding.inflate(inflater, container, false);
        context = getActivity();
        initialize();
        return binding.getRoot();
    }

    void initialize() {
        makeRing(context, NotificationType.OTHER);
        setOnButtonsClickListener();
    }

    void setOnButtonsClickListener() {
        binding.buttonClose.setOnClickListener(v -> dismiss());
        binding.buttonSubmit.setOnClickListener(v -> {
            String number = binding.editTextSerial.getText().toString();
            if (number.length() < 3) {
                View view = binding.editTextSerial;
                binding.editTextSerial.setError(getString(R.string.error_format));
                view.requestFocus();
            } else {
                MyApplication.getApplicationComponent().MyDatabase()
                        .onOffLoadDao().updateOnOffLoad(number, uuid);
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}