package com.record.utils;

import android.content.Intent;
import android.content.res.XmlResourceParser;
import android.net.Uri;
import android.util.Log;
import com.record.bean.Version;
import com.wangjie.androidbucket.services.network.http.HttpConstants;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

public class SoftwareUpdateHelper {
    private static final String DOWNLOAD_PATH = "mylife";
    private static final String UPDATE_INFO_URL = Val.versionUrl;
    private HttpDownloader downloader;
    private String softDownloadUrl;
    private String softName;

    public HttpDownloader getDownloader() {
        return this.downloader;
    }

    public SoftwareUpdateHelper() {
        this.softDownloadUrl = "";
        this.softName = "";
        this.downloader = null;
        this.downloader = new HttpDownloader();
    }

    public boolean isExistUpdate(XmlResourceParser localXml) {
        Version version = parse(downloadXml(UPDATE_INFO_URL));
        String localVersionNum = parseXml(localXml).getVersionNum();
        String newVersionNum = "";
        if (version != null) {
            newVersionNum = version.getVersionNum();
            this.softDownloadUrl = version.getDownloadUrl();
            this.softName = this.softDownloadUrl.substring(this.softDownloadUrl.lastIndexOf("/") + 1, this.softDownloadUrl.lastIndexOf(".")) + "." + this.softDownloadUrl.substring(this.softDownloadUrl.lastIndexOf(".") + 1, this.softDownloadUrl.length()).toLowerCase();
        }
        GeneralHelper.d("newVersionNum" + newVersionNum + "---localVersionNum" + localVersionNum);
        if ("" == newVersionNum || "".equals(newVersionNum) || localVersionNum.equals(newVersionNum)) {
            return false;
        }
        return true;
    }

    public boolean isAlreadyExist() throws IOException {
        HttpURLConnection urlConn = (HttpURLConnection) new URL(this.softDownloadUrl).openConnection();
        if (!FileUtils.isFileExist(DOWNLOAD_PATH + File.separator + this.softName)) {
            return false;
        }
        if (FileUtils.getSDFile(this.softName, DOWNLOAD_PATH).length() == ((long) urlConn.getContentLength())) {
            return true;
        }
        FileUtils.delFile(DOWNLOAD_PATH + File.separator + this.softName);
        return false;
    }

    public File getLocalFile() throws IOException {
        return FileUtils.getSDFile(this.softName, DOWNLOAD_PATH);
    }

    public void download() throws IOException {
        Log.i("override SoftwareUpdateHelper", "softDownloadUrl:" + this.softDownloadUrl + ",,DOWNLOAD_PATH" + DOWNLOAD_PATH + ",,softName" + this.softName);
        this.downloader.download(this.softDownloadUrl, DOWNLOAD_PATH, this.softName);
    }

    public void download(String softDownloadUrl, String DOWNLOAD_PATH, String softName) throws IOException {
        Log.i("override SoftwareUpdateHelper", "softDownloadUrl:" + softDownloadUrl + ",,DOWNLOAD_PATH" + DOWNLOAD_PATH + ",,softName" + softName);
        this.softDownloadUrl = softDownloadUrl;
        this.softName = softName;
        this.downloader.download(softDownloadUrl, DOWNLOAD_PATH, softName);
    }

    private Version parse(String vsersionXml) {
        SAXParserFactory spf = SAXParserFactory.newInstance();
        VersionContentHandler versionHandler = new VersionContentHandler();
        try {
            XMLReader xmlReader = spf.newSAXParser().getXMLReader();
            xmlReader.setContentHandler(versionHandler);
            xmlReader.parse(new InputSource(new StringReader(vsersionXml)));
            return versionHandler.getVersion();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e2) {
            e2.printStackTrace();
        } catch (IOException e3) {
            e3.printStackTrace();
        }
        return null;
    }

    public Version parseXml(XmlResourceParser xml) {
        Exception e;
        Version version_temp = null;
        String tag_temp = "";
        try {
            int eventType = xml.next();
            Version version_temp2 = null;
            while (true) {
                if (eventType == 0) {
                    try {
                        version_temp = new Version();
                    } catch (Exception e2) {
                        e = e2;
                        version_temp = version_temp2;
                    }
                } else if (eventType == 2) {
                    switch (xml.getDepth()) {
                        case 2:
                            tag_temp = xml.getName();
                            break;
                    }
                    version_temp = version_temp2;
                } else if (eventType == 3) {
                    version_temp = version_temp2;
                } else if (eventType == 4) {
                    if ("versionNum".equals(tag_temp)) {
                        version_temp2.setVersionNum(xml.getText());
                    } else if ("size".equals(tag_temp)) {
                        version_temp2.setSize(xml.getText());
                    } else if ("updateTime".equals(tag_temp)) {
                        version_temp2.setUpdateTime(xml.getText());
                    } else if ("describe".equals(tag_temp)) {
                        version_temp2.setDescribe(xml.getText());
                    }
                    version_temp2.setSize(xml.getText());
                    version_temp2.setUpdateTime(xml.getText());
                    version_temp2.setDescribe(xml.getText());
                    version_temp = version_temp2;
                } else if (eventType == 1) {
                    return version_temp2;
                } else {
                    version_temp = version_temp2;
                }
                eventType = xml.next();
                version_temp2 = version_temp;
            }
        } catch (Exception e3) {
            e = e3;
        }
        e.printStackTrace();
        return version_temp;
    }

    private String downloadXml(String urlStr) {
        return new HttpDownloader().download(urlStr);
    }

    public Intent openFile(File f) {
        Intent intent = new Intent();
        intent.addFlags(268435456);
        intent.setAction("android.intent.action.VIEW");
        System.out.println(getMIMEType(f) + "soft open type -----------------application/vnd.android.package-archive");
        intent.setDataAndType(Uri.fromFile(f), HttpConstants.CONTENT_TYPE_APK);
        return intent;
    }

    private String getMIMEType(File f) {
        String type = "";
        String fName = f.getName();
        String end = fName.substring(fName.lastIndexOf(".") + 1, fName.length()).toLowerCase();
        if (end.equals("m4a") || end.equals("mp3") || end.equals("mid") || end.equals("xmf") || end.equals("ogg") || end.equals("wav")) {
            type = "audio";
        } else if (end.equals("3gp") || end.equals("mp4")) {
            type = "video";
        } else if (end.equals("jpg") || end.equals("gif") || end.equals("png") || end.equals("jpeg") || end.equals("bmp")) {
            type = "image";
        } else if (end.equals("apk")) {
            type = HttpConstants.CONTENT_TYPE_APK;
        } else {
            type = "*";
        }
        if (end.equals("apk")) {
            return type;
        }
        return type + "/*";
    }

    public void deleteFile() throws Exception {
        System.out.println("FilePath : mylife" + File.separator + this.softName);
        File file = FileUtils.getSDFile(this.softName, DOWNLOAD_PATH);
        System.out.println("检查文件是否存在");
        if (file.exists()) {
            System.out.println("文件存在，删除文件");
            FileUtils.delFile(file);
        }
    }
}
