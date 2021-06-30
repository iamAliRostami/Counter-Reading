package com.leon.counter_reading.adapters;

import android.app.Activity;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.fragment.app.FragmentTransaction;

import com.leon.counter_reading.fragments.ReadingFragment;
import com.leon.counter_reading.tables.CounterStateDto;
import com.leon.counter_reading.tables.KarbariDto;
import com.leon.counter_reading.tables.OnOffLoadDto;
import com.leon.counter_reading.tables.OnOffLoadReading;
import com.leon.counter_reading.tables.QotrDictionary;
import com.leon.counter_reading.tables.ReadingConfigDefaultDto;
import com.leon.counter_reading.tables.ReadingData;
import com.leon.counter_reading.tables.TrackingDto;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class ViewPagerAdapterReading extends FragmentStatePagerAdapter {
//    final ReadingData readingData;
    final ArrayList<OnOffLoadDto> onOffLoadDtos = new ArrayList<>();
    final ArrayList<ReadingConfigDefaultDto> readingConfigDefaultDtos = new ArrayList<>();
    final ArrayList<KarbariDto> karbariDtos = new ArrayList<>();
    //    final ArrayList<QotrDictionary> qotrDictionaries = new ArrayList<>();
//    final ArrayList<TrackingDto> trackingDtos = new ArrayList<>();
    final ArrayList<CounterStateDto> counterStateDtos = new ArrayList<>();
    SpinnerCustomAdapter adapter;
//    ArrayList<OnOffLoadReading> onOffLoadReadings = new ArrayList<>();

    public ViewPagerAdapterReading(@NonNull FragmentManager fm, int behavior,
                                   ReadingData readingData, Activity activity) {
        super(fm, behavior);
//        this.readingData = readingData;
        onOffLoadDtos.addAll(readingData.onOffLoadDtos);
        final ArrayList<String> items = new ArrayList<>();
        for (int i = 0; i < readingData.counterStateDtos.size(); i++) {
            items.add(readingData.counterStateDtos.get(i).title);
        }
        adapter = new SpinnerCustomAdapter(activity, items);
        counterStateDtos.addAll(readingData.counterStateDtos);
        for (int i = 0; i < readingData.onOffLoadDtos.size(); i++) {
            for (ReadingConfigDefaultDto readingConfigDefaultDto : readingData.readingConfigDefaultDtos) {
                if (readingData.onOffLoadDtos.get(i).zoneId == readingConfigDefaultDto.zoneId)
                    readingConfigDefaultDtos.add(readingConfigDefaultDto);
            }
            for (KarbariDto karbariDto : readingData.karbariDtos) {
                if (readingData.onOffLoadDtos.get(i).karbariCode == karbariDto.moshtarakinId)
                    karbariDtos.add(karbariDto);
            }
            for (QotrDictionary qotrDictionary : readingData.qotrDictionary) {
                if (readingData.onOffLoadDtos.get(i).qotrCode == qotrDictionary.id)
                    readingData.onOffLoadDtos.get(i).qotr = qotrDictionary.title;
                if (readingData.onOffLoadDtos.get(i).sifoonQotrCode == qotrDictionary.id)
                    readingData.onOffLoadDtos.get(i).sifoonQotr = qotrDictionary.title;
            }
            for (TrackingDto trackingDto : readingData.trackingDtos) {
                if (readingData.onOffLoadDtos.get(i).trackNumber == trackingDto.trackNumber) {
//                    trackingDtos.add(trackingDto);
                    readingData.onOffLoadDtos.get(i).hasPreNumber = trackingDto.hasPreNumber;
                    readingData.onOffLoadDtos.get(i).displayBillId = trackingDto.displayBillId;
                    readingData.onOffLoadDtos.get(i).displayRadif = trackingDto.displayRadif;
                }
            }
        }
    }

    @NotNull
    @Override
    public Fragment getItem(int position) {
        return ReadingFragment.newInstance(onOffLoadDtos.get(position),
                readingConfigDefaultDtos.get(position), karbariDtos.get(position),
                /*trackingDtos.get(position), qotrDictionaries.get(position).title,*
                /*items,*/ counterStateDtos, adapter, position);
    }

    @Override
    public int getCount() {
        return onOffLoadDtos.size();
    }

    @Override
    public int getItemPosition(@NotNull Object object) {
//        notifyDataSetChanged();
        return POSITION_NONE;
    }

    @Override
    public void destroyItem(@NotNull ViewGroup container, int position, @NotNull Object object) {
        FragmentManager manager = ((Fragment) object).getFragmentManager();
        FragmentTransaction trans;
        if (manager != null) {
            trans = manager.beginTransaction();
            trans.remove((Fragment) object);
            trans.commit();
        }
        super.destroyItem(container, position, object);
    }
}