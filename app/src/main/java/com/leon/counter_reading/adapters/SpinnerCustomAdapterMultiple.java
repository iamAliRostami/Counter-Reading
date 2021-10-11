package com.leon.counter_reading.adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckedTextView;
import android.widget.TextView;

import com.leon.counter_reading.R;

public class SpinnerCustomAdapterMultiple extends BaseAdapter {
    private final String[] items;
    private final LayoutInflater inflater;

    public SpinnerCustomAdapterMultiple(Activity activity, String[] items) {
        super();
        this.items = items;
        inflater = (LayoutInflater.from(activity));
    }

    @SuppressLint({"ViewHolder", "InflateParams"})
    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        view = inflater.inflate(R.layout.item_dropdown_menu_multiple, null);
        CheckedTextView item = view.findViewById(R.id.text_view);
        item.setText(items[position]);
        return view;
    }

    @Override
    public int getCount() {
        return items.length;
    }

    @Override
    public Object getItem(int i) {
        return items[i];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
}
