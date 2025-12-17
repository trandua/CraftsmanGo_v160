package com.googleplay.licensing;

import android.content.Context;
import android.util.Log;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;

/* loaded from: classes2.dex */
public class APKExpansionPolicy implements Policy {
    private static final String DEFAULT_MAX_RETRIES = "0";
    private static final String DEFAULT_RETRY_COUNT = "0";
    private static final String DEFAULT_RETRY_UNTIL = "0";
    private static final String DEFAULT_VALIDITY_TIMESTAMP = "0";
    public static final int MAIN_FILE_URL_INDEX = 0;
    private static final long MILLIS_PER_MINUTE = 60000;
    public static final int PATCH_FILE_URL_INDEX = 1;
    private static final String PREFS_FILE = "com.android.vending.licensing.APKExpansionPolicy";
    private static final String PREF_LAST_RESPONSE = "lastResponse";
    private static final String PREF_MAX_RETRIES = "maxRetries";
    private static final String PREF_RETRY_COUNT = "retryCount";
    private static final String PREF_RETRY_UNTIL = "retryUntil";
    private static final String PREF_VALIDITY_TIMESTAMP = "validityTimestamp";
    private static final String TAG = "APKExpansionPolicy";
    private int mLastResponse;
    private long mMaxRetries;
    private PreferenceObfuscator mPreferences;
    private long mRetryCount;
    private long mRetryUntil;
    private long mValidityTimestamp;
    private long mLastResponseTime = 0;
    private Vector<String> mExpansionURLs = new Vector<>();
    private Vector<String> mExpansionFileNames = new Vector<>();
    private Vector<Long> mExpansionFileSizes = new Vector<>();

    public APKExpansionPolicy(Context context, Obfuscator obfuscator) {
        PreferenceObfuscator preferenceObfuscator = new PreferenceObfuscator(context.getSharedPreferences(PREFS_FILE, 0), obfuscator);
        this.mPreferences = preferenceObfuscator;
        this.mLastResponse = Integer.parseInt(preferenceObfuscator.getString(PREF_LAST_RESPONSE, Integer.toString(Policy.RETRY)));
        this.mValidityTimestamp = Long.parseLong(this.mPreferences.getString(PREF_VALIDITY_TIMESTAMP, "0"));
        this.mRetryUntil = Long.parseLong(this.mPreferences.getString(PREF_RETRY_UNTIL, "0"));
        this.mMaxRetries = Long.parseLong(this.mPreferences.getString(PREF_MAX_RETRIES, "0"));
        this.mRetryCount = Long.parseLong(this.mPreferences.getString(PREF_RETRY_COUNT, "0"));
    }

    private Map<String, String> decodeExtras(String str) {
        HashMap hashMap = new HashMap();
        try {
            for (NameValuePair nameValuePair : URLEncodedUtils.parse(new URI("?" + str), "UTF-8")) {
                String name = nameValuePair.getName();
                int i = 0;
                while (hashMap.containsKey(name)) {
                    StringBuilder sb = new StringBuilder();
                    sb.append(nameValuePair.getName());
                    i++;
                    sb.append(i);
                    name = sb.toString();
                }
                hashMap.put(name, nameValuePair.getValue());
            }
        } catch (URISyntaxException unused) {
            Log.w(TAG, "Invalid syntax error while decoding extras data from server.");
        }
        return hashMap;
    }

    private void setLastResponse(int i) {
        this.mLastResponseTime = System.currentTimeMillis();
        this.mLastResponse = i;
        this.mPreferences.putString(PREF_LAST_RESPONSE, Integer.toString(i));
    }

    private void setMaxRetries(String str) {
        Long l;
        try {
            l = Long.valueOf(Long.parseLong(str));
        } catch (NumberFormatException unused) {
            Log.w(TAG, "Licence retry count (GR) missing, grace period disabled");
            l = 0L;
            str = "0";
        }
        this.mMaxRetries = l.longValue();
        this.mPreferences.putString(PREF_MAX_RETRIES, str);
    }

    private void setRetryCount(long j) {
        this.mRetryCount = j;
        this.mPreferences.putString(PREF_RETRY_COUNT, Long.toString(j));
    }

    private void setRetryUntil(String str) {
        Long l;
        try {
            l = Long.valueOf(Long.parseLong(str));
        } catch (NumberFormatException unused) {
            Log.w(TAG, "License retry timestamp (GT) missing, grace period disabled");
            l = 0L;
            str = "0";
        }
        this.mRetryUntil = l.longValue();
        this.mPreferences.putString(PREF_RETRY_UNTIL, str);
    }

    private void setValidityTimestamp(String str) {
        Long valueOf;
        try {
            valueOf = Long.valueOf(Long.parseLong(str));
        } catch (NumberFormatException unused) {
            Log.w(TAG, "License validity timestamp (VT) missing, caching for a minute");
            valueOf = Long.valueOf(System.currentTimeMillis() + 60000);
            str = Long.toString(valueOf.longValue());
        }
        this.mValidityTimestamp = valueOf.longValue();
        this.mPreferences.putString(PREF_VALIDITY_TIMESTAMP, str);
    }

    @Override // com.googleplay.licensing.Policy
    public boolean allowAccess() {
        long currentTimeMillis = System.currentTimeMillis();
        int i = this.mLastResponse;
        return i == 256 || i != 291 || currentTimeMillis >= this.mLastResponseTime + 60000 || currentTimeMillis <= this.mRetryUntil || this.mRetryCount <= this.mMaxRetries;
    }

    public String getExpansionFileName(int i) {
        if (i < this.mExpansionFileNames.size()) {
            return this.mExpansionFileNames.elementAt(i);
        }
        return null;
    }

    public long getExpansionFileSize(int i) {
        if (i < this.mExpansionFileSizes.size()) {
            return this.mExpansionFileSizes.elementAt(i).longValue();
        }
        return -1L;
    }

    public String getExpansionURL(int i) {
        if (i < this.mExpansionURLs.size()) {
            return this.mExpansionURLs.elementAt(i);
        }
        return null;
    }

    public int getExpansionURLCount() {
        return this.mExpansionURLs.size();
    }

    public long getMaxRetries() {
        return this.mMaxRetries;
    }

    public long getRetryCount() {
        return this.mRetryCount;
    }

    public long getRetryUntil() {
        return this.mRetryUntil;
    }

    public long getValidityTimestamp() {
        return this.mValidityTimestamp;
    }

    @Override // com.googleplay.licensing.Policy
    public void processServerResponse(int i, ResponseData responseData) {
        setRetryCount(i != 291 ? 0L : this.mRetryCount + 1);
        if (i == 256) {
            Map<String, String> decodeExtras = decodeExtras(responseData.extra);
            this.mLastResponse = i;
            setValidityTimestamp(Long.toString(System.currentTimeMillis() + 60000));
            for (String str : decodeExtras.keySet()) {
                if (str.equals("VT")) {
                    setValidityTimestamp(decodeExtras.get(str));
                } else if (str.equals("GT")) {
                    setRetryUntil(decodeExtras.get(str));
                } else if (str.equals("GR")) {
                    setMaxRetries(decodeExtras.get(str));
                } else if (str.startsWith("FILE_URL")) {
                    setExpansionURL(Integer.parseInt(str.substring(8)) - 1, decodeExtras.get(str));
                } else if (str.startsWith("FILE_NAME")) {
                    setExpansionFileName(Integer.parseInt(str.substring(9)) - 1, decodeExtras.get(str));
                } else if (str.startsWith("FILE_SIZE")) {
                    setExpansionFileSize(Integer.parseInt(str.substring(9)) - 1, Long.parseLong(decodeExtras.get(str)));
                }
            }
        } else if (i == 561) {
            setValidityTimestamp("0");
            setRetryUntil("0");
            setMaxRetries("0");
        }
        setLastResponse(i);
        this.mPreferences.commit();
    }

    public void resetPolicy() {
        this.mPreferences.putString(PREF_LAST_RESPONSE, Integer.toString(Policy.RETRY));
        setRetryUntil("0");
        setMaxRetries("0");
        setRetryCount(Long.parseLong("0"));
        setValidityTimestamp("0");
        this.mPreferences.commit();
    }

    public void setExpansionFileName(int i, String str) {
        if (i >= this.mExpansionFileNames.size()) {
            this.mExpansionFileNames.setSize(i + 1);
        }
        this.mExpansionFileNames.set(i, str);
    }

    public void setExpansionFileSize(int i, long j) {
        if (i >= this.mExpansionFileSizes.size()) {
            this.mExpansionFileSizes.setSize(i + 1);
        }
        this.mExpansionFileSizes.set(i, Long.valueOf(j));
    }

    public void setExpansionURL(int i, String str) {
        if (i >= this.mExpansionURLs.size()) {
            this.mExpansionURLs.setSize(i + 1);
        }
        this.mExpansionURLs.set(i, str);
    }
}
