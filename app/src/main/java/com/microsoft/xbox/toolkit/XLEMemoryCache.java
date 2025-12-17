package com.microsoft.xbox.toolkit;

import android.util.LruCache;

/* loaded from: classes3.dex */
public class XLEMemoryCache<K, V> {
    private int itemCount = 0;
    private final LruCache<K, XLEMemoryCacheEntry<V>> lruCache;
    private final int maxFileSizeBytes;

    public XLEMemoryCache(int i, int i2) {
        if (i < 0) {
            throw new IllegalArgumentException("sizeInBytes");
        }
        if (i2 >= 0) {
            this.maxFileSizeBytes = i2;
            this.lruCache = i == 0 ? null : new LruCache<K, XLEMemoryCacheEntry<V>>(i) { // from class: com.microsoft.xbox.toolkit.XLEMemoryCache.1
                private XLEMemoryCache this$0;

//                @Override // android.util.LruCache
//                public /* bridge */ /* synthetic */ void entryRemoved(boolean z, Object obj, Object obj2, Object obj3) {
//                    entryRemoved(z, (boolean) obj, (XLEMemoryCacheEntry) ((XLEMemoryCacheEntry) obj2), (XLEMemoryCacheEntry) ((XLEMemoryCacheEntry) obj3));
//                }

//                @Override // android.util.LruCache
//                public /* bridge */ /* synthetic */ int sizeOf(Object obj, Object obj2) {
//                    return sizeOf((AnonymousClass1) obj, (XLEMemoryCacheEntry) ((XLEMemoryCacheEntry) obj2));
//                }

                public void entryRemoved(boolean z, K k, XLEMemoryCacheEntry<V> xLEMemoryCacheEntry, XLEMemoryCacheEntry<V> xLEMemoryCacheEntry2) {
                    XLEMemoryCache.access$006(this.this$0);
                }

                public int sizeOf(K k, XLEMemoryCacheEntry<V> xLEMemoryCacheEntry) {
                    return xLEMemoryCacheEntry.getByteCount();
                }
            };
            return;
        }
        throw new IllegalArgumentException("maxFileSizeInBytes");
    }

    static int access$006(XLEMemoryCache xLEMemoryCache) {
        int i = xLEMemoryCache.itemCount - 1;
        xLEMemoryCache.itemCount = i;
        return i;
    }

    public boolean add(K k, V v, int i) {
        if (i > this.maxFileSizeBytes || this.lruCache == null) {
            return false;
        }
        XLEMemoryCacheEntry<V> xLEMemoryCacheEntry = new XLEMemoryCacheEntry<>(v, i);
        this.itemCount++;
        this.lruCache.put(k, xLEMemoryCacheEntry);
        return true;
    }

    public V get(K k) {
        XLEMemoryCacheEntry<V> xLEMemoryCacheEntry;
        LruCache<K, XLEMemoryCacheEntry<V>> lruCache = this.lruCache;
        if (lruCache == null || (xLEMemoryCacheEntry = lruCache.get(k)) == null) {
            return null;
        }
        return xLEMemoryCacheEntry.getValue();
    }

    public int getBytesCurrent() {
        LruCache<K, XLEMemoryCacheEntry<V>> lruCache = this.lruCache;
        if (lruCache == null) {
            return 0;
        }
        return lruCache.size();
    }

    public int getBytesFree() {
        LruCache<K, XLEMemoryCacheEntry<V>> lruCache = this.lruCache;
        if (lruCache == null) {
            return 0;
        }
        return lruCache.maxSize() - this.lruCache.size();
    }

    public int getItemsInCache() {
        return this.itemCount;
    }
}
