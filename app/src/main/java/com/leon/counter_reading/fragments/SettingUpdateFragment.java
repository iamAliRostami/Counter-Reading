package com.leon.counter_reading.fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.leon.counter_reading.BuildConfig;
import com.leon.counter_reading.R;
import com.leon.counter_reading.databinding.FragmentSettingUpdateBinding;
import com.leon.counter_reading.enums.DialogType;
import com.leon.counter_reading.enums.ProgressType;
import com.leon.counter_reading.infrastructure.IAbfaService;
import com.leon.counter_reading.infrastructure.ICallback;
import com.leon.counter_reading.infrastructure.ICallbackError;
import com.leon.counter_reading.infrastructure.ICallbackIncomplete;
import com.leon.counter_reading.utils.CustomDialog;
import com.leon.counter_reading.utils.CustomErrorHandling;
import com.leon.counter_reading.utils.CustomFile;
import com.leon.counter_reading.utils.CustomToast;
import com.leon.counter_reading.utils.HttpClientWrapper;
import com.leon.counter_reading.utils.NetworkHelper;

import org.jetbrains.annotations.NotNull;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;

public class SettingUpdateFragment extends Fragment {

    FragmentSettingUpdateBinding binding;
    Activity activity;

    public SettingUpdateFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentSettingUpdateBinding.inflate(inflater, container, false);
        initialize();
        return binding.getRoot();
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    void initialize() {
        activity = getActivity();
        binding.imageViewUpdate.setImageDrawable(ContextCompat.getDrawable(activity, R.drawable.img_update));
        binding.textViewCurrentVersion.setText(BuildConfig.VERSION_NAME);
        setOnButtonReceiveClickListener();
    }

    void setOnButtonReceiveClickListener() {
        binding.buttonReceive.setOnClickListener(v -> {
            Retrofit retrofit = NetworkHelper.getInstance();
            IAbfaService iAbfaService = retrofit.create(IAbfaService.class);
            Call<ResponseBody> call = iAbfaService.getLastApk();
            HttpClientWrapper.callHttpAsyncProgressDismiss(call, ProgressType.SHOW.getValue(),
                    activity, new Update(), new UpdateIncomplete(), new UpdateError());
        });
    }

    class Update implements ICallback<ResponseBody> {
        @Override
        public void execute(Response<ResponseBody> response) {
            if (!CustomFile.writeResponseBodyToDisk(response.body(), activity))
                activity.runOnUiThread(() -> new CustomToast().warning(activity.getString(R.string.error_update)));
        }
    }

    class UpdateIncomplete implements ICallbackIncomplete<ResponseBody> {
        @Override
        public void executeIncomplete(Response<ResponseBody> response) {
            CustomErrorHandling customErrorHandlingNew = new CustomErrorHandling(activity);
            String error = customErrorHandlingNew.getErrorMessageDefault(response);
            new CustomDialog(DialogType.Yellow, activity, error,
                    activity.getString(R.string.dear_user),
                    activity.getString(R.string.update),
                    activity.getString(R.string.accepted));
        }
    }

    class UpdateError implements ICallbackError {
        @Override
        public void executeError(Throwable t) {
            CustomErrorHandling customErrorHandlingNew = new CustomErrorHandling(activity);
            String error = customErrorHandlingNew.getErrorMessageTotal(t);
            new CustomDialog(DialogType.Red, getContext(), error,
                    activity.getString(R.string.dear_user),
                    activity.getString(R.string.update),
                    activity.getString(R.string.accepted));
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding.imageViewUpdate.setImageDrawable(null);
    }
}