package com.mojang.minecraftpe.Webview;

import android.webkit.JavascriptInterface;

//import com.craftsman.go.StringFog;

import java.io.PrintStream;

/* loaded from: classes3.dex */
class WebviewHostInterface {
    private MinecraftWebview mView;

    public WebviewHostInterface(MinecraftWebview minecraftWebview) {
        this.mView = minecraftWebview;
    }

    @JavascriptInterface
    public void sendToHost(String str, String str2) {
        sendToHost(str, str2, "");
    }

    @JavascriptInterface
    public void sendToHost(String str, String str2, String str3) {
        PrintStream printStream = System.out;
//        printStream.println(StringFog.decrypt("nY8mZLk+sPi9nmg=\n", "zupIAO1R+Jc=\n") + str + StringFog.decrypt("Ffc=\n", "OdfKHixudbw=\n") + str2 + StringFog.decrypt("ubg=\n", "lZi2PHDQQ0A=\n") + str3);
        this.mView.sendToHost(str, str2, str3);
    }
}
