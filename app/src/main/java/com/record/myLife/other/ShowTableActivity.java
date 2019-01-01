package com.record.myLife.other;

import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnMultiChoiceClickListener;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import com.record.myLife.R;
import com.record.myLife.base.BaseActivity;
import com.record.myLife.view.dialog.AlertDialogM.Builder;
import com.record.service.SystemBarTintManager;
import com.record.utils.db.DbUtils;
import com.umeng.analytics.MobclickAgent;
import java.util.Iterator;
import java.util.TreeSet;

public class ShowTableActivity extends BaseActivity {
    static String TAG = "override";
    Button btn_set_back;
    Button btn_show_table_column;
    EditText btn_show_table_id_pre;
    EditText btn_show_table_id_suf;
    Button btn_show_table_table;
    TreeSet<String> columnSet = new TreeSet();
    Context context;
    LayoutInflater inflater;
    OnClickListener myClickListener = new OnClickListener() {
        public void onClick(View v) {
            int id = v.getId();
            if (id == R.id.btn_show_table_table) {
                ShowTableActivity.this.showTableClearDialog();
            } else if (id == R.id.btn_set_back) {
                ShowTableActivity.this.onBackPressed();
            }
        }
    };
    String selectTableName = "";
    String[] tableColumnArr = null;
    String[] tableNameArr = null;
    TableLayout tl_show_table;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_table);
        this.context = this;
        TAG += getClass().getSimpleName();
        this.inflater = getLayoutInflater();
        SystemBarTintManager.setMIUIbar(this);
        this.btn_show_table_table = (Button) findViewById(R.id.btn_show_table_table);
        this.btn_show_table_column = (Button) findViewById(R.id.btn_show_table_column);
        this.btn_set_back = (Button) findViewById(R.id.btn_set_back);
        this.tl_show_table = (TableLayout) findViewById(R.id.tl_show_table);
        this.btn_show_table_id_pre = (EditText) findViewById(R.id.btn_show_table_id_pre);
        this.btn_show_table_id_suf = (EditText) findViewById(R.id.btn_show_table_id_suf);
        this.btn_show_table_table.setOnClickListener(this.myClickListener);
        this.btn_show_table_column.setOnClickListener(this.myClickListener);
        this.btn_set_back.setOnClickListener(this.myClickListener);
    }

    private void initSetUI(String tableName, TreeSet<String> columnSet) {
        if (tableName != null && tableName.length() > 0) {
            Cursor cursor;
            this.tl_show_table.removeAllViews();
            String select = "*";
            if (columnSet != null && columnSet.size() > 0) {
                select = "";
                Iterator it = columnSet.iterator();
                while (it.hasNext()) {
                    String str2 = ((String) it.next()).trim();
                    if (str2.length() > 0) {
                        select = select + str2.substring(0, str2.indexOf(" ")) + ",";
                    }
                }
                if (select.length() > 1) {
                    select = select.substring(0, select.length() - 1);
                }
            }
            int pre = 0;
            int suf = 0;
            try {
                pre = Integer.parseInt(this.btn_show_table_id_pre.getText().toString());
            } catch (Exception e) {
            }
            try {
                suf = Integer.parseInt(this.btn_show_table_id_suf.getText().toString());
            } catch (Exception e2) {
            }
            String where = "";
            String id = "id";
            if (pre > 0) {
                where = " where " + id + " >= " + pre;
            }
            if (suf > 0) {
                if (where.length() > 0) {
                    where = where + " and " + id + " <= " + suf;
                } else {
                    where = " where " + id + " <= " + suf;
                }
            }
            try {
                cursor = DbUtils.getDb(this.context).rawQuery("select " + select + " from " + tableName + " " + where, null);
            } catch (Exception e3) {
                cursor = DbUtils.getDb(this.context).rawQuery("select " + select + " from " + tableName + " " + where.replace(id, "Id"), null);
            }
            if (cursor != null && cursor.getCount() > 0) {
                String[] columns = cursor.getColumnNames();
                TableRow tr = (TableRow) this.inflater.inflate(R.layout.template_table_row, null);
                for (String c : columns) {
                    TextView tv = (TextView) this.inflater.inflate(R.layout.template_text_frame, null);
                    tv.setText(c);
                    tr.addView(tv);
                }
                this.tl_show_table.addView(tr);
                while (cursor.moveToNext()) {
                    TableRow tr2 = (TableRow) this.inflater.inflate(R.layout.template_table_row, null);
                    for (int i = 0; i < columns.length; i++) {
                        TextView tv2 = (TextView) this.inflater.inflate(R.layout.template_text_frame, null);
                        tv2.setText(cursor.getString(i));
                        tr2.addView(tv2);
                    }
                    this.tl_show_table.addView(tr2);
                }
            }
        }
    }

    private void showTableClearDialog() {
        this.selectTableName = "";
        if (this.tableNameArr == null) {
            Cursor cursor = DbUtils.getDb(this.context).rawQuery("select name from sqlite_master where type='table' order by name", null);
            if (cursor.getCount() > 0) {
                this.tableNameArr = new String[cursor.getCount()];
                int i = 0;
                while (cursor.moveToNext()) {
                    this.tableNameArr[i] = cursor.getString(0);
                    i++;
                }
            }
            DbUtils.close(cursor);
        }
        new Builder(this.context).setTitle((CharSequence) "请选择要清除的表").setItems(this.tableNameArr, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                ShowTableActivity.this.selectTableName = ShowTableActivity.this.tableNameArr[which];
                ShowTableActivity.this.columnSet = new TreeSet();
                ShowTableActivity.this.showTableColumnDialog(ShowTableActivity.this.selectTableName);
                dialog.cancel();
            }
        }).create().show();
    }

    private void showTableColumnDialog(String tableName) {
        Cursor cursor = DbUtils.getDb(this.context).query("sqlite_master", new String[]{"sql"}, "tbl_name=?", new String[]{tableName}, null, null, null);
        String sql = "";
        cursor.moveToFirst();
        if (!cursor.isAfterLast()) {
            sql = cursor.getString(0).toString();
        }
        log(sql);
        int start = sql.indexOf("(");
        this.tableColumnArr = sql.substring(start + 1, sql.lastIndexOf(")")).split(",");
        new Builder(this.context).setTitle((CharSequence) "请选择要清除的字段").setMultiChoiceItems(this.tableColumnArr, new boolean[this.tableColumnArr.length], new OnMultiChoiceClickListener() {
            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                if (isChecked) {
                    ShowTableActivity.this.columnSet.add(ShowTableActivity.this.tableColumnArr[which]);
                    ShowTableActivity.log(ShowTableActivity.this.columnSet.toString());
                } else {
                    ShowTableActivity.this.columnSet.remove(ShowTableActivity.this.tableColumnArr[which]);
                }
                dialog.cancel();
            }
        }).setPositiveButton(getStr(R.string.str_sure), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                ShowTableActivity.this.initSetUI(ShowTableActivity.this.selectTableName, ShowTableActivity.this.columnSet);
            }
        }).create().show();
    }

    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(R.anim.push_to_right_in, R.anim.push_to_right_out);
    }

    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    public void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

    public String getStr(int str) {
        return getResources().getString(str);
    }

    public static void log(String str) {
        Log.i(TAG, ":" + str);
    }
}
