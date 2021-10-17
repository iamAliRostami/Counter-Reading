package com.leon.counter_reading.utils.backup_restore;

import com.leon.counter_reading.tables.KarbariDto;

public class KarbariDtoTemp {
    public int id;
    public int moshtarakinId;
    public int provinceId;
    public String title;

    public int isMaskooni;
    public int isSaxt;
    public int hasReadingVibrate;
    public int isTejari;

    public KarbariDto getKarbariDto() {
        KarbariDto karbariDto = new KarbariDto();
        karbariDto.id = id;
        karbariDto.moshtarakinId = moshtarakinId;
        karbariDto.provinceId = provinceId;
        karbariDto.title = title;

        karbariDto.isMaskooni = isMaskooni == 1;
        karbariDto.isSaxt = isSaxt == 1;
        karbariDto.hasReadingVibrate = hasReadingVibrate == 1;
        karbariDto.isTejari = isTejari == 1;
        return karbariDto;
    }
}