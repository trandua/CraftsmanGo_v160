package com.microsoft.aad.adal;

import android.os.Build;
import android.os.Process;
import com.microsoft.aad.adal.AuthenticationConstants;
import java.io.IOException;
import java.net.URL;
import java.util.Map;
import java.util.UUID;

/* loaded from: classes3.dex */
public class WebRequestHandler implements IWebRequestHandler {
    public static final String HEADER_ACCEPT = "Accept";
    public static final String HEADER_ACCEPT_JSON = "application/json";
    private static final String TAG = "WebRequestHandler";
    private UUID mRequestCorrelationId = null;

    private Map<String, String> updateHeaders(Map<String, String> map) {
        UUID uuid = this.mRequestCorrelationId;
        if (uuid != null) {
            map.put(AuthenticationConstants.AAD.CLIENT_REQUEST_ID, uuid.toString());
        }
        map.put(AuthenticationConstants.AAD.ADAL_ID_PLATFORM, AuthenticationConstants.AAD.ADAL_ID_PLATFORM_VALUE);
        map.put(AuthenticationConstants.AAD.ADAL_ID_VERSION, AuthenticationContext.getVersionName());
        map.put(AuthenticationConstants.AAD.ADAL_ID_OS_VER, "" + Build.VERSION.SDK_INT);
        map.put(AuthenticationConstants.AAD.ADAL_ID_DM, Build.MODEL);
        return map;
    }

    @Override // com.microsoft.aad.adal.IWebRequestHandler
    public HttpWebResponse sendGet(URL url, Map<String, String> map) throws IOException {
        Logger.m14614v(TAG, "WebRequestHandler thread" + Process.myTid());
        return new HttpWebRequest(url, "GET", updateHeaders(map)).send();
    }

    @Override // com.microsoft.aad.adal.IWebRequestHandler
    public HttpWebResponse sendPost(URL url, Map<String, String> map, byte[] bArr, String str) throws IOException {
        Logger.m14614v(TAG, "WebRequestHandler thread" + Process.myTid());
        return new HttpWebRequest(url, "POST", updateHeaders(map), bArr, str).send();
    }

    @Override // com.microsoft.aad.adal.IWebRequestHandler
    public void setRequestCorrelationId(UUID uuid) {
        this.mRequestCorrelationId = uuid;
    }
}
