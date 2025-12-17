package com.microsoft.aad.adal;

import android.content.Context;
import android.security.KeyPairGeneratorSpec;
import android.util.Base64;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.DigestException;
import java.security.GeneralSecurityException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.spec.AlgorithmParameterSpec;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.security.auth.x500.X500Principal;
//import kotlin.jvm.internal.CharCompanionObject;

/* loaded from: classes3.dex */
public class StorageHelper {
    private static final String ADALKS = "adalks";
    private static final String ANDROID_KEY_STORE = "AndroidKeyStore";
    private static final String CIPHER_ALGORITHM = "AES/CBC/PKCS5Padding";
    public static final int DATA_KEY_LENGTH = 16;
    private static final String ENCODE_VERSION = "E1";
    private static final String HMAC_ALGORITHM = "HmacSHA256";
    private static final String HMAC_KEY_HASH_ALGORITHM = "SHA256";
    public static final int HMAC_LENGTH = 32;
    private static final String KEYSPEC_ALGORITHM = "AES";
    private static final int KEY_FILE_SIZE = 1024;
    private static final int KEY_SIZE = 256;
    private static final String KEY_STORE_CERT_ALIAS = "AdalKey";
    private static final int KEY_VERSION_BLOB_LENGTH = 4;
    private static final String TAG = "StorageHelper";
    public static final String VERSION_ANDROID_KEY_STORE = "A001";
    public static final String VERSION_USER_DEFINED = "U001";
    private static final String WRAP_ALGORITHM = "RSA/ECB/PKCS1Padding";
    private String mBlobVersion;
    private final Context mContext;
    private KeyPair mKeyPair;
    private SecretKey mHMACKey = null;
    private SecretKey mKey = null;
    private SecretKey mSecretKeyFromAndroidKeyStore = null;
    private final SecureRandom mRandom = new SecureRandom();

    private char getEncodeVersionLengthPrefix() {
        return 'c';
    }

    public StorageHelper(Context context) {
        this.mContext = context;
    }

    private void assertHMac(byte[] bArr, int i, int i2, byte[] bArr2) throws DigestException {
        if (bArr2.length != i2 - i) {
            throw new IllegalArgumentException("Unexpected HMAC length");
        }
        byte b = 0;
        for (int i3 = i; i3 < i2; i3++) {
            b = (byte) (b | (bArr2[i3 - i] ^ bArr[i3]));
        }
        if (b != 0) {
            throw new DigestException();
        }
    }

    private void deleteKeyFile() {
        Context context = this.mContext;
        File file = new File(context.getDir(context.getPackageName(), 0), ADALKS);
        if (file.exists()) {
            Logger.m14614v("StorageHelper:deleteKeyFile", "Delete KeyFile");
            if (file.delete()) {
                return;
            }
            Logger.m14614v("StorageHelper:deleteKeyFile", "Delete KeyFile failed");
        }
    }

    private boolean doesKeyPairExist() throws GeneralSecurityException, IOException {
        boolean containsAlias;
        synchronized (this) {
            KeyStore keyStore = KeyStore.getInstance(ANDROID_KEY_STORE);
            KeyStore.LoadStoreParameter loadStoreParameter = null;
            keyStore.load(null);
            try {
                containsAlias = keyStore.containsAlias(KEY_STORE_CERT_ALIAS);
            } catch (NullPointerException e) {
                throw new KeyStoreException(e);
            }
        }
        return containsAlias;
    }

    private KeyPair generateKeyPairFromAndroidKeyStore() throws GeneralSecurityException, IOException {
        KeyPair generateKeyPair;
        synchronized (this) {
            KeyStore.LoadStoreParameter loadStoreParameter = null;
            KeyStore.getInstance(ANDROID_KEY_STORE).load(null);
            Logger.m14614v("StorageHelper:generateKeyPairFromAndroidKeyStore", "Generate KeyPair from AndroidKeyStore");
            Calendar calendar = Calendar.getInstance();
            Calendar calendar2 = Calendar.getInstance();
            calendar2.add(1, 100);
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA", ANDROID_KEY_STORE);
            keyPairGenerator.initialize(getKeyPairGeneratorSpec(this.mContext, calendar.getTime(), calendar2.getTime()));
            try {
                generateKeyPair = keyPairGenerator.generateKeyPair();
            } catch (IllegalStateException e) {
                throw new KeyStoreException(e);
            }
        }
        return generateKeyPair;
    }

    private SecretKey generateSecretKey() throws NoSuchAlgorithmException {
        KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
        keyGenerator.init(256, this.mRandom);
        return keyGenerator.generateKey();
    }

    private SecretKey getHMacKey(SecretKey secretKey) throws NoSuchAlgorithmException {
        byte[] encoded = secretKey.getEncoded();
        return encoded != null ? new SecretKeySpec(MessageDigest.getInstance("SHA256").digest(encoded), "AES") : secretKey;
    }

    private synchronized SecretKey getKey(String str) throws GeneralSecurityException, IOException {
        char c;
        int hashCode = str.hashCode();
        if (hashCode != 1984080) {
            if (hashCode == 2579900 && str.equals(VERSION_USER_DEFINED)) {
                c = 0;
            }
            c = 65535;
        } else {
            if (str.equals(VERSION_ANDROID_KEY_STORE)) {
                c = 1;
            }
            c = 65535;
        }
        if (c == 0) {
            return getSecretKey(AuthenticationSettings.INSTANCE.getSecretKeyData());
        } else if (c == 1) {
            SecretKey secretKey = this.mSecretKeyFromAndroidKeyStore;
            if (secretKey != null) {
                return secretKey;
            }
            this.mKeyPair = readKeyPair();
            SecretKey unwrappedSecretKey = getUnwrappedSecretKey();
            this.mSecretKeyFromAndroidKeyStore = unwrappedSecretKey;
            return unwrappedSecretKey;
        } else {
            throw new IOException("Unknown keyVersion.");
        }
    }

    private SecretKey getKeyOrCreate(String str) throws GeneralSecurityException, IOException {
        synchronized (this) {
            if (VERSION_USER_DEFINED.equals(str)) {
                return getSecretKey(AuthenticationSettings.INSTANCE.getSecretKeyData());
            }
            try {
                this.mSecretKeyFromAndroidKeyStore = getKey(str);
            } catch (IOException | GeneralSecurityException unused) {
                Logger.m14614v("StorageHelper:getKeyOrCreate", "Key does not exist in AndroidKeyStore, try to generate new keys.");
            }
            if (this.mSecretKeyFromAndroidKeyStore == null) {
                this.mKeyPair = generateKeyPairFromAndroidKeyStore();
                SecretKey generateSecretKey = generateSecretKey();
                this.mSecretKeyFromAndroidKeyStore = generateSecretKey;
                writeKeyData(wrap(generateSecretKey));
            }
            return this.mSecretKeyFromAndroidKeyStore;
        }
    }

    private AlgorithmParameterSpec getKeyPairGeneratorSpec(Context context, Date date, Date date2) {
        return new KeyPairGeneratorSpec.Builder(context).setAlias(KEY_STORE_CERT_ALIAS).setSubject(new X500Principal(String.format(Locale.ROOT, "CN=%s, OU=%s", KEY_STORE_CERT_ALIAS, context.getPackageName()))).setSerialNumber(BigInteger.ONE).setStartDate(date).setEndDate(date2).build();
    }

    private SecretKey getSecretKey(byte[] bArr) {
        if (bArr != null) {
            return new SecretKeySpec(bArr, "AES");
        }
        throw new IllegalArgumentException("rawBytes");
    }

    private SecretKey getUnwrappedSecretKey() throws GeneralSecurityException, IOException {
        SecretKey unwrap;
        synchronized (this) {
            Logger.m14614v("StorageHelper:getUnwrappedSecretKey", "Reading SecretKey");
            try {
                unwrap = unwrap(readKeyData());
                Logger.m14614v("StorageHelper:getUnwrappedSecretKey", "Finished reading SecretKey");
            } catch (IOException e) {
                e = e;
                Logger.m14610e("StorageHelper:getUnwrappedSecretKey", "Unwrap failed for AndroidKeyStore", "", ADALError.ANDROIDKEYSTORE_FAILED, e);
                this.mKeyPair = null;
                deleteKeyFile();
                resetKeyPairFromAndroidKeyStore();
                Logger.m14614v("StorageHelper:getUnwrappedSecretKey", "Removed previous key pair info.");
                throw e;
            } catch (GeneralSecurityException e2) {
//                e = e2;
//                Logger.m14610e("StorageHelper:getUnwrappedSecretKey", "Unwrap failed for AndroidKeyStore", "", ADALError.ANDROIDKEYSTORE_FAILED, e);
                this.mKeyPair = null;
                deleteKeyFile();
                resetKeyPairFromAndroidKeyStore();
                Logger.m14614v("StorageHelper:getUnwrappedSecretKey", "Removed previous key pair info.");
                throw e2;
            }
        }
        return unwrap;
    }

    private byte[] readKeyData() throws IOException {
        Context context = this.mContext;
        File file = new File(context.getDir(context.getPackageName(), 0), ADALKS);
        if (file.exists()) {
            Logger.m14614v(TAG, "Reading key data from a file");
            FileInputStream fileInputStream = new FileInputStream(file);
            try {
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                byte[] bArr = new byte[1024];
                while (true) {
                    int read = fileInputStream.read(bArr);
                    if (read == -1) {
                        return byteArrayOutputStream.toByteArray();
                    }
                    byteArrayOutputStream.write(bArr, 0, read);
                }
            } finally {
                fileInputStream.close();
            }
        } else {
            throw new IOException("Key file to read does not exist");
        }
    }

    private KeyPair readKeyPair() throws GeneralSecurityException, IOException {
        KeyPair keyPair;
        synchronized (this) {
            if (doesKeyPairExist()) {
                Logger.m14614v("StorageHelper:readKeyPair", "Reading Key entry");
                KeyStore keyStore = KeyStore.getInstance(ANDROID_KEY_STORE);
                KeyStore.LoadStoreParameter loadStoreParameter = null;
                keyStore.load(null);
                try {
                    KeyStore.ProtectionParameter protectionParameter = null;
                    KeyStore.PrivateKeyEntry privateKeyEntry = (KeyStore.PrivateKeyEntry) keyStore.getEntry(KEY_STORE_CERT_ALIAS, null);
                    keyPair = new KeyPair(privateKeyEntry.getCertificate().getPublicKey(), privateKeyEntry.getPrivateKey());
                } catch (RuntimeException e) {
                    throw new KeyStoreException(e);
                }
            } else {
                throw new KeyStoreException("KeyPair entry does not exist.");
            }
        }
        return keyPair;
    }

    private void resetKeyPairFromAndroidKeyStore() throws KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException {
        synchronized (this) {
            KeyStore keyStore = KeyStore.getInstance(ANDROID_KEY_STORE);
            KeyStore.LoadStoreParameter loadStoreParameter = null;
            keyStore.load(null);
            keyStore.deleteEntry(KEY_STORE_CERT_ALIAS);
        }
    }

    private SecretKey unwrap(byte[] bArr) throws GeneralSecurityException {
        Cipher cipher = Cipher.getInstance(WRAP_ALGORITHM);
        cipher.init(4, this.mKeyPair.getPrivate());
        try {
            return (SecretKey) cipher.unwrap(bArr, "AES", 3);
        } catch (IllegalArgumentException e) {
            throw new KeyStoreException(e);
        }
    }

    private byte[] wrap(SecretKey secretKey) throws GeneralSecurityException {
        Logger.m14614v(TAG, "Wrap secret key.");
        Cipher cipher = Cipher.getInstance(WRAP_ALGORITHM);
        cipher.init(3, this.mKeyPair.getPublic());
        return cipher.wrap(secretKey);
    }

    private void writeKeyData(byte[] bArr) throws IOException {
        Logger.m14614v(TAG, "Writing key data to a file");
        Context context = this.mContext;
        FileOutputStream fileOutputStream = new FileOutputStream(new File(context.getDir(context.getPackageName(), 0), ADALKS));
        try {
            fileOutputStream.write(bArr);
        } finally {
            fileOutputStream.close();
        }
    }

    public String decrypt(String str) throws GeneralSecurityException, IOException {
        Logger.m14614v("StorageHelper:decrypt", "Starting decryption");
        if (!StringExtensions.isNullOrBlank(str)) {
            int charAt = str.charAt(0) - 'a';
            if (charAt > 0) {
                int i = charAt + 1;
                if (str.substring(1, i).equals(ENCODE_VERSION)) {
                    byte[] decode = Base64.decode(str.substring(i), 0);
                    String str2 = new String(decode, 0, 4, "UTF-8");
                    Logger.m14612i("StorageHelper:decrypt", "", "Encrypt version:".concat(str2));
                    SecretKey key = getKey(str2);
                    SecretKey hMacKey = getHMacKey(key);
                    int length = (decode.length - 16) - 32;
                    int length2 = decode.length - 32;
                    int i2 = length - 4;
                    if (length < 0 || length2 < 0 || i2 < 0) {
                        throw new IOException("Invalid byte array input for decryption.");
                    }
                    Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
                    Mac mac = Mac.getInstance("HmacSHA256");
                    mac.init(hMacKey);
                    mac.update(decode, 0, length2);
                    assertHMac(decode, length2, decode.length, mac.doFinal());
                    cipher.init(2, key, new IvParameterSpec(decode, length, 16));
                    String str3 = new String(cipher.doFinal(decode, 4, i2), "UTF-8");
                    Logger.m14614v("StorageHelper:decrypt", "Finished decryption");
                    return str3;
                }
                throw new IllegalArgumentException(String.format("Encode version received was: '%s', Encode version supported is: '%s'", str, ENCODE_VERSION));
            }
            throw new IllegalArgumentException(String.format("Encode version length: '%s' is not valid, it must be greater of equal to 0", Integer.valueOf(charAt)));
        }
        throw new IllegalArgumentException("Input is empty or null");
    }

    public String encrypt(String str) throws GeneralSecurityException, IOException {
        Logger.m14614v("StorageHelper:encrypt", "Starting encryption");
        if (!StringExtensions.isNullOrBlank(str)) {
            SecretKey loadSecretKeyForEncryption = loadSecretKeyForEncryption();
            this.mKey = loadSecretKeyForEncryption;
            this.mHMACKey = getHMacKey(loadSecretKeyForEncryption);
            Logger.m14612i("StorageHelper:encrypt", "", "Encrypt version:" + this.mBlobVersion);
            byte[] bytes = this.mBlobVersion.getBytes("UTF-8");
            byte[] bytes2 = str.getBytes("UTF-8");
            byte[] bArr = new byte[16];
            this.mRandom.nextBytes(bArr);
            IvParameterSpec ivParameterSpec = new IvParameterSpec(bArr);
            Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
            Mac mac = Mac.getInstance("HmacSHA256");
            cipher.init(1, this.mKey, ivParameterSpec);
            byte[] doFinal = cipher.doFinal(bytes2);
            mac.init(this.mHMACKey);
            mac.update(bytes);
            mac.update(doFinal);
            mac.update(bArr);
            byte[] doFinal2 = mac.doFinal();
            byte[] bArr2 = new byte[bytes.length + doFinal.length + 16 + doFinal2.length];
            System.arraycopy(bytes, 0, bArr2, 0, bytes.length);
            System.arraycopy(doFinal, 0, bArr2, bytes.length, doFinal.length);
            System.arraycopy(bArr, 0, bArr2, bytes.length + doFinal.length, 16);
            System.arraycopy(doFinal2, 0, bArr2, bytes.length + doFinal.length + 16, doFinal2.length);
            String str2 = new String(Base64.encode(bArr2, 2), "UTF-8");
            Logger.m14614v("StorageHelper:encrypt", "Finished encryption");
            return getEncodeVersionLengthPrefix() + ENCODE_VERSION + str2;
        }
        throw new IllegalArgumentException("Input is empty or null");
    }

    public SecretKey loadSecretKeyForEncryption() throws IOException, GeneralSecurityException {
        SecretKey loadSecretKeyForEncryption;
        synchronized (this) {
            loadSecretKeyForEncryption = loadSecretKeyForEncryption(AuthenticationSettings.INSTANCE.getSecretKeyData() == null ? VERSION_ANDROID_KEY_STORE : VERSION_USER_DEFINED);
        }
        return loadSecretKeyForEncryption;
    }

    public SecretKey loadSecretKeyForEncryption(String str) throws IOException, GeneralSecurityException {
        synchronized (this) {
            SecretKey secretKey = this.mKey;
            if (secretKey == null || this.mHMACKey == null) {
                this.mBlobVersion = str;
                return getKeyOrCreate(str);
            }
            return secretKey;
        }
    }
}
