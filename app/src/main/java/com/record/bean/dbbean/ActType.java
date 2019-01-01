package com.record.bean.dbbean;

public class ActType {
    private String colorInt = "";
    private String colorStr = "";
    private int id = 0;
    private String image = "";
    private String imgurl = "";
    private int isDefault = 0;
    private int type = 0;
    private String typeDesc = "";
    private String typeName = "";
    private String userId = "";

    public ActType(int id, String userId, int type, String typeName, String typeDesc, String image, String imgurl, String colorStr, String colorInt, int isDefault) {
        this.id = id;
        this.userId = userId;
        this.type = type;
        this.typeName = typeName;
        this.typeDesc = typeDesc;
        this.image = image;
        this.imgurl = imgurl;
        this.colorStr = colorStr;
        this.colorInt = colorInt;
        this.isDefault = isDefault;
    }

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUserId() {
        return this.userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public int getType() {
        return this.type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getTypeName() {
        return this.typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public String getTypeDesc() {
        return this.typeDesc;
    }

    public void setTypeDesc(String typeDesc) {
        this.typeDesc = typeDesc;
    }

    public String getImage() {
        return this.image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getimgurl() {
        return this.imgurl;
    }

    public void setimgurl(String imgurl) {
        this.imgurl = imgurl;
    }

    public String getColorStr() {
        return this.colorStr;
    }

    public void setColorStr(String colorStr) {
        this.colorStr = colorStr;
    }

    public String getColorInt() {
        return this.colorInt;
    }

    public void setColorInt(String colorInt) {
        this.colorInt = colorInt;
    }

    public int getIsDefault() {
        return this.isDefault;
    }

    public void setIsDefault(int isDefault) {
        this.isDefault = isDefault;
    }
}
