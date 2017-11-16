package com.myyg.adapter;

import android.support.v4.app.Fragment;

/**
 * Created by JOHN on 2016/4/9.
 */
public class FragmentPagerModel {
    private String title;
    private Fragment fragment;

    public FragmentPagerModel() {
    }

    public FragmentPagerModel(String title, Fragment fragment) {
        this.title = title;
        this.fragment = fragment;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Fragment getFragment() {
        return fragment;
    }

    public void setFragment(Fragment fragment) {
        this.fragment = fragment;
    }

}
