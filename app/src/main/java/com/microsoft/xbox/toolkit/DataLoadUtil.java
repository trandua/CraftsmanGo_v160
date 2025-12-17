package com.microsoft.xbox.toolkit;

import com.microsoft.xbox.toolkit.SingleEntryLoadingStatus;
import com.microsoft.xbox.toolkit.network.IDataLoaderRunnable;
import com.microsoft.xbox.xle.app.XLEUtil;
import java.util.Date;

/* loaded from: classes3.dex */
public class DataLoadUtil {
    public static <T> AsyncResult<T> load(boolean z, long j, Date date, SingleEntryLoadingStatus singleEntryLoadingStatus, final IDataLoaderRunnable<T> iDataLoaderRunnable) {
        XLEAssert.assertNotNull(singleEntryLoadingStatus);
        XLEAssert.assertNotNull(iDataLoaderRunnable);
        XLEAssert.assertIsNotUIThread();
        SingleEntryLoadingStatus.WaitResult waitForNotLoading = singleEntryLoadingStatus.waitForNotLoading();
        if (waitForNotLoading.waited) {
            XLEException xLEException = waitForNotLoading.error;
            return xLEException == null ? safeReturnResult(null, iDataLoaderRunnable, null, AsyncActionStatus.NO_OP_SUCCESS) : safeReturnResult(null, iDataLoaderRunnable, xLEException, AsyncActionStatus.NO_OP_FAIL);
        } else if (XLEUtil.shouldRefresh(date, j) || z) {
            ThreadManager.UIThreadSend(new Runnable() { // from class: com.microsoft.xbox.toolkit.DataLoadUtil.1
                @Override // java.lang.Runnable
                public void run() {
                    iDataLoaderRunnable.onPreExecute();
                }
            });
            int shouldRetryCountOnTokenError = iDataLoaderRunnable.getShouldRetryCountOnTokenError();
            int i = 0;
            XLEException e = null;
            while (true) {
                if (i > shouldRetryCountOnTokenError) {
                    break;
                }
                try {
                    T buildData = iDataLoaderRunnable.buildData();
                    postExecute(buildData, iDataLoaderRunnable, null, AsyncActionStatus.SUCCESS);
                    singleEntryLoadingStatus.setSuccess();
                    return new AsyncResult<>(buildData, iDataLoaderRunnable, null, AsyncActionStatus.SUCCESS);
                } catch (XLEException e2) {
                    e = e2;
                    if (e.getErrorCode() != XLEErrorCode.NOT_AUTHORIZED) {
                        e.getErrorCode();
                        break;
                    }
                    i++;
                } catch (Exception e3) {
                    e = new XLEException(iDataLoaderRunnable.getDefaultErrorCode(), e3);
                }
            }
            singleEntryLoadingStatus.setFailed(e);
            return safeReturnResult(null, iDataLoaderRunnable, e, AsyncActionStatus.FAIL);
        } else {
            singleEntryLoadingStatus.setSuccess();
            return safeReturnResult(null, iDataLoaderRunnable, null, AsyncActionStatus.NO_CHANGE);
        }
    }

    public static <T> NetworkAsyncTask<T> StartLoadFromUI(boolean z, final long j, final Date date, final SingleEntryLoadingStatus singleEntryLoadingStatus, final IDataLoaderRunnable<T> iDataLoaderRunnable) {
        NetworkAsyncTask<T> networkAsyncTask = new NetworkAsyncTask<T>() { // from class: com.microsoft.xbox.toolkit.DataLoadUtil.2
            @Override // com.microsoft.xbox.toolkit.NetworkAsyncTask
            public T onError() {
                return null;
            }

            @Override // com.microsoft.xbox.toolkit.NetworkAsyncTask
            public void onNoAction() {
            }

            @Override // com.microsoft.xbox.toolkit.XLEAsyncTask
            public void onPostExecute(T t) {
            }

            @Override // com.microsoft.xbox.toolkit.XLEAsyncTask
            public void onPreExecute() {
            }

            {
                this.forceLoad = this.forceLoad;
            }

            @Override // com.microsoft.xbox.toolkit.NetworkAsyncTask
            public boolean checkShouldExecute() {
                return this.forceLoad;
            }

            @Override // com.microsoft.xbox.toolkit.NetworkAsyncTask
            public T loadDataInBackground() {
                return (T) DataLoadUtil.load(this.forceLoad, j, date, singleEntryLoadingStatus, iDataLoaderRunnable).getResult();
            }
        };
        networkAsyncTask.execute();
        return networkAsyncTask;
    }

    private static <T> void postExecute(T t, IDataLoaderRunnable<T> iDataLoaderRunnable, XLEException xLEException, AsyncActionStatus asyncActionStatus) {
        ThreadManager.UIThreadSend(new Runnable() { // from class: com.microsoft.xbox.toolkit.DataLoadUtil.3
//            final /* synthetic */ AsyncActionStatus val$asyncActionStatus;
//            final XLEException val$error;
//            final T val$result;
//            final IDataLoaderRunnable<T> val$runner;
//            final AsyncActionStatus val$status;
//            final /* synthetic */ Object val$t;
//            final /* synthetic */ XLEException val$xLEException;
//
//            /* JADX WARN: Multi-variable type inference failed */
//            {
//                this.val$t = t;
//                this.val$xLEException = xLEException;
//                this.val$asyncActionStatus = asyncActionStatus;
//                this.val$runner = iDataLoaderRunnable;
//                this.val$result = t;
//                this.val$error = xLEException;
//                this.val$status = asyncActionStatus;
//            }

            @Override // java.lang.Runnable
            public void run() {
                IDataLoaderRunnable<T> iDataLoaderRunnable2 = iDataLoaderRunnable;
                iDataLoaderRunnable2.onPostExcute(new AsyncResult(t, iDataLoaderRunnable2, xLEException, asyncActionStatus));
            }
        });
    }

    private static <T> AsyncResult<T> safeReturnResult(T t, IDataLoaderRunnable<T> iDataLoaderRunnable, XLEException xLEException, AsyncActionStatus asyncActionStatus) {
        postExecute(t, iDataLoaderRunnable, xLEException, asyncActionStatus);
        return new AsyncResult<>(t, iDataLoaderRunnable, xLEException, asyncActionStatus);
    }
}
