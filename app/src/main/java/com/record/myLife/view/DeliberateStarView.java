package com.record.myLife.view;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnDragListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RatingBar.OnRatingBarChangeListener;
import android.widget.TextView;
import com.record.myLife.R;
import com.record.utils.FormatUtils;

public class DeliberateStarView extends LinearLayout {
    Context context;
    int count = 4;
    int currentIndex = 2;
    int imageId = 0;
    int is_all_star_indicator = 0;
    int is_show_score_textview = 0;
    int is_show_total_value_cirle = 1;
    int is_show_total_value_star = 0;
    ImageView iv_total_rating;
    LinearLayout ll;
    View ll_deleberate_circle_and_prompt;
    View ll_remind_tomato_total;
    OnRatingBarChangeListener myOnRatingBarChangeListener = new OnRatingBarChangeListener() {
        public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
            int rating_index;
            int temp_id = ratingBar.getId();
            if (DeliberateStarView.this.rb_remind_tomato_goal.getId() == temp_id) {
                rating_index = 0;
                DeliberateStarView.this.tx_deliberate_goal.setText(rating + "");
            } else if (DeliberateStarView.this.rb_remind_tomato_feedback.getId() == temp_id) {
                rating_index = 1;
                DeliberateStarView.this.tx_deliberate_feedback.setText(rating + "");
            } else if (DeliberateStarView.this.rb_remind_tomato_focus.getId() == temp_id) {
                rating_index = 2;
                DeliberateStarView.this.tx_deliberate_focus.setText(rating + "");
            } else {
                rating_index = 3;
                DeliberateStarView.this.tx_deliberate_unconfortable.setText(rating + "");
            }
            String[][] goal_arr = new String[4][];
            goal_arr[0] = new String[]{"无练习目标\n想都不敢想吗？", "只知练习目标方向\n有方向的人成长更快！", "只知道部分目标指标\n试着将目标写下来！", "目标清晰\n这是刻意练的开始！", "目标清晰并有可量化指标\n 恭喜！继续保持！"};
            goal_arr[1] = new String[]{"无任何反馈\n真的没有吗?!", "有1个反馈\n反馈是进步的开始，及时改进！", "有2个反馈\n很好，请保持！", "有3个反馈！\n很棒！你就是这样超过别人的！", "有4个以上反馈！\n祝贺！你已甩了别人几条街！"};
            goal_arr[2] = new String[]{"中断/走神超过5次以上\n什么原因？！", "中断/走神4次\n反省并优化！", "中断/走神超过3次\n还可以更专注！", "中断/走神1~2次\n这不可避免，但已不错！", "完全专注其中！\n我是注意力战斗机！"};
            goal_arr[3] = new String[]{"没有任何不适\n舒服区会让你停滞不前！", "感受到挫折/不适1次\n应该高兴，你在成长！", "感受到挫折/不适2次\n别怕，还有更多！", "感受到挫折/不适3次\n是否进入恐慌区了？", "感受到挫折/不适超过4次\n迈过这道坎，你会更坚强！"};
            String prompt = "";
            if (rating <= 1.0f) {
                prompt = goal_arr[rating_index][0];
            } else if (rating > 1.0f && rating <= 2.0f) {
                prompt = goal_arr[rating_index][1];
            } else if (rating > 2.0f && rating <= 3.0f) {
                prompt = goal_arr[rating_index][2];
            } else if (rating > 3.0f && rating <= 4.0f) {
                prompt = goal_arr[rating_index][3];
            } else if (rating > 4.0f && rating <= 5.0f) {
                prompt = goal_arr[rating_index][4];
            }
            DeliberateStarView.this.tv_deliberate_prompt.setText(prompt);
            DeliberateStarView.this.setTotalRating();
        }
    };
    RatingBar rb_remind_tomato_feedback;
    RatingBar rb_remind_tomato_focus;
    RatingBar rb_remind_tomato_goal;
    RatingBar rb_remind_tomato_total;
    RatingBar rb_remind_tomato_unconfortable;
    TextView tv_deliberate_prompt;
    TextView tx_deliberate_feedback;
    TextView tx_deliberate_focus;
    TextView tx_deliberate_goal;
    TextView tx_deliberate_total_val;
    TextView tx_deliberate_unconfortable;
    TextView tx_total_rating;

    public DeliberateStarView(Context context) {
        super(context);
        this.context = context;
    }

    public DeliberateStarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        parserAttrs(context, attrs, 0, 0);
        initView();
    }

    @SuppressLint({"NewApi"})
    public DeliberateStarView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.context = context;
        parserAttrs(context, attrs, defStyle, 0);
        initView();
    }

    @TargetApi(21)
    public DeliberateStarView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        parserAttrs(context, attrs, defStyleAttr, defStyleRes);
        initView();
    }

    private void parserAttrs(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
//        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.DeliberateStarView, defStyleAttr, defStyleRes);
//        try {
//            this.is_show_total_value_cirle = a.getInteger(0, 1);
//            this.is_show_total_value_star = a.getInteger(1, 0);
//            this.is_all_star_indicator = a.getInteger(2, 0);
//            this.is_show_score_textview = a.getInteger(2, 0);
//        } finally {
//            a.recycle();
//        }
    }

    private void initView() {
        LayoutInflater inflater = (LayoutInflater) this.context.getSystemService("layout_inflater");
        if (this.ll == null) {
            this.ll = (LinearLayout) inflater.inflate(R.layout.tem_ll_deliberate, this);
        }
        this.ll_deleberate_circle_and_prompt = this.ll.findViewById(R.id.ll_deleberate_circle_and_prompt);
        this.tv_deliberate_prompt = (TextView) this.ll.findViewById(R.id.tv_deliberate_prompt);
        this.tx_total_rating = (TextView) this.ll.findViewById(R.id.tx_total_rating);
        this.tx_deliberate_total_val = (TextView) this.ll.findViewById(R.id.tx_deliberate_total_val);
        this.tx_deliberate_feedback = (TextView) this.ll.findViewById(R.id.tx_deliberate_feedback);
        this.tx_deliberate_unconfortable = (TextView) this.ll.findViewById(R.id.tx_deliberate_unconfortable);
        this.tx_deliberate_goal = (TextView) this.ll.findViewById(R.id.tx_deliberate_goal);
        this.tx_deliberate_focus = (TextView) this.ll.findViewById(R.id.tx_deliberate_focus);
        this.iv_total_rating = (ImageView) this.ll.findViewById(R.id.iv_total_rating);
        this.rb_remind_tomato_goal = (RatingBar) this.ll.findViewById(R.id.rb_remind_tomato_goal);
        this.rb_remind_tomato_feedback = (RatingBar) this.ll.findViewById(R.id.rb_remind_tomato_feedback);
        this.rb_remind_tomato_focus = (RatingBar) this.ll.findViewById(R.id.rb_remind_tomato_focus);
        this.rb_remind_tomato_unconfortable = (RatingBar) this.ll.findViewById(R.id.rb_remind_tomato_unconfortable);
        this.rb_remind_tomato_total = (RatingBar) this.ll.findViewById(R.id.rb_remind_tomato_total);
        this.ll_remind_tomato_total = this.ll.findViewById(R.id.ll_remind_tomato_total);
        this.rb_remind_tomato_feedback.setOnRatingBarChangeListener(this.myOnRatingBarChangeListener);
        this.rb_remind_tomato_unconfortable.setOnRatingBarChangeListener(this.myOnRatingBarChangeListener);
        this.rb_remind_tomato_goal.setOnRatingBarChangeListener(this.myOnRatingBarChangeListener);
        this.rb_remind_tomato_focus.setOnRatingBarChangeListener(this.myOnRatingBarChangeListener);
        if (this.is_show_total_value_cirle == 0) {
            this.ll_deleberate_circle_and_prompt.setVisibility(8);
        } else {
            this.ll_deleberate_circle_and_prompt.setVisibility(0);
        }
        if (this.is_show_total_value_star == 0) {
            this.ll_remind_tomato_total.setVisibility(8);
        } else {
            this.ll_remind_tomato_total.setVisibility(0);
        }
        if (1 == this.is_all_star_indicator) {
            this.rb_remind_tomato_goal.setIsIndicator(true);
            this.rb_remind_tomato_feedback.setIsIndicator(true);
            this.rb_remind_tomato_focus.setIsIndicator(true);
            this.rb_remind_tomato_unconfortable.setIsIndicator(true);
            this.rb_remind_tomato_total.setIsIndicator(true);
        }
        if (1 == this.is_show_score_textview) {
            this.tx_deliberate_total_val.setVisibility(0);
            this.tx_deliberate_feedback.setVisibility(0);
            this.tx_deliberate_unconfortable.setVisibility(0);
            this.tx_deliberate_goal.setVisibility(0);
            this.tx_deliberate_focus.setVisibility(0);
        } else {
            this.tx_deliberate_total_val.setVisibility(8);
            this.tx_deliberate_feedback.setVisibility(8);
            this.tx_deliberate_unconfortable.setVisibility(8);
            this.tx_deliberate_goal.setVisibility(8);
            this.tx_deliberate_focus.setVisibility(8);
        }
        this.rb_remind_tomato_unconfortable.setOnDragListener(new OnDragListener() {
            public boolean onDrag(View v, DragEvent event) {
                return false;
            }
        });
    }

    public float getRatingByGoal() {
        return this.rb_remind_tomato_goal.getRating();
    }

    public float getRatingByFeedback() {
        return this.rb_remind_tomato_feedback.getRating();
    }

    public float getRatingByFocus() {
        return this.rb_remind_tomato_focus.getRating();
    }

    public float getRatingByUncomforable() {
        return this.rb_remind_tomato_unconfortable.getRating();
    }

    public double getTotalRating() {
        return (((((double) this.rb_remind_tomato_goal.getRating()) * 0.15d) + (((double) this.rb_remind_tomato_feedback.getRating()) * 0.4d)) + (((double) this.rb_remind_tomato_focus.getRating()) * 0.15d)) + (((double) this.rb_remind_tomato_unconfortable.getRating()) * 0.3d);
    }

    public void setAllRating(float rating_goal, float rating_feedback, float rating_focus, float rating_uncomfortable, float total_val) {
        this.rb_remind_tomato_goal.setRating(rating_goal);
        this.rb_remind_tomato_feedback.setRating(rating_feedback);
        this.rb_remind_tomato_focus.setRating(rating_focus);
        this.rb_remind_tomato_unconfortable.setRating(rating_uncomfortable);
        this.rb_remind_tomato_total.setRating(total_val);
        this.tx_deliberate_goal.setText(rating_goal + "");
        this.tx_deliberate_feedback.setText(rating_feedback + "");
        this.tx_deliberate_focus.setText(rating_focus + "");
        this.tx_deliberate_unconfortable.setText(rating_uncomfortable + "");
        this.tx_deliberate_total_val.setText(total_val + "");
    }

    private void setTotalRating() {
        double totalRating = getTotalRating();
        String total_rating = FormatUtils.format_2fra(totalRating);
        this.tx_total_rating.setText(total_rating);
        if (totalRating <= 2.0d) {
            this.iv_total_rating.setImageResource(R.drawable.x_circle_blue_1_shap);
            this.tx_total_rating.setTextColor(getResources().getColor(R.color.black_tran_es));
        } else if (totalRating <= 2.0d || totalRating >= 4.0d) {
            this.tx_total_rating.setTextColor(getResources().getColor(R.color.white2));
            this.iv_total_rating.setImageResource(R.drawable.x_circle_blue_2);
        } else {
            this.iv_total_rating.setImageResource(R.drawable.x_circle_blue_1);
            this.tx_total_rating.setTextColor(getResources().getColor(R.color.white2));
        }
        this.rb_remind_tomato_total.setRating(Float.parseFloat(total_rating));
        this.tx_deliberate_total_val.setText(total_rating + "");
    }
}
