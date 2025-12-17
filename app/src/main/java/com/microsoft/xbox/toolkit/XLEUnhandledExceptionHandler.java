package com.microsoft.xbox.toolkit;

import java.lang.Thread;
import java.util.Date;

/* loaded from: classes3.dex */
public class XLEUnhandledExceptionHandler implements Thread.UncaughtExceptionHandler {
    public static XLEUnhandledExceptionHandler Instance = new XLEUnhandledExceptionHandler();
    private Thread.UncaughtExceptionHandler oldExceptionHandler = Thread.getDefaultUncaughtExceptionHandler();

    private void printStackTrace(String str, Throwable th) {
        StackTraceElement[] stackTrace;
        new Date();
        String str2 = "";
        for (StackTraceElement stackTraceElement : th.getStackTrace()) {
            str2 = str2 + String.format("\t%s\n", stackTraceElement.toString());
        }
    }

    @Override // java.lang.Thread.UncaughtExceptionHandler
    public void uncaughtException(Thread thread, Throwable th) {
        th.toString();
        if (th.getCause() != null) {
            printStackTrace("CAUSE STACK TRACE", th.getCause());
        }
        printStackTrace("MAIN THREAD STACK TRACE", th);
        this.oldExceptionHandler.uncaughtException(thread, th);
    }
}
