package com.record.myLife.settings.remind;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import com.record.myLife.R;
import com.record.myLife.base.BaseActivity;
import com.record.service.SystemBarTintManager;
import com.record.utils.DateTime;
import com.record.utils.GeneralHelper;
import com.record.utils.db.DbUtils;
import com.umeng.analytics.MobclickAgent;

public class RestActivity extends BaseActivity {
    String Date = "";
    Button btn_add_note_content;
    Context context;
    EditText et_add_note_content;
    ImageView iv_add_note_close;
    OnClickListener myClickListener = new OnClickListener() {
        public void onClick(View v) {
            int id = v.getId();
            if (id == R.id.iv_add_note_close) {
                RestActivity.this.finish();
            } else if (id == R.id.btn_add_note_content) {
                String note = RestActivity.this.et_add_note_content.getText().toString().trim();
                ContentValues values = new ContentValues();
                values.put("remarks", RestActivity.this.et_add_note_content.getText().toString().trim());
                DbUtils.insertOrUpdateDb_allocation(RestActivity.this.context, RestActivity.this.Date, values);
                if (note != null && note.length() > 0) {
                    GeneralHelper.toastShort(RestActivity.this.context, "添加成功！");
                }
                RestActivity.this.setResult(1);
                RestActivity.this.finish();
            }
        }
    };
    RatingBar rb_add_note_rate;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_note);
        this.context = this;
        SystemBarTintManager.setMIUIbar(this);
        this.iv_add_note_close = (ImageView) findViewById(R.id.iv_add_note_close);
        this.btn_add_note_content = (Button) findViewById(R.id.btn_add_note_content);
        this.rb_add_note_rate = (RatingBar) findViewById(R.id.rb_add_note_rate);
        this.et_add_note_content = (EditText) findViewById(R.id.et_add_note_content);
        this.iv_add_note_close.setOnClickListener(this.myClickListener);
        this.btn_add_note_content.setOnClickListener(this.myClickListener);
        Intent it = getIntent();
        if (it != null) {
            this.Date = it.getStringExtra("Date");
        }
        if (this.Date == null || this.Date.equals("")) {
            this.Date = DateTime.getDateString();
        }
        initUI(this.Date);
    }

    private void initUI(String date) {
        Cursor cursor = DbUtils.getDb(this.context).rawQuery("Select * from t_allocation where " + DbUtils.getWhereUserId(this.context) + " and time is '" + date + "'", null);
        if (cursor.getCount() > 0) {
            cursor.moveToNext();
            String remarks = cursor.getString(cursor.getColumnIndex("remarks"));
            if (remarks != null && remarks.length() > 0) {
                this.et_add_note_content.setText(remarks);
            }
        }
        DbUtils.close(cursor);
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
