package com.leon.counter_reading;

import static android.os.Build.UNKNOWN;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.app.job.JobInfo;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import androidx.core.app.ActivityCompat;
import androidx.multidex.MultiDex;

import com.leon.counter_reading.di.component.ActivityComponent;
import com.leon.counter_reading.di.component.ApplicationComponent;
import com.leon.counter_reading.di.component.DaggerActivityComponent;
import com.leon.counter_reading.di.component.DaggerApplicationComponent;
import com.leon.counter_reading.di.module.CustomDialogModule;
import com.leon.counter_reading.di.module.FlashModule;
import com.leon.counter_reading.di.module.LocationTrackingModule;
import com.leon.counter_reading.di.module.MyDatabaseModule;
import com.leon.counter_reading.di.module.NetworkModule;
import com.leon.counter_reading.di.module.SharedPreferenceModule;
import com.leon.counter_reading.enums.SharedReferenceNames;
import com.leon.counter_reading.infrastructure.ILocationTracking;
import com.leon.counter_reading.tables.ReadingData;
import com.leon.counter_reading.utils.locating.CheckSensor;

import org.acra.annotation.AcraMailSender;
import org.acra.annotation.AcraScheduler;
import org.acra.config.CoreConfigurationBuilder;
import org.acra.config.MailSenderConfigurationBuilder;
import org.acra.data.StringFormat;

import java.util.ArrayList;

import es.dmoral.toasty.Toasty;

@AcraMailSender(mailTo = "ali.rostami33@ymail.com")
@AcraScheduler(requiresNetworkType = JobInfo.NETWORK_TYPE_UNMETERED,
        requiresBatteryNotLow = true)
public class MyApplication extends Application {
    public static final String FONT_NAME = "font/font_1.ttf";
    public static final int TOAST_TEXT_SIZE = 20;

    public static final int GPS_CODE = 1231;
    public static final int REQUEST_NETWORK_CODE = 1232;
    public static final int REQUEST_WIFI_CODE = 1233;
    public static final int CAMERA_REQUEST = 1888;
    public static final int GALLERY_REQUEST = 1889;
    public static final int CARRIER_PRIVILEGE_STATUS = 901;

    public static final int CAMERA = 1446;
    public static final int REPORT = 1445;
    public static final int NAVIGATION = 1903;
    public static final int COUNTER_LOCATION = 1914;
    public static final int DESCRIPTION = 1909;

    public static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10;
    public static final long MIN_TIME_BW_UPDATES = 10000;
    public static final long FASTEST_INTERVAL = 10000;
    public static final int MAX_IMAGE_SIZE = 200000;

    public static int POSITION = -1;
    public static Bitmap BITMAP_SELECTED_IMAGE;
    public static Uri PHOTO_URI;

    public static boolean FOCUS_ON_EDIT_TEXT;
    public static ArrayList<Integer> IS_MANE = new ArrayList<>();
    public static ReadingData readingData, readingDataTemp;

    private static Context appContext;
    private static int ERROR_COUNTER = 0;
    private static ApplicationComponent applicationComponent;
    private static ActivityComponent activityComponent;

    @Override
    public void onCreate() {
        appContext = getApplicationContext();

        Toasty.Config.getInstance()
                .tintIcon(true)
                .setToastTypeface(Typeface.createFromAsset(appContext.getAssets(), MyApplication.FONT_NAME))
                .setTextSize(TOAST_TEXT_SIZE)
                .allowQueue(true).apply();
        applicationComponent = DaggerApplicationComponent
                .builder()
                .networkModule(new NetworkModule())
                .flashModule(new FlashModule(appContext))
                .myDatabaseModule(new MyDatabaseModule(appContext))
                .sharedPreferenceModule(new SharedPreferenceModule(appContext, SharedReferenceNames.ACCOUNT))
                .build();
        applicationComponent.inject(this);


        super.onCreate();
//        throw new RuntimeException("Test Crash"); // Force a crash
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
        CoreConfigurationBuilder builder = new CoreConfigurationBuilder(base);
        //core configuration:
        builder.withBuildConfigClass(BuildConfig.class).withReportFormat(StringFormat.JSON);
        //each plugin you chose above can be configured with its builder like this:
        builder.getPluginConfigurationBuilder(MailSenderConfigurationBuilder.class)
                //required
                .withMailTo("ali.rostami33@ymail.com")
                //defaults to true
                .withReportAsFile(true)
                //defaults to ACRA-report.stacktrace
                .withReportFileName("Crash.txt")
                //defaults to "<applicationId> Crash Report"
//                .withSubject("".concat(BuildConfig.COMPANY_NAME.name()))
                //defaults to empty
                .withBody("برنامه کرش کرده است.")
                //make sure to enable all plugins you want to use:
                .withEnabled(true);
//        ACRA.init(this);
//        Log.e("here", "after acra initialized");
    }

    public static ActivityComponent getActivityComponent() {
        return activityComponent;
    }

    public static void setActivityComponent(Activity activity) {
        MyApplication.activityComponent = DaggerActivityComponent
                .builder()
                .customDialogModule(new CustomDialogModule(activity))
                .locationTrackingModule(new LocationTrackingModule(activity))
                .build();
    }

    public static ILocationTracking getLocationTracker(Activity activity) {
        return CheckSensor.checkSensor(activity, false) ?
                activityComponent.LocationTrackingGoogle() :
                activityComponent.LocationTrackingGps();
    }

    public static ApplicationComponent getApplicationComponent() {
        return applicationComponent;
    }

    public static Context getContext() {
        return appContext;
    }

    public static int getErrorCounter() {
        return ERROR_COUNTER;
    }

    public static void setErrorCounter(int errorCounter) {
        MyApplication.ERROR_COUNTER = errorCounter;
    }

    public static void onActivitySetTheme(Activity activity, int theme, boolean actionBar) {
        if (!actionBar) {
            if (theme == 1) {
                activity.setTheme(R.style.AppTheme_NoActionBar);
            } else if (theme == 2) {
                activity.setTheme(R.style.GreenBlue_NoActionBar);
            } else if (theme == 3) {
                activity.setTheme(R.style.AppTheme_Indigo_NoActionBar);
            } else if (theme == 4) {
                activity.setTheme(R.style.AppTheme_DarkGrey_NoActionBar);
            }
        } else {
            if (theme == 1) {
                activity.setTheme(R.style.AppTheme);
            } else if (theme == 2) {
                activity.setTheme(R.style.GreenBlue);
            } else if (theme == 3) {
                activity.setTheme(R.style.AppTheme_Indigo);
            } else if (theme == 4) {
                activity.setTheme(R.style.AppTheme_DarkGrey);
            }
        }
    }

    public static String getDBName() {
        return "MyDatabase_7";
    }

    public static String getAndroidVersion() {
        String release = Build.VERSION.RELEASE;
        int sdkVersion = Build.VERSION.SDK_INT;
        return "Android SDK: " + sdkVersion + " (" + release + ")";
    }

    @SuppressLint("HardwareIds")
    public static String getSerial(Activity activity) {
        String serial = Build.SERIAL;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (hasCarrierPrivileges(activity))
                serial = Build.getSerial();
        }
        if (serial.equals(UNKNOWN))
            serial = Settings.Secure.getString(new ContextWrapper(activity).getContentResolver(),
                    Settings.Secure.ANDROID_ID);
        return serial;
    }

    private static boolean hasCarrierPrivileges(Activity activity) {
        TelephonyManager tm = (TelephonyManager)
                new ContextWrapper(activity).getSystemService(TELEPHONY_SERVICE);
        boolean isCarrier = tm.hasCarrierPrivileges();
        if (!isCarrier) {
            int hasPermission = ActivityCompat.checkSelfPermission(activity,
                    "android.permission.READ_PRIVILEGED_PHONE_STATE");
            if (hasPermission != PackageManager.PERMISSION_GRANTED) {
                if (!activity.shouldShowRequestPermissionRationale("android.permission.READ_PRIVILEGED_PHONE_STATE")) {
                    ActivityCompat.requestPermissions(activity, new String[]{
                            "android.permission.READ_PRIVILEGED_PHONE_STATE"}, CARRIER_PRIVILEGE_STATUS);
                }
            }
        }
        return isCarrier;
    }


    public static void showSoftKeyboard(View view) {
        if (view.requestFocus()) {
            InputMethodManager imm = (InputMethodManager) appContext.getSystemService(Context.INPUT_METHOD_SERVICE);
            try {
//                imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void hideSoftKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) appContext.getSystemService(Context.INPUT_METHOD_SERVICE);
        try {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
