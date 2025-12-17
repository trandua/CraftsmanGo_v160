package com.microsoft.xbox.toolkit.system;

import android.graphics.PixelFormat;
import android.os.Build;
import android.os.Environment;
import android.provider.Settings;
import android.util.TypedValue;
import android.view.Display;
import android.view.WindowManager;
import com.microsoft.xbox.toolkit.XLEAssert;
import com.microsoft.xboxtcui.XboxTcuiSdk;
import java.net.NetworkInterface;
import java.util.Collections;
import java.util.Iterator;

/* loaded from: classes3.dex */
public class SystemUtil {
    private static final int MAX_SD_SCREEN_PIXELS = 384000;

    public static int DIPtoPixels(float f) {
        return (int) TypedValue.applyDimension(1, f, XboxTcuiSdk.getResources().getDisplayMetrics());
    }

    public static int SPtoPixels(float f) {
        return (int) TypedValue.applyDimension(2, f, XboxTcuiSdk.getResources().getDisplayMetrics());
    }

    public static boolean TEST_randomFalseOutOf(int i) {
        XLEAssert.assertTrue(false);
        return true;
    }

    public static void TEST_randomSleep(int i) {
        XLEAssert.assertTrue(false);
    }

    public static int getColorDepth() {
        PixelFormat.getPixelFormatInfo(1, null);
        throw null;
    }

    public static String getDeviceId() {
        return Settings.Secure.getString(XboxTcuiSdk.getContentResolver(), "android_id");
    }

    public static String getDeviceModelName() {
        return Build.MODEL;
    }

    public static String getDeviceType() {
        XLEAssert.assertTrue(false);
        return "";
    }

    private static Display getDisplay() {
        return ((WindowManager) XboxTcuiSdk.getSystemService("window")).getDefaultDisplay();
    }

    /* JADX WARN: Removed duplicated region for block: B:6:0x0014 A[Catch: Exception -> 0x0061, TryCatch #0 {Exception -> 0x0061, blocks: (B:3:0x0002, B:4:0x000e, B:6:0x0014, B:8:0x001c, B:10:0x0026, B:13:0x002d, B:16:0x0038, B:17:0x004e, B:19:0x0054, B:20:0x005c), top: B:25:0x0002 }] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public static String getMACAddress(String str) {
        try {
            Iterator it = Collections.list(NetworkInterface.getNetworkInterfaces()).iterator();
            while (it.hasNext()) {
                NetworkInterface networkInterface = (NetworkInterface) it.next();
                if (str == null || networkInterface.getName().equalsIgnoreCase(str)) {
                    byte[] hardwareAddress = networkInterface.getHardwareAddress();
                    if (hardwareAddress == null) {
                        return "";
                    }
                    StringBuilder sb = new StringBuilder();
                    int length = hardwareAddress.length;
                    for (int i = 0; i < length; i++) {
                        sb.append(String.format("%02X:", Byte.valueOf(hardwareAddress[i])));
                    }
                    if (sb.length() > 0) {
                        sb.deleteCharAt(sb.length() - 1);
                    }
                    return sb.toString();
                }
                while (it.hasNext()) {
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public static int getOrientation() {
        int rotation = getRotation();
        return (rotation == 0 || rotation == 2) ? 1 : 2;
    }

    public static int getRotation() {
        return getDisplay().getRotation();
    }

    public static int getScreenHeight() {
        return XboxTcuiSdk.getResources().getDisplayMetrics().heightPixels;
    }

    public static float getScreenHeightInches() {
        return getScreenHeight() / XboxTcuiSdk.getResources().getDisplayMetrics().ydpi;
    }

    public static int getScreenWidth() {
        return XboxTcuiSdk.getResources().getDisplayMetrics().widthPixels;
    }

    public static float getScreenWidthHeightAspectRatio() {
        int screenWidth = getScreenWidth();
        int screenHeight = getScreenHeight();
        if (screenWidth <= 0 || screenHeight <= 0) {
            return 0.0f;
        }
        return screenWidth > screenHeight ? screenWidth / screenHeight : screenHeight / screenWidth;
    }

    public static float getScreenWidthInches() {
        return getScreenWidth() / XboxTcuiSdk.getResources().getDisplayMetrics().xdpi;
    }

    public static int getSdkInt() {
        return Build.VERSION.SDK_INT;
    }

    public static float getYDPI() {
        return XboxTcuiSdk.getResources().getDisplayMetrics().ydpi;
    }

    public static boolean isHDScreen() {
        return getScreenHeight() * getScreenWidth() > MAX_SD_SCREEN_PIXELS;
    }

    public static boolean isKindle() {
        String str = Build.MANUFACTURER;
        return str != null && "AMAZON".compareToIgnoreCase(str) == 0;
    }

    public static boolean isSDCardAvailable() {
        return Environment.getExternalStorageState().equals("mounted");
    }

    public static boolean isSlate() {
        return Math.sqrt(Math.pow((double) getScreenWidthInches(), 2.0d) + Math.pow((double) getScreenHeightInches(), 2.0d)) > 6.0d;
    }
}
