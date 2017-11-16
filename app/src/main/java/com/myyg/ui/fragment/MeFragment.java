package com.myyg.ui.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.andview.refreshview.XRefreshView;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.myyg.R;
import com.myyg.base.BaseApplication;
import com.myyg.common.ImageLoaderEx;
import com.myyg.constant.SysStatusCode;
import com.myyg.constant.URLS;
import com.myyg.listener.OnLoginVerifyListener;
import com.myyg.model.MessageResult;
import com.myyg.model.UserModel;
import com.myyg.ui.activity.AccountDetailsActivity;
import com.myyg.ui.activity.AccountRechargeActivity;
import com.myyg.ui.activity.BiddingRecordActivity;
import com.myyg.ui.activity.MemberAwardActivity;
import com.myyg.ui.activity.MyReceiptAddressActivity;
import com.myyg.ui.activity.NoticeActivity;
import com.myyg.ui.activity.PersonalCenterActivity;
import com.myyg.ui.activity.QRcodeActivity;
import com.myyg.ui.activity.RechargeRecordActivity;
import com.myyg.ui.activity.SettingActivity;
import com.myyg.ui.activity.ShareOrderActivity;
import com.myyg.ui.activity.WinningRecordActivity;
import com.myyg.utils.CommonHelper;
import com.myyg.utils.UIHelper;
import com.myyg.widget.RoundedImageView;
import com.myyg.widget.viewpagerindicator.BadgeView;
import com.myyg.widget.xrefreshview.MyygRefreshHeader;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.text.MessageFormat;


@SuppressLint("ValidFragment")
public class MeFragment extends Fragment implements View.OnClickListener {

    private XRefreshView xRefreshView;

    private TextView tv_user_name;

    private TextView tv_money;

    private TextView tv_level;

    private TextView tv_score;

    private RoundedImageView riv_avatar;

    private OnLoginVerifyListener listener;

    private FrameLayout fl_header;

    private ImageView iv_notice;

    private String baseUrl = CommonHelper.getStaticBasePath();

    private BadgeView badgeView;

    private TextView tv_mobile_or_email;

    /**
     *
     */
    public MeFragment(OnLoginVerifyListener listener) {
        this.listener = listener;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_me, container, false);
        this.xRefreshView = (XRefreshView) view.findViewById(R.id.xrefreshview);
        RelativeLayout rl_all = (RelativeLayout) view.findViewById(R.id.rl_all);
        RelativeLayout rl_reward = (RelativeLayout) view.findViewById(R.id.rl_reward);
        RelativeLayout rl_member = (RelativeLayout) view.findViewById(R.id.rl_member);
        RelativeLayout rl_share = (RelativeLayout) view.findViewById(R.id.rl_share);
        RelativeLayout rl_account_details = (RelativeLayout) view.findViewById(R.id.rl_account_details);
        RelativeLayout rl_address = (RelativeLayout) view.findViewById(R.id.rl_address);
        RelativeLayout rl_qr_code = (RelativeLayout) view.findViewById(R.id.rl_qr_code);
        iv_notice = (ImageView) view.findViewById(R.id.iv_notice);
        this.fl_header = (FrameLayout) view.findViewById(R.id.fl_header);
        Button btn_fragment_me_recharge = (Button) view.findViewById(R.id.btn_fragment_me_recharge);
        RoundedImageView riv_avatar = (RoundedImageView) view.findViewById(R.id.riv_avatar);
        this.tv_money = (TextView) view.findViewById(R.id.tv_money);
        this.tv_user_name = (TextView) view.findViewById(R.id.tv_user_name);
        this.tv_level = (TextView) view.findViewById(R.id.tv_level);
        this.riv_avatar = (RoundedImageView) view.findViewById(R.id.riv_avatar);
        this.tv_score = (TextView) view.findViewById(R.id.tv_score);
        this.tv_mobile_or_email = (TextView) view.findViewById(R.id.tv_mobile_or_email);
        ImageView iv_set = (ImageView) view.findViewById(R.id.iv_set);
        rl_all.setOnClickListener(this);
        rl_reward.setOnClickListener(this);
        rl_member.setOnClickListener(this);
        rl_share.setOnClickListener(this);
        rl_account_details.setOnClickListener(this);
        rl_address.setOnClickListener(this);
        rl_qr_code.setOnClickListener(this);
        this.iv_notice.setOnClickListener(this);
        btn_fragment_me_recharge.setOnClickListener(this);
        riv_avatar.setOnClickListener(this);
        iv_set.setOnClickListener(this);
        this.badgeView = new BadgeView(getActivity(), fl_header);
        this.badgeView.setBadgePosition(BadgeView.POSITION_TOP_RIGHT);
        this.badgeView.setBadgeMargin(CommonHelper.dp2px(getContext(), 10), CommonHelper.dp2px(getContext(), 10));
        this.xRefreshView();
        return view;

    }

    /**
     * 刷新用户最新数据
     */
    private void xRefreshView() {
        MyygRefreshHeader header = new MyygRefreshHeader(this.getActivity());
        this.xRefreshView.setCustomHeaderView(header);
        this.xRefreshView.setPullLoadEnable(false);
        this.xRefreshView.setMoveForHorizontal(true);
        this.xRefreshView.setXRefreshViewListener(new XRefreshView.SimpleXRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(() -> {
                    refresh();
                    getNoticeCount();
                }, 1000);
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_all:
                UIHelper.startActivity(this.getActivity(), BiddingRecordActivity.class);
                break;
            case R.id.rl_reward:
                UIHelper.startActivity(this.getActivity(), WinningRecordActivity.class);
                break;
            case R.id.rl_member:
                UIHelper.startActivity(this.getActivity(), MemberAwardActivity.class);
                break;
            case R.id.rl_share:
                UIHelper.startActivity(this.getActivity(), ShareOrderActivity.class);
                break;
            case R.id.rl_account_details:
                //UIHelper.startActivity(this.getActivity(), RechargeRecordActivity.class);
                UIHelper.startActivity(this.getActivity(), AccountDetailsActivity.class);
                break;
            case R.id.rl_address:
                UIHelper.startActivity(this.getActivity(), MyReceiptAddressActivity.class);
                break;
            case R.id.rl_qr_code:
                UIHelper.startActivity(this.getActivity(), QRcodeActivity.class);
                break;
            case R.id.iv_notice:
                UIHelper.startActivity(this.getActivity(), NoticeActivity.class);
                break;
            case R.id.btn_fragment_me_recharge:
                UIHelper.startActivity(this.getActivity(), AccountRechargeActivity.class);
                break;
            case R.id.riv_avatar:
                UIHelper.startActivity(this.getActivity(), PersonalCenterActivity.class);
                break;
            case R.id.iv_set:
                UIHelper.startActivityForResult(this.getActivity(), SettingActivity.class, 0x02, null);
                break;
        }
    }

    /**
     * 刷新用户信息
     */
    private void refresh() {
        HttpUtils http = new HttpUtils();
        RequestParams params = BaseApplication.getParams();
        http.send(HttpRequest.HttpMethod.GET, URLS.USER_GET_INFO, params, new RequestCallBack<String>() {

            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                try {
                    MessageResult message = MessageResult.parse(responseInfo.result);
                    if (message.getCode() != SysStatusCode.SUCCESS) {
                        //UIHelper.toastMessage(getActivity(), message.getMsg());
                        return;
                    }
                    UserModel user = UserModel.parse(message.getData());
                    BaseApplication.sharedLogin(user);
                    showUser();
                } catch (Exception ex) {

                } finally {
                    xRefreshView.stopRefresh();
                }
            }

            @Override
            public void onFailure(HttpException e, String s) {
                xRefreshView.stopRefresh();
            }
        });
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        if (isVisibleToUser && isVisible()) {
            this.showUser();
        }
        super.setUserVisibleHint(isVisibleToUser);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        if (getUserVisibleHint()) {
            this.showUser();
        }
        super.onActivityCreated(savedInstanceState);
    }

    /**
     * 显示用户信息
     */


    private void showUser() {
        UserModel user = BaseApplication.getUser();
        if (user == null) {
            listener.onCallback(false, 4);
            return;
        }
        String strMoney = MessageFormat.format("余额：<font color=\"#C62334\">{0}</font> 元", user.getMoney());
        this.tv_money.setText(Html.fromHtml(strMoney));
        String strScore = MessageFormat.format("云购币：<font color=\"#C62334\">{0}</font>", String.valueOf(user.getScore()));
        this.tv_score.setText(Html.fromHtml(strScore));
        this.tv_user_name.setText(user.getUsername());
        this.tv_level.setText(user.getLevel());
        String mobile = user.getMobile();
        String mobileOrEmail = TextUtils.isEmpty(mobile) ? user.getEmail() : mobile;
        this.tv_mobile_or_email.setText(mobileOrEmail);
        if (TextUtils.isEmpty(mobileOrEmail)) {
            this.tv_mobile_or_email.setVisibility(View.GONE);
        }
        String avatar = MessageFormat.format("{0}/{1}", this.baseUrl, user.getAvatar());
        ImageLoader.getInstance().displayImage(avatar, this.riv_avatar, ImageLoaderEx.getDefaultDisplayImageOptions());
        this.riv_avatar.setVisibility(View.VISIBLE);
    }

    @Override
    public void onResume() {
        super.onResume();
        this.getNoticeCount();
        this.refresh();
    }

    /**
     * 获取未读消息数
     */
    private void getNoticeCount() {
        HttpUtils http = new HttpUtils();
        RequestParams params = BaseApplication.getParams();
        http.send(HttpRequest.HttpMethod.GET, URLS.USER_NOTICE_COUNT, params, new RequestCallBack<String>() {

            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                MessageResult result = MessageResult.parse(responseInfo.result);
                if (result.getCode() != SysStatusCode.SUCCESS) {
                    return;
                }
                int count = Integer.parseInt(result.getData());
                badgeView.hide();
                if (count > 0) {
                    badgeView.setText(count + "");
                    badgeView.show();
                }
            }

            @Override
            public void onFailure(HttpException e, String s) {

            }
        });
    }
}
