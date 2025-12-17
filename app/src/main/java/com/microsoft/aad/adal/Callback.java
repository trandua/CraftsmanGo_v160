package com.microsoft.aad.adal;

/* loaded from: classes3.dex */
interface Callback<T> {
    void onError(Throwable th);

    void onSuccess(T t);
}
