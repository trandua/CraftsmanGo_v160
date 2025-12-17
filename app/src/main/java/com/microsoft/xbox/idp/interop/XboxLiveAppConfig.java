package com.microsoft.xbox.idp.interop;

/* loaded from: classes3.dex */
public class XboxLiveAppConfig {
    private final long f12601id = create();

    private static native long create();

    private static native void delete(long j);

    private static native String getEnvironment(long j);

    private static native int getOverrideTitleId(long j);

    private static native String getSandbox(long j);

    private static native String getScid(long j);

    private static native int getTitleId(long j);

    public void finalize() throws Throwable {
        super.finalize();
        delete(this.f12601id);
    }

    public String getEnvironment() {
        return getEnvironment(this.f12601id);
    }

    public int getOverrideTitleId() {
        return getOverrideTitleId(this.f12601id);
    }

    public String getSandbox() {
        return getSandbox(this.f12601id);
    }

    public String getScid() {
        return getScid(this.f12601id);
    }

    public int getTitleId() {
        return getTitleId(this.f12601id);
    }
}
