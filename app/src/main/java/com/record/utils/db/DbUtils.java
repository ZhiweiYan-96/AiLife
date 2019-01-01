package com.record.utils.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import com.record.bean.Act;
import com.record.bean.Act2;
import com.record.bean.Goal;
import com.record.bean.IDemoChart;
import com.record.bean.Record;
import com.record.bean.Record2;
import com.record.bean.Statics;
import com.record.bean.User;
import com.record.bean.XYColumn;
import com.record.bean.dbbean.Allocation;
import com.record.myLife.BaseApplication;
import com.record.myLife.R;
import com.record.thread.AllocationAndStaticsRunnable;
import com.record.utils.DateTime;
import com.record.utils.FormatUtils;
import com.record.utils.GeneralHelper;
import com.record.utils.PushInitUtils;
import com.record.utils.Sql;
import com.record.utils.ToastUtils;
import com.record.utils.UserUtils;
import com.record.utils.Val;
import com.record.utils.Val.SHARE_STRING;
import com.umeng.analytics.onlineconfig.a;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.TreeSet;

public class DbUtils {
    private static String VERSION_NAME;
    private static SQLiteDatabase db;

    public static SQLiteDatabase getDb(Context context) {
        if (db == null) {
            db = new DBHelper(context, "mylife_db").getWritableDatabase();
        }
        try {
            if (VERSION_NAME == null) {
                VERSION_NAME = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (db == null && BaseApplication.getInstance() != null) {
            db = new DBHelper(BaseApplication.getInstance(), "mylife_db").getWritableDatabase();
        }
        return db;
    }

    public static SQLiteDatabase getDb2(Context context) {
        if (db != null) {
            return db;
        }
        return getDb(context);
    }

    public static void reGetDb(Context context) {
        try {
            if (db != null) {
                db.close();
            }
        } catch (Exception e) {
            exceptionHandler(e);
        }
        db = new DBHelper(context, "mylife_db").getWritableDatabase();
    }

    public static void addTimeAndSave2(Context context, String addStartTime, String addEndTime, String checkActId) {
        String addStartDate = addStartTime.substring(0, addStartTime.indexOf(" "));
        String addEndDate = addEndTime.substring(0, addEndTime.indexOf(" "));
        TreeSet<Integer> goalIdSet1 = updateDbActItem_ChangeEndTime(context, addStartTime, checkActId);
        TreeSet<Integer> goalIdSet2 = updateDbActItem_ChangeStartTime(context, addEndTime, checkActId);
        TreeSet<Integer> goalIdSet3 = deleteActItem_deleteRecords(context, addStartTime, addEndTime, checkActId);
    }

    public static long addTimeAndSave(Context context, String addStartTime, String addEndTime, int record, String checkActId) {
        String addStartDate = addStartTime.substring(0, addStartTime.indexOf(" "));
        String addEndDate = addEndTime.substring(0, addEndTime.indexOf(" "));
        TreeSet<Integer> goalIdSet1 = updateDbActItem_ChangeEndTime(context, addStartTime, "");
        TreeSet<Integer> goalIdSet2 = updateDbActItem_ChangeStartTime(context, addEndTime, "");
        TreeSet<Integer> goalIdSet3 = deleteActItem_deleteRecords(context, addStartTime, addEndTime, "");
        ContentValues values = new ContentValues();
        values.put("userId", queryUserId(context));
        values.put("actId", checkActId);
        values.put("actType", queryActTypeById(context, checkActId));
        values.put("startTime", addStartTime);
        values.put("take", Integer.valueOf(DateTime.cal_secBetween(addStartTime, addEndTime)));
        values.put("stopTime", addEndTime);
        values.put("isEnd", Integer.valueOf(1));
        values.put("isRecord", Integer.valueOf(record));
        long tempItemsId = getDb(context).insert("t_act_item", null, values);
        ToastUtils.toastShort(context, "添加成功！");
        TreeSet<String> dateArr = new TreeSet();
        dateArr.add(addEndDate);
        dateArr.add(addStartDate);
        goalIdSet3.add(Integer.valueOf(Integer.parseInt(checkActId)));
        Iterator it = goalIdSet1.iterator();
        while (it.hasNext()) {
            goalIdSet3.add(Integer.valueOf(((Integer) it.next()).intValue()));
        }
        it = goalIdSet2.iterator();
        while (it.hasNext()) {
            goalIdSet3.add(Integer.valueOf(((Integer) it.next()).intValue()));
        }
        new Thread(new AllocationAndStaticsRunnable(context, goalIdSet3, dateArr)).start();
        return tempItemsId;
    }

    public static void deleteUnhandlerTomato(Context context, String startTime) {
        getDb(context).delete("t_unhandler_tomato", "startTime = '" + startTime + "'", null);
    }

    public static ArrayList<Integer> querySubGoalIdByBigGoalId(Context context, int bigGoalId) {
        ArrayList<Integer> arr = null;
        Cursor cursor = getDb(context).rawQuery(Sql.goalGetSubGoalById_id(context, bigGoalId), null);
        if (cursor.getCount() > 0) {
            arr = new ArrayList();
            while (cursor.moveToNext()) {
                arr.add(Integer.valueOf(getInt(cursor, "id")));
            }
        }
        close(cursor);
        return arr;
    }

    public static ArrayList<Integer> querySubGoalIdByBigGoalIdContainDelte(Context context, int bigGoalId) {
        ArrayList<Integer> arr = null;
        Cursor cursor = getDb(context).rawQuery(Sql.goalGetSubGoalByIdContainDelte_id(context, bigGoalId), null);
        if (cursor.getCount() > 0) {
            arr = new ArrayList();
            while (cursor.moveToNext()) {
                arr.add(Integer.valueOf(getInt(cursor, "id")));
            }
        }
        close(cursor);
        return arr;
    }

    public static ArrayList<Integer> querySubGoalIdByBigGoalIdFinish(Context context, int bigGoalId) {
        ArrayList<Integer> arr = null;
        Cursor cursor = getDb(context).rawQuery(Sql.goalGetSubGoalByIdFinish(context, bigGoalId), null);
        if (cursor.getCount() > 0) {
            arr = new ArrayList();
            while (cursor.moveToNext()) {
                arr.add(Integer.valueOf(getInt(cursor, "id")));
            }
        }
        close(cursor);
        return arr;
    }

    public static int queryBigGoalIdBySubGoalId(Context context, int subGoalId) {
        int bigGoalId = 0;
        Cursor cursor = getDb(context).rawQuery(Sql.goalGetBigGoalBySubId(context, subGoalId), null);
        if (cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                bigGoalId = getInt(cursor, "isSubGoal");
            }
        }
        close(cursor);
        return bigGoalId;
    }

    public static void queryUpdateLabelActType(Context context) {
        ContentValues values = new ContentValues();
        values.put("actType", Integer.valueOf(1));
        getDb(context).update("t_sub_type", values, " Id > 0 ", null);
    }

    public static void updateGoalIconBroomTOTrash(Context context) {
        ContentValues values = new ContentValues();
        values.put("image", "trash");
        getDb(context).update("t_act", values, " type = 40 ", null);
    }

    public static void queryUpdateLabelAddGoalIdData(Context context) {
        String tableName = "t_routine_link";
        Cursor cursor = getDb(context).rawQuery("select Id,goalId,itemsId from " + tableName, null);
        if (cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                int Id = getInt(cursor, "Id");
                int goalId = getInt(cursor, "goalId");
                int itemsId = getInt(cursor, "itemsId");
                if (goalId == 0) {
                    Cursor cursor2 = getDb(context).rawQuery("select actId,actType from t_act_item where id is " + itemsId, null);
                    if (cursor2.getCount() > 0) {
                        cursor2.moveToNext();
                        ContentValues values = new ContentValues();
                        values.put("goalId", Integer.valueOf(getInt(cursor2, "actId")));
                        values.put("goalType", Integer.valueOf(getInt(cursor2, "actType")));
                        getDb(context).update(tableName, values, " Id is " + Id, null);
                    }
                    close(cursor2);
                }
            }
        }
        close(cursor);
    }

    public static void staticsGoalAllAutoUpdateBigGoalByGoalId(Context context, int goalId) {
        int type = 20;
        int isSubGoal = 0;
        Cursor cursor = getDb2(context).rawQuery("select type,isSubGoal from t_act where  id is " + goalId, null);
        if (cursor.getCount() > 0) {
            cursor.moveToNext();
            type = cursor.getInt(cursor.getColumnIndex(a.a));
            isSubGoal = cursor.getInt(cursor.getColumnIndex("isSubGoal"));
        }
        close(cursor);
        if (type == 11) {
            if (isSubGoal == 0) {
                ArrayList<Integer> subIdArr = getSubGoalIdArrByBigGoalId(context, goalId);
                if (subIdArr != null) {
                    Iterator it = subIdArr.iterator();
                    while (it.hasNext()) {
                        int id = ((Integer) it.next()).intValue();
                        statisticsGoalById(context, id);
                        compareStaticsLocWithSer(context, id);
                    }
                }
            } else if (isSubGoal > 0) {
                getDb(context).delete("t_goal_statics", getWhereUserId(context) + " and goalId = " + goalId + " and staticsType = " + 6, null);
            }
            statisticsGoalById(context, goalId);
            compareStaticsLocWithSer(context, goalId);
            if (isSubGoal == 0) {
                compareStaticsWithSub(context, goalId);
            } else {
                statisticsGoalById(context, isSubGoal);
                compareStaticsLocWithSer(context, isSubGoal);
                compareStaticsWithSub(context, isSubGoal);
                log("统计单个目标的上下级（如果有）----大目标统计结束");
            }
            log("统计单个目标的上下级（如果有）----目标" + goalId + "统计结束");
        }
    }

    public static void staticsGoalAll2(Context context) {
        log("全部统计数据，并生成5，6类型");
        compareStatisticsAllTo5(context);
        compareStatisticsAllTo6(context);
    }

    public static void staticsGoalAll(Context context) {
        log("全部统计数据，并生成5，6类型");
        staticsLocal(context);
        compareStatisticsAllTo5(context);
        compareStatisticsAllTo6(context);
    }

    public static void compareStatisticsAllTo5(Context context) {
        Cursor cursor = getDb(context).rawQuery(Sql.goalGetGoal_id(context) + " and type = 11", null);
        if (cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                compareStaticsLocWithSer(context, getInt(cursor, "id"));
            }
        }
        close(cursor);
    }

    public static void compareStatisticsAllTo6(Context context) {
        Cursor cursor = getDb(context).rawQuery(Sql.goalGetBigGoalsNotSub(context), null);
        if (cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                compareStaticsWithSub(context, getInt(cursor, "id"));
            }
        }
        close(cursor);
    }

    public static void compareStaticsLocWithSer(Context context, int id) {
        Statics compareStatics = getStaticsByLocWithSer(getStaticsByGoalId(context, id, Val.STATISTICS_TYPE_ALL), getStaticsByGoalId(context, id, Val.STATISTICS_TYPE_UNUPLOAD), getStaticsByGoalId(context, id, Val.STATISTICS_TYPE_SERVER));
        ContentValues values = getContentValueByStatics(compareStatics);
        values.put("staticsType", Integer.valueOf(Val.STATISTICS_TYPE_COMPARE_LOC_SER));
        saveOrUpdateStatics(context, values);
        ContentValues values2 = new ContentValues();
        values2.put("hadSpend", Double.valueOf(compareStatics.getHadInvest()));
        getDb(context).update("t_act", values2, "id is ?", new String[]{compareStatics.getGoalId() + ""});
    }

    public static void compareStaticsWithSub(Context context, int goalId) {
        Cursor cursor = getDb(context).rawQuery("select id from t_act where " + getWhereUserId(context) + " and isSubGoal = " + goalId + " limit 2", null);
        if (cursor.getCount() > 0) {
            Statics statics = getStaticsByCompareWithSub(context, goalId);
            statics.setStaticsType(Val.STATISTICS_TYPE_COMPARE_LOC_SUB);
            saveOrUpdateStatics(context, getContentValueByStatics(statics));
        } else {
            getDb(context).delete("t_goal_statics", "userId = ? and goalId = ? and staticsType = ?", new String[]{queryUserId(context), goalId + "", Val.STATISTICS_TYPE_COMPARE_LOC_SUB + ""});
        }
        close(cursor);
    }

    public static ContentValues getContentValueByStatics(Statics statics) {
        if (statics == null) {
            return null;
        }
        ContentValues values = new ContentValues();
        values.put("userId", Integer.valueOf(statics.getUserId()));
        values.put("goalId", Integer.valueOf(statics.getGoalId()));
        values.put("goalName", statics.getGoalName());
        values.put("goalType", Integer.valueOf(statics.getGoalType()));
        values.put("staticsType", Integer.valueOf(statics.getStaticsType()));
        values.put("expectInvest", Double.valueOf(statics.getExpectInvest()));
        values.put("hadInvest", Double.valueOf(statics.getHadInvest()));
        values.put("todayInvest", Double.valueOf(statics.getTodayInvest()));
        values.put("sevenInvest", Double.valueOf(statics.getSevenInvest()));
        values.put("createTime", statics.getCreateTime());
        values.put("startTime", statics.getStartTime());
        values.put("deadline", statics.getDeadline());
        return values;
    }

    public static Statics getStaticsByLocWithSer(Statics locStatics, Statics unStatic, Statics serStatics) {
        if (serStatics == null) {
            return locStatics;
        }
        if (locStatics == null) {
            return serStatics;
        }
        if (unStatic == null || serStatics == null) {
            return locStatics;
        }
        log("getStaticsByLocWithSer本地与服务器数据比较");
        locStatics.setHadInvest(serStatics.getHadInvest() + unStatic.getHadInvest());
        return locStatics;
    }

    public static Statics getStaticsByCompareWithSub(Context context, int goalId) {
        Statics bigStatics = null;
        if (goalId > 0) {
            bigStatics = getStaticsByGoalId(context, goalId, Val.STATISTICS_TYPE_COMPARE_LOC_SER);
            ArrayList<Statics> arrStatics = getUnFinishStaticsArrByBigGoalId(context, goalId);
            if (arrStatics != null && arrStatics.size() > 0) {
                Iterator it = arrStatics.iterator();
                while (it.hasNext()) {
                    Statics subStatics = (Statics) it.next();
                    if (subStatics != null && bigStatics.getStaticsType() == subStatics.getStaticsType() && subStatics.getStaticsType() == Val.STATISTICS_TYPE_COMPARE_LOC_SER) {
                        bigStatics.setHadInvest(bigStatics.getHadInvest() + subStatics.getHadInvest());
                        bigStatics.setSevenInvest(bigStatics.getSevenInvest() + subStatics.getSevenInvest());
                        bigStatics.setTodayInvest(bigStatics.getTodayInvest() + subStatics.getTodayInvest());
                    }
                }
            }
            Statics finishStatics = getStaticsByGoalId(context, goalId, Val.STATISTICS_TYPE_SUB_FINISH_IN_SERVER);
            if (finishStatics != null) {
                bigStatics.setHadInvest(bigStatics.getHadInvest() + finishStatics.getHadInvest());
                bigStatics.setSevenInvest(bigStatics.getSevenInvest() + finishStatics.getSevenInvest());
                bigStatics.setTodayInvest(bigStatics.getTodayInvest() + finishStatics.getTodayInvest());
            }
            try {
                double[] investDoubleArr = getStaticsSevenInvest(context, goalId);
                if (investDoubleArr != null) {
                    bigStatics.setHadInvest(bigStatics.getHadInvest() + investDoubleArr[0]);
                    bigStatics.setSevenInvest(bigStatics.getSevenInvest() + investDoubleArr[1]);
                    bigStatics.setTodayInvest(bigStatics.getTodayInvest() + investDoubleArr[2]);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            Cursor cursor2 = getDb(context).rawQuery("Select id,severId from t_act where " + getWhereUserId(context) + " and isManuscript is not 1 and isFinish = 1 and severId > 0 and isSubGoal is " + goalId, null);
            if (cursor2.getCount() > 0) {
                ArrayList<Integer> idArr = new ArrayList();
                ArrayList<Integer> sIdArr = new ArrayList();
                while (cursor2.moveToNext()) {
                    int id = cursor2.getInt(cursor2.getColumnIndex("id"));
                    int serverId = cursor2.getInt(cursor2.getColumnIndex("severId"));
                    idArr.add(Integer.valueOf(id));
                    sIdArr.add(Integer.valueOf(serverId));
                }
                try {
                    int sGoalId = querysGoalIdByActId(context, goalId);
                    if (sGoalId > 0) {
                        ArrayList<Integer> sSubGoalArr = queryBigToSubgoalArrBysGoalId(context, sGoalId);
                        if (sSubGoalArr == null || sSubGoalArr.size() <= 0) {
                            bigStatics = addStaticsHadInvest(context, bigStatics, idArr);
                        } else {
                            ArrayList<Integer> indexArr = new ArrayList();
                            Iterator it2 = sIdArr.iterator();
                            while (it2.hasNext()) {
                                int tempServerId = ((Integer) it2.next()).intValue();
                                if (sSubGoalArr.contains(Integer.valueOf(tempServerId))) {
                                    indexArr.add(Integer.valueOf(sIdArr.indexOf(new Integer(tempServerId))));
                                }
                            }
                            it2 = indexArr.iterator();
                            while (it2.hasNext()) {
                                idArr.remove(((Integer) it2.next()).intValue());
                            }
                            bigStatics = addStaticsHadInvest(context, bigStatics, idArr);
                        }
                    } else {
                        bigStatics = addStaticsHadInvest(context, bigStatics, idArr);
                    }
                } catch (Exception e2) {
                    exceptionHandler(e2);
                }
            }
            close(cursor2);
        }
        return bigStatics;
    }

    public static Statics addStaticsHadInvest(Context context, Statics bigStatics, ArrayList<Integer> idArr) {
        if (!(idArr == null || idArr.size() <= 0 || bigStatics == null)) {
            Iterator it = idArr.iterator();
            while (it.hasNext()) {
                Statics tempSubStatics = getStaticsByGoalId(context, ((Integer) it.next()).intValue(), Val.STATISTICS_TYPE_SERVER);
                if (tempSubStatics != null && tempSubStatics.getHadInvest() > 0.0d) {
                    bigStatics.setHadInvest(bigStatics.getHadInvest() + tempSubStatics.getHadInvest());
                }
            }
        }
        return bigStatics;
    }

    public static double[] getStaticsSevenInvest(Context context, int goalId) {
        double[] investDoubleArr = null;
        ArrayList<Integer> finishGoalIdArr = querySubGoalIdByBigGoalIdFinish(context, goalId);
        if (finishGoalIdArr != null) {
            String where = "";
            Iterator it = finishGoalIdArr.iterator();
            while (it.hasNext()) {
                where = where + " actId = " + ((Integer) it.next()).intValue() + " or ";
            }
            if (where.length() > 5) {
                where = where.substring(0, where.lastIndexOf("or "));
            }
            if (where.length() > 0) {
                String stopTime;
                investDoubleArr = new double[3];
                String sql = "select selection from (select * from t_act_item where isUpload is not 1 and isDelete is not 1 and " + getWhereUserId(context) + " ) where " + where;
                double hadInvest = 0.0d;
                Cursor cursor = getDb(context).rawQuery(sql.replace("selection", "sum(take)"), null);
                if (cursor.getCount() > 0) {
                    while (cursor.moveToNext()) {
                        hadInvest = getDou(cursor, "sum(take)");
                    }
                }
                close(cursor);
                String now = DateTime.getTimeString();
                int index = now.indexOf(" ");
                String todayStart = now.substring(0, index) + " 00:00:00";
                String todayEnd = now.substring(0, index) + " 23:59:59";
                double sevenInvest = 0.0d;
                String dateBefore7 = DateTime.beforeNDays2Str(-6) + " 00:00:00";
                cursor = getDb(context).rawQuery(sql.replace("selection", "sum(take)") + " and startTime >= '" + dateBefore7 + "' and stopTime <= '" + todayEnd + "' ", null);
                if (cursor.getCount() > 0) {
                    while (cursor.moveToNext()) {
                        sevenInvest = getDou(cursor, "sum(take)");
                    }
                }
                close(cursor);
                try {
                    cursor = getDb(context).rawQuery(sql.replace("selection", "stopTime") + " and startTime <= '" + dateBefore7 + "' and stopTime >= '" + dateBefore7 + "' ", null);
                    if (cursor.getCount() > 0) {
                        cursor.moveToNext();
                        stopTime = getStr(cursor, "stopTime");
                        if (stopTime != null) {
                            sevenInvest += (double) DateTime.cal_secBetween(dateBefore7, stopTime);
                        }
                    }
                    close(cursor);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (sevenInvest > 604800.0d) {
                    sevenInvest = 604800.0d;
                }
                double todayInvest = 0.0d;
                cursor = getDb(context).rawQuery(sql.replace("selection", "sum(take)") + " and startTime >= '" + todayStart + "' and stopTime <= '" + todayEnd + "' ", null);
                if (cursor.getCount() > 0) {
                    cursor.moveToNext();
                    todayInvest = (double) getInt(cursor, "sum(take)");
                }
                close(cursor);
                cursor = getDb(context).rawQuery(sql.replace("selection", "stopTime") + " and startTime < '" + todayStart + "' and stopTime > '" + todayStart + "' ", null);
                if (cursor.getCount() > 0) {
                    cursor.moveToNext();
                    stopTime = getStr(cursor, "stopTime");
                    if (stopTime != null) {
                        todayInvest += (double) DateTime.cal_secBetween(todayStart, stopTime);
                    }
                }
                close(cursor);
                if (todayInvest > 86400.0d) {
                    todayInvest = 86400.0d;
                }
                investDoubleArr[0] = hadInvest;
                investDoubleArr[1] = sevenInvest;
                investDoubleArr[2] = todayInvest;
                log("统计已经完成，未上传子目标的投入  investDoubleArr[0]:" + investDoubleArr[0] + "，investDoubleArr[1]:" + investDoubleArr[1] + ",investDoubleArr[2]" + investDoubleArr[2]);
            }
        }
        return investDoubleArr;
    }

    public static ArrayList<Integer> getSubGoalIdArrByBigGoalId(Context context, int goalId) {
        ArrayList<Integer> arr = null;
        Cursor cursor = getDb(context).rawQuery(Sql.goalGetSubGoalByIdContainDelte_id(context, goalId), null);
        if (cursor.getCount() > 0) {
            arr = new ArrayList();
            while (cursor.moveToNext()) {
                arr.add(Integer.valueOf(getInt(cursor, "id")));
            }
        }
        close(cursor);
        return arr;
    }

    public static ArrayList<Goal> getSubGoalIdArrByBigGoalId2(Context context, int goalId) {
        ArrayList<Goal> arr = null;
        Cursor cursor = getDb(context).rawQuery(Sql.goalGetSubGoalById(context, goalId), null);
        if (cursor.getCount() > 0) {
            arr = new ArrayList();
            while (cursor.moveToNext()) {
                int id = getInt(cursor, "id");
                String goalName = getStr(cursor, "actName");
                if (id > 0) {
                    arr.add(new Goal(id, goalName));
                }
            }
        }
        close(cursor);
        return arr;
    }

    public static ArrayList<Statics> getUnFinishStaticsArrByBigGoalId(Context context, int goalId) {
        ArrayList<Statics> arr = null;
        Cursor cursor = getDb(context).rawQuery(Sql.goalGetSubGoalByIdContainDelteWithoutFinish_id(context, goalId), null);
        if (cursor.getCount() > 0) {
            arr = new ArrayList();
            while (cursor.moveToNext()) {
                Statics statics = getStaticsByGoalId(context, getInt(cursor, "id"), Val.STATISTICS_TYPE_COMPARE_LOC_SER);
                if (statics != null) {
                    arr.add(statics);
                }
            }
        }
        close(cursor);
        return arr;
    }

    public static ArrayList<Statics> getStaticsArrByBigGoalId(Context context, int goalId) {
        ArrayList<Statics> arr = null;
        Cursor cursor = getDb(context).rawQuery(Sql.goalGetSubGoalByIdContainDelte_id(context, goalId), null);
        if (cursor.getCount() > 0) {
            arr = new ArrayList();
            while (cursor.moveToNext()) {
                Statics statics = getStaticsByGoalId(context, getInt(cursor, "id"), Val.STATISTICS_TYPE_COMPARE_LOC_SER);
                if (statics != null) {
                    arr.add(statics);
                }
            }
        }
        close(cursor);
        return arr;
    }

    public static Statics getStaticsByGoalId(Context context, int goalId, int staticsType) {
        Cursor cursor = getDb(context).rawQuery(Sql.staticsGetStaticsByGoalIdAndType(context, goalId, staticsType), null);
        if (cursor.getCount() <= 0 || !cursor.moveToNext()) {
            close(cursor);
            return null;
        }
        Statics statics = new Statics(getInt(cursor, "userId"), goalId, getStr(cursor, "goalName"), getInt(cursor, "goalType"), staticsType, getDou(cursor, "expectInvest"), getDou(cursor, "hadInvest"), getDou(cursor, "todayInvest"), getDou(cursor, "sevenInvest"), getStr(cursor, "createTime"), getStr(cursor, "startTime"), getStr(cursor, "deadline"));
        close(cursor);
        return statics;
    }

    public static int queryStaticsByGoalId(Context context, int goalId, int staticsType) {
        Cursor cursor = getDb(context).rawQuery(Sql.staticsGetStaticsByGoalIdAndType_Id(context, goalId, staticsType), null);
        if (cursor.getCount() > 0) {
            close(cursor);
            return 1;
        }
        close(cursor);
        return 0;
    }

    public static int queryBigToSubgoalBysGoalId(Context context, int sGoalId) {
        Cursor cursor = getDb(context).rawQuery("select id from t_server_bigtosubgoal where " + getWhereUserId(context) + " and sGoalId = " + sGoalId + " limit 1", null);
        if (cursor.getCount() > 0) {
            close(cursor);
            return 1;
        }
        close(cursor);
        return 0;
    }

    public static ArrayList<Integer> queryBigToSubgoalArrBysGoalId(Context context, int sGoalId) {
        Exception e;
        ArrayList<Integer> list = null;
        try {
            Cursor cursor = getDb(context).rawQuery("select * from t_server_bigtosubgoal where " + getWhereUserId(context) + " and sGoalId = " + sGoalId, null);
            if (cursor.getCount() > 0) {
                cursor.moveToNext();
                ArrayList<Integer> list2 = new ArrayList();
                try {
                    String arr = cursor.getString(cursor.getColumnIndex("sSubFinishedGoalId"));
                    if (arr != null) {
                        for (String str : arr.split(",")) {
                            list2.add(Integer.valueOf(Integer.parseInt(str)));
                        }
                    }
                    list = list2;
                } catch (Exception e2) {
                    e = e2;
                    list = list2;
                    exceptionHandler(e);
                    return list;
                }
            }
            close(cursor);
        } catch (Exception e3) {
            e = e3;
            exceptionHandler(e);
            return list;
        }
        return list;
    }

    public static void staticsLocal(Context context) {
        Cursor cursor = getDb(context).rawQuery(Sql.getAllGoalsNotManuscript(context) + " and type = 11", null);
        if (cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                statisticsGoalById(context, getInt(cursor, "id"));
            }
        }
        close(cursor);
    }

    public static void statisticsGoalById(Context context, int id) {
        Cursor cursor = getDb(context).rawQuery(Sql.getGoalById(context, id) + " and type = 11 ", null);
        if (cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                int userId = getInt(cursor, "userId");
                int goalId = getInt(cursor, "id");
                int goalType = getInt(cursor, a.a);
                int expectInvest = getInt(cursor, "expectSpend");
                String goalName = getStr(cursor, "actName");
                String createTime = getStr(cursor, "createTime");
                String startTime = getStr(cursor, "startTime");
                String deadline = getStr(cursor, "deadline");
                ContentValues values = new ContentValues();
                values.put("userId", Integer.valueOf(userId));
                values.put("goalId", Integer.valueOf(goalId));
                values.put("goalType", Integer.valueOf(goalType));
                values.put("expectInvest", Integer.valueOf(expectInvest));
                values.put("goalName", goalName);
                values.put("createTime", createTime);
                values.put("startTime", startTime);
                values.put("deadline", deadline);
                statisticsGoalById2(context, Val.STATISTICS_TYPE_ALL, goalId, values);
                statisticsGoalById2(context, Val.STATISTICS_TYPE_UNUPLOAD, goalId, values);
            }
        }
        close(cursor);
    }

    public static void statisticsGoalById2(Context context, int staticsType, int goalId, ContentValues contentValues) {
        if (goalId != 0) {
            String stopTime;
            String str = "";
            if (staticsType == Val.STATISTICS_TYPE_ALL) {
                contentValues.put("staticsType", Integer.valueOf(Val.STATISTICS_TYPE_ALL));
            } else if (staticsType == Val.STATISTICS_TYPE_UNUPLOAD) {
                contentValues.put("staticsType", Integer.valueOf(Val.STATISTICS_TYPE_UNUPLOAD));
                str = " and isUpload is not 1 ";
            }
            double hadInvest = 0.0d;
            Cursor cursor = getDb(context).rawQuery("select sum(take) from t_act_item where " + getWhereUserId(context) + " and actId is " + goalId + " " + str, null);
            if (cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    hadInvest = getDou(cursor, "sum(take)");
                }
            }
            close(cursor);
            String now = DateTime.getTimeString();
            int index = now.indexOf(" ");
            String todayStart = now.substring(0, index) + " 00:00:00";
            String todayEnd = now.substring(0, index) + " 23:59:59";
            double sevenInvest = 0.0d;
            String dateBefore7 = DateTime.beforeNDays2Str(-6) + " 00:00:00";
            cursor = getDb(context).rawQuery("select sum(take) from t_act_item where " + getWhereUserId(context) + " and  actId is " + goalId + " and startTime >= '" + dateBefore7 + "' and stopTime <= '" + todayEnd + "' " + str, null);
            if (cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    sevenInvest = getDou(cursor, "sum(take)");
                }
            }
            close(cursor);
            try {
                cursor = getDb(context).rawQuery("select stopTime from t_act_item where " + getWhereUserId(context) + " and  actId is " + goalId + " and startTime <= '" + dateBefore7 + "' and stopTime >= '" + dateBefore7 + "' " + str, null);
                if (cursor.getCount() > 0) {
                    cursor.moveToNext();
                    stopTime = getStr(cursor, "stopTime");
                    if (stopTime != null) {
                        sevenInvest += (double) DateTime.cal_secBetween(dateBefore7, stopTime);
                    }
                }
                close(cursor);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (sevenInvest > 604800.0d) {
                sevenInvest = 604800.0d;
            }
            double todayInvest = 0.0d;
            cursor = getDb(context).rawQuery("select sum(take) from t_act_item where " + getWhereUserId(context) + " and  actId is " + goalId + " and startTime >= '" + todayStart + "' and stopTime <= '" + todayEnd + "' " + str, null);
            if (cursor.getCount() > 0) {
                cursor.moveToNext();
                todayInvest = (double) getInt(cursor, "sum(take)");
            }
            close(cursor);
            cursor = getDb(context).rawQuery("select stopTime from t_act_item where " + getWhereUserId(context) + " and  actId is " + goalId + " and startTime < '" + todayStart + "' and stopTime > '" + todayStart + "' " + str, null);
            if (cursor.getCount() > 0) {
                cursor.moveToNext();
                stopTime = getStr(cursor, "stopTime");
                if (stopTime != null) {
                    todayInvest += (double) DateTime.cal_secBetween(todayStart, stopTime);
                }
            }
            close(cursor);
            if (todayInvest > 86400.0d) {
                todayInvest = 86400.0d;
            }
            contentValues.put("hadInvest", Double.valueOf(hadInvest));
            contentValues.put("sevenInvest", Double.valueOf(sevenInvest));
            contentValues.put("todayInvest", Double.valueOf(todayInvest));
            saveOrUpdateStatics(context, contentValues);
        }
    }

    public static void saveOrUpdateStatics(Context context, ContentValues values) {
        if (values != null) {
            Cursor cursor = getDb(context).rawQuery("select Id from t_goal_statics where goalId is " + values.getAsInteger("goalId") + " and staticsType is " + values.getAsInteger("staticsType") + " and userId is " + values.getAsInteger("userId"), null);
            if (cursor.getCount() > 0) {
                cursor.moveToNext();
                getDb(context).update("t_goal_statics", values, "id is " + getInt(cursor, "Id"), null);
            } else {
                getDb(context).insert("t_goal_statics", null, values);
            }
            close(cursor);
            return;
        }
        log("保存Statics时ContentValues为空！");
    }

    public static void saveScreenOff(Context context) {
        ContentValues values = new ContentValues();
        values.put("saveTime", DateTime.getTimeString());
        values.put(a.a, Integer.valueOf(2));
        getDb(context).insert("t_screen_log", null, values);
    }

    public static void saveScreenOn(Context context) {
        ContentValues values = new ContentValues();
        values.put("saveTime", DateTime.getTimeString());
        values.put(a.a, Integer.valueOf(1));
        getDb(context).insert("t_screen_log", null, values);
    }

    public static void deleteData(Context context) {
        getDb(context).delete("t_screen_log", "saveTime < '" + DateTime.beforeNDays2Str(-6) + "'", null);
    }

    public static void addCreateTimeIntoGoal(Context context) {
        String tempString = DateTime.getTimeString();
        Cursor cursor2 = getDb(context).rawQuery("select id,startTime from t_act_item order by startTime limit 1", null);
        if (cursor2.getCount() > 0) {
            cursor2.moveToNext();
            String temp = getStr(cursor2, "startTime");
            if (temp != null) {
                tempString = temp;
            }
        }
        close(cursor2);
        Cursor cursor = getDb(context).rawQuery("select id,startTime,createTime from t_act", null);
        if (cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                String id = getStr(cursor, "id");
                String startTime = getStr(cursor, "startTime");
                String createTime = getStr(cursor, "createTime");
                ContentValues values;
                if (startTime != null && createTime == null) {
                    values = new ContentValues();
                    values.put("createTime", startTime);
                    getDb(context).update("t_act", values, "id is " + id, null);
                } else if (startTime == null && createTime == null) {
                    values = new ContentValues();
                    values.put("createTime", tempString);
                    getDb(context).update("t_act", values, "id is " + id, null);
                }
            }
        }
        close(cursor);
    }

    public static HashMap<Integer, Integer> getAct2TypeMap(Context context) {
        HashMap<Integer, Integer> map = new HashMap();
        Cursor cur = getDb2(context).rawQuery("select id,type from t_act where " + getWhereUserId(context), null);
        if (cur.getCount() > 0) {
            while (cur.moveToNext()) {
                map.put(Integer.valueOf(cur.getInt(cur.getColumnIndex("id"))), Integer.valueOf(cur.getInt(cur.getColumnIndex(a.a))));
            }
        }
        close(cur);
        return map;
    }

    public static int queryColorByLabelId(Context context, String id) {
        int colorInt = 0;
        Cursor cursor = getDb2(context).rawQuery("select labelColor from t_sub_type where " + getWhereUserId(context) + " and id is " + id, null);
        if (cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                colorInt = cursor.getInt(cursor.getColumnIndex("labelColor"));
            }
        }
        close(cursor);
        return colorInt;
    }

    public static int queryColorByActId(Context context, String id) {
        int colorInt = context.getResources().getColor(R.color.bg_blue1);
        Cursor cursor = getDb2(context).rawQuery("select color from t_act where " + getWhereUserId(context) + " and id is " + id, null);
        if (cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                colorInt = context.getResources().getColor(((Integer) Val.col_Str2Int_Map.get(cursor.getString(cursor.getColumnIndex("color")))).intValue());
            }
        }
        close(cursor);
        return colorInt;
    }

    public static ArrayList<Integer> queryActIdByType(Context context, String type) {
        ArrayList<Integer> list = new ArrayList();
        Cursor cursor = getDb2(context).rawQuery("select id from t_act where " + getWhereUserId(context) + " and type is " + type, null);
        if (cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                int id = cursor.getInt(cursor.getColumnIndex("id"));
                if (id > 0) {
                    list.add(Integer.valueOf(id));
                }
            }
        }
        close(cursor);
        return list;
    }

    public static int queryActIdByType2(Context context, String type) {
        int id = 0;
        Cursor cursor = getDb2(context).rawQuery("select id from t_act where " + getWhereUserId(context) + " and type is " + type, null);
        if (cursor.getCount() > 0) {
            cursor.moveToNext();
            id = cursor.getInt(cursor.getColumnIndex("id"));
        }
        close(cursor);
        return id;
    }

    public static int queryActIdBysGoalId(Context context, String serverId) {
        int id = 0;
        Cursor cursor = getDb(context).rawQuery("select id from t_act where " + getWhereUserId(context) + " and severId is " + serverId, null);
        if (cursor.getCount() > 0) {
            cursor.moveToNext();
            id = cursor.getInt(cursor.getColumnIndex("id"));
        }
        close(cursor);
        return id;
    }

    public static String queryEndUpdateTimeByUserId(Context context, String userId) {
        String endUpdateTime = null;
        Cursor cursor = getDb(context).rawQuery("select endUpdateTime from t_user where id is " + userId, null);
        if (cursor.getCount() > 0) {
            cursor.moveToNext();
            endUpdateTime = cursor.getString(cursor.getColumnIndex("endUpdateTime"));
        }
        close(cursor);
        return endUpdateTime;
    }

    public static String queryEndUpdateTimeBysGoalId(Context context, String serverId) {
        String endUpdateTime = null;
        Cursor cursor = getDb(context).rawQuery("select endUpdateTime from t_act where " + getWhereUserId(context) + " and severId is " + serverId, null);
        if (cursor.getCount() > 0) {
            cursor.moveToNext();
            endUpdateTime = cursor.getString(cursor.getColumnIndex("endUpdateTime"));
        }
        close(cursor);
        return endUpdateTime;
    }

    public static String queryEndUpdateTimeBysLabelId(Context context, String sLabelId) {
        String endUpdateTime = null;
        Cursor cursor = getDb(context).rawQuery("select endUpdateTime from t_sub_type where " + getWhereUserId(context) + " and sLabelId is " + sLabelId, null);
        if (cursor.getCount() > 0) {
            cursor.moveToNext();
            endUpdateTime = cursor.getString(cursor.getColumnIndex("endUpdateTime"));
        }
        close(cursor);
        return endUpdateTime;
    }

    public static String queryEndUpdateTimeByItemsId(Context context, String itemsId) {
        String endUpdateTime = null;
        Cursor cursor = getDb(context).rawQuery("select endUpdateTime from t_act_item where " + getWhereUserId(context) + " and id is " + itemsId, null);
        if (cursor.getCount() > 0) {
            cursor.moveToNext();
            endUpdateTime = cursor.getString(cursor.getColumnIndex("endUpdateTime"));
        }
        close(cursor);
        return endUpdateTime;
    }

    public static String queryEndUpdateTimeByLabelLinksId(Context context, String labelLinkId) {
        String endUpdateTime = null;
        Cursor cursor = getDb(context).rawQuery("select endUpdateTime from t_routine_link where " + getWhereUserId(context) + " and Id is " + labelLinkId, null);
        if (cursor.getCount() > 0) {
            cursor.moveToNext();
            endUpdateTime = cursor.getString(cursor.getColumnIndex("endUpdateTime"));
        }
        close(cursor);
        return endUpdateTime;
    }

    public static String queryEndUpdateTimeByAllocationId(Context context, String date) {
        String endUpdateTime = null;
        Cursor cursor = getDb(context).rawQuery("select endUpdateTime from t_allocation where " + getWhereUserId(context) + " and time is '" + date + "'", null);
        if (cursor.getCount() > 0) {
            cursor.moveToNext();
            endUpdateTime = cursor.getString(cursor.getColumnIndex("endUpdateTime"));
        }
        close(cursor);
        return endUpdateTime;
    }

    public static Integer queryActTypeById(Context context, String id) {
        int type = 20;
        Cursor cursor = getDb2(context).rawQuery("select type from t_act where " + getWhereUserId(context) + " and id is " + id, null);
        if (cursor.getCount() > 0) {
            cursor.moveToNext();
            type = cursor.getInt(cursor.getColumnIndex(a.a));
        }
        close(cursor);
        return Integer.valueOf(type);
    }

    public static Integer queryActTypeByLabelId(Context context, String labelId) {
        int type = 0;
        Cursor cursor = getDb2(context).rawQuery("select actType from t_sub_type where " + getWhereUserId(context) + " and Id is " + labelId, null);
        if (cursor.getCount() > 0) {
            cursor.moveToNext();
            type = cursor.getInt(cursor.getColumnIndex("actType"));
        }
        close(cursor);
        return Integer.valueOf(type);
    }

    public static int queryActTypeByItemsId(Context context, int itemsId) {
        int type = 0;
        Cursor cursor = getDb2(context).rawQuery("select actType from t_act_item where " + getWhereUserId(context) + " and id is " + itemsId, null);
        if (cursor.getCount() > 0) {
            cursor.moveToNext();
            type = cursor.getInt(cursor.getColumnIndex("actType"));
        }
        close(cursor);
        return type;
    }

    public static Integer queryLabelColorByLabelId(Context context, String labelId) {
        int type = 0;
        Cursor cursor = getDb2(context).rawQuery("select labelColor from t_sub_type where " + getWhereUserId(context) + " and Id is " + labelId, null);
        if (cursor.getCount() > 0) {
            cursor.moveToNext();
            type = cursor.getInt(cursor.getColumnIndex("labelColor"));
        }
        close(cursor);
        return Integer.valueOf(type);
    }

    public static String queryLabelNameByLabelId(Context context, String labelId) {
        String name = "";
        Cursor cursor = getDb2(context).rawQuery("select name from t_sub_type where " + getWhereUserId(context) + " and Id is " + labelId, null);
        if (cursor.getCount() > 0) {
            cursor.moveToNext();
            name = cursor.getString(cursor.getColumnIndex(IDemoChart.NAME));
        }
        close(cursor);
        return name;
    }

    public static Integer queryLabelIdBysLabelId(Context context, String slabelId) {
        int Id = 0;
        Cursor cursor = getDb2(context).rawQuery("select Id from t_sub_type where " + getWhereUserId(context) + " and sLabelId is " + slabelId, null);
        if (cursor.getCount() > 0) {
            cursor.moveToNext();
            Id = cursor.getInt(cursor.getColumnIndex("Id"));
        }
        close(cursor);
        return Integer.valueOf(Id);
    }

    public static Integer queryLabelLinkIdBysLabelLinkId(Context context, String labelLinkId) {
        int Id = 0;
        Cursor cursor = getDb2(context).rawQuery("select Id from t_routine_link where " + getWhereUserId(context) + " and sLabelLinkId is " + labelLinkId, null);
        if (cursor.getCount() > 0) {
            cursor.moveToNext();
            Id = cursor.getInt(cursor.getColumnIndex("Id"));
        }
        close(cursor);
        return Integer.valueOf(Id);
    }

    public static ArrayList<Record2> queryItemsIdByDate2(Context context, String date) {
        int id;
        int actId;
        String color;
        int isUpload;
        ContentValues values;
        int cal_secBetween;
        int space = date.indexOf(" ");
        if (date.indexOf(" ") > 0) {
            date = date.substring(0, space);
        }
        String zeroTime = date + " 00:00:00";
        String endTime = date + " 23:59:59";
        String now = DateTime.getTimeString();
        ArrayList<Record2> list = new ArrayList();
        String tableName = "t_act_item";
        Cursor cursor = getDb(context).rawQuery("select * from " + tableName + " where " + getWhereUserId(context) + " and isDelete is not 1 and isEnd = 1 and startTime >= '" + zeroTime + "' and startTime <= '" + endTime + "' order by startTime", null);
        if (cursor.getCount() > 0) {
            int lastId = 0;
            int lastActId = 0;
            int lastType = 0;
            String lastStartTime = "";
            String lastStopTime = "";
            String lastRemark = "";
            int lastIsUpload = 0;
            String lastColor = "";
            while (cursor.moveToNext()) {
                int cal_secBetween2;
                id = getInt(cursor, "id");
                actId = getInt(cursor, "actId");
                int actType = getInt(cursor, "actType");
                color = queryColorByActId(context, actId);
                String begin = getStr(cursor, "startTime");
                String end = getStr(cursor, "stopTime");
                String remark = getStr(cursor, "remarks");
                isUpload = getInt(cursor, "isUpload");
                boolean isStopTimeBigerTwentyFour = DateTime.compare_date(end, endTime) > 0;
                if (!cursor.isFirst()) {
                    try {
                        if (begin.equals(lastStartTime)) {
                            if (remark == null && lastRemark == null) {
                                if (lastType != 11 || actType == 11) {
                                    deleteItems(context, tableName, lastId, lastIsUpload, now);
                                    deleteLabelLinkByItemsId(context, lastId + "");
                                    list.remove(list.size() - 1);
                                } else {
                                    deleteItems(context, tableName, id, isUpload, now);
                                    deleteLabelLinkByItemsId(context, id + "");
                                }
                            } else if (remark == null || lastRemark == null) {
                                if (remark == null && lastRemark != null) {
                                    deleteItems(context, tableName, id, isUpload, now);
                                    deleteLabelLinkByItemsId(context, id + "");
                                } else if (remark != null && lastRemark == null) {
                                    deleteItems(context, tableName, lastId, lastIsUpload, now);
                                    deleteLabelLinkByItemsId(context, lastId + "");
                                    list.remove(list.size() - 1);
                                }
                            } else if (lastType != 11 || actType == 11) {
                                deleteItems(context, tableName, lastId, lastIsUpload, now);
                                deleteLabelLinkByItemsId(context, lastId + "");
                                list.remove(list.size() - 1);
                            } else {
                                deleteItems(context, tableName, id, isUpload, now);
                                deleteLabelLinkByItemsId(context, id + "");
                            }
                        } else if (DateTime.compare_date(begin, lastStartTime) > 0 && DateTime.compare_date(lastStopTime, begin) > 0) {
                            int take = DateTime.cal_secBetween(lastStartTime, begin);
                            values = new ContentValues();
                            values.put("take", Integer.valueOf(DateTime.cal_secBetween(lastStartTime, begin)));
                            values.put("stopTime", begin);
                            if (isUpload > 0) {
                                values.put("endUpdateTime", now);
                            }
                            getDb(context).update(tableName, values, " id = " + lastId, null);
                            updateLabel(context, lastId, take);
                            list.remove(list.size() - 1);
                            cal_secBetween2 = DateTime.cal_secBetween(zeroTime, lastStartTime);
                            cal_secBetween = DateTime.cal_secBetween(zeroTime, begin);
                            list.add(new Record2(begin, end, lastActId, context.getResources().getColor(((Integer) Val.col_Str2Int_Map.get(lastColor)).intValue()), lastId));
                        }
                    } catch (Exception e) {
                        exceptionHandler(context, e);
                    }
                }
                cal_secBetween2 = DateTime.cal_secBetween(zeroTime, begin);
                cal_secBetween = DateTime.cal_secBetween(zeroTime, end);
                if (isStopTimeBigerTwentyFour) {
                }
                list.add(new Record2(begin, end, actId, context.getResources().getColor(((Integer) Val.col_Str2Int_Map.get(color)).intValue()), id));
                lastId = id;
                lastActId = actId;
                lastStartTime = begin;
                lastStopTime = end;
                lastRemark = remark;
                lastIsUpload = isUpload;
                lastType = actType;
                lastColor = color;
            }
        }
        close(cursor);
        Cursor cursor2 = getDb(context).rawQuery("select * from t_act_item where " + getWhereUserId(context) + " and isDelete is not 1 and isEnd = 1 and startTime < '" + zeroTime + "' and stopTime > '" + zeroTime + "' and stopTime <= '" + endTime + "' order by startTime", null);
        if (cursor2.getCount() > 0) {
            while (cursor2.moveToNext()) {
                id = getInt(cursor2, "id");
                actId = getInt(cursor2, "actId");
                color = queryColorByActId(context, actId);
                String start = getStr(cursor2, "startTime");
                String stop = getStr(cursor2, "stopTime");
                cal_secBetween = DateTime.cal_secBetween(zeroTime, stop);
                isUpload = getInt(cursor2, "isUpload");
                if (!cursor2.isFirst()) {
                    deleteItems(context, tableName, id, isUpload, now);
                    deleteLabelLinkByItemsId(context, id + "");
                } else if (list == null || list.size() <= 0 || cal_secBetween <= DateTime.cal_secBetween(zeroTime, ((Record2) list.get(0)).getBegin())) {
                    list.add(new Record2(start, stop, actId, context.getResources().getColor(((Integer) Val.col_Str2Int_Map.get(color)).intValue()), id));
                } else {
                    String stopTime = ((Record2) list.get(0)).getBegin();
                    values = new ContentValues();
                    values.put("take", Integer.valueOf(DateTime.cal_secBetween(start, stopTime)));
                    values.put("stopTime", stopTime);
                    if (isUpload > 0) {
                        values.put("endUpdateTime", now);
                    }
                    getDb(context).update(tableName, values, " id = " + id, null);
                    list.add(new Record2(start, ((Record2) list.get(0)).getBegin(), actId, context.getResources().getColor(((Integer) Val.col_Str2Int_Map.get(color)).intValue()), id));
                }
            }
        }
        close(cursor2);
        log("修复今天交叉记录2：" + list.toString());
        log("修复今天交叉记录3：" + list.toString());
        return list;
    }

    public static ArrayList<Record> queryItemsIdByDate(Context context, String date) {
        int id;
        int actId;
        String color;
        int isUpload;
        ContentValues values;
        int stopInt;
        int space = date.indexOf(" ");
        if (date.indexOf(" ") > 0) {
            date = date.substring(0, space);
        }
        String zeroTime = date + " 00:00:00";
        String endTime = date + " 23:59:59";
        String now = DateTime.getTimeString();
        ArrayList<Record> list = new ArrayList();
        String tableName = "t_act_item";
        Cursor cursor = getDb(context).rawQuery("select * from " + tableName + " where " + getWhereUserId(context) + " and isDelete is not 1 and isEnd = 1 and startTime >= '" + zeroTime + "' and startTime <= '" + endTime + "' order by startTime", null);
        if (cursor.getCount() > 0) {
            int lastId = 0;
            int lastActId = 0;
            int lastType = 0;
            String lastStartTime = "";
            String lastStopTime = "";
            String lastRemark = "";
            int lastIsUpload = 0;
            String lastColor = "";
            while (cursor.moveToNext()) {
                id = getInt(cursor, "id");
                actId = getInt(cursor, "actId");
                int actType = getInt(cursor, "actType");
                color = queryColorByActId(context, actId);
                String begin = getStr(cursor, "startTime");
                String end = getStr(cursor, "stopTime");
                String remark = getStr(cursor, "remarks");
                isUpload = getInt(cursor, "isUpload");
                boolean isStopTimeBigerTwentyFour = DateTime.compare_date(end, endTime) > 0;
                if (!cursor.isFirst()) {
                    try {
                        if (begin.equals(lastStartTime)) {
                            if (remark == null && lastRemark == null) {
                                if (lastType != 11 || actType == 11) {
                                    deleteItems(context, tableName, lastId, lastIsUpload, now);
                                    deleteLabelLinkByItemsId(context, lastId + "");
                                    list.remove(list.size() - 1);
                                } else {
                                    deleteItems(context, tableName, id, isUpload, now);
                                    deleteLabelLinkByItemsId(context, id + "");
                                }
                            } else if (remark == null || lastRemark == null) {
                                if (remark == null && lastRemark != null) {
                                    deleteItems(context, tableName, id, isUpload, now);
                                    deleteLabelLinkByItemsId(context, id + "");
                                } else if (remark != null && lastRemark == null) {
                                    deleteItems(context, tableName, lastId, lastIsUpload, now);
                                    deleteLabelLinkByItemsId(context, lastId + "");
                                    list.remove(list.size() - 1);
                                }
                            } else if (lastType != 11 || actType == 11) {
                                deleteItems(context, tableName, lastId, lastIsUpload, now);
                                deleteLabelLinkByItemsId(context, lastId + "");
                                list.remove(list.size() - 1);
                            } else {
                                deleteItems(context, tableName, id, isUpload, now);
                                deleteLabelLinkByItemsId(context, id + "");
                            }
                        } else if (DateTime.compare_date(begin, lastStartTime) > 0 && DateTime.compare_date(lastStopTime, begin) > 0) {
                            int take = DateTime.cal_secBetween(lastStartTime, begin);
                            values = new ContentValues();
                            values.put("take", Integer.valueOf(DateTime.cal_secBetween(lastStartTime, begin)));
                            values.put("stopTime", begin);
                            if (isUpload > 0) {
                                values.put("endUpdateTime", now);
                            }
                            getDb(context).update(tableName, values, " id = " + lastId, null);
                            updateLabel(context, lastId, take);
                            list.remove(list.size() - 1);
                            list.add(new Record(DateTime.cal_secBetween(zeroTime, lastStartTime), DateTime.cal_secBetween(zeroTime, begin), lastActId, ((Integer) Val.col_Str2Int_Map.get(lastColor)).intValue(), lastId));
                        }
                    } catch (Exception e) {
                        exceptionHandler(context, e);
                    }
                }
                int stratInt = DateTime.cal_secBetween(zeroTime, begin);
                stopInt = DateTime.cal_secBetween(zeroTime, end);
                if (isStopTimeBigerTwentyFour) {
                    stopInt = 86400;
                }
                list.add(new Record(stratInt, stopInt, actId, ((Integer) Val.col_Str2Int_Map.get(color)).intValue(), id));
                lastId = id;
                lastActId = actId;
                lastStartTime = begin;
                lastStopTime = end;
                lastRemark = remark;
                lastIsUpload = isUpload;
                lastType = actType;
                lastColor = color;
            }
        }
        close(cursor);
        Cursor cursor2 = getDb(context).rawQuery("select * from t_act_item where " + getWhereUserId(context) + " and isDelete is not 1 and isEnd = 1 and startTime < '" + zeroTime + "' and stopTime > '" + zeroTime + "' and stopTime <= '" + endTime + "' order by startTime", null);
        if (cursor2.getCount() > 0) {
            while (cursor2.moveToNext()) {
                id = getInt(cursor2, "id");
                actId = getInt(cursor2, "actId");
                color = queryColorByActId(context, actId);
                String start = getStr(cursor2, "startTime");
                stopInt = DateTime.cal_secBetween(zeroTime, getStr(cursor2, "stopTime"));
                isUpload = getInt(cursor2, "isUpload");
                if (!cursor2.isFirst()) {
                    deleteItems(context, tableName, id, isUpload, now);
                    deleteLabelLinkByItemsId(context, id + "");
                } else if (list == null || list.size() <= 0 || stopInt <= ((Record) list.get(0)).getBegin()) {
                    list.add(new Record(0, stopInt, actId, ((Integer) Val.col_Str2Int_Map.get(color)).intValue(), id));
                } else {
                    String stopTime = date + " " + DateTime.calculateTime2((long) ((Record) list.get(0)).getBegin());
                    values = new ContentValues();
                    values.put("take", Integer.valueOf(DateTime.cal_secBetween(start, stopTime)));
                    values.put("stopTime", stopTime);
                    if (isUpload > 0) {
                        values.put("endUpdateTime", now);
                    }
                    getDb(context).update(tableName, values, " id = " + id, null);
                    list.add(new Record(0, ((Record) list.get(0)).getBegin(), actId, ((Integer) Val.col_Str2Int_Map.get(color)).intValue(), id));
                }
            }
        }
        close(cursor2);
        log("修复今天交叉记录2：" + list.toString());
        log("修复今天交叉记录3：" + list.toString());
        return list;
    }

    public static void deleteItems(Context context, String tableName, int id, int isUpload, String now) {
        if (isUpload == 0) {
            getDb(context).delete(tableName, " id = " + id, null);
        } else {
            getDb(context).update(tableName, getDeleteValues(), " id = " + id, null);
        }
    }

    public static ContentValues getDeleteValues(String time) {
        ContentValues values = new ContentValues();
        values.put("isDelete", Integer.valueOf(1));
        values.put("deleteTime", time);
        values.put("endUpdateTime", time);
        return values;
    }

    public static ContentValues getDeleteValues() {
        ContentValues values = new ContentValues();
        values.put("isDelete", Integer.valueOf(1));
        values.put("deleteTime", DateTime.getTimeString());
        values.put("endUpdateTime", DateTime.getTimeString());
        return values;
    }

    public static void updateLabel(Context context, int itemId, int take) {
        ContentValues values = new ContentValues();
        values.put("take", Integer.valueOf(take));
        values.put("endUpdateTime", DateTime.getTimeString());
        getDb(context).update("t_routine_link", values, " itemsId =  " + itemId, null);
    }

    public static String queryStartTimebyItemsId(Context context, String itemId) {
        String startTime = "";
        Cursor cursor = getDb2(context).rawQuery("select startTime from t_act_item where " + getWhereUserId(context) + " and id is " + itemId, null);
        if (cursor.getCount() > 0) {
            cursor.moveToNext();
            startTime = cursor.getString(cursor.getColumnIndex("startTime"));
        }
        close(cursor);
        return startTime;
    }

    public static String queryStopTimebyItemsId(Context context, String itemId) {
        String startTime = "";
        Cursor cursor = getDb2(context).rawQuery("select stopTime from t_act_item where " + getWhereUserId(context) + " and id is " + itemId, null);
        if (cursor.getCount() > 0) {
            cursor.moveToNext();
            startTime = cursor.getString(cursor.getColumnIndex("stopTime"));
        }
        close(cursor);
        return startTime;
    }

    public static Integer queryItemsIdBysItemsId(Context context, String sGoalItemId) {
        int id = 0;
        Cursor cursor = getDb2(context).rawQuery("select id from t_act_item where " + getWhereUserId(context) + " and sGoalItemId is " + sGoalItemId, null);
        if (cursor.getCount() > 0) {
            cursor.moveToNext();
            id = cursor.getInt(cursor.getColumnIndex("id"));
        }
        close(cursor);
        return Integer.valueOf(id);
    }

    public static String queryRemarkByItemsId(Context context, String itemId) {
        String remark = null;
        Cursor cursor = getDb2(context).rawQuery("select remarks from t_act_item where " + getWhereUserId(context) + " and id is " + itemId, null);
        if (cursor.getCount() > 0) {
            cursor.moveToNext();
            remark = cursor.getString(cursor.getColumnIndex("remarks"));
        }
        close(cursor);
        return remark;
    }

    public static int queryIsUploadByActItemId(Context context, String actItemId) {
        int isUpload = 0;
        Cursor cursor = getDb2(context).rawQuery("select isUpload from t_act_item where " + getWhereUserId(context) + " and id is " + actItemId, null);
        if (cursor.getCount() > 0) {
            cursor.moveToNext();
            isUpload = cursor.getInt(cursor.getColumnIndex("isUpload"));
        }
        close(cursor);
        return isUpload;
    }

    public static int queryIsUploadBySubTypeId(Context context, String labelId) {
        int isUpload = 0;
        Cursor cursor = getDb2(context).rawQuery("select isUpload from t_act_item where " + getWhereUserId(context) + " and Id is " + labelId, null);
        if (cursor.getCount() > 0) {
            cursor.moveToNext();
            isUpload = cursor.getInt(cursor.getColumnIndex("isUpload"));
        }
        close(cursor);
        return isUpload;
    }

    public static int deleteLabelLinkByItemsId(Context context, String itemsId) {
        int isUpload = 0;
        Cursor cursor = getDb2(context).rawQuery("select Id,isUpload from t_routine_link where " + getWhereUserId(context) + " and itemsId is " + itemsId, null);
        if (cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                int id = cursor.getInt(cursor.getColumnIndex("Id"));
                isUpload = cursor.getInt(cursor.getColumnIndex("isUpload"));
                if (isUpload > 0) {
                    ContentValues values = new ContentValues();
                    values.put("isDelete", Integer.valueOf(1));
                    values.put("deleteTime", DateTime.getTimeString());
                    values.put("endUpdateTime", DateTime.getTimeString());
                    getDb(context).update("t_routine_link", values, "Id = " + id, null);
                    log("更新标签：isDelete" + values);
                } else {
                    getDb(context).delete("t_routine_link", "Id = " + id, null);
                    log("删除标签");
                }
            }
        }
        close(cursor);
        return isUpload;
    }

    public static String queryActNameById(Context context, String id) {
        String actName = "";
        Cursor cursor = getDb2(context).rawQuery("select actName from t_act where " + getWhereUserId(context) + " and id is " + id, null);
        if (cursor.getCount() > 0) {
            cursor.moveToNext();
            actName = cursor.getString(cursor.getColumnIndex("actName"));
        }
        close(cursor);
        return actName;
    }

    public static Act2 getAct2ByActId(Context context, String actId) {
        Act2 act = null;
        Cursor cursor = getDb2(context).rawQuery("select * from t_act where userId is " + queryUserId(context) + " and id is " + actId, null);
        if (cursor.getCount() > 0) {
            cursor.moveToNext();
            act = new Act2(cursor.getInt(cursor.getColumnIndex("id")), cursor.getString(cursor.getColumnIndex("image")), cursor.getString(cursor.getColumnIndex("color")), cursor.getString(cursor.getColumnIndex("actName")), cursor.getString(cursor.getColumnIndex("intruction")), cursor.getString(cursor.getColumnIndex("startTime")), cursor.getString(cursor.getColumnIndex("deadline")), cursor.getString(cursor.getColumnIndex("level")), cursor.getInt(cursor.getColumnIndex("timeOfEveryday")), cursor.getInt(cursor.getColumnIndex("expectSpend")), cursor.getInt(cursor.getColumnIndex("hadSpend")), cursor.getInt(cursor.getColumnIndex("position")), cursor.getInt(cursor.getColumnIndex(a.a)));
        }
        close(cursor);
        return act;
    }

    public static boolean queryIsGoalCounting(Context context, int type, int type2) {
        Cursor cursor = getDb2(context).rawQuery("select actId from t_act_item where " + getWhereUserId(context) + " and isEnd is not 1 order by startTime desc", null);
        if (cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                Cursor cursor2 = getDb2(context).rawQuery("select type from t_act where id is " + cursor.getInt(cursor.getColumnIndex("actId")), null);
                if (cursor2.getCount() > 0) {
                    cursor2.moveToNext();
                    int dbType = cursor2.getInt(cursor2.getColumnIndex(a.a));
                    if (dbType == type || dbType == type2) {
                        close(cursor);
                        close(cursor2);
                        return true;
                    }
                }
            }
        }
        close(cursor);
        return false;
    }

    public static int queryActHadSpend(Context context, String Date, String actId) {
        int hadSpeed = 0;
        if (Date != null) {
            if (Date.indexOf(" ") > 0) {
                Date = Date.substring(0, Date.indexOf(" "));
            }
            Cursor cursor = getDb2(context).rawQuery("Select * from t_act_item where userId is " + queryUserId(context) + " and actId is " + actId + " and startTime >= '" + Date + " 00:00:00' and startTime <= '" + Date + " 23:59:59' and isDelete is not 1 order by startTime", null);
            if (cursor.getCount() > 0) {
                String zeroTime = Date + " 00:00:00";
                String tentyTime = Date + " 23:59:59";
                while (cursor.moveToNext()) {
                    int take = cursor.getInt(cursor.getColumnIndex("take"));
                    String startTime = cursor.getString(cursor.getColumnIndex("startTime"));
                    String stopTime = cursor.getString(cursor.getColumnIndex("stopTime"));
                    if (!stopTime.substring(0, stopTime.indexOf(" ")).equals(startTime.substring(0, startTime.indexOf(" ")))) {
                        take = DateTime.cal_secBetween(startTime, tentyTime);
                    }
                    if (take > 0) {
                        hadSpeed += take;
                    }
                    if (cursor.isFirst()) {
                        int spendtemp = queryDb_stopTime2(context, actId, zeroTime, startTime);
                        if (spendtemp > 0) {
                            hadSpeed += spendtemp;
                        }
                    }
                }
            }
            close(cursor);
        }
        return hadSpeed;
    }

    public static int queryActHadSpend(Context context, String stringTime, String encTime, String actId) {
        int hadSpeed = 0;
        if (stringTime != null) {
            Cursor cursor = getDb2(context).rawQuery("Select * from t_act_item where userId is " + queryUserId(context) + " and actId is " + actId + " and startTime >= '" + stringTime + "' and startTime <= '" + encTime + "' and isDelete is not 1 order by startTime", null);
            if (cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    int take = cursor.getInt(cursor.getColumnIndex("take"));
                    String startTime = cursor.getString(cursor.getColumnIndex("startTime"));
                    String stopTime = cursor.getString(cursor.getColumnIndex("stopTime"));
                    String start_str = startTime.substring(0, startTime.indexOf(" "));
                    String stop_str = stopTime.substring(0, stopTime.indexOf(" "));
                    if (cursor.isLast() && !stop_str.equals(start_str)) {
                        take = DateTime.cal_secBetween(startTime, start_str + " 23:59:59");
                    }
                    if (take > 0) {
                        hadSpeed += take;
                    }
                    if (cursor.isFirst()) {
                        int spendtemp = queryDb_stopTime2(context, actId, start_str + " 00:00:00", startTime);
                        if (spendtemp > 0) {
                            hadSpeed += spendtemp;
                        }
                    }
                }
            }
            close(cursor);
        }
        return hadSpeed;
    }

    private static int queryDb_stopTime2(Context context, String actId, String zeroTime, String endTime) {
        int counter = 0;
        Cursor cursor2 = getDb(context).rawQuery("select * from t_act_item where " + getWhereUserId(context) + " and actId is " + actId + " and stopTime > '" + zeroTime + "' and stopTime <= '" + endTime + "'", null);
        if (cursor2.getCount() > 0) {
            cursor2.moveToNext();
            String stopTime2 = cursor2.getString(cursor2.getColumnIndex("stopTime"));
            String startTime2 = cursor2.getString(cursor2.getColumnIndex("startTime"));
            if (!startTime2.substring(0, startTime2.indexOf(" ")).equals(stopTime2.substring(0, stopTime2.indexOf(" ")))) {
                counter = DateTime.cal_secBetween(zeroTime, stopTime2);
            }
        }
        close(cursor2);
        return counter;
    }

    public static int queryAllocation(Context context, String Date) {
        int count = 0;
        try {
            Cursor cur = getDb2(context).rawQuery("select * from t_allocation where " + getWhereUserId(context) + " and time is '" + Date + "'", null);
            if (cur.getCount() > 0) {
                cur.moveToNext();
                count = (cur.getInt(cur.getColumnIndex("invest")) + cur.getInt(cur.getColumnIndex("routine"))) + cur.getInt(cur.getColumnIndex("sleep"));
            }
            close(cur);
        } catch (Exception e) {
            exceptionHandler(e);
        }
        return count;
    }

    public static int queryAllocationInvest(Context context, String Date) {
        int invest = 0;
        try {
            Cursor cur = getDb2(context).rawQuery("select * from t_allocation where " + getWhereUserId(context) + " and time is '" + Date + "'", null);
            if (cur.getCount() > 0) {
                cur.moveToNext();
                invest = cur.getInt(cur.getColumnIndex("invest"));
            }
            close(cur);
        } catch (Exception e) {
            exceptionHandler(e);
        }
        return invest;
    }

    public static String[] queryTodayAllocation(Context context) {
        String[] strArr = new String[5];
        String investStr = "";
        String wasteStr = "";
        Cursor cur = getDb(context).rawQuery("Select * from t_allocation where userId is " + User.getInstance().getUserId() + " and time is '" + DateTime.getDateString() + "'", null);
        if (cur.getCount() > 0) {
            cur.moveToNext();
            int invest = cur.getInt(cur.getColumnIndex("invest"));
            int waste = cur.getInt(cur.getColumnIndex("waste"));
            if (waste < 0) {
                waste = 0;
            }
            if (invest < 0) {
                invest = 0;
            }
            int lar = Math.max(waste, invest);
            int max = 3600;
            if (lar < 3600) {
                max = invest + waste;
                investStr = (invest / 60) + "min " + FormatUtils.format_1fra((((double) invest) / ((double) max)) * 100.0d) + "%";
                wasteStr = (waste / 60) + "min " + FormatUtils.format_1fra((((double) waste) / ((double) max)) * 100.0d) + "%";
            } else {
                if (((lar < 72000 ? 1 : 0) & (lar >= 3600 ? 1 : 0)) != 0) {
                    max = invest + waste;
                    investStr = FormatUtils.format_1fra(((double) invest) / 3600.0d) + "h " + FormatUtils.format_1fra((((double) invest) / ((double) max)) * 100.0d) + "%";
                    wasteStr = FormatUtils.format_1fra(((double) waste) / 3600.0d) + "h " + FormatUtils.format_1fra((((double) waste) / ((double) max)) * 100.0d) + "%";
                } else if (lar >= 72000) {
                    if (waste > 86400) {
                        waste = 86400;
                    }
                    if (invest > 86400) {
                        invest = 86400;
                    }
                    max = invest + waste;
                    investStr = FormatUtils.format_1fra(((double) invest) / 3600.0d) + "h " + FormatUtils.format_1fra((((double) invest) / ((double) max)) * 100.0d) + "%";
                    wasteStr = FormatUtils.format_1fra(((double) waste) / 3600.0d) + "h " + FormatUtils.format_1fra((((double) waste) / ((double) max)) * 100.0d) + "%";
                }
            }
            strArr[2] = invest + "";
            strArr[3] = waste + "";
            strArr[4] = max + "";
        } else {
            investStr = "0min 0%";
            wasteStr = "0min 0%";
        }
        strArr[0] = investStr;
        strArr[1] = wasteStr;
        close(cur);
        return strArr;
    }

    public static TreeMap<String, Integer> getTypeXseries2(Context context, int type, int dayBefore) {
        TreeMap<String, Integer> treeMap = new TreeMap();
        if (type == 0) {
            return null;
        }
        try {
            String date = DateTime.beforeNDays2Str(-dayBefore);
            int i = 0;
            while (i <= dayBefore) {
                treeMap.put(DateTime.beforeNDays2Str(i == 0 ? 0 : -i), Integer.valueOf(0));
                i++;
            }
            String column = "";
            if (type == 10) {
                column = "invest";
            } else if (type == 20) {
                column = "routine";
            } else if (type == 30) {
                column = "sleep";
            } else if (type == 40) {
                column = "waste";
            }
            Cursor cursor = getDb2(context).rawQuery("Select " + column + ",time from t_allocation where userId is " + queryUserId(context) + " and time >= '" + date + "' and time < '" + DateTime.getDateString() + " 23:59:59' order by time", null);
            if (cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    treeMap.put(cursor.getString(cursor.getColumnIndex("time")), Integer.valueOf(cursor.getInt(cursor.getColumnIndex(column))));
                }
            }
            close(cursor);
            return treeMap;
        } catch (Exception e) {
            exceptionHandler(e);
            return treeMap;
        }
    }

    public static ArrayList<XYColumn> getTypeXseries(Context context, int type, int dayBefore) {
        Exception e;
        ArrayList<XYColumn> arr = null;
        if (type == 0) {
            return null;
        }
        try {
            ArrayList<XYColumn> arr2 = new ArrayList();
            try {
                String date = DateTime.beforeNDays2Str(-dayBefore);
                TreeMap<String, Integer> map = new TreeMap();
                int i = 0;
                while (i <= dayBefore) {
                    map.put(DateTime.beforeNDays2Str(i == 0 ? 0 : -i), Integer.valueOf((dayBefore - i) + 1));
                    i++;
                }
                String column = "";
                if (type == 10) {
                    column = "invest";
                } else if (type == 20) {
                    column = "routine";
                } else if (type == 30) {
                    column = "sleep";
                } else if (type == 40) {
                    column = "waste";
                }
                Cursor cursor = getDb2(context).rawQuery("Select " + column + ",time from t_allocation where userId is " + queryUserId(context) + " and time >= '" + date + "' and time < '" + DateTime.getDateString() + " 23:59:59' order by time", null);
                if (cursor.getCount() > 0) {
                    while (cursor.moveToNext()) {
                        int columnCounter = cursor.getInt(cursor.getColumnIndex(column));
                        String time = cursor.getString(cursor.getColumnIndex("time"));
                        arr2.add(new XYColumn(((Integer) map.get(time)).intValue(), columnCounter, DateTime.getDay3(context, time + " 00:00:00") + "\n" + time.substring(time.lastIndexOf("-") + 1, time.length())));
                        map.remove(time);
                    }
                }
                close(cursor);
                arr = arr2;
            } catch (Exception e2) {
                e = e2;
                arr = arr2;
                exceptionHandler(e);
                return arr;
            }
        } catch (Exception e3) {
            e = e3;
            exceptionHandler(e);
            return arr;
        }
        return arr;
    }

    /* JADX WARNING: Missing block: B:6:0x000c, code:
            if (r38.length() == 0) goto L_0x000e;
     */
    public static java.util.ArrayList<com.record.bean.XYColumn> getXseries2(android.content.Context r37, java.lang.String r38, java.lang.String r39, boolean r40) {
        /*
        r4 = 0;
        r5 = new java.util.ArrayList;	 Catch:{ Exception -> 0x03a3 }
        r5.<init>();	 Catch:{ Exception -> 0x03a3 }
        if (r38 == 0) goto L_0x000e;
    L_0x0008:
        r33 = r38.length();	 Catch:{ Exception -> 0x0218 }
        if (r33 != 0) goto L_0x001e;
    L_0x000e:
        r33 = queryUserId(r37);	 Catch:{ Exception -> 0x0218 }
        r34 = "30";
        r0 = r37;
        r1 = r33;
        r2 = r34;
        r38 = queryActId(r0, r1, r2);	 Catch:{ Exception -> 0x0218 }
    L_0x001e:
        r18 = 0;
        r33 = 1;
        r0 = r40;
        r1 = r33;
        if (r0 != r1) goto L_0x0072;
    L_0x0028:
        r33 = 15;
        r0 = r39;
        r1 = r33;
        r18 = com.record.utils.DateTime.beforeNDaysArr(r0, r1);	 Catch:{ Exception -> 0x0218 }
    L_0x0032:
        r33 = 0;
        r9 = r18[r33];	 Catch:{ Exception -> 0x0218 }
        r9 = (java.util.Set) r9;	 Catch:{ Exception -> 0x0218 }
        r33 = 1;
        r8 = r18[r33];	 Catch:{ Exception -> 0x0218 }
        r8 = (java.util.Map) r8;	 Catch:{ Exception -> 0x0218 }
        r33 = r9.iterator();	 Catch:{ Exception -> 0x0218 }
        r19 = r33.next();	 Catch:{ Exception -> 0x0218 }
        r19 = (java.lang.String) r19;	 Catch:{ Exception -> 0x0218 }
        r33 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0218 }
        r33.<init>();	 Catch:{ Exception -> 0x0218 }
        r0 = r33;
        r1 = r19;
        r33 = r0.append(r1);	 Catch:{ Exception -> 0x0218 }
        r34 = " 00:00:00";
        r33 = r33.append(r34);	 Catch:{ Exception -> 0x0218 }
        r21 = r33.toString();	 Catch:{ Exception -> 0x0218 }
        r15 = r9.iterator();	 Catch:{ Exception -> 0x0218 }
        r11 = "";
    L_0x0065:
        r33 = r15.hasNext();	 Catch:{ Exception -> 0x0218 }
        if (r33 == 0) goto L_0x007d;
    L_0x006b:
        r11 = r15.next();	 Catch:{ Exception -> 0x0218 }
        r11 = (java.lang.String) r11;	 Catch:{ Exception -> 0x0218 }
        goto L_0x0065;
    L_0x0072:
        r33 = 15;
        r0 = r39;
        r1 = r33;
        r18 = com.record.utils.DateTime.afterNDaysArr(r0, r1);	 Catch:{ Exception -> 0x0218 }
        goto L_0x0032;
    L_0x007d:
        r33 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0218 }
        r33.<init>();	 Catch:{ Exception -> 0x0218 }
        r0 = r33;
        r33 = r0.append(r11);	 Catch:{ Exception -> 0x0218 }
        r34 = " 23:59:59";
        r33 = r33.append(r34);	 Catch:{ Exception -> 0x0218 }
        r11 = r33.toString();	 Catch:{ Exception -> 0x0218 }
        r33 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0218 }
        r33.<init>();	 Catch:{ Exception -> 0x0218 }
        r34 = "actId:";
        r33 = r33.append(r34);	 Catch:{ Exception -> 0x0218 }
        r0 = r33;
        r1 = r38;
        r33 = r0.append(r1);	 Catch:{ Exception -> 0x0218 }
        r34 = "startDate";
        r33 = r33.append(r34);	 Catch:{ Exception -> 0x0218 }
        r0 = r33;
        r1 = r21;
        r33 = r0.append(r1);	 Catch:{ Exception -> 0x0218 }
        r34 = ",endDate";
        r33 = r33.append(r34);	 Catch:{ Exception -> 0x0218 }
        r0 = r33;
        r33 = r0.append(r11);	 Catch:{ Exception -> 0x0218 }
        r33 = r33.toString();	 Catch:{ Exception -> 0x0218 }
        log(r33);	 Catch:{ Exception -> 0x0218 }
        r17 = new java.util.HashMap;	 Catch:{ Exception -> 0x0218 }
        r17.<init>();	 Catch:{ Exception -> 0x0218 }
        r16 = 1;
        r33 = r9.iterator();	 Catch:{ Exception -> 0x0218 }
    L_0x00d1:
        r34 = r33.hasNext();	 Catch:{ Exception -> 0x0218 }
        if (r34 == 0) goto L_0x00ed;
    L_0x00d7:
        r25 = r33.next();	 Catch:{ Exception -> 0x0218 }
        r25 = (java.lang.String) r25;	 Catch:{ Exception -> 0x0218 }
        r34 = java.lang.Integer.valueOf(r16);	 Catch:{ Exception -> 0x0218 }
        r0 = r17;
        r1 = r25;
        r2 = r34;
        r0.put(r1, r2);	 Catch:{ Exception -> 0x0218 }
        r16 = r16 + 1;
        goto L_0x00d1;
    L_0x00ed:
        r33 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0218 }
        r33.<init>();	 Catch:{ Exception -> 0x0218 }
        r34 = "map:";
        r33 = r33.append(r34);	 Catch:{ Exception -> 0x0218 }
        r34 = r17.toString();	 Catch:{ Exception -> 0x0218 }
        r33 = r33.append(r34);	 Catch:{ Exception -> 0x0218 }
        r33 = r33.toString();	 Catch:{ Exception -> 0x0218 }
        log(r33);	 Catch:{ Exception -> 0x0218 }
        r14 = new java.util.ArrayList;	 Catch:{ Exception -> 0x0218 }
        r14.<init>();	 Catch:{ Exception -> 0x0218 }
        r33 = getDb2(r37);	 Catch:{ Exception -> 0x0218 }
        r34 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0218 }
        r34.<init>();	 Catch:{ Exception -> 0x0218 }
        r35 = "select * from t_act_item where ";
        r34 = r34.append(r35);	 Catch:{ Exception -> 0x0218 }
        r35 = getWhereUserId(r37);	 Catch:{ Exception -> 0x0218 }
        r34 = r34.append(r35);	 Catch:{ Exception -> 0x0218 }
        r35 = " and actId is ";
        r34 = r34.append(r35);	 Catch:{ Exception -> 0x0218 }
        r0 = r34;
        r1 = r38;
        r34 = r0.append(r1);	 Catch:{ Exception -> 0x0218 }
        r35 = " and stopTime >= '";
        r34 = r34.append(r35);	 Catch:{ Exception -> 0x0218 }
        r0 = r34;
        r1 = r21;
        r34 = r0.append(r1);	 Catch:{ Exception -> 0x0218 }
        r35 = "' and stopTime <= '";
        r34 = r34.append(r35);	 Catch:{ Exception -> 0x0218 }
        r0 = r34;
        r34 = r0.append(r11);	 Catch:{ Exception -> 0x0218 }
        r35 = "' order by startTime";
        r34 = r34.append(r35);	 Catch:{ Exception -> 0x0218 }
        r34 = r34.toString();	 Catch:{ Exception -> 0x0218 }
        r35 = 0;
        r7 = r33.rawQuery(r34, r35);	 Catch:{ Exception -> 0x0218 }
        r33 = r7.getCount();	 Catch:{ Exception -> 0x0218 }
        if (r33 <= 0) goto L_0x035d;
    L_0x0161:
        r26 = r19;
    L_0x0163:
        r33 = r7.moveToNext();	 Catch:{ Exception -> 0x0218 }
        if (r33 == 0) goto L_0x035d;
    L_0x0169:
        r33 = "startTime";
        r0 = r33;
        r33 = r7.getColumnIndex(r0);	 Catch:{ Exception -> 0x0218 }
        r0 = r33;
        r20 = r7.getString(r0);	 Catch:{ Exception -> 0x0218 }
        r33 = "stopTime";
        r0 = r33;
        r33 = r7.getColumnIndex(r0);	 Catch:{ Exception -> 0x0218 }
        r0 = r33;
        r23 = r7.getString(r0);	 Catch:{ Exception -> 0x0218 }
        r33 = 0;
        r34 = " ";
        r0 = r20;
        r1 = r34;
        r34 = r0.indexOf(r1);	 Catch:{ Exception -> 0x0218 }
        r0 = r20;
        r1 = r33;
        r2 = r34;
        r22 = r0.substring(r1, r2);	 Catch:{ Exception -> 0x0218 }
        r33 = 0;
        r34 = " ";
        r0 = r23;
        r1 = r34;
        r34 = r0.indexOf(r1);	 Catch:{ Exception -> 0x0218 }
        r0 = r23;
        r1 = r33;
        r2 = r34;
        r24 = r0.substring(r1, r2);	 Catch:{ Exception -> 0x0218 }
        r33 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0218 }
        r33.<init>();	 Catch:{ Exception -> 0x0218 }
        r0 = r33;
        r1 = r22;
        r33 = r0.append(r1);	 Catch:{ Exception -> 0x0218 }
        r34 = " 00:00:00";
        r33 = r33.append(r34);	 Catch:{ Exception -> 0x0218 }
        r32 = r33.toString();	 Catch:{ Exception -> 0x0218 }
        r33 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0218 }
        r33.<init>();	 Catch:{ Exception -> 0x0218 }
        r0 = r33;
        r1 = r22;
        r33 = r0.append(r1);	 Catch:{ Exception -> 0x0218 }
        r34 = " 23:59:59";
        r33 = r33.append(r34);	 Catch:{ Exception -> 0x0218 }
        r28 = r33.toString();	 Catch:{ Exception -> 0x0218 }
        r6 = 0;
        r33 = r7.isFirst();	 Catch:{ Exception -> 0x0218 }
        if (r33 == 0) goto L_0x021e;
    L_0x01e6:
        r0 = r37;
        r1 = r38;
        r2 = r32;
        r3 = r20;
        r27 = queryDb_stopTime1(r0, r1, r2, r3);	 Catch:{ Exception -> 0x0218 }
        r33 = java.lang.Integer.valueOf(r27);	 Catch:{ Exception -> 0x0218 }
        r0 = r33;
        r14.add(r0);	 Catch:{ Exception -> 0x0218 }
        r0 = r26;
        r1 = r22;
        r33 = r0.equals(r1);	 Catch:{ Exception -> 0x0218 }
        if (r33 != 0) goto L_0x021e;
    L_0x0205:
        r0 = r28;
        r1 = r23;
        r6 = com.record.utils.DateTime.cal_secBetween(r0, r1);	 Catch:{ Exception -> 0x0218 }
        r33 = java.lang.Integer.valueOf(r6);	 Catch:{ Exception -> 0x0218 }
        r0 = r33;
        r14.add(r0);	 Catch:{ Exception -> 0x0218 }
        goto L_0x0163;
    L_0x0218:
        r10 = move-exception;
        r4 = r5;
    L_0x021a:
        exceptionHandler(r10);
    L_0x021d:
        return r4;
    L_0x021e:
        r0 = r26;
        r1 = r22;
        r33 = r0.equals(r1);	 Catch:{ Exception -> 0x0218 }
        if (r33 != 0) goto L_0x026f;
    L_0x0228:
        r27 = 0;
        r34 = r14.iterator();	 Catch:{ Exception -> 0x0218 }
    L_0x022e:
        r33 = r34.hasNext();	 Catch:{ Exception -> 0x0218 }
        if (r33 == 0) goto L_0x0241;
    L_0x0234:
        r33 = r34.next();	 Catch:{ Exception -> 0x0218 }
        r33 = (java.lang.Integer) r33;	 Catch:{ Exception -> 0x0218 }
        r13 = r33.intValue();	 Catch:{ Exception -> 0x0218 }
        r27 = r27 + r13;
        goto L_0x022e;
    L_0x0241:
        r34 = new com.record.bean.XYColumn;	 Catch:{ Exception -> 0x0218 }
        r0 = r17;
        r1 = r26;
        r33 = r0.get(r1);	 Catch:{ Exception -> 0x0218 }
        r33 = (java.lang.Integer) r33;	 Catch:{ Exception -> 0x0218 }
        r33 = r33.intValue();	 Catch:{ Exception -> 0x0218 }
        r0 = r34;
        r1 = r33;
        r2 = r27;
        r3 = r26;
        r0.<init>(r1, r2, r3);	 Catch:{ Exception -> 0x0218 }
        r0 = r34;
        r5.add(r0);	 Catch:{ Exception -> 0x0218 }
        r0 = r17;
        r1 = r26;
        r0.remove(r1);	 Catch:{ Exception -> 0x0218 }
        r14 = new java.util.ArrayList;	 Catch:{ Exception -> 0x0218 }
        r14.<init>();	 Catch:{ Exception -> 0x0218 }
        r26 = r22;
    L_0x026f:
        r0 = r24;
        r1 = r22;
        r33 = r0.equals(r1);	 Catch:{ Exception -> 0x0218 }
        if (r33 == 0) goto L_0x02a9;
    L_0x0279:
        r0 = r20;
        r1 = r23;
        r6 = com.record.utils.DateTime.cal_secBetween(r0, r1);	 Catch:{ Exception -> 0x0218 }
        r33 = java.lang.Integer.valueOf(r6);	 Catch:{ Exception -> 0x0218 }
        r0 = r33;
        r14.add(r0);	 Catch:{ Exception -> 0x0218 }
    L_0x028a:
        r33 = r7.isLast();	 Catch:{ Exception -> 0x0218 }
        if (r33 == 0) goto L_0x0163;
    L_0x0290:
        r27 = 0;
        r34 = r14.iterator();	 Catch:{ Exception -> 0x0218 }
    L_0x0296:
        r33 = r34.hasNext();	 Catch:{ Exception -> 0x0218 }
        if (r33 == 0) goto L_0x0334;
    L_0x029c:
        r33 = r34.next();	 Catch:{ Exception -> 0x0218 }
        r33 = (java.lang.Integer) r33;	 Catch:{ Exception -> 0x0218 }
        r13 = r33.intValue();	 Catch:{ Exception -> 0x0218 }
        r27 = r27 + r13;
        goto L_0x0296;
    L_0x02a9:
        r0 = r20;
        r1 = r28;
        r6 = com.record.utils.DateTime.cal_secBetween(r0, r1);	 Catch:{ Exception -> 0x0218 }
        r33 = java.lang.Integer.valueOf(r6);	 Catch:{ Exception -> 0x0218 }
        r0 = r33;
        r14.add(r0);	 Catch:{ Exception -> 0x0218 }
        r27 = 0;
        r34 = r14.iterator();	 Catch:{ Exception -> 0x0218 }
    L_0x02c0:
        r33 = r34.hasNext();	 Catch:{ Exception -> 0x0218 }
        if (r33 == 0) goto L_0x02d3;
    L_0x02c6:
        r33 = r34.next();	 Catch:{ Exception -> 0x0218 }
        r33 = (java.lang.Integer) r33;	 Catch:{ Exception -> 0x0218 }
        r13 = r33.intValue();	 Catch:{ Exception -> 0x0218 }
        r27 = r27 + r13;
        goto L_0x02c0;
    L_0x02d3:
        r29 = 0;
        r0 = r17;
        r1 = r26;
        r33 = r0.get(r1);	 Catch:{ Exception -> 0x032f }
        r33 = (java.lang.Integer) r33;	 Catch:{ Exception -> 0x032f }
        r29 = r33.intValue();	 Catch:{ Exception -> 0x032f }
    L_0x02e3:
        r31 = r27;
        r30 = new com.record.bean.XYColumn;	 Catch:{ Exception -> 0x0218 }
        r0 = r30;
        r1 = r29;
        r2 = r31;
        r3 = r26;
        r0.<init>(r1, r2, r3);	 Catch:{ Exception -> 0x0218 }
        r0 = r30;
        r5.add(r0);	 Catch:{ Exception -> 0x0218 }
        r0 = r17;
        r1 = r26;
        r0.remove(r1);	 Catch:{ Exception -> 0x0218 }
        r14 = new java.util.ArrayList;	 Catch:{ Exception -> 0x0218 }
        r14.<init>();	 Catch:{ Exception -> 0x0218 }
        r33 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0218 }
        r33.<init>();	 Catch:{ Exception -> 0x0218 }
        r0 = r33;
        r1 = r24;
        r33 = r0.append(r1);	 Catch:{ Exception -> 0x0218 }
        r34 = " 00:00:00";
        r33 = r33.append(r34);	 Catch:{ Exception -> 0x0218 }
        r33 = r33.toString();	 Catch:{ Exception -> 0x0218 }
        r0 = r33;
        r1 = r23;
        r6 = com.record.utils.DateTime.cal_secBetween(r0, r1);	 Catch:{ Exception -> 0x0218 }
        r33 = java.lang.Integer.valueOf(r6);	 Catch:{ Exception -> 0x0218 }
        r0 = r33;
        r14.add(r0);	 Catch:{ Exception -> 0x0218 }
        r26 = r24;
        goto L_0x028a;
    L_0x032f:
        r10 = move-exception;
        r10.printStackTrace();	 Catch:{ Exception -> 0x0218 }
        goto L_0x02e3;
    L_0x0334:
        r0 = r17;
        r1 = r26;
        r33 = r0.get(r1);	 Catch:{ Exception -> 0x0218 }
        r33 = (java.lang.Integer) r33;	 Catch:{ Exception -> 0x0218 }
        r29 = r33.intValue();	 Catch:{ Exception -> 0x0218 }
        r33 = new com.record.bean.XYColumn;	 Catch:{ Exception -> 0x0218 }
        r0 = r33;
        r1 = r29;
        r2 = r27;
        r3 = r26;
        r0.<init>(r1, r2, r3);	 Catch:{ Exception -> 0x0218 }
        r0 = r33;
        r5.add(r0);	 Catch:{ Exception -> 0x0218 }
        r0 = r17;
        r1 = r26;
        r0.remove(r1);	 Catch:{ Exception -> 0x0218 }
        goto L_0x0163;
    L_0x035d:
        close(r7);	 Catch:{ Exception -> 0x0218 }
        r33 = r5.toString();	 Catch:{ Exception -> 0x0218 }
        log(r33);	 Catch:{ Exception -> 0x0218 }
        r33 = r17.entrySet();	 Catch:{ Exception -> 0x0218 }
        r34 = r33.iterator();	 Catch:{ Exception -> 0x0218 }
    L_0x036f:
        r33 = r34.hasNext();	 Catch:{ Exception -> 0x0218 }
        if (r33 == 0) goto L_0x03a0;
    L_0x0375:
        r12 = r34.next();	 Catch:{ Exception -> 0x0218 }
        r12 = (java.util.Map.Entry) r12;	 Catch:{ Exception -> 0x0218 }
        r26 = r12.getKey();	 Catch:{ Exception -> 0x0218 }
        r26 = (java.lang.String) r26;	 Catch:{ Exception -> 0x0218 }
        r35 = new com.record.bean.XYColumn;	 Catch:{ Exception -> 0x0218 }
        r33 = r12.getValue();	 Catch:{ Exception -> 0x0218 }
        r33 = (java.lang.Integer) r33;	 Catch:{ Exception -> 0x0218 }
        r33 = r33.intValue();	 Catch:{ Exception -> 0x0218 }
        r36 = 0;
        r0 = r35;
        r1 = r33;
        r2 = r36;
        r3 = r26;
        r0.<init>(r1, r2, r3);	 Catch:{ Exception -> 0x0218 }
        r0 = r35;
        r5.add(r0);	 Catch:{ Exception -> 0x0218 }
        goto L_0x036f;
    L_0x03a0:
        r4 = r5;
        goto L_0x021d;
    L_0x03a3:
        r10 = move-exception;
        goto L_0x021a;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.record.utils.db.DbUtils.getXseries2(android.content.Context, java.lang.String, java.lang.String, boolean):java.util.ArrayList<com.record.bean.XYColumn>");
    }

    public static ArrayList<XYColumn> getXseries(Context context, String actId, int dayBefore) {
        Exception e;
        ArrayList<XYColumn> arr = null;
        if (actId != null) {
            try {
                if (actId.length() != 0) {
                    ArrayList<XYColumn> arr2 = new ArrayList();
                    try {
                        String tempDate;
                        Iterator it;
                        String date = DateTime.beforeNDays2Str(-dayBefore);
                        HashMap<String, Integer> map = new HashMap();
                        int i = 0;
                        while (i <= dayBefore) {
                            map.put(DateTime.beforeNDays2Str(i == 0 ? 0 : -i), Integer.valueOf((dayBefore - i) + 1));
                            i++;
                        }
                        ArrayList<Integer> invest = new ArrayList();
                        Cursor cursor = getDb2(context).rawQuery("Select * from t_act_item where userId is " + queryUserId(context) + " and actId is " + actId + " and startTime > '" + date + "' and startTime < '" + DateTime.getDateString() + " 23:59:59' and isDelete is not 1 order by startTime", null);
                        if (cursor.getCount() > 0) {
                            tempDate = date;
                            while (cursor.moveToNext()) {
                                int tempInt;
                                String startTime = cursor.getString(cursor.getColumnIndex("startTime"));
                                String stopTime = cursor.getString(cursor.getColumnIndex("stopTime"));
                                String start_str = startTime.substring(0, startTime.indexOf(" "));
                                String stop_str = stopTime.substring(0, stopTime.indexOf(" "));
                                String zeroTime = start_str + " 00:00:00";
                                String tentyTime = start_str + " 23:59:59";
                                if (cursor.isFirst()) {
                                    invest.add(Integer.valueOf(queryDb_stopTime1(context, actId, zeroTime, startTime)));
                                }
                                if (!tempDate.equals(start_str)) {
                                    tempInt = 0;
                                    it = invest.iterator();
                                    while (it.hasNext()) {
                                        tempInt += ((Integer) it.next()).intValue();
                                    }
                                    arr2.add(new XYColumn(((Integer) map.get(tempDate)).intValue(), tempInt, DateTime.getDay3(context, tempDate + " 00:00:00") + "\n" + tempDate.substring(tempDate.lastIndexOf("-") + 1, tempDate.length())));
                                    map.remove(tempDate);
                                    invest = new ArrayList();
                                    tempDate = start_str;
                                }
                                if (stop_str.equals(start_str)) {
                                    invest.add(Integer.valueOf(DateTime.cal_secBetween(startTime, stopTime)));
                                } else {
                                    invest.add(Integer.valueOf(DateTime.cal_secBetween(startTime, tentyTime)));
                                    tempInt = 0;
                                    it = invest.iterator();
                                    while (it.hasNext()) {
                                        tempInt += ((Integer) it.next()).intValue();
                                    }
                                    arr2.add(new XYColumn(((Integer) map.get(tempDate)).intValue(), tempInt, DateTime.getDay3(context, tempDate + " 00:00:00") + "\n" + tempDate.substring(tempDate.lastIndexOf("-") + 1, tempDate.length())));
                                    map.remove(tempDate);
                                    invest = new ArrayList();
                                    invest.add(Integer.valueOf(DateTime.cal_secBetween(stop_str + " 00:00:00", stopTime)));
                                    tempDate = stop_str;
                                }
                                if (cursor.isLast()) {
                                    tempInt = 0;
                                    it = invest.iterator();
                                    while (it.hasNext()) {
                                        tempInt += ((Integer) it.next()).intValue();
                                    }
                                    arr2.add(new XYColumn(((Integer) map.get(tempDate)).intValue(), tempInt, DateTime.getDay3(context, tempDate + " 00:00:00") + "\n" + tempDate.substring(tempDate.lastIndexOf("-") + 1, tempDate.length())));
                                    map.remove(tempDate);
                                }
                            }
                        }
                        close(cursor);
                        for (Entry<String, Integer> entry : map.entrySet()) {
                            tempDate = (String) entry.getKey();
                            arr2.add(new XYColumn(((Integer) entry.getValue()).intValue(), 0, DateTime.getDay3(context, tempDate + " 00:00:00") + "\n" + tempDate.substring(tempDate.lastIndexOf("-") + 1, tempDate.length())));
                        }
                        arr = arr2;
                    } catch (Exception e2) {
                        e = e2;
                        arr = arr2;
                        exceptionHandler(e);
                        return arr;
                    }
                    return arr;
                }
            } catch (Exception e3) {
                e = e3;
                exceptionHandler(e);
                return arr;
            }
        }
        return null;
    }

    private static int queryDb_stopTime1(Context context, String actId, String zeroTime, String endTime) {
        int counter = 0;
        Cursor cursor2 = getDb(context).rawQuery("select * from t_act_item where userId is ? and actId is " + actId + " and stopTime > '" + zeroTime + "' and stopTime <= '" + endTime + "'", new String[]{User.getInstance().getUserId() + ""});
        if (cursor2.getCount() > 0) {
            cursor2.moveToNext();
            String stopTime2 = cursor2.getString(cursor2.getColumnIndex("stopTime"));
            String startTime2 = cursor2.getString(cursor2.getColumnIndex("startTime"));
            if (!startTime2.substring(0, startTime2.indexOf(" ")).equals(stopTime2.substring(0, stopTime2.indexOf(" ")))) {
                counter = DateTime.cal_secBetween(zeroTime, stopTime2);
            }
        }
        close(cursor2);
        return counter;
    }

    public static String queryUserName(Context context) {
        if (User.getInstance() == null) {
            UserUtils.isLoginUser(context);
        }
        if (User.getInstance().getUserId() == 0) {
            UserUtils.isLoginUser(context);
            int id = User.getInstance().getUserId();
        }
        return User.getInstance().getUserName();
    }

    public static String queryUserId(Context context) {
        if (User.getInstance() == null) {
            UserUtils.isLoginUser(context);
        }
        int id = User.getInstance().getUserId();
        if (id == 0) {
            UserUtils.isLoginUser(context);
            id = User.getInstance().getUserId();
        }
        return id + "";
    }

    public static int queryUserId2(Context context) {
        if (User.getInstance() == null) {
            UserUtils.isLoginUser(context);
        }
        int id = User.getInstance().getUserId();
        if (id != 0) {
            return id;
        }
        UserUtils.isLoginUser(context);
        return User.getInstance().getUserId();
    }

    public static int queryAuthorizationByUserId(Context context, int userId) {
        try {
            Cursor cursor = getDb(context).rawQuery("select age from t_user where id  = " + queryUserId(context), null);
            if (cursor.getCount() <= 0) {
                return 0;
            }
            cursor.moveToNext();
            return cursor.getInt(cursor.getColumnIndex("age"));
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    public static int queryIsShowUploadGuideByUserId(Context context, int userId) {
        try {
            Cursor cursor = getDb(context).rawQuery("select gender from t_user where id  = " + queryUserId(context), null);
            if (cursor.getCount() <= 0) {
                return 0;
            }
            cursor.moveToNext();
            return cursor.getInt(cursor.getColumnIndex("gender"));
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    public static long queryUserUid(Context context) {
        if (User.getInstance().getUserId() == 0) {
            UserUtils.isLoginUser(context);
            int id = User.getInstance().getUserId();
        }
        try {
            return Long.parseLong(User.getInstance().getUid());
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    public static long queryUserUidByUserId(Context context, int id) {
        if (queryUserId2(context) == id) {
            return queryUserUid(context);
        }
        Cursor cursor = getDb(context).rawQuery("select uid from t_user where id is " + id, null);
        if (cursor.getCount() > 0) {
            cursor.moveToNext();
            try {
                long uid = Long.parseLong(cursor.getString(cursor.getColumnIndex("uid")));
                close(cursor);
                return uid;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        close(cursor);
        return 0;
    }

    public static int querysGoalIdByActId(Context context, int actId) {
        Cursor cursor = getDb(context).rawQuery("select severId from t_act where id is " + actId, null);
        if (cursor.getCount() > 0) {
            cursor.moveToNext();
            try {
                int severId = getInt(cursor, "severId");
                close(cursor);
                return severId;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        close(cursor);
        return 0;
    }

    public static String queryColorByActId(Context context, int id) {
        String color = "bg_green1";
        Cursor cursor = getDb(context).rawQuery("select color from t_act where id is " + id, null);
        if (cursor.getCount() > 0) {
            cursor.moveToNext();
            color = cursor.getString(cursor.getColumnIndex("color"));
        }
        close(cursor);
        return color;
    }

    public static String queryUserNameByUserId(Context context, int id) {
        if (queryUserId2(context) == id) {
            return queryUserName(context);
        }
        String userName = "";
        Cursor cursor = getDb(context).rawQuery("select userName from t_user where id is " + id, null);
        if (cursor.getCount() > 0) {
            cursor.moveToNext();
            userName = cursor.getString(cursor.getColumnIndex("userName"));
        }
        close(cursor);
        return userName;
    }

    public static int queryIsHideByGoalId(Context context, String goalId) {
        int isHided = 0;
        Cursor cursor = getDb(context).rawQuery("select isHided from t_act where id is " + goalId, null);
        if (cursor.getCount() > 0) {
            cursor.moveToNext();
            isHided = cursor.getInt(cursor.getColumnIndex("isHided"));
        }
        close(cursor);
        return isHided;
    }

    public static int queryResetCountByGoalId(Context context, String goalId) {
        int resetCount = 0;
        Cursor cursor = getDb(context).rawQuery("select resetCount from t_act where id is " + goalId, null);
        if (cursor.getCount() > 0) {
            cursor.moveToNext();
            resetCount = cursor.getInt(cursor.getColumnIndex("resetCount"));
        }
        close(cursor);
        resetCount = 3 - resetCount;
        if (resetCount <= 0) {
            return 0;
        }
        return resetCount;
    }

    public static int queryResetCountByGoalId2(Context context, String goalId) {
        int resetCount = 0;
        Cursor cursor = getDb(context).rawQuery("select resetCount from t_act where id is " + goalId, null);
        if (cursor.getCount() > 0) {
            cursor.moveToNext();
            resetCount = cursor.getInt(cursor.getColumnIndex("resetCount"));
        }
        close(cursor);
        return resetCount;
    }

    public static int queryIsDeleteByGoalId(Context context, String goalId) {
        int isDelete = -1;
        Cursor cursor = getDb(context).rawQuery("select isDelete from t_act where id is " + goalId, null);
        if (cursor.getCount() > 0) {
            cursor.moveToNext();
            isDelete = cursor.getInt(cursor.getColumnIndex("isDelete"));
        }
        close(cursor);
        return isDelete;
    }

    public static double querySitemIdByItemsId(Context context, int itemsId) {
        double sGoalItemsId = 0.0d;
        Cursor cursor = getDb(context).rawQuery("select sGoalItemId from t_act_item where id is " + itemsId, null);
        if (cursor.getCount() > 0) {
            cursor.moveToNext();
            sGoalItemsId = cursor.getDouble(cursor.getColumnIndex("sGoalItemId"));
        }
        close(cursor);
        return sGoalItemsId;
    }

    public static double querysLabelIdByItemsId(Context context, int subTypeId) {
        double sLabelId = 0.0d;
        Cursor cursor = getDb(context).rawQuery("select sLabelId from t_sub_type where Id is " + subTypeId, null);
        if (cursor.getCount() > 0) {
            cursor.moveToNext();
            sLabelId = cursor.getDouble(cursor.getColumnIndex("sLabelId"));
        }
        close(cursor);
        return sLabelId;
    }

    public static String getWhereUserId(Context context) {
        return " userId is " + queryUserId(context) + " ";
    }

    public static int queryTryUserId(Context context) {
        int TRY_ID = 0;
        Cursor cursor = getDb(context).rawQuery("select id from t_user where userName is '测试'", null);
        if (cursor.getCount() > 0) {
            cursor.moveToNext();
            TRY_ID = cursor.getInt(cursor.getColumnIndex("id"));
        }
        close(cursor);
        return TRY_ID;
    }

    public static String getWhereUserIdNotTry(Context context) {
        String where = "";
        int tryId = queryTryUserId(context);
        if (tryId > 0) {
            return "userId is not " + tryId + " and userId is " + queryUserId(context);
        }
        return "userId is " + queryUserId(context);
    }

    public static String queryWeibo(Context context, String userid, String sendtype, String weiboType) {
        String content = SHARE_STRING.weiboDefault;
        Cursor cursor = getDb(context).rawQuery("select content from t_weibo where userId is " + userid + " and sendtype is " + sendtype + " and weiboType is " + weiboType, null);
        if (cursor.getCount() > 0) {
            cursor.moveToNext();
            content = cursor.getString(cursor.getColumnIndex(PushInitUtils.RESPONSE_CONTENT));
        } else {
            initWeiboData(context, userid);
            content = queryWeibo(context, userid, sendtype, weiboType);
        }
        close(cursor);
        if (content == null || content.trim().length() == 0) {
            return SHARE_STRING.weiboDefault;
        }
        return content;
    }

    public static int queryUnhandlerTomatoCount(Context context) {
        Cursor cursor = getDb(context).rawQuery("select count(Id) from t_unhandler_tomato", null);
        if (cursor.getCount() <= 0) {
            return 0;
        }
        cursor.moveToNext();
        return cursor.getInt(cursor.getColumnIndex("count(Id)"));
    }

    public static String queryActId(Context context, String userid, String type) {
        String actId = "";
        Cursor cursor = getDb(context).rawQuery("select * from t_act where userId is " + userid + " and type is " + type, null);
        if (cursor.getCount() > 0) {
            cursor.moveToNext();
            actId = cursor.getString(cursor.getColumnIndex("id"));
        }
        close(cursor);
        return actId;
    }

    public static String queryActId(Context context) {
        String actId = "";
        Cursor cursor = getDb(context).rawQuery("select actId from t_act_item where " + getWhereUserId(context) + " and isDelete is not 1 and isEnd is not 1 order by startTime desc", null);
        if (cursor.getCount() > 0) {
            cursor.moveToNext();
            actId = cursor.getString(cursor.getColumnIndex("actId"));
        }
        close(cursor);
        return actId;
    }

    public static String queryActId(Context context, String type) {
        String actId = "";
        Cursor cursor = getDb(context).rawQuery("select id from t_act where " + getWhereUserId(context) + " and type is " + type, null);
        if (cursor.getCount() > 0) {
            cursor.moveToNext();
            actId = cursor.getString(cursor.getColumnIndex("id"));
        }
        close(cursor);
        return actId;
    }

    public static double queryStaticsHadInvestByGoalId(Context context, int goalId) {
        if (goalId > 0) {
            Cursor cursor = getDb2(context).rawQuery(Sql.staticsQueryHadInvestByGoalId(context, goalId), null);
            if (cursor.getCount() <= 0 || !cursor.moveToNext()) {
                close(cursor);
            } else {
                double invest = getDou(cursor, "hadInvest");
                close(cursor);
                return invest;
            }
        }
        return 0.0d;
    }

    public static double queryStaticsHadInvestByDateGoalId(Context context, int goalId, String date) {
        if (goalId > 0) {
            Cursor cursor = getDb2(context).rawQuery("select SUM(take) from t_act_item where actId = " + goalId + " and startTime > '" + date + " 00:00:00' and isDelete is not 1", null);
            if (cursor.getCount() <= 0 || !cursor.moveToNext()) {
                close(cursor);
            } else {
                double invest = getDou(cursor, "SUM(take)");
                close(cursor);
                return invest;
            }
        }
        return 0.0d;
    }

    public static void initWeiboData(Context context, String userid) {
        String[] sendtypeArr = new String[]{"1", "2", "3", "4", "5"};
        String[] contentArr = new String[]{SHARE_STRING.weiboDefault, SHARE_STRING.weiboGoal, SHARE_STRING.weiboHistory, SHARE_STRING.weiboEvery, SHARE_STRING.weiboSleep};
        for (int i = 0; i < sendtypeArr.length; i++) {
            Cursor cursor = getDb(context).rawQuery("select id from t_weibo where userId is " + userid + " and sendtype is " + sendtypeArr[i], null);
            if (cursor.getCount() == 0) {
                ContentValues values = new ContentValues();
                values.put("userId", queryUserId(context));
                values.put("sendtype", sendtypeArr[i]);
                values.put("weiboType", "1");
                values.put(PushInitUtils.RESPONSE_CONTENT, contentArr[i]);
                values.put("time", DateTime.getTimeString());
                getDb(context).insert("t_weibo", null, values);
            }
            close(cursor);
        }
    }

    public static String queryAllocation_invest(Context context, String userid) {
        String hour = "0分钟";
        int second = 0;
        Cursor cursor = getDb(context).rawQuery("select invest from t_allocation where userId is " + userid + " and time is '" + DateTime.getDateString() + "'", null);
        if (cursor.getCount() > 0) {
            cursor.moveToLast();
            second = cursor.getInt(cursor.getColumnIndex("invest"));
        }
        if (second > 0 && second < 3600) {
            hour = FormatUtils.format_1fra(((double) second) / 60.0d) + "分钟";
        } else if (second > 3600) {
            hour = FormatUtils.format_1fra(((double) second) / 3600.0d) + "小时";
        }
        close(cursor);
        return hour;
    }

    public static void insertDB_todayAllocation(Context context) {
        Cursor cursor = getDb(context).rawQuery("select startTime from t_act_item where " + getWhereUserId(context) + " limit 1", null);
        String start = DateTime.getDateString();
        if (cursor.getCount() > 0) {
            cursor.moveToNext();
            String startTime = cursor.getString(cursor.getColumnIndex("startTime"));
            start = startTime.substring(0, startTime.indexOf(" "));
        }
        close(cursor);
        if (getDb(context).rawQuery("Select userId,time from t_allocation where userId is ? and time is ?", new String[]{User.getInstance().getUserId() + "", start}).getCount() == 0) {
            ContentValues values = new ContentValues();
            values.put("userId", Integer.valueOf(User.getInstance().getUserId()));
            values.put("time", start);
            getDb(context).insert("t_allocation", null, values);
            Log.i("override DbUtils", "插入" + start + "时间分配！");
        }
        close(cursor);
    }

    public static void insertOrUpdateDb_allocation(Context context, String date, ContentValues values) {
        Cursor cursor = getDb(context).rawQuery("Select * from t_allocation where userid is ? and time is '" + date + "'", new String[]{User.getInstance().getUserId() + ""});
        if (cursor.getCount() > 0) {
            getDb(context).update("t_allocation", values, " userid is ? and  time is  '" + date + "'", new String[]{User.getInstance().getUserId() + ""});
        } else {
            getDb(context).insert("t_allocation", null, values);
        }
        close(cursor);
    }

    public static void insertDb_items(Context context) {
        Cursor actCursor = getDb(context).rawQuery("Select Id from t_act_item where userId is ?", new String[]{User.getInstance().getUserId() + ""});
        if (actCursor.getCount() == 0) {
            ContentValues values = new ContentValues();
            values.put("userId", Integer.valueOf(User.getInstance().getUserId()));
            values.put("actId", Integer.valueOf(1));
            values.put("startTime", "2014-02-20 06:00:00");
            values.put("take", Integer.valueOf(3600));
            values.put("stopTime", "2014-02-20 07:00:00");
            values.put("isEnd", Integer.valueOf(1));
            getDb(context).insert("t_act_item", null, values);
            values = new ContentValues();
            values.put("userId", Integer.valueOf(User.getInstance().getUserId()));
            values.put("actId", Integer.valueOf(2));
            values.put("startTime", "2014-02-20 23:00:00");
            values.put("take", Integer.valueOf(7200));
            values.put("stopTime", "2014-02-21 01:00:00");
            values.put("isEnd", Integer.valueOf(1));
            getDb(context).insert("t_act_item", null, values);
            values = new ContentValues();
            values.put("userId", Integer.valueOf(User.getInstance().getUserId()));
            values.put("actId", Integer.valueOf(1));
            values.put("startTime", "2014-02-21 08:00:00");
            values.put("take", Integer.valueOf(3600));
            values.put("stopTime", "2014-02-21 09:00:00");
            values.put("isEnd", Integer.valueOf(1));
            getDb(context).insert("t_act_item", null, values);
            values = new ContentValues();
            values.put("userId", Integer.valueOf(User.getInstance().getUserId()));
            values.put("actId", Integer.valueOf(1));
            values.put("startTime", "2014-02-21 23:00:00");
            values.put("take", Integer.valueOf(3600));
            values.put("stopTime", "2014-02-22 07:00:00");
            values.put("isEnd", Integer.valueOf(1));
            getDb(context).insert("t_act_item", null, values);
            values = new ContentValues();
            values.put("userId", Integer.valueOf(User.getInstance().getUserId()));
            values.put("actId", Integer.valueOf(1));
            values.put("startTime", "2014-02-22 08:00:00");
            values.put("take", Integer.valueOf(3600));
            values.put("stopTime", "2014-02-24 21:52:00");
            getDb(context).insert("t_act_item", null, values);
        }
        close(actCursor);
    }

    public static void insertDb_goalType(Context context) {
        Cursor actCursor = getDb(context).rawQuery("Select id from t_act where userId is ? order by position limit 1", new String[]{queryUserId(context)});
        if (actCursor.getCount() == 0) {
            ContentValues values = new ContentValues();
            values.put("userId", Integer.valueOf(User.getInstance().getUserId()));
            values.put("image", "desklamp");
            values.put("color", Val.col_Str_Arr[0]);
            values.put("actName", context.getResources().getString(R.string.str_invest));
            values.put("position", Integer.valueOf(1));
            values.put(a.a, Integer.valueOf(10));
            values.put("createTime", DateTime.getTimeString());
            values.put("intruction", context.getResources().getString(R.string.str_ins_invest));
            int actid = (int) getDb(context).insert("t_act", null, values);
            values = new ContentValues();
            values.put("userId", Integer.valueOf(User.getInstance().getUserId()));
            values.put("image", "computer");
            values.put("color", Val.col_Str_Arr[3]);
            values.put("actName", context.getResources().getString(R.string.str_routine));
            values.put("position", Integer.valueOf(2));
            values.put(a.a, Integer.valueOf(20));
            values.put("createTime", DateTime.getTimeString());
            values.put("intruction", context.getResources().getString(R.string.str_ins_routine));
            actid = (int) getDb(context).insert("t_act", null, values);
            values = new ContentValues();
            values.put("userId", Integer.valueOf(User.getInstance().getUserId()));
            values.put("image", "bed");
            values.put("color", Val.col_Str_Arr[6]);
            values.put("actName", context.getResources().getString(R.string.str_sleep));
            values.put("position", Integer.valueOf(3));
            values.put(a.a, Integer.valueOf(30));
            values.put("createTime", DateTime.getTimeString());
            values.put("intruction", context.getResources().getString(R.string.str_ins_sleep));
            actid = (int) getDb(context).insert("t_act", null, values);
            values = new ContentValues();
            values.put("userId", Integer.valueOf(User.getInstance().getUserId()));
            values.put("image", "trash");
            values.put("color", Val.col_Str_Arr[9]);
            values.put("actName", context.getResources().getString(R.string.str_waste));
            values.put("position", Integer.valueOf(4));
            values.put(a.a, Integer.valueOf(40));
            values.put("createTime", DateTime.getTimeString());
            values.put("isDefault", Integer.valueOf(1));
            values.put("intruction", context.getResources().getString(R.string.str_ins_waste));
            int insert = (int) getDb(context).insert("t_act", null, values);
        }
        close(actCursor);
    }

    public static void insertDb_LabelType(Context context) {
        try {
            String[] typeLabelArr = new String[]{context.getResources().getString(R.string.str_Housework), context.getResources().getString(R.string.str_Sport)};
            Cursor actCursor = getDb(context).rawQuery("Select Id from t_sub_type where " + getWhereUserId(context) + " and labelType is 1", null);
            if (actCursor.getCount() == 0) {
                for (String put : typeLabelArr) {
                    ContentValues values = new ContentValues();
                    values.put("userId", Integer.valueOf(User.getInstance().getUserId()));
                    values.put("actType", Integer.valueOf(20));
                    values.put("labelType", Integer.valueOf(1));
                    values.put(IDemoChart.NAME, put);
                    values.put("time", DateTime.getTimeString());
                    getDb(context).insert("t_sub_type", null, values);
                }
            }
            close(actCursor);
        } catch (Exception e) {
            exceptionHandler(e);
        }
    }

    public static void getActAndSet(Context context, String actId) {
        Cursor actCursor = getDb(context).rawQuery("select * from t_act where id is " + actId + " and  userId is " + User.getInstance().getUserId(), null);
        if (actCursor.getCount() > 0) {
            actCursor.moveToNext();
            int actid = actCursor.getInt(actCursor.getColumnIndex("id"));
            String image = actCursor.getString(actCursor.getColumnIndex("image"));
            String color = actCursor.getString(actCursor.getColumnIndex("color"));
            Act.getInstance().SetAct(actid, actCursor.getString(actCursor.getColumnIndex("actName")), image, color, actCursor.getInt(actCursor.getColumnIndex("position")));
        }
        close(actCursor);
    }

    public static void getActAndSet_v2(Context context, String actId) {
        Cursor actCursor = getDb2(context).rawQuery("select * from t_act where id is " + actId + " and  userId is " + User.getInstance().getUserId(), null);
        if (actCursor.getCount() > 0) {
            actCursor.moveToNext();
            int actid = actCursor.getInt(actCursor.getColumnIndex("id"));
            int type = actCursor.getInt(actCursor.getColumnIndex(a.a));
            String image = actCursor.getString(actCursor.getColumnIndex("image"));
            String color = actCursor.getString(actCursor.getColumnIndex("color"));
            Act.getInstance().SetAct(actid, actCursor.getString(actCursor.getColumnIndex("actName")), image, color, actCursor.getString(actCursor.getColumnIndex("intruction")), type);
        }
        close(actCursor);
    }

    public static HashMap<Integer, String> getActIdToNameMap(Context context) {
        HashMap<Integer, String> idToName = new HashMap();
        Cursor actCursor = getDb2(context).rawQuery("select * from t_act where " + getWhereUserId(context), null);
        if (actCursor.getCount() > 0) {
            while (actCursor.moveToNext()) {
                int actid = actCursor.getInt(actCursor.getColumnIndex("id"));
                int type = actCursor.getInt(actCursor.getColumnIndex(a.a));
                idToName.put(Integer.valueOf(actid), actCursor.getString(actCursor.getColumnIndex("actName")));
            }
        }
        close(actCursor);
        return idToName;
    }

    public static ArrayList getSummarysByDay(Context context, String startDate, String endDate) {
        ArrayList list = new ArrayList();
        Cursor actCursor = getDb2(context).rawQuery("SELECT * FROM t_allocation WHERE " + getWhereUserId(context) + " AND time >= '" + startDate + "' AND time <= '" + endDate + "' ORDER BY time;", null);
        if (actCursor.getCount() > 0) {
            while (actCursor.moveToNext()) {
                int id = getInt(actCursor, "id");
                String userId = getStr(actCursor, "userId");
                int invest = getInt(actCursor, "invest");
                int waste = getInt(actCursor, "waste");
                int routine = getInt(actCursor, "routine");
                int sleep = getInt(actCursor, "sleep");
                String date = getStr(actCursor, "time");
                String remarks = getStr(actCursor, "remarks");
                ArrayList arrayList = list;
                arrayList.add(new Allocation(date, getDou(actCursor, "earnMoney"), id, invest, getStr(actCursor, "morningVoice"), remarks, routine, sleep, userId, waste));
            }
        }
        close(actCursor);
        return list;
    }

    public static void getActAndSet(Context context) {
        Cursor actCursor = getDb(context).rawQuery("select * from t_act where  userId is " + User.getInstance().getUserId(), null);
        if (actCursor.getCount() > 0) {
            actCursor.moveToNext();
            int actid = actCursor.getInt(actCursor.getColumnIndex("id"));
            String image = actCursor.getString(actCursor.getColumnIndex("image"));
            String color = actCursor.getString(actCursor.getColumnIndex("color"));
            Act.getInstance().SetAct(actid, actCursor.getString(actCursor.getColumnIndex("actName")), image, color, actCursor.getInt(actCursor.getColumnIndex("position")));
        }
        close(actCursor);
    }

    public static String getActNameById(Context context, int id) {
        Cursor cursor2 = getDb(context).rawQuery("select actName from t_act where Id is " + id + " and  userId is " + User.getInstance().getUserId(), null);
        if (cursor2.getCount() > 0) {
            cursor2.moveToNext();
            String actName = cursor2.getString(cursor2.getColumnIndex("actName"));
            close(cursor2);
            return actName;
        }
        close(cursor2);
        return "";
    }

    public static TreeSet<Integer> updateDbActItem_ChangeEndTime(Context context, String addStartTime, String where) {
        TreeSet<Integer> goalIdSet = new TreeSet();
        Cursor cursor = getDb(context).rawQuery("select * from t_act_item where " + getWhereUserId(context) + " and isDelete is not 1   and startTime <= '" + addStartTime + "' and stopTime >= '" + addStartTime + "' " + where, null);
        if (cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                ContentValues values;
                String isEnd = cursor.getString(cursor.getColumnIndex("isEnd"));
                String tempid = cursor.getString(cursor.getColumnIndex("id"));
                String startTime = cursor.getString(cursor.getColumnIndex("startTime"));
                String stopTime = cursor.getString(cursor.getColumnIndex("stopTime"));
                int isUpload = cursor.getInt(cursor.getColumnIndex("isUpload"));
                int take = DateTime.cal_secBetween(startTime, addStartTime);
                goalIdSet.add(Integer.valueOf(cursor.getInt(cursor.getColumnIndex("actId"))));
                if (take > 0) {
                    values = new ContentValues();
                    values.put("stopTime", addStartTime);
                    values.put("take", Integer.valueOf(take));
                    if (isUpload > 0) {
                        values.put("endUpdateTime", DateTime.getTimeString());
                    }
                    getDb(context).update("t_act_item", values, " id is ? ", new String[]{tempid});
                }
                values = new ContentValues();
                values.put("take", Integer.valueOf(take));
                values.put("endUpdateTime", DateTime.getTimeString());
                getDb(context).update("t_routine_link", values, " itemsId is ? ", new String[]{tempid});
            }
        }
        close(cursor);
        return goalIdSet;
    }

    public static TreeSet<Integer> updateDbActItem_ChangeStartTime(Context context, String addEndTime, String where) {
        TreeSet<Integer> goalIdSet = new TreeSet();
        Cursor cursor = getDb(context).rawQuery("select * from t_act_item where " + getWhereUserId(context) + " and isDelete is not 1   and   startTime <= '" + addEndTime + "' and stopTime >= '" + addEndTime + "' " + where, null);
        if (cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                ContentValues values;
                String isEnd = cursor.getString(cursor.getColumnIndex("isEnd"));
                String tempid = cursor.getString(cursor.getColumnIndex("id"));
                String startTime = cursor.getString(cursor.getColumnIndex("startTime"));
                int take = DateTime.cal_secBetween(addEndTime, cursor.getString(cursor.getColumnIndex("stopTime")));
                int isUpload = cursor.getInt(cursor.getColumnIndex("isUpload"));
                goalIdSet.add(Integer.valueOf(cursor.getInt(cursor.getColumnIndex("actId"))));
                if (take >= 0) {
                    values = new ContentValues();
                    values.put("startTime", addEndTime);
                    values.put("take", Integer.valueOf(take));
                    if (isUpload > 0) {
                        values.put("endUpdateTime", DateTime.getTimeString());
                    }
                    getDb(context).update("t_act_item", values, " id is ? ", new String[]{tempid});
                }
                values = new ContentValues();
                values.put("take", Integer.valueOf(take));
                values.put("endUpdateTime", DateTime.getTimeString());
                getDb(context).update("t_routine_link", values, " itemsId =  " + tempid, null);
            }
        }
        close(cursor);
        return goalIdSet;
    }

    public static TreeSet<Integer> deleteActItem_deleteRecords(Context context, String addStartTime, String addEndTime, String where) {
        TreeSet<Integer> goalIdSet = new TreeSet();
        Cursor cursor = getDb(context).rawQuery("select * from t_act_item where " + getWhereUserId(context) + " and isDelete is not 1   and  isEnd is 1 and startTime >= '" + addStartTime + "' and stopTime <= '" + addEndTime + "' " + where, null);
        if (cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                String tempid = cursor.getString(cursor.getColumnIndex("id"));
                int isUpload = cursor.getInt(cursor.getColumnIndex("isUpload"));
                goalIdSet.add(Integer.valueOf(cursor.getInt(cursor.getColumnIndex("actId"))));
                if (isUpload > 0) {
                    ContentValues values = new ContentValues();
                    values.put("isDelete", Integer.valueOf(1));
                    values.put("deleteTime", DateTime.getTimeString());
                    values.put("endUpdateTime", DateTime.getTimeString());
                    getDb(context).update("t_act_item", values, " id is ? ", new String[]{tempid});
                    log("更新删除items:" + tempid);
                } else {
                    getDb(context).delete("t_act_item", " id is ? ", new String[]{tempid});
                    log("删除items:" + tempid);
                }
                deleteLabelLinkByItemsId(context, tempid + "");
            }
        }
        close(cursor);
        return goalIdSet;
    }

    /* JADX WARNING: Removed duplicated region for block: B:32:0x013c  */
    /* JADX WARNING: Removed duplicated region for block: B:7:0x0080  */
    /* JADX WARNING: Removed duplicated region for block: B:15:0x00e8  */
    /* JADX WARNING: Removed duplicated region for block: B:18:0x00f2  */
    public static java.lang.String[] queryLastRecordStopTime(android.content.Context r17, java.lang.String r18) {
        /*
        r14 = new java.lang.StringBuilder;
        r14.<init>();
        r0 = r18;
        r14 = r14.append(r0);
        r15 = " 00:00:00";
        r14 = r14.append(r15);
        r5 = r14.toString();
        r14 = new java.lang.StringBuilder;
        r14.<init>();
        r0 = r18;
        r14 = r14.append(r0);
        r15 = " 23:59:59";
        r14 = r14.append(r15);
        r4 = r14.toString();
        r2 = r5;
        r1 = "";
        if (r18 == 0) goto L_0x0100;
    L_0x002f:
        r14 = com.record.utils.DateTime.getDateString();
        r0 = r18;
        r14 = r0.contains(r14);
        if (r14 == 0) goto L_0x0100;
    L_0x003b:
        r1 = com.record.utils.DateTime.getTimeString();
    L_0x003f:
        r14 = getDb(r17);
        r15 = new java.lang.StringBuilder;
        r15.<init>();
        r16 = "select stopTime from t_act_item where ";
        r15 = r15.append(r16);
        r16 = getWhereUserId(r17);
        r15 = r15.append(r16);
        r16 = " and isDelete is not 1 and stopTime >= '";
        r15 = r15.append(r16);
        r15 = r15.append(r5);
        r16 = "' and stopTime < '";
        r15 = r15.append(r16);
        r15 = r15.append(r4);
        r16 = "' order by stopTime desc limit 1";
        r15 = r15.append(r16);
        r15 = r15.toString();
        r16 = 0;
        r6 = r14.rawQuery(r15, r16);
        r14 = r6.getCount();
        if (r14 <= 0) goto L_0x013c;
    L_0x0080:
        r6.moveToNext();
        r14 = "stopTime";
        r14 = r6.getColumnIndex(r14);
        r10 = r6.getString(r14);
        r2 = r10;
        r14 = getDb(r17);
        r15 = new java.lang.StringBuilder;
        r15.<init>();
        r16 = "select * from t_act_item where ";
        r15 = r15.append(r16);
        r16 = getWhereUserId(r17);
        r15 = r15.append(r16);
        r16 = " and isDelete is not 1 and startTime >= '";
        r15 = r15.append(r16);
        r15 = r15.append(r10);
        r16 = "'  order by startTime  limit 1";
        r15 = r15.append(r16);
        r15 = r15.toString();
        r16 = 0;
        r7 = r14.rawQuery(r15, r16);
        r14 = r7.getCount();
        if (r14 <= 0) goto L_0x0122;
    L_0x00c5:
        r7.moveToNext();
        r14 = "startTime";
        r14 = r7.getColumnIndex(r14);
        r9 = r7.getString(r14);
        r14 = com.record.utils.DateTime.cal_secBetween(r10, r9);
        r15 = 43200; // 0xa8c0 float:6.0536E-41 double:2.13436E-319;
        if (r14 >= r15) goto L_0x0108;
    L_0x00db:
        r1 = r9;
    L_0x00dc:
        close(r7);
    L_0x00df:
        close(r6);
        r14 = com.record.utils.DateTime.compareNow(r2);
        if (r14 <= 0) goto L_0x00ec;
    L_0x00e8:
        r1 = com.record.utils.DateTime.getTimeString();
    L_0x00ec:
        r14 = com.record.utils.DateTime.compareNow(r1);
        if (r14 <= 0) goto L_0x00f6;
    L_0x00f2:
        r1 = com.record.utils.DateTime.getTimeString();
    L_0x00f6:
        r14 = 2;
        r14 = new java.lang.String[r14];
        r15 = 0;
        r14[r15] = r2;
        r15 = 1;
        r14[r15] = r1;
        return r14;
    L_0x0100:
        r14 = 3600; // 0xe10 float:5.045E-42 double:1.7786E-320;
        r1 = com.record.utils.DateTime.beforeSecond(r2, r14);
        goto L_0x003f;
    L_0x0108:
        if (r18 == 0) goto L_0x011b;
    L_0x010a:
        r14 = com.record.utils.DateTime.getDateString();
        r0 = r18;
        r14 = r0.contains(r14);
        if (r14 == 0) goto L_0x011b;
    L_0x0116:
        r1 = com.record.utils.DateTime.getTimeString();
        goto L_0x00dc;
    L_0x011b:
        r14 = 3600; // 0xe10 float:5.045E-42 double:1.7786E-320;
        r1 = com.record.utils.DateTime.beforeSecond(r2, r14);
        goto L_0x00dc;
    L_0x0122:
        if (r18 == 0) goto L_0x0135;
    L_0x0124:
        r14 = com.record.utils.DateTime.getDateString();
        r0 = r18;
        r14 = r0.contains(r14);
        if (r14 == 0) goto L_0x0135;
    L_0x0130:
        r1 = com.record.utils.DateTime.getTimeString();
        goto L_0x00dc;
    L_0x0135:
        r14 = 3600; // 0xe10 float:5.045E-42 double:1.7786E-320;
        r1 = com.record.utils.DateTime.beforeSecond(r2, r14);
        goto L_0x00dc;
    L_0x013c:
        r3 = com.record.utils.DateTime.pars2Calender2(r18);
        r14 = 5;
        r15 = -1;
        r3.add(r14, r15);
        r11 = com.record.utils.DateTime.formatDate(r3);
        r14 = new java.lang.StringBuilder;
        r14.<init>();
        r14 = r14.append(r11);
        r15 = " 00:00:00";
        r14 = r14.append(r15);
        r13 = r14.toString();
        r14 = new java.lang.StringBuilder;
        r14.<init>();
        r14 = r14.append(r11);
        r15 = " 23:59:59";
        r14 = r14.append(r15);
        r12 = r14.toString();
        r14 = getDb(r17);
        r15 = new java.lang.StringBuilder;
        r15.<init>();
        r16 = "select stopTime from t_act_item where ";
        r15 = r15.append(r16);
        r16 = getWhereUserId(r17);
        r15 = r15.append(r16);
        r16 = " and isDelete is not 1 and stopTime >= '";
        r15 = r15.append(r16);
        r15 = r15.append(r13);
        r16 = "' and stopTime < '";
        r15 = r15.append(r16);
        r15 = r15.append(r12);
        r16 = "' order by stopTime desc limit 1";
        r15 = r15.append(r16);
        r15 = r15.toString();
        r16 = 0;
        r8 = r14.rawQuery(r15, r16);
        r14 = r8.getCount();
        if (r14 <= 0) goto L_0x01be;
    L_0x01b0:
        r8.moveToNext();
        r14 = "stopTime";
        r14 = r8.getColumnIndex(r14);
        r10 = r8.getString(r14);
        r2 = r10;
    L_0x01be:
        close(r8);
        goto L_0x00df;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.record.utils.db.DbUtils.queryLastRecordStopTime(android.content.Context, java.lang.String):java.lang.String[]");
    }

    public static HashMap querySleepLastRecordByType(Context context, String type) {
        HashMap hashMap = null;
        Cursor cursor = getDb(context).rawQuery("SELECT * from t_act_item where " + getWhereUserId(context) + " and actType = " + type + " and isDelete is not 1  order by stopTime desc limit 1", null);
        if (cursor.getCount() > 0) {
            hashMap = new HashMap();
            while (cursor.moveToNext()) {
                hashMap.put("take", Integer.valueOf(getInt(cursor, "take")));
                hashMap.put("isRecord", Integer.valueOf(getInt(cursor, "isRecord")));
                hashMap.put("isUpload", Integer.valueOf(getInt(cursor, "isUpload")));
                hashMap.put("userId", getStr(cursor, "userId"));
                hashMap.put("actType", getStr(cursor, "actType"));
                hashMap.put("startTime", getStr(cursor, "startTime"));
                hashMap.put("stopTime", getStr(cursor, "stopTime"));
                hashMap.put("isEnd", Integer.valueOf(getInt(cursor, "isEnd")));
                hashMap.put("remarks", getStr(cursor, "remarks"));
            }
        }
        close(cursor);
        return hashMap;
    }

    public static void addLabelLink(Context context, int labelId, int itemsId) {
        if (labelId > 0) {
            String id = labelId + "";
            int take = 0;
            Cursor cursor = getDb(context).rawQuery("select take from t_act_item where id is " + itemsId, null);
            if (cursor.getCount() > 0) {
                cursor.moveToNext();
                take = cursor.getInt(cursor.getColumnIndex("take"));
            }
            close(cursor);
            if (take > 0) {
                Cursor cursor5 = getDb(context).rawQuery("select Id from t_routine_link where itemsId is " + itemsId + " and subTypeId is " + id, null);
                if (cursor5.getCount() > 0) {
                    close(cursor5);
                    return;
                }
                close(cursor5);
                ContentValues values = new ContentValues();
                values.put("userId", queryUserId(context));
                values.put("itemsId", Integer.valueOf(itemsId));
                values.put("subTypeId", id);
                values.put("take", Integer.valueOf(take));
                Cursor cursor33 = getDb(context).rawQuery("select * from t_act_item where id is " + itemsId, null);
                if (cursor33.getCount() > 0) {
                    cursor33.moveToNext();
                    values.put("goalId", Integer.valueOf(cursor33.getInt(cursor33.getColumnIndex("actId"))));
                    values.put("goalType", Integer.valueOf(cursor33.getInt(cursor33.getColumnIndex("actType"))));
                }
                close(cursor33);
                values.put("time", DateTime.getTimeString());
                getDb(context).insert("t_routine_link", null, values);
                values = new ContentValues();
                values.put("lastUseTime", DateTime.getTimeString());
                getDb(context).update("t_sub_type", values, " id is ? ", new String[]{id});
                Cursor cursor2 = getDb(context).rawQuery("select * from t_sub_type where id is " + id, null);
                if (cursor2.getCount() > 0) {
                    cursor2.moveToNext();
                    String LabelName = cursor2.getString(cursor2.getColumnIndex(IDemoChart.NAME));
                    String remarksLocal = "";
                    Cursor cursor3 = getDb(context).rawQuery("select remarks from t_act_item where id is " + itemsId, null);
                    if (cursor3.getCount() > 0) {
                        cursor3.moveToNext();
                        remarksLocal = cursor3.getString(cursor3.getColumnIndex("remarks"));
                    }
                    close(cursor3);
                    ContentValues values2 = new ContentValues();
                    if (remarksLocal != null) {
                        values2.put("remarks", remarksLocal + " [" + LabelName + "]");
                    } else {
                        values2.put("remarks", "[" + LabelName + "]");
                    }
                    getDb(context).update("t_act_item", values2, " id is ? ", new String[]{"" + itemsId});
                }
                close(cursor2);
            }
        }
    }

    public static void close(Cursor cursor) {
        if (cursor != null) {
            try {
                if (!cursor.isClosed()) {
                    cursor.close();
                }
            } catch (Exception e) {
                exceptionHandler(e);
            }
        }
    }

    public static ContentValues getErrorValues(Exception e) {
        return getErrorValues(e, "");
    }

    public static ContentValues getErrorValues(Exception e, String paramsStr) {
        e.printStackTrace();
        String errorString = GeneralHelper.getExceptionString(e);
        ContentValues values = new ContentValues();
        String str = "ErrorData";
        StringBuilder append = new StringBuilder().append(errorString);
        String str2 = (paramsStr == null || paramsStr.length() <= 0) ? "" : "\n\n" + paramsStr;
        values.put(str, append.append(str2).toString());
        values.put("CreateDate", DateTime.getTimeString());
        if (VERSION_NAME != null) {
            values.put("Version", VERSION_NAME);
        }
        return values;
    }

    public static void exceptionHandler(Exception e) {
        try {
            if (db != null) {
                db.insert("t_error_data", null, getErrorValues(e));
            } else {
                Log.e("override DbUtils", "db为空！");
            }
        } catch (Exception e1) {
            e1.printStackTrace();
        }
    }

    public static void exceptionHandler(Context context, Exception e) {
        try {
            getDb(context).insert("t_error_data", null, getErrorValues(e));
        } catch (Exception e1) {
            e1.printStackTrace();
        }
    }

    public static void exceptionHandler(Context context, Exception e, String paramsStr) {
        try {
            getDb(context).insert("t_error_data", null, getErrorValues(e, paramsStr));
        } catch (Exception e1) {
            e1.printStackTrace();
        }
    }

    public static void deleteSameError(Context context) {
        try {
            getDb(context).execSQL("delete from t_error_data where Id not in (select min(Id) from t_error_data group by ErrorData);");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static String getStr(Cursor cursor, String column) {
        return cursor.getString(cursor.getColumnIndex(column));
    }

    public static int getInt(Cursor cursor, String column) {
        return cursor.getInt(cursor.getColumnIndex(column));
    }

    public static double getDou(Cursor cursor, String column) {
        return cursor.getDouble(cursor.getColumnIndex(column));
    }

    public static void log(String str) {
        Log.i("override DbUtils", ":" + str);
    }
}
