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
                                                String first, String last) {
        String[] items = new String[counterStateDtos.size() + 2];
        items[0] = first;
        for (int i = 0; i < counterStateDtos.size(); i++) {
            items[i + 1] = counterStateDtos.get(i).title;
        }
        items[items.length - 1] = last;
        return items;
    }
}