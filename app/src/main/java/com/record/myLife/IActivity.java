package com.record.myLife;

import android.os.Message;

public interface IActivity {
    void init();

    void initView();

    void refresh(Message message);
}
