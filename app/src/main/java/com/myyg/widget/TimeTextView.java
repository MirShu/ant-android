package com.myyg.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.CountDownTimer;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.myyg.R;

/**
 * Created by JOHN on 2016/6/21.
 */
public class TimeTextView extends LinearLayout {
    private Context mContext;
    private TextView tv_time;
    private OnTimeTextListener listener;
    private MyygCountDownTimer timer;

    public TimeTextView(Context context) {
        this(context, null);
    }

    public TimeTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TimeTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
        this.initView(attrs, defStyleAttr);
    }

    /**
     * 初始化View
     */
    private void initView(AttributeSet attrs, int defStyleAttr) {
        LayoutInflater layoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = layoutInflater.inflate(R.layout.view_time_text, null, false);
        this.addView(layout);
        this.tv_time = (TextView) layout.findViewById(R.id.tv_time);
        TypedArray typedArray = this.mContext.obtainStyledAttributes(attrs, R.styleable.TimeTextView, defStyleAttr, R.style.timeTextStyle);
        int textColor = typedArray.getColor(R.styleable.TimeTextView_TimeTextColor, 0);
        float textSize = typedArray.getDimension(R.styleable.TimeTextView_TimeTextSize, 16f);
        this.tv_time.setTextColor(textColor);
        this.tv_time.setTextSize(textSize);
    }

    /**
     * 设置时间
     *
     * @param time
     */
    public void setTime(long time) {
        this.setTime(time, 0);
    }

    /**
     * @param time
     * @param interva
     */
    public void setTime(long time, long interva) {
        this.timer = new MyygCountDownTimer(time, interva);
        this.timer.start();
    }

    /**
     * @param listener
     */
    public void setListener(OnTimeTextListener listener) {
        this.listener = listener;
    }

    /**
     *
     */
    class MyygCountDownTimer extends CountDownTimer {
        /**
         * @param millisInFuture    The number of millis in the future from the call
         *                          to {@link #start()} until the countdown is done and {@link #onFinish()}
         *                          is called.
         * @param countDownInterval The interval along the way to receive
         *                          {@link #onTick(long)} callbacks.
         */
        public MyygCountDownTimer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            tv_time.setText(millisUntilFinished / 1000 + "s");
        }

        @Override
        public void onFinish() {
            if (listener != null) {
                listener.onFinish();
            }
        }
    }

    /**
     *
     */
    public interface OnTimeTextListener {
        void onFinish();
    }
}
