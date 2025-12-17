package com.google.android.vending.expansion.downloader.impl;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.os.Build;
import android.os.Messenger;
import androidx.core.app.NotificationCompat;
import com.google.android.vending.expansion.downloader.DownloadProgressInfo;
import com.google.android.vending.expansion.downloader.DownloaderClientMarshaller;
import com.google.android.vending.expansion.downloader.Helpers;
import com.google.android.vending.expansion.downloader.IDownloaderClient;
import com.mojang.minecraftpe.packagesource.PackageSource;

/* loaded from: classes7.dex */
public class DownloadNotification implements IDownloaderClient {
    static final String LOGTAG = "DownloadNotification";
    static final int NOTIFICATION_ID = -908767821;
    private NotificationCompat.Builder mActiveDownloadBuilder;
    private NotificationCompat.Builder mBuilder;
    private String mChannelId;
    private IDownloaderClient mClientProxy;
    private PendingIntent mContentIntent;
    private NotificationCompat.Builder mCurrentBuilder;
    private CharSequence mLabel;
    private Notification mLatestNotification;
    private final NotificationManager mNotificationManager;
    private DownloadProgressInfo mProgressInfo;
    private final Service mService;
    private int mState = -1;
    private String mCurrentText = PackageSource.getStringResource(PackageSource.StringResourceId.STATE_UNKNOWN);

    /* JADX INFO: Access modifiers changed from: package-private */
    public DownloadNotification(Service service, String channelId, CharSequence applicationLabel) {
        this.mService = service;
        this.mChannelId = channelId;
        this.mLabel = applicationLabel;
        this.mNotificationManager = (NotificationManager) service.getSystemService("notification");
        this.mActiveDownloadBuilder = new NotificationCompat.Builder(service);
        this.mBuilder = new NotificationCompat.Builder(service);
        this.mActiveDownloadBuilder.setPriority(-1);
        this.mActiveDownloadBuilder.setCategory("progress");
        this.mBuilder.setChannelId(channelId);
        this.mActiveDownloadBuilder.setChannelId(channelId);
        this.mBuilder.setPriority(-1);
        this.mBuilder.setCategory("progress");
        this.mCurrentBuilder = this.mBuilder;
    }

    public PendingIntent getClientIntent() {
        return this.mContentIntent;
    }

    @Override // com.google.android.vending.expansion.downloader.IDownloaderClient
    public void onDownloadProgress(DownloadProgressInfo progress) {
        this.mProgressInfo = progress;
        IDownloaderClient iDownloaderClient = this.mClientProxy;
        if (iDownloaderClient != null) {
            iDownloaderClient.onDownloadProgress(progress);
        }
        if (progress.mOverallTotal <= 0) {
            this.mBuilder.setTicker(this.mLabel);
            this.mBuilder.setSmallIcon(17301633);
            this.mBuilder.setContentTitle(this.mLabel);
            this.mBuilder.setContentText(this.mCurrentText);
            this.mCurrentBuilder = this.mBuilder;
        } else {
            this.mActiveDownloadBuilder.setProgress((int) progress.mOverallTotal, (int) progress.mOverallProgress, false);
            this.mActiveDownloadBuilder.setContentText(Helpers.getDownloadProgressString(progress.mOverallProgress, progress.mOverallTotal));
            this.mActiveDownloadBuilder.setSmallIcon(17301633);
            NotificationCompat.Builder builder = this.mActiveDownloadBuilder;
            builder.setTicker(((Object) this.mLabel) + ": " + this.mCurrentText);
            this.mActiveDownloadBuilder.setContentTitle(this.mLabel);
            this.mActiveDownloadBuilder.setContentInfo(String.format(PackageSource.getStringResource(PackageSource.StringResourceId.TIME_REMAINING_NOTIFICATION), Helpers.getTimeRemaining(progress.mTimeRemaining)));
            this.mCurrentBuilder = this.mActiveDownloadBuilder;
        }
        Notification build = this.mCurrentBuilder.build();
        this.mLatestNotification = build;
        this.mNotificationManager.notify(NOTIFICATION_ID, build);
    }

    /* JADX WARN: Removed duplicated region for block: B:35:0x008f  */
    /* JADX WARN: Removed duplicated region for block: B:36:0x0095  */
    @Override // com.google.android.vending.expansion.downloader.IDownloaderClient
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public void onDownloadStateChanged(int r7) {
        /*
            r6 = this;
            com.google.android.vending.expansion.downloader.IDownloaderClient r0 = r6.mClientProxy
            if (r0 == 0) goto L_0x0007
            r0.onDownloadStateChanged(r7)
        L_0x0007:
            int r0 = r6.mState
            if (r7 == r0) goto L_0x00af
            r6.mState = r7
            r0 = 1
            if (r7 == r0) goto L_0x00af
            android.app.PendingIntent r1 = r6.mContentIntent
            if (r1 != 0) goto L_0x0016
            goto L_0x00af
        L_0x0016:
            r1 = 17301634(0x1080082, float:2.497962E-38)
            r2 = 17301642(0x108008a, float:2.4979642E-38)
            r3 = 0
            if (r7 == 0) goto L_0x0051
            r4 = 7
            if (r7 == r4) goto L_0x004c
            r4 = 2
            if (r7 == r4) goto L_0x0047
            r4 = 3
            if (r7 == r4) goto L_0x0047
            r4 = 4
            if (r7 == r4) goto L_0x003f
            r4 = 5
            if (r7 == r4) goto L_0x004c
            switch(r7) {
                case 15: goto L_0x003a;
                case 16: goto L_0x003a;
                case 17: goto L_0x003a;
                case 18: goto L_0x003a;
                case 19: goto L_0x003a;
                default: goto L_0x0031;
            }
        L_0x0031:
            com.mojang.minecraftpe.packagesource.PackageSource$StringResourceId r7 = com.google.android.vending.expansion.downloader.Helpers.getDownloaderStringResourceIDFromPlaystoreState(r7)
            r1 = 17301642(0x108008a, float:2.4979642E-38)
        L_0x0038:
            r2 = 1
            goto L_0x0057
        L_0x003a:
            com.mojang.minecraftpe.packagesource.PackageSource$StringResourceId r7 = com.google.android.vending.expansion.downloader.Helpers.getDownloaderStringResourceIDFromPlaystoreState(r7)
            goto L_0x0053
        L_0x003f:
            r1 = 17301633(0x1080081, float:2.4979616E-38)
            com.mojang.minecraftpe.packagesource.PackageSource$StringResourceId r7 = com.google.android.vending.expansion.downloader.Helpers.getDownloaderStringResourceIDFromPlaystoreState(r7)
            goto L_0x0038
        L_0x0047:
            com.mojang.minecraftpe.packagesource.PackageSource$StringResourceId r7 = com.google.android.vending.expansion.downloader.Helpers.getDownloaderStringResourceIDFromPlaystoreState(r7)
            goto L_0x0038
        L_0x004c:
            com.mojang.minecraftpe.packagesource.PackageSource$StringResourceId r7 = com.google.android.vending.expansion.downloader.Helpers.getDownloaderStringResourceIDFromPlaystoreState(r7)
            goto L_0x0056
        L_0x0051:
            com.mojang.minecraftpe.packagesource.PackageSource$StringResourceId r7 = com.mojang.minecraftpe.packagesource.PackageSource.StringResourceId.STATE_UNKNOWN
        L_0x0053:
            r1 = 17301642(0x108008a, float:2.4979642E-38)
        L_0x0056:
            r2 = 0
        L_0x0057:
            java.lang.String r7 = com.mojang.minecraftpe.packagesource.PackageSource.getStringResource(r7)
            r6.mCurrentText = r7
            androidx.core.app.NotificationCompat$Builder r7 = r6.mCurrentBuilder
            java.lang.StringBuilder r4 = new java.lang.StringBuilder
            r4.<init>()
            java.lang.CharSequence r5 = r6.mLabel
            r4.append(r5)
            java.lang.String r5 = ": "
            r4.append(r5)
            java.lang.String r5 = r6.mCurrentText
            r4.append(r5)
            java.lang.String r4 = r4.toString()
            r7.setTicker(r4)
            androidx.core.app.NotificationCompat$Builder r7 = r6.mCurrentBuilder
            r7.setSmallIcon(r1)
            androidx.core.app.NotificationCompat$Builder r7 = r6.mCurrentBuilder
            java.lang.CharSequence r1 = r6.mLabel
            r7.setContentTitle(r1)
            androidx.core.app.NotificationCompat$Builder r7 = r6.mCurrentBuilder
            java.lang.String r1 = r6.mCurrentText
            r7.setContentText(r1)
            if (r2 == 0) goto L_0x0095
            androidx.core.app.NotificationCompat$Builder r7 = r6.mCurrentBuilder
            r7.setOngoing(r0)
            goto L_0x009f
        L_0x0095:
            androidx.core.app.NotificationCompat$Builder r7 = r6.mCurrentBuilder
            r7.setOngoing(r3)
            androidx.core.app.NotificationCompat$Builder r7 = r6.mCurrentBuilder
            r7.setAutoCancel(r0)
        L_0x009f:
            androidx.core.app.NotificationCompat$Builder r7 = r6.mCurrentBuilder
            android.app.Notification r7 = r7.build()
            r6.mLatestNotification = r7
            android.app.NotificationManager r0 = r6.mNotificationManager
            int r1 = com.google.android.vending.expansion.downloader.impl.DownloadNotification.NOTIFICATION_ID
            r0.notify(r1, r7)
        L_0x00af:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.vending.expansion.downloader.impl.DownloadNotification.onDownloadStateChanged(int):void");
    }

    @Override // com.google.android.vending.expansion.downloader.IDownloaderClient
    public void onServiceConnected(Messenger m) {
    }

    public void resendState() {
        IDownloaderClient iDownloaderClient = this.mClientProxy;
        if (iDownloaderClient != null) {
            iDownloaderClient.onDownloadStateChanged(this.mState);
        }
    }

    public void setClientIntent(PendingIntent clientIntent) {
        this.mBuilder.setContentIntent(clientIntent);
        this.mActiveDownloadBuilder.setContentIntent(clientIntent);
        this.mContentIntent = clientIntent;
    }

    public void setMessenger(Messenger msg) {
        IDownloaderClient CreateProxy = DownloaderClientMarshaller.CreateProxy(msg);
        this.mClientProxy = CreateProxy;
        DownloadProgressInfo downloadProgressInfo = this.mProgressInfo;
        if (downloadProgressInfo != null) {
            CreateProxy.onDownloadProgress(downloadProgressInfo);
        }
        int i = this.mState;
        if (i != -1) {
            this.mClientProxy.onDownloadStateChanged(i);
        }
    }

    public void startForeground() {
        if (Build.VERSION.SDK_INT >= 26) {
            if (this.mLatestNotification == null) {
                this.mBuilder.setTicker(this.mLabel);
                this.mBuilder.setSmallIcon(17301633);
                this.mBuilder.setContentTitle(this.mLabel);
                this.mBuilder.setContentText(this.mCurrentText);
                this.mLatestNotification = this.mBuilder.build();
            }
            this.mService.startForeground(NOTIFICATION_ID, this.mLatestNotification);
        }
    }
}
