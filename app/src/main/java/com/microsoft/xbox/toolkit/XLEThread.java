package com.microsoft.xbox.toolkit;

/* loaded from: classes3.dex */
public class XLEThread extends Thread {
    public XLEThread(Runnable runnable, String str) {
        super(runnable, str);
        setUncaughtExceptionHandler(XLEUnhandledExceptionHandler.Instance);
    }
}
