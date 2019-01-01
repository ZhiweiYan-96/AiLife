package com.record.thread;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.os.Build.VERSION;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.record.bean.IDemoChart;
import com.record.conts.Sofeware;
import com.record.myLife.R;
import com.record.service.AutoBackupService;
import com.record.service.TimerService;
import com.record.utils.DateTime;
import com.record.utils.Val;
import com.record.utils.db.DbBase;
import com.record.utils.db.DbUtils;
import com.record.utils.net.HttpRequestProxy;
import com.umeng.analytics.onlineconfig.a;
import java.io.File;
import java.util.HashMap;
import java.util.Iterator;

public class UploadRunnable extends DbBase implements Runnable {
    static String TAG = "override";
    static HashMap<Integer, Integer> actId2sGoalIdMap = null;
    private static Context context;
    private static Handler handler;
    public static boolean isContinueUpload = true;
    public static double uploadCount = 0.0d;
    static int uploadGoalInvestTime = 0;
    static int uploadItemsCount = 0;
    public static double uploadProgress = 0.0d;
    SendEmialRunnable emialRunnable = null;

    public UploadRunnable(Context context, Handler handler) {
        context = context;
        handler = handler;
        TAG = "override " + getClass().getSimpleName();
    }

    public void run() {
        try {
            uploadProgress = 0.0d;
            uploadCount = 0.0d;
            uploadItemsCount = 0;
            uploadGoalInvestTime = 0;
            isContinueUpload = true;
            String result = HttpRequestProxy.doPost(Sofeware.HTTP_BASE + Sofeware.HELLO, new HashMap());
            isServerDown(context, result);
            log("测试访问result:" + result);
            if (result != null) {
                try {
                    if (!result.equalsIgnoreCase("null") && JSON.parseObject(result).getIntValue("status") == 1) {
                        TimerService.updateCounter2Db(context);
                        getUploadCount(context);
                        if (isContinueUpload) {
                            uploadUserInfo(context, 0, true);
                            if (isContinueUpload) {
                                uploadUserInfo(context, -1, true);
                                if (isContinueUpload) {
                                    uploadGoals(context, 0, true);
                                    if (isContinueUpload) {
                                        uploadGoals(context, -1, true);
                                        if (isContinueUpload) {
                                            uploadItemOneByOne(context, 0, true);
                                            if (isContinueUpload) {
                                                uploadItemOneByOne(context, -1, true);
                                                if (isContinueUpload) {
                                                    if (uploadGoalInvestTime > 600) {
                                                        downloadStatics();
                                                        if (isContinueUpload) {
                                                            log("需要更新统计：" + uploadGoalInvestTime);
                                                        } else {
                                                            return;
                                                        }
                                                    }
                                                    uploadAllocation(context, 0, true);
                                                    if (isContinueUpload) {
                                                        uploadAllocation(context, -1, true);
                                                        if (isContinueUpload) {
                                                            uploadLabel(context, 0, true);
                                                            if (isContinueUpload) {
                                                                uploadLabel(context, -1, true);
                                                                if (isContinueUpload) {
                                                                    uploadLabelLink(context, 0, true);
                                                                    if (isContinueUpload) {
                                                                        uploadLabelLink(context, -1, true);
                                                                        if (isContinueUpload) {
                                                                            uploadDeleberateRecord(context, 0, true);
                                                                            if (isContinueUpload) {
                                                                                uploadDeleberateRecord(context, -1, true);
                                                                                if (isContinueUpload) {
                                                                                    uploadErrorData(true);
                                                                                    if (uploadItemsCount > 0) {
                                                                                        DbUtils.staticsGoalAll(context);
                                                                                        log("统计所有目标");
                                                                                        context.sendBroadcast(new Intent(Val.INTENT_ACTION_UPDATE_UI_GOAL));
                                                                                    }
                                                                                    sendUploadComplete(handler, context.getString(R.string.str_upload_complete));
                                                                                    isAutoBackup();
                                                                                    return;
                                                                                }
                                                                                return;
                                                                            }
                                                                            return;
                                                                        }
                                                                        return;
                                                                    }
                                                                    return;
                                                                }
                                                                return;
                                                            }
                                                            return;
                                                        }
                                                        return;
                                                    }
                                                    return;
                                                }
                                                return;
                                            }
                                            return;
                                        }
                                        return;
                                    }
                                    return;
                                }
                                return;
                            }
                            return;
                        }
                        return;
                    }
                } catch (Exception e) {
                    sendMsg(handler, context.getString(R.string.str_cant_connect_server));
                    DbUtils.exceptionHandler(e);
                    return;
                }
            }
            if (JSON.parseObject(result).getString("msg").length() > 0) {
                sendMsg(handler, JSON.parseObject(result).getString("msg"));
            } else {
                sendMsg(handler, context.getString(R.string.str_cant_connect_server));
            }
        } catch (Exception e2) {
            DbUtils.exceptionHandler(e2);
            sendUploadFailed(handler, context.getString(R.string.str_upload_failed));
        }
    }

    private void isAutoBackup() {
        if (new File(Environment.getExternalStorageDirectory() + File.separator + Val.SD_BACKUP_DIR, Val.SD_BACKUP_NAME).exists()) {
            context.startService(new Intent(context, AutoBackupService.class));
        }
    }

    public static double getUploadCount(Context context) {
        int count = (((0 + uploadUserInfo(context, 0, false)) + uploadUserInfo(context, -1, false)) + uploadGoals(context, 0, false)) + uploadGoals(context, -1, false);
        uploadItemsCount = uploadItemOneByOne(context, 0, false);
        uploadItemsCount += uploadItemOneByOne(context, -1, false);
        uploadCount = (double) ((((((((((count + uploadItemsCount) + uploadAllocation(context, 0, false)) + uploadAllocation(context, -1, false)) + uploadLabel(context, 0, false)) + uploadLabel(context, -1, false)) + uploadLabelLink(context, 0, false)) + uploadLabelLink(context, -1, false)) + uploadDeleberateRecord(context, 0, false)) + uploadDeleberateRecord(context, -1, false)) + uploadErrorData(false));
        return uploadCount;
    }

    private static int uploadUserInfo(Context context, int goalId, boolean isUpload) {
        try {
            log("开始上传用户信息，goalId：" + goalId);
            String tableName = "t_user";
            String where = DbUtils.getWhereUserIdNotTry(context).replace("userId", "id");
            String sql = "select * from " + tableName + " where " + where + " and isUpload is not 1 ";
            if (goalId == -1) {
                sql = "select * from " + tableName + " where " + where + " and endUpdateTime > uploadTime";
            }
            Cursor cursor = DbUtils.getDb(context).rawQuery(sql, null);
            if (cursor.getCount() <= 0) {
                log(tableName + "暂无数据上传！");
            } else if (isUpload) {
                while (cursor.moveToNext()) {
                    if (isUidExist(context, DbBase.getInt(cursor, "id"))) {
                        int id = DbBase.getInt(cursor, "id");
                        HashMap<String, Object> map = getUploadmap(context, id);
                        map.put("integral", Integer.valueOf(DbBase.getInt(cursor, "integral")));
                        map.put("nick", DbBase.getStr(cursor, "nickname"));
                        map.put("gender", Integer.valueOf(DbBase.getInt(cursor, "genderInt")));
                        map.put("birthday", DbBase.getStr(cursor, "birthday"));
                        map.put("profession", Integer.valueOf(DbBase.getInt(cursor, "professionInt")));
                        map.put("age", Integer.valueOf(DbBase.getInt(cursor, "age")));
                        map.put("phone", DbBase.getStr(cursor, "phone"));
                        map.put("qq", DbBase.getStr(cursor, "qq"));
                        String result = HttpRequestProxy.doPost(Sofeware.HTTP_BASE + Sofeware.UPLOAD_USER_INFO, map);
                        if (!isServerDown(context, result)) {
                            return 0;
                        }
                        JSONObject jsonObject = (JSONObject) JSON.parse(result);
                        log(jsonObject.toString());
                        if (jsonObject.getIntValue("status") == 1) {
                            uploadProgress += 1.0d;
                            if (uploadProgress % 2.0d == 0.0d) {
                                sendUploadProgress(handler);
                            }
                            ContentValues values = getUploadValues();
                            values.put("uploadTime", DateTime.getTimeString());
                            DbUtils.getDb(context).update(tableName, values, "id is " + id, null);
                            log(tableName + "上传成功! id: " + id + " ,values：" + values);
                        } else {
                            log("上传失败!");
                        }
                    } else {
                        log("Uid存在，不能上传！");
                    }
                }
                log(tableName + "用户信息上传完成");
            } else {
                int count = cursor.getCount();
                DbUtils.close(cursor);
                return count;
            }
            DbUtils.close(cursor);
        } catch (Exception e) {
            DbUtils.exceptionHandler(e);
        }
        return 0;
    }

    private static int uploadGoals(Context context, int goalId, boolean isUpload) {
        try {
            log("开始上传目标，goalId：" + goalId);
            String tableName = "t_act";
            String sql = "select * from " + tableName + " where " + DbUtils.getWhereUserIdNotTry(context) + " and isUpload is not 1  order by isSubGoal ";
            if (goalId > 0) {
                sql = "select * from " + tableName + " where " + DbUtils.getWhereUserIdNotTry(context) + " and id is " + goalId;
            } else if (goalId == -1) {
                sql = "select * from " + tableName + " where " + DbUtils.getWhereUserIdNotTry(context) + " and endUpdateTime > uploadTime  order by isSubGoal ";
            }
            Cursor cursor = DbUtils.getDb(context).rawQuery(sql, null);
            if (cursor.getCount() <= 0) {
                log(tableName + "暂无数据上传！");
            } else if (isUpload) {
                while (cursor.moveToNext()) {
                    if (isUidExist(context, DbBase.getInt(cursor, "userId"))) {
                        int id = DbBase.getInt(cursor, "id");
                        HashMap<String, Object> map = getUploadmap(context, DbBase.getInt(cursor, "userId"));
                        int isSubGaol = DbBase.getInt(cursor, "isSubGoal");
                        if (isSubGaol > 0) {
                            int serverId = DbUtils.querysGoalIdByActId(context, isSubGaol);
                            if (serverId == 0) {
                                serverId = uploadGoalOnlyOne(context, isSubGaol);
                                if (serverId > 0) {
                                    isSubGaol = serverId;
                                } else {
                                    log(isSubGaol + "其大目标上传失败！！");
                                }
                            } else {
                                isSubGaol = serverId;
                            }
                        }
                        map.put("isSubGoal", Integer.valueOf(isSubGaol));
                        map.put("sGoalId", Integer.valueOf(DbBase.getInt(cursor, "severId")));
                        map.put("image", DbBase.getStr(cursor, "image"));
                        map.put("color", DbBase.getStr(cursor, "color"));
                        map.put("goalName", DbBase.getStr(cursor, "actName"));
                        map.put(a.a, Integer.valueOf(DbBase.getInt(cursor, a.a)));
                        map.put("startTime", DbBase.getStr(cursor, "startTime"));
                        map.put("deadline", DbBase.getStr(cursor, "deadline"));
                        map.put("level", DbBase.getStr(cursor, "level"));
                        map.put("timeOfEveryday", DbBase.getStr(cursor, "timeOfEveryday"));
                        map.put("expectSpend", Integer.valueOf(DbBase.getInt(cursor, "expectSpend")));
                        map.put("hadSpend", Integer.valueOf(DbBase.getInt(cursor, "hadSpend")));
                        map.put("hadWaste", Integer.valueOf(DbBase.getInt(cursor, "hadWaste")));
                        map.put("isFinish", Integer.valueOf(DbBase.getInt(cursor, "isFinish")));
                        map.put("isDelete", Integer.valueOf(DbBase.getInt(cursor, "isDelete")));
                        map.put("finishTime", DbBase.getStr(cursor, "finishTime"));
                        map.put("deleteTime", DbBase.getStr(cursor, "deleteTime"));
                        map.put("intruction", DbBase.getStr(cursor, "intruction"));
                        map.put("position", Integer.valueOf(DbBase.getInt(cursor, "position")));
                        map.put("endUpdateTime", DbBase.getStr(cursor, "endUpdateTime"));
                        map.put("isManuscript", Integer.valueOf(DbBase.getInt(cursor, "isManuscript")));
                        map.put("isDefault", Integer.valueOf(DbBase.getInt(cursor, "isDefault")));
                        map.put("isHided", Integer.valueOf(DbBase.getInt(cursor, "isHided")));
                        map.put("resetCount", Integer.valueOf(DbBase.getInt(cursor, "resetCount")));
                        map.put("createTime", DbBase.getStr(cursor, "createTime"));
                        String result = HttpRequestProxy.doPost(Sofeware.HTTP_BASE + Sofeware.UPLOAD_GOALS, map);
                        if (!isServerDown(context, result)) {
                            return 0;
                        }
                        JSONObject jsonObject = (JSONObject) JSON.parse(result);
                        log(jsonObject.toString());
                        int status = jsonObject.getIntValue("status");
                        int sGoalId = jsonObject.getIntValue("sGoalId");
                        if (status != 1 || sGoalId <= 0) {
                            log("上传失败!");
                        } else {
                            uploadProgress += 1.0d;
                            if (uploadProgress % 2.0d == 0.0d) {
                                sendUploadProgress(handler);
                            }
                            ContentValues values = getUploadValues();
                            values.put("severId", Integer.valueOf(sGoalId));
                            values.put("uploadTime", DateTime.getTimeString());
                            DbUtils.getDb(context).update(tableName, values, "id is " + id, null);
                            log(tableName + "上传成功! id: " + id + " ,values：" + values);
                        }
                    } else {
                        log("Uid存在，不能上传！");
                    }
                }
                log(tableName + "目标上传完成");
            } else {
                int count = cursor.getCount();
                DbUtils.close(cursor);
                return count;
            }
            DbUtils.close(cursor);
        } catch (Exception e) {
            DbUtils.exceptionHandler(e);
        }
        return 0;
    }

    private static int uploadGoalOnlyOne(Context context, int goalId) {
        try {
            log("开始上传目标，goalId：" + goalId);
            String tableName = "t_act";
            Cursor cursor = DbUtils.getDb(context).rawQuery("select * from " + tableName + " where " + DbUtils.getWhereUserIdNotTry(context) + " and id is " + goalId, null);
            if (cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    if (isUidExist(context, DbBase.getInt(cursor, "userId"))) {
                        int id = DbBase.getInt(cursor, "id");
                        HashMap<String, Object> map = getUploadmap(context, DbBase.getInt(cursor, "userId"));
                        map.put("sGoalId", Integer.valueOf(DbBase.getInt(cursor, "severId")));
                        map.put("image", DbBase.getStr(cursor, "image"));
                        map.put("color", DbBase.getStr(cursor, "color"));
                        map.put("goalName", DbBase.getStr(cursor, "actName"));
                        map.put(a.a, Integer.valueOf(DbBase.getInt(cursor, a.a)));
                        map.put("startTime", DbBase.getStr(cursor, "startTime"));
                        map.put("deadline", DbBase.getStr(cursor, "deadline"));
                        map.put("level", DbBase.getStr(cursor, "level"));
                        map.put("timeOfEveryday", DbBase.getStr(cursor, "timeOfEveryday"));
                        map.put("expectSpend", Integer.valueOf(DbBase.getInt(cursor, "expectSpend")));
                        map.put("hadSpend", Integer.valueOf(DbBase.getInt(cursor, "hadSpend")));
                        map.put("hadWaste", Integer.valueOf(DbBase.getInt(cursor, "hadWaste")));
                        map.put("isFinish", Integer.valueOf(DbBase.getInt(cursor, "isFinish")));
                        map.put("isDelete", Integer.valueOf(DbBase.getInt(cursor, "isDelete")));
                        map.put("finishTime", DbBase.getStr(cursor, "finishTime"));
                        map.put("deleteTime", DbBase.getStr(cursor, "deleteTime"));
                        map.put("intruction", DbBase.getStr(cursor, "intruction"));
                        map.put("position", Integer.valueOf(DbBase.getInt(cursor, "position")));
                        map.put("endUpdateTime", DbBase.getStr(cursor, "endUpdateTime"));
                        map.put("isManuscript", Integer.valueOf(DbBase.getInt(cursor, "isManuscript")));
                        map.put("isDefault", Integer.valueOf(DbBase.getInt(cursor, "isDefault")));
                        map.put("isHided", Integer.valueOf(DbBase.getInt(cursor, "isHided")));
                        map.put("createTime", DbBase.getStr(cursor, "createTime"));
                        map.put("isSubGoal", Integer.valueOf(DbBase.getInt(cursor, "isSubGoal")));
                        JSONObject jsonObject = (JSONObject) JSON.parse(HttpRequestProxy.doPost(Sofeware.HTTP_BASE + Sofeware.UPLOAD_GOALS, map));
                        log("id:" + id + " " + jsonObject.toString());
                        int status = jsonObject.getIntValue("status");
                        int sGoalId = jsonObject.getIntValue("sGoalId");
                        if (status != 1 || sGoalId <= 0) {
                            log("上传失败!");
                        } else {
                            uploadProgress += 1.0d;
                            if (uploadProgress % 2.0d == 0.0d) {
                                sendUploadProgress(handler);
                            }
                            ContentValues values = getUploadValues();
                            values.put("severId", Integer.valueOf(sGoalId));
                            values.put("uploadTime", DateTime.getTimeString());
                            DbUtils.getDb(context).update(tableName, values, "id is " + id, null);
                            log(tableName + "上传成功! id: " + id + " ,values：" + values);
                            DbUtils.close(cursor);
                            return sGoalId;
                        }
                    }
                    log("Uid存在，不能上传！");
                }
                log(tableName + "目标上传完成");
            } else {
                log(tableName + "暂无数据上传！");
            }
            DbUtils.close(cursor);
        } catch (Exception e) {
            DbUtils.exceptionHandler(e);
        }
        return 0;
    }

    private static int uploadItemOneByOne(Context context, int itemsId, boolean isUpload) {
        try {
            String tableName = "t_act_item";
            log(tableName + "上传记录,itemsId" + itemsId);
            String sql = "select * from " + tableName + " where " + DbUtils.getWhereUserIdNotTry(context) + " and isUpload is not 1";
            if (itemsId > 0) {
                sql = "select * from " + tableName + " where " + DbUtils.getWhereUserIdNotTry(context) + " and id is " + itemsId;
            } else if (itemsId == -1) {
                sql = "select * from " + tableName + " where " + DbUtils.getWhereUserIdNotTry(context) + " and endUpdateTime > uploadTime";
            }
            Cursor cursor = DbUtils.getDb(context).rawQuery(sql, null);
            if (cursor.getCount() <= 0) {
                log("uploadItemOneByOne暂无数据上传！");
            } else if (isUpload) {
                while (cursor.moveToNext()) {
                    int userId = DbBase.getInt(cursor, "userId");
                    if (isUidExist(context, userId)) {
                        HashMap<String, Object> map = getUploadmap(context, userId);
                        int id = DbBase.getInt(cursor, "id");
                        int actId = DbBase.getInt(cursor, "actId");
                        int sGoalId = getGoalIdByActId(actId);
                        if (sGoalId > 0) {
                            map.put("sGoalItemId", Double.valueOf(DbBase.getDou(cursor, "sGoalItemId")));
                            map.put("sGoalId", Integer.valueOf(sGoalId));
                            int type = DbBase.getInt(cursor, "actType");
                            map.put("goalType", Integer.valueOf(type));
                            map.put("startTime", DbBase.getStr(cursor, "startTime"));
                            double take = DbBase.getDou(cursor, "take");
                            map.put("take", Double.valueOf(take));
                            map.put("stopTime", DbBase.getStr(cursor, "stopTime"));
                            map.put("isEnd", Integer.valueOf(DbBase.getInt(cursor, "isEnd")));
                            map.put("isRecord", Integer.valueOf(DbBase.getInt(cursor, "isRecord")));
                            map.put("remarks", DbBase.getStr(cursor, "remarks"));
                            map.put("isDelete", Integer.valueOf(DbBase.getInt(cursor, "isDelete")));
                            map.put("deleteTime", DbBase.getStr(cursor, "deleteTime"));
                            map.put("endUpdateTime", DbBase.getStr(cursor, "endUpdateTime"));
                            if (type == 11 || type == 10) {
                                uploadGoalInvestTime = (int) (((double) uploadGoalInvestTime) + take);
                            }
                            String result = HttpRequestProxy.doPost(Sofeware.HTTP_BASE + Sofeware.UPLOAD_ITEM_ONE_BY_ONE, map);
                            if (!isServerDown(context, result)) {
                                return 0;
                            }
                            JSONObject jsonObject = (JSONObject) JSON.parse(result);
                            log("id:" + id + " json返回：" + jsonObject.toString());
                            if (jsonObject.getIntValue("status") == 1) {
                                uploadProgress += 1.0d;
                                if (uploadProgress % 2.0d == 0.0d) {
                                    sendUploadProgress(handler);
                                }
                                String sGoalItemId = jsonObject.get("sGoalItemId") + "";
                                ContentValues values = getUploadValues();
                                values.put("sGoalItemId", sGoalItemId);
                                values.put("uploadTime", DateTime.getTimeString());
                                DbUtils.getDb(context).update(tableName, values, "id is " + id, null);
                                log(tableName + "上传成功!sGoalItemId：" + sGoalItemId + ",id:" + id);
                            } else {
                                log(id + "上传失败!" + result + "-------------------------");
                            }
                        } else {
                            log("目标id：" + actId + " 未上传，无法上传记录！");
                            uploadGoals(context, actId, true);
                        }
                    } else {
                        log("Uid不存在，不能上传！");
                        return 0;
                    }
                }
                log("uploadItemOneByOne上传记录完成");
            } else {
                int count = cursor.getCount();
                DbUtils.close(cursor);
                return count;
            }
            DbUtils.close(cursor);
        } catch (Exception e) {
            DbUtils.exceptionHandler(e);
        }
        return 0;
    }

    private static int uploadAllocation(Context context, int uploadType, boolean isUpload) {
        try {
            String sql = "select * from t_allocation where " + DbUtils.getWhereUserIdNotTry(context) + " and isUpload is not 1";
            if (uploadType == -1) {
                sql = "select * from t_allocation where " + DbUtils.getWhereUserIdNotTry(context) + " and endUpdateTime > uploadTime";
            }
            Cursor cursor = DbUtils.getDb(context).rawQuery(sql, null);
            if (cursor.getCount() <= 0) {
                log("uploadAllocation暂无数据上传！");
            } else if (isUpload) {
                while (cursor.moveToNext()) {
                    int userId = DbBase.getInt(cursor, "userId");
                    if (isUidExist(context, userId)) {
                        HashMap<String, Object> map = getUploadmap(context, userId);
                        int id = DbBase.getInt(cursor, "id");
                        map.put("invest", Integer.valueOf(DbBase.getInt(cursor, "invest")));
                        map.put("waste", Integer.valueOf(DbBase.getInt(cursor, "waste")));
                        map.put("routine", Integer.valueOf(DbBase.getInt(cursor, "routine")));
                        map.put("sleep", Integer.valueOf(DbBase.getInt(cursor, "sleep")));
                        map.put("remarks", DbBase.getStr(cursor, "remarks"));
                        map.put("time", DbBase.getStr(cursor, "time"));
                        map.put("morningVoice", DbBase.getStr(cursor, "morningVoice"));
                        map.put("earnMoney", Double.valueOf(DbBase.getDou(cursor, "earnMoney")));
                        map.put("endUpdateTime", DbBase.getStr(cursor, "endUpdateTime"));
                        String result = HttpRequestProxy.doPost(Sofeware.HTTP_BASE + Sofeware.UPLOAD_ALLOCATION, map);
                        if (!isServerDown(context, result)) {
                            return 0;
                        }
                        JSONObject jsonObject = (JSONObject) JSON.parse(result);
                        log("id:" + id + " " + jsonObject.toString());
                        if (jsonObject.getIntValue("status") == 1) {
                            uploadProgress += 1.0d;
                            if (uploadProgress % 2.0d == 0.0d) {
                                sendUploadProgress(handler);
                            }
                            ContentValues values = getUploadValues();
                            values.put("uploadTime", DateTime.getTimeString());
                            DbUtils.getDb(context).update("t_allocation", values, "id is " + id, null);
                            log(id + "上传成功! ");
                        } else {
                            log(id + "上传失败!" + result + "----------------------------");
                        }
                    } else {
                        log("Uid不存在，不能上传！");
                        return 0;
                    }
                }
                log("uploadAllocation上传记录完成");
            } else {
                int count = cursor.getCount();
                DbUtils.close(cursor);
                return count;
            }
            DbUtils.close(cursor);
        } catch (Exception e) {
            DbUtils.exceptionHandler(e);
        }
        return 0;
    }

    private static int uploadLabel(Context context, int subTypeId, boolean isUpload) {
        try {
            log("上传标签,subTypeId" + subTypeId);
            String tableName = "t_sub_Type";
            String sql = "select * from " + tableName + " where " + DbUtils.getWhereUserIdNotTry(context) + " and isUpload is not 1";
            if (subTypeId > 0) {
                sql = "select * from " + tableName + " where " + DbUtils.getWhereUserIdNotTry(context) + " and Id is " + subTypeId;
            } else if (subTypeId == -1) {
                sql = "select * from " + tableName + " where " + DbUtils.getWhereUserIdNotTry(context) + " and endUpdateTime > uploadTime";
            }
            Cursor cursor = DbUtils.getDb(context).rawQuery(sql, null);
            if (cursor.getCount() <= 0) {
                log("uploadLabel暂无数据上传！");
            } else if (isUpload) {
                while (cursor.moveToNext()) {
                    int userId = DbBase.getInt(cursor, "userId");
                    if (isUidExist(context, userId)) {
                        HashMap<String, Object> map = getUploadmap(context, userId);
                        int id = DbBase.getInt(cursor, "Id");
                        int actId = DbBase.getInt(cursor, "actId");
                        int sGoalId = 0;
                        if (actId > 0) {
                            sGoalId = getGoalIdByActId(actId);
                        }
                        map.put("sLabelId", Double.valueOf(DbBase.getDou(cursor, "sLabelId")));
                        map.put("goalType", Integer.valueOf(DbBase.getInt(cursor, "actType")));
                        map.put("goalId", Integer.valueOf(sGoalId));
                        map.put("labelType", Integer.valueOf(DbBase.getInt(cursor, "labelType")));
                        map.put(IDemoChart.NAME, DbBase.getStr(cursor, IDemoChart.NAME));
                        map.put("describe", DbBase.getStr(cursor, "describe"));
                        map.put("lastUseTime", DbBase.getStr(cursor, "lastUseTime"));
                        map.put("isDelete", Integer.valueOf(DbBase.getInt(cursor, "isDelete")));
                        map.put("createTime", DbBase.getStr(cursor, "time"));
                        map.put("labelColor", Integer.valueOf(DbBase.getInt(cursor, "labelColor")));
                        map.put("labelPosition", Integer.valueOf(DbBase.getInt(cursor, "labelPosition")));
                        map.put("deleteTime", DbBase.getStr(cursor, "deleteTime"));
                        map.put("endUpdateTime", DbBase.getStr(cursor, "endUpdateTime"));
                        String result = HttpRequestProxy.doPost(Sofeware.HTTP_BASE + Sofeware.UPLOAD_LABEL, map);
                        if (!isServerDown(context, result)) {
                            return 0;
                        }
                        JSONObject jsonObject = (JSONObject) JSON.parse(result);
                        log("id:" + id + " ," + jsonObject.toString());
                        int sLabelId = (int) jsonObject.getDoubleValue("sLabelId");
                        if (((int) jsonObject.getDoubleValue("status")) != 1 || sLabelId <= 0) {
                            log(id + "上传失败!" + result + "========================================");
                        } else {
                            uploadProgress += 1.0d;
                            if (uploadProgress % 2.0d == 0.0d) {
                                sendUploadProgress(handler);
                            }
                            ContentValues values = getUploadValues();
                            values.put("uploadTime", DateTime.getTimeString());
                            values.put("sLabelId", Integer.valueOf(sLabelId));
                            DbUtils.getDb(context).update(tableName, values, "Id is " + id, null);
                            log(id + "上传成功! ");
                        }
                    } else {
                        log("Uid不存在，不能上传！");
                        return -1;
                    }
                }
                log("uploadLabel上传记录完成");
            } else {
                int count = cursor.getCount();
                DbUtils.close(cursor);
                return count;
            }
            DbUtils.close(cursor);
        } catch (Exception e) {
            DbUtils.exceptionHandler(e);
        }
        return 0;
    }

    private static int uploadLabelLink(Context context, int uploadType, boolean isUpload) {
        try {
            String tableName = "t_routine_link";
            String sql = "select * from " + tableName + " where " + DbUtils.getWhereUserIdNotTry(context) + " and isUpload is not 1";
            if (uploadType == -1) {
                sql = "select * from " + tableName + " where " + DbUtils.getWhereUserIdNotTry(context) + " and endUpdateTime > uploadTime";
                log(tableName + "开始上传修改记录！");
            }
            Cursor cursor = DbUtils.getDb(context).rawQuery(sql, null);
            if (cursor.getCount() <= 0) {
                log("uploadLabelLink暂无数据上传！");
            } else if (isUpload) {
                while (cursor.moveToNext()) {
                    int userId = DbBase.getInt(cursor, "userId");
                    if (isUidExist(context, userId)) {
                        HashMap<String, Object> map = getUploadmap(context, userId);
                        int goalId = DbBase.getInt(cursor, "goalId");
                        int sGoalId = getGoalIdByActId(goalId);
                        if (sGoalId == 0 && goalId > 0) {
                            uploadGoals(context, goalId, true);
                        }
                        int itemsId = DbBase.getInt(cursor, "itemsId");
                        double sGoalItemsId = DbUtils.querySitemIdByItemsId(context, itemsId);
                        if (sGoalItemsId == 0.0d) {
                            uploadItemOneByOne(context, itemsId, true);
                        }
                        int subTypeId = DbBase.getInt(cursor, "subTypeId");
                        double sLabelId = DbUtils.querysLabelIdByItemsId(context, subTypeId);
                        if (sLabelId == 0.0d) {
                            uploadLabel(context, subTypeId, true);
                        }
                        if (sGoalItemsId == 0.0d || sGoalId == 0 || sLabelId == 0.0d) {
                            log("基础数据未上传，请上传后再试：sGoalItemsId:" + sGoalItemsId + ",sGoalId:" + sGoalId + ",sLabelId:" + sLabelId);
                        } else {
                            int id = DbBase.getInt(cursor, "Id");
                            map.put("sLabelLinkId", Double.valueOf(DbBase.getDou(cursor, "sLabelLinkId")));
                            map.put("sItemsId", Double.valueOf(sGoalItemsId));
                            map.put("sGoalId", Integer.valueOf(sGoalId));
                            map.put("goalType", Integer.valueOf(DbBase.getInt(cursor, "goalType")));
                            map.put("take", Integer.valueOf(DbBase.getInt(cursor, "take")));
                            map.put("sLabelId", Double.valueOf(sLabelId));
                            map.put("time", DbBase.getStr(cursor, "time"));
                            map.put("isDelete", Integer.valueOf(DbBase.getInt(cursor, "isDelete")));
                            map.put("deleteTime", DbBase.getStr(cursor, "deleteTime"));
                            map.put("endUpdateTime", DbBase.getStr(cursor, "endUpdateTime"));
                            String result = HttpRequestProxy.doPost(Sofeware.HTTP_BASE + Sofeware.UPLOAD_LABEL_LINK, map);
                            if (!isServerDown(context, result)) {
                                return 0;
                            }
                            JSONObject jsonObject = (JSONObject) JSON.parse(result);
                            log("id:" + id + " ," + jsonObject.toString());
                            int status = jsonObject.getIntValue("status");
                            String sLabelLinkId = jsonObject.getString("sLabelLinkId");
                            if (sLabelLinkId != null) {
                                sLabelLinkId.replace(",", "");
                            }
                            if (status != 1 || Double.parseDouble(sLabelLinkId) <= 0.0d) {
                                log(id + "上传失败!" + result + "------------------------------");
                            } else {
                                uploadProgress += 1.0d;
                                if (uploadProgress % 2.0d == 0.0d) {
                                    sendUploadProgress(handler);
                                }
                                ContentValues values = getUploadValues();
                                values.put("uploadTime", DateTime.getTimeString());
                                values.put("sLabelLinkId", sLabelLinkId);
                                DbUtils.getDb(context).update(tableName, values, "Id is " + id, null);
                                log(id + "上传成功!ContentValues： " + values);
                            }
                        }
                    } else {
                        log("Uid不存在，不能上传！");
                        return -1;
                    }
                }
                log("uploadLabelLink上传记录完成");
            } else {
                int count = cursor.getCount();
                DbUtils.close(cursor);
                return count;
            }
            DbUtils.close(cursor);
        } catch (Exception e) {
            DbUtils.exceptionHandler(e);
        }
        return 0;
    }

    private static int uploadDeleberateRecord(Context context, int uploadType, boolean isUpload) {
        String methodName = new Object() {
        }.getClass().getEnclosingMethod().getName();
        try {
            String tableName = "t_deliberate_record";
            String server_column = "sDeliberateRecordId";
            String sql = "select * from " + tableName + " where " + DbUtils.getWhereUserIdNotTry(context) + " and isUpload is not 1";
            if (uploadType == -1) {
                sql = "select * from " + tableName + " where " + DbUtils.getWhereUserIdNotTry(context) + " and endUpdateTime > uploadTime";
                log(tableName + "开始上传修改记录！");
            }
            Cursor cursor = DbUtils.getDb(context).rawQuery(sql, null);
            if (cursor.getCount() <= 0) {
                log(methodName + "暂无数据上传！");
            } else if (isUpload) {
                while (cursor.moveToNext()) {
                    int userId = DbBase.getInt(cursor, "userId");
                    if (isUidExist(context, userId)) {
                        HashMap<String, Object> map = getUploadmap(context, userId);
                        int itemsId = DbBase.getInt(cursor, "itemsId");
                        double sGoalItemsId = DbUtils.querySitemIdByItemsId(context, itemsId);
                        if (sGoalItemsId == 0.0d) {
                            uploadItemOneByOne(context, itemsId, true);
                            sGoalItemsId = DbUtils.querySitemIdByItemsId(context, itemsId);
                        }
                        if (sGoalItemsId == 0.0d) {
                            log("基础数据未上传，请上传后再试：sGoalItemsId:" + sGoalItemsId);
                        } else {
                            int id = DbBase.getInt(cursor, "Id");
                            map.put("sItemsId", Double.valueOf(sGoalItemsId));
                            map.put("keyVal1", DbBase.getStr(cursor, "keyVal1"));
                            map.put("keyVal2", DbBase.getStr(cursor, "keyVal2"));
                            map.put("keyVal3", DbBase.getStr(cursor, "keyVal3"));
                            map.put("keyVal4", DbBase.getStr(cursor, "keyVal4"));
                            map.put("totalVal", DbBase.getStr(cursor, "totalVal"));
                            map.put(server_column, DbBase.getStr(cursor, server_column));
                            map.put("createTime", DbBase.getStr(cursor, "createTime"));
                            map.put("endUpdateTime", DbBase.getStr(cursor, "endUpdateTime"));
                            map.put("isDelete", Integer.valueOf(DbBase.getInt(cursor, "isDelete")));
                            map.put("deleteTime", DbBase.getStr(cursor, "deleteTime"));
                            String result = HttpRequestProxy.doPost(Sofeware.HTTP_BASE + Sofeware.UPLOAD_DELIBERATE_RECORD, map);
                            if (!isServerDown(context, result)) {
                                return 0;
                            }
                            JSONObject jsonObject = (JSONObject) JSON.parse(result);
                            log("id:" + id + " ," + jsonObject.toString());
                            int status = jsonObject.getIntValue("status");
                            String sLabelLinkId = jsonObject.getString(server_column);
                            if (sLabelLinkId != null) {
                                sLabelLinkId.replace(",", "");
                            }
                            if (status != 1 || Double.parseDouble(sLabelLinkId) <= 0.0d) {
                                log(id + "上传失败!" + result + "------------------------------");
                            } else {
                                uploadProgress += 1.0d;
                                if (uploadProgress % 2.0d == 0.0d) {
                                    sendUploadProgress(handler);
                                }
                                ContentValues values = getUploadValues();
                                values.put("uploadTime", DateTime.getTimeString());
                                values.put(server_column, sLabelLinkId);
                                DbUtils.getDb(context).update(tableName, values, "Id is " + id, null);
                                log(id + "上传成功!ContentValues： " + values);
                            }
                        }
                    } else {
                        log("Uid不存在，不能上传！");
                        return -1;
                    }
                }
                log(methodName + "上传记录完成");
            } else {
                int count = cursor.getCount();
                DbUtils.close(cursor);
                return count;
            }
            DbUtils.close(cursor);
        } catch (Exception e) {
            DbUtils.exceptionHandler(e);
        }
        return 0;
    }

    private static int uploadErrorData(boolean isUpload) {
        String userName = DbUtils.queryUserName(context);
        DbUtils.deleteSameError(context);
        String tableName = "t_error_data";
        Cursor cursor = DbUtils.getDb(context).rawQuery("select * from " + tableName + " where IsUpload is not 1 order by CreateDate desc", null);
        if (cursor.getCount() > 0) {
            int i = 0;
            if (isUpload) {
                while (cursor.moveToNext()) {
                    String Id = cursor.getString(cursor.getColumnIndex("Id"));
                    String ErrorData = cursor.getString(cursor.getColumnIndex("ErrorData"));
                    String CreateDate = cursor.getString(cursor.getColumnIndex("CreateDate"));
                    String Version = cursor.getString(cursor.getColumnIndex("Version"));
                    HashMap<String, Object> map = new HashMap();
                    map.put("errorData", ErrorData);
                    map.put("createTime", CreateDate);
                    map.put("version", Version);
                    map.put("brand", Build.PRODUCT);
                    map.put("model", userName);
                    map.put("sdk", VERSION.SDK);
                    map.put("release", VERSION.RELEASE);
                    String result = HttpRequestProxy.doPost(Sofeware.HTTP_BASE + Sofeware.UPLOAD_ERROR_DATA, map);
                    if (result == null) {
                        break;
                    }
                    int status = JSON.parseObject(result).getIntValue("status");
                    uploadProgress += 1.0d;
                    if (uploadProgress % 2.0d == 0.0d) {
                        sendUploadProgress(handler);
                    }
                    if (status == 1) {
                        DbUtils.getDb(context).delete(tableName, "Id = " + Id, null);
                    }
                    i++;
                    if (i > 50) {
                        break;
                    }
                }
            }
            int count = cursor.getCount();
            DbUtils.close(cursor);
            return count;
        }
        return 0;
    }

    public void downloadStatics() {
        String tableName = "t_goal_statics";
        JSONObject jsonObject = (JSONObject) JSON.parse(HttpRequestProxy.doPost(Sofeware.HTTP_BASE + Sofeware.DOWNLOAD_STATICS, getUploadmap(context, DbUtils.queryUserId2(context))));
        if (jsonObject != null) {
            downLoadStaticsFromArray(tableName, context, jsonObject.getJSONArray("goalStatics"));
            updataUserStatics(jsonObject.getJSONObject("userStatics"));
            try {
                JSONArray GoalSubid = jsonObject.getJSONArray("GoalSubid");
                if (GoalSubid != null) {
                    downloadBigToSubGoalFromArray("t_server_bigtosubgoal", context, GoalSubid);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void updataUserStatics(JSONObject userStatics) {
        if (userStatics != null) {
            try {
                double investment = userStatics.getDouble("investment").doubleValue();
                double property = userStatics.getDouble("property").doubleValue();
                ContentValues values = new ContentValues();
                values.put("investment", Double.valueOf(investment));
                values.put("property", Double.valueOf(property));
                DbUtils.getDb(context).update("t_user", values, "id = " + DbUtils.queryUserId(context), null);
                log("更新用户统计成功！");
            } catch (Exception e) {
                DbUtils.exceptionHandler(e);
            }
        }
    }

    public static void downLoadStaticsFromArray(String tableName, Context context, JSONArray staticsArr) {
        if (staticsArr != null) {
            Iterator it = staticsArr.iterator();
            while (it.hasNext()) {
                JSONObject subJson = JSON.parseObject(it.next().toString());
                if (subJson != null) {
                    int goalId = subJson.getIntValue("goalId");
                    int staticsType = subJson.getIntValue("staticsType");
                    if (staticsType == 0) {
                        staticsType = Val.STATISTICS_TYPE_SERVER;
                    } else if (staticsType == Val.STATISTICS_TYPE_SUB_FINISH_IN_SERVER) {
                        staticsType = Val.STATISTICS_TYPE_SUB_FINISH_IN_SERVER;
                    }
                    int actId = DbUtils.queryActIdBysGoalId(context, goalId + "");
                    if (actId > 0) {
                        if (DbUtils.queryStaticsByGoalId(context, actId, staticsType) > 0) {
                            DbUtils.getDb(context).update(tableName, getStaticsContentValue(context, subJson), " userId = ? and goalId = ? and staticsType = ?", new String[]{DbUtils.queryUserId(context), actId + "", "" + staticsType});
                            log(tableName + "进行更新，当前类型：" + staticsType + ",actId:" + actId);
                        } else {
                            ContentValues values = getStaticsContentValue(context, subJson);
                            values.put("userId", DbUtils.queryUserId(context));
                            values.put("goalId", Integer.valueOf(actId));
                            values.put("staticsType", Integer.valueOf(staticsType));
                            DbUtils.getDb(context).insert(tableName, null, values);
                            log(tableName + "开始插入，当前类型：" + staticsType + ",actId:" + actId);
                        }
                    }
                }
            }
        }
    }

    public static void downloadBigToSubGoalFromArray(String tableName, Context context, JSONArray staticsArr) {
        if (staticsArr != null) {
            Iterator it = staticsArr.iterator();
            while (it.hasNext()) {
                JSONObject subJson = JSON.parseObject(it.next().toString());
                if (subJson != null) {
                    int sGoalId = subJson.getIntValue("goalId");
                    String subFinishedIdArr = subJson.getString("subFinishedId");
                    if (subFinishedIdArr != null && subFinishedIdArr.length() > 0) {
                        int isHad = DbUtils.queryBigToSubgoalBysGoalId(context, sGoalId);
                        ContentValues values = new ContentValues();
                        values.put("userId", DbUtils.queryUserId(context));
                        values.put("sGoalId", Integer.valueOf(sGoalId));
                        values.put("sSubFinishedGoalId", subFinishedIdArr);
                        if (isHad > 0) {
                            DbUtils.getDb(context).update(tableName, values, " userId = ? and sGoalId = ? ", new String[]{DbUtils.queryUserId(context), sGoalId + ""});
                        } else {
                            DbUtils.getDb(context).insert(tableName, null, values);
                        }
                    }
                }
            }
        }
    }

    private static ContentValues getStaticsContentValue(Context context, JSONObject subJson) {
        ContentValues value = new ContentValues();
        value.put("hadInvest", subJson.getDouble("hadInvest"));
        value.put("todayInvest", subJson.getDouble("todayInvest"));
        value.put("sevenInvest", subJson.getDouble("sevenInvest"));
        return value;
    }

    private static int getGoalIdByActId(int actId) {
        int severId;
        if (actId2sGoalIdMap == null || actId2sGoalIdMap.size() <= 0) {
            actId2sGoalIdMap = new HashMap();
        } else {
            try {
                severId = ((Integer) actId2sGoalIdMap.get(Integer.valueOf(actId))).intValue();
                if (severId > 0) {
                    return severId;
                }
            } catch (Exception e) {
            }
        }
        severId = DbUtils.querysGoalIdByActId(context, actId);
        if (severId <= 0) {
            return severId;
        }
        actId2sGoalIdMap.put(Integer.valueOf(actId), Integer.valueOf(severId));
        return severId;
    }

    public static HashMap<String, Object> getUploadmap(Context context, int id) {
        HashMap<String, Object> map = new HashMap();
        map.put("token", Sofeware.getToken());
        map.put("sUserId", Long.valueOf(DbUtils.queryUserUidByUserId(context, id)));
        map.put("userName", DbUtils.queryUserNameByUserId(context, id));
        return map;
    }

    public static ContentValues getUploadValues() {
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

    public static boolean isServerDown(Context context, String result) {
        if (result == null) {
            isContinueUpload = false;
            sendUploadFailed(handler, context.getString(R.string.str_cant_connect_server));
        }
        return isContinueUpload;
    }

    public static void sendMsg(Handler handler, String str) {
        if (handler != null && str != null) {
            Message msg = new Message();
            msg.arg1 = 100;
            msg.obj = str;
            handler.sendMessage(msg);
        }
    }

    public static void sendUploadComplete(Handler handler, String str) {
        if (handler != null && str != null) {
            Message msg = new Message();
            msg.arg1 = 101;
            msg.obj = str;
            handler.sendMessage(msg);
        }
    }

    public static void sendUploadFailed(Handler handler, String str) {
        if (handler != null && str != null) {
            Message msg = new Message();
            msg.arg1 = 102;
            msg.obj = str;
            handler.sendMessage(msg);
        }
    }

    public static void sendUploadProgress(Handler handler) {
        if (handler != null) {
            Message msg = new Message();
            msg.arg1 = 103;
            int progress = (int) ((uploadProgress / uploadCount) * 100.0d);
            if (progress > 100) {
                progress = 100;
            }
            msg.obj = progress + "%";
            handler.sendMessage(msg);
        }
    }

    private static void log(String str) {
        Log.i(TAG, ":" + str);
    }
}
