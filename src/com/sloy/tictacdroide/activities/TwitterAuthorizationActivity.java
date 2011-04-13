package com.sloy.tictacdroide.activities;

import com.sloy.tictacdroide.R;
import com.sloy.tictacdroide.components.ApplicationController;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

public class TwitterAuthorizationActivity extends Activity {

	  private ApplicationController app;
	  private WebView webView;
	  
	  private WebViewClient webViewClient = new WebViewClient() {
	    @Override
	    public void onLoadResource(WebView view, String url) {
	      // the URL we're looking for looks like this:
	      // http://otweet.com/authenticated?oauth_token=1234567890qwertyuiop
	      Uri uri = Uri.parse(url);
	      Log.d("tictacdroide", uri.getHost());
	      if (uri.getHost().equals("otweet.com")) {
	        String token = uri.getQueryParameter("oauth_token");
	        if (null != token) {
	          webView.setVisibility(View.INVISIBLE);
	          app.authorizedTwitter();
	          finish();
	        } else {
	          Toast.makeText(getApplicationContext(), "Error, try again...", Toast.LENGTH_SHORT).show();
	        }
	      } else {
	        super.onLoadResource(view, url);
	      }
	    }
	  };

	  @Override
	  protected void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    app = (ApplicationController)getApplication();
	    setContentView(R.layout.authorization_view);
	    setUpViews();
	  }
	  
	  @Override
	  protected void onResume() {
	    super.onResume();
	    String authURL = app.beginTwitterAuthorization();
	    webView.loadUrl(authURL);
	  }

	  private void setUpViews() {
	    webView = (WebView)findViewById(R.id.web_view);
	    webView.setWebViewClient(webViewClient);
	  }

	}
