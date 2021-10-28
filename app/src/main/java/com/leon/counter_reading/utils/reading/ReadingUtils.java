package com.leon.counter_reading.utils.reading;

import static com.leon.counter_reading.helpers.Constants.readingData;

import com.leon.counter_reading.R;

public class ReadingUtils {
    /**
     * Create above image icons resource
     **/
    static public int[] setAboveIcons() {
        int[] imageSrc = new int[15];
        imageSrc[0] = R.drawable.img_default_level;
        imageSrc[1] = R.drawable.img_normal_level;
        imageSrc[2] = R.drawable.img_high_level;
        imageSrc[3] = R.drawable.img_low_level;
        imageSrc[4] = R.drawable.img_low_level;
        imageSrc[5] = R.drawable.img_visit_default;
        imageSrc[6] = R.drawable.img_visit;
        imageSrc[7] = R.drawable.img_writing;
        imageSrc[8] = R.drawable.img_successful_default;
        imageSrc[9] = R.drawable.img_successful;
        imageSrc[10] = R.drawable.img_mistake;
        imageSrc[11] = R.drawable.img_failure;
        imageSrc[12] = R.drawable.img_delete_temp;
        imageSrc[13] = R.drawable.img_construction;
        imageSrc[14] = R.drawable.img_broken_pipe;
        return imageSrc;
    }

    static public int setExceptionImage(int position) {
        for (int i = 0; i < readingData.counterStateDtos.size(); i++) {
            if (readingData.counterStateDtos.get(i).moshtarakinId ==
                    readingData.onOffLoadDtos.get(position).preCounterStateCode &&
                    readingData.counterStateDtos.get(i).isXarab) {
                return 14;
            }
        }

        for (int i = 0; i < readingData.karbariDtos.size(); i++) {
            if (readingData.karbariDtos.get(i).moshtarakinId ==
                    readingData.onOffLoadDtos.get(position).karbariCode &&
                    readingData.karbariDtos.get(i).isSaxt) {
                return 13;
            }
        }
        if (readingData.onOffLoadDtos.get(position).noeVagozariId == 4) {
            return 13;
        }

        if (readingData.onOffLoadDtos.get(position).hazf > 0) {
            return 12;
        }
        return -1;
    }
}
