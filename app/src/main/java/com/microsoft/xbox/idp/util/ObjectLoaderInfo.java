package com.microsoft.xbox.idp.util;

import android.app.LoaderManager;
import com.microsoft.xbox.idp.toolkit.ObjectLoader;
import com.microsoft.xbox.idp.util.ErrorHelper;

/* loaded from: classes3.dex */
public class ObjectLoaderInfo implements ErrorHelper.LoaderInfo {
    private final LoaderManager.LoaderCallbacks<?> callbacks;

    public ObjectLoaderInfo(LoaderManager.LoaderCallbacks<?> loaderCallbacks) {
        this.callbacks = loaderCallbacks;
    }

    @Override // com.microsoft.xbox.idp.util.ErrorHelper.LoaderInfo
    public void clearCache(Object obj) {
        ObjectLoader.Cache objectLoaderCache = CacheUtil.getObjectLoaderCache();
        synchronized (objectLoaderCache) {
            objectLoaderCache.remove(obj);
        }
    }

    @Override // com.microsoft.xbox.idp.util.ErrorHelper.LoaderInfo
    public LoaderManager.LoaderCallbacks<?> getLoaderCallbacks() {
        return this.callbacks;
    }

    @Override // com.microsoft.xbox.idp.util.ErrorHelper.LoaderInfo
    public boolean hasCachedData(Object obj) {
        boolean z;
        ObjectLoader.Cache objectLoaderCache = CacheUtil.getObjectLoaderCache();
        synchronized (objectLoaderCache) {
            z = objectLoaderCache.get(obj) != null;
        }
        return z;
    }
}
