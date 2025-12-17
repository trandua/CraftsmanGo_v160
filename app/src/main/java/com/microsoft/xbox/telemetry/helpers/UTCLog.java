package com.microsoft.xbox.telemetry.helpers;

import android.util.Log;

/* loaded from: classes3.dex */
public class UTCLog {
    static final String UTCLOGTAG = "UTCLOGGING";

    public static void log(String str, Object... objArr) {
        String format;
        try {
            StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
            if (stackTrace.length > 3) {
                String methodName = stackTrace[3].getMethodName();
                format = String.format(String.format("%s: ", methodName) + str, objArr);
            } else {
                format = String.format(str, objArr);
            }
            Log.d(UTCLOGTAG, format);
        } catch (Exception e) {
            UTCError.trackException(e, "UTCLog.log");
            if (e.getMessage().equals("Format specifier: s")) {
                Log.e(UTCLOGTAG, e.getMessage());
            }
            Log.e(UTCLOGTAG, e.getMessage());
        }
    }
}
