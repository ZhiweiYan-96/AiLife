package com.record.utils;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Environment;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.Log;
import android.widget.Toast;
import com.record.bean.IDemoChart;
import com.record.utils.db.DbUtils;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;

public class GeneralHelper {
    public static final boolean D = true;
    private static final String DATABASE_PATH = (Environment.getExternalStorageDirectory().getAbsolutePath() + "/qcwp");
    public static final String TAG = "AutoTag";
    public static List<File> list = new ArrayList();

    public static String getExceptionString(Exception e) {
        String errorString = e.toString() + "\r\n";
        StackTraceElement[] stackArr = e.getStackTrace();
        for (int i = 0; i < stackArr.length; i++) {
            errorString = errorString + stackArr[i].toString();
            if (i + 1 != stackArr.length) {
                errorString = errorString + "\r\n";
            }
        }
        return errorString;
    }

    public static void i(String msg) {
        Log.i("AutoTag", msg);
    }

    public static void d(String msg) {
        Log.d("AutoTag", msg);
    }

    public static void e(String msg, Exception e) {
        Log.e("AutoTag", msg, e);
    }

    public static void w(String msg) {
        Log.w("AutoTag", msg);
    }

    public static void v(String msg) {
        Log.v("AutoTag", msg);
    }

    public static void toastShort(Context context, String msg) {
        Toast.makeText(context, msg, 0).show();
    }

    public static void toastShort(Context context, int rId) {
        Toast.makeText(context, rId, 0).show();
    }

    public static void toastLong(Context context, String msg) {
        Toast.makeText(context, msg, 1).show();
    }

    public static void toastLong(Context context, int rId) {
        Toast.makeText(context, rId, 1).show();
    }

    public static void closeService(String className, Context context) {
        ArrayList<RunningServiceInfo> runningService = (ArrayList) ((ActivityManager) context.getSystemService("activity")).getRunningServices(30);
        RunningServiceInfo runServiceInfo = null;
        for (int i = 0; i < runningService.size(); i++) {
            if (((RunningServiceInfo) runningService.get(i)).service.getClassName().toString().equals(className)) {
                runServiceInfo = (RunningServiceInfo) runningService.get(i);
                break;
            }
        }
        if (runServiceInfo != null) {
            ComponentName serviceCMP = runServiceInfo.service;
            Intent intent = new Intent();
            intent.setComponent(serviceCMP);
            context.stopService(intent);
            d(className + " --->>> close");
        }
    }

    public static boolean checkNetworkConnection(Context context) {
        if (isWiFiActive(context) || isNetworkAvailable(context)) {
            return false;
        }
        return true;
    }

    public static boolean checkNetworkConnectionAndToast(Context context) {
        boolean netSataus = checkNetworkConnection(context);
        if (netSataus) {
            toastShort(context, "无法连接到网络,请稍后再试!");
        }
        return netSataus;
    }

    public static boolean isWiFiActive(Context context) {
        WifiManager mWifiManager = (WifiManager) context.getSystemService(NetUtils.WIFI_NET);
        WifiInfo wifiInfo = mWifiManager.getConnectionInfo();
        int ipAddress = wifiInfo == null ? 0 : wifiInfo.getIpAddress();
        if (!mWifiManager.isWifiEnabled() || ipAddress == 0) {
            return false;
        }
        return true;
    }

    public static boolean isNumeric(String str) {
        int i = str.length();
        do {
            i--;
            if (i < 0) {
                return true;
            }
        } while (Character.isDigit(str.charAt(i)));
        return false;
    }

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService("connectivity");
        if (connectivity == null) {
            return false;
        }
        NetworkInfo info = connectivity.getActiveNetworkInfo();
        if (info == null || !info.isAvailable()) {
            return false;
        }
        return true;
    }

    public static String parseNetraffic(long dataSize) {
        double temp_val;
        if (dataSize > 1048576) {
            temp_val = ((double) dataSize) / 1048576.0d;
            return String.format("%.2f", new Object[]{Double.valueOf(temp_val)}) + "MB";
        } else if (dataSize <= PlaybackStateCompat.ACTION_PLAY_FROM_MEDIA_ID) {
            return dataSize + "B";
        } else {
            temp_val = ((double) dataSize) / 1024.0d;
            return String.format("%.2f", new Object[]{Double.valueOf(temp_val)}) + "KB";
        }
    }

    public static String getTableCreateString(Context context, String tableName) {
        String sql = "未能得到该表创建字段！";
        try {
            if (DbUtils.getDb(context) == null) {
                return "DbUtils.getDb(context)为空！";
            }
            Cursor cursor = DbUtils.getDb(context).query("sqlite_master", new String[]{"sql"}, "tbl_name=?", new String[]{tableName}, null, null, null);
            cursor.moveToFirst();
            sql = cursor.getString(0).toString();
            logI(sql);
            return sql;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public static void printBDTableByClumn(Context context, String tableName, String[] Columns) {
        try {
            int i;
            String columStr = "";
            for (i = 0; i < Columns.length; i++) {
                if (i != Columns.length - 1) {
                    columStr = columStr + Columns[i] + ",";
                } else {
                    columStr = columStr + Columns[i];
                }
            }
            if (DbUtils.getDb(context) == null) {
                logE("打印数据库表时,db为空！");
                return;
            }
            Cursor cursor = DbUtils.getDb(context).rawQuery("select " + columStr + " from " + tableName, null);
            String buf = "";
            if (cursor.getCount() > 0) {
                String[] arrStr = cursor.getColumnNames();
                for (String str : arrStr) {
                    buf = buf + str + " \t\t";
                }
                buf = buf + "\r";
                while (cursor.moveToNext()) {
                    for (String columnIndex : arrStr) {
                        buf = buf + cursor.getString(cursor.getColumnIndex(columnIndex)) + "\t";
                    }
                    buf = buf + "\r";
                }
                buf = buf + "\r";
            }
            Log.i("override GeneralHelper", "数据库信息如下：表名" + tableName + "\r" + buf);
        } catch (Exception e) {
            e.printStackTrace();
            logE(getExceptionString(e));
        }
    }

    public static void printBDTable(Context context, String tableName, boolean isExportTable, boolean isPrintTable) {
        try {
            if (DbUtils.getDb(context) == null) {
                logE("打印数据库表时,db为空！");
                return;
            }
            Cursor cursor = DbUtils.getDb(context).rawQuery("select * from " + tableName, null);
            String buf = "";
            if (cursor.getCount() > 0) {
                String[] arrStr = cursor.getColumnNames();
                for (String str : arrStr) {
                    buf = buf + str + " \t";
                }
                buf = buf + "\r";
                while (cursor.moveToNext()) {
                    for (String columnIndex : arrStr) {
                        buf = buf + cursor.getString(cursor.getColumnIndex(columnIndex)) + "\t";
                    }
                    buf = buf + "\r";
                }
                buf = buf + "\r";
            }
            if (isExportTable) {
                exportDatabase(tableName, new StringBuffer(buf));
            }
            if (isPrintTable) {
                Log.i("override", buf);
            }
        } catch (Exception e) {
            e.printStackTrace();
            logI(getExceptionString(e));
        }
    }

    public static String printCursorTable(Context context, Cursor cursor) {
        try {
            if (DbUtils.getDb(context) == null) {
                logE("打印数据库表时,db为空！");
                return "打印数据库表时,db为空！";
            }
            String buf = "";
            if (cursor.getCount() > 0) {
                String[] arrStr = cursor.getColumnNames();
                for (String str : arrStr) {
                    buf = buf + str + " \t";
                }
                buf = buf + "\r";
                while (cursor.moveToNext()) {
                    for (String columnIndex : arrStr) {
                        buf = buf + cursor.getString(cursor.getColumnIndex(columnIndex)) + "\t";
                    }
                    buf = buf + "\r";
                }
                buf = buf + "\r";
            }
            return buf.toString();
        } catch (Exception e) {
            e.printStackTrace();
            logI(getExceptionString(e));
            return "未查询到数据！";
        }
    }

    private static void exportDatabase(String tableName, StringBuffer tableBuffer) {
        String fileName = DateTime.getTimeString().replace("-", "").replace(":", "").replace(" ", "");
        try {
            File file = new File(DATABASE_PATH);
            File f = new File(file, tableName + fileName + ".txt");
            if (!file.exists()) {
                file.mkdir();
            }
            if (!f.exists()) {
                f.createNewFile();
            }
            FileOutputStream out = new FileOutputStream(f);
            ByteArrayInputStream in = new ByteArrayInputStream(tableBuffer.toString().getBytes("utf-8"));
            byte[] tempbuf = new byte[10240];
            while (true) {
                int count = in.read(tempbuf);
                if (count > 0) {
                    out.write(tempbuf, 0, count);
                } else {
                    out.flush();
                    out.close();
                    in.close();
                    return;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void exportAllDatabase(Context context) {
        Cursor cursor = DbUtils.getDb(context).rawQuery("select name from sqlite_master where type='table' order by name", null);
        if (cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                printBDTable(context, cursor.getString(cursor.getColumnIndex(IDemoChart.NAME)), true, false);
            }
        }
    }

    public static void exportThisDatabase() {
        try {
            File sd = Environment.getExternalStorageDirectory();
            File data = Environment.getDataDirectory();
            if (sd.canWrite()) {
                File currentDB = new File(data, "//data//com.auto.activity//databases");
                File backupDB = new File(sd, "qcwp/databases");
                if (currentDB.exists()) {
                    FileChannel src = new FileInputStream(currentDB).getChannel();
                    FileChannel dst = new FileOutputStream(backupDB).getChannel();
                    dst.transferFrom(src, 0, src.size());
                    src.close();
                    dst.close();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void exportDir(String packageName) {
        try {
            File data = Environment.getDataDirectory();
            File sd = Environment.getExternalStorageDirectory();
            if (sd.canWrite()) {
                File currentDB = new File(data + "/data/" + packageName);
                if (currentDB.exists()) {
                    curFile(currentDB);
                    if (list != null) {
                        logI("开始指定数据库文件导出...");
                        logI(list.toString());
                        FileChannel src = null;
                        FileChannel dst = null;
                        for (File f : list) {
                            String tmpFilePath = f.getAbsoluteFile().toString().replace(currentDB.toString(), "");
                            File path = new File(sd + File.separator + Val.SD_BACKUP_DIR + File.separator + packageName + tmpFilePath.substring(0, tmpFilePath.lastIndexOf("/")));
                            if (!path.exists()) {
                                path.mkdirs();
                            }
                            File file = new File(sd + File.separator + Val.SD_BACKUP_DIR + File.separator + packageName + tmpFilePath);
                            src = new FileInputStream(f.getAbsolutePath()).getChannel();
                            dst = new FileOutputStream(file.getAbsolutePath()).getChannel();
                            dst.transferFrom(src, 0, src.size());
                        }
                        if (src != null) {
                            src.close();
                        }
                        if (dst != null) {
                            dst.close();
                        }
                    }
                    logI("指定数据库文件导出成功！");
                    return;
                }
                logE("指定数据库失败，指定包名" + packageName + "不存在！");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void curFile(File file) {
        for (File f : file.listFiles()) {
            if (f.isDirectory()) {
                curFile(f);
            } else {
                list.add(f);
            }
        }
    }

    public static void logE(String str) {
        Log.e("override", "GeneralHelper类log：\r" + str);
    }

    public static void logI(String str) {
        Log.i("override GeneralHelper类log", ":" + str);
    }
}
