package com.record.utils;

import android.os.PatternMatcher;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public final class Regexp {
    public static final String ID_card_regexp = "^\\d{10}|\\d{13}|\\d{15}|\\d{18}$";
    static final Set SEPARATOR_SET = new TreeSet();
    public static final String ZIP_regexp = "^[0-9]{6}$";
    public static final String date_regexp = "^((((19){1}|(20){1})d{2})|d{2})[-\\s]{1}[01]{1}d{1}[-\\s]{1}[0-3]{1}d{1}$";
    public static final String email_regexp = "(?:\\w[-._\\w]*\\w@\\w[-._\\w]*\\w\\.\\w{2,3}$)";
    public static final String http_regexp = "(http|https|ftp)://([^/:]+)(:\\d*)?([^#\\s]*)";
    public static final String icon_regexp = "^(/{0,1}\\w){1,}\\.(gif|dmp|png|jpg)$|^\\w{1,}\\.(gif|dmp|png|jpg)$";
    public static final String integer_regexp = "^-?\\d+$";
    public static final String letter_number_regexp = "^[A-Za-z0-9]+$";
    public static final String letter_number_underline_regexp = "^\\w+$";
    public static final String letter_regexp = "^[A-Za-z]+$";
    public static final String lower_letter_regexp = "^[a-z]+$";
    public static List matchingResultList = new ArrayList();
    public static final String negative_integers_regexp = "^-[0-9]*[1-9][0-9]*$";
    public static final String negative_rational_numbers_regexp = "^(-(([0-9]+\\.[0-9]*[1-9][0-9]*)|([0-9]*[1-9][0-9]*\\.[0-9]+)|([0-9]*[1-9][0-9]*)))$";
    public static final String non_negative_integers_regexp = "^\\d+$";
    public static final String non_negative_rational_numbers_regexp = "^\\d+(\\.\\d+)?$";
    public static final String non_positive_integers_regexp = "^((-\\d+)|(0+))$";
    public static final String non_positive_rational_numbers_regexp = "^((-\\d+(\\.\\d+)?)|(0+(\\.0+)?))$";
    public static final String non_special_char_regexp = "^[^'\"\\;,:-<>\\s].+$";
    public static final String non_zero_negative_integers_regexp = "^[1-9]+\\d*$";
    public static final String phone_regexp = "^[1][3-8]\\d{9}$";
    public static final String positive_integer_regexp = "^[0-9]*[1-9][0-9]*$";
    public static final String positive_rational_numbers_regexp = "^(([0-9]+\\.[0-9]*[1-9][0-9]*)|([0-9]*[1-9][0-9]*\\.[0-9]+)|([0-9]*[1-9][0-9]*))$";
    public static final String rational_numbers_regexp = "^(-?\\d+)(\\.\\d+)?$";
    public static HashMap regexpHash = new HashMap();
    public static final String tel_regexp = "^(?:0[0-9]{2,3}[-\\s]{1}|\\(0[0-9]{2,4}\\))[0-9]{6,8}$|^[1-9]{1}[0-9]{5,7}$|^[1-9]{1}[0-9]{10}$";
    public static final String upward_letter_regexp = "^[A-Z]+$";
    public static final String url_regexp = "(\\w+)://([^/:]+)(:\\d*)?([^#\\s]*)";

    private Regexp() {
        SEPARATOR_SET.add("(");
        SEPARATOR_SET.add(")");
        SEPARATOR_SET.add("[");
        SEPARATOR_SET.add("]");
        SEPARATOR_SET.add("{");
        SEPARATOR_SET.add("}");
        SEPARATOR_SET.add("<");
        SEPARATOR_SET.add(">");
    }

    public static Regexp getInstance() {
        return new Regexp();
    }

    public void putRegexpHash(String regexpName, String regexp) {
        regexpHash.put(regexpName, regexp);
    }

    public String getRegexpHash(String regexpName) {
        if (regexpHash.get(regexpName) != null) {
            return (String) regexpHash.get(regexpName);
        }
        System.out.println("在regexpHash中没有此正规表达式");
        return "";
    }

    public void clearRegexpHash() {
        regexpHash.clear();
    }

    public static boolean isMatch(String source, String regexp) {
        boolean z = false;
        try {
            PatternMatcher mat = new PatternMatcher(regexp, 0);
            mat.match(source);
            return mat.match(source);
        } catch (Exception e) {
            e.printStackTrace();
            return z;
        }
    }
}
