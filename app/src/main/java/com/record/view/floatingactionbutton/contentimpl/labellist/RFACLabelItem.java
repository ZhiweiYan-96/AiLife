package com.record.view.floatingactionbutton.contentimpl.labellist;

import android.graphics.drawable.Drawable;
import java.io.Serializable;

public class RFACLabelItem<T> implements Serializable {
    private Drawable drawable;
    private Integer iconNormalColor;
    private Integer iconPressedColor;
    private String label;
    private Drawable labelBackgroundDrawable;
    private Integer labelColor;
    private Integer labelSizeSp;
    private boolean labelTextBold = true;
    private int resId = -1;
    private T wrapper;

    public RFACLabelItem() {

    }

    public RFACLabelItem(int resId, String label) {
        this.resId = resId;
        this.label = label;
    }

    public int getResId() {
        return this.resId;
    }

    public RFACLabelItem<T> setResId(int resId) {
        this.resId = resId;
        return this;
    }

    public String getLabel() {
        return this.label;
    }

    public RFACLabelItem<T> setLabel(String label) {
        this.label = label;
        return this;
    }

    public T getWrapper() {
        return this.wrapper;
    }

    public RFACLabelItem<T> setWrapper(T wrapper) {
        this.wrapper = wrapper;
        return this;
    }

    public Drawable getDrawable() {
        return this.drawable;
    }

    public RFACLabelItem<T> setDrawable(Drawable drawable) {
        this.drawable = drawable;
        return this;
    }

    public Integer getIconNormalColor() {
        return this.iconNormalColor;
    }

    public RFACLabelItem<T> setIconNormalColor(Integer iconNormalColor) {
        this.iconNormalColor = iconNormalColor;
        return this;
    }

    public Integer getIconPressedColor() {
        return this.iconPressedColor;
    }

    public RFACLabelItem<T> setIconPressedColor(Integer iconPressedColor) {
        this.iconPressedColor = iconPressedColor;
        return this;
    }

    public boolean isLabelTextBold() {
        return this.labelTextBold;
    }

    public RFACLabelItem<T> setLabelTextBold(boolean labelTextBold) {
        this.labelTextBold = labelTextBold;
        return this;
    }

    public Drawable getLabelBackgroundDrawable() {
        return this.labelBackgroundDrawable;
    }

    public RFACLabelItem<T> setLabelBackgroundDrawable(Drawable labelBackgroundDrawable) {
        this.labelBackgroundDrawable = labelBackgroundDrawable;
        return this;
    }

    public Integer getLabelColor() {
        return this.labelColor;
    }

    public RFACLabelItem<T> setLabelColor(Integer labelColor) {
        this.labelColor = labelColor;
        return this;
    }

    public Integer getLabelSizeSp() {
        return this.labelSizeSp;
    }

    public RFACLabelItem<T> setLabelSizeSp(Integer labelSizeSp) {
        this.labelSizeSp = labelSizeSp;
        return this;
    }
}
