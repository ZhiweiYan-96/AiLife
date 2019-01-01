package com.record.bean;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;

public class DateData {
    boolean isHadDeleteItems = false;
    private Comparator<Record> myComparator;
    private String recordDate;
    private ArrayList<Record> recordList;

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

    public DateData(String recordDate) {
        this.recordDate = recordDate;
        if (this.myComparator == null) {
            this.myComparator = new MyComparator();
        }
        if (this.recordList == null) {
            this.recordList = new ArrayList();
        }
    }

    public DateData(String recordDate, ArrayList<Record> recordList) {
        this.recordDate = recordDate;
        if (this.myComparator == null) {
            this.myComparator = new MyComparator();
        }
        this.recordList = recordList;
        Collections.sort(recordList, this.myComparator);
    }

    public void addOrDeleteOrResolveRecord(Record r, boolean isContinueState) {
        ArrayList<Record> tempList = getRecordList();
        if (tempList.size() != 0) {
            this.isHadDeleteItems = false;
            tempList = deleteRecord(tempList, r, isContinueState);
            if (!(r.getGoalId() == 0 || this.isHadDeleteItems)) {
                tempList = addRecord(tempList, r);
            }
            this.recordList = mergeRecord(tempList);
        } else if (r.getGoalId() != 0) {
            tempList.add(r);
        }
    }

    private ArrayList<Record> mergeRecord(ArrayList<Record> tempList) {
        ArrayList<Record> cloneList = (ArrayList) tempList.clone();
        for (int i = 0; i < tempList.size(); i++) {
            Record preR;
            Record nextR;
            if (i > 0) {
                preR = (Record) tempList.get(i - 1);
            } else {
                preR = (Record) tempList.get(0);
            }
            Record curRecord = (Record) tempList.get(i);
            if (i + 1 < tempList.size()) {
                nextR = (Record) tempList.get(i + 1);
            } else {
                nextR = (Record) tempList.get(i);
            }
            int itemsId;
            if (preR.getEnd() == curRecord.getBegin() && preR.getGoalId() == curRecord.getGoalId()) {
                if (preR.getItemsId() <= 0 || curRecord.getItemsId() <= 0) {
                    cloneList.remove(preR);
                    cloneList.remove(curRecord);
                    itemsId = preR.getItemsId();
                    if (itemsId == 0) {
                        itemsId = curRecord.getItemsId();
                    }
                    cloneList.add(new Record(preR.getBegin(), curRecord.getEnd(), curRecord.getGoalId(), curRecord.getColor(), itemsId));
                    Collections.sort(cloneList, this.myComparator);
                    return mergeRecord(cloneList);
                }
            } else if (curRecord.getEnd() == nextR.getBegin() && curRecord.getGoalId() == nextR.getGoalId() && (nextR.getItemsId() <= 0 || curRecord.getItemsId() <= 0)) {
                cloneList.remove(curRecord);
                cloneList.remove(nextR);
                itemsId = curRecord.getItemsId();
                if (itemsId == 0) {
                    itemsId = nextR.getItemsId();
                }
                cloneList.add(new Record(curRecord.getBegin(), nextR.getEnd(), curRecord.getGoalId(), curRecord.getColor(), itemsId));
                Collections.sort(cloneList, this.myComparator);
                return mergeRecord(cloneList);
            }
        }
        return cloneList;
    }

    private ArrayList<Record> addRecord(ArrayList<Record> tempList, Record r) {
        ArrayList<Record> cloneList = (ArrayList) tempList.clone();
        int begin = r.getBegin();
        int end = r.getEnd();
        boolean isContain = false;
        for (int i = 0; i < tempList.size(); i++) {
            Record curR = (Record) tempList.get(i);
            if (begin >= curR.getBegin() && end <= curR.getEnd()) {
                isContain = true;
                break;
            }
        }
        if (!isContain) {
            cloneList.add(r);
            Collections.sort(cloneList, this.myComparator);
        }
        return cloneList;
    }

    private ArrayList<Record> deleteRecord(ArrayList<Record> tempList, Record addRecord, boolean isContinueState) {
        ArrayList<Record> cloneList = (ArrayList) tempList.clone();
        int addRecordBegin = addRecord.getBegin();
        int addRecordEnd = addRecord.getEnd();
        boolean isCurrentRecordHadAdd = false;
        for (int i = 0; i < tempList.size(); i++) {
            Record curRecord = (Record) tempList.get(i);
            if (addRecord.getBegin() == curRecord.getBegin() && addRecord.getEnd() == curRecord.getEnd()) {
                this.isHadDeleteItems = true;
                cloneList.remove(curRecord);
                if (!(addRecord.getGoalId() == curRecord.getGoalId() || addRecord.getGoalId() == 0)) {
                    cloneList.add(addRecord);
                }
                Collections.sort(cloneList, this.myComparator);
                return cloneList;
            }
            if (addRecord.getBegin() <= curRecord.getBegin() && addRecord.getEnd() >= curRecord.getEnd()) {
                this.isHadDeleteItems = true;
                cloneList.remove(curRecord);
                if (isContinueState && addRecord.getGoalId() != 0 && !isCurrentRecordHadAdd) {
                    isCurrentRecordHadAdd = true;
                    cloneList.add(addRecord);
                } else if (!(addRecord.getGoalId() == 0 || isCurrentRecordHadAdd)) {
                    isCurrentRecordHadAdd = true;
                    if (curRecord.getItemsId() > 0 && addRecord.getGoalId() == curRecord.getGoalId()) {
                        addRecord.setItemsId(curRecord.getItemsId());
                    }
                    cloneList.add(addRecord);
                }
                Collections.sort(cloneList, this.myComparator);
            } else if (curRecord.getEnd() > addRecordBegin && curRecord.getEnd() <= addRecordEnd && addRecordEnd > curRecord.getEnd()) {
                this.isHadDeleteItems = true;
                cloneList.remove(curRecord);
                cloneList.add(new Record(curRecord.getBegin(), addRecordBegin, curRecord.getGoalId(), curRecord.getColor(), curRecord.getItemsId()));
                if (isContinueState && addRecord.getGoalId() != 0 && !isCurrentRecordHadAdd) {
                    isCurrentRecordHadAdd = true;
                    cloneList.add(addRecord);
                } else if (!(addRecord.getGoalId() == 0 || addRecord.getGoalId() == curRecord.getGoalId() || isCurrentRecordHadAdd)) {
                    isCurrentRecordHadAdd = true;
                    cloneList.add(addRecord);
                }
                Collections.sort(cloneList, this.myComparator);
            } else if (addRecordEnd > curRecord.getBegin() && addRecordEnd < curRecord.getEnd() && addRecordBegin < curRecord.getBegin()) {
                this.isHadDeleteItems = true;
                cloneList.remove(curRecord);
                cloneList.add(new Record(addRecordEnd, curRecord.getEnd(), curRecord.getGoalId(), curRecord.getColor(), curRecord.getItemsId()));
                if (isContinueState && addRecord.getGoalId() != 0 && !isCurrentRecordHadAdd) {
                    isCurrentRecordHadAdd = true;
                    cloneList.add(addRecord);
                } else if (!(addRecord.getGoalId() == 0 || addRecord.getGoalId() == curRecord.getGoalId() || isCurrentRecordHadAdd)) {
                    isCurrentRecordHadAdd = true;
                    cloneList.add(addRecord);
                }
                Collections.sort(cloneList, this.myComparator);
            } else if (addRecordBegin >= curRecord.getBegin() && addRecordEnd <= curRecord.getEnd()) {
                if (addRecordBegin > curRecord.getBegin() && addRecordEnd < curRecord.getEnd()) {
                    this.isHadDeleteItems = true;
                    cloneList.remove(curRecord);
                    if (!(addRecord.getGoalId() == 0 || addRecord.getGoalId() == curRecord.getGoalId())) {
                        cloneList.add(addRecord);
                    }
                    cloneList.add(new Record(curRecord.getBegin(), addRecordBegin, curRecord.getGoalId(), curRecord.getColor(), curRecord.getItemsId()));
                    cloneList.add(new Record(addRecordEnd, curRecord.getEnd(), curRecord.getGoalId(), curRecord.getColor()));
                    Collections.sort(cloneList, this.myComparator);
                    return cloneList;
                } else if (addRecordBegin == curRecord.getBegin() && addRecordEnd < curRecord.getEnd()) {
                    this.isHadDeleteItems = true;
                    cloneList.remove(curRecord);
                    if (!(addRecord.getGoalId() == 0 || addRecord.getGoalId() == curRecord.getGoalId())) {
                        cloneList.add(addRecord);
                    }
                    cloneList.add(new Record(addRecordEnd, curRecord.getEnd(), curRecord.getGoalId(), curRecord.getColor(), curRecord.getItemsId()));
                    Collections.sort(cloneList, this.myComparator);
                    return cloneList;
                } else if (addRecordBegin > curRecord.getBegin() && addRecordEnd == curRecord.getEnd()) {
                    this.isHadDeleteItems = true;
                    cloneList.remove(curRecord);
                    if (!(addRecord.getGoalId() == 0 || addRecord.getGoalId() == curRecord.getGoalId())) {
                        cloneList.add(addRecord);
                    }
                    cloneList.add(new Record(curRecord.getBegin(), addRecordBegin, curRecord.getGoalId(), curRecord.getColor(), curRecord.getItemsId()));
                    Collections.sort(cloneList, this.myComparator);
                    return cloneList;
                }
            }
        }
        return cloneList;
    }

    public ArrayList<Record> getRecordList() {
        return this.recordList;
    }

    public void setRecordList(ArrayList<Record> recordList) {
        this.recordList = recordList;
    }

    public String toString() {
        String str = "";
        if (this.recordList != null) {
            int i = 0;
            Iterator it = this.recordList.iterator();
            while (it.hasNext()) {
                str = str + i + "->" + ((Record) it.next()) + "\n";
                i++;
            }
        }
        return str;
    }
}
