package com.record.service;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import com.record.bean.User;
import com.record.utils.DateTime;
import com.record.utils.db.DbUtils;
import com.umeng.analytics.onlineconfig.a;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

public class RecalculateAllocationRunnable implements Runnable {
    public static HashMap<Integer, Integer> Act2TypeMap;
    static Context context;
    public static ArrayList<Integer> invest_int = new ArrayList();
    public static ArrayList<Integer> routine_int = new ArrayList();
    public static ArrayList<Integer> sleep_int = new ArrayList();
    public static ArrayList<Integer> waste_int = new ArrayList();
    ArrayList<String> dateList;

    public RecalculateAllocationRunnable(Context context, ArrayList<String> dateList) {
        context = context;
        this.dateList = dateList;
    }

    public void run() {
        try {
            if (this.dateList != null) {
                Iterator it = this.dateList.iterator();
                while (it.hasNext()) {
                    try {
                        queryAndUpdateDb_Allocation_v2(((String) it.next()) + " 00:00:00");
                    } catch (Exception e) {
                        DbUtils.exceptionHandler(e);
                    }
                }
            }
        } catch (Exception e2) {
        }
    }

    private static void clearAllocationArr() {
        invest_int = new ArrayList();
        routine_int = new ArrayList();
        sleep_int = new ArrayList();
        waste_int = new ArrayList();
    }

    public static void queryAndUpdateDb_Allocation_v2(String time) {
        clearAllocationArr();
        String Date = time.substring(0, time.indexOf(" "));
        Cursor cursor = DbUtils.getDb(context).rawQuery("select * from t_act_item where userId is ? and startTime >= '" + Date + " 00:00:00' and startTime <= '" + Date + " 23:59:59' order by startTime", new String[]{User.getInstance().getUserId() + ""});
        String stopTime;
        int actId;
        if (cursor.getCount() > 0) {
            log("每日分配 ：有" + Date + "的记录");
            String zeroTime = Date + " 00:00:00";
            String tentyTime = Date + " 23:59:59";
            while (cursor.moveToNext()) {
                int counter;
                String startTime = cursor.getString(cursor.getColumnIndex("startTime"));
                stopTime = cursor.getString(cursor.getColumnIndex("stopTime"));
                actId = cursor.getInt(cursor.getColumnIndex("actId"));
                String start_str = startTime.substring(0, startTime.indexOf(" "));
                if (stopTime.substring(0, stopTime.indexOf(" ")).equals(start_str)) {
                    counter = DateTime.cal_secBetween(startTime, stopTime);
                } else {
                    counter = DateTime.cal_secBetween(startTime, tentyTime);
                }
                addAllocation(actId, counter);
                if (cursor.isFirst()) {
                    queryDb_stopTime1(zeroTime, startTime);
                }
                if (cursor.isLast()) {
                    ContentValues values;
                    if (start_str.equals(DateTime.getDateString())) {
                        values = cal_allocation(DateTime.getTimeString());
                        log("每日分配 ：values" + values.toString());
                    } else {
                        values = cal_allocation(start_str + " 23:59:59");
                    }
                    insertOrUpdateDb_allocation(Date, values);
                }
            }
        } else {
            boolean falg;
            DbUtils.close(cursor);
            if (Date.equals(DateTime.getDateString())) {
                falg = queryDb_stopTime(true, Date + " 00:00:00", DateTime.getTimeString());
            } else {
                falg = queryDb_stopTime(true, Date + " 00:00:00", Date + " 23:59:59");
            }
            if (!falg) {
                String time_temp = Date + " 23:59:59";
                cursor = DbUtils.getDb(context).rawQuery("select * from t_act_item where " + DbUtils.getWhereUserId(context) + " and startTime <= '" + Date + " 00:00:00' and stopTime >= '" + Date + " 23:59:59' order by startTime desc", null);
                if (cursor.getCount() > 0) {
                    cursor.moveToNext();
                    String start1 = cursor.getString(cursor.getColumnIndex("startTime"));
                    stopTime = cursor.getString(cursor.getColumnIndex("stopTime"));
                    actId = cursor.getInt(cursor.getColumnIndex("actId"));
                    int userId = cursor.getInt(cursor.getColumnIndex("userId"));
                    DateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                    try {
                        Date dt1 = df.parse(start1);
                        Date dt2 = df.parse(stopTime);
                        Date dt3 = df.parse(time_temp);
                        if (dt3.getTime() > dt1.getTime() && dt3.getTime() < dt2.getTime()) {
                            addAllocation(actId, 86399);
                            insertOrUpdateDb_allocation(Date, cal_allocation(time_temp));
                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                } else {
                    try {
                        if (DateTime.getDateString().equals(Date)) {
                            addAllocation(((Integer) DbUtils.queryActIdByType(context, "40").get(0)).intValue(), DateTime.cal_secBetween(Date + " 00:00:00", DateTime.getTimeString()));
                            insertOrUpdateDb_allocation(Date, cal_allocation(DateTime.getTimeString()));
                        } else {
                            addAllocation(((Integer) DbUtils.queryActIdByType(context, "40").get(0)).intValue(), 86399);
                            insertOrUpdateDb_allocation(Date, cal_allocation(time_temp));
                        }
                    } catch (Exception e2) {
                        DbUtils.exceptionHandler(e2);
                    }
                }
                DbUtils.close(cursor);
            }
        }
        DbUtils.close(cursor);
    }

    private static void insertOrUpdateDb_allocation(String date, ContentValues values) {
        Cursor cursor = DbUtils.getDb(context).rawQuery("Select * from t_allocation where userid is ? and time is '" + date + "'", new String[]{User.getInstance().getUserId() + ""});
        if (cursor.getCount() > 0) {
            DbUtils.getDb(context).update("t_allocation", values, " userid is ? and  time is  '" + date + "'", new String[]{User.getInstance().getUserId() + ""});
        } else {
            DbUtils.getDb(context).insert("t_allocation", null, values);
        }
        DbUtils.close(cursor);
    }

    private static ContentValues cal_allocation(String time) {
        String date = time.substring(0, time.indexOf(" "));
        int totalCount = DateTime.cal_secBetween(date + " 00:00:00", time);
        int vest = 0;
        Iterator it = invest_int.iterator();
        while (it.hasNext()) {
            vest += ((Integer) it.next()).intValue();
        }
        int routine = 0;
        it = routine_int.iterator();
        while (it.hasNext()) {
            routine += ((Integer) it.next()).intValue();
        }
        int sleep = 0;
        it = sleep_int.iterator();
        while (it.hasNext()) {
            sleep += ((Integer) it.next()).intValue();
        }
        int waste = totalCount - ((vest + routine) + sleep);
        ContentValues values = new ContentValues();
        values.put("userId", Integer.valueOf(User.getInstance().getUserId()));
        values.put("invest", Integer.valueOf(vest));
        values.put("routine", Integer.valueOf(routine));
        values.put("sleep", Integer.valueOf(sleep));
        values.put("waste", Integer.valueOf(waste));
        values.put("time", date);
        return values;
    }

    private static boolean queryDb_stopTime(boolean isClearArr, String zeroTime, String endTime) {
        if (isClearArr) {
            clearAllocationArr();
        }
        Cursor cursor2 = DbUtils.getDb(context).rawQuery("select * from t_act_item where userId is ? and stopTime > '" + zeroTime + "' and stopTime <= '" + endTime + "'", new String[]{User.getInstance().getUserId() + ""});
        if (cursor2.getCount() > 0) {
            cursor2.moveToNext();
            addAllocation(cursor2.getInt(cursor2.getColumnIndex("actId")), DateTime.cal_secBetween(zeroTime, cursor2.getString(cursor2.getColumnIndex("stopTime"))));
            insertOrUpdateDb_allocation(zeroTime.substring(0, zeroTime.indexOf(" ")), cal_allocation(endTime));
            return true;
        }
        DbUtils.close(cursor2);
        return false;
    }

    private static boolean queryDb_stopTime1(String zeroTime, String endTime) {
        Cursor cursor2 = DbUtils.getDb(context).rawQuery("select * from t_act_item where userId is ? and startTime < '" + zeroTime + "' and stopTime >= '" + zeroTime + "' order by stopTime", new String[]{User.getInstance().getUserId() + ""});
        if (cursor2.getCount() > 0) {
            while (cursor2.moveToNext()) {
                int actId2 = cursor2.getInt(cursor2.getColumnIndex("actId"));
                String stopTime2 = cursor2.getString(cursor2.getColumnIndex("stopTime"));
                String startTime2 = cursor2.getString(cursor2.getColumnIndex("startTime"));
                if (!startTime2.substring(0, startTime2.indexOf(" ")).equals(stopTime2.substring(0, stopTime2.indexOf(" ")))) {
                    addAllocation(actId2, DateTime.cal_secBetween(zeroTime, stopTime2));
                }
            }
            return true;
        }
        DbUtils.close(cursor2);
        return false;
    }

    public static HashMap<Integer, Integer> getAct2TypeMap() {
        HashMap<Integer, Integer> map = new HashMap();
        Cursor cur = DbUtils.getDb2(context).rawQuery("select id,type from t_act where " + DbUtils.getWhereUserId(context), null);
        if (cur.getCount() > 0) {
            while (cur.moveToNext()) {
                map.put(Integer.valueOf(cur.getInt(cur.getColumnIndex("id"))), Integer.valueOf(cur.getInt(cur.getColumnIndex(a.a))));
            }
        }
        DbUtils.close(cur);
        return map;
    }

    private static void addAllocation(int actId, int counter) {
        if (Act2TypeMap == null || Act2TypeMap.get(Integer.valueOf(actId)) == null) {
            Act2TypeMap = getAct2TypeMap();
        }
        if (Act2TypeMap.size() > 0) {
            if (((Integer) Act2TypeMap.get(Integer.valueOf(actId))).intValue() == 10 || ((Integer) Act2TypeMap.get(Integer.valueOf(actId))).intValue() == 11) {
                invest_int.add(Integer.valueOf(counter));
            } else if (((Integer) Act2TypeMap.get(Integer.valueOf(actId))).intValue() == 20) {
                routine_int.add(Integer.valueOf(counter));
            } else if (((Integer) Act2TypeMap.get(Integer.valueOf(actId))).intValue() == 30) {
                sleep_int.add(Integer.valueOf(counter));
            } else if (((Integer) Act2TypeMap.get(Integer.valueOf(actId))).intValue() == 40) {
                waste_int.add(Integer.valueOf(counter));
            }
        }
        log("每日分配：invest_int:" + invest_int.toString() + ",routine_int:" + routine_int + ",sleep_int:" + sleep_int);
    }

    private static void log(String str) {
        Log.i("override Main", ":" + str);
    }
}
