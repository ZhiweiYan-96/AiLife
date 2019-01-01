package com.record.utils;

import com.sun.mail.imap.IMAPStore;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.SecureRandom;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;

public class StringUtils {
    private static final String DES = "DES";
    private static final String PASSWORD_CRYPT_KEY = "__jDlog_";

    public static boolean encryptFile(String srcFilePath, String password, String desFilePath, String desFileName) throws Exception {
        getFile(SimpleCrypto.encrypt(password, getBytes(srcFilePath)), desFilePath, desFileName);
        return false;
    }

    public static boolean decryptFile(String srcFilePath, String password, String desFilePath, String desFileName) throws Exception {
        getFile(SimpleCrypto.decrypt(password, getBytes(srcFilePath)), desFilePath, desFileName);
        return false;
    }

    public static byte[] getBytes(String filePath) {
        byte[] buffer = null;
        try {
            FileInputStream fis = new FileInputStream(new File(filePath));
            ByteArrayOutputStream bos = new ByteArrayOutputStream(IMAPStore.RESPONSE);
            byte[] buf = new byte[IMAPStore.RESPONSE];
            while (true) {
                int n = fis.read(buf);
                if (n != -1) {
                    bos.write(buf, 0, n);
                } else {
                    fis.close();
                    bos.close();
                    return bos.toByteArray();
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return buffer;
        } catch (IOException e2) {
            e2.printStackTrace();
            return buffer;
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:27:0x0060 A:{SYNTHETIC, Splitter: B:27:0x0060} */
    /* JADX WARNING: Removed duplicated region for block: B:63:? A:{SYNTHETIC, RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:30:0x0065 A:{SYNTHETIC, Splitter: B:30:0x0065} */
    /* JADX WARNING: Removed duplicated region for block: B:38:0x0076 A:{SYNTHETIC, Splitter: B:38:0x0076} */
    /* JADX WARNING: Removed duplicated region for block: B:41:0x007b A:{SYNTHETIC, Splitter: B:41:0x007b} */
    /* JADX WARNING: Removed duplicated region for block: B:27:0x0060 A:{SYNTHETIC, Splitter: B:27:0x0060} */
    /* JADX WARNING: Removed duplicated region for block: B:30:0x0065 A:{SYNTHETIC, Splitter: B:30:0x0065} */
    /* JADX WARNING: Removed duplicated region for block: B:63:? A:{SYNTHETIC, RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:38:0x0076 A:{SYNTHETIC, Splitter: B:38:0x0076} */
    /* JADX WARNING: Removed duplicated region for block: B:41:0x007b A:{SYNTHETIC, Splitter: B:41:0x007b} */
    /* JADX WARNING: Removed duplicated region for block: B:38:0x0076 A:{SYNTHETIC, Splitter: B:38:0x0076} */
    /* JADX WARNING: Removed duplicated region for block: B:41:0x007b A:{SYNTHETIC, Splitter: B:41:0x007b} */
    /* JADX WARNING: Removed duplicated region for block: B:27:0x0060 A:{SYNTHETIC, Splitter: B:27:0x0060} */
    /* JADX WARNING: Removed duplicated region for block: B:63:? A:{SYNTHETIC, RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:30:0x0065 A:{SYNTHETIC, Splitter: B:30:0x0065} */
    public static void getFile(byte[] r12, java.lang.String r13, java.lang.String r14) {
        /*
        r0 = 0;
        r8 = 0;
        r6 = 0;
        r3 = new java.io.File;	 Catch:{ Exception -> 0x005a }
        r3.<init>(r13);	 Catch:{ Exception -> 0x005a }
        r10 = r3.exists();	 Catch:{ Exception -> 0x005a }
        if (r10 != 0) goto L_0x0011;
    L_0x000e:
        r3.mkdirs();	 Catch:{ Exception -> 0x005a }
    L_0x0011:
        r10 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x005a }
        r10.<init>();	 Catch:{ Exception -> 0x005a }
        r10 = r10.append(r13);	 Catch:{ Exception -> 0x005a }
        r11 = java.io.File.separator;	 Catch:{ Exception -> 0x005a }
        r10 = r10.append(r11);	 Catch:{ Exception -> 0x005a }
        r10 = r10.append(r14);	 Catch:{ Exception -> 0x005a }
        r2 = r10.toString();	 Catch:{ Exception -> 0x005a }
        r10 = "override StringUtil";
        android.util.Log.i(r10, r2);	 Catch:{ Exception -> 0x005a }
        r7 = new java.io.File;	 Catch:{ Exception -> 0x005a }
        r7.<init>(r2);	 Catch:{ Exception -> 0x005a }
        r9 = new java.io.FileOutputStream;	 Catch:{ Exception -> 0x0095, all -> 0x0089 }
        r9.<init>(r7);	 Catch:{ Exception -> 0x0095, all -> 0x0089 }
        r1 = new java.io.BufferedOutputStream;	 Catch:{ Exception -> 0x0098, all -> 0x008c }
        r1.<init>(r9);	 Catch:{ Exception -> 0x0098, all -> 0x008c }
        r1.write(r12);	 Catch:{ Exception -> 0x009c, all -> 0x0090 }
        if (r1 == 0) goto L_0x0044;
    L_0x0041:
        r1.close();	 Catch:{ IOException -> 0x004d }
    L_0x0044:
        if (r9 == 0) goto L_0x00a1;
    L_0x0046:
        r9.close();	 Catch:{ IOException -> 0x0052 }
        r6 = r7;
        r8 = r9;
        r0 = r1;
    L_0x004c:
        return;
    L_0x004d:
        r5 = move-exception;
        r5.printStackTrace();
        goto L_0x0044;
    L_0x0052:
        r5 = move-exception;
        r5.printStackTrace();
        r6 = r7;
        r8 = r9;
        r0 = r1;
        goto L_0x004c;
    L_0x005a:
        r4 = move-exception;
    L_0x005b:
        r4.printStackTrace();	 Catch:{ all -> 0x0073 }
        if (r0 == 0) goto L_0x0063;
    L_0x0060:
        r0.close();	 Catch:{ IOException -> 0x006e }
    L_0x0063:
        if (r8 == 0) goto L_0x004c;
    L_0x0065:
        r8.close();	 Catch:{ IOException -> 0x0069 }
        goto L_0x004c;
    L_0x0069:
        r5 = move-exception;
        r5.printStackTrace();
        goto L_0x004c;
    L_0x006e:
        r5 = move-exception;
        r5.printStackTrace();
        goto L_0x0063;
    L_0x0073:
        r10 = move-exception;
    L_0x0074:
        if (r0 == 0) goto L_0x0079;
    L_0x0076:
        r0.close();	 Catch:{ IOException -> 0x007f }
    L_0x0079:
        if (r8 == 0) goto L_0x007e;
    L_0x007b:
        r8.close();	 Catch:{ IOException -> 0x0084 }
    L_0x007e:
        throw r10;
    L_0x007f:
        r5 = move-exception;
        r5.printStackTrace();
        goto L_0x0079;
    L_0x0084:
        r5 = move-exception;
        r5.printStackTrace();
        goto L_0x007e;
    L_0x0089:
        r10 = move-exception;
        r6 = r7;
        goto L_0x0074;
    L_0x008c:
        r10 = move-exception;
        r6 = r7;
        r8 = r9;
        goto L_0x0074;
    L_0x0090:
        r10 = move-exception;
        r6 = r7;
        r8 = r9;
        r0 = r1;
        goto L_0x0074;
    L_0x0095:
        r4 = move-exception;
        r6 = r7;
        goto L_0x005b;
    L_0x0098:
        r4 = move-exception;
        r6 = r7;
        r8 = r9;
        goto L_0x005b;
    L_0x009c:
        r4 = move-exception;
        r6 = r7;
        r8 = r9;
        r0 = r1;
        goto L_0x005b;
    L_0x00a1:
        r6 = r7;
        r8 = r9;
        r0 = r1;
        goto L_0x004c;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.record.utils.StringUtils.getFile(byte[], java.lang.String, java.lang.String):void");
    }

    public static byte[] encrypt(byte[] src, byte[] key) throws Exception {
        SecureRandom sr = SecureRandom.getInstance("SHA1PRNG", "Crypto");
        SecretKey securekey = SecretKeyFactory.getInstance(DES).generateSecret(new DESKeySpec(key));
        Cipher cipher = Cipher.getInstance("AES/ECB/ZeroBytePadding");
        cipher.init(1, securekey, sr);
        return cipher.doFinal(src);
    }

    public static byte[] decrypt(byte[] src, byte[] key) throws Exception {
        SecureRandom sr = SecureRandom.getInstance("SHA1PRNG", "Crypto");
        SecretKey securekey = SecretKeyFactory.getInstance(DES).generateSecret(new DESKeySpec(key));
        Cipher cipher = Cipher.getInstance("AES/ECB/ZeroBytePadding");
        cipher.init(2, securekey, sr);
        return cipher.doFinal(src);
    }

    public static final String decrypt(String data) {
        try {
            return new String(decrypt(hex2byte(data.getBytes()), PASSWORD_CRYPT_KEY.getBytes()));
        } catch (Exception e) {
            return null;
        }
    }

    public static final String encrypt(String password) {
        try {
            return byte2hex(encrypt(password.getBytes(), PASSWORD_CRYPT_KEY.getBytes()));
        } catch (Exception e) {
            return null;
        }
    }

    public static String byte2hex(byte[] b) {
        String hs = "";
        String stmp = "";
        for (byte b2 : b) {
            stmp = Integer.toHexString(b2 & 255);
            if (stmp.length() == 1) {
                hs = hs + "0" + stmp;
            } else {
                hs = hs + stmp;
            }
        }
        return hs.toUpperCase();
    }

    public static byte[] hex2byte(byte[] b) {
        if (b.length % 2 != 0) {
            throw new IllegalArgumentException("长度不是偶数");
        }
        byte[] b2 = new byte[(b.length / 2)];
        for (int n = 0; n < b.length; n += 2) {
            b2[n / 2] = (byte) Integer.parseInt(new String(b, n, 2), 16);
        }
        return b2;
    }
}
