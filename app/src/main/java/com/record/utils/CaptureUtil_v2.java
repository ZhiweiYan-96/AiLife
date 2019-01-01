package com.record.utils;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.v4.view.InputDeviceCompat;
import android.util.Log;
import android.view.View;
import com.record.myLife.R;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CaptureUtil_v2 {
    public static int CAPTURE_TYPE_INCLUDE_SCROLL = 2;
    public static int CAPTURE_TYPE_JUST_SCREEN = 3;
    public static int CAPTURE_TYPE_JUST_SCREEN_WITH_WATER_PIC = 4;
    private static Bitmap b;
    public int scrollViewId = 1;

    public static String shootWithWater(Activity activity, String path, String FileName) {
        File destDir = new File(path);
        if (!destDir.exists()) {
            destDir.mkdirs();
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssS");
        Date date = new Date();
        FileName = "mylife" + sdf.format(date) + ".jpg";
        Val.picFilePath = path + FileName;
        savePic(takeScreenShotWithWater(activity), path + FileName);
        return FileName;
    }

    private static Bitmap takeScreenShotWithWater(Activity activity) {
        View view = activity.getWindow().getDecorView().getRootView();
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache();
        Rect frame = new Rect();
        activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
        int ttop = activity.getWindow().findViewById(16908290).getTop();
        int top = frame.top;
        Bitmap b3 = Bitmap.createBitmap(view.getDrawingCache(), 0, 0, view.getWidth(), view.getHeight());
        view.destroyDrawingCache();
        Bitmap bitmap = b3;
        Bitmap outBitmap = null;
        if (bitmap != null) {
            outBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Config.ARGB_8888);
            Canvas ca = new Canvas(outBitmap);
            ca.drawBitmap(bitmap, 1.0f, 1.0f, null);
            Bitmap inBitmap = BitmapFactory.decodeResource(activity.getResources(), R.drawable.ic_logo24x24capture);
            if (inBitmap != null) {
                Log.i("Bitmap", "inBitmap有值！！");
                ca.drawBitmap(inBitmap, 2.0f, 5.0f, null);
                Paint textPaint = new Paint(InputDeviceCompat.SOURCE_KEYBOARD);
                textPaint.setColor(-1);
                textPaint.setTextSize((float) inBitmap.getHeight());
                ca.drawText("爱今天", (float) (inBitmap.getWidth() + 1), (float) inBitmap.getHeight(), textPaint);
//                ca.save(31);
                ca.save();
                ca.restore();
            } else {
                Log.i("Bitmap", "inBitmap为空！！");
            }
        }
        view.destroyDrawingCache();
        return outBitmap;
    }

    private static void savePic(Bitmap b, String strFileName) {
        FileNotFoundException e;
        File file;
        FileOutputStream fileOutputStream;
        IOException e2;
        File file2 = new File(strFileName);
        try {
            file2.createNewFile();
            log(strFileName);
            FileOutputStream fos = new FileOutputStream(file2);
            if (fos != null) {
                try {
                    b.compress(CompressFormat.PNG, 100, fos);
                    fos.flush();
                    fos.close();
                } catch (FileNotFoundException e3) {
                    e = e3;
                    file = file2;
                    fileOutputStream = fos;
                    e.printStackTrace();
                    System.out.println(e);
                } catch (IOException e4) {
                    e2 = e4;
                    file = file2;
                    fileOutputStream = fos;
                    e2.printStackTrace();
                }
            }
            file = file2;
            fileOutputStream = fos;
        } catch (FileNotFoundException e5) {
            e = e5;
            file = file2;
            e.printStackTrace();
            System.out.println(e);
        } catch (IOException e6) {
            e2 = e6;
            file = file2;
            e2.printStackTrace();
        }
    }

    public static List<String> GetFiles(String Path, String Extension) {
        List<String> lstFile = new ArrayList();
        File[] files = new File(Path).listFiles();
        for (File f : files) {
            if (f.isFile() && f.getPath().substring(f.getPath().length() - Extension.length()).equals(Extension)) {
                lstFile.add(f.getName());
            }
        }
        return lstFile;
    }

    public static void log(String str) {
        Log.i("override", "CaptureUtil_v2:" + str);
    }
}
