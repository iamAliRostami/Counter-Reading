package com.leon.counter_reading.activities;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Debug;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import com.leon.counter_reading.MyApplication;
import com.leon.counter_reading.R;
import com.leon.counter_reading.databinding.ActivityReportForbidBinding;
import com.leon.counter_reading.enums.BundleEnum;
import com.leon.counter_reading.enums.ProgressType;
import com.leon.counter_reading.enums.SharedReferenceKeys;
import com.leon.counter_reading.enums.SharedReferenceNames;
import com.leon.counter_reading.fragments.HighQualityFragment;
import com.leon.counter_reading.infrastructure.IAbfaService;
import com.leon.counter_reading.infrastructure.ICallback;
import com.leon.counter_reading.infrastructure.ICallbackError;
import com.leon.counter_reading.infrastructure.ICallbackIncomplete;
import com.leon.counter_reading.infrastructure.ISharedPreferenceManager;
import com.leon.counter_reading.tables.ForbiddenDto;
import com.leon.counter_reading.utils.CustomFile;
import com.leon.counter_reading.utils.GPSTracker;
import com.leon.counter_reading.utils.HttpClientWrapper;
import com.leon.counter_reading.utils.MyDatabaseClient;
import com.leon.counter_reading.utils.NetworkHelper;
import com.leon.counter_reading.utils.SharedPreferenceManager;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;

import static com.leon.counter_reading.utils.CustomFile.createImageFile;

public class ReportForbidActivity extends AppCompatActivity {
    ActivityReportForbidBinding binding;
    ISharedPreferenceManager sharedPreferenceManager;
    Activity activity;
    int zoneId;
    ForbiddenDto forbiddenDto = new ForbiddenDto();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPreferenceManager = new SharedPreferenceManager(getApplicationContext(),
                SharedReferenceNames.ACCOUNT.getValue());
        int theme = sharedPreferenceManager.getIntData(SharedReferenceKeys.THEME_STABLE.getValue());
        MyApplication.onActivitySetTheme(this, theme, true);
        binding = ActivityReportForbidBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        activity = this;
        initialize();
    }

    void initialize() {
        if (getIntent().getExtras() != null)
            zoneId = getIntent().getExtras().getInt(BundleEnum.ZONE_ID.getValue());
        forbiddenDto.File = new ArrayList<>();
        forbiddenDto.bitmaps = new ArrayList<>();
        setOnButtonPhotoClickListener();
        setOnButtonSubmitClickListener();
        setOnImageViewDeleteClickListener();
        setOnImageViewTakenClickListener();
        setOnEditTextChangeListener();
    }

    void setOnEditTextChangeListener() {
        binding.editTextPreAccount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().length() == 15) {
                    View view = binding.editTextNextAccount;
                    view.requestFocus();
                }
            }
        });
        binding.editTextNextAccount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().length() == 15) {
                    View view = binding.editTextPostalCode;
                    view.requestFocus();
                }
            }
        });
        binding.editTextPostalCode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().length() == 10) {
                    View view = binding.editTextAhadNumber;
                    view.requestFocus();
                }
            }
        });
    }

    void setOnImageViewDeleteClickListener() {
        binding.imageViewDelete.setOnClickListener(v -> {
            forbiddenDto.File.remove(forbiddenDto.File.size() - 1);
            forbiddenDto.bitmaps.remove(forbiddenDto.bitmaps.size() - 1);
            if (forbiddenDto.File.size() > 0) {
                binding.imageViewTaken.setImageBitmap(
                        forbiddenDto.bitmaps.get(forbiddenDto.bitmaps.size() - 1));
            } else {
                binding.relativeLayoutImage.setVisibility(View.GONE);
            }
        });
    }

    void setOnButtonSubmitClickListener() {
        binding.buttonSubmit.setOnClickListener(v -> {
            View view = null;
            boolean cancel = false;
            if (binding.editTextPreAccount.getText().length() < 5) {
                binding.editTextPreAccount.setError(getString(R.string.error_format));
                view = binding.editTextPreAccount;
                cancel = true;
            } else if (binding.editTextNextAccount.getText().length() < 5) {
                binding.editTextNextAccount.setError(getString(R.string.error_format));
                view = binding.editTextNextAccount;
                cancel = true;
            } else if (binding.editTextPostalCode.getText().length() < 10) {
                binding.editTextPostalCode.setError(getString(R.string.error_format));
                view = binding.editTextPostalCode;
                cancel = true;
            } else if (binding.editTextAhadNumber.getText().toString().isEmpty()) {
                binding.editTextAhadNumber.setError(getString(R.string.error_empty));
                view = binding.editTextAhadNumber;
                cancel = true;
            } else if (binding.editTextDescription.getText().toString().isEmpty()) {
                binding.editTextDescription.setError(getString(R.string.error_empty));
                view = binding.editTextDescription;
                cancel = true;
            }
            if (!cancel)
                sendForbid();
            else view.requestFocus();
        });
    }

    void sendForbid() {
        GPSTracker gpsTracker = new GPSTracker(activity);
        forbiddenDto.prepareToSend(
                gpsTracker.getAccuracy(), gpsTracker.getLongitude(), gpsTracker.getLatitude(),
                binding.editTextPostalCode.getText().toString(),
                binding.editTextDescription.getText().toString(),
                binding.editTextPreAccount.getText().toString(),
                binding.editTextNextAccount.getText().toString(),
                binding.editTextAhadNumber.getText().toString(), zoneId);

        Retrofit retrofit = NetworkHelper.getInstance();
        IAbfaService iAbfaService = retrofit.create(IAbfaService.class);
        Call<ForbiddenDto.ForbiddenDtoResponses> call = iAbfaService.singleForbidden(
                forbiddenDto.File,
                forbiddenDto.zoneIdRequestBody,
                forbiddenDto.descriptionRequestBody,
                forbiddenDto.preEshterakRequestBody,
                forbiddenDto.nextEshterakRequestBody,
                forbiddenDto.postalCodeRequestBody,
                forbiddenDto.tedadVahedRequestBody,
                forbiddenDto.xRequestBody,
                forbiddenDto.yRequestBody,
                forbiddenDto.gisAccuracyRequestBody);
        HttpClientWrapper.callHttpAsync(call, ProgressType.SHOW.getValue(), activity,
                new Forbidden(), new ForbiddenIncomplete(), new Error());
    }

    void setOnImageViewTakenClickListener() {
        binding.imageViewTaken.setOnClickListener(v -> {
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            HighQualityFragment highQualityFragment =
                    HighQualityFragment.newInstance(forbiddenDto.bitmaps.get(forbiddenDto.bitmaps.size() - 1));
            highQualityFragment.show(fragmentTransaction, "Image # 2");
        });
    }

    void setOnButtonPhotoClickListener() {
        binding.buttonPhoto.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(ReportForbidActivity.this);
            builder.setTitle(R.string.choose_document);
            builder.setMessage(R.string.select_source);
            builder.setPositiveButton(R.string.gallery, (dialog, which) -> {
                dialog.dismiss();
                Intent intent = new Intent("android.intent.action.PICK");
                intent.setType("image/*");
                startActivityForResult(intent, MyApplication.GALLERY_REQUEST);
            });
            builder.setNegativeButton(R.string.camera, (dialog, which) -> {
                dialog.dismiss();
                Intent cameraIntent = new Intent("android.media.action.IMAGE_CAPTURE");
                if (cameraIntent.resolveActivity(ReportForbidActivity.this.getPackageManager()) != null) {
                    File photoFile = null;
                    try {
                        photoFile = createImageFile(activity);
                    } catch (IOException e) {
                        Log.e("Main", e.toString());
                    }
                    if (photoFile != null) {
                        StrictMode.VmPolicy.Builder builderTemp = new StrictMode.VmPolicy.Builder();
                        StrictMode.setVmPolicy(builderTemp.build());
                        cameraIntent.putExtra("output", Uri.fromFile(photoFile));
                        startActivityForResult(cameraIntent, MyApplication.CAMERA_REQUEST);
                    }
                }
            });
            builder.setNeutralButton("", (dialog, which) -> dialog.dismiss());
            builder.create().show();
        });
    }

    class Forbidden implements ICallback<ForbiddenDto.ForbiddenDtoResponses> {
        @Override
        public void execute(Response<ForbiddenDto.ForbiddenDtoResponses> response) {
            if (!response.isSuccessful())
                MyDatabaseClient.getInstance(activity).getMyDatabase().forbiddenDao().
                        insertForbiddenDto(forbiddenDto);
            finish();
        }
    }

    class ForbiddenIncomplete implements ICallbackIncomplete<ForbiddenDto.ForbiddenDtoResponses> {
        @Override
        public void executeIncomplete(Response<ForbiddenDto.ForbiddenDtoResponses> response) {
            MyDatabaseClient.getInstance(activity).getMyDatabase().forbiddenDao().
                    insertForbiddenDto(forbiddenDto);
            finish();
        }
    }

    class Error implements ICallbackError {
        @Override
        public void executeError(Throwable t) {
            MyDatabaseClient.getInstance(activity).getMyDatabase().forbiddenDao().
                    insertForbiddenDto(forbiddenDto);
            finish();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        MyApplication.bitmapSelectedImage = null;
        if (resultCode == RESULT_OK) {
            if (requestCode == MyApplication.GALLERY_REQUEST && data != null) {
                Uri selectedImage = data.getData();
                Bitmap bitmap;
                try {
                    Uri uri = data.getData();
                    Objects.requireNonNull(uri);
                    InputStream inputStream = this.getContentResolver().openInputStream(
                            Objects.requireNonNull(selectedImage));
                    bitmap = BitmapFactory.decodeStream(inputStream);
                    MyApplication.bitmapSelectedImage = bitmap;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (requestCode == MyApplication.CAMERA_REQUEST) {
                ContentResolver contentResolver = this.getContentResolver();
                try {
                    MyApplication.bitmapSelectedImage = MediaStore.Images.Media.getBitmap(
                            contentResolver, Uri.parse(MyApplication.fileName));
                } catch (IOException e) {
                    Log.e("Error", e.toString());
                    e.printStackTrace();
                }
            }
            forbiddenDto.bitmaps.add(MyApplication.bitmapSelectedImage);
            binding.relativeLayoutImage.setVisibility(View.VISIBLE);
            binding.imageViewTaken.setImageBitmap(MyApplication.bitmapSelectedImage);
            forbiddenDto.File.add(CustomFile.bitmapToFile(MyApplication.bitmapSelectedImage, activity));
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        Runtime.getRuntime().totalMemory();
        Runtime.getRuntime().freeMemory();
        Runtime.getRuntime().maxMemory();
        Debug.getNativeHeapAllocatedSize();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Runtime.getRuntime().totalMemory();
        Runtime.getRuntime().freeMemory();
        Runtime.getRuntime().maxMemory();
        Debug.getNativeHeapAllocatedSize();
    }
}