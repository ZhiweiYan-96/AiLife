package com.record.bean;

import android.content.Context;
import android.content.Intent;

public interface IDemoChart {
    public static final String DESC = "desc";
    public static final String NAME = "name";

    Intent execute(Context context);

    String getDesc();

    String getName();
}
