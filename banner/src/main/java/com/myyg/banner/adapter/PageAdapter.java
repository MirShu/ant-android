package com.myyg.banner.adapter;

import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import com.myyg.banner.R;
import com.myyg.banner.holder.BannerHolder;
import com.myyg.banner.holder.ViewHolderCreator;
import com.myyg.banner.view.LoopViewPager;

import java.util.List;

/**
 * Created by JOHN on 2016/5/24.
 */
public class PageAdapter<T> extends PagerAdapter {
    protected List<T> mDatas;
    protected ViewHolderCreator holderCreator;
    private boolean canLoop = true;
    private LoopViewPager viewPager;
    private final int MULTIPLE_COUNT = 300;

    public int toRealPosition(int position) {
        int realCount = getRealCount();
        if (realCount == 0)
            return 0;
        int realPosition = position % realCount;
        return realPosition;
    }

    @Override
    public int getCount() {
        return canLoop ? getRealCount() * MULTIPLE_COUNT : getRealCount();
    }

    public int getRealCount() {
        return mDatas == null ? 0 : mDatas.size();
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        int realPosition = toRealPosition(position);
        View view = getView(realPosition, null, container);
        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        View view = (View) object;
        container.removeView(view);
    }

    @Override
    public void finishUpdate(ViewGroup container) {
        int position = viewPager.getCurrentItem();
        if (position == 0) {
            position = viewPager.getFristItem();
        } else if (position == getCount() - 1) {
            position = viewPager.getLastItem();
        }
        try {
            viewPager.setCurrentItem(position, false);
        } catch (IllegalStateException e) {

        }
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    public void setCanLoop(boolean canLoop) {
        this.canLoop = canLoop;
    }

    public void setViewPager(LoopViewPager viewPager) {
        this.viewPager = viewPager;
    }

    public PageAdapter(ViewHolderCreator holderCreator, List<T> datas) {
        this.holderCreator = holderCreator;
        this.mDatas = datas;
    }

    public View getView(int position, View view, ViewGroup container) {
        BannerHolder holder = null;
        if (view == null) {
            holder = (BannerHolder) holderCreator.createHolder();
            view = holder.create(container.getContext());
            view.setTag(R.id.cb_item_tag, holder);
        } else {
            holder = (BannerHolder<T>) view.getTag(R.id.cb_item_tag);
        }
        if (mDatas != null && !mDatas.isEmpty())
            holder.modify(container.getContext(), position, mDatas.get(position));
        return view;
    }
}
