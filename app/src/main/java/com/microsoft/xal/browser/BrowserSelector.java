package com.microsoft.xal.browser;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.pm.Signature;
import android.net.Uri;
import android.util.Base64;
import androidx.browser.customtabs.CustomTabsService;
import androidx.core.os.EnvironmentCompat;
import com.microsoft.xal.browser.BrowserSelectionResult;
import com.microsoft.xal.logging.XalLogger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import org.jose4j.jws.AlgorithmIdentifiers;

/* loaded from: classes3.dex */
public class BrowserSelector {
    private static final Map<String, String> customTabsAllowedBrowsers;

    static {
        HashMap hashMap = new HashMap();
        customTabsAllowedBrowsers = hashMap;
        hashMap.put("com.android.chrome", "OJGKRT0HGZNU+LGa8F7GViztV4g=");
        hashMap.put("org.mozilla.firefox", "kg9Idqale0pqL0zK9l99Kc4m/yw=");
        hashMap.put("com.microsoft.emmx", "P2QOJ59jvOpxCCrn6MfvotoBTK0=");
        hashMap.put("com.sec.android.app.sbrowser", "nKUXDzgZGd/gRG/NqxixmhQ7MWM=");
    }

    private static boolean browserAllowedForCustomTabs(Context context, XalLogger xalLogger, String str) {
        String str2 = customTabsAllowedBrowsers.get(str);
        if (str2 == null) {
            return false;
        }
        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(str, 64);
            if (packageInfo == null) {
                xalLogger.Important("No package info found for package: " + str);
                return false;
            }
            for (Signature signature : packageInfo.signatures) {
                if (hashFromSignature(signature).equals(str2)) {
                    return true;
                }
            }
            return false;
        } catch (PackageManager.NameNotFoundException e) {
            xalLogger.Error("browserAllowedForCustomTabs() Error in getPackageInfo(): " + e);
            return false;
        } catch (NoSuchAlgorithmException e2) {
            xalLogger.Error("browserAllowedForCustomTabs() Error in hashFromSignature(): " + e2);
            return false;
        }
    }

    private static boolean browserInfoImpliesNoUserDefault(BrowserSelectionResult.BrowserInfo browserInfo) {
        return browserInfo.versionCode == 0 && browserInfo.versionName.equals(AlgorithmIdentifiers.NONE);
    }

    private static boolean browserSupportsCustomTabs(Context context, String str) {
        for (ResolveInfo resolveInfo : context.getPackageManager().queryIntentServices(new Intent(CustomTabsService.ACTION_CUSTOM_TABS_CONNECTION), 0)) {
            if (resolveInfo.serviceInfo.packageName.equals(str)) {
                return true;
            }
        }
        return false;
    }

    private static String hashFromSignature(Signature signature) throws NoSuchAlgorithmException {
        MessageDigest messageDigest = MessageDigest.getInstance("SHA");
        messageDigest.update(signature.toByteArray());
        return Base64.encodeToString(messageDigest.digest(), 2);
    }

    public static BrowserSelectionResult selectBrowser(Context context, boolean z) {
        String str;
        XalLogger xalLogger = new XalLogger("BrowserSelector");
        BrowserSelectionResult.BrowserInfo userDefaultBrowserInfo = userDefaultBrowserInfo(context, xalLogger);
        boolean z2 = false;
        if (z) {
            str = "inProcRequested";
        } else if (browserInfoImpliesNoUserDefault(userDefaultBrowserInfo)) {
            str = "noDefault";
        } else {
            String str2 = userDefaultBrowserInfo.packageName;
            z2 = browserSupportsCustomTabs(context, str2);
            if (!z2) {
                xalLogger.Important("selectBrowser() Default browser does not support custom tabs.");
                str = "CTNotSupported";
            } else if (!browserAllowedForCustomTabs(context, xalLogger, str2)) {
                xalLogger.Important("selectBrowser() Default browser supports custom tabs, but is not allowed.");
                str = "CTSupportedButNotAllowed";
            } else {
                xalLogger.Important("selectBrowser() Default browser supports custom tabs and is allowed.");
                z2 = true;
                str = "CTSupportedAndAllowed";
            }
        }
        BrowserSelectionResult browserSelectionResult = new BrowserSelectionResult(userDefaultBrowserInfo, str, z2);
        xalLogger.close();
        return browserSelectionResult;
    }

    private static BrowserSelectionResult.BrowserInfo userDefaultBrowserInfo(Context context, XalLogger xalLogger) {
        String str;
        ResolveInfo resolveActivity = context.getPackageManager().resolveActivity(new Intent("android.intent.action.VIEW", Uri.parse("https://microsoft.com")), 65536);
        String str2 = resolveActivity == null ? null : resolveActivity.activityInfo.packageName;
        if (str2 == null) {
            xalLogger.Important("userDefaultBrowserInfo() No default browser resolved.");
            return new BrowserSelectionResult.BrowserInfo(AlgorithmIdentifiers.NONE, 0, AlgorithmIdentifiers.NONE);
        } else if (str2.equals("android")) {
            xalLogger.Important("userDefaultBrowserInfo() System resolved as default browser.");
            return new BrowserSelectionResult.BrowserInfo("android", 0, AlgorithmIdentifiers.NONE);
        } else {
            int i = -1;
            try {
                PackageInfo packageInfo = context.getPackageManager().getPackageInfo(str2, 0);
                i = packageInfo.versionCode;
                str = packageInfo.versionName;
            } catch (PackageManager.NameNotFoundException e) {
                xalLogger.Error("userDefaultBrowserInfo() Error in getPackageInfo(): " + e);
                str = EnvironmentCompat.MEDIA_UNKNOWN;
            }
            xalLogger.Important("userDefaultBrowserInfo() Found " + str2 + " as user's default browser.");
            return new BrowserSelectionResult.BrowserInfo(str2, i, str);
        }
    }
}
