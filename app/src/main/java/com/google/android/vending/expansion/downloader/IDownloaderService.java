package com.google.android.vending.expansion.downloader;

import android.os.Messenger;

/* loaded from: classes7.dex */
public interface IDownloaderService {
    public static final int FLAGS_DOWNLOAD_OVER_CELLULAR = 1;

    void onClientUpdated(Messenger clientMessenger);

    void requestAbortDownload();

    void requestContinueDownload();

    void requestDownloadStatus();

    void requestPauseDownload();

    void setDownloadFlags(int flags);
}
