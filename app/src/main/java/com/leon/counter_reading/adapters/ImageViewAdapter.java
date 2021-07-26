package com.leon.counter_reading.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;

import com.leon.counter_reading.R;
import com.leon.counter_reading.fragments.HighQualityFragment;
import com.leon.counter_reading.tables.Image;
import com.leon.counter_reading.utils.CustomFile;

import java.util.ArrayList;

import static com.leon.counter_reading.MyApplication.IMAGE_NUMBER;

public class ImageViewAdapter extends BaseAdapter {
    public ArrayList<Image> images;
    LayoutInflater inflater;
    Context context;
    ImageViewHolder holder;

    public ImageViewAdapter(Context c, ArrayList<Image> images) {
        this.images = images;
        context = c;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public int getCount() {
        return IMAGE_NUMBER;
    }

    public Object getItem(int position) {
        return position;
    }

    public long getItemId(int position) {
        return images.size();
    }

    @SuppressLint("InflateParams")
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            view = inflater.inflate(R.layout.item_image, null);
        }
        holder = new ImageViewHolder(view);
        holder.imageView.setOnClickListener(view1 -> {

        });
        if (position < images.size()) {
            holder.imageView.setImageBitmap(CustomFile.loadImage(context, images.get(position).address));
            holder.imageView.setOnLongClickListener(v -> {
                FragmentTransaction fragmentTransaction =
                        ((FragmentActivity) context).getSupportFragmentManager().beginTransaction();
                HighQualityFragment highQualityFragment =
                        HighQualityFragment.newInstance(CustomFile.loadImage(context, images.get(position).address));
                highQualityFragment.show(fragmentTransaction, "Image # 1");

                return false;
            });
        }

        return view;
    }

    public static class ImageViewHolder {

        public ImageView imageView;

        public ImageViewHolder(View view) {
            imageView = view.findViewById(R.id.image_view);
        }

    }
}
