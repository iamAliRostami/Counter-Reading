package com.leon.counter_reading.adapters;

import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.fragment.app.FragmentTransaction;

import com.leon.counter_reading.fragments.ReadingFragment;
import com.leon.counter_reading.fragments.ReadingFragment1;
import com.leon.counter_reading.tables.CounterStateDto;
import com.leon.counter_reading.tables.KarbariDto;
import com.leon.counter_reading.tables.QotrDictionary;
import com.leon.counter_reading.tables.ReadingConfigDefaultDto;
import com.leon.counter_reading.tables.ReadingData;
import com.leon.counter_reading.tables.TrackingDto;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class ViewPagerAdapterReading1 extends FragmentStatePagerAdapter {
    final ReadingData readingData;
    final ArrayList<ReadingConfigDefaultDto> readingConfigDefaultDtos = new ArrayList<>();
    final ArrayList<KarbariDto> karbariDtos = new ArrayList<>();
    final ArrayList<QotrDictionary> qotrDictionaries = new ArrayList<>();
    final ArrayList<TrackingDto> trackingDtos = new ArrayList<>();
    final ArrayList<CounterStateDto> counterStateDtos = new ArrayList<>();
    final ArrayList<String> items = new ArrayList<>();

    public ViewPagerAdapterReading1(@NonNull FragmentManager fm, int behavior,
                                    ReadingData readingData) {
        super(fm, behavior);
        this.readingData = readingData;
        for (int i = 0; i < readingData.counterStateDtos.size(); i++) {
            items.add(readingData.counterStateDtos.get(i).title);
        }
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
                    qotrDictionaries.add(qotrDictionary);
            }
            for (TrackingDto trackingDto : readingData.trackingDtos) {
                if (readingData.onOffLoadDtos.get(i).trackNumber == trackingDto.trackNumber)
                    trackingDtos.add(trackingDto);
            }
        }
        counterStateDtos.addAll(readingData.counterStateDtos);
    }

    @NotNull
    @Override
    public Fragment getItem(int position) {
        return ReadingFragment1.newInstance(readingData.onOffLoadDtos.get(position),
                readingConfigDefaultDtos.get(position), karbariDtos.get(position),
                trackingDtos.get(position), qotrDictionaries.get(position).title,
                 items, counterStateDtos, position);
    }

    @Override
    public int getCount() {
        return readingData.onOffLoadDtos.size();
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