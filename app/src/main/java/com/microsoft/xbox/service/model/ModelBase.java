package com.microsoft.xbox.service.model;

import com.microsoft.xbox.toolkit.AsyncActionStatus;
import com.microsoft.xbox.toolkit.AsyncResult;
import com.microsoft.xbox.toolkit.DataLoadUtil;
import com.microsoft.xbox.toolkit.ModelData;
import com.microsoft.xbox.toolkit.SingleEntryLoadingStatus;
import com.microsoft.xbox.toolkit.XLEAssert;
import com.microsoft.xbox.toolkit.XLEObservable;
import com.microsoft.xbox.toolkit.network.IDataLoaderRunnable;
import com.microsoft.xbox.xle.app.XLEUtil;
import java.util.Date;

/* loaded from: classes3.dex */
public abstract class ModelBase<T> extends XLEObservable<UpdateData> implements ModelData<T> {
    protected static final long MilliSecondsInADay = 86400000;
    protected static final long MilliSecondsInAnHour = 3600000;
    protected static final long MilliSecondsInHalfHour = 1800000;
    protected Date lastRefreshTime;
    protected IDataLoaderRunnable<T> loaderRunnable;
    protected boolean isLoading = false;
    protected long lastInvalidatedTick = 0;
    protected long lifetime = MilliSecondsInADay;
    private SingleEntryLoadingStatus loadingStatus = new SingleEntryLoadingStatus();

    public boolean getIsLoading() {
        return this.loadingStatus.getIsLoading();
    }

    public boolean hasValidData() {
        return this.lastRefreshTime != null;
    }

    public void invalidateData() {
        this.lastRefreshTime = null;
    }

    public boolean isLoaded() {
        return this.lastRefreshTime != null;
    }

    public AsyncResult<T> loadData(boolean z, IDataLoaderRunnable<T> iDataLoaderRunnable) {
        XLEAssert.assertIsNotUIThread();
        return DataLoadUtil.load(z, this.lifetime, this.lastRefreshTime, this.loadingStatus, iDataLoaderRunnable);
    }

    public void loadInternal(boolean z, UpdateType updateType, IDataLoaderRunnable<T> iDataLoaderRunnable) {
        loadInternal(z, updateType, iDataLoaderRunnable, this.lastRefreshTime);
    }

    public void loadInternal(boolean z, UpdateType updateType, IDataLoaderRunnable<T> iDataLoaderRunnable, Date date) {
        AsyncResult<T> asyncResult;
        XLEAssert.assertIsUIThread();
        if (getIsLoading() || (!z && !shouldRefresh(date))) {
            asyncResult = new AsyncResult(new UpdateData(updateType, !getIsLoading()), this, null);
        } else {
            DataLoadUtil.StartLoadFromUI(z, this.lifetime, this.lastRefreshTime, this.loadingStatus, iDataLoaderRunnable);
            asyncResult = new AsyncResult(new UpdateData(updateType, false), this, null);
        }
        notifyObservers((AsyncResult<UpdateData>) asyncResult);
    }

    public boolean shouldRefresh() {
        return shouldRefresh(this.lastRefreshTime);
    }

    public boolean shouldRefresh(Date date) {
        return XLEUtil.shouldRefresh(date, this.lifetime);
    }

    @Override // com.microsoft.xbox.toolkit.ModelData
    public void updateWithNewData(AsyncResult<T> asyncResult) {
        this.isLoading = false;
        if (asyncResult.getException() == null && asyncResult.getStatus() == AsyncActionStatus.SUCCESS) {
            this.lastRefreshTime = new Date();
        }
    }
}
