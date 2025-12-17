package com.mojang.minecraftpe.packagesource.googleplay;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import com.google.android.vending.expansion.downloader.DownloaderClientMarshaller;
//import com.craftsman.go.StringFog;

/* loaded from: classes3.dex */
public class ApkXDownloaderAlarmReceiver extends BroadcastReceiver {
    @Override // android.content.BroadcastReceiver
    public void onReceive(Context context, Intent intent) {
//        Log.d(StringFog.decrypt("q3LhIvE33HaGbese0CrqdItw5yjQO85xnGf4\n", "6gKKerVYqxg=\n"), StringFog.decrypt("17Kg/TeYG2T1u6j5P9w=\n", "lt7Bj1q4aQE=\n"));
        try {
            DownloaderClientMarshaller.startDownloadServiceIfRequired(context, intent, ApkXDownloaderService.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
