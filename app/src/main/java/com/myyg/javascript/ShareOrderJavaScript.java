package com.myyg.javascript;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.JavascriptInterface;

import com.myyg.ui.activity.GoodsDetailsActivity;
import com.myyg.utils.UIHelper;

/**
 * Created by JOHN on 2016/9/27.
 */
public class ShareOrderJavaScript extends BaseJavaScript {
    /**
     * @param mContext
     */
    public ShareOrderJavaScript(Activity mContext) {
        super(mContext);
    }

    /**
     * @param goodsId
     */
    @JavascriptInterface
    public void openGoodInfo(String goodsId) {
        this.mHandler.post(() -> {
            Bundle bundle = new Bundle();
            bundle.putInt(GoodsDetailsActivity.GOODS_ID_TAG, Integer.valueOf(goodsId));
            UIHelper.startActivity(this.mContext, GoodsDetailsActivity.class, bundle);
        });
    }
}
