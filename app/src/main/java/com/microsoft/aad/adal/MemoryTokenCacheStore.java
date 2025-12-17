package com.microsoft.aad.adal;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/* loaded from: classes3.dex */
public class MemoryTokenCacheStore implements ITokenCacheStore {
    private static final String TAG = "MemoryTokenCacheStore";
    private static final long serialVersionUID = 3465700945655867086L;
    private final Map<String, TokenCacheItem> mCache = new HashMap();
    private transient Object mCacheLock = new Object();

    private void readObject(ObjectInputStream objectInputStream) throws IOException, ClassNotFoundException {
        objectInputStream.defaultReadObject();
        this.mCacheLock = new Object();
    }

    private void writeObject(ObjectOutputStream objectOutputStream) throws IOException {
        synchronized (this) {
            objectOutputStream.defaultWriteObject();
        }
    }

    @Override // com.microsoft.aad.adal.ITokenCacheStore
    public boolean contains(String str) {
        boolean z;
        if (str != null) {
            Logger.m14612i(TAG, "contains Item from cache.", "Key: " + str);
            synchronized (this.mCacheLock) {
                z = this.mCache.get(str) != null;
            }
            return z;
        }
        throw new IllegalArgumentException();
    }

    @Override // com.microsoft.aad.adal.ITokenCacheStore, com.microsoft.aad.adal.ITokenStoreQuery
    public Iterator<TokenCacheItem> getAll() {
        Iterator<TokenCacheItem> it;
        Logger.m14614v(TAG, "Retrieving all items from cache. ");
        synchronized (this.mCacheLock) {
            it = this.mCache.values().iterator();
        }
        return it;
    }

    @Override // com.microsoft.aad.adal.ITokenCacheStore
    public TokenCacheItem getItem(String str) {
        TokenCacheItem tokenCacheItem;
        if (str != null) {
            Logger.m14612i(TAG, "Get Item from cache. ", "Key:" + str);
            synchronized (this.mCacheLock) {
                tokenCacheItem = this.mCache.get(str);
            }
            return tokenCacheItem;
        }
        throw new IllegalArgumentException("The input key is null.");
    }

    @Override // com.microsoft.aad.adal.ITokenCacheStore
    public void removeAll() {
        Logger.m14614v(TAG, "Remove all items from cache.");
        synchronized (this.mCacheLock) {
            this.mCache.clear();
        }
    }

    @Override // com.microsoft.aad.adal.ITokenCacheStore
    public void removeItem(String str) {
        if (str != null) {
            Logger.m14612i(TAG, "Remove Item from cache. ", "Key:" + str.hashCode());
            synchronized (this.mCacheLock) {
                this.mCache.remove(str);
            }
            return;
        }
        throw new IllegalArgumentException();
    }

    @Override // com.microsoft.aad.adal.ITokenCacheStore
    public void setItem(String str, TokenCacheItem tokenCacheItem) {
        if (tokenCacheItem == null) {
            throw new IllegalArgumentException("item");
        }
        if (str != null) {
            Logger.m14612i(TAG, "Set Item to cache. ", "Key: " + str);
            synchronized (this.mCacheLock) {
                this.mCache.put(str, tokenCacheItem);
            }
            return;
        }
        throw new IllegalArgumentException();
    }
}
