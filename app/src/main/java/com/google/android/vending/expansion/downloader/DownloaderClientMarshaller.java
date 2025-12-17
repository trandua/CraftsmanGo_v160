package com.google.android.vending.expansion.downloader;

import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import com.google.android.vending.expansion.downloader.impl.DownloaderService;

/* loaded from: classes7.dex */
public class DownloaderClientMarshaller {
    public static final int DOWNLOAD_REQUIRED = 2;
    public static final int LVL_CHECK_REQUIRED = 1;
    public static final int MSG_ONDOWNLOADPROGRESS = 11;
    public static final int MSG_ONDOWNLOADSTATE_CHANGED = 10;
    public static final int MSG_ONSERVICECONNECTED = 12;
    public static final int NO_DOWNLOAD_REQUIRED = 0;
    public static final String PARAM_MESSENGER = "EMH";
    public static final String PARAM_NEW_STATE = "newState";
    public static final String PARAM_PROGRESS = "progress";

    /* loaded from: classes7.dex */
    private static class Proxy implements IDownloaderClient {
        private Messenger mServiceMessenger;

        public Proxy(Messenger msg) {
            this.mServiceMessenger = msg;
        }

        private void send(int method, Bundle params) {
            Message obtain = Message.obtain((Handler) null, method);
            obtain.setData(params);
            try {
                this.mServiceMessenger.send(obtain);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        @Override // com.google.android.vending.expansion.downloader.IDownloaderClient
        public void onDownloadProgress(DownloadProgressInfo progress) {
            Bundle bundle = new Bundle(1);
            bundle.putParcelable("progress", progress);
            send(11, bundle);
        }

        @Override // com.google.android.vending.expansion.downloader.IDownloaderClient
        public void onDownloadStateChanged(int newState) {
            Bundle bundle = new Bundle(1);
            bundle.putInt(DownloaderClientMarshaller.PARAM_NEW_STATE, newState);
            send(10, bundle);
        }

        @Override // com.google.android.vending.expansion.downloader.IDownloaderClient
        public void onServiceConnected(Messenger m) {
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes7.dex */
    public static class Stub implements IStub {
        private boolean mBound;
        private Context mContext;
        private Class<?> mDownloaderServiceClass;
        private IDownloaderClient mItf;
        private Messenger mServiceMessenger;
        final Messenger mMessenger = new Messenger(new Handler() { // from class: com.google.android.vending.expansion.downloader.DownloaderClientMarshaller.Stub.1
            @Override // android.os.Handler
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case 10:
                        Stub.this.mItf.onDownloadStateChanged(msg.getData().getInt(DownloaderClientMarshaller.PARAM_NEW_STATE));
                        return;
                    case 11:
                        Bundle data = msg.getData();
                        if (Stub.this.mContext != null) {
                            data.setClassLoader(Stub.this.mContext.getClassLoader());
                            Stub.this.mItf.onDownloadProgress((DownloadProgressInfo) msg.getData().getParcelable("progress"));
                            return;
                        }
                        return;
                    case 12:
                        Stub.this.mItf.onServiceConnected((Messenger) msg.getData().getParcelable("EMH"));
                        return;
                    default:
                        return;
                }
            }
        });
        private ServiceConnection mConnection = new ServiceConnection() { // from class: com.google.android.vending.expansion.downloader.DownloaderClientMarshaller.Stub.2
            @Override // android.content.ServiceConnection
            public void onServiceConnected(ComponentName className, IBinder service) {
                Stub.this.mServiceMessenger = new Messenger(service);
                Stub.this.mItf.onServiceConnected(Stub.this.mServiceMessenger);
            }

            @Override // android.content.ServiceConnection
            public void onServiceDisconnected(ComponentName className) {
                Stub.this.mServiceMessenger = null;
            }
        };

        public Stub(IDownloaderClient itf, Class<?> downloaderService) {
            this.mItf = null;
            this.mItf = itf;
            this.mDownloaderServiceClass = downloaderService;
        }

        @Override // com.google.android.vending.expansion.downloader.IStub
        public void connect(Context c) {
            this.mContext = c;
            Intent intent = new Intent(c, this.mDownloaderServiceClass);
            intent.putExtra("EMH", this.mMessenger);
            if (c.bindService(intent, this.mConnection, 2)) {
                this.mBound = true;
            }
        }

        @Override // com.google.android.vending.expansion.downloader.IStub
        public void disconnect(Context c) {
            if (this.mBound) {
                c.unbindService(this.mConnection);
                this.mBound = false;
            }
            this.mContext = null;
        }

        @Override // com.google.android.vending.expansion.downloader.IStub
        public Messenger getMessenger() {
            return this.mMessenger;
        }
    }

    public static IDownloaderClient CreateProxy(Messenger msg) {
        return new Proxy(msg);
    }

    public static IStub CreateStub(IDownloaderClient itf, Class<?> downloaderService) {
        return new Stub(itf, downloaderService);
    }

    public static int startDownloadServiceIfRequired(Context context, PendingIntent notificationClient, Class<?> serviceClass) throws PackageManager.NameNotFoundException {
        return DownloaderService.startDownloadServiceIfRequired(context, notificationClient, serviceClass);
    }

    public static int startDownloadServiceIfRequired(Context context, Intent notificationClient, Class<?> serviceClass) throws PackageManager.NameNotFoundException {
        return DownloaderService.startDownloadServiceIfRequired(context, notificationClient, serviceClass);
    }
}
