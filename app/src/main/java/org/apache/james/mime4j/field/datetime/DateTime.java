package org.apache.james.mime4j.field.datetime;

import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

/* loaded from: classes.dex */
public class DateTime {
    private final Date date;
    private final int day;
    private final int hour;
    private final int minute;
    private final int month;
    private final int second;
    private final int timeZone;
    private final int year;

    public DateTime(String str, int i, int i2, int i3, int i4, int i5, int i6) {
        int convertToYear = convertToYear(str);
        this.year = convertToYear;
        this.date = convertToDate(convertToYear, i, i2, i3, i4, i5, i6);
        this.month = i;
        this.day = i2;
        this.hour = i3;
        this.minute = i4;
        this.second = i5;
        this.timeZone = i6;
    }

    public static Date convertToDate(int i, int i2, int i3, int i4, int i5, int i6, int i7) {
        GregorianCalendar gregorianCalendar = new GregorianCalendar(TimeZone.getTimeZone("GMT+0"));
        gregorianCalendar.set(i, i2 - 1, i3, i4, i5, i6);
        gregorianCalendar.set(14, 0);
        if (i7 != Integer.MIN_VALUE) {
            gregorianCalendar.add(12, (((i7 / 100) * 60) + (i7 % 100)) * (-1));
        }
        return gregorianCalendar.getTime();
    }

    private int convertToYear(String str) {
        int parseInt = Integer.parseInt(str);
        int length = str.length();
        return (length == 1 || length == 2) ? (parseInt < 0 || parseInt >= 50) ? parseInt + 1900 : parseInt + 2000 : length != 3 ? parseInt : parseInt + 1900;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        DateTime dateTime = (DateTime) obj;
        Date date = this.date;
        if (date == null) {
            if (dateTime.date != null) {
                return false;
            }
        } else if (!date.equals(dateTime.date)) {
            return false;
        }
        return this.day == dateTime.day && this.hour == dateTime.hour && this.minute == dateTime.minute && this.month == dateTime.month && this.second == dateTime.second && this.timeZone == dateTime.timeZone && this.year == dateTime.year;
    }

    public Date getDate() {
        return this.date;
    }

    public int getDay() {
        return this.day;
    }

    public int getHour() {
        return this.hour;
    }

    public int getMinute() {
        return this.minute;
    }

    public int getMonth() {
        return this.month;
    }

    public int getSecond() {
        return this.second;
    }

    public int getTimeZone() {
        return this.timeZone;
    }

    public int getYear() {
        return this.year;
    }

    public int hashCode() {
        Date date = this.date;
        return (((((((((((((((date == null ? 0 : date.hashCode()) + 31) * 31) + this.day) * 31) + this.hour) * 31) + this.minute) * 31) + this.month) * 31) + this.second) * 31) + this.timeZone) * 31) + this.year;
    }

    public void print() {
        System.out.println(toString());
    }

    public String toString() {
        return getYear() + " " + getMonth() + " " + getDay() + "; " + getHour() + " " + getMinute() + " " + getSecond() + " " + getTimeZone();
    }
}
