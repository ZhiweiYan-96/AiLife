package com.record.utils;

import android.content.Context;
import android.database.Cursor;
import com.record.bean.User;
import com.record.utils.db.DbUtils;

public class Sql {
    public static String MainInitGoalSql = ("select * from t_act where userId is " + User.getInstance().getUserId() + " and isDelete is not 1 and isFinish is not 1 and isManuscript is not 1 order by position");

    public static String cal_TodayAllocat(Context context) {
        return "select * from t_allocation where " + DbUtils.getWhereUserId(context) + " order by time desc";
    }

    public static Cursor queryAndUpdateDb_Allocation_v2(Context context, String Date) {
        return DbUtils.getDb(context).rawQuery("select * from t_act_item where userId is ? and isDelete is not 1 and startTime >= '" + Date + " 00:00:00' and startTime <= '" + Date + " 23:59:59'  order by startTime", new String[]{User.getInstance().getUserId() + ""});
    }

    public static Cursor queryAndUpdateDb_Allocation_v2_2(Context context, String Date) {
        return DbUtils.getDb(context).rawQuery("select * from t_act_item where " + DbUtils.getWhereUserId(context) + " and isDelete is not 1 and startTime <= '" + Date + " 00:00:00' and stopTime >= '" + Date + " 23:59:59' order by startTime desc", null);
    }

    public static String widgetGoalsList(Context context) {
        return "select * from t_act where userId is " + User.getInstance().getUserId() + " and isDelete is not 1 and isFinish is not 1 and isManuscript is not 1 and type is not 10 and isHided is not 1 order by position";
    }

    public static String goalGetGoalsList(Context context) {
        return "select * from t_act where userId is " + User.getInstance().getUserId() + " and isDelete is not 1 and isFinish is not 1 and isManuscript is not 1 and type is not 10 and isHided is not 1 order by position";
    }

    public static String GoalsList_initUi(Context context, String where) {
        return "select * from t_act where " + DbUtils.getWhereUserId(context) + " and isManuscript is not 1 and type is 11 " + where + " ORDER BY position ";
    }

    public static String GoalsList(Context context) {
        return "select * from t_act where " + DbUtils.getWhereUserId(context) + " and isManuscript is not 1  and isFinish is not 1 and isDelete is not 1 and type is not 10 order by position";
    }

    public static String getGoalsNotSub(Context context) {
        return "Select * from (Select * from t_act where " + DbUtils.getWhereUserId(context) + " and isDelete is not 1 and isFinish is not 1 and isManuscript is not 1 and   Type is 11) where isSubGoal isnull or isSubGoal is 0 order by position";
    }

    public static String getGoalsNotSub(Context context, int id) {
        return "Select * from (Select * from t_act where " + DbUtils.getWhereUserId(context) + " and isDelete is not 1 and isFinish is not 1 and isManuscript is not 1 and   Type is 11 and id is not " + id + " ) where isSubGoal isnull or isSubGoal is 0 order by position";
    }

    public static String goalGetBigGoalsNotSub(Context context) {
        return "Select * from (Select * from t_act where " + DbUtils.getWhereUserId(context) + " and isDelete is not 1 and isFinish is not 1 and isManuscript is not 1 and type = 11) where isSubGoal isnull or isSubGoal is 0 order by position";
    }

    public static String getBigGoalsWithOtherType(Context context) {
        return "Select * from (Select * from t_act where " + DbUtils.getWhereUserId(context) + " and isDelete is not 1 and isFinish is not 1 and isManuscript is not 1 and type is not 10 ) where isSubGoal isnull or isSubGoal is 0 order by position";
    }

    public static String getBigGoalsWithOtherType2(Context context) {
        return "Select * from (Select * from t_act where " + DbUtils.getWhereUserId(context) + " and isDelete is not 1 and isFinish is not 1 and isManuscript is not 1 ) where isSubGoal isnull or isSubGoal is 0 ORDER BY position";
    }

    public static String getSubGoals(Context context, String id) {
        return "Select * from t_act where " + DbUtils.getWhereUserId(context) + " and isDelete is not 1 and isFinish is not 1 and isManuscript is not 1 and isSubGoal is " + id + " ORDER BY position ";
    }

    public static String getManuscriptGoal(Context context) {
        return "select * from t_act where isManuscript is 1 and " + DbUtils.getWhereUserId(context) + " and isDelete is not 1 order by id desc";
    }

    public static String getAllGoalsNotManuscript(Context context) {
        return "select * from t_act where " + DbUtils.getWhereUserId(context) + " and isManuscript is not 1 order by position";
    }

    public static String getGoalById(Context context, int id) {
        return "select * from t_act where id is " + id;
    }

    public static String goalGetSubGoalById(Context context, int id) {
        return "Select * from t_act where " + DbUtils.getWhereUserId(context) + " and isDelete is not 1 and isFinish is not 1 and isManuscript is not 1 and isSubGoal is " + id;
    }

    public static String goalGetGoal_id(Context context) {
        return "Select id from t_act where " + DbUtils.getWhereUserId(context) + " and isDelete is not 1 and isFinish is not 1 and isManuscript is not 1";
    }

    public static String goalGetSubGoalById_id(Context context, int id) {
        return "Select id from t_act where " + DbUtils.getWhereUserId(context) + " and isDelete is not 1 and isFinish is not 1 and isManuscript is not 1 and isSubGoal is " + id;
    }

    public static String goalGetSubGoalByIdContainDelte_id(Context context, int id) {
        return "Select id from t_act where " + DbUtils.getWhereUserId(context) + " and isManuscript is not 1 and isSubGoal is " + id;
    }

    public static String goalGetSubGoalByIdFinish(Context context, int id) {
        return "Select id from t_act where " + DbUtils.getWhereUserId(context) + " and isManuscript is not 1 and isFinish = 1 and isDelete is not 1 and isSubGoal is " + id;
    }

    public static String goalGetSubGoalByIdContainDelteWithoutFinish_id(Context context, int id) {
        return "Select id from t_act where " + DbUtils.getWhereUserId(context) + " and isManuscript is not 1 and isFinish is not 1 and isSubGoal is " + id;
    }

    public static String goalGetBigGoalBySubId(Context context, int id) {
        return "Select isSubGoal from t_act where " + DbUtils.getWhereUserId(context) + " and isManuscript is not 1  and id is " + id;
    }

    public static String staticsGetStaticsByGoalIdAndType(Context context, int goalId, int staticsType) {
        return "Select * from t_goal_statics where " + DbUtils.getWhereUserId(context) + " and goalId is " + goalId + " and staticsType is " + staticsType;
    }

    public static String staticsGetStaticsByGoalIdAndType_Id(Context context, int goalId, int staticsType) {
        return "Select Id from t_goal_statics where " + DbUtils.getWhereUserId(context) + " and goalId = " + goalId + " and staticsType = " + staticsType;
    }

    public static String staticsQueryMaxTypeStatics(Context context, int goalId) {
        return "select * from t_goal_statics where " + DbUtils.getWhereUserId(context) + " and staticsType is (select Max(staticsType) from t_goal_statics where goalId is " + goalId + ") and goalId is " + goalId;
    }

    public static String staticsQueryHadInvestByGoalId(Context context, int goalId) {
        return "select hadInvest from t_goal_statics where " + DbUtils.getWhereUserId(context) + " and staticsType is (select Max(staticsType) from t_goal_statics where goalId is " + goalId + ") and goalId is " + goalId;
    }

    public static String goalGetGoalListForUiShown_containHide(Context context, String where) {
        return "select * from t_act where " + DbUtils.getWhereUserId(context) + " and isManuscript is not 1 and type is 11 " + where + " ORDER BY position";
    }

    public static String actItemIsContainOthers(Context context, String addStartTime, String addEndTime) {
        return "select Id from t_act_item where " + DbUtils.getWhereUserId(context) + "  and isDelete is not 1   and  isEnd is 1 and startTime >= '" + addStartTime + "' and stopTime <= '" + addEndTime + "' ";
    }

    public static String actItemIsContainOthers(Context context, String addStartTime, String addEndTime, String except) {
        return "select Id from t_act_item where " + DbUtils.getWhereUserId(context) + "  and isDelete is not 1   and  isEnd is 1 and startTime >= '" + addStartTime + "' and stopTime <= '" + addEndTime + "' " + except;
    }

    public static String actItemIsStartTimeOverrideOthers(Context context, String addStartTime) {
        return "select Id from t_act_item where " + DbUtils.getWhereUserId(context) + "  and isDelete is not 1    and startTime < '" + addStartTime + "' and stopTime > '" + addStartTime + "' limit 1";
    }

    public static String actItemIsStartTimeOverrideOthers(Context context, String addStartTime, String except) {
        return "select Id from t_act_item where " + DbUtils.getWhereUserId(context) + "  and isDelete is not 1    and startTime < '" + addStartTime + "' and stopTime > '" + addStartTime + "' " + except + " limit 1";
    }

    public static String method(Context context) {
        return "";
    }
}
