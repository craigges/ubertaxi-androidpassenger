package com.jozibear247_cab;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.jozibear247_cab.utils.Const;
import com.jozibear247_cab.utils.PreferenceHelper;

public class paygate_webview extends Activity{
	private WebView mWebView;
	private PreferenceHelper preferenceHelper;
	 
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        mWebView = new WebView(this);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.loadUrl("http://www.jozibear247.com/user/paygate/"+ Const.Params.REQUEST_ID2+
				"" + PreferenceHelper.getInstance(this).getRequestId());
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });
 
        this.setContentView(mWebView);
        
    }
 
    @Override
    public void onBackPressed() {
    	// TODO Auto-generated method stub
    	super.onBackPressed();
    	Intent i= new Intent(paygate_webview.this, MainDrawerActivity.class);
		startActivity(i);
		finish();
    }
}

