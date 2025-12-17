package com.microsoft.aad.adal;

import android.net.Uri;
import android.util.Base64;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.StringTokenizer;

/* loaded from: classes3.dex */
final class StringExtensions {
    public static final String ENCODING_UTF8 = "UTF_8";
    private static final String TAG = "StringExtensions";
    private static final String TOKEN_HASH_ALGORITHM = "SHA256";

    private StringExtensions() {
    }

    public static String createHash(String str) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        return !isNullOrBlank(str) ? new String(Base64.encode(MessageDigest.getInstance("SHA256").digest(str.getBytes("UTF_8")), 2), "UTF_8") : str;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static String encodeBase64URLSafeString(byte[] bArr) throws UnsupportedEncodingException {
        return new String(Base64.encode(bArr, 11), "UTF_8");
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static List<String> getStringTokens(String str, String str2) {
        StringTokenizer stringTokenizer = new StringTokenizer(str, str2);
        ArrayList arrayList = new ArrayList();
        while (stringTokenizer.hasMoreTokens()) {
            String nextToken = stringTokenizer.nextToken();
            if (!isNullOrBlank(nextToken)) {
                arrayList.add(nextToken);
            }
        }
        return arrayList;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static URL getUrl(String str) {
        try {
            return new URL(str);
        } catch (MalformedURLException e) {
            Logger.m14610e("StringExtensions:getUrl", ADALError.DEVELOPER_AUTHORITY_IS_NOT_VALID_URL.getDescription(), e.getMessage(), ADALError.DEVELOPER_AUTHORITY_IS_NOT_VALID_URL, e);
            return null;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static HashMap<String, String> getUrlParameters(String str) {
        Uri parse = Uri.parse(str);
        HashMap<String, String> urlFormDecode = HashMapExtensions.urlFormDecode(parse.getFragment());
        return (urlFormDecode == null || urlFormDecode.isEmpty()) ? HashMapExtensions.urlFormDecode(parse.getEncodedQuery()) : urlFormDecode;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static boolean hasPrefixInHeader(String str, String str2) {
        return str.startsWith(str2) && str.length() > str2.length() + 2 && Character.isWhitespace(str.charAt(str2.length()));
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static boolean isNullOrBlank(String str) {
        return str == null || str.trim().length() == 0;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static String removeQuoteInHeaderValue(String str) {
        if (isNullOrBlank(str)) {
            return null;
        }
        return str.replace("\"", "");
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static ArrayList<String> splitWithQuotes(String str, char c) {
        ArrayList<String> arrayList = new ArrayList<>();
        int i = 0;
        boolean z = false;
        for (int i2 = 0; i2 < str.length(); i2++) {
            if (str.charAt(i2) == c && !z) {
                String substring = str.substring(i, i2);
                if (!isNullOrBlank(substring.trim())) {
                    arrayList.add(substring);
                }
                i = i2 + 1;
            } else if (str.charAt(i2) == '\"') {
                z = !z;
            }
        }
        String substring2 = str.substring(i);
        if (!isNullOrBlank(substring2.trim())) {
            arrayList.add(substring2);
        }
        return arrayList;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static String urlFormDecode(String str) throws UnsupportedEncodingException {
        return URLDecoder.decode(str, "UTF_8");
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static String urlFormEncode(String str) throws UnsupportedEncodingException {
        return URLEncoder.encode(str, "UTF_8");
    }
}
