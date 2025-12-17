package com.mojang.minecraftpe.Webview;

import android.os.Build;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowInsets;
import android.view.WindowInsetsController;
import android.webkit.WebSettings;
import android.webkit.WebView;

//import com.craftsman.go.StringFog;
import com.mojang.minecraftpe.MainActivity;
import com.mojang.minecraftpe.PopupView;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;

/* loaded from: classes3.dex */
public class MinecraftWebview {
    private MainActivity mActivity;
    private int mId;
    public WebView mWebView;
    public PopupView mWebViewPopup;

    private native void nativeOnWebError(int i, int i2, String str);

    private native void nativeSendToHost(int i, String str, String str2, String str3);

    public void sendToHost(String str, String str2, String str3) {
        nativeSendToHost(this.mId, str, str2, str3);
    }

    public void onWebError(int i, String str) {
        nativeOnWebError(this.mId, i, str);
    }

    public MinecraftWebview(int i) {
        this.mId = i;
        MainActivity mainActivity = MainActivity.mInstance;
        this.mActivity = mainActivity;
        mainActivity.runOnUiThread(new Runnable() { // from class: com.mojang.minecraftpe.Webview.MinecraftWebview.1
            @Override // java.lang.Runnable
            public void run() {
                MinecraftWebview.this._createWebView();
            }
        });
    }

    public void teardown() {
        this.mWebViewPopup.dismiss();
        this.mWebViewPopup = null;
        this.mWebView = null;
        this.mActivity = null;
        this.mId = -1;
    }

    public void setRect(float f, float f2, float f3, float f4) {
        final int i = (int) f;
        final int i2 = (int) f2;
        final int i3 = (int) f3;
        final int i4 = (int) f4;
        this.mActivity.runOnUiThread(new Runnable() { // from class: com.mojang.minecraftpe.Webview.MinecraftWebview.2
            @Override // java.lang.Runnable
            public void run() {
                MinecraftWebview.this.mWebViewPopup.setRect(i, i2, i3, i4);
                MinecraftWebview.this.mWebViewPopup.update();
            }
        });
    }

    public void setPropagatedAlpha(float f) {
        setShowView(((double) f) == 1.0d);
    }

    public void setUrl(final String str) {
        this.mActivity.runOnUiThread(new Runnable() { // from class: com.mojang.minecraftpe.Webview.MinecraftWebview.3
            @Override // java.lang.Runnable
            public void run() {
                MinecraftWebview.this.mWebView.loadUrl(str);
            }
        });
    }

    public void setShowView(final boolean z) {
        if (z) {
            _hideSystemBars();
        }
        this.mActivity.runOnUiThread(new Runnable() { // from class: com.mojang.minecraftpe.Webview.MinecraftWebview.4
            @Override // java.lang.Runnable
            public void run() {
                String str;
                String str2;
                MinecraftWebview.this.mWebViewPopup.setVisible(z);
                MinecraftWebview minecraftWebview = MinecraftWebview.this;
                Object[] objArr = new Object[1];
//                if (z) {
//                    str = "jv6k5lkn\n";
//                    str2 = "4ZD3jjZQcOM=\n";
//                } else {
//                    str = "AP/mouLw\n";
//                    str2 = "b5Guy4aVXyo=\n";
//                }
                objArr[0] = z? "onShow" : "onHide";
                minecraftWebview.sendToWebView(String.format("window.ipcCodeScreenRenderer.%s();", objArr));
            }
        });
    }

    public void sendToWebView(final String str) {
        this.mActivity.runOnUiThread(new Runnable() { // from class: com.mojang.minecraftpe.Webview.MinecraftWebview.5
            @Override // java.lang.Runnable
            public void run() {
                MinecraftWebview.this.mWebView.evaluateJavascript(str, null);
            }
        });
    }

    public void _injectApi() {
        MainActivity mainActivity = this.mActivity;
        if (mainActivity != null) {
            String _readResource = _readResource(mainActivity.getResources().getIdentifier("code_builder_hosted_editor", "raw", this.mActivity.getPackageName()));
            if (_readResource != null) {
                this.mWebView.evaluateJavascript(_readResource, null);
                return;
            } else {
                onWebError(0, "Unable to inject api");
                return;
            }
        }
        onWebError(0, "_injectApi called after teardown");
    }

    private String _readResource(int i) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        InputStream openRawResource = this.mActivity.getResources().openRawResource(i);
        try {
            byte[] bArr = new byte[256];
            while (true) {
                int read = openRawResource.read(bArr);
                if (read > 0) {
                    byteArrayOutputStream.write(bArr, 0, read);
                } else {
                    openRawResource.close();
                    return byteArrayOutputStream.toString();
                }
            }
        } catch (IOException e) {
            PrintStream printStream = System.out;
//            printStream.println(StringFog.decrypt("ITlzoZv9xF4IeGion/3EWAIrdbiM+oEK\n", "Z1gazf6Z5Co=\n") + i + StringFog.decrypt("pMs0t+1OTwD20y/j\n", "hLxdw4VuKnI=\n") + e.toString());
            return null;
        }
    }

    private Boolean _hideSystemBars(View view) {
        if (view == null) {
            return false;
        }
        if (Build.VERSION.SDK_INT >= 30) {
            WindowInsetsController windowInsetsController = view.getWindowInsetsController();
            if (windowInsetsController == null) {
                return false;
            }
            windowInsetsController.setSystemBarsBehavior(WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE);
            windowInsetsController.hide(WindowInsets.Type.systemBars());
            return true;
        }
        view.setSystemUiVisibility(5894);
        return true;
    }

    private void _hideSystemBars() {
        MainActivity mainActivity = this.mActivity;
        if (mainActivity != null) {
            mainActivity.runOnUiThread(new Runnable() { // from class: com.mojang.minecraftpe.Webview.MinecraftWebview.6
                @Override // java.lang.Runnable
                public final void run() {
                    MinecraftWebview.this.lambda$_hideSystemBars$1$MinecraftWebview();
                }
            });
        }
    }

    public void lambda$_hideSystemBars$1$MinecraftWebview() {
        WebView webView = this.mWebView;
        if (webView == null || _hideSystemBars(webView).booleanValue()) {
            return;
        }
        new Thread(new Runnable() { // from class: com.mojang.minecraftpe.Webview.MinecraftWebview.7
            @Override // java.lang.Runnable
            public final void run() {
                MinecraftWebview.this.lambda$null$0$MinecraftWebview();
            }
        }).start();
    }

    public void lambda$null$0$MinecraftWebview() {
        try {
            Thread.sleep(50L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        _hideSystemBars();
    }

    public void _createWebView() {
        if (!MainActivity.mInstance.isPublishBuild()) {
            WebView.setWebContentsDebuggingEnabled(true);
        }
        WebView webView = new WebView(this.mActivity);
        this.mWebView = webView;
        webView.setLayoutParams(new ViewGroup.LayoutParams(-1, -1));
        this.mWebView.setWebViewClient(new MinecraftWebViewClient(this));
        this.mWebView.setWebChromeClient(new MinecraftChromeClient(this));
        this.mWebView.addJavascriptInterface(new WebviewHostInterface(this), "codeBuilderHostInterface");
        WebSettings settings = this.mWebView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        this.mWebViewPopup = new PopupView(this.mActivity);
        View rootView = this.mActivity.findViewById(android.R.id.content).getRootView();
        this.mWebViewPopup.setContentView(this.mWebView);
        this.mWebViewPopup.setParentView(rootView);
    }
}
