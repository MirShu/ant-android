package com.myyg.ui.activity;

import android.text.TextUtils;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.andview.refreshview.XWebView;
import com.myyg.R;
import com.myyg.base.BaseActivity;
import com.myyg.base.BaseApplication;
import com.myyg.javascript.ShareOrderJavaScript;

import java.util.HashMap;

public class WebBrowseActivity extends BaseActivity {
    private final String TAG = WebBrowseActivity.class.getSimpleName();
    public final static String WEB_BROWSE_LINK_TAG = "web_browse_link_tag";
    public final static String WEB_BROWSE_TITLE_TAG = "web_browse_title_tag";

    private XWebView wv_browse;

    private ShareOrderJavaScript javaScript;

    @Override
    public void initView() {
        setContentView(R.layout.activity_web_browse);
        this.wv_browse = (XWebView) findViewById(R.id.wv_browse);
    }

    @Override
    public void initData() {
        this.javaScript = new ShareOrderJavaScript(this);
    }

    @Override
    public void fillView() {
        String url = this.getIntent().getStringExtra(WEB_BROWSE_LINK_TAG);
        String title = this.getIntent().getStringExtra(WEB_BROWSE_TITLE_TAG);
        this.setToolBar(title);
        this.wv_browse.clearCache(true);
        this.wv_browse.setVerticalScrollBarEnabled(false);
        this.wv_browse.setHorizontalScrollBarEnabled(false);
        this.wv_browse.getSettings().setDefaultTextEncodingName("utf-8");
        this.wv_browse.requestFocus();
        this.wv_browse.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        this.wv_browse.getSettings().setSupportZoom(false);
        this.wv_browse.getSettings().setJavaScriptEnabled(true);
        this.wv_browse.getSettings().setAllowFileAccess(true);
        this.wv_browse.addJavascriptInterface(javaScript, "nativeApp");
        HashMap<String, String> hashMapHander = new HashMap<>();
        hashMapHander.put("myyg-sign", "0BA2E855E4240099FFD0620916BC8197");
        String appToken = BaseApplication.getToken();
        if (!TextUtils.isEmpty(appToken)) {
            hashMapHander.put("myyg-token", appToken);
        }
        this.wv_browse.loadUrl(url, hashMapHander);
        this.wv_browse.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });
        if (TextUtils.isEmpty(title)) {
            this.wv_browse.setWebChromeClient(new WebChromeClient() {
                @Override
                public void onReceivedTitle(WebView view, String title) {
                    setToolBar(title);
                }
            });
        }
    }
}
