package com.microsoft.aad.adal;

import android.net.Uri;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;

/* loaded from: classes3.dex */
final class Utility {
    private Utility() {
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static URL constructAuthorityUrl(URL url, String str) throws MalformedURLException {
        return new URL(new Uri.Builder().scheme(url.getProtocol()).authority(str).appendPath(url.getPath().replaceFirst("/", "")).build().toString());
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static Date getImmutableDateObject(Date date) {
        return date != null ? new Date(date.getTime()) : date;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static boolean isClaimsChallengePresent(AuthenticationRequest authenticationRequest) {
        return !StringExtensions.isNullOrBlank(authenticationRequest.getClaimsChallenge());
    }
}
