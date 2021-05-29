package com.leon.counter_reading.fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.leon.counter_reading.R;
import com.leon.counter_reading.activities.LoginActivity;
import com.leon.counter_reading.databinding.FragmentSettingChangePasswordBinding;
import com.leon.counter_reading.enums.DialogType;
import com.leon.counter_reading.enums.ProgressType;
import com.leon.counter_reading.enums.SharedReferenceKeys;
import com.leon.counter_reading.enums.SharedReferenceNames;
import com.leon.counter_reading.infrastructure.IAbfaService;
import com.leon.counter_reading.infrastructure.ICallback;
import com.leon.counter_reading.infrastructure.ICallbackError;
import com.leon.counter_reading.infrastructure.ICallbackIncomplete;
import com.leon.counter_reading.infrastructure.ISharedPreferenceManager;
import com.leon.counter_reading.tables.PasswordInfo;
import com.leon.counter_reading.tables.SimpleResponse;
import com.leon.counter_reading.utils.CustomDialog;
import com.leon.counter_reading.utils.CustomErrorHandling;
import com.leon.counter_reading.utils.CustomToast;
import com.leon.counter_reading.utils.HttpClientWrapper;
import com.leon.counter_reading.utils.NetworkHelper;
import com.leon.counter_reading.utils.SharedPreferenceManager;

import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;

public class SettingChangePasswordFragment extends Fragment {
    FragmentSettingChangePasswordBinding binding;
    Activity activity;

    public SettingChangePasswordFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = com.leon.counter_reading.databinding.FragmentSettingChangePasswordBinding.inflate(inflater, container, false);
        activity = getActivity();
        initialize();
        return binding.getRoot();
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    void initialize() {
        binding.imageViewPassword.setImageDrawable(
                ContextCompat.getDrawable(activity, R.drawable.img_change_password));
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
                if (binding.editTextNewPassword.getText().toString().equals(binding.editTextNewPasswordConfirm.getText().toString()))
                    attemptChangePassword();
                else {
                    binding.editTextNewPassword.setError(getString(R.string.password_and_confirm_not_same));
                    binding.editTextNewPasswordConfirm.setError(getString(R.string.password_and_confirm_not_same));
                    new CustomToast().error(getString(R.string.password_and_confirm_not_same));
                }
            } else
                view.requestFocus();
        });
    }

    void attemptChangePassword() {
        ISharedPreferenceManager sharedPreferenceManager = new SharedPreferenceManager(activity, SharedReferenceNames.ACCOUNT.getValue());
        Retrofit retrofit = NetworkHelper.getInstance(sharedPreferenceManager.getStringData(SharedReferenceKeys.TOKEN.getValue()));
        IAbfaService iAbfaService = retrofit.create(IAbfaService.class);
        PasswordInfo passwordInfo = new PasswordInfo(binding.editTextOldPassword.getText().toString(),
                binding.editTextNewPassword.getText().toString(),
                binding.editTextNewPasswordConfirm.getText().toString());
        Call<SimpleResponse> call = iAbfaService.changePassword(passwordInfo);
        HttpClientWrapper.callHttpAsync(call, ProgressType.SHOW.getValue(), activity, new ChangePassword(),
                new ChangePasswordIncomplete(), new ChangePasswordError());
    }

    class ChangePassword implements ICallback<SimpleResponse> {
        @Override
        public void execute(Response<SimpleResponse> response) {
            if (response.body() != null)
                new CustomToast().success(response.body().message, Toast.LENGTH_LONG);
            Intent intent = new Intent(activity, LoginActivity.class);
            startActivity(intent);
            activity.finish();
        }
    }

    class ChangePasswordIncomplete implements ICallbackIncomplete<SimpleResponse> {
        @Override
        public void executeIncomplete(Response<SimpleResponse> response) {
            CustomErrorHandling customErrorHandlingNew = new CustomErrorHandling(activity);
            String error = customErrorHandlingNew.getErrorMessageDefault(response);
            if (response.code() == 400 && response.errorBody() != null) {
                try {
                    JSONObject jObjError = new JSONObject(response.errorBody().string());
                    error = jObjError.getString("message");
                    new CustomToast().error(error, Toast.LENGTH_LONG);
                } catch (Exception e) {
                    Log.e("error", e.toString());
                }
            } else
                new CustomDialog(DialogType.Yellow, activity, error,
                        activity.getString(R.string.dear_user),
                        activity.getString(R.string.change_password),
                        activity.getString(R.string.accepted));
        }
    }

    class ChangePasswordError implements ICallbackError {
        @Override
        public void executeError(Throwable t) {
            CustomErrorHandling customErrorHandlingNew = new CustomErrorHandling(activity);
            String error = customErrorHandlingNew.getErrorMessageTotal(t);
            new CustomDialog(DialogType.YellowRedirect, activity, error,
                    activity.getString(R.string.dear_user),
                    activity.getString(R.string.change_password),
                    activity.getString(R.string.accepted));
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding.imageViewPassword.setImageDrawable(null);
    }
}