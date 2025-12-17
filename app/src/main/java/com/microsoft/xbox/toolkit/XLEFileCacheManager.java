package com.microsoft.xbox.toolkit;

import com.microsoft.xbox.toolkit.system.SystemUtil;
import com.microsoft.xboxtcui.XboxTcuiSdk;
import java.io.File;
import java.util.HashMap;

/* loaded from: classes3.dex */
public class XLEFileCacheManager {
    public static XLEFileCache emptyFileCache = new XLEFileCache();
    private static HashMap<String, XLEFileCache> sAllCaches = new HashMap<>();
    private static HashMap<XLEFileCache, File> sCacheRootDirMap = new HashMap<>();

    public static XLEFileCache createCache(String str, int i) {
        XLEFileCache createCache;
        synchronized (XLEFileCacheManager.class) {
            createCache = createCache(str, i, true);
        }
        return createCache;
    }

    public static XLEFileCache createCache(String str, int i, boolean z) {
        synchronized (XLEFileCacheManager.class) {
            if (i <= 0) {
                throw new IllegalArgumentException("maxFileNumber must be > 0");
            }
            if (str == null || str.length() <= 0) {
                throw new IllegalArgumentException("subDirectory must be not null and at least one character length");
            }
            XLEFileCache xLEFileCache = sAllCaches.get(str);
            if (xLEFileCache == null) {
                if (!z) {
                    return emptyFileCache;
                } else if (!SystemUtil.isSDCardAvailable()) {
                    return emptyFileCache;
                } else {
                    xLEFileCache = new XLEFileCache(str, i);
                    File file = new File(XboxTcuiSdk.getActivity().getCacheDir(), str);
                    if (!file.exists()) {
                        file.mkdirs();
                    }
                    String[] list = file.list();
                    xLEFileCache.size = list != null ? list.length : 0;
                    sAllCaches.put(str, xLEFileCache);
                    sCacheRootDirMap.put(xLEFileCache, file);
                }
            } else if (xLEFileCache.maxFileNumber != i) {
                throw new IllegalArgumentException("The same subDirectory with different maxFileNumber already exist.");
            }
            return xLEFileCache;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static File getCacheRootDir(XLEFileCache xLEFileCache) {
        return sCacheRootDirMap.get(xLEFileCache);
    }

    public static String getCacheStatus() {
        return sAllCaches.values().toString();
    }
}
