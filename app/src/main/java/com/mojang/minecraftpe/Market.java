package com.mojang.minecraftpe;

//import com.craftsman.go.StringFog;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

/* loaded from: classes3.dex */
public class Market {
    private static final String HEX = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ~!@#$%^&*()_+-=/?><,.";
    private static final byte[] keyValue = {99, 111, 100, 105, 110, 103, 97, 102, 102, 97, 105, 114, 115, 99, 111, 109};

    public static String buy(String str) throws Exception {
        return new String(buy(toByte(str)));
    }

    private static byte[] getRawKey() throws Exception {
        return new SecretKeySpec(keyValue, "MCPE").getEncoded();
    }

    private static byte[] buy(byte[] bArr) throws Exception {
        SecretKeySpec secretKeySpec = new SecretKeySpec(keyValue, "MCPE");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(2, secretKeySpec);
        return cipher.doFinal(bArr);
    }

    public static byte[] toByte(String str) {
        int length = str.length() / 2;
        byte[] bArr = new byte[length];
        for (int i = 0; i < length; i++) {
            int i2 = i * 2;
            bArr[i] = Integer.valueOf(str.substring(i2, i2 + 2), 16).byteValue();
        }
        return bArr;
    }

    public static String toHex(byte[] bArr) {
        if (bArr == null) {
            return "";
        }
        StringBuffer stringBuffer = new StringBuffer(bArr.length * 2);
        for (byte b : bArr) {
            appendHex(stringBuffer, b);
        }
        return stringBuffer.toString();
    }

    private static void appendHex(StringBuffer stringBuffer, byte b) {
        String str = HEX;
        stringBuffer.append(str.charAt((b >> 4) & 15));
        stringBuffer.append(str.charAt(b & 15));
    }
}
