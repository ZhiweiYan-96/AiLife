package com.record.utils;

import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;

public class MyCipher {
    public static long cryptProgress = 0;
    public static long fileSize = 0;

    /* JADX WARNING: Removed duplicated region for block: B:22:0x007d A:{SYNTHETIC, Splitter: B:22:0x007d} */
    /* JADX WARNING: Removed duplicated region for block: B:25:0x0082 A:{SYNTHETIC, Splitter: B:25:0x0082} */
    /* JADX WARNING: Removed duplicated region for block: B:28:0x0087 A:{SYNTHETIC, Splitter: B:28:0x0087} */
    /* JADX WARNING: Removed duplicated region for block: B:22:0x007d A:{SYNTHETIC, Splitter: B:22:0x007d} */
    /* JADX WARNING: Removed duplicated region for block: B:25:0x0082 A:{SYNTHETIC, Splitter: B:25:0x0082} */
    /* JADX WARNING: Removed duplicated region for block: B:28:0x0087 A:{SYNTHETIC, Splitter: B:28:0x0087} */
    /* JADX WARNING: Removed duplicated region for block: B:56:0x00c3 A:{SYNTHETIC, Splitter: B:56:0x00c3} */
    /* JADX WARNING: Removed duplicated region for block: B:59:0x00c8 A:{SYNTHETIC, Splitter: B:59:0x00c8} */
    /* JADX WARNING: Removed duplicated region for block: B:62:0x00cd A:{SYNTHETIC, Splitter: B:62:0x00cd} */
    /* JADX WARNING: Removed duplicated region for block: B:22:0x007d A:{SYNTHETIC, Splitter: B:22:0x007d} */
    /* JADX WARNING: Removed duplicated region for block: B:25:0x0082 A:{SYNTHETIC, Splitter: B:25:0x0082} */
    /* JADX WARNING: Removed duplicated region for block: B:28:0x0087 A:{SYNTHETIC, Splitter: B:28:0x0087} */
    /* JADX WARNING: Removed duplicated region for block: B:56:0x00c3 A:{SYNTHETIC, Splitter: B:56:0x00c3} */
    /* JADX WARNING: Removed duplicated region for block: B:59:0x00c8 A:{SYNTHETIC, Splitter: B:59:0x00c8} */
    /* JADX WARNING: Removed duplicated region for block: B:62:0x00cd A:{SYNTHETIC, Splitter: B:62:0x00cd} */
    /* JADX WARNING: Removed duplicated region for block: B:56:0x00c3 A:{SYNTHETIC, Splitter: B:56:0x00c3} */
    /* JADX WARNING: Removed duplicated region for block: B:59:0x00c8 A:{SYNTHETIC, Splitter: B:59:0x00c8} */
    /* JADX WARNING: Removed duplicated region for block: B:62:0x00cd A:{SYNTHETIC, Splitter: B:62:0x00cd} */
    public static boolean deCryptByDes(java.io.File r18, java.lang.String r19, java.lang.String r20) {
        /*
        r14 = 0;
        cryptProgress = r14;
        r14 = 0;
        fileSize = r14;
        r10 = 0;
        r8 = 0;
        r11 = 0;
        r4 = 0;
        r14 = r18.length();	 Catch:{ Exception -> 0x00ec }
        fileSize = r14;	 Catch:{ Exception -> 0x00ec }
        r14 = "DES/ECB/NoPadding";
        r3 = javax.crypto.Cipher.getInstance(r14);	 Catch:{ Exception -> 0x00ec }
        r14 = 2;
        r15 = com.record.utils.Val.getPassword();	 Catch:{ Exception -> 0x00ec }
        r15 = r15.getBytes();	 Catch:{ Exception -> 0x00ec }
        r15 = toKey(r15);	 Catch:{ Exception -> 0x00ec }
        r3.init(r14, r15);	 Catch:{ Exception -> 0x00ec }
        r9 = new java.io.FileInputStream;	 Catch:{ Exception -> 0x00ec }
        r0 = r18;
        r9.<init>(r0);	 Catch:{ Exception -> 0x00ec }
        r7 = new java.io.File;	 Catch:{ Exception -> 0x00ee, all -> 0x00e0 }
        r14 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x00ee, all -> 0x00e0 }
        r14.<init>();	 Catch:{ Exception -> 0x00ee, all -> 0x00e0 }
        r0 = r19;
        r14 = r14.append(r0);	 Catch:{ Exception -> 0x00ee, all -> 0x00e0 }
        r0 = r20;
        r14 = r14.append(r0);	 Catch:{ Exception -> 0x00ee, all -> 0x00e0 }
        r14 = r14.toString();	 Catch:{ Exception -> 0x00ee, all -> 0x00e0 }
        r7.<init>(r14);	 Catch:{ Exception -> 0x00ee, all -> 0x00e0 }
        r14 = r7.exists();	 Catch:{ Exception -> 0x00ee, all -> 0x00e0 }
        if (r14 != 0) goto L_0x0052;
    L_0x004f:
        r7.createNewFile();	 Catch:{ Exception -> 0x00ee, all -> 0x00e0 }
    L_0x0052:
        r12 = new java.io.FileOutputStream;	 Catch:{ Exception -> 0x00ee, all -> 0x00e0 }
        r12.<init>(r7);	 Catch:{ Exception -> 0x00ee, all -> 0x00e0 }
        r5 = new javax.crypto.CipherInputStream;	 Catch:{ Exception -> 0x00f1, all -> 0x00e3 }
        r5.<init>(r9, r3);	 Catch:{ Exception -> 0x00f1, all -> 0x00e3 }
        r14 = 1024; // 0x400 float:1.435E-42 double:5.06E-321;
        r2 = new byte[r14];	 Catch:{ Exception -> 0x0074, all -> 0x00e7 }
    L_0x0060:
        r13 = r5.read(r2);	 Catch:{ Exception -> 0x0074, all -> 0x00e7 }
        if (r13 <= 0) goto L_0x008b;
    L_0x0066:
        r14 = 0;
        r12.write(r2, r14, r13);	 Catch:{ Exception -> 0x0074, all -> 0x00e7 }
        r14 = cryptProgress;	 Catch:{ Exception -> 0x0074, all -> 0x00e7 }
        r0 = (long) r13;	 Catch:{ Exception -> 0x0074, all -> 0x00e7 }
        r16 = r0;
        r14 = r14 + r16;
        cryptProgress = r14;	 Catch:{ Exception -> 0x0074, all -> 0x00e7 }
        goto L_0x0060;
    L_0x0074:
        r6 = move-exception;
        r4 = r5;
        r11 = r12;
        r8 = r9;
    L_0x0078:
        r6.printStackTrace();	 Catch:{ all -> 0x00c0 }
        if (r4 == 0) goto L_0x0080;
    L_0x007d:
        r4.close();	 Catch:{ IOException -> 0x00b1 }
    L_0x0080:
        if (r8 == 0) goto L_0x0085;
    L_0x0082:
        r8.close();	 Catch:{ IOException -> 0x00b6 }
    L_0x0085:
        if (r11 == 0) goto L_0x008a;
    L_0x0087:
        r11.close();	 Catch:{ IOException -> 0x00bb }
    L_0x008a:
        return r10;
    L_0x008b:
        r10 = 1;
        if (r5 == 0) goto L_0x0091;
    L_0x008e:
        r5.close();	 Catch:{ IOException -> 0x009f }
    L_0x0091:
        if (r9 == 0) goto L_0x0096;
    L_0x0093:
        r9.close();	 Catch:{ IOException -> 0x00a4 }
    L_0x0096:
        if (r12 == 0) goto L_0x009b;
    L_0x0098:
        r12.close();	 Catch:{ IOException -> 0x00a9 }
    L_0x009b:
        r4 = r5;
        r11 = r12;
        r8 = r9;
        goto L_0x008a;
    L_0x009f:
        r6 = move-exception;
        r6.printStackTrace();
        goto L_0x0091;
    L_0x00a4:
        r6 = move-exception;
        r6.printStackTrace();
        goto L_0x0096;
    L_0x00a9:
        r6 = move-exception;
        r6.printStackTrace();
        r4 = r5;
        r11 = r12;
        r8 = r9;
        goto L_0x008a;
    L_0x00b1:
        r6 = move-exception;
        r6.printStackTrace();
        goto L_0x0080;
    L_0x00b6:
        r6 = move-exception;
        r6.printStackTrace();
        goto L_0x0085;
    L_0x00bb:
        r6 = move-exception;
        r6.printStackTrace();
        goto L_0x008a;
    L_0x00c0:
        r14 = move-exception;
    L_0x00c1:
        if (r4 == 0) goto L_0x00c6;
    L_0x00c3:
        r4.close();	 Catch:{ IOException -> 0x00d1 }
    L_0x00c6:
        if (r8 == 0) goto L_0x00cb;
    L_0x00c8:
        r8.close();	 Catch:{ IOException -> 0x00d6 }
    L_0x00cb:
        if (r11 == 0) goto L_0x00d0;
    L_0x00cd:
        r11.close();	 Catch:{ IOException -> 0x00db }
    L_0x00d0:
        throw r14;
    L_0x00d1:
        r6 = move-exception;
        r6.printStackTrace();
        goto L_0x00c6;
    L_0x00d6:
        r6 = move-exception;
        r6.printStackTrace();
        goto L_0x00cb;
    L_0x00db:
        r6 = move-exception;
        r6.printStackTrace();
        goto L_0x00d0;
    L_0x00e0:
        r14 = move-exception;
        r8 = r9;
        goto L_0x00c1;
    L_0x00e3:
        r14 = move-exception;
        r11 = r12;
        r8 = r9;
        goto L_0x00c1;
    L_0x00e7:
        r14 = move-exception;
        r4 = r5;
        r11 = r12;
        r8 = r9;
        goto L_0x00c1;
    L_0x00ec:
        r6 = move-exception;
        goto L_0x0078;
    L_0x00ee:
        r6 = move-exception;
        r8 = r9;
        goto L_0x0078;
    L_0x00f1:
        r6 = move-exception;
        r11 = r12;
        r8 = r9;
        goto L_0x0078;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.record.utils.MyCipher.deCryptByDes(java.io.File, java.lang.String, java.lang.String):boolean");
    }

    /* JADX WARNING: Removed duplicated region for block: B:32:0x00f5  */
    /* JADX WARNING: Removed duplicated region for block: B:51:0x0149  */
    /* JADX WARNING: Removed duplicated region for block: B:35:0x0120  */
    /* JADX WARNING: Removed duplicated region for block: B:25:0x00db  */
    /* JADX WARNING: Removed duplicated region for block: B:27:0x00e0  */
    /* JADX WARNING: Removed duplicated region for block: B:29:0x00e5  */
    /* JADX WARNING: Removed duplicated region for block: B:32:0x00f5  */
    /* JADX WARNING: Removed duplicated region for block: B:35:0x0120  */
    /* JADX WARNING: Removed duplicated region for block: B:51:0x0149  */
    /* JADX WARNING: Removed duplicated region for block: B:45:0x013b  */
    /* JADX WARNING: Removed duplicated region for block: B:47:0x0140  */
    /* JADX WARNING: Removed duplicated region for block: B:49:0x0145  */
    /* JADX WARNING: Removed duplicated region for block: B:25:0x00db  */
    /* JADX WARNING: Removed duplicated region for block: B:27:0x00e0  */
    /* JADX WARNING: Removed duplicated region for block: B:29:0x00e5  */
    /* JADX WARNING: Removed duplicated region for block: B:32:0x00f5  */
    /* JADX WARNING: Removed duplicated region for block: B:51:0x0149  */
    /* JADX WARNING: Removed duplicated region for block: B:35:0x0120  */
    /* JADX WARNING: Removed duplicated region for block: B:45:0x013b  */
    /* JADX WARNING: Removed duplicated region for block: B:47:0x0140  */
    /* JADX WARNING: Removed duplicated region for block: B:49:0x0145  */
    /* JADX WARNING: Removed duplicated region for block: B:25:0x00db  */
    /* JADX WARNING: Removed duplicated region for block: B:27:0x00e0  */
    /* JADX WARNING: Removed duplicated region for block: B:29:0x00e5  */
    /* JADX WARNING: Removed duplicated region for block: B:32:0x00f5  */
    /* JADX WARNING: Removed duplicated region for block: B:35:0x0120  */
    /* JADX WARNING: Removed duplicated region for block: B:51:0x0149  */
    /* JADX WARNING: Removed duplicated region for block: B:45:0x013b  */
    /* JADX WARNING: Removed duplicated region for block: B:47:0x0140  */
    /* JADX WARNING: Removed duplicated region for block: B:49:0x0145  */
    public static boolean backupDbFileToLocal_v3(java.io.File r22, java.lang.String r23, java.lang.String r24) throws java.lang.Exception {
        /*
        r18 = 0;
        cryptProgress = r18;
        r18 = 0;
        fileSize = r18;
        r13 = 0;
        r18 = new java.lang.StringBuilder;
        r18.<init>();
        r19 = android.os.Environment.getExternalStorageDirectory();
        r18 = r18.append(r19);
        r19 = java.io.File.separator;
        r18 = r18.append(r19);
        r0 = r18;
        r1 = r23;
        r18 = r0.append(r1);
        r7 = r18.toString();
        r18 = new java.lang.StringBuilder;
        r18.<init>();
        r0 = r18;
        r1 = r24;
        r18 = r0.append(r1);
        r19 = com.record.utils.DateTime.getTimeString2();
        r18 = r18.append(r19);
        r6 = r18.toString();
        r11 = 0;
        r14 = 0;
        r4 = 0;
        r18 = r22.length();	 Catch:{ Exception -> 0x0157 }
        fileSize = r18;	 Catch:{ Exception -> 0x0157 }
        r18 = "DES/ECB/NoPadding";
        r3 = javax.crypto.Cipher.getInstance(r18);	 Catch:{ Exception -> 0x0157 }
        r18 = 1;
        r19 = com.record.utils.Val.getPassword();	 Catch:{ Exception -> 0x0157 }
        r19 = r19.getBytes();	 Catch:{ Exception -> 0x0157 }
        r19 = toKey(r19);	 Catch:{ Exception -> 0x0157 }
        r0 = r18;
        r1 = r19;
        r3.init(r0, r1);	 Catch:{ Exception -> 0x0157 }
        r12 = new java.io.FileInputStream;	 Catch:{ Exception -> 0x0157 }
        r0 = r22;
        r12.<init>(r0);	 Catch:{ Exception -> 0x0157 }
        r16 = new java.io.File;	 Catch:{ Exception -> 0x015a, all -> 0x014b }
        r0 = r16;
        r0.<init>(r7);	 Catch:{ Exception -> 0x015a, all -> 0x014b }
        r18 = r16.exists();	 Catch:{ Exception -> 0x015a, all -> 0x014b }
        if (r18 != 0) goto L_0x007c;
    L_0x0079:
        r16.mkdirs();	 Catch:{ Exception -> 0x015a, all -> 0x014b }
    L_0x007c:
        r9 = new java.io.File;	 Catch:{ Exception -> 0x015a, all -> 0x014b }
        r18 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x015a, all -> 0x014b }
        r18.<init>();	 Catch:{ Exception -> 0x015a, all -> 0x014b }
        r0 = r18;
        r18 = r0.append(r7);	 Catch:{ Exception -> 0x015a, all -> 0x014b }
        r19 = java.io.File.separator;	 Catch:{ Exception -> 0x015a, all -> 0x014b }
        r18 = r18.append(r19);	 Catch:{ Exception -> 0x015a, all -> 0x014b }
        r0 = r18;
        r18 = r0.append(r6);	 Catch:{ Exception -> 0x015a, all -> 0x014b }
        r18 = r18.toString();	 Catch:{ Exception -> 0x015a, all -> 0x014b }
        r0 = r18;
        r9.<init>(r0);	 Catch:{ Exception -> 0x015a, all -> 0x014b }
        r18 = r9.exists();	 Catch:{ Exception -> 0x015a, all -> 0x014b }
        if (r18 != 0) goto L_0x00a7;
    L_0x00a4:
        r9.createNewFile();	 Catch:{ Exception -> 0x015a, all -> 0x014b }
    L_0x00a7:
        r15 = new java.io.FileOutputStream;	 Catch:{ Exception -> 0x015a, all -> 0x014b }
        r15.<init>(r9);	 Catch:{ Exception -> 0x015a, all -> 0x014b }
        r5 = new javax.crypto.CipherInputStream;	 Catch:{ Exception -> 0x015e, all -> 0x014e }
        r5.<init>(r12, r3);	 Catch:{ Exception -> 0x015e, all -> 0x014e }
        r18 = 1024; // 0x400 float:1.435E-42 double:5.06E-321;
        r0 = r18;
        r2 = new byte[r0];	 Catch:{ Exception -> 0x00d2, all -> 0x0152 }
    L_0x00b7:
        r17 = r5.read(r2);	 Catch:{ Exception -> 0x00d2, all -> 0x0152 }
        if (r17 <= 0) goto L_0x0124;
    L_0x00bd:
        r18 = 0;
        r0 = r18;
        r1 = r17;
        r15.write(r2, r0, r1);	 Catch:{ Exception -> 0x00d2, all -> 0x0152 }
        r18 = cryptProgress;	 Catch:{ Exception -> 0x00d2, all -> 0x0152 }
        r0 = r17;
        r0 = (long) r0;	 Catch:{ Exception -> 0x00d2, all -> 0x0152 }
        r20 = r0;
        r18 = r18 + r20;
        cryptProgress = r18;	 Catch:{ Exception -> 0x00d2, all -> 0x0152 }
        goto L_0x00b7;
    L_0x00d2:
        r8 = move-exception;
        r4 = r5;
        r14 = r15;
        r11 = r12;
    L_0x00d6:
        r8.printStackTrace();	 Catch:{ all -> 0x0138 }
        if (r4 == 0) goto L_0x00de;
    L_0x00db:
        r4.close();
    L_0x00de:
        if (r11 == 0) goto L_0x00e3;
    L_0x00e0:
        r11.close();
    L_0x00e3:
        if (r14 == 0) goto L_0x00e8;
    L_0x00e5:
        r14.close();
    L_0x00e8:
        r9 = new java.io.File;
        r0 = r24;
        r9.<init>(r7, r0);
        r18 = r9.exists();
        if (r18 == 0) goto L_0x00f8;
    L_0x00f5:
        r9.delete();
    L_0x00f8:
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
        if (r18 == 0) goto L_0x0149;
    L_0x0120:
        r10.renameTo(r9);
    L_0x0123:
        return r13;
    L_0x0124:
        r13 = 1;
        if (r5 == 0) goto L_0x012a;
    L_0x0127:
        r5.close();
    L_0x012a:
        if (r12 == 0) goto L_0x012f;
    L_0x012c:
        r12.close();
    L_0x012f:
        if (r15 == 0) goto L_0x0163;
    L_0x0131:
        r15.close();
        r4 = r5;
        r14 = r15;
        r11 = r12;
        goto L_0x00e8;
    L_0x0138:
        r18 = move-exception;
    L_0x0139:
        if (r4 == 0) goto L_0x013e;
    L_0x013b:
        r4.close();
    L_0x013e:
        if (r11 == 0) goto L_0x0143;
    L_0x0140:
        r11.close();
    L_0x0143:
        if (r14 == 0) goto L_0x0148;
    L_0x0145:
        r14.close();
    L_0x0148:
        throw r18;
    L_0x0149:
        r13 = 0;
        goto L_0x0123;
    L_0x014b:
        r18 = move-exception;
        r11 = r12;
        goto L_0x0139;
    L_0x014e:
        r18 = move-exception;
        r14 = r15;
        r11 = r12;
        goto L_0x0139;
    L_0x0152:
        r18 = move-exception;
        r4 = r5;
        r14 = r15;
        r11 = r12;
        goto L_0x0139;
    L_0x0157:
        r8 = move-exception;
        goto L_0x00d6;
    L_0x015a:
        r8 = move-exception;
        r11 = r12;
        goto L_0x00d6;
    L_0x015e:
        r8 = move-exception;
        r14 = r15;
        r11 = r12;
        goto L_0x00d6;
    L_0x0163:
        r4 = r5;
        r14 = r15;
        r11 = r12;
        goto L_0x00e8;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.record.utils.MyCipher.backupDbFileToLocal_v3(java.io.File, java.lang.String, java.lang.String):boolean");
    }

    public static SecretKey toKey(byte[] key) throws Exception {
        return SecretKeyFactory.getInstance("DES").generateSecret(new DESKeySpec(key));
    }
}
