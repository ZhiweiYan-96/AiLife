package com.record.utils;

import android.os.Environment;
import android.util.Log;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class FileUtils {
    private static String SDCardRoot = (Environment.getExternalStorageDirectory() + "/");

    public static String getSDPATH() {
        return SDCardRoot;
    }

    public static boolean isSDCardAvailable() {
        if ("mounted".equals(Environment.getExternalStorageState())) {
            return true;
        }
        return false;
    }

    public static void copyFiles(String path1, String path2) throws Exception {
        File file = new File(path1);
        if (file != null && file.exists() && file.isDirectory()) {
            if (new File(path2 + Val.SD_BACKUP_NAME).exists()) {
                Log.i("override FileUtils", "文件存在,不进行覆盖");
            } else {
                Log.i("override FileUtils", "itodayss/data_back文件不存在,开始转移到新目录！");
                File f = new File(path2);
                if (!f.exists()) {
                    f.mkdir();
                }
                for (File file2 : file.listFiles()) {
                    copyFiles(file2.toString(), path2 + "/" + file2.getName());
                }
            }
        }
        System.out.println("复制成功！");
    }

    public static void copy(String path1, String path2) throws IOException {
        DataInputStream in = new DataInputStream(new BufferedInputStream(new FileInputStream(path1)));
        if (in != null) {
            byte[] date = new byte[in.available()];
            in.read(date);
            DataOutputStream out = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(path2)));
            out.write(date);
            in.close();
            out.close();
        }
    }

    public static File creatSDFile(String fileName, String dir) throws IOException {
        File dirFile = new File(SDCardRoot + dir);
        if (!dirFile.exists()) {
            dirFile.mkdirs();
        }
        File file = new File(SDCardRoot + dir + File.separator + fileName);
        if (!file.exists()) {
            file.createNewFile();
        }
        return file;
    }

    public static File getSDFile(String fileName, String dir) throws IOException {
        return new File(SDCardRoot + dir + File.separator + fileName);
    }

    public static File creatSDDir(String dirName) {
        File dir = new File(SDCardRoot + dirName);
        dir.mkdir();
        return dir;
    }

    public static boolean isFileExist(String fileName) {
        return new File(SDCardRoot + fileName).exists();
    }

    public static boolean creatDirIfDirIsFileThenDelete(String filePath) {
        File path = new File(filePath);
        if (!path.exists()) {
            path.mkdirs();
        } else if (path.isFile()) {
            path.delete();
            path = new File(filePath);
            if (!path.exists()) {
                path.mkdirs();
            }
        }
        return path.exists();
    }

//    public static File write2SDFromInput(String dir, String fileName, InputStream input) {
//        IOException e2;
//        Throwable th;
//        File file = null;
//        OutputStream output = null;
//        try {
//            creatSDDir(dir);
//            file = creatSDFile(fileName, dir);
//            OutputStream output2 = new FileOutputStream(file);
//            try {
//                byte[] buffer = new byte[4096];
//                while (true) {
//                    int temp = input.read(buffer);
//                    if (temp == -1) {
//                        break;
//                    }
//                    output2.write(buffer, 0, temp);
//                }
//                output2.flush();
//                try {
//                    output2.close();
//                    output = output2;
//                } catch (IOException e2) {
//                    e2.printStackTrace();
//                    output = output2;
//                }
//            } catch (IOException e3) {
//                e2 = e3;
//                output = output2;
//                try {
//                    e2.printStackTrace();
//                    try {
//                        output.close();
//                    } catch (IOException e22) {
//                        e22.printStackTrace();
//                    }
//                    return file;
//                } catch (Throwable th2) {
//                    th = th2;
//                    try {
//                        output.close();
//                    } catch (IOException e222) {
//                        e222.printStackTrace();
//                    }
//                    throw th;
//                }
//            } catch (Throwable th3) {
//                th = th3;
//                output = output2;
//                output.close();
//                throw th;
//            }
//        } catch (IOException e4) {
//            e2 = e4;
//            e2.printStackTrace();
//            output.close();
//            return file;
//        }
//        return file;
//    }

    public static boolean delFile(String filePath) {
        File file = new File(SDCardRoot + filePath);
        if (file.exists()) {
            return file.delete();
        }
        return false;
    }

    public static boolean delFile(File file) {
        if (file.exists()) {
            return file.delete();
        }
        return false;
    }

    public static long getFileSizes(File f) throws Exception {
        if (f.exists()) {
            return (long) new FileInputStream(f).available();
        }
        return 0;
    }
}
