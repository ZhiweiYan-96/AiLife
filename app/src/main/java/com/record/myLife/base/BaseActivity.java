package com.record.myLife.base;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import com.record.utils.db.DbUtils;

public abstract class BaseActivity extends Activity {
    Context context;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.context = this;
        DbUtils.getDb(this);
    }
}
