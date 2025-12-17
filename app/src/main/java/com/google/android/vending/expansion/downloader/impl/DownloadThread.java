package com.google.android.vending.expansion.downloader.impl;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.os.PowerManager;
import android.os.Process;
import android.util.Log;
import com.google.android.vending.expansion.downloader.Constants;
import com.google.android.vending.expansion.downloader.Helpers;
import com.google.android.vending.expansion.downloader.impl.DownloaderService;
import com.microsoft.aad.adal.AuthenticationConstants;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Locale;
import org.apache.http.HttpStatus;
import org.apache.http.protocol.HTTP;

/* loaded from: classes7.dex */
public class DownloadThread {
    private Context mContext;
    private final DownloadsDB mDB;
    private DownloadInfo mInfo;
    private final DownloadNotification mNotification;
    private DownloaderService mService;
    private String mUserAgent;

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes7.dex */
    public static class InnerState {
        public int mBytesNotified;
        public int mBytesSoFar;
        public int mBytesThisSession;
        public boolean mContinuingDownload;
        public String mHeaderContentDisposition;
        public String mHeaderContentLength;
        public String mHeaderContentLocation;
        public String mHeaderETag;
        public long mTimeLastNotification;

        private InnerState() {
            this.mBytesSoFar = 0;
            this.mBytesThisSession = 0;
            this.mContinuingDownload = false;
            this.mBytesNotified = 0;
            this.mTimeLastNotification = 0L;
        }
    }

    /* loaded from: classes7.dex */
    private class RetryDownload extends Throwable {
        private static final long serialVersionUID = 6196036036517540229L;

        private RetryDownload() {
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes7.dex */
    public static class State {
        public String mFilename;
        public String mNewUri;
        public int mRedirectCount;
        public String mRequestUri;
        public FileOutputStream mStream;
        public boolean mCountRetry = false;
        public int mRetryAfter = 0;
        public boolean mGotData = false;

        public State(DownloadInfo info, DownloaderService service) {
            this.mRedirectCount = 0;
            this.mRedirectCount = info.mRedirectCount;
            this.mRequestUri = info.mUri;
            this.mFilename = service.generateTempSaveFileName(info.mFileName);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes7.dex */
    public class StopRequest extends Throwable {
        private static final long serialVersionUID = 6338592678988347973L;
        public int mFinalStatus;

        public StopRequest(int finalStatus, String message) {
            super(message);
            this.mFinalStatus = finalStatus;
        }

        public StopRequest(int finalStatus, String message, Throwable throwable) {
            super(message, throwable);
            this.mFinalStatus = finalStatus;
        }
    }

    public DownloadThread(DownloadInfo info, DownloaderService service, DownloadNotification notification) {
        this.mContext = service;
        this.mInfo = info;
        this.mService = service;
        this.mNotification = notification;
        this.mDB = DownloadsDB.getDB(service);
        this.mUserAgent = "APKXDL (Linux; U; Android " + Build.VERSION.RELEASE + AuthenticationConstants.Broker.CHALLENGE_REQUEST_CERT_AUTH_DELIMETER + Locale.getDefault().toString() + "; " + Build.DEVICE + "/" + Build.ID + ")" + service.getPackageName();
    }

    private void addRequestHeaders(InnerState innerState, HttpURLConnection request) {
        if (innerState.mContinuingDownload) {
            if (innerState.mHeaderETag != null) {
                request.setRequestProperty("If-Match", innerState.mHeaderETag);
            }
            request.setRequestProperty("Range", "bytes=" + innerState.mBytesSoFar + Constants.FILENAME_SEQUENCE_SEPARATOR);
        }
    }

    private boolean cannotResume(InnerState innerState) {
        return innerState.mBytesSoFar > 0 && innerState.mHeaderETag == null;
    }

    private void checkConnectivity(State state) throws StopRequest {
        int networkAvailabilityState = this.mService.getNetworkAvailabilityState(this.mDB);
        if (networkAvailabilityState == 2) {
            throw new StopRequest(195, "waiting for network to return");
        } else if (networkAvailabilityState == 3) {
            throw new StopRequest(197, "waiting for wifi");
        } else if (networkAvailabilityState == 5) {
            throw new StopRequest(195, "roaming is not allowed");
        } else if (networkAvailabilityState == 6) {
            throw new StopRequest(196, "waiting for wifi or for download over cellular to be authorized");
        }
    }

    private void checkPausedOrCanceled(State state) throws StopRequest {
        if (this.mService.getControl() == 1 && this.mService.getStatus() == 193) {
            throw new StopRequest(this.mService.getStatus(), "download paused");
        }
    }

    private void cleanupDestination(State state, int finalStatus) {
        closeDestination(state);
        if (state.mFilename != null && DownloaderService.isStatusError(finalStatus)) {
            new File(state.mFilename).delete();
            state.mFilename = null;
        }
    }

    private void closeDestination(State state) {
        try {
            if (state.mStream != null) {
                state.mStream.close();
                state.mStream = null;
            }
        } catch (IOException unused) {
        }
    }

    private void executeDownload(State state, HttpURLConnection request) throws StopRequest, RetryDownload {
        InnerState innerState = new InnerState();
        checkPausedOrCanceled(state);
        setupDestinationFile(state, innerState);
        addRequestHeaders(innerState, request);
        checkConnectivity(state);
        this.mNotification.onDownloadStateChanged(3);
        handleExceptionalStatus(state, innerState, request, sendRequest(state, request));
        processResponseHeaders(state, innerState, request);
        InputStream openResponseEntity = openResponseEntity(state, request);
        this.mNotification.onDownloadStateChanged(4);
        transferData(state, innerState, new byte[4096], openResponseEntity);
    }

    private void finalizeDestinationFile(State state) throws StopRequest {
        syncDestination(state);
        String str = state.mFilename;
        String generateSaveFileName = Helpers.generateSaveFileName(this.mService, this.mInfo.mFileName);
        if (!state.mFilename.equals(generateSaveFileName)) {
            File file = new File(str);
            File file2 = new File(generateSaveFileName);
            if (this.mInfo.mTotalBytes == -1 || this.mInfo.mCurrentBytes != this.mInfo.mTotalBytes) {
                throw new StopRequest(DownloaderService.STATUS_FILE_DELIVERED_INCORRECTLY, "file delivered with incorrect size. probably due to network not browser configured");
            } else if (!file.renameTo(file2)) {
                throw new StopRequest(492, "unable to finalize destination file");
            }
        }
    }

    private int getFinalStatusForHttpError(State state) {
        if (this.mService.getNetworkAvailabilityState(this.mDB) != 1) {
            return 195;
        }
        if (this.mInfo.mNumFailed < 5) {
            state.mCountRetry = true;
            return 194;
        }
        Log.w(Constants.TAG, "reached max retries for " + this.mInfo.mNumFailed);
        return 495;
    }

    private void handleEndOfStream(State state, InnerState innerState) throws StopRequest {
        this.mInfo.mCurrentBytes = innerState.mBytesSoFar;
        this.mDB.updateDownload(this.mInfo);
        if (!((innerState.mHeaderContentLength == null || innerState.mBytesSoFar == Integer.parseInt(innerState.mHeaderContentLength)) ? false : true)) {
            return;
        }
        if (cannotResume(innerState)) {
            throw new StopRequest(489, "mismatched content length");
        }
        throw new StopRequest(getFinalStatusForHttpError(state), "closed socket before end of file");
    }

    private void handleExceptionalStatus(State state, InnerState innerState, HttpURLConnection connection, int responseCode) throws StopRequest, RetryDownload {
        if (responseCode == 503 && this.mInfo.mNumFailed < 5) {
            handleServiceUnavailable(state, connection);
        }
        if (responseCode != (innerState.mContinuingDownload ? HttpStatus.SC_PARTIAL_CONTENT : 200)) {
            handleOtherStatus(state, innerState, responseCode);
        } else {
            state.mRedirectCount = 0;
        }
    }

    private void handleOtherStatus(State state, InnerState innerState, int statusCode) throws StopRequest {
        int i = !DownloaderService.isStatusError(statusCode) ? (statusCode < 300 || statusCode >= 400) ? (!innerState.mContinuingDownload || statusCode != 200) ? 494 : 489 : 493 : statusCode;
        throw new StopRequest(i, "http error " + statusCode);
    }

    private void handleServiceUnavailable(State state, HttpURLConnection connection) throws StopRequest {
        state.mCountRetry = true;
        String headerField = connection.getHeaderField("Retry-After");
        if (headerField != null) {
            try {
                state.mRetryAfter = Integer.parseInt(headerField);
                if (state.mRetryAfter >= 0) {
                    if (state.mRetryAfter < 30) {
                        state.mRetryAfter = 30;
                    } else if (state.mRetryAfter > 86400) {
                        state.mRetryAfter = Constants.MAX_RETRY_AFTER;
                    }
                    state.mRetryAfter += Helpers.sRandom.nextInt(31);
                    state.mRetryAfter *= 1000;
                } else {
                    state.mRetryAfter = 0;
                }
            } catch (NumberFormatException unused) {
            }
        }
        throw new StopRequest(194, "got 503 Service Unavailable, will retry later");
    }

    private void logNetworkState() {
        StringBuilder sb = new StringBuilder();
        sb.append("Net ");
        sb.append(this.mService.getNetworkAvailabilityState(this.mDB) == 1 ? "Up" : "Down");
        Log.i(Constants.TAG, sb.toString());
    }

    private void notifyDownloadCompleted(int status, boolean countRetry, int retryAfter, int redirectCount, boolean gotData, String filename) {
        updateDownloadDatabase(status, countRetry, retryAfter, redirectCount, gotData, filename);
        DownloaderService.isStatusCompleted(status);
    }

    private InputStream openResponseEntity(State state, HttpURLConnection response) throws StopRequest {
        try {
            return response.getInputStream();
        } catch (IOException e) {
            logNetworkState();
            int finalStatusForHttpError = getFinalStatusForHttpError(state);
            throw new StopRequest(finalStatusForHttpError, "while getting entity: " + e.toString(), e);
        }
    }

    private void processResponseHeaders(State state, InnerState innerState, HttpURLConnection response) throws StopRequest {
        if (!innerState.mContinuingDownload) {
            readResponseHeaders(state, innerState, response);
            try {
                state.mFilename = this.mService.generateSaveFile(this.mInfo.mFileName, this.mInfo.mTotalBytes);
                try {
                    state.mStream = new FileOutputStream(state.mFilename);
                } catch (FileNotFoundException e) {
                    try {
                        if (new File(Helpers.getSaveFilePath(this.mService)).mkdirs()) {
                            state.mStream = new FileOutputStream(state.mFilename);
                        }
                    } catch (Exception unused) {
                        throw new StopRequest(492, "while opening destination file: " + e.toString(), e);
                    }
                }
                updateDatabaseFromHeaders(state, innerState);
                checkConnectivity(state);
            } catch (DownloaderService.GenerateSaveFileError e2) {
                throw new StopRequest(e2.mStatus, e2.mMessage);
            }
        }
    }

    private int readFromResponse(State state, InnerState innerState, byte[] data, InputStream entityStream) throws StopRequest {
        try {
            return entityStream.read(data);
        } catch (IOException e) {
            logNetworkState();
            this.mInfo.mCurrentBytes = innerState.mBytesSoFar;
            this.mDB.updateDownload(this.mInfo);
            if (cannotResume(innerState)) {
                throw new StopRequest(489, "while reading response: " + e.toString() + ", can't resume interrupted download with no ETag", e);
            }
            int finalStatusForHttpError = getFinalStatusForHttpError(state);
            throw new StopRequest(finalStatusForHttpError, "while reading response: " + e.toString(), e);
        }
    }

    private void readResponseHeaders(State state, InnerState innerState, HttpURLConnection response) throws StopRequest {
        String headerField = response.getHeaderField("Content-Disposition");
        if (headerField != null) {
            innerState.mHeaderContentDisposition = headerField;
        }
        String headerField2 = response.getHeaderField("Content-Location");
        if (headerField2 != null) {
            innerState.mHeaderContentLocation = headerField2;
        }
        String headerField3 = response.getHeaderField("ETag");
        if (headerField3 != null) {
            innerState.mHeaderETag = headerField3;
        }
        String str = null;
        String headerField4 = response.getHeaderField(HTTP.TRANSFER_ENCODING);
        if (headerField4 != null) {
            str = headerField4;
        }
        String headerField5 = response.getHeaderField("Content-Type");
        if (headerField5 == null || headerField5.equals("application/vnd.android.obb")) {
            if (str == null) {
                long contentLength = response.getContentLength();
                if (headerField5 != null) {
                    if (contentLength == -1 || contentLength == this.mInfo.mTotalBytes) {
                        innerState.mHeaderContentLength = Long.toString(contentLength);
                    } else {
                        Log.e(Constants.TAG, "Incorrect file size delivered.");
                    }
                }
            }
            if (innerState.mHeaderContentLength == null && (str == null || !str.equalsIgnoreCase(HTTP.CHUNK_CODING))) {
                throw new StopRequest(495, "can't know size of download, giving up");
            }
            return;
        }
        throw new StopRequest(DownloaderService.STATUS_FILE_DELIVERED_INCORRECTLY, "file delivered with incorrect Mime type");
    }

    private void reportProgress(State state, InnerState innerState) {
        long currentTimeMillis = System.currentTimeMillis();
        if (innerState.mBytesSoFar - innerState.mBytesNotified > 4096 && currentTimeMillis - innerState.mTimeLastNotification > 1000) {
            this.mInfo.mCurrentBytes = innerState.mBytesSoFar;
            this.mDB.updateDownloadCurrentBytes(this.mInfo);
            innerState.mBytesNotified = innerState.mBytesSoFar;
            innerState.mTimeLastNotification = currentTimeMillis;
            this.mService.notifyUpdateBytes(innerState.mBytesThisSession + this.mService.mBytesSoFar);
        }
    }

    private int sendRequest(State state, HttpURLConnection request) throws StopRequest {
        try {
            return request.getResponseCode();
        } catch (IOException e) {
            logNetworkState();
            int finalStatusForHttpError = getFinalStatusForHttpError(state);
            throw new StopRequest(finalStatusForHttpError, "while trying to execute request: " + e.toString(), e);
        } catch (IllegalArgumentException e2) {
            throw new StopRequest(495, "while trying to execute request: " + e2.toString(), e2);
        }
    }

    private void setupDestinationFile(State state, InnerState innerState) throws StopRequest {
        if (state.mFilename != null) {
            if (Helpers.isFilenameValid(state.mFilename)) {
                File file = new File(state.mFilename);
                if (file.exists()) {
                    long length = file.length();
                    if (length == 0) {
                        file.delete();
                        state.mFilename = null;
                    } else if (this.mInfo.mETag != null) {
                        try {
                            state.mStream = new FileOutputStream(state.mFilename, true);
                            innerState.mBytesSoFar = (int) length;
                            if (this.mInfo.mTotalBytes != -1) {
                                innerState.mHeaderContentLength = Long.toString(this.mInfo.mTotalBytes);
                            }
                            innerState.mHeaderETag = this.mInfo.mETag;
                            innerState.mContinuingDownload = true;
                        } catch (FileNotFoundException e) {
                            throw new StopRequest(492, "while opening destination for resuming: " + e.toString(), e);
                        }
                    } else {
                        file.delete();
                        throw new StopRequest(489, "Trying to resume a download that can't be resumed");
                    }
                }
            } else {
                throw new StopRequest(492, "found invalid internal destination filename");
            }
        }
        if (state.mStream != null) {
            closeDestination(state);
        }
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r4v1 */
    /* JADX WARN: Type inference failed for: r4v12, types: [java.io.FileNotFoundException] */
    /* JADX WARN: Type inference failed for: r4v13, types: [java.io.SyncFailedException] */
    /* JADX WARN: Type inference failed for: r4v19 */
    /* JADX WARN: Type inference failed for: r4v20 */
    /* JADX WARN: Type inference failed for: r4v25 */
    /* JADX WARN: Type inference failed for: r4v26 */
    /* JADX WARN: Type inference failed for: r4v3, types: [java.lang.Object] */
    /* JADX WARN: Type inference failed for: r4v4, types: [java.io.FileNotFoundException] */
    /* JADX WARN: Type inference failed for: r4v5, types: [java.lang.Object] */
    /* JADX WARN: Type inference failed for: r4v6, types: [java.io.SyncFailedException] */
    /* JADX WARN: Type inference failed for: r4v8 */
    /* JADX WARN: Type inference failed for: r4v9 */
    /* JADX WARN: Type inference failed for: r5v8, types: [java.lang.StringBuilder] */
    /* JADX WARN: Type inference failed for: r6v0, types: [java.lang.StringBuilder] */
    /* JADX WARN: Type inference failed for: r6v1, types: [java.lang.StringBuilder] */
    /* JADX WARN: Unknown variable types count: 1 */
    /* JADX WARN: Unsupported multi-entry loop pattern (BACK_EDGE: B:18:0x0039 -> B:45:0x00b4). Please submit an issue!!! */
    /* JADX WARN: Unsupported multi-entry loop pattern (BACK_EDGE: B:20:0x003f -> B:45:0x00b4). Please submit an issue!!! */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    private void syncDestination(com.google.android.vending.expansion.downloader.impl.DownloadThread.State r10) {
        /*
            r9 = this;
            java.lang.String r0 = "file "
            java.lang.String r1 = "exception while closing file: "
            java.lang.String r2 = "IOException while closing synced file: "
            java.lang.String r3 = "LVLDL"
            r4 = 0
            java.io.FileOutputStream r5 = new java.io.FileOutputStream     // Catch: all -> 0x0028, RuntimeException -> 0x002b, IOException -> 0x0044, SyncFailedException -> 0x0069, FileNotFoundException -> 0x008f
            java.lang.String r6 = r10.mFilename     // Catch: all -> 0x0028, RuntimeException -> 0x002b, IOException -> 0x0044, SyncFailedException -> 0x0069, FileNotFoundException -> 0x008f
            r7 = 1
            r5.<init>(r6, r7)     // Catch: all -> 0x0028, RuntimeException -> 0x002b, IOException -> 0x0044, SyncFailedException -> 0x0069, FileNotFoundException -> 0x008f
            java.io.FileDescriptor r4 = r5.getFD()     // Catch: RuntimeException -> 0x001d, IOException -> 0x0020, SyncFailedException -> 0x0023, FileNotFoundException -> 0x0025, all -> 0x00b5
            r4.sync()     // Catch: RuntimeException -> 0x001d, IOException -> 0x0020, SyncFailedException -> 0x0023, FileNotFoundException -> 0x0025, all -> 0x00b5
            r5.close()     // Catch: RuntimeException -> 0x0038, IOException -> 0x003e
            goto L_0x00b4
        L_0x001d:
            r10 = move-exception
            r4 = r5
            goto L_0x002c
        L_0x0020:
            r0 = move-exception
            r4 = r5
            goto L_0x0045
        L_0x0023:
            r4 = move-exception
            goto L_0x006d
        L_0x0025:
            r4 = move-exception
            goto L_0x0093
        L_0x0028:
            r10 = move-exception
            goto L_0x00b7
        L_0x002b:
            r10 = move-exception
        L_0x002c:
            java.lang.String r0 = "exception while syncing file: "
            android.util.Log.w(r3, r0, r10)     // Catch: all -> 0x0028
            if (r4 == 0) goto L_0x00b4
            r4.close()     // Catch: RuntimeException -> 0x0038, IOException -> 0x003e
            goto L_0x00b4
        L_0x0038:
            r10 = move-exception
            android.util.Log.w(r3, r1, r10)
            goto L_0x00b4
        L_0x003e:
            r10 = move-exception
            android.util.Log.w(r3, r2, r10)
            goto L_0x00b4
        L_0x0044:
            r0 = move-exception
        L_0x0045:
            java.lang.StringBuilder r5 = new java.lang.StringBuilder     // Catch: all -> 0x0028
            r5.<init>()     // Catch: all -> 0x0028
            java.lang.String r6 = "IOException trying to sync "
            r5.append(r6)     // Catch: all -> 0x0028
            java.lang.String r10 = r10.mFilename     // Catch: all -> 0x0028
            r5.append(r10)     // Catch: all -> 0x0028
            java.lang.String r10 = ": "
            r5.append(r10)     // Catch: all -> 0x0028
            r5.append(r0)     // Catch: all -> 0x0028
            java.lang.String r10 = r5.toString()     // Catch: all -> 0x0028
            android.util.Log.w(r3, r10)     // Catch: all -> 0x0028
            if (r4 == 0) goto L_0x00b4
            r4.close()     // Catch: RuntimeException -> 0x0038, IOException -> 0x003e
            goto L_0x00b4
        L_0x0069:
            r5 = move-exception
            r8 = r5
            r5 = r4
            r4 = r8
        L_0x006d:
            java.lang.StringBuilder r6 = new java.lang.StringBuilder     // Catch: all -> 0x00b5
            r6.<init>()     // Catch: all -> 0x00b5
            r6.append(r0)     // Catch: all -> 0x00b5
            java.lang.String r10 = r10.mFilename     // Catch: all -> 0x00b5
            r6.append(r10)     // Catch: all -> 0x00b5
            java.lang.String r10 = " sync failed: "
            r6.append(r10)     // Catch: all -> 0x00b5
            r6.append(r4)     // Catch: all -> 0x00b5
            java.lang.String r10 = r6.toString()     // Catch: all -> 0x00b5
            android.util.Log.w(r3, r10)     // Catch: all -> 0x00b5
            if (r5 == 0) goto L_0x00b4
            r5.close()     // Catch: RuntimeException -> 0x0038, IOException -> 0x003e
            goto L_0x00b4
        L_0x008f:
            r5 = move-exception
            r8 = r5
            r5 = r4
            r4 = r8
        L_0x0093:
            java.lang.StringBuilder r6 = new java.lang.StringBuilder     // Catch: all -> 0x00b5
            r6.<init>()     // Catch: all -> 0x00b5
            r6.append(r0)     // Catch: all -> 0x00b5
            java.lang.String r10 = r10.mFilename     // Catch: all -> 0x00b5
            r6.append(r10)     // Catch: all -> 0x00b5
            java.lang.String r10 = " not found: "
            r6.append(r10)     // Catch: all -> 0x00b5
            r6.append(r4)     // Catch: all -> 0x00b5
            java.lang.String r10 = r6.toString()     // Catch: all -> 0x00b5
            android.util.Log.w(r3, r10)     // Catch: all -> 0x00b5
            if (r5 == 0) goto L_0x00b4
            r5.close()     // Catch: RuntimeException -> 0x0038, IOException -> 0x003e
        L_0x00b4:
            return
        L_0x00b5:
            r10 = move-exception
            r4 = r5
        L_0x00b7:
            if (r4 == 0) goto L_0x00c6
            r4.close()     // Catch: RuntimeException -> 0x00bd, IOException -> 0x00c2
            goto L_0x00c6
        L_0x00bd:
            r0 = move-exception
            android.util.Log.w(r3, r1, r0)
            goto L_0x00c6
        L_0x00c2:
            r0 = move-exception
            android.util.Log.w(r3, r2, r0)
        L_0x00c6:
            throw r10
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.vending.expansion.downloader.impl.DownloadThread.syncDestination(com.google.android.vending.expansion.downloader.impl.DownloadThread$State):void");
    }

    private void transferData(State state, InnerState innerState, byte[] data, InputStream entityStream) throws StopRequest {
        while (true) {
            int readFromResponse = readFromResponse(state, innerState, data, entityStream);
            if (readFromResponse == -1) {
                handleEndOfStream(state, innerState);
                return;
            }
            state.mGotData = true;
            writeDataToDestination(state, data, readFromResponse);
            innerState.mBytesSoFar += readFromResponse;
            innerState.mBytesThisSession += readFromResponse;
            reportProgress(state, innerState);
            checkPausedOrCanceled(state);
        }
    }

    private void updateDatabaseFromHeaders(State state, InnerState innerState) {
        this.mInfo.mETag = innerState.mHeaderETag;
        this.mDB.updateDownload(this.mInfo);
    }

    private void updateDownloadDatabase(int status, boolean countRetry, int retryAfter, int redirectCount, boolean gotData, String filename) {
        this.mInfo.mStatus = status;
        this.mInfo.mRetryAfter = retryAfter;
        this.mInfo.mRedirectCount = redirectCount;
        this.mInfo.mLastMod = System.currentTimeMillis();
        if (!countRetry) {
            this.mInfo.mNumFailed = 0;
        } else if (gotData) {
            this.mInfo.mNumFailed = 1;
        } else {
            this.mInfo.mNumFailed++;
        }
        this.mDB.updateDownload(this.mInfo);
    }

    private String userAgent() {
        return this.mUserAgent;
    }

    private void writeDataToDestination(State state, byte[] data, int bytesRead) throws StopRequest {
        try {
            if (state.mStream == null) {
                state.mStream = new FileOutputStream(state.mFilename, true);
            }
            state.mStream.write(data, 0, bytesRead);
            closeDestination(state);
        } catch (IOException e) {
            if (!Helpers.isExternalMediaMounted()) {
                throw new StopRequest(499, "external media not mounted while writing destination file");
            } else if (Helpers.getAvailableBytes(Helpers.getFilesystemRoot(state.mFilename)) < bytesRead) {
                throw new StopRequest(498, "insufficient space while writing destination file", e);
            } else {
                throw new StopRequest(492, "while writing destination file: " + e.toString(), e);
            }
        }
    }

    public void run() {
        boolean z;
        int i;
        int i2;
        boolean z2;
        String str;
        int i3;
        Process.setThreadPriority(10);
        State state = new State(this.mInfo, this.mService);
        PowerManager.WakeLock wakeLock = null;
        try {
            try {
                @SuppressLint("InvalidWakeLockTag") PowerManager.WakeLock newWakeLock = ((PowerManager) this.mContext.getSystemService(Context.POWER_SERVICE)).newWakeLock(1, Constants.TAG);
                newWakeLock.acquire();

                boolean z3 = false;
                while (!z3) {
                    HttpURLConnection httpURLConnection = (HttpURLConnection) new URL(state.mRequestUri).openConnection();
                    httpURLConnection.setRequestProperty("User-Agent", userAgent());
                    try {
                        executeDownload(state, httpURLConnection);
                        httpURLConnection.disconnect();
                        z3 = true;
                    } catch (RetryDownload unused) {
                        httpURLConnection.disconnect();
                    } catch (Throwable th) {
                        httpURLConnection.disconnect();
                        throw th;
                    }
                }
                finalizeDestinationFile(state);
                if (newWakeLock != null) {
                    newWakeLock.release();
                }
                cleanupDestination(state, 200);
                z = state.mCountRetry;
                i = state.mRetryAfter;
                i2 = state.mRedirectCount;
                z2 = state.mGotData;
                str = state.mFilename;
                i3 = 200;
            } catch (Throwable th2) {
                if (0 != 0) {
                    wakeLock.release();
                }
                cleanupDestination(state, 491);
                notifyDownloadCompleted(491, state.mCountRetry, state.mRetryAfter, state.mRedirectCount, state.mGotData, state.mFilename);
                throw th2;
            }
        } catch (StopRequest e) {
            Log.w(Constants.TAG, "Aborting request for download " + this.mInfo.mFileName + ": " + e.getMessage());
            e.printStackTrace();
            int i4 = e.mFinalStatus;
            if (0 != 0) {
                wakeLock.release();
            }
            cleanupDestination(state, i4);
            notifyDownloadCompleted(i4, state.mCountRetry, state.mRetryAfter, state.mRedirectCount, state.mGotData, state.mFilename);
            return;
        } catch (Throwable th3) {
            Log.w(Constants.TAG, "Exception for " + this.mInfo.mFileName + ": " + th3);
            if (0 != 0) {
                wakeLock.release();
            }
            cleanupDestination(state, 491);
            z = state.mCountRetry;
            i = state.mRetryAfter;
            i2 = state.mRedirectCount;
            z2 = state.mGotData;
            str = state.mFilename;
            i3 = 491;
        }
        notifyDownloadCompleted(i3, z, i, i2, z2, str);
    }
}
