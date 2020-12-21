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
import com.leon.counter_reading.infrastructure.IAbfaService;
import com.leon.counter_reading.infrastructure.ICallback;
import com.leon.counter_reading.infrastructure.ICallbackError;
import com.leon.counter_reading.infrastructure.ICallbackIncomplete;
import com.leon.counter_reading.infrastructure.ISharedPreferenceManager;
import com.leon.counter_reading.tables.Image;
import com.leon.counter_reading.utils.CustomDialog;
import com.leon.counter_reading.utils.CustomErrorHandling;
import com.leon.counter_reading.utils.CustomFile;
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

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;

import static com.leon.counter_reading.utils.CustomFile.createImageFile;

public class TakePhotoActivity extends AppCompatActivity {
    ActivityTakePhotoBinding binding;
    ISharedPreferenceManager sharedPreferenceManager;
    int imageNumber = 1, imageNumberTemp = 0;
    ArrayList<Bitmap> bitmaps;
    boolean replace = false;
    String uuid;
    ArrayList<Image> images;
    Activity activity;
    Image.ImageGrouped imageGrouped;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPreferenceManager = new SharedPreferenceManager(getApplicationContext(),
                SharedReferenceNames.ACCOUNT.getValue());
        int theme = sharedPreferenceManager.getIntData(SharedReferenceKeys.THEME_STABLE.getValue());
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
        imageGrouped = new Image.ImageGrouped();
        imageSetup();
        setOnButtonSendClickListener();
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
            } else {
                MyDatabaseClient.getInstance(activity).getMyDatabase().imageDao().deleteImage(images.get(0).id);
                images.remove(0);
                imageSetup();
            }
            if (images.size() > 1) {
                bitmap = CustomFile.loadImage(activity, images.get(1).address);
                if (bitmap != null) {
                    imageNumber = images.size() + 1;
                    bitmaps.add(bitmap);
                    binding.imageView2.setImageBitmap(bitmap);
                    binding.imageViewDelete2.setVisibility(View.VISIBLE);
                } else {
                    MyDatabaseClient.getInstance(activity).getMyDatabase().imageDao().deleteImage(images.get(1).id);
                    images.remove(1);
                    imageSetup();
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
                    binding.imageView3.setImageBitmap(CustomFile.loadImage(activity, images.get(2).address));
                    binding.imageViewDelete3.setVisibility(View.VISIBLE);
                } else {
                    MyDatabaseClient.getInstance(activity).getMyDatabase().imageDao().deleteImage(images.get(2).id);
                    images.remove(2);
                    imageSetup();
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
                } else {
                    MyDatabaseClient.getInstance(activity).getMyDatabase().imageDao().deleteImage(images.get(3).id);
                    images.remove(3);
                    imageSetup();
                }
            } else
                binding.imageViewDelete4.setVisibility(View.GONE);
        } else {
            binding.imageView1.setImageDrawable(ContextCompat.getDrawable(activity,
                    R.drawable.img_take_photo));
            binding.imageView2.setImageDrawable(ContextCompat.getDrawable(activity,
                    R.drawable.img_take_photo));
            binding.imageView3.setImageDrawable(ContextCompat.getDrawable(activity,
                    R.drawable.img_take_photo));
            binding.imageView4.setImageDrawable(ContextCompat.getDrawable(activity,
                    R.drawable.img_take_photo));
            binding.imageViewDelete1.setImageDrawable(ContextCompat.getDrawable(activity,
                    android.R.drawable.ic_delete));
            binding.imageViewDelete2.setImageDrawable(ContextCompat.getDrawable(activity,
                    android.R.drawable.ic_delete));
            binding.imageViewDelete3.setImageDrawable(ContextCompat.getDrawable(activity,
                    android.R.drawable.ic_delete));
            binding.imageViewDelete4.setImageDrawable(ContextCompat.getDrawable(activity,
                    android.R.drawable.ic_delete));
            binding.imageViewDelete1.setVisibility(View.GONE);
            binding.imageViewDelete2.setVisibility(View.GONE);
            binding.imageViewDelete3.setVisibility(View.GONE);
            binding.imageViewDelete4.setVisibility(View.GONE);
        }
        setOnImageViewPickerClickListener();
        setOnImageViewDeleteClickListener();
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    void setOnImageViewPickerClickListener() {
        binding.imageView1.setOnClickListener(v -> {
            if (imageNumber > 1) {
                replace = true;
                imageNumberTemp = 1;
            } else {
                replace = false;
            }
            imagePicker();
        });
        binding.imageView2.setOnClickListener(v -> {
            if (imageNumber > 2) {
                replace = true;
                imageNumberTemp = 2;
            } else {
                replace = false;
            }
            imagePicker();

        });
        binding.imageView3.setOnClickListener(v -> {
            if (imageNumber > 3) {
                replace = true;
                imageNumberTemp = 3;
            } else {
                replace = false;
            }
            imagePicker();

        });
        binding.imageView4.setOnClickListener(v -> {
            if (imageNumber > 4) {
                replace = true;
                imageNumberTemp = 4;
            } else {
                replace = false;
            }
            imagePicker();
        });
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    void setOnImageViewDeleteClickListener() {
        binding.imageViewDelete1.setOnClickListener(v -> {
            if (imageNumber > 1) {
                removeImage(0);
                binding.imageView1.setImageBitmap(((BitmapDrawable)
                        binding.imageView2.getDrawable()).getBitmap());
                binding.imageView2.setImageBitmap(((BitmapDrawable)
                        binding.imageView3.getDrawable()).getBitmap());
                binding.imageView3.setImageBitmap(((BitmapDrawable)
                        binding.imageView4.getDrawable()).getBitmap());
                binding.imageView4.setImageDrawable(getDrawable(R.drawable.img_take_photo));
                if (imageNumber == 1)
                    binding.imageViewDelete1.setVisibility(View.GONE);
                else if (imageNumber == 2)
                    binding.imageViewDelete2.setVisibility(View.GONE);
                else if (imageNumber == 3)
                    binding.imageViewDelete3.setVisibility(View.GONE);
                else if (imageNumber == 4)
                    binding.imageViewDelete4.setVisibility(View.GONE);
            }
        });
        binding.imageViewDelete2.setOnClickListener(v -> {
            if (imageNumber > 2) {
                removeImage(1);
                binding.imageView2.setImageBitmap(((BitmapDrawable) binding.imageView3.getDrawable()).getBitmap());
                binding.imageView3.setImageBitmap(((BitmapDrawable) binding.imageView4.getDrawable()).getBitmap());
                binding.imageView4.setImageDrawable(getDrawable(R.drawable.img_take_photo));
                if (imageNumber == 2)
                    binding.imageViewDelete2.setVisibility(View.GONE);
                else if (imageNumber == 3)
                    binding.imageViewDelete3.setVisibility(View.GONE);
                else if (imageNumber == 4)
                    binding.imageViewDelete4.setVisibility(View.GONE);
            }
        });
        binding.imageViewDelete3.setOnClickListener(v -> {
            if (imageNumber > 3) {
                removeImage(2);
                binding.imageView3.setImageBitmap(((BitmapDrawable) binding.imageView4.getDrawable()).getBitmap());
                binding.imageView4.setImageDrawable(getDrawable(R.drawable.img_take_photo));
                if (imageNumber == 3)
                    binding.imageViewDelete3.setVisibility(View.GONE);
                else if (imageNumber == 4)
                    binding.imageViewDelete4.setVisibility(View.GONE);
            }
        });
        binding.imageViewDelete4.setOnClickListener(v -> {
            if (imageNumber > 4) {
                removeImage(3);
                binding.imageView4.setImageDrawable(getDrawable(R.drawable.img_take_photo));
                if (imageNumber == 4)
                    binding.imageViewDelete4.setVisibility(View.GONE);
            }
        });
    }

    void removeImage(int index) {
        imageNumber = imageNumber - 1;
        bitmaps.remove(index);
        MyDatabaseClient.getInstance(activity).getMyDatabase().imageDao().
                deleteImage(images.get(index).id);
        images.remove(index);
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

    void setOnButtonSendClickListener() {
        binding.buttonSaveSend.setOnClickListener(v -> {
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
            if (imageGrouped.File.size() > 0) {
                imageGrouped.OnOffLoadId = images.get(0).OnOffLoadId;
                imageGrouped.Description = images.get(0).Description;
                uploadImage();
            } else {
                CustomToast customToast = new CustomToast();
                customToast.warning(getString(R.string.there_is_no_images));
            }
        });
    }

    void uploadImage() {
        Retrofit retrofit = NetworkHelper.getInstance();
        IAbfaService iAbfaService = retrofit.create(IAbfaService.class);
        Call<Integer> call = iAbfaService.fileUploadGrouped(imageGrouped.File, imageGrouped.OnOffLoadId, imageGrouped.Description);
        HttpClientWrapper.callHttpAsync(call, ProgressType.SHOW.getValue(), activity,
                new upload(), new uploadIncomplete(), new uploadError());
    }

    class upload implements ICallback<Integer> {
        @Override
        public void execute(Response<Integer> response) {
            saveImages(true);
            CustomToast customToast = new CustomToast();
            customToast.success(activity.getString(R.string.upload_multimedia_successful));
        }
    }

    class uploadIncomplete implements ICallbackIncomplete<Integer> {

        @Override
        public void executeIncomplete(Response<Integer> response) {
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
                images.set(imageNumberTemp - 1, image);
                if (imageNumberTemp == 1) {
                    binding.imageView1.setImageBitmap(MyApplication.bitmapSelectedImage);
                } else if (imageNumberTemp == 2) {
                    binding.imageView2.setImageBitmap(MyApplication.bitmapSelectedImage);
                } else if (imageNumberTemp == 3) {
                    binding.imageView3.setImageBitmap(MyApplication.bitmapSelectedImage);
                } else if (imageNumberTemp == 4) {
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
        MyApplication.bitmapSelectedImage = null;
        bitmaps = null;
        Runtime.getRuntime().totalMemory();
        Runtime.getRuntime().freeMemory();
        Runtime.getRuntime().maxMemory();
        Debug.getNativeHeapAllocatedSize();
    }
}