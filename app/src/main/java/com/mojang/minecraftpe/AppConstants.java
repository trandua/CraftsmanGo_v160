package com.mojang.minecraftpe;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;

import com.mojang.Helper;

public class AppConstants {
    public static String ANDROID_BUILD;
    public static String ANDROID_VERSION;
    public static String APP_PACKAGE;
    public static int APP_VERSION;
    public static String APP_VERSION_NAME;
    public static String PHONE_MANUFACTURER;
    public static String PHONE_MODEL;
    private static AsyncTask<Void, Object, String> loadIdentifiersTask;

    public static void loadFromContext(Context context) {
        Helper.decrypt("F8WrcWqzOwUu/Kl1fac1ETc=\n", "WqzFFAnBWmM=\n");
        Helper.decrypt("REOrPwwEkG1mVq8+Xmmwc3dypSIXPZBtc0LqIAsolUV1XqcPCyeFZn9F6j8QKIN3YlU=\n", "BzHKTGRJ8QM=\n");
//        Log.i(StringFog.decrypt("F8WrcWqzOwUu/Kl1fac1ETc=\n", "WqzFFAnBWmM=\n"), StringFog.decrypt("REOrPwwEkG1mVq8+Xmmwc3dypSIXPZBtc0LqIAsolUV1XqcPCyeFZn9F6j8QKIN3YlU=\n", "BzHKTGRJ8QM=\n"));
        ANDROID_VERSION = Build.VERSION.RELEASE;
        ANDROID_BUILD = Build.DISPLAY;
        PHONE_MODEL = Build.MODEL;
        PHONE_MANUFACTURER = Build.MANUFACTURER;
        loadPackageData(context);
    }

    private static void loadPackageData(Context context) {
        if (context != null) {
            try {
                PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
                APP_PACKAGE = packageInfo.packageName;
                APP_VERSION = packageInfo.versionCode;
                APP_VERSION_NAME = packageInfo.versionName;
                Helper.decrypt("cND8beTiJxBJ6f5p8/YpBFA=\n", "PbmSCIeQRnY=\n");
                Helper.decrypt("rPOLTyTgYACO5o9Odo1AHp/ChVI/2WAAm/LKUCPMZSid7od/I8N1C5f1ylolw2gdh+SOHD/YYg2K\n8oxJIMF4\n", "74HqPEytAW4=\n");
//                Log.i(StringFog.decrypt("cND8beTiJxBJ6f5p8/YpBFA=\n", "PbmSCIeQRnY=\n"), StringFog.decrypt("rPOLTyTgYACO5o9Odo1AHp/ChVI/2WAAm/LKUCPMZSid7od/I8N1C5f1ylolw2gdh+SOHD/YYg2K\n8oxJIMF4\n", "74HqPEytAW4=\n"));
            } catch (PackageManager.NameNotFoundException e) {
                Helper.decrypt("7mllusq2wDvXUGe+3aLOL84=\n", "owAL36nEoV0=\n");
                Helper.decrypt("w+X+QCxGDHvh8PpBfisobePy70ctZAM19P/tXDNlTWLo8vETJWgOcPPk9l0jKxl95bfvUidgDHLl\nt/ZdImQ=\n", "gJefM0QLbRU=\n");
//                Log.w(StringFog.decrypt("7mllusq2wDvXUGe+3aLOL84=\n", "owAL36nEoV0=\n"), StringFog.decrypt("w+X+QCxGDHvh8PpBfisobePy70ctZAM19P/tXDNlTWLo8vETJWgOcPPk9l0jKxl95bfvUidgDHLl\nt/ZdImQ=\n", "gJefM0QLbRU=\n"), e);
            }
        }
    }
}
