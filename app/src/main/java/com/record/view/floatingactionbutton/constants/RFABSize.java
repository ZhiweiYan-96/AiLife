package com.record.view.floatingactionbutton.constants;

public enum RFABSize {
    NORMAL(0, 56),
    MINI(1, 40);
    
    int code;
    int dpSize;

    private RFABSize(int code, int dpSize) {
        this.code = code;
        this.dpSize = dpSize;
    }

    public static RFABSize getRFABSizeByCode(int code) {
        for (RFABSize rfabSize : values()) {
            if (code == rfabSize.code) {
                return rfabSize;
            }
        }
        return NORMAL;
    }

    public int getCode() {
        return this.code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public int getDpSize() {
        return this.dpSize;
    }

    public void setDpSize(int dpSize) {
        this.dpSize = dpSize;
    }
}
