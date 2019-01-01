package com.record.thread;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import com.record.myLife.main.TodayActivity;
import com.record.utils.Val;
import com.record.utils.db.DbBase;
import com.record.utils.db.DbUtils;
import java.util.Iterator;
import java.util.TreeSet;

public class AllocationAndStaticsRunnable extends DbBase implements Runnable {
    static String TAG = "override";
    TreeSet<String> changeDateArr;
    private Context context;
    TreeSet<Integer> goalIdSet;

    public AllocationAndStaticsRunnable(Context context, TreeSet<Integer> goalIdSet, TreeSet<String> changeDateArr) {
        this.context = context;
        this.goalIdSet = goalIdSet;
        this.changeDateArr = changeDateArr;
        TAG = "override " + getClass().getSimpleName();
    }

    public void run() {
        try {
            if (this.changeDateArr != null && this.changeDateArr.size() > 0) {
                allocation(this.changeDateArr);
                TreeSet<Integer> typeArr = new TreeSet();
                log("添加时间影响到的id有：" + this.goalIdSet.toString());
                if (this.goalIdSet != null && this.goalIdSet.size() > 0) {
                    Iterator it = this.goalIdSet.iterator();
                    while (it.hasNext()) {
                        int goalId = ((Integer) it.next()).intValue();
                        typeArr.add(DbUtils.queryActTypeById(this.context, goalId + ""));
                        DbUtils.staticsGoalAllAutoUpdateBigGoalByGoalId(this.context, goalId);
                    }
                }
                if (typeArr.contains(Integer.valueOf(11))) {
                    this.context.sendBroadcast(new Intent(Val.INTENT_ACTION_UPDATE_UI_GOAL));
                }
                this.context.sendBroadcast(new Intent(Val.INTENT_ACTION_WIDGET_UPDATE_BAR_UI));
            }
        } catch (Exception e) {
            DbUtils.exceptionHandler(e);
        }
    }

    private void allocation(TreeSet<String> changeDateArr) {
        if (changeDateArr != null && changeDateArr.size() > 0) {
            Iterator it = changeDateArr.iterator();
            while (it.hasNext()) {
                String date = (String) it.next();
                if (date.contains(" ")) {
                    TodayActivity.queryAndUpdateDb_Allocation_v2(date);
                } else {
                    TodayActivity.queryAndUpdateDb_Allocation_v2(date + " 00:00:00");
                }
            }
        }
    }

    public static ContentValues getValues() {
        ContentValues values = new ContentValues();
        values.put("isUpload", Integer.valueOf(1));
        return values;
    }

    public static boolean isUidExist(Context context, int id) {
        if (DbUtils.queryUserUidByUserId(context, id) > 0) {
            return true;
        }
        return false;
    }

    private static void log(String str) {
        Log.i(TAG, ":" + str);
    }
}
