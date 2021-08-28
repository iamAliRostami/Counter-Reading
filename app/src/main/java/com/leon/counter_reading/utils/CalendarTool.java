package com.leon.counter_reading.utils;

import android.annotation.SuppressLint;

import org.jetbrains.annotations.NotNull;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Objects;

public class CalendarTool {
    static final String[] iranianWeekDayStr = {
            "دوشنبه",
            "سه شنبه",
            "چهارشنبه",
            "پنجشنبه",
            "جمعه",
            "شنبه",
            "یکشنبه"};
    static final String[] iranianMonthStr = new String[]{
            "NA",
            "فروردین",
            "اردیبهشت",
            "خرداد",
            "تیر",
            "مرداد",
            "شهریور",
            "مهر",
            "آبان",
            "آذر",
            "دی",
            "بهمن",
            "اسفند",
    };
    private int irYear;
    private int irMonth;
    private int irDay;
    private int gYear;
    private int gMonth;
    private int gDay;
    private int juYear;
    private int juMonth;
    private int juDay;
    private int leap;
    private int JDN;
    private int march;

    public CalendarTool() {
        Calendar calendar = new GregorianCalendar();
        setGregorianDate(calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH) + 1,
                calendar.get(Calendar.DAY_OF_MONTH));
    }

    public CalendarTool(int year, int month, int day) {
        setGregorianDate(year, month, day);
    }

    @SuppressLint("SimpleDateFormat")
    public static long findDifferentDays(String preDate) {
        CalendarTool calendarToolPre = new CalendarTool();
        if (preDate.length() == 8)
            calendarToolPre.setIranianDate(1300 + Integer.parseInt(preDate.substring(0, 2)),
                    Integer.parseInt(preDate.substring(3, 5)),
                    Integer.parseInt(preDate.substring(6, 8)));
        else calendarToolPre.setIranianDate(Integer.parseInt(preDate.substring(0, 4)),
                Integer.parseInt(preDate.substring(5, 7)),
                Integer.parseInt(preDate.substring(8, 10)));

        String date = calendarToolPre.getGregorianYear() + "-";
        if (calendarToolPre.getGregorianMonth() < 10)
            date += "0";
        date += calendarToolPre.getGregorianMonth() + "-";
        if (calendarToolPre.getGregorianDay() < 10)
            date += "0";
        date += calendarToolPre.getGregorianDay();

        Date convertedDate = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        try {
            convertedDate = dateFormat.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Date date1 = new Date();
        long difference = Math.abs(date1.getTime() - Objects.requireNonNull(convertedDate).getTime());
        return difference / (24 * 60 * 60 * 1000);
    }

    public int getIranianYear() {
        return irYear;
    }

    public int getIranianMonth() {
        return irMonth;
    }

    public int getIranianDay() {
        return irDay;
    }

    public String getIranianWeekDayStr() {
        return (iranianWeekDayStr[getDayOfWeek()]);
    }

    public String getIranianMonthStr() {
        return (iranianMonthStr[getIranianMonth()]);
    }

    public int getGregorianYear() {
        return gYear;
    }

    public int getGregorianMonth() {
        return gMonth;
    }

    public int getGregorianDay() {
        return gDay;
    }

    public int getJulianYear() {
        return juYear;
    }

    public int getJulianMonth() {
        return juMonth;
    }

    public int getJulianDay() {
        return juDay;
    }

    public String getIranianDate() {
        return (irYear + "/" + irMonth + "/" + irDay);
    }

    public void setIranianDate(String jalaliAirDate) {
        String[] dateSplit = jalaliAirDate.split("/");
        int year = Integer.parseInt(dateSplit[0]);
        if (year < 1300)
            year += 1300;
        setIranianDate(year,
                Integer.parseInt(dateSplit[1]),
                Integer.parseInt(dateSplit[2]));
    }

    public String getGregorianDate() {
        return (gYear + "/" + gMonth + "/" + gDay);
    }

    public String getJulianDate() {
        return (juYear + "/" + juMonth + "/" + juDay);
    }

    public String getWeekDayStr() {
        String[] weekDayStr = {
                "دوشنبه",
                "سه شنبه",
                "چهارشنبه",
                "پنجشنبه",
                "جمعه",
                "شنبه",
                "یکشنبه"};
        return (weekDayStr[getDayOfWeek()]);
    }

    @NotNull
    public String toString() {
        return (getWeekDayStr() +
                ", Gregorian:[" + getGregorianDate() +
                "], Julian:[" + getJulianDate() +
                "], Iranian:[" + getIranianDate() + "]");
    }

    public int getDayOfWeek() {
        return (JDN % 7);
    }

    public void nextDay() {
        JDN++;
        JDNToIranian();
        JDNToJulian();
        JDNToGregorian();
    }

    public void nextDay(int days) {
        JDN += days;
        JDNToIranian();
        JDNToJulian();
        JDNToGregorian();
    }

    public void previousDay() {
        JDN--;
        JDNToIranian();
        JDNToJulian();
        JDNToGregorian();
    }

    public void previousDay(int days) {
        JDN -= days;
        JDNToIranian();
        JDNToJulian();
        JDNToGregorian();
    }

    public void setIranianDate(int year, int month, int day) {
        irYear = year;
        irMonth = month;
        irDay = day;
        JDN = IranianDateToJDN();
        JDNToIranian();
        JDNToJulian();
        JDNToGregorian();
    }

    public void setGregorianDate(int year, int month, int day) {
        gYear = year;
        gMonth = month;
        gDay = day;
        JDN = gregorianDateToJDN(year, month, day);
        JDNToIranian();
        JDNToJulian();
        JDNToGregorian();
    }

    public void setJulianDate(int year, int month, int day) {
        juYear = year;
        juMonth = month;
        juDay = day;
        JDN = julianDateToJDN(year, month, day);
        JDNToIranian();
        JDNToJulian();
        JDNToGregorian();
    }

    private void IranianCalendar() {
        // Iranian years starting the 33-year rule
        int[] Breaks =
                {-61, 9, 38, 199, 426, 686, 756, 818, 1111, 1181,
                        1210, 1635, 2060, 2097, 2192, 2262, 2324, 2394, 2456, 3178};
        int jm, N, leapJ, leapG, jp, j, jump;
        gYear = irYear + 621;
        leapJ = -14;
        jp = Breaks[0];
        // Find the limiting years for the Iranian year 'irYear'
        j = 1;
        do {
            jm = Breaks[j];
            jump = jm - jp;
            if (irYear >= jm) {
                leapJ += (jump / 33 * 8 + (jump % 33) / 4);
                jp = jm;
            }
            j++;
        } while ((j < 20) && (irYear >= jm));
        N = irYear - jp;
        // Find the number of leap years from AD 621 to the begining of the current
        // Iranian year in the Iranian (Jalali) calendar
        leapJ += (N / 33 * 8 + ((N % 33) + 3) / 4);
        if (((jump % 33) == 4) && ((jump - N) == 4))
            leapJ++;
        // And the same in the Gregorian date of Farvardin the first
        leapG = gYear / 4 - ((gYear / 100 + 1) * 3 / 4) - 150;
        march = 20 + leapJ - leapG;
        // Find how many years have passed since the last leap year
        if ((jump - N) < 6)
            N = N - jump + ((jump + 4) / 33 * 33);
        leap = (((N + 1) % 33) - 1) % 4;
        if (leap == -1)
            leap = 4;
    }

    public boolean IsLeap(int irYear1) {
        // Iranian years starting the 33-year rule
        int[] Breaks =
                {-61, 9, 38, 199, 426, 686, 756, 818, 1111, 1181,
                        1210, 1635, 2060, 2097, 2192, 2262, 2324, 2394, 2456, 3178};
        int jm, N, leapJ, leapG, jp, j, jump;
        gYear = irYear1 + 621;
        leapJ = -14;
        jp = Breaks[0];
        // Find the limiting years for the Iranian year 'irYear'
        j = 1;
        do {
            jm = Breaks[j];
            jump = jm - jp;
            if (irYear1 >= jm) {
                leapJ += (jump / 33 * 8 + (jump % 33) / 4);
                jp = jm;
            }
            j++;
        } while ((j < 20) && (irYear1 >= jm));
        N = irYear1 - jp;
        // Find the number of leap years from AD 621 to the begining of the current
        // Iranian year in the Iranian (Jalali) calendar
        leapJ += (N / 33 * 8 + ((N % 33) + 3) / 4);
        if (((jump % 33) == 4) && ((jump - N) == 4))
            leapJ++;
        // And the same in the Gregorian date of Farvardin the first
        leapG = gYear / 4 - ((gYear / 100 + 1) * 3 / 4) - 150;
        march = 20 + leapJ - leapG;
        // Find how many years have passed since the last leap year
        if ((jump - N) < 6)
            N = N - jump + ((jump + 4) / 33 * 33);
        leap = (((N + 1) % 33) - 1) % 4;
        if (leap == -1)
            leap = 4;
        return leap == 4 || leap == 0;

    }

    private int IranianDateToJDN() {
        IranianCalendar();
        return (gregorianDateToJDN(gYear, 3, march) + (irMonth - 1) * 31 - irMonth / 7 * (irMonth - 7) + irDay - 1);
    }

    private void JDNToIranian() {
        JDNToGregorian();
        irYear = gYear - 621;
        IranianCalendar(); // This invocation will update 'leap' and 'march'
        int JDN1F = gregorianDateToJDN(gYear, 3, march);
        int k = JDN - JDN1F;
        if (k >= 0) {
            if (k <= 185) {
                irMonth = 1 + k / 31;
                irDay = (k % 31) + 1;
                return;
            } else
                k -= 186;
        } else {
            irYear--;
            k += 179;
            if (leap == 1)
                k++;
        }
        irMonth = 7 + k / 30;
        irDay = (k % 30) + 1;
    }

    private int julianDateToJDN(int year, int month, int day) {
        return (year + (month - 8) / 6 + 100100) * 1461 / 4 + (153 * ((month + 9) % 12) + 2) / 5 + day - 34840408;
    }

    private void JDNToJulian() {
        int j = 4 * JDN + 139361631;
        int i = ((j % 1461) / 4) * 5 + 308;
        juDay = (i % 153) / 5 + 1;
        juMonth = ((i / 153) % 12) + 1;
        juYear = j / 1461 - 100100 + (8 - juMonth) / 6;
    }

    private int gregorianDateToJDN(int year, int month, int day) {
        int jdn = (year + (month - 8) / 6 + 100100) * 1461 / 4 + (153 * ((month + 9) % 12) + 2) / 5 + day - 34840408;
        jdn = jdn - (year + 100100 + (month - 8) / 6) / 100 * 3 / 4 + 752;
        return (jdn);
    }

    private void JDNToGregorian() {
        int j = 4 * JDN + 139361631;
        j = j + (((((4 * JDN + 183187720) / 146097) * 3) / 4) * 4 - 3908);
        int i = ((j % 1461) / 4) * 5 + 308;
        gDay = (i % 153) / 5 + 1;
        gMonth = ((i / 153) % 12) + 1;
        gYear = j / 1461 - 100100 + (8 - gMonth) / 6;
    }
}
