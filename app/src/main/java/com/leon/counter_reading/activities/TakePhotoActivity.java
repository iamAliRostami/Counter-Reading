package com.leon.counter_reading.activities;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageDecoder;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Debug;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ContextThemeWrapper;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentTransaction;

import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.leon.counter_reading.MyApplication;
import com.leon.counter_reading.R;
import com.leon.counter_reading.databinding.ActivityTakePhotoBinding;
import com.leon.counter_reading.enums.BundleEnum;
import com.leon.counter_reading.enums.SharedReferenceKeys;
import com.leon.counter_reading.enums.SharedReferenceNames;
import com.leon.counter_reading.fragments.HighQualityFragment;
import com.leon.counter_reading.infrastructure.ISharedPreferenceManager;
import com.leon.counter_reading.tables.Image;
import com.leon.counter_reading.utils.CustomFile;
import com.leon.counter_reading.utils.CustomToast;
import com.leon.counter_reading.utils.MyDatabaseClient;
import com.leon.counter_reading.utils.PermissionManager;
import com.leon.counter_reading.utils.SharedPreferenceManager;
import com.leon.counter_reading.utils.photo.PrepareMultimedia;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Objects;

import static com.leon.counter_reading.utils.CustomFile.createImageFile;

public class  TakePhotoActivity extends AppCompatActivity {
    Activity activity;
    ISharedPreferenceManager sharedPreferenceManager;
    ActivityTakePhotoBinding binding;
    ArrayList<Bitmap> bitmaps;
    Image.ImageGrouped imageGrouped = new Image.ImageGrouped();
    ArrayList<Image> images;
    int imageNumber = 1, imageNumberTemp = 0;
    boolean replace = false, result;
    int position, trackNumber;
    String uuid;

    View.OnLongClickListener onLongClickListener = v -> {
        Bitmap bitmap = null;
        int id = v.getId();
        if (id == R.id.image_View_1) {
            if (bitmaps.size() > 0)
                bitmap = bitmaps.get(0);
        } else if (id == R.id.image_View_2) {
            if (bitmaps.size() > 1)
                bitmap = bitmaps.get(1);
        } else if (id == R.id.image_View_3) {
            if (bitmaps.size() > 2)
                bitmap = bitmaps.get(2);
        } else if (id == R.id.image_View_4) {
            if (bitmaps.size() > 3)
                bitmap = bitmaps.get(3);
        }
        if (bitmap != null) {
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            HighQualityFragment highQualityFragment =
                    HighQualityFragment.newInstance(bitmap);
            highQualityFragment.show(fragmentTransaction, "Image # 1");
        }
        return false;
    };

    View.OnClickListener onPickerClickListener = v -> {
        int id = v.getId();
        if (id == R.id.image_View_1) {
            replace = imageNumber > 1;
            if (replace) {
                imageNumberTemp = 1;
            }
        } else if (id == R.id.image_View_2) {
            replace = imageNumber > 2;
            if (replace) {
                imageNumberTemp = 2;
            }
        } else if (id == R.id.image_View_3) {
            replace = imageNumber > 3;
            if (replace) {
                imageNumberTemp = 3;
            }
        } else if (id == R.id.image_View_4) {
            replace = imageNumber > 4;
            if (replace) {
                imageNumberTemp = 4;
            }
        }
        imagePicker();
    };

    View.OnClickListener onDeleteClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            boolean b = false;
            int id = v.getId();
            if (id == R.id.image_View_delete_1) {
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
            } else if (id == R.id.image_View_delete_2) {
                if (imageNumber > 2) {
                    removeImage(1);
                    b = true;
                    binding.imageView2.setImageBitmap(((BitmapDrawable)
                            binding.imageView3.getDrawable()).getBitmap());
                    binding.imageView3.setImageBitmap(((BitmapDrawable)
                            binding.imageView4.getDrawable()).getBitmap());
                }
            } else if (id == R.id.image_View_delete_3) {
                if (imageNumber > 3) {
                    removeImage(2);
                    b = true;
                    binding.imageView3.setImageBitmap(((BitmapDrawable)
                            binding.imageView4.getDrawable()).getBitmap());
                }
            } else if (id == R.id.image_View_delete_4) {
                if (imageNumber > 4) {
                    removeImage(3);
                    b = true;
                }
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
        if (PermissionManager.checkCameraPermission(getApplicationContext()))
            initialize();
        else askCameraPermission();
    }

    void initialize() {
        sharedPreferenceManager = new SharedPreferenceManager(activity, SharedReferenceNames.ACCOUNT.getValue());
        if (getIntent().getExtras() != null) {
            uuid = getIntent().getExtras().getString(BundleEnum.BILL_ID.getValue());
            position = getIntent().getExtras().getInt(BundleEnum.POSITION.getValue());
            trackNumber = getIntent().getExtras().getInt(BundleEnum.TRACKING.getValue());
            result = getIntent().getExtras().getBoolean(BundleEnum.IMAGE.getValue());
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

    void imageSetup() {
        images = new ArrayList<>();
        if (!result)
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

    void imagePicker() {
        AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(TakePhotoActivity.this, R.style.AlertDialogCustom));
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
                    e.printStackTrace();
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

    void setOnImageViewDeleteClickListener() {
        binding.imageViewDelete1.setOnClickListener(onDeleteClickListener);
        binding.imageViewDelete2.setOnClickListener(onDeleteClickListener);
        binding.imageViewDelete3.setOnClickListener(onDeleteClickListener);
        binding.imageViewDelete4.setOnClickListener(onDeleteClickListener);
    }

    void removeImage(int index) {
        imageNumber = imageNumber - 1;
        bitmaps.remove(index);
        MyDatabaseClient.getInstance(activity).getMyDatabase().imageDao().
                deleteImage(images.get(index).id);
        images.remove(index);
    }

    void setOnButtonSendClickListener() {
        binding.buttonSaveSend.setOnClickListener(v ->
                new PrepareMultimedia(activity, position, result, bitmaps, images,
                        binding.editTextDescription.getText().toString().isEmpty() ?
                                getString(R.string.description) :
                                binding.editTextDescription.getText().toString()).
                        execute(activity));
    }

    void askCameraPermission() {
        PermissionListener permissionlistener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                new CustomToast().info(getString(R.string.access_granted));
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
                    if (Build.VERSION.SDK_INT > 28) {
                        ImageDecoder.Source source = ImageDecoder.createSource(this.getContentResolver(), Uri.parse(MyApplication.fileName));
                        MyApplication.bitmapSelectedImage = ImageDecoder.decodeBitmap(source);
                    } else
                        MyApplication.bitmapSelectedImage = MediaStore.Images.Media.getBitmap(
                                contentResolver, Uri.parse(MyApplication.fileName));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            Image image = new Image();
            image.OnOffLoadId = uuid;
            image.trackNumber = trackNumber;
            image.File = CustomFile.bitmapToFile(MyApplication.bitmapSelectedImage, activity);
            if (replace) {
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
        Debug.getNativeHeapAllocatedSize();
        System.runFinalization();
        Runtime.getRuntime().totalMemory();
        Runtime.getRuntime().freeMemory();
        Runtime.getRuntime().maxMemory();
        Runtime.getRuntime().gc();
        System.gc();
        super.onStop();
    }

    @Override
    protected void onDestroy() {
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
}