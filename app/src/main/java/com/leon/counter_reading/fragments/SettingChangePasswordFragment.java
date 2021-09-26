package com.leon.counter_reading.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.leon.counter_reading.R;
import com.leon.counter_reading.databinding.FragmentSettingChangePasswordBinding;
import com.leon.counter_reading.utils.CustomToast;
import com.leon.counter_reading.utils.updating.ChangePassword;

import org.jetbrains.annotations.NotNull;

public class SettingChangePasswordFragment extends Fragment {
    private FragmentSettingChangePasswordBinding binding;
    private Activity activity;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentSettingChangePasswordBinding.inflate(inflater, container, false);
        activity = getActivity();
        initialize();
        return binding.getRoot();
    }

    private void initialize() {
        binding.imageViewPassword.setImageDrawable(ContextCompat
                .getDrawable(activity, R.drawable.img_change_password));
        setOnButtonChangePasswordClickListener();
    }

    void setOnButtonChangePasswordClickListener() {
        binding.buttonChangePassword.setOnClickListener(v -> {
            View view = null;
            boolean cancel = false;
            if (binding.editTextOldPassword.getText().length() < 1) {
                cancel = true;
                binding.editTextOldPassword.setError(getString(R.string.error_empty));
                view = binding.editTextOldPassword;
            }
            if (binding.editTextNewPassword.getText().length() < 1) {
                cancel = true;
                binding.editTextNewPassword.setError(getString(R.string.error_empty));
                view = binding.editTextNewPassword;
            }
            if (binding.editTextNewPasswordConfirm.getText().length() < 1) {
                cancel = true;
                binding.editTextNewPasswordConfirm.setError(getString(R.string.error_empty));
                view = binding.editTextNewPasswordConfirm;
            }
            if (!cancel) {
                if (binding.editTextNewPassword.getText().toString().equals(
                        binding.editTextNewPasswordConfirm.getText().toString()))
                    new ChangePassword(activity, binding.editTextOldPassword.getText().toString(),
                            binding.editTextNewPassword.getText().toString(),
                            binding.editTextNewPasswordConfirm.getText().toString());
                else {
                    binding.editTextNewPassword.setError(getString(R.string.password_and_confirm_not_same));
                    binding.editTextNewPasswordConfirm.setError(getString(R.string.password_and_confirm_not_same));
                    new CustomToast().error(getString(R.string.password_and_confirm_not_same));
                }
            } else
                view.requestFocus();
        });
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding.imageViewPassword.setImageDrawable(null);
    }
}