package com.microsoft.aad.adal;

import android.app.usage.UsageStatsManager;
import android.content.Context;

/* loaded from: classes3.dex */
public class UsageStatsManagerWrapper {
    private static UsageStatsManagerWrapper sInstance;

    public static UsageStatsManagerWrapper getInstance() {
        UsageStatsManagerWrapper usageStatsManagerWrapper;
        synchronized (UsageStatsManagerWrapper.class) {
            if (sInstance == null) {
                sInstance = new UsageStatsManagerWrapper();
            }
            usageStatsManagerWrapper = sInstance;
        }
        return usageStatsManagerWrapper;
    }

    static void setInstance(UsageStatsManagerWrapper usageStatsManagerWrapper) {
        synchronized (UsageStatsManagerWrapper.class) {
            sInstance = usageStatsManagerWrapper;
        }
    }

    public boolean isAppInactive(Context context) {
        return ((UsageStatsManager) context.getSystemService("usagestats")).isAppInactive(context.getPackageName());
    }
}
