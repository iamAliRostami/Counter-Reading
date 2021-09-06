package com.leon.counter_reading.tables;

import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import java.util.ArrayList;

@Entity(tableName = "CounterStateDto", indices = @Index(value = {"customId"/*,"id","moshtarakinId"*/}, unique = true))
public class CounterStateDto {
    @PrimaryKey(autoGenerate = true)
    public int customId;
    public int id;
    public int moshtarakinId;
    public String title;
    public int zoneId;
    public int clientOrder;
    public boolean canEnterNumber;
    public boolean isMane;
    public boolean canNumberBeLessThanPre;
    public boolean isTavizi;
    public boolean shouldEnterNumber;
    public boolean isXarab;
    public boolean isFaqed;

    public static ArrayList<String> getCounterStateItems(ArrayList<CounterStateDto> counterStateDtos) {
        ArrayList<String> items = new ArrayList<>();
        for (CounterStateDto counterStateDto : counterStateDtos) {
            items.add(counterStateDto.title);
        }
        return items;
    }

    public static String[] getCounterStateItems(ArrayList<CounterStateDto> counterStateDtos,
                                                String[] customItems) {
        String[] items = new String[counterStateDtos.size() + 3];
        items[0] = customItems[0];
        for (int i = 0; i < counterStateDtos.size(); i++) {
            items[i + 1] = counterStateDtos.get(i).title;
        }
//        items[items.length - 1] = last;
        items[items.length - 2] = customItems[1];
        items[items.length - 1] = customItems[2];
        return items;
    }
}