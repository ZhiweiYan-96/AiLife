package com.record.utils;

import android.content.Context;
import com.record.bean.DayInfo;
import com.record.myLife.R;
import com.sun.mail.imap.IMAPStore;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

public class DateTime {
    public static final String DATE_FORMAT_LINE = "yyyy-MM-dd";
    private static final long DAY_MILLI = 86400000;
    private Calendar cNow = Calendar.getInstance();
    private Date dNow = new Date(this.lNow);
    private long lNow = System.currentTimeMillis();
    private Time now = new Time(this.lNow);
    private Timestamp tNow = new Timestamp(this.lNow);
    private java.sql.Date today = new java.sql.Date(this.lNow);

    public static int getMaxNum(String time) {
        return pars2Calender(time).getActualMaximum(5);
    }

    public static String getDay3(Calendar c) {
        if (c != null) {
            int i = c.get(7) - 1;
            if (i == 1) {
                return "一";
            }
            if (i == 2) {
                return "二";
            }
            if (i == 3) {
                return "三";
            }
            if (i == 4) {
                return "四";
            }
            if (i == 5) {
                return "五";
            }
            if (i == 6) {
                return "六";
            }
            if (i == 0) {
                return "日";
            }
            if (i == 7) {
                return "日";
            }
        }
        return "";
    }

    public static String getDay2(Calendar c) {
        if (c != null) {
            int i = c.get(7) - 1;
            if (i == 1) {
                return "周一";
            }
            if (i == 2) {
                return "周二";
            }
            if (i == 3) {
                return "周三";
            }
            if (i == 4) {
                return "周四";
            }
            if (i == 5) {
                return "周五";
            }
            if (i == 6) {
                return "周六";
            }
            if (i == 0) {
                return "周日";
            }
            if (i == 7) {
                return "周日";
            }
        }
        return "";
    }

    public static String getDayOfWeek(String time) {
        Calendar c = pars2Calender(time);
        if (c != null) {
            int i = c.get(7) - 1;
            if (i == 1) {
                return "周一";
            }
            if (i == 2) {
                return "周二";
            }
            if (i == 3) {
                return "周三";
            }
            if (i == 4) {
                return "周四";
            }
            if (i == 5) {
                return "周五";
            }
            if (i == 6) {
                return "周六";
            }
            if (i == 0) {
                return "周日";
            }
            if (i == 7) {
                return "周日";
            }
        }
        return "";
    }

    public static String getDay3(Context context, String timestamp) {
        Calendar c = pars2Calender(timestamp);
        if (c != null) {
            int i = c.get(7) - 1;
            if (i == 1) {
                return context.getResources().getString(R.string.str_Mon);
            }
            if (i == 2) {
                return context.getResources().getString(R.string.str_Tues);
            }
            if (i == 3) {
                return context.getResources().getString(R.string.str_Wed);
            }
            if (i == 4) {
                return context.getResources().getString(R.string.str_Thur);
            }
            if (i == 5) {
                return context.getResources().getString(R.string.str_Fri);
            }
            if (i == 6) {
                return context.getResources().getString(R.string.str_Sat);
            }
            if (i == 0) {
                return context.getResources().getString(R.string.str_Sun);
            }
            if (i == 7) {
                return context.getResources().getString(R.string.str_Sun);
            }
        }
        return "";
    }

    public static String getHourAndMinFromTimestamp(String timestamp) {
        if (timestamp == null) {
            return "";
        }
        try {
            return timestamp.substring(timestamp.indexOf(" ") + 1, timestamp.lastIndexOf(":"));
        } catch (Exception e) {
            e.printStackTrace();
            return "出错啦！";
        }
    }

    public static String getMonAndDayFromTimestamp(String timestamp) {
        if (timestamp == null) {
            return "";
        }
        try {
            return timestamp.substring(timestamp.indexOf("-") + 1, timestamp.indexOf(" "));
        } catch (Exception e) {
            e.printStackTrace();
            return "出错啦！";
        }
    }

    public static String getMonDayWeekFromTimestamp(String timestamp) {
        if (timestamp == null) {
            return "";
        }
        Calendar c = pars2Calender(timestamp);
        try {
            return timestamp.substring(timestamp.indexOf("-") + 1, timestamp.indexOf(" ")).replace("-", "月") + "日" + "　" + getDay2(c);
        } catch (Exception e) {
            e.printStackTrace();
            return "出错啦！";
        }
    }

    public static String getMonDayWeekFromTimestamp2(String timestamp) {
        if (timestamp == null) {
            return "";
        }
        Calendar c = pars2Calender(timestamp);
        int start = timestamp.indexOf("-");
        int end = timestamp.indexOf(" ");
        String year = timestamp.substring(0, start);
        try {
            return year + "年" + (timestamp.substring(start + 1, end).replace("-", "月") + "日" + "　" + getDay2(c));
        } catch (Exception e) {
            e.printStackTrace();
            return "出错啦！";
        }
    }

    public static String convertTsToYMD(String timestamp) {
        if (timestamp == null) {
            return "";
        }
        int start = timestamp.indexOf("-");
        int end = timestamp.indexOf(" ");
        String year = timestamp.substring(0, start);
        try {
            return year + "年" + (timestamp.substring(start + 1, end).replace("-", "月") + "日");
        } catch (Exception e) {
            e.printStackTrace();
            return "出错啦！";
        }
    }

    public static String getDateFromTimestamp(String timestamp) {
        if (timestamp == null) {
            return "";
        }
        try {
            return timestamp.substring(0, timestamp.indexOf(" "));
        } catch (Exception e) {
            e.printStackTrace();
            return "出错啦！";
        }
    }

    public static int getYear(Calendar c) {
        if (c != null) {
            return c.get(1);
        }
        return Calendar.getInstance().get(1);
    }

    public static String getYearStr(Calendar c) {
        if (c != null) {
            return c.get(1) + "";
        }
        return Calendar.getInstance().get(1) + "";
    }

    public static int getMonth(Calendar c) {
        if (c != null) {
            return c.get(2);
        }
        return Calendar.getInstance().get(2);
    }

    public static String getMonth2(Calendar c) {
        String str = "";
        if (c != null) {
            return getMonthString(c.get(2) + 1);
        }
        return getMonthString(Calendar.getInstance().get(2) + 1);
    }

    public static String getMonthString(int month) {
        String monthStr = "";
        if (month == 1) {
            return "1月";
        }
        if (month == 2) {
            return "2月";
        }
        if (month == 3) {
            return "3月";
        }
        if (month == 4) {
            return "4月";
        }
        if (month == 5) {
            return "5月";
        }
        if (month == 6) {
            return "6月";
        }
        if (month == 7) {
            return "7月";
        }
        if (month == 8) {
            return "8月";
        }
        if (month == 9) {
            return "9月";
        }
        if (month == 10) {
            return "10月";
        }
        if (month == 11) {
            return "11月";
        }
        if (month == 12) {
            return "12月";
        }
        return monthStr;
    }

    public static int getDate(Calendar c) {
        if (c != null) {
            return c.get(5);
        }
        return Calendar.getInstance().get(5);
    }

    public static int getDay(Calendar c) {
        if (c != null) {
            return c.get(7);
        }
        return Calendar.getInstance().get(7);
    }

    public static int getHour(int tatalSecond) {
        return tatalSecond / 3600;
    }

    public static int getHour(Calendar c) {
        if (c != null) {
            return c.get(10);
        }
        return Calendar.getInstance().get(10);
    }

    public static int getMinute(Calendar c) {
        if (c != null) {
            return c.get(12);
        }
        return Calendar.getInstance().get(12);
    }

    public static int getSecond(Calendar c) {
        if (c != null) {
            return c.get(13);
        }
        return Calendar.getInstance().get(13);
    }

    public static Calendar beforeNDays(Calendar c, int n) {
        Calendar calendar;
        long offset = (long) ((((n * 24) * 60) * 60) * IMAPStore.RESPONSE);
        if (c != null) {
            calendar = c;
        } else {
            calendar = Calendar.getInstance();
        }
        calendar.setTimeInMillis(calendar.getTimeInMillis() - offset);
        return calendar;
    }

    public static String beforeNDays_v2(String date, int n) {
        if (date.indexOf(" ") <= 0) {
            date = date + " 00:00:00";
        }
        Calendar calendar = pars2Calender(date);
        calendar.add(5, n);
        return formatDate(calendar);
    }

    public static Calendar beforeNDays(int n) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(5, n);
        return calendar;
    }

    public static String beforeNDays2Str(int n) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(5, n);
        return formatDate(calendar);
    }

    public static String beforeNDays2Str2(int n) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(5, n);
        return formatTime(calendar);
    }

    public static String beforeSecond(int n) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(13, n);
        return formatTime(calendar);
    }

    public static String beforeSecond(String time, int n) {
        Calendar calendar = pars2Calender(time);
        calendar.add(13, n);
        return formatTime(calendar);
    }

    public static Calendar afterNDays(Calendar c, int n) {
        Calendar calendar;
        long offset = (long) ((((n * 24) * 60) * 60) * IMAPStore.RESPONSE);
        if (c != null) {
            calendar = c;
        } else {
            calendar = Calendar.getInstance();
        }
        calendar.setTimeInMillis(calendar.getTimeInMillis() + offset);
        return calendar;
    }

    public static String TimeStringToHourMIn(String TimeString) {
        String time = "";
        try {
            return TimeString.substring(TimeString.indexOf(" ") + 1, TimeString.lastIndexOf(":"));
        } catch (Exception e) {
            e.printStackTrace();
            return time;
        }
    }

    public static Calendar yesterday(Calendar c) {
        Calendar calendar;
        if (c != null) {
            calendar = c;
        } else {
            calendar = Calendar.getInstance();
        }
        calendar.setTimeInMillis(calendar.getTimeInMillis() - 86400000);
        return calendar;
    }

    public static Calendar tomorrow(Calendar c) {
        Calendar calendar;
        if (c != null) {
            calendar = c;
        } else {
            calendar = Calendar.getInstance();
        }
        calendar.setTimeInMillis(calendar.getTimeInMillis() + 86400000);
        return calendar;
    }

    public static Calendar before(Calendar c, long offset) {
        Calendar calendar;
        if (c != null) {
            calendar = c;
        } else {
            calendar = Calendar.getInstance();
        }
        calendar.setTimeInMillis(calendar.getTimeInMillis() - offset);
        return calendar;
    }

    public static Calendar after(Calendar c, long offset) {
        Calendar calendar;
        if (c != null) {
            calendar = c;
        } else {
            calendar = Calendar.getInstance();
        }
        calendar.setTimeInMillis(calendar.getTimeInMillis() - offset);
        return calendar;
    }

    public static String format(Calendar c, String pattern) {
        Calendar calendar;
        if (c != null) {
            calendar = c;
        } else {
            calendar = Calendar.getInstance();
        }
        if (pattern == null || pattern.equals("")) {
            pattern = "yyyy年MM月dd日 HH:mm:ss";
        }
        return new SimpleDateFormat(pattern).format(calendar.getTime());
    }

    public static String formatTime(Calendar c) {
        Calendar calendar;
        if (c != null) {
            calendar = c;
        } else {
            calendar = Calendar.getInstance();
        }
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(calendar.getTime());
    }

    public static String formatDate(Calendar c) {
        Calendar calendar;
        if (c != null) {
            calendar = c;
        } else {
            calendar = Calendar.getInstance();
        }
        return new SimpleDateFormat(DATE_FORMAT_LINE).format(calendar.getTime());
    }

    public static String formatMonth(Calendar c) {
        Calendar calendar;
        if (c != null) {
            calendar = c;
        } else {
            calendar = Calendar.getInstance();
        }
        return new SimpleDateFormat("yyyy年MM月").format(calendar.getTime());
    }

    public static long differToSecond(Date date1, Date date2) {
        return 1;
    }

    public static Calendar Date2Calendar(Date d) {
        Calendar c = Calendar.getInstance();
        c.setTime(d);
        return c;
    }

    public static Date Calendar2Date(Calendar c) {
        return c.getTime();
    }

    public static Timestamp Date2Timestamp(Date d) {
        return new Timestamp(d.getTime());
    }

    public static boolean isDate(String d) {
        try {
            pars2Calender2(d);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static Timestamp Calendar2Timestamp(Calendar c) {
        return new Timestamp(c.getTimeInMillis());
    }

    public static Calendar Timestamp2Calendar(Timestamp ts) {
        Calendar c = Calendar.getInstance();
        c.setTime(ts);
        return c;
    }

    public static String getTimeString() {
        return format(Calendar.getInstance(), "yyyy-MM-dd HH:mm:ss");
    }

    public static String getTimeString2() {
        return format(Calendar.getInstance(), "yyyy-MM-dd HH:mm:ss").replace("-", "").replace(" ", "_").replace(":", "");
    }

    public static String getTimeString3() {
        return format(Calendar.getInstance(), "MMddHHmm");
    }

    public static String getTimeStr1229() {
        return format(Calendar.getInstance(), "HH:mm");
    }

    public static String getTimeStr1229(String Time) {
        return format(pars2Calender(Time), "HH:mm");
    }

    public static String getTimeStr12shi29fen() {
        return format(Calendar.getInstance(), "HH时mm分");
    }

    public static Calendar getNextHour() {
        Calendar c = Calendar.getInstance();
        c.add(11, 1);
        c.set(12, 0);
        c.set(13, 0);
        return c;
    }

    public static Calendar getNextHalf() {
        Calendar c = Calendar.getInstance();
        int hour = c.get(11);
        int min = c.get(12);
        int sec = c.get(13);
        int day = c.get(5);
        int mon = c.get(2) + 1;
        if (min < 30) {
            min = 30;
        } else if (min >= 30) {
            min = 0;
            c.add(11, 1);
        }
        c.set(12, min);
        c.set(13, 0);
        return c;
    }

    public static Calendar getNextquarter() {
        Calendar c = Calendar.getInstance();
        int hour = c.get(11);
        int min = c.get(12);
        int sec = c.get(13);
        int day = c.get(5);
        int mon = c.get(2) + 1;
        if (min < 15) {
            min = 15;
        } else if (min >= 15 && min < 30) {
            min = 30;
        } else if (min >= 30 && min < 45) {
            min = 45;
        } else if (min >= 45) {
            min = 0;
            c.add(11, 1);
        }
        c.set(12, min);
        c.set(13, 0);
        return c;
    }

    public static String[] getDateOfWeekStartAndEnd(String date, int startDayOfWeek) {
        int dif;
        String[] arr = new String[2];
        if (date.indexOf(" ") < 0) {
            date = date + " 00:00:00";
        }
        Calendar c = pars2Calender(date);
        int d = c.get(7);
        System.out.println(d);
        if (startDayOfWeek > d) {
            dif = d + (7 - startDayOfWeek);
        } else {
            dif = d - startDayOfWeek;
        }
        System.out.println(dif);
        c.add(5, -dif);
        arr[0] = format(c, DATE_FORMAT_LINE);
        c.add(5, 6);
        arr[1] = format(c, DATE_FORMAT_LINE);
        return arr;
    }

    public static String[] getDateOfWeekStartAndEndForLastWeek(String date, int startDayOfWeek) {
        int dif;
        String[] arr = new String[2];
        if (date.indexOf(" ") < 0) {
            date = date + " 00:00:00";
        }
        Calendar c = pars2Calender(date);
        int d = c.get(7);
        System.out.println(d);
        if (startDayOfWeek > d) {
            dif = d + (7 - startDayOfWeek);
        } else {
            dif = d - startDayOfWeek;
        }
        System.out.println(dif);
        c.add(5, -dif);
        c.add(5, -7);
        arr[0] = format(c, DATE_FORMAT_LINE);
        c.add(5, 6);
        arr[1] = format(c, DATE_FORMAT_LINE);
        return arr;
    }

    public static String[] getDateOfWeekStartAndEndForNextWeek(String date, int startDayOfWeek) {
        int dif;
        String[] arr = new String[2];
        if (date.indexOf(" ") < 0) {
            date = date + " 00:00:00";
        }
        Calendar c = pars2Calender(date);
        int d = c.get(7);
        System.out.println(d);
        if (startDayOfWeek > d) {
            dif = d + (7 - startDayOfWeek);
        } else {
            dif = d - startDayOfWeek;
        }
        System.out.println(dif);
        c.add(5, -dif);
        c.add(5, 7);
        arr[0] = format(c, DATE_FORMAT_LINE);
        c.add(5, 6);
        arr[1] = format(c, DATE_FORMAT_LINE);
        return arr;
    }

    public static String getDateOfWeekStart(int startDayOfWeek) {
        int dif;
        Calendar c = Calendar.getInstance();
        int d = c.get(7);
        if (startDayOfWeek > d) {
            dif = d + (7 - startDayOfWeek);
        } else {
            dif = d - startDayOfWeek;
        }
        c.add(5, -dif);
        return formatDate(c);
    }

    public static String[] getDateOfMonthStartAndEnd(String date) {
        String[] arr = new String[3];
        if (date.indexOf(" ") < 0) {
            date = date + " 00:00:00";
        }
        Calendar c = pars2Calender(date);
        int day = c.get(5);
        int min = c.getActualMinimum(5);
        int max = c.getActualMaximum(5);
        c.add(5, -(day - min));
        arr[0] = format(c, DATE_FORMAT_LINE);
        c.add(5, max - min);
        arr[1] = format(c, DATE_FORMAT_LINE);
        arr[2] = max + "";
        return arr;
    }

    public static String[] getDateOfMonthStartAndEndLastMonth(String date) {
        String[] arr = new String[3];
        if (date.indexOf(" ") < 0) {
            date = date + " 00:00:00";
        }
        Calendar c = pars2Calender(date);
        c.add(2, -1);
        int day = c.get(5);
        int min = c.getActualMinimum(5);
        int max = c.getActualMaximum(5);
        c.add(5, -(day - min));
        arr[0] = format(c, DATE_FORMAT_LINE);
        c.add(5, max - min);
        arr[1] = format(c, DATE_FORMAT_LINE);
        arr[2] = max + "";
        return arr;
    }

    public static String[] getDateOfMonthStartAndEndNextMonth(String date) {
        String[] arr = new String[3];
        if (date.indexOf(" ") < 0) {
            date = date + " 00:00:00";
        }
        Calendar c = pars2Calender(date);
        c.add(2, 1);
        int day = c.get(5);
        int min = c.getActualMinimum(5);
        int max = c.getActualMaximum(5);
        c.add(5, -(day - min));
        arr[0] = format(c, DATE_FORMAT_LINE);
        c.add(5, max - min);
        arr[1] = format(c, DATE_FORMAT_LINE);
        arr[2] = max + "";
        return arr;
    }

    public static String getTime1MinuteLater() {
        Calendar c = Calendar.getInstance();
        c.set(12, c.get(12) + 60);
        return formatTime(c);
    }

    public static String getTime1MinuteLater2() {
        Calendar c = Calendar.getInstance();
        c.set(12, c.get(12) + 60);
        String date = formatTime(c);
        return date.substring(date.indexOf(" ") + 1, date.length());
    }

    public static String getTime_nSecondLater(int n) {
        Calendar c = Calendar.getInstance();
        c.set(13, c.get(13) + 20);
        String date = formatTime(c);
        return date.substring(date.indexOf(" ") + 1, date.length());
    }

    public static String getDateString() {
        return format(Calendar.getInstance(), DATE_FORMAT_LINE);
    }

    public static String getDateStringZero() {
        return format(Calendar.getInstance(), DATE_FORMAT_LINE) + " 00:00:00";
    }

    public static Calendar pars2Calender(String s) {
        return Timestamp2Calendar(Timestamp.valueOf(s));
    }

    public static Calendar pars2Calender2(String date) {
        return Timestamp2Calendar(Timestamp.valueOf(date + " 00:00:00"));
    }

    public static String calculateTime(long totalSecond) {
        long hours = totalSecond / 3600;
        long minute = (totalSecond % 3600) / 60;
        long second = (totalSecond % 3600) % 60;
        String retValue = "";
        if (hours > 0) {
            retValue = retValue + hours + "小时";
        }
        if (minute > 0) {
            retValue = retValue + minute + "分";
        }
        return retValue + second + "秒";
    }

    public static String calculateTime3(long totalSecond) {
        long days = totalSecond / 86400;
        long hours = (totalSecond % 86400) / 3600;
        long minute = ((totalSecond % 86400) % 3600) / 60;
        String retValue = "";
        if (days > 0) {
            retValue = retValue + days + "天";
        }
        if (hours > 0) {
            retValue = retValue + hours + "小时";
        }
        if (minute > 0) {
            retValue = retValue + minute + "分";
        }
        if (retValue.equals("")) {
            return retValue + "<1分";
        }
        return retValue;
    }

    public static String calculateTime5(Context context, long totalSecond) {
        long days = totalSecond / 86400;
        long hours = (totalSecond % 86400) / 3600;
        long minute = ((totalSecond % 86400) % 3600) / 60;
        String retValue = "";
        if (days > 0) {
            retValue = retValue + days + context.getResources().getString(R.string.str_Day);
        }
        if (hours > 0) {
            retValue = retValue + hours + context.getResources().getString(R.string.str_hour_short);
        }
        if (minute > 0) {
            retValue = retValue + minute + context.getResources().getString(R.string.str_minute_short);
        }
        if (retValue.equals("")) {
            return retValue + "<1" + context.getResources().getString(R.string.str_minute_short);
        }
        return retValue;
    }

    public static String calculateTime10(long totalSecond) {
        return FormatUtils.format_1fra(((double) totalSecond) / 3600.0d) + "h";
    }

    public static String calculateTime4(long totalSecond) {
        long hours = (totalSecond % 86400) / 3600;
        long minute = ((totalSecond % 86400) % 3600) / 60;
        String retValue = "";
        if (hours > 0) {
            retValue = retValue + hours + ":";
        }
        if (minute < 10) {
            return retValue + "0" + minute;
        }
        return retValue + minute;
    }

    public static String calculateTime2(long totalSecond) {
        long hours = totalSecond / 3600;
        long minute = (totalSecond % 3600) / 60;
        long second = (totalSecond % 3600) % 60;
        String retValue = "";
        if (hours <= 0) {
            retValue = retValue + "00:";
        } else if (hours > 9) {
            retValue = retValue + hours + ":";
        } else {
            retValue = retValue + "0" + hours + ":";
        }
        if (minute <= 0) {
            retValue = retValue + "00:";
        } else if (minute > 9) {
            retValue = retValue + minute + ":";
        } else {
            retValue = retValue + "0" + minute + ":";
        }
        if (second > 9) {
            return retValue + second;
        }
        return retValue + "0" + second;
    }

    public static String calculateTime8(long totalSecond) {
        long hours = totalSecond / 3600;
        long minute = (totalSecond % 3600) / 60;
        long second = (totalSecond % 3600) % 60;
        String retValue = "";
        if (hours <= 0) {
            retValue = retValue + "00:";
        } else if (hours > 9) {
            retValue = retValue + hours + ":";
        } else {
            retValue = retValue + "0" + hours + ":";
        }
        if (minute <= 0) {
            return retValue + "00";
        }
        if (minute > 9) {
            return retValue + minute;
        }
        return retValue + "0" + minute;
    }

    public static String calculateTime9(float hoursFloat) {
        long totalSecond = (long) (3600.0f * hoursFloat);
        long hours = totalSecond / 3600;
        long minute = (totalSecond % 3600) / 60;
        long second = (totalSecond % 3600) % 60;
        String retValue = "";
        if (hours <= 0) {
            retValue = retValue + "00:";
        } else if (hours > 9) {
            retValue = retValue + hours + ":";
        } else {
            retValue = retValue + "0" + hours + ":";
        }
        if (minute <= 0) {
            return retValue + "00";
        }
        if (minute > 9) {
            return retValue + minute;
        }
        return retValue + "0" + minute;
    }

    public static String calculateTime6(long totalSecond) {
        long hours = totalSecond / 3600;
        long minute = (totalSecond % 3600) / 60;
        long second = (totalSecond % 3600) % 60;
        String retValue = "";
        if (hours > 0) {
            retValue = retValue + hours + "时";
        }
        if (minute > 0) {
            retValue = retValue + minute + "分";
        } else if (hours > 0) {
            retValue = retValue + "0分";
        }
        if (second > 0) {
            retValue = retValue + second + "秒";
        }
        if (hours == 0 && minute == 0 && second == 0) {
            return "0秒";
        }
        return retValue;
    }

    public static String calculateTime7(long totalSecond) {
        long hours = totalSecond / 3600;
        long minute = (totalSecond % 3600) / 60;
        long second = (totalSecond % 3600) % 60;
        String retValue = "";
        if (minute > 0) {
            retValue = retValue + minute + ":";
        } else {
            retValue = retValue + "0:";
        }
        if (second <= 0) {
            return retValue + "00";
        }
        if (second > 9) {
            return retValue + second;
        }
        return retValue + "0" + second;
    }

    public static int compare_date(String timeString1, String timeString2) {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        try {
            Date dt1 = df.parse(timeString1);
            Date dt2 = df.parse(timeString2);
            if (dt1.getTime() > dt2.getTime()) {
                return 1;
            }
            if (dt1.getTime() < dt2.getTime()) {
                return -1;
            }
            return 0;
        } catch (Exception exception) {
            exception.printStackTrace();
            return 0;
        }
    }

    public static int compare_time(Object timeString1, Object timeString2) {
        try {
            Calendar time1;
            Calendar time2;
            if (String.class.isInstance(timeString1)) {
                time1 = pars2Calender(null + "");
            } else {
                time1 = (Calendar) timeString1;
            }
            if (String.class.isInstance(timeString2)) {
                time2 = pars2Calender(null + "");
            } else {
                time2 = (Calendar) timeString2;
            }
            Date dt1 = time1.getTime();
            Date dt2 = time2.getTime();
            if (dt1.getTime() > dt2.getTime()) {
                return 1;
            }
            if (dt1.getTime() < dt2.getTime()) {
                return -1;
            }
            return 0;
        } catch (Exception exception) {
            exception.printStackTrace();
            return 0;
        }
    }

    public static int compareNow(String time) {
        try {
            Date dt1 = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse(time);
            Date dt2 = Calendar.getInstance().getTime();
            if (dt1.getTime() > dt2.getTime()) {
                return 1;
            }
            if (dt1.getTime() < dt2.getTime()) {
                return -1;
            }
            return 0;
        } catch (Exception exception) {
            exception.printStackTrace();
            return 0;
        }
    }

    public static Date toDate(String dateStr, String styleStr) {
        try {
            return new SimpleDateFormat(styleStr).parse(dateStr);
        } catch (ParseException pe) {
            pe.printStackTrace();
            return null;
        }
    }

    public static int getBetweenDayNumber(Date startDate, Date endDate) {
        return (int) ((endDate.getTime() - startDate.getTime()) / 86400000);
    }

    public static int getBetweenDayNumber(String startDateStr, Date endDate) {
        long dayNumber = 0;
        try {
            dayNumber = (endDate.getTime() - new SimpleDateFormat(DATE_FORMAT_LINE).parse(startDateStr).getTime()) / 86400000;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return (int) dayNumber;
    }

    public static int getBetweenDayNumberByTime(Date startDate, Date endDate) {
        return (int) ((endDate.getTime() - startDate.getTime()) / 60000);
    }

    public static int cal_daysBetween(Date startDate, Date endDate) {
        return (int) ((endDate.getTime() - startDate.getTime()) / 86400000);
    }

    public static int cal_daysBetween(String startDateStr, Date endDate) {
        long dayNumber = 0;
        try {
            dayNumber = (endDate.getTime() - new SimpleDateFormat(DATE_FORMAT_LINE).parse(startDateStr).getTime()) / 86400000;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return (int) dayNumber;
    }

    public static int cal_daysBetween(String startDateStr, String endDateStr) {
        long dayNumber = 0;
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_LINE);
        try {
            dayNumber = (sdf.parse(endDateStr).getTime() - sdf.parse(startDateStr).getTime()) / 86400000;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return (int) dayNumber;
    }

    public static int cal_daysBetween_v2(String start, String end) {
        if (start == null || end == null) {
            return 0;
        }
        start = start.replace("T", " ");
        end = end.replace("T", " ");
        int startInt = start.indexOf(" ");
        int endInt = end.indexOf(" ");
        if (startInt > 0) {
            start = start.substring(0, startInt);
        }
        if (endInt > 0) {
            end = end.substring(0, endInt);
        }
        return compare_date(start + " 00:00:00", end + " 00:00:00");
    }

    public static HashMap<String, Integer> cal_daysMapBetween(String start, String end) {
        if (start == null && end == null) {
            return null;
        }
        int startInt = start.indexOf(" ");
        int endInt = end.indexOf(" ");
        if (startInt > 0) {
            start = start.substring(0, startInt);
        }
        if (endInt > 0) {
            end = end.substring(0, endInt);
        }
        HashMap<String, Integer> map = new HashMap();
        int flag = compare_date(start + " 00:00:00", end + " 00:00:00");
        if (flag > 0) {
            String temp = end;
            end = start;
            start = temp;
        } else if (flag == 0) {
            map.put(start, Integer.valueOf(0));
            return map;
        }
        int diff = cal_daysBetween(start, end);
        Calendar cal = pars2Calender(start + " 00:00:00");
        map.put(start, Integer.valueOf(0));
        for (int i = 0; i < diff; i++) {
            cal.add(5, 1);
            map.put(formatDate(cal), Integer.valueOf(0));
        }
        return map;
    }

    public static int cal_hoursBetween(Date startDate, Date endDate) {
        return (int) ((endDate.getTime() - startDate.getTime()) / 60000);
    }

    public static int cal_secBetween(Date startDate, Date endDate) {
        long dayNumber = 0;
        try {
            dayNumber = (endDate.getTime() - startDate.getTime()) / 1000;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return (int) dayNumber;
    }

    public static int cal_secBetween(String startDate, String endDate) {
        long dayNumber = 0;
        try {
            dayNumber = (pars2Calender(endDate).getTime().getTime() - pars2Calender(startDate).getTime().getTime()) / 1000;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return (int) dayNumber;
    }

    public static int[] getDayBetween(String startTime, String deadline) {
        if (startTime == null || deadline == null) {
            return null;
        }
        Date today = Calendar.getInstance().getTime();
        Date start = pars2Calender(startTime).getTime();
        Date dead = pars2Calender(deadline).getTime();
        int use = getBetweenDayNumber(start, today);
        return new int[]{getBetweenDayNumber(start, dead), use};
    }

    public static Object[] beforeNDaysArr(String startTime, int day) {
        if (startTime == null) {
            return null;
        }
        Calendar cal = pars2Calender(startTime);
        cal.add(5, -day);
        Set<String> daySet = new TreeSet();
        TreeMap<String, DayInfo> dayMap = new TreeMap();
        for (int i = 0; i <= day; i++) {
            String tempDate = formatDate(cal);
            dayMap.put(tempDate, new DayInfo(i + 1, 0, "", 0, format(cal, "dd"), getDay3(cal)));
            daySet.add(tempDate);
            cal.add(5, 1);
        }
        return new Object[]{daySet, dayMap};
    }

    public static Object[] afterNDaysArr(String startTime, int day) {
        if (startTime == null) {
            return null;
        }
        Calendar cal = pars2Calender(startTime);
        Set<String> daySet = new TreeSet();
        Map<String, DayInfo> dayMap = new HashMap();
        for (int i = 0; i <= day; i++) {
            String tempDate = formatDate(cal);
            dayMap.put(tempDate, new DayInfo(i + 1, 0, "", 0, format(cal, "dd"), getDay3(cal)));
            daySet.add(tempDate);
            cal.add(5, 1);
        }
        return new Object[]{daySet, dayMap};
    }

    public static String getFirstDateForMonth(String time) {
        return time.substring(0, time.lastIndexOf("-")) + "-01";
    }

    public static String getFirstDateForWeek(Context context, String time) {
        int startDayOfWeek = PreferUtils.getSP(context).getInt(Val.CONFIGURE_START_DATE_OF_WEEK, 2);
        Calendar calendar = pars2Calender(time);
        int dayOfWeek = calendar.get(7);
        if (startDayOfWeek == 1) {
            if (dayOfWeek > startDayOfWeek) {
                calendar.add(5, -(dayOfWeek - startDayOfWeek));
            }
        } else if (dayOfWeek == 1) {
            calendar.add(5, -(7 - dayOfWeek));
        } else if (dayOfWeek > startDayOfWeek) {
            calendar.add(5, -(dayOfWeek - startDayOfWeek));
        }
        return formatDate(calendar);
    }

    public long getLNow() {
        return this.lNow;
    }

    public void setLNow(long now) {
        this.lNow = now;
    }

    public Calendar getCNow() {
        return this.cNow;
    }

    public void setCNow(Calendar now) {
        this.cNow = now;
    }

    public Date getDNow() {
        return this.dNow;
    }

    public void setDNow(Date now) {
        this.dNow = now;
    }

    public Timestamp getTNow() {
        return this.tNow;
    }

    public void setTNow(Timestamp now) {
        this.tNow = now;
    }

    public java.sql.Date getToday() {
        return this.today;
    }

    public void setToday(java.sql.Date today) {
        this.today = today;
    }

    public Time getNow() {
        return this.now;
    }

    public void setNow(Time now) {
        this.now = now;
    }

    public static void main(String[] args) {
    }
}
