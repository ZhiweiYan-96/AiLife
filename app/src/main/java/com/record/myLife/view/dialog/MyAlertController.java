package com.record.myLife.view.dialog;

import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnKeyListener;
import android.content.DialogInterface.OnMultiChoiceClickListener;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.CursorAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import com.record.myLife.R;
import java.lang.ref.WeakReference;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

public class MyAlertController {
    private static final int MATCH_PARENT = -1;
    public static int m_MyAlertContentViewId = R.layout.huz_alert_dialog;
    private ListAdapter mAdapter;
    OnClickListener mButtonHandler = new OnClickListener() {
        public void onClick(View v) {
            Message m = null;
            if (v == MyAlertController.this.mButtonPositive && MyAlertController.this.mButtonPositiveMessage != null) {
                m = Message.obtain(MyAlertController.this.mButtonPositiveMessage);
            } else if (v == MyAlertController.this.mButtonNegative && MyAlertController.this.mButtonNegativeMessage != null) {
                m = Message.obtain(MyAlertController.this.mButtonNegativeMessage);
            } else if (v == MyAlertController.this.mButtonNeutral && MyAlertController.this.mButtonNeutralMessage != null) {
                m = Message.obtain(MyAlertController.this.mButtonNeutralMessage);
            }
            if (m != null) {
                m.sendToTarget();
            }
        }
    };
    private Button mButtonNegative;
    private Message mButtonNegativeMessage;
    private CharSequence mButtonNegativeText;
    private Button mButtonNeutral;
    private Message mButtonNeutralMessage;
    private CharSequence mButtonNeutralText;
    private Button mButtonPositive;
    private Message mButtonPositiveMessage;
    private CharSequence mButtonPositiveText;
    private int mCheckedItem = -1;
    protected final Context mContext;
    private View mCustomTitleView;
    private final DialogInterface mDialogInterface;
    protected boolean mForceInverseBackground;
    private Handler mHandler;
    private Drawable mIcon;
    private int mIconId = 0;
    private ImageView mIconView;
    private ListView mListView;
    private CharSequence mMessage;
    private TextView mMessageView;
    private ScrollView mScrollView;
    private CharSequence mTitle;
    private TextView mTitleView;
    private View mView;
    private int mViewSpacingBottom;
    private int mViewSpacingLeft;
    private int mViewSpacingRight;
    private boolean mViewSpacingSpecified = false;
    private int mViewSpacingTop;
    private final Window mWindow;

    public static class AlertParams {
        public ListAdapter mAdapter;
        public boolean mCancelable;
        public int mCheckedItem = -1;
        public boolean[] mCheckedItems;
        public final Context mContext;
        public Cursor mCursor;
        public View mCustomTitleView;
        public boolean mForceInverseBackground;
        public Drawable mIcon;
        public int mIconId = -1;
        public final LayoutInflater mInflater;
        public String mIsCheckedColumn;
        public boolean mIsMultiChoice;
        public boolean mIsSingleChoice;
        public CharSequence[] mItems;
        public String mLabelColumn;
        public CharSequence mMessage;
        public DialogInterface.OnClickListener mNegativeButtonListener;
        public CharSequence mNegativeButtonText;
        public DialogInterface.OnClickListener mNeutralButtonListener;
        public CharSequence mNeutralButtonText;
        public OnCancelListener mOnCancelListener;
        public OnMultiChoiceClickListener mOnCheckboxClickListener;
        public DialogInterface.OnClickListener mOnClickListener;
        public OnItemSelectedListener mOnItemSelectedListener;
        public OnKeyListener mOnKeyListener;
        public OnPrepareListViewListener mOnPrepareListViewListener;
        public DialogInterface.OnClickListener mPositiveButtonListener;
        public CharSequence mPositiveButtonText;
        public boolean mRecycleOnMeasure = true;
        public CharSequence mTitle;
        public View mView;
        public int mViewSpacingBottom;
        public int mViewSpacingLeft;
        public int mViewSpacingRight;
        public boolean mViewSpacingSpecified = false;
        public int mViewSpacingTop;

        public interface OnPrepareListViewListener {
            void onPrepareListView(ListView listView);
        }

        public AlertParams(Context context) {
            this.mContext = context;
            this.mCancelable = true;
            this.mInflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
        }

        public void apply(MyAlertController dialog) {
            if (this.mCustomTitleView != null) {
                dialog.setCustomTitle(this.mCustomTitleView);
            } else {
                if (this.mTitle != null) {
                    dialog.setTitle(this.mTitle);
                }
                if (this.mIcon != null) {
                    dialog.setIcon(this.mIcon);
                }
                if (this.mIconId >= 0) {
                    dialog.setIcon(this.mIconId);
                }
            }
            if (this.mMessage != null) {
                dialog.setMessage(this.mMessage);
            }
            if (this.mPositiveButtonText != null) {
                dialog.setButton(-1, this.mPositiveButtonText, this.mPositiveButtonListener, null);
            }
            if (this.mNegativeButtonText != null) {
                dialog.setButton(-2, this.mNegativeButtonText, this.mNegativeButtonListener, null);
            }
            if (this.mNeutralButtonText != null) {
                dialog.setButton(-3, this.mNeutralButtonText, this.mNeutralButtonListener, null);
            }
            if (this.mForceInverseBackground) {
                dialog.setInverseBackgroundForced(true);
            }
            if (!(this.mItems == null && this.mCursor == null && this.mAdapter == null)) {
                createListView(dialog);
            }
            if (this.mView == null) {
                return;
            }
            if (this.mViewSpacingSpecified) {
                dialog.setView(this.mView, this.mViewSpacingLeft, this.mViewSpacingTop, this.mViewSpacingRight, this.mViewSpacingBottom);
                return;
            }
            dialog.setView(this.mView);
        }

        private void createListView(final MyAlertController dialog) {
            ListAdapter adapter = null;
            final RecycleListView listView = (RecycleListView) this.mInflater.inflate(R.layout.huz_select_dialog, null);
            if (!this.mIsMultiChoice) {
                int layout = this.mIsSingleChoice ? R.layout.huz_select_dialog_singlechoice : R.layout.huz_select_dialog_item;
                adapter = this.mCursor == null ? this.mAdapter != null ? this.mAdapter : new ArrayAdapter(this.mContext, layout,16908308, this.mItems) : new SimpleCursorAdapter(this.mContext, layout, this.mCursor, new String[]{this.mLabelColumn}, new int[]{16908308});
            } else if (this.mCursor == null) {
                adapter = new ArrayAdapter<CharSequence>(this.mContext, R.layout.huz_select_dialog_multichoice, 16908308, this.mItems) {
                    public View getView(int position, View convertView, ViewGroup parent) {
                        View view = super.getView(position, convertView, parent);
                        if (AlertParams.this.mCheckedItems != null && AlertParams.this.mCheckedItems[position]) {
                            listView.setItemChecked(position, true);
                        }
                        return view;
                    }
                };
            } else {
                ListAdapter anonymousClass2 = new CursorAdapter(this.mContext, this.mCursor, false) {
                    private final int mIsCheckedIndex = 0;
                    private final int mLabelIndex = 0;

                    public void bindView(View view, Context context, Cursor cursor) {
                        boolean z = true;
                        ((CheckedTextView) view.findViewById(16908308)).setText(cursor.getString(this.mLabelIndex));
                        RecycleListView recycleListView = listView;
                        int position = cursor.getPosition();
                        if (cursor.getInt(this.mIsCheckedIndex) != 1) {
                            z = false;
                        }
                        recycleListView.setItemChecked(position, z);
                    }

                    public View newView(Context context, Cursor cursor, ViewGroup parent) {
                        return AlertParams.this.mInflater.inflate(R.layout.huz_select_dialog_multichoice, parent, false);
                    }
                };
            }
            if (this.mOnPrepareListViewListener != null) {
                this.mOnPrepareListViewListener.onPrepareListView(listView);
            }
            dialog.mAdapter = adapter;
            dialog.mCheckedItem = this.mCheckedItem;
            if (this.mOnClickListener != null) {
                listView.setOnItemClickListener(new OnItemClickListener() {
                    public void onItemClick(AdapterView parent, View v, int position, long id) {
                        AlertParams.this.mOnClickListener.onClick(dialog.mDialogInterface, position);
                        if (!AlertParams.this.mIsSingleChoice) {
                            dialog.mDialogInterface.dismiss();
                        }
                    }
                });
            } else if (this.mOnCheckboxClickListener != null) {
                listView.setOnItemClickListener(new OnItemClickListener() {
                    public void onItemClick(AdapterView parent, View v, int position, long id) {
                        if (AlertParams.this.mCheckedItems != null) {
                            AlertParams.this.mCheckedItems[position] = listView.isItemChecked(position);
                        }
                        AlertParams.this.mOnCheckboxClickListener.onClick(dialog.mDialogInterface, position, listView.isItemChecked(position));
                    }
                });
            }
            if (this.mOnItemSelectedListener != null) {
                listView.setOnItemSelectedListener(this.mOnItemSelectedListener);
            }
            if (this.mIsSingleChoice) {
                listView.setChoiceMode(1);
            } else if (this.mIsMultiChoice) {
                listView.setChoiceMode(2);
            }
            listView.mRecycleOnMeasure = this.mRecycleOnMeasure;
            dialog.mListView = listView;
        }
    }

    private static final class ButtonHandler extends Handler {
        private static final int MSG_DISMISS_DIALOG = 1;
        private WeakReference<DialogInterface> mDialog;

        public ButtonHandler(DialogInterface dialog) {
            this.mDialog = new WeakReference(dialog);
        }

        public void handleMessage(Message msg) {
            switch (msg.what) {
                case -3:
                case -2:
                case -1:
                    ((DialogInterface.OnClickListener) msg.obj).onClick((DialogInterface) this.mDialog.get(), msg.what);
                    return;
                case 1:
                    ((DialogInterface) msg.obj).dismiss();
                    return;
                default:
                    return;
            }
        }
    }

    public static class RecycleListView extends ListView {
        boolean mRecycleOnMeasure = true;

        public RecycleListView(Context context) {
            super(context);
        }

        public RecycleListView(Context context, AttributeSet attrs) {
            super(context, attrs);
        }

        public RecycleListView(Context context, AttributeSet attrs, int defStyle) {
            super(context, attrs, defStyle);
        }

        protected boolean recycleOnMeasure() {
            return this.mRecycleOnMeasure;
        }
    }

    public MyAlertController(Context context, DialogInterface di, Window window) {
        this.mContext = context;
        this.mDialogInterface = di;
        this.mWindow = window;
        this.mHandler = new ButtonHandler(di);
    }

    static boolean canTextInput(View v) {
        if (v.onCheckIsTextEditor()) {
            return true;
        }
        if (!(v instanceof ViewGroup)) {
            return false;
        }
        ViewGroup vg = (ViewGroup) v;
        int i = vg.getChildCount();
        while (i > 0) {
            i--;
            if (canTextInput(vg.getChildAt(i))) {
                return true;
            }
        }
        return false;
    }

    public void installContent() {
        this.mWindow.requestFeature(1);
        if (this.mView == null || !canTextInput(this.mView)) {
            this.mWindow.setFlags(131072, 131072);
        }
        this.mWindow.setContentView(m_MyAlertContentViewId);
        setupView();
    }

    public void setTitle(CharSequence title) {
        this.mTitle = title;
        if (this.mTitleView != null) {
            this.mTitleView.setText(title);
        }
    }

    public void setCustomTitle(View customTitleView) {
        this.mCustomTitleView = customTitleView;
    }

    public void setMessage(CharSequence message) {
        this.mMessage = message;
        if (this.mMessageView != null) {
            this.mMessageView.setText(message);
        }
    }

    public void setView(View view) {
        this.mView = view;
        this.mViewSpacingSpecified = false;
    }

    public void setView(View view, int viewSpacingLeft, int viewSpacingTop, int viewSpacingRight, int viewSpacingBottom) {
        this.mView = view;
        this.mViewSpacingSpecified = true;
        this.mViewSpacingLeft = viewSpacingLeft;
        this.mViewSpacingTop = viewSpacingTop;
        this.mViewSpacingRight = viewSpacingRight;
        this.mViewSpacingBottom = viewSpacingBottom;
    }

    public void setButton(int whichButton, CharSequence text, DialogInterface.OnClickListener listener, Message msg) {
        if (msg == null && listener != null) {
            msg = this.mHandler.obtainMessage(whichButton, listener);
        }
        switch (whichButton) {
            case -3:
                this.mButtonNeutralText = text;
                this.mButtonNeutralMessage = msg;
                return;
            case -2:
                this.mButtonNegativeText = text;
                this.mButtonNegativeMessage = msg;
                return;
            case -1:
                this.mButtonPositiveText = text;
                this.mButtonPositiveMessage = msg;
                return;
            default:
                throw new IllegalArgumentException("Button does not exist");
        }
    }

    public void setIcon(int resId) {
        this.mIconId = resId;
        if (this.mIconView == null) {
            return;
        }
        if (resId > 0) {
            this.mIconView.setImageResource(this.mIconId);
        } else if (resId == 0) {
            this.mIconView.setVisibility(View.GONE);
        }
    }

    public void setIcon(Drawable icon) {
        this.mIcon = icon;
        if (this.mIconView != null && this.mIcon != null) {
            this.mIconView.setImageDrawable(icon);
        }
    }

    public void setInverseBackgroundForced(boolean forceInverseBackground) {
        this.mForceInverseBackground = forceInverseBackground;
    }

    public ListView getListView() {
        return this.mListView;
    }

    public Button getButton(int whichButton) {
        switch (whichButton) {
            case -3:
                return this.mButtonNeutral;
            case -2:
                return this.mButtonNegative;
            case -1:
                return this.mButtonPositive;
            default:
                return null;
        }
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return this.mScrollView != null && this.mScrollView.executeKeyEvent(event);
    }

    public boolean onKeyUp(int keyCode, KeyEvent event) {
        return this.mScrollView != null && this.mScrollView.executeKeyEvent(event);
    }

    private void setupView() {
        LinearLayout contentPanel = (LinearLayout) this.mWindow.findViewById(R.id.contentPanel);
        setupContent(contentPanel);
        boolean hasButtons = setupButtons();
        LinearLayout topPanel = (LinearLayout) this.mWindow.findViewById(R.id.topPanel);
        boolean hasTitle = setupTitle(topPanel);
        View buttonPanel = this.mWindow.findViewById(R.id.buttonPanel);
        if (!hasButtons) {
            buttonPanel.setVisibility(View.GONE);
        }
        FrameLayout customPanel = null;
        if (this.mView != null) {
            customPanel = (FrameLayout) this.mWindow.findViewById(R.id.customPanel);
            FrameLayout custom = (FrameLayout) this.mWindow.findViewById(R.id.custom);
            custom.addView(this.mView, new LayoutParams(-1, -1));
            if (this.mViewSpacingSpecified) {
                custom.setPadding(this.mViewSpacingLeft, this.mViewSpacingTop, this.mViewSpacingRight, this.mViewSpacingBottom);
            }
            if (this.mListView != null) {
                ((LinearLayout.LayoutParams) customPanel.getLayoutParams()).weight = 0.0f;
            }
        } else {
            this.mWindow.findViewById(R.id.customPanel).setVisibility(View.GONE);
        }
        if (!(hasTitle && this.mMessage == null && this.mView == null)) {
        }
        setBackground(topPanel, contentPanel, customPanel, hasButtons, null, hasTitle, buttonPanel);
    }

    private boolean setupTitle(LinearLayout topPanel) {
        if (this.mCustomTitleView != null) {
            topPanel.addView(this.mCustomTitleView, new LinearLayout.LayoutParams(-1, -2));
            this.mWindow.findViewById(R.id.title_template).setVisibility(View.GONE);
            return true;
        }
        boolean hasTextTitle = !TextUtils.isEmpty(this.mTitle);
        this.mIconView = (ImageView) this.mWindow.findViewById(R.id.icon);
        if (hasTextTitle) {
            this.mTitleView = (TextView) this.mWindow.findViewById(R.id.alertTitle);
            this.mTitleView.setTextColor(-16777216);
            this.mTitleView.setText(this.mTitle);
            if (this.mIconId > 0) {
                this.mIconView.setImageResource(this.mIconId);
                return true;
            } else if (this.mIcon != null) {
                this.mIconView.setImageDrawable(this.mIcon);
                return true;
            } else if (this.mIconId != 0) {
                return true;
            } else {
                this.mTitleView.setPadding(this.mIconView.getPaddingLeft(), this.mIconView.getPaddingTop(), this.mIconView.getPaddingRight(), this.mIconView.getPaddingBottom());
                this.mIconView.setVisibility(View.GONE);
                return true;
            }
        }
        this.mWindow.findViewById(R.id.title_template).setVisibility(8);
        this.mIconView.setVisibility(View.GONE);
        return false;
    }

    private void setupContent(LinearLayout contentPanel) {
        this.mScrollView = (ScrollView) this.mWindow.findViewById(R.id.scrollView);
        this.mScrollView.setFocusable(false);
        this.mMessageView = (TextView) this.mWindow.findViewById(R.id.message);
        if (this.mMessageView != null) {
            if (this.mMessage != null) {
                this.mMessageView.setText(this.mMessage);
                return;
            }
            this.mMessageView.setVisibility(View.GONE);
            this.mScrollView.removeView(this.mMessageView);
            if (this.mListView != null) {
                contentPanel.removeView(this.mWindow.findViewById(R.id.scrollView));
                contentPanel.addView(this.mListView, new LinearLayout.LayoutParams(-1, -1));
                contentPanel.setLayoutParams(new LinearLayout.LayoutParams(-1, 0, 1.0f));
                return;
            }
            contentPanel.setVisibility(View.GONE);
        }
    }

    private boolean setupButtons() {
        int whichButtons = 0;
        this.mButtonPositive = (Button) this.mWindow.findViewById(R.id.button1);
        this.mButtonPositive.setOnClickListener(this.mButtonHandler);
        if (TextUtils.isEmpty(this.mButtonPositiveText)) {
            this.mButtonPositive.setVisibility(View.GONE);
        } else {
            this.mButtonPositive.setText(this.mButtonPositiveText);
            this.mButtonPositive.setVisibility(View.VISIBLE);
            whichButtons = 0 | 1;
        }
        this.mButtonNegative = (Button) this.mWindow.findViewById(R.id.button2);
        this.mButtonNegative.setOnClickListener(this.mButtonHandler);
        if (TextUtils.isEmpty(this.mButtonNegativeText)) {
            this.mButtonNegative.setVisibility(View.GONE);
        } else {
            this.mButtonNegative.setText(this.mButtonNegativeText);
            this.mButtonNegative.setVisibility(View.VISIBLE);
            whichButtons |= 2;
        }
        this.mButtonNeutral = (Button) this.mWindow.findViewById(R.id.button3);
        this.mButtonNeutral.setOnClickListener(this.mButtonHandler);
        if (TextUtils.isEmpty(this.mButtonNeutralText)) {
            this.mButtonNeutral.setVisibility(View.GONE);
        } else {
            this.mButtonNeutral.setText(this.mButtonNeutralText);
            this.mButtonNeutral.setVisibility(View.VISIBLE);
            whichButtons |= 4;
        }
        if (whichButtons == 1) {
            centerButton(this.mButtonPositive);
        } else if (whichButtons == 2) {
            centerButton(this.mButtonNeutral);
        } else if (whichButtons == 4) {
            centerButton(this.mButtonNeutral);
        }
        if (whichButtons != 0) {
            return true;
        }
        return false;
    }

    private void centerButton(Button button) {
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) button.getLayoutParams();
        params.gravity = 1;
        params.weight = 0.5f;
        button.setLayoutParams(params);
        this.mWindow.findViewById(R.id.leftSpacer).setVisibility(View.VISIBLE);
        this.mWindow.findViewById(R.id.rightSpacer).setVisibility(View.VISIBLE);
    }

    private void setBackground(LinearLayout topPanel, LinearLayout contentPanel, View customPanel, boolean hasButtons, TypedArray axxd, boolean hasTitle, View buttonPanel) {
        View[] views = new View[4];
        boolean[] light = new boolean[4];
        View lastView = null;
        int pos = 0;
        if (hasTitle) {
            views[0] = topPanel;
            light[0] = true;
            pos = 0 + 1;
        }
        if (contentPanel.getVisibility() == View.GONE) {
            contentPanel = null;
        }
        views[pos] = contentPanel;
        light[pos] = true;
        pos++;
        if (customPanel != null) {
            views[pos] = customPanel;
            light[pos] = true;
            pos++;
        }
        if (hasButtons) {
            views[pos] = buttonPanel;
            light[pos] = true;
        }
        boolean setView = false;
        for (pos = 0; pos < views.length; pos++) {
            View v = views[pos];
            if (v != null) {
                if (lastView != null) {
                    if (!setView) {
                    }
                    setView = true;
                }
                lastView = v;
                boolean lastLight = light[pos];
            }
        }
        if (lastView == null || setView) {
        }
        ((LinearLayout) this.mWindow.findViewById(R.id.pcenterPanel)).setBackgroundResource(R.drawable.huz_popup_full);
        if (this.mListView != null && this.mAdapter != null) {
            this.mListView.setAdapter(this.mAdapter);
            if (this.mCheckedItem > -1) {
                this.mListView.setItemChecked(this.mCheckedItem, true);
                this.mListView.setSelection(this.mCheckedItem);
            }
        }
    }
}
