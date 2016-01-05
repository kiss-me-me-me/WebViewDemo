package com.example.demo;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.WebBackForwardList;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * @version 1.0.0
 * @description: webView读取html文档
 * @date: 2015年9月20日 下午3:35:59
 * @author: xiefeng
 */
public class WebpageSigningActivity extends Activity implements OnClickListener {

	private String TAG = WebpageSigningActivity.class.getSimpleName();
	private RelativeLayout rlTitle;
	private RelativeLayout imbBack, imbClose;
	private TextView tvTitle;
	private TextView tvError;
	private ProgressWebView webView;

	private ProgressBar pbModuleDownload;
//	private String mFileUrl = "file:///android_asset/demo1225/page/product/main.html";// 协议URL
	private String mFileUrl = "http://baidu.com";// 协议URL

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sfbest);
		initView();
		initData();
		initLister();
		try {
			initWebView();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (webView != null)
			webView.removeAllViews();
		webView = null;
	}

	private void initView() {
		rlTitle = (RelativeLayout) findViewById(R.id.rl_title);
		tvTitle = (TextView) findViewById(R.id.tv_title);
		tvError = (TextView) findViewById(R.id.tv_error);
		imbBack = (RelativeLayout) findViewById(R.id.imb_back);
		imbClose = (RelativeLayout) findViewById(R.id.imb_close);
		webView = (ProgressWebView) findViewById(R.id.sfbest_webview);
		pbModuleDownload = (ProgressBar) findViewById(R.id.more_loading);
		tvError.setBackgroundColor(0);
		tvError.setEnabled(false);
	}

	private void initData() {
		if (getIntent().getExtras() != null) {
			mFileUrl = getIntent().getExtras().getString("fileUrl");
			if (mFileUrl.contains("http")) {
				rlTitle.setVisibility(View.VISIBLE);
			} else {
				rlTitle.setVisibility(View.GONE);
			}
		}
	}

	private void initLister() {
		imbBack.setOnClickListener(this);
		imbClose.setOnClickListener(this);
	}

	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.tv_error:
			loadingWebView();
			break;
		case R.id.imb_back:
			WebBackForwardList mWebBackForwardList = webView
					.copyBackForwardList();
			String historyUrl = mWebBackForwardList.getItemAtIndex(
					mWebBackForwardList.getCurrentIndex() - 1).getUrl();
			if (historyUrl.contains("http")) {
				rlTitle.setVisibility(View.VISIBLE);
			} else {
				rlTitle.setVisibility(View.GONE);
			}
			if (webView.canGoBack()) {
				webView.goBack();
			} else {
				WebpageSigningActivity.this.finish();
			}
			break;
		case R.id.imb_close:
			rlTitle.setVisibility(View.GONE);
			webView.loadUrl(mFileUrl);
			break;
		default:
			break;
		}
	}

	/**
	 * 初始化webView
	 */
	@SuppressLint("SetJavaScriptEnabled")
	private void initWebView() throws NoSuchMethodException,
			InvocationTargetException, IllegalAccessException {
		// 触摸焦点起作用：如果不设置，则在点击网页文本输入框时，不能弹出软键盘及不响应其他的一些事件
		webView.requestFocus(View.FOCUS_DOWN);
		webView.setBackgroundColor(0);
		webView.setVerticalScrollBarEnabled(false);
		webView.setHorizontalScrollBarEnabled(false);

		WebSettings webSettings = webView.getSettings();
		webSettings.setBlockNetworkImage(false);
		webSettings.setAllowFileAccess(true); // 设置允许访问文件数据
		webSettings.setLoadsImagesAutomatically(true);
		webSettings.setDefaultTextEncodingName("utf-8"); // 设置编码
		webSettings.setDatabaseEnabled(true);
		webSettings.setLoadWithOverviewMode(true);
		webSettings.setUseWideViewPort(true);
		webSettings.setJavaScriptEnabled(true); // 支持js互调
		webSettings.setJavaScriptCanOpenWindowsAutomatically(true); // 允许js弹出窗口
		webSettings.setSupportZoom(true);
		webSettings.setBuiltInZoomControls(true);
		webSettings.setDomStorageEnabled(true);
		webSettings.setDatabasePath("/data/data/"
				+ webView.getContext().getPackageName() + "/databases/");

		webView.setWebChromeClient(new MyWebChromeClient());
		webView.setWebViewClient(new DialogWebViewClient());

		try {
			if (Build.VERSION.SDK_INT >= 16) {
				Class<?> clazz = webView.getSettings().getClass();
				Method method = clazz.getMethod(
						"setAllowUniversalAccessFromFileURLs", boolean.class);
				if (method != null) {
					method.invoke(webView.getSettings(), true);
				}
			}
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}

		webSettings.setJavaScriptEnabled(true);
		webSettings.setSupportZoom(false);
		webSettings.setTextSize(WebSettings.TextSize.NORMAL);//
		webSettings.setAllowFileAccess(true);

		loadingWebView();
	}

	/**
	 * 加载url
	 */
	private void loadingWebView() {
		tvError.setVisibility(View.GONE);
		pbModuleDownload.setVisibility(View.VISIBLE);
		webView.setVisibility(View.VISIBLE);
		webView.loadUrl(mFileUrl);

	}

	/**
	 * 自定义webView进度展示
	 */
	private class MyWebChromeClient extends WebChromeClient {

		public void onProgressChanged(WebView view, int newProgress) {
			if (webView != null) {
				ProgressBar progressbar = webView.getProgressbar();
				if (newProgress == 100) {
					progressbar.setVisibility(View.GONE);
				} else {
					if (progressbar.getVisibility() == View.GONE)
						progressbar.setVisibility(View.VISIBLE);
					progressbar.setProgress(newProgress);
				}
				super.onProgressChanged(view, newProgress);
			}
		}

		@Override
		public void onReceivedTitle(WebView view, String title) {
			super.onReceivedTitle(view, title);
			Log.d("ANDROID_LAB", "TITLE=" + title);
			tvTitle.setText(title);

		}

		// 修改h5页面的弹框样式
		@Override
		public boolean onJsAlert(WebView view, String url, String message,
				JsResult result) {
			// 对alert的简单封装
			new AlertDialog.Builder(WebpageSigningActivity.this)
					.setTitle("提示")
					.setMessage(message)
					.setPositiveButton("确定",
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface arg0,
										int arg1) {
									System.out.println("测试");
								}
							}).create().show();
			result.confirm(); // 处理来自用户的确认回复。
			return true;
		}

		@Override
		public boolean onJsConfirm(WebView view, String url, String message,
				JsResult result) {
			// 对alert的简单封装
			// new AlertDialog.Builder(WebpageSigningActivity.this)
			// .setTitle("提示")
			// .setMessage(message)
			// .setPositiveButton("确定",
			// new DialogInterface.OnClickListener() {
			// @Override
			// public void onClick(DialogInterface arg0,
			// int arg1) {
			// System.out.println("测试1");
			// }
			// }).create().show();
			result.confirm(); // 处理来自用户的确认回复。
			return true;
		}

		@Override
		public boolean onJsPrompt(WebView view, String url, String message,
				String defaultValue, JsPromptResult result) {
			// 对alert的简单封装
			// new AlertDialog.Builder(WebpageSigningActivity.this)
			// .setTitle("提示" + defaultValue)
			// .setMessage(message)
			// .setPositiveButton("确定",
			// new DialogInterface.OnClickListener() {
			// @Override
			// public void onClick(DialogInterface arg0,
			// int arg1) {
			// System.out.println("测试");
			// }
			// }).create().show();
			result.confirm(); // 处理来自用户的确认回复。
			return true;
		}

		@Override
		public void onRequestFocus(WebView view) {
			// TODO Auto-generated method stub
			super.onRequestFocus(view);
		}

		@Override
		public void onShowCustomView(View view, CustomViewCallback callback) {
			// TODO Auto-generated method stub
			super.onShowCustomView(view, callback);
		}

	}

	private class DialogWebViewClient extends WebViewClient {

		@SuppressLint("NewApi")
		@Override
		// 新开页面时用自己定义的webview来显示，不用系统自带的浏览器来显示
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			webView.clearCache(true);
			webView.postUrl(url, null);
			if (url.contains("http")) {
				rlTitle.setVisibility(View.VISIBLE);
			} else {
				rlTitle.setVisibility(View.GONE);
			}
			return super.shouldOverrideUrlLoading(view, url);
		}

		// 加载错误时要做的工作
		@Override
		public void onReceivedError(WebView view, int errorCode,
				String description, String failingUrl) {
			super.onReceivedError(view, errorCode, description, failingUrl);
			Log.d(TAG, "onReceivedError:" + failingUrl.toString()
					+ ",errorCode:" + description);
			view.loadUrl("javascript:document.body.innerHTML=\"" + "" + "\"");
			webView.setVisibility(View.GONE);
			if (null != pbModuleDownload) {
				pbModuleDownload.setVisibility(View.GONE);
			}
			webView.setVerticalScrollBarEnabled(false);
			webView.setHorizontalScrollBarEnabled(false);

			setErrorView(failingUrl);
		}

		@Override
		public void onPageFinished(WebView view, String url) {
			if (null != pbModuleDownload) {
				pbModuleDownload.setVisibility(View.GONE);
			}
			super.onPageFinished(view, url);
		}
	}

	/**
	 * 加载失败点击重新加载
	 * 
	 * @param url
	 */
	private void setErrorView(String url) {
		if (null != pbModuleDownload) {
			pbModuleDownload.setVisibility(View.GONE);
		}
		tvError.setText("提示：加载失败，点击可重新加载。");
		tvError.setEnabled(true);
		tvError.setOnClickListener(WebpageSigningActivity.this);
		tvError.setVisibility(View.VISIBLE);
	}
}
