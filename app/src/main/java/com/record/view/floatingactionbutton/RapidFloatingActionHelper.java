package com.record.view.floatingactionbutton;

import android.animation.AnimatorSet;
import android.content.Context;
import com.record.view.floatingactionbutton.listener.OnRapidFloatingActionListener;

public final class RapidFloatingActionHelper implements OnRapidFloatingActionListener {
    private Context context;
    private RapidFloatingActionButton rfaBtn;
    private RapidFloatingActionContent rfaContent;
    private RapidFloatingActionLayout rfaLayout;

    public RapidFloatingActionHelper(Context context, RapidFloatingActionLayout rfaLayout, RapidFloatingActionButton rfaBtn, RapidFloatingActionContent rfaContent) {
        this.context = context;
        this.rfaLayout = rfaLayout;
        this.rfaBtn = rfaBtn;
        this.rfaContent = rfaContent;
    }

    public RapidFloatingActionHelper setRfaLayout(RapidFloatingActionLayout rfaLayout) {
        this.rfaLayout = rfaLayout;
        return this;
    }

    public RapidFloatingActionHelper setRfaButton(RapidFloatingActionButton rfaBtn) {
        this.rfaBtn = rfaBtn;
        return this;
    }

    public RapidFloatingActionHelper setRfaContent(RapidFloatingActionContent rfaContent) {
        this.rfaContent = rfaContent;
        return this;
    }

    public final RapidFloatingActionHelper build() {
        this.rfaLayout.setOnRapidFloatingActionListener(this);
        this.rfaBtn.setOnRapidFloatingActionListener(this);
        this.rfaContent.setOnRapidFloatingActionListener(this);
        this.rfaLayout.setContentView(this.rfaContent);
        this.rfaContent.initAfterRFABHelperBuild();
        return this;
    }

    public void onRFABClick() {
        this.rfaLayout.toggleContent();
    }

    public void toggleContent() {
        this.rfaLayout.toggleContent();
    }

    public void expandContent() {
        this.rfaLayout.expandContent();
    }

    public void collapseContent() {
        this.rfaLayout.collapseContent();
    }

    public final RapidFloatingActionLayout obtainRFALayout() {
        return this.rfaLayout;
    }

    public final RapidFloatingActionButton obtainRFAButton() {
        return this.rfaBtn;
    }

    public final RapidFloatingActionContent obtainRFAContent() {
        return this.rfaContent;
    }

    public void onExpandAnimator(AnimatorSet animatorSet) {
        this.rfaContent.onExpandAnimator(animatorSet);
        this.rfaBtn.onExpandAnimator(animatorSet);
    }

    public void onCollapseAnimator(AnimatorSet animatorSet) {
        this.rfaContent.onCollapseAnimator(animatorSet);
        this.rfaBtn.onCollapseAnimator(animatorSet);
    }
}
