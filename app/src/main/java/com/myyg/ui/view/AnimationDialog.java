package com.myyg.ui.view;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.myyg.R;
import com.myyg.base.BaseApplication;
import com.myyg.model.GoodsModel;
import com.myyg.utils.CommonHelper;
import com.myyg.utils.MyLog;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.text.MessageFormat;

/**
 * Created by JOHN on 2016/7/3.
 */
public class AnimationDialog extends Dialog {
    private Context mContent;
    private GoodsModel model;
    private int[] startLocation;
    private int[] targetLocation;
    private int left;
    private int top;
    private int width;
    private ImageView iv_goods;
    private String baseUrl = CommonHelper.getStaticBasePath();

    public AnimationDialog(Context context, GoodsModel model, int[] startLocation, int width, int[] targetLocation) {
        super(context, R.style.transparent_dialog);
        this.mContent = context;
        this.model = model;
        this.startLocation = startLocation;
        this.targetLocation = targetLocation;
        this.left = startLocation[0];
        this.top = startLocation[1] - CommonHelper.dp2px(this.mContent, 15);
        this.width = width;
        this.left = this.left + (width / 2 - CommonHelper.dp2px(this.mContent, 100) / 2);
        this.initView();
    }

    /**
     *
     */
    private void initView() {
        String imgUrl = MessageFormat.format("{0}/{1}", baseUrl, model.getThumb());
        this.setContentView(R.layout.view_animation);
        Window dialogWindow = this.getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.gravity = Gravity.FILL;
        lp.width = BaseApplication.WINDOW_WIDTH;
        lp.height = BaseApplication.WINDOW_HEIGHT;
        dialogWindow.setAttributes(lp);
        this.iv_goods = (ImageView) this.findViewById(R.id.iv_goods);
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) this.iv_goods.getLayoutParams();
        layoutParams.width = width;
        layoutParams.height = width;
        this.iv_goods.setLayoutParams(layoutParams);
        this.iv_goods.setX(left);
        this.iv_goods.setY(top);
        ImageLoader.getInstance().displayImage(imgUrl, this.iv_goods);
        this.executeAnimation();
    }

    /**
     * 执行动画
     */
    private void executeAnimation() {
        ObjectAnimator rotation = ObjectAnimator.ofFloat(this.iv_goods, "rotation", 0f, 720f);//旋转
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(this.iv_goods, "scaleX", 1.0f, 0.2f);//缩放X
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(this.iv_goods, "scaleY", 1.0f, 0.2f);//缩放Y
        ObjectAnimator alpha = ObjectAnimator.ofFloat(this.iv_goods, "alpha", 1.0f, 0.5f);//透明度
        ObjectAnimator translationX = ObjectAnimator.ofFloat(this.iv_goods, "translationX", startLocation[0], this.targetLocation[0]);//位移X
        ObjectAnimator translationY = ObjectAnimator.ofFloat(this.iv_goods, "translationY", startLocation[1], this.targetLocation[1]);//位移Y

        AnimatorSet set = new AnimatorSet();
        set.play(rotation)
                .with(scaleX)
                .with(scaleY)
                .with(alpha)
                .with(translationX)
                .with(translationY);
        set.setDuration(800);
        set.start();
        set.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                dismiss();
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
    }
}
