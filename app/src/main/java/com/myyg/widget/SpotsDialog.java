package com.myyg.widget;


import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.widget.TextView;

import com.myyg.R;
import com.myyg.widget.loading.AnimatedView;
import com.myyg.widget.loading.AnimatorPlayer;
import com.myyg.widget.loading.HesitateInterpolator;
import com.myyg.widget.loading.ProgressLayout;

/**
 * Created by JOHN on 2015/11/28.
 * 引用地址：https://github.com/d-max/spots-dialog
 */
public class SpotsDialog extends AlertDialog {

    private static final int DELAY = 150;
    private static final int DURATION = 1500;

    private int size;
    private AnimatedView[] spots;
    private AnimatorPlayer animator;
    private CharSequence message;
    private TextView dmax_spots_title;

    public SpotsDialog(Context context) {
        this(context, R.style.spotsDialogDefault);
    }

    public SpotsDialog(Context context, CharSequence message) {
        this(context);
        this.message = message;
    }

    public SpotsDialog(Context context, CharSequence message, int theme) {
        this(context, theme);
        this.message = message;
    }

    public SpotsDialog(Context context, int theme) {
        super(context, theme);
    }

    public SpotsDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.dmax_spots_dialog);
        setCanceledOnTouchOutside(false);
        initMessage();
        initProgress();
    }


    @Override
    protected void onStart() {
        super.onStart();

        animator = new AnimatorPlayer(createAnimations());
        animator.play();
    }

    @Override
    protected void onStop() {
        super.onStop();

        animator.stop();
    }

    @Override
    public void setMessage(CharSequence message) {
        if (dmax_spots_title != null) {
            dmax_spots_title.setText(message);
        }
    }

    //~

    private void initMessage() {
        if (message != null && message.length() > 0) {
            ((TextView) findViewById(R.id.dmax_spots_title)).setText(message);
        }
    }

    private void initProgress() {
        ProgressLayout progress = (ProgressLayout) findViewById(R.id.dmax_spots_progress);
        size = progress.getSpotsCount();

        spots = new AnimatedView[size];
        int size = getContext().getResources().getDimensionPixelSize(R.dimen.spot_size);
        int progressWidth = getContext().getResources().getDimensionPixelSize(R.dimen.progress_width);
        for (int i = 0; i < spots.length; i++) {
            AnimatedView v = new AnimatedView(getContext());
            v.setBackgroundResource(R.drawable.dmax_spots_spot);
            v.setTarget(progressWidth);
            v.setXFactor(-1f);
            progress.addView(v, size, size);
            spots[i] = v;
        }
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private Animator[] createAnimations() {
        Animator[] animators = new Animator[size];
        for (int i = 0; i < spots.length; i++) {
            Animator move = ObjectAnimator.ofFloat(spots[i], "xFactor", 0, 1);
            move.setDuration(DURATION);
            move.setInterpolator(new HesitateInterpolator());
            move.setStartDelay(DELAY * i);
            animators[i] = move;
        }
        return animators;
    }
}
