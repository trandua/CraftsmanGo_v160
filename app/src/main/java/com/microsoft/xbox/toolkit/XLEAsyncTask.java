package com.microsoft.xbox.toolkit;

import com.microsoft.xbox.toolkit.network.XLEThreadPool;

/* loaded from: classes3.dex */
public abstract class XLEAsyncTask<Result> {
    private Runnable doBackgroundAndPostExecuteRunnable;
    private XLEThreadPool threadPool;
    protected boolean cancelled = false;
    public XLEAsyncTask chainedTask = null;
    protected boolean isBusy = false;

    public abstract Result doInBackground();

    public abstract void onPostExecute(Result result);

    public abstract void onPreExecute();

    public XLEAsyncTask(XLEThreadPool xLEThreadPool) {
        this.doBackgroundAndPostExecuteRunnable = null;
        this.threadPool = xLEThreadPool;
        this.doBackgroundAndPostExecuteRunnable = new Runnable() { // from class: com.microsoft.xbox.toolkit.XLEAsyncTask.1
            @Override // java.lang.Runnable
            public void run() {
                final Object doInBackground = !XLEAsyncTask.this.cancelled ? XLEAsyncTask.this.doInBackground() : null;
                ThreadManager.UIThreadPost(new Runnable() { // from class: com.microsoft.xbox.toolkit.XLEAsyncTask.1.1
                    /* JADX WARN: Multi-variable type inference failed */
                    @Override // java.lang.Runnable
                    public void run() {
                        XLEAsyncTask.this.isBusy = false;
                        if (XLEAsyncTask.this.cancelled) {
                            return;
                        }
                        XLEAsyncTask.this.onPostExecute((Result) doInBackground);
                        if (XLEAsyncTask.this.chainedTask != null) {
                            XLEAsyncTask.this.chainedTask.execute();
                        }
                    }
                });
            }
        };
    }

    public static void executeAll(XLEAsyncTask... xLEAsyncTaskArr) {
        if (xLEAsyncTaskArr.length > 0) {
            int i = 0;
            while (i < xLEAsyncTaskArr.length - 1) {
                XLEAsyncTask xLEAsyncTask = xLEAsyncTaskArr[i];
                i++;
                xLEAsyncTask.chainedTask = xLEAsyncTaskArr[i];
            }
            xLEAsyncTaskArr[0].execute();
        }
    }

    public void cancel() {
        XLEAssert.assertTrue(Thread.currentThread() == ThreadManager.UIThread);
        this.cancelled = true;
    }

    public void execute() {
        XLEAssert.assertTrue(Thread.currentThread() == ThreadManager.UIThread);
        this.cancelled = false;
        this.isBusy = true;
        onPreExecute();
        executeBackground();
    }

    public void executeBackground() {
        this.cancelled = false;
        this.threadPool.run(this.doBackgroundAndPostExecuteRunnable);
    }

    public boolean getIsBusy() {
        return this.isBusy && !this.cancelled;
    }
}
