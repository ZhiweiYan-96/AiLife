package com.record.custom;

import com.record.utils.DateTime;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class DateTimeBean {
    public int diffToEnd = 0;
    public int diffToStart = 0;
    private int startDateOfWeek = 1;
    public String startDateOfWeekStr = "";

    public DateTimeBean(int startDateOfWeek) {
        this.startDateOfWeek = startDateOfWeek;
        calc();
    }

    public void calc() {
        Calendar cal = Calendar.getInstance();
        int diff = cal.get(7) - this.startDateOfWeek;
        if (diff >= 0) {
            this.diffToStart = diff;
            this.diffToEnd = 6 - diff;
        } else {
            this.diffToStart = 6;
            this.diffToEnd = 0;
        }
        cal.set(5, -this.diffToStart);
        this.startDateOfWeekStr = formatDate(cal);
    }

    public String getStartDateBefordWeeks(int week) {
        Calendar cal = Calendar.getInstance();
        cal.add(5, -(this.diffToStart + (week * 7)));
        return formatDate(cal);
    }

    public int getDiffBeforeWeeks(int week) {
        return this.diffToStart + (week * 7);
    }

    private String formatDate(Calendar c) {
        Calendar calendar;
        if (c != null) {
            calendar = c;
        } else {
            calendar = Calendar.getInstance();
        }
        return new SimpleDateFormat(DateTime.DATE_FORMAT_LINE).format(calendar.getTime());
    }
}
