package com.leon.counter_reading.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Debug;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentTransaction;

import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.leon.counter_reading.MyApplication;
import com.leon.counter_reading.R;
import com.leon.counter_reading.databinding.ActivityTakePhotoBinding;
import com.leon.counter_reading.enums.BundleEnum;
import com.leon.counter_reading.enums.DialogType;
import com.leon.counter_reading.enums.ProgressType;
import com.leon.counter_reading.enums.SharedReferenceKeys;
import com.leon.counter_reading.enums.SharedReferenceNames;
import com.leon.counter_reading.fragments.HighQualityFragment;
import com.leon.counter_reading.infrastructure.IAbfaService;
import com.leon.counter_reading.infrastructure.ICallback;
import com.leon.counter_reading.infrastructure.ICallbackError;
import com.leon.counter_reading.infrastructure.ICallbackIncomplete;
import com.leon.counter_reading.infrastructure.ISharedPreferenceManager;
import com.leon.counter_reading.tables.Image;
import com.leon.counter_reading.utils.CustomDialog;
import com.leon.counter_reading.utils.CustomErrorHandling;
import com.leon.counter_reading.utils.CustomFile;
import com.leon.counter_reading.utils.CustomProgressBar;
import com.leon.counter_reading.utils.CustomToast;
import com.leon.counter_reading.utils.HttpClientWrapper;
import com.leon.counter_reading.utils.MyDatabaseClient;
import com.leon.counter_reading.utils.NetworkHelper;
import com.leon.counter_reading.utils.PermissionManager;
import com.leon.counter_reading.utils.SharedPreferenceManager;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Objects;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;

import static com.leon.counter_reading.utils.CustomFile.createImageFile;

public class TakePhotoActivity extends AppCompatActivity {
    Activity activity;
    ActivityTakePhotoBinding binding;
    ArrayList<Bitmap> bitmaps;
    Image.ImageGrouped imageGrouped = new Image.ImageGrouped();
    ArrayList<Image> images;
    int imageNumber = 1, imageNumberTemp = 0;
    boolean replace = false;
    String uuid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ISharedPreferenceManager sharedPreferenceManager =
                new SharedPreferenceManager(getApplicationContext(),
                        SharedReferenceNames.ACCOUNT.getValue());
        int theme = sharedPreferenceManager.getIntData(
                SharedReferenceKeys.THEME_STABLE.getValue());
        MyApplication.onActivitySetTheme(this, theme, true);
        binding = ActivityTakePhotoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        activity = this;
        if (PermissionManager.checkStoragePermission(getApplicationContext()))
            initialize();
        else askStoragePermission();
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    void initialize() {
        if (getIntent().getExtras() != null) {
            uuid = getIntent().getExtras().getString(BundleEnum.BILL_ID.getValue());
        }
        imageSetup();
        setOnButtonSendClickListener();
    }

    void imageReset(int index) {
        MyDatabaseClient.getInstance(activity).getMyDatabase().imageDao().
                deleteImage(images.get(index).id);
        images.remove(index);
        imageSetup();
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    void imageSetup() {
        images = new ArrayList<>();
        images.addAll(MyDatabaseClient.getInstance(activity).getMyDatabase().imageDao()
                .getImagesByOnOffLoadId(uuid));
        bitmaps = new ArrayList<>();
        if (images.size() > 0) {
            binding.editTextDescription.setText(images.get(0).Description);
            Bitmap bitmap = CustomFile.loadImage(activity, images.get(0).address);
            if (bitmap != null) {
                imageNumber = images.size() + 1;
                bitmaps.add(bitmap);
                binding.imageView1.setImageBitmap(bitmap);
                binding.imageViewDelete1.setVisibility(View.VISIBLE);
                if (images.get(0).isSent)
                    binding.imageViewSent1.setVisibility(View.VISIBLE);
            } else {
                imageReset(0);
            }
            if (images.size() > 1) {
                bitmap = CustomFile.loadImage(activity, images.get(1).address);
                if (bitmap != null) {
                    imageNumber = images.size() + 1;
                    bitmaps.add(bitmap);
                    binding.imageView2.setImageBitmap(bitmap);
                    binding.imageViewDelete2.setVisibility(View.VISIBLE);
                    if (images.get(1).isSent)
                        binding.imageViewSent2.setVisibility(View.VISIBLE);
                } else {
                    imageReset(1);
                }
            } else {
                binding.imageViewDelete2.setVisibility(View.GONE);
                binding.imageViewDelete3.setVisibility(View.GONE);
                binding.imageViewDelete4.setVisibility(View.GONE);
            }
            if (images.size() > 2) {
                bitmap = CustomFile.loadImage(activity, images.get(2).address);
                if (bitmap != null) {
                    imageNumber = images.size() + 1;
                    bitmaps.add(bitmap);
                    binding.imageView3.setImageBitmap(
                            CustomFile.loadImage(activity, images.get(2).address));
                    binding.imageViewDelete3.setVisibility(View.VISIBLE);
                    if (images.get(2).isSent)
                        binding.imageViewSent3.setVisibility(View.VISIBLE);
                } else {
                    imageReset(2);
                }
            } else {
                binding.imageViewDelete3.setVisibility(View.GONE);
                binding.imageViewDelete4.setVisibility(View.GONE);
            }
            if (images.size() > 3) {
                bitmap = CustomFile.loadImage(activity, images.get(3).address);
                if (bitmap != null) {
                    imageNumber = images.size() + 1;
                    bitmaps.add(bitmap);
                    binding.imageView4.setImageBitmap(bitmap);
                    binding.imageViewDelete4.setVisibility(View.VISIBLE);
                    if (images.get(3).isSent)
                        binding.imageViewSent4.setVisibility(View.VISIBLE);
                } else {
                    imageReset(3);
                }
            } else
                binding.imageViewDelete4.setVisibility(View.GONE);
        } else {
            binding.imageView1.setImageDrawable(
                    ContextCompat.getDrawable(activity, R.drawable.img_take_photo));
            binding.imageView2.setImageDrawable(
                    ContextCompat.getDrawable(activity, R.drawable.img_take_photo));
            binding.imageView3.setImageDrawable(
                    ContextCompat.getDrawable(activity, R.drawable.img_take_photo));
            binding.imageView4.setImageDrawable(
                    ContextCompat.getDrawable(activity, R.drawable.img_take_photo));
            binding.imageViewDelete1.setImageDrawable(
                    ContextCompat.getDrawable(activity, android.R.drawable.ic_delete));
            binding.imageViewDelete2.setImageDrawable(
                    ContextCompat.getDrawable(activity, android.R.drawable.ic_delete));
            binding.imageViewDelete3.setImageDrawable(
                    ContextCompat.getDrawable(activity, android.R.drawable.ic_delete));
            binding.imageViewDelete4.setImageDrawable(
                    ContextCompat.getDrawable(activity, android.R.drawable.ic_delete));
            binding.imageViewDelete1.setVisibility(View.GONE);
            binding.imageViewDelete2.setVisibility(View.GONE);
            binding.imageViewDelete3.setVisibility(View.GONE);
            binding.imageViewDelete4.setVisibility(View.GONE);
            binding.imageViewSent1.setVisibility(View.GONE);
            binding.imageViewSent2.setVisibility(View.GONE);
            binding.imageViewSent3.setVisibility(View.GONE);
            binding.imageViewSent4.setVisibility(View.GONE);
        }
        setOnImageViewPickerClickListener();
        setOnImageViewDeleteClickListener();
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    void setOnImageViewPickerClickListener() {
        binding.imageView1.setOnClickListener(onPickerClickListener);
        binding.imageView2.setOnClickListener(onPickerClickListener);
        binding.imageView3.setOnClickListener(onPickerClickListener);
        binding.imageView4.setOnClickListener(onPickerClickListener);

        binding.imageView1.setOnLongClickListener(onLongClickListener);
        binding.imageView2.setOnLongClickListener(onLongClickListener);
        binding.imageView3.setOnLongClickListener(onLongClickListener);
        binding.imageView4.setOnLongClickListener(onLongClickListener);
    }

    @SuppressLint("NonConstantResourceId")
    View.OnLongClickListener onLongClickListener = v -> {
        Bitmap bitmap = null;
        switch (v.getId()) {
            case R.id.image_View_1:
                if (bitmaps.size() > 0)
                    bitmap = bitmaps.get(0);
                break;
            case R.id.image_View_2:
                if (bitmaps.size() > 1)
                    bitmap = bitmaps.get(1);
                break;
            case R.id.image_View_3:
                if (bitmaps.size() > 2)
                    bitmap = bitmaps.get(2);
                break;
            case R.id.image_View_4:
                if (bitmaps.size() > 3)
                    bitmap = bitmaps.get(3);
                break;
        }
        if (bitmap != null) {
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            HighQualityFragment highQualityFragment =
                    HighQualityFragment.newInstance(bitmap);
            highQualityFragment.show(fragmentTransaction, "Image # 2");
        }
        return false;
    };

    @SuppressLint("NonConstantResourceId")
    View.OnClickListener onPickerClickListener = v -> {
        switch (v.getId()) {
            case R.id.image_View_1:
                replace = imageNumber > 1;
                if (replace) {
                    imageNumberTemp = 1;
                }
                break;
            case R.id.image_View_2:
                replace = imageNumber > 2;
                if (replace) {
                    imageNumberTemp = 2;
                }
                break;
            case R.id.image_View_3:
                replace = imageNumber > 3;
                if (replace) {
                    imageNumberTemp = 3;
                }
                break;
            case R.id.image_View_4:
                replace = imageNumber > 4;
                if (replace) {
                    imageNumberTemp = 4;
                }
                break;
        }
        imagePicker();
    };

    @SuppressLint("QueryPermissionsNeeded")
    void imagePicker() {
        AlertDialog.Builder builder = new AlertDialog.Builder(TakePhotoActivity.this);
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
            if (cameraIntent.resolveActivity(TakePhotoActivity.this.getPackageManager()) != null) {
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
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    void setOnImageViewDeleteClickListener() {
        binding.imageViewDelete1.setOnClickListener(onDeleteClickListener);
        binding.imageViewDelete2.setOnClickListener(onDeleteClickListener);
        binding.imageViewDelete3.setOnClickListener(onDeleteClickListener);
        binding.imageViewDelete4.setOnClickListener(onDeleteClickListener);
    }

    @SuppressLint("NonConstantResourceId")
    View.OnClickListener onDeleteClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            boolean b = false;
            switch (v.getId()) {
                case R.id.image_View_delete_1:
                    if (imageNumber > 1) {
                        removeImage(0);
                        b = true;
                        binding.imageView1.setImageBitmap(((BitmapDrawable)
                                binding.imageView2.getDrawable()).getBitmap());
                        binding.imageView2.setImageBitmap(((BitmapDrawable)
                                binding.imageView3.getDrawable()).getBitmap());
                        binding.imageView3.setImageBitmap(((BitmapDrawable)
                                binding.imageView4.getDrawable()).getBitmap());
                    }
                    break;
                case R.id.image_View_delete_2:
                    if (imageNumber > 2) {
                        removeImage(1);
                        b = true;
                        binding.imageView2.setImageBitmap(((BitmapDrawable)
                                binding.imageView3.getDrawable()).getBitmap());
                        binding.imageView3.setImageBitmap(((BitmapDrawable)
                                binding.imageView4.getDrawable()).getBitmap());
                    }
                    break;
                case R.id.image_View_delete_3:
                    if (imageNumber > 3) {
                        removeImage(2);
                        b = true;
                        binding.imageView3.setImageBitmap(((BitmapDrawable)
                                binding.imageView4.getDrawable()).getBitmap());
                    }
                    break;
                case R.id.image_View_delete_4:
                    if (imageNumber > 4) {
                        removeImage(3);
                        b = true;
                    }
                    break;
            }
            if (b) {
                if (imageNumber == 1) {
                    binding.imageViewDelete1.setVisibility(View.GONE);
                    binding.imageViewSent1.setVisibility(View.GONE);
                } else if (imageNumber == 2) {
                    binding.imageViewDelete2.setVisibility(View.GONE);
                    binding.imageViewSent2.setVisibility(View.GONE);
                } else if (imageNumber == 3) {
                    binding.imageViewDelete3.setVisibility(View.GONE);
                    binding.imageViewSent3.setVisibility(View.GONE);
                } else if (imageNumber == 4) {
                    binding.imageViewDelete4.setVisibility(View.GONE);
                    binding.imageViewSent4.setVisibility(View.GONE);
                }
                binding.imageView4.setImageDrawable(ContextCompat.getDrawable(activity,
                        R.drawable.img_take_photo));
            }
        }
    };

    void removeImage(int index) {
        imageNumber = imageNumber - 1;
        bitmaps.remove(index);
        MyDatabaseClient.getInstance(activity).getMyDatabase().imageDao().
                deleteImage(images.get(index).id);
        images.remove(index);
    }

    void setOnButtonSendClickListener() {
        binding.buttonSaveSend.setOnClickListener(v -> new prepareMultiMedia().execute());
    }

    @SuppressLint("StaticFieldLeak")
    class prepareMultiMedia extends AsyncTask<Integer, Integer, Integer> {
        CustomProgressBar customProgressBar;

        public prepareMultiMedia() {
            super();
        }

        @Override
        protected Integer doInBackground(Integer... integers) {
            for (int i = 0; i < images.size(); i++) {
                if (images.get(i).File == null)
                    images.get(i).File = CustomFile.bitmapToFile(bitmaps.get(i), activity);
                if (binding.editTextDescription.getText().toString().isEmpty())
                    images.get(i).Description = getString(R.string.description);
                else images.get(i).Description = binding.editTextDescription.getText().toString();
                if (!images.get(i).isSent) {
                    imageGrouped.File.add(images.get(i).File);
                }
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            customProgressBar = new CustomProgressBar();
            customProgressBar.show(activity, false);
        }

        @Override
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);
            customProgressBar.getDialog().dismiss();
            uploadImage();
        }

        void uploadImage() {
            if (imageGrouped.File.size() > 0) {
                imageGrouped.OnOffLoadId = RequestBody.create(
                        images.get(0).OnOffLoadId, MediaType.parse("text/plain"));
                imageGrouped.Description = RequestBody.create(
                        images.get(0).Description, MediaType.parse("text/plain"));
                Retrofit retrofit = NetworkHelper.getInstance();
                IAbfaService iAbfaService = retrofit.create(IAbfaService.class);
                Call<Image.ImageUploadResponse> call = iAbfaService.fileUploadGrouped(
                        imageGrouped.File, imageGrouped.OnOffLoadId, imageGrouped.Description);
                HttpClientWrapper.callHttpAsync(call, ProgressType.SHOW.getValue(), activity,
                        new upload(), new uploadIncomplete(), new uploadError());
            } else {
                CustomToast customToast = new CustomToast();
                activity.runOnUiThread(() -> customToast.warning(getString(R.string.there_is_no_images)));
            }
        }
    }

    class upload implements ICallback<Image.ImageUploadResponse> {
        @Override
        public void execute(Response<Image.ImageUploadResponse> response) {
            if (response.body() != null && response.body().status == 200) {
                CustomToast customToast = new CustomToast();
                customToast.success(response.body().message);
            } else {
                new CustomDialog(DialogType.Yellow, activity,
                        activity.getString(R.string.error_upload),
                        activity.getString(R.string.dear_user),
                        activity.getString(R.string.upload_multimedia),
                        activity.getString(R.string.accepted));
            }
            saveImages(response.body() != null && response.body().status == 200);
        }
    }

    class uploadIncomplete implements ICallbackIncomplete<Image.ImageUploadResponse> {

        @Override
        public void executeIncomplete(Response<Image.ImageUploadResponse> response) {
            CustomErrorHandling customErrorHandlingNew = new CustomErrorHandling(activity);
            String error = customErrorHandlingNew.getErrorMessageDefault(response);
            new CustomDialog(DialogType.Yellow, activity, error,
                    activity.getString(R.string.dear_user),
                    activity.getString(R.string.upload_multimedia),
                    activity.getString(R.string.accepted));
            saveImages(false);
        }
    }

    class uploadError implements ICallbackError {
        @Override
        public void executeError(Throwable t) {
            CustomErrorHandling customErrorHandlingNew = new CustomErrorHandling(activity);
            String error = customErrorHandlingNew.getErrorMessageTotal(t);
            new CustomDialog(DialogType.Red, activity, error,
                    activity.getString(R.string.dear_user),
                    activity.getString(R.string.upload_multimedia),
                    activity.getString(R.string.accepted));
            saveImages(false);
        }
    }

    void saveImages(boolean isSent) {
        for (int i = 0; i < images.size(); i++) {
            images.get(i).isSent = isSent;
            if (MyDatabaseClient.getInstance(activity).getMyDatabase().imageDao()
                    .getImagesById(images.get(i).id).size() > 0)
                MyDatabaseClient.getInstance(activity).getMyDatabase().imageDao()
                        .updateImage(images.get(i));
            else {
                String address = CustomFile.saveTempBitmap(bitmaps.get(i), getApplicationContext());
                if (!address.equals(getString(R.string.error_external_storage_is_not_writable))) {
                    images.get(i).address = address;
                    MyDatabaseClient.getInstance(activity).getMyDatabase().imageDao()
                            .insertImage(images.get(i));
                }
            }
        }
        if (isSent) {
            binding.imageViewSent1.setVisibility(View.VISIBLE);
            if (images.size() > 1)
                binding.imageViewSent2.setVisibility(View.VISIBLE);
            if (images.size() > 2)
                binding.imageViewSent3.setVisibility(View.VISIBLE);
            if (images.size() > 3)
                binding.imageViewSent4.setVisibility(View.VISIBLE);
        }
    }

    void askStoragePermission() {
        PermissionListener permissionlistener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                CustomToast customToast = new CustomToast();
                customToast.info(getString(R.string.access_granted));
                initialize();
            }

            @Override
            public void onPermissionDenied(ArrayList<String> deniedPermissions) {
                PermissionManager.forceClose(activity);
            }
        };
        new TedPermission(this)
                .setPermissionListener(permissionlistener)
                .setRationaleMessage(getString(R.string.confirm_permission))
                .setRationaleConfirmText(getString(R.string.allow_permission))
                .setDeniedMessage(getString(R.string.if_reject_permission))
                .setDeniedCloseButtonText(getString(R.string.close))
                .setGotoSettingButtonText(getString(R.string.allow_permission))
                .setPermissions(
                        Manifest.permission.CAMERA,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                ).check();
    }

    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
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
            Image image = new Image();
            image.OnOffLoadId = uuid;
            image.File = CustomFile.bitmapToFile(MyApplication.bitmapSelectedImage, activity);
            if (replace) {
                //TODO
                bitmaps.set(imageNumberTemp - 1, MyApplication.bitmapSelectedImage);
                MyDatabaseClient.getInstance(activity).getMyDatabase().imageDao().deleteImage(
                        images.get(imageNumberTemp - 1).id);
                images.set(imageNumberTemp - 1, image);
                if (imageNumberTemp == 1) {
                    binding.imageViewSent1.setVisibility(View.GONE);
                    binding.imageView1.setImageBitmap(MyApplication.bitmapSelectedImage);
                } else if (imageNumberTemp == 2) {
                    binding.imageViewSent2.setVisibility(View.GONE);
                    binding.imageView2.setImageBitmap(MyApplication.bitmapSelectedImage);
                } else if (imageNumberTemp == 3) {
                    binding.imageViewSent3.setVisibility(View.GONE);
                    binding.imageView3.setImageBitmap(MyApplication.bitmapSelectedImage);
                } else if (imageNumberTemp == 4) {
                    binding.imageViewSent4.setVisibility(View.GONE);
                    binding.imageView4.setImageBitmap(MyApplication.bitmapSelectedImage);
                }
            } else {
                //TODO
                bitmaps.add(MyApplication.bitmapSelectedImage);
                images.add(image);
                if (imageNumber == 1) {
                    binding.imageViewDelete1.setVisibility(View.VISIBLE);
                    binding.imageView1.setImageBitmap(MyApplication.bitmapSelectedImage);
                } else if (imageNumber == 2) {
                    binding.imageViewDelete2.setVisibility(View.VISIBLE);
                    binding.imageView2.setImageBitmap(MyApplication.bitmapSelectedImage);
                } else if (imageNumber == 3) {
                    binding.imageViewDelete3.setVisibility(View.VISIBLE);
                    binding.imageView3.setImageBitmap(MyApplication.bitmapSelectedImage);
                } else if (imageNumber == 4) {
                    binding.imageViewDelete4.setVisibility(View.VISIBLE);
                    binding.imageView4.setImageBitmap(MyApplication.bitmapSelectedImage);
                }
                imageNumber = imageNumber + 1;
            }
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
        binding.imageView1.setImageDrawable(null);
        binding.imageView2.setImageDrawable(null);
        binding.imageView3.setImageDrawable(null);
        binding.imageView4.setImageDrawable(null);
        binding.imageViewDelete1.setImageDrawable(null);
        binding.imageViewDelete2.setImageDrawable(null);
        binding.imageViewDelete3.setImageDrawable(null);
        binding.imageViewDelete4.setImageDrawable(null);
        binding.imageViewSent1.setImageDrawable(null);
        binding.imageViewSent2.setImageDrawable(null);
        binding.imageViewSent3.setImageDrawable(null);
        binding.imageViewSent4.setImageDrawable(null);
        MyApplication.bitmapSelectedImage = null;
        bitmaps = null;
        imageGrouped = null;
        images = null;
        Runtime.getRuntime().totalMemory();
        Runtime.getRuntime().freeMemory();
        Runtime.getRuntime().maxMemory();
        Debug.getNativeHeapAllocatedSize();
    }
}