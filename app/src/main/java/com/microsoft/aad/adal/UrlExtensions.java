package com.microsoft.aad.adal;

import java.net.URL;
import java.util.Locale;

/* loaded from: classes3.dex */
final class UrlExtensions {
    private UrlExtensions() {
    }

    public static boolean isADFSAuthority(URL url) {
        String path = url.getPath();
        return !StringExtensions.isNullOrBlank(path) && path.toLowerCase(Locale.ENGLISH).equals("/adfs");
    }
}
