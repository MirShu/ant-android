package com.myyg.ui.activity;

import android.widget.ImageView;

import com.myyg.R;
import com.myyg.base.BaseActivity;
import com.myyg.base.BaseApplication;
import com.myyg.common.ImageLoaderEx;
import com.myyg.model.UserModel;
import com.myyg.utils.CommonHelper;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.text.MessageFormat;

/**
 * Created by Administrator on 2016/5/30.
 */
public class QRcodeActivity extends BaseActivity {

    private ImageView image_qr;

    private String baseUrl = CommonHelper.getStaticBasePath();

    @Override
    public void initView() {
        setContentView(R.layout.activity_qr_code);
        this.image_qr = (ImageView) findViewById(R.id.image_qr);

    }

    @Override
    public void initData() {

    }

    @Override
    public void fillView() {
        this.setToolBar("我的二维码");
        this.showQRimage();
    }

    /**
     *
     */
    private void showQRimage() {
        UserModel user = BaseApplication.getUser();
        String qrCode = MessageFormat.format("{0}/{1}", baseUrl, user.getQrcode());
        ImageLoader.getInstance().displayImage(qrCode, this.image_qr, ImageLoaderEx.getDisplayImageOptions());
    }
}
