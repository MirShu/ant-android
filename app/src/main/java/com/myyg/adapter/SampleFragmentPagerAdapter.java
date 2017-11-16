package com.myyg.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

/**
 * Created by JOHN on 2016/4/9.
 */
public class SampleFragmentPagerAdapter extends FragmentPagerAdapter {

    protected Context mContext;

    protected List<FragmentPagerModel> listModel;

    public SampleFragmentPagerAdapter(Context context, FragmentManager fm, List<FragmentPagerModel> listModel) {
        super(fm);
        this.mContext = context;
        this.listModel = listModel;
    }

    @Override
    public Fragment getItem(int position) {
        FragmentPagerModel model = this.listModel.get(position);
        Fragment fragment = model.getFragment();
        return fragment;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        FragmentPagerModel model = this.listModel.get(position);
        String title = model.getTitle();
        return title;
    }

    @Override
    public int getCount() {
        return this.listModel.size();
    }
}