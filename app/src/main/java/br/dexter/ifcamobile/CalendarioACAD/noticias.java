package br.dexter.ifcamobile.CalendarioACAD;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import br.dexter.ifcamobile.R;
import fr.castorflex.android.smoothprogressbar.SmoothProgressBar;

public class noticias extends AppCompatActivity
{
    private WebView webView;
    private SmoothProgressBar mProgressBar;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_noticias);

        webView = findViewById(R.id.webViewSuite);
        mProgressBar = findViewById(R.id.progressBar);

        ImageView handleBack = findViewById(R.id.BackHandle);
        handleBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient()
        {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon)
            {
                mProgressBar.setVisibility(View.VISIBLE);
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                mProgressBar.setVisibility(View.GONE);
            }

            @Override
            public void onPageFinished(WebView view, String url)
            {
                webView.loadUrl(
                        "javascript:var x = document.getElementsByTagName('header')[0].style.display='none';" +
                        "javascript:var x = document.getElementsByTagName('footer')[0].style.display='none';" +
                        "javascript:var x = document.getElementsByTagName('main')[0].style.display='visible';" +
                        "javascript:var x = document.getElementById('barra-brasil'); x.parentNode.removeChild(x); " +
                        "javascript:var x = document.getElementById('crumbs'); x.parentNode.removeChild(x); " +
                        "javascript:var x = document.getElementById('navigation'); x.parentNode.removeChild(x); "
                );

                webView.loadUrl(
                        "javascript:var con = document.getElementById('em-destaque'); con.parentNode.removeChild(con);" +
                        "javascript:var x = document.getElementById('navigation'); x.parentNode.removeChild(x); "
                );

                mProgressBar.setVisibility(View.GONE);
            }
        });
        webView.loadUrl("https://caxias.ifma.edu.br/categoria/noticias/");
    }

    @Override
    public void onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack();
        } else {
            super.onBackPressed();
        }
    }
}