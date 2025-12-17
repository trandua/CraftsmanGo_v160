package com.google.android.vending.expansion.downloader.impl;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.IBinder;
import android.os.Messenger;
import android.os.SystemClock;
import android.provider.Settings;
import android.util.Log;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import com.google.android.vending.expansion.downloader.Constants;
import com.google.android.vending.expansion.downloader.DownloadProgressInfo;
import com.google.android.vending.expansion.downloader.DownloaderServiceMarshaller;
import com.google.android.vending.expansion.downloader.Helpers;
import com.google.android.vending.expansion.downloader.IDownloaderService;
import com.google.android.vending.expansion.downloader.IStub;
import com.googleplay.licensing.AESObfuscator;
import com.googleplay.licensing.APKExpansionPolicy;
import com.googleplay.licensing.LicenseChecker;
import com.googleplay.licensing.LicenseCheckerCallback;
import java.io.File;

/* loaded from: classes7.dex */
public abstract class DownloaderService extends CustomIntentService implements IDownloaderService {
    public static final String ACTION_DOWNLOADS_CHANGED = "downloadsChanged";
    public static final String ACTION_DOWNLOAD_COMPLETE = "lvldownloader.intent.action.DOWNLOAD_COMPLETE";
    public static final String ACTION_DOWNLOAD_STATUS = "lvldownloader.intent.action.DOWNLOAD_STATUS";
    public static final int CONTROL_PAUSED = 1;
    public static final int CONTROL_RUN = 0;
    public static final int DOWNLOAD_REQUIRED = 2;
    public static final String EXTRA_FILE_NAME = "downloadId";
    public static final String EXTRA_IS_WIFI_REQUIRED = "isWifiRequired";
    public static final String EXTRA_MESSAGE_HANDLER = "EMH";
    public static final String EXTRA_PACKAGE_NAME = "EPN";
    public static final String EXTRA_PENDING_INTENT = "EPI";
    public static final String EXTRA_STATUS_CURRENT_FILE_SIZE = "CFS";
    public static final String EXTRA_STATUS_CURRENT_PROGRESS = "CFP";
    public static final String EXTRA_STATUS_STATE = "ESS";
    public static final String EXTRA_STATUS_TOTAL_PROGRESS = "TFP";
    public static final String EXTRA_STATUS_TOTAL_SIZE = "ETS";
    private static final String LOG_TAG = "LVLDL";
    public static final int LVL_CHECK_REQUIRED = 1;
    public static final int NETWORK_CANNOT_USE_ROAMING = 5;
    public static final int NETWORK_MOBILE = 1;
    public static final int NETWORK_NO_CONNECTION = 2;
    public static final int NETWORK_OK = 1;
    public static final int NETWORK_RECOMMENDED_UNUSABLE_DUE_TO_SIZE = 4;
    public static final int NETWORK_TYPE_DISALLOWED_BY_REQUESTOR = 6;
    public static final int NETWORK_UNUSABLE_DUE_TO_SIZE = 3;
    public static final int NETWORK_WIFI = 2;
    public static final int NO_DOWNLOAD_REQUIRED = 0;
    private static final float SMOOTHING_FACTOR = 0.005f;
    public static final int STATUS_CANCELED = 490;
    public static final int STATUS_CANNOT_RESUME = 489;
    public static final int STATUS_DEVICE_NOT_FOUND_ERROR = 499;
    public static final int STATUS_FILE_ALREADY_EXISTS_ERROR = 488;
    public static final int STATUS_FILE_DELIVERED_INCORRECTLY = 487;
    public static final int STATUS_FILE_ERROR = 492;
    public static final int STATUS_FORBIDDEN = 403;
    public static final int STATUS_HTTP_DATA_ERROR = 495;
    public static final int STATUS_HTTP_EXCEPTION = 496;
    public static final int STATUS_INSUFFICIENT_SPACE_ERROR = 498;
    public static final int STATUS_PAUSED_BY_APP = 193;
    public static final int STATUS_PENDING = 190;
    public static final int STATUS_QUEUED_FOR_WIFI = 197;
    public static final int STATUS_QUEUED_FOR_WIFI_OR_CELLULAR_PERMISSION = 196;
    public static final int STATUS_RUNNING = 192;
    public static final int STATUS_SUCCESS = 200;
    public static final int STATUS_TOO_MANY_REDIRECTS = 497;
    public static final int STATUS_UNHANDLED_HTTP_CODE = 494;
    public static final int STATUS_UNHANDLED_REDIRECT = 493;
    public static final int STATUS_UNKNOWN_ERROR = 491;
    public static final int STATUS_WAITING_FOR_NETWORK = 195;
    public static final int STATUS_WAITING_TO_RETRY = 194;
    private static final String TEMP_EXT = ".tmp";
    public static final int VISIBILITY_HIDDEN = 2;
    public static final int VISIBILITY_VISIBLE = 0;
    public static final int VISIBILITY_VISIBLE_NOTIFY_COMPLETED = 1;
    private static boolean sIsRunning;
    private PendingIntent mAlarmIntent;
    float mAverageDownloadSpeed;
    long mBytesAtSample;
    long mBytesSoFar;
    private Messenger mClientMessenger;
    private BroadcastReceiver mConnReceiver;
    private ConnectivityManager mConnectivityManager;
    private int mControl;
    int mFileCount;
    private boolean mIsAtLeast3G;
    private boolean mIsAtLeast4G;
    private boolean mIsCellularConnection;
    private boolean mIsConnected;
    private boolean mIsFailover;
    private boolean mIsRoaming;
    long mMillisecondsAtSample;
    private DownloadNotification mNotification;
    private PackageInfo mPackageInfo;
    private PendingIntent mPendingIntent;
    private final Messenger mServiceMessenger;
    private final IStub mServiceStub;
    private boolean mStateChanged;
    private int mStatus;
    long mTotalLength;
    private WifiManager mWifiManager;

    /* loaded from: classes7.dex */
    public static class GenerateSaveFileError extends Exception {
        private static final long serialVersionUID = 3465966015408936540L;
        String mMessage;
        int mStatus;

        public GenerateSaveFileError(int status, String message) {
            this.mStatus = status;
            this.mMessage = message;
        }
    }

    /* loaded from: classes7.dex */
    private class InnerBroadcastReceiver extends BroadcastReceiver {
        final Service mService;

        InnerBroadcastReceiver(Service service) {
            this.mService = service;
        }

        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            DownloaderService.this.pollNetworkState();
            if (DownloaderService.this.mStateChanged && !DownloaderService.isServiceRunning()) {
                Log.d("LVLDL", "InnerBroadcastReceiver Called");
                Intent intent2 = new Intent(context, this.mService.getClass());
                intent2.putExtra(DownloaderService.EXTRA_PENDING_INTENT, DownloaderService.this.mPendingIntent);
                ContextCompat.startForegroundService(context, intent2);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes7.dex */
    public class LVLRunnable implements Runnable {
        final Context mContext;

        LVLRunnable(Context context, PendingIntent intent) {
            this.mContext = context;
            DownloaderService.this.mPendingIntent = intent;
        }

        @Override // java.lang.Runnable
        public void run() {
            DownloaderService.setServiceRunning(true);
            DownloaderService.this.mNotification.onDownloadStateChanged(2);
            final APKExpansionPolicy aPKExpansionPolicy = new APKExpansionPolicy(this.mContext, new AESObfuscator(DownloaderService.this.getSALT(), this.mContext.getPackageName(), Settings.Secure.getString(this.mContext.getContentResolver(), "android_id")));
            aPKExpansionPolicy.resetPolicy();
            new LicenseChecker(this.mContext, aPKExpansionPolicy, DownloaderService.this.getPublicKey()).checkAccess(new LicenseCheckerCallback() { // from class: com.google.android.vending.expansion.downloader.impl.DownloaderService.LVLRunnable.1
                @Override // com.googleplay.licensing.LicenseCheckerCallback
                public void allow(int reason) {
                    int i;
                    try {
                        int expansionURLCount = aPKExpansionPolicy.getExpansionURLCount();
                        DownloadsDB db = DownloadsDB.getDB(LVLRunnable.this.mContext);
                        if (expansionURLCount != 0) {
                            i = 0;
                            for (int i2 = 0; i2 < expansionURLCount; i2++) {
                                String expansionFileName = aPKExpansionPolicy.getExpansionFileName(i2);
                                if (expansionFileName != null) {
                                    DownloadInfo downloadInfo = new DownloadInfo(i2, expansionFileName, LVLRunnable.this.mContext.getPackageName());
                                    long expansionFileSize = aPKExpansionPolicy.getExpansionFileSize(i2);
                                    if (DownloaderService.this.handleFileUpdated(db, i2, expansionFileName, expansionFileSize)) {
                                        i |= -1;
                                        downloadInfo.resetDownload();
                                        downloadInfo.mUri = aPKExpansionPolicy.getExpansionURL(i2);
                                        downloadInfo.mTotalBytes = expansionFileSize;
                                        downloadInfo.mStatus = i;
                                        db.updateDownload(downloadInfo);
                                    } else {
                                        DownloadInfo downloadInfoByFileName = db.getDownloadInfoByFileName(downloadInfo.mFileName);
                                        if (downloadInfoByFileName == null) {
                                            Log.d("LVLDL", "file " + downloadInfo.mFileName + " found. Not downloading.");
                                            downloadInfo.mStatus = 200;
                                            downloadInfo.mTotalBytes = expansionFileSize;
                                            downloadInfo.mCurrentBytes = expansionFileSize;
                                            downloadInfo.mUri = aPKExpansionPolicy.getExpansionURL(i2);
                                            db.updateDownload(downloadInfo);
                                        } else if (downloadInfoByFileName.mStatus != 200) {
                                            downloadInfoByFileName.mUri = aPKExpansionPolicy.getExpansionURL(i2);
                                            db.updateDownload(downloadInfoByFileName);
                                            i |= -1;
                                        }
                                    }
                                }
                            }
                        } else {
                            i = 0;
                        }
                        try {
                            db.updateMetadata(LVLRunnable.this.mContext.getPackageManager().getPackageInfo(LVLRunnable.this.mContext.getPackageName(), 0).versionCode, i);
                            int startDownloadServiceIfRequired = DownloaderService.startDownloadServiceIfRequired(LVLRunnable.this.mContext, DownloaderService.this.mPendingIntent, DownloaderService.this.getClass());
                            if (startDownloadServiceIfRequired == 0) {
                                DownloaderService.this.mNotification.onDownloadStateChanged(5);
                            } else if (startDownloadServiceIfRequired == 1) {
                                Log.e("LVLDL", "In LVL checking loop!");
                                DownloaderService.this.mNotification.onDownloadStateChanged(15);
                                throw new RuntimeException("Error with LVL checking and database integrity");
                            }
                        } catch (PackageManager.NameNotFoundException e) {
                            e.printStackTrace();
                            throw new RuntimeException("Error with getting information from package name");
                        }
                    } finally {
                        DownloaderService.setServiceRunning(false);
                    }
                }

                @Override // com.googleplay.licensing.LicenseCheckerCallback
                public void applicationError(int errorCode) {
                    try {
                        DownloaderService.this.mNotification.onDownloadStateChanged(16);
                    } finally {
                        DownloaderService.setServiceRunning(false);
                    }
                }

                @Override // com.googleplay.licensing.LicenseCheckerCallback
                public void dontAllow(int reason) {
                    try {
                        if (reason != 291) {
                            if (reason == 561) {
                                DownloaderService.this.mNotification.onDownloadStateChanged(15);
                            }
                        }
                        DownloaderService.this.mNotification.onDownloadStateChanged(16);
                    } finally {
                        DownloaderService.setServiceRunning(false);
                    }
                }
            });
        }
    }

    public DownloaderService() {
        super("LVLDownloadService");
        IStub CreateStub = DownloaderServiceMarshaller.CreateStub(this);
        this.mServiceStub = CreateStub;
        this.mServiceMessenger = CreateStub.getMessenger();
    }

    private void cancelAlarms() {
        if (this.mAlarmIntent != null) {
            AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            if (alarmManager == null) {
                Log.e("LVLDL", "couldn't get alarm manager");
                return;
            }
            alarmManager.cancel(this.mAlarmIntent);
            this.mAlarmIntent = null;
        }
    }

    private static boolean isLVLCheckRequired(DownloadsDB db, PackageInfo pi) {
        return db.mVersionCode != pi.versionCode;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static synchronized boolean isServiceRunning() {
        boolean z;
        synchronized (DownloaderService.class) {
            z = sIsRunning;
        }
        return z;
    }

    public static boolean isStatusClientError(int status) {
        return status >= 400 && status < 500;
    }

    public static boolean isStatusCompleted(int status) {
        return (status >= 200 && status < 300) || (status >= 400 && status < 600);
    }

    public static boolean isStatusError(int status) {
        return status >= 400 && status < 600;
    }

    public static boolean isStatusInformational(int status) {
        return status >= 100 && status < 200;
    }

    public static boolean isStatusServerError(int status) {
        return status >= 500 && status < 600;
    }

    public static boolean isStatusSuccess(int status) {
        return status >= 200 && status < 300;
    }

    private void scheduleAlarm(long wakeUp) {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        if (alarmManager == null) {
            Log.e("LVLDL", "couldn't get alarm manager");
            return;
        }
        String alarmReceiverClassName = getAlarmReceiverClassName();
        Intent intent = new Intent(Constants.ACTION_RETRY);
        intent.putExtra(EXTRA_PENDING_INTENT, this.mPendingIntent);
        intent.setClassName(getPackageName(), alarmReceiverClassName);
        this.mAlarmIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);
        alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + wakeUp, this.mAlarmIntent);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static synchronized void setServiceRunning(boolean isRunning) {
        synchronized (DownloaderService.class) {
            sIsRunning = isRunning;
        }
    }

    public static int startDownloadServiceIfRequired(Context context, PendingIntent pendingIntent, Class<?> serviceClass) throws PackageManager.NameNotFoundException {
        return startDownloadServiceIfRequired(context, pendingIntent, context.getPackageName(), serviceClass.getName());
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r0v2, types: [boolean] */
    /* JADX WARN: Type inference failed for: r0v3, types: [int] */
    /* JADX WARN: Type inference failed for: r0v4 */
    /* JADX WARN: Unknown variable types count: 1 */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public static int startDownloadServiceIfRequired(android.content.Context r11, android.app.PendingIntent r12, java.lang.String r13, java.lang.String r14) throws android.content.pm.PackageManager.NameNotFoundException {
        /*
            android.content.pm.PackageManager r0 = r11.getPackageManager()
            java.lang.String r1 = r11.getPackageName()
            r2 = 0
            android.content.pm.PackageInfo r0 = r0.getPackageInfo(r1, r2)
            com.google.android.vending.expansion.downloader.impl.DownloadsDB r1 = com.google.android.vending.expansion.downloader.impl.DownloadsDB.getDB(r11)
            boolean r0 = isLVLCheckRequired(r1, r0)
            int r3 = r1.mStatus
            r4 = 1
            r5 = 2
            if (r3 != 0) goto L_0x0034
            com.google.android.vending.expansion.downloader.impl.DownloadInfo[] r3 = r1.getDownloads()
            if (r3 == 0) goto L_0x0039
            int r6 = r3.length
        L_0x0022:
            if (r2 >= r6) goto L_0x0039
            r7 = r3[r2]
            java.lang.String r8 = r7.mFileName
            long r9 = r7.mTotalBytes
            boolean r7 = com.google.android.vending.expansion.downloader.Helpers.doesFileExist(r11, r8, r9, r4)
            if (r7 != 0) goto L_0x0036
            r0 = -1
            r1.updateStatus(r0)
        L_0x0034:
            r0 = 2
            goto L_0x0039
        L_0x0036:
            int r2 = r2 + 1
            goto L_0x0022
        L_0x0039:
            if (r0 == r4) goto L_0x003e
            if (r0 == r5) goto L_0x003e
            goto L_0x004e
        L_0x003e:
            android.content.Intent r1 = new android.content.Intent
            r1.<init>()
            r1.setClassName(r13, r14)
            java.lang.String r13 = "EPI"
            r1.putExtra(r13, r12)
            androidx.core.content.ContextCompat.startForegroundService(r11, r1)
        L_0x004e:
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.vending.expansion.downloader.impl.DownloaderService.startDownloadServiceIfRequired(android.content.Context, android.app.PendingIntent, java.lang.String, java.lang.String):int");
    }

    public static int startDownloadServiceIfRequired(Context context, Intent intent, Class<?> serviceClass) throws PackageManager.NameNotFoundException {
        return startDownloadServiceIfRequired(context, (PendingIntent) intent.getParcelableExtra(EXTRA_PENDING_INTENT), serviceClass);
    }

    private void updateNetworkState(NetworkInfo info) {
        boolean z = this.mIsConnected;
        boolean z2 = this.mIsFailover;
        boolean z3 = this.mIsCellularConnection;
        boolean z4 = this.mIsRoaming;
        boolean z5 = this.mIsAtLeast3G;
        boolean z6 = false;
        if (info != null) {
            this.mIsRoaming = info.isRoaming();
            this.mIsFailover = info.isFailover();
            this.mIsConnected = info.isConnected();
            updateNetworkType(info.getType(), info.getSubtype());
        } else {
            this.mIsRoaming = false;
            this.mIsFailover = false;
            this.mIsConnected = false;
            updateNetworkType(-1, -1);
        }
        if (!(!this.mStateChanged && z == this.mIsConnected && z2 == this.mIsFailover && z3 == this.mIsCellularConnection && z4 == this.mIsRoaming && z5 == this.mIsAtLeast3G)) {
            z6 = true;
        }
        this.mStateChanged = z6;
    }

    private void updateNetworkType(int type, int subType) {
        if (type != 0) {
            if (type != 1) {
                if (type == 6) {
                    this.mIsCellularConnection = true;
                    this.mIsAtLeast3G = true;
                    this.mIsAtLeast4G = true;
                    return;
                } else if (!(type == 7 || type == 9)) {
                    return;
                }
            }
            this.mIsCellularConnection = false;
            this.mIsAtLeast3G = false;
            this.mIsAtLeast4G = false;
            return;
        }
        this.mIsCellularConnection = true;
        switch (subType) {
            case 1:
            case 2:
            case 4:
            case 7:
            case 11:
                this.mIsAtLeast3G = false;
                this.mIsAtLeast4G = false;
                return;
            case 3:
            case 5:
            case 6:
            case 8:
            case 9:
            case 10:
                this.mIsAtLeast3G = true;
                this.mIsAtLeast4G = false;
                return;
            case 12:
            default:
                this.mIsCellularConnection = false;
                this.mIsAtLeast3G = false;
                this.mIsAtLeast4G = false;
                return;
            case 13:
            case 14:
            case 15:
                this.mIsAtLeast3G = true;
                this.mIsAtLeast4G = true;
                return;
        }
    }

    public String generateSaveFile(String filename, long filesize) throws GenerateSaveFileError {
        String generateTempSaveFileName = generateTempSaveFileName(filename);
        File file = new File(generateTempSaveFileName);
        if (!Helpers.isExternalMediaMounted()) {
            Log.d("LVLDL", "External media not mounted: " + generateTempSaveFileName);
            throw new GenerateSaveFileError(499, "external media is not yet mounted");
        } else if (file.exists()) {
            Log.d("LVLDL", "File already exists: " + generateTempSaveFileName);
            throw new GenerateSaveFileError(488, "requested destination file already exists");
        } else if (Helpers.getAvailableBytes(Helpers.getFilesystemRoot(generateTempSaveFileName)) >= filesize) {
            return generateTempSaveFileName;
        } else {
            throw new GenerateSaveFileError(498, "insufficient space on external storage");
        }
    }

    public String generateTempSaveFileName(String fileName) {
        return Helpers.getSaveFilePath(this) + File.separator + fileName + TEMP_EXT;
    }

    public abstract String getAlarmReceiverClassName();

    public int getControl() {
        return this.mControl;
    }

    public String getLogMessageForNetworkError(int networkError) {
        return networkError != 2 ? networkError != 3 ? networkError != 4 ? networkError != 5 ? networkError != 6 ? "unknown error with network connectivity" : "download was requested to not use the current network type" : "download cannot use the current network connection because it is roaming" : "download size exceeds recommended limit for mobile network" : "download size exceeds limit for mobile network" : "no network connection available";
    }

    public int getNetworkAvailabilityState(DownloadsDB db) {
        if (!this.mIsConnected) {
            return 2;
        }
        if (!this.mIsCellularConnection) {
            return 1;
        }
        int i = db.mFlags;
        if (this.mIsRoaming) {
            return 5;
        }
        return (i & 1) != 0 ? 1 : 6;
    }

    public abstract String getNotificationChannelId();

    public abstract String getPublicKey();

    public abstract byte[] getSALT();

    public int getStatus() {
        return this.mStatus;
    }

    public boolean handleFileUpdated(DownloadsDB db, int index, String filename, long fileSize) {
        String str;
        DownloadInfo downloadInfoByFileName = db.getDownloadInfoByFileName(filename);
        if (!(downloadInfoByFileName == null || (str = downloadInfoByFileName.mFileName) == null)) {
            if (filename.equals(str)) {
                return false;
            }
            File file = new File(Helpers.generateSaveFileName(this, str));
            if (file.exists()) {
                file.delete();
            }
        }
        return true ^ Helpers.doesFileExist(this, filename, fileSize, true);
    }

    public boolean isWiFi() {
        return this.mIsConnected && !this.mIsCellularConnection;
    }

    public void notifyUpdateBytes(long totalBytesSoFar) {
        long j;
        long uptimeMillis = SystemClock.uptimeMillis();
        long j2 = this.mMillisecondsAtSample;
        if (0 != j2) {
            float f = ((float) (totalBytesSoFar - this.mBytesAtSample)) / ((float) (uptimeMillis - j2));
            float f2 = this.mAverageDownloadSpeed;
            if (0.0f != f2) {
                this.mAverageDownloadSpeed = (f * SMOOTHING_FACTOR) + (f2 * 0.995f);
            } else {
                this.mAverageDownloadSpeed = f;
            }
            j = (long) (((float) (this.mTotalLength - totalBytesSoFar)) / this.mAverageDownloadSpeed);
        } else {
            j = -1;
        }
        this.mMillisecondsAtSample = uptimeMillis;
        this.mBytesAtSample = totalBytesSoFar;
        this.mNotification.onDownloadProgress(new DownloadProgressInfo(this.mTotalLength, totalBytesSoFar, j, this.mAverageDownloadSpeed));
    }

    @Override // com.google.android.vending.expansion.downloader.impl.CustomIntentService, android.app.Service
    public IBinder onBind(Intent paramIntent) {
        Log.d("LVLDL", "Service Bound");
        return this.mServiceMessenger.getBinder();
    }

    @Override // com.google.android.vending.expansion.downloader.IDownloaderService
    public void onClientUpdated(Messenger clientMessenger) {
        this.mClientMessenger = clientMessenger;
        this.mNotification.setMessenger(clientMessenger);
    }

    @Override // com.google.android.vending.expansion.downloader.impl.CustomIntentService, android.app.Service
    public void onCreate() {
        super.onCreate();
        try {
            this.mPackageInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            DownloadNotification downloadNotification = new DownloadNotification(this, getNotificationChannelId(), getPackageManager().getApplicationLabel(getApplicationInfo()));
            this.mNotification = downloadNotification;
            downloadNotification.startForeground();
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override // com.google.android.vending.expansion.downloader.impl.CustomIntentService, android.app.Service
    public void onDestroy() {
        BroadcastReceiver broadcastReceiver = this.mConnReceiver;
        if (broadcastReceiver != null) {
            unregisterReceiver(broadcastReceiver);
            this.mConnReceiver = null;
        }
        this.mServiceStub.disconnect(this);
        super.onDestroy();
    }

    @Override // com.google.android.vending.expansion.downloader.impl.CustomIntentService
    protected void onHandleIntent(Intent intent) {
        int i;
        boolean z = true;
        setServiceRunning(true);
        try {
            DownloadsDB db = DownloadsDB.getDB(this);
            PendingIntent pendingIntent = (PendingIntent) intent.getParcelableExtra(EXTRA_PENDING_INTENT);
            if (pendingIntent != null) {
                this.mNotification.setClientIntent(pendingIntent);
                this.mPendingIntent = pendingIntent;
            } else {
                PendingIntent pendingIntent2 = this.mPendingIntent;
                if (pendingIntent2 != null) {
                    this.mNotification.setClientIntent(pendingIntent2);
                } else {
                    Log.e("LVLDL", "Downloader started in bad state without notification intent.");
                    return;
                }
            }
            if (isLVLCheckRequired(db, this.mPackageInfo)) {
                updateLVL(this);
                return;
            }
            DownloadInfo[] downloads = db.getDownloads();
            this.mBytesSoFar = 0L;
            this.mTotalLength = 0L;
            this.mFileCount = downloads.length;
            for (DownloadInfo downloadInfo : downloads) {
                if (downloadInfo.mStatus == 200 && !Helpers.doesFileExist(this, downloadInfo.mFileName, downloadInfo.mTotalBytes, true)) {
                    downloadInfo.mStatus = 0;
                    downloadInfo.mCurrentBytes = 0L;
                }
                this.mTotalLength += downloadInfo.mTotalBytes;
                this.mBytesSoFar += downloadInfo.mCurrentBytes;
            }
            pollNetworkState();
            if (this.mConnReceiver == null) {
                this.mConnReceiver = new InnerBroadcastReceiver(this);
                IntentFilter intentFilter = new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE");
                intentFilter.addAction("android.net.wifi.WIFI_STATE_CHANGED");
                registerReceiver(this.mConnReceiver, intentFilter);
            }
            for (DownloadInfo downloadInfo2 : downloads) {
                long j = downloadInfo2.mCurrentBytes;
                Log.v("LVLDL", "onHandleIntent downloadInfo status: " + downloadInfo2.mStatus);
                if (downloadInfo2.mStatus != 200) {
                    DownloadThread downloadThread = new DownloadThread(downloadInfo2, this, this.mNotification);
                    cancelAlarms();
                    scheduleAlarm(5000L);
                    downloadThread.run();
                    cancelAlarms();
                }
                db.updateFromDb(downloadInfo2);
                int i2 = downloadInfo2.mStatus;
                if (i2 == 200) {
                    this.mBytesSoFar += downloadInfo2.mCurrentBytes - j;
                    db.updateMetadata(this.mPackageInfo.versionCode, 0);
                } else if (i2 != 403) {
                    if (i2 == 487) {
                        downloadInfo2.mCurrentBytes = 0L;
                        db.updateDownload(downloadInfo2);
                        i = 13;
                    } else if (i2 == 490) {
                        i = 18;
                    } else if (i2 == 498) {
                        i = 17;
                    } else if (i2 != 499) {
                        switch (i2) {
                            case 193:
                                z = false;
                                i = 7;
                                break;
                            case 194:
                            case 195:
                                i = 6;
                                break;
                            case 196:
                            case 197:
                                WifiManager wifiManager = this.mWifiManager;
                                if (wifiManager != null && !wifiManager.isWifiEnabled()) {
                                    i = 8;
                                    break;
                                } else {
                                    i = 9;
                                    break;
                                }
                            default:
                                z = false;
                                i = 19;
                                break;
                        }
                    } else {
                        i = 14;
                    }
                    if (z) {
                        scheduleAlarm(60000L);
                    } else {
                        cancelAlarms();
                    }
                    this.mNotification.onDownloadStateChanged(i);
                    return;
                } else {
                    updateLVL(this);
                    return;
                }
            }
            this.mNotification.onDownloadStateChanged(5);
        } finally {
            setServiceRunning(false);
        }
    }

    @Override // com.google.android.vending.expansion.downloader.impl.CustomIntentService, android.app.Service
    public int onStartCommand(Intent paramIntent, int flags, int startId) {
        int onStartCommand = super.onStartCommand(paramIntent, flags, startId);
        DownloadNotification downloadNotification = this.mNotification;
        if (downloadNotification != null) {
            downloadNotification.startForeground();
        }
        return onStartCommand;
    }

    void pollNetworkState() {
        if (this.mConnectivityManager == null) {
            this.mConnectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        }
        if (this.mWifiManager == null) {
            this.mWifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        }
        ConnectivityManager connectivityManager = this.mConnectivityManager;
        if (connectivityManager == null) {
            Log.w("LVLDL", "couldn't get connectivity manager to poll network state");
        } else {
            updateNetworkState(connectivityManager.getActiveNetworkInfo());
        }
    }

    @Override // com.google.android.vending.expansion.downloader.IDownloaderService
    public void requestAbortDownload() {
        this.mControl = 1;
        this.mStatus = 490;
    }

    @Override // com.google.android.vending.expansion.downloader.IDownloaderService
    public void requestContinueDownload() {
        if (this.mControl == 1) {
            this.mControl = 0;
        }
        Intent intent = new Intent(this, getClass());
        intent.putExtra(EXTRA_PENDING_INTENT, this.mPendingIntent);
        ContextCompat.startForegroundService(this, intent);
    }

    @Override // com.google.android.vending.expansion.downloader.IDownloaderService
    public void requestDownloadStatus() {
        this.mNotification.resendState();
    }

    @Override // com.google.android.vending.expansion.downloader.IDownloaderService
    public void requestPauseDownload() {
        this.mControl = 1;
        this.mStatus = 193;
    }

    @Override // com.google.android.vending.expansion.downloader.IDownloaderService
    public void setDownloadFlags(int flags) {
        DownloadsDB.getDB(this).updateFlags(flags);
    }

    @Override // com.google.android.vending.expansion.downloader.impl.CustomIntentService
    protected boolean shouldStop() {
        return DownloadsDB.getDB(this).mStatus == 0;
    }

    public void updateLVL(final Context context) {
        Context applicationContext = context.getApplicationContext();
        new Handler(applicationContext.getMainLooper()).post(new LVLRunnable(applicationContext, this.mPendingIntent));
    }
}
