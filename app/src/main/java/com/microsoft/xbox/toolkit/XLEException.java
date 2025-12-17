package com.microsoft.xbox.toolkit;

/* loaded from: classes3.dex */
public class XLEException extends Exception {
    private long errorCode;
    private boolean isHandled;
    private Object userObject;

    public XLEException(long j) {
        this(j, null, null, null);
    }

    public XLEException(long j, String str) {
        this(j, str, null, null);
    }

    public XLEException(long j, String str, Throwable th) {
        this(j, null, th, null);
    }

    public XLEException(long j, String str, Throwable th, Object obj) {
        super(str, th);
        this.errorCode = j;
        this.userObject = obj;
        this.isHandled = false;
    }

    public XLEException(long j, Throwable th) {
        this(j, null, th, null);
    }

    public long getErrorCode() {
        return this.errorCode;
    }

    public boolean getIsHandled() {
        return this.isHandled;
    }

    public Object getUserObject() {
        return this.userObject;
    }

    public void setIsHandled(boolean z) {
        this.isHandled = z;
    }

    @Override // java.lang.Throwable
    public String toString() {
        StackTraceElement[] stackTrace;
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("XLEException ErrorCode: %d; ErrorMessage: %s \n\n", Long.valueOf(this.errorCode), getMessage()));
        if (getCause() != null) {
            sb.append(String.format("\t Cause ErrorMessage: %s, StackTrace: ", getCause().toString()));
            for (StackTraceElement stackTraceElement : getCause().getStackTrace()) {
                sb.append("\n\n \t " + stackTraceElement.toString());
            }
        }
        return sb.toString();
    }
}
