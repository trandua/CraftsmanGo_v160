package com.google.android.vending.expansion.downloader.impl;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

/* loaded from: classes7.dex */
public abstract class CustomIntentService extends Service {
    private static final String LOG_TAG = "CustomIntentService";
    private static final int WHAT_MESSAGE = -10;
    private String mName;
    private boolean mRedelivery;
    private volatile ServiceHandler mServiceHandler;
    private volatile Looper mServiceLooper;

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes7.dex */
    public final class ServiceHandler extends Handler {
        public ServiceHandler(Looper looper) {
            super(looper);
        }

        @Override // android.os.Handler
        public void handleMessage(Message paramMessage) {
            CustomIntentService.this.onHandleIntent((Intent) paramMessage.obj);
            if (CustomIntentService.this.shouldStop()) {
                Log.d(CustomIntentService.LOG_TAG, "stopSelf");
                CustomIntentService.this.stopSelf(paramMessage.arg1);
                Log.d(CustomIntentService.LOG_TAG, "afterStopSelf");
            }
        }
    }

    public CustomIntentService(String paramString) {
        this.mName = paramString;
    }

    @Override // android.app.Service
    public IBinder onBind(Intent paramIntent) {
        return null;
    }

    @Override // android.app.Service
    public void onCreate() {
        super.onCreate();
        HandlerThread handlerThread = new HandlerThread("IntentService[" + this.mName + "]");
        handlerThread.start();
        this.mServiceLooper = handlerThread.getLooper();
        this.mServiceHandler = new ServiceHandler(this.mServiceLooper);
    }

    @Override // android.app.Service
    public void onDestroy() {
        Thread thread = this.mServiceLooper.getThread();
        if (thread != null && thread.isAlive()) {
            thread.interrupt();
        }
        this.mServiceLooper.quit();
        Log.d(LOG_TAG, "onDestroy");
    }

    protected abstract void onHandleIntent(Intent paramIntent);

    @Override // android.app.Service
    public void onStart(Intent paramIntent, int startId) {
        if (!this.mServiceHandler.hasMessages(-10)) {
            Message obtainMessage = this.mServiceHandler.obtainMessage();
            obtainMessage.arg1 = startId;
            obtainMessage.obj = paramIntent;
            obtainMessage.what = -10;
            this.mServiceHandler.sendMessage(obtainMessage);
        }
    }

    @Override // android.app.Service
    public int onStartCommand(Intent paramIntent, int flags, int startId) {
        onStart(paramIntent, startId);
        return this.mRedelivery ? 3 : 2;
    }

    public void setIntentRedelivery(boolean enabled) {
        this.mRedelivery = enabled;
    }

    protected abstract boolean shouldStop();
}
