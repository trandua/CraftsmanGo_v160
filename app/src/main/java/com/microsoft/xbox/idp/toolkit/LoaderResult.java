package com.microsoft.xbox.idp.toolkit;

/* loaded from: classes3.dex */
public abstract class LoaderResult<T> {
    private final T data;
    private final HttpError error;
    private final Exception exception;

    public abstract boolean isReleased();

    public abstract void release();

    /* JADX INFO: Access modifiers changed from: protected */
    public LoaderResult(Exception exc) {
        this.data = null;
        this.error = null;
        this.exception = exc;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public LoaderResult(T t, HttpError httpError) {
        this.data = t;
        this.error = httpError;
        this.exception = null;
    }

    public T getData() {
        return this.data;
    }

    public HttpError getError() {
        return this.error;
    }

    public Exception getException() {
        return this.exception;
    }

    public boolean hasData() {
        return this.data != null;
    }

    public boolean hasError() {
        return this.error != null;
    }

    public boolean hasException() {
        return this.exception != null;
    }
}
