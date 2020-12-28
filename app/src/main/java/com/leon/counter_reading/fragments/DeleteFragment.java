package com.leon.counter_reading.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import androidx.fragment.app.DialogFragment;

import com.leon.counter_reading.R;
import com.leon.counter_reading.databinding.FragmentDeleteBinding;
import com.leon.counter_reading.enums.BundleEnum;
import com.leon.counter_reading.enums.SharedReferenceKeys;
import com.leon.counter_reading.enums.SharedReferenceNames;
import com.leon.counter_reading.infrastructure.ISharedPreferenceManager;
import com.leon.counter_reading.utils.Crypto;
import com.leon.counter_reading.utils.CustomToast;
import com.leon.counter_reading.utils.MyDatabaseClient;
import com.leon.counter_reading.utils.SharedPreferenceManager;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class DeleteFragment extends DialogFragment {
    int zoneId;
    FragmentDeleteBinding binding;
    Activity activity;

    public DeleteFragment() {
    }

    public static DeleteFragment newInstance(int zoneId) {
        DeleteFragment fragment = new DeleteFragment();
        Bundle args = new Bundle();
        args.putInt(BundleEnum.ZONE_ID.getValue(), zoneId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            zoneId = getArguments().getInt(BundleEnum.ZONE_ID.getValue());
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
                ISharedPreferenceManager sharedPreferenceManager =
                        new SharedPreferenceManager(activity, SharedReferenceNames.ACCOUNT.getValue());
                String password = binding.editTextPassword.getText().toString();
                String username = binding.editTextUsername.getText().toString();
                if (sharedPreferenceManager.getStringData(
                        SharedReferenceKeys.USERNAME_TEMP.getValue()).contains(username) &&
                        Crypto.decrypt(sharedPreferenceManager.getStringData(
                                SharedReferenceKeys.PASSWORD.getValue())).contains(password)
                ) {
                    if (zoneId == 0) {
                        MyDatabaseClient.getInstance(activity).getMyDatabase().
                                readingConfigDefaultDao().updateReadingConfigDefaultByArchive(true);
                    } else {
                        MyDatabaseClient.getInstance(activity).getMyDatabase().
                                readingConfigDefaultDao().updateReadingConfigDefaultByArchive(zoneId, true);
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