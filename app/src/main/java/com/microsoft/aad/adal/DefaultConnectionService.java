package com.microsoft.aad.adal;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;

/* loaded from: classes3.dex */
class DefaultConnectionService implements IConnectionService {
    private static final String TAG = "DefaultConnectionService";
    private final Context mConnectionContext;

    /* JADX INFO: Access modifiers changed from: package-private */
    public DefaultConnectionService(Context context) {
        this.mConnectionContext = context;
    }

    @Override // com.microsoft.aad.adal.IConnectionService
    public boolean isConnectionAvailable() {
        NetworkInfo activeNetworkInfo = ((ConnectivityManager) this.mConnectionContext.getSystemService("connectivity")).getActiveNetworkInfo();
        return (activeNetworkInfo == null || !activeNetworkInfo.isConnectedOrConnecting() || isNetworkDisabledFromOptimizations()) ? false : true;
    }

    public boolean isNetworkDisabledFromOptimizations() {
        ADALError aDALError;
        String str;
        if (Build.VERSION.SDK_INT < 23) {
            return false;
        }
        if (UsageStatsManagerWrapper.getInstance().isAppInactive(this.mConnectionContext)) {
            aDALError = ADALError.NO_NETWORK_CONNECTION_POWER_OPTIMIZATION;
            str = "Client app is inactive. Network is disabled.";
        } else {
            PowerManagerWrapper powerManagerWrapper = PowerManagerWrapper.getInstance();
            if (!powerManagerWrapper.isDeviceIdleMode(this.mConnectionContext) || powerManagerWrapper.isIgnoringBatteryOptimizations(this.mConnectionContext)) {
                return false;
            }
            aDALError = ADALError.NO_NETWORK_CONNECTION_POWER_OPTIMIZATION;
            str = "Device is dozing. Network is disabled.";
        }
        Logger.m14617w(TAG, str, "", aDALError);
        return true;
    }
}
