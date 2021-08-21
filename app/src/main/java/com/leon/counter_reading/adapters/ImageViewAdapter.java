package com.leon.counter_reading.adapters;

import static com.leon.counter_reading.MyApplication.photoURI;
import static com.leon.counter_reading.activities.TakePhotoActivity.replace;
import static com.leon.counter_reading.utils.CustomFile.createImageFile;

import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.appcompat.view.ContextThemeWrapper;
import androidx.core.content.FileProvider;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;

import com.leon.counter_reading.BuildConfig;
import com.leon.counter_reading.MyApplication;
import com.leon.counter_reading.R;
import com.leon.counter_reading.activities.TakePhotoActivity;
import com.leon.counter_reading.fragments.HighQualityFragment;
import com.leon.counter_reading.tables.Image;
import com.leon.counter_reading.utils.DifferentCompanyManager;
import com.leon.counter_reading.utils.MyDatabaseClient;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class ImageViewAdapter extends BaseAdapter {
    private final ArrayList<Image> images;
    private final LayoutInflater inflater;
    private final Context context;

    public ImageViewAdapter(Context c, ArrayList<Image> images) {
        this.images = images;
        context = c;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public int getCount() {
        return DifferentCompanyManager.getImageNumber(DifferentCompanyManager.getActiveCompanyName());
    }

    @Override
    public Object getItem(int position) {
        return images.get(position);
    }

    @Override
    public long getItemId(int position) {
        return images.size();
    }

    @SuppressLint("InflateParams")
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            view = inflater.inflate(R.layout.item_image, null);
        }
        ImageViewHolder holder = new ImageViewHolder(view);
        holder.imageViewDelete.setVisibility(position < images.size() ? View.VISIBLE : View.GONE);
        holder.imageViewSent.setVisibility(position < images.size() && images.get(position).isSent ?
                View.VISIBLE : View.GONE);
        holder.imageView.setOnClickListener(view1 -> {
            replace = position < images.size() ? position + 1 : 0;
            imagePicker();
        });

        if (position < images.size()) {
            final Bitmap[] bitmap;
            bitmap = new Bitmap[]{images.get(position).bitmap};
            holder.imageView.setImageBitmap(bitmap[0]);
            holder.imageView.setOnLongClickListener(v -> {
                if (bitmap[0] != null) {
                    FragmentTransaction fragmentTransaction =
                            ((FragmentActivity) context).getSupportFragmentManager().beginTransaction();
                    HighQualityFragment highQualityFragment =
                            HighQualityFragment.newInstance(bitmap[0]);
                    highQualityFragment.show(fragmentTransaction, "Image # 1");
                }
                return false;
            });
            holder.imageViewDelete.setOnClickListener(v -> {
                MyDatabaseClient.getInstance(context).getMyDatabase().imageDao().
                        deleteImage(images.get(position).id);
                images.remove(position);
                notifyDataSetChanged();
                bitmap[0] = null;
            });
        } else {
            holder.imageView.setImageDrawable(AppCompatResources.getDrawable(context, R.drawable.img_camera));

        }
        return view;
    }

    void imagePicker() {
        AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(context, R.style.AlertDialogCustom));
        builder.setTitle(R.string.choose_document);
        builder.setMessage(R.string.select_source);
        builder.setPositiveButton(R.string.gallery, (dialog, which) -> {
            dialog.dismiss();
            Intent intent = new Intent("android.intent.action.PICK");
            intent.setType("image/*");
            ((TakePhotoActivity) (context)).startActivityForResult(intent, MyApplication.GALLERY_REQUEST);
        });
        builder.setNegativeButton(R.string.camera, (dialog, which) -> {
            dialog.dismiss();
            Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (cameraIntent.resolveActivity(context.getPackageManager()) != null) {
                // Create the File where the photo should go
                File photoFile = null;
                try {
                    photoFile = createImageFile(context);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                // Continue only if the File was successfully created
                if (photoFile != null) {
                    photoURI = FileProvider.getUriForFile(context,
                            BuildConfig.APPLICATION_ID.concat(".provider"),
                            photoFile);
                    cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                    try {
                        ((TakePhotoActivity) (context)).startActivityForResult(cameraIntent, MyApplication.CAMERA_REQUEST);
                    } catch (ActivityNotFoundException e) {
                        e.printStackTrace();
                        // display error state to the user
                    }
                }
            }
        });
        builder.setNeutralButton("", (dialog, which) -> dialog.dismiss());
        builder.create().show();
    }

    static class ImageViewHolder {

        public ImageView imageView, imageViewDelete, imageViewSent;

        public ImageViewHolder(View view) {
            imageView = view.findViewById(R.id.image_view);
            imageViewSent = view.findViewById(R.id.image_View_sent);
            imageViewDelete = view.findViewById(R.id.image_View_delete);
        }
    }
}
