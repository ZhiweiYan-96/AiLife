package com.record.myLife.main;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.alibaba.fastjson.asm.Opcodes;
import com.qq.e.comm.constants.ErrorCode.InitError;
import com.record.bean.Act;
import com.record.bean.Act2;
import com.record.bean.User;
import com.record.myLife.R;
import com.record.myLife.base.BaseActivity;
import com.record.myLife.view.CustomDialog;
import com.record.myLife.view.dialog.AlertDialogM.Builder;
import com.record.service.SystemBarTintManager;
import com.record.service.TimerService;
import com.record.utils.DateTime;
import com.record.utils.FormatUtils;
import com.record.utils.GeneralHelper;
import com.record.utils.GeneralUtils;
import com.record.utils.MyNotification;
import com.record.utils.ShowGuideImgUtils;
import com.record.utils.Sql;
import com.record.utils.Val;
import com.record.utils.db.DbUtils;
import com.record.utils.dialog.DialogUtils;
import com.record.view.pullrefresh.view.PullToRefreshBase;
import com.record.view.wheel.widget.NumericWheelAdapter;
import com.record.view.wheel.widget.OnWheelChangedListener;
import com.record.view.wheel.widget.WheelView;
import com.sun.mail.imap.IMAPStore;
import com.umeng.analytics.MobclickAgent;
import com.umeng.analytics.onlineconfig.a;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

public class AddActActivity_v2 extends BaseActivity {
    public static final int POSITION_DEFAULT = 1;
    static String TAG = "override";
    private static final String[] spiner_data = new String[]{"业界Top1%", "业界Top5%", "业界Top10%", "业界Top20%", "外语考试", "司法考试", "财务考试", "高考考研", "学期考试", "自定义"};
    private static final String[] spiner_data2 = new String[]{"两月坚持", "一月习惯", "两周适应", "一周努力", "三天冲刺", "一天尝试", "自定义"};
    private static final int[] spiner_data2_int = new int[]{PullToRefreshBase.SMOOTH_SCROLL_DURATION_MS, Opcodes.FCMPG, 100, 50, 24, 4, 0};
    private static final int[] spiner_data_int = new int[]{10000, 5400, 1800, 600, 2000, 5000, 5000, 2500, IMAPStore.RESPONSE, 0};
    private static final int[] spiner_days2_int = new int[]{60, 30, 14, 7, 3, 1, 7};
    private static final int[] spiner_days_int = new int[]{1666, 675, 225, 75, 500, 625, 625, 312, 111, 0};
    final int ADD_TYPE_GOAL = 1;
    final int ADD_TYPE_HABIT = 2;
    int BigGoal = 0;
    long Cal_tempDays;
    TextWatcher EverydayWatcher = new TextWatcher() {
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        public void afterTextChanged(Editable s) {
            String everydayNeed = AddActActivity_v2.this.et_everyday_need_time_dialog.getText().toString().trim().replace(",", "");
            String deadDate = AddActActivity_v2.this.tv_add_deadline_val.getText().toString();
            if (deadDate.length() > 0 && everydayNeed.length() > 0) {
                Calendar c2;
                if (AddActActivity_v2.this.createGoalTime == null || AddActActivity_v2.this.createGoalTime.length() <= 0) {
                    c2 = Calendar.getInstance();
                } else {
                    c2 = DateTime.pars2Calender(AddActActivity_v2.this.createGoalTime);
                }
                try {
                    double everydayNeed_D = Double.parseDouble(everydayNeed);
                    long dif = DateTime.pars2Calender(deadDate + " 23:59:59").getTimeInMillis() - c2.getTimeInMillis();
                    if (dif > 0) {
                        double needTime = ((double) (((dif / 1000) / 86400) + 1)) * everydayNeed_D;
                        AddActActivity_v2.this.timeOfEveryday = 3600.0d * everydayNeed_D;
                        AddActActivity_v2.this.tv_add_needTime_val.setText(needTime + "");
                        AddActActivity_v2.this.tv_add_everyday_val.setText("" + FormatUtils.format_1fra(everydayNeed_D));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    };
    OnClickListener IvLabelListener = new OnClickListener() {
        public void onClick(View v) {
            if (AddActActivity_v2.this.dialog_label_select.isShowing()) {
                AddActActivity_v2.this.dialog_label_select.cancel();
            }
            int id = v.getId();
            if (id == R.id.iv_color_bg_green1) {
                AddActActivity_v2.this.sel_color = R.color.bg_green1;
            } else if (id == R.id.iv_color_bg_green2) {
                AddActActivity_v2.this.sel_color = R.color.bg_green2;
            } else if (id == R.id.iv_color_bg_green3) {
                AddActActivity_v2.this.sel_color = R.color.bg_green3;
            } else if (id == R.id.iv_color_bg_yellow1) {
                AddActActivity_v2.this.sel_color = R.color.bg_yellow1;
            } else if (id == R.id.iv_color_bg_yellow2) {
                AddActActivity_v2.this.sel_color = R.color.bg_yellow2;
            } else if (id == R.id.iv_color_bg_yellow3) {
                AddActActivity_v2.this.sel_color = R.color.bg_yellow3;
            } else if (id == R.id.iv_color_bg_blue1) {
                AddActActivity_v2.this.sel_color = R.color.bg_blue1;
            } else if (id == R.id.iv_color_bg_blue2) {
                AddActActivity_v2.this.sel_color = R.color.bg_blue2;
            } else if (id == R.id.iv_color_bg_blue3) {
                AddActActivity_v2.this.sel_color = R.color.bg_blue3;
            } else if (id == R.id.iv_color_bg_red1) {
                AddActActivity_v2.this.sel_color = R.color.bg_red1;
            } else if (id == R.id.iv_color_bg_red2) {
                AddActActivity_v2.this.sel_color = R.color.bg_red2;
            } else if (id == R.id.iv_color_bg_red3) {
                AddActActivity_v2.this.sel_color = R.color.bg_red3;
            }
            if (AddActActivity_v2.this.sel_color > 0) {
                AddActActivity_v2.this.iv_add_color2.setImageResource(((Integer) Val.col_Str2xml_circle_Int_Map.get(Val.col_Int2Str_Map.get(Integer.valueOf(AddActActivity_v2.this.sel_color)))).intValue());
            }
        }
    };
    TextWatcher NeedTimeWatcher = new TextWatcher() {
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        public void afterTextChanged(Editable s) {
            String needTime = AddActActivity_v2.this.tv_add_everyday_val.getText().toString().trim().replace(",", "");
            try {
                if (needTime.length() > 0) {
                    double need = Double.parseDouble(needTime);
                    if (need > 20.0d) {
                        AddActActivity_v2.this.tv_add_everyday_val_info.setText(AddActActivity_v2.this.getString(R.string.str_dont_more_than_20));
                        AddActActivity_v2.this.tv_add_everyday_val_info.setVisibility(0);
                        return;
                    } else if (need < 0.1d) {
                        AddActActivity_v2.this.tv_add_everyday_val_info.setText(AddActActivity_v2.this.getString(R.string.str_dont_less_than_01));
                        AddActActivity_v2.this.tv_add_everyday_val_info.setVisibility(0);
                        return;
                    } else {
                        AddActActivity_v2.this.btn_edit_everyday_need_time.setVisibility(0);
                    }
                }
                AddActActivity_v2.this.tv_add_everyday_val_info.setVisibility(8);
            } catch (Exception e) {
                DbUtils.exceptionHandler(e);
                AddActActivity_v2.this.tv_add_everyday_val_info.setText(AddActActivity_v2.this.getString(R.string.str_everyday_error));
                AddActActivity_v2.this.tv_add_everyday_val_info.setVisibility(0);
            }
        }
    };
    TextWatcher NeetimeEditWatcher = new TextWatcher() {
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        public void afterTextChanged(Editable s) {
            try {
                String needTime = AddActActivity_v2.this.et_need_time_dialog.getText().toString().trim().replace(",", "");
                if (needTime.length() > 6) {
                    GeneralHelper.toastShort(AddActActivity_v2.this.context, " 需要时间过长哦，改小些吧！");
                    return;
                }
                String deadDate = AddActActivity_v2.this.tv_add_deadline_val.getText().toString();
                if (deadDate.length() > 0 && needTime.length() > 0) {
                    Calendar c2 = Calendar.getInstance();
                    try {
                        if (AddActActivity_v2.this.actId != null && AddActActivity_v2.this.actId.length() > 0) {
                            c2 = DateTime.pars2Calender(DbUtils.getAct2ByActId(AddActActivity_v2.this.context, AddActActivity_v2.this.actId).getStartTime());
                        }
                    } catch (NumberFormatException e) {
                        DbUtils.exceptionHandler(e);
                    }
                    double dif = ((double) DateTime.pars2Calender(deadDate + " 23:59:59").getTimeInMillis()) - ((double) c2.getTimeInMillis());
                    if (dif > 0.0d) {
                        double hour = Double.parseDouble(needTime) / (((dif / 1000.0d) / 86400.0d) + 1.0d);
                        AddActActivity_v2.this.timeOfEveryday = 3600.0d * hour;
                        AddActActivity_v2.this.tv_add_everyday_val.setText("" + FormatUtils.format_1fra(hour));
                    }
                }
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
    };
    String actId = "";
    OnClickListener addTimeOnClickListener = new OnClickListener() {
        public void onClick(View v) {
            int id = v.getId();
            if (id == R.id.btn_tem_time_cancel) {
                try {
                    if (AddActActivity_v2.this.actId != null && AddActActivity_v2.this.actId.length() > 0) {
                        AddActActivity_v2.this.initData2(AddActActivity_v2.this.actId);
                    }
                } catch (Exception e) {
                    DbUtils.exceptionHandler(e);
                    AddActActivity_v2.this.tv_add_everyday_val.setText("");
                    AddActActivity_v2.this.tv_add_deadline_val.setText("");
                    AddActActivity_v2.this.tv_add_deadline_val.setHint("点击选择");
                }
                AddActActivity_v2.this.popup.dismiss();
            } else if (id == R.id.btn_tem_time_save) {
                if (AddActActivity_v2.this.Cal_tempDays > 0) {
                    AddActActivity_v2.this.btn_edit_everyday_need_time.setVisibility(0);
                    GeneralHelper.toastShort(AddActActivity_v2.this.context, "距最后期限只有" + AddActActivity_v2.this.Cal_tempDays + "天！请珍惜时间！");
                    if (AddActActivity_v2.this.calTempHours > 0.0d) {
                        GeneralHelper.toastShort(AddActActivity_v2.this.context, "每天至少需投资" + FormatUtils.format_1fra(AddActActivity_v2.this.calTempHours) + "小时!请把握时间！");
                    }
                }
                AddActActivity_v2.this.popup.dismiss();
            }
        }
    };
    int add_type = 1;
    HashMap<Integer, Integer> arr2dayMap = new HashMap();
    HashMap<Integer, Integer> arr2monthMap = new HashMap();
    HashMap<Integer, Integer> arr2yearMap = new HashMap();
    Button btn_add_back;
    Button btn_add_color2;
    Button btn_add_delete;
    Button btn_add_goal_type;
    Button btn_add_habit_type;
    Button btn_add_level_change;
    TextView btn_add_needTime_val;
    Button btn_add_reset;
    Button btn_add_save;
    Button btn_add_start_reset;
    Button btn_edit_everyday_need_time;
    Button btn_edit_need_time;
    double calTempHours;
    private OnWheelChangedListener changedListener1 = new OnWheelChangedListener() {
        public void onChanged(WheelView wheel, int oldValue, int newValue) {
            AddActActivity_v2.this.year_select = ((Integer) AddActActivity_v2.this.arr2yearMap.get(Integer.valueOf(newValue))).intValue();
            AddActActivity_v2.log("OnWheelChangedListener 选择年:" + AddActActivity_v2.this.year_select);
            Calendar c = Calendar.getInstance();
            c.set(AddActActivity_v2.this.year_select, AddActActivity_v2.this.month_select - 1, 1);
            int actualMax = c.getActualMaximum(5);
            AddActActivity_v2.log("OnWheelChangedListener 选择的月:" + AddActActivity_v2.this.month_select + ",year_select:" + AddActActivity_v2.this.year_select + ",最大值是：" + c.getActualMaximum(5));
            AddActActivity_v2.this.wv_tem_day.setAdapter(new NumericWheelAdapter(1, c.getActualMaximum(5), "%02d"));
            if (AddActActivity_v2.this.day_select > actualMax) {
                AddActActivity_v2.this.wv_tem_day.scroll(((Integer) AddActActivity_v2.this.day2arrMap.get(Integer.valueOf(actualMax))).intValue(), InitError.INIT_AD_ERROR);
            }
            AddActActivity_v2.this.calTimeOfEveryDay();
        }
    };
    private OnWheelChangedListener changedListener2 = new OnWheelChangedListener() {
        public void onChanged(WheelView wheel, int oldValue, int newValue) {
            AddActActivity_v2.this.month_select = ((Integer) AddActActivity_v2.this.arr2monthMap.get(Integer.valueOf(newValue))).intValue();
            Calendar c = Calendar.getInstance();
            c.set(AddActActivity_v2.this.year_select, AddActActivity_v2.this.month_select - 1, 1);
            int actualMax = c.getActualMaximum(5);
            AddActActivity_v2.log("OnWheelChangedListener 选择的月:" + AddActActivity_v2.this.month_select + ",年:" + AddActActivity_v2.this.year_select + ",天数最大值是：" + c.getActualMaximum(5));
            AddActActivity_v2.this.wv_tem_day.setAdapter(new NumericWheelAdapter(1, c.getActualMaximum(5), "%02d"));
            if (AddActActivity_v2.this.day_select > actualMax) {
                AddActActivity_v2.this.wv_tem_day.scroll(((Integer) AddActActivity_v2.this.day2arrMap.get(Integer.valueOf(actualMax))).intValue(), InitError.INIT_AD_ERROR);
            }
            AddActActivity_v2.this.calTimeOfEveryDay();
        }
    };
    private OnWheelChangedListener changedListener3 = new OnWheelChangedListener() {
        public void onChanged(WheelView wheel, int oldValue, int newValue) {
            AddActActivity_v2.this.day_select = newValue + 1;
            AddActActivity_v2.log("OnWheelChangedListener 选择的日:" + AddActActivity_v2.this.day_select);
            AddActActivity_v2.this.calTimeOfEveryDay();
        }
    };
    Context context;
    String createGoalTime = "";
    HashMap<Integer, Integer> day2arrMap = new HashMap();
    int day_select = 1;
    Dialog dialog_label_select;
    TextView et_add_belong_to_val;
    TextView et_add_name;
    EditText et_add_remark;
    TextView et_add_type;
    TextView et_add_type_val;
    EditText et_everyday_need_time_dialog;
    EditText et_need_time_dialog;
    AlertDialog iconDialog = null;
    ArrayList<Integer> idList;
    boolean isManuscript = false;
    boolean isResetGoalStartTime = false;
    ImageView iv_add_color2;
    ImageView iv_add_label2;
    LinearLayout ll_add_deadline_goal;
    int manuscriptId = 0;
    HashMap<Integer, Integer> month2arrMap = new HashMap();
    int month_select = 1;
    OnClickListener myClickListener = new OnClickListener() {
        public void onClick(View v) {
            int id = v.getId();
            if (id == R.id.btn_add_save) {
                AddActActivity_v2.this.saveData();
            } else if (id == R.id.btn_add_delete) {
                AddActActivity_v2.this.deleteGoal();
            } else if (id == R.id.btn_add_color2) {
                AddActActivity_v2.this.clickSelectOrLabel();
            } else if (id == R.id.btn_add_level_change) {
                AddActActivity_v2.this.showEditLevelDialog();
            } else if (id == R.id.btn_add_back) {
                AddActActivity_v2.this.saveTempData();
            } else if (id == R.id.rl_add_level_choose) {
                AddActActivity_v2.this.showSelectLevelDialog();
            } else if (id == R.id.tv_add_deadline_val || id == R.id.tv_add_deadline_val_pre) {
                AddActActivity_v2.this.showPopDialog();
            } else if (id == R.id.btn_edit_need_time) {
                AddActActivity_v2.this.showEditNeedTimeDialog();
            } else if (id == R.id.btn_edit_everyday_need_time) {
                AddActActivity_v2.this.showEditEverydayNeedTimeDialog();
            } else if (id == R.id.rl_add_type || id == R.id.et_add_type || id == R.id.et_add_type_val) {
                AddActActivity_v2.this.showChooseGoalType();
            } else if (id == R.id.rl_add_belong_to) {
                AddActActivity_v2.this.ShowGoalTypeDialog();
            } else if (id == R.id.btn_add_reset) {
                AddActActivity_v2.this.showConfirmResetDialog();
            } else if (id == R.id.btn_add_start_reset) {
                AddActActivity_v2.this.showIsResetStartDateDialog();
            } else if (id == R.id.btn_add_habit_type) {
                AddActActivity_v2.this.updateUiHabitType();
            } else if (id == R.id.btn_add_goal_type) {
                AddActActivity_v2.this.updateUiGoalType();
            }
        }
    };
    ArrayList<String> nameList;
    PopupWindow popup;
    int position = 0;
    RelativeLayout rl_add_act_v2_habit_info;
    RelativeLayout rl_add_belong_to;
    RelativeLayout rl_add_level_choose;
    RelativeLayout rl_add_start_val_pre;
    RelativeLayout rl_add_type;
    int sel_color = R.color.bg_green1;
    int sel_label = R.drawable.ic_label_desklamp;
    Button set_btn_version;
    String temp = "";
    double timeOfEveryday = 0.0d;
    TextView tv_add_deadline_val;
    TextView tv_add_deadline_val_pre;
    TextView tv_add_everyday_val;
    TextView tv_add_everyday_val_info;
    TextView tv_add_level;
    TextView tv_add_level_val;
    TextView tv_add_needTime_val;
    TextView tv_add_start_val;
    TextView tv_tem_time_info;
    TextView tv_tem_time_needTime;
    View v_add_goal_type_bottom_bg;
    View v_add_habit_type_bottom_bg;
    WheelView wv_tem_day;
    HashMap<Integer, Integer> year2arrMap = new HashMap();
    int year_select = 1;

    class ImageAdapter extends BaseAdapter {
        Context context;
        LayoutInflater inflater;

        public ImageAdapter(Context c) {
            this.context = c;
            this.inflater = AddActActivity_v2.this.getLayoutInflater();
        }

        public int getCount() {
            return Val.labelIdArr2.length;
        }

        public Object getItem(int position) {
            return null;
        }

        public long getItemId(int position) {
            return (long) position;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            RelativeLayout rl;
            if (null == null) {
                rl = (RelativeLayout) this.inflater.inflate(R.layout.tem_icon, null);
            } else {
                rl = (RelativeLayout) convertView;
            }
            ((ImageView) rl.findViewById(R.id.iv_tem_select_icon_icon)).setImageResource(Val.labelIdArr2[position]);
            return rl;
        }
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addact_v2);
        initUiFind();
        init();
        initView();
    }

    private void init() {
        this.context = this;
        TAG += getClass().getSimpleName();
        SystemBarTintManager.setMIUIbar(this);
        this.isResetGoalStartTime = false;
        try {
            ShowGuideImgUtils.showImage(this.context, Val.CONFIGURE_IS_SHOW_ADD_GOAL_INSIDE_GUIDE, 1, R.drawable.guide_add_goal_inside);
        } catch (Exception e) {
            DbUtils.exceptionHandler(e);
        }
    }

    private void initView() {
        this.actId = getIntent().getStringExtra("ActId");
        if (this.actId == null || this.actId.length() <= 0) {
            this.rl_add_start_val_pre.setVisibility(8);
            if (getIntent().getIntExtra("isNotMantscript", 0) == 0) {
                isExistMantscript();
            } else {
                updateUiGoalType();
            }
        } else {
            initData(this.actId);
            this.rl_add_start_val_pre.setVisibility(0);
            this.btn_add_delete.setVisibility(0);
        }
        if (this.actId == null || this.actId.length() <= 0) {
            this.btn_add_reset.setVisibility(0);
            this.btn_add_reset.setOnClickListener(this.myClickListener);
            return;
        }
        this.btn_add_reset.setVisibility(8);
    }

    private void isExistMantscript() {
        Cursor cursor = DbUtils.getDb(this.context).rawQuery(Sql.getManuscriptGoal(this.context), null);
        if (cursor.getCount() > 0) {
            cursor.moveToNext();
            int id = cursor.getInt(cursor.getColumnIndex("id"));
            if (id > 0) {
                this.isManuscript = true;
                this.manuscriptId = id;
                initData(id + "");
            }
        } else {
            updateUiHabitType();
        }
        DbUtils.close(cursor);
    }

    private void initUiFind() {
        this.iv_add_color2 = (ImageView) findViewById(R.id.iv_add_color2);
        this.iv_add_label2 = (ImageView) findViewById(R.id.iv_add_label2);
        this.btn_add_color2 = (Button) findViewById(R.id.btn_add_color2);
        this.et_add_name = (TextView) findViewById(R.id.et_add_name);
        this.btn_add_save = (Button) findViewById(R.id.btn_add_save);
        this.btn_add_delete = (Button) findViewById(R.id.btn_add_delete);
        this.btn_add_back = (Button) findViewById(R.id.btn_add_back);
        this.btn_add_level_change = (Button) findViewById(R.id.btn_add_level_change);
        this.btn_edit_need_time = (Button) findViewById(R.id.btn_edit_need_time);
        this.btn_add_reset = (Button) findViewById(R.id.btn_add_reset);
        this.btn_edit_everyday_need_time = (Button) findViewById(R.id.btn_edit_everyday_need_time);
        this.btn_add_start_reset = (Button) findViewById(R.id.btn_add_start_reset);
        this.btn_add_habit_type = (Button) findViewById(R.id.btn_add_habit_type);
        this.btn_add_goal_type = (Button) findViewById(R.id.btn_add_goal_type);
        this.v_add_habit_type_bottom_bg = findViewById(R.id.v_add_habit_type_bottom_bg);
        this.v_add_goal_type_bottom_bg = findViewById(R.id.v_add_goal_type_bottom_bg);
        this.tv_add_level = (TextView) findViewById(R.id.tv_add_level);
        this.tv_add_level_val = (TextView) findViewById(R.id.tv_add_level_val);
        this.tv_add_needTime_val = (TextView) findViewById(R.id.tv_add_needTime_val);
        this.btn_add_needTime_val = (TextView) findViewById(R.id.btn_add_needTime_val);
        this.tv_add_deadline_val = (TextView) findViewById(R.id.tv_add_deadline_val);
        this.tv_add_everyday_val = (TextView) findViewById(R.id.tv_add_everyday_val);
        this.tv_add_everyday_val_info = (TextView) findViewById(R.id.tv_add_everyday_val_info);
        this.tv_add_deadline_val_pre = (TextView) findViewById(R.id.tv_add_deadline_val_pre);
        this.et_add_type_val = (TextView) findViewById(R.id.et_add_type_val);
        this.et_add_belong_to_val = (TextView) findViewById(R.id.et_add_belong_to_val);
        this.tv_add_start_val = (TextView) findViewById(R.id.tv_add_start_val);
        this.et_add_remark = (EditText) findViewById(R.id.et_add_remark);
        this.rl_add_level_choose = (RelativeLayout) findViewById(R.id.rl_add_level_choose);
        this.rl_add_belong_to = (RelativeLayout) findViewById(R.id.rl_add_belong_to);
        this.rl_add_type = (RelativeLayout) findViewById(R.id.rl_add_type);
        this.rl_add_act_v2_habit_info = (RelativeLayout) findViewById(R.id.rl_add_act_v2_habit_info);
        this.rl_add_start_val_pre = (RelativeLayout) findViewById(R.id.rl_add_start_val_pre);
        this.et_add_type = (TextView) findViewById(R.id.et_add_type);
        this.et_add_type_val = (TextView) findViewById(R.id.et_add_type_val);
        this.ll_add_deadline_goal = (LinearLayout) findViewById(R.id.ll_add_deadline_goal);
        this.btn_add_save.setOnClickListener(this.myClickListener);
        this.btn_add_color2.setOnClickListener(this.myClickListener);
        this.btn_add_delete.setOnClickListener(this.myClickListener);
        this.btn_add_back.setOnClickListener(this.myClickListener);
        this.tv_add_deadline_val.setOnClickListener(this.myClickListener);
        this.tv_add_deadline_val_pre.setOnClickListener(this.myClickListener);
        this.btn_add_level_change.setOnClickListener(this.myClickListener);
        this.rl_add_level_choose.setOnClickListener(this.myClickListener);
        this.btn_edit_need_time.setOnClickListener(this.myClickListener);
        this.btn_edit_everyday_need_time.setOnClickListener(this.myClickListener);
        this.rl_add_belong_to.setOnClickListener(this.myClickListener);
        this.rl_add_type.setOnClickListener(this.myClickListener);
        this.btn_add_start_reset.setOnClickListener(this.myClickListener);
        this.btn_add_goal_type.setOnClickListener(this.myClickListener);
        this.btn_add_habit_type.setOnClickListener(this.myClickListener);
        this.tv_add_everyday_val.addTextChangedListener(this.NeedTimeWatcher);
    }

    private void initUI() {
        if (this.tv_add_level_val.getText().toString().length() > 0) {
            this.btn_add_level_change.setVisibility(0);
        } else {
            this.btn_add_level_change.setVisibility(8);
        }
        if (this.tv_add_needTime_val.getText().toString().length() > 0) {
            this.btn_edit_need_time.setVisibility(0);
        } else {
            this.btn_edit_need_time.setVisibility(8);
        }
        if (this.tv_add_everyday_val.getText().toString().length() > 0) {
            this.btn_edit_everyday_need_time.setVisibility(0);
        } else {
            this.btn_edit_everyday_need_time.setVisibility(8);
        }
    }

    private void initData(String actId) {
        try {
            Cursor cursor = DbUtils.getDb(this.context).rawQuery("select * from t_act where " + DbUtils.getWhereUserId(this.context) + " and Id is " + actId, null);
            if (cursor.getCount() > 0) {
                cursor.moveToNext();
                String color = cursor.getString(cursor.getColumnIndex("color"));
                String image = cursor.getString(cursor.getColumnIndex("image"));
                String actName = cursor.getString(cursor.getColumnIndex("actName"));
                String intruction = cursor.getString(cursor.getColumnIndex("intruction"));
                this.createGoalTime = cursor.getString(cursor.getColumnIndex("startTime"));
                String deadline = cursor.getString(cursor.getColumnIndex("deadline"));
                String level = cursor.getString(cursor.getColumnIndex("level"));
                this.timeOfEveryday = (double) cursor.getInt(cursor.getColumnIndex("timeOfEveryday"));
                int isSubGoal = cursor.getInt(cursor.getColumnIndex("isSubGoal"));
                int expectSpend = cursor.getInt(cursor.getColumnIndex("expectSpend"));
                this.position = cursor.getInt(cursor.getColumnIndex("position"));
                if (color != null) {
                    this.sel_color = ((Integer) Val.col_Str2Int_Map.get(color)).intValue();
                }
                if (image != null) {
                    this.sel_label = Val.getLabelIntByName(image);
                }
                this.iv_add_label2.setImageResource(this.sel_label);
                try {
                    if (Val.col_Str2xml_circle_Int_Map == null) {
                        Val.setMap();
                    }
                    this.iv_add_color2.setImageResource(((Integer) Val.col_Str2xml_circle_Int_Map.get(color)).intValue());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (actName != null) {
                    this.et_add_name.setText(actName);
                }
                if (intruction != null) {
                    this.et_add_remark.setText(intruction);
                }
                if (level != null) {
                    this.tv_add_level_val.setText(level);
                }
                if (expectSpend > 0) {
                    this.tv_add_needTime_val.setText((expectSpend / 3600) + "");
                    updateUiGoalType();
                } else {
                    updateUiHabitType();
                }
                if (deadline != null && deadline.length() > 0) {
                    this.tv_add_deadline_val.setText(deadline.substring(0, deadline.indexOf(" ")));
                }
                if (this.timeOfEveryday > 0.0d) {
                    this.tv_add_everyday_val.setText(FormatUtils.format_1fra(this.timeOfEveryday / 3600.0d));
                }
                this.BigGoal = isSubGoal;
                updateUiIsSubGoal(isSubGoal);
                this.tv_add_start_val.setText(this.createGoalTime.substring(0, this.createGoalTime.indexOf(" ")));
            } else {
                finish();
            }
            DbUtils.close(cursor);
        } catch (Exception e2) {
            e2.printStackTrace();
        }
    }

    private void updateUiIsSubGoal(int subGoalId) {
        if (subGoalId > 0) {
            this.et_add_type_val.setText(getString(R.string.str_sub_actType));
            this.rl_add_belong_to.setVisibility(0);
            this.et_add_belong_to_val.setText(DbUtils.queryActNameById(this.context, subGoalId + ""));
            return;
        }
        this.et_add_type_val.setText(getString(R.string.str_big_goal));
        this.rl_add_belong_to.setVisibility(8);
    }

    private void initData2(String actId) {
        Act2 act = DbUtils.getAct2ByActId(this.context, actId);
        this.tv_add_deadline_val.setText(act.getDeadline().substring(0, act.getDeadline().indexOf(" ")));
        this.tv_add_everyday_val.setText(FormatUtils.format_1fra(((double) act.getTimeOfEveryday()) / 3600.0d));
        this.timeOfEveryday = (double) act.getTimeOfEveryday();
        try {
            int needInt = Integer.parseInt(this.tv_add_needTime_val.getText().toString());
            if (act.getExpectSpend() / 3600 != needInt) {
                String startTime = act.getStartTime();
                String Deadline = act.getDeadline();
                long dif = DateTime.pars2Calender(Deadline).getTimeInMillis() - DateTime.pars2Calender(startTime).getTimeInMillis();
                if (dif > 0) {
                    this.Cal_tempDays = (dif / 1000) / 86400;
                    this.calTempHours = ((double) needInt) / ((double) this.Cal_tempDays);
                    this.timeOfEveryday = (double) ((int) (this.calTempHours * 3600.0d));
                    this.tv_add_everyday_val.setText("" + FormatUtils.format_1fra(this.calTempHours));
                }
            }
        } catch (NumberFormatException e) {
            DbUtils.exceptionHandler(e);
        }
    }

    private boolean isDataCorrect() {
        try {
            String needtime = this.tv_add_needTime_val.getText().toString().trim();
            if (needtime.length() == 0) {
                GeneralHelper.toastShort(this.context, getString(R.string.str_level_not_null));
                return false;
            }
            double needInt = Double.parseDouble(needtime);
            if (needInt < 1.0d) {
                GeneralHelper.toastShort(this.context, getString(R.string.str_need_time_must_more_than_ten));
                return false;
            } else if (needInt > 20000.0d) {
                GeneralHelper.toastShort(this.context, getString(R.string.str_need_time_must_less_than_20000));
                return false;
            } else {
                String deadlineVal = this.tv_add_deadline_val.getText().toString().trim();
                if (deadlineVal.length() == 0) {
                    GeneralHelper.toastShort(this.context, getString(R.string.str_deadline_not_null));
                    return false;
                } else if (DateTime.compare_date(DateTime.getTimeString(), deadlineVal + " 23:59:59") >= 0) {
                    GeneralHelper.toastShort(this.context, getString(R.string.str_dealine_must_later_now));
                    return false;
                } else if (this.timeOfEveryday > 0.0d && this.timeOfEveryday / 3600.0d > 20.0d) {
                    GeneralHelper.toastShort(this.context, getString(R.string.str_dont_more_than_20));
                    return false;
                } else if (this.timeOfEveryday / 3600.0d >= 0.1d) {
                    return true;
                } else {
                    GeneralHelper.toastLong(this.context, getString(R.string.str_dont_less_than_01));
                    return false;
                }
            }
        } catch (Exception e) {
            GeneralHelper.toastShort(this.context, getString(R.string.str_time_error));
            return false;
        }
    }

    private void saveData() {
        String actName = this.et_add_name.getText().toString();
        if (actName == null || actName.trim().equals("")) {
            GeneralHelper.toastShort(this.context, getString(R.string.str_goalName_not_null));
            return;
        }
        String level = this.tv_add_level_val.getText().toString().trim();
        String needtime = this.tv_add_needTime_val.getText().toString().trim();
        String deadlineVal = this.tv_add_deadline_val.getText().toString().trim();
        String everyday = this.tv_add_everyday_val.getText().toString().trim();
        String remark = this.et_add_remark.getText().toString().trim();
        ContentValues values;
        String time;
        if (this.add_type != 1) {
            values = new ContentValues();
            values.put("userId", Integer.valueOf(User.getInstance().getUserId()));
            values.put("image", Val.getLabelNameById(this.sel_label));
            values.put("color", (String) Val.col_Int2Str_Map.get(Integer.valueOf(this.sel_color)));
            values.put("actName", actName);
            values.put(a.a, Integer.valueOf(11));
            values.put("expectSpend", Integer.valueOf(0));
            values.put("deadline", "");
            values.put("intruction", remark);
            values.put("timeOfEveryday", Integer.valueOf(0));
            values.put("level", "");
            values.put("isSubGoal", Integer.valueOf(0));
            if (this.actId == null || this.actId.length() <= 0) {
                time = DateTime.getTimeString();
                values.put("createTime", time);
                values.put("startTime", time);
                values.put("position", Integer.valueOf(1));
                if (this.isManuscript) {
                    values.put("isManuscript", Integer.valueOf(0));
                    if (DbUtils.getDb(this.context).update("t_act", values, "id is ?", new String[]{this.manuscriptId + ""}) == 0) {
                        DbUtils.getDb(this.context).insert("t_act", null, values);
                    }
                } else {
                    DbUtils.getDb(this.context).insert("t_act", null, values);
                }
                GeneralHelper.toastShort(this.context, getString(R.string.str_add_success));
                setResult(-1);
                finish();
                return;
            }
            values.put("position", Integer.valueOf(this.position));
            values.put("isDelete", Integer.valueOf(0));
            values.put("endUpdateTime", DateTime.getTimeString());
            DbUtils.getDb(this.context).update("t_act", values, " id is ?", new String[]{this.actId});
            GeneralHelper.toastShort(this.context, getString(R.string.str_modify_success));
            setResult(7);
            finish();
        } else if (isDataCorrect()) {
            values = new ContentValues();
            values.put("userId", Integer.valueOf(User.getInstance().getUserId()));
            values.put("image", Val.getLabelNameById(this.sel_label));
            values.put("color", (String) Val.col_Int2Str_Map.get(Integer.valueOf(this.sel_color)));
            values.put("actName", actName);
            values.put(a.a, Integer.valueOf(11));
            values.put("expectSpend", Double.valueOf(Double.parseDouble(needtime) * 3600.0d));
            values.put("deadline", deadlineVal + " 23:59:59");
            values.put("intruction", remark);
            values.put("timeOfEveryday", Double.valueOf(this.timeOfEveryday));
            values.put("level", level);
            values.put("isSubGoal", Integer.valueOf(this.BigGoal));
            if (this.actId == null || this.actId.length() <= 0) {
                Cursor cursor = DbUtils.getDb(this.context).rawQuery(Sql.widgetGoalsList(this.context), null);
                if (cursor.getCount() > 0) {
                    while (cursor.moveToNext()) {
                        if (cursor.getString(cursor.getColumnIndex("actName")).equals(actName)) {
                            GeneralHelper.toastShort(this.context, getString(R.string.str_goal_name_repeat));
                            return;
                        }
                    }
                }
                time = DateTime.getTimeString();
                values.put("startTime", time);
                values.put("createTime", time);
                DbUtils.getDb(this.context).rawQuery("select max(position) from t_act", null).moveToNext();
                values.put("position", Integer.valueOf(1));
                if (this.isManuscript) {
                    values.put("isManuscript", Integer.valueOf(0));
                    if (DbUtils.getDb(this.context).update("t_act", values, "id is ?", new String[]{this.manuscriptId + ""}) == 0) {
                        DbUtils.getDb(this.context).insert("t_act", null, values);
                    }
                } else {
                    DbUtils.getDb(this.context).insert("t_act", null, values);
                }
                GeneralHelper.toastShort(this.context, getString(R.string.str_add_success));
                setResult(-1);
                MyNotification myNoti = new MyNotification(this.context);
                if (TimerService.timer == null) {
                    myNoti.initNoti();
                } else {
                    myNoti.initCountingNoti(Act.getInstance().getId() + "");
                }
                finish();
                return;
            }
            values.put("position", Integer.valueOf(this.position));
            values.put("isDelete", Integer.valueOf(0));
            values.put("endUpdateTime", DateTime.getTimeString());
            if (this.isResetGoalStartTime) {
                values.put("startTime", DateTime.getTimeString());
            }
            DbUtils.getDb(this.context).update("t_act", values, " id is ?", new String[]{this.actId});
            GeneralHelper.toastShort(this.context, getString(R.string.str_modify_success));
            setResult(7);
            finish();
        }
    }

    private void deleteGoal() {
        ArrayList<Integer> arr = DbUtils.querySubGoalIdByBigGoalId(this.context, Integer.parseInt(this.actId));
        if (arr == null || arr.size() <= 0) {
            new Builder(this.context).setTitle(getString(R.string.str_is_delete_goal)).setNegativeButton(getString(R.string.str_cancel), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                    AddActActivity_v2.log("取消");
                }
            }).setPositiveButton(getString(R.string.str_delete), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    if (AddActActivity_v2.this.actId != null && AddActActivity_v2.this.actId.length() > 0) {
                        AddActActivity_v2.this.setResult(8);
                        if (TimerService.timer != null) {
                            int tempActId = Integer.parseInt(AddActActivity_v2.this.actId);
                            if (Act.getInstance().getId() == tempActId) {
                                try {
                                    AddActActivity_v2.this.sendBroadcast(new Intent(Val.INTENT_ACTION_STOP_COUNTER));
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                AddActActivity_v2.log("删除活动类型，停止计时！当前活动id：" + tempActId);
                            }
                        }
                        GeneralHelper.toastShort(AddActActivity_v2.this.context, AddActActivity_v2.this.getString(R.string.str_delete_success));
                        ContentValues values = new ContentValues();
                        values.put("isDelete", "1");
                        values.put("deleteTime", DateTime.getTimeString());
                        DbUtils.getDb(AddActActivity_v2.this.context).update("t_act", values, "id is " + AddActActivity_v2.this.actId, null);
                        dialog.cancel();
                        AddActActivity_v2.this.finish();
                    }
                    AddActActivity_v2.log("删除");
                }
            }).create().show();
        } else {
            GeneralUtils.toastShort(this.context, getString(R.string.str_subgoal_exist_cant_delete));
        }
    }

    private void updateUiGoalType() {
        this.add_type = 1;
        this.v_add_goal_type_bottom_bg.setBackgroundColor(getResources().getColor(this.sel_color));
        this.v_add_habit_type_bottom_bg.setBackgroundColor(getResources().getColor(R.color.white));
        this.ll_add_deadline_goal.setVisibility(0);
        this.rl_add_act_v2_habit_info.setVisibility(8);
    }

    private void updateUiHabitType() {
        this.add_type = 2;
        this.v_add_habit_type_bottom_bg.setBackgroundColor(getResources().getColor(this.sel_color));
        this.v_add_goal_type_bottom_bg.setBackgroundColor(getResources().getColor(R.color.white));
        this.ll_add_deadline_goal.setVisibility(8);
        this.rl_add_act_v2_habit_info.setVisibility(0);
    }

    private void clickSelectOrLabel() {
        new Builder(this.context).setTitle((int) R.string.str_choose_type).setItems(new String[]{getString(R.string.str_icon), getString(R.string.str_color)}, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0) {
                    AddActActivity_v2.this.clickIcon();
                } else if (which == 1) {
                    AddActActivity_v2.this.clickAddColor();
                }
                dialog.cancel();
            }
        }).create().show();
    }

    private void clickIcon() {
        GridView gridview = (GridView) getLayoutInflater().inflate(R.layout.tem_grid_view, null);
        gridview.setAdapter(new ImageAdapter(this));
        gridview.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                AddActActivity_v2.this.sel_label = Val.labelIdArr2[position];
                AddActActivity_v2.this.iv_add_label2.setImageResource(AddActActivity_v2.this.sel_label);
                if (AddActActivity_v2.this.iconDialog != null && AddActActivity_v2.this.iconDialog.isShowing()) {
                    AddActActivity_v2.this.iconDialog.cancel();
                }
            }
        });
        this.iconDialog = new Builder(this.context).setTitle((int) R.string.str_select).setView(gridview).create();
        this.iconDialog.show();
    }

    private void clickAddColor() {
        this.dialog_label_select = new CustomDialog(this.context, R.style.customDialog, R.layout.dialog_color2);
        this.dialog_label_select.show();
        ((ImageView) this.dialog_label_select.findViewById(R.id.iv_color_bg_green1)).setOnClickListener(this.IvLabelListener);
        ((ImageView) this.dialog_label_select.findViewById(R.id.iv_color_bg_green2)).setOnClickListener(this.IvLabelListener);
        ((ImageView) this.dialog_label_select.findViewById(R.id.iv_color_bg_green3)).setOnClickListener(this.IvLabelListener);
        ((ImageView) this.dialog_label_select.findViewById(R.id.iv_color_bg_yellow1)).setOnClickListener(this.IvLabelListener);
        ((ImageView) this.dialog_label_select.findViewById(R.id.iv_color_bg_yellow2)).setOnClickListener(this.IvLabelListener);
        ((ImageView) this.dialog_label_select.findViewById(R.id.iv_color_bg_yellow3)).setOnClickListener(this.IvLabelListener);
        ((ImageView) this.dialog_label_select.findViewById(R.id.iv_color_bg_blue1)).setOnClickListener(this.IvLabelListener);
        ((ImageView) this.dialog_label_select.findViewById(R.id.iv_color_bg_blue2)).setOnClickListener(this.IvLabelListener);
        ((ImageView) this.dialog_label_select.findViewById(R.id.iv_color_bg_blue3)).setOnClickListener(this.IvLabelListener);
        ((ImageView) this.dialog_label_select.findViewById(R.id.iv_color_bg_red1)).setOnClickListener(this.IvLabelListener);
        ((ImageView) this.dialog_label_select.findViewById(R.id.iv_color_bg_red2)).setOnClickListener(this.IvLabelListener);
        ((ImageView) this.dialog_label_select.findViewById(R.id.iv_color_bg_red3)).setOnClickListener(this.IvLabelListener);
    }

    private void showIsResetStartDateDialog() {
        new Builder(this.context).setTitle(getString(R.string.str_prompt)).setMessage(getString(R.string.str_is_reset_goal_start_date_prompt)).setPositiveButton(getString(R.string.str_reset), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                AddActActivity_v2.this.tv_add_start_val.setText(DateTime.getDateString() + "(" + AddActActivity_v2.this.getString(R.string.str_today) + ")");
                GeneralUtils.toastShort(AddActActivity_v2.this.context, AddActActivity_v2.this.getString(R.string.str_successfully));
                AddActActivity_v2.this.createGoalTime = DateTime.getTimeString();
                AddActActivity_v2.this.calTimeOfEveryDay();
                AddActActivity_v2.this.isResetGoalStartTime = true;
                dialog.cancel();
            }
        }).setNegativeButton(getString(R.string.str_cancel), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        }).create().show();
    }

    private void showChooseGoalType() {
        new Builder(this.context).setTitle(getString(R.string.str_set_current_goal_type)).setPositiveButton(getString(R.string.str_big_goal), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                AddActActivity_v2.this.updateUiIsSubGoal(0);
                AddActActivity_v2.this.BigGoal = 0;
                dialog.dismiss();
            }
        }).setNegativeButton(getString(R.string.str_sub_actType), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                if (AddActActivity_v2.this.actId != null && AddActActivity_v2.this.actId.length() > 0) {
                    ArrayList<Integer> arr = DbUtils.querySubGoalIdByBigGoalIdContainDelte(AddActActivity_v2.this.context, Integer.parseInt(AddActActivity_v2.this.actId));
                    if (arr != null && arr.size() > 0) {
                        DialogUtils.showPrompt(AddActActivity_v2.this.context, AddActActivity_v2.this.getString(R.string.str_this_goal_have_sub_goal));
                        return;
                    }
                }
                AddActActivity_v2.this.ShowGoalTypeDialog();
                dialog.dismiss();
            }
        }).create().show();
    }

    private void saveTempData() {
        try {
            if (this.actId == null || this.actId.length() == 0) {
                String actName = this.et_add_name.getText().toString();
                String level = this.tv_add_level_val.getText().toString().trim();
                String needtime = this.tv_add_needTime_val.getText().toString().trim();
                String deadlineVal = this.tv_add_deadline_val.getText().toString().trim();
                String everyday = this.tv_add_everyday_val.getText().toString().trim();
                String remark = this.et_add_remark.getText().toString().trim();
                ContentValues values = new ContentValues();
                values.put("userId", DbUtils.queryUserId(this.context));
                values.put("image", Val.getLabelNameById(this.sel_label));
                values.put("color", (String) Val.col_Int2Str_Map.get(Integer.valueOf(this.sel_color)));
                values.put(a.a, Integer.valueOf(11));
                values.put("isManuscript", Integer.valueOf(1));
                values.put("isSubGoal", Integer.valueOf(this.BigGoal));
                boolean isHadData = false;
                if (actName != null && actName.length() > 0) {
                    values.put("actName", actName);
                    isHadData = true;
                }
                if (needtime != null && needtime.length() > 0) {
                    values.put("expectSpend", Double.valueOf(Double.parseDouble(needtime) * 3600.0d));
                    isHadData = true;
                }
                if (deadlineVal != null && deadlineVal.length() > 0) {
                    isHadData = true;
                    values.put("deadline", deadlineVal + " 23:59:59");
                }
                if (remark != null && remark.length() > 0) {
                    values.put("intruction", remark);
                    isHadData = true;
                }
                if (this.timeOfEveryday > 0.0d) {
                    values.put("timeOfEveryday", Double.valueOf(this.timeOfEveryday));
                    isHadData = true;
                }
                if (level != null && level.length() > 0) {
                    values.put("level", level);
                    isHadData = true;
                }
                if (isHadData) {
                    if (!this.isManuscript || this.manuscriptId <= 0) {
                        DbUtils.getDb(this.context).insert("t_act", null, values);
                        log("保存草稿成功！");
                    } else {
                        DbUtils.getDb(this.context).update("t_act", values, "id is ?", new String[]{this.manuscriptId + ""});
                        log("更新草稿成功！");
                    }
                    GeneralUtils.toastShort(this.context, getString(R.string.str_temp_data_had_save));
                }
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        finish();
    }

    private void ShowGoalTypeDialog() {
        if (this.nameList == null) {
            String sql = "";
            if (this.actId == null || this.actId.length() <= 0) {
                sql = Sql.getGoalsNotSub(this.context);
            } else {
                sql = Sql.getGoalsNotSub(this.context, Integer.parseInt(this.actId));
            }
            Cursor cursor = DbUtils.getDb(this.context).rawQuery(sql, null);
            if (cursor.getCount() > 0) {
                this.nameList = new ArrayList();
                this.idList = new ArrayList();
                while (cursor.moveToNext()) {
                    this.idList.add(Integer.valueOf(DbUtils.getInt(cursor, "id")));
                    this.nameList.add(DbUtils.getStr(cursor, "actName"));
                }
                if (this.idList.size() > 0) {
                    ShowGoalTypeDialog1();
                } else {
                    DialogUtils.showPrompt(this.context, getString(R.string.str_no_goal_to_choose));
                }
            } else {
                DialogUtils.showPrompt(this.context, getString(R.string.str_no_goal_to_choose));
            }
            DbUtils.close(cursor);
            return;
        }
        ShowGoalTypeDialog1();
    }

    private void ShowGoalTypeDialog1() {
        CharSequence[] nameArr = new String[this.nameList.size()];
        for (int i = 0; i < this.nameList.size(); i++) {
            nameArr[i] = (String) this.nameList.get(i);
        }
        new Builder(this.context).setTitle(getString(R.string.str_select)).setItems(nameArr, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                AddActActivity_v2.this.BigGoal = ((Integer) AddActActivity_v2.this.idList.get(which)).intValue();
                AddActActivity_v2.this.updateUiIsSubGoal(AddActActivity_v2.this.BigGoal);
                dialog.cancel();
            }
        }).create().show();
    }

    private void showConfirmResetDialog() {
        new Builder(this.context).setTitle(getString(R.string.str_prompt)).setMessage(getString(R.string.str_reset_prompt)).setPositiveButton(getString(R.string.str_clear), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                AddActActivity_v2.this.resetData();
                GeneralUtils.toastShort(AddActActivity_v2.this.context, AddActActivity_v2.this.getString(R.string.str_successfully));
                dialog.dismiss();
            }
        }).setNegativeButton(getString(R.string.str_cancel), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        }).create().show();
    }

    private void resetData() {
        if (this.manuscriptId > 0) {
            Cursor cursor = DbUtils.getDb(this.context).rawQuery("select isUpload from t_act where isManuscript is 1 and id is " + this.manuscriptId, null);
            if (cursor.getCount() > 0) {
                cursor.moveToNext();
                ContentValues values;
                if (cursor.getInt(cursor.getColumnIndex("isUpload")) > 0) {
                    values = new ContentValues();
                    values.put("isDelete", Integer.valueOf(1));
                    DbUtils.getDb(this.context).update("t_act", values, "id is " + this.manuscriptId, null);
                } else {
                    Cursor cursor2 = DbUtils.getDb(this.context).rawQuery("select id from t_act_item where actId is " + this.manuscriptId + " limit 1", null);
                    if (cursor2.getCount() == 0) {
                        DbUtils.getDb(this.context).delete("t_act", "id is " + this.manuscriptId, null);
                        log("删除草稿目标!");
                    } else {
                        values = new ContentValues();
                        values.put("isDelete", Integer.valueOf(1));
                        DbUtils.getDb(this.context).update("t_act", values, "id is " + this.manuscriptId, null);
                    }
                    DbUtils.close(cursor2);
                }
            }
            DbUtils.close(cursor);
        }
        setResult(15);
        finish();
    }

    private void showSelectLevelDialog() {
        if (this.BigGoal > 0) {
            new Builder(this.context).setItems(spiner_data2, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    AddActActivity_v2.this.btn_add_level_change.setVisibility(0);
                    AddActActivity_v2.this.btn_edit_need_time.setVisibility(0);
                    AddActActivity_v2.this.tv_add_level_val.setText(AddActActivity_v2.spiner_data2[which]);
                    int hour = AddActActivity_v2.spiner_data2_int[which];
                    if (hour == 0) {
                        AddActActivity_v2.this.tv_add_needTime_val.setText("");
                        AddActActivity_v2.this.showEditNeedTimeDialog();
                        return;
                    }
                    AddActActivity_v2.this.tv_add_needTime_val.setText("" + hour);
                    AddActActivity_v2.this.tv_add_deadline_val.setText(DateTime.beforeNDays2Str(AddActActivity_v2.spiner_days2_int[which] - 1));
                    AddActActivity_v2.this.calTimeOfEveryDay();
                }
            }).setTitle(getResources().getString(R.string.str_select_type)).create().show();
        } else {
            new Builder(this.context).setItems(spiner_data, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    AddActActivity_v2.this.btn_add_level_change.setVisibility(0);
                    AddActActivity_v2.this.btn_edit_need_time.setVisibility(0);
                    AddActActivity_v2.this.tv_add_level_val.setText(AddActActivity_v2.spiner_data[which]);
                    int hour = AddActActivity_v2.spiner_data_int[which];
                    if (hour == 0) {
                        AddActActivity_v2.this.tv_add_needTime_val.setText("");
                        AddActActivity_v2.this.showEditNeedTimeDialog();
                        return;
                    }
                    AddActActivity_v2.this.tv_add_needTime_val.setText("" + hour);
                    AddActActivity_v2.this.tv_add_deadline_val.setText(DateTime.beforeNDays2Str(AddActActivity_v2.spiner_days_int[which] - 1));
                    AddActActivity_v2.this.calTimeOfEveryDay();
                    dialog.cancel();
                }
            }).setTitle(getResources().getString(R.string.str_select_type)).create().show();
        }
    }

    private void showEditLevelDialog() {
        final EditText et = new EditText(this.context);
        et.setBackgroundColor(getResources().getColor(R.color.white));
        et.setText(this.tv_add_level_val.getText().toString());
        new Builder(this.context).setView(et).setTitle(getResources().getString(R.string.str_edit_type)).setNegativeButton(getResources().getString(R.string.str_cancel), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).setPositiveButton(getResources().getString(R.string.str_sure), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                String str = et.getText().toString();
                String promt = AddActActivity_v2.this.getResources().getString(R.string.str_level_no_null);
                if (str == null || str.trim().length() == 0) {
                    GeneralHelper.toastShort(AddActActivity_v2.this.context, promt);
                    return;
                }
                AddActActivity_v2.this.tv_add_level_val.setText(str);
                AddActActivity_v2.this.closeInput();
                dialog.cancel();
            }
        }).create().show();
    }

    private void showEditEverydayNeedTimeDialog() {
        this.et_everyday_need_time_dialog = new EditText(this.context);
        this.et_everyday_need_time_dialog.setBackgroundColor(getResources().getColor(R.color.white));
        this.et_everyday_need_time_dialog.setInputType(8192);
        String str = this.tv_add_everyday_val.getText().toString();
        if (str != null) {
            str.replace(",", "");
            this.et_everyday_need_time_dialog.setText(str);
        }
        this.et_everyday_need_time_dialog.addTextChangedListener(this.EverydayWatcher);
        new Builder(this.context).setView(this.et_everyday_need_time_dialog).setTitle(getResources().getString(R.string.str_modify)).setNegativeButton(getResources().getString(R.string.str_cancel), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).setPositiveButton(getResources().getString(R.string.str_sure), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                String str = AddActActivity_v2.this.et_everyday_need_time_dialog.getText().toString();
                String promt = AddActActivity_v2.this.getResources().getString(R.string.str_no_null);
                if (str == null || str.trim().length() == 0) {
                    GeneralHelper.toastShort(AddActActivity_v2.this.context, promt);
                    return;
                }
                try {
                    Double needTime = Double.valueOf(Double.parseDouble(str));
                    if (needTime.doubleValue() > 20.0d) {
                        GeneralHelper.toastShort(AddActActivity_v2.this.context, AddActActivity_v2.this.getResources().getString(R.string.str_dont_more_than_20));
                    } else if (needTime.doubleValue() < 0.1d) {
                        GeneralHelper.toastShort(AddActActivity_v2.this.context, AddActActivity_v2.this.getResources().getString(R.string.str_dont_less_than_01));
                    } else {
                        AddActActivity_v2.this.tv_add_everyday_val.setText(str);
                        AddActActivity_v2.this.closeInput();
                        dialog.cancel();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    GeneralHelper.toastShort(AddActActivity_v2.this.context, AddActActivity_v2.this.getResources().getString(R.string.str_please_input_right_name));
                }
            }
        }).create().show();
    }

    private void showEditNeedTimeDialog() {
        this.et_need_time_dialog = new EditText(this.context);
        this.et_need_time_dialog.setBackgroundColor(getResources().getColor(R.color.white));
        this.et_need_time_dialog.setInputType(2);
        this.et_need_time_dialog.setText(this.tv_add_needTime_val.getText().toString());
        this.et_need_time_dialog.addTextChangedListener(this.NeetimeEditWatcher);
        new Builder(this.context).setView(this.et_need_time_dialog).setTitle(getResources().getString(R.string.str_need_time)).setNegativeButton(getResources().getString(R.string.str_cancel), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).setPositiveButton(getResources().getString(R.string.str_sure), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                String str = AddActActivity_v2.this.et_need_time_dialog.getText().toString();
                String promt = "";
                promt = AddActActivity_v2.this.getResources().getString(R.string.str_need_time_no_null);
                if (str == null || str.trim().length() == 0) {
                    GeneralHelper.toastShort(AddActActivity_v2.this.context, promt);
                    return;
                }
                try {
                    double needTime = Double.parseDouble(str);
                    if (needTime > 20000.0d) {
                        GeneralHelper.toastShort(AddActActivity_v2.this.context, AddActActivity_v2.this.getResources().getString(R.string.str_need_time_must_less_than_20000));
                    } else if (needTime < 1.0d) {
                        GeneralHelper.toastShort(AddActActivity_v2.this.context, AddActActivity_v2.this.getResources().getString(R.string.str_need_time_must_more_than_ten));
                    } else {
                        AddActActivity_v2.this.tv_add_needTime_val.setText(str);
                        AddActActivity_v2.this.closeInput();
                        dialog.cancel();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    GeneralHelper.toastShort(AddActActivity_v2.this.context, AddActActivity_v2.this.getResources().getString(R.string.str_please_input_right_name));
                }
            }
        }).create().show();
    }

    private void closeInput() {
        try {
            ((InputMethodManager) getSystemService("input_method")).hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 2);
        } catch (Exception e) {
            DbUtils.exceptionHandler(e);
        }
    }

    private void showPopDialog() {
        if (this.popup != null && this.popup.isShowing()) {
            this.popup.dismiss();
        }
        String needTime = this.tv_add_needTime_val.getText().toString();
        if (needTime == null || needTime.length() == 0) {
            GeneralHelper.toastShort(this.context, "请先选择级别！");
            return;
        }
        int i;
        try {
            ((InputMethodManager) getSystemService("input_method")).hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 2);
        } catch (Exception e) {
            DbUtils.exceptionHandler(e);
        }
        Calendar cal2 = Calendar.getInstance();
        int YEAR = cal2.get(1);
        int MONTH = cal2.get(2);
        int DAY_OF_MONTH = cal2.get(5);
        this.year_select = YEAR;
        for (i = 0; i <= 50; i++) {
            this.arr2yearMap.put(Integer.valueOf(i), Integer.valueOf(YEAR + i));
            this.year2arrMap.put(Integer.valueOf(YEAR + i), Integer.valueOf(i));
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
        this.tv_tem_time_needTime = (TextView) rl.findViewById(R.id.tv_tem_time_needTime);
        this.tv_tem_time_info = (TextView) rl.findViewById(R.id.tv_tem_time_info);
        WheelView wv_tem_year = (WheelView) rl.findViewById(R.id.wv_tem_year);
        wv_tem_year.setAdapter(new NumericWheelAdapter(YEAR, YEAR + 50, "%04d"));
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
            String hadSelectDate = this.tv_add_deadline_val.getText().toString();
            if (hadSelectDate.length() > 0) {
                Calendar tempCalendar = DateTime.pars2Calender(hadSelectDate + " 23:59:59");
                this.year_select = tempCalendar.get(1);
                wv_tem_year.setCurrentItem(((Integer) this.year2arrMap.get(Integer.valueOf(tempCalendar.get(1)))).intValue());
                wv_tem_month.setCurrentItem(((Integer) this.month2arrMap.get(Integer.valueOf(tempCalendar.get(2) + 1))).intValue());
                this.wv_tem_day.setCurrentItem(((Integer) this.day2arrMap.get(Integer.valueOf(tempCalendar.get(5)))).intValue());
            } else {
                wv_tem_year.setCurrentItem(((Integer) this.year2arrMap.get(Integer.valueOf(YEAR))).intValue());
                wv_tem_month.setCurrentItem(((Integer) this.month2arrMap.get(Integer.valueOf(MONTH + 1))).intValue());
                this.wv_tem_day.setCurrentItem(((Integer) this.day2arrMap.get(Integer.valueOf(DAY_OF_MONTH))).intValue());
            }
        } catch (Exception e2) {
            DbUtils.exceptionHandler(e2);
            wv_tem_year.setCurrentItem(((Integer) this.year2arrMap.get(Integer.valueOf(YEAR))).intValue());
            wv_tem_month.setCurrentItem(((Integer) this.month2arrMap.get(Integer.valueOf(MONTH + 1))).intValue());
            this.wv_tem_day.setCurrentItem(((Integer) this.day2arrMap.get(Integer.valueOf(DAY_OF_MONTH))).intValue());
        }
        Button btn_tem_time_cancel = (Button) rl.findViewById(R.id.btn_tem_time_cancel);
        ((Button) rl.findViewById(R.id.btn_tem_time_save)).setOnClickListener(this.addTimeOnClickListener);
        btn_tem_time_cancel.setOnClickListener(this.addTimeOnClickListener);
        this.popup = new PopupWindow(rl, -1, -2);
        this.popup.setOutsideTouchable(true);
        this.popup.showAtLocation(this.btn_add_save, 80, 0, 0);
    }

    private void calTimeOfEveryDay() {
        Calendar c = Calendar.getInstance();
        if (this.actId != null && this.actId.length() > 0) {
            c = DateTime.pars2Calender(DbUtils.getAct2ByActId(this.context, this.actId).getStartTime());
        }
        long now = c.getTimeInMillis();
        log("计算每天需要时间：" + this.year_select + "年" + this.month_select + "月" + this.day_select);
        if (this.day_select <= 0 || this.month_select <= 0 || this.year_select <= 2013) {
            calculateTime(this.tv_add_deadline_val.getText().toString());
            return;
        }
        c.set(1, this.year_select);
        c.set(2, this.month_select - 1);
        c.set(5, this.day_select);
        String dealineStr = DateTime.format(c, DateTime.DATE_FORMAT_LINE);
        log("选择的日期：" + dealineStr);
        long deadline = c.getTimeInMillis();
        long dif = deadline - now;
        this.tv_add_deadline_val.setText(dealineStr);
        if (dif < 0 || c.getTimeInMillis() <= Calendar.getInstance().getTimeInMillis()) {
            this.tv_add_everyday_val.setText("");
            this.tv_add_everyday_val_info.setVisibility(0);
            this.tv_add_everyday_val_info.setText("期限应是今天往后的时间哦！");
            return;
        }
        this.Cal_tempDays = ((dif / 1000) / 86400) + 1;
        String needTime = this.tv_add_needTime_val.getText().toString().trim();
        if (needTime.length() > 6) {
            GeneralHelper.toastShort(this.context, "需要时间过长哦，改小些吧！");
        } else if (needTime.length() == 0) {
            GeneralHelper.toastShort(this.context, "需要时间不能为空哦！");
        } else if (needTime != null) {
            this.calTempHours = Double.parseDouble(needTime) / ((double) this.Cal_tempDays);
            this.timeOfEveryday = (double) ((int) (this.calTempHours * 3600.0d));
            log("每天需要：" + this.timeOfEveryday + "秒");
            if (this.Cal_tempDays == 0) {
                this.tv_add_everyday_val.setText("");
                this.tv_add_everyday_val_info.setVisibility(0);
                this.tv_add_everyday_val_info.setText("期限应是今天往后的时间哦！");
            } else {
                this.tv_add_everyday_val.setText("" + FormatUtils.format_1fra(this.calTempHours));
            }
            if (this.calTempHours > 20.0d) {
                this.tv_add_everyday_val_info.setVisibility(0);
                if (DateTime.getDateString().equals(Long.valueOf(deadline))) {
                    this.tv_add_everyday_val_info.setText("期限应是今天往后的时间哦！");
                    return;
                } else {
                    this.tv_add_everyday_val_info.setText("每天投资应小于20小时，注意安排好休息时间!");
                    return;
                }
            }
            this.tv_add_everyday_val_info.setVisibility(8);
        }
    }

    private void calculateTime(String dealineStr) {
        try {
            log("选择的日期：" + dealineStr);
            long dif = DateTime.pars2Calender(dealineStr + " 23:59:59").getTimeInMillis() - Calendar.getInstance().getTimeInMillis();
            this.tv_add_deadline_val.setText(dealineStr);
            if (dif < 0) {
                this.tv_add_everyday_val.setText("");
                this.tv_add_everyday_val_info.setVisibility(0);
                this.tv_add_everyday_val_info.setText("期限应是今天往后的时间哦！");
                return;
            }
            this.Cal_tempDays = ((dif / 1000) / 86400) + 1;
            String needTime = this.tv_add_needTime_val.getText().toString().trim();
            if (needTime.length() > 6) {
                GeneralHelper.toastShort(this.context, "需要时间过长哦，改小些吧！");
            } else if (needTime.length() == 0) {
                GeneralHelper.toastShort(this.context, "需要时间不能为空哦！");
            } else if (needTime != null) {
                this.calTempHours = Double.parseDouble(needTime) / ((double) this.Cal_tempDays);
                this.timeOfEveryday = (double) ((int) (this.calTempHours * 3600.0d));
                log("每天需要：" + this.timeOfEveryday + "秒");
                if (this.Cal_tempDays == 0) {
                    this.tv_add_everyday_val.setText("");
                    this.tv_add_everyday_val_info.setVisibility(0);
                    this.tv_add_everyday_val_info.setText("期限应是今天往后的时间哦！");
                } else {
                    this.tv_add_everyday_val.setText("" + FormatUtils.format_1fra(this.calTempHours));
                }
                if (this.calTempHours > 20.0d) {
                    this.tv_add_everyday_val_info.setVisibility(0);
                    this.tv_add_everyday_val_info.setText("每天投资应小于20小时，注意安排好休息时间!");
                    return;
                }
                this.tv_add_everyday_val_info.setVisibility(8);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void log(String str) {
        Log.i(TAG, ":" + str);
    }

    public static void startActivity(Context context, String actId) {
        Intent it = new Intent(context, AddActActivity_v2.class);
        it.putExtra("ActId", actId);
        context.startActivity(it);
    }

    public static void startActivity(Context context, String actId, int isNotMantscript) {
        Intent it = new Intent(context, AddActActivity_v2.class);
        it.putExtra("ActId", actId);
        it.putExtra("isNotMantscript", isNotMantscript);
        context.startActivity(it);
    }

    public void onBackPressed() {
        saveTempData();
        if (this.popup != null && this.popup.isShowing()) {
            this.popup.dismiss();
        }
        super.onBackPressed();
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
}
