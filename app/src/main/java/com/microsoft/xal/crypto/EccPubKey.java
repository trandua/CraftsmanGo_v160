package com.microsoft.xal.crypto;

import android.util.Base64;
import java.math.BigInteger;
import java.security.interfaces.ECPublicKey;

/* loaded from: classes3.dex */
public class EccPubKey {
    private final ECPublicKey publicKey;

    /* JADX INFO: Access modifiers changed from: package-private */
    public EccPubKey(ECPublicKey eCPublicKey) {
        this.publicKey = eCPublicKey;
    }

    private String getBase64Coordinate(BigInteger bigInteger) {
        int i;
        byte[] byteArray = bigInteger.toByteArray();
        if (byteArray.length > 32) {
            i = byteArray.length - 32;
        } else {
            if (byteArray.length < 32) {
                byte[] bArr = new byte[32];
                System.arraycopy(byteArray, 0, bArr, 32 - byteArray.length, byteArray.length);
                byteArray = bArr;
            }
            i = 0;
        }
        return Base64.encodeToString(byteArray, i, 32, 11);
    }

    public String getBase64UrlX() {
        return getBase64Coordinate(getX());
    }

    public String getBase64UrlY() {
        return getBase64Coordinate(getY());
    }

    public BigInteger getX() {
        return this.publicKey.getW().getAffineX();
    }

    public BigInteger getY() {
        return this.publicKey.getW().getAffineY();
    }
}
