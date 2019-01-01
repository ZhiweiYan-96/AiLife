package com.record.myLife.main;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.record.bean.User;
import com.record.myLife.R;
import com.record.myLife.base.BaseActivity;
import com.record.myLife.settings.SetActivity;
import com.record.myLife.settings.label.LabelDetailActivity;
import com.record.myLife.view.dialog.AlertDialogM.Builder;
import com.record.utils.DateTime;
import com.record.utils.FormatUtils;
import com.record.utils.GeneralHelper;
import com.record.utils.GeneralUtils;
import com.record.utils.UserUtils;
import com.record.utils.Val;
import com.record.utils.db.DbUtils;
import com.umeng.analytics.MobclickAgent;

public class MeActivity extends BaseActivity {
    Button btn_label_back;
    Button btn_label_mood;
    Button btn_label_subtype;
    Context context;
    LayoutInflater inflater;
    ImageView iv_label_add;
    TextView iv_label_info_instruction;
    ImageView iv_laber_info_isopen;
    TextView iv_me_info_instrution;
    ImageView iv_today_set_v2;
    OnClickListener myClickListener = new OnClickListener() {
        @SuppressLint({"NewApi"})
        public void onClick(View v) {
            int id = v.getId();
            if (id == R.id.iv_today_set_v2) {
                MeActivity.this.startActivity(new Intent(MeActivity.this.context, SetActivity.class));
                MeActivity.this.overridePendingTransition(R.anim.push_to_left_in, R.anim.push_to_left_out);
                MobclickAgent.onEvent(MeActivity.this.getApplicationContext(), "my_click_Settings_btn");
            } else if (id == R.id.rl_me_name) {
                MeActivity.this.startActivity(new Intent(MeActivity.this.context, UserInfoActivity.class));
                MeActivity.this.overridePendingTransition(R.anim.push_to_left_in, R.anim.push_to_left_out);
                MobclickAgent.onEvent(MeActivity.this.getApplicationContext(), "my_My_information_activate_btn");
            } else if (id == R.id.btn_label_back) {
                MeActivity.this.finish();
                MeActivity.this.overridePendingTransition(R.anim.push_to_right_in, R.anim.push_to_right_out);
            } else if (id == R.id.iv_me_info_instrution) {
                new Builder(MeActivity.this.context).setTitle(MeActivity.this.getResources().getString(R.string.str_info)).setMessage(MeActivity.this.getResources().getString(R.string.str_me_instruction)).setPositiveButton(MeActivity.this.getResources().getString(R.string.str_sure), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                }).create().show();
                MobclickAgent.onEvent(MeActivity.this.getApplicationContext(), "my_question_mark_activate_btn");
            } else if (id == R.id.rl_goal_name_week) {
                String[] rankArr = Val.getWeeKNextRankNameAndTime(MeActivity.this.context, (int) MeActivity.this.weekInvest);
                double nextNeed = Double.parseDouble(rankArr[2]);
                if (nextNeed >= 0.0d) {
                    GeneralUtils.toastLong(MeActivity.this.context, MeActivity.this.getString(R.string.str_this_week_prompt).replace("{本周学习}", FormatUtils.format_1fra(MeActivity.this.weekInvest / 3600.0d)).replace("{下一级}", rankArr[1]).replace("{下一级小时}", FormatUtils.format_1fra(nextNeed / 3600.0d)));
                    return;
                }
                GeneralUtils.toastLong(MeActivity.this.context, MeActivity.this.getString(R.string.str_Congratulations_you_are_at_the_top));
            } else if (id == R.id.rl_me_today_use_rate_pre) {
//                GeneralUtils.toastShort(MeActivity.this.context, MeActivity.this.getString(R.string.str_use_rate_prompt2));
            }
        }
    };
    OnClickListener myClickListener2 = new OnClickListener() {
        public void onClick(View v) {
            String id = ((TextView) ((RelativeLayout) v).getChildAt(0)).getText().toString();
            Intent it = new Intent(MeActivity.this.context, LabelDetailActivity.class);
            it.putExtra("id", id);
            MeActivity.this.startActivity(it);
            MeActivity.this.overridePendingTransition(R.anim.push_to_left_in, R.anim.push_to_left_out);
        }
    };
    RelativeLayout rl_goal_name_week;
    RelativeLayout rl_me_name;
    RelativeLayout rl_me_today_use_rate_pre;
    Button set_btn_noti;
    TextView tv_me_Asset_class;
    TextView tv_me_invest_waste;
    TextView tv_me_nick;
    TextView tv_me_system_judge;
    TextView tv_me_system_week_judge;
    TextView tv_me_today_gain;
    TextView tv_me_today_use_rate;
    TextView tv_me_total_invest;
    TextView tv_me_userName;
    TextView tv_total_money;
    double weekInvest;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_me);
        this.context = this;
        this.inflater = getLayoutInflater();
        this.btn_label_back = (Button) findViewById(R.id.btn_label_back);
        this.iv_today_set_v2 = (ImageView) findViewById(R.id.iv_today_set_v2);
        this.tv_me_today_gain = (TextView) findViewById(R.id.tv_me_today_gain);
        this.tv_total_money = (TextView) findViewById(R.id.tv_total_money);
        this.tv_me_Asset_class = (TextView) findViewById(R.id.tv_me_Asset_class);
        this.tv_me_invest_waste = (TextView) findViewById(R.id.tv_me_invest_waste);
        this.tv_me_today_use_rate = (TextView) findViewById(R.id.tv_me_today_use_rate);
        this.tv_me_system_judge = (TextView) findViewById(R.id.tv_me_system_judge);
        this.tv_me_total_invest = (TextView) findViewById(R.id.tv_me_total_invest);
        this.iv_me_info_instrution = (TextView) findViewById(R.id.iv_me_info_instrution);
        this.tv_me_userName = (TextView) findViewById(R.id.tv_me_userName);
        this.tv_me_system_week_judge = (TextView) findViewById(R.id.tv_me_system_week_judge);
        this.tv_me_nick = (TextView) findViewById(R.id.tv_me_nick);
        this.rl_me_name = (RelativeLayout) findViewById(R.id.rl_me_name);
        this.rl_goal_name_week = (RelativeLayout) findViewById(R.id.rl_goal_name_week);
        this.rl_me_today_use_rate_pre = (RelativeLayout) findViewById(R.id.rl_me_today_use_rate_pre);
        this.iv_today_set_v2.setOnClickListener(this.myClickListener);
        this.btn_label_back.setOnClickListener(this.myClickListener);
        this.rl_me_name.setOnClickListener(this.myClickListener);
        this.iv_me_info_instrution.setOnClickListener(this.myClickListener);
        this.rl_goal_name_week.setOnClickListener(this.myClickListener);
        this.rl_me_today_use_rate_pre.setOnClickListener(this.myClickListener);
        initUI();
    }

    private void initUI() {
        UserUtils.isLoginUser(this.context);
        User user = User.getInstance();
        if (user != null) {
            if (user.getNickname() == null || user.getNickname().length() <= 0 || user.getNickname().toLowerCase().contains("null")) {
                this.tv_me_nick.setText("");
                this.tv_me_nick.setHint(getResources().getString(R.string.str_Add_your_name));
            } else {
                this.tv_me_nick.setText(user.getNickname());
            }
            String userName = user.getUserName();
            if (userName == null || userName.length() <= 0 || userName.equalsIgnoreCase("null") || userName.equals("测试")) {
                this.tv_me_userName.setText("");
                this.tv_me_userName.setVisibility(View.GONE);
            } else {
                this.tv_me_userName.setVisibility(View.VISIBLE);
                this.tv_me_userName.setText(user.getUserName());
            }
        } else {
            this.tv_me_nick.setHint(getResources().getString(R.string.str_Add_your_name));
            this.tv_me_userName.setVisibility(View.INVISIBLE);
        }
        Cursor cursor5 = DbUtils.getDb(this.context).rawQuery("select earnMoney from t_allocation where " + DbUtils.getWhereUserId(this.context) + " and time is '" + DateTime.getDateString() + "'", null);
        if (cursor5.getCount() > 0) {
            cursor5.moveToNext();
            this.tv_me_today_gain.setText(FormatUtils.format_1fra(cursor5.getDouble(cursor5.getColumnIndex("earnMoney"))));
        } else {
            this.tv_me_today_gain.setText("0");
        }
        DbUtils.close(cursor5);
        double tempUnuploadInvest = 0.0d;
        Cursor cursor3 = DbUtils.getDb(this.context).rawQuery("select sum(take) from t_act_item where " + DbUtils.getWhereUserId(this.context) + " and isUpload is not 1 and isDelete is not 1 and actType = 11 or actType = 10", null);
        if (cursor3.getCount() > 0) {
            cursor3.moveToNext();
            tempUnuploadInvest = cursor3.getDouble(cursor3.getColumnIndex("sum(take)"));
        }
        DbUtils.close(cursor3);
        Cursor cursor3_1 = DbUtils.getDb(this.context).rawQuery("select investment from t_user where id = " + DbUtils.queryUserId(this.context), null);
        if (cursor3_1.getCount() > 0) {
            cursor3_1.moveToNext();
            tempUnuploadInvest += cursor3_1.getDouble(cursor3_1.getColumnIndex("investment"));
        }
        DbUtils.close(cursor3_1);
        this.tv_me_total_invest.setText(FormatUtils.format_1fra(tempUnuploadInvest / 3600.0d) + "h");
        this.tv_total_money.setText(FormatUtils.format_1fra((tempUnuploadInvest / 3600.0d) * 2.0d));
        Cursor cursor6 = DbUtils.getDb(this.context).rawQuery("select sum(invest) from t_allocation where " + DbUtils.getWhereUserId(this.context) + " and time >= '" + DateTime.getDateOfWeekStart(getSharedPreferences(Val.CONFIGURE_NAME, 0).getInt(Val.CONFIGURE_START_DATE_OF_WEEK, 2)) + "'", null);
        if (cursor6.getCount() > 0) {
            cursor6.moveToNext();
            this.weekInvest = cursor6.getDouble(cursor6.getColumnIndex("sum(invest)"));
            log("本周投资" + this.weekInvest);
            this.tv_me_system_week_judge.setText(Val.getWeeKNextRankNameAndTime(this.context, (int) this.weekInvest)[0] + getString(R.string.str_level));
        }
        DbUtils.close(cursor6);
        Cursor cursor2 = DbUtils.getDb(this.context).rawQuery("select * from t_allocation where " + DbUtils.getWhereUserId(this.context) + " and time is '" + DateTime.getDateString() + "'", null);
        if (cursor2.getCount() > 0) {
            cursor2.moveToNext();
            int invest = cursor2.getInt(cursor2.getColumnIndex("invest"));
            int waste = cursor2.getInt(cursor2.getColumnIndex("waste"));
            if (invest < 0) {
                invest = 0;
            }
            if (waste < 0) {
                waste = 0;
            }
            double investD = ((double) invest) / 3600.0d;
            double wasteD = ((double) waste) / 3600.0d;
            double rate = investD / (wasteD + investD);
            if (wasteD + investD == 0.0d) {
                rate = 0.0d;
            }
            log(investD + ",,(wasteD+investD)" + (wasteD + investD));
            this.tv_me_invest_waste.setText(FormatUtils.format_1fra(investD) + "/" + FormatUtils.format_1fra(wasteD) + "h");
            this.tv_me_today_use_rate.setText(FormatUtils.format_1fra(100.0d * rate) + "%");
            this.tv_me_system_judge.setText(getLearnRate(this.context, 100.0d * rate) + getString(R.string.str_level));
        } else {
            this.tv_me_invest_waste.setText("0/0h");
            this.tv_me_today_use_rate.setText("-");
            this.tv_me_system_judge.setText("-");
        }
        DbUtils.close(cursor2);
    }

    private static String getLearnRate(Context context, double rate) {
        String rateName = "";
        if (rate > 95.0d) {
            return context.getResources().getString(R.string.str_rate1);
        }
        if (rate >= 90.0d && rate < 95.0d) {
            return context.getResources().getString(R.string.str_rate2);
        }
        if (rate >= 70.0d && rate < 90.0d) {
            return context.getResources().getString(R.string.str_rate3);
        }
        if (rate >= 60.0d && rate < 70.0d) {
            return context.getResources().getString(R.string.str_rate4);
        }
        if (rate >= 50.0d && rate < 60.0d) {
            return context.getResources().getString(R.string.str_rate5);
        }
        if (rate >= 40.0d && rate < 50.0d) {
            return context.getResources().getString(R.string.str_rate6);
        }
        if (rate >= 30.0d && rate < 40.0d) {
            return context.getResources().getString(R.string.str_rate7);
        }
        if (rate >= 20.0d && rate < 30.0d) {
            return context.getResources().getString(R.string.str_rate8);
        }
        if (rate >= 5.0d && rate < 20.0d) {
            return context.getResources().getString(R.string.str_rate9);
        }
        if (rate < 0.0d || rate >= 5.0d) {
            return rateName;
        }
        return context.getResources().getString(R.string.str_rate10);
    }

    public void addName(String editString) {
        final EditText et = new EditText(this.context);
        et.setBackgroundColor(getResources().getColor(R.color.white));
        et.setHint(getResources().getString(R.string.str_your_name));
        et.setText(editString);
        new Builder(this.context).setView(et).setTitle(getResources().getString(R.string.str_add_or_modify)).setNegativeButton(getResources().getString(R.string.str_cancel), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).setPositiveButton(getResources().getString(R.string.str_sure), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                String str = et.getText().toString();
                if (str == null || str.trim().length() == 0) {
                    GeneralHelper.toastShort(MeActivity.this.context, MeActivity.this.getResources().getString(R.string.str_prompt_no_null));
                    return;
                }
                ContentValues values = new ContentValues();
                values.put("nickName", str);
                values.put("endUpdateTime", DateTime.getTimeString());
                DbUtils.getDb(MeActivity.this.context).update("t_user", values, " id is ? ", new String[]{DbUtils.queryUserId(MeActivity.this.context)});
                MeActivity.this.tv_me_nick.setText(str);
                UserUtils.isLoginUser(MeActivity.this.context);
                dialog.cancel();
            }
        }).create().show();
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void onBackPressed() {
        setResult(-1);
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

    public void log(String str) {
        Log.i("override Login", ":" + str);
    }
}
