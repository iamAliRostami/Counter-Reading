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
import com.leon.counter_reading.tables.QotrDictionary;
import com.leon.counter_reading.tables.ReadingConfigDefaultDto;
import com.leon.counter_reading.tables.ReadingData;
import com.leon.counter_reading.tables.TrackingDto;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class ViewPagerAdapterReading extends FragmentStatePagerAdapter {
    private final ArrayList<OnOffLoadDto> onOffLoadDtos = new ArrayList<>();
    private final ArrayList<ReadingConfigDefaultDto> readingConfigDefaultDtos = new ArrayList<>();
    private final ArrayList<KarbariDto> karbariDtos = new ArrayList<>();
    private final ArrayList<CounterStateDto> counterStateDtos = new ArrayList<>();
    private final SpinnerCustomAdapter adapter;

    public ViewPagerAdapterReading(@NonNull FragmentManager fm,
                                   ReadingData readingData, Activity activity) {
        super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        onOffLoadDtos.addAll(readingData.onOffLoadDtos);
        final String[] items = new String[readingData.counterStateDtos.size()];
        for (int i = 0; i < readingData.counterStateDtos.size(); i++) {
            items[i] = readingData.counterStateDtos.get(i).title;
        }
        adapter = new SpinnerCustomAdapter(activity, items);
        counterStateDtos.addAll(readingData.counterStateDtos);
        for (int i = 0; i < readingData.onOffLoadDtos.size(); i++) {
            ArrayList<ReadingConfigDefaultDto> configDefaultDtos = readingData.readingConfigDefaultDtos;
            for (int j = 0, configDefaultDtosSize = configDefaultDtos.size(); j < configDefaultDtosSize; j++) {
                ReadingConfigDefaultDto readingConfigDefaultDto = configDefaultDtos.get(j);
                if (readingData.onOffLoadDtos.get(i).zoneId == readingConfigDefaultDto.zoneId)
                    readingConfigDefaultDtos.add(readingConfigDefaultDto);
            }
            ArrayList<KarbariDto> dtos = readingData.karbariDtos;
            for (int j = 0, dtosSize = dtos.size(); j < dtosSize; j++) {
                KarbariDto karbariDto = dtos.get(j);
                if (readingData.onOffLoadDtos.get(i).karbariCode == karbariDto.moshtarakinId)
                    karbariDtos.add(karbariDto);
            }
            ArrayList<QotrDictionary> dictionary = readingData.qotrDictionary;
            for (int j = 0, dictionarySize = dictionary.size(); j < dictionarySize; j++) {
                QotrDictionary qotrDictionary = dictionary.get(j);
                if (readingData.onOffLoadDtos.get(i).qotrCode == qotrDictionary.id)
                    readingData.onOffLoadDtos.get(i).qotr = qotrDictionary.title;
                if (readingData.onOffLoadDtos.get(i).sifoonQotrCode == qotrDictionary.id)
                    readingData.onOffLoadDtos.get(i).sifoonQotr = qotrDictionary.title;
            }
            ArrayList<TrackingDto> trackingDtos = readingData.trackingDtos;
            for (int j = 0, trackingDtosSize = trackingDtos.size(); j < trackingDtosSize; j++) {
                TrackingDto trackingDto = trackingDtos.get(j);
                if (readingData.onOffLoadDtos.get(i).trackNumber == trackingDto.trackNumber) {
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
                counterStateDtos, adapter, position);
    }

    @Override
    public int getCount() {
        return onOffLoadDtos.size();
    }

    @Override
    public int getItemPosition(@NotNull Object object) {
        return POSITION_NONE;
    }

    @Override
    public void destroyItem(@NotNull ViewGroup container, int position, @NotNull Object object) {
        FragmentManager manager = ((Fragment) object).getParentFragmentManager();
        FragmentTransaction trans = manager.beginTransaction();
        trans.remove((Fragment) object);
        trans.commit();
        super.destroyItem(container, position, object);
    }
}