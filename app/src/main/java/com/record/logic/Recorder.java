package com.record.logic;

import android.content.ContentValues;
import android.content.Context;
import com.record.thread.AllocationAndStaticsRunnable;
import com.record.utils.DateTime;
import com.record.utils.ToastUtils;
import com.record.utils.db.DbUtils;
import java.util.Iterator;
import java.util.TreeSet;

public class Recorder {
    public void addTimeSave(Context context, String addStartTime, String addEndTime, String checkActId, int labelId) {
        TreeSet<Integer> goalIdSet1 = DbUtils.updateDbActItem_ChangeEndTime(context, addStartTime, "");
        TreeSet<Integer> goalIdSet2 = DbUtils.updateDbActItem_ChangeStartTime(context, addEndTime, "");
        TreeSet<Integer> goalIdSet3 = DbUtils.deleteActItem_deleteRecords(context, addStartTime, addEndTime, "");
        ContentValues values = new ContentValues();
        values.put("userId", DbUtils.queryUserId(context));
        values.put("actId", checkActId);
        values.put("actType", DbUtils.queryActTypeById(context, checkActId));
        values.put("startTime", addStartTime);
        values.put("take", Integer.valueOf(DateTime.cal_secBetween(addStartTime, addEndTime)));
        values.put("stopTime", addEndTime);
        values.put("isEnd", Integer.valueOf(1));
        values.put("isRecord", Integer.valueOf(0));
        long itemsId = DbUtils.getDb(context).insert("t_act_item", null, values);
        ToastUtils.toastShort(context, "添加成功！");
        DbUtils.addLabelLink(context, labelId, (int) itemsId);
        String date1 = addStartTime.substring(0, addStartTime.indexOf(" "));
        String date2 = addEndTime.substring(0, addEndTime.indexOf(" "));
        TreeSet<String> dateArr = new TreeSet();
        dateArr.add(date1);
        dateArr.add(date2);
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
    }
}
