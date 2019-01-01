package com.record.myLife.main;

import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.qq.e.comm.constants.ErrorCode.InitError;
import com.record.myLife.R;
import com.record.myLife.base.BaseActivity;
import com.record.myLife.view.EditActivity;
import com.record.myLife.view.dialog.AlertDialogM.Builder;
import com.record.service.SystemBarTintManager;
import com.record.utils.DateTime;
import com.record.utils.Val;
import com.record.utils.db.DbUtils;
import com.record.view.wheel.widget.NumericWheelAdapter;
import com.record.view.wheel.widget.OnWheelChangedListener;
import com.record.view.wheel.widget.WheelView;
import com.umeng.analytics.MobclickAgent;
import java.util.Calendar;
import java.util.HashMap;

public class UserInfoActivity extends BaseActivity {
    public static String COLUMN_NICKNAME = "nickname";
    public static String COLUMN_QQ = "qq";
    static String TAG = "override";
    int GENDER_MAN = 1;
    int GENDER_OTHER = 3;
    int GENDER_WOMAN = 2;
    int PROFESSION_FREE_OCCUPTION = 3;
    int PROFESSION_OFFIC_WORKER = 2;
    int PROFESSION_OTHER = 5;
    int PROFESSION_STUDENT = 1;
    int PROFESSION_WAIT_JOB = 4;
    OnClickListener addTimeOnClickListener = new OnClickListener() {
        public void onClick(View v) {
            int id = v.getId();
            if (id == R.id.btn_tem_time_cancel) {
                UserInfoActivity.this.popup.dismiss();
            } else if (id == R.id.btn_tem_time_save) {
                try {
                    Calendar c = Calendar.getInstance();
                    c.set(1, UserInfoActivity.this.year_select);
                    c.set(2, UserInfoActivity.this.month_select - 1);
                    c.set(5, UserInfoActivity.this.day_select);
                    String date = DateTime.formatDate(c);
                    ContentValues values = new ContentValues();
                    values.put("birthday", date);
                    values.put("endUpdateTime", DateTime.getTimeString());
                    DbUtils.getDb(UserInfoActivity.this.context).update("t_user", values, "id = " + DbUtils.queryUserId(UserInfoActivity.this.context), null);
                    UserInfoActivity.this.initUI();
                } catch (Exception e) {
                    DbUtils.exceptionHandler(e);
                }
                UserInfoActivity.this.popup.dismiss();
            }
        }
    };
    HashMap<Integer, Integer> arr2dayMap = new HashMap();
    HashMap<Integer, Integer> arr2monthMap = new HashMap();
    HashMap<Integer, Integer> arr2yearMap = new HashMap();
    Button btn_label_back;
    Button btn_userinfo_occupation;
    Button btn_userinfo_qq;
    private OnWheelChangedListener changedListener1 = new OnWheelChangedListener() {
        public void onChanged(WheelView wheel, int oldValue, int newValue) {
            UserInfoActivity.this.year_select = ((Integer) UserInfoActivity.this.arr2yearMap.get(Integer.valueOf(newValue))).intValue();
            UserInfoActivity.log("OnWheelChangedListener 选择年:" + UserInfoActivity.this.year_select);
            Calendar c = Calendar.getInstance();
            c.set(UserInfoActivity.this.year_select, UserInfoActivity.this.month_select - 1, 1);
            int actualMax = c.getActualMaximum(5);
            UserInfoActivity.log("OnWheelChangedListener 选择的月:" + UserInfoActivity.this.month_select + ",year_select:" + UserInfoActivity.this.year_select + ",最大值是：" + c.getActualMaximum(5));
            UserInfoActivity.this.wv_tem_day.setAdapter(new NumericWheelAdapter(1, c.getActualMaximum(5), "%02d"));
            if (UserInfoActivity.this.day_select > actualMax) {
                UserInfoActivity.this.wv_tem_day.scroll(((Integer) UserInfoActivity.this.day2arrMap.get(Integer.valueOf(actualMax))).intValue(), InitError.INIT_AD_ERROR);
            }
        }
    };
    private OnWheelChangedListener changedListener2 = new OnWheelChangedListener() {
        public void onChanged(WheelView wheel, int oldValue, int newValue) {
            UserInfoActivity.this.month_select = ((Integer) UserInfoActivity.this.arr2monthMap.get(Integer.valueOf(newValue))).intValue();
            Calendar c = Calendar.getInstance();
            c.set(UserInfoActivity.this.year_select, UserInfoActivity.this.month_select - 1, 1);
            int actualMax = c.getActualMaximum(5);
            UserInfoActivity.log("OnWheelChangedListener 选择的月:" + UserInfoActivity.this.month_select + ",年:" + UserInfoActivity.this.year_select + ",天数最大值是：" + c.getActualMaximum(5));
            UserInfoActivity.this.wv_tem_day.setAdapter(new NumericWheelAdapter(1, c.getActualMaximum(5), "%02d"));
            if (UserInfoActivity.this.day_select > actualMax) {
                UserInfoActivity.this.wv_tem_day.scroll(((Integer) UserInfoActivity.this.day2arrMap.get(Integer.valueOf(actualMax))).intValue(), InitError.INIT_AD_ERROR);
            }
        }
    };
    private OnWheelChangedListener changedListener3 = new OnWheelChangedListener() {
        public void onChanged(WheelView wheel, int oldValue, int newValue) {
            UserInfoActivity.this.day_select = newValue + 1;
            UserInfoActivity.log("OnWheelChangedListener 选择的日:" + UserInfoActivity.this.day_select);
        }
    };
    Context context;
    HashMap<Integer, Integer> day2arrMap = new HashMap();
    int day_select = 1;
    String[] genderArr = null;
    HashMap<Integer, Integer> month2arrMap = new HashMap();
    int month_select = 1;
    OnClickListener myClickListener = new OnClickListener() {
        public void onClick(View v) {
            int id = v.getId();
            if (id == R.id.btn_label_back) {
                UserInfoActivity.this.onBackPressed();
            } else if (id == R.id.rl_userinfo_nickName) {
                UserInfoActivity.this.clickNickName();
                UserInfoActivity.this.overridePendingTransition(R.anim.push_to_left_in, R.anim.push_to_left_out);
            } else if (id == R.id.btn_userinfo_qq) {
                UserInfoActivity.this.clickQQ();
                UserInfoActivity.this.overridePendingTransition(R.anim.push_to_left_in, R.anim.push_to_left_out);
            } else if (id == R.id.rl_userinfo_birthday) {
                UserInfoActivity.this.showPopDialog();
            } else if (id == R.id.rl_userinfo_gender) {
                UserInfoActivity.this.showGenderSelectDialog();
            } else if (id == R.id.btn_userinfo_occupation) {
                UserInfoActivity.this.showOccupationDialog();
            }
        }
    };
    String[] occupationArr = null;
    PopupWindow popup;
    RelativeLayout rl_userinfo_autograph;
    RelativeLayout rl_userinfo_birthday;
    RelativeLayout rl_userinfo_gender;
    RelativeLayout rl_userinfo_nickName;
    String temp = "";
    TextView tv_userinfo_autograph;
    TextView tv_userinfo_birthday;
    TextView tv_userinfo_gender;
    TextView tv_userinfo_nickname;
    TextView tv_userinfo_occupation;
    TextView tv_userinfo_qq;
    WheelView wv_tem_day;
    HashMap<Integer, Integer> year2arrMap = new HashMap();
    int year_select = 1;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);
        this.context = this;
        TAG += getClass().getSimpleName();
        SystemBarTintManager.setMIUIbar(this);
        this.tv_userinfo_nickname = (TextView) findViewById(R.id.tv_userinfo_nickname);
        this.tv_userinfo_birthday = (TextView) findViewById(R.id.tv_userinfo_birthday);
        this.tv_userinfo_gender = (TextView) findViewById(R.id.tv_userinfo_gender);
        this.tv_userinfo_occupation = (TextView) findViewById(R.id.tv_userinfo_occupation);
        this.tv_userinfo_qq = (TextView) findViewById(R.id.tv_userinfo_qq);
        this.tv_userinfo_occupation = (TextView) findViewById(R.id.tv_userinfo_occupation);
        this.tv_userinfo_autograph = (TextView) findViewById(R.id.tv_userinfo_autograph);
        this.rl_userinfo_nickName = (RelativeLayout) findViewById(R.id.rl_userinfo_nickName);
        this.rl_userinfo_birthday = (RelativeLayout) findViewById(R.id.rl_userinfo_birthday);
        this.rl_userinfo_gender = (RelativeLayout) findViewById(R.id.rl_userinfo_gender);
        this.rl_userinfo_autograph = (RelativeLayout) findViewById(R.id.rl_userinfo_autograph);
        this.btn_userinfo_qq = (Button) findViewById(R.id.btn_userinfo_qq);
        this.btn_userinfo_occupation = (Button) findViewById(R.id.btn_userinfo_occupation);
        this.btn_label_back = (Button) findViewById(R.id.btn_label_back);
        this.rl_userinfo_nickName.setOnClickListener(this.myClickListener);
        this.rl_userinfo_gender.setOnClickListener(this.myClickListener);
        this.rl_userinfo_birthday.setOnClickListener(this.myClickListener);
        this.btn_userinfo_qq.setOnClickListener(this.myClickListener);
        this.btn_userinfo_occupation.setOnClickListener(this.myClickListener);
        this.btn_label_back.setOnClickListener(this.myClickListener);
        initUI();
    }

    private void initUI() {
        Cursor cursor = DbUtils.getDb(this.context).rawQuery("select * from t_user where id = " + DbUtils.queryUserId(this.context), null);
        if (cursor.getCount() > 0) {
            cursor.moveToNext();
            String nickname = cursor.getString(cursor.getColumnIndex("nickname"));
            int genderInt = cursor.getInt(cursor.getColumnIndex("genderInt"));
            int professionInt = cursor.getInt(cursor.getColumnIndex("professionInt"));
            String birthday = cursor.getString(cursor.getColumnIndex("birthday"));
            String qq = cursor.getString(cursor.getColumnIndex("qq"));
            if (isNotnull(nickname)) {
                this.tv_userinfo_nickname.setText(nickname);
            } else {
                this.tv_userinfo_nickname.setText("");
            }
            if (genderInt == this.GENDER_MAN) {
                this.tv_userinfo_gender.setText(getString(R.string.str_man));
            } else if (genderInt == this.GENDER_WOMAN) {
                this.tv_userinfo_gender.setText(getString(R.string.str_woman));
            } else if (genderInt == this.GENDER_OTHER) {
                this.tv_userinfo_gender.setText(getString(R.string.str_other));
            } else {
                this.tv_userinfo_gender.setText("");
            }
            if (isNotnull(birthday)) {
                this.tv_userinfo_birthday.setText(birthday);
            } else {
                this.tv_userinfo_birthday.setText("");
            }
            if (professionInt == this.PROFESSION_STUDENT) {
                this.tv_userinfo_occupation.setText(getString(R.string.str_student));
            } else if (professionInt == this.PROFESSION_OFFIC_WORKER) {
                this.tv_userinfo_occupation.setText(getString(R.string.str_office_worker));
            } else if (professionInt == this.PROFESSION_FREE_OCCUPTION) {
                this.tv_userinfo_occupation.setText(getString(R.string.str_free_occupation));
            } else if (professionInt == this.PROFESSION_WAIT_JOB) {
                this.tv_userinfo_occupation.setText(getString(R.string.str_wait_job));
            } else if (professionInt == this.PROFESSION_OTHER) {
                this.tv_userinfo_occupation.setText(getString(R.string.str_other));
            } else {
                this.tv_userinfo_occupation.setText("");
            }
            if (isNotnull(qq)) {
                this.tv_userinfo_qq.setText(qq);
                return;
            } else {
                this.tv_userinfo_qq.setText("");
                return;
            }
        }
        finish();
    }

    private boolean isNotnull(String str) {
        if (str == null || str.length() <= 0 || str.equalsIgnoreCase("null") || str.equals("0")) {
            return false;
        }
        return true;
    }

    private void showPopDialog() {
        int i;
        if (this.popup != null && this.popup.isShowing()) {
            this.popup.dismiss();
        }
        Calendar cal2 = Calendar.getInstance();
        int YEAR = cal2.get(1);
        int MONTH = cal2.get(2);
        int DAY_OF_MONTH = cal2.get(5);
        this.year_select = 1990;
        for (i = 0; i <= 80; i++) {
            int a = 80 - i;
            this.arr2yearMap.put(Integer.valueOf(i), Integer.valueOf(YEAR - a));
            this.year2arrMap.put(Integer.valueOf(YEAR - a), Integer.valueOf(i));
        }
        for (i = 0; i <= 12; i++) {
            this.arr2monthMap.put(Integer.valueOf(i), Integer.valueOf(i + 1));
            this.month2arrMap.put(Integer.valueOf(i + 1), Integer.valueOf(i));
        }
        int maxDay = cal2.getMaximum(5);
        for (i = 0; i <= maxDay; i++) {
            this.arr2dayMap.put(Integer.valueOf(i), Integer.valueOf(i + 1));
            this.day2arrMap.put(Integer.valueOf(i + 1), Integer.valueOf(i));
        }
        log("YEAR:" + YEAR + ",MONTH:" + MONTH + ",DAY_OF_MONTH:" + DAY_OF_MONTH);
        RelativeLayout rl = (RelativeLayout) getLayoutInflater().inflate(R.layout.tem_date_select, null);
        WheelView wv_tem_year = (WheelView) rl.findViewById(R.id.wv_tem_year);
        wv_tem_year.setAdapter(new NumericWheelAdapter(YEAR - 80, YEAR, "%04d"));
        wv_tem_year.addChangingListener(this.changedListener1);
        wv_tem_year.setLabel("年");
        wv_tem_year.setCyclic(true);
        WheelView wv_tem_month = (WheelView) rl.findViewById(R.id.wv_tem_month);
        wv_tem_month.setAdapter(new NumericWheelAdapter(1, 12, "%02d"));
        wv_tem_month.addChangingListener(this.changedListener2);
        wv_tem_month.setLabel("月");
        wv_tem_month.setCyclic(true);
        this.wv_tem_day = (WheelView) rl.findViewById(R.id.wv_tem_day);
        this.wv_tem_day.setAdapter(new NumericWheelAdapter(1, maxDay, "%02d"));
        this.wv_tem_day.setLabel("日");
        this.wv_tem_day.addChangingListener(this.changedListener3);
        this.wv_tem_day.setCyclic(true);
        try {
            String hadSelectDate = this.tv_userinfo_birthday.getText().toString();
            if (hadSelectDate.length() > 0) {
                Calendar tempCalendar = DateTime.pars2Calender(hadSelectDate + " 23:59:59");
                this.year_select = tempCalendar.get(1);
                wv_tem_year.setCurrentItem(((Integer) this.year2arrMap.get(Integer.valueOf(tempCalendar.get(1)))).intValue());
                wv_tem_month.setCurrentItem(((Integer) this.month2arrMap.get(Integer.valueOf(tempCalendar.get(2) + 1))).intValue());
                this.wv_tem_day.setCurrentItem(((Integer) this.day2arrMap.get(Integer.valueOf(tempCalendar.get(5)))).intValue());
            } else {
                wv_tem_year.setCurrentItem(((Integer) this.year2arrMap.get(Integer.valueOf(1990))).intValue());
                wv_tem_month.setCurrentItem(((Integer) this.month2arrMap.get(Integer.valueOf(MONTH + 1))).intValue());
                this.wv_tem_day.setCurrentItem(((Integer) this.day2arrMap.get(Integer.valueOf(DAY_OF_MONTH))).intValue());
            }
        } catch (Exception e) {
            DbUtils.exceptionHandler(e);
            wv_tem_year.setCurrentItem(((Integer) this.year2arrMap.get(Integer.valueOf(1990))).intValue());
            wv_tem_month.setCurrentItem(((Integer) this.month2arrMap.get(Integer.valueOf(MONTH + 1))).intValue());
            this.wv_tem_day.setCurrentItem(((Integer) this.day2arrMap.get(Integer.valueOf(DAY_OF_MONTH))).intValue());
        }
        Button btn_tem_time_cancel = (Button) rl.findViewById(R.id.btn_tem_time_cancel);
        ((Button) rl.findViewById(R.id.btn_tem_time_save)).setOnClickListener(this.addTimeOnClickListener);
        btn_tem_time_cancel.setOnClickListener(this.addTimeOnClickListener);
        this.popup = new PopupWindow(rl, -1, -2);
        this.popup.setOutsideTouchable(true);
        this.popup.showAtLocation(this.tv_userinfo_birthday, 80, 0, 0);
    }

    private String[] getOccupationArr() {
        if (this.occupationArr == null) {
            this.occupationArr = new String[]{getString(R.string.str_student), getString(R.string.str_office_worker), getString(R.string.str_free_occupation), getString(R.string.str_wait_job), getString(R.string.str_other)};
        }
        return this.occupationArr;
    }

    private void showOccupationDialog() {
        new Builder(this.context).setTitle((int) R.string.str_choose).setItems(getOccupationArr(), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                int type = which + 1;
                ContentValues values = new ContentValues();
                values.put("professionInt", Integer.valueOf(type));
                values.put("endUpdateTime", DateTime.getTimeString());
                DbUtils.getDb(UserInfoActivity.this.context).update("t_user", values, "id = " + DbUtils.queryUserId(UserInfoActivity.this.context), null);
                dialog.cancel();
                UserInfoActivity.this.initUI();
            }
        }).create().show();
    }

    private String[] getGenderArr() {
        if (this.genderArr == null) {
            this.genderArr = new String[]{getString(R.string.str_man), getString(R.string.str_woman), getString(R.string.str_other)};
        }
        return this.genderArr;
    }

    private void showGenderSelectDialog() {
        new Builder(this.context).setTitle((int) R.string.str_choose).setItems(getGenderArr(), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                int type = which + 1;
                ContentValues values = new ContentValues();
                values.put("genderInt", Integer.valueOf(type));
                values.put("endUpdateTime", DateTime.getTimeString());
                DbUtils.getDb(UserInfoActivity.this.context).update("t_user", values, "id = " + DbUtils.queryUserId(UserInfoActivity.this.context), null);
                dialog.cancel();
                UserInfoActivity.this.initUI();
            }
        }).create().show();
    }

    private void clickQQ() {
        Intent it = new Intent(this.context, EditActivity.class);
        it.setAction(Val.INTENT_ACTION_EDIT_USER_INFO);
        it.putExtra("column", COLUMN_QQ);
        it.putExtra("columnValue", this.tv_userinfo_qq.getText().toString());
        startActivity(it);
    }

    private void clickNickName() {
        Intent it = new Intent(this.context, EditActivity.class);
        it.setAction(Val.INTENT_ACTION_EDIT_USER_INFO);
        it.putExtra("column", COLUMN_NICKNAME);
        it.putExtra("columnValue", this.tv_userinfo_nickname.getText().toString());
        startActivity(it);
    }

    public void onBackPressed() {
        if (this.popup == null || !this.popup.isShowing()) {
            super.onBackPressed();
            finish();
            overridePendingTransition(R.anim.push_to_right_in, R.anim.push_to_right_out);
            return;
        }
        this.popup.dismiss();
    }

    protected void onResume() {
        super.onResume();
        initUI();
        MobclickAgent.onResume(this);
    }

    public void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

    public static void log(String str) {
        Log.i(TAG, ":" + str);
    }
}
