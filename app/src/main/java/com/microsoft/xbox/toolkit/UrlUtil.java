package com.microsoft.xbox.toolkit;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

/* loaded from: classes3.dex */
public class UrlUtil {
    public static boolean UrisEqualCaseInsensitive(URI uri, URI uri2) {
        if (uri == uri2) {
            return true;
        }
        if (uri == null || uri2 == null) {
            return false;
        }
        return JavaUtil.stringsEqualCaseInsensitive(uri.toString(), uri2.toString());
    }

    public static String encodeUrl(String str) {
        URI encodedUri;
        if (str == null || str.length() == 0 || (encodedUri = getEncodedUri(str)) == null) {
            return null;
        }
        return encodedUri.toString();
    }

    public static URI getEncodedUri(String str) {
        if (str == null || str.length() == 0) {
            return null;
        }
        return getEncodedUriNonNull(str);
    }

    public static URI getEncodedUriNonNull(String str) {
        try {
            URL url = new URL(str);
            return new URI(url.getProtocol(), url.getUserInfo(), url.getHost(), url.getPort(), url.getPath(), url.getQuery(), url.getRef());
        } catch (MalformedURLException | URISyntaxException unused) {
            return null;
        }
    }

    public static URI getUri(String str) {
        if (JavaUtil.isNullOrEmpty(str)) {
            return null;
        }
        try {
            return new URI(str);
        } catch (Exception unused) {
            return null;
        }
    }
}
