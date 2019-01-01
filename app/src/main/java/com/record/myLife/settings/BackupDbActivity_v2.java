package com.record.myLife.settings;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources.NotFoundException;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.StatFs;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.record.bean.dbbean.Allocation;
import com.record.myLife.BuildConfig;
import com.record.myLife.R;
import com.record.myLife.base.BaseActivity;
import com.record.myLife.view.dialog.AlertDialogM.Builder;
import com.record.service.SystemBarTintManager;
import com.record.utils.DateTime;
import com.record.utils.FileUtils;
import com.record.utils.GeneralHelper;
import com.record.utils.GeneralUtils;
import com.record.utils.RemindUtils;
import com.record.utils.StringUtils;
import com.record.utils.ToastUtils;
import com.record.utils.Val;
import com.record.utils.db.DbUtils;
import com.record.utils.db.service.ActTypeService;
import com.record.utils.dialog.DialogUtils;
import com.umeng.analytics.MobclickAgent;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;

public class BackupDbActivity_v2 extends BaseActivity {
    public static double cryptProgress = 0.0d;
    public static double fileSize = 0.0d;
    String DB_NAME = "mylife_db";
    String DB_PATH = "/data/data/com.record.myLife/databases/";
    int HANDLER_BACKUP_FINISH = 2;
    int HANDLER_BACKUP_START = 3;
    int HANDLER_DIALOG = 9;
    int HANDLER_ERROR = 1;
    int HANDLER_IMPORT_FINISH = 6;
    int HANDLER_IMPORT_START = 5;
    int HANDLER_PB = 4;
    int HANDLER_TOAST = 7;
    String SD_BACKUP_DIR = "itodayss";
    Thread backupThread = null;
    Button btn_backup_backup_to_sd;
    Button btn_backup_leading_in;
    Button btn_export;
    Button btn_support_back;
    Context context;
    File data = Environment.getDataDirectory();
    String encodeset = "gb2312";
    String exportPath = "";
    Thread importThread = null;
    boolean isNormal = true;
    ImageView iv_auto_backup;
    TextView iv_backup_instrution;
    OnClickListener myClickListener = new OnClickListener() {
        public void onClick(View v) {
            int id = v.getId();
            if (id == R.id.btn_export) {
                BackupDbActivity_v2.this.showExportItemsDialog();
            } else if (id == R.id.btn_backup_backup_to_sd) {
                BackupDbActivity_v2.this.clickBackup();
            } else if (id == R.id.btn_backup_leading_in) {
                BackupDbActivity_v2.this.clickImport();
            } else if (id == R.id.btn_support_back) {
                BackupDbActivity_v2.this.doBeforExitActivity();
            } else if (id == R.id.iv_backup_instrution) {
                new Builder(BackupDbActivity_v2.this.context).setTitle(BackupDbActivity_v2.this.getResources().getString(R.string.str_prompt)).setMessage(BackupDbActivity_v2.this.getResources().getString(R.string.str_backup_instruction)).setNegativeButton(BackupDbActivity_v2.this.getResources().getString(R.string.str_sure), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                }).create().show();
            } else if (id == R.id.iv_auto_backup) {
                SharedPreferences sp = BackupDbActivity_v2.this.getSharedPreferences(Val.CONFIGURE_NAME, 0);
                if (sp.getInt(Val.CONFIGURE_IS_AUTO_BACKUP_DATA, 0) > 0) {
                    BackupDbActivity_v2.this.iv_auto_backup.setImageResource(R.drawable.ic_off_v2);
                    sp.edit().putInt(Val.CONFIGURE_IS_AUTO_BACKUP_DATA, 0).commit();
                    RemindUtils.cancelAutoBackup(BackupDbActivity_v2.this.context);
                    GeneralHelper.toastShort(BackupDbActivity_v2.this.context, BackupDbActivity_v2.this.getResources().getString(R.string.str_auto_backup_close));
                    return;
                }
                BackupDbActivity_v2.this.iv_auto_backup.setImageResource(R.drawable.ic_on_v2);
                sp.edit().putInt(Val.CONFIGURE_IS_AUTO_BACKUP_DATA, 1).commit();
                RemindUtils.quicksetAutoBackup(BackupDbActivity_v2.this.context);
                GeneralHelper.toastShort(BackupDbActivity_v2.this.context, BackupDbActivity_v2.this.getResources().getString(R.string.str_auto_backup_open));
            }
        }
    };
    Handler myHandler = new Handler() {
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int action = msg.arg1;
            if (BackupDbActivity_v2.this.HANDLER_ERROR == action) {
                DialogUtils.showPrompt(BackupDbActivity_v2.this.context, msg.obj.toString());
                if (BackupDbActivity_v2.this.isNormal) {
                    BackupDbActivity_v2.this.doWhileError();
                }
            } else if (BackupDbActivity_v2.this.HANDLER_PB == action) {
                if (BackupDbActivity_v2.this.isNormal) {
                    BackupDbActivity_v2.this.tv_auto_backup_v2_percent.setVisibility(0);
                    BackupDbActivity_v2.this.rl_auto_backup_v2_pb.setMax(100);
                    int progress = (int) ((BackupDbActivity_v2.cryptProgress / BackupDbActivity_v2.fileSize) * 100.0d);
                    BackupDbActivity_v2.this.tv_auto_backup_v2_percent.setText(progress + "%");
                    BackupDbActivity_v2.this.rl_auto_backup_v2_pb.setProgress(progress);
                }
            } else if (BackupDbActivity_v2.this.HANDLER_BACKUP_FINISH == action) {
                if (BackupDbActivity_v2.this.isNormal) {
                    DialogUtils.showPrompt(BackupDbActivity_v2.this.context, BackupDbActivity_v2.this.getString(R.string.str_backup_success_prompt));
                    BackupDbActivity_v2.this.doAfterSuccess();
                    return;
                }
                BackupDbActivity_v2.this.finish();
            } else if (BackupDbActivity_v2.this.HANDLER_IMPORT_FINISH == action) {
                DialogUtils.showPrompt(BackupDbActivity_v2.this.context, BackupDbActivity_v2.this.getString(R.string.str_restore_success));
                BackupDbActivity_v2.this.doAfterSuccess();
                BackupDbActivity_v2.this.sendBroadcast(new Intent(Val.INTENT_ACTION_LOGIN));
            } else if (BackupDbActivity_v2.this.HANDLER_TOAST == action) {
                if (BackupDbActivity_v2.this.isNormal) {
                    GeneralUtils.toastShort(BackupDbActivity_v2.this.context, msg.obj.toString());
                }
            } else if (BackupDbActivity_v2.this.HANDLER_DIALOG == action) {
                DialogUtils.showPrompt(BackupDbActivity_v2.this.context, msg.obj.toString());
            }
        }
    };
    String packageName = BuildConfig.APPLICATION_ID;
    ProgressBar rl_auto_backup_v2_pb;
    TextView tv_auto_backup_v2_percent;
    TextView tv_backup_backup_path_prompt;
    TextView tv_backup_backup_to_sd_path;

    class ExportData implements Runnable {
        String actionTypeName = "";
        Cursor cursor = null;
        String date = "";

        public ExportData(Cursor cursor, String actionTypeName, String date) {
            this.cursor = cursor;
            this.actionTypeName = actionTypeName;
            this.date = date;
        }

        public void run() {
            if (this.cursor != null) {
                String today = DateTime.getDateString();
                String fileName = this.date + "~" + today + "_" + DateTime.getTimeString3() + ".xls";
                if (BackupDbActivity_v2.this.exportExcel(this.cursor, fileName, this.date, today).isSuccess) {
                    BackupDbActivity_v2.this.sendDialog("成功导出！\n导出目录:" + BackupDbActivity_v2.this.exportPath + "\n文件名:" + fileName);
                    DbUtils.close(this.cursor);
                    return;
                }
                BackupDbActivity_v2.this.sendToast("导出xls失败，正在尝试导出cvs...");
                fileName = this.date + "~" + today + "_" + DateTime.getTimeString3() + ".cvs";
                TempExportResult isSuccess = BackupDbActivity_v2.this.exportCSV(this.cursor, fileName, this.date, today);
                if (isSuccess.isSuccess) {
                    BackupDbActivity_v2.this.sendDialog("成功导出！\n导出目录:" + BackupDbActivity_v2.this.exportPath + "\n文件名:" + fileName);
                } else {
                    BackupDbActivity_v2.this.sendDialog("导出失败，请稍候再试!\n错误信息：\n" + isSuccess.errorStr);
                }
                DbUtils.close(this.cursor);
            }
        }
    }

    class TempExportResult {
        String errorStr;
        boolean isSuccess;

        public TempExportResult(boolean isSuccess, String errorStr) {
            this.isSuccess = isSuccess;
            this.errorStr = errorStr;
        }
    }

    class backupDbRunnble implements Runnable {
        Context context;

        public backupDbRunnble(Context context) {
            this.context = context;
        }

        /* JADX WARNING: Removed duplicated region for block: B:50:0x0164  */
        /* JADX WARNING: Removed duplicated region for block: B:95:0x0251  */
        /* JADX WARNING: Removed duplicated region for block: B:53:0x018f  */
        /* JADX WARNING: Removed duplicated region for block: B:64:0x0212 A:{SYNTHETIC, Splitter: B:64:0x0212} */
        /* JADX WARNING: Removed duplicated region for block: B:67:0x0217 A:{SYNTHETIC, Splitter: B:67:0x0217} */
        /* JADX WARNING: Removed duplicated region for block: B:70:0x021c A:{SYNTHETIC, Splitter: B:70:0x021c} */
        /* JADX WARNING: Removed duplicated region for block: B:50:0x0164  */
        /* JADX WARNING: Removed duplicated region for block: B:53:0x018f  */
        /* JADX WARNING: Removed duplicated region for block: B:95:0x0251  */
        /* JADX WARNING: Removed duplicated region for block: B:80:0x0234 A:{SYNTHETIC, Splitter: B:80:0x0234} */
        /* JADX WARNING: Removed duplicated region for block: B:83:0x0239 A:{SYNTHETIC, Splitter: B:83:0x0239} */
        /* JADX WARNING: Removed duplicated region for block: B:86:0x023e A:{SYNTHETIC, Splitter: B:86:0x023e} */
        /* JADX WARNING: Removed duplicated region for block: B:80:0x0234 A:{SYNTHETIC, Splitter: B:80:0x0234} */
        /* JADX WARNING: Removed duplicated region for block: B:83:0x0239 A:{SYNTHETIC, Splitter: B:83:0x0239} */
        /* JADX WARNING: Removed duplicated region for block: B:86:0x023e A:{SYNTHETIC, Splitter: B:86:0x023e} */
        /* JADX WARNING: Removed duplicated region for block: B:64:0x0212 A:{SYNTHETIC, Splitter: B:64:0x0212} */
        /* JADX WARNING: Removed duplicated region for block: B:67:0x0217 A:{SYNTHETIC, Splitter: B:67:0x0217} */
        /* JADX WARNING: Removed duplicated region for block: B:70:0x021c A:{SYNTHETIC, Splitter: B:70:0x021c} */
        /* JADX WARNING: Removed duplicated region for block: B:50:0x0164  */
        /* JADX WARNING: Removed duplicated region for block: B:95:0x0251  */
        /* JADX WARNING: Removed duplicated region for block: B:53:0x018f  */
        /* JADX WARNING: Removed duplicated region for block: B:80:0x0234 A:{SYNTHETIC, Splitter: B:80:0x0234} */
        /* JADX WARNING: Removed duplicated region for block: B:83:0x0239 A:{SYNTHETIC, Splitter: B:83:0x0239} */
        /* JADX WARNING: Removed duplicated region for block: B:86:0x023e A:{SYNTHETIC, Splitter: B:86:0x023e} */
        /* JADX WARNING: Removed duplicated region for block: B:64:0x0212 A:{SYNTHETIC, Splitter: B:64:0x0212} */
        /* JADX WARNING: Removed duplicated region for block: B:67:0x0217 A:{SYNTHETIC, Splitter: B:67:0x0217} */
        /* JADX WARNING: Removed duplicated region for block: B:70:0x021c A:{SYNTHETIC, Splitter: B:70:0x021c} */
        /* JADX WARNING: Removed duplicated region for block: B:50:0x0164  */
        /* JADX WARNING: Removed duplicated region for block: B:53:0x018f  */
        /* JADX WARNING: Removed duplicated region for block: B:95:0x0251  */
        public void run() {
            /*
            r22 = this;
            r18 = 0;
            com.record.myLife.settings.BackupDbActivity_v2.cryptProgress = r18;
            r18 = 0;
            com.record.myLife.settings.BackupDbActivity_v2.fileSize = r18;
            r7 = 0;
            r6 = 0;
            r17 = 0;
            r12 = 0;
            r14 = 0;
            r4 = 0;
            r18 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x01c0 }
            r18.<init>();	 Catch:{ Exception -> 0x01c0 }
            r19 = android.os.Environment.getExternalStorageDirectory();	 Catch:{ Exception -> 0x01c0 }
            r18 = r18.append(r19);	 Catch:{ Exception -> 0x01c0 }
            r19 = java.io.File.separator;	 Catch:{ Exception -> 0x01c0 }
            r18 = r18.append(r19);	 Catch:{ Exception -> 0x01c0 }
            r0 = r22;
            r0 = com.record.myLife.settings.BackupDbActivity_v2.this;	 Catch:{ Exception -> 0x01c0 }
            r19 = r0;
            r0 = r19;
            r0 = r0.SD_BACKUP_DIR;	 Catch:{ Exception -> 0x01c0 }
            r19 = r0;
            r18 = r18.append(r19);	 Catch:{ Exception -> 0x01c0 }
            r7 = r18.toString();	 Catch:{ Exception -> 0x01c0 }
            r18 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x01c0 }
            r18.<init>();	 Catch:{ Exception -> 0x01c0 }
            r19 = com.record.utils.Val.SD_BACKUP_NAME;	 Catch:{ Exception -> 0x01c0 }
            r18 = r18.append(r19);	 Catch:{ Exception -> 0x01c0 }
            r19 = com.record.utils.DateTime.getTimeString2();	 Catch:{ Exception -> 0x01c0 }
            r18 = r18.append(r19);	 Catch:{ Exception -> 0x01c0 }
            r6 = r18.toString();	 Catch:{ Exception -> 0x01c0 }
            r0 = r22;
            r0 = com.record.myLife.settings.BackupDbActivity_v2.this;	 Catch:{ Exception -> 0x01c0 }
            r18 = r0;
            r17 = r18.getDbFile();	 Catch:{ Exception -> 0x01c0 }
            r18 = r17.exists();	 Catch:{ Exception -> 0x01c0 }
            if (r18 != 0) goto L_0x0092;
        L_0x005d:
            r0 = r22;
            r0 = com.record.myLife.settings.BackupDbActivity_v2.this;	 Catch:{ Exception -> 0x01c0 }
            r18 = r0;
            r0 = r22;
            r0 = com.record.myLife.settings.BackupDbActivity_v2.this;	 Catch:{ Exception -> 0x01c0 }
            r19 = r0;
            r20 = 2131165352; // 0x7f0700a8 float:1.7944919E38 double:1.052935586E-314;
            r19 = r19.getString(r20);	 Catch:{ Exception -> 0x01c0 }
            r18.sendError(r19);	 Catch:{ Exception -> 0x01c0 }
            if (r4 == 0) goto L_0x0078;
        L_0x0075:
            r4.close();	 Catch:{ IOException -> 0x0083 }
        L_0x0078:
            if (r12 == 0) goto L_0x007d;
        L_0x007a:
            r12.close();	 Catch:{ IOException -> 0x0088 }
        L_0x007d:
            if (r14 == 0) goto L_0x0082;
        L_0x007f:
            r14.close();	 Catch:{ IOException -> 0x008d }
        L_0x0082:
            return;
        L_0x0083:
            r8 = move-exception;
            r8.printStackTrace();
            goto L_0x0078;
        L_0x0088:
            r8 = move-exception;
            r8.printStackTrace();
            goto L_0x007d;
        L_0x008d:
            r8 = move-exception;
            r8.printStackTrace();
            goto L_0x0082;
        L_0x0092:
            r18 = r17.length();	 Catch:{ Exception -> 0x01c0 }
            r0 = r18;
            r0 = (double) r0;	 Catch:{ Exception -> 0x01c0 }
            r18 = r0;
            com.record.myLife.settings.BackupDbActivity_v2.fileSize = r18;	 Catch:{ Exception -> 0x01c0 }
            r18 = "开始备份";
            com.record.myLife.settings.BackupDbActivity_v2.log(r18);	 Catch:{ Exception -> 0x01c0 }
            r0 = r22;
            r0 = com.record.myLife.settings.BackupDbActivity_v2.this;	 Catch:{ Exception -> 0x01c0 }
            r18 = r0;
            r0 = r22;
            r0 = com.record.myLife.settings.BackupDbActivity_v2.this;	 Catch:{ Exception -> 0x01c0 }
            r19 = r0;
            r20 = 2131165621; // 0x7f0701b5 float:1.7945464E38 double:1.052935719E-314;
            r19 = r19.getString(r20);	 Catch:{ Exception -> 0x01c0 }
            r18.sendToast(r19);	 Catch:{ Exception -> 0x01c0 }
            r18 = "DES/ECB/NoPadding";
            r3 = javax.crypto.Cipher.getInstance(r18);	 Catch:{ Exception -> 0x01c0 }
            r18 = 1;
            r19 = com.record.utils.Val.getPassword();	 Catch:{ Exception -> 0x01c0 }
            r19 = r19.getBytes();	 Catch:{ Exception -> 0x01c0 }
            r19 = com.record.utils.MyCipher.toKey(r19);	 Catch:{ Exception -> 0x01c0 }
            r0 = r18;
            r1 = r19;
            r3.init(r0, r1);	 Catch:{ Exception -> 0x01c0 }
            r13 = new java.io.FileInputStream;	 Catch:{ Exception -> 0x01c0 }
            r0 = r17;
            r13.<init>(r0);	 Catch:{ Exception -> 0x01c0 }
            com.record.utils.FileUtils.creatDirIfDirIsFileThenDelete(r7);	 Catch:{ Exception -> 0x0275, all -> 0x0269 }
            r9 = new java.io.File;	 Catch:{ Exception -> 0x0275, all -> 0x0269 }
            r18 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0275, all -> 0x0269 }
            r18.<init>();	 Catch:{ Exception -> 0x0275, all -> 0x0269 }
            r0 = r18;
            r18 = r0.append(r7);	 Catch:{ Exception -> 0x0275, all -> 0x0269 }
            r19 = java.io.File.separator;	 Catch:{ Exception -> 0x0275, all -> 0x0269 }
            r18 = r18.append(r19);	 Catch:{ Exception -> 0x0275, all -> 0x0269 }
            r0 = r18;
            r18 = r0.append(r6);	 Catch:{ Exception -> 0x0275, all -> 0x0269 }
            r18 = r18.toString();	 Catch:{ Exception -> 0x0275, all -> 0x0269 }
            r0 = r18;
            r9.<init>(r0);	 Catch:{ Exception -> 0x0275, all -> 0x0269 }
            r18 = r9.exists();	 Catch:{ Exception -> 0x0275, all -> 0x0269 }
            if (r18 != 0) goto L_0x0108;
        L_0x0105:
            r9.createNewFile();	 Catch:{ Exception -> 0x0275, all -> 0x0269 }
        L_0x0108:
            r15 = new java.io.FileOutputStream;	 Catch:{ Exception -> 0x0275, all -> 0x0269 }
            r15.<init>(r9);	 Catch:{ Exception -> 0x0275, all -> 0x0269 }
            r5 = new javax.crypto.CipherInputStream;	 Catch:{ Exception -> 0x0279, all -> 0x026c }
            r5.<init>(r13, r3);	 Catch:{ Exception -> 0x0279, all -> 0x026c }
            r18 = 10240; // 0x2800 float:1.4349E-41 double:5.059E-320;
            r0 = r18;
            r2 = new byte[r0];	 Catch:{ Exception -> 0x027e, all -> 0x0270 }
            r11 = 0;
        L_0x0119:
            r16 = r5.read(r2);	 Catch:{ Exception -> 0x027e, all -> 0x0270 }
            if (r16 <= 0) goto L_0x0143;
        L_0x011f:
            r18 = 0;
            r0 = r18;
            r1 = r16;
            r15.write(r2, r0, r1);	 Catch:{ Exception -> 0x027e, all -> 0x0270 }
            r18 = com.record.myLife.settings.BackupDbActivity_v2.cryptProgress;	 Catch:{ Exception -> 0x027e, all -> 0x0270 }
            r0 = r16;
            r0 = (double) r0;	 Catch:{ Exception -> 0x027e, all -> 0x0270 }
            r20 = r0;
            r18 = r18 + r20;
            com.record.myLife.settings.BackupDbActivity_v2.cryptProgress = r18;	 Catch:{ Exception -> 0x027e, all -> 0x0270 }
            r18 = r11 % 3;
            if (r18 != 0) goto L_0x0140;
        L_0x0137:
            r0 = r22;
            r0 = com.record.myLife.settings.BackupDbActivity_v2.this;	 Catch:{ Exception -> 0x027e, all -> 0x0270 }
            r18 = r0;
            r18.sendPb();	 Catch:{ Exception -> 0x027e, all -> 0x0270 }
        L_0x0140:
            r11 = r11 + 1;
            goto L_0x0119;
        L_0x0143:
            if (r5 == 0) goto L_0x0148;
        L_0x0145:
            r5.close();	 Catch:{ IOException -> 0x01ae }
        L_0x0148:
            if (r13 == 0) goto L_0x014d;
        L_0x014a:
            r13.close();	 Catch:{ IOException -> 0x01b3 }
        L_0x014d:
            if (r15 == 0) goto L_0x0152;
        L_0x014f:
            r15.close();	 Catch:{ IOException -> 0x01b8 }
        L_0x0152:
            r4 = r5;
            r14 = r15;
            r12 = r13;
        L_0x0155:
            r9 = new java.io.File;
            r18 = com.record.utils.Val.SD_BACKUP_NAME;
            r0 = r18;
            r9.<init>(r7, r0);
            r18 = r9.exists();
            if (r18 == 0) goto L_0x0167;
        L_0x0164:
            r9.delete();
        L_0x0167:
            r10 = new java.io.File;
            r18 = new java.lang.StringBuilder;
            r18.<init>();
            r0 = r18;
            r18 = r0.append(r7);
            r19 = java.io.File.separator;
            r18 = r18.append(r19);
            r0 = r18;
            r18 = r0.append(r6);
            r18 = r18.toString();
            r0 = r18;
            r10.<init>(r0);
            r18 = r10.exists();
            if (r18 == 0) goto L_0x0251;
        L_0x018f:
            r10.renameTo(r9);
            r0 = r22;
            r0 = com.record.myLife.settings.BackupDbActivity_v2.this;
            r18 = r0;
            r0 = r22;
            r0 = com.record.myLife.settings.BackupDbActivity_v2.this;
            r19 = r0;
            r0 = r19;
            r0 = r0.HANDLER_BACKUP_FINISH;
            r19 = r0;
            r18.sendSuccess(r19);
            r18 = "备份完成";
            com.record.myLife.settings.BackupDbActivity_v2.log(r18);
            goto L_0x0082;
        L_0x01ae:
            r8 = move-exception;
            r8.printStackTrace();
            goto L_0x0148;
        L_0x01b3:
            r8 = move-exception;
            r8.printStackTrace();
            goto L_0x014d;
        L_0x01b8:
            r8 = move-exception;
            r8.printStackTrace();
            r4 = r5;
            r14 = r15;
            r12 = r13;
            goto L_0x0155;
        L_0x01c0:
            r8 = move-exception;
        L_0x01c1:
            r8.printStackTrace();	 Catch:{ all -> 0x0231 }
            r0 = r22;
            r0 = com.record.myLife.settings.BackupDbActivity_v2.this;	 Catch:{ all -> 0x0231 }
            r18 = r0;
            r19 = new java.lang.StringBuilder;	 Catch:{ all -> 0x0231 }
            r19.<init>();	 Catch:{ all -> 0x0231 }
            r0 = r22;
            r0 = com.record.myLife.settings.BackupDbActivity_v2.this;	 Catch:{ all -> 0x0231 }
            r20 = r0;
            r21 = 2131165311; // 0x7f07007f float:1.7944836E38 double:1.052935566E-314;
            r20 = r20.getString(r21);	 Catch:{ all -> 0x0231 }
            r19 = r19.append(r20);	 Catch:{ all -> 0x0231 }
            r20 = "\n";
            r19 = r19.append(r20);	 Catch:{ all -> 0x0231 }
            r0 = r22;
            r0 = r0.context;	 Catch:{ all -> 0x0231 }
            r20 = r0;
            r20 = r20.getResources();	 Catch:{ all -> 0x0231 }
            r21 = 2131165765; // 0x7f070245 float:1.7945756E38 double:1.05293579E-314;
            r20 = r20.getString(r21);	 Catch:{ all -> 0x0231 }
            r19 = r19.append(r20);	 Catch:{ all -> 0x0231 }
            r20 = "\n";
            r19 = r19.append(r20);	 Catch:{ all -> 0x0231 }
            r20 = com.record.utils.GeneralHelper.getExceptionString(r8);	 Catch:{ all -> 0x0231 }
            r19 = r19.append(r20);	 Catch:{ all -> 0x0231 }
            r19 = r19.toString();	 Catch:{ all -> 0x0231 }
            r18.sendError(r19);	 Catch:{ all -> 0x0231 }
            if (r4 == 0) goto L_0x0215;
        L_0x0212:
            r4.close();	 Catch:{ IOException -> 0x0227 }
        L_0x0215:
            if (r12 == 0) goto L_0x021a;
        L_0x0217:
            r12.close();	 Catch:{ IOException -> 0x022c }
        L_0x021a:
            if (r14 == 0) goto L_0x0155;
        L_0x021c:
            r14.close();	 Catch:{ IOException -> 0x0221 }
            goto L_0x0155;
        L_0x0221:
            r8 = move-exception;
            r8.printStackTrace();
            goto L_0x0155;
        L_0x0227:
            r8 = move-exception;
            r8.printStackTrace();
            goto L_0x0215;
        L_0x022c:
            r8 = move-exception;
            r8.printStackTrace();
            goto L_0x021a;
        L_0x0231:
            r18 = move-exception;
        L_0x0232:
            if (r4 == 0) goto L_0x0237;
        L_0x0234:
            r4.close();	 Catch:{ IOException -> 0x0242 }
        L_0x0237:
            if (r12 == 0) goto L_0x023c;
        L_0x0239:
            r12.close();	 Catch:{ IOException -> 0x0247 }
        L_0x023c:
            if (r14 == 0) goto L_0x0241;
        L_0x023e:
            r14.close();	 Catch:{ IOException -> 0x024c }
        L_0x0241:
            throw r18;
        L_0x0242:
            r8 = move-exception;
            r8.printStackTrace();
            goto L_0x0237;
        L_0x0247:
            r8 = move-exception;
            r8.printStackTrace();
            goto L_0x023c;
        L_0x024c:
            r8 = move-exception;
            r8.printStackTrace();
            goto L_0x0241;
        L_0x0251:
            r0 = r22;
            r0 = com.record.myLife.settings.BackupDbActivity_v2.this;
            r18 = r0;
            r0 = r22;
            r0 = com.record.myLife.settings.BackupDbActivity_v2.this;
            r19 = r0;
            r20 = 2131165312; // 0x7f070080 float:1.7944838E38 double:1.0529355663E-314;
            r19 = r19.getString(r20);
            r18.sendError(r19);
            goto L_0x0082;
        L_0x0269:
            r18 = move-exception;
            r12 = r13;
            goto L_0x0232;
        L_0x026c:
            r18 = move-exception;
            r14 = r15;
            r12 = r13;
            goto L_0x0232;
        L_0x0270:
            r18 = move-exception;
            r4 = r5;
            r14 = r15;
            r12 = r13;
            goto L_0x0232;
        L_0x0275:
            r8 = move-exception;
            r12 = r13;
            goto L_0x01c1;
        L_0x0279:
            r8 = move-exception;
            r14 = r15;
            r12 = r13;
            goto L_0x01c1;
        L_0x027e:
            r8 = move-exception;
            r4 = r5;
            r14 = r15;
            r12 = r13;
            goto L_0x01c1;
            */
            throw new UnsupportedOperationException("Method not decompiled: com.record.myLife.settings.BackupDbActivity_v2.backupDbRunnble.run():void");
        }
    }

    class importRunnable implements Runnable {
        Context context;

        public importRunnable(Context context) {
            this.context = context;
        }

        /* JADX WARNING: Removed duplicated region for block: B:43:0x0186 A:{SYNTHETIC, Splitter: B:43:0x0186} */
        /* JADX WARNING: Removed duplicated region for block: B:46:0x018b A:{SYNTHETIC, Splitter: B:46:0x018b} */
        /* JADX WARNING: Removed duplicated region for block: B:106:? A:{SYNTHETIC, RETURN} */
        /* JADX WARNING: Removed duplicated region for block: B:49:0x0190 A:{SYNTHETIC, Splitter: B:49:0x0190} */
        /* JADX WARNING: Removed duplicated region for block: B:77:0x01e9 A:{SYNTHETIC, Splitter: B:77:0x01e9} */
        /* JADX WARNING: Removed duplicated region for block: B:80:0x01ee A:{SYNTHETIC, Splitter: B:80:0x01ee} */
        /* JADX WARNING: Removed duplicated region for block: B:83:0x01f3 A:{SYNTHETIC, Splitter: B:83:0x01f3} */
        /* JADX WARNING: Removed duplicated region for block: B:43:0x0186 A:{SYNTHETIC, Splitter: B:43:0x0186} */
        /* JADX WARNING: Removed duplicated region for block: B:46:0x018b A:{SYNTHETIC, Splitter: B:46:0x018b} */
        /* JADX WARNING: Removed duplicated region for block: B:49:0x0190 A:{SYNTHETIC, Splitter: B:49:0x0190} */
        /* JADX WARNING: Removed duplicated region for block: B:106:? A:{SYNTHETIC, RETURN} */
        /* JADX WARNING: Removed duplicated region for block: B:77:0x01e9 A:{SYNTHETIC, Splitter: B:77:0x01e9} */
        /* JADX WARNING: Removed duplicated region for block: B:80:0x01ee A:{SYNTHETIC, Splitter: B:80:0x01ee} */
        /* JADX WARNING: Removed duplicated region for block: B:83:0x01f3 A:{SYNTHETIC, Splitter: B:83:0x01f3} */
        /* JADX WARNING: Removed duplicated region for block: B:43:0x0186 A:{SYNTHETIC, Splitter: B:43:0x0186} */
        /* JADX WARNING: Removed duplicated region for block: B:46:0x018b A:{SYNTHETIC, Splitter: B:46:0x018b} */
        /* JADX WARNING: Removed duplicated region for block: B:106:? A:{SYNTHETIC, RETURN} */
        /* JADX WARNING: Removed duplicated region for block: B:49:0x0190 A:{SYNTHETIC, Splitter: B:49:0x0190} */
        /* JADX WARNING: Removed duplicated region for block: B:77:0x01e9 A:{SYNTHETIC, Splitter: B:77:0x01e9} */
        /* JADX WARNING: Removed duplicated region for block: B:80:0x01ee A:{SYNTHETIC, Splitter: B:80:0x01ee} */
        /* JADX WARNING: Removed duplicated region for block: B:83:0x01f3 A:{SYNTHETIC, Splitter: B:83:0x01f3} */
        public void run() {
            /*
            r20 = this;
            r16 = 0;
            com.record.myLife.settings.BackupDbActivity_v2.cryptProgress = r16;
            r16 = 0;
            com.record.myLife.settings.BackupDbActivity_v2.fileSize = r16;
            r9 = 0;
            r11 = 0;
            r4 = 0;
            r16 = new java.lang.StringBuilder;
            r16.<init>();
            r17 = android.os.Environment.getExternalStorageDirectory();
            r16 = r16.append(r17);
            r17 = java.io.File.separator;
            r16 = r16.append(r17);
            r0 = r20;
            r0 = com.record.myLife.settings.BackupDbActivity_v2.this;
            r17 = r0;
            r0 = r17;
            r0 = r0.SD_BACKUP_DIR;
            r17 = r0;
            r16 = r16.append(r17);
            r17 = java.io.File.separator;
            r16 = r16.append(r17);
            r17 = com.record.utils.Val.SD_BACKUP_NAME;
            r16 = r16.append(r17);
            r14 = r16.toString();
            r15 = new java.io.File;	 Catch:{ Exception -> 0x0212 }
            r15.<init>(r14);	 Catch:{ Exception -> 0x0212 }
            r16 = r15.exists();	 Catch:{ Exception -> 0x0212 }
            if (r16 != 0) goto L_0x00bc;
        L_0x0049:
            r16 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0212 }
            r16.<init>();	 Catch:{ Exception -> 0x0212 }
            r17 = android.os.Environment.getExternalStorageDirectory();	 Catch:{ Exception -> 0x0212 }
            r16 = r16.append(r17);	 Catch:{ Exception -> 0x0212 }
            r17 = java.io.File.separator;	 Catch:{ Exception -> 0x0212 }
            r16 = r16.append(r17);	 Catch:{ Exception -> 0x0212 }
            r0 = r20;
            r0 = com.record.myLife.settings.BackupDbActivity_v2.this;	 Catch:{ Exception -> 0x0212 }
            r17 = r0;
            r0 = r17;
            r0 = r0.SD_BACKUP_DIR;	 Catch:{ Exception -> 0x0212 }
            r17 = r0;
            r16 = r16.append(r17);	 Catch:{ Exception -> 0x0212 }
            r17 = java.io.File.separator;	 Catch:{ Exception -> 0x0212 }
            r16 = r16.append(r17);	 Catch:{ Exception -> 0x0212 }
            r17 = com.record.utils.Val.SD_BACKUP_NAME2;	 Catch:{ Exception -> 0x0212 }
            r16 = r16.append(r17);	 Catch:{ Exception -> 0x0212 }
            r14 = r16.toString();	 Catch:{ Exception -> 0x0212 }
            r15 = new java.io.File;	 Catch:{ Exception -> 0x0212 }
            r15.<init>(r14);	 Catch:{ Exception -> 0x0212 }
            r16 = r15.exists();	 Catch:{ Exception -> 0x0212 }
            if (r16 != 0) goto L_0x00bc;
        L_0x0087:
            r0 = r20;
            r0 = com.record.myLife.settings.BackupDbActivity_v2.this;	 Catch:{ Exception -> 0x0212 }
            r16 = r0;
            r0 = r20;
            r0 = com.record.myLife.settings.BackupDbActivity_v2.this;	 Catch:{ Exception -> 0x0212 }
            r17 = r0;
            r18 = 2131165313; // 0x7f070081 float:1.794484E38 double:1.052935567E-314;
            r17 = r17.getString(r18);	 Catch:{ Exception -> 0x0212 }
            r16.sendError(r17);	 Catch:{ Exception -> 0x0212 }
            if (r4 == 0) goto L_0x00a2;
        L_0x009f:
            r4.close();	 Catch:{ IOException -> 0x00ad }
        L_0x00a2:
            if (r9 == 0) goto L_0x00a7;
        L_0x00a4:
            r9.close();	 Catch:{ IOException -> 0x00b2 }
        L_0x00a7:
            if (r11 == 0) goto L_0x00ac;
        L_0x00a9:
            r11.close();	 Catch:{ IOException -> 0x00b7 }
        L_0x00ac:
            return;
        L_0x00ad:
            r6 = move-exception;
            r6.printStackTrace();
            goto L_0x00a2;
        L_0x00b2:
            r6 = move-exception;
            r6.printStackTrace();
            goto L_0x00a7;
        L_0x00b7:
            r6 = move-exception;
            r6.printStackTrace();
            goto L_0x00ac;
        L_0x00bc:
            r0 = r20;
            r0 = com.record.myLife.settings.BackupDbActivity_v2.this;	 Catch:{ Exception -> 0x0212 }
            r16 = r0;
            r0 = r20;
            r0 = com.record.myLife.settings.BackupDbActivity_v2.this;	 Catch:{ Exception -> 0x0212 }
            r17 = r0;
            r18 = 2131165627; // 0x7f0701bb float:1.7945476E38 double:1.052935722E-314;
            r17 = r17.getString(r18);	 Catch:{ Exception -> 0x0212 }
            r16.sendToast(r17);	 Catch:{ Exception -> 0x0212 }
            r16 = r15.length();	 Catch:{ Exception -> 0x0212 }
            r0 = r16;
            r0 = (double) r0;	 Catch:{ Exception -> 0x0212 }
            r16 = r0;
            com.record.myLife.settings.BackupDbActivity_v2.fileSize = r16;	 Catch:{ Exception -> 0x0212 }
            r16 = "DES/ECB/NoPadding";
            r3 = javax.crypto.Cipher.getInstance(r16);	 Catch:{ Exception -> 0x0212 }
            r16 = 2;
            r17 = com.record.utils.Val.getPassword();	 Catch:{ Exception -> 0x0212 }
            r17 = r17.getBytes();	 Catch:{ Exception -> 0x0212 }
            r17 = com.record.utils.MyCipher.toKey(r17);	 Catch:{ Exception -> 0x0212 }
            r0 = r16;
            r1 = r17;
            r3.init(r0, r1);	 Catch:{ Exception -> 0x0212 }
            r10 = new java.io.FileInputStream;	 Catch:{ Exception -> 0x0212 }
            r10.<init>(r15);	 Catch:{ Exception -> 0x0212 }
            r7 = new java.io.File;	 Catch:{ Exception -> 0x0215, all -> 0x0206 }
            r16 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0215, all -> 0x0206 }
            r16.<init>();	 Catch:{ Exception -> 0x0215, all -> 0x0206 }
            r0 = r20;
            r0 = com.record.myLife.settings.BackupDbActivity_v2.this;	 Catch:{ Exception -> 0x0215, all -> 0x0206 }
            r17 = r0;
            r0 = r17;
            r0 = r0.DB_PATH;	 Catch:{ Exception -> 0x0215, all -> 0x0206 }
            r17 = r0;
            r16 = r16.append(r17);	 Catch:{ Exception -> 0x0215, all -> 0x0206 }
            r0 = r20;
            r0 = com.record.myLife.settings.BackupDbActivity_v2.this;	 Catch:{ Exception -> 0x0215, all -> 0x0206 }
            r17 = r0;
            r0 = r17;
            r0 = r0.DB_NAME;	 Catch:{ Exception -> 0x0215, all -> 0x0206 }
            r17 = r0;
            r16 = r16.append(r17);	 Catch:{ Exception -> 0x0215, all -> 0x0206 }
            r16 = r16.toString();	 Catch:{ Exception -> 0x0215, all -> 0x0206 }
            r0 = r16;
            r7.<init>(r0);	 Catch:{ Exception -> 0x0215, all -> 0x0206 }
            r16 = r7.exists();	 Catch:{ Exception -> 0x0215, all -> 0x0206 }
            if (r16 != 0) goto L_0x0136;
        L_0x0133:
            r7.createNewFile();	 Catch:{ Exception -> 0x0215, all -> 0x0206 }
        L_0x0136:
            r12 = new java.io.FileOutputStream;	 Catch:{ Exception -> 0x0215, all -> 0x0206 }
            r12.<init>(r7);	 Catch:{ Exception -> 0x0215, all -> 0x0206 }
            r5 = new javax.crypto.CipherInputStream;	 Catch:{ Exception -> 0x0219, all -> 0x0209 }
            r5.<init>(r10, r3);	 Catch:{ Exception -> 0x0219, all -> 0x0209 }
            r16 = 10240; // 0x2800 float:1.4349E-41 double:5.059E-320;
            r0 = r16;
            r2 = new byte[r0];	 Catch:{ Exception -> 0x0167, all -> 0x020d }
            r8 = 0;
        L_0x0147:
            r13 = r5.read(r2);	 Catch:{ Exception -> 0x0167, all -> 0x020d }
            if (r13 <= 0) goto L_0x019b;
        L_0x014d:
            r16 = 0;
            r0 = r16;
            r12.write(r2, r0, r13);	 Catch:{ Exception -> 0x0167, all -> 0x020d }
            r16 = com.record.myLife.settings.BackupDbActivity_v2.cryptProgress;	 Catch:{ Exception -> 0x0167, all -> 0x020d }
            r0 = (double) r13;	 Catch:{ Exception -> 0x0167, all -> 0x020d }
            r18 = r0;
            r16 = r16 + r18;
            com.record.myLife.settings.BackupDbActivity_v2.cryptProgress = r16;	 Catch:{ Exception -> 0x0167, all -> 0x020d }
            r0 = r20;
            r0 = com.record.myLife.settings.BackupDbActivity_v2.this;	 Catch:{ Exception -> 0x0167, all -> 0x020d }
            r16 = r0;
            r16.sendPb();	 Catch:{ Exception -> 0x0167, all -> 0x020d }
            goto L_0x0147;
        L_0x0167:
            r6 = move-exception;
            r4 = r5;
            r11 = r12;
            r9 = r10;
        L_0x016b:
            r6.printStackTrace();	 Catch:{ all -> 0x01e6 }
            r0 = r20;
            r0 = com.record.myLife.settings.BackupDbActivity_v2.this;	 Catch:{ all -> 0x01e6 }
            r16 = r0;
            r0 = r20;
            r0 = com.record.myLife.settings.BackupDbActivity_v2.this;	 Catch:{ all -> 0x01e6 }
            r17 = r0;
            r18 = 2131165476; // 0x7f070124 float:1.794517E38 double:1.0529356473E-314;
            r17 = r17.getString(r18);	 Catch:{ all -> 0x01e6 }
            r16.sendError(r17);	 Catch:{ all -> 0x01e6 }
            if (r4 == 0) goto L_0x0189;
        L_0x0186:
            r4.close();	 Catch:{ IOException -> 0x01dc }
        L_0x0189:
            if (r9 == 0) goto L_0x018e;
        L_0x018b:
            r9.close();	 Catch:{ IOException -> 0x01e1 }
        L_0x018e:
            if (r11 == 0) goto L_0x00ac;
        L_0x0190:
            r11.close();	 Catch:{ IOException -> 0x0195 }
            goto L_0x00ac;
        L_0x0195:
            r6 = move-exception;
            r6.printStackTrace();
            goto L_0x00ac;
        L_0x019b:
            r0 = r20;
            r0 = com.record.myLife.settings.BackupDbActivity_v2.this;	 Catch:{ Exception -> 0x0167, all -> 0x020d }
            r16 = r0;
            r0 = r20;
            r0 = com.record.myLife.settings.BackupDbActivity_v2.this;	 Catch:{ Exception -> 0x0167, all -> 0x020d }
            r17 = r0;
            r0 = r17;
            r0 = r0.HANDLER_IMPORT_FINISH;	 Catch:{ Exception -> 0x0167, all -> 0x020d }
            r17 = r0;
            r16.sendSuccess(r17);	 Catch:{ Exception -> 0x0167, all -> 0x020d }
            r16 = "导入完成";
            com.record.myLife.settings.BackupDbActivity_v2.log(r16);	 Catch:{ Exception -> 0x0167, all -> 0x020d }
            if (r5 == 0) goto L_0x01ba;
        L_0x01b7:
            r5.close();	 Catch:{ IOException -> 0x01c9 }
        L_0x01ba:
            if (r10 == 0) goto L_0x01bf;
        L_0x01bc:
            r10.close();	 Catch:{ IOException -> 0x01ce }
        L_0x01bf:
            if (r12 == 0) goto L_0x01c4;
        L_0x01c1:
            r12.close();	 Catch:{ IOException -> 0x01d3 }
        L_0x01c4:
            r4 = r5;
            r11 = r12;
            r9 = r10;
            goto L_0x00ac;
        L_0x01c9:
            r6 = move-exception;
            r6.printStackTrace();
            goto L_0x01ba;
        L_0x01ce:
            r6 = move-exception;
            r6.printStackTrace();
            goto L_0x01bf;
        L_0x01d3:
            r6 = move-exception;
            r6.printStackTrace();
            r4 = r5;
            r11 = r12;
            r9 = r10;
            goto L_0x00ac;
        L_0x01dc:
            r6 = move-exception;
            r6.printStackTrace();
            goto L_0x0189;
        L_0x01e1:
            r6 = move-exception;
            r6.printStackTrace();
            goto L_0x018e;
        L_0x01e6:
            r16 = move-exception;
        L_0x01e7:
            if (r4 == 0) goto L_0x01ec;
        L_0x01e9:
            r4.close();	 Catch:{ IOException -> 0x01f7 }
        L_0x01ec:
            if (r9 == 0) goto L_0x01f1;
        L_0x01ee:
            r9.close();	 Catch:{ IOException -> 0x01fc }
        L_0x01f1:
            if (r11 == 0) goto L_0x01f6;
        L_0x01f3:
            r11.close();	 Catch:{ IOException -> 0x0201 }
        L_0x01f6:
            throw r16;
        L_0x01f7:
            r6 = move-exception;
            r6.printStackTrace();
            goto L_0x01ec;
        L_0x01fc:
            r6 = move-exception;
            r6.printStackTrace();
            goto L_0x01f1;
        L_0x0201:
            r6 = move-exception;
            r6.printStackTrace();
            goto L_0x01f6;
        L_0x0206:
            r16 = move-exception;
            r9 = r10;
            goto L_0x01e7;
        L_0x0209:
            r16 = move-exception;
            r11 = r12;
            r9 = r10;
            goto L_0x01e7;
        L_0x020d:
            r16 = move-exception;
            r4 = r5;
            r11 = r12;
            r9 = r10;
            goto L_0x01e7;
        L_0x0212:
            r6 = move-exception;
            goto L_0x016b;
        L_0x0215:
            r6 = move-exception;
            r9 = r10;
            goto L_0x016b;
        L_0x0219:
            r6 = move-exception;
            r11 = r12;
            r9 = r10;
            goto L_0x016b;
            */
            throw new UnsupportedOperationException("Method not decompiled: com.record.myLife.settings.BackupDbActivity_v2.importRunnable.run():void");
        }
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getIntent().getIntExtra("fromAutoBackup", 0) > 0) {
            this.isNormal = false;
            backupData_v2();
            return;
        }
        SystemBarTintManager.setMIUIbar(this);
        setContentView(R.layout.activity_backup);
        this.context = this;
        this.btn_support_back = (Button) findViewById(R.id.btn_support_back);
        this.btn_backup_leading_in = (Button) findViewById(R.id.btn_backup_leading_in);
        this.btn_backup_backup_to_sd = (Button) findViewById(R.id.btn_backup_backup_to_sd);
        this.btn_export = (Button) findViewById(R.id.btn_export);
        this.tv_backup_backup_to_sd_path = (TextView) findViewById(R.id.tv_backup_backup_to_sd_path);
        this.tv_backup_backup_path_prompt = (TextView) findViewById(R.id.tv_backup_backup_path_prompt);
        this.iv_backup_instrution = (TextView) findViewById(R.id.iv_backup_instrution);
        this.tv_auto_backup_v2_percent = (TextView) findViewById(R.id.tv_auto_backup_v2_percent);
        this.iv_auto_backup = (ImageView) findViewById(R.id.iv_auto_backup);
        this.rl_auto_backup_v2_pb = (ProgressBar) findViewById(R.id.rl_auto_backup_v2_pb);
        this.tv_auto_backup_v2_percent.setVisibility(4);
        this.btn_support_back.setOnClickListener(this.myClickListener);
        this.btn_backup_leading_in.setOnClickListener(this.myClickListener);
        this.btn_backup_backup_to_sd.setOnClickListener(this.myClickListener);
        this.iv_backup_instrution.setOnClickListener(this.myClickListener);
        this.iv_auto_backup.setOnClickListener(this.myClickListener);
        this.btn_export.setOnClickListener(this.myClickListener);
        initUI();
        SharedPreferences sp = getSharedPreferences(Val.CONFIGURE_NAME, 0);
        if (sp.getInt(Val.CONFIGURE_IS_SHOW_DIALOG_BACKUP_INSTRUCTION, 0) < 2) {
            this.myClickListener.onClick(this.iv_backup_instrution);
            sp.edit().putInt(Val.CONFIGURE_IS_SHOW_DIALOG_BACKUP_INSTRUCTION, 2).commit();
        }
    }

    private void doWhileError() {
        this.btn_backup_backup_to_sd.setClickable(true);
        this.btn_backup_leading_in.setClickable(true);
        this.tv_auto_backup_v2_percent.setVisibility(4);
    }

    private void doBeforeBackupOrImport() {
        this.btn_backup_backup_to_sd.setClickable(false);
        this.btn_backup_leading_in.setClickable(false);
    }

    private void doAfterSuccess() {
        this.rl_auto_backup_v2_pb.setMax(10000);
        this.rl_auto_backup_v2_pb.setProgress(0);
        this.tv_auto_backup_v2_percent.setVisibility(4);
        this.btn_backup_backup_to_sd.setClickable(true);
        this.btn_backup_leading_in.setClickable(true);
    }

    private void initUI() {
        getSharedPreferences(Val.CONFIGURE_NAME_DOT, 0).edit().putInt(Val.CONFIGURE_SET_BACK_UP, 1).commit();
        if (new File(Environment.getExternalStorageDirectory() + "/" + this.SD_BACKUP_DIR + "/" + Val.SD_BACKUP_NAME).exists()) {
            this.tv_backup_backup_to_sd_path.setText(getResources().getString(R.string.str_back_up_path2) + "/" + this.SD_BACKUP_DIR + "/" + Val.SD_BACKUP_NAME);
            this.tv_backup_backup_to_sd_path.setVisibility(0);
            this.tv_backup_backup_path_prompt.setVisibility(4);
        } else {
            this.tv_backup_backup_path_prompt.setVisibility(0);
            this.tv_backup_backup_to_sd_path.setVisibility(4);
        }
        if (getSharedPreferences(Val.CONFIGURE_NAME, 0).getInt(Val.CONFIGURE_IS_AUTO_BACKUP_DATA, 0) > 0) {
            this.iv_auto_backup.setImageResource(R.drawable.ic_on_v2);
        } else {
            this.iv_auto_backup.setImageResource(R.drawable.ic_off_v2);
        }
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
            DB_PATH = Environment.getDataDirectory() + "/data/" + this.packageName + "/databases/";
            DB_NAME = this.DB_NAME;
        }
        if (new File(DB_PATH + DB_NAME).exists()) {
            Log.v("override", "删除：" + DB_PATH + DB_NAME);
            new File(DB_PATH + DB_NAME).delete();
            Log.v("override", "删SUCCESS");
        }
        if (new File(DB_PATH + DB_NAME).exists()) {
            GeneralHelper.toastShort(this.context, "原数据库存在！");
        } else {
            File f = new File(DB_PATH);
            if (!f.exists()) {
                f.mkdir();
            }
            try {
                String sbPath = Environment.getExternalStorageDirectory() + File.separator + this.SD_BACKUP_DIR + File.separator + Val.SD_BACKUP_NAME;
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

    /* JADX WARNING: Removed duplicated region for block: B:44:0x01b3 A:{SYNTHETIC, Splitter: B:44:0x01b3} */
    /* JADX WARNING: Removed duplicated region for block: B:47:0x01b8 A:{SYNTHETIC, Splitter: B:47:0x01b8} */
    /* JADX WARNING: Removed duplicated region for block: B:50:0x01bd A:{SYNTHETIC, Splitter: B:50:0x01bd} */
    /* JADX WARNING: Removed duplicated region for block: B:78:0x01fe A:{SYNTHETIC, Splitter: B:78:0x01fe} */
    /* JADX WARNING: Removed duplicated region for block: B:81:0x0203 A:{SYNTHETIC, Splitter: B:81:0x0203} */
    /* JADX WARNING: Removed duplicated region for block: B:84:0x0208 A:{SYNTHETIC, Splitter: B:84:0x0208} */
    /* JADX WARNING: Removed duplicated region for block: B:44:0x01b3 A:{SYNTHETIC, Splitter: B:44:0x01b3} */
    /* JADX WARNING: Removed duplicated region for block: B:47:0x01b8 A:{SYNTHETIC, Splitter: B:47:0x01b8} */
    /* JADX WARNING: Removed duplicated region for block: B:50:0x01bd A:{SYNTHETIC, Splitter: B:50:0x01bd} */
    /* JADX WARNING: Removed duplicated region for block: B:78:0x01fe A:{SYNTHETIC, Splitter: B:78:0x01fe} */
    /* JADX WARNING: Removed duplicated region for block: B:81:0x0203 A:{SYNTHETIC, Splitter: B:81:0x0203} */
    /* JADX WARNING: Removed duplicated region for block: B:84:0x0208 A:{SYNTHETIC, Splitter: B:84:0x0208} */
    /* JADX WARNING: Removed duplicated region for block: B:44:0x01b3 A:{SYNTHETIC, Splitter: B:44:0x01b3} */
    /* JADX WARNING: Removed duplicated region for block: B:47:0x01b8 A:{SYNTHETIC, Splitter: B:47:0x01b8} */
    /* JADX WARNING: Removed duplicated region for block: B:50:0x01bd A:{SYNTHETIC, Splitter: B:50:0x01bd} */
    /* JADX WARNING: Removed duplicated region for block: B:78:0x01fe A:{SYNTHETIC, Splitter: B:78:0x01fe} */
    /* JADX WARNING: Removed duplicated region for block: B:81:0x0203 A:{SYNTHETIC, Splitter: B:81:0x0203} */
    /* JADX WARNING: Removed duplicated region for block: B:84:0x0208 A:{SYNTHETIC, Splitter: B:84:0x0208} */
    public boolean insertData_v2() {
        /*
        r25 = this;
        r17 = 0;
        r4 = "";
        r3 = "";
        r0 = r25;
        r0 = r0.context;
        r22 = r0;
        r22 = com.record.utils.db.DbUtils.getDb(r22);
        r10 = r22.getPath();
        if (r10 == 0) goto L_0x0040;
    L_0x0016:
        r22 = r10.length();
        if (r22 <= 0) goto L_0x0040;
    L_0x001c:
        r22 = "/";
        r0 = r22;
        r14 = r10.lastIndexOf(r0);
        if (r14 <= 0) goto L_0x0040;
    L_0x0026:
        r22 = 0;
        r23 = r14 + 1;
        r0 = r22;
        r1 = r23;
        r4 = r10.substring(r0, r1);
        r22 = r14 + 1;
        r23 = r10.length();
        r0 = r22;
        r1 = r23;
        r3 = r10.substring(r0, r1);
    L_0x0040:
        if (r4 == 0) goto L_0x0050;
    L_0x0042:
        r22 = r4.length();
        if (r22 == 0) goto L_0x0050;
    L_0x0048:
        if (r3 == 0) goto L_0x0050;
    L_0x004a:
        r22 = r3.length();
        if (r22 != 0) goto L_0x007b;
    L_0x0050:
        r22 = new java.lang.StringBuilder;
        r22.<init>();
        r23 = android.os.Environment.getDataDirectory();
        r22 = r22.append(r23);
        r23 = "/data/";
        r22 = r22.append(r23);
        r0 = r25;
        r0 = r0.packageName;
        r23 = r0;
        r22 = r22.append(r23);
        r23 = "/databases/";
        r22 = r22.append(r23);
        r4 = r22.toString();
        r0 = r25;
        r3 = r0.DB_NAME;
    L_0x007b:
        r22 = new java.io.File;
        r23 = new java.lang.StringBuilder;
        r23.<init>();
        r0 = r23;
        r23 = r0.append(r4);
        r0 = r23;
        r23 = r0.append(r3);
        r23 = r23.toString();
        r22.<init>(r23);
        r22 = r22.exists();
        if (r22 == 0) goto L_0x00d8;
    L_0x009b:
        r22 = "override";
        r23 = new java.lang.StringBuilder;
        r23.<init>();
        r24 = "删除：";
        r23 = r23.append(r24);
        r0 = r23;
        r23 = r0.append(r4);
        r0 = r23;
        r23 = r0.append(r3);
        r23 = r23.toString();
        android.util.Log.v(r22, r23);
        r22 = new java.io.File;
        r23 = new java.lang.StringBuilder;
        r23.<init>();
        r0 = r23;
        r23 = r0.append(r4);
        r0 = r23;
        r23 = r0.append(r3);
        r23 = r23.toString();
        r22.<init>(r23);
        r22.delete();
    L_0x00d8:
        r9 = new java.io.File;
        r22 = new java.lang.StringBuilder;
        r22.<init>();
        r0 = r22;
        r22 = r0.append(r4);
        r0 = r22;
        r22 = r0.append(r3);
        r22 = r22.toString();
        r0 = r22;
        r9.<init>(r0);
        r22 = r9.exists();
        if (r22 != 0) goto L_0x021b;
    L_0x00fa:
        r12 = new java.io.File;
        r12.<init>(r4);
        r22 = r12.exists();
        if (r22 != 0) goto L_0x0108;
    L_0x0105:
        r12.mkdirs();
    L_0x0108:
        r15 = 0;
        r18 = 0;
        r7 = 0;
        r22 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0238 }
        r22.<init>();	 Catch:{ Exception -> 0x0238 }
        r23 = android.os.Environment.getExternalStorageDirectory();	 Catch:{ Exception -> 0x0238 }
        r22 = r22.append(r23);	 Catch:{ Exception -> 0x0238 }
        r23 = java.io.File.separator;	 Catch:{ Exception -> 0x0238 }
        r22 = r22.append(r23);	 Catch:{ Exception -> 0x0238 }
        r0 = r25;
        r0 = r0.SD_BACKUP_DIR;	 Catch:{ Exception -> 0x0238 }
        r23 = r0;
        r22 = r22.append(r23);	 Catch:{ Exception -> 0x0238 }
        r23 = java.io.File.separator;	 Catch:{ Exception -> 0x0238 }
        r22 = r22.append(r23);	 Catch:{ Exception -> 0x0238 }
        r23 = com.record.utils.Val.SD_BACKUP_NAME;	 Catch:{ Exception -> 0x0238 }
        r22 = r22.append(r23);	 Catch:{ Exception -> 0x0238 }
        r21 = r22.toString();	 Catch:{ Exception -> 0x0238 }
        r22 = "DES/ECB/NoPadding";
        r6 = javax.crypto.Cipher.getInstance(r22);	 Catch:{ Exception -> 0x0238 }
        r22 = 2;
        r23 = com.record.utils.Val.getPassword();	 Catch:{ Exception -> 0x0238 }
        r23 = r23.getBytes();	 Catch:{ Exception -> 0x0238 }
        r23 = com.record.utils.MyCipher.toKey(r23);	 Catch:{ Exception -> 0x0238 }
        r0 = r22;
        r1 = r23;
        r6.init(r0, r1);	 Catch:{ Exception -> 0x0238 }
        r16 = new java.io.FileInputStream;	 Catch:{ Exception -> 0x0238 }
        r0 = r16;
        r1 = r21;
        r0.<init>(r1);	 Catch:{ Exception -> 0x0238 }
        r13 = new java.io.File;	 Catch:{ Exception -> 0x023b, all -> 0x0227 }
        r22 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x023b, all -> 0x0227 }
        r22.<init>();	 Catch:{ Exception -> 0x023b, all -> 0x0227 }
        r0 = r22;
        r22 = r0.append(r4);	 Catch:{ Exception -> 0x023b, all -> 0x0227 }
        r0 = r22;
        r22 = r0.append(r3);	 Catch:{ Exception -> 0x023b, all -> 0x0227 }
        r22 = r22.toString();	 Catch:{ Exception -> 0x023b, all -> 0x0227 }
        r0 = r22;
        r13.<init>(r0);	 Catch:{ Exception -> 0x023b, all -> 0x0227 }
        r22 = r13.exists();	 Catch:{ Exception -> 0x023b, all -> 0x0227 }
        if (r22 != 0) goto L_0x0182;
    L_0x017f:
        r13.createNewFile();	 Catch:{ Exception -> 0x023b, all -> 0x0227 }
    L_0x0182:
        r19 = new java.io.FileOutputStream;	 Catch:{ Exception -> 0x023b, all -> 0x0227 }
        r0 = r19;
        r0.<init>(r13);	 Catch:{ Exception -> 0x023b, all -> 0x0227 }
        r8 = new javax.crypto.CipherInputStream;	 Catch:{ Exception -> 0x0240, all -> 0x022b }
        r0 = r16;
        r8.<init>(r0, r6);	 Catch:{ Exception -> 0x0240, all -> 0x022b }
        r22 = 1024; // 0x400 float:1.435E-42 double:5.06E-321;
        r0 = r22;
        r5 = new byte[r0];	 Catch:{ Exception -> 0x01a8, all -> 0x0231 }
    L_0x0196:
        r20 = r8.read(r5);	 Catch:{ Exception -> 0x01a8, all -> 0x0231 }
        if (r20 <= 0) goto L_0x01c1;
    L_0x019c:
        r22 = 0;
        r0 = r19;
        r1 = r22;
        r2 = r20;
        r0.write(r5, r1, r2);	 Catch:{ Exception -> 0x01a8, all -> 0x0231 }
        goto L_0x0196;
    L_0x01a8:
        r11 = move-exception;
        r7 = r8;
        r18 = r19;
        r15 = r16;
    L_0x01ae:
        r11.printStackTrace();	 Catch:{ all -> 0x01fb }
        if (r7 == 0) goto L_0x01b6;
    L_0x01b3:
        r7.close();	 Catch:{ IOException -> 0x01ec }
    L_0x01b6:
        if (r15 == 0) goto L_0x01bb;
    L_0x01b8:
        r15.close();	 Catch:{ IOException -> 0x01f1 }
    L_0x01bb:
        if (r18 == 0) goto L_0x01c0;
    L_0x01bd:
        r18.close();	 Catch:{ IOException -> 0x01f6 }
    L_0x01c0:
        return r17;
    L_0x01c1:
        r17 = 1;
        if (r8 == 0) goto L_0x01c8;
    L_0x01c5:
        r8.close();	 Catch:{ IOException -> 0x01d8 }
    L_0x01c8:
        if (r16 == 0) goto L_0x01cd;
    L_0x01ca:
        r16.close();	 Catch:{ IOException -> 0x01dd }
    L_0x01cd:
        if (r19 == 0) goto L_0x01d2;
    L_0x01cf:
        r19.close();	 Catch:{ IOException -> 0x01e2 }
    L_0x01d2:
        r7 = r8;
        r18 = r19;
        r15 = r16;
        goto L_0x01c0;
    L_0x01d8:
        r11 = move-exception;
        r11.printStackTrace();
        goto L_0x01c8;
    L_0x01dd:
        r11 = move-exception;
        r11.printStackTrace();
        goto L_0x01cd;
    L_0x01e2:
        r11 = move-exception;
        r11.printStackTrace();
        r7 = r8;
        r18 = r19;
        r15 = r16;
        goto L_0x01c0;
    L_0x01ec:
        r11 = move-exception;
        r11.printStackTrace();
        goto L_0x01b6;
    L_0x01f1:
        r11 = move-exception;
        r11.printStackTrace();
        goto L_0x01bb;
    L_0x01f6:
        r11 = move-exception;
        r11.printStackTrace();
        goto L_0x01c0;
    L_0x01fb:
        r22 = move-exception;
    L_0x01fc:
        if (r7 == 0) goto L_0x0201;
    L_0x01fe:
        r7.close();	 Catch:{ IOException -> 0x020c }
    L_0x0201:
        if (r15 == 0) goto L_0x0206;
    L_0x0203:
        r15.close();	 Catch:{ IOException -> 0x0211 }
    L_0x0206:
        if (r18 == 0) goto L_0x020b;
    L_0x0208:
        r18.close();	 Catch:{ IOException -> 0x0216 }
    L_0x020b:
        throw r22;
    L_0x020c:
        r11 = move-exception;
        r11.printStackTrace();
        goto L_0x0201;
    L_0x0211:
        r11 = move-exception;
        r11.printStackTrace();
        goto L_0x0206;
    L_0x0216:
        r11 = move-exception;
        r11.printStackTrace();
        goto L_0x020b;
    L_0x021b:
        r0 = r25;
        r0 = r0.context;
        r22 = r0;
        r23 = "原数据库存在！";
        com.record.utils.GeneralHelper.toastShort(r22, r23);
        goto L_0x01c0;
    L_0x0227:
        r22 = move-exception;
        r15 = r16;
        goto L_0x01fc;
    L_0x022b:
        r22 = move-exception;
        r18 = r19;
        r15 = r16;
        goto L_0x01fc;
    L_0x0231:
        r22 = move-exception;
        r7 = r8;
        r18 = r19;
        r15 = r16;
        goto L_0x01fc;
    L_0x0238:
        r11 = move-exception;
        goto L_0x01ae;
    L_0x023b:
        r11 = move-exception;
        r15 = r16;
        goto L_0x01ae;
    L_0x0240:
        r11 = move-exception;
        r18 = r19;
        r15 = r16;
        goto L_0x01ae;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.record.myLife.settings.BackupDbActivity_v2.insertData_v2():boolean");
    }

    private void showExportItemsDialog() {
        final String[] exportArr = new String[]{"导出全部数据", "导出本月数据", "导出本周数据", "导出今天数据"};
        new Builder(this.context).setTitle((CharSequence) "选择导出项").setItems(exportArr, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                BackupDbActivity_v2.this.exportDate(BackupDbActivity_v2.this.getDate(which), exportArr[which]);
            }
        }).setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        }).create().show();
    }

    private String getDate(int which) {
        String date = "";
        if (which == 0) {
            return "2000-01-01";
        }
        if (1 == which) {
            return DateTime.getFirstDateForMonth(DateTime.getTimeString());
        }
        if (2 == which) {
            return DateTime.getFirstDateForWeek(this.context, DateTime.getTimeString());
        }
        if (3 == which) {
            return DateTime.getDateString();
        }
        return date;
    }

    private void exportDate(String date, String actionTypeName) {
        if (this.importThread == null || !this.importThread.isAlive()) {
            Cursor cursor = DbUtils.getDb(this.context).rawQuery("select * from " + "t_act_item" + " where " + DbUtils.getWhereUserId(this.context) + " and isDelete is not 1 and startTime >= '" + date + " 00:00:00'  order by startTime", null);
            if (cursor.getCount() > 0) {
                ToastUtils.toastShort(this.context, "正在导出，请稍候...");
                this.importThread = new Thread(new ExportData(cursor, actionTypeName, date));
                this.importThread.start();
                return;
            }
            ToastUtils.toastShort(this.context, "没有" + date + "以后的数据！");
            return;
        }
        ToastUtils.toastShort(this.context, "正在导出数据，请稍候再试！");
    }

    public TempExportResult exportCSV(Cursor cursor, String fileName, String startDate, String endDate) {
        this.exportPath = Environment.getExternalStorageDirectory() + "/" + Val.SD_BACKUP_DIR;
        File file = new File(this.exportPath, fileName);
        FileUtils.creatDirIfDirIsFileThenDelete(this.exportPath);
        try {
            if (!file.exists()) {
                file.createNewFile();
            }
            BufferedWriter bfw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), this.encodeset));
            HashMap<Integer, String> typeMap = ActTypeService.getActTypeMap(this.context);
            HashMap<Integer, String> idToNameMap = DbUtils.getActIdToNameMap(this.context);
            int temid = 1;
            bfw.write("每天记录：");
            bfw.newLine();
            bfw.write("序号,目标,类型,开始时间,结束时间,共花费(秒),备注");
            bfw.newLine();
            cursor.moveToFirst();
            while (cursor.moveToNext()) {
                int actId = cursor.getInt(cursor.getColumnIndex("actId"));
                String typeStr = "";
                try {
                    typeStr = (String) typeMap.get(Integer.valueOf(cursor.getInt(cursor.getColumnIndex("actType"))));
                } catch (Exception e) {
                    DbUtils.exceptionHandler(this.context, e);
                }
                String startTime = cursor.getString(cursor.getColumnIndex("startTime"));
                String stopTime = cursor.getString(cursor.getColumnIndex("stopTime"));
                int take = cursor.getInt(cursor.getColumnIndex("take"));
                String remarks = cursor.getString(cursor.getColumnIndex("remarks"));
                if (remarks == null) {
                    remarks = "";
                }
                String name = "" + actId;
                try {
                    name = (String) idToNameMap.get(Integer.valueOf(actId));
                } catch (Exception e2) {
                    e2.printStackTrace();
                }
                if (typeStr != null) {
                    if (typeStr.length() != 0) {
                        bfw.write(temid + "," + name + "," + typeStr + "," + startTime + "," + stopTime + "," + take + "," + remarks);
                        bfw.newLine();
                        temid++;
                    }
                }
                typeStr = "";
                bfw.write(temid + "," + name + "," + typeStr + "," + startTime + "," + stopTime + "," + take + "," + remarks);
                bfw.newLine();
                temid++;
            }
            bfw.newLine();
            bfw.newLine();
            bfw.write("晨音与总结：");
            bfw.newLine();
            bfw.write("序号,日期,晨音,总结");
            bfw.newLine();
            ArrayList<Allocation> summaryList = DbUtils.getSummarysByDay(this.context, startDate, endDate);
            int listSize = summaryList.size();
            for (int i = 0; i < listSize; i++) {
                Allocation al = (Allocation) summaryList.get(i);
                bfw.write((i + 1) + "," + al.getDate() + "," + (al.getMorningVoice() == null ? "" : al.getMorningVoice()) + "," + (al.getRemarks() == null ? "" : al.getRemarks()));
                bfw.newLine();
            }
            bfw.flush();
            bfw.close();
            return new TempExportResult(true, "");
        } catch (Exception e22) {
            DbUtils.exceptionHandler(e22);
            return new TempExportResult(false, GeneralHelper.getExceptionString(e22));
        }
    }

    private TempExportResult exportExcel(Cursor cursor, String fileName, String startDate, String endDate) {
        try {
            this.exportPath = Environment.getExternalStorageDirectory() + "/" + Val.SD_BACKUP_DIR;
            File file = new File(this.exportPath, fileName);
            WorkbookSettings wbSettings = new WorkbookSettings();
            wbSettings.setLocale(new Locale(Val.CONFIGURE_LANGUAGE_DEFAULT, "ZH"));
            WritableWorkbook workbook = Workbook.createWorkbook(file, wbSettings);
            WritableSheet sheet = workbook.createSheet("爱今天", 0);
            sheet.addCell(new Label(0, 0, "晨音与总结："));
            int row = 0 + 1;
            sheet.addCell(new Label(0, row, "序号"));
            sheet.addCell(new Label(1, row, "日期"));
            sheet.addCell(new Label(2, row, "晨音"));
            sheet.addCell(new Label(3, row, "总结"));
            row++;
            ArrayList<Allocation> summaryList = DbUtils.getSummarysByDay(this.context, startDate, endDate);
            int listSize = summaryList.size();
            for (int i = 0; i < listSize; i++) {
                Allocation al = (Allocation) summaryList.get(i);
                String moring = al.getMorningVoice() == null ? "" : al.getMorningVoice();
                String remark = al.getRemarks() == null ? "" : al.getRemarks();
                sheet.addCell(new Label(0, row, (i + 1) + ""));
                sheet.addCell(new Label(1, row, al.getDate()));
                sheet.addCell(new Label(2, row, moring));
                sheet.addCell(new Label(3, row, remark));
                row++;
            }
            row = (row + 1) + 1;
            HashMap<Integer, String> typeMap = ActTypeService.getActTypeMap(this.context);
            HashMap<Integer, String> idToNameMap = DbUtils.getActIdToNameMap(this.context);
            sheet.addCell(new Label(0, row, "每天记录："));
            row++;
            sheet.addCell(new Label(0, row, "序号"));
            sheet.addCell(new Label(1, row, "目标"));
            sheet.addCell(new Label(2, row, "类型"));
            sheet.addCell(new Label(3, row, "开始时间"));
            sheet.addCell(new Label(4, row, "结束时间"));
            sheet.addCell(new Label(5, row, "共花费(秒)"));
            sheet.addCell(new Label(6, row, "备注"));
            row++;
            int temid = 1;
            cursor.moveToFirst();
            while (cursor.moveToNext()) {
                int actId = cursor.getInt(cursor.getColumnIndex("actId"));
                int temp_type = cursor.getInt(cursor.getColumnIndex("actType"));
                String startTime = cursor.getString(cursor.getColumnIndex("startTime"));
                String stopTime = cursor.getString(cursor.getColumnIndex("stopTime"));
                int take = cursor.getInt(cursor.getColumnIndex("take"));
                String remarks = cursor.getString(cursor.getColumnIndex("remarks"));
                if (remarks == null) {
                    remarks = "";
                }
                String name = "" + actId;
                try {
                    name = (String) idToNameMap.get(Integer.valueOf(actId));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                sheet.addCell(new Label(0, row, temid + ""));
                sheet.addCell(new Label(1, row, name));
                try {
                    sheet.addCell(new Label(2, row, (String) typeMap.get(Integer.valueOf(temp_type))));
                } catch (Exception e2) {
                    e2.printStackTrace();
                }
                try {
                    sheet.addCell(new Label(3, row, startTime));
                    sheet.addCell(new Label(4, row, stopTime));
                    sheet.addCell(new Label(5, row, take + ""));
                    sheet.addCell(new Label(6, row, remarks));
                    row++;
                    temid++;
                } catch (Exception e22) {
                    e22.printStackTrace();
                    return new TempExportResult(false, "导出Excel异常！");
                }
            }
            workbook.write();
            try {
                workbook.close();
                return new TempExportResult(true, "");
            } catch (WriteException e3) {
                return new TempExportResult(false, GeneralHelper.getExceptionString(e3));
            }
        } catch (Exception e222) {
            e222.printStackTrace();
            return new TempExportResult(false, "");
        }
    }

    private void clickImport() {
        if (this.backupThread != null && this.backupThread.isAlive()) {
            GeneralUtils.toastShort(this.context, getString(R.string.str_backing_up2));
        } else if (this.importThread != null && this.importThread.isAlive()) {
            GeneralUtils.toastShort(this.context, getString(R.string.str_importing_data2));
        } else if (this.importThread == null || !this.importThread.isAlive()) {
            new Builder(this.context).setTitle(getResources().getString(R.string.str_is_leading_in)).setMessage(getResources().getString(R.string.str_is_leading_in_prompt) + "\n\n" + getResources().getString(R.string.str_prompt) + "\n" + getResources().getString(R.string.str_leading_in_guide)).setNegativeButton(getResources().getString(R.string.str_cancel), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            }).setPositiveButton(getResources().getString(R.string.str_leading_in), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    if (BackupDbActivity_v2.this.importThread == null || !BackupDbActivity_v2.this.importThread.isAlive()) {
                        BackupDbActivity_v2.this.importThread = new Thread(new importRunnable(BackupDbActivity_v2.this.context));
                        BackupDbActivity_v2.this.importThread.start();
                    }
                    dialog.cancel();
                }
            }).create().show();
        }
    }

    public void clickBackup() {
        if (this.importThread != null && this.importThread.isAlive()) {
            GeneralUtils.toastShort(this.context, getString(R.string.str_importing_data));
        } else if (this.backupThread != null && this.backupThread.isAlive()) {
            GeneralHelper.toastShort(this.context, getString(R.string.str_backing_up));
        } else if (new File(Environment.getExternalStorageDirectory() + "/" + this.SD_BACKUP_DIR + "/" + Val.SD_BACKUP_NAME).exists()) {
            new Builder(this.context).setTitle(getResources().getString(R.string.str_is_backup)).setMessage(getResources().getString(R.string.str_detect_backup_file_exist)).setNegativeButton(getResources().getString(R.string.str_cancel), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            }).setPositiveButton(getResources().getString(R.string.str_backup), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    BackupDbActivity_v2.this.backupData_v2();
                    dialog.cancel();
                }
            }).create().show();
        } else {
            backupData_v2();
        }
    }

    public void backupData_v2() {
        try {
            log("SD卡存在");
            if (!isSdExist()) {
                DialogUtils.showPrompt(this.context, getResources().getString(R.string.str_no_detect_sd));
            } else if (isSdCanWrite()) {
                log("SD卡可以写入");
                long freeSize = getSDFreeSize();
                File file = new File(DbUtils.getDb(this.context).getPath());
                if (!file.exists()) {
                    file = getDbFile();
                }
                if (file.exists()) {
                    long fileSize = (file.length() / PlaybackStateCompat.ACTION_PLAY_FROM_MEDIA_ID) / PlaybackStateCompat.ACTION_PLAY_FROM_MEDIA_ID;
                    log("freeSize" + freeSize + ",fileSize:" + fileSize);
                    if (freeSize > fileSize) {
                        try {
                            log("启动备份线程");
                            if (this.backupThread == null || !this.backupThread.isAlive()) {
                                this.backupThread = new Thread(new backupDbRunnble(this.context));
                                this.backupThread.start();
                                return;
                            }
                            GeneralHelper.toastShort(this.context, getString(R.string.str_backing_up));
                            return;
                        } catch (Exception e) {
                            DialogUtils.showPrompt(this.context, getResources().getString(R.string.str_back_up_fail));
                            DbUtils.exceptionHandler(e);
                            return;
                        }
                    }
                    DialogUtils.showPrompt(this.context, getResources().getString(R.string.str_space_no_enough));
                    return;
                }
                DialogUtils.showPrompt(this.context, getResources().getString(R.string.str_read_db_error));
            } else {
                DialogUtils.showPrompt(this.context, getResources().getString(R.string.str_sd_cannot_write));
            }
        } catch (NotFoundException e2) {
            e2.printStackTrace();
        }
    }

    public void backupData_withoutEncryted() {
        if (!isSdExist()) {
            GeneralHelper.toastShort(this.context, getResources().getString(R.string.str_no_detect_sd));
        } else if (isSdCanWrite()) {
            long freeSize = getSDFreeSize();
            File file = new File(DbUtils.getDb(this.context).getPath());
            if (!file.exists()) {
                file = getDbFile();
            }
            if (file.exists()) {
                long fileSize = (file.length() / PlaybackStateCompat.ACTION_PLAY_FROM_MEDIA_ID) / PlaybackStateCompat.ACTION_PLAY_FROM_MEDIA_ID;
                log("freeSize" + freeSize + ",fileSize:" + fileSize);
                if (getSDFreeSize() > fileSize) {
                    try {
                        this.tv_backup_backup_to_sd_path.setText(getResources().getString(R.string.str_back_up_path) + backupDbFileToLocal(file).getPath().replace(Environment.getExternalStorageDirectory().toString(), ""));
                        this.tv_backup_backup_to_sd_path.setVisibility(0);
                        GeneralHelper.toastShort(this.context, getResources().getString(R.string.str_back_up_success));
                        return;
                    } catch (Exception e) {
                        GeneralHelper.toastShort(this.context, getResources().getString(R.string.str_back_up_fail));
                        DbUtils.exceptionHandler(e);
                        return;
                    }
                }
                GeneralHelper.toastShort(this.context, getResources().getString(R.string.str_space_no_enough));
                return;
            }
            GeneralHelper.toastShort(this.context, getResources().getString(R.string.str_read_db_error));
        } else {
            GeneralHelper.toastShort(this.context, getResources().getString(R.string.str_sd_cannot_write));
        }
    }

    private File backupDbFileToLocal(File srcfile) throws Exception {
        File path = new File(Environment.getExternalStorageDirectory() + File.separator + this.SD_BACKUP_DIR);
        if (!path.exists()) {
            path.mkdirs();
        }
        File fileDst = new File(path.getPath(), Val.SD_BACKUP_NAME + DateTime.getTimeString2());
        FileChannel src = new FileInputStream(srcfile.getAbsolutePath()).getChannel();
        new FileOutputStream(fileDst.getAbsolutePath()).getChannel().transferFrom(src, 0, src.size());
        log("备份成功" + fileDst.getPath());
        File file = new File(path.getPath(), Val.SD_BACKUP_NAME);
        if (file.exists()) {
            file.delete();
            log("备份成功,删除这前备份文件：" + file.getPath());
        }
        fileDst.renameTo(file);
        log("备份成功,改名成功：" + file.getPath());
        return file;
    }

    private File backupDbFileToLocal_v2(File srcfile) throws Exception {
        String desFilePath = Environment.getExternalStorageDirectory() + File.separator + this.SD_BACKUP_DIR;
        String desFileName = Val.SD_BACKUP_NAME + DateTime.getTimeString2();
        StringUtils.encryptFile(srcfile.getPath(), Val.getPassword(), desFilePath, desFileName);
        log("备份成功" + desFilePath + desFileName);
        File file = new File(desFilePath, Val.SD_BACKUP_NAME);
        if (file.exists()) {
            file.delete();
            log("备份成功,删除这前备份文件：" + file.getPath());
        }
        File file2 = new File(desFilePath + File.separator + desFileName);
        if (file2.exists()) {
            file2.renameTo(file);
            log("备份成功,改名成功：" + file.getPath());
        } else {
            log("备份失败！！");
        }
        return file;
    }

    /* JADX WARNING: Removed duplicated region for block: B:32:0x00de  */
    /* JADX WARNING: Removed duplicated region for block: B:51:0x0167  */
    /* JADX WARNING: Removed duplicated region for block: B:35:0x0123  */
    /* JADX WARNING: Removed duplicated region for block: B:45:0x0159  */
    /* JADX WARNING: Removed duplicated region for block: B:47:0x015e  */
    /* JADX WARNING: Removed duplicated region for block: B:49:0x0163  */
    /* JADX WARNING: Removed duplicated region for block: B:25:0x00c2  */
    /* JADX WARNING: Removed duplicated region for block: B:27:0x00c7  */
    /* JADX WARNING: Removed duplicated region for block: B:29:0x00cc  */
    /* JADX WARNING: Removed duplicated region for block: B:32:0x00de  */
    /* JADX WARNING: Removed duplicated region for block: B:35:0x0123  */
    /* JADX WARNING: Removed duplicated region for block: B:51:0x0167  */
    /* JADX WARNING: Removed duplicated region for block: B:25:0x00c2  */
    /* JADX WARNING: Removed duplicated region for block: B:27:0x00c7  */
    /* JADX WARNING: Removed duplicated region for block: B:29:0x00cc  */
    /* JADX WARNING: Removed duplicated region for block: B:32:0x00de  */
    /* JADX WARNING: Removed duplicated region for block: B:51:0x0167  */
    /* JADX WARNING: Removed duplicated region for block: B:35:0x0123  */
    /* JADX WARNING: Removed duplicated region for block: B:45:0x0159  */
    /* JADX WARNING: Removed duplicated region for block: B:47:0x015e  */
    /* JADX WARNING: Removed duplicated region for block: B:49:0x0163  */
    /* JADX WARNING: Removed duplicated region for block: B:25:0x00c2  */
    /* JADX WARNING: Removed duplicated region for block: B:27:0x00c7  */
    /* JADX WARNING: Removed duplicated region for block: B:29:0x00cc  */
    /* JADX WARNING: Removed duplicated region for block: B:32:0x00de  */
    /* JADX WARNING: Removed duplicated region for block: B:35:0x0123  */
    /* JADX WARNING: Removed duplicated region for block: B:51:0x0167  */
    /* JADX WARNING: Removed duplicated region for block: B:45:0x0159  */
    /* JADX WARNING: Removed duplicated region for block: B:47:0x015e  */
    /* JADX WARNING: Removed duplicated region for block: B:49:0x0163  */
    private boolean backupDbFileToLocal_v3(java.io.File r21) throws java.lang.Exception {
        /*
        r20 = this;
        r13 = 0;
        r18 = new java.lang.StringBuilder;
        r18.<init>();
        r19 = android.os.Environment.getExternalStorageDirectory();
        r18 = r18.append(r19);
        r19 = java.io.File.separator;
        r18 = r18.append(r19);
        r0 = r20;
        r0 = r0.SD_BACKUP_DIR;
        r19 = r0;
        r18 = r18.append(r19);
        r7 = r18.toString();
        r18 = new java.lang.StringBuilder;
        r18.<init>();
        r19 = com.record.utils.Val.SD_BACKUP_NAME;
        r18 = r18.append(r19);
        r19 = com.record.utils.DateTime.getTimeString2();
        r18 = r18.append(r19);
        r6 = r18.toString();
        r11 = 0;
        r14 = 0;
        r4 = 0;
        r18 = "DES/ECB/NoPadding";
        r3 = javax.crypto.Cipher.getInstance(r18);	 Catch:{ Exception -> 0x017a }
        r18 = 1;
        r19 = com.record.utils.Val.getPassword();	 Catch:{ Exception -> 0x017a }
        r19 = r19.getBytes();	 Catch:{ Exception -> 0x017a }
        r19 = com.record.utils.MyCipher.toKey(r19);	 Catch:{ Exception -> 0x017a }
        r0 = r18;
        r1 = r19;
        r3.init(r0, r1);	 Catch:{ Exception -> 0x017a }
        r12 = new java.io.FileInputStream;	 Catch:{ Exception -> 0x017a }
        r0 = r21;
        r12.<init>(r0);	 Catch:{ Exception -> 0x017a }
        r16 = new java.io.File;	 Catch:{ Exception -> 0x017d, all -> 0x016e }
        r0 = r16;
        r0.<init>(r7);	 Catch:{ Exception -> 0x017d, all -> 0x016e }
        r18 = r16.exists();	 Catch:{ Exception -> 0x017d, all -> 0x016e }
        if (r18 != 0) goto L_0x006e;
    L_0x006b:
        r16.mkdirs();	 Catch:{ Exception -> 0x017d, all -> 0x016e }
    L_0x006e:
        r9 = new java.io.File;	 Catch:{ Exception -> 0x017d, all -> 0x016e }
        r18 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x017d, all -> 0x016e }
        r18.<init>();	 Catch:{ Exception -> 0x017d, all -> 0x016e }
        r0 = r18;
        r18 = r0.append(r7);	 Catch:{ Exception -> 0x017d, all -> 0x016e }
        r19 = java.io.File.separator;	 Catch:{ Exception -> 0x017d, all -> 0x016e }
        r18 = r18.append(r19);	 Catch:{ Exception -> 0x017d, all -> 0x016e }
        r0 = r18;
        r18 = r0.append(r6);	 Catch:{ Exception -> 0x017d, all -> 0x016e }
        r18 = r18.toString();	 Catch:{ Exception -> 0x017d, all -> 0x016e }
        r0 = r18;
        r9.<init>(r0);	 Catch:{ Exception -> 0x017d, all -> 0x016e }
        r18 = r9.exists();	 Catch:{ Exception -> 0x017d, all -> 0x016e }
        if (r18 != 0) goto L_0x0099;
    L_0x0096:
        r9.createNewFile();	 Catch:{ Exception -> 0x017d, all -> 0x016e }
    L_0x0099:
        r15 = new java.io.FileOutputStream;	 Catch:{ Exception -> 0x017d, all -> 0x016e }
        r15.<init>(r9);	 Catch:{ Exception -> 0x017d, all -> 0x016e }
        r5 = new javax.crypto.CipherInputStream;	 Catch:{ Exception -> 0x0181, all -> 0x0171 }
        r5.<init>(r12, r3);	 Catch:{ Exception -> 0x0181, all -> 0x0171 }
        r18 = 1024; // 0x400 float:1.435E-42 double:5.06E-321;
        r0 = r18;
        r2 = new byte[r0];	 Catch:{ Exception -> 0x00b9, all -> 0x0175 }
    L_0x00a9:
        r17 = r5.read(r2);	 Catch:{ Exception -> 0x00b9, all -> 0x0175 }
        if (r17 <= 0) goto L_0x0141;
    L_0x00af:
        r18 = 0;
        r0 = r18;
        r1 = r17;
        r15.write(r2, r0, r1);	 Catch:{ Exception -> 0x00b9, all -> 0x0175 }
        goto L_0x00a9;
    L_0x00b9:
        r8 = move-exception;
        r4 = r5;
        r14 = r15;
        r11 = r12;
    L_0x00bd:
        r8.printStackTrace();	 Catch:{ all -> 0x0156 }
        if (r4 == 0) goto L_0x00c5;
    L_0x00c2:
        r4.close();
    L_0x00c5:
        if (r11 == 0) goto L_0x00ca;
    L_0x00c7:
        r11.close();
    L_0x00ca:
        if (r14 == 0) goto L_0x00cf;
    L_0x00cc:
        r14.close();
    L_0x00cf:
        r9 = new java.io.File;
        r18 = com.record.utils.Val.SD_BACKUP_NAME;
        r0 = r18;
        r9.<init>(r7, r0);
        r18 = r9.exists();
        if (r18 == 0) goto L_0x00fb;
    L_0x00de:
        r9.delete();
        r18 = new java.lang.StringBuilder;
        r18.<init>();
        r19 = "备份成功,删除这前备份文件：";
        r18 = r18.append(r19);
        r19 = r9.getPath();
        r18 = r18.append(r19);
        r18 = r18.toString();
        log(r18);
    L_0x00fb:
        r10 = new java.io.File;
        r18 = new java.lang.StringBuilder;
        r18.<init>();
        r0 = r18;
        r18 = r0.append(r7);
        r19 = java.io.File.separator;
        r18 = r18.append(r19);
        r0 = r18;
        r18 = r0.append(r6);
        r18 = r18.toString();
        r0 = r18;
        r10.<init>(r0);
        r18 = r10.exists();
        if (r18 == 0) goto L_0x0167;
    L_0x0123:
        r10.renameTo(r9);
        r18 = new java.lang.StringBuilder;
        r18.<init>();
        r19 = "备份成功,改名成功：";
        r18 = r18.append(r19);
        r19 = r9.getPath();
        r18 = r18.append(r19);
        r18 = r18.toString();
        log(r18);
    L_0x0140:
        return r13;
    L_0x0141:
        r13 = 1;
        if (r5 == 0) goto L_0x0147;
    L_0x0144:
        r5.close();
    L_0x0147:
        if (r12 == 0) goto L_0x014c;
    L_0x0149:
        r12.close();
    L_0x014c:
        if (r15 == 0) goto L_0x0186;
    L_0x014e:
        r15.close();
        r4 = r5;
        r14 = r15;
        r11 = r12;
        goto L_0x00cf;
    L_0x0156:
        r18 = move-exception;
    L_0x0157:
        if (r4 == 0) goto L_0x015c;
    L_0x0159:
        r4.close();
    L_0x015c:
        if (r11 == 0) goto L_0x0161;
    L_0x015e:
        r11.close();
    L_0x0161:
        if (r14 == 0) goto L_0x0166;
    L_0x0163:
        r14.close();
    L_0x0166:
        throw r18;
    L_0x0167:
        r18 = "备份失败！！";
        log(r18);
        r13 = 0;
        goto L_0x0140;
    L_0x016e:
        r18 = move-exception;
        r11 = r12;
        goto L_0x0157;
    L_0x0171:
        r18 = move-exception;
        r14 = r15;
        r11 = r12;
        goto L_0x0157;
    L_0x0175:
        r18 = move-exception;
        r4 = r5;
        r14 = r15;
        r11 = r12;
        goto L_0x0157;
    L_0x017a:
        r8 = move-exception;
        goto L_0x00bd;
    L_0x017d:
        r8 = move-exception;
        r11 = r12;
        goto L_0x00bd;
    L_0x0181:
        r8 = move-exception;
        r14 = r15;
        r11 = r12;
        goto L_0x00bd;
    L_0x0186:
        r4 = r5;
        r14 = r15;
        r11 = r12;
        goto L_0x00cf;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.record.myLife.settings.BackupDbActivity_v2.backupDbFileToLocal_v3(java.io.File):boolean");
    }

    private void sendError(String str) {
        Message msg = new Message();
        msg.arg1 = this.HANDLER_ERROR;
        msg.obj = str;
        this.myHandler.sendMessage(msg);
    }

    private void sendToast(String str) {
        Message msg = new Message();
        msg.arg1 = this.HANDLER_TOAST;
        msg.obj = str;
        this.myHandler.sendMessage(msg);
    }

    private void sendDialog(String str) {
        Message msg = new Message();
        msg.arg1 = this.HANDLER_DIALOG;
        msg.obj = str;
        this.myHandler.sendMessage(msg);
    }

    private void sendPb() {
        Message msg = new Message();
        msg.arg1 = this.HANDLER_PB;
        this.myHandler.sendMessage(msg);
    }

    private void sendSuccess(int type) {
        Message msg = new Message();
        msg.arg1 = type;
        this.myHandler.sendMessage(msg);
    }

    private File getDbFile() {
        String packageName = "";
        try {
            PackageInfo info = getPackageManager().getPackageInfo(getPackageName(), 0);
            if (info.packageName != null) {
                packageName = info.packageName;
                log(packageName);
            }
        } catch (NameNotFoundException e) {
            packageName = this.packageName;
            DbUtils.exceptionHandler(e);
        }
        if (packageName == null || packageName.length() == 0) {
            packageName = this.packageName;
        }
        File dbFile = new File(Environment.getDataDirectory() + "/data/" + packageName + "/databases/" + this.DB_NAME);
        if (dbFile.exists()) {
            return dbFile;
        }
        return null;
    }

    public boolean isSdExist() {
        if (Environment.getExternalStorageState().equals("mounted")) {
            return true;
        }
        return false;
    }

    public boolean isSdCanWrite() {
        if (Environment.getExternalStorageDirectory().canWrite()) {
            return true;
        }
        return false;
    }

    public long getSDFreeSize() {
        StatFs sf = new StatFs(Environment.getExternalStorageDirectory().getPath());
        return ((((long) sf.getAvailableBlocks()) * ((long) sf.getBlockSize())) / PlaybackStateCompat.ACTION_PLAY_FROM_MEDIA_ID) / PlaybackStateCompat.ACTION_PLAY_FROM_MEDIA_ID;
    }

    public boolean doBeforExitActivity() {
        if (this.backupThread != null && this.backupThread.isAlive()) {
            GeneralUtils.toastShort(this.context, getString(R.string.str_backing_up));
            return false;
        } else if (this.importThread == null || !this.importThread.isAlive()) {
            finish();
            overridePendingTransition(R.anim.push_to_right_in, R.anim.push_to_right_out);
            return true;
        } else {
            GeneralUtils.toastShort(this.context, getString(R.string.str_importing_data));
            return false;
        }
    }

    protected void onDestroy() {
        super.onDestroy();
    }

    public void onBackPressed() {
        doBeforExitActivity();
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
        Log.i("override BackupDb", ":" + str);
    }
}
