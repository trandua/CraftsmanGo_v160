package com.microsoft.aad.adal;

import android.content.Context;
import android.os.PowerManager;

/* loaded from: classes3.dex */
public class PowerManagerWrapper {
    private static PowerManagerWrapper sInstance;

    public static PowerManagerWrapper getInstance() {
        PowerManagerWrapper powerManagerWrapper;
        synchronized (PowerManagerWrapper.class) {
            if (sInstance == null) {
                sInstance = new PowerManagerWrapper();
            }
            powerManagerWrapper = sInstance;
        }
        return powerManagerWrapper;
    }

    static void setInstance(PowerManagerWrapper powerManagerWrapper) {
        sInstance = powerManagerWrapper;
    }

    public boolean isDeviceIdleMode(Context context) {
        return ((PowerManager) context.getSystemService("power")).isDeviceIdleMode();
    }

    public boolean isIgnoringBatteryOptimizations(Context context) {
        return ((PowerManager) context.getSystemService("power")).isIgnoringBatteryOptimizations(context.getPackageName());
    }
}
