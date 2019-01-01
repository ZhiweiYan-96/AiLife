package com.record.myLife.view;

import android.annotation.SuppressLint;
import android.app.NotificationManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import com.record.myLife.R;
import com.record.myLife.base.BaseActivity;
import com.record.utils.DateTime;
import com.record.utils.GeneralHelper;
import com.record.utils.PreferUtils;
import com.record.utils.Val;
import com.record.utils.db.DbUtils;
import com.umeng.analytics.MobclickAgent;

public class AddNoteActivity extends BaseActivity {
    String Date = "";
    String action = "";
    Context context;
    EditText et_add_note_content;
    ImageView iv_add_note_check;
    ImageView iv_add_note_close;
    OnClickListener myClickListener = new OnClickListener() {
        public void onClick(View v) {
            int id = v.getId();
            if (id == R.id.iv_add_note_close) {
                AddNoteActivity.this.finish();
            } else if (id != R.id.iv_add_note_check) {
            } else {
                String note;
                ContentValues values;
                if (AddNoteActivity.this.action == null || AddNoteActivity.this.action.length() == 0 || Val.INTENT_ACTION_NOTI_RESTROPECTION_REGISTER.equals(AddNoteActivity.this.action)) {
                    note = AddNoteActivity.this.et_add_note_content.getText().toString().trim();
                    values = new ContentValues();
                    values.put("remarks", AddNoteActivity.this.et_add_note_content.getText().toString().trim());
                    DbUtils.insertOrUpdateDb_allocation(AddNoteActivity.this.context, AddNoteActivity.this.Date, values);
                    if (note != null && note.length() > 0) {
                        GeneralHelper.toastShort(AddNoteActivity.this.context, AddNoteActivity.this.getString(R.string.str_add_success));
                    }
                    AddNoteActivity.this.setResult(1);
                    AddNoteActivity.this.finish();
                } else if (AddNoteActivity.this.action.equals(Val.INTENT_ACTION_NOTI_MORNING_VOICE)) {
                    note = AddNoteActivity.this.et_add_note_content.getText().toString().trim();
                    values = new ContentValues();
                    values.put("morningVoice", AddNoteActivity.this.et_add_note_content.getText().toString().trim());
                    DbUtils.insertOrUpdateDb_allocation(AddNoteActivity.this.context, AddNoteActivity.this.Date, values);
                    if (note != null && note.length() > 0) {
                        GeneralHelper.toastShort(AddNoteActivity.this.context, AddNoteActivity.this.getString(R.string.str_add_success));
                    }
                    AddNoteActivity.this.setResult(1);
                    AddNoteActivity.this.finish();
                }
            }
        }
    };
    TextView tv_add_note_last_content;
    TextView tv_add_note_title;

    @SuppressLint({"NewApi"})
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_note);
        this.context = this;
        initView();
        init();
        clearNoti(this.context);
    }

    private void clearNoti(Context context) {
        try {
            ((NotificationManager) context.getSystemService("notification")).cancel(2);
        } catch (Exception e) {
            DbUtils.exceptionHandler(e);
        }
    }

    @SuppressLint({"NewApi"})
    private void init() {
        Intent it = getIntent();
        if (it != null) {
            this.Date = it.getStringExtra("Date");
        }
        if (this.Date == null || this.Date.equals("")) {
            this.Date = DateTime.getDateString();
        }
        this.action = it.getAction();
        if (this.action == null || this.action.length() == 0 || Val.INTENT_ACTION_NOTI_RESTROPECTION_REGISTER.equals(this.action)) {
            initUI(this.Date);
        } else if (this.action.equals(Val.INTENT_ACTION_NOTI_MORNING_VOICE)) {
            initMorningUI(this.Date);
        }
        try {
            setFinishOnTouchOutside(false);
        } catch (Exception e) {
            DbUtils.exceptionHandler(e);
        }
    }

    private void initView() {
        this.iv_add_note_close = (ImageView) findViewById(R.id.iv_add_note_close);
        this.iv_add_note_check = (ImageView) findViewById(R.id.iv_add_note_check);
        this.et_add_note_content = (EditText) findViewById(R.id.et_add_note_content);
        this.tv_add_note_title = (TextView) findViewById(R.id.tv_add_note_title);
        this.tv_add_note_last_content = (TextView) findViewById(R.id.tv_add_note_last_content);
        this.iv_add_note_close.setOnClickListener(this.myClickListener);
        this.iv_add_note_check.setOnClickListener(this.myClickListener);
    }

    private void initUI(String date) {
        String remarks;
        String hint = PreferUtils.getSP(this.context).getString(Val.CONFIGURE_SUMMARIZE_PROMPT, getString(R.string.str_summarize_prompt_defualt));
        if (hint == null || hint.length() <= 0) {
            this.et_add_note_content.setHint(getString(R.string.str_summarize_prompt_defualt));
        } else {
            this.et_add_note_content.setHint(hint);
        }
        Cursor cursor2 = DbUtils.getDb(this.context).rawQuery("Select * from t_allocation where " + DbUtils.getWhereUserId(this.context) + " and remarks is not null and time < '" + date + "' order by time desc limit 3", null);
        if (cursor2.getCount() > 0) {
            this.tv_add_note_last_content.setVisibility(0);
            while (cursor2.moveToNext()) {
                remarks = cursor2.getString(cursor2.getColumnIndex("remarks"));
                if (remarks != null && remarks.trim().length() > 0) {
                    this.tv_add_note_last_content.setText("您上一次是这样总结的：\n" + remarks);
                    break;
                }
            }
        } else {
            this.tv_add_note_last_content.setVisibility(8);
        }
        DbUtils.close(cursor2);
        Cursor cursor3 = DbUtils.getDb(this.context).rawQuery("Select * from t_allocation where " + DbUtils.getWhereUserId(this.context) + " and morningVoice is not null and time = '" + DateTime.getDateString() + "'", null);
        if (cursor3.getCount() > 0) {
            this.tv_add_note_last_content.setVisibility(0);
            while (cursor3.moveToNext()) {
                remarks = cursor3.getString(cursor3.getColumnIndex("morningVoice"));
                if (remarks != null && remarks.trim().length() > 0) {
                    this.tv_add_note_last_content.setText("\n您上一次的晨音：\n" + remarks + "\n\n" + this.tv_add_note_last_content.getText().toString());
                    break;
                }
            }
        }
        DbUtils.close(cursor3);
        Cursor cursor = DbUtils.getDb(this.context).rawQuery("Select * from t_allocation where " + DbUtils.getWhereUserId(this.context) + " and time is '" + date + "'", null);
        if (cursor.getCount() > 0) {
            cursor.moveToNext();
            remarks = cursor.getString(cursor.getColumnIndex("remarks"));
            if (remarks != null && remarks.length() > 0) {
                this.et_add_note_content.setText(remarks);
            }
        }
        DbUtils.close(cursor);
    }

    private void initMorningUI(String date) {
        this.tv_add_note_title.setText(getResources().getString(R.string.str_morning_voice));
        String hint = PreferUtils.getSP(this.context).getString(Val.CONFIGURE_MORNING_VOICE_PROMPT, getString(R.string.str_moning_voice_prompt_defualt));
        if (hint == null || hint.length() <= 0) {
            this.et_add_note_content.setHint(getString(R.string.str_moning_voice_prompt_defualt));
        } else {
            this.et_add_note_content.setHint(hint);
        }
        Cursor cursor2 = DbUtils.getDb(this.context).rawQuery("Select * from t_allocation where " + DbUtils.getWhereUserId(this.context) + " and morningVoice is not null and time < '" + date + "' order by time desc limit 3", null);
        if (cursor2.getCount() > 0) {
            this.tv_add_note_last_content.setVisibility(0);
            while (cursor2.moveToNext()) {
                String remarks = cursor2.getString(cursor2.getColumnIndex("morningVoice"));
                if (remarks != null && remarks.trim().length() > 0) {
                    this.tv_add_note_last_content.setText("您上一次的晨音：\n" + remarks);
                    break;
                }
            }
        } else {
            this.tv_add_note_last_content.setVisibility(8);
        }
        DbUtils.close(cursor2);
        Cursor cursor = DbUtils.getDb(this.context).rawQuery("Select * from t_allocation where " + DbUtils.getWhereUserId(this.context) + " and time is '" + date + "'", null);
        if (cursor.getCount() > 0) {
            cursor.moveToNext();
            String morningVoice = cursor.getString(cursor.getColumnIndex("morningVoice"));
            if (morningVoice != null && morningVoice.length() > 0) {
                this.et_add_note_content.setText(morningVoice);
            }
        }
        DbUtils.close(cursor);
    }

    public void onBackPressed() {
    }

    protected void onResume() {
        super.onResume();
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
