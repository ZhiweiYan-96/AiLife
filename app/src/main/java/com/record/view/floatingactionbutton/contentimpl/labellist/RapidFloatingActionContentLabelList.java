package com.record.view.floatingactionbutton.contentimpl.labellist;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.record.myLife.R;
import com.record.view.floatingactionbutton.RapidFloatingActionContent;
import com.record.view.floatingactionbutton.constants.RFABSize;
import com.record.view.floatingactionbutton.widget.CircleButtonDrawable;
import com.record.view.floatingactionbutton.widget.CircleButtonProperties;
import com.wangjie.androidbucket.utils.ABTextUtil;
import com.wangjie.androidbucket.utils.ABViewUtil;
import com.wangjie.androidbucket.utils.imageprocess.ABImageProcess;
import com.wangjie.androidbucket.utils.imageprocess.ABShape;
import java.util.Collection;
import java.util.List;

public class RapidFloatingActionContentLabelList extends RapidFloatingActionContent implements OnClickListener {
    private LinearLayout contentView;
    private int iconShadowColor;
    private int iconShadowDx;
    private int iconShadowDy;
    private int iconShadowRadius;
    private List<RFACLabelItem> items;
    private OvershootInterpolator mOvershootInterpolator = new OvershootInterpolator();
    private OnRapidFloatingActionContentLabelListListener onRapidFloatingActionContentLabelListListener;
    private int rfacItemDrawableSizePx;

    public interface OnRapidFloatingActionContentLabelListListener<T> {
        void onRFACItemIconClick(int i, RFACLabelItem<T> rFACLabelItem);

        void onRFACItemLabelClick(int i, RFACLabelItem<T> rFACLabelItem);
    }

    public void setOnRapidFloatingActionContentLabelListListener(OnRapidFloatingActionContentLabelListListener onRapidFloatingActionContentLabelListListener) {
        this.onRapidFloatingActionContentLabelListListener = onRapidFloatingActionContentLabelListListener;
    }

    public RapidFloatingActionContentLabelList(Context context) {
        super(context);
    }

    public RapidFloatingActionContentLabelList(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public RapidFloatingActionContentLabelList(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public RapidFloatingActionContentLabelList(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    protected void initInConstructor() {
        this.rfacItemDrawableSizePx = ABTextUtil.dip2px(getContext(), 24.0f);
        this.contentView = new LinearLayout(getContext());
        this.contentView.setLayoutParams(new LayoutParams(-1, -1));
        this.contentView.setOrientation(LinearLayout.VERTICAL);
        setRootView(this.contentView);
    }

    protected void initAfterRFABHelperBuild() {
        refreshItems();
    }

    public List<RFACLabelItem> getItems() {
        return this.items;
    }

    public RapidFloatingActionContentLabelList setItems(List<RFACLabelItem> items) {
        if (!ABTextUtil.isEmpty((Collection) items)) {
            this.items = items;
        }
        return this;
    }

    private void refreshItems() {
        if (ABTextUtil.isEmpty((Collection) this.items)) {
            throw new RuntimeException(getClass().getSimpleName() + "[items] can not be empty!");
        }
        this.contentView.removeAllViews();
        int size = this.items.size();
        for (int i = 0; i < size; i++) {
            RFACLabelItem item = (RFACLabelItem) this.items.get(i);
            View itemView = LayoutInflater.from(getContext()).inflate(R.layout.rfab__content_label_list_item, null);
            View rootView = ABViewUtil.obtainView(itemView, R.id.rfab__content_label_list_root_view);
            TextView labelTv = (TextView) ABViewUtil.obtainView(itemView, R.id.rfab__content_label_list_label_tv);
            ImageView iconIv = (ImageView) ABViewUtil.obtainView(itemView, R.id.rfab__content_label_list_icon_iv);
            rootView.setOnClickListener(this);
            labelTv.setOnClickListener(this);
            iconIv.setOnClickListener(this);
//            rootView.setTag(R.id.rfab__id_content_label_list_item_position, Integer.valueOf(i));
//            labelTv.setTag(R.id.rfab__id_content_label_list_item_position, Integer.valueOf(i));
//            iconIv.setTag(R.id.rfab__id_content_label_list_item_position, Integer.valueOf(i));
            CircleButtonProperties circleButtonProperties = new CircleButtonProperties().setStandardSize(RFABSize.MINI).setShadowColor(this.iconShadowColor).setShadowRadius(this.iconShadowRadius).setShadowDx(this.iconShadowDx).setShadowDy(this.iconShadowDy);
            int shadowOffsetHalf = circleButtonProperties.getShadowOffsetHalf();
            int minPadding = ABTextUtil.dip2px(getContext(), 8.0f);
            if (shadowOffsetHalf < minPadding) {
                int deltaPadding = minPadding - shadowOffsetHalf;
                rootView.setPadding(0, deltaPadding, 0, deltaPadding);
            }
            int realItemSize = circleButtonProperties.getRealSizePx(getContext());
            LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) iconIv.getLayoutParams();
            if (lp == null) {
                ViewGroup.LayoutParams layoutParams = new LinearLayout.LayoutParams(-2, -2);
            }
            lp.rightMargin = (this.onRapidFloatingActionListener.obtainRFAButton().getRfabProperties().getRealSizePx(getContext()) - realItemSize) / 2;
            lp.width = realItemSize;
            lp.height = realItemSize;
            iconIv.setLayoutParams(lp);
            Integer normalColor = item.getIconNormalColor();
            Integer pressedColor = item.getIconPressedColor();
            CircleButtonDrawable circleButtonDrawable = new CircleButtonDrawable(getContext(), circleButtonProperties, normalColor == null ? getResources().getColor(R.color.rfab__color_background_normal) : normalColor.intValue());
            circleButtonDrawable = new CircleButtonDrawable(getContext(), circleButtonProperties, pressedColor == null ? getResources().getColor(R.color.rfab__color_background_pressed) : pressedColor.intValue());
            if (VERSION.SDK_INT > 11) {
                iconIv.setLayerType(1, circleButtonDrawable.getPaint());
            }
            ABViewUtil.setBackgroundDrawable(iconIv, ABShape.selectorClickSimple(circleButtonDrawable, circleButtonDrawable));
            int padding = ABTextUtil.dip2px(getContext(), 8.0f) + shadowOffsetHalf;
            iconIv.setPadding(padding, padding, padding, padding);
            CharSequence label = item.getLabel();
            if (ABTextUtil.isEmpty(label)) {
                labelTv.setVisibility(GONE);
            } else {
                if (item.isLabelTextBold()) {
                    labelTv.getPaint().setFakeBoldText(true);
                }
                labelTv.setVisibility(VISIBLE);
                labelTv.setText(label);
                Drawable bgDrawable = item.getLabelBackgroundDrawable();
                if (bgDrawable != null) {
                    ABViewUtil.setBackgroundDrawable(labelTv, bgDrawable);
                }
                Integer labelColor = item.getLabelColor();
                if (labelColor != null) {
                    labelTv.setTextColor(labelColor.intValue());
                }
                Integer labelSize = item.getLabelSizeSp();
                if (labelSize != null) {
                    labelTv.setTextSize(2, (float) labelSize.intValue());
                }
            }
            Drawable drawable = item.getDrawable();
            if (drawable != null) {
                iconIv.setVisibility(VISIBLE);
                drawable.setBounds(0, 0, this.rfacItemDrawableSizePx, this.rfacItemDrawableSizePx);
                iconIv.setImageDrawable(drawable);
            } else {
                int resId = item.getResId();
                if (resId > 0) {
                    iconIv.setVisibility(VISIBLE);
                    iconIv.setImageDrawable(ABImageProcess.getResourceDrawableBounded(getContext(), resId, this.rfacItemDrawableSizePx));
                } else {
                    iconIv.setVisibility(VISIBLE);
                }
            }
            this.contentView.addView(itemView);
        }
    }

    protected void initialContentViews(View rootView) {
    }

    public void onClick(View v) {
        if (this.onRapidFloatingActionContentLabelListListener != null) {
//            Integer position = (Integer) v.getTag(R.id.rfab__id_content_label_list_item_position);
//            if (position != null) {
//                int i = v.getId();
//                if (i == R.id.rfab__content_label_list_label_tv) {
//                    this.onRapidFloatingActionContentLabelListListener.onRFACItemLabelClick(position.intValue(), (RFACLabelItem) this.items.get(position.intValue()));
//                } else if (i == R.id.rfab__content_label_list_icon_iv) {
//                    this.onRapidFloatingActionContentLabelListListener.onRFACItemIconClick(position.intValue(), (RFACLabelItem) this.items.get(position.intValue()));
//                } else if (i == R.id.rfab__content_label_list_root_view) {
//                    this.onRapidFloatingActionListener.collapseContent();
//                }
//            }
        }
    }

    public RapidFloatingActionContentLabelList setIconShadowRadius(int iconShadowRadius) {
        this.iconShadowRadius = iconShadowRadius;
        return this;
    }

    public RapidFloatingActionContentLabelList setIconShadowColor(int iconShadowColor) {
        this.iconShadowColor = iconShadowColor;
        return this;
    }

    public RapidFloatingActionContentLabelList setIconShadowDx(int iconShadowDx) {
        this.iconShadowDx = iconShadowDx;
        return this;
    }

    public RapidFloatingActionContentLabelList setIconShadowDy(int iconShadowDy) {
        this.iconShadowDy = iconShadowDy;
        return this;
    }

    public void onExpandAnimator(AnimatorSet animatorSet) {
        int count = this.contentView.getChildCount();
        int i = 0;
        while (i < count) {
            ImageView iconIv = (ImageView) ABViewUtil.obtainView(this.contentView.getChildAt(i), R.id.rfab__content_label_list_icon_iv);
            if (iconIv != null) {
                ObjectAnimator animator = new ObjectAnimator();
                animator.setTarget(iconIv);
                animator.setFloatValues(new float[]{45.0f, 0.0f});
                animator.setPropertyName("rotation");
                animator.setInterpolator(this.mOvershootInterpolator);
                animator.setStartDelay((long) ((count * i) * 20));
                animatorSet.playTogether(new Animator[]{animator});
                i++;
            } else {
                return;
            }
        }
    }

    public void onCollapseAnimator(AnimatorSet animatorSet) {
        int count = this.contentView.getChildCount();
        int i = 0;
        while (i < count) {
            ImageView iconIv = (ImageView) ABViewUtil.obtainView(this.contentView.getChildAt(i), R.id.rfab__content_label_list_icon_iv);
            if (iconIv != null) {
                ObjectAnimator animator = new ObjectAnimator();
                animator.setTarget(iconIv);
                animator.setFloatValues(new float[]{0.0f, 45.0f});
                animator.setPropertyName("rotation");
                animator.setInterpolator(this.mOvershootInterpolator);
                animator.setStartDelay((long) ((count * i) * 20));
                animatorSet.playTogether(new Animator[]{animator});
                i++;
            } else {
                return;
            }
        }
    }
}
