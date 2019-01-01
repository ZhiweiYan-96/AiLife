package com.record.utils;

import java.text.NumberFormat;

public class FormatUtils {
    static NumberFormat formater;

    public static String format_1fra(double val) {
        formater = NumberFormat.getInstance();
        formater.setMinimumFractionDigits(1);
        formater.setMaximumFractionDigits(1);
        return formater.format(val);
    }

    public static String format_2fra(double val) {
        formater = NumberFormat.getInstance();
        formater.setMinimumFractionDigits(2);
        formater.setMaximumFractionDigits(2);
        return formater.format(val);
    }

    public static String format_0fra(double val) {
        formater = NumberFormat.getInstance();
        formater.setMinimumFractionDigits(0);
        formater.setMaximumFractionDigits(0);
        return formater.format(val);
    }
}
