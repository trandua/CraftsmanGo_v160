package com.google.android.vending.expansion.downloader;

import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.os.SystemClock;
import android.util.Log;
import com.mojang.minecraftpe.packagesource.PackageSource;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Random;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/* loaded from: classes7.dex */
public class Helpers {
    public static final int FS_CANNOT_READ = 2;
    public static final int FS_DOES_NOT_EXIST = 1;
    public static final int FS_READABLE = 0;
    public static Random sRandom = new Random(SystemClock.uptimeMillis());
    private static final Pattern CONTENT_DISPOSITION_PATTERN = Pattern.compile("attachment;\\s*filename\\s*=\\s*\"([^\"]*)\"");

    private Helpers() {
    }

    public static boolean canWriteOBBFile(Context c) {
        File file = new File(getSaveFilePath(c));
        return file.exists() ? file.isDirectory() && file.canWrite() : file.mkdirs();
    }

    static void deleteFile(String path) {
        try {
            new File(path).delete();
        } catch (Exception e) {
            Log.w(Constants.TAG, "file: '" + path + "' couldn't be deleted", e);
        }
    }

    public static boolean doesFileExist(Context c, String fileName, long fileSize, boolean deleteFileOnMismatch) {
        File file = new File(generateSaveFileName(c, fileName));
        if (!file.exists()) {
            return false;
        }
        if (file.length() == fileSize) {
            return true;
        }
        if (!deleteFileOnMismatch) {
            return false;
        }
        file.delete();
        return false;
    }

    public static String generateSaveFileName(Context c, String fileName) {
        return getSaveFilePath(c) + File.separator + fileName;
    }

    public static long getAvailableBytes(File root) {
        StatFs statFs = new StatFs(root.getPath());
        return statFs.getBlockSize() * (statFs.getAvailableBlocks() - 4);
    }

    public static String getDownloadProgressPercent(long overallProgress, long overallTotal) {
        if (overallTotal == 0) {
            return "";
        }
        return Long.toString((overallProgress * 100) / overallTotal) + "%";
    }

    public static String getDownloadProgressString(long overallProgress, long overallTotal) {
        if (overallTotal == 0) {
            return "";
        }
        return String.format("%.2f", Float.valueOf(((float) overallProgress) / 1048576.0f)) + "MB /" + String.format("%.2f", Float.valueOf(((float) overallTotal) / 1048576.0f)) + "MB";
    }

    public static String getDownloadProgressStringNotification(long overallProgress, long overallTotal) {
        if (overallTotal == 0) {
            return "";
        }
        return getDownloadProgressString(overallProgress, overallTotal) + " (" + getDownloadProgressPercent(overallProgress, overallTotal) + ")";
    }

    public static PackageSource.StringResourceId getDownloaderStringResourceIDFromPlaystoreState(int state) {
        switch (state) {
            case 1:
                return PackageSource.StringResourceId.STATE_IDLE;
            case 2:
                return PackageSource.StringResourceId.STATE_FETCHING_URL;
            case 3:
                return PackageSource.StringResourceId.STATE_CONNECTING;
            case 4:
                return PackageSource.StringResourceId.STATE_DOWNLOADING;
            case 5:
                return PackageSource.StringResourceId.STATE_COMPLETED;
            case 6:
                return PackageSource.StringResourceId.STATE_PAUSED_NETWORK_UNAVAILABLE;
            case 7:
                return PackageSource.StringResourceId.STATE_PAUSED_BY_REQUEST;
            case 8:
                return PackageSource.StringResourceId.STATE_PAUSED_WIFI_DISABLED;
            case 9:
                return PackageSource.StringResourceId.STATE_PAUSED_WIFI_UNAVAILABLE;
            case 10:
                return PackageSource.StringResourceId.STATE_PAUSED_WIFI_DISABLED;
            case 11:
                return PackageSource.StringResourceId.STATE_PAUSED_WIFI_UNAVAILABLE;
            case 12:
                return PackageSource.StringResourceId.STATE_PAUSED_ROAMING;
            case 13:
                return PackageSource.StringResourceId.STATE_PAUSED_NETWORK_SETUP_FAILURE;
            case 14:
                return PackageSource.StringResourceId.STATE_PAUSED_SDCARD_UNAVAILABLE;
            case 15:
                return PackageSource.StringResourceId.STATE_FAILED_UNLICENSED;
            case 16:
                return PackageSource.StringResourceId.STATE_FAILED_FETCHING_URL;
            case 17:
                return PackageSource.StringResourceId.STATE_FAILED_SDCARD_FULL;
            case 18:
                return PackageSource.StringResourceId.STATE_FAILED_CANCELLED;
            default:
                return PackageSource.StringResourceId.STATE_UNKNOWN;
        }
    }

    public static String getExpansionAPKFileName(Context c, boolean mainFile, int versionCode) {
        StringBuilder sb = new StringBuilder();
        sb.append(mainFile ? "main." : "patch.");
        sb.append(versionCode);
        sb.append(".");
        sb.append(c.getPackageName());
        sb.append(".obb");
        return sb.toString();
    }

    public static int getFileStatus(Context c, String fileName) {
        File file = new File(generateSaveFileName(c, fileName));
        if (file.exists()) {
            return file.canRead() ? 0 : 2;
        }
        return 1;
    }

    public static File getFilesystemRoot(String path) {
        File downloadCacheDirectory = Environment.getDownloadCacheDirectory();
        if (path.startsWith(downloadCacheDirectory.getPath())) {
            return downloadCacheDirectory;
        }
        File externalStorageDirectory = Environment.getExternalStorageDirectory();
        if (path.startsWith(externalStorageDirectory.getPath())) {
            return externalStorageDirectory;
        }
        throw new IllegalArgumentException("Cannot determine filesystem root for " + path);
    }

    public static String getSaveFilePath(Context c) {
        if (Build.VERSION.SDK_INT >= 19) {
            return c.getObbDir().toString();
        }
        File externalStorageDirectory = Environment.getExternalStorageDirectory();
        return externalStorageDirectory.toString() + Constants.EXP_PATH + c.getPackageName();
    }

    public static String getSpeedString(float bytesPerMillisecond) {
        return String.format("%.2f", Float.valueOf((bytesPerMillisecond * 1000.0f) / 1024.0f));
    }

    public static String getTimeRemaining(long durationInMilliseconds) {
        return (durationInMilliseconds > 3600000 ? new SimpleDateFormat("HH:mm", Locale.getDefault()) : new SimpleDateFormat("mm:ss", Locale.getDefault())).format(new Date(durationInMilliseconds - TimeZone.getDefault().getRawOffset()));
    }

    public static boolean isExternalMediaMounted() {
        return Environment.getExternalStorageState().equals("mounted");
    }

    public static boolean isFilenameValid(String filename) {
        String replaceFirst = filename.replaceFirst("/+", "/");
        return replaceFirst.startsWith(Environment.getDownloadCacheDirectory().toString()) || replaceFirst.startsWith(Environment.getExternalStorageDirectory().toString());
    }

    static String parseContentDisposition(String contentDisposition) {
        try {
            Matcher matcher = CONTENT_DISPOSITION_PATTERN.matcher(contentDisposition);
            if (matcher.find()) {
                return matcher.group(1);
            }
            return null;
        } catch (IllegalStateException unused) {
            return null;
        }
    }
}
