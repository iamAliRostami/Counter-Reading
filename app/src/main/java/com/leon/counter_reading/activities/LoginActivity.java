package com.leon.counter_reading.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Debug;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.auth0.android.jwt.JWT;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.leon.counter_reading.BuildConfig;
import com.leon.counter_reading.R;
import com.leon.counter_reading.databinding.ActivityLoginBinding;
import com.leon.counter_reading.enums.DialogType;
import com.leon.counter_reading.enums.ProgressType;
import com.leon.counter_reading.enums.SharedReferenceKeys;
import com.leon.counter_reading.enums.SharedReferenceNames;
import com.leon.counter_reading.infrastructure.IAbfaService;
import com.leon.counter_reading.infrastructure.ICallback;
import com.leon.counter_reading.infrastructure.ICallbackError;
import com.leon.counter_reading.infrastructure.ICallbackIncomplete;
import com.leon.counter_reading.infrastructure.ISharedPreferenceManager;
import com.leon.counter_reading.tables.LoginFeedBack;
import com.leon.counter_reading.tables.LoginInfo;
import com.leon.counter_reading.utils.Crypto;
import com.leon.counter_reading.utils.CustomDialog;
import com.leon.counter_reading.utils.CustomErrorHandling;
import com.leon.counter_reading.utils.CustomToast;
import com.leon.counter_reading.utils.HttpClientWrapper;
import com.leon.counter_reading.utils.NetworkHelper;
import com.leon.counter_reading.utils.PermissionManager;
import com.leon.counter_reading.utils.SharedPreferenceManager;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;

import static android.os.Build.UNKNOWN;
import static com.leon.counter_reading.MyApplication.CARRIER_PRIVILEGE_STATUS;

public class LoginActivity extends AppCompatActivity {
    ISharedPreferenceManager sharedPreferenceManager;
    ActivityLoginBinding binding;
    Activity activity;
    String username, password;
    int counter = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        activity = this;
        checkReadPhoneStatePermission();
    }

    void checkReadPhoneStatePermission() {
        if (ActivityCompat.checkSelfPermission(activity,
                Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
            initialize();
        } else {
            askReadPhoneStatusPermission();
        }
    }

    void askReadPhoneStatusPermission() {
        PermissionListener permissionlistener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                new CustomToast().info(activity.getString(R.string.access_granted));
                checkReadPhoneStatePermission();
            }

            @Override
            public void onPermissionDenied(ArrayList<String> deniedPermissions) {
                PermissionManager.forceClose(activity);
            }
        };
        new TedPermission(activity)
                .setPermissionListener(permissionlistener)
                .setRationaleMessage(activity.getString(R.string.confirm_permission))
                .setRationaleConfirmText(activity.getString(R.string.allow_permission))
                .setDeniedMessage(activity.getString(R.string.if_reject_permission))
                .setDeniedCloseButtonText(activity.getString(R.string.close))
                .setGotoSettingButtonText(activity.getString(R.string.allow_permission))
                .setPermissions(Manifest.permission.READ_PHONE_STATE).check();
    }

    void initialize() {
        binding.textViewVersion.setText(getString(R.string.version).concat(" ").concat(getAndroidVersion())
                .concat(" *** ").concat(BuildConfig.VERSION_NAME));
        loadPreference();
        binding.imageViewPassword.setImageResource(R.drawable.img_password);
        binding.imageViewLogo.setImageResource(R.drawable.img_login_logo);
        binding.imageViewPerson.setImageResource(R.drawable.img_profile);
        binding.imageViewUsername.setImageResource(R.drawable.img_user);
        setOnButtonLoginClickListener();
        setOnButtonLongCLickListener();
        setOnImageViewPasswordClickListener();
        setEditTextUsernameOnFocusChangeListener();
        setEditTextPasswordOnFocusChangeListener();
    }

    String getAndroidVersion() {
        String release = Build.VERSION.RELEASE;
        int sdkVersion = Build.VERSION.SDK_INT;
        return "Android SDK: " + sdkVersion + " (" + release + ")";
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    void setEditTextUsernameOnFocusChangeListener() {
        binding.editTextUsername.setOnFocusChangeListener((view, b) -> {
            binding.editTextUsername.setHint("");
            if (b) {
                binding.linearLayoutUsername.setBackground(ContextCompat.getDrawable(
                        getApplicationContext(), R.drawable.border_black_2));
                binding.editTextPassword.setTextColor(
                        ContextCompat.getColor(activity, R.color.black));
            } else {
                binding.linearLayoutUsername.setBackground(ContextCompat.getDrawable(
                        getApplicationContext(), R.drawable.border_gray_2));
                binding.editTextPassword.setTextColor(
                        ContextCompat.getColor(activity, R.color.gray));
            }
        });
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    void setEditTextPasswordOnFocusChangeListener() {
        binding.editTextPassword.setOnFocusChangeListener((view, b) -> {
            binding.editTextPassword.setHint("");
            if (b) {
                binding.linearLayoutPassword.setBackground(ContextCompat.getDrawable(
                        getApplicationContext(), R.drawable.border_black_2));
                binding.editTextPassword.setTextColor(ContextCompat.getColor(
                        getApplicationContext(), R.color.black));
            } else {
                binding.linearLayoutPassword.setBackground(ContextCompat.getDrawable(
                        getApplicationContext(), R.drawable.border_gray_2));
                binding.editTextPassword.setTextColor(ContextCompat.getColor(
                        getApplicationContext(), R.color.gray));
            }
        });
    }

    void setOnImageViewPasswordClickListener() {
        binding.imageViewPassword.setOnClickListener(v ->
                binding.imageViewPassword.setOnClickListener(view -> {
                    if (binding.editTextPassword.getInputType() != InputType.TYPE_CLASS_TEXT) {
                        binding.editTextPassword.setInputType(InputType.TYPE_CLASS_TEXT);
                    } else
                        binding.editTextPassword.setInputType(InputType.TYPE_CLASS_TEXT |
                                InputType.TYPE_TEXT_VARIATION_PASSWORD);
                }));
    }

    void setOnButtonLongCLickListener() {
        binding.buttonLogin.setOnLongClickListener(v -> {
            attempt(false);
            return false;
        });
    }

    void setOnButtonLoginClickListener() {
        binding.buttonLogin.setOnClickListener(v -> attempt(true));
    }

    void attempt(boolean isLogin) {
        View view;
        boolean cancel = false;
        if (binding.editTextUsername.getText().length() < 1) {
            binding.editTextUsername.setError(getString(R.string.error_empty));
            view = binding.editTextUsername;
            view.requestFocus();
            cancel = true;
        }
        if (!cancel && binding.editTextPassword.getText().length() < 1) {
            binding.editTextPassword.setError(getString(R.string.error_empty));
            view = binding.editTextPassword;
            view.requestFocus();
            cancel = true;
        }
        if (!cancel) {
            username = binding.editTextUsername.getText().toString();
            password = binding.editTextPassword.getText().toString();
            if (isLogin) {
                counter++;
                if (counter < 4)
                    attemptLogin();
                else
                    offlineLogin();
            } else {
                attemptRegister();
            }
        }
    }

    void offlineLogin() {
        if (sharedPreferenceManager.getStringData(SharedReferenceKeys.USERNAME.getValue()).equals(username) &&
                Crypto.decrypt(sharedPreferenceManager.getStringData(SharedReferenceKeys.PASSWORD.getValue()))
                        .equals(password)) {
            new CustomToast().info(getString(R.string.check_connection), Toast.LENGTH_LONG);
            Intent intent = new Intent(activity, HomeActivity.class);
            startActivity(intent);
            finish();
        } else {
            new CustomToast().warning(getString(R.string.error_is_not_match), Toast.LENGTH_LONG);
        }
        counter = 0;
    }

    void attemptRegister() {
        Retrofit retrofit = NetworkHelper.getInstance();
        final IAbfaService iAbfaService = retrofit.create(IAbfaService.class);
        Call<LoginFeedBack> call = iAbfaService.register(new LoginInfo(username, password, getSerial()));
        HttpClientWrapper.callHttpAsync(call, ProgressType.SHOW.getValue(), this,
                new Register(), new GetErrorIncomplete(), new GetError());
    }

    void attemptLogin() {
        Retrofit retrofit = NetworkHelper.getInstance();
        final IAbfaService iAbfaService = retrofit.create(IAbfaService.class);
        Call<LoginFeedBack> call = iAbfaService.login(new LoginInfo(username, password, getSerial()));
        HttpClientWrapper.callHttpAsync(call, ProgressType.SHOW.getValue(), this,
                new Login(), new GetErrorIncomplete(), new GetError());
    }

    @SuppressLint("HardwareIds")
    String getSerial() {
        String serial = Build.SERIAL;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (hasCarrierPrivileges())
                serial = Build.getSerial();
        }
        if (serial.equals(UNKNOWN))
            serial = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        return serial;
    }

    boolean hasCarrierPrivileges() {
        TelephonyManager tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        boolean isCarrier = tm.hasCarrierPrivileges();
        if (!isCarrier) {
            int hasPermission = ActivityCompat.checkSelfPermission(getApplicationContext(),
                    "android.permission.READ_PRIVILEGED_PHONE_STATE");
            if (hasPermission != PackageManager.PERMISSION_GRANTED) {
                if (!shouldShowRequestPermissionRationale("android.permission.READ_PRIVILEGED_PHONE_STATE")) {
                    ActivityCompat.requestPermissions(LoginActivity.this, new String[]{
                            "android.permission.READ_PRIVILEGED_PHONE_STATE"}, CARRIER_PRIVILEGE_STATUS);
                }
            }
        }
        return isCarrier;
    }

    void savePreference(LoginFeedBack loginFeedBack) {
        sharedPreferenceManager.putData(
                SharedReferenceKeys.DISPLAY_NAME.getValue(), loginFeedBack.displayName);
        sharedPreferenceManager.putData(
                SharedReferenceKeys.USER_CODE.getValue(), loginFeedBack.userCode);
        sharedPreferenceManager.putData(
                SharedReferenceKeys.TOKEN.getValue(), loginFeedBack.access_token);
        sharedPreferenceManager.putData(
                SharedReferenceKeys.REFRESH_TOKEN.getValue(), loginFeedBack.refresh_token);
        sharedPreferenceManager.putData(
                SharedReferenceKeys.XSRF.getValue(), loginFeedBack.XSRFToken);
        sharedPreferenceManager.putData(
                SharedReferenceKeys.USERNAME_TEMP.getValue(), username);
        sharedPreferenceManager.putData(
                SharedReferenceKeys.PASSWORD_TEMP.getValue(), Crypto.encrypt(password));
        if (binding.checkBoxSave.isChecked()) {
            sharedPreferenceManager.putData(
                    SharedReferenceKeys.USERNAME.getValue(), username);
            sharedPreferenceManager.putData(
                    SharedReferenceKeys.PASSWORD.getValue(), Crypto.encrypt(password));
        }
    }

    void loadPreference() {
        sharedPreferenceManager = new SharedPreferenceManager(
                activity, SharedReferenceNames.ACCOUNT.getValue());
        if (sharedPreferenceManager.checkIsNotEmpty(SharedReferenceKeys.USERNAME.getValue()) &&
                sharedPreferenceManager.checkIsNotEmpty(SharedReferenceKeys.PASSWORD.getValue())) {
            binding.editTextUsername.setText(sharedPreferenceManager.getStringData(
                    SharedReferenceKeys.USERNAME.getValue()));
            binding.editTextPassword.setText(Crypto.decrypt(sharedPreferenceManager.getStringData(
                    SharedReferenceKeys.PASSWORD.getValue())));
        }
    }

    static class Register implements ICallback<LoginFeedBack> {
        @Override
        public void execute(Response<LoginFeedBack> response) {
            String message;
            if (response.body() != null) {
                message = response.body().message;
                new CustomToast().success(message);
            }
        }
    }

    class Login implements ICallback<LoginFeedBack> {
        @Override
        public void execute(Response<LoginFeedBack> response) {
            LoginFeedBack loginFeedBack = response.body();
            if (loginFeedBack == null || loginFeedBack.access_token == null ||
                    loginFeedBack.refresh_token == null ||
                    loginFeedBack.access_token.length() < 1 ||
                    loginFeedBack.refresh_token.length() < 1) {
                new CustomToast().warning(getString(R.string.error_is_not_match), Toast.LENGTH_LONG);
            } else {
                List<String> cookieList = response.headers().values("Set-Cookie");
                loginFeedBack.XSRFToken = (cookieList.get(1).split(";"))[0];
                JWT jwt = new JWT(loginFeedBack.access_token);
                loginFeedBack.displayName = jwt.getClaim("DisplayName").asString();
                loginFeedBack.userCode = jwt.getClaim("UserCode").asString();
                savePreference(loginFeedBack);
                Intent intent = new Intent(activity, HomeActivity.class);
                startActivity(intent);
                finish();
            }
        }
    }

    class GetErrorIncomplete implements ICallbackIncomplete<LoginFeedBack> {
        @Override
        public void executeIncomplete(Response<LoginFeedBack> response) {
            CustomErrorHandling customErrorHandlingNew = new CustomErrorHandling(activity);
            String error = customErrorHandlingNew.getErrorMessageDefault(response);
            if (response.code() == 401) {
                error = LoginActivity.this.getString(R.string.error_is_not_match);
                if (response.errorBody() != null) {
                    try {
                        JSONObject jObjError = new JSONObject(response.errorBody().string());
                        error = jObjError.getString("message");
                    } catch (Exception e) {
                        Log.e("error", e.toString());
                    }
                }
                new CustomToast().warning(error, Toast.LENGTH_LONG);
            } else
                new CustomDialog(DialogType.Yellow, LoginActivity.this, error,
                        LoginActivity.this.getString(R.string.dear_user),
                        LoginActivity.this.getString(R.string.login),
                        LoginActivity.this.getString(R.string.accepted));
        }
    }

    class GetError implements ICallbackError {
        @Override
        public void executeError(Throwable t) {
            CustomErrorHandling customErrorHandlingNew = new CustomErrorHandling(activity);
            String error = customErrorHandlingNew.getErrorMessageTotal(t);
            new CustomDialog(DialogType.YellowRedirect, LoginActivity.this, error,
                    LoginActivity.this.getString(R.string.dear_user),
                    LoginActivity.this.getString(R.string.login),
                    LoginActivity.this.getString(R.string.accepted));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        activity = null;
        binding.imageViewPerson.setImageDrawable(null);
        binding.imageViewPassword.setImageDrawable(null);
        binding.imageViewLogo.setImageDrawable(null);
        binding.imageViewUsername.setImageDrawable(null);
        binding = null;
        Debug.getNativeHeapAllocatedSize();
        System.runFinalization();
        Runtime.getRuntime().totalMemory();
        Runtime.getRuntime().freeMemory();
        Runtime.getRuntime().maxMemory();
        Runtime.getRuntime().gc();
        System.gc();
        super.onDestroy();
    }

    @Override
    protected void onStop() {
        Debug.getNativeHeapAllocatedSize();
        System.runFinalization();
        Runtime.getRuntime().totalMemory();
        Runtime.getRuntime().freeMemory();
        Runtime.getRuntime().maxMemory();
        Runtime.getRuntime().gc();
        System.gc();
        super.onStop();
    }

}