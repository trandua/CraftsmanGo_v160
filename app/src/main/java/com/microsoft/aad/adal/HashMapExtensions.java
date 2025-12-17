package com.microsoft.aad.adal;

import android.text.TextUtils;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes3.dex */
public final class HashMapExtensions {
    private static final String TAG = "HashMapExtensions";

    private HashMapExtensions() {
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static Map<String, String> getJsonResponse(HttpWebResponse httpWebResponse) throws JSONException {
        HashMap hashMap = new HashMap();
        if (httpWebResponse != null && !TextUtils.isEmpty(httpWebResponse.getBody())) {
            JSONObject jSONObject = new JSONObject(httpWebResponse.getBody());
            Iterator<String> keys = jSONObject.keys();
            while (keys.hasNext()) {
                String next = keys.next();
                hashMap.put(next, jSONObject.getString(next));
            }
        }
        return hashMap;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static HashMap<String, String> jsonStringAsMap(String str) throws JSONException {
        HashMap<String, String> hashMap = new HashMap<>();
        if (!StringExtensions.isNullOrBlank(str)) {
            JSONObject jSONObject = new JSONObject(str);
            Iterator<String> keys = jSONObject.keys();
            while (keys.hasNext()) {
                String next = keys.next();
                hashMap.put(next, jSONObject.getString(next));
            }
        }
        return hashMap;
    }

    static HashMap<String, List<String>> jsonStringAsMapList(String str) throws JSONException {
        HashMap<String, List<String>> hashMap = new HashMap<>();
        if (!StringExtensions.isNullOrBlank(str)) {
            JSONObject jSONObject = new JSONObject(str);
            Iterator<String> keys = jSONObject.keys();
            while (keys.hasNext()) {
                String next = keys.next();
                ArrayList arrayList = new ArrayList();
                JSONArray jSONArray = new JSONArray(jSONObject.getString(next));
                for (int i = 0; i < jSONArray.length(); i++) {
                    arrayList.add(jSONArray.get(i).toString());
                }
                hashMap.put(next, arrayList);
            }
        }
        return hashMap;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static HashMap<String, String> urlFormDecode(String str) {
        return urlFormDecodeData(str, "&");
    }

    static HashMap<String, String> urlFormDecodeData(String str, String str2) {
        String str3;
        HashMap<String, String> hashMap = new HashMap<>();
        if (!StringExtensions.isNullOrBlank(str)) {
            StringTokenizer stringTokenizer = new StringTokenizer(str, str2);
            String str4 = null;
            while (stringTokenizer.hasMoreTokens()) {
                String[] split = stringTokenizer.nextToken().split("=");
                if (split.length == 2) {
                    try {
                        str3 = StringExtensions.urlFormDecode(split[0].trim());
                        str4 = StringExtensions.urlFormDecode(split[1].trim());
                    } catch (UnsupportedEncodingException e) {
                        Logger.m14613i(TAG, ADALError.ENCODING_IS_NOT_SUPPORTED.getDescription(), e.getMessage(), null);
                        str3 = null;
                    }
                } else if (split.length == 1) {
                    try {
                        str3 = StringExtensions.urlFormDecode(split[0].trim());
                        str4 = "";
                    } catch (UnsupportedEncodingException e2) {
                        throw new RuntimeException(e2);
                    }
                } else {
                    str4 = null;
                    str3 = null;
                }
                if (!StringExtensions.isNullOrBlank(str3)) {
                    hashMap.put(str3, str4);
                }
            }
        }
        return hashMap;
    }
}
