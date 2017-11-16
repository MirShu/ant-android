package com.myyg.ui.activity;

import android.view.View;

import com.myyg.R;
import com.myyg.base.BaseActivity;
import com.myyg.utils.UIHelper;

/**
 * Created by Administrator on 2016/5/31.
 */
    public class ViewFriendsActivity extends BaseActivity {
        @Override
        public void initView() {
            setContentView(R.layout.activity_view_friends);
        }

        @Override
        public void initData() {

        }

    @Override
    public void fillView() {
        setToolBar("会员返利");
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        switch (view.getId()) {
            case R.id.atm_button:
                UIHelper.startActivity(this.mContext, ATMsActivity.class);
                break;
        }
    }
}
