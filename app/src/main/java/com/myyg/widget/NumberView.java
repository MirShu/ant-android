package com.myyg.widget;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.FloatEvaluator;
import android.animation.IntEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.myyg.R;
import com.myyg.utils.CommonHelper;
import com.myyg.utils.MyLog;
import com.myyg.utils.UIHelper;

import java.text.MessageFormat;

/**
 * Created by shiyuankao on 2016/5/26.
 */
public class NumberView extends LinearLayout {
    private static final String TAG = NumberView.class.getSimpleName();
    private Context mContext;
    private TextView tv_reduce;
    private TextView tv_plus;
    private EditText et_number;
    private OnNumberListener listener;
    private int maxNumber = Integer.MAX_VALUE;

    public NumberView(Context context) {
        this(context, null);
    }

    public NumberView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public NumberView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
        this.init();
    }

    /**
     * 初始化
     */
    private void init() {
        LayoutInflater layoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = layoutInflater.inflate(R.layout.view_number, null, false);
        this.addView(layout);
        this.tv_reduce = (TextView) this.findViewById(R.id.tv_reduce);
        this.et_number = (EditText) this.findViewById(R.id.et_number);
        this.tv_plus = (TextView) this.findViewById(R.id.tv_plus);
        this.bindListener();
    }

    private void bindListener() {
        this.tv_reduce.setOnClickListener(v -> reduce());
        this.tv_plus.setOnClickListener(v -> plus());
        this.et_number.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                int number = 1;
                try {
                    String strNumber = et_number.getText().toString().trim();
                    if (TextUtils.isEmpty(strNumber)) {
                        et_number.setText("1");
                        et_number.selectAll();
                        return;
                    }
                    number = Integer.parseInt(strNumber);
                    if (number > maxNumber) {
                        number = maxNumber;
                        et_number.setText(number + "");
                        et_number.setSelection(String.valueOf(number).length());
                        UIHelper.toastMessage(mContext, MessageFormat.format("最多{0}人次", String.valueOf(maxNumber)));
                    }
                } catch (Exception ex) {
                    et_number.setText(maxNumber + "");
                    et_number.setSelection(String.valueOf(maxNumber).length());
                }
                if (listener != null) {
                    listener.onChange(number);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    /**
     * @param maxNumber
     */
    public void setMaxNumber(int maxNumber) {
        this.maxNumber = maxNumber;
    }

    /**
     * @param isEnabled
     */
    public void setEnabled(boolean isEnabled) {
        this.et_number.setEnabled(isEnabled);
    }

    private void reduce() {
        int number = getNumber();
        number = number - 1;
        if (number < 1) {
            return;
        }
        this.et_number.setText(number + "");
        this.et_number.setSelection(et_number.getText().toString().length());
        this.changeTextSize();
        if (this.listener != null) {
            this.listener.onChange(number);
        }
    }

    private void plus() {
        int number = getNumber();
        number = number + 1;
        if (number < 1 || number > this.maxNumber) {
            UIHelper.toastMessage(mContext, MessageFormat.format("最多{0}人次", String.valueOf(maxNumber)));
            return;
        }
        this.et_number.setText(number + "");
        this.et_number.setSelection(et_number.getText().toString().length());
        this.changeTextSize();
        if (this.listener != null) {
            this.listener.onChange(number);
        }
    }

    public void setWidth(int width) {
        LinearLayout.LayoutParams layoutParams = (LayoutParams) this.et_number.getLayoutParams();
        layoutParams.width = width;
        this.et_number.setLayoutParams(layoutParams);
    }

    public void setHeight(int height) {
        this.et_number.setHeight(height);
    }

    public int getNumber() {
        String strNumber = this.et_number.getText().toString().trim();
        int number = 0;
        if (!TextUtils.isEmpty(strNumber)) {
            number = Integer.parseInt(strNumber);
        }
        return number;
    }

    public void setNumber(int number) {
        this.et_number.setText(number + "");
    }

    public void setNumberListener(OnNumberListener listener) {
        this.listener = listener;
    }

    /**
     * 改变EditText字体大小
     */
    private void changeTextSize() {
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                et_number.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        ValueAnimator valueAnimator = ValueAnimator.ofInt(14, 28);
        valueAnimator.setEvaluator(new IntEvaluator());
        valueAnimator.setInterpolator(new AccelerateInterpolator());
        valueAnimator.addUpdateListener(animator -> {
            int size = (Integer) (animator.getAnimatedValue());
            et_number.setTextSize(TypedValue.COMPLEX_UNIT_SP, size);
        });
        valueAnimator.setDuration(50);
        ValueAnimator valueReduction = ValueAnimator.ofInt(28, 14);
        valueReduction.setEvaluator(new IntEvaluator());
        valueReduction.setInterpolator(new AccelerateInterpolator());
        valueReduction.addUpdateListener(animator -> {
            int size = (Integer) (animator.getAnimatedValue());
            et_number.setTextSize(TypedValue.COMPLEX_UNIT_SP, size);
        });
        valueReduction.setDuration(50);
        animatorSet.play(valueReduction).after(valueAnimator);
        animatorSet.start();
    }

    public interface OnNumberListener {
        void onChange(int number);
    }
}
