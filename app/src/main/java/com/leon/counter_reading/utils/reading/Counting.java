package com.leon.counter_reading.utils.reading;

import static com.leon.counter_reading.utils.CalendarTool.findDifferentDays;

import com.leon.counter_reading.tables.KarbariDto;
import com.leon.counter_reading.tables.OnOffLoadDto;
import com.leon.counter_reading.tables.ReadingConfigDefaultDto;

public class Counting {

    public static double dailyAverage(int preNumber, int currentNumber, String preDate) {
        return (currentNumber - preNumber) / (double) findDifferentDays(preDate);
    }

    public static double monthlyAverage(int preNumber, int currentNumber, String preDate) {
        return dailyAverage(preNumber, currentNumber, preDate) * 30;
    }

    public static double monthlyAverage(int preNumber, int currentNumber, String preDate, int zarib) {
        return dailyAverage(preNumber, currentNumber, preDate) * 30 / zarib;
    }

    public static int checkHighLow(OnOffLoadDto onOffLoadDto,
                                   KarbariDto karbariDto,
                                   ReadingConfigDefaultDto readingConfigDefaultDto,
                                   int currentNumber) {
        double average = monthlyAverage(onOffLoadDto.preNumber, currentNumber, onOffLoadDto.preDate);
        double preAverage = onOffLoadDto.preAverage;
        int difference = currentNumber - onOffLoadDto.preNumber;
        /**
         * if (karbariDto.isMaskooni && karbariDto.isTejari) {
         // فرمول میانگین گیری عجیب احتمالا
         } else*/
        if (karbariDto.isMaskooni) {
            /**
             * ضرایب
             */
            average = monthlyAverage(onOffLoadDto.preNumber, currentNumber, onOffLoadDto.preDate, onOffLoadDto.ahadMaskooniOrAsli);
            if (readingConfigDefaultDto.highConstBoundMaskooni < difference)
                return 1;
            else if (readingConfigDefaultDto.lowConstBoundMaskooni > difference)
                return -1;
            else if ((100 + readingConfigDefaultDto.highPercentBoundMaskooni) * preAverage < (average * 100))
                return 1;
            else if ((100 - readingConfigDefaultDto.lowPercentBoundMaskooni) * preAverage > (average * 100))
                return -1;
        } else if (karbariDto.isTejari) {
            /**
             * محاسبه فقط تجاری ساده با ظرفیت
             */
            average = monthlyAverage(onOffLoadDto.preNumber, currentNumber, onOffLoadDto.preDate, onOffLoadDto.ahadTejariOrFari);
            if (readingConfigDefaultDto.highConstZarfiatBound < difference)
                return 1;
            else if (readingConfigDefaultDto.lowConstZarfiatBound > difference)
                return -1;
            else if ((100 + readingConfigDefaultDto.highPercentZarfiatBound) * preAverage < (average * 100))
                return 1;
            else if ((100 - readingConfigDefaultDto.lowPercentZarfiatBound) * preAverage > (average * 100))
                return -1;
        } else if (karbariDto.isSaxt || onOffLoadDto.noeVagozariId == 4) {
            if (readingConfigDefaultDto.highConstBoundSaxt < difference)
                return 1;
            else if (readingConfigDefaultDto.lowConstBoundSaxt > difference)
                return -1;
            else if ((100 + readingConfigDefaultDto.highPercentBoundSaxt) * preAverage < (average * 100))
                return 1;
            else if ((100 - readingConfigDefaultDto.lowPercentBoundSaxt) * preAverage > (average * 100))
                return -1;
        } else {
            if ((100 + readingConfigDefaultDto.highPercentRateBoundNonMaskooni) * preAverage < (average * 100))
                return 1;
            else if ((100 - readingConfigDefaultDto.lowPercentRateBoundNonMaskooni) * preAverage > (average * 100))
                return -1;
        }
        return 0;
    }

    public static int checkHighLowMakoos(OnOffLoadDto onOffLoadDto,
                                         KarbariDto karbariDto,
                                         ReadingConfigDefaultDto readingConfigDefaultDto,
                                         int currentNumber) {
        double average = monthlyAverage(currentNumber, onOffLoadDto.preNumber, onOffLoadDto.preDate);
        double preAverage = onOffLoadDto.preAverage;
        int difference = onOffLoadDto.preNumber - currentNumber;
        /**
         * if (karbariDto.isMaskooni && karbariDto.isTejari) {
         // فرمول میانگین گیری عجیب احتمالا
         } else
         */
        if (karbariDto.isMaskooni) {
            /**
             * ضرایب
             */
            average = monthlyAverage(onOffLoadDto.preNumber, currentNumber, onOffLoadDto.preDate, onOffLoadDto.ahadMaskooniOrAsli);
            if (readingConfigDefaultDto.highConstBoundMaskooni < difference)
                return 1;
            else if (readingConfigDefaultDto.lowConstBoundMaskooni > difference)
                return -1;
            else if ((100 + readingConfigDefaultDto.highPercentBoundMaskooni) * preAverage < (average * 100))
                return 1;
            else if ((100 - readingConfigDefaultDto.lowPercentBoundMaskooni) * preAverage > (average * 100))
                return -1;
        } else if (karbariDto.isTejari) {
            /**
             *  محاسبه فقط تجاری ساده با ظرفیت
             */
            average = monthlyAverage(onOffLoadDto.preNumber, currentNumber, onOffLoadDto.preDate, onOffLoadDto.ahadTejariOrFari);
            if (readingConfigDefaultDto.highConstZarfiatBound < difference)
                return 1;
            else if (readingConfigDefaultDto.lowConstZarfiatBound > difference)
                return -1;
            else if ((100 + readingConfigDefaultDto.highPercentZarfiatBound) * preAverage < (average * 100))
                return 1;
            else if ((100 - readingConfigDefaultDto.lowPercentZarfiatBound) * preAverage > (average * 100))
                return -1;
        } else if (karbariDto.isSaxt || onOffLoadDto.noeVagozariId == 4) {
            if (readingConfigDefaultDto.highConstBoundSaxt < difference)
                return 1;
            else if (readingConfigDefaultDto.lowConstBoundSaxt > difference)
                return -1;
            else if ((100 + readingConfigDefaultDto.highPercentBoundSaxt) * preAverage < (average * 100))
                return 1;
            else if ((100 - readingConfigDefaultDto.lowPercentBoundSaxt) * preAverage > (average * 100))
                return -1;
        } else {
            if ((100 + readingConfigDefaultDto.highPercentRateBoundNonMaskooni) * preAverage < (average * 100))
                return 1;
            else if ((100 - readingConfigDefaultDto.lowPercentRateBoundNonMaskooni) * preAverage > (average * 100))
                return -1;
        }
        return 0;
    }
}