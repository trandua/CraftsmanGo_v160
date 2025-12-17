package com.googleplay.iab;

import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;

/* loaded from: classes2.dex */
public class Security {
    private static final String KEY_FACTORY_ALGORITHM = "RSA";
    private static final String SIGNATURE_ALGORITHM = "SHA1withRSA";
    private static final String TAG = "TAGG-IABUtil";

    public static PublicKey generatePublicKey(String str) {
        try {
            return KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(Base64.decode(str, 0)));
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        } catch (InvalidKeySpecException e2) {
            Log.e(TAG, "Invalid key specification.");
            throw new IllegalArgumentException(e2);
        }
    }

    public static boolean verify(PublicKey publicKey, String str, String str2) {
        String str3;
        try {
            byte[] decode = Base64.decode(str2, 0);
            try {
                Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);
                signature.initVerify(publicKey);
                signature.update(str.getBytes());
                if (signature.verify(decode)) {
                    return true;
                }
                Log.e(TAG, "Signature verification failed.");
                return false;
            } catch (InvalidKeyException unused) {
                str3 = "Invalid key specification.";
                Log.e(TAG, str3);
                return false;
            } catch (NoSuchAlgorithmException unused2) {
                str3 = "NoSuchAlgorithmException.";
                Log.e(TAG, str3);
                return false;
            } catch (SignatureException unused3) {
                str3 = "Signature exception.";
                Log.e(TAG, str3);
                return false;
            }
        } catch (IllegalArgumentException unused4) {
            Log.e(TAG, "Base64 decoding failed.");
            return false;
        }
    }

    public static boolean verifyPurchase(String str, String str2, String str3) {
        if (TextUtils.isEmpty(str2) || TextUtils.isEmpty(str) || TextUtils.isEmpty(str3)) {
            Log.e(TAG, "Purchase verification failed: missing data.");
            return false;
        }
        return verify(generatePublicKey(str), str2, str3);
    }
}
