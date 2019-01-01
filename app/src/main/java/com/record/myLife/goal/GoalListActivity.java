package com.record.myLife.goal;

import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.record.bean.Act;
import com.record.myLife.R;
import com.record.myLife.base.BaseActivity;
import com.record.myLife.main.AddActActivity_v2;
import com.record.myLife.view.dialog.AlertDialogM.Builder;
import com.record.service.SystemBarTintManager;
import com.record.service.TimerService;
import com.record.thread.StaticsGoalRunnable;
import com.record.utils.DateTime;
import com.record.utils.FormatUtils;
import com.record.utils.GeneralUtils;
import com.record.utils.ShowGuideImgUtils;
import com.record.utils.Sql;
import com.record.utils.Val;
import com.record.utils.db.DbUtils;
import com.umeng.analytics.MobclickAgent;
import com.umeng.analytics.onlineconfig.a;
import java.util.Calendar;

public class GoalListActivity extends BaseActivity implements OnLongClickListener {
    static String TAG = "override";
    int CURRENT_SELECT = 1;
    int SELECT_DELETE = 3;
    int SELECT_FINISH = 2;
    int SELECT_GOALS = 1;
    Thread StaticsGoalThread = null;
    Button btn_goal_list_deleted;
    Button btn_goal_list_finished;
    Button btn_goal_list_under_way;
    Button btn_set_back;
    Button btn_sort;
    Context context;
    LayoutInflater inflater;
    LinearLayout ll_goal_list_items;
    OnClickListener myClickListener = new OnClickListener() {
        public void onClick(View v) {
            int id = v.getId();
            if (id == R.id.btn_goal_list_under_way) {
                GoalListActivity.this.CURRENT_SELECT = 1;
                GoalListActivity.this.setButtonBg(GoalListActivity.this.CURRENT_SELECT);
                GoalListActivity.this.initUI(GoalListActivity.this.getWhere(GoalListActivity.this.CURRENT_SELECT));
            } else if (id == R.id.btn_goal_list_finished) {
                GoalListActivity.this.CURRENT_SELECT = 2;
                GoalListActivity.this.setButtonBg(GoalListActivity.this.CURRENT_SELECT);
                GoalListActivity.this.initUI(GoalListActivity.this.getWhere(GoalListActivity.this.CURRENT_SELECT));
            } else if (id == R.id.btn_goal_list_deleted) {
                GoalListActivity.this.CURRENT_SELECT = 3;
                GoalListActivity.this.setButtonBg(GoalListActivity.this.CURRENT_SELECT);
                GoalListActivity.this.initUI(GoalListActivity.this.getWhere(GoalListActivity.this.CURRENT_SELECT));
            } else if (id == R.id.btn_set_back) {
                GoalListActivity.this.finish();
            } else if (id == R.id.iv_goal_is_show) {
                GoalListActivity.this.changeViewIsHided(v);
                GoalListActivity.this.setResult(-1);
            } else if (id == R.id.rl_goal_list_outer) {
                GoalListActivity.this.startActivity(v);
                GoalListActivity.this.setResult(-1);
            } else if (id == R.id.btn_sort) {
                GoalSortActivity.startActivity(GoalListActivity.this.context);
            }
        }
    };
    Handler myhHandler = new Handler() {
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (GoalListActivity.this.CURRENT_SELECT == 1) {
                GoalListActivity.this.setButtonBg(1);
                GoalListActivity.this.initUI(GoalListActivity.this.getWhere(1));
            }
        }
    };
    boolean overDealine = false;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goal_list);
        SystemBarTintManager.setMIUIbar(this);
        this.context = this;
        this.inflater = getLayoutInflater();
        TAG += getClass().getSimpleName();
        this.ll_goal_list_items = (LinearLayout) findViewById(R.id.ll_goal_list_items);
        this.btn_goal_list_under_way = (Button) findViewById(R.id.btn_goal_list_under_way);
        this.btn_goal_list_finished = (Button) findViewById(R.id.btn_goal_list_finished);
        this.btn_goal_list_deleted = (Button) findViewById(R.id.btn_goal_list_deleted);
        this.btn_set_back = (Button) findViewById(R.id.btn_set_back);
        this.btn_sort = (Button) findViewById(R.id.btn_sort);
        this.btn_goal_list_deleted.setOnClickListener(this.myClickListener);
        this.btn_goal_list_finished.setOnClickListener(this.myClickListener);
        this.btn_goal_list_under_way.setOnClickListener(this.myClickListener);
        this.btn_set_back.setOnClickListener(this.myClickListener);
        this.btn_sort.setOnClickListener(this.myClickListener);
        this.CURRENT_SELECT = this.SELECT_GOALS;
        setButtonBg(this.CURRENT_SELECT);
        initUI(getWhere(this.CURRENT_SELECT));
        startStaticsThread();
    }

    private void startStaticsThread() {
        if (this.StaticsGoalThread == null) {
            this.StaticsGoalThread = new Thread(new StaticsGoalRunnable(this.context));
            this.StaticsGoalThread.start();
            new Thread(new Runnable() {
                public void run() {
                    while (GoalListActivity.this.StaticsGoalThread != null && GoalListActivity.this.StaticsGoalThread.isAlive()) {
                        try {
                            Thread.sleep(500);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    GoalListActivity.this.myhHandler.sendEmptyMessage(1);
                }
            }).start();
        }
    }

    private void initUI(String where) {
        Cursor cursor = DbUtils.getDb(this.context).rawQuery(Sql.goalGetGoalListForUiShown_containHide(this.context, where), null);
        this.ll_goal_list_items.removeAllViews();
        if (cursor.getCount() > 0) {
            ShowGuideImgUtils.showImage(this.context, Val.CONFIGURE_IS_SHOW_GUIDE_GOALS_LIST, 1, R.drawable.guide_goals);
            while (cursor.moveToNext()) {
                int id = getInt(cursor, "id");
                String color = getStr(cursor, "color");
                String image = getStr(cursor, "image");
                String actName = getStr(cursor, "actName");
                String intruction = getStr(cursor, "intruction");
                String startTime = getStr(cursor, "startTime");
                String createTime = getStr(cursor, "createTime");
                String deadline = getStr(cursor, "deadline");
                int expectSpend = getInt(cursor, "expectSpend");
                int isFinish = getInt(cursor, "isFinish");
                int isDelete = getInt(cursor, "isDelete");
                int isHided = getInt(cursor, "isHided");
                int type = getInt(cursor, a.a);
                String finishTime = getStr(cursor, "finishTime");
                String str = getStr(R.string.str_under_way);
                if (isDelete > 0) {
                    str = getStr(R.string.str_deleted);
                }
                if (isFinish > 0) {
                    str = getStr(R.string.str_finished);
                }
                double hadInvest = DbUtils.queryStaticsHadInvestByGoalId(this.context, id);
                View rl = (RelativeLayout) this.inflater.inflate(R.layout.tem_goal_list, null);
                RelativeLayout rl_goal_list_outer = (RelativeLayout) rl.findViewById(R.id.rl_goal_list_outer);
                TextView tv_goal_name = (TextView) rl.findViewById(R.id.tv_goal_name);
                TextView tv_goal_startTime = (TextView) rl.findViewById(R.id.tv_goal_startTime);
                TextView tv_goal_list_create_date = (TextView) rl.findViewById(R.id.tv_goal_list_create_date);
                TextView tv_goal_list_deadline = (TextView) rl.findViewById(R.id.tv_goal_list_deadline);
                TextView tv_goal_list_used_time = (TextView) rl.findViewById(R.id.tv_goal_list_used_time);
                TextView tv_goal_list_status = (TextView) rl.findViewById(R.id.tv_goal_list_status);
                ImageView iv_goal_show_label_bg = (ImageView) rl.findViewById(R.id.iv_goal_show_label_bg);
                ImageView iv_goal_show_label = (ImageView) rl.findViewById(R.id.iv_goal_show_label);
                ImageView iv_goal_is_show = (ImageView) rl.findViewById(R.id.iv_goal_is_show);
                ((TextView) rl.findViewById(R.id.tv_goal_list_id)).setText(id + "");
                iv_goal_show_label.setImageResource(Val.getLabelIntByName(image));
                if (isDelete > 0 || isFinish > 0) {
                    iv_goal_is_show.setVisibility(8);
                    rl_goal_list_outer.setOnClickListener(this.myClickListener);
                } else {
                    iv_goal_is_show.setVisibility(0);
                    iv_goal_is_show.setOnClickListener(this.myClickListener);
                    rl_goal_list_outer.setOnClickListener(this.myClickListener);
                    if (isHided > 0) {
                        iv_goal_is_show.setImageResource(R.drawable.ic_off_v2);
                    } else if (isHided == 0) {
                        iv_goal_is_show.setImageResource(R.drawable.ic_on_v2);
                    }
                }
                iv_goal_show_label_bg.setImageResource(((Integer) Val.col_Str2xml_circle_Int_Map.get(color)).intValue());
                if (actName == null || actName.length() <= 0) {
                    tv_goal_name.setText("");
                } else {
                    tv_goal_name.setText(actName);
                }
                if (intruction == null || intruction.length() <= 0) {
                    tv_goal_startTime.setText("");
                } else {
                    tv_goal_startTime.setText(intruction);
                }
                if (expectSpend > 0) {
                    if (startTime == null || startTime.length() <= 0) {
                        tv_goal_list_create_date.setText(getStr(R.string.str_create_date));
                    } else {
                        tv_goal_list_create_date.setText(getStr(R.string.str_create_date) + startTime.substring(0, startTime.indexOf(" ")));
                    }
                    if (deadline == null || deadline.length() <= 0) {
                        tv_goal_list_deadline.setText(getStr(R.string.str_deadline_date));
                    } else {
                        tv_goal_list_deadline.setText(getStr(R.string.str_deadline_date) + deadline.substring(0, deadline.indexOf(" ")));
                    }
                    if (expectSpend > 0) {
                        tv_goal_list_used_time.setText(getStr(R.string.str_used_time) + FormatUtils.format_1fra(hadInvest / 3600.0d) + "/" + FormatUtils.format_1fra((double) (expectSpend / 3600)) + "h " + getStr(R.string.str_pratical_expend));
                    } else {
                        tv_goal_list_used_time.setText(getStr(R.string.str_used_time));
                    }
                    tv_goal_list_status.setText(getStr(R.string.str_goal_status) + str);
                } else {
                    if (createTime == null || createTime.length() <= 0) {
                        tv_goal_list_create_date.setVisibility(8);
                    } else {
                        tv_goal_list_create_date.setText(getStr(R.string.str_create_date) + createTime.substring(0, createTime.indexOf(" ")));
                    }
                    tv_goal_list_deadline.setVisibility(8);
                    tv_goal_list_used_time.setText(getStr(R.string.str_used_time) + FormatUtils.format_1fra(hadInvest / 3600.0d) + "h");
                    tv_goal_list_status.setText(getStr(R.string.str_goal_status) + str);
                }
                if (isFinish > 0) {
                    rl_goal_list_outer.setOnLongClickListener(this);
                }
                this.ll_goal_list_items.addView(rl);
            }
        } else {
            this.ll_goal_list_items.addView((RelativeLayout) this.inflater.inflate(R.layout.tem_text_for_goal_list, null));
        }
        DbUtils.close(cursor);
    }

    private void startActivity(View v) {
        String id = ((TextView) ((RelativeLayout) v).getChildAt(0)).getText().toString();
        if (DbUtils.queryIsDeleteByGoalId(this.context, id) > 0) {
            showIsRenewGoalDialog(id);
        } else if (id != null && id.length() > 0) {
            Intent it = new Intent(this.context, GoalActivity.class);
            it.putExtra("id", id);
            startActivityForResult(it, 14);
        }
    }

    private void showIsRenewGoalDialog(final String id) {
        String msg;
        String goalName = "";
        String deadline = "";
        String deleteTime = "";
        int hadSpend = 0;
        Cursor cursor = DbUtils.getDb(this.context).rawQuery("select * from t_act where id is " + id, null);
        if (cursor.getCount() > 0) {
            cursor.moveToNext();
            goalName = cursor.getString(cursor.getColumnIndex("actName"));
            hadSpend = cursor.getInt(cursor.getColumnIndex("hadSpend"));
            deadline = cursor.getString(cursor.getColumnIndex("deadline"));
            deleteTime = cursor.getString(cursor.getColumnIndex("deleteTime"));
        }
        DbUtils.close(cursor);
        this.overDealine = false;
        String msg2 = "";
        if (deadline == null || deadline.length() <= 0 || Calendar.getInstance().getTime().getTime() < DateTime.pars2Calender(deadline).getTime().getTime()) {
            this.overDealine = false;
            msg2 = getString(R.string.str_renew_goal_msg);
            if (deleteTime == null || deleteTime.length() <= 0) {
                msg = msg2.replace("{日期}", getStr(R.string.str_the_day_before));
            } else {
                msg = msg2.replace("{日期}", DateTime.getDateFromTimestamp(deleteTime));
            }
            try {
                msg = msg.replace("{投资时间}", FormatUtils.format_1fra((double) (hadSpend / 3600)));
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                msg = msg.replace("{目标名字}", "\"" + goalName + "\"");
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        } else {
            this.overDealine = true;
            msg2 = getString(R.string.str_renew_goal_msg_2);
            if (deleteTime == null || deleteTime.length() <= 0) {
                msg = msg2.replace("{日期}", getStr(R.string.str_the_day_before));
            } else {
                msg = msg2.replace("{日期}", DateTime.getDateFromTimestamp(deleteTime));
            }
            try {
                msg = msg.replace("{投资时间}", FormatUtils.format_1fra((double) (hadSpend / 3600)));
            } catch (Exception e22) {
                e22.printStackTrace();
            }
            try {
                msg = msg.replace("{目标名字}", "\"" + goalName + "\"");
            } catch (Exception e222) {
                e222.printStackTrace();
            }
        }
        new Builder(this.context).setTitle(getString(R.string.str_is_renew_goal)).setMessage(msg).setPositiveButton(this.context.getString(R.string.str_renew), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                if (GoalListActivity.this.overDealine) {
                    Intent it = new Intent(GoalListActivity.this.context, AddActActivity_v2.class);
                    it.putExtra("ActId", id + "");
                    GoalListActivity.this.startActivityForResult(it, 5);
                } else {
                    ContentValues values = new ContentValues();
                    values.put("isDelete", Integer.valueOf(0));
                    values.put("endUpdateTime", DateTime.getTimeString());
                    DbUtils.getDb(GoalListActivity.this.context).update("t_act", values, "id is " + id, null);
                    GoalListActivity.this.initUI(GoalListActivity.this.getWhere(GoalListActivity.this.CURRENT_SELECT));
                }
                dialog.cancel();
            }
        }).setNegativeButton(getString(R.string.str_cancel), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        }).create().show();
    }

    private void changeViewIsHided(View v) {
        String id = ((TextView) ((RelativeLayout) v.getParent().getParent()).getChildAt(0)).getText().toString();
        if (id != null && id.length() > 0) {
            int isHided = DbUtils.queryIsHideByGoalId(this.context, id);
            if (isHided > 0) {
                ((ImageView) v).setImageResource(R.drawable.ic_on_v2);
                updateDbIsHide(0, id);
                GeneralUtils.toastShort(this.context, getString(R.string.str_goal_show_no_main_prompt));
            } else if (isHided == 0) {
                try {
                    Act act = Act.getInstance();
                    if (!(TimerService.timer == null || act == null || act.getId() != Integer.parseInt(id))) {
                        GeneralUtils.toastShort(this.context, getString(R.string.str_goal_is_counting_cant_hide_prompt));
                        return;
                    }
                } catch (Exception e) {
                    DbUtils.exceptionHandler(e);
                }
                ((ImageView) v).setImageResource(R.drawable.ic_off_v2);
                updateDbIsHide(1, id);
                GeneralUtils.toastShort(this.context, getString(R.string.str_goal_not_show_on_main_prompt));
            }
        }
    }

    private void updateDbIsHide(int isHide, String goalId) {
        ContentValues values = new ContentValues();
        values.put("isHided", Integer.valueOf(isHide));
        values.put("endUpdateTime", DateTime.getTimeString());
        DbUtils.getDb(this.context).update("t_act", values, "id is " + goalId, null);
    }

    private void setButtonBg(int type) {
        if (type == this.SELECT_GOALS) {
            this.btn_goal_list_under_way.setBackgroundResource(R.drawable.sel_blue2white);
            this.btn_goal_list_finished.setBackgroundResource(R.drawable.sel_white2blue);
            this.btn_goal_list_deleted.setBackgroundResource(R.drawable.sel_white2blue);
        } else if (type == this.SELECT_FINISH) {
            this.btn_goal_list_under_way.setBackgroundResource(R.drawable.sel_white2blue);
            this.btn_goal_list_finished.setBackgroundResource(R.drawable.sel_blue2white);
            this.btn_goal_list_deleted.setBackgroundResource(R.drawable.sel_white2blue);
        } else if (type == this.SELECT_DELETE) {
            this.btn_goal_list_under_way.setBackgroundResource(R.drawable.sel_white2blue);
            this.btn_goal_list_finished.setBackgroundResource(R.drawable.sel_white2blue);
            this.btn_goal_list_deleted.setBackgroundResource(R.drawable.sel_blue2white);
        }
    }

    private String getWhere(int type) {
        String where = "";
        if (type == this.SELECT_GOALS) {
            return "and isFinish is not 1 and isDelete is not 1";
        }
        if (type == this.SELECT_FINISH) {
            return "and isFinish is  1";
        }
        if (type == this.SELECT_DELETE) {
            return "and isDelete is  1";
        }
        return where;
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 7 || resultCode == -1) {
            initUI(getWhere(this.CURRENT_SELECT));
            setResult(-1);
        }
    }

    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    public String getStr(int str) {
        return getResources().getString(str);
    }

    public static String getStr(Cursor cursor, String column) {
        return cursor.getString(cursor.getColumnIndex(column));
    }

    public static int getInt(Cursor cursor, String column) {
        return cursor.getInt(cursor.getColumnIndex(column));
    }

    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    public void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

    public static void log(String str) {
        Log.i(TAG, ":" + str);
    }

    public boolean onLongClick(View v) {
        final String id = ((TextView) ((RelativeLayout) v).getChildAt(0)).getText().toString();
        new Builder(this.context).setTitle(getString(R.string.str_restart_finish_goal)).setPositiveButton(this.context.getString(R.string.str_activate), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                try {
                    Cursor cursor = DbUtils.getDb(GoalListActivity.this.context).rawQuery("select * from t_act where id = " + id, null);
                    if (cursor.getCount() > 0) {
                        cursor.moveToNext();
                        int isSubGoal = cursor.getInt(cursor.getColumnIndex("isSubGoal"));
                        if (isSubGoal > 0) {
                            Cursor cursor2 = DbUtils.getDb(GoalListActivity.this.context).rawQuery("select * from t_act where id = " + isSubGoal, null);
                            if (cursor2.getCount() > 0) {
                                cursor2.moveToNext();
                                int isFinish = cursor2.getInt(cursor2.getColumnIndex("isFinish"));
                                String actName = cursor2.getString(cursor2.getColumnIndex("actName"));
                                if (isFinish > 0) {
                                    GoalListActivity.this.showDailogActivateGoalAndParentGoal(Integer.parseInt(id), isSubGoal, actName);
                                } else {
                                    GoalListActivity.this.activateGoal(id);
                                    GoalListActivity.this.initUI(GoalListActivity.this.getWhere(GoalListActivity.this.CURRENT_SELECT));
                                    GoalListActivity.this.setResult(-1);
                                }
                            }
                            DbUtils.close(cursor2);
                        } else {
                            GoalListActivity.this.activateGoal(id);
                            GoalListActivity.this.initUI(GoalListActivity.this.getWhere(GoalListActivity.this.CURRENT_SELECT));
                            GoalListActivity.this.setResult(-1);
                        }
                    }
                    DbUtils.close(cursor);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                dialog.cancel();
            }
        }).setNegativeButton(getString(R.string.str_cancel), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        }).create().show();
        return false;
    }

    private void showDailogActivateGoalAndParentGoal(final int id, final int parentid, String parentname) {
        new Builder(this.context).setTitle(getString(R.string.str_restart_finish_goal)).setMessage("提示：当前目标为子目标，其大目标(" + parentname + ")也已完成，重新激活当前目标会改变目标的附属状态(即变成大目标/习惯)!").setPositiveButton((CharSequence) "激活当前与大目标", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                GoalListActivity.this.activateGoalAndParentGoal(id, parentid);
                GoalListActivity.this.initUI(GoalListActivity.this.getWhere(GoalListActivity.this.CURRENT_SELECT));
                GoalListActivity.this.setResult(-1);
                dialog.cancel();
            }
        }).setNeutralButton((CharSequence) "只激活当前目标", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                GoalListActivity.this.activateGoalAndChangeSubStatus(id);
                GoalListActivity.this.initUI(GoalListActivity.this.getWhere(GoalListActivity.this.CURRENT_SELECT));
                GoalListActivity.this.setResult(-1);
                dialog.cancel();
            }
        }).setNegativeButton((CharSequence) "我再想想", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        }).create().show();
    }

    private void activateGoalAndParentGoal(int id, int parentid) {
        ContentValues values = new ContentValues();
        values.put("isFinish", Integer.valueOf(0));
        values.put("endUpdateTime", DateTime.getTimeString());
        DbUtils.getDb(this.context).update("t_act", values, "id is " + id, null);
        ContentValues values2 = new ContentValues();
        values2.put("isFinish", Integer.valueOf(0));
        values2.put("endUpdateTime", DateTime.getTimeString());
        DbUtils.getDb(this.context).update("t_act", values2, "id is " + parentid, null);
    }

    private void activateGoalAndChangeSubStatus(int id) {
        ContentValues values = new ContentValues();
        values.put("isFinish", Integer.valueOf(0));
        values.put("isSubGoal", Integer.valueOf(0));
        values.put("endUpdateTime", DateTime.getTimeString());
        DbUtils.getDb(this.context).update("t_act", values, "id is " + id, null);
    }

    private void activateGoal(String id) {
        ContentValues values = new ContentValues();
        values.put("isFinish", Integer.valueOf(0));
        values.put("endUpdateTime", DateTime.getTimeString());
        DbUtils.getDb(this.context).update("t_act", values, "id is " + id, null);
    }
}
