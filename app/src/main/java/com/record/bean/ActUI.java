package com.record.bean;

import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class ActUI {
    ImageView iv_temp_show_label;
    ImageView iv_temp_show_start;
    ImageView iv_temp_top_left_corner;
    RelativeLayout rl_temp_show_label_bg;
    RelativeLayout rl_temp_show_outer;
    RelativeLayout rl_temp_show_pb;
    TextView tv_temp_show_actName;
    TextView tv_temp_show_hours;
    TextView tv_temp_show_id;
    TextView tv_temp_show_remark;

    public ActUI(RelativeLayout rl_temp_show_outer, TextView tv_temp_show_id, RelativeLayout rl_temp_show_label_bg, ImageView iv_temp_show_label, ImageView iv_temp_show_start, TextView tv_temp_show_actName, TextView tv_temp_show_remark, ImageView iv_temp_top_left_corner, TextView tv_temp_show_hours) {
        this.rl_temp_show_outer = rl_temp_show_outer;
        this.tv_temp_show_id = tv_temp_show_id;
        this.rl_temp_show_label_bg = rl_temp_show_label_bg;
        this.iv_temp_show_label = iv_temp_show_label;
        this.iv_temp_show_start = iv_temp_show_start;
        this.tv_temp_show_actName = tv_temp_show_actName;
        this.tv_temp_show_remark = tv_temp_show_remark;
        this.iv_temp_top_left_corner = iv_temp_top_left_corner;
        this.tv_temp_show_hours = tv_temp_show_hours;
    }

    public ImageView getIv_temp_top_left_corner() {
        return this.iv_temp_top_left_corner;
    }

    public void setIv_temp_top_left_corner(ImageView iv_temp_top_left_corner) {
        this.iv_temp_top_left_corner = iv_temp_top_left_corner;
    }

    public TextView getTv_temp_show_hours() {
        return this.tv_temp_show_hours;
    }

    public void setTv_temp_show_hours(TextView tv_temp_show_hours) {
        this.tv_temp_show_hours = tv_temp_show_hours;
    }

    public TextView getTv_temp_show_id() {
        return this.tv_temp_show_id;
    }

    public void setTv_temp_show_id(TextView tv_temp_show_id) {
        this.tv_temp_show_id = tv_temp_show_id;
    }

    public RelativeLayout getRl_temp_show_label_bg() {
        return this.rl_temp_show_label_bg;
    }

    public void setRl_temp_show_label_bg(RelativeLayout rl_temp_show_label_bg) {
        this.rl_temp_show_label_bg = rl_temp_show_label_bg;
    }

    public ImageView getIv_temp_show_label() {
        return this.iv_temp_show_label;
    }

    public void setIv_temp_show_label(ImageView iv_temp_show_label) {
        this.iv_temp_show_label = iv_temp_show_label;
    }

    public ImageView getIv_temp_show_start() {
        return this.iv_temp_show_start;
    }

    public void setIv_temp_show_start(ImageView iv_temp_show_start) {
        this.iv_temp_show_start = iv_temp_show_start;
    }

    public TextView getTv_temp_show_actName() {
        return this.tv_temp_show_actName;
    }

    public void setTv_temp_show_actName(TextView tv_temp_show_actName) {
        this.tv_temp_show_actName = tv_temp_show_actName;
    }

    public TextView getTv_temp_show_remark() {
        return this.tv_temp_show_remark;
    }

    public void setTv_temp_show_remark(TextView tv_temp_show_remark) {
        this.tv_temp_show_remark = tv_temp_show_remark;
    }

    public RelativeLayout getRl_temp_show_outer() {
        return this.rl_temp_show_outer;
    }

    public void setRl_temp_show_outer(RelativeLayout rl_temp_show_outer) {
        this.rl_temp_show_outer = rl_temp_show_outer;
    }

    public RelativeLayout getRl_temp_show_pb() {
        return this.rl_temp_show_pb;
    }

    public void setRl_temp_show_pb(RelativeLayout rl_temp_show_pb) {
        this.rl_temp_show_pb = rl_temp_show_pb;
    }
}
