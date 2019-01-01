package com.record.view.floatingactionbutton.listener;

import android.animation.AnimatorSet;
import com.record.view.floatingactionbutton.RapidFloatingActionButton;
import com.record.view.floatingactionbutton.RapidFloatingActionContent;
import com.record.view.floatingactionbutton.RapidFloatingActionLayout;

public interface OnRapidFloatingActionListener {
    void collapseContent();

    void expandContent();

    RapidFloatingActionButton obtainRFAButton();

    RapidFloatingActionContent obtainRFAContent();

    RapidFloatingActionLayout obtainRFALayout();

    void onCollapseAnimator(AnimatorSet animatorSet);

    void onExpandAnimator(AnimatorSet animatorSet);

    void onRFABClick();

    void toggleContent();
}
