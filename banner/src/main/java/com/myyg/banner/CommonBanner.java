package com.myyg.banner;

/**
 * Created by JOHN on 2016/5/24.
 */

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.myyg.banner.adapter.PageAdapter;
import com.myyg.banner.holder.ViewHolderCreator;
import com.myyg.banner.listener.OnItemClickListener;
import com.myyg.banner.listener.PageChangeListener;
import com.myyg.banner.view.LoopViewPager;

import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * 页面翻转控件，极方便的广告栏
 * 支持无限循环，自动翻页，翻页特效
 *
 * @author Sai 支持自动翻页
 */
public class CommonBanner<T> extends LinearLayout {
    private List<T> mDatas;
    private int[] page_indicatorId;
    private ArrayList<ImageView> mPointViews = new ArrayList<ImageView>();
    private PageChangeListener pageChangeListener;
    private ViewPager.OnPageChangeListener onPageChangeListener;
    private PageAdapter pageAdapter;
    private LoopViewPager view_pager;
    private ViewPagerScroller scroller;
    private ViewGroup ll_indicator;
    private long autoTurningTime;
    private boolean turning;
    private boolean canTurn = false;
    private boolean manualPageable = true;
    private boolean canLoop = true;

    public enum PageIndicatorAlign {
        ALIGN_PARENT_LEFT, ALIGN_PARENT_RIGHT, CENTER_HORIZONTAL
    }

    private AdSwitchTask adSwitchTask;

    public CommonBanner(Context context) {
        super(context);
        init(context);
    }

    public CommonBanner(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CommonBanner);
        canLoop = a.getBoolean(R.styleable.CommonBanner_canLoop, true);
        a.recycle();
        init(context);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public CommonBanner(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CommonBanner);
        canLoop = a.getBoolean(R.styleable.CommonBanner_canLoop, true);
        a.recycle();
        init(context);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public CommonBanner(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CommonBanner);
        canLoop = a.getBoolean(R.styleable.CommonBanner_canLoop, true);
        a.recycle();
        init(context);
    }

    private void init(Context context) {
        View hView = LayoutInflater.from(context).inflate(R.layout.banner_viewpager, this, true);
        view_pager = (LoopViewPager) hView.findViewById(R.id.view_pager);
        ll_indicator = (ViewGroup) hView.findViewById(R.id.ll_indicator);
        initViewPagerScroll();

        adSwitchTask = new AdSwitchTask(this);
    }

    static class AdSwitchTask implements Runnable {

        private final WeakReference<CommonBanner> reference;

        AdSwitchTask(CommonBanner commonBanner) {
            this.reference = new WeakReference<CommonBanner>(commonBanner);
        }

        @Override
        public void run() {
            CommonBanner commonBanner = reference.get();

            if (commonBanner != null) {
                if (commonBanner.view_pager != null && commonBanner.turning) {
                    int page = commonBanner.view_pager.getCurrentItem() + 1;
                    commonBanner.view_pager.setCurrentItem(page);
                    commonBanner.postDelayed(commonBanner.adSwitchTask, commonBanner.autoTurningTime);
                }
            }
        }
    }

    public CommonBanner setPages(ViewHolderCreator holderCreator, List<T> datas) {
        this.mDatas = datas;
        pageAdapter = new PageAdapter(holderCreator, mDatas);
        view_pager.setAdapter(pageAdapter, canLoop);

        if (page_indicatorId != null)
            setPageIndicator(page_indicatorId);
        return this;
    }

    /**
     * 通知数据变化
     * 如果只是增加数据建议使用 notifyDataSetAdd()
     */
    public void notifyDataSetChanged() {
        view_pager.getAdapter().notifyDataSetChanged();
        if (page_indicatorId != null)
            setPageIndicator(page_indicatorId);
    }

    /**
     * 设置底部指示器是否可见
     *
     * @param visible
     */
    public CommonBanner setPointViewVisible(boolean visible) {
        ll_indicator.setVisibility(visible ? View.VISIBLE : View.GONE);
        return this;
    }

    /**
     * 底部指示器资源图片
     *
     * @param page_indicatorId
     */
    public CommonBanner setPageIndicator(int[] page_indicatorId) {
        ll_indicator.removeAllViews();
        mPointViews.clear();
        this.page_indicatorId = page_indicatorId;
        if (mDatas == null) return this;
        for (int count = 0; count < mDatas.size(); count++) {
            // 翻页指示的点
            ImageView pointView = new ImageView(getContext());
            pointView.setPadding(5, 0, 5, 0);
            if (mPointViews.isEmpty())
                pointView.setImageResource(page_indicatorId[1]);
            else
                pointView.setImageResource(page_indicatorId[0]);
            mPointViews.add(pointView);
            ll_indicator.addView(pointView);
        }
        pageChangeListener = new PageChangeListener(mPointViews, page_indicatorId);
        view_pager.setOnPageChangeListener(pageChangeListener);
        pageChangeListener.onPageSelected(view_pager.getRealItem());
        if (onPageChangeListener != null)
            pageChangeListener.setOnPageChangeListener(onPageChangeListener);
        return this;
    }

    /**
     * 指示器的方向
     *
     * @param align 三个方向：居左 （RelativeLayout.ALIGN_PARENT_LEFT），居中 （RelativeLayout.CENTER_HORIZONTAL），居右 （RelativeLayout.ALIGN_PARENT_RIGHT）
     * @return
     */
    public CommonBanner setPageIndicatorAlign(PageIndicatorAlign align) {
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) ll_indicator.getLayoutParams();
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT, align == PageIndicatorAlign.ALIGN_PARENT_LEFT ? RelativeLayout.TRUE : 0);
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, align == PageIndicatorAlign.ALIGN_PARENT_RIGHT ? RelativeLayout.TRUE : 0);
        layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL, align == PageIndicatorAlign.CENTER_HORIZONTAL ? RelativeLayout.TRUE : 0);
        ll_indicator.setLayoutParams(layoutParams);
        return this;
    }

    /***
     * 是否开启了翻页
     *
     * @return
     */
    public boolean isTurning() {
        return turning;
    }

    /***
     * 开始翻页
     *
     * @param autoTurningTime 自动翻页时间
     * @return
     */
    public CommonBanner startTurning(long autoTurningTime) {
        //如果是正在翻页的话先停掉
        if (turning) {
            stopTurning();
        }
        //设置可以翻页并开启翻页
        canTurn = true;
        this.autoTurningTime = autoTurningTime;
        turning = true;
        postDelayed(adSwitchTask, autoTurningTime);
        return this;
    }

    public void stopTurning() {
        turning = false;
        removeCallbacks(adSwitchTask);
    }

    /**
     * 自定义翻页动画效果
     *
     * @param transformer
     * @return
     */
    public CommonBanner setPageTransformer(ViewPager.PageTransformer transformer) {
        this.view_pager.setPageTransformer(true, transformer);
        return this;
    }


    /**
     * 设置ViewPager的滑动速度
     */
    private void initViewPagerScroll() {
        try {
            Field mScroller = null;
            mScroller = ViewPager.class.getDeclaredField("mScroller");
            mScroller.setAccessible(true);
            scroller = new ViewPagerScroller(this.view_pager.getContext());
            mScroller.set(this.view_pager, scroller);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public boolean isManualPageable() {
        return this.view_pager.isCanScroll();
    }

    public void setManualPageable(boolean manualPageable) {
        this.view_pager.setCanScroll(manualPageable);
    }

    //触碰控件的时候，翻页应该停止，离开的时候如果之前是开启了翻页的话则重新启动翻页
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {

        int action = ev.getAction();
        if (action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_CANCEL || action == MotionEvent.ACTION_OUTSIDE) {
            // 开始翻页
            if (canTurn) startTurning(autoTurningTime);
        } else if (action == MotionEvent.ACTION_DOWN) {
            // 停止翻页
            if (canTurn) stopTurning();
        }
        return super.dispatchTouchEvent(ev);
    }

    //获取当前的页面index
    public int getCurrentItem() {
        if (this.view_pager != null) {
            return this.view_pager.getRealItem();
        }
        return -1;
    }

    //设置当前的页面index
    public void setcurrentitem(int index) {
        if (this.view_pager != null) {
            this.view_pager.setCurrentItem(index);
        }
    }

    public ViewPager.OnPageChangeListener getOnPageChangeListener() {
        return onPageChangeListener;
    }

    /**
     * 设置翻页监听器
     *
     * @param onPageChangeListener
     * @return
     */
    public CommonBanner setOnPageChangeListener(ViewPager.OnPageChangeListener onPageChangeListener) {
        this.onPageChangeListener = onPageChangeListener;
        //如果有默认的监听器（即是使用了默认的翻页指示器）则把用户设置的依附到默认的上面，否则就直接设置
        if (pageChangeListener != null)
            pageChangeListener.setOnPageChangeListener(onPageChangeListener);
        else this.view_pager.setOnPageChangeListener(onPageChangeListener);
        return this;
    }

    public boolean isCanLoop() {
        return this.view_pager.isCanLoop();
    }

    /**
     * 监听item点击
     *
     * @param onItemClickListener
     */
    public CommonBanner setOnItemClickListener(AdapterView.OnItemClickListener onItemClickListener) {
        if (onItemClickListener == null) {
            this.view_pager.setOnItemClickListener(null);
            return this;
        }
        this.view_pager.setOnItemClickListener((OnItemClickListener) onItemClickListener);
        return this;
    }

    /**
     * 设置ViewPager的滚动速度
     *
     * @param scrollDuration
     */
    public void setScrollDuration(int scrollDuration) {
        scroller.setScrollDuration(scrollDuration);
    }

    public int getScrollDuration() {
        return scroller.getScrollDuration();
    }

    public LoopViewPager getViewPager() {
        return this.view_pager;
    }

    public void setCanLoop(boolean canLoop) {
        this.canLoop = canLoop;
        this.view_pager.setCanLoop(canLoop);
    }
}

