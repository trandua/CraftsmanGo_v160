package com.microsoft.xal.androidjava;

import android.content.Context;
import android.os.Build;
import android.provider.Settings;
import com.google.android.vending.expansion.downloader.Constants;

/* loaded from: classes3.dex */
public class DeviceInfo {
    public static String GetDeviceId(Context context) {
        String string = Settings.Secure.getString(context.getContentResolver(), "android_id");
        int length = string.length();
        int i = 0;
        if (length < 32) {
            StringBuilder sb = new StringBuilder();
            sb.append(String.format("%0" + (32 - length) + "d", 0));
            sb.append(string);
            string = sb.toString();
        }
        StringBuilder sb2 = new StringBuilder();
        int i2 = 0;
        while (i < 5) {
            if (i != 0) {
                sb2.append(Constants.FILENAME_SEQUENCE_SEPARATOR);
            }
            int i3 = new int[]{8, 4, 4, 4, 12}[i] + i2;
            sb2.append(string.substring(i2, i3));
            i++;
            i2 = i3;
        }
        return sb2.toString();
    }

    public static String GetOsVersion() {
        return Build.VERSION.RELEASE;
    }
}
