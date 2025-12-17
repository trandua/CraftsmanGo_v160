package com.microsoft.xal.crypto;

import org.spongycastle.pqc.jcajce.spec.McElieceCCA2KeyGenParameterSpec;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/* loaded from: classes3.dex */
public class ShaHasher {
    private MessageDigest f12597md = MessageDigest.getInstance(McElieceCCA2KeyGenParameterSpec.SHA256);

    public ShaHasher() throws NoSuchAlgorithmException {
    }

    public void AddBytes(byte[] bArr) {
        this.f12597md.update(bArr);
    }

    public byte[] SignHash() {
        return this.f12597md.digest();
    }
}
