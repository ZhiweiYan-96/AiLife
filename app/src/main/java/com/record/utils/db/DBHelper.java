package com.record.utils.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {
    private static int version = 49;

    public DBHelper(Context context, String name, CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public DBHelper(Context context, String name) {
        this(context, name, null, version);
    }

    private void createTableUser(SQLiteDatabase db) {
        db.execSQL(((((((((((((((((((((("create table t_user(id integer primary key autoincrement not null," + "userName varchar(10),") + "password varchar(10),") + "email varchar(10),") + "nickname varchar(10),") + "uid integer,") + "integral integer,") + "isLogin integer,") + "genderInt integer,") + "professionInt integer,") + "qq varchar(20),") + "birthday varchar(20),") + "investment double,") + "property double,") + "endUpdateTime varchar(20),") + "uploadTime varchar(20),") + "loginTime varchar(20),") + "profession varchar(10),") + "gender varchar(10),") + "age integer,") + "phone varchar(20),") + "isUpload integer") + ");");
    }

    private void createTableAct(SQLiteDatabase db) {
        db.execSQL((((((((((((((((((((((((((((("create table t_act(id integer primary key autoincrement not null," + "userId varchar(10),") + "image varchar(10),") + "color varchar(10),") + "actName varchar(10),") + "type integer,") + "startTime varchar(20),") + "deadline varchar(20),") + "level varchar(20),") + "timeOfEveryday integer,") + "expectSpend integer,") + "hadSpend integer,") + "hadWaste integer,") + "isFinish integer,") + "isDelete integer,") + "finishTime varchar(20),") + "deleteTime varchar(20),") + "intruction varchar(20),") + "position integer,") + "endUpdateTime varchar(20),") + "severId integer,") + "isSubGoal integer,") + "isManuscript integer,") + "isDefault integer,") + "isUpload integer,") + "isHided integer,") + "resetCount integer,") + "uploadTime varchar(20),") + "createTime varchar(20)") + ");");
    }

    private void createTableActItem(SQLiteDatabase db) {
        db.execSQL(((((((((((((((("create table t_act_item(id integer primary key autoincrement not null," + "userId varchar(10),") + "actId integer,") + "actType integer,") + "startTime varchar(10),") + "take integer,") + "stopTime varchar(10),") + "isEnd varchar(10),") + "isRecord integer,") + "remarks varchar(200),") + "isUpload integer,") + "sGoalItemId double,") + "isDelete integer,") + "deleteTime varchar(20),") + "endUpdateTime varchar(20),") + "uploadTime varchar(20)") + ");");
    }

    private void createTableAllocationItem(SQLiteDatabase db) {
        db.execSQL((((((((((((("create table t_allocation(id integer primary key autoincrement not null," + "userId varchar(10),") + "invest integer,") + "waste integer,") + "routine integer,") + "sleep integer,") + "time varchar(20),") + "remarks varchar(20),") + "morningVoice varchar(20),") + "earnMoney double,") + "endUpdateTime varchar(20),") + "uploadTime varchar(20),") + "isUpload integer") + ");");
    }

    private void createTableWeibo(SQLiteDatabase db) {
        db.execSQL((((((("create table t_weibo (Id integer primary key autoincrement not null," + "userId integer,") + "sendtype integer,") + "weiboType integer,") + "content varchar(20),") + "time varchar(50),") + "isUpload integer") + ");");
    }

    private void createTableError(SQLiteDatabase db) {
        db.execSQL((((("create table t_error_data (Id integer primary key autoincrement not null," + "ErrorData varchar(200),") + "CreateDate varchar(50),") + "Version varchar(20),") + "IsUpload integer") + ");");
    }

    private void createTableStatistic(SQLiteDatabase db) {
        db.execSQL(((((("create table t_statistic (Id integer primary key autoincrement not null," + "userId integer,") + "actId integer,") + "value integer,") + "date varchar(20),") + "isUpload integer") + ");");
    }

    private void createSubType(SQLiteDatabase db) {
        db.execSQL((((((((((((((((("create table t_sub_type (Id integer primary key autoincrement not null," + "userId integer,") + "actType integer,") + "actId integer,") + "labelType integer,") + "name varchar(20),") + "describe varchar(20),") + "lastUseTime varchar(20),") + "isDelete integer,") + "time varchar(20),") + "labelColor integer,") + "labelPosition integer,") + "sLabelId double,") + "deleteTime varchar(20),") + "endUpdateTime varchar(20),") + "uploadTime varchar(20),") + "isUpload integer") + ");");
    }

    private void createRemind(SQLiteDatabase db) {
        db.execSQL(((((((((((("create table t_remind (Id integer primary key autoincrement not null," + "userId integer,") + "remindType integer,") + "date varchar(20),") + "startTime varchar(20),") + "noteTime varchar(20),") + "delay integer,") + "isCancel integer,") + "isFinish integer,") + "isMiss integer,") + "time varchar(20),") + "isUpload integer") + ");");
    }

    private void createRoutineLink(SQLiteDatabase db) {
        db.execSQL(((((((((((((("create table t_routine_link (Id integer primary key autoincrement not null," + "userId integer,") + "itemsId integer,") + "goalId integer,") + "goalType integer,") + "take integer,") + "subTypeId integer,") + "time varchar(20),") + "sLabelLinkId double,") + "isDelete integer,") + "deleteTime varchar(20),") + "endUpdateTime varchar(20),") + "uploadTime varchar(20),") + "isUpload integer") + ");");
    }

    private void createActLink(SQLiteDatabase db) {
        db.execSQL(((((("create table t_act_link (Id integer primary key autoincrement not null," + "userId integer,") + "bigGoalId integer,") + "subGoalId integer,") + "createTime varchar(20),") + "isUpload integer") + ");");
    }

    private void createGoalStatics(SQLiteDatabase db) {
        db.execSQL(((((((((((((("create table t_goal_statics (Id integer primary key autoincrement not null," + "userId integer,") + "goalId integer,") + "goalName varchar(20),") + "goalType integer,") + "staticsType integer,") + "expectInvest double,") + "hadInvest double,") + "todayInvest double,") + "sevenInvest double,") + "createTime String,") + "startTime String,") + "deadline String,") + "isUpload integer") + ");");
    }

    private void createFeedBack(SQLiteDatabase db) {
        db.execSQL((((((("create table t_feed_back (Id integer primary key autoincrement not null," + "userId integer,") + "content varchar(50),") + "contact varchar(20),") + "sendTime varchar(20),") + "isSended integer,") + "isUpload integer") + ");");
    }

    private void createServerSubId(SQLiteDatabase db) {
        db.execSQL((((("create table t_server_bigtosubgoal (Id integer primary key autoincrement not null," + "userId integer,") + "sGoalId integer,") + "sSubFinishedGoalId varchar(100),") + "isUpload integer") + ");");
    }

    private void createPrompt(SQLiteDatabase db) {
        db.execSQL(((("create table t_prompt (Id integer primary key autoincrement not null," + "sId integer,") + "content varchar(100),") + "isUpload integer") + ");");
    }

    private void createUnHandlerTomato(SQLiteDatabase db) {
        db.execSQL(((((("create table t_unhandler_tomato (Id integer primary key autoincrement not null," + "startTime varchar(30),") + "length double,") + "type integer,") + "sId integer,") + "isUpload integer") + ");");
    }

    private void createScreenLog(SQLiteDatabase db) {
        db.execSQL(((((("create table t_screen_log (Id integer primary key autoincrement not null," + "saveTime varchar(30),") + "type integer,") + "take integer,") + "iscalc integer,") + "isUpload integer") + ");");
    }

    private void createDeliberateRecord(SQLiteDatabase db) {
        db.execSQL((((((((((((((("create table t_deliberate_record (Id integer primary key autoincrement not null," + "userId integer,") + "itemsId integer,") + "keyVal1 double,") + "keyVal2 double,") + "keyVal3 double,") + "keyVal4  double,") + "totalVal  double,") + "sDeliberateRecordId  double,") + "createTime varchar(20),") + "endUpdateTime varchar(20),") + "deleteTime varchar(20),") + "isDelete integer,") + "uploadTime varchar(20),") + "isUpload integer") + ");");
    }

    public void onCreate(SQLiteDatabase db) {
        createTableUser(db);
        createTableActItem(db);
        createTableAct(db);
        createTableAllocationItem(db);
        createTableError(db);
        createTableWeibo(db);
        createTableStatistic(db);
        createSubType(db);
        createRemind(db);
        createRoutineLink(db);
        createActLink(db);
        createFeedBack(db);
        createGoalStatics(db);
        createServerSubId(db);
        createPrompt(db);
        createUnHandlerTomato(db);
        createScreenLog(db);
        createDeliberateRecord(db);
        if (version == 0) {
            upgrade(db);
        }
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        upgrade(db);
    }

    private void upgrade(SQLiteDatabase db) {
        if (isTableExist(db, "t_user") == 0) {
            createTableUser(db);
        } else {
            addTableColumn(db, "t_user", new String[]{"uid", "uploadTime", "endUpdateTime", "age", "phone", "qq", "birthday", "professionInt", "genderInt", "loginTime", "investment", "property"}, new String[]{"integer", "varchar(20)", "varchar(20)", "integer", "varchar(20)", "varchar(20)", "varchar(20)", "integer", "integer", "varchar(20)", "double", "double"});
        }
        String tableName = "t_act";
        if (isTableExist(db, tableName) == 0) {
            createTableAct(db);
        } else {
            addTableColumn(db, tableName, new String[]{"isHided", "isFinish", "isDefault", "isDelete", "isSubGoal", "isManuscript", "finishTime", "deleteTime", "endUpdateTime", "severId", "uploadTime", "createTime", "resetCount"}, new String[]{"integer", "integer", "integer", "integer", "integer", "integer", "varchar(20)", "varchar(20)", "varchar(20)", "integer", "varchar(20)", "varchar(20)", "integer"});
        }
        tableName = "t_act_item";
        if (isTableExist(db, tableName) == 0) {
            createTableActItem(db);
        } else {
            addTableColumn(db, tableName, new String[]{"isRecord", "actType", "isDelete", "sGoalItemId", "deleteTime", "endUpdateTime", "uploadTime"}, new String[]{"integer", "integer", "integer", "double", "varchar(20)", "varchar(20)", "varchar(20)"});
        }
        tableName = "t_allocation";
        if (isTableExist(db, tableName) == 0) {
            createTableAllocationItem(db);
        } else {
            addTableColumn(db, tableName, new String[]{"morningVoice", "earnMoney", "endUpdateTime", "uploadTime"}, new String[]{"varchar(20)", "double", "varchar(20)", "varchar(20)"});
        }
        if (isTableExist(db, "t_weibo") == 0) {
            createTableWeibo(db);
        }
        tableName = "t_error_data";
        if (isTableExist(db, tableName) == 0) {
            createTableError(db);
        } else {
            addTableColumn(db, tableName, new String[]{"Version"}, new String[]{"varchar(20)"});
        }
        if (isTableExist(db, "t_statistic") == 0) {
            createTableStatistic(db);
        }
        tableName = "t_sub_type";
        if (isTableExist(db, tableName) == 0) {
            createSubType(db);
        } else {
            addTableColumn(db, tableName, new String[]{"labelType", "lastUseTime", "labelPosition", "labelColor", "sLabelId", "deleteTime", "endUpdateTime", "uploadTime"}, new String[]{"integer", "varchar(20)", "integer", "integer", "double", "varchar(20)", "varchar(20)", "varchar(20)"});
        }
        if (isTableExist(db, "t_remind") == 0) {
            createRemind(db);
        }
        tableName = "t_routine_link";
        if (isTableExist(db, tableName) == 0) {
            createRoutineLink(db);
        } else {
            addTableColumn(db, tableName, new String[]{"goalId", "goalType", "sLabelLinkId", "isDelete", "deleteTime", "endUpdateTime", "uploadTime"}, new String[]{"integer", "integer", "double", "integer", "varchar(20)", "varchar(20)", "varchar(20)"});
        }
        if (isTableExist(db, "t_act_link") == 0) {
            createActLink(db);
        }
        if (isTableExist(db, "t_feed_back") == 0) {
            createFeedBack(db);
        }
        if (isTableExist(db, "t_goal_statics") == 0) {
            createGoalStatics(db);
        }
        if (isTableExist(db, "t_server_bigtosubgoal") == 0) {
            createServerSubId(db);
        }
        if (isTableExist(db, "t_prompt") == 0) {
            createPrompt(db);
        }
        if (isTableExist(db, "t_unhandler_tomato") == 0) {
            createUnHandlerTomato(db);
        }
        tableName = "t_screen_log";
        if (isTableExist(db, tableName) == 0) {
            createScreenLog(db);
        } else {
            addTableColumn(db, tableName, new String[]{"take", "iscalc"}, new String[]{"integer", "integer"});
        }
        if (isTableExist(db, "t_deliberate_record") == 0) {
            createDeliberateRecord(db);
        }
    }

    private boolean addTableColumn(SQLiteDatabase db, String tableName, String[] columnNames, String[] columnPropertys) {
        SQLiteDatabase sQLiteDatabase = db;
        Cursor cursor = sQLiteDatabase.query("sqlite_master", new String[]{"sql"}, "tbl_name=?", new String[]{tableName}, null, null, null);
        String sql = "";
        cursor.moveToFirst();
        if (!cursor.isAfterLast()) {
            sql = cursor.getString(0).toString();
        }
        String execString = "";
        if (columnNames == null || columnNames.length == 0) {
            return false;
        }
        db.beginTransaction();
        for (int i = 0; i < columnNames.length; i++) {
            execString = "ALTER TABLE " + tableName + " ADD " + columnNames[i] + " " + columnPropertys[i] + " ";
            if (!sql.contains(columnNames[i])) {
                db.execSQL(execString);
            }
        }
        DbUtils.close(cursor);
        db.setTransactionSuccessful();
        db.endTransaction();
        return true;
    }

    private int isTableExist(SQLiteDatabase db, String tableName) {
        int tableCount = 0;
        SQLiteDatabase sQLiteDatabase = db;
        Cursor cursor = sQLiteDatabase.query("sqlite_master", new String[]{"COUNT(*) AS tableCount"}, "type=? and name=?", new String[]{"table", tableName}, null, null, null);
        while (cursor.moveToNext()) {
            tableCount = cursor.getInt(cursor.getColumnIndex("tableCount"));
        }
        return tableCount;
    }
}
