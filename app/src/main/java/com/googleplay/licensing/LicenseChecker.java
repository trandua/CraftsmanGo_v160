package com.googleplay.licensing;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import com.googleplay.licensing.ILicenseResultListener;
import com.googleplay.licensing.ILicensingService;
import com.googleplay.util.Base64;
import com.googleplay.util.Base64DecoderException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

/* loaded from: classes2.dex */
public class LicenseChecker implements ServiceConnection {
    private static final boolean DEBUG_LICENSE_ERROR = false;
    private static final String KEY_FACTORY_ALGORITHM = "RSA";
    private static final SecureRandom RANDOM = new SecureRandom();
    private static final String TAG = "LicenseChecker";
    private static final int TIMEOUT_MS = 10000;
    private final Context mContext;
    private Handler mHandler;
    private final String mPackageName;
    private final Policy mPolicy;
    private PublicKey mPublicKey;
    private ILicensingService mService;
    private final String mVersionCode;
    private final Set<LicenseValidator> mChecksInProgress = new HashSet();
    private final Queue<LicenseValidator> mPendingChecks = new LinkedList();

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes2.dex */
    public class ResultListener extends ILicenseResultListener.Stub {
        private static final int ERROR_CONTACTING_SERVER = 257;
        private static final int ERROR_INVALID_PACKAGE_NAME = 258;
        private static final int ERROR_NON_MATCHING_UID = 259;
        private Runnable mOnTimeout;
        private final LicenseValidator mValidator;

        public ResultListener(LicenseValidator licenseValidator) {
            this.mValidator = licenseValidator;
            this.mOnTimeout = new Runnable() { // from class: com.googleplay.licensing.LicenseChecker.ResultListener.1
                @Override // java.lang.Runnable
                public void run() {
                    Log.i(LicenseChecker.TAG, "Check timed out.");
                    LicenseChecker.this.handleServiceConnectionError(ResultListener.this.mValidator);
                    LicenseChecker.this.finishCheck(ResultListener.this.mValidator);
                }
            };
            startTimeout();
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void clearTimeout() {
            Log.i(LicenseChecker.TAG, "Clearing timeout.");
            LicenseChecker.this.mHandler.removeCallbacks(this.mOnTimeout);
        }

        private void startTimeout() {
            Log.i(LicenseChecker.TAG, "Start monitoring timeout.");
            LicenseChecker.this.mHandler.postDelayed(this.mOnTimeout, 10000);
        }

        @Override // com.googleplay.licensing.ILicenseResultListener
        public void verifyLicense(final int i, final String str, final String str2) {
            LicenseChecker.this.mHandler.post(new Runnable() { // from class: com.googleplay.licensing.LicenseChecker.ResultListener.2
                @Override // java.lang.Runnable
                public void run() {
                    Log.i(LicenseChecker.TAG, "Received response.");
                    if (LicenseChecker.this.mChecksInProgress.contains(ResultListener.this.mValidator)) {
                        ResultListener.this.clearTimeout();
                        ResultListener.this.mValidator.verify(LicenseChecker.this.mPublicKey, i, str, str2);
                        LicenseChecker.this.finishCheck(ResultListener.this.mValidator);
                    }
                }
            });
        }
    }

    public LicenseChecker(Context context, Policy policy, String str) {
        this.mContext = context;
        this.mPolicy = policy;
        this.mPublicKey = generatePublicKey(str);
        String packageName = context.getPackageName();
        this.mPackageName = packageName;
        this.mVersionCode = getVersionCode(context, packageName);
        HandlerThread handlerThread = new HandlerThread("background thread");
        handlerThread.start();
        this.mHandler = new Handler(handlerThread.getLooper());
    }

    private void cleanupService() {
        if (this.mService != null) {
            try {
                this.mContext.unbindService(this);
            } catch (IllegalArgumentException unused) {
                Log.e(TAG, "Unable to unbind from licensing service (already unbound)");
            }
            this.mService = null;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void finishCheck(LicenseValidator licenseValidator) {
        synchronized (this) {
            this.mChecksInProgress.remove(licenseValidator);
            if (this.mChecksInProgress.isEmpty()) {
                cleanupService();
            }
        }
    }

    private int generateNonce() {
        return RANDOM.nextInt();
    }

    private static PublicKey generatePublicKey(String str) {
        try {
            return KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(Base64.decode(str)));
        } catch (Base64DecoderException e) {
            Log.e(TAG, "Could not decode from Base64.");
            throw new IllegalArgumentException(e);
        } catch (NoSuchAlgorithmException e2) {
            throw new RuntimeException(e2);
        } catch (InvalidKeySpecException e3) {
            Log.e(TAG, "Invalid key specification.");
            throw new IllegalArgumentException(e3);
        }
    }

    private static String getVersionCode(Context context, String str) {
        try {
            return String.valueOf(context.getPackageManager().getPackageInfo(str, 0).versionCode);
        } catch (PackageManager.NameNotFoundException unused) {
            Log.e(TAG, "Package not found. could not get version code.");
            return "";
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleServiceConnectionError(LicenseValidator licenseValidator) {
        synchronized (this) {
            this.mPolicy.processServerResponse(Policy.RETRY, null);
            if (this.mPolicy.allowAccess()) {
                licenseValidator.getCallback().allow(Policy.RETRY);
            } else {
                licenseValidator.getCallback().dontAllow(Policy.RETRY);
            }
        }
    }

    private void runChecks() {
        while (true) {
            LicenseValidator poll = this.mPendingChecks.poll();
            if (poll == null) {
                return;
            }
            try {
                Log.i(TAG, "Calling checkLicense on service for " + poll.getPackageName());
                this.mService.checkLicense((long) poll.getNonce(), poll.getPackageName(), new ResultListener(poll));
                this.mChecksInProgress.add(poll);
            } catch (RemoteException e) {
                Log.w(TAG, "RemoteException in checkLicense call.", e);
                handleServiceConnectionError(poll);
            }
        }
    }

    public void checkAccess(LicenseCheckerCallback licenseCheckerCallback) {
        synchronized (this) {
            if (this.mPolicy.allowAccess()) {
                Log.i(TAG, "Using cached license response");
                licenseCheckerCallback.allow(256);
            } else {
                LicenseValidator licenseValidator = new LicenseValidator(this.mPolicy, new NullDeviceLimiter(), licenseCheckerCallback, generateNonce(), this.mPackageName, this.mVersionCode);
                if (this.mService == null) {
                    Log.i(TAG, "Binding to licensing service.");
                    try {
                        if (this.mContext.bindService(new Intent(new String(Base64.decode("Y29tLmFuZHJvaWQudmVuZGluZy5saWNlbnNpbmcuSUxpY2Vuc2luZ1NlcnZpY2U="))).setPackage("com.android.vending"), this, Context.BIND_AUTO_CREATE)) {
                            this.mPendingChecks.offer(licenseValidator);
                        } else {
                            Log.e(TAG, "Could not bind to service.");
                            handleServiceConnectionError(licenseValidator);
                        }
                    } catch (Base64DecoderException e) {
                        e.printStackTrace();
                    } catch (SecurityException unused) {
                        licenseCheckerCallback.applicationError(6);
                    }
                } else {
                    this.mPendingChecks.offer(licenseValidator);
                    runChecks();
                }
            }
        }
    }

    public void onDestroy() {
        synchronized (this) {
            cleanupService();
            this.mHandler.getLooper().quit();
        }
    }

    @Override // android.content.ServiceConnection
    public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
        synchronized (this) {
            this.mService = ILicensingService.Stub.asInterface(iBinder);
            runChecks();
        }
    }

    @Override // android.content.ServiceConnection
    public void onServiceDisconnected(ComponentName componentName) {
        synchronized (this) {
            Log.w(TAG, "Service unexpectedly disconnected.");
            this.mService = null;
        }
    }
}
