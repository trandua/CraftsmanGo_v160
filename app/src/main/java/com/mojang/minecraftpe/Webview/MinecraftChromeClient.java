package com.mojang.minecraftpe.Webview;

import android.webkit.WebChromeClient;
import android.webkit.WebView;
import com.mojang.minecraftpe.MainActivity;

/* loaded from: classes3.dex */
class MinecraftChromeClient extends WebChromeClient {
    public MinecraftWebview mView;

    public MinecraftChromeClient(MinecraftWebview minecraftWebview) {
        this.mView = minecraftWebview;
    }

    @Override // android.webkit.WebChromeClient
    public void onProgressChanged(WebView webView, int i) {
        super.onProgressChanged(webView, i);
        MainActivity.mInstance.runOnUiThread(new Runnable() { // from class: com.mojang.minecraftpe.Webview.MinecraftChromeClient.1
            @Override // java.lang.Runnable
            public void run() {
                MinecraftChromeClient.this.mView._injectApi();
            }
        });
    }
}
