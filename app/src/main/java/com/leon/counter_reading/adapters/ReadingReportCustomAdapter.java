package com.leon.counter_reading.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckedTextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import com.leon.counter_reading.helpers.MyApplication;
import com.leon.counter_reading.R;
import com.leon.counter_reading.fragments.AhadFragment;
import com.leon.counter_reading.fragments.KarbariFragment;
import com.leon.counter_reading.fragments.TaviziFragment;
import com.leon.counter_reading.tables.CounterReportDto;
import com.leon.counter_reading.tables.OffLoadReport;
import com.leon.counter_reading.utils.DifferentCompanyManager;

import java.util.ArrayList;

public class ReadingReportCustomAdapter extends BaseAdapter {
    private final ArrayList<CounterReportDto> counterReportDtos;
    private final ArrayList<OffLoadReport> offLoadReports;
    private final String uuid;
    private final LayoutInflater inflater;
    private final Context context;
    private final int tracking;

    public ReadingReportCustomAdapter(Context context, String uuid, int tracking,
                                      ArrayList<CounterReportDto> counterReportDtos,
                                      ArrayList<OffLoadReport> offLoadReports) {
        this.counterReportDtos = counterReportDtos;
        this.offLoadReports = offLoadReports;
        this.uuid = uuid;
        this.tracking = tracking;
        this.context = context;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return counterReportDtos.size();
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
        ReadingReportCheckBoxViewHolder holder;
        View view = convertView;
        if (view == null) {
            view = inflater.inflate(R.layout.item_public, null);
        }
        holder = new ReadingReportCheckBoxViewHolder(view);
        holder.checkBox.setText(counterReportDtos.get(position).title);
        holder.checkBox.setOnClickListener(view1 -> {
            holder.checkBox.setChecked(!holder.checkBox.isChecked());
            if (holder.checkBox.isChecked()) {
                OffLoadReport offLoadReport = new OffLoadReport(uuid, tracking,
                        counterReportDtos.get(position).id);
                MyApplication.getApplicationComponent().MyDatabase()
                        .offLoadReportDao().insertOffLoadReport(offLoadReport);
                offLoadReports.add(offLoadReport);
                FragmentManager fragmentManager = ((AppCompatActivity) context).getSupportFragmentManager();
                if (counterReportDtos.get(position).isAhad) {
                    AhadFragment ahadFragment = AhadFragment.newInstance(uuid, position);
                    ahadFragment.show(fragmentManager, context.getString(R.string.number).concat(DifferentCompanyManager.getAhad(DifferentCompanyManager.getActiveCompanyName())));
                }
                if (counterReportDtos.get(position).isTavizi) {
                    TaviziFragment taviziFragment = TaviziFragment.newInstance(uuid);
                    taviziFragment.show(fragmentManager, context.getString(R.string.counter_serial));
                }
                if (counterReportDtos.get(position).isKarbari) {
                    KarbariFragment karbariFragment = KarbariFragment.newInstance(uuid, position);
                    karbariFragment.show(fragmentManager, context.getString(R.string.karbari));
                }
            } else {
                for (int i = 0; i < offLoadReports.size(); i++) {
                    if (offLoadReports.get(i).reportId == counterReportDtos.get(position).id) {
                        MyApplication.getApplicationComponent().MyDatabase().offLoadReportDao().
                                deleteOffLoadReport(offLoadReports.get(i).reportId, tracking, uuid);
                        offLoadReports.remove(offLoadReports.get(i));
                    }
                }
            }
            counterReportDtos.get(position).isSelected = holder.checkBox.isChecked();
        });

        holder.checkBox.setChecked(counterReportDtos.get(position).isSelected);
        return view;
    }


}

class ReadingReportCheckBoxViewHolder {
    final CheckedTextView checkBox;

    ReadingReportCheckBoxViewHolder(View view) {
        checkBox = view.findViewById(android.R.id.text1);
    }
}
