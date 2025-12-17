package com.microsoft.xbox.idp.util;

import android.util.Log;
import com.microsoft.xbox.idp.toolkit.BitmapLoader;
import com.microsoft.xbox.idp.toolkit.ObjectLoader;

/* loaded from: classes3.dex */
public final class CacheUtil {
    private static final String TAG = "CacheUtil";
    private static final BitmapLoader.Cache bitmapCache = new BitmapLoaderCache(50);
    private static final ObjectLoader.Cache objectLoaderCache = new ObjectLoaderCache();

    public static void clearCaches() {
        Log.d(TAG, "clearCaches");
        ObjectLoader.Cache cache = objectLoaderCache;
        synchronized (cache) {
            cache.clear();
        }
        BitmapLoader.Cache cache2 = bitmapCache;
        synchronized (cache2) {
            cache2.clear();
        }
    }

    public static BitmapLoader.Cache getBitmapCache() {
        return bitmapCache;
    }

    public static ObjectLoader.Cache getObjectLoaderCache() {
        return objectLoaderCache;
    }
}
