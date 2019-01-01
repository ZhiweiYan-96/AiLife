package com.record.thread;

import android.content.Context;
import android.util.Log;
import com.record.utils.db.DbBase;

public class BackupDataRunnable extends DbBase implements Runnable {
    static String TAG = "override";
    private Context context;

    public BackupDataRunnable(Context context) {
        this.context = context;
        TAG = "override " + getClass().getSimpleName();
    }

    /* JADX WARNING: Removed duplicated region for block: B:63:0x0138 A:{Catch:{ Exception -> 0x016d }} */
    /* JADX WARNING: Removed duplicated region for block: B:66:0x0163 A:{Catch:{ Exception -> 0x016d }} */
    /* JADX WARNING: Removed duplicated region for block: B:97:0x01b6 A:{SYNTHETIC, Splitter: B:97:0x01b6} */
    /* JADX WARNING: Removed duplicated region for block: B:100:0x01bb A:{SYNTHETIC, Splitter: B:100:0x01bb} */
    /* JADX WARNING: Removed duplicated region for block: B:103:0x01c0 A:{SYNTHETIC, Splitter: B:103:0x01c0} */
    /* JADX WARNING: Removed duplicated region for block: B:52:0x011c A:{SYNTHETIC, Splitter: B:52:0x011c} */
    /* JADX WARNING: Removed duplicated region for block: B:55:0x0121 A:{SYNTHETIC, Splitter: B:55:0x0121} */
    /* JADX WARNING: Removed duplicated region for block: B:58:0x0126 A:{SYNTHETIC, Splitter: B:58:0x0126} */
    /* JADX WARNING: Removed duplicated region for block: B:63:0x0138 A:{Catch:{ Exception -> 0x016d }} */
    /* JADX WARNING: Removed duplicated region for block: B:66:0x0163 A:{Catch:{ Exception -> 0x016d }} */
    /* JADX WARNING: Removed duplicated region for block: B:97:0x01b6 A:{SYNTHETIC, Splitter: B:97:0x01b6} */
    /* JADX WARNING: Removed duplicated region for block: B:100:0x01bb A:{SYNTHETIC, Splitter: B:100:0x01bb} */
    /* JADX WARNING: Removed duplicated region for block: B:103:0x01c0 A:{SYNTHETIC, Splitter: B:103:0x01c0} */
    /* JADX WARNING: Removed duplicated region for block: B:97:0x01b6 A:{SYNTHETIC, Splitter: B:97:0x01b6} */
    /* JADX WARNING: Removed duplicated region for block: B:100:0x01bb A:{SYNTHETIC, Splitter: B:100:0x01bb} */
    /* JADX WARNING: Removed duplicated region for block: B:103:0x01c0 A:{SYNTHETIC, Splitter: B:103:0x01c0} */
    /* JADX WARNING: Removed duplicated region for block: B:52:0x011c A:{SYNTHETIC, Splitter: B:52:0x011c} */
    /* JADX WARNING: Removed duplicated region for block: B:55:0x0121 A:{SYNTHETIC, Splitter: B:55:0x0121} */
    /* JADX WARNING: Removed duplicated region for block: B:58:0x0126 A:{SYNTHETIC, Splitter: B:58:0x0126} */
    /* JADX WARNING: Removed duplicated region for block: B:63:0x0138 A:{Catch:{ Exception -> 0x016d }} */
    /* JADX WARNING: Removed duplicated region for block: B:66:0x0163 A:{Catch:{ Exception -> 0x016d }} */
    /* JADX WARNING: Removed duplicated region for block: B:97:0x01b6 A:{SYNTHETIC, Splitter: B:97:0x01b6} */
    /* JADX WARNING: Removed duplicated region for block: B:100:0x01bb A:{SYNTHETIC, Splitter: B:100:0x01bb} */
    /* JADX WARNING: Removed duplicated region for block: B:103:0x01c0 A:{SYNTHETIC, Splitter: B:103:0x01c0} */
    /* JADX WARNING: Removed duplicated region for block: B:52:0x011c A:{SYNTHETIC, Splitter: B:52:0x011c} */
    /* JADX WARNING: Removed duplicated region for block: B:55:0x0121 A:{SYNTHETIC, Splitter: B:55:0x0121} */
    /* JADX WARNING: Removed duplicated region for block: B:58:0x0126 A:{SYNTHETIC, Splitter: B:58:0x0126} */
    /* JADX WARNING: Removed duplicated region for block: B:63:0x0138 A:{Catch:{ Exception -> 0x016d }} */
    /* JADX WARNING: Removed duplicated region for block: B:66:0x0163 A:{Catch:{ Exception -> 0x016d }} */
    public void run() {
        /*
        r22 = this;
        r7 = 0;
        r6 = 0;
        r17 = 0;
        r11 = 0;
        r13 = 0;
        r4 = 0;
        r19 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x01e9 }
        r19.<init>();	 Catch:{ Exception -> 0x01e9 }
        r20 = android.os.Environment.getExternalStorageDirectory();	 Catch:{ Exception -> 0x01e9 }
        r19 = r19.append(r20);	 Catch:{ Exception -> 0x01e9 }
        r20 = java.io.File.separator;	 Catch:{ Exception -> 0x01e9 }
        r19 = r19.append(r20);	 Catch:{ Exception -> 0x01e9 }
        r20 = com.record.utils.Val.SD_BACKUP_DIR;	 Catch:{ Exception -> 0x01e9 }
        r19 = r19.append(r20);	 Catch:{ Exception -> 0x01e9 }
        r7 = r19.toString();	 Catch:{ Exception -> 0x01e9 }
        r19 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x01e9 }
        r19.<init>();	 Catch:{ Exception -> 0x01e9 }
        r20 = com.record.utils.Val.SD_BACKUP_NAME;	 Catch:{ Exception -> 0x01e9 }
        r19 = r19.append(r20);	 Catch:{ Exception -> 0x01e9 }
        r20 = com.record.utils.DateTime.getTimeString2();	 Catch:{ Exception -> 0x01e9 }
        r19 = r19.append(r20);	 Catch:{ Exception -> 0x01e9 }
        r6 = r19.toString();	 Catch:{ Exception -> 0x01e9 }
        r18 = new java.io.File;	 Catch:{ Exception -> 0x01e9 }
        r0 = r22;
        r0 = r0.context;	 Catch:{ Exception -> 0x01e9 }
        r19 = r0;
        r19 = com.record.utils.db.DbUtils.getDb(r19);	 Catch:{ Exception -> 0x01e9 }
        r19 = r19.getPath();	 Catch:{ Exception -> 0x01e9 }
        r18.<init>(r19);	 Catch:{ Exception -> 0x01e9 }
        r19 = r18.exists();	 Catch:{ Exception -> 0x01ec, all -> 0x01d3 }
        if (r19 != 0) goto L_0x008c;
    L_0x0054:
        if (r4 == 0) goto L_0x0059;
    L_0x0056:
        r4.close();	 Catch:{ IOException -> 0x0066 }
    L_0x0059:
        if (r11 == 0) goto L_0x005e;
    L_0x005b:
        r11.close();	 Catch:{ IOException -> 0x0082 }
    L_0x005e:
        if (r13 == 0) goto L_0x0063;
    L_0x0060:
        r13.close();	 Catch:{ IOException -> 0x0087 }
    L_0x0063:
        r17 = r18;
    L_0x0065:
        return;
    L_0x0066:
        r8 = move-exception;
        r8.printStackTrace();	 Catch:{ Exception -> 0x006b }
        goto L_0x0059;
    L_0x006b:
        r8 = move-exception;
        r17 = r18;
    L_0x006e:
        com.record.utils.db.DbUtils.exceptionHandler(r8);
    L_0x0071:
        r0 = r22;
        r0 = r0.context;
        r19 = r0;
        r20 = new android.content.Intent;
        r21 = "INTENT_ACTION_AUTO_BACKUP_FINISH";
        r20.<init>(r21);
        r19.sendBroadcast(r20);
        goto L_0x0065;
    L_0x0082:
        r8 = move-exception;
        r8.printStackTrace();	 Catch:{ Exception -> 0x006b }
        goto L_0x005e;
    L_0x0087:
        r8 = move-exception;
        r8.printStackTrace();	 Catch:{ Exception -> 0x006b }
        goto L_0x0063;
    L_0x008c:
        r19 = "开始备份";
        log(r19);	 Catch:{ Exception -> 0x01ec, all -> 0x01d3 }
        r19 = "DES/ECB/NoPadding";
        r3 = javax.crypto.Cipher.getInstance(r19);	 Catch:{ Exception -> 0x01ec, all -> 0x01d3 }
        r19 = 1;
        r20 = com.record.utils.Val.getPassword();	 Catch:{ Exception -> 0x01ec, all -> 0x01d3 }
        r20 = r20.getBytes();	 Catch:{ Exception -> 0x01ec, all -> 0x01d3 }
        r20 = com.record.utils.MyCipher.toKey(r20);	 Catch:{ Exception -> 0x01ec, all -> 0x01d3 }
        r0 = r19;
        r1 = r20;
        r3.init(r0, r1);	 Catch:{ Exception -> 0x01ec, all -> 0x01d3 }
        r12 = new java.io.FileInputStream;	 Catch:{ Exception -> 0x01ec, all -> 0x01d3 }
        r0 = r18;
        r12.<init>(r0);	 Catch:{ Exception -> 0x01ec, all -> 0x01d3 }
        r15 = new java.io.File;	 Catch:{ Exception -> 0x01f1, all -> 0x01d7 }
        r15.<init>(r7);	 Catch:{ Exception -> 0x01f1, all -> 0x01d7 }
        r19 = r15.exists();	 Catch:{ Exception -> 0x01f1, all -> 0x01d7 }
        if (r19 != 0) goto L_0x00c1;
    L_0x00be:
        r15.mkdirs();	 Catch:{ Exception -> 0x01f1, all -> 0x01d7 }
    L_0x00c1:
        r9 = new java.io.File;	 Catch:{ Exception -> 0x01f1, all -> 0x01d7 }
        r19 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x01f1, all -> 0x01d7 }
        r19.<init>();	 Catch:{ Exception -> 0x01f1, all -> 0x01d7 }
        r0 = r19;
        r19 = r0.append(r7);	 Catch:{ Exception -> 0x01f1, all -> 0x01d7 }
        r20 = java.io.File.separator;	 Catch:{ Exception -> 0x01f1, all -> 0x01d7 }
        r19 = r19.append(r20);	 Catch:{ Exception -> 0x01f1, all -> 0x01d7 }
        r0 = r19;
        r19 = r0.append(r6);	 Catch:{ Exception -> 0x01f1, all -> 0x01d7 }
        r19 = r19.toString();	 Catch:{ Exception -> 0x01f1, all -> 0x01d7 }
        r0 = r19;
        r9.<init>(r0);	 Catch:{ Exception -> 0x01f1, all -> 0x01d7 }
        r19 = r9.exists();	 Catch:{ Exception -> 0x01f1, all -> 0x01d7 }
        if (r19 != 0) goto L_0x00ec;
    L_0x00e9:
        r9.createNewFile();	 Catch:{ Exception -> 0x01f1, all -> 0x01d7 }
    L_0x00ec:
        r14 = new java.io.FileOutputStream;	 Catch:{ Exception -> 0x01f1, all -> 0x01d7 }
        r14.<init>(r9);	 Catch:{ Exception -> 0x01f1, all -> 0x01d7 }
        r5 = new javax.crypto.CipherInputStream;	 Catch:{ Exception -> 0x01f7, all -> 0x01dc }
        r5.<init>(r12, r3);	 Catch:{ Exception -> 0x01f7, all -> 0x01dc }
        r19 = 10240; // 0x2800 float:1.4349E-41 double:5.059E-320;
        r0 = r19;
        r2 = new byte[r0];	 Catch:{ Exception -> 0x010c, all -> 0x01e2 }
    L_0x00fc:
        r16 = r5.read(r2);	 Catch:{ Exception -> 0x010c, all -> 0x01e2 }
        if (r16 <= 0) goto L_0x0170;
    L_0x0102:
        r19 = 0;
        r0 = r19;
        r1 = r16;
        r14.write(r2, r0, r1);	 Catch:{ Exception -> 0x010c, all -> 0x01e2 }
        goto L_0x00fc;
    L_0x010c:
        r8 = move-exception;
        r4 = r5;
        r13 = r14;
        r11 = r12;
        r17 = r18;
    L_0x0112:
        r8.printStackTrace();	 Catch:{ all -> 0x01b3 }
        r19 = "备份出错";
        log(r19);	 Catch:{ all -> 0x01b3 }
        if (r4 == 0) goto L_0x011f;
    L_0x011c:
        r4.close();	 Catch:{ IOException -> 0x01a1 }
    L_0x011f:
        if (r11 == 0) goto L_0x0124;
    L_0x0121:
        r11.close();	 Catch:{ IOException -> 0x01a7 }
    L_0x0124:
        if (r13 == 0) goto L_0x0129;
    L_0x0126:
        r13.close();	 Catch:{ IOException -> 0x01ad }
    L_0x0129:
        r9 = new java.io.File;	 Catch:{ Exception -> 0x016d }
        r19 = com.record.utils.Val.SD_BACKUP_NAME;	 Catch:{ Exception -> 0x016d }
        r0 = r19;
        r9.<init>(r7, r0);	 Catch:{ Exception -> 0x016d }
        r19 = r9.exists();	 Catch:{ Exception -> 0x016d }
        if (r19 == 0) goto L_0x013b;
    L_0x0138:
        r9.delete();	 Catch:{ Exception -> 0x016d }
    L_0x013b:
        r10 = new java.io.File;	 Catch:{ Exception -> 0x016d }
        r19 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x016d }
        r19.<init>();	 Catch:{ Exception -> 0x016d }
        r0 = r19;
        r19 = r0.append(r7);	 Catch:{ Exception -> 0x016d }
        r20 = java.io.File.separator;	 Catch:{ Exception -> 0x016d }
        r19 = r19.append(r20);	 Catch:{ Exception -> 0x016d }
        r0 = r19;
        r19 = r0.append(r6);	 Catch:{ Exception -> 0x016d }
        r19 = r19.toString();	 Catch:{ Exception -> 0x016d }
        r0 = r19;
        r10.<init>(r0);	 Catch:{ Exception -> 0x016d }
        r19 = r10.exists();	 Catch:{ Exception -> 0x016d }
        if (r19 == 0) goto L_0x0071;
    L_0x0163:
        r10.renameTo(r9);	 Catch:{ Exception -> 0x016d }
        r19 = "备份完成";
        log(r19);	 Catch:{ Exception -> 0x016d }
        goto L_0x0071;
    L_0x016d:
        r8 = move-exception;
        goto L_0x006e;
    L_0x0170:
        if (r5 == 0) goto L_0x0175;
    L_0x0172:
        r5.close();	 Catch:{ IOException -> 0x0185 }
    L_0x0175:
        if (r12 == 0) goto L_0x017a;
    L_0x0177:
        r12.close();	 Catch:{ IOException -> 0x0192 }
    L_0x017a:
        if (r14 == 0) goto L_0x017f;
    L_0x017c:
        r14.close();	 Catch:{ IOException -> 0x0197 }
    L_0x017f:
        r4 = r5;
        r13 = r14;
        r11 = r12;
        r17 = r18;
        goto L_0x0129;
    L_0x0185:
        r8 = move-exception;
        r8.printStackTrace();	 Catch:{ Exception -> 0x018a }
        goto L_0x0175;
    L_0x018a:
        r8 = move-exception;
        r4 = r5;
        r13 = r14;
        r11 = r12;
        r17 = r18;
        goto L_0x006e;
    L_0x0192:
        r8 = move-exception;
        r8.printStackTrace();	 Catch:{ Exception -> 0x018a }
        goto L_0x017a;
    L_0x0197:
        r8 = move-exception;
        r8.printStackTrace();	 Catch:{ Exception -> 0x018a }
        r4 = r5;
        r13 = r14;
        r11 = r12;
        r17 = r18;
        goto L_0x0129;
    L_0x01a1:
        r8 = move-exception;
        r8.printStackTrace();	 Catch:{ Exception -> 0x016d }
        goto L_0x011f;
    L_0x01a7:
        r8 = move-exception;
        r8.printStackTrace();	 Catch:{ Exception -> 0x016d }
        goto L_0x0124;
    L_0x01ad:
        r8 = move-exception;
        r8.printStackTrace();	 Catch:{ Exception -> 0x016d }
        goto L_0x0129;
    L_0x01b3:
        r19 = move-exception;
    L_0x01b4:
        if (r4 == 0) goto L_0x01b9;
    L_0x01b6:
        r4.close();	 Catch:{ IOException -> 0x01c4 }
    L_0x01b9:
        if (r11 == 0) goto L_0x01be;
    L_0x01bb:
        r11.close();	 Catch:{ IOException -> 0x01c9 }
    L_0x01be:
        if (r13 == 0) goto L_0x01c3;
    L_0x01c0:
        r13.close();	 Catch:{ IOException -> 0x01ce }
    L_0x01c3:
        throw r19;	 Catch:{ Exception -> 0x016d }
    L_0x01c4:
        r8 = move-exception;
        r8.printStackTrace();	 Catch:{ Exception -> 0x016d }
        goto L_0x01b9;
    L_0x01c9:
        r8 = move-exception;
        r8.printStackTrace();	 Catch:{ Exception -> 0x016d }
        goto L_0x01be;
    L_0x01ce:
        r8 = move-exception;
        r8.printStackTrace();	 Catch:{ Exception -> 0x016d }
        goto L_0x01c3;
    L_0x01d3:
        r19 = move-exception;
        r17 = r18;
        goto L_0x01b4;
    L_0x01d7:
        r19 = move-exception;
        r11 = r12;
        r17 = r18;
        goto L_0x01b4;
    L_0x01dc:
        r19 = move-exception;
        r13 = r14;
        r11 = r12;
        r17 = r18;
        goto L_0x01b4;
    L_0x01e2:
        r19 = move-exception;
        r4 = r5;
        r13 = r14;
        r11 = r12;
        r17 = r18;
        goto L_0x01b4;
    L_0x01e9:
        r8 = move-exception;
        goto L_0x0112;
    L_0x01ec:
        r8 = move-exception;
        r17 = r18;
        goto L_0x0112;
    L_0x01f1:
        r8 = move-exception;
        r11 = r12;
        r17 = r18;
        goto L_0x0112;
    L_0x01f7:
        r8 = move-exception;
        r13 = r14;
        r11 = r12;
        r17 = r18;
        goto L_0x0112;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.record.thread.BackupDataRunnable.run():void");
    }

    private static void log(String str) {
        Log.i(TAG, ":" + str);
    }
}
