package com.googleplay.licensing;

import android.content.Context;
import android.util.Log;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;

/* loaded from: classes2.dex */
public class ServerManagedPolicy implements Policy {
    private static final String DEFAULT_MAX_RETRIES = "0";
    private static final String DEFAULT_RETRY_COUNT = "0";
    private static final String DEFAULT_RETRY_UNTIL = "0";
    private static final String DEFAULT_VALIDITY_TIMESTAMP = "0";
    private static final long MILLIS_PER_MINUTE = 60000;
    private static final String PREFS_FILE = "com.android.vending.licensing.ServerManagedPolicy";
    private static final String PREF_LAST_RESPONSE = "lastResponse";
    private static final String PREF_MAX_RETRIES = "maxRetries";
    private static final String PREF_RETRY_COUNT = "retryCount";
    private static final String PREF_RETRY_UNTIL = "retryUntil";
    private static final String PREF_VALIDITY_TIMESTAMP = "validityTimestamp";
    private static final String TAG = "ServerManagedPolicy";
    private static boolean isServerResponse;
    private int mLastResponse;
    private long mMaxRetries;
    private PreferenceObfuscator mPreferences;
    private long mRetryCount;
    private long mRetryUntil;
    private long mValidityTimestamp;
    private long mOriginalVT = 60000;
    private long mOriginalGT = 0;
    private long mOriginalRetries = 0;
    private long mLastResponseTime = 0;

    public ServerManagedPolicy(Context context, Obfuscator obfuscator) {
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
                hashMap.put(nameValuePair.getName(), nameValuePair.getValue());
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
            this.mOriginalRetries = l.longValue();
        } catch (NumberFormatException unused) {
            Log.w(TAG, "Licence retry count (GR) missing, grace period disabled");
            l = 0L;
            str = "0";
        }
        Log.i(TAG, String.format("license check retries = %d", l));
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
            this.mOriginalGT = l.longValue() - System.currentTimeMillis();
        } catch (NumberFormatException unused) {
            Log.w(TAG, "License retry timestamp (GT) missing, grace period disabled");
            l = 0L;
            str = "0";
        }
        Log.i(TAG, String.format("license retry until timestamp = %d", l));
        this.mRetryUntil = l.longValue();
        this.mPreferences.putString(PREF_RETRY_UNTIL, str);
    }

    private void setValidityTimestamp(String str) {
        Long valueOf;
        try {
            valueOf = Long.valueOf(Long.parseLong(str));
            this.mOriginalVT = valueOf.longValue() - System.currentTimeMillis();
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
        if (i != 256) {
            return i != 291 || currentTimeMillis >= this.mLastResponseTime + 60000 || currentTimeMillis <= this.mRetryUntil || this.mRetryCount <= this.mMaxRetries;
        } else if (currentTimeMillis <= this.mValidityTimestamp) {
            Log.i(TAG, isServerResponse ? "Server license response" : "Cached license response");
            return true;
        } else {
            return true;
        }
    }

    public long[] getExtraLicenseData() {
        return new long[]{this.mOriginalVT, this.mOriginalGT, this.mOriginalRetries};
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
        String str;
        isServerResponse = true;
        setRetryCount(i != 291 ? 0L : this.mRetryCount + 1);
        if (i != 256) {
            if (i == 561) {
                str = "0";
                setValidityTimestamp("0");
                setRetryUntil("0");
            }
            setLastResponse(i);
            this.mPreferences.commit();
        }
        Map<String, String> decodeExtras = decodeExtras(responseData.extra);
        this.mLastResponse = i;
        setValidityTimestamp(decodeExtras.get("VT"));
        setRetryUntil(decodeExtras.get("GT"));
        str = decodeExtras.get("GR");
        setMaxRetries(str);
        setLastResponse(i);
        this.mPreferences.commit();
    }
}
