package com.microsoft.aad.adal;

/* loaded from: classes3.dex */
class ServerRespondingWithRetryableException extends AuthenticationException {
    static final long serialVersionUID = 1;

    public ServerRespondingWithRetryableException(String str) {
        super(null, str);
    }

    public ServerRespondingWithRetryableException(String str, HttpWebResponse httpWebResponse) {
        super((ADALError) null, str, httpWebResponse);
    }

    public ServerRespondingWithRetryableException(String str, Throwable th) {
        super((ADALError) null, str, th);
    }
}
