package com.google.android.vending.expansion.downloader;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;
import android.util.Log;

/* loaded from: classes7.dex */
class SystemFacade {
    private Context mContext;
    private NotificationManager mNotificationManager;

    public SystemFacade(Context context) {
        this.mContext = context;
        this.mNotificationManager = (NotificationManager) context.getSystemService("notification");
    }

    public void cancelAllNotifications() {
        this.mNotificationManager.cancelAll();
    }

    public void cancelNotification(long id) {
        this.mNotificationManager.cancel((int) id);
    }

    public long currentTimeMillis() {
        return System.currentTimeMillis();
    }

    public Integer getActiveNetworkType() {
        ConnectivityManager connectivityManager = (ConnectivityManager) this.mContext.getSystemService("connectivity");
        if (connectivityManager == null) {
            Log.w(Constants.TAG, "couldn't get connectivity manager");
            return null;
        }
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        if (activeNetworkInfo == null) {
            return null;
        }
        return Integer.valueOf(activeNetworkInfo.getType());
    }

    public Long getMaxBytesOverMobile() {
        return 2147483647L;
    }

    public Long getRecommendedMaxBytesOverMobile() {
        return 2097152L;
    }

    public boolean isNetworkRoaming() {
        ConnectivityManager connectivityManager = (ConnectivityManager) this.mContext.getSystemService("connectivity");
        if (connectivityManager == null) {
            Log.w(Constants.TAG, "couldn't get connectivity manager");
            return false;
        }
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        boolean z = activeNetworkInfo != null && activeNetworkInfo.getType() == 0;
        TelephonyManager telephonyManager = (TelephonyManager) this.mContext.getSystemService("phone");
        if (telephonyManager != null) {
            return z && telephonyManager.isNetworkRoaming();
        }
        Log.w(Constants.TAG, "couldn't get telephony manager");
        return false;
    }

    public void postNotification(long id, Notification notification) {
        this.mNotificationManager.notify((int) id, notification);
    }

    public void sendBroadcast(Intent intent) {
        this.mContext.sendBroadcast(intent);
    }

    public void startThread(Thread thread) {
        thread.start();
    }

    public boolean userOwnsPackage(int uid, String packageName) throws PackageManager.NameNotFoundException {
        return this.mContext.getPackageManager().getApplicationInfo(packageName, 0).uid == uid;
    }
}
