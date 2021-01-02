package com.leon.counter_reading.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import androidx.fragment.app.DialogFragment;

import com.leon.counter_reading.R;
import com.leon.counter_reading.databinding.FragmentSerialBinding;
import com.leon.counter_reading.enums.BundleEnum;
import com.leon.counter_reading.utils.MyDatabaseClient;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class TaviziFragment extends DialogFragment {
    String uuid;
    int position;
    FragmentSerialBinding binding;
    Context context;

    public TaviziFragment() {
    }

    public static TaviziFragment newInstance(String uuid, int position) {
        TaviziFragment fragment = new TaviziFragment();
        Bundle args = new Bundle();
        args.putString(BundleEnum.BILL_ID.getValue(), uuid);
        args.putInt(BundleEnum.POSITION.getValue(), position);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            uuid = getArguments().getString(BundleEnum.BILL_ID.getValue());
            position = getArguments().getInt(BundleEnum.POSITION.getValue());
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
        setOnButtonsClickListener();
    }

    void setOnButtonsClickListener() {
        binding.buttonClose.setOnClickListener(v -> dismiss());
        binding.buttonSubmit.setOnClickListener(v -> {
            String number = binding.editTextSerial.getText().toString();
            if (number.isEmpty()) {
                View view = binding.editTextSerial;
                binding.editTextSerial.setError(getString(R.string.error_empty));
                view.requestFocus();
            } else {
//                ((ReadingActivity) getActivity()).updateOnOffLoadByCounterSerial(position, number);
                MyDatabaseClient.getInstance(context).getMyDatabase().onOffLoadDao().
                        updateOnOffLoad(number, uuid);
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