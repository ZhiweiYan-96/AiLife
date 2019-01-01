package com.record.myLife.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import com.record.bean.DateData;
import com.record.bean.Record;
import com.record.myLife.R;
import java.util.ArrayList;

public class TimeLineView extends FrameLayout {
    private static final int DAY_RANGE = 86400;
    static String TAG = "override";
    public static final int TYPE_SCALE_DAY = 1;
    public static final int TYPE_SCALE_MINUTE = 2;
    private Context context;
    private DateData dateData;
    private boolean isShowScale = true;
    private int parentHeight;
    private int scale = 6;
    private int scaleType = 1;
    private int selectHour = 0;

    public TimeLineView(Context context) {
        super(context);
        this.context = context;
        TAG += getClass().getSimpleName();
    }

    public TimeLineView(Context context, boolean isShowScale) {
        super(context);
        this.context = context;
        this.isShowScale = isShowScale;
        TAG += getClass().getSimpleName();
    }

    public TimeLineView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        TAG += getClass().getSimpleName();
    }

    public void setData(DateData dateData, int type, int selectHour) {
        this.scaleType = type;
        this.selectHour = selectHour;
        setData(dateData);
    }

    public void setData(DateData dateData) {
        if (dateData != null) {
            this.dateData = dateData;
            if (this.scaleType == 1) {
                LinearLayout tem_timeline_vertical_itemslayout = (LinearLayout) findViewById(R.id.tem_timeline_vertical_itemslayout);
                tem_timeline_vertical_itemslayout.removeAllViews();
                ArrayList<Record> tempList = dateData.getRecordList();
                if (tempList != null) {
                    for (int i = 0; i < tempList.size(); i++) {
                        Record preR;
                        if (i > 0) {
                            preR = (Record) tempList.get(i - 1);
                        } else {
                            preR = (Record) tempList.get(0);
                        }
                        Record curR = (Record) tempList.get(i);
                        if (i == 0 && curR.getBegin() != 0) {
                            tem_timeline_vertical_itemslayout.addView(generateView(0, curR.getBegin()));
                        }
                        if (curR.getBegin() != preR.getEnd() && curR.getBegin() > preR.getEnd()) {
                            tem_timeline_vertical_itemslayout.addView(generateView(0, curR.getBegin() - preR.getEnd()));
                        }
                        tem_timeline_vertical_itemslayout.addView(generateView(curR.getColor(), curR.getRanage()));
                    }
                    return;
                }
                return;
            }
            getMinLayout();
        }
    }

    private void getMinLayout() {
        int secondStart = this.selectHour * 3600;
        int secondEnd = (this.selectHour + 1) * 3600;
        ArrayList<Record> tempList = this.dateData.getRecordList();
        if (tempList != null) {
            LinearLayout tem_timeline_vertical_itemslayout = (LinearLayout) findViewById(R.id.tem_timeline_vertical_itemslayout);
            tem_timeline_vertical_itemslayout.removeAllViews();
            int temp = 0;
            int i = 0;
            while (i < tempList.size()) {
                Record preR;
                if (i > 0) {
                    preR = (Record) tempList.get(i - 1);
                } else {
                    preR = (Record) tempList.get(0);
                }
                Record curR = (Record) tempList.get(i);
                if (curR.getBegin() > secondStart && curR.getBegin() < secondEnd && temp == 0) {
                    temp = 1;
                    tem_timeline_vertical_itemslayout.addView(generateMinuteView(0, curR.getBegin() - secondStart));
                }
                if (curR.getBegin() != preR.getEnd() && curR.getBegin() > preR.getEnd() && secondStart < preR.getEnd() && secondEnd > curR.getBegin()) {
                    temp = 1;
                    tem_timeline_vertical_itemslayout.addView(generateMinuteView(0, curR.getBegin() - preR.getEnd()));
                }
                if (curR.getBegin() <= secondStart && curR.getEnd() >= secondEnd) {
                    tem_timeline_vertical_itemslayout.addView(generateMinuteView(curR.getColor(), 3600));
                    return;
                } else if (preR.getEnd() > secondStart || curR.getBegin() < secondEnd) {
                    if (curR.getBegin() < secondStart && curR.getEnd() > secondStart) {
                        temp = 1;
                        tem_timeline_vertical_itemslayout.addView(generateMinuteView(curR.getColor(), curR.getEnd() - secondStart));
                    } else if (curR.getBegin() < secondEnd && curR.getEnd() > secondEnd) {
                        temp = 1;
                        tem_timeline_vertical_itemslayout.addView(generateMinuteView(curR.getColor(), secondEnd - curR.getBegin()));
                    } else if (secondStart <= curR.getBegin() && secondEnd >= curR.getEnd()) {
                        temp = 1;
                        tem_timeline_vertical_itemslayout.addView(generateMinuteView(curR.getColor(), curR.getRanage()));
                    }
                    i++;
                } else {
                    tem_timeline_vertical_itemslayout.addView(generateMinuteView(0, 3600));
                    return;
                }
            }
        }
    }

    private View generateMinuteView(int color, int range) {
        View v = new View(this.context);
        if (color != 0) {
            v.setBackgroundColor(getResources().getColor(color));
        }
        v.setLayoutParams(new LayoutParams(-1, (int) ((((double) range) / 3600.0d) * ((double) this.parentHeight))));
        return v;
    }

    private View generateView(int color, int range) {
        View v = new View(this.context);
        if (color != 0) {
            v.setBackgroundColor(getResources().getColor(color));
        }
        v.setLayoutParams(new LayoutParams(-1, (int) ((((double) range) / 86400.0d) * ((double) this.parentHeight))));
        return v;
    }

    private void updateScaleText() {
        if (this.isShowScale) {
            LinearLayout tem_timeline_vertical_overlaytime = (LinearLayout) findViewById(R.id.tem_timeline_vertical_overlaytime);
            int height = (int) (((double) this.parentHeight) / ((double) this.scale));
            int length = 24 / this.scale;
            for (int i = 0; i < this.scale; i++) {
                int scaleValue = i * length;
                TextView tv = new TextView(this.context);
                tv.setGravity(1);
                tv.setText(scaleValue + "");
                tv.setTextColor(-16777216);
                tv.setLayoutParams(new LayoutParams(-1, height));
                tem_timeline_vertical_overlaytime.addView(tv);
            }
        }
    }

    public void setTimelineHeight(int height) {
        this.parentHeight = height;
        log("高为：" + height);
        removeAllViews();
        addView(inflate(getContext(), R.layout.tem_timeline_vertical, null));
        updateScaleText();
        setData(this.dateData);
    }

    public static void log(String str) {
        Log.i(TAG, ":" + str);
    }
}
