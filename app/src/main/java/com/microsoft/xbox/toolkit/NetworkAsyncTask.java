package com.microsoft.xbox.toolkit;

import com.microsoft.xbox.toolkit.network.XLEThreadPool;

/* loaded from: classes3.dex */
public abstract class NetworkAsyncTask<T> extends XLEAsyncTask<T> {
    protected boolean forceLoad;
    private boolean shouldExecute;

    public abstract boolean checkShouldExecute();

    public abstract T loadDataInBackground();

    public abstract T onError();

    public abstract void onNoAction();

    public NetworkAsyncTask() {
        super(XLEThreadPool.networkOperationsThreadPool);
        this.forceLoad = true;
        this.shouldExecute = true;
    }

    public NetworkAsyncTask(XLEThreadPool xLEThreadPool) {
        super(XLEThreadPool.networkOperationsThreadPool);
        this.forceLoad = true;
        this.shouldExecute = true;
    }

    @Override // com.microsoft.xbox.toolkit.XLEAsyncTask
    public final T doInBackground() {
        try {
            return loadDataInBackground();
        } catch (Exception unused) {
            return onError();
        }
    }

    @Override // com.microsoft.xbox.toolkit.XLEAsyncTask
    public void execute() {
        XLEAssert.assertTrue(Thread.currentThread() == ThreadManager.UIThread);
        boolean z = this.cancelled;
        boolean checkShouldExecute = checkShouldExecute();
        this.shouldExecute = checkShouldExecute;
        if (checkShouldExecute || this.forceLoad) {
            this.isBusy = true;
            onPreExecute();
            super.executeBackground();
            return;
        }
        onNoAction();
        this.isBusy = false;
    }

    public void load(boolean z) {
        this.forceLoad = z;
        execute();
    }
}
