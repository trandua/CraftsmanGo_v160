package com.microsoft.aad.adal;

import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Looper;
import android.os.NetworkOnMainThreadException;
import android.util.SparseArray;
import androidx.exifinterface.media.ExifInterface;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
//import com.crafting.minecrafting.lokicraft.BuildConfig;
import com.microsoft.aad.adal.AuthenticationConstants;
import com.microsoft.aad.adal.AuthenticationRequest;
import com.microsoft.aad.adal.BrokerProxy;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.atomic.AtomicReference;

/* loaded from: classes3.dex */
public class AuthenticationContext {
    private static final SparseArray<AuthenticationRequestState> DELEGATE_MAP = new SparseArray<>();
    private static final int EXCLUDE_INDEX = 8;
    private static final String REQUEST_ID = "requestId:";
    private static final String TAG = "AuthenticationContext";
    private String mAuthority;
    private Context mContext;
    private boolean mIsAuthorityValidated;
    private ITokenCacheStore mTokenCacheStore;
    private boolean mValidateAuthority;
    private BrokerProxy mBrokerProxy = null;
    private boolean mExtendedLifetimeEnabled = false;
    private UUID mRequestCorrelationId = null;

    public static String getVersionName() {
        return "1.14.0";
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes3.dex */
    public static final class SettableFuture<V> extends FutureTask<V> {
        SettableFuture() {
            super(new Callable<V>() { // from class: com.microsoft.aad.adal.AuthenticationContext.SettableFuture.1
                @Override // java.util.concurrent.Callable
                public V call() throws Exception {
                    return null;
                }
            });
        }

        @Override // java.util.concurrent.FutureTask
        public void set(V v) {
            super.set(v);
        }

        @Override // java.util.concurrent.FutureTask
        public void setException(Throwable th) {
            super.setException(th);
        }
    }

    public AuthenticationContext(Context context, String str, ITokenCacheStore iTokenCacheStore) {
        initialize(context, str, iTokenCacheStore, true, false);
    }

    public AuthenticationContext(Context context, String str, boolean z) {
        PRNGFixes.apply();
        initialize(context, str, new DefaultTokenCacheStore(context), z, true);
    }

    public AuthenticationContext(Context context, String str, boolean z, ITokenCacheStore iTokenCacheStore) {
        initialize(context, str, iTokenCacheStore, z, false);
    }

    private boolean checkADFSValidationRequirements(String str) throws AuthenticationException {
        URL url = StringExtensions.getUrl(this.mAuthority);
        if (this.mAuthority == null || url == null) {
            throw new AuthenticationException(ADALError.DEVELOPER_AUTHORITY_IS_NOT_VALID_URL);
        }
        if (UrlExtensions.isADFSAuthority(url) && this.mValidateAuthority && !this.mIsAuthorityValidated && str == null) {
            ADALError aDALError = ADALError.DEVELOPER_AUTHORITY_CAN_NOT_BE_VALIDED;
            throw new AuthenticationException(aDALError, "AD FS validation requires a loginHint be provided or an " + getClass().getSimpleName() + " in which the current authority has previously been validated.");
        }
        return true;
    }

    private boolean checkADFSValidationRequirements(String str, AuthenticationCallback<AuthenticationResult> authenticationCallback) {
        try {
            return checkADFSValidationRequirements(str);
        } catch (AuthenticationException e) {
            authenticationCallback.onError(e);
            return false;
        }
    }

    private void checkInternetPermission() {
        if (this.mContext.getPackageManager().checkPermission("android.permission.INTERNET", this.mContext.getPackageName()) != PackageManager.PERMISSION_GRANTED) {
            throw new IllegalStateException(new AuthenticationException(ADALError.DEVELOPER_INTERNET_PERMISSION_MISSING));
        }
    }

    private boolean checkPreRequirements(String str, String str2) throws AuthenticationException {
        if (this.mContext != null) {
            if (AuthenticationSettings.INSTANCE.getUseBroker()) {
                this.mBrokerProxy.verifyBrokerPermissionsAPI22AndLess();
            }
            if (StringExtensions.isNullOrBlank(str)) {
                throw new IllegalArgumentException(AuthenticationConstants.AAD.RESOURCE);
            }
            if (StringExtensions.isNullOrBlank(str2)) {
                throw new IllegalArgumentException("clientId");
            }
            return true;
        }
        throw new IllegalArgumentException("context", new AuthenticationException(ADALError.DEVELOPER_CONTEXT_IS_NOT_PROVIDED));
    }

    private boolean checkPreRequirements(String str, String str2, AuthenticationCallback<AuthenticationResult> authenticationCallback) {
        if (authenticationCallback != null) {
            try {
                return checkPreRequirements(str, str2);
            } catch (AuthenticationException e) {
                authenticationCallback.onError(e);
                return false;
            }
        }
        throw new IllegalArgumentException("callback");
    }

    private AcquireTokenRequest createAcquireTokenRequest(APIEvent aPIEvent) {
        return new AcquireTokenRequest(this.mContext, this, aPIEvent);
    }

    private APIEvent createApiEvent(Context context, String str, String str2, String str3) {
        APIEvent aPIEvent = new APIEvent("Microsoft.ADAL.api_event", context, str);
        aPIEvent.setRequestId(str2);
        aPIEvent.setAPIId(str3);
        aPIEvent.setAuthority(getAuthority());
        Telemetry.getInstance().startEvent(str2, aPIEvent.getEventName());
        return aPIEvent;
    }

    private static String extractAuthority(String str) {
        int indexOf;
        int indexOf2;
        if (StringExtensions.isNullOrBlank(str) || (indexOf = str.indexOf(47, 8)) < 0 || indexOf == str.length() - 1 || ((indexOf2 = str.indexOf("/", 0)) >= 0 && indexOf2 <= indexOf + 1)) {
            throw new IllegalArgumentException(AuthenticationConstants.OAuth2.AUTHORITY);
        }
        return indexOf2 >= 0 ? str.substring(0, indexOf2) : str;
    }

    private String getRedirectUri(String str) {
        return StringExtensions.isNullOrBlank(str) ? this.mContext.getApplicationContext().getPackageName() : str;
    }

    private void initialize(Context context, String str, ITokenCacheStore iTokenCacheStore, boolean z, boolean z2) {
        if (context == null) {
            throw new IllegalArgumentException("appContext");
        }
        if (str != null) {
            BrokerProxy brokerProxy = new BrokerProxy(context);
            this.mBrokerProxy = brokerProxy;
            if (z2 || brokerProxy.canUseLocalCache(str)) {
                this.mContext = context;
                checkInternetPermission();
                this.mAuthority = extractAuthority(str);
                this.mValidateAuthority = z;
                this.mTokenCacheStore = iTokenCacheStore;
                return;
            }
            throw new UnsupportedOperationException("Local cache is not supported for broker usage");
        }
        throw new IllegalArgumentException(AuthenticationConstants.OAuth2.AUTHORITY);
    }

    private void throwIfClaimsInBothExtraQpAndClaimsParameter(String str, String str2) {
        if (!StringExtensions.isNullOrBlank(str) && !StringExtensions.isNullOrBlank(str2) && str2.contains("claims")) {
            throw new IllegalArgumentException("claims cannot be sent in claims parameter and extra qp.");
        }
    }

    private IWindowComponent wrapActivity(Activity activity) {
        if (activity != null) {
            return new IWindowComponent() { // from class: com.microsoft.aad.adal.AuthenticationContext.1
                private Activity mRefActivity;
//                final /* synthetic */ Activity val$activity;
//
//                {
//                    this.val$activity = activity;
//                    this.mRefActivity = activity;
//                }

                @Override // com.microsoft.aad.adal.IWindowComponent
                public void startActivityForResult(Intent intent, int i) {
                    Activity activity2 = this.mRefActivity;
                    if (activity2 != null) {
                        activity2.startActivityForResult(intent, i);
                    }
                }
            };
        }
        throw new IllegalArgumentException("activity");
    }

    public void acquireToken(Activity activity, String str, String str2, String str3, PromptBehavior promptBehavior, AuthenticationCallback<AuthenticationResult> authenticationCallback) {
        if (checkPreRequirements(str, str2, authenticationCallback) && checkADFSValidationRequirements(null, authenticationCallback)) {
            String redirectUri = getRedirectUri(str3);
            String registerNewRequest = Telemetry.registerNewRequest();
            APIEvent createApiEvent = createApiEvent(this.mContext, str2, registerNewRequest, "108");
            createApiEvent.setPromptBehavior(promptBehavior.toString());
            AuthenticationRequest authenticationRequest = new AuthenticationRequest(this.mAuthority, str, str2, redirectUri, null, promptBehavior, null, getRequestCorrelationId(), getExtendedLifetimeEnabled(), null);
            authenticationRequest.setTelemetryRequestId(registerNewRequest);
            createAcquireTokenRequest(createApiEvent).acquireToken(wrapActivity(activity), false, authenticationRequest, authenticationCallback);
        }
    }

    public void acquireToken(Activity activity, String str, String str2, String str3, PromptBehavior promptBehavior, String str4, AuthenticationCallback<AuthenticationResult> authenticationCallback) {
        if (checkPreRequirements(str, str2, authenticationCallback) && checkADFSValidationRequirements(null, authenticationCallback)) {
            String redirectUri = getRedirectUri(str3);
            String registerNewRequest = Telemetry.registerNewRequest();
            APIEvent createApiEvent = createApiEvent(this.mContext, str2, registerNewRequest, "111");
            createApiEvent.setPromptBehavior(promptBehavior.toString());
            AuthenticationRequest authenticationRequest = new AuthenticationRequest(this.mAuthority, str, str2, redirectUri, null, promptBehavior, str4, getRequestCorrelationId(), getExtendedLifetimeEnabled(), null);
            authenticationRequest.setTelemetryRequestId(registerNewRequest);
            createAcquireTokenRequest(createApiEvent).acquireToken(wrapActivity(activity), false, authenticationRequest, authenticationCallback);
        }
    }

    public void acquireToken(Activity activity, String str, String str2, String str3, String str4, AuthenticationCallback<AuthenticationResult> authenticationCallback) {
        if (checkPreRequirements(str, str2, authenticationCallback) && checkADFSValidationRequirements(str4, authenticationCallback)) {
            String registerNewRequest = Telemetry.registerNewRequest();
            APIEvent createApiEvent = createApiEvent(this.mContext, str2, registerNewRequest, "100");
            createApiEvent.setLoginHint(str4);
            AuthenticationRequest authenticationRequest = new AuthenticationRequest(this.mAuthority, str, str2, getRedirectUri(str3), str4, PromptBehavior.Auto, null, getRequestCorrelationId(), getExtendedLifetimeEnabled(), null);
            authenticationRequest.setUserIdentifierType(AuthenticationRequest.UserIdentifierType.LoginHint);
            authenticationRequest.setTelemetryRequestId(registerNewRequest);
            createAcquireTokenRequest(createApiEvent).acquireToken(wrapActivity(activity), false, authenticationRequest, authenticationCallback);
        }
    }

    public void acquireToken(Activity activity, String str, String str2, String str3, String str4, PromptBehavior promptBehavior, String str5, AuthenticationCallback<AuthenticationResult> authenticationCallback) {
        if (checkPreRequirements(str, str2, authenticationCallback) && checkADFSValidationRequirements(str4, authenticationCallback)) {
            String redirectUri = getRedirectUri(str3);
            String registerNewRequest = Telemetry.registerNewRequest();
            APIEvent createApiEvent = createApiEvent(this.mContext, str2, registerNewRequest, "115");
            createApiEvent.setPromptBehavior(promptBehavior.toString());
            createApiEvent.setLoginHint(str4);
            AuthenticationRequest authenticationRequest = new AuthenticationRequest(this.mAuthority, str, str2, redirectUri, str4, promptBehavior, str5, getRequestCorrelationId(), getExtendedLifetimeEnabled(), null);
            authenticationRequest.setUserIdentifierType(AuthenticationRequest.UserIdentifierType.LoginHint);
            authenticationRequest.setTelemetryRequestId(registerNewRequest);
            createAcquireTokenRequest(createApiEvent).acquireToken(wrapActivity(activity), false, authenticationRequest, authenticationCallback);
        }
    }

    public void acquireToken(Activity activity, String str, String str2, String str3, String str4, PromptBehavior promptBehavior, String str5, String str6, AuthenticationCallback<AuthenticationResult> authenticationCallback) {
        throwIfClaimsInBothExtraQpAndClaimsParameter(str6, str5);
        if (checkPreRequirements(str, str2, authenticationCallback) && checkADFSValidationRequirements(str4, authenticationCallback)) {
            String redirectUri = getRedirectUri(str3);
            String registerNewRequest = Telemetry.registerNewRequest();
            APIEvent createApiEvent = createApiEvent(this.mContext, str2, registerNewRequest, "118");
            createApiEvent.setPromptBehavior(promptBehavior.toString());
            createApiEvent.setLoginHint(str4);
            AuthenticationRequest authenticationRequest = new AuthenticationRequest(this.mAuthority, str, str2, redirectUri, str4, promptBehavior, str5, getRequestCorrelationId(), getExtendedLifetimeEnabled(), str6);
            authenticationRequest.setUserIdentifierType(AuthenticationRequest.UserIdentifierType.LoginHint);
            authenticationRequest.setTelemetryRequestId(registerNewRequest);
            createAcquireTokenRequest(createApiEvent).acquireToken(wrapActivity(activity), false, authenticationRequest, authenticationCallback);
        }
    }

    public void acquireToken(Activity activity, String str, String str2, String str3, String str4, String str5, AuthenticationCallback<AuthenticationResult> authenticationCallback) {
        if (checkPreRequirements(str, str2, authenticationCallback) && checkADFSValidationRequirements(str4, authenticationCallback)) {
            String registerNewRequest = Telemetry.registerNewRequest();
            APIEvent createApiEvent = createApiEvent(this.mContext, str2, registerNewRequest, "104");
            createApiEvent.setLoginHint(str4);
            AuthenticationRequest authenticationRequest = new AuthenticationRequest(this.mAuthority, str, str2, getRedirectUri(str3), str4, PromptBehavior.Auto, str5, getRequestCorrelationId(), getExtendedLifetimeEnabled(), null);
            authenticationRequest.setUserIdentifierType(AuthenticationRequest.UserIdentifierType.LoginHint);
            authenticationRequest.setTelemetryRequestId(registerNewRequest);
            createAcquireTokenRequest(createApiEvent).acquireToken(wrapActivity(activity), false, authenticationRequest, authenticationCallback);
        }
    }

    public void acquireToken(IWindowComponent iWindowComponent, String str, String str2, String str3, String str4, PromptBehavior promptBehavior, String str5, AuthenticationCallback<AuthenticationResult> authenticationCallback) {
        if (checkPreRequirements(str, str2, authenticationCallback) && checkADFSValidationRequirements(str4, authenticationCallback)) {
            String redirectUri = getRedirectUri(str3);
            String registerNewRequest = Telemetry.registerNewRequest();
            APIEvent createApiEvent = createApiEvent(this.mContext, str2, registerNewRequest, "116");
            createApiEvent.setPromptBehavior(promptBehavior.toString());
            createApiEvent.setLoginHint(str4);
            AuthenticationRequest authenticationRequest = new AuthenticationRequest(this.mAuthority, str, str2, redirectUri, str4, promptBehavior, str5, getRequestCorrelationId(), getExtendedLifetimeEnabled(), null);
            authenticationRequest.setUserIdentifierType(AuthenticationRequest.UserIdentifierType.LoginHint);
            authenticationRequest.setTelemetryRequestId(registerNewRequest);
            createAcquireTokenRequest(createApiEvent).acquireToken(iWindowComponent, false, authenticationRequest, authenticationCallback);
        }
    }

    public void acquireToken(IWindowComponent iWindowComponent, String str, String str2, String str3, String str4, PromptBehavior promptBehavior, String str5, String str6, AuthenticationCallback<AuthenticationResult> authenticationCallback) {
        throwIfClaimsInBothExtraQpAndClaimsParameter(str6, str5);
        if (checkPreRequirements(str, str2, authenticationCallback) && checkADFSValidationRequirements(str4, authenticationCallback)) {
            String redirectUri = getRedirectUri(str3);
            String registerNewRequest = Telemetry.registerNewRequest();
            APIEvent createApiEvent = createApiEvent(this.mContext, str2, registerNewRequest, "119");
            createApiEvent.setPromptBehavior(promptBehavior.toString());
            createApiEvent.setLoginHint(str4);
            AuthenticationRequest authenticationRequest = new AuthenticationRequest(this.mAuthority, str, str2, redirectUri, str4, promptBehavior, str5, getRequestCorrelationId(), getExtendedLifetimeEnabled(), str6);
            authenticationRequest.setUserIdentifierType(AuthenticationRequest.UserIdentifierType.LoginHint);
            authenticationRequest.setTelemetryRequestId(registerNewRequest);
            createAcquireTokenRequest(createApiEvent).acquireToken(iWindowComponent, false, authenticationRequest, authenticationCallback);
        }
    }

    public void acquireToken(String str, String str2, String str3, String str4, PromptBehavior promptBehavior, String str5, AuthenticationCallback<AuthenticationResult> authenticationCallback) {
        if (checkPreRequirements(str, str2, authenticationCallback) && checkADFSValidationRequirements(str4, authenticationCallback)) {
            String redirectUri = getRedirectUri(str3);
            String registerNewRequest = Telemetry.registerNewRequest();
            APIEvent createApiEvent = createApiEvent(this.mContext, str2, registerNewRequest, "117");
            createApiEvent.setPromptBehavior(promptBehavior.toString());
            createApiEvent.setLoginHint(str4);
            AuthenticationRequest authenticationRequest = new AuthenticationRequest(this.mAuthority, str, str2, redirectUri, str4, promptBehavior, str5, getRequestCorrelationId(), getExtendedLifetimeEnabled(), null);
            authenticationRequest.setUserIdentifierType(AuthenticationRequest.UserIdentifierType.LoginHint);
            authenticationRequest.setTelemetryRequestId(registerNewRequest);
            createAcquireTokenRequest(createApiEvent).acquireToken(null, true, authenticationRequest, authenticationCallback);
        }
    }

    public void acquireToken(String str, String str2, String str3, String str4, PromptBehavior promptBehavior, String str5, String str6, AuthenticationCallback<AuthenticationResult> authenticationCallback) {
        throwIfClaimsInBothExtraQpAndClaimsParameter(str6, str5);
        if (checkPreRequirements(str, str2, authenticationCallback) && checkADFSValidationRequirements(str4, authenticationCallback)) {
            String redirectUri = getRedirectUri(str3);
            String registerNewRequest = Telemetry.registerNewRequest();
            APIEvent createApiEvent = createApiEvent(this.mContext, str2, registerNewRequest, "120");
            createApiEvent.setPromptBehavior(promptBehavior.toString());
            createApiEvent.setLoginHint(str4);
            AuthenticationRequest authenticationRequest = new AuthenticationRequest(this.mAuthority, str, str2, redirectUri, str4, promptBehavior, str5, getRequestCorrelationId(), getExtendedLifetimeEnabled(), str6);
            authenticationRequest.setUserIdentifierType(AuthenticationRequest.UserIdentifierType.LoginHint);
            authenticationRequest.setTelemetryRequestId(registerNewRequest);
            createAcquireTokenRequest(createApiEvent).acquireToken(null, false, authenticationRequest, authenticationCallback);
        }
    }

    @Deprecated
    public void acquireTokenByRefreshToken(String str, String str2, AuthenticationCallback<AuthenticationResult> authenticationCallback) {
        if (checkADFSValidationRequirements(null, authenticationCallback)) {
            if (StringExtensions.isNullOrBlank(str)) {
                throw new IllegalArgumentException("Refresh token is not provided");
            }
            if (StringExtensions.isNullOrBlank(str2)) {
                throw new IllegalArgumentException("ClientId is not provided");
            }
            if (authenticationCallback != null) {
                String registerNewRequest = Telemetry.registerNewRequest();
                APIEvent createApiEvent = createApiEvent(this.mContext, str2, registerNewRequest, "4");
                createApiEvent.setPromptBehavior(PromptBehavior.Auto.toString());
                createApiEvent.setIsDeprecated(true);
                AuthenticationRequest authenticationRequest = new AuthenticationRequest(this.mAuthority, null, str2, getRequestCorrelationId(), getExtendedLifetimeEnabled());
                authenticationRequest.setSilent(true);
                authenticationRequest.setTelemetryRequestId(registerNewRequest);
                createAcquireTokenRequest(createApiEvent).refreshTokenWithoutCache(str, authenticationRequest, authenticationCallback);
                return;
            }
            throw new IllegalArgumentException("Callback is not provided");
        }
    }

    @Deprecated
    public void acquireTokenByRefreshToken(String str, String str2, String str3, AuthenticationCallback<AuthenticationResult> authenticationCallback) {
        if (checkADFSValidationRequirements(null, authenticationCallback)) {
            if (StringExtensions.isNullOrBlank(str)) {
                throw new IllegalArgumentException("Refresh token is not provided");
            }
            if (StringExtensions.isNullOrBlank(str2)) {
                throw new IllegalArgumentException("ClientId is not provided");
            }
            if (authenticationCallback != null) {
                String registerNewRequest = Telemetry.registerNewRequest();
                APIEvent createApiEvent = createApiEvent(this.mContext, str2, registerNewRequest, "5");
                createApiEvent.setPromptBehavior(PromptBehavior.Auto.toString());
                createApiEvent.setIsDeprecated(true);
                AuthenticationRequest authenticationRequest = new AuthenticationRequest(this.mAuthority, str3, str2, getRequestCorrelationId(), getExtendedLifetimeEnabled());
                authenticationRequest.setTelemetryRequestId(registerNewRequest);
                authenticationRequest.setSilent(true);
                createAcquireTokenRequest(createApiEvent).refreshTokenWithoutCache(str, authenticationRequest, authenticationCallback);
                return;
            }
            throw new IllegalArgumentException("Callback is not provided");
        }
    }

    @Deprecated
    public Future<AuthenticationResult> acquireTokenSilent(String str, String str2, String str3, final AuthenticationCallback<AuthenticationResult> authenticationCallback) {
        final SettableFuture settableFuture = new SettableFuture();
        try {
            checkPreRequirements(str, str2);
            checkADFSValidationRequirements(null);
            String registerNewRequest = Telemetry.registerNewRequest();
            final APIEvent createApiEvent = createApiEvent(this.mContext, str2, registerNewRequest, ExifInterface.GPS_MEASUREMENT_2D);
            createApiEvent.setIsDeprecated(true);
            final AuthenticationRequest authenticationRequest = new AuthenticationRequest(this.mAuthority, str, str2, str3, getRequestCorrelationId(), getExtendedLifetimeEnabled());
            authenticationRequest.setSilent(true);
            authenticationRequest.setPrompt(PromptBehavior.Auto);
            authenticationRequest.setUserIdentifierType(AuthenticationRequest.UserIdentifierType.UniqueId);
            authenticationRequest.setTelemetryRequestId(registerNewRequest);
            createAcquireTokenRequest(createApiEvent).acquireToken(null, false, authenticationRequest, new AuthenticationCallback<AuthenticationResult>() { // from class: com.microsoft.aad.adal.AuthenticationContext.2
                @Override // com.microsoft.aad.adal.AuthenticationCallback
                public void onSuccess(AuthenticationResult authenticationResult) {
                    createApiEvent.setWasApiCallSuccessful(true, null);
                    createApiEvent.setCorrelationId(authenticationRequest.getCorrelationId().toString());
                    createApiEvent.setIdToken(authenticationResult.getIdToken());
                    createApiEvent.stopTelemetryAndFlush();
                    AuthenticationCallback authenticationCallback2 = authenticationCallback;
                    if (authenticationCallback2 != null) {
                        authenticationCallback2.onSuccess(authenticationResult);
                    }
                    settableFuture.set(authenticationResult);
                }

                @Override // com.microsoft.aad.adal.AuthenticationCallback
                public void onError(Exception exc) {
                    createApiEvent.setWasApiCallSuccessful(false, exc);
                    createApiEvent.setCorrelationId(authenticationRequest.getCorrelationId().toString());
                    createApiEvent.stopTelemetryAndFlush();
                    AuthenticationCallback authenticationCallback2 = authenticationCallback;
                    if (authenticationCallback2 != null) {
                        authenticationCallback2.onError(exc);
                    }
                    settableFuture.setException(exc);
                }
            });
            return settableFuture;
        } catch (AuthenticationException e) {
            if (authenticationCallback != null) {
                authenticationCallback.onError(e);
            }
            settableFuture.setException(e);
            return settableFuture;
        }
    }

    public void acquireTokenSilentAsync(String str, String str2, String str3, AuthenticationCallback<AuthenticationResult> authenticationCallback) {
        if (checkPreRequirements(str, str2, authenticationCallback) && checkADFSValidationRequirements(null, authenticationCallback)) {
            String registerNewRequest = Telemetry.registerNewRequest();
            APIEvent createApiEvent = createApiEvent(this.mContext, str2, registerNewRequest, "3");
            createApiEvent.setPromptBehavior(PromptBehavior.Auto.toString());
            AuthenticationRequest authenticationRequest = new AuthenticationRequest(this.mAuthority, str, str2, str3, getRequestCorrelationId(), getExtendedLifetimeEnabled());
            authenticationRequest.setSilent(true);
            authenticationRequest.setPrompt(PromptBehavior.Auto);
            authenticationRequest.setUserIdentifierType(AuthenticationRequest.UserIdentifierType.UniqueId);
            authenticationRequest.setTelemetryRequestId(registerNewRequest);
            createAcquireTokenRequest(createApiEvent).acquireToken(null, false, authenticationRequest, authenticationCallback);
        }
    }

    public AuthenticationResult acquireTokenSilentSync(String str, String str2, String str3) throws AuthenticationException, InterruptedException {
        checkPreRequirements(str, str2);
        checkADFSValidationRequirements(null);
        final AtomicReference atomicReference = new AtomicReference();
        final AtomicReference atomicReference2 = new AtomicReference();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        String registerNewRequest = Telemetry.registerNewRequest();
        APIEvent createApiEvent = createApiEvent(this.mContext, str2, registerNewRequest, "1");
        createApiEvent.setPromptBehavior(PromptBehavior.Auto.toString());
        AuthenticationRequest authenticationRequest = new AuthenticationRequest(this.mAuthority, str, str2, str3, getRequestCorrelationId(), getExtendedLifetimeEnabled());
        authenticationRequest.setSilent(true);
        authenticationRequest.setPrompt(PromptBehavior.Auto);
        authenticationRequest.setUserIdentifierType(AuthenticationRequest.UserIdentifierType.UniqueId);
        authenticationRequest.setTelemetryRequestId(registerNewRequest);
        Looper myLooper = Looper.myLooper();
        if (myLooper != null && myLooper == this.mContext.getMainLooper()) {
            Logger.m14611e("AuthenticationContext:acquireTokenSilentSync", "Sync network calls must not be invoked in main thread. This method will throw android.os.NetworkOnMainThreadException in next major release", new NetworkOnMainThreadException());
        }
        createAcquireTokenRequest(createApiEvent).acquireToken(null, false, authenticationRequest, new AuthenticationCallback<AuthenticationResult>() { // from class: com.microsoft.aad.adal.AuthenticationContext.3
            @Override // com.microsoft.aad.adal.AuthenticationCallback
            public void onSuccess(AuthenticationResult authenticationResult) {
                atomicReference.set(authenticationResult);
                countDownLatch.countDown();
            }

            @Override // com.microsoft.aad.adal.AuthenticationCallback
            public void onError(Exception exc) {
                atomicReference2.set(exc);
                countDownLatch.countDown();
            }
        });
        countDownLatch.await();
        Exception exc = (Exception) atomicReference2.get();
        if (exc == null) {
            return (AuthenticationResult) atomicReference.get();
        }
        if (exc instanceof AuthenticationException) {
            throw ((AuthenticationException) exc);
        }
        if (exc instanceof RuntimeException) {
            throw ((RuntimeException) exc);
        }
        if (exc.getCause() == null) {
            throw new AuthenticationException(ADALError.ERROR_SILENT_REQUEST, exc.getMessage(), exc);
        }
        if (exc.getCause() instanceof AuthenticationException) {
            throw ((AuthenticationException) exc.getCause());
        }
        if (exc.getCause() instanceof RuntimeException) {
            throw ((RuntimeException) exc.getCause());
        }
        throw new AuthenticationException(ADALError.ERROR_SILENT_REQUEST, exc.getCause().getMessage(), exc.getCause());
    }

    public boolean cancelAuthenticationActivity(int i) throws AuthenticationException {
        AuthenticationRequestState waitingRequest = getWaitingRequest(i);
        if (waitingRequest == null || waitingRequest.getDelegate() == null) {
            Logger.m14614v("AuthenticationContext:cancelAuthenticationActivity", "Current callback is empty. There is not any active authentication.");
            return true;
        }
        String format = waitingRequest.getRequest() != null ? String.format(" CorrelationId: %s", waitingRequest.getRequest().getCorrelationId().toString()) : "No correlation id associated with waiting request";
        Logger.m14614v("AuthenticationContext:cancelAuthenticationActivity", "Current callback is not empty. There is an active authentication Activity." + format);
        Intent intent = new Intent(AuthenticationConstants.Browser.ACTION_CANCEL);
        intent.putExtras(new Bundle());
        intent.putExtra(AuthenticationConstants.Browser.REQUEST_ID, i);
        boolean sendBroadcast = LocalBroadcastManager.getInstance(this.mContext).sendBroadcast(intent);
        if (sendBroadcast) {
            Logger.m14614v("AuthenticationContext:cancelAuthenticationActivity", "Cancel broadcast message was successful." + format);
            waitingRequest.setCancelled(true);
            waitingRequest.getDelegate().onError(new AuthenticationCancelError("Cancel broadcast message was successful."));
        } else {
            Logger.m14617w("AuthenticationContext:cancelAuthenticationActivity", "Cancel broadcast message was not successful." + format, "", ADALError.BROADCAST_CANCEL_NOT_SUCCESSFUL);
        }
        return sendBroadcast;
    }

    public void deserialize(String str) throws AuthenticationException {
        if (StringExtensions.isNullOrBlank(str)) {
            throw new IllegalArgumentException("serializedBlob");
        }
        if (this.mBrokerProxy.canSwitchToBroker(this.mAuthority) == BrokerProxy.SwitchToBroker.CANNOT_SWITCH_TO_BROKER) {
            TokenCacheItem deserialize = SSOStateSerializer.deserialize(str);
            getCache().setItem(CacheKey.createCacheKey(deserialize), deserialize);
            return;
        }
        throw new UsageAuthenticationException(ADALError.FAIL_TO_IMPORT, "Failed to import the serialized blob because broker is enabled.");
    }

    public String getAuthority() {
        return this.mAuthority;
    }

    public String getBrokerUser() {
        BrokerProxy brokerProxy = this.mBrokerProxy;
        if (brokerProxy != null) {
            return brokerProxy.getCurrentUser();
        }
        return null;
    }

    public UserInfo[] getBrokerUsers() throws OperationCanceledException, AuthenticatorException, IOException {
        BrokerProxy brokerProxy = this.mBrokerProxy;
        if (brokerProxy != null) {
            return brokerProxy.getBrokerUsers();
        }
        return null;
    }

    public ITokenCacheStore getCache() {
        return this.mTokenCacheStore;
    }

    public String getCorrelationInfoFromWaitingRequest(AuthenticationRequestState authenticationRequestState) {
        UUID requestCorrelationId = getRequestCorrelationId();
        if (authenticationRequestState.getRequest() != null) {
            requestCorrelationId = authenticationRequestState.getRequest().getCorrelationId();
        }
        return String.format(" CorrelationId: %s", requestCorrelationId.toString());
    }

    public boolean getExtendedLifetimeEnabled() {
        return this.mExtendedLifetimeEnabled;
    }

    public boolean getIsAuthorityValidated() {
        return this.mIsAuthorityValidated;
    }

    public String getRedirectUriForBroker() {
        PackageHelper packageHelper = new PackageHelper(this.mContext);
        String packageName = this.mContext.getPackageName();
        String currentSignatureForPackage = packageHelper.getCurrentSignatureForPackage(packageName);
        String brokerRedirectUrl = PackageHelper.getBrokerRedirectUrl(packageName, currentSignatureForPackage);
        Logger.m14615v("AuthenticationContext:getRedirectUriForBroker", "Get expected redirect Uri. ", "Broker redirectUri:" + brokerRedirectUrl + " packagename:" + packageName + " signatureDigest:" + currentSignatureForPackage, null);
        return brokerRedirectUrl;
    }

    public UUID getRequestCorrelationId() {
        UUID uuid = this.mRequestCorrelationId;
        return uuid == null ? UUID.randomUUID() : uuid;
    }

    public boolean getValidateAuthority() {
        return this.mValidateAuthority;
    }

    public AuthenticationRequestState getWaitingRequest(int i) throws AuthenticationException {
        AuthenticationRequestState authenticationRequestState;
        Logger.m14614v("AuthenticationContext:getWaitingRequest", "Get waiting request. requestId:" + i);
        SparseArray<AuthenticationRequestState> sparseArray = DELEGATE_MAP;
        synchronized (sparseArray) {
            authenticationRequestState = sparseArray.get(i);
        }
        if (authenticationRequestState != null) {
            return authenticationRequestState;
        }
        Logger.m14609e("AuthenticationContext:getWaitingRequest", "Request callback is not available. requestId:" + i, null, ADALError.CALLBACK_IS_NOT_FOUND);
        ADALError aDALError = ADALError.CALLBACK_IS_NOT_FOUND;
        throw new AuthenticationException(aDALError, "Request callback is not available for requestId:" + i);
    }

    public void onActivityResult(int i, int i2, Intent intent) {
        AuthenticationRequestState authenticationRequestState;
        if (i != 1001) {
            return;
        }
        if (intent == null) {
            Logger.m14609e("AuthenticationContext:onActivityResult", "onActivityResult BROWSER_FLOW data is null.", "", ADALError.ON_ACTIVITY_RESULT_INTENT_NULL);
            return;
        }
        int i3 = intent.getExtras().getInt(AuthenticationConstants.Browser.REQUEST_ID);
        SparseArray<AuthenticationRequestState> sparseArray = DELEGATE_MAP;
        synchronized (sparseArray) {
            authenticationRequestState = sparseArray.get(i3);
        }
        if (authenticationRequestState != null) {
            new AcquireTokenRequest(this.mContext, this, authenticationRequestState.getAPIEvent()).onActivityResult(i, i2, intent);
            return;
        }
        Logger.m14609e("AuthenticationContext:onActivityResult", "onActivityResult did not find the waiting request. requestId:" + i3, null, ADALError.ON_ACTIVITY_RESULT_INTENT_NULL);
    }

    public void putWaitingRequest(int i, AuthenticationRequestState authenticationRequestState) {
        if (authenticationRequestState != null) {
            Logger.m14614v(TAG, "Put waiting request. requestId:" + i + " " + getCorrelationInfoFromWaitingRequest(authenticationRequestState));
            SparseArray<AuthenticationRequestState> sparseArray = DELEGATE_MAP;
            synchronized (sparseArray) {
                sparseArray.put(i, authenticationRequestState);
            }
        }
    }

    public void removeWaitingRequest(int i) {
        Logger.m14614v(TAG, "Remove waiting request. requestId:" + i);
        SparseArray<AuthenticationRequestState> sparseArray = DELEGATE_MAP;
        synchronized (sparseArray) {
            sparseArray.remove(i);
        }
    }

    public String serialize(String str) throws AuthenticationException {
        if (StringExtensions.isNullOrBlank(str)) {
            throw new IllegalArgumentException("uniqueUserId");
        }
        if (this.mBrokerProxy.canSwitchToBroker(this.mAuthority) == BrokerProxy.SwitchToBroker.CANNOT_SWITCH_TO_BROKER) {
            try {
                TokenCacheItem fRTItem = new TokenCacheAccessor(this.mTokenCacheStore, getAuthority(), Telemetry.registerNewRequest()).getFRTItem("1", str);
                if (fRTItem == null) {
                    Logger.m14612i("AuthenticationContext:serialize", "Cannot find the family token cache item for this userID", "");
                    throw new UsageAuthenticationException(ADALError.FAIL_TO_EXPORT, "Failed to export the FID because no family token cache item is found.");
                } else if (!StringExtensions.isNullOrBlank(fRTItem.getFamilyClientId())) {
                    return SSOStateSerializer.serialize(fRTItem);
                } else {
                    throw new UsageAuthenticationException(ADALError.FAIL_TO_EXPORT, "tokenItem does not contain family refresh token");
                }
            } catch (MalformedURLException e) {
                throw new AuthenticationException(ADALError.DEVELOPER_AUTHORITY_IS_NOT_VALID_URL, e.getMessage(), e);
            }
        }
        throw new UsageAuthenticationException(ADALError.FAIL_TO_EXPORT, "Failed to export the family refresh token cache item because broker is enabled.");
    }

    public void setExtendedLifetimeEnabled(boolean z) {
        this.mExtendedLifetimeEnabled = z;
    }

    public void setIsAuthorityValidated(boolean z) {
        this.mIsAuthorityValidated = z;
    }

    public void setRequestCorrelationId(UUID uuid) {
        this.mRequestCorrelationId = uuid;
        Logger.setCorrelationId(uuid);
    }
}
