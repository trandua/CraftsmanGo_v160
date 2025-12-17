package com.microsoft.aad.adal;

import android.util.Pair;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/* loaded from: classes3.dex */
public final class Telemetry {
    private static final Telemetry INSTANCE = new Telemetry();
    private static final String TAG = "Telemetry";
    private static boolean sAllowPii;
    private DefaultDispatcher mDispatcher = null;
    private final Map<Pair<String, String>, String> mEventTracking = new ConcurrentHashMap();

    public static boolean getAllowPii() {
        return sAllowPii;
    }

    public static Telemetry getInstance() {
        Telemetry telemetry;
        synchronized (Telemetry.class) {
            telemetry = INSTANCE;
        }
        return telemetry;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static String registerNewRequest() {
        return UUID.randomUUID().toString();
    }

    public static void setAllowPii(boolean z) {
        sAllowPii = z;
    }

    public void flush(String str) {
        DefaultDispatcher defaultDispatcher = this.mDispatcher;
        if (defaultDispatcher != null) {
            defaultDispatcher.flush(str);
        }
    }

    public void registerDispatcher(IDispatcher iDispatcher, boolean z) {
        synchronized (this) {
            this.mDispatcher = z ? new AggregatedDispatcher(iDispatcher) : new DefaultDispatcher(iDispatcher);
        }
    }

    public void startEvent(String str, String str2) {
        if (this.mDispatcher != null) {
            this.mEventTracking.put(new Pair<>(str, str2), Long.toString(System.currentTimeMillis()));
        }
    }

    public void stopEvent(String str, IEvents iEvents, String str2) {
        if (this.mDispatcher != null) {
            String remove = this.mEventTracking.remove(new Pair(str, str2));
            if (StringExtensions.isNullOrBlank(remove)) {
                Logger.m14617w(TAG, "Stop Event called without a corresponding start_event", "", null);
                return;
            }
            long parseLong = Long.parseLong(remove);
            long currentTimeMillis = System.currentTimeMillis();
            String l = Long.toString(currentTimeMillis);
            iEvents.setProperty("Microsoft.ADAL.start_time", remove);
            iEvents.setProperty("Microsoft.ADAL.stop_time", l);
            iEvents.setProperty("Microsoft.ADAL.response_time", Long.toString(currentTimeMillis - parseLong));
            this.mDispatcher.receive(str, iEvents);
        }
    }
}
