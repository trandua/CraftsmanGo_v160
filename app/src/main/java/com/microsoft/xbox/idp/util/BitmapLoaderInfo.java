package com.microsoft.xbox.idp.util;

import android.app.LoaderManager;
import com.microsoft.xbox.idp.toolkit.BitmapLoader;
import com.microsoft.xbox.idp.util.ErrorHelper;

/* loaded from: classes3.dex */
public class BitmapLoaderInfo implements ErrorHelper.LoaderInfo {
    private final LoaderManager.LoaderCallbacks<?> callbacks;

    public BitmapLoaderInfo(LoaderManager.LoaderCallbacks<?> loaderCallbacks) {
        this.callbacks = loaderCallbacks;
    }

    @Override // com.microsoft.xbox.idp.util.ErrorHelper.LoaderInfo
    public void clearCache(Object obj) {
        BitmapLoader.Cache bitmapCache = CacheUtil.getBitmapCache();
        synchronized (bitmapCache) {
            bitmapCache.remove(obj);
        }
    }

    @Override // com.microsoft.xbox.idp.util.ErrorHelper.LoaderInfo
    public LoaderManager.LoaderCallbacks<?> getLoaderCallbacks() {
        return this.callbacks;
    }

    @Override // com.microsoft.xbox.idp.util.ErrorHelper.LoaderInfo
    public boolean hasCachedData(Object obj) {
        boolean z;
        BitmapLoader.Cache bitmapCache = CacheUtil.getBitmapCache();
        synchronized (bitmapCache) {
            z = bitmapCache.get(obj) != null;
        }
        return z;
    }
}
