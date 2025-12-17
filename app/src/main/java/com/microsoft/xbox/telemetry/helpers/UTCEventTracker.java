package com.microsoft.xbox.telemetry.helpers;

/* loaded from: classes3.dex */
public class UTCEventTracker {

    /* loaded from: classes3.dex */
    public interface UTCEventDelegate {
        void call();
    }

    /* loaded from: classes3.dex */
    public interface UTCStringEventDelegate {
        String call();
    }

    public static String callStringTrackWrapper(UTCStringEventDelegate uTCStringEventDelegate) {
        try {
            return uTCStringEventDelegate.call();
        } catch (Exception e) {
            UTCLog.log(e.getMessage(), new Object[0]);
            return null;
        }
    }

    public static void callTrackWrapper(UTCEventDelegate uTCEventDelegate) {
        try {
            uTCEventDelegate.call();
        } catch (Exception e) {
            UTCLog.log(e.getMessage(), new Object[0]);
        }
    }
}
