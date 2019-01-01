package com.record.utils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpDownloader {
    private File resultFile = null;
    private long size = 0;
    private long sourceSize = 0;
    private URL url = null;

    public long getSize() {
        return this.size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public long getSourceSize() {
        return this.sourceSize;
    }

    public File getResultFile() {
        return this.resultFile;
    }

    public double getFileSize(String filePath) {
        double filesize;
        HttpURLConnection urlcon = null;
        try {
            urlcon = (HttpURLConnection) new URL(filePath).openConnection();
            System.out.println("inputStream:" + getInputStreamFromUrl(filePath).available());
            filesize = (double) urlcon.getContentLength();
        } catch (IOException e) {
            filesize = 0.0d;
            e.printStackTrace();
        } finally {
            urlcon.disconnect();
        }
        return filesize;
    }

    /* JADX WARNING: Removed duplicated region for block: B:35:0x0060 A:{SYNTHETIC, Splitter: B:35:0x0060} */
    /* JADX WARNING: Removed duplicated region for block: B:13:0x0034 A:{SYNTHETIC, Splitter: B:13:0x0034} */
    /* JADX WARNING: Removed duplicated region for block: B:29:0x0054 A:{SYNTHETIC, Splitter: B:29:0x0054} */
    public java.lang.String download(java.lang.String r12) {
        /*
        r11 = this;
        r6 = new java.lang.StringBuffer;
        r6.<init>();
        r4 = 0;
        r0 = 0;
        r5 = 0;
        r7 = new java.net.URL;	 Catch:{ MalformedURLException -> 0x006f, IOException -> 0x004e }
        r7.<init>(r12);	 Catch:{ MalformedURLException -> 0x006f, IOException -> 0x004e }
        r8 = r7.openConnection();	 Catch:{ MalformedURLException -> 0x006f, IOException -> 0x004e }
        r8 = (java.net.HttpURLConnection) r8;	 Catch:{ MalformedURLException -> 0x006f, IOException -> 0x004e }
        r3 = r8.getInputStream();	 Catch:{ MalformedURLException -> 0x006f, IOException -> 0x004e }
        r1 = new java.io.BufferedReader;	 Catch:{ MalformedURLException -> 0x006f, IOException -> 0x004e }
        r9 = new java.io.InputStreamReader;	 Catch:{ MalformedURLException -> 0x006f, IOException -> 0x004e }
        r10 = "utf-8";
        r9.<init>(r3, r10);	 Catch:{ MalformedURLException -> 0x006f, IOException -> 0x004e }
        r1.<init>(r9);	 Catch:{ MalformedURLException -> 0x006f, IOException -> 0x004e }
    L_0x0023:
        r4 = r1.readLine();	 Catch:{ MalformedURLException -> 0x002d, IOException -> 0x006c, all -> 0x0069 }
        if (r4 == 0) goto L_0x003c;
    L_0x0029:
        r6.append(r4);	 Catch:{ MalformedURLException -> 0x002d, IOException -> 0x006c, all -> 0x0069 }
        goto L_0x0023;
    L_0x002d:
        r2 = move-exception;
        r0 = r1;
    L_0x002f:
        r2.printStackTrace();	 Catch:{ all -> 0x005d }
        if (r0 == 0) goto L_0x0037;
    L_0x0034:
        r0.close();	 Catch:{ IOException -> 0x0049 }
    L_0x0037:
        r9 = r6.toString();
        return r9;
    L_0x003c:
        if (r1 == 0) goto L_0x0041;
    L_0x003e:
        r1.close();	 Catch:{ IOException -> 0x0043 }
    L_0x0041:
        r0 = r1;
        goto L_0x0037;
    L_0x0043:
        r2 = move-exception;
        r2.printStackTrace();
        r0 = r1;
        goto L_0x0037;
    L_0x0049:
        r2 = move-exception;
        r2.printStackTrace();
        goto L_0x0037;
    L_0x004e:
        r2 = move-exception;
    L_0x004f:
        r2.printStackTrace();	 Catch:{ all -> 0x005d }
        if (r0 == 0) goto L_0x0037;
    L_0x0054:
        r0.close();	 Catch:{ IOException -> 0x0058 }
        goto L_0x0037;
    L_0x0058:
        r2 = move-exception;
        r2.printStackTrace();
        goto L_0x0037;
    L_0x005d:
        r9 = move-exception;
    L_0x005e:
        if (r0 == 0) goto L_0x0063;
    L_0x0060:
        r0.close();	 Catch:{ IOException -> 0x0064 }
    L_0x0063:
        throw r9;
    L_0x0064:
        r2 = move-exception;
        r2.printStackTrace();
        goto L_0x0063;
    L_0x0069:
        r9 = move-exception;
        r0 = r1;
        goto L_0x005e;
    L_0x006c:
        r2 = move-exception;
        r0 = r1;
        goto L_0x004f;
    L_0x006f:
        r2 = move-exception;
        goto L_0x002f;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.record.utils.HttpDownloader.download(java.lang.String):java.lang.String");
    }

    /* JADX WARNING: Removed duplicated region for block: B:42:0x00d5 A:{SYNTHETIC, Splitter: B:42:0x00d5} */
    /* JADX WARNING: Removed duplicated region for block: B:45:0x00da A:{Catch:{ IOException -> 0x00de }} */
    /* JADX WARNING: Removed duplicated region for block: B:24:0x00af A:{SYNTHETIC, Splitter: B:24:0x00af} */
    /* JADX WARNING: Removed duplicated region for block: B:53:? A:{SYNTHETIC, RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:27:0x00b4 A:{Catch:{ IOException -> 0x00cd }} */
    public boolean download(java.lang.String r17, java.lang.String r18, java.lang.String r19) throws java.io.IOException {
        /*
        r16 = this;
        r11 = new java.lang.StringBuilder;
        r11.<init>();
        r0 = r17;
        r11 = r11.append(r0);
        r12 = "<--下载地址";
        r11 = r11.append(r12);
        r11 = r11.toString();
        com.record.utils.GeneralHelper.d(r11);
        r9 = new java.net.URL;
        r0 = r17;
        r9.<init>(r0);
        r10 = r9.openConnection();
        r10 = (java.net.HttpURLConnection) r10;
        r11 = r10.getResponseCode();
        r12 = 200; // 0xc8 float:2.8E-43 double:9.9E-322;
        if (r11 != r12) goto L_0x0036;
    L_0x002d:
        r11 = r10.getContentLength();
        r12 = (long) r11;
        r0 = r16;
        r0.sourceSize = r12;
    L_0x0036:
        r5 = r10.getInputStream();
        r11 = new java.lang.StringBuilder;
        r11.<init>();
        r0 = r18;
        r11 = r11.append(r0);
        r12 = java.io.File.separator;
        r11 = r11.append(r12);
        r0 = r19;
        r11 = r11.append(r0);
        r11 = r11.toString();
        r11 = com.record.utils.FileUtils.isFileExist(r11);
        if (r11 == 0) goto L_0x0079;
    L_0x005b:
        r11 = new java.lang.StringBuilder;
        r11.<init>();
        r0 = r18;
        r11 = r11.append(r0);
        r12 = java.io.File.separator;
        r11 = r11.append(r12);
        r0 = r19;
        r11 = r11.append(r0);
        r11 = r11.toString();
        com.record.utils.FileUtils.delFile(r11);
    L_0x0079:
        r4 = 0;
        r6 = 0;
        com.record.utils.FileUtils.creatSDDir(r18);	 Catch:{ Exception -> 0x00e6 }
        r0 = r19;
        r1 = r18;
        r4 = com.record.utils.FileUtils.creatSDFile(r0, r1);	 Catch:{ Exception -> 0x00e6 }
        r7 = new java.io.FileOutputStream;	 Catch:{ Exception -> 0x00e6 }
        r7.<init>(r4);	 Catch:{ Exception -> 0x00e6 }
        r11 = 1024; // 0x400 float:1.435E-42 double:5.06E-321;
        r2 = new byte[r11];	 Catch:{ Exception -> 0x00a7, all -> 0x00e3 }
    L_0x008f:
        r8 = r5.read(r2);	 Catch:{ Exception -> 0x00a7, all -> 0x00e3 }
        r11 = -1;
        if (r8 == r11) goto L_0x00b8;
    L_0x0096:
        if (r8 <= 0) goto L_0x00a2;
    L_0x0098:
        r0 = r16;
        r12 = r0.size;	 Catch:{ Exception -> 0x00a7, all -> 0x00e3 }
        r14 = (long) r8;	 Catch:{ Exception -> 0x00a7, all -> 0x00e3 }
        r12 = r12 + r14;
        r0 = r16;
        r0.size = r12;	 Catch:{ Exception -> 0x00a7, all -> 0x00e3 }
    L_0x00a2:
        r11 = 0;
        r7.write(r2, r11, r8);	 Catch:{ Exception -> 0x00a7, all -> 0x00e3 }
        goto L_0x008f;
    L_0x00a7:
        r3 = move-exception;
        r6 = r7;
    L_0x00a9:
        r3.printStackTrace();	 Catch:{ all -> 0x00d2 }
        r11 = 0;
        if (r6 == 0) goto L_0x00b2;
    L_0x00af:
        r6.close();	 Catch:{ IOException -> 0x00cd }
    L_0x00b2:
        if (r5 == 0) goto L_0x00b7;
    L_0x00b4:
        r5.close();	 Catch:{ IOException -> 0x00cd }
    L_0x00b7:
        return r11;
    L_0x00b8:
        r7.flush();	 Catch:{ Exception -> 0x00a7, all -> 0x00e3 }
        if (r7 == 0) goto L_0x00c0;
    L_0x00bd:
        r7.close();	 Catch:{ IOException -> 0x00c8 }
    L_0x00c0:
        if (r5 == 0) goto L_0x00c5;
    L_0x00c2:
        r5.close();	 Catch:{ IOException -> 0x00c8 }
    L_0x00c5:
        r11 = 1;
        r6 = r7;
        goto L_0x00b7;
    L_0x00c8:
        r3 = move-exception;
        r3.printStackTrace();
        goto L_0x00c5;
    L_0x00cd:
        r3 = move-exception;
        r3.printStackTrace();
        goto L_0x00b7;
    L_0x00d2:
        r11 = move-exception;
    L_0x00d3:
        if (r6 == 0) goto L_0x00d8;
    L_0x00d5:
        r6.close();	 Catch:{ IOException -> 0x00de }
    L_0x00d8:
        if (r5 == 0) goto L_0x00dd;
    L_0x00da:
        r5.close();	 Catch:{ IOException -> 0x00de }
    L_0x00dd:
        throw r11;
    L_0x00de:
        r3 = move-exception;
        r3.printStackTrace();
        goto L_0x00dd;
    L_0x00e3:
        r11 = move-exception;
        r6 = r7;
        goto L_0x00d3;
    L_0x00e6:
        r3 = move-exception;
        goto L_0x00a9;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.record.utils.HttpDownloader.download(java.lang.String, java.lang.String, java.lang.String):boolean");
    }

    /* JADX WARNING: Removed duplicated region for block: B:75:0x00b5  */
    /* JADX WARNING: Removed duplicated region for block: B:35:0x0071  */
    /* JADX WARNING: Removed duplicated region for block: B:63:0x00a5 A:{SYNTHETIC, Splitter: B:63:0x00a5} */
    /* JADX WARNING: Removed duplicated region for block: B:30:0x0068 A:{SYNTHETIC, Splitter: B:30:0x0068} */
    /* JADX WARNING: Removed duplicated region for block: B:35:0x0071  */
    /* JADX WARNING: Removed duplicated region for block: B:75:0x00b5  */
    public int downFile(java.lang.String r13, java.lang.String r14, java.lang.String r15) {
        /*
        r12 = this;
        r3 = 0;
        r7 = new java.lang.StringBuilder;	 Catch:{ IOException -> 0x0092 }
        r7.<init>();	 Catch:{ IOException -> 0x0092 }
        r7 = r7.append(r14);	 Catch:{ IOException -> 0x0092 }
        r8 = java.io.File.separator;	 Catch:{ IOException -> 0x0092 }
        r7 = r7.append(r8);	 Catch:{ IOException -> 0x0092 }
        r7 = r7.append(r15);	 Catch:{ IOException -> 0x0092 }
        r7 = r7.toString();	 Catch:{ IOException -> 0x0092 }
        r7 = com.record.utils.FileUtils.isFileExist(r7);	 Catch:{ IOException -> 0x0092 }
        if (r7 == 0) goto L_0x0030;
    L_0x001e:
        r7 = com.record.utils.FileUtils.creatSDFile(r15, r14);	 Catch:{ IOException -> 0x0092 }
        r12.resultFile = r7;	 Catch:{ IOException -> 0x0092 }
        r7 = 1;
        if (r3 == 0) goto L_0x002a;
    L_0x0027:
        r3.close();	 Catch:{ IOException -> 0x002b }
    L_0x002a:
        return r7;
    L_0x002b:
        r1 = move-exception;
        r1.printStackTrace();
        goto L_0x002a;
    L_0x0030:
        r3 = r12.getInputStreamFromUrl(r13);	 Catch:{ IOException -> 0x0092 }
        r2 = 0;
        r4 = 0;
        r7 = java.lang.System.out;	 Catch:{ IOException -> 0x00ca }
        r8 = "创建文件";
        r7.println(r8);	 Catch:{ IOException -> 0x00ca }
        com.record.utils.FileUtils.creatSDDir(r14);	 Catch:{ IOException -> 0x00ca }
        r2 = com.record.utils.FileUtils.creatSDFile(r15, r14);	 Catch:{ IOException -> 0x00ca }
        r5 = new java.io.FileOutputStream;	 Catch:{ IOException -> 0x00ca }
        r5.<init>(r2);	 Catch:{ IOException -> 0x00ca }
        r7 = 4096; // 0x1000 float:5.74E-42 double:2.0237E-320;
        r0 = new byte[r7];	 Catch:{ IOException -> 0x0061, all -> 0x00c7 }
    L_0x004d:
        r6 = r3.read(r0);	 Catch:{ IOException -> 0x0061, all -> 0x00c7 }
        r7 = -1;
        if (r6 == r7) goto L_0x007d;
    L_0x0054:
        if (r6 <= 0) goto L_0x005c;
    L_0x0056:
        r8 = r12.size;	 Catch:{ IOException -> 0x0061, all -> 0x00c7 }
        r10 = (long) r6;	 Catch:{ IOException -> 0x0061, all -> 0x00c7 }
        r8 = r8 + r10;
        r12.size = r8;	 Catch:{ IOException -> 0x0061, all -> 0x00c7 }
    L_0x005c:
        r7 = 0;
        r5.write(r0, r7, r6);	 Catch:{ IOException -> 0x0061, all -> 0x00c7 }
        goto L_0x004d;
    L_0x0061:
        r1 = move-exception;
        r4 = r5;
    L_0x0063:
        r1.printStackTrace();	 Catch:{ all -> 0x00a2 }
        if (r4 == 0) goto L_0x006b;
    L_0x0068:
        r4.close();	 Catch:{ IOException -> 0x008d }
    L_0x006b:
        r12.resultFile = r2;	 Catch:{ IOException -> 0x0092 }
        r7 = r12.resultFile;	 Catch:{ IOException -> 0x0092 }
        if (r7 != 0) goto L_0x00b5;
    L_0x0071:
        r7 = -1;
        if (r3 == 0) goto L_0x002a;
    L_0x0074:
        r3.close();	 Catch:{ IOException -> 0x0078 }
        goto L_0x002a;
    L_0x0078:
        r1 = move-exception;
        r1.printStackTrace();
        goto L_0x002a;
    L_0x007d:
        r5.flush();	 Catch:{ IOException -> 0x0061, all -> 0x00c7 }
        if (r5 == 0) goto L_0x0085;
    L_0x0082:
        r5.close();	 Catch:{ IOException -> 0x0087 }
    L_0x0085:
        r4 = r5;
        goto L_0x006b;
    L_0x0087:
        r1 = move-exception;
        r1.printStackTrace();	 Catch:{ IOException -> 0x0092 }
        r4 = r5;
        goto L_0x006b;
    L_0x008d:
        r1 = move-exception;
        r1.printStackTrace();	 Catch:{ IOException -> 0x0092 }
        goto L_0x006b;
    L_0x0092:
        r1 = move-exception;
        r1.printStackTrace();	 Catch:{ all -> 0x00a9 }
        r7 = -1;
        if (r3 == 0) goto L_0x002a;
    L_0x0099:
        r3.close();	 Catch:{ IOException -> 0x009d }
        goto L_0x002a;
    L_0x009d:
        r1 = move-exception;
        r1.printStackTrace();
        goto L_0x002a;
    L_0x00a2:
        r7 = move-exception;
    L_0x00a3:
        if (r4 == 0) goto L_0x00a8;
    L_0x00a5:
        r4.close();	 Catch:{ IOException -> 0x00b0 }
    L_0x00a8:
        throw r7;	 Catch:{ IOException -> 0x0092 }
    L_0x00a9:
        r7 = move-exception;
        if (r3 == 0) goto L_0x00af;
    L_0x00ac:
        r3.close();	 Catch:{ IOException -> 0x00c2 }
    L_0x00af:
        throw r7;
    L_0x00b0:
        r1 = move-exception;
        r1.printStackTrace();	 Catch:{ IOException -> 0x0092 }
        goto L_0x00a8;
    L_0x00b5:
        if (r3 == 0) goto L_0x00ba;
    L_0x00b7:
        r3.close();	 Catch:{ IOException -> 0x00bd }
    L_0x00ba:
        r7 = 0;
        goto L_0x002a;
    L_0x00bd:
        r1 = move-exception;
        r1.printStackTrace();
        goto L_0x00ba;
    L_0x00c2:
        r1 = move-exception;
        r1.printStackTrace();
        goto L_0x00af;
    L_0x00c7:
        r7 = move-exception;
        r4 = r5;
        goto L_0x00a3;
    L_0x00ca:
        r1 = move-exception;
        goto L_0x0063;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.record.utils.HttpDownloader.downFile(java.lang.String, java.lang.String, java.lang.String):int");
    }

    public InputStream getInputStreamFromUrl(String urlStr) throws IOException {
        this.url = new URL(urlStr);
        return ((HttpURLConnection) this.url.openConnection()).getInputStream();
    }
}
