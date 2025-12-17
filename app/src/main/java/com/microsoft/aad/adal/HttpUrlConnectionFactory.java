package com.microsoft.aad.adal;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

/* loaded from: classes3.dex */
final class HttpUrlConnectionFactory {
    private static HttpURLConnection sMockedConnection;
    private static URL sMockedConnectionOpenUrl;

    private HttpUrlConnectionFactory() {
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static HttpURLConnection createHttpUrlConnection(URL url) throws IOException {
        HttpURLConnection httpURLConnection = sMockedConnection;
        if (httpURLConnection == null) {
            return (HttpURLConnection) url.openConnection();
        }
        sMockedConnectionOpenUrl = url;
        return httpURLConnection;
    }

    static URL getMockedConnectionOpenUrl() {
        return sMockedConnectionOpenUrl;
    }

    static void setMockedHttpUrlConnection(HttpURLConnection httpURLConnection) {
        sMockedConnection = httpURLConnection;
        if (httpURLConnection == null) {
            sMockedConnectionOpenUrl = null;
        }
    }
}
