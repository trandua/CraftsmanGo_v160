package com.mojang.minecraftpe;

import android.os.Looper;
import android.util.Log;

//import com.craftsman.go.StringFog;

/* loaded from: classes3.dex */
public class NotificationListenerService {
    private static String sDeviceRegistrationToken = "";

    public native void nativePushNotificationReceived(int i, String str, String str2, String str3);

    public NotificationListenerService() {
        retrieveDeviceToken();
    }

    public void onNewToken(String str) {
//        String decrypt = StringFog.decrypt("8wafPxs67CbK\n", "vm/xWnhIjUA=\n");
//        Log.i(decrypt, StringFog.decrypt("w12IKzEhNjfuXd9bADcoftlXlG4bfmA=\n", "jTj/C3VEQF4=\n") + str);
        sDeviceRegistrationToken = str;
    }

    public static String getDeviceRegistrationToken() {
        if (sDeviceRegistrationToken.isEmpty()) {
            retrieveDeviceToken();
        }
        return sDeviceRegistrationToken;
    }

    private static void retrieveDeviceToken() {
        if (Thread.currentThread().equals(Looper.getMainLooper().getThread())) {
//            Log.e(StringFog.decrypt("uLAnp9+tnwOB\n", "9dlJwrzf/mU=\n"), StringFog.decrypt("nCzE/NnarhymKt/789q+Cbct1efs1r8LuyDVu83WuQ+7Jsbw+9a7FLEm5PrU1qNV+2PD/dDGoRny\nLd/hn8G4E/Is3rXS0qQT8jfY59rSqVM=\n", "0kOwlb+zzX0=\n"));
        }
    }
}
