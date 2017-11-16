package com.myyg.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.myyg.R;
import com.myyg.base.BaseActivity;
import com.myyg.base.BaseApplication;
import com.myyg.utils.UIHelper;
import com.myyg.widget.FlowLayout;

import java.util.List;

/**
 * Created by Administrator on 2016/5/22.
 */
public class SearchActivity extends BaseActivity {
    private static final int GOODS_GO_SHOP_REQUEST_CODE = 0x01;

    private TextView tv;

    private EditText et_search;

    @Override
    public void initView() {
        setContentView(R.layout.activity_search);
        this.et_search = (EditText) findViewById(R.id.et_search);
        this.showSearchGoods();
        this.showKeyword();
    }

    @Override
    public void initData() {

    }

    @Override
    public void fillView() {

    }

    private void showKeyword() {
        FlowLayout mFlowLayout = (FlowLayout) findViewById(R.id.id_flowlayout);
        LayoutInflater mInflater = LayoutInflater.from(this);
        List<String> listHotKeyword = BaseApplication.getHotKeyword();
        for (String keyword : listHotKeyword) {
            this.tv = (TextView) mInflater.inflate(R.layout.item_hot_search, mFlowLayout, false);
            tv.setText(keyword);
            mFlowLayout.addView(tv);
            tv.setOnClickListener(v -> {
                Bundle bundle = new Bundle();
                bundle.putString(ShowSearchGoodsActivity.KEYWORD, keyword);
                UIHelper.startActivityForResult(mContext, ShowSearchGoodsActivity.class, GOODS_GO_SHOP_REQUEST_CODE, bundle);
            });
        }
    }

    private void showSearchGoods() {
        this.et_search.setOnKeyListener((v, keyCode, event) -> {
            if (keyCode == KeyEvent.KEYCODE_ENTER) {
                ((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(SearchActivity.this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                search();
            }
            return false;
        });
    }

    /**
     *
     */
    private void search() {
        String searchGoods = this.et_search.getText().toString().trim();
        if (TextUtils.isEmpty(searchGoods)) {
            UIHelper.toastMessage(mContext, "输入内容不能为空");
            return;
        }
        Bundle bundle = new Bundle();
        bundle.putString(ShowSearchGoodsActivity.KEYWORD, searchGoods);
        UIHelper.startActivityForResult(mContext, ShowSearchGoodsActivity.class, GOODS_GO_SHOP_REQUEST_CODE, bundle);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_cancel:
                this.finish();
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK) {
            return;
        }
        switch (requestCode) {
            case GOODS_GO_SHOP_REQUEST_CODE:
                setResult(RESULT_OK);
                this.finish();
                break;
        }
    }
}
