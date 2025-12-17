package com.microsoft.xbox.idp.util;

import android.net.Uri;
import android.text.TextUtils;
import com.microsoft.aad.adal.WebRequestHandler;

/* loaded from: classes3.dex */
public class HttpUtil {

    /* loaded from: classes3.dex */
    public enum ImageSize {
        SMALL(64, 64),
        MEDIUM(208, 208),
        LARGE(424, 424);
        
        public final int f12607h;
        public final int f12608w;

        ImageSize(int i, int i2) {
            this.f12608w = i;
            this.f12607h = i2;
        }
    }

    public static HttpCall appendCommonParameters(HttpCall httpCall, String str) {
        httpCall.setXboxContractVersionHeaderValue(str);
        httpCall.setContentTypeHeaderValue(WebRequestHandler.HEADER_ACCEPT_JSON);
        httpCall.setRetryAllowed(true);
        return httpCall;
    }

    public static String getEndpoint(Uri uri) {
        return uri.getScheme() + "://" + uri.getEncodedAuthority();
    }

    public static Uri.Builder getImageSizeUrlParams(Uri.Builder builder, ImageSize imageSize) {
        return builder.appendQueryParameter("w", Integer.toString(imageSize.f12608w)).appendQueryParameter("h", Integer.toString(imageSize.f12607h));
    }

    public static String getPathAndQuery(Uri uri) {
        String encodedPath = uri.getEncodedPath();
        String encodedQuery = uri.getEncodedQuery();
        String encodedFragment = uri.getEncodedFragment();
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append(encodedPath);
        if (!TextUtils.isEmpty(encodedQuery)) {
            stringBuffer.append("?");
            stringBuffer.append(encodedQuery);
        }
        if (!TextUtils.isEmpty(encodedFragment)) {
            stringBuffer.append("#");
            stringBuffer.append(encodedFragment);
        }
        return stringBuffer.toString();
    }
}
