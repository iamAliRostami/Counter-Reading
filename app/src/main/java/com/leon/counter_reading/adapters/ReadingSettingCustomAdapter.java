package com.leon.counter_reading.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckedTextView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.leon.counter_reading.R;
import com.leon.counter_reading.tables.TrackingDto;
import com.leon.counter_reading.utils.MyDatabaseClient;

import java.util.ArrayList;

public class ReadingSettingCustomAdapter extends BaseAdapter {
    private final ArrayList<TrackingDto> trackingDtos;
    private final LayoutInflater inflater;
    private final Context context;

    public ReadingSettingCustomAdapter(Context context, ArrayList<TrackingDto> trackingDtos) {
        this.trackingDtos = trackingDtos;
        this.context = context;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getViewTypeCount() {
        return getCount();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public int getCount() {
        return trackingDtos.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @SuppressLint("InflateParams")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            if (position % 2 == 1)
                convertView = inflater.inflate(R.layout.item_reading_setting_1, null);
            else
                convertView = inflater.inflate(R.layout.item_reading_setting_2, null);
        }
        ReadingSettingCheckBoxViewHolder holder = new ReadingSettingCheckBoxViewHolder(convertView);
        holder.textViewTrackNumber.setText(String.valueOf(trackingDtos.get(position).trackNumber));
        holder.textViewZoneTitle.setText(trackingDtos.get(position).zoneTitle);
        holder.textViewStartEshterak.setText(trackingDtos.get(position).fromEshterak);
        holder.textViewEndEshterak.setText(trackingDtos.get(position).toEshterak);
        holder.textViewStartDate.setText(trackingDtos.get(position).fromDate);
        holder.textViewEndDate.setText(trackingDtos.get(position).toDate);
        holder.textViewNumber.setText(String.valueOf(trackingDtos.get(position).itemQuantity));

        holder.linearLayout.setOnClickListener(view1 -> {
            holder.checkBox.setChecked(!holder.checkBox.isChecked());
            trackingDtos.get(position).isActive = holder.checkBox.isChecked();
            MyDatabaseClient.getInstance(context).getMyDatabase().
                    trackingDao().updateTrackingDtoByStatus(
                    trackingDtos.get(position).id, trackingDtos.get(position).isActive);
        });
        holder.checkBox.setChecked(trackingDtos.get(position).isActive);
        return convertView;
    }
}

class ReadingSettingCheckBoxViewHolder {
    final CheckedTextView checkBox;
    final LinearLayout linearLayout;
    final TextView textViewTrackNumber;
    final TextView textViewZoneTitle;
    final TextView textViewStartDate;
    final TextView textViewEndDate;
    final TextView textViewStartEshterak;
    final TextView textViewEndEshterak;
    final TextView textViewNumber;

    ReadingSettingCheckBoxViewHolder(View view) {
        checkBox = view.findViewById(android.R.id.text1);
        linearLayout = view.findViewById(R.id.linear_layout);
        textViewEndDate = view.findViewById(R.id.text_view_end_date);
        textViewStartDate = view.findViewById(R.id.text_view_start_date);
        textViewEndEshterak = view.findViewById(R.id.text_view_end_eshterak);
        textViewStartEshterak = view.findViewById(R.id.text_view_start_eshterak);
        textViewTrackNumber = view.findViewById(R.id.text_view_track_number);
        textViewZoneTitle = view.findViewById(R.id.text_view_zone_title);
        textViewNumber = view.findViewById(R.id.text_view_number);
    }
}