package com.record.utils;

import com.record.bean.DateData;
import com.record.bean.Record;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

public class RecordUtils {
    Map<String, DateData> date2RecordList;
    Comparator<Record> myComparator;

    class MyComparator implements Comparator<Record> {
        MyComparator() {
        }

        public int compare(Record lhs, Record rhs) {
            if (lhs.getBegin() > rhs.getBegin()) {
                return 1;
            }
            return -1;
        }
    }

    public RecordUtils() {
        if (this.date2RecordList == null) {
            this.date2RecordList = new HashMap();
        }
        if (this.myComparator == null) {
            this.myComparator = new MyComparator();
        }
    }

    public void addOrDeleteOrResolveRecord(String date, Record r, boolean isContinueState) {
        if (this.date2RecordList == null) {
            this.date2RecordList = new HashMap();
        }
        if (this.date2RecordList.size() == 0) {
        }
        DateData data = (DateData) this.date2RecordList.get(date);
        if (data == null) {
            data = new DateData(date);
        }
        data.addOrDeleteOrResolveRecord(r, isContinueState);
        this.date2RecordList.put(date, data);
    }

    public DateData getDateData(String date) {
        return (DateData) this.date2RecordList.get(date);
    }

    public void setDateData(String date, DateData data) {
        this.date2RecordList.put(date, data);
    }
}
