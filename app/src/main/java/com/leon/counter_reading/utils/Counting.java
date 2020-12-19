package com.leon.counter_reading.utils;

import com.leon.counter_reading.tables.KarbariDto;
import com.leon.counter_reading.tables.OnOffLoadDto;
import com.leon.counter_reading.tables.ReadingConfigDefaultDto;

import static com.leon.counter_reading.utils.CalendarTool.findDifferentDays;

public class Counting {

    public static double dailyAverage(int preNumber, int currentNumber, String preDate) {
        return (currentNumber - preNumber) / (double) findDifferentDays(preDate);
    }

    public static double monthlyAverage(int preNumber, int currentNumber, String preDate) {
        return dailyAverage(preNumber, currentNumber, preDate) * 30;
    }

    public static int checkHighLow(OnOffLoadDto onOffLoadDto,
                                   KarbariDto karbariDto,
                                   ReadingConfigDefaultDto readingConfigDefaultDto,
                                   int currentNumber) {
        double average = monthlyAverage(onOffLoadDto.preNumber, currentNumber, onOffLoadDto.preDate);
        double preAverage = onOffLoadDto.preAverage;
        int difference = currentNumber - onOffLoadDto.preNumber;
        if (karbariDto.isMaskooni) {
            if (readingConfigDefaultDto.highConstBoundMaskooni < difference)
                return 1;
            else if (readingConfigDefaultDto.lowConstBoundMaskooni > difference)
                return -1;
            else if ((100 + readingConfigDefaultDto.highPercentBoundMaskooni) * preAverage < (average * 100))
                return 1;
            else if ((100 - readingConfigDefaultDto.lowPercentBoundMaskooni) * preAverage > (average * 100))
                return -1;
        } else if (karbariDto.isSaxt) {
            if (readingConfigDefaultDto.highConstBoundSaxt < difference)
                return 1;
            else if (readingConfigDefaultDto.lowConstBoundSaxt > difference)
                return -1;
            else if ((100 + readingConfigDefaultDto.highPercentBoundSaxt) * preAverage < (average * 100))
                return 1;
            else if ((100 - readingConfigDefaultDto.lowPercentBoundSaxt) * preAverage > (average * 100))
                return -1;
        } else if (onOffLoadDto.ahadTejariOrFari > 0) {
            if ((100 + readingConfigDefaultDto.highPercentRateBoundNonMaskooni) * preAverage < (average * 100))
                return 1;
            else if ((100 - readingConfigDefaultDto.lowPercentRateBoundNonMaskooni) * preAverage > (average * 100))
                return -1;
        }
        return 0;
    }
}