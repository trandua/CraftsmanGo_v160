package com.googleplay.licensing;

/* loaded from: classes2.dex */
public class NullDeviceLimiter implements DeviceLimiter {
    @Override // com.googleplay.licensing.DeviceLimiter
    public int isDeviceAllowed(String str) {
        return 256;
    }
}
