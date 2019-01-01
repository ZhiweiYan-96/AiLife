package com.record.bean;

import java.io.Serializable;

public class Version implements Serializable {
    private static final long serialVersionUID = 1;
    public String describe;
    public String downloadUrl;
    public String size;
    public String updateTime;
    public String versionCode;
    public String versionNum;

    public String getVersionCode() {
        return this.versionCode;
    }

    public void setVersionCode(String versionCode) {
        this.versionCode = versionCode;
    }

    public String getVersionNum() {
        return this.versionNum;
    }

    public void setVersionNum(String versionNum) {
        this.versionNum = versionNum;
    }

    public String getSize() {
        return this.size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getDownloadUrl() {
        return this.downloadUrl;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }

    public String getDescribe() {
        return this.describe;
    }

    public void setDescribe(String describe) {
        this.describe = describe;
    }

    public String getUpdateTime() {
        return this.updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public String toString() {
        return "Version [versionNum=" + this.versionNum + ", size=" + this.size + ", downloadUrl=" + this.downloadUrl + ", describe=" + this.describe + ", updateTime=" + this.updateTime + "]";
    }
}
