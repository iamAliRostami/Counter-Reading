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
import com.leon.counter_reading.enums.SharedReferenceKeys;
import com.leon.counter_reading.enums.SharedReferenceNames;
import com.leon.counter_reading.infrastructure.IAbfaService;
import com.leon.counter_reading.infrastructure.ICallback;
import com.leon.counter_reading.infrastructure.ICallbackError;
import com.leon.counter_reading.infrastructure.ICallbackIncomplete;
import com.leon.counter_reading.infrastructure.ISharedPreferenceManager;
import com.leon.counter_reading.tables.LastInfo;
import com.leon.counter_reading.utils.CustomDialog;
import com.leon.counter_reading.utils.CustomErrorHandling;
import com.leon.counter_reading.utils.CustomFile;
import com.leon.counter_reading.utils.CustomToast;
import com.leon.counter_reading.utils.HttpClientWrapper;
import com.leon.counter_reading.utils.NetworkHelper;
import com.leon.counter_reading.utils.SharedPreferenceManager;

import org.jetbrains.annotations.NotNull;

import java.text.DecimalFormat;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;

public class SettingUpdateFragment extends Fragment {
    FragmentSettingUpdateBinding binding;
    Activity activity;
    boolean firstTime = true;
    int versionCode;
    ISharedPreferenceManager sharedPreferenceManager;

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
        activity = getActivity();
        initialize();
        return binding.getRoot();
    }

//    @SuppressLint("UseCompatLoadingForDrawables")
    void initialize() {
        sharedPreferenceManager = new SharedPreferenceManager(activity, SharedReferenceNames.ACCOUNT.getValue());
        binding.imageViewUpdate.
                setImageDrawable(ContextCompat.getDrawable(activity, R.drawable.img_update));
        setOnButtonReceiveClickListener();
    }

    void updateInfo() {
        Retrofit retrofit = NetworkHelper.getInstance(sharedPreferenceManager.getStringData(SharedReferenceKeys.TOKEN.getValue()));
        IAbfaService iAbfaService = retrofit.create(IAbfaService.class);
        Call<LastInfo> call = iAbfaService.getLastInfo();
        HttpClientWrapper.callHttpAsync(call, ProgressType.SHOW.getValue(), activity,
                new UpdateInfo(), new UpdateInfoIncomplete(), new UpdateError());
    }

    void getUpdateFile() {
//        activity.runOnUiThread(() -> binding.progressBar.setVisibility(View.VISIBLE));
        Retrofit retrofit = NetworkHelper.getInstance(sharedPreferenceManager.getStringData(SharedReferenceKeys.TOKEN.getValue()));
        IAbfaService iAbfaService = retrofit.create(IAbfaService.class);
        Call<ResponseBody> call = iAbfaService.getLastApk();
        HttpClientWrapper.callHttpAsyncProgressDismiss(call, ProgressType.SHOW_CANCELABLE.getValue(),
                activity, new Update(), new UpdateIncomplete(), new UpdateError());
    }

    void setOnButtonReceiveClickListener() {
        binding.buttonReceive.setOnClickListener(v -> {
            if (firstTime) {
                updateInfo();
            } else if (BuildConfig.VERSION_CODE >= versionCode) {
                new CustomToast().success(getString(R.string.you_are_updated));
            } else {
                getUpdateFile();
            }
        });
    }

//    @SuppressLint("SetTextI18n")
    class UpdateInfo implements ICallback<LastInfo> {
        @Override
        public void execute(Response<LastInfo> response) {
            if (response.body() != null) {
                activity.runOnUiThread(() -> {
                    binding.textViewVersion.setText(response.body().versionName);
                    ISharedPreferenceManager sharedPreferenceManager = new SharedPreferenceManager(
                            activity, SharedReferenceNames.ACCOUNT.getValue());
                    sharedPreferenceManager.putData(SharedReferenceKeys.DATE.getValue(),
                            response.body().uploadDateJalali);
                    binding.textViewDate.setText(response.body().uploadDateJalali);
                    binding.textViewPossibility.setText(response.body().description);
                    float size = (float) response.body().sizeInByte / (1028 * 1028);
                    binding.textViewSize.setText(new DecimalFormat("###.##").format(size).
                            concat(getString(R.string.mega_byte)));

//                    binding.linearLayoutUpdate.setVisibility(View.VISIBLE);
                    binding.linearLayout1.setVisibility(View.VISIBLE);
                    binding.linearLayout2.setVisibility(View.VISIBLE);
                    binding.linearLayout3.setVisibility(View.VISIBLE);
                    binding.linearLayout4.setVisibility(View.VISIBLE);
//                    binding.progressBar.setVisibility(View.GONE);
                    binding.buttonReceive.setText(getString(R.string.receive_file));
                });
                versionCode = response.body().versionCode;
                firstTime = false;
            }
        }
    }

    class UpdateInfoIncomplete implements ICallbackIncomplete<LastInfo> {
        @Override
        public void executeIncomplete(Response<LastInfo> response) {
            CustomErrorHandling customErrorHandlingNew = new CustomErrorHandling(activity);
            String error = customErrorHandlingNew.getErrorMessageDefault(response);
            new CustomDialog(DialogType.Yellow, activity, error,
                    activity.getString(R.string.dear_user),
                    activity.getString(R.string.update),
                    activity.getString(R.string.accepted));
        }
    }

    class Update implements ICallback<ResponseBody> {
        @Override
        public void execute(Response<ResponseBody> response) {
            if (!CustomFile.writeResponseApkToDisk(response.body(), activity))
                activity.runOnUiThread(() ->
                        new CustomToast().warning(activity.getString(R.string.error_update)));
//            binding.progressBar.setVisibility(View.GONE);
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
//            binding.progressBar.setVisibility(View.GONE);
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
//            binding.progressBar.setVisibility(View.GONE);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding.imageViewUpdate.setImageDrawable(null);
        binding = null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (HttpClientWrapper.call != null)
            HttpClientWrapper.call.cancel();
    }
}