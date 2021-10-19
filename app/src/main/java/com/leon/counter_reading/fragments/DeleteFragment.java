package com.leon.counter_reading.fragments;

import static com.leon.counter_reading.utils.MakeNotification.makeRing;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import androidx.fragment.app.DialogFragment;

import com.leon.counter_reading.MyApplication;
import com.leon.counter_reading.R;
import com.leon.counter_reading.databinding.FragmentDeleteBinding;
import com.leon.counter_reading.enums.BundleEnum;
import com.leon.counter_reading.enums.NotificationType;
import com.leon.counter_reading.enums.SharedReferenceKeys;
import com.leon.counter_reading.infrastructure.ISharedPreferenceManager;
import com.leon.counter_reading.utils.Crypto;
import com.leon.counter_reading.utils.CustomToast;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class DeleteFragment extends DialogFragment {
    String id;
    FragmentDeleteBinding binding;
    Activity activity;

    public DeleteFragment() {
    }

    public static DeleteFragment newInstance(String id) {
        DeleteFragment fragment = new DeleteFragment();
        Bundle args = new Bundle();
        args.putString(BundleEnum.BILL_ID.getValue(), id);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            id = getArguments().getString(BundleEnum.BILL_ID.getValue());
            getArguments().clear();
        }
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentDeleteBinding.inflate(inflater, container, false);
        activity = getActivity();
        initialize();
        return binding.getRoot();
    }

    void initialize() {
        makeRing(activity, NotificationType.SAVE);
        setOnImageViewPasswordClickListener();
        setOnButtonsClickListener();
    }

    void setOnButtonsClickListener() {
        binding.buttonSubmit.setOnClickListener(v -> {
            View view;
            if (binding.editTextUsername.getText().toString().isEmpty()) {
                binding.editTextUsername.setError(getString(R.string.error_empty));
                view = binding.editTextUsername;
                view.requestFocus();
            } else if (binding.editTextPassword.getText().toString().isEmpty()) {
                binding.editTextPassword.setError(getString(R.string.error_empty));
                view = binding.editTextPassword;
                view.requestFocus();
            } else {
                ISharedPreferenceManager sharedPreferenceManager = MyApplication.getApplicationComponent().SharedPreferenceModel();
                String password = binding.editTextPassword.getText().toString();
                String username = binding.editTextUsername.getText().toString();
                if (sharedPreferenceManager.getStringData(
                        SharedReferenceKeys.USERNAME_TEMP.getValue()).contains(username) &&
                        Crypto.decrypt(sharedPreferenceManager.getStringData(
                                SharedReferenceKeys.PASSWORD_TEMP.getValue())).contains(password)
                ) {
                    if (id.isEmpty()) {
                        MyApplication.getApplicationComponent().MyDatabase().
                                trackingDao().updateTrackingDtoByArchive(true, false);
                    } else {
                        MyApplication.getApplicationComponent().MyDatabase().
                                trackingDao().updateTrackingDtoByArchive(id, true, false);
                    }
                    Intent intent = activity.getIntent();
                    activity.finish();
                    startActivity(intent);
                } else {
                    new CustomToast().warning(getString(R.string.error_is_not_match));
                }
            }
        });
        binding.buttonClose.setOnClickListener(v -> dismiss());
    }

    void setOnImageViewPasswordClickListener() {
        binding.imageViewPassword.setOnClickListener(
                v -> binding.imageViewPassword.setOnClickListener(view -> {
                    if (binding.editTextPassword.getInputType() != InputType.TYPE_CLASS_TEXT) {
                        binding.editTextPassword.setInputType(InputType.TYPE_CLASS_TEXT);
                    } else
                        binding.editTextPassword.setInputType(InputType.TYPE_CLASS_TEXT |
                                InputType.TYPE_TEXT_VARIATION_PASSWORD);
                }));
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