package com.microsoft.xbox.idp.interop;

import android.content.Context;
import android.telephony.TelephonyManager;
import android.util.Log;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;

/* loaded from: classes3.dex */
public class Interop {
    private static final String DNET_SCOPE = "open-user.auth.dnet.xboxlive.com";
    private static final String PACKAGE_NAME_TO_REMOVE = "com.microsoft.onlineid.sample";
    private static final String POLICY = "mbi_ssl";
    private static final String PROD_SCOPE = "open-user.auth.xboxlive.com";
    public static final String TAG = "Interop";
    private static Context s_context;

    /* loaded from: classes3.dex */
    public interface ErrorCallback {
        void onError(int i, int i2, String str);
    }

    /* loaded from: classes3.dex */
    public interface EventInitializationCallback extends ErrorCallback {
        void onSuccess();
    }

    public static native boolean deinitializeInterop();

    public static native boolean initializeInterop(Context context);

    private static native void notificiation_registration_callback(String str);

    /* loaded from: classes3.dex */
    public enum AuthFlowScreenStatus {
        NO_ERROR(0),
        ERROR_USER_CANCEL(1),
        PROVIDER_ERROR(2);
        
        private final int f12598id;

        AuthFlowScreenStatus(int i) {
            this.f12598id = i;
        }

        public int getId() {
            return this.f12598id;
        }
    }

    /* loaded from: classes3.dex */
    public enum ErrorStatus {
        TRY_AGAIN(0),
        CLOSE(1);
        
        private final int f12599id;

        ErrorStatus(int i) {
            this.f12599id = i;
        }

        public int getId() {
            return this.f12599id;
        }
    }

    /* loaded from: classes3.dex */
    public enum ErrorType {
        BAN(0),
        CREATION(1),
        OFFLINE(2),
        CATCHALL(3);
        
        private final int f12600id;

        ErrorType(int i) {
            this.f12600id = i;
        }

        public int getId() {
            return this.f12600id;
        }
    }

    public static String GetLocalStoragePath(Context context) {
        return context.getFilesDir().getPath();
    }

    public static void NotificationRegisterCallback(String str) {
        Log.i("Interop", "NotificationRegisterCallback, token:" + str);
        notificiation_registration_callback(str);
    }

    public static String ReadConfigFile(Context context) {
        int read = 0;
        s_context = context;
        InputStream openRawResource = context.getResources().openRawResource(context.getResources().getIdentifier("xboxservices", "raw", context.getPackageName()));
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byte[] bArr = new byte[1024];
        while (true) {
            try {
                read = openRawResource.read(bArr);
            } catch (IOException unused) {
            }
            if (read != -1) {
                byteArrayOutputStream.write(bArr, 0, read);
            } else {
                try {
                    byteArrayOutputStream.close();
                    try {
                        openRawResource.close();
                        return byteArrayOutputStream.toString();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                } catch (IOException e2) {
                    throw new RuntimeException(e2);
                }
            }
        }
    }

    public static void RegisterWithGNS(Context context) {
        Log.i("XSAPI.Android", "trying to register..");
        try {
            TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            if (telephonyManager != null) {
                String deviceId = telephonyManager.getDeviceId();
                if (deviceId != null) {
                    NotificationRegisterCallback(deviceId);
                } else {
                    Log.e("Interop", "Failed to get device ID");
                }
            } else {
                Log.e("Interop", "Failed to get TelephonyManager");
            }
        } catch (Exception e) {
            Log.e("Interop", "Getting device ID failed, message:" + e.getMessage());
        }
    }

    public static Context getApplicationContext() {
        return s_context;
    }

    public static String getLocale() {
        String locale = Locale.getDefault().toString();
        Log.i("Interop", "locale is: " + locale);
        return locale;
    }

    public static String getSystemProxy() {
        String property;
        String property2 = System.getProperty("http.proxyHost");
        if (property2 == null || (property = System.getProperty("http.proxyPort")) == null) {
            return "";
        }
        String str = "http://" + property2 + "/" + property;
        Log.i("Interop", str);
        return str;
    }
}
