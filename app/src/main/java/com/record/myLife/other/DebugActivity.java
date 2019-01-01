package com.record.myLife.other;

import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import com.record.conts.Sofeware;
import com.record.myLife.BuildConfig;
import com.record.myLife.R;
import com.record.myLife.base.BaseActivity;
import com.record.myLife.view.MyTextDialog;
import com.record.myLife.view.dialog.AlertDialogM.Builder;
import com.record.service.SystemBarTintManager;
import com.record.thread.UploadRunnable;
import com.record.utils.GeneralHelper;
import com.record.utils.GeneralUtils;
import com.record.utils.NetUtils;
import com.record.utils.Val;
import com.record.utils.db.DbUtils;
import com.record.utils.log.MyLog;
import com.record.utils.net.HttpRequestProxy;
import com.umeng.analytics.MobclickAgent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;

public class DebugActivity extends BaseActivity {
    static String TAG = "override";
    public static boolean debug_save_logfile_for_tomato_bool = false;
    Context context;
    Button debug_btn_change;
    Button debug_btn_clear_all_table_upload;
    Button debug_btn_clear_table_upload;
    Button debug_btn_copy_data;
    Button debug_btn_current_upload_route;
    Button debug_btn_current_upload_test;
    Button debug_btn_exportDb;
    Button debug_btn_inportDb;
    Button debug_btn_show_table;
    Button debug_btn_start_upload_thread;
    Button debug_save_log_on_itodayss;
    Button debug_save_logfile_for_tomato;
    Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg != null && msg.obj != null) {
                GeneralHelper.toastShort(DebugActivity.this.context, msg.obj.toString());
            }
        }
    };
    OnClickListener myClickListener = new OnClickListener() {
        public void onClick(View v) {
            boolean z = true;
            int id = v.getId();
            if (id == R.id.debug_btn_exportDb) {
                GeneralHelper.toastShort(DebugActivity.this, "正在导出数据库...");
                GeneralHelper.exportDir(BuildConfig.APPLICATION_ID);
                GeneralHelper.toastShort(DebugActivity.this, "在导出数据库成功！");
            } else if (R.id.debug_btn_inportDb == id) {
                DebugActivity.this.showImportPromt();
            } else if (R.id.debug_btn_change == id) {
            } else {
                if (R.id.debug_btn_clear_all_table_upload == id) {
                    DebugActivity.this.showConfirmClearAllDialog();
                } else if (R.id.debug_btn_clear_table_upload == id) {
                    DebugActivity.this.showTableClearDialog();
                } else if (R.id.debug_btn_start_upload_thread == id) {
                    if (NetUtils.isWiFiAvailable_Toast(DebugActivity.this.context)) {
                        DebugActivity.this.startUploadThread();
                    }
                } else if (R.id.debug_btn_show_table == id) {
                    DebugActivity.this.startActivity(new Intent(DebugActivity.this.context, ShowTableActivity.class));
                    DebugActivity.this.overridePendingTransition(R.anim.push_to_left_in, R.anim.push_to_left_out);
                } else if (R.id.debug_btn_copy_data == id) {
                    DebugActivity.this.copeData();
                } else if (R.id.debug_btn_current_upload_route == id) {
                    DebugActivity.this.showDialogEditHTTP();
                } else if (R.id.debug_btn_current_upload_test == id) {
                    DebugActivity.this.requestHello();
                } else if (R.id.debug_save_log_on_itodayss == id) {
                    if (MyLog.MYLOG_WRITE_TO_FILE.booleanValue()) {
                        z = false;
                    }
                    MyLog.MYLOG_WRITE_TO_FILE = Boolean.valueOf(z);
                    DebugActivity.this.debug_save_log_on_itodayss.setText("是否保存日志：" + MyLog.MYLOG_WRITE_TO_FILE);
                } else if (R.id.debug_save_logfile_for_tomato == id) {
                    if (DebugActivity.debug_save_logfile_for_tomato_bool) {
                        z = false;
                    }
                    DebugActivity.debug_save_logfile_for_tomato_bool = z;
                    DebugActivity.this.debug_save_logfile_for_tomato.setText("logfile_for_tomato:" + DebugActivity.debug_save_logfile_for_tomato_bool);
                }
            }
        }
    };
    String selectTableName = "";
    String[] tableColumnArr = null;
    String[] tableNameArr = null;
    Thread thread = null;
    Thread uploadThread = null;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_debug);
        this.context = this;
        SystemBarTintManager.setMIUIbar(this);
        this.debug_btn_exportDb = (Button) findViewById(R.id.debug_btn_exportDb);
        this.debug_btn_inportDb = (Button) findViewById(R.id.debug_btn_inportDb);
        this.debug_btn_change = (Button) findViewById(R.id.debug_btn_change);
        this.debug_btn_copy_data = (Button) findViewById(R.id.debug_btn_copy_data);
        this.debug_btn_current_upload_route = (Button) findViewById(R.id.debug_btn_current_upload_route);
        this.debug_btn_current_upload_test = (Button) findViewById(R.id.debug_btn_current_upload_test);
        this.debug_save_log_on_itodayss = (Button) findViewById(R.id.debug_save_log_on_itodayss);
        this.debug_save_logfile_for_tomato = (Button) findViewById(R.id.debug_save_logfile_for_tomato);
        this.debug_btn_exportDb.setOnClickListener(this.myClickListener);
        this.debug_btn_inportDb.setOnClickListener(this.myClickListener);
        this.debug_btn_change.setOnClickListener(this.myClickListener);
        this.debug_btn_copy_data.setOnClickListener(this.myClickListener);
        this.debug_btn_current_upload_route.setOnClickListener(this.myClickListener);
        this.debug_btn_current_upload_test.setOnClickListener(this.myClickListener);
        this.debug_save_log_on_itodayss.setOnClickListener(this.myClickListener);
        this.debug_save_logfile_for_tomato.setOnClickListener(this.myClickListener);
        initUi();
        this.debug_btn_clear_all_table_upload = (Button) findViewById(R.id.debug_btn_clear_all_table_upload);
        this.debug_btn_clear_table_upload = (Button) findViewById(R.id.debug_btn_clear_table_upload);
        this.debug_btn_start_upload_thread = (Button) findViewById(R.id.debug_btn_start_upload_thread);
        this.debug_btn_show_table = (Button) findViewById(R.id.debug_btn_show_table);
        this.debug_btn_clear_all_table_upload.setOnClickListener(this.myClickListener);
        this.debug_btn_clear_table_upload.setOnClickListener(this.myClickListener);
        this.debug_btn_start_upload_thread.setOnClickListener(this.myClickListener);
        this.debug_btn_show_table.setOnClickListener(this.myClickListener);
        this.debug_btn_current_upload_route.setText("上传：" + Sofeware.HTTP_BASE);
    }

    private void initUi() {
        this.debug_save_log_on_itodayss.setText("是否保存日志：" + MyLog.MYLOG_WRITE_TO_FILE);
        this.debug_save_logfile_for_tomato.setText("logfile_for_tomato:" + debug_save_logfile_for_tomato_bool);
    }

    private void showDialogEditHTTP() {
        final EditText et = new EditText(this.context);
        et.setLayoutParams(new LayoutParams(-1, -2));
        et.setText(Sofeware.HTTP_BASE);
        new Builder(this.context).setTitle((CharSequence) "请输入网址").setView(et).setNeutralButton((CharSequence) "本地", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Sofeware.HTTP_BASE = "http://hyh.api.itodayss.com";
                DebugActivity.this.debug_btn_current_upload_route.setText(Sofeware.HTTP_BASE);
                dialog.cancel();
            }
        }).setPositiveButton((CharSequence) "确定", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Sofeware.HTTP_BASE = et.getText().toString();
                DebugActivity.this.debug_btn_current_upload_route.setText(Sofeware.HTTP_BASE);
                dialog.cancel();
            }
        }).setNegativeButton((CharSequence) "取消", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        }).create().show();
    }

    private void requestHello() {
        if (this.thread == null || !this.thread.isAlive()) {
            this.thread = new Thread(new Runnable() {
                public void run() {
                    try {
                        String result = HttpRequestProxy.doPost(Sofeware.HTTP_BASE + Sofeware.HELLO, new HashMap());
                        Message msg = new Message();
                        msg.obj = result;
                        DebugActivity.this.handler.sendMessage(msg);
                        DebugActivity.log(result);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            this.thread.start();
            GeneralHelper.toastShort(this.context, "开始测试：" + Sofeware.HTTP_BASE);
            return;
        }
        GeneralHelper.toastShort(this.context, "正在测试，请稍候...");
    }

    private void copeData() {
        new Builder(this.context).setTitle(getString(R.string.str_prompt)).setMessage((CharSequence) "数据复制将数据数据库的错误信息，只是为了测试大文件备份，请确认！").setPositiveButton(getString(R.string.str_sure), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                String table = "t_error_data";
                Cursor cursor = DbUtils.getDb(DebugActivity.this.context).rawQuery("select * from " + table + " limit 10000", null);
                if (cursor.getCount() > 0) {
                    DbUtils.getDb(DebugActivity.this.context).beginTransaction();
                    while (cursor.moveToNext()) {
                        String ErrorData = cursor.getString(cursor.getColumnIndex("ErrorData"));
                        ContentValues values = new ContentValues();
                        values.put("ErrorData", ErrorData + ErrorData);
                        DbUtils.getDb(DebugActivity.this.context).insert(table, null, values);
                    }
                    DbUtils.getDb(DebugActivity.this.context).setTransactionSuccessful();
                    DbUtils.getDb(DebugActivity.this.context).endTransaction();
                }
                GeneralUtils.toastShort(DebugActivity.this.context, "完成");
                dialog.cancel();
            }
        }).setNegativeButton(getString(R.string.str_cancel), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        }).create().show();
    }

    private void showConfirmClearAllDialog() {
        new Builder(this.context).setTitle((CharSequence) "注意").setMessage((CharSequence) "这里是开发者调用的，您当前选择清除全部表的isUpload属性，清除会引导数据重新上传，是否继续?").setPositiveButton((CharSequence) "清除", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                DebugActivity.this.clearAllTableIsUpload();
                dialog.cancel();
            }
        }).setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        }).create().show();
    }

    private void startUploadThread() {
        if (this.uploadThread == null || !this.uploadThread.isAlive()) {
            GeneralUtils.toastShort(this.context, "正在启动上传线程...");
            this.uploadThread = new Thread(new UploadRunnable(this.context, null));
            this.uploadThread.start();
            GeneralUtils.toastShort(this.context, "上传线程成功启动！");
            return;
        }
        GeneralUtils.toastShort(this.context, "线程已经启动！");
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
                DebugActivity.this.selectTableName = DebugActivity.this.tableNameArr[which];
                DebugActivity.this.showTableColumnDialog(DebugActivity.this.selectTableName);
                dialog.cancel();
            }
        }).create().show();
    }

    private void showTableColumnDialog(final String tableName) {
        Cursor cursor = DbUtils.getDb(this.context).query("sqlite_master", new String[]{"sql"}, "tbl_name=?", new String[]{tableName}, null, null, null);
        String sql = "";
        cursor.moveToFirst();
        if (!cursor.isAfterLast()) {
            sql = cursor.getString(0).toString();
        }
        log(sql);
        int start = sql.indexOf("(");
        this.tableColumnArr = sql.substring(start + 1, sql.lastIndexOf(")")).split(",");
        new Builder(this.context).setTitle((CharSequence) "请选择要清除的字段").setItems(this.tableColumnArr, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                String value;
                String[] arr = DebugActivity.this.tableColumnArr[which].trim().split(" ");
                if (arr[1].contains("integer") || arr[1].contains("double") || arr[1].contains("long")) {
                    value = "0";
                } else {
                    value = null;
                }
                DebugActivity.this.showComfirmIsClearPro(tableName, arr[0], value);
                dialog.cancel();
            }
        }).create().show();
    }

    private void showComfirmIsClearPro(final String tableName, final String column, final String pro) {
        new Builder(this.context).setTitle((CharSequence) "注意").setMessage("这里是开发者调用的，您当前选择是清除" + tableName + "表的" + column + "字段的值，清除可能会导致数据丢失,及其它意想不到的后果！！！请确认后再继续！").setPositiveButton((CharSequence) "确认,清除", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                ContentValues values = new ContentValues();
                values.put(column, pro);
                try {
                    DbUtils.getDb(DebugActivity.this.context).update(tableName, values, " id > 0 ", null);
                } catch (Exception e) {
                    e.printStackTrace();
                    DbUtils.getDb(DebugActivity.this.context).update(tableName, values, " Id > 0 ", null);
                }
                GeneralUtils.toastLong(DebugActivity.this.context, tableName + "清除成功！values为：" + values.toString());
                dialog.cancel();
            }
        }).setNegativeButton((CharSequence) "取消", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        }).create().show();
    }

    private void clearAllTableIsUpload() {
        String[] tableNameArr = new String[]{"t_act", "t_act_item", "t_sub_type", "t_routine_link"};
        String[] serverIdColumnArr = new String[]{"severId", "sGoalItemId", "sLabelId", "sLabelLinkId"};
        Cursor cursor = DbUtils.getDb(this.context).rawQuery("select name from sqlite_master where type='table' order by name", null);
        if (cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                String name = cursor.getString(0).trim();
                if (!("sqlite_sequence".equals(name) || "android_metadata".equals(name))) {
                    ContentValues values = new ContentValues();
                    values.put("isUpload", Integer.valueOf(0));
                    log("清除" + name + "的isUpload属性！");
                    for (int i = 0; i < tableNameArr.length; i++) {
                        if (tableNameArr[i].equals(name)) {
                            values.put(serverIdColumnArr[i], Integer.valueOf(0));
                            log("清除" + name + "的" + serverIdColumnArr[i] + "属性！");
                        }
                    }
                    try {
                        DbUtils.getDb(this.context).update(name, values, "id > 0", null);
                    } catch (Exception e) {
                        e.printStackTrace();
                        DbUtils.getDb(this.context).update(name, values, "Id > 0", null);
                    }
                }
            }
            GeneralUtils.toastLong(this.context, "清除isUpload成功！");
        }
        DbUtils.close(cursor);
    }

    private void showImportPromt() {
        new MyTextDialog.Builder(this.context).setTitle("说明").setMessage("这里是开发人员调试用的，请匆乱操作。本次导入需要在itodayss里有无加密数据data_backup文件!\n注意：导入将覆盖原有的数据！！！").setPositiveButton("导入", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                DebugActivity.this.insertData_withoutEncryted();
                dialog.cancel();
            }
        }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        }).create().show();
    }

    public void insertData_withoutEncryted() {
        String DB_PATH = "";
        String DB_NAME = "";
        String dbFilePath = DbUtils.getDb(this.context).getPath();
        if (dbFilePath != null && dbFilePath.length() > 0) {
            int index = dbFilePath.lastIndexOf("/");
            if (index > 0) {
                DB_PATH = dbFilePath.substring(0, index + 1);
                DB_NAME = dbFilePath.substring(index + 1, dbFilePath.length());
            }
        }
        if (DB_PATH == null || DB_PATH.length() == 0 || DB_NAME == null || DB_NAME.length() == 0) {
            DB_PATH = Environment.getDataDirectory() + "/data/" + Val.packageName + "/databases/";
            DB_NAME = Val.DB_NAME;
        }
        if (new File(DB_PATH + DB_NAME).exists()) {
            Log.v("override", "删除：" + DB_PATH + DB_NAME);
            new File(DB_PATH + DB_NAME).delete();
            Log.v("override BackupDbActivity", "删除成功！");
        }
        if (new File(DB_PATH + DB_NAME).exists()) {
            GeneralHelper.toastShort(this.context, "原数据库存在！");
        } else {
            File f = new File(DB_PATH);
            if (!f.exists()) {
                f.mkdir();
            }
            try {
                String sbPath = Environment.getExternalStorageDirectory() + File.separator + Val.SD_BACKUP_DIR + File.separator + Val.SD_BACKUP_NAME;
                log(sbPath);
                File file = new File(sbPath);
                if (file.exists()) {
                    InputStream is = new FileInputStream(file);
                    OutputStream os = new FileOutputStream(DB_PATH + DB_NAME);
                    byte[] buffer = new byte[1024];
                    while (true) {
                        int length = is.read(buffer);
                        if (length <= 0) {
                            break;
                        }
                        os.write(buffer, 0, length);
                    }
                    os.flush();
                    os.close();
                    is.close();
                } else {
                    GeneralHelper.toastLong(this.context, getResources().getString(R.string.str_leading_in_file_no_exist) + "SD卡下：" + sbPath.replace(Environment.getExternalStorageDirectory().toString(), ""));
                    return;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        Cursor cursor = SQLiteDatabase.openOrCreateDatabase(DB_PATH + DB_NAME, null).rawQuery("select * from t_user", null);
        Log.v("override", "查看数据是否导入成功...");
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            log("导入文件的id：" + cursor.getString(cursor.getColumnIndex("id")));
        }
        cursor.close();
        Log.v("override MainActivity", "文件方式导入数据库成功！");
        GeneralHelper.toastShort(this.context, "恢复成功，返回主页看看吧！");
        DbUtils.reGetDb(this.context);
        sendBroadcast(new Intent(Val.INTENT_ACTION_LOGIN));
    }

    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    public void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

    private static void log(String str) {
        Log.i(TAG, ":" + str);
    }
}
