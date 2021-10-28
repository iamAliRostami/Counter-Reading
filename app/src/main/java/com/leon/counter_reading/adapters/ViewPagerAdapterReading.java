package com.leon.counter_reading.adapters;

import android.app.Activity;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.fragment.app.FragmentTransaction;

import com.leon.counter_reading.helpers.MyApplication;
import com.leon.counter_reading.R;
import com.leon.counter_reading.fragments.ReadingFragment;
import com.leon.counter_reading.tables.CounterStateDto;
import com.leon.counter_reading.tables.KarbariDto;
import com.leon.counter_reading.tables.OnOffLoadDto;
import com.leon.counter_reading.tables.ReadingConfigDefaultDto;
import com.leon.counter_reading.tables.ReadingData;
import com.leon.counter_reading.utils.CustomToast;

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
            int k = 0;
            boolean found = false;
            while (!found && k < readingData.readingConfigDefaultDtos.size()) {
                if (readingData.onOffLoadDtos.get(i).zoneId == readingData.readingConfigDefaultDtos.get(k).zoneId) {
                    readingConfigDefaultDtos.add(readingData.readingConfigDefaultDtos.get(k));
                    found = true;
                }
                k++;
            }
            k = 0;
            found = false;
            while (!found && k < readingData.karbariDtos.size()) {
                if (readingData.onOffLoadDtos.get(i).karbariCode == readingData.karbariDtos.get(k).moshtarakinId) {
                    karbariDtos.add(readingData.karbariDtos.get(k));
                    found = true;
                }
                k++;
            }
            k = 0;
            found = false;
            while (!found && k < readingData.trackingDtos.size()) {
                if (readingData.onOffLoadDtos.get(i).trackNumber == readingData.trackingDtos.get(k).trackNumber) {
                    readingData.onOffLoadDtos.get(i).hasPreNumber = readingData.trackingDtos.get(k).hasPreNumber;
                    readingData.onOffLoadDtos.get(i).displayBillId = readingData.trackingDtos.get(k).displayBillId;
                    readingData.onOffLoadDtos.get(i).displayRadif = readingData.trackingDtos.get(k).displayRadif;
                    found = true;
                }
                k++;
            }
            for (int j = 0; j < readingData.qotrDictionary.size(); j++) {
                if (readingData.onOffLoadDtos.get(i).qotrCode == readingData.qotrDictionary.get(j).id)
                    readingData.onOffLoadDtos.get(i).qotr = readingData.qotrDictionary.get(j).title;
                if (readingData.onOffLoadDtos.get(i).sifoonQotrCode == readingData.qotrDictionary.get(j).id)
                    readingData.onOffLoadDtos.get(i).sifoonQotr = readingData.qotrDictionary.get(j).title;
            }
        }
    }

    @NotNull
    @Override
    public Fragment getItem(int position) {
        try {
            return ReadingFragment.newInstance(onOffLoadDtos.get(position),
                    readingConfigDefaultDtos.get(position), karbariDtos.get(position),
                    counterStateDtos, adapter, position);
        } catch (Exception e) {
            new CustomToast().error(MyApplication.getContext().getString(R.string.error_download_data), Toast.LENGTH_LONG);
        }
        return null;
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