package com.myyg.ui.activity;

import android.support.v4.view.ViewPager;
import android.view.Menu;

import com.myyg.R;
import com.myyg.adapter.FragmentPagerModel;
import com.myyg.adapter.SampleFragmentPagerAdapter;
import com.myyg.base.BaseActivity;
import com.myyg.constant.SysEnums;
import com.myyg.ui.fragment.ShareOrderFragment;
import com.myyg.widget.viewpagerindicator.LinePagerIndicator;

import java.util.ArrayList;
import java.util.List;

public class ShareOrderActivity extends BaseActivity {
    private LinePagerIndicator line_tab_indicator;

    private ViewPager view_pager;

    private List<FragmentPagerModel> listFragmentPager;

    private SampleFragmentPagerAdapter adapter;

    @Override
    public void initView() {
        setContentView(R.layout.activity_share_order);
        this.line_tab_indicator = (LinePagerIndicator) this.findViewById(R.id.line_tab_indicator);
        this.line_tab_indicator.setSelectedColor(0xFFf95667);
        this.line_tab_indicator.setIndicatorColor(0xFFf95667);
        this.line_tab_indicator.setUnderlineHeight(0);
        this.line_tab_indicator.setIndicatorHeight(10);
        this.line_tab_indicator.setBackgroundColor(0xFFffffff);
        this.view_pager = (ViewPager) this.findViewById(R.id.view_pager);
    }

    @Override
    public void initData() {
        this.listFragmentPager = new ArrayList<>();
        this.listFragmentPager.add(new FragmentPagerModel("已晒单", ShareOrderFragment.newInstance(SysEnums.EnumBaskOrder.Yes)));
        this.listFragmentPager.add(new FragmentPagerModel("未晒单", ShareOrderFragment.newInstance(SysEnums.EnumBaskOrder.No)));
        this.adapter = new SampleFragmentPagerAdapter(this.mContext, this.getSupportFragmentManager(), listFragmentPager);
        this.view_pager.setAdapter(adapter);
        this.line_tab_indicator.setViewPager(this.view_pager);
    }

    @Override
    public void fillView() {
        this.setToolBar("我的晒单");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.getMenuInflater().inflate(R.menu.menu_share_order, menu);
        return super.onCreateOptionsMenu(menu);
    }
}
