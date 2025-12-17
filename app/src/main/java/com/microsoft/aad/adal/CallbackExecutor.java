package com.microsoft.aad.adal;

import android.os.Handler;
import android.os.Looper;
import java.util.concurrent.atomic.AtomicReference;

/* loaded from: classes3.dex */
public final class CallbackExecutor<T> {
    private static final String TAG = "CallbackExecutor";
    private final AtomicReference<Callback<T>> mCallbackReference;
    private final Handler mHandler;

    /* JADX INFO: Access modifiers changed from: package-private */
    public CallbackExecutor(Callback<T> callback) {
        AtomicReference<Callback<T>> atomicReference = new AtomicReference<>(null);
        this.mCallbackReference = atomicReference;
        this.mHandler = Looper.myLooper() != null ? new Handler() : null;
        atomicReference.set(callback);
    }

    public void onSuccess(final T t) {
        final Callback<T> andSet = this.mCallbackReference.getAndSet(null);
        if (andSet == null) {
            Logger.m14614v(TAG, "Callback does not exist.");
            return;
        }
        Handler handler = this.mHandler;
        if (handler == null) {
            andSet.onSuccess(t);
        } else {
            handler.post(new Runnable() { // from class: com.microsoft.aad.adal.CallbackExecutor.1
                /* JADX WARN: Multi-variable type inference failed */
                @Override // java.lang.Runnable
                public void run() {
                    andSet.onSuccess(t);
                }
            });
        }
    }

    public void onError(final Throwable th) {
        final Callback<T> andSet = this.mCallbackReference.getAndSet(null);
        if (andSet == null) {
            Logger.m14614v(TAG, "Callback does not exist.");
            return;
        }
        Handler handler = this.mHandler;
        if (handler == null) {
            andSet.onError(th);
        } else {
            handler.post(new Runnable() { // from class: com.microsoft.aad.adal.CallbackExecutor.2
                @Override // java.lang.Runnable
                public void run() {
                    andSet.onError(th);
                }
            });
        }
    }
}
