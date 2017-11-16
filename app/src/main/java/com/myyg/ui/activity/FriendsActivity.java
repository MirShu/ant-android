package com.myyg.ui.activity;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.myyg.R;
import com.myyg.adapter.recycler.RecyclerAdapter;
import com.myyg.adapter.recycler.RecyclerViewHolder;
import com.myyg.base.BaseActivity;
import com.myyg.base.BaseApplication;
import com.myyg.constant.SysStatusCode;
import com.myyg.constant.URLS;
import com.myyg.model.FriendsModel;
import com.myyg.model.MessageResult;
import com.myyg.model.UserModel;
import com.myyg.utils.DateHelper;
import com.myyg.utils.UIHelper;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Administrator on 2016/5/31.
 */
public class FriendsActivity extends BaseActivity {
    private final static String TAG = FriendsActivity.class.getSimpleName();

    public final static String REWARD_TAG = "reward_tag";

    public final static String PERCENT_TAG = "percent_tag";

    private int pageIndex = 1;

    private RecyclerAdapter<FriendsModel> adapter;

    private List<FriendsModel> listFriends = new ArrayList<>();

    private RecyclerView rv_friends;

    private TextView tv_reward;

    private TextView tv_percent;

    private String reward;

    private String percent;

    private TextView tv_empty_tips;

    @Override
    public void initView() {
        setContentView(R.layout.activity_friends);
        this.rv_friends = (RecyclerView) findViewById(R.id.rv_friends);
        this.tv_reward = (TextView) findViewById(R.id.tv_reward);
        this.tv_percent = (TextView) findViewById(R.id.tv_percent);
        this.tv_empty_tips = (TextView) this.findViewById(R.id.tv_empty_tips);
        this.bindRecycleView();
        this.bindListener();
    }

    @Override
    public void initData() {
        this.reward = this.getIntent().getStringExtra(REWARD_TAG);
        this.percent = this.getIntent().getStringExtra(PERCENT_TAG);
        this.tv_reward.setText(reward);
        this.tv_percent.setText(percent);
    }

    @Override
    public void fillView() {
        this.setToolBar("邀请记录");
        this.loadData();
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

    /**
     * 绑定用户邀请列表数据
     */
    private void bindRecycleView() {
        UserModel user = BaseApplication.getUser();
        this.tv_reward.setText(user.getReward());
        this.tv_percent.setText(user.getPercent());
        this.listFriends.clear();
        this.adapter = new RecyclerAdapter<FriendsModel>(FriendsActivity.this, this.listFriends, R.layout.item_friends) {
            @Override
            public void convert(RecyclerViewHolder helper, FriendsModel item, int position) {
                helper.setText(R.id.tv_time, MessageFormat.format("{0}", DateHelper.getYYYYMMDD(new Date(item.getTime() * 1000))));
                helper.setText(R.id.tv_name, item.getName());
                helper.setText(R.id.buy_status, item.getBuy_status());
            }
        };
        this.rv_friends.setHasFixedSize(true);
        this.rv_friends.setLayoutManager(new LinearLayoutManager(this.mContext));
        this.rv_friends.setAdapter(this.adapter);
    }

    /**
     * 获取用户邀请列表数据
     */
    private void loadData() {
        String url = MessageFormat.format("{0}/{1}/{2}", URLS.USER_FRIENDS, pageIndex, 500);
        HttpUtils http = new HttpUtils();
        RequestParams params = BaseApplication.getParams();
        http.send(HttpRequest.HttpMethod.GET, url, params, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                MessageResult message = MessageResult.parse(responseInfo.result);
                if (message.getCode() != SysStatusCode.SUCCESS) {
                    UIHelper.toastMessage(mContext, message.getMsg());
                    return;
                }
                List<FriendsModel> list = JSON.parseArray(message.getData(), FriendsModel.class);
                listFriends.addAll(list);
                tv_empty_tips.setVisibility(View.GONE);
                if (listFriends.size() == 0) {
                    tv_empty_tips.setVisibility(View.VISIBLE);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(HttpException e, String s) {

            }
        });
    }
}
