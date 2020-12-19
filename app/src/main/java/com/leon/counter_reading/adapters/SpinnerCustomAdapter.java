package com.leon.counter_reading.adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckedTextView;

import com.leon.counter_reading.R;

import java.util.ArrayList;

public class SpinnerCustomAdapter extends BaseAdapter {
    ArrayList<String> items;
    LayoutInflater inflater;

    public SpinnerCustomAdapter(Activity activity, ArrayList<String> items) {
        super();
        this.items = items;
        inflater = (LayoutInflater.from(activity));
    }

    @SuppressLint({"ViewHolder", "InflateParams"})
    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        view = inflater.inflate(R.layout.item_dropdown_menu, null);
        CheckedTextView item = view.findViewById(R.id.checked_text_view);
        item.setText(items.get(position));
        return view;
    }

    @SuppressLint("InflateParams")
    @Override
    public View getDropDownView(int position, View view, ViewGroup parent) {
        view = inflater.inflate(R.layout.item_dropdown_menu_popup, null);
        CheckedTextView item = view.findViewById(R.id.checked_text_view);
        item.setText(items.get(position));
        return view;
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int i) {
        return items.get(i);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
}
