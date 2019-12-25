package com.solleftea24;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.webkit.DownloadListener;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.onesignal.OneSignal;

import im.delight.android.webview.AdvancedWebView;

public class MainActivity extends Activity implements AdvancedWebView.Listener {

    private AdvancedWebView mWebView;
    public ProgressBar progressBar;
    String loadUrl;
    //View ll_pView, pView;
    //SwipeRefreshLayout mSwipeRefreshLayout;
    //ProgressDialog progressBar;

    private static final String URL = "https://solleftea24.se/";

    View ivSplash, ivError;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);




        isOnline();
        if (!isOnline()){try {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Info");
            builder.setIcon(android.R.drawable.ic_dialog_alert);

            builder.setMessage("Internet not available, Cross check your internet connectivity and try again")
                    .setCancelable(false)
                    .setPositiveButton("Exit", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            Intent homeIntent = new Intent(Intent.ACTION_MAIN);
                            homeIntent.addCategory( Intent.CATEGORY_HOME );
                            homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(homeIntent);
                        }
                    })
                    .setNegativeButton("Retry", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            Intent intent = new Intent(MainActivity.this,MainActivity.class);
                            startActivity(intent);
                        }
                    });
            AlertDialog alert = builder.create();
            alert.show();
//            AlertDialog alertDialog = new AlertDialog.Builder(ErrorActivity.this).create();
//
//            alertDialog.setTitle("Info");
//            alertDialog.setMessage("Internet not available, Cross check your internet connectivity and try again");
//            alertDialog.setIcon(android.R.drawable.ic_dialog_alert);
//            alertDialog.setButton("Exit", new DialogInterface.OnClickListener() {
//                public void onClick(DialogInterface dialog, int which) {
//                    Intent homeIntent = new Intent(Intent.ACTION_MAIN);
//                    homeIntent.addCategory( Intent.CATEGORY_HOME );
//                    homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                    startActivity(homeIntent);
//
//                }
//            });
//
//            alertDialog.show();

        }catch (Exception e){
            e.printStackTrace();
        }
        }

        OneSignal.startInit(this).init();

        loadUrl = URL;

        progressBar = (ProgressBar)findViewById(R.id.prg);

        //ll_pView = (View) findViewById(R.id.ll_pView);

       // pView = (View) findViewById(R.id.pView);

        ivSplash = findViewById(R.id.ivSplash);
        ivError = findViewById(R.id.ivError);



        mWebView = (AdvancedWebView) findViewById(R.id.webView);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setPluginState(WebSettings.PluginState.ON);
        //mWebView.getSettings().setLoadWithOverviewMode(true);
        //mWebView.getSettings().setUseWideViewPort(true);
        //mWebView.getSettings().setBuiltInZoomControls(true);

        mWebView.setListener(this, this);
        mWebView.setDownloadListener(new DownloadListener() {
            public void onDownloadStart(String url, String userAgent,
                                        String contentDisposition, String mimetype,
                                        long contentLength) {
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            }
        });

        mWebView.setWebChromeClient(new WebChromeClient() {


            @Override
            public void onProgressChanged(WebView view, int progress) {
               // LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(pView.getLayoutParams());
               // lp.weight = progress;
               // pView.setLayoutParams(lp);

               // ll_pView.setVisibility(progress == 100 ? View.GONE : View.VISIBLE);


//                if (progress == 100)
//                    progressBar.dismiss();
//                else
//                    progressBar.show();

               checkNavigations();
            }

        //mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.activity_main_swipe_refresh_layout);

        //mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
           // @Override
            public void onRefresh() {
                mWebView.reload();
            }
        });

        this.mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                progressBar.setVisibility(view.GONE);

                //ll_pView.setVisibility(View.GONE);
                //mSwipeRefreshLayout.setRefreshing(false);
                //progressBar.dismiss();
                checkNavigations();
                super.onPageFinished(view, url);
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                progressBar.setVisibility(view.VISIBLE );
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                handleURL(url);
                return true;
            }
        });

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    mWebView.loadUrl(loadUrl);
                    //progressBar = ProgressDialog.show(MainActivity.this, "", "Loading...");
                } catch (Exception e) {
                    e.printStackTrace();
                }
                ivSplash.setVisibility(View.GONE);
            }
        }, 3000);


        (new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    OnEverySecond();

                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        })).start();
    }

    public boolean IsNetworkAvailable() {
        try {
            ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo info = cm.getActiveNetworkInfo();
            // boolean isWiFi = info.getType() == ConnectivityManager.TYPE_WIFI;
            return info != null && info.getState() == NetworkInfo.State.CONNECTED;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public void OnEverySecond() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (IsNetworkAvailable()) {
                    ivError.setVisibility(View.GONE);
                } else {
                    ivError.setVisibility(View.VISIBLE);
                }
            }
        });
    }


    public void handleURL(String url) {
        if (url.startsWith("tel:") || url.startsWith("geo:") || url.startsWith("mailto:")) {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(intent);
            return;
        } else
            mWebView.loadUrl(url);
        checkNavigations();
    }

    private void checkNavigations() {
        //btnBack.setVisibility(mWebView.canGoBack() ? View.VISIBLE : View.GONE);
        //btnForward.setVisibility(mWebView.canGoForward() ? View.VISIBLE : View.GONE);
    }

    @SuppressLint("NewApi")
    @Override
    protected void onResume() {
        super.onResume();
        mWebView.onResume();
        // ...
    }

    @SuppressLint("NewApi")
    @Override
    protected void onPause() {
        mWebView.onPause();
        // ...
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        mWebView.onDestroy();
        // ...
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        mWebView.onActivityResult(requestCode, resultCode, intent);
        // ...
    }

    @Override
    public void onPageStarted(String url, Bitmap favicon) {
    }

    @Override
    public void onPageFinished(String url) {

    }

    @Override
    public void onPageError(int errorCode, String description, String failingUrl) {
    }

    @Override
    public void onDownloadRequested(String url, String suggestedFilename, String mimeType, long contentLength, String contentDisposition, String userAgent) {
    }

    @Override
    public void onExternalPageRequest(String url) {
    }

    @Override
    public void onBackPressed() {
        if (mWebView.canGoBack()) {
            mWebView.goBack();
        } else {
            super.onBackPressed();


        }
    }

    protected boolean isOnline(){
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if(netInfo != null && netInfo.isConnectedOrConnecting()){
            return true;
        }
        else{
            return false;
        }

    }
}


