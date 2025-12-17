package com.microsoft.aad.adal;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/* loaded from: classes3.dex */
public class DefaultTokenCacheStore implements ITokenCacheStore, ITokenStoreQuery {
    private static final Object LOCK = new Object();
    private static final String SHARED_PREFERENCE_NAME = "com.microsoft.aad.adal.cache";
    private static final String TAG = "DefaultTokenCacheStore";
    private static final int TOKEN_VALIDITY_WINDOW = 10;
    private static StorageHelper sHelper = null;
    private static final long serialVersionUID = 1;
    private Context mContext;
    private Gson mGson = new GsonBuilder().registerTypeAdapter(Date.class, new DateTimeAdapter()).create();
    private SharedPreferences mPrefs;

    public DefaultTokenCacheStore(Context context) {
        if (context != null) {
            this.mContext = context;
            if (!StringExtensions.isNullOrBlank(AuthenticationSettings.INSTANCE.getSharedPrefPackageName())) {
                try {
                    this.mContext = context.createPackageContext(AuthenticationSettings.INSTANCE.getSharedPrefPackageName(), 0);
                } catch (PackageManager.NameNotFoundException unused) {
                    throw new IllegalArgumentException("Package name:" + AuthenticationSettings.INSTANCE.getSharedPrefPackageName() + " is not found");
                }
            }
            SharedPreferences sharedPreferences = this.mContext.getSharedPreferences(SHARED_PREFERENCE_NAME, 0);
            this.mPrefs = sharedPreferences;
            if (sharedPreferences != null) {
                validateSecretKeySetting();
                return;
            }
            throw new IllegalStateException(ADALError.DEVICE_SHARED_PREF_IS_NOT_AVAILABLE.getDescription());
        }
        throw new IllegalArgumentException("Context is null");
    }

    private String decrypt(String str, String str2) {
        if (!StringExtensions.isNullOrBlank(str)) {
            try {
                return getStorageHelper().decrypt(str2);
            } catch (IOException | GeneralSecurityException e) {
                Logger.m14610e(TAG, "Decryption failure. ", "", ADALError.DECRYPTION_FAILED, e);
                removeItem(str);
                return null;
            }
        }
        throw new IllegalArgumentException("key is null or blank");
    }

    private String encrypt(String str) {
        try {
            return getStorageHelper().encrypt(str);
        } catch (IOException | GeneralSecurityException e) {
            Logger.m14610e(TAG, "Encryption failure. ", "", ADALError.ENCRYPTION_FAILED, e);
            return null;
        }
    }

    private static Calendar getTokenValidityTime() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(13, 10);
        return calendar;
    }

    private boolean isAboutToExpire(Date date) {
        return date != null && date.before(getTokenValidityTime().getTime());
    }

    private void validateSecretKeySetting() {
        AuthenticationSettings.INSTANCE.getSecretKeyData();
    }

    @Override // com.microsoft.aad.adal.ITokenStoreQuery
    public void clearTokensForUser(String str) {
        for (TokenCacheItem tokenCacheItem : getTokensForUser(str)) {
            if (tokenCacheItem.getUserInfo() != null && tokenCacheItem.getUserInfo().getUserId() != null && tokenCacheItem.getUserInfo().getUserId().equalsIgnoreCase(str)) {
                try {
                    removeItem(CacheKey.createCacheKey(tokenCacheItem));
                } catch (AuthenticationException e) {
                    Logger.m14610e(TAG, "Fail to create cache key. ", "", e.getCode(), e);
                }
            }
        }
    }

    @Override // com.microsoft.aad.adal.ITokenCacheStore
    public boolean contains(String str) {
        if (str != null) {
            return this.mPrefs.contains(str);
        }
        throw new IllegalArgumentException();
    }

    @Override // com.microsoft.aad.adal.ITokenCacheStore, com.microsoft.aad.adal.ITokenStoreQuery
    public Iterator<TokenCacheItem> getAll() {
        Map<String, ?> all = this.mPrefs.getAll();
        ArrayList arrayList = new ArrayList(all.values().size());
        for (Map.Entry<String, ?> entry : all.entrySet()) {
            String decrypt = decrypt(entry.getKey(), (String) entry.getValue());
            if (decrypt != null) {
                arrayList.add((TokenCacheItem) this.mGson.fromJson(decrypt, TokenCacheItem.class));
            }
        }
        return arrayList.iterator();
    }

    @Override // com.microsoft.aad.adal.ITokenCacheStore
    public TokenCacheItem getItem(String str) {
        String decrypt;
        if (str == null) {
            throw new IllegalArgumentException("The key is null.");
        }
        if (!this.mPrefs.contains(str) || (decrypt = decrypt(str, this.mPrefs.getString(str, ""))) == null) {
            return null;
        }
        return (TokenCacheItem) this.mGson.fromJson(decrypt, TokenCacheItem.class);
    }

    public StorageHelper getStorageHelper() {
        synchronized (LOCK) {
            if (sHelper == null) {
                Logger.m14614v(TAG, "Started to initialize storage helper");
                sHelper = new StorageHelper(this.mContext);
                Logger.m14614v(TAG, "Finished to initialize storage helper");
            }
        }
        return sHelper;
    }

    @Override // com.microsoft.aad.adal.ITokenStoreQuery
    public List<TokenCacheItem> getTokensAboutToExpire() {
        Iterator<TokenCacheItem> all = getAll();
        ArrayList arrayList = new ArrayList();
        while (all.hasNext()) {
            TokenCacheItem next = all.next();
            if (isAboutToExpire(next.getExpiresOn())) {
                arrayList.add(next);
            }
        }
        return arrayList;
    }

    @Override // com.microsoft.aad.adal.ITokenStoreQuery
    public List<TokenCacheItem> getTokensForResource(String str) {
        Iterator<TokenCacheItem> all = getAll();
        ArrayList arrayList = new ArrayList();
        while (all.hasNext()) {
            TokenCacheItem next = all.next();
            if (str.equals(next.getResource())) {
                arrayList.add(next);
            }
        }
        return arrayList;
    }

    @Override // com.microsoft.aad.adal.ITokenStoreQuery
    public List<TokenCacheItem> getTokensForUser(String str) {
        Iterator<TokenCacheItem> all = getAll();
        ArrayList arrayList = new ArrayList();
        while (all.hasNext()) {
            TokenCacheItem next = all.next();
            if (next.getUserInfo() != null && next.getUserInfo().getUserId().equalsIgnoreCase(str)) {
                arrayList.add(next);
            }
        }
        return arrayList;
    }

    @Override // com.microsoft.aad.adal.ITokenStoreQuery
    public Set<String> getUniqueUsersWithTokenCache() {
        Iterator<TokenCacheItem> all = getAll();
        HashSet hashSet = new HashSet();
        while (all.hasNext()) {
            TokenCacheItem next = all.next();
            if (next.getUserInfo() != null && !hashSet.contains(next.getUserInfo().getUserId())) {
                hashSet.add(next.getUserInfo().getUserId());
            }
        }
        return hashSet;
    }

    @Override // com.microsoft.aad.adal.ITokenCacheStore
    public void removeAll() {
        SharedPreferences.Editor edit = this.mPrefs.edit();
        edit.clear();
        edit.apply();
    }

    @Override // com.microsoft.aad.adal.ITokenCacheStore
    public void removeItem(String str) {
        if (str == null) {
            throw new IllegalArgumentException();
        }
        if (this.mPrefs.contains(str)) {
            SharedPreferences.Editor edit = this.mPrefs.edit();
            edit.remove(str);
            edit.apply();
        }
    }

    @Override // com.microsoft.aad.adal.ITokenCacheStore
    public void setItem(String str, TokenCacheItem tokenCacheItem) {
        if (str == null) {
            throw new IllegalArgumentException();
        }
        if (tokenCacheItem != null) {
            String encrypt = encrypt(this.mGson.toJson(tokenCacheItem));
            if (encrypt != null) {
                SharedPreferences.Editor edit = this.mPrefs.edit();
                edit.putString(str, encrypt);
                edit.apply();
                return;
            }
            Logger.m14609e(TAG, "Encrypted output is null. ", "", ADALError.ENCRYPTION_FAILED);
            return;
        }
        throw new IllegalArgumentException("item");
    }
}
