package com.myyg.adapter;

import android.content.Context;
import android.support.v4.app.FragmentManager;

import com.myyg.R;
import com.myyg.widget.viewpagerindicator.FadingTabPagerIndicator;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by JOHN on 2016/4/9.
 */
public class MainFragmentPagerAdapter extends SampleFragmentPagerAdapter implements FadingTabPagerIndicator.FadingTab {
    private List<Integer> listNormalIcon;
    private List<Integer> listSelectIcon;

    public MainFragmentPagerAdapter(Context context, FragmentManager fm, List<FragmentPagerModel> listModel) {
        super(context, fm, listModel);
        this.initData();
    }

    /**
     * 初始化数据
     */
    private void initData() {
        listNormalIcon = new ArrayList<>();
        listNormalIcon.add(R.mipmap.ic_home);
        listNormalIcon.add(R.mipmap.ic_announced);
        listNormalIcon.add(R.mipmap.ic_baskorder);
        listNormalIcon.add(R.mipmap.ic_shopcart);
        listNormalIcon.add(R.mipmap.ic_me);

        listSelectIcon = new ArrayList<>();
        listSelectIcon.add(R.mipmap.ic_home_select);
        listSelectIcon.add(R.mipmap.ic_announced_select);
        listSelectIcon.add(R.mipmap.ic_baskorder_select);
        listSelectIcon.add(R.mipmap.ic_shopcart_select);
        listSelectIcon.add(R.mipmap.ic_me_select);
    }

    @Override
    public int getTabNormalIconResId(int position) {
        return this.listNormalIcon.get(position);
    }

    @Override
    public int getTabSelectIconResId(int position) {
        return this.listSelectIcon.get(position);
    }

    @Override
    public int getTabNormalTextColor(int position) {
        return this.mContext.getResources().getColor(R.color.text_normal);
    }

    @Override
    public int getTabSelectTextColor(int position) {
        return this.mContext.getResources().getColor(R.color.text_select);
    }
}
