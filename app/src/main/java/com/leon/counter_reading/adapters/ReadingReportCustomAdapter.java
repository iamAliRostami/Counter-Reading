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

import com.leon.counter_reading.R;
import com.leon.counter_reading.fragments.AhadFragment;
import com.leon.counter_reading.fragments.KarbariFragment;
import com.leon.counter_reading.fragments.TaviziFragment;
import com.leon.counter_reading.tables.CounterReportDto;
import com.leon.counter_reading.tables.OffLoadReport;
import com.leon.counter_reading.utils.MyDatabaseClient;

import java.util.ArrayList;

public class ReadingReportCustomAdapter extends BaseAdapter {
    ArrayList<CounterReportDto> counterReportDtos;
    ArrayList<OffLoadReport> offLoadReports;
    String uuid;
    int position;
    LayoutInflater inflater;
    Context context;

    public ReadingReportCustomAdapter(Context context, String uuid, int position,
                                      ArrayList<CounterReportDto> counterReportDtos,
                                      ArrayList<OffLoadReport> offLoadReports) {
        this.counterReportDtos = counterReportDtos;
        this.offLoadReports = offLoadReports;
        this.uuid = uuid;
        this.position = position;
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
        CheckBoxViewHolder holder;
        View view = convertView;
        if (view == null) {
            view = inflater.inflate(R.layout.item_public, null);
        }
        holder = new CheckBoxViewHolder(view);
        holder.checkBox.setText(counterReportDtos.get(position).title);
        holder.checkBox.setOnClickListener(view1 -> {
            holder.checkBox.setChecked(!holder.checkBox.isChecked());
            if (holder.checkBox.isChecked()) {
                OffLoadReport offLoadReport = new OffLoadReport();
                offLoadReport.reportId = counterReportDtos.get(position).id;
                offLoadReport.onOffLoadId = uuid;
                MyDatabaseClient.getInstance(context).getMyDatabase().offLoadReportDao().
                        insertOffLoadReport(offLoadReport);
                MyDatabaseClient.getInstance(context).getMyDatabase().onOffLoadDao().
                        updateOnOffLoad(true, uuid);
                offLoadReports.add(offLoadReport);
                if (counterReportDtos.get(position).isAhad) {
                    AhadFragment ahadFragment = AhadFragment.newInstance(uuid, position);
                    FragmentManager fragmentManager = ((AppCompatActivity) context).getSupportFragmentManager();
                    ahadFragment.show(fragmentManager, context.getString(R.string.ahad_number));
                }
                if (counterReportDtos.get(position).isTavizi) {
                    TaviziFragment taviziFragment = TaviziFragment.newInstance(uuid, position);
                    FragmentManager fragmentManager = ((AppCompatActivity) context).getSupportFragmentManager();
                    if (fragmentManager != null) {
                        taviziFragment.show(fragmentManager, context.getString(R.string.counter_serial));
                    }
                }
                if (counterReportDtos.get(position).isKarbari) {
                    KarbariFragment karbariFragment = KarbariFragment.newInstance(uuid, position);
                    FragmentManager fragmentManager = ((AppCompatActivity) context).getSupportFragmentManager();
                    if (fragmentManager != null) {
                        karbariFragment.show(fragmentManager, context.getString(R.string.karbari));
                    }
                }
            } else {
                for (int i = 0; i < offLoadReports.size(); i++) {
                    if (offLoadReports.get(i).reportId == counterReportDtos.get(position).id) {
                        MyDatabaseClient.getInstance(context).getMyDatabase().offLoadReportDao().
                                deleteOffLoadReport(offLoadReports.get(i).reportId, uuid);
                        offLoadReports.remove(offLoadReports.get(i));
                    }
                }
            }
            counterReportDtos.get(position).isSelected = holder.checkBox.isChecked();
        });

        holder.checkBox.setChecked(counterReportDtos.get(position).isSelected);
        return view;
    }

    static class CheckBoxViewHolder {
        CheckedTextView checkBox;
        CheckBoxViewHolder(View view) {
            checkBox = view.findViewById(android.R.id.text1);
        }
    }
}
