package com.record.utils;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.record.myLife.R;

public class LayoutUtils {
    Activity activity;
    Context context;
    LayoutInflater inflater;

    public LayoutUtils(Activity activity) {
        this.activity = activity;
        this.inflater = activity.getLayoutInflater();
    }

    public LinearLayout getLinear() {
        return new LinearLayout(this.context);
    }

    public RelativeLayout getRItems(String id, String name, String color, String label) {
        RelativeLayout rl = (RelativeLayout) this.inflater.inflate(R.layout.template_act, null);
        TextView tv_name = (TextView) rl.findViewById(R.id.tv_temp_act_name);
        ImageView iv_color = (ImageView) rl.findViewById(R.id.iv_temp_color);
        ImageView iv_label = (ImageView) rl.findViewById(R.id.iv_temp_label);
        ((TextView) rl.findViewById(R.id.tv_temp_act_id)).setText(id);
        tv_name.setText(name);
        iv_color.setBackgroundColor(this.activity.getResources().getColor(((Integer) Val.col_Str2Int_Map.get(color)).intValue()));
        iv_label.setImageResource(Val.getLabelIntByName(label));
        return rl;
    }
}
