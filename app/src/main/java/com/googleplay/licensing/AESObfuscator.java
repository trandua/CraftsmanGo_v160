package com.googleplay.licensing;

import com.googleplay.util.Base64;
import com.googleplay.util.Base64DecoderException;
import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import org.jose4j.keys.AesKey;

/* loaded from: classes2.dex */
public class AESObfuscator implements Obfuscator {
    private static final String CIPHER_ALGORITHM = "AES/CBC/PKCS5Padding";
    private static final byte[] IV = {16, 74, 71, -80, 32, 101, -47, 72, 117, -14, 0, -29, 70, 65, -12, 74};
    private static final String KEYGEN_ALGORITHM = "PBEWITHSHAAND256BITAES-CBC-BC";
    private static final String UTF8 = "UTF-8";
    private static final String header = "com.android.vending.licensing.AESObfuscator-1|";
    private Cipher mDecryptor;
    private Cipher mEncryptor;

    public AESObfuscator(byte[] bArr, String str, String str2) {
        try {
            SecretKeyFactory secretKeyFactory = SecretKeyFactory.getInstance(KEYGEN_ALGORITHM);
            SecretKeySpec secretKeySpec = new SecretKeySpec(secretKeyFactory.generateSecret(new PBEKeySpec((str + str2).toCharArray(), bArr, 1024, 256)).getEncoded(), AesKey.ALGORITHM);
            Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
            this.mEncryptor = cipher;
            byte[] bArr2 = IV;
            cipher.init(1, secretKeySpec, new IvParameterSpec(bArr2));
            Cipher cipher2 = Cipher.getInstance(CIPHER_ALGORITHM);
            this.mDecryptor = cipher2;
            cipher2.init(2, secretKeySpec, new IvParameterSpec(bArr2));
        } catch (GeneralSecurityException e) {
            throw new RuntimeException("Invalid environment", e);
        }
    }

    @Override // com.googleplay.licensing.Obfuscator
    public String obfuscate(String str, String str2) {
        if (str == null) {
            return null;
        }
        try {
            Cipher cipher = this.mEncryptor;
            return Base64.encode(cipher.doFinal((header + str2 + str).getBytes("UTF-8")));
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("Invalid environment", e);
        } catch (GeneralSecurityException e2) {
            throw new RuntimeException("Invalid environment", e2);
        }
    }

    @Override // com.googleplay.licensing.Obfuscator
    public String unobfuscate(String str, String str2) throws ValidationException {
        if (str == null) {
            return null;
        }
        try {
            String str3 = new String(this.mDecryptor.doFinal(Base64.decode(str)), "UTF-8");
            if (str3.indexOf(header + str2) == 0) {
                return str3.substring(str2.length() + 46, str3.length());
            }
            throw new ValidationException("Header not found (invalid data or key):" + str);
        } catch (Base64DecoderException e) {
            throw new ValidationException(e.getMessage() + ":" + str);
        } catch (UnsupportedEncodingException e2) {
            throw new RuntimeException("Invalid environment", e2);
        } catch (BadPaddingException e3) {
            throw new ValidationException(e3.getMessage() + ":" + str);
        } catch (IllegalBlockSizeException e4) {
            throw new ValidationException(e4.getMessage() + ":" + str);
        }
    }
}
