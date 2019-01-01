package com.record.utils.db.service;

import android.content.Context;
import android.database.Cursor;
import com.record.myLife.R;
import com.record.utils.db.DbBase;
import com.record.utils.db.DbUtils;
import com.umeng.analytics.onlineconfig.a;
import java.util.HashMap;

public class ActTypeService extends BaseDbService {
    public static HashMap<Integer, String> getActTypeMap(Context context) {
        HashMap<Integer, String> map = new HashMap();
        map.put(Integer.valueOf(10), context.getResources().getString(R.string.str_invest));
        map.put(Integer.valueOf(11), context.getResources().getString(R.string.str_invest));
        map.put(Integer.valueOf(20), context.getResources().getString(R.string.str_routine));
        map.put(Integer.valueOf(21), context.getResources().getString(R.string.str_routine));
        map.put(Integer.valueOf(30), context.getResources().getString(R.string.str_sleep));
        map.put(Integer.valueOf(31), context.getResources().getString(R.string.str_sleep));
        map.put(Integer.valueOf(40), context.getResources().getString(R.string.str_waste));
        map.put(Integer.valueOf(41), context.getResources().getString(R.string.str_waste));
        Cursor cursor = DbUtils.getDb(context).rawQuery("Select * from t_act where " + DbUtils.getWhereUserId(context) + " AND type != 11 AND isDelete = 0", null);
        if (cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                int type = DbBase.getInt(cursor, a.a);
                map.put(Integer.valueOf(type), DbBase.getStr(cursor, "actName"));
            }
        }
        DbUtils.close(cursor);
        return map;
    }
}
