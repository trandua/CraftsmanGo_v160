package com.mojang.minecraftpe.Webview;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

//import com.craftsman.go.StringFog;
import com.mojang.minecraftpe.MainActivity;
import java.io.PrintStream;

/* loaded from: classes3.dex */
class MinecraftWebViewClient extends WebViewClient {
    private MinecraftWebview mView;

    public MinecraftWebViewClient(MinecraftWebview minecraftWebview) {
        this.mView = minecraftWebview;
    }

    @Override // android.webkit.WebViewClient
    public void onPageStarted(WebView webView, String str, Bitmap bitmap) {
        PrintStream printStream = System.out;
//        printStream.println(StringFog.decrypt("9LFKllZogH7LqkqAS2ODfg==\n", "p8Ur5CIN5F4=\n") + str);
        super.onPageStarted(webView, str, bitmap);
    }

    @Override // android.webkit.WebViewClient
    public void onPageFinished(WebView webView, String str) {
        PrintStream printStream = System.out;
//        printStream.println(StringFog.decrypt("KH/J6ZmE3G1OesjhjoXXbk4=\n", "bhangOrsuQk=\n") + str);
        super.onPageFinished(webView, str);
    }

    @Override // android.webkit.WebViewClient
    public void onReceivedError(WebView webView, WebResourceRequest webResourceRequest, WebResourceError webResourceError) {
//        System.out.println(String.format(StringFog.decrypt("OWpeSLVFbhNcdENGowwlB1xtXkvnQDg=\n", "fBgsJ8dlS2A=\n"), webResourceError.getDescription().toString(), webResourceRequest.getUrl().toString()));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            this.mView.onWebError(webResourceError.getErrorCode(), webResourceError.getDescription().toString());
        }
        super.onReceivedError(webView, webResourceRequest, webResourceError);
    }

    @Override // android.webkit.WebViewClient
    public boolean shouldOverrideUrlLoading(WebView webView, WebResourceRequest webResourceRequest) {
        Uri url = webResourceRequest.getUrl();
        Uri parse = Uri.parse(webView.getUrl());
        if (!webResourceRequest.hasGesture() || parse.getHost().equals(url.getHost())) {
            return super.shouldOverrideUrlLoading(webView, webResourceRequest);
        }
        MainActivity.mInstance.launchUri(webResourceRequest.getUrl().toString());
        return true;
    }
}
