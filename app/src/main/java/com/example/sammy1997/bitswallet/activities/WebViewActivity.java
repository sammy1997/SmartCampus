package com.example.sammy1997.bitswallet.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.example.sammy1997.bitswallet.R;
import com.example.sammy1997.bitswallet.WalletActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class WebViewActivity extends AppCompatActivity {

    DatabaseReference reference;
    String url;
    Activity activity;
    WebView webView;
    SharedPreferences preferences;
    ProgressDialog progress;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);
        String url = getIntent().getExtras().getString("url");
        activity = this;
        preferences = getApplicationContext().getSharedPreferences("details",MODE_PRIVATE);
        webView = findViewById(R.id.webView);
        webView.setWebViewClient(new WebViewClient());
        reference = FirebaseDatabase.getInstance().getReference();
        String walletID = getApplicationContext().getSharedPreferences("details",
                Context.MODE_PRIVATE).getString("walletID","");

        final String balance = getApplicationContext().getSharedPreferences("details",
                Context.MODE_PRIVATE).getString("balance","");


        webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        webView.setWebChromeClient(new WebChromeClient());
        webView.getSettings().setJavaScriptEnabled(true);

        progress = ProgressDialog.show(this, "Loading.....",
                "Please wait.", true);

        ProgressBar spinner = new android.widget.ProgressBar(WebViewActivity.this, null,android.R.attr.progressBarStyle);
        spinner.getIndeterminateDrawable().setColorFilter(Color.parseColor("#FF4081"), android.graphics.PorterDuff.Mode.SRC_IN);
        progress.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        progress.setContentView(spinner);
        progress.setCancelable(false);
        progress.show();

        webView.setWebViewClient(new WebViewClient() {

            public void onPageFinished(WebView view, String url) {
                if (WebViewActivity.this.isDestroyed()) {
                    return;
                }
                if (progress != null)
                    progress.dismiss();
            }

            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                Log.w("WebActivity", "Error loading page " + description);
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                return false;
            }
        });

        reference.child("wallet").child(walletID).child("wallet").child("curr_balance").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(!balance.equals(dataSnapshot.getValue() + "")){
                    if (activity!=null){
                        if (preferences.getBoolean("to_be_ordered",false)){
                            Intent intent = new Intent(activity, OrderPlaceIntermediateActivity.class);
                            startActivity(intent);
                            finish();
                            overridePendingTransition(R.anim.fade_in,R.anim.fade_out);
                        }else {
                            Intent intent =new Intent(activity,WalletActivity.class);
                            startActivity(intent);
                            finish();
                            overridePendingTransition(R.anim.fade_in,R.anim.fade_out);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        webView.loadUrl(url);


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (progress != null && progress.isShowing())
            progress.dismiss();
    }

    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if ((keyCode == KeyEvent.KEYCODE_BACK) && webView.canGoBack()) {
            webView.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
