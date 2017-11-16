package com.myyg.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.EditText;

import com.myyg.R;

/**
 * Created by JOHN on 2016/4/9.
 */
public class LineEditText extends EditText {

    // 画笔 用来画下划线
    private Paint paint;

    public LineEditText(Context context) {
        this(context, null);
    }

    public LineEditText(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LineEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.initView();
    }

    /**
     *
     */
    private void initView() {
        paint = new Paint();
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(getResources().getColor(R.color.hint_color));
        // 开启抗锯齿 较耗内存
        paint.setAntiAlias(true);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawLine(0, this.getHeight() - 1, this.getWidth() - 1, this.getHeight() - 1, paint);
    }
}
