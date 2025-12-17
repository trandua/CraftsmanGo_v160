package com.microsoft.xal.crypto;

/* loaded from: classes3.dex */
public class SecureRandom {
    public static byte[] GenerateRandomBytes(int i) {
        byte[] bArr = new byte[i];
        new java.security.SecureRandom().nextBytes(bArr);
        return bArr;
    }
}
