package com.mojang.minecraftpe.packagesource.googleplay;

import com.google.android.vending.expansion.downloader.impl.DownloaderService;

/* loaded from: classes3.dex */
public class ApkXDownloaderService extends DownloaderService {
    @Override // com.google.android.vending.expansion.downloader.impl.DownloaderService
    public String getPublicKey() {
        return ApkXDownloaderClient.getLicenseKey();
    }

    @Override // com.google.android.vending.expansion.downloader.impl.DownloaderService
    public byte[] getSALT() {
        return ApkXDownloaderClient.SALT;
    }

    @Override // com.google.android.vending.expansion.downloader.impl.DownloaderService
    public String getNotificationChannelId() {
        return ApkXDownloaderClient.getNotificationChannelId();
    }

    @Override // com.google.android.vending.expansion.downloader.impl.DownloaderService
    public String getAlarmReceiverClassName() {
        return ApkXDownloaderAlarmReceiver.class.getName();
    }
}
