package com.wemakeprice.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.TextView;

import org.w3c.dom.Text;

public class MainActivity extends AppCompatActivity {

    private static final String JAVASCRIPT_OBJ = "android";
    private static final String BASE_URL = "file:///android_asset/webview.html";

    private WebView mWebView;
    private TextView mStatusTv;
    private Button mScriptBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mWebView = findViewById(R.id.webview);
        mStatusTv = findViewById(R.id.status_tv);
        mScriptBtn = findViewById(R.id.script_btn);

        __initWebView(mWebView);
        __initView();
    }

    private void __initWebView(WebView webView) {

        //크롬 inspect 가능하게함
        WebView.setWebContentsDebuggingEnabled(true);

        // 웹뷰에서 mixed content 허용하게 수정
        webView.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);

        //쿠키 사용
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.setAcceptCookie(true);
        cookieManager.setAcceptThirdPartyCookies(webView, true);
        cookieManager.setAcceptCookie(true);

        //자바스크립트
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setSaveFormData(true);
        webView.getSettings().setAppCacheEnabled(true);
        webView.getSettings().setDatabaseEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);

        //fit screen\
        webView.setInitialScale(1);
        webView.getSettings().setUseWideViewPort(true);  // 웹뷰가 html의 viewport 메타 태그를 지원
        webView.getSettings().setLoadWithOverviewMode(true);  // 웹뷰가 html 컨텐츠가 웹뷰보다 클 경우 스크린 크기에 맞게 조정

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                return super.shouldOverrideUrlLoading(view, request);
            }
        });

        //자바스크립트 브릿지
        webView.addJavascriptInterface(new JavaScriptInterface(), JAVASCRIPT_OBJ);

        webView.loadUrl(BASE_URL);
    }

    private void __initView() {
        mScriptBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String script = String.format("javascript: changeKeyword('%s')", "버튼 클릭");
                mWebView.evaluateJavascript(script, null);
            }
        });
    }


    class JavaScriptInterface {
        /**
         * Web -> Native로 호출됨
         *
         * @param fromWeb
         */
        @android.webkit.JavascriptInterface
        public void updateKeyword(final String fromWeb) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mStatusTv.setText(fromWeb);
                }
            });
        }
    }
}

