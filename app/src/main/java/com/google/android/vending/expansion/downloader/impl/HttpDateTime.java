package com.google.android.vending.expansion.downloader.impl;

import android.text.format.Time;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/* loaded from: classes7.dex */
public final class HttpDateTime {
    private static final String HTTP_DATE_RFC_REGEXP = "([0-9]{1,2})[- ]([A-Za-z]{3,9})[- ]([0-9]{2,4})[ ]([0-9]{1,2}:[0-9][0-9]:[0-9][0-9])";
    private static final Pattern HTTP_DATE_RFC_PATTERN = Pattern.compile(HTTP_DATE_RFC_REGEXP);
    private static final String HTTP_DATE_ANSIC_REGEXP = "[ ]([A-Za-z]{3,9})[ ]+([0-9]{1,2})[ ]([0-9]{1,2}:[0-9][0-9]:[0-9][0-9])[ ]([0-9]{2,4})";
    private static final Pattern HTTP_DATE_ANSIC_PATTERN = Pattern.compile(HTTP_DATE_ANSIC_REGEXP);

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes7.dex */
    public static class TimeOfDay {
        int hour;
        int minute;
        int second;

        TimeOfDay(int h, int m, int s) {
            this.hour = h;
            this.minute = m;
            this.second = s;
        }
    }

    private static int getDate(String dateString) {
        return dateString.length() == 2 ? ((dateString.charAt(0) - '0') * 10) + (dateString.charAt(1) - '0') : dateString.charAt(0) - '0';
    }

    private static int getMonth(String monthString) {
        int lowerCase = ((Character.toLowerCase(monthString.charAt(0)) + Character.toLowerCase(monthString.charAt(1))) + Character.toLowerCase(monthString.charAt(2))) - 291;
        if (lowerCase == 9) {
            return 11;
        }
        if (lowerCase == 10) {
            return 1;
        }
        if (lowerCase == 22) {
            return 0;
        }
        if (lowerCase == 26) {
            return 7;
        }
        if (lowerCase == 29) {
            return 2;
        }
        if (lowerCase == 32) {
            return 3;
        }
        if (lowerCase == 40) {
            return 6;
        }
        if (lowerCase == 42) {
            return 5;
        }
        if (lowerCase == 48) {
            return 10;
        }
        switch (lowerCase) {
            case 35:
                return 9;
            case 36:
                return 4;
            case 37:
                return 8;
            default:
                throw new IllegalArgumentException();
        }
    }

    private static TimeOfDay getTime(String timeString) {
        int i;
        int i2 = 0;
        int i3 = 0;
        int charAt = timeString.charAt(0) - '0';
        if (timeString.charAt(1) != ':') {
            i = 2;
            charAt = (charAt * 10) + (timeString.charAt(1) - '0');
        } else {
            i = 1;
        }
        int i4 = i + 1 + 1 + 1 + 1;
        return new TimeOfDay(charAt, ((timeString.charAt(i2) - '0') * 10) + (timeString.charAt(i3) - '0'), ((timeString.charAt(i4) - '0') * 10) + (timeString.charAt(i4 + 1) - '0'));
    }

    private static int getYear(String yearString) {
        if (yearString.length() == 2) {
            int charAt = ((yearString.charAt(0) - '0') * 10) + (yearString.charAt(1) - '0');
            return charAt >= 70 ? charAt + 1900 : charAt + 2000;
        } else if (yearString.length() == 3) {
            return ((yearString.charAt(0) - '0') * 100) + ((yearString.charAt(1) - '0') * 10) + (yearString.charAt(2) - '0') + 1900;
        } else {
            if (yearString.length() == 4) {
                return ((yearString.charAt(0) - '0') * 1000) + ((yearString.charAt(1) - '0') * 100) + ((yearString.charAt(2) - '0') * 10) + (yearString.charAt(3) - '0');
            }
            return 1970;
        }
    }

    public static long parse(String timeString) throws IllegalArgumentException {
        int i;
        TimeOfDay timeOfDay;
        int i2;
        int i3;
        int i4;
        Matcher matcher = HTTP_DATE_RFC_PATTERN.matcher(timeString);
        if (matcher.find()) {
            i4 = getDate(matcher.group(1));
            i3 = getMonth(matcher.group(2));
            i = getYear(matcher.group(3));
            timeOfDay = getTime(matcher.group(4));
        } else {
            Matcher matcher2 = HTTP_DATE_ANSIC_PATTERN.matcher(timeString);
            if (matcher2.find()) {
                i3 = getMonth(matcher2.group(1));
                i4 = getDate(matcher2.group(2));
                timeOfDay = getTime(matcher2.group(3));
                i = getYear(matcher2.group(4));
            } else {
                throw new IllegalArgumentException();
            }
        }
        if (i >= 2038) {
            i4 = 1;
            i3 = 0;
            i2 = 2038;
        } else {
            i2 = i;
        }
        Time time = new Time("UTC");
        time.set(timeOfDay.second, timeOfDay.minute, timeOfDay.hour, i4, i3, i2);
        return time.toMillis(false);
    }
}
