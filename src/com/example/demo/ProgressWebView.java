package com.example.demo;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ClipDrawable;
import android.graphics.drawable.ColorDrawable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.webkit.WebView;
import android.widget.ProgressBar;

public class ProgressWebView extends WebView {

	private ProgressBar progressbar;
	
	public ProgressWebView(Context context) {
		super(context);
		
		progressbar = new ProgressBar(context, null,
				android.R.attr.progressBarStyleHorizontal);
		progressbar.setBackgroundColor(Color.parseColor("#00000000"));
//		progressbar.setBackgroundResource(R.color.transparent);
		ClipDrawable d = new ClipDrawable(new ColorDrawable(Color.parseColor("#f34b4e")), Gravity.LEFT, ClipDrawable.HORIZONTAL);
		progressbar.setProgressDrawable(d);
		
		progressbar.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
				6, 0, 0));
		addView(progressbar);
		// setWebViewClient(new WebViewClient(){});
		setWebChromeClient(new WebChromeClient());
		try{
			removeJavascriptInterface("searchBoxJavaBridge_");
			removeJavascriptInterface("accessibility")  ;
			removeJavascriptInterface("accessibilityTraversal");
		}catch(Throwable e){
			
		}
	}

	public ProgressWebView(Context context, AttributeSet attrs) {
		super(context, attrs);
		progressbar = new ProgressBar(context, null,
				android.R.attr.progressBarStyleHorizontal);
//		progressbar.setBackgroundResource(R.color.transparent);
		setBackgroundColor(Color.parseColor("#00000000"));
		ClipDrawable d = new ClipDrawable(new ColorDrawable(Color.parseColor("#f34b4e")), Gravity.LEFT, ClipDrawable.HORIZONTAL);
		progressbar.setProgressDrawable(d);
		
		progressbar.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
				6, 0, 0));
		addView(progressbar);
		// setWebViewClient(new WebViewClient(){});
		setWebChromeClient(new WebChromeClient());
	}

	public ProgressBar getProgressbar() {
		return progressbar;
	}

	public class WebChromeClient extends android.webkit.WebChromeClient {
		@Override
		public void onProgressChanged(WebView view, int newProgress) {
			if (newProgress == 100) {
				progressbar.setVisibility(GONE);
			} else {
				if (progressbar.getVisibility() == GONE)
					progressbar.setVisibility(VISIBLE);
				progressbar.setProgress(newProgress);
			}
			super.onProgressChanged(view, newProgress);
		}

	}

	@Override
	protected void onScrollChanged(int l, int t, int oldl, int oldt) {
		LayoutParams lp = (LayoutParams) progressbar.getLayoutParams();
		lp.x = l;
		lp.y = t;
		progressbar.setLayoutParams(lp);
		super.onScrollChanged(l, t, oldl, oldt);
	}

}
