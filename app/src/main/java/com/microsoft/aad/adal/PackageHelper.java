package com.microsoft.aad.adal;

import android.accounts.AccountManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.util.Base64;
import com.microsoft.aad.adal.AuthenticationConstants;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes3.dex */
public class PackageHelper {
    private static final String TAG = "CallerInfo";
    private final AccountManager mAcctManager;
    private Context mContext;

    public PackageHelper(Context context) {
        this.mContext = context;
        this.mAcctManager = AccountManager.get(context);
    }

    public static String getBrokerRedirectUrl(String str, String str2) {
        if (!StringExtensions.isNullOrBlank(str) && !StringExtensions.isNullOrBlank(str2)) {
            try {
                return String.format("%s://%s/%s", AuthenticationConstants.Broker.REDIRECT_PREFIX, URLEncoder.encode(str, "UTF_8"), URLEncoder.encode(str2, "UTF_8"));
            } catch (UnsupportedEncodingException e) {
                Logger.m14610e(TAG, ADALError.ENCODING_IS_NOT_SUPPORTED.getDescription(), "", ADALError.ENCODING_IS_NOT_SUPPORTED, e);
            }
        }
        return "";
    }

    public String getCurrentSignatureForPackage(String str) {
        try {
            PackageInfo packageInfo = this.mContext.getPackageManager().getPackageInfo(str, 64);
            if (packageInfo != null && packageInfo.signatures != null && packageInfo.signatures.length > 0) {
                Signature signature = packageInfo.signatures[0];
                MessageDigest messageDigest = MessageDigest.getInstance("SHA");
                messageDigest.update(signature.toByteArray());
                return Base64.encodeToString(messageDigest.digest(), 2);
            }
            return null;
        } catch (PackageManager.NameNotFoundException unused) {
            Logger.m14609e(TAG, "Calling App's package does not exist in PackageManager. ", "", ADALError.APP_PACKAGE_NAME_NOT_FOUND);
            return null;
        } catch (NoSuchAlgorithmException unused2) {
            Logger.m14609e(TAG, "Digest SHA algorithm does not exists. ", "", ADALError.DEVICE_NO_SUCH_ALGORITHM);
            return null;
        }
    }

    public int getUIDForPackage(String str) {
        try {
            ApplicationInfo applicationInfo = this.mContext.getPackageManager().getApplicationInfo(str, 0);
            if (applicationInfo != null) {
                return applicationInfo.uid;
            }
            return 0;
        } catch (PackageManager.NameNotFoundException e) {
            Logger.m14610e(TAG, "Package is not found. ", "Package name: " + str, ADALError.PACKAGE_NAME_NOT_FOUND, e);
            return 0;
        }
    }
}
