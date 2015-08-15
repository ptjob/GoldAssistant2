package com.parttime.common.activity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JsResult;
import android.webkit.SslErrorHandler;
import android.webkit.WebBackForwardList;
import android.webkit.WebChromeClient;
import android.webkit.WebHistoryItem;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.TextView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.parttime.base.WithTitleActivity;
import com.qingmu.jianzhidaren.R;

/**
 * Created by cjz on 2015/8/8.
 */
public class WebBrowserActivity extends WithTitleActivity{

    public static final String EXTRA_URL = "extra_url";

    @ViewInject(R.id.webvi_main)
    protected WebView webView;


    private String url;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_web_browser);
        ViewUtils.inject(this);
        super.onCreate(savedInstanceState);
        getIntentData(getIntent());
        load();
    }

    private void load(){
        if(url != null && !url.startsWith("http://") && !url.startsWith("https://")){
            url = "http://" + url;
        }
        webView.loadUrl(url);
    }

    private void getIntentData(Intent intent){
        url = intent.getStringExtra(EXTRA_URL);
    }

    @Override
    protected void initViews() {
        super.initViews();
        left(ImageButton.class, R.drawable.back, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goBack();
            }
        });
        initWebView();
    }

    private void initWebView(){
        setDisplay(webView);
        setZoomEnable(webView);
        setMediaEnable(webView);

        webView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient());
        webView.setWebChromeClient(new MyWebChromeClient());

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    public void goBack() {
        if (webView.canGoBack()) {
            webView.goBack();

            WebBackForwardList webBackForwardList = webView.copyBackForwardList();
            WebHistoryItem itemAtIndex = webBackForwardList.getCurrentItem();

        } else {
            super.finish();
        }
    }

    @Override
    protected ViewGroup getLeftWrapper() {
        return null;
    }

    @Override
    protected ViewGroup getRightWrapper() {
        return null;
    }

    @Override
    protected TextView getCenter() {
        return null;
    }


    private void setDisplay(WebView webView) {
        WebSettings settings = webView.getSettings();
        settings.setUseWideViewPort(true);
        settings.setLoadWithOverviewMode(true);
    }

    private void setZoomEnable(WebView webView) {
        WebSettings settings = webView.getSettings();
        settings.setSupportZoom(true);
        settings.setBuiltInZoomControls(true);
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void setMediaEnable(WebView webView) {
        WebSettings settings = webView.getSettings();
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                settings.setMediaPlaybackRequiresUserGesture(true); //需要api-17以上才能支持。
            }
        } catch (Error e) {
            e.printStackTrace();
        }
        settings.setPluginState(WebSettings.PluginState.ON_DEMAND);
        settings.setJavaScriptEnabled(true);

    }

    private class MyWebChromeClient extends WebChromeClient {

        @Override
        public void onReceivedTitle(WebView view, String title) {
//            mIsURLLoadFinish = true;
//            mTitleInitializer.changeCenter(title);
            center(title);
        }

        @Override
        public boolean onJsAlert(WebView view, String url, String message, final JsResult result) {
            AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
            builder.setTitle("提示").setMessage(message).setPositiveButton("确定", null);
            result.confirm();
            if (!WebBrowserActivity.this.isFinishing()) {
                builder.show();
            }
            return true;
        }
    }
}
