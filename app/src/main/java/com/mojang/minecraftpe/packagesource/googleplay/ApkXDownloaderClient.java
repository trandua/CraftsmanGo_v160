package com.mojang.minecraftpe.packagesource.googleplay;

import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP;
import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Messenger;
import android.os.storage.OnObbStateChangeListener;
import android.os.storage.StorageManager;
import android.provider.Settings;
import android.util.Log;
import com.google.android.vending.expansion.downloader.DownloadProgressInfo;
import com.google.android.vending.expansion.downloader.DownloaderClientMarshaller;
import com.google.android.vending.expansion.downloader.DownloaderServiceMarshaller;
import com.google.android.vending.expansion.downloader.Helpers;
import com.google.android.vending.expansion.downloader.IDownloaderClient;
import com.google.android.vending.expansion.downloader.IDownloaderService;
import com.google.android.vending.expansion.downloader.IStub;
import com.googleplay.licensing.AESObfuscator;
import com.googleplay.licensing.APKExpansionPolicy;
import com.googleplay.licensing.LicenseChecker;
import com.googleplay.licensing.LicenseCheckerCallback;
//import com.craftsman.go.StringFog;
import com.mojang.minecraftpe.ActivityListener;
import com.mojang.minecraftpe.MainActivity;
import com.mojang.minecraftpe.packagesource.PackageSource;
import com.mojang.minecraftpe.packagesource.PackageSourceListener;
import java.io.File;

/* loaded from: classes3.dex */
public class ApkXDownloaderClient extends PackageSource implements IDownloaderClient, ActivityListener {
    private static final String LOG_TAG = "ApkXDownloaderClient";
    public static final byte[] SALT = {78, -97, 80, -51, 45, -99, 108, 52, -42, 25, 48, 24, -76, -105, 9, 38, -43, 81, 6, 14};
    private static String licenseKey;
    private static String notificationChannelId;
    public MainActivity mActivity;
    public IStub mDownloaderClientStub;
    public PackageSourceListener mListener;
    public NotificationManager mNotificationManager;
    private IDownloaderService mRemoteService;
    public StorageManager mStorageManager;

    public static int convertOBBStateToMountState(int i) {
        if (i == 1) {
            return 7;
        }
        if (i == 2) {
            return 8;
        }
        switch (i) {
            case 20:
                return 4;
            case 21:
                return 2;
            case 22:
                return 3;
            case 23:
                return 5;
            case 24:
                return 1;
            case 25:
                return 6;
            default:
                return 0;
        }
    }

    public static int convertStateToFailedReason(int i) {
        switch (i) {
            case 15:
                return 2;
            case 16:
                return 3;
            case 17:
                return 4;
            case 18:
                return 5;
            default:
                return 0;
        }
    }

    public static int convertStateToPausedReason(int i) {
        switch (i) {
            case 6:
                return 1;
            case 7:
                return 2;
            case 8:
                return 3;
            case 9:
                return 4;
            case 10:
                return 5;
            case 11:
                return 6;
            case 12:
                return 7;
            case 13:
                return 8;
            case 14:
                return 9;
            default:
                return 0;
        }
    }

    static ApkXDownloaderClient create(String str, PackageSourceListener packageSourceListener) {
        return new ApkXDownloaderClient(MainActivity.mInstance, str, packageSourceListener);
    }

    public ApkXDownloaderClient(MainActivity mainActivity, String str, PackageSourceListener packageSourceListener) {
        this.mNotificationManager = (NotificationManager) this.mActivity.getSystemService(Context.NOTIFICATION_SERVICE);
        this.mStorageManager = (StorageManager) this.mActivity.getSystemService(Context.STORAGE_SERVICE);
        this.mListener = packageSourceListener;
        this.mActivity = mainActivity;
        mainActivity.addListener(this);
        licenseKey = str;
        notificationChannelId = String.format("%1$s_APKXDownload", this.mActivity.getCallingPackage());
    }

    public static final String getLicenseKey() {
        return licenseKey;
    }

    public static final String getNotificationChannelId() {
        return notificationChannelId;
    }

    @Override // com.mojang.minecraftpe.packagesource.PackageSource
    public void destructor() {
//        Log.i(LOG_TAG, StringFog.decrypt("bhrEFMdSC/xlDQ==\n", "Cn+3YLUnaIg=\n"));
        this.mActivity.removeListener(this);
    }

    @Override // com.mojang.minecraftpe.packagesource.PackageSource
    public String getMountPath(String str) {
        StorageManager storageManager = this.mStorageManager;
        if (storageManager == null) {
            return null;
        }
        return storageManager.getMountedObbPath(str);
    }

    @Override // com.mojang.minecraftpe.packagesource.PackageSource
    public String getDownloadDirectoryPath() {
        return Helpers.getSaveFilePath(this.mActivity);
    }

    public String getOBBFilePath(String str) {
        return Helpers.generateSaveFileName(this.mActivity, str);
    }

    @Override // com.mojang.minecraftpe.packagesource.PackageSource
    public void mountFiles(String str) {
        if (str == null || str.isEmpty()) {
//            Log.e(LOG_TAG, String.format(StringFog.decrypt("C+tUT992HUMD9wEMi1YdQwPqQEzOEFMKFaMBSNgQEUIW8FgP\n", "ZoQhIaswdC8=\n"), str));
            return;
        }
        String oBBFilePath = getOBBFilePath(str);
        String str2 = LOG_TAG;
//        Log.d(str2, String.format(StringFog.decrypt("lY6Kf0c/yqOdkt88EwnCu5Db3zYWCoTh\n", "+OH/ETN5o88=\n"), oBBFilePath));
        if (new File(oBBFilePath).exists()) {
            this.mStorageManager.mountObb(oBBFilePath, null, new OnObbStateChangeListener() { // from class: com.mojang.minecraftpe.packagesource.googleplay.ApkXDownloaderClient.1
                @Override // android.os.storage.OnObbStateChangeListener
                public void onObbStateChange(String str3, int i) {
                    super.onObbStateChange(str3, i);
//                    Log.d(StringFog.decrypt("ji1LsLMLUgajMkGMkhZmBKY4Tpw=\n", "z10g6PdkJWg=\n"), String.format(StringFog.decrypt("pEABMAXRIim/Sw06BuwxLesDbiIG9j5y6wlrIUCudju/Tzo3XaJzLOU=\n", "yy5OUmeCVkg=\n"), str3, Integer.valueOf(i)));
                    int convertOBBStateToMountState = ApkXDownloaderClient.convertOBBStateToMountState(i);
                    boolean isObbMounted = ApkXDownloaderClient.this.mStorageManager.isObbMounted(str3);
                    String mountPath = ApkXDownloaderClient.this.getMountPath(str3);
//                    Log.d(StringFog.decrypt("P4XD8v6diH8SmsnO34C8fReQxt4=\n", "fvWoqrry/xE=\n"), String.format(StringFog.decrypt("9Y09G/p+pFXuhjER+UO3UbrOUgr3WKJX/8MCGOxF6hS9xgFetA2+Ue3DAhjsReoUvcYBXrY=\n", "muNyeZgt0DQ=\n"), str3, mountPath));
                    if (mountPath == null) {
                        mountPath = "";
                    }
                    if (isObbMounted && mountPath == "") {
                        convertOBBStateToMountState = 4;
                    }
                    ApkXDownloaderClient.this.mListener.onMountStateChanged(mountPath, convertOBBStateToMountState);
                }
            });
        } else {
//            Log.e(str2, String.format(StringFog.decrypt("eBFaAj5H6ZFwDQ9BanHhiX1eCEk5JqCZehtcTCRu9N1wBkYfPi8=\n", "FX4vbEoBgP0=\n"), oBBFilePath));
        }
    }

    @Override // com.mojang.minecraftpe.packagesource.PackageSource
    public void unmountFiles(String str) {
        if (str == null || str.isEmpty()) {
//            Log.w(LOG_TAG, String.format(StringFog.decrypt("sLmii81UJsmsu6qXmBdy6ay7qorZVzev4vK8w5hTIa+gur+QwRQ=\n", "xdfP5Lg6Uo8=\n"), str));
            return;
        }
        String oBBFilePath = getOBBFilePath(str);
        String str2 = LOG_TAG;
//        Log.d(str2, String.format(StringFog.decrypt("RB+bml72sz1YHZOGC7XnC1AFns8Lv+IIFl8=\n", "MXH29SuYx3s=\n"), oBBFilePath));
        if (new File(oBBFilePath).exists()) {
            this.mStorageManager.unmountObb(oBBFilePath, false, null);
        } else {
//            Log.w(str2, String.format(StringFog.decrypt("uVx57/FDGP+lXnHzpABMya1GfKCjCB+e7FZ75fcNAta4EnH47V4Ylw==\n", "zDIUgIQtbLk=\n"), oBBFilePath));
        }
    }

    @Override // com.mojang.minecraftpe.packagesource.PackageSource
    public void downloadFiles(final String str, final long j, final boolean z, final boolean z2) {
        String string = Settings.Secure.getString(this.mActivity.getContentResolver(), "android_id");
        MainActivity mainActivity = this.mActivity;
        final APKExpansionPolicy aPKExpansionPolicy = new APKExpansionPolicy(mainActivity, new AESObfuscator(SALT, mainActivity.getPackageName(), string));
        aPKExpansionPolicy.resetPolicy();
        new LicenseChecker(this.mActivity, aPKExpansionPolicy, licenseKey).checkAccess(new LicenseCheckerCallback() { // from class: com.mojang.minecraftpe.packagesource.googleplay.ApkXDownloaderClient.2
            @Override // com.googleplay.licensing.LicenseCheckerCallback
            public void allow(int i) {
//                Log.i(StringFog.decrypt("Hlvfhu0UOrQzRNW6zAkOtjZO2qo=\n", "Xyu03ql7Tdo=\n"), String.format(StringFog.decrypt("yjEOzbCmqfbuPQ7Du6eP1Oo0D8m9vuyYpjkBxLGi9pWjPEM=\n", "hlhtqN7VzLU=\n"), Integer.valueOf(i)));
//                Log.i(StringFog.decrypt("moMMghxsK9e3nAa+PXEf1bKWCa4=\n", "2/Nn2lgDXLk=\n"), String.format(StringFog.decrypt("SfQfHYEF/3Jt+B8TigTZUGnxHhmMHbocJdgECIoV7lhr+lwMgFb8WGv5XB6GGv8Ra/wRHdVWvRR2\nulBYnB/gVD+9WRzB\n", "BZ18eO92mjE=\n"), str, Long.valueOf(j)));
                int expansionURLCount = aPKExpansionPolicy.getExpansionURLCount();
                for (int i2 = 0; i2 < expansionURLCount; i2++) {
//                    Log.i(StringFog.decrypt("SZnDnyFmhs5khsmjAHuyzGGMxrM=\n", "COmox2UJ8aA=\n"), String.format(StringFog.decrypt("KWXRgY69lgQNadGPhbywJglg0IWDpdNqRUrbiIXunSYIaYjEx+uAYEkswY2aq8lnQGic\n", "ZQyy5ODO80c=\n"), aPKExpansionPolicy.getExpansionFileName(i2), Long.valueOf(aPKExpansionPolicy.getExpansionFileSize(i2))));
                }
                if (z && (expansionURLCount == 0 || !str.equalsIgnoreCase(aPKExpansionPolicy.getExpansionFileName(0)))) {
//                    Log.e(StringFog.decrypt("+RAq1/0IUhDUDyDr3BVmEtEFL/s=\n", "uGBBj7lnJX4=\n"), String.format(StringFog.decrypt("7nh2JM6QE/PKdHYqxZE10c59dyDDiFadgkdwM8mFH9PDZXwuzsMQ0ct9cCWOwzDZznQ1L8GOE4qC\nNjAyh89W1s1keyWAjRfdxys1ZoWQUZ4=\n", "ohEVQaDjdrA=\n"), str, aPKExpansionPolicy.getExpansionFileName(0)));
                    ApkXDownloaderClient.this.mListener.onDownloadStateChanged(false, false, false, false, true, 0, 6);
                } else if (z2 && (expansionURLCount == 0 || j != aPKExpansionPolicy.getExpansionFileSize(0))) {
//                    Log.e(StringFog.decrypt("ZZCf+sbsxkFIj5XG5/HyQ02FmtY=\n", "JOD0ooKDsS8=\n"), String.format(StringFog.decrypt("hIJ0TOuJIJOgjnRC4IgGsaSHdUjmkWX96L1yW+ycLLOpn35G69ojsaGHck2r2gO5pI43WuyAIOro\nzDJaotZltqeeeU2liSyqrdE3DqCJYv4=\n", "yOsXKYX6RdA=\n"), Long.valueOf(j), Long.valueOf(aPKExpansionPolicy.getExpansionFileSize(0))));
                    ApkXDownloaderClient.this.mListener.onDownloadStateChanged(false, false, false, false, true, 0, 6);
                } else if (Helpers.canWriteOBBFile(ApkXDownloaderClient.this.mActivity)) {
                    ApkXDownloaderClient.this.launchDownloader();
                } else {
                    ApkXDownloaderClient.this.mListener.onDownloadStateChanged(false, false, false, false, true, 0, 1);
                }
            }

            @Override // com.googleplay.licensing.LicenseCheckerCallback
            public void dontAllow(int i) {
//                Log.i(StringFog.decrypt("rNg3wllMu2mBxz3+eFGPa4TNMu4=\n", "7ahcmh0jzAc=\n"), String.format(StringFog.decrypt("5Lksg+Vo+uPAtSyN7mncwcS8LYfocL+NiLQgiP9a88zHp3XGrn8=\n", "qNBP5osbn6A=\n"), Integer.valueOf(i)));
                ApkXDownloaderClient.this.mListener.onDownloadStateChanged(false, false, false, false, true, 0, i != 291 ? i != 561 ? 0 : 2 : 7);
            }

            @Override // com.googleplay.licensing.LicenseCheckerCallback
            public void applicationError(int i) {
//                Log.i(StringFog.decrypt("rt2duHfcVCeDwpeEVsFgJYbImJQ=\n", "76324DOzI0k=\n"), String.format(StringFog.decrypt("7tDlcCh+4tDK3OV+I3/E8s7V5HQlZqe+gtz0Zyl/vbOH3Q==\n", "ormGFUYNh5M=\n"), Integer.valueOf(i)));
                ApkXDownloaderClient.this.mListener.onDownloadStateChanged(false, false, false, false, true, 0, 8);
            }
        });
    }

    @Override // com.mojang.minecraftpe.packagesource.PackageSource
    public void pauseDownload() {
        IDownloaderService iDownloaderService = this.mRemoteService;
        if (iDownloaderService != null) {
            iDownloaderService.requestPauseDownload();
        }
    }

    @Override // com.mojang.minecraftpe.packagesource.PackageSource
    public void resumeDownload() {
        IDownloaderService iDownloaderService = this.mRemoteService;
        if (iDownloaderService != null) {
            iDownloaderService.requestContinueDownload();
        }
    }

    @Override // com.mojang.minecraftpe.packagesource.PackageSource
    public void resumeDownloadOnCell() {
        IDownloaderService iDownloaderService = this.mRemoteService;
        if (iDownloaderService != null) {
            iDownloaderService.setDownloadFlags(1);
            this.mRemoteService.requestContinueDownload();
        }
    }

    @Override // com.mojang.minecraftpe.packagesource.PackageSource
    public void abortDownload() {
        IDownloaderService iDownloaderService = this.mRemoteService;
        if (iDownloaderService != null) {
            iDownloaderService.requestAbortDownload();
        }
    }

    public void launchDownloader() {
        this.mActivity.runOnUiThread(new Runnable() { // from class: com.mojang.minecraftpe.packagesource.googleplay.ApkXDownloaderClient.3
            IDownloaderClient client;

            @Override // java.lang.Runnable
            public void run() {
                int i;
                ApkXDownloaderClient.this.deleteObbFiles();
                ApkXDownloaderClient.this.mDownloaderClientStub = DownloaderClientMarshaller.CreateStub(this.client, ApkXDownloaderService.class);
                ApkXDownloaderClient.this.mDownloaderClientStub.connect(ApkXDownloaderClient.this.mActivity);
                Intent intent = ApkXDownloaderClient.this.mActivity.getIntent();
                Intent intent2 = new Intent(ApkXDownloaderClient.this.mActivity, ApkXDownloaderClient.this.mActivity.getClass());
                intent2.setFlags(FLAG_ACTIVITY_NEW_TASK | FLAG_ACTIVITY_CLEAR_TOP);
                intent2.setAction(intent.getAction());
                if (intent.getCategories() != null) {
                    for (String str : intent.getCategories()) {
                        intent2.addCategory(str);
                    }
                }
                if (Build.VERSION.SDK_INT >= 26) {
                    String stringResource = PackageSource.getStringResource(PackageSource.StringResourceId.NOTIFICATIONCHANNEL_NAME);
                    String stringResource2 = PackageSource.getStringResource(PackageSource.StringResourceId.NOTIFICATIONCHANNEL_DESCRIPTION);
                    NotificationChannel notificationChannel = new NotificationChannel(ApkXDownloaderClient.getNotificationChannelId(), stringResource, NotificationManager.IMPORTANCE_LOW);
                    notificationChannel.setDescription(stringResource2);
                    ApkXDownloaderClient.this.mNotificationManager.createNotificationChannel(notificationChannel);
                }
                try {
                    i = DownloaderClientMarshaller.startDownloadServiceIfRequired(ApkXDownloaderClient.this.mActivity, PendingIntent.getActivity(ApkXDownloaderClient.this.mActivity, 0, intent2, PendingIntent.FLAG_UPDATE_CURRENT), ApkXDownloaderService.class);
                } catch (Exception e) {
                    e.printStackTrace();
                    i = 0;
                }
                ApkXDownloaderClient.this.mListener.onDownloadStarted();
//                Log.i(StringFog.decrypt("hStLfe+oWWqoNEFBzrVtaK0+TlE=\n", "xFsgJavHLgQ=\n"), String.format(StringFog.decrypt("guGNylUEqleZ7pTLVwiLSs6t2NdCDZxMvOWL0VoYzh2K\n", "7oD4pDZs7jg=\n"), Integer.valueOf(i)));
                if (i == 0) {
                    ApkXDownloaderClient.this.mListener.onDownloadStateChanged(false, false, false, true, false, 0, 0);
                }
            }
        });
    }

    public void deleteObbFiles() {
        File[] listFiles;
        for (File file : new File(Helpers.getSaveFilePath(this.mActivity)).listFiles()) {
            String name = file.getName();
            if (name.endsWith(".obb")) {
                String str = LOG_TAG;
//                Log.i(str, String.format(StringFog.decrypt("DUFhqA/XHD4LYmShHsFzcUlAaKEexjoyDgRrpBfXc3ka\n", "aSQNzXuyU1w=\n"), name));
                if (!file.delete()) {
//                    Log.e(str, String.format(StringFog.decrypt("degI3I3ruVtzyw3VnP3WFDHrBdCV65IZZeJE3Zzik010rQLQlevWHGI=\n", "EY1kufmO9jk=\n"), name));
                }
            }
        }
    }

    @Override // com.google.android.vending.expansion.downloader.IDownloaderClient
    public void onServiceConnected(Messenger messenger) {
//        Log.i(LOG_TAG, StringFog.decrypt("E3WJ91bmzcYZWLX8SvXH0Rl/\n", "fBvakiSQpKU=\n"));
        IDownloaderService CreateProxy = DownloaderServiceMarshaller.CreateProxy(messenger);
        this.mRemoteService = CreateProxy;
        CreateProxy.onClientUpdated(this.mDownloaderClientStub.getMessenger());
    }

    @Override // com.google.android.vending.expansion.downloader.IDownloaderClient
    public void onDownloadStateChanged(int i) {
        boolean z;
        boolean z2;
        boolean z3;
        boolean z4;
        int i2;
        boolean z5;
        int convertStateToFailedReason;
        String stringResource = PackageSource.getStringResource(Helpers.getDownloaderStringResourceIDFromPlaystoreState(i));
        switch (i) {
            case 1:
            case 2:
            case 3:
                z = false;
                z2 = true;
                z3 = false;
                z4 = false;
                z5 = false;
                i2 = 0;
                convertStateToFailedReason = 0;
                break;
            case 4:
                z = false;
                z2 = false;
                z3 = false;
                z4 = false;
                z5 = false;
                i2 = 0;
                convertStateToFailedReason = 0;
                break;
            case 5:
                z = false;
                z2 = false;
                z3 = false;
                z4 = true;
                z5 = false;
                i2 = 0;
                convertStateToFailedReason = 0;
                break;
            case 6:
            case 7:
            case 8:
            case 9:
            case 10:
            case 11:
            case 12:
            case 13:
            case 14:
                int convertStateToPausedReason = convertStateToPausedReason(i);
                z = i == 8 || i == 9;
                i2 = convertStateToPausedReason;
                z2 = false;
                z3 = true;
                z4 = false;
                z5 = false;
                convertStateToFailedReason = 0;
                break;
            case 15:
            case 16:
            case 17:
            case 18:
            case 19:
                convertStateToFailedReason = convertStateToFailedReason(i);
                z = false;
                z2 = false;
                z3 = false;
                z4 = false;
                z5 = true;
                i2 = 0;
                break;
            default:
                z = false;
                z2 = true;
                z3 = true;
                z4 = false;
                z5 = false;
                i2 = 0;
                convertStateToFailedReason = 0;
                break;
        }
//        Log.i(LOG_TAG, String.format(StringFog.decrypt("S2zwOMRR55FFZucj0kvuvUxj2jDWW6vTBHHANsdasd4BcQ==\n", "JAK0V7M/i/4=\n"), stringResource));
        this.mListener.onDownloadStateChanged(z, z2, z3, z4, z5, i2, convertStateToFailedReason);
    }

    @Override // com.google.android.vending.expansion.downloader.IDownloaderClient
    public void onDownloadProgress(DownloadProgressInfo downloadProgressInfo) {
        long j = downloadProgressInfo.mOverallProgress;
        long j2 = downloadProgressInfo.mOverallTotal;
        float f = downloadProgressInfo.mCurrentSpeed;
        long j3 = downloadProgressInfo.mTimeRemaining;
//        Log.i(LOG_TAG, String.format(StringFog.decrypt("072+XkdKHm7dt6pDX0MAZM+g2hwQARYhk/PfVQ==\n", "vNP6MTAkcgE=\n"), Long.valueOf(j), Long.valueOf(j2)));
        this.mListener.onDownloadProgress(j, j2, f, j3);
    }

    @Override // com.mojang.minecraftpe.ActivityListener
    public void onResume() {
//        Log.i(LOG_TAG, StringFog.decrypt("1Ke35afFwn0=\n", "u8nlgNSwrxg=\n"));
        IStub iStub = this.mDownloaderClientStub;
        if (iStub != null) {
            iStub.connect(this.mActivity);
        }
    }

    @Override // com.mojang.minecraftpe.ActivityListener
    public void onStop() {
//        Log.i(LOG_TAG, StringFog.decrypt("hpWfresA\n", "6fvM2YRww+c=\n"));
        IStub iStub = this.mDownloaderClientStub;
        if (iStub != null) {
            iStub.disconnect(this.mActivity);
        }
    }

    @Override // com.mojang.minecraftpe.ActivityListener
    public void onDestroy() {
//        Log.i(LOG_TAG, StringFog.decrypt("T2WmF+qpIXBZ\n", "IAvicpndUx8=\n"));
        this.mActivity.removeListener(this);
    }

    @Override // com.mojang.minecraftpe.ActivityListener
    public void onActivityResult(int i, int i2, Intent intent) {
//        Log.i(LOG_TAG, StringFog.decrypt("G9W36SR5Y0AAwqTvI2V5XQ==\n", "dLv2ilAQFSk=\n"));
    }
}
