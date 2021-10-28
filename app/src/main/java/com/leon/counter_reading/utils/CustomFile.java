package com.leon.counter_reading.utils;

import static com.leon.counter_reading.helpers.Constants.MAX_IMAGE_SIZE;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.StrictMode;
import android.util.Log;
import android.widget.Toast;

import androidx.core.content.FileProvider;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.leon.counter_reading.BuildConfig;
import com.leon.counter_reading.helpers.MyApplication;
import com.leon.counter_reading.R;
import com.leon.counter_reading.tables.ReadingData;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Objects;
import java.util.Random;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;

public class CustomFile {

    public static boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }

    public static Bitmap loadImage(Context context, String address) {
        try {
            File f = new File(context.getExternalFilesDir(null), context.getString(R.string.camera_folder));
            f = new File(f, address);
            return BitmapFactory.decodeStream(new FileInputStream(f));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }


    @SuppressLint("SimpleDateFormat")
    public static MultipartBody.Part bitmapToFile(Bitmap bitmap, Context context) {
        String timeStamp = (new SimpleDateFormat(context.getString(R.string.save_format_name))).format(new Date());
        String fileNameToSave = "JPEG_" + new Random().nextInt() + "_" + timeStamp + ".jpg";
        File f = new File(context.getCacheDir(), fileNameToSave);
        try {
            f.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
//        long startTime = Calendar.getInstance().getTimeInMillis();
        byte[] bitmapData = compressBitmap(bitmap/*, MyApplication.MAX_IMAGE_SIZE*/);
//        long endTime = Calendar.getInstance().getTimeInMillis();
//        Log.e("Time 2", String.valueOf(endTime - startTime));
        FileOutputStream fos;
        try {
            fos = new FileOutputStream(f);
            fos.write(bitmapData);
            fos.flush();
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        RequestBody requestBody = RequestBody.create(f, MediaType.parse("image/jpeg"));
        return MultipartBody.Part.createFormData("File", f.getName(), requestBody);
    }

    public static byte[] compressBitmap(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        if (stream.toByteArray().length > MAX_IMAGE_SIZE) {
//            int qualityPercent = (int) (100 * ((double) MAX_IMAGE_SIZE / stream.toByteArray().length));
//            int qualityPercent = Math.max((int)
//                            (100 * ((double) MAX_IMAGE_SIZE / stream.toByteArray().length))
//                    , 20);
            int qualityPercent = Math.max((int) ((double)
                    stream.toByteArray().length / MAX_IMAGE_SIZE), 20);

            bitmap = Bitmap.createScaledBitmap(bitmap
                    , (int) ((double) bitmap.getWidth() * qualityPercent / 100)
                    , (int) ((double) bitmap.getHeight() * qualityPercent / 100), false);
            stream = new ByteArrayOutputStream();
//            bitmap.compress(Bitmap.CompressFormat.JPEG, Math.max(qualityPercent, 20), stream);
            bitmap.compress(Bitmap.CompressFormat.JPEG, /*qualityPercent*/100, stream);
        }
        return stream.toByteArray();
    }

    public static Bitmap rotateImage(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(),
                matrix, true);
    }

    public static byte[] compressBitmap(String file, int width, int height, int maxSizeBytes) {
        BitmapFactory.Options bmpFactoryOptions = new BitmapFactory.Options();
        bmpFactoryOptions.inJustDecodeBounds = true;
        Bitmap bitmap;

        int heightRatio = (int) Math.ceil(bmpFactoryOptions.outHeight / (float) height);
        int widthRatio = (int) Math.ceil(bmpFactoryOptions.outWidth / (float) width);

        if (heightRatio > 1 || widthRatio > 1) {
            bmpFactoryOptions.inSampleSize = Math.max(heightRatio, widthRatio);
        }

        bmpFactoryOptions.inJustDecodeBounds = false;
        bitmap = BitmapFactory.decodeFile(file, bmpFactoryOptions);

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        int currSize;
        int currQuality = 100;

        do {
            bitmap.compress(Bitmap.CompressFormat.JPEG, currQuality, stream);
            currSize = stream.toByteArray().length;
            // limit quality by 5 percent every time
            currQuality -= 5;

        } while (currSize >= maxSizeBytes);

        return stream.toByteArray();
    }

    public static String bitmapToBinary(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        return Arrays.toString(byteArray);
    }

    public static Bitmap binaryToBitmap(String s) {
        byte[] bytes = s.getBytes();
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }

    public static String saveTempBitmap(Bitmap bitmap, Context context) {
        if (isExternalStorageWritable()) {
            return saveImage(bitmap, context);
        } else {
            new CustomToast().warning(context.getString(R.string.error_external_storage_is_not_writable));
            return context.getString(R.string.error_external_storage_is_not_writable);
        }
    }

    @SuppressLint("SimpleDateFormat")
    static String saveImage(Bitmap bitmapImage, Context context) {
        File mediaStorageDir = new File(context.getExternalFilesDir(null) + context.getString(R.string.camera_folder));
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                return null;
            }
        }
        String timeStamp = (new SimpleDateFormat(
                context.getString(R.string.save_format_name_melli))).format(new Date());
        String fileNameToSave = "JPEG_" + timeStamp + ".jpg";
        File file = new File(mediaStorageDir, fileNameToSave);
        if (file.exists()) file.delete();
        try {
            FileOutputStream out = new FileOutputStream(file);
            bitmapImage.compress(Bitmap.CompressFormat.JPEG, 40, out);
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        MediaScannerConnection.scanFile(context, new String[]{file.getPath()}, new String[]{"image/jpeg"}, null);
        return fileNameToSave;
    }

    @SuppressLint({"SimpleDateFormat"})
    public static File createImageFileOld(Context context) throws IOException {
        String timeStamp = (new SimpleDateFormat(context.getString(R.string.save_format_name))).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
//        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File storageDir = new File(String.valueOf(context.getExternalFilesDir(
                Environment.DIRECTORY_PICTURES)));
        storageDir.mkdirs();
        File image = File.createTempFile(imageFileName, ".jpg", storageDir);
        StringBuilder stringBuilder = (new StringBuilder()).append("file:");
        Objects.requireNonNull(image);
        return image;
    }

    @SuppressLint({"SimpleDateFormat"})
    public static File createImageFile(Context context) throws IOException {
        // Create an image file name
        String timeStamp = (new SimpleDateFormat(context.getString(R.string.save_format_name))).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        return File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
    }

    @SuppressLint({"SimpleDateFormat"})
    public static String createAudioFile(Context context) {
        File storageDir = new File(context.getExternalFilesDir(null).getAbsolutePath() + context.getString(R.string.audio_folder));
        storageDir.mkdirs();
        String timeStamp = (new SimpleDateFormat(
                context.getString(R.string.save_format_name))).format(new Date());
        String audioFileName = "audio_" + timeStamp;
        return context.getExternalFilesDir(null).getAbsolutePath() +
                context.getString(R.string.audio_folder) + audioFileName + ".ogg";
    }

    public static MultipartBody.Part prepareVoiceToSend(String fileName) {
        File file = new File(fileName);
        RequestBody requestFile = RequestBody.create(file, MediaType.parse(("multipart/form-data")));
        return MultipartBody.Part.createFormData("FILE", file.getName(), requestFile);
    }

    static File findFile(File dir, String name) {
        File[] children = dir.listFiles();
        if (children != null) {
            for (File child : children) {
                if (child.isDirectory()) {
                    File found = findFile(child, name);
                    if (found != null) return found;
                } else {
                    if (name.equals(child.getName())) return child;
                }
            }
        }
        return null;
    }

    public static ReadingData readData() {
        File root = Environment.getExternalStorageDirectory();
        File file = findFile(root, "json.txt");

        StringBuilder text = new StringBuilder();
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;
            while ((line = br.readLine()) != null) {
                text.append(line);
                text.append('\n');
            }
            br.close();
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
        String json = text.toString();
//        Log.e("json", json);

        Gson gson = new GsonBuilder().create();
        return gson.fromJson(json, ReadingData.class);
    }

    @SuppressLint({"SimpleDateFormat", "SetTextI18n"})
    public static boolean writeResponseApkToDisk(ResponseBody body, Activity activity) {
        if (isExternalStorageWritable()) {
            try {
                File storageDir = new File(String.valueOf(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)));
                if (!storageDir.exists()) {
                    if (!storageDir.mkdirs()) {
                        return false;
                    }
                }
                String timeStamp = (new SimpleDateFormat(
                        activity.getString(R.string.save_format_name))).format(new Date());
                String fileName = activity.getPackageName().substring(
                        activity.getPackageName().lastIndexOf(".") + 1) + "_" +
                        DifferentCompanyManager.getActiveCompanyName().toString() +
                        "_" + timeStamp + ".apk";
                File futureStudioIconFile = new File(storageDir, fileName);
//                String root = Environment.getExternalStorageDirectory().toString();
//                File futureStudioIconFile = new File(root +
//                        File.separator + "Download" + File.separator + fileName);
                InputStream inputStream = null;
                OutputStream outputStream = null;
                try {
                    byte[] fileReader = new byte[7168];
                    long fileSize = body.contentLength();
                    long fileSizeDownloaded = 0;
                    inputStream = body.byteStream();
                    outputStream = new FileOutputStream(futureStudioIconFile);
                    while (true) {
                        int read = inputStream.read(fileReader);
                        if (read == -1) {
                            break;
                        }
                        outputStream.write(fileReader, 0, read);
                        fileSizeDownloaded += read;
                        Log.e(".apk file", "file download: " + fileSizeDownloaded + " of " + fileSize);
                    }
                    outputStream.flush();
                    new CustomToast().success(
                            activity.getString(R.string.file_downloaded), Toast.LENGTH_LONG);
                    runFile(activity, fileName);
                    return true;
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    if (inputStream != null) {
                        inputStream.close();
                    }
                    if (outputStream != null) {
                        outputStream.close();
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            new CustomToast().warning(
                    activity.getString(R.string.error_external_storage_is_not_writable));
        }
        return false;
    }

    public static void runFile(Activity activity, String fileName) {
        StrictMode.VmPolicy.Builder newBuilder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(newBuilder.build());//TODO Create directory
        File storageDir = new File(String.valueOf(Environment
                .getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)));
        File toInstall = new File(storageDir, fileName);
        Intent intent;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Uri apkUri = FileProvider.getUriForFile(activity, BuildConfig.APPLICATION_ID +
                    ".provider", toInstall);
            intent = new Intent(Intent.ACTION_INSTALL_PACKAGE);
            intent.setData(apkUri);
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        } else {
            Uri apkUri = Uri.fromFile(toInstall);
            intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(apkUri, "application/vnd.android.package-archive");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        activity.startActivity(intent);
    }
}
