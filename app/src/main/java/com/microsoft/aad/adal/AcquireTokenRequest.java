package com.microsoft.aad.adal;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Process;
import android.util.Log;
import com.microsoft.aad.adal.AuthenticationConstants;
import com.microsoft.aad.adal.BrokerProxy;
import com.microsoft.aad.adal.TelemetryUtils;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes3.dex */
public class AcquireTokenRequest {
    public static final String TAG = "AcquireTokenRequest";
    private static final ExecutorService THREAD_EXECUTOR = Executors.newSingleThreadExecutor();
    private static Handler sHandler = null;
    public APIEvent mAPIEvent;
    private final AuthenticationContext mAuthContext;
    private final IBrokerProxy mBrokerProxy;
    public final Context mContext;
    private Discovery mDiscovery;
    public TokenCacheAccessor mTokenCacheAccessor;

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes3.dex */
    public static class CallbackHandler {
        public AuthenticationCallback<AuthenticationResult> mCallback;
        private Handler mRefHandler;

        public CallbackHandler(Handler handler, AuthenticationCallback<AuthenticationResult> authenticationCallback) {
            this.mRefHandler = handler;
            this.mCallback = authenticationCallback;
        }

        public AuthenticationCallback<AuthenticationResult> getCallback() {
            return this.mCallback;
        }

        public void onError(final AuthenticationException authenticationException) {
            AuthenticationCallback<AuthenticationResult> authenticationCallback = this.mCallback;
            if (authenticationCallback != null) {
                Handler handler = this.mRefHandler;
                if (handler != null) {
                    handler.post(new Runnable() { // from class: com.microsoft.aad.adal.AcquireTokenRequest.CallbackHandler.1
                        @Override // java.lang.Runnable
                        public void run() {
                            CallbackHandler.this.mCallback.onError(authenticationException);
                        }
                    });
                } else {
                    authenticationCallback.onError(authenticationException);
                }
            }
        }

        public void onSuccess(final AuthenticationResult authenticationResult) {
            AuthenticationCallback<AuthenticationResult> authenticationCallback = this.mCallback;
            if (authenticationCallback != null) {
                Handler handler = this.mRefHandler;
                if (handler != null) {
                    handler.post(new Runnable() { // from class: com.microsoft.aad.adal.AcquireTokenRequest.CallbackHandler.2
                        @Override // java.lang.Runnable
                        public void run() {
                            CallbackHandler.this.mCallback.onSuccess(authenticationResult);
                        }
                    });
                } else {
                    authenticationCallback.onSuccess(authenticationResult);
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public AcquireTokenRequest(Context context, AuthenticationContext authenticationContext, APIEvent aPIEvent) {
        this.mContext = context;
        this.mAuthContext = authenticationContext;
        this.mDiscovery = new Discovery(context);
        if (authenticationContext.getCache() != null && aPIEvent != null) {
            this.mTokenCacheAccessor = new TokenCacheAccessor(authenticationContext.getCache(), authenticationContext.getAuthority(), aPIEvent.getTelemetryRequestId());
        }
        this.mBrokerProxy = new BrokerProxy(context);
        this.mAPIEvent = aPIEvent;
    }

    private void acquireTokenInteractiveFlow(CallbackHandler callbackHandler, IWindowComponent iWindowComponent, boolean z, AuthenticationRequest authenticationRequest) throws AuthenticationException {
        if (iWindowComponent != null || z) {
            HttpWebRequest.throwIfNetworkNotAvailable(this.mContext);
            int hashCode = callbackHandler.getCallback().hashCode();
            authenticationRequest.setRequestId(hashCode);
            this.mAuthContext.putWaitingRequest(hashCode, new AuthenticationRequestState(hashCode, authenticationRequest, callbackHandler.getCallback(), this.mAPIEvent));
            BrokerProxy.SwitchToBroker canSwitchToBroker = this.mBrokerProxy.canSwitchToBroker(authenticationRequest.getAuthority());
            if (canSwitchToBroker == BrokerProxy.SwitchToBroker.CANNOT_SWITCH_TO_BROKER || !this.mBrokerProxy.verifyUser(authenticationRequest.getLoginHint(), authenticationRequest.getUserId())) {
                Logger.m14615v("AcquireTokenRequest:acquireTokenInteractiveFlow", "Starting Authentication Activity for embedded flow. ", " Callback is: " + callbackHandler.getCallback().hashCode(), null);
                new AcquireTokenInteractiveRequest(this.mContext, authenticationRequest, this.mTokenCacheAccessor).acquireToken(iWindowComponent, z ? new AuthenticationDialog(getHandler(), this.mContext, this, authenticationRequest) : null);
                return;
            } else if (canSwitchToBroker != BrokerProxy.SwitchToBroker.NEED_PERMISSIONS_TO_SWITCH_TO_BROKER) {
                Logger.m14615v("AcquireTokenRequest:acquireTokenInteractiveFlow", "Launch activity for interactive authentication via broker with callback. ", "" + callbackHandler.getCallback().hashCode(), null);
                new AcquireTokenWithBrokerRequest(authenticationRequest, this.mBrokerProxy).acquireTokenWithBrokerInteractively(iWindowComponent);
                return;
            } else {
                throw new UsageAuthenticationException(ADALError.DEVELOPER_BROKER_PERMISSIONS_MISSING, "Broker related permissions are missing for GET_ACCOUNTS");
            }
        }
        ADALError aDALError = ADALError.AUTH_REFRESH_FAILED_PROMPT_NOT_ALLOWED;
        throw new AuthenticationException(aDALError, authenticationRequest.getLogInfo() + " Cannot launch webview, acitivity is null.");
    }

    private AuthenticationResult acquireTokenSilentFlow(AuthenticationRequest authenticationRequest) throws AuthenticationException {
        BrokerProxy.SwitchToBroker canSwitchToBroker;
        AuthenticationResult tryAcquireTokenSilentLocally = tryAcquireTokenSilentLocally(authenticationRequest);
        if (isAccessTokenReturned(tryAcquireTokenSilentLocally) || (canSwitchToBroker = this.mBrokerProxy.canSwitchToBroker(authenticationRequest.getAuthority())) == BrokerProxy.SwitchToBroker.CANNOT_SWITCH_TO_BROKER || !this.mBrokerProxy.verifyUser(authenticationRequest.getLoginHint(), authenticationRequest.getUserId())) {
            return tryAcquireTokenSilentLocally;
        }
        if (canSwitchToBroker != BrokerProxy.SwitchToBroker.NEED_PERMISSIONS_TO_SWITCH_TO_BROKER) {
            Logger.m14608d("AcquireTokenRequest:acquireTokenSilentFlow", "Cannot get AT from local cache, switch to Broker for auth, clear tokens from local cache for the user.");
            removeTokensForUser(authenticationRequest);
            return tryAcquireTokenSilentWithBroker(authenticationRequest);
        }
        throw new UsageAuthenticationException(ADALError.DEVELOPER_BROKER_PERMISSIONS_MISSING, "Broker related permissions are missing for GET_ACCOUNTS");
    }

    private Handler getHandler() {
        Handler handler;
        synchronized (this) {
            if (sHandler == null) {
                HandlerThread handlerThread = new HandlerThread("AcquireTokenRequestHandlerThread");
                handlerThread.start();
                sHandler = new Handler(handlerThread.getLooper());
            }
            handler = sHandler;
        }
        return handler;
    }

    private boolean isAccessTokenReturned(AuthenticationResult authenticationResult) {
        return (authenticationResult == null || StringExtensions.isNullOrBlank(authenticationResult.getAccessToken())) ? false : true;
    }

    public void performAcquireTokenRequest(CallbackHandler callbackHandler, IWindowComponent iWindowComponent, boolean z, AuthenticationRequest authenticationRequest) throws AuthenticationException {
        AuthenticationResult tryAcquireTokenSilent = tryAcquireTokenSilent(authenticationRequest);
        if (isAccessTokenReturned(tryAcquireTokenSilent)) {
            this.mAPIEvent.setWasApiCallSuccessful(true, null);
            this.mAPIEvent.setCorrelationId(authenticationRequest.getCorrelationId().toString());
            this.mAPIEvent.setIdToken(tryAcquireTokenSilent.getIdToken());
            this.mAPIEvent.stopTelemetryAndFlush();
            callbackHandler.onSuccess(tryAcquireTokenSilent);
            return;
        }
        Logger.m14608d("AcquireTokenRequest:performAcquireTokenRequest", "Trying to acquire token interactively.");
        acquireTokenInteractiveFlow(callbackHandler, iWindowComponent, z, authenticationRequest);
    }

    private void performAuthorityValidation(AuthenticationRequest authenticationRequest, URL url) throws AuthenticationException {
        Telemetry.getInstance().startEvent(authenticationRequest.getTelemetryRequestId(), "Microsoft.ADAL.authority_validation");
        APIEvent aPIEvent = new APIEvent("Microsoft.ADAL.authority_validation");
        aPIEvent.setCorrelationId(authenticationRequest.getCorrelationId().toString());
        aPIEvent.setRequestId(authenticationRequest.getTelemetryRequestId());
        if (this.mAuthContext.getValidateAuthority()) {
            try {
                validateAuthority(url, authenticationRequest.getUpnSuffix(), authenticationRequest.isSilent(), authenticationRequest.getCorrelationId());
                aPIEvent.setValidationStatus("yes");
            } catch (AuthenticationException e) {
                if (e.getCode() == null || (!e.getCode().equals(ADALError.DEVICE_CONNECTION_IS_NOT_AVAILABLE) && !e.getCode().equals(ADALError.NO_NETWORK_CONNECTION_POWER_OPTIMIZATION))) {
                    aPIEvent.setValidationStatus("no");
                } else {
                    aPIEvent.setValidationStatus("not_done");
                }
                throw e;
            } catch (Throwable th) {
                Telemetry.getInstance().stopEvent(authenticationRequest.getTelemetryRequestId(), aPIEvent, "Microsoft.ADAL.authority_validation");
                throw th;
            }
        } else {
            if (!UrlExtensions.isADFSAuthority(url) && !AuthorityValidationMetadataCache.containsAuthorityHost(url)) {
                try {
                    this.mDiscovery.validateAuthority(url);
                } catch (AuthenticationException unused) {
                    AuthorityValidationMetadataCache.updateInstanceDiscoveryMap(url.getHost(), new InstanceDiscoveryMetadata(false));
                    Logger.m14614v("AcquireTokenRequest:performAuthorityValidation", "Fail to get authority validation metadata back. Ignore the failure since authority validation is turned off.");
                }
            }
            aPIEvent.setValidationStatus("not_done");
        }
        Telemetry.getInstance().stopEvent(authenticationRequest.getTelemetryRequestId(), aPIEvent, "Microsoft.ADAL.authority_validation");
        InstanceDiscoveryMetadata cachedInstanceDiscoveryMetadata = AuthorityValidationMetadataCache.getCachedInstanceDiscoveryMetadata(url);
        if (cachedInstanceDiscoveryMetadata == null || !cachedInstanceDiscoveryMetadata.isValidated()) {
            return;
        }
        updatePreferredNetworkLocation(url, authenticationRequest, cachedInstanceDiscoveryMetadata);
    }

    private void removeTokensForUser(AuthenticationRequest authenticationRequest) throws AuthenticationException {
        if (this.mTokenCacheAccessor != null) {
            String userId = !StringExtensions.isNullOrBlank(authenticationRequest.getUserId()) ? authenticationRequest.getUserId() : authenticationRequest.getLoginHint();
            try {
                TokenCacheItem fRTItem = this.mTokenCacheAccessor.getFRTItem("1", userId);
                if (fRTItem != null) {
                    this.mTokenCacheAccessor.removeTokenCacheItem(fRTItem, authenticationRequest.getResource());
                }
                try {
                    TokenCacheItem mRRTItem = this.mTokenCacheAccessor.getMRRTItem(authenticationRequest.getClientId(), userId);
                    TokenCacheItem regularRefreshTokenCacheItem = this.mTokenCacheAccessor.getRegularRefreshTokenCacheItem(authenticationRequest.getResource(), authenticationRequest.getClientId(), userId);
                    if (mRRTItem != null) {
                        this.mTokenCacheAccessor.removeTokenCacheItem(mRRTItem, authenticationRequest.getResource());
                    } else if (regularRefreshTokenCacheItem != null) {
                        this.mTokenCacheAccessor.removeTokenCacheItem(regularRefreshTokenCacheItem, authenticationRequest.getResource());
                    } else {
                        Logger.m14614v("AcquireTokenRequest:removeTokensForUser", "No token items need to be deleted for the user.");
                    }
                } catch (MalformedURLException e) {
                    throw new AuthenticationException(ADALError.DEVELOPER_AUTHORITY_IS_NOT_VALID_URL, e.getMessage(), e);
                }
            } catch (MalformedURLException e2) {
                throw new AuthenticationException(ADALError.DEVELOPER_AUTHORITY_IS_NOT_VALID_URL, e2.getMessage(), e2);
            }
        }
    }

    private boolean shouldTrySilentFlow(AuthenticationRequest authenticationRequest) {
        return (!Utility.isClaimsChallengePresent(authenticationRequest) && authenticationRequest.getPrompt() == PromptBehavior.Auto) || authenticationRequest.isSilent();
    }

    private AuthenticationResult tryAcquireTokenSilent(AuthenticationRequest authenticationRequest) throws AuthenticationException {
        String str;
        if (shouldTrySilentFlow(authenticationRequest)) {
            Logger.m14614v("AcquireTokenRequest:tryAcquireTokenSilent", "Try to acquire token silently, return valid AT or use RT in the cache.");
            AuthenticationResult acquireTokenSilentFlow = acquireTokenSilentFlow(authenticationRequest);
            boolean isAccessTokenReturned = isAccessTokenReturned(acquireTokenSilentFlow);
            if (isAccessTokenReturned || !authenticationRequest.isSilent()) {
                if (isAccessTokenReturned) {
                    Logger.m14614v("AcquireTokenRequest:tryAcquireTokenSilent", "Token is successfully returned from silent flow. ");
                    return acquireTokenSilentFlow;
                }
                return acquireTokenSilentFlow;
            }
            if (acquireTokenSilentFlow == null) {
                str = "No result returned from acquireTokenSilent";
            } else {
                str = " ErrorCode:" + acquireTokenSilentFlow.getErrorCode();
            }
            Logger.m14609e("AcquireTokenRequest:tryAcquireTokenSilent", "Prompt is not allowed and failed to get token. " + str, authenticationRequest.getLogInfo(), ADALError.AUTH_REFRESH_FAILED_PROMPT_NOT_ALLOWED);
            AuthenticationException authenticationException = new AuthenticationException(ADALError.AUTH_REFRESH_FAILED_PROMPT_NOT_ALLOWED, authenticationRequest.getLogInfo() + " " + str);
            authenticationException.setHttpResponse(acquireTokenSilentFlow);
            throw authenticationException;
        }
        return null;
    }

    private AuthenticationResult tryAcquireTokenSilentLocally(AuthenticationRequest authenticationRequest) throws AuthenticationException {
        Logger.m14614v("AcquireTokenRequest:tryAcquireTokenSilentLocally", "Try to silently get token from local cache.");
        return new AcquireTokenSilentHandler(this.mContext, authenticationRequest, this.mTokenCacheAccessor).getAccessToken();
    }

    private AuthenticationResult tryAcquireTokenSilentWithBroker(AuthenticationRequest authenticationRequest) throws AuthenticationException {
        return new AcquireTokenWithBrokerRequest(authenticationRequest, this.mBrokerProxy).acquireTokenWithBrokerSilent();
    }

    private void updatePreferredNetworkLocation(URL url, AuthenticationRequest authenticationRequest, InstanceDiscoveryMetadata instanceDiscoveryMetadata) throws AuthenticationException {
        if (instanceDiscoveryMetadata == null || !instanceDiscoveryMetadata.isValidated() || instanceDiscoveryMetadata.getPreferredNetwork() == null || url.getHost().equalsIgnoreCase(instanceDiscoveryMetadata.getPreferredNetwork())) {
            return;
        }
        try {
            authenticationRequest.setAuthority(Utility.constructAuthorityUrl(url, instanceDiscoveryMetadata.getPreferredNetwork()).toString());
        } catch (MalformedURLException unused) {
            Logger.m14612i(TAG, "preferred network is invalid", "use exactly the same authority url that is passed");
        }
    }

    public void validateAcquireTokenRequest(AuthenticationRequest authenticationRequest) throws AuthenticationException {
        URL url = StringExtensions.getUrl(authenticationRequest.getAuthority());
        if (url != null) {
            performAuthorityValidation(authenticationRequest, url);
            BrokerProxy.SwitchToBroker canSwitchToBroker = this.mBrokerProxy.canSwitchToBroker(authenticationRequest.getAuthority());
            if (canSwitchToBroker == BrokerProxy.SwitchToBroker.CANNOT_SWITCH_TO_BROKER || !this.mBrokerProxy.verifyUser(authenticationRequest.getLoginHint(), authenticationRequest.getUserId()) || authenticationRequest.isSilent()) {
                return;
            }
            if (canSwitchToBroker != BrokerProxy.SwitchToBroker.NEED_PERMISSIONS_TO_SWITCH_TO_BROKER) {
                verifyBrokerRedirectUri(authenticationRequest);
                return;
            }
            throw new UsageAuthenticationException(ADALError.DEVELOPER_BROKER_PERMISSIONS_MISSING, "Broker related permissions are missing for GET_ACCOUNTS.");
        }
        throw new AuthenticationException(ADALError.DEVELOPER_AUTHORITY_IS_NOT_VALID_URL);
    }

    private void validateAuthority(URL url, String str, boolean z, UUID uuid) throws AuthenticationException {
        boolean isADFSAuthority = UrlExtensions.isADFSAuthority(url);
        if (AuthorityValidationMetadataCache.isAuthorityValidated(url)) {
            return;
        }
        if (isADFSAuthority && this.mAuthContext.getIsAuthorityValidated()) {
            return;
        }
        Logger.m14614v("AcquireTokenRequest:validateAuthority", "Start validating authority");
        this.mDiscovery.setCorrelationId(uuid);
        Discovery.verifyAuthorityValidInstance(url);
        if (z || !isADFSAuthority || str == null) {
            if (z && UrlExtensions.isADFSAuthority(url)) {
                Logger.m14614v("AcquireTokenRequest:validateAuthority", "Silent request. Skipping AD FS authority validation");
            }
            this.mDiscovery.validateAuthority(url);
        } else {
            this.mDiscovery.validateAuthorityADFS(url, str);
        }
        Logger.m14614v("AcquireTokenRequest:validateAuthority", "The passed in authority is valid.");
        this.mAuthContext.setIsAuthorityValidated(true);
    }

    private void verifyBrokerRedirectUri(AuthenticationRequest authenticationRequest) throws UsageAuthenticationException {
        String redirectUri = authenticationRequest.getRedirectUri();
        String redirectUriForBroker = this.mAuthContext.getRedirectUriForBroker();
        if (StringExtensions.isNullOrBlank(redirectUri)) {
            Logger.m14609e("AcquireTokenRequest:verifyBrokerRedirectUri", "The redirectUri is null or blank. ", "The redirect uri is expected to be:" + redirectUriForBroker, ADALError.DEVELOPER_REDIRECTURI_INVALID);
            throw new UsageAuthenticationException(ADALError.DEVELOPER_REDIRECTURI_INVALID, "The redirectUri is null or blank.");
        } else if (redirectUri.startsWith(AuthenticationConstants.Broker.BROWSER_EXT_INSTALL_PREFIX)) {
            PackageHelper packageHelper = new PackageHelper(this.mContext);
            try {
                String encode = URLEncoder.encode(this.mContext.getPackageName(), "UTF_8");
                String encode2 = URLEncoder.encode(packageHelper.getCurrentSignatureForPackage(this.mContext.getPackageName()), "UTF_8");
                if (!redirectUri.startsWith(AuthenticationConstants.Broker.BROWSER_EXT_INSTALL_PREFIX + encode + "/")) {
                    Logger.m14609e("AcquireTokenRequest:verifyBrokerRedirectUri", "The base64 url encoded package name component of the redirect uri does not match the expected value. ", "This apps package name is: " + encode + " so the redirect uri is expected to be: " + redirectUriForBroker, ADALError.DEVELOPER_REDIRECTURI_INVALID);
                    throw new UsageAuthenticationException(ADALError.DEVELOPER_REDIRECTURI_INVALID, "The base64 url encoded package name component of the redirect uri does not match the expected value. ");
                } else if (redirectUri.equalsIgnoreCase(redirectUriForBroker)) {
                    Logger.m14614v("AcquireTokenRequest:verifyBrokerRedirectUri", "The broker redirect URI is valid.");
                } else {
                    Logger.m14609e("AcquireTokenRequest:verifyBrokerRedirectUri", "The base64 url encoded signature component of the redirect uri does not match the expected value. ", "This apps signature is: " + encode2 + " so the redirect uri is expected to be: " + redirectUriForBroker, ADALError.DEVELOPER_REDIRECTURI_INVALID);
                    throw new UsageAuthenticationException(ADALError.DEVELOPER_REDIRECTURI_INVALID, "The base64 url encoded signature component of the redirect uri does not match the expected value.");
                }
            } catch (UnsupportedEncodingException e) {
                Logger.m14610e("AcquireTokenRequest:verifyBrokerRedirectUri", ADALError.ENCODING_IS_NOT_SUPPORTED.getDescription(), e.getMessage(), ADALError.ENCODING_IS_NOT_SUPPORTED, e);
                throw new UsageAuthenticationException(ADALError.ENCODING_IS_NOT_SUPPORTED, "The verifying BrokerRedirectUri process failed because the base64 url encoding is not supported.", e);
            }
        } else {
            Logger.m14609e("AcquireTokenRequest:verifyBrokerRedirectUri", "The prefix of the redirect uri does not match the expected value. ", " The valid broker redirect URI prefix: msauth so the redirect uri is expected to be: " + redirectUriForBroker, ADALError.DEVELOPER_REDIRECTURI_INVALID);
            throw new UsageAuthenticationException(ADALError.DEVELOPER_REDIRECTURI_INVALID, "The prefix of the redirect uri does not match the expected value.");
        }
    }

    public void waitingRequestOnError(CallbackHandler callbackHandler, AuthenticationRequestState authenticationRequestState, int i, AuthenticationException authenticationException) {
        if (authenticationRequestState != null) {
            try {
                if (authenticationRequestState.getDelegate() != null) {
                    Logger.m14614v("AcquireTokenRequest:waitingRequestOnError", "Sending error to callback" + this.mAuthContext.getCorrelationInfoFromWaitingRequest(authenticationRequestState));
                    authenticationRequestState.getAPIEvent().setWasApiCallSuccessful(false, authenticationException);
                    authenticationRequestState.getAPIEvent().setCorrelationId(authenticationRequestState.getRequest().getCorrelationId().toString());
                    authenticationRequestState.getAPIEvent().stopTelemetryAndFlush();
                    if (callbackHandler != null) {
                        callbackHandler.onError(authenticationException);
                    } else {
                        authenticationRequestState.getDelegate().onError(authenticationException);
                    }
                }
            } finally {
                if (authenticationException != null) {
                    this.mAuthContext.removeWaitingRequest(i);
                }
            }
        }
    }

    private void waitingRequestOnError(AuthenticationRequestState authenticationRequestState, int i, AuthenticationException authenticationException) {
        waitingRequestOnError(null, authenticationRequestState, i, authenticationException);
    }

    public void acquireToken(final IWindowComponent iWindowComponent, final boolean z, final AuthenticationRequest authenticationRequest, AuthenticationCallback<AuthenticationResult> authenticationCallback) {
        final CallbackHandler callbackHandler = new CallbackHandler(getHandler(), authenticationCallback);
        Logger.setCorrelationId(authenticationRequest.getCorrelationId());
        Logger.m14614v("AcquireTokenRequest:acquireToken", "Sending async task from thread:" + Process.myTid());
        THREAD_EXECUTOR.execute(new Runnable() { // from class: com.microsoft.aad.adal.AcquireTokenRequest.1
            @Override // java.lang.Runnable
            public void run() {
                Logger.m14614v("AcquireTokenRequest:acquireToken", "Running task in thread:" + Process.myTid());
                try {
                    AcquireTokenRequest.this.validateAcquireTokenRequest(authenticationRequest);
                    AcquireTokenRequest.this.performAcquireTokenRequest(callbackHandler, iWindowComponent, z, authenticationRequest);
                } catch (AuthenticationException e) {
                    AcquireTokenRequest.this.mAPIEvent.setWasApiCallSuccessful(false, e);
                    AcquireTokenRequest.this.mAPIEvent.setCorrelationId(authenticationRequest.getCorrelationId().toString());
                    AcquireTokenRequest.this.mAPIEvent.stopTelemetryAndFlush();
                    callbackHandler.onError(e);
                }
            }
        });
    }

    public void onActivityResult(int i, final int i2, Intent intent) {
        AuthenticationException authenticationException;
        if (i == 1001) {
            getHandler();
            if (intent == null) {
                Logger.m14609e("AcquireTokenRequest:onActivityResult", "BROWSER_FLOW data is null.", "", ADALError.ON_ACTIVITY_RESULT_INTENT_NULL);
                return;
            }
            Bundle extras = intent.getExtras();
            int i3 = extras.getInt(AuthenticationConstants.Browser.REQUEST_ID);
            try {
                final AuthenticationRequestState waitingRequest = this.mAuthContext.getWaitingRequest(i3);
                Logger.m14614v("AcquireTokenRequest:onActivityResult", "Waiting request found. RequestId:" + i3);
                String correlationInfoFromWaitingRequest = this.mAuthContext.getCorrelationInfoFromWaitingRequest(waitingRequest);
                if (i2 == 2004) {
                    String stringExtra = intent.getStringExtra(AuthenticationConstants.Broker.ACCOUNT_ACCESS_TOKEN);
                    this.mBrokerProxy.saveAccount(intent.getStringExtra(AuthenticationConstants.Broker.ACCOUNT_NAME));
                    Date date = new Date(intent.getLongExtra(AuthenticationConstants.Broker.ACCOUNT_EXPIREDATE, 0L));
                    String stringExtra2 = intent.getStringExtra(AuthenticationConstants.Broker.ACCOUNT_IDTOKEN);
                    String stringExtra3 = intent.getStringExtra(AuthenticationConstants.Broker.ACCOUNT_USERINFO_TENANTID);
                    UserInfo userInfoFromBrokerResult = UserInfo.getUserInfoFromBrokerResult(intent.getExtras());
                    String stringExtra4 = intent.getStringExtra(AuthenticationConstants.Broker.CliTelemInfo.SERVER_ERROR);
                    String stringExtra5 = intent.getStringExtra(AuthenticationConstants.Broker.CliTelemInfo.SERVER_SUBERROR);
                    String stringExtra6 = intent.getStringExtra(AuthenticationConstants.Broker.CliTelemInfo.RT_AGE);
                    String stringExtra7 = intent.getStringExtra(AuthenticationConstants.Broker.CliTelemInfo.SPE_RING);
                    String str = null;
                    Date date2 = null;
                    AuthenticationResult authenticationResult = new AuthenticationResult(stringExtra, null, date, false, userInfoFromBrokerResult, stringExtra3, stringExtra2, null);
                    authenticationResult.setAuthority(intent.getStringExtra(AuthenticationConstants.Broker.ACCOUNT_AUTHORITY));
                    TelemetryUtils.CliTelemInfo cliTelemInfo = new TelemetryUtils.CliTelemInfo();
                    cliTelemInfo.setServerErrorCode(stringExtra4);
                    cliTelemInfo.setServerSubErrorCode(stringExtra5);
                    cliTelemInfo.setRefreshTokenAge(stringExtra6);
                    cliTelemInfo.setSpeRing(stringExtra7);
                    authenticationResult.setCliTelemInfo(cliTelemInfo);
                    if (authenticationResult.getAccessToken() != null) {
                        Exception exc = null;
                        waitingRequest.getAPIEvent().setWasApiCallSuccessful(true, null);
                        waitingRequest.getAPIEvent().setCorrelationId(waitingRequest.getRequest().getCorrelationId().toString());
                        waitingRequest.getAPIEvent().setIdToken(authenticationResult.getIdToken());
                        waitingRequest.getAPIEvent().setServerErrorCode(cliTelemInfo.getServerErrorCode());
                        waitingRequest.getAPIEvent().setServerSubErrorCode(cliTelemInfo.getServerSubErrorCode());
                        waitingRequest.getAPIEvent().setRefreshTokenAge(cliTelemInfo.getRefreshTokenAge());
                        waitingRequest.getAPIEvent().setSpeRing(cliTelemInfo.getSpeRing());
                        waitingRequest.getAPIEvent().stopTelemetryAndFlush();
                        waitingRequest.getDelegate().onSuccess(authenticationResult);
                    }
                } else if (i2 == 2001) {
                    Logger.m14614v("AcquireTokenRequest:onActivityResult", "User cancelled the flow. RequestId:" + i3 + " " + correlationInfoFromWaitingRequest);
                    StringBuilder sb = new StringBuilder("User cancelled the flow RequestId:");
                    sb.append(i3);
                    sb.append(correlationInfoFromWaitingRequest);
                    waitingRequestOnError(waitingRequest, i3, new AuthenticationCancelError(sb.toString()));
                } else {
                    if (i2 == 2006) {
                        Logger.m14614v("AcquireTokenRequest:onActivityResult", "Device needs to have broker installed, we expect the apps to call usback when the broker is installed");
                        authenticationException = new AuthenticationException(ADALError.BROKER_APP_INSTALLATION_STARTED);
                    } else if (i2 == 2005) {
                        Serializable serializable = extras.getSerializable(AuthenticationConstants.Browser.RESPONSE_AUTHENTICATION_EXCEPTION);
                        if (serializable != null && (serializable instanceof AuthenticationException)) {
                            authenticationException = (AuthenticationException) serializable;
                            Logger.m14617w("AcquireTokenRequest:onActivityResult", "Webview returned exception.", authenticationException.getMessage(), ADALError.WEBVIEW_RETURNED_AUTHENTICATION_EXCEPTION);
                        }
                        authenticationException = new AuthenticationException(ADALError.WEBVIEW_RETURNED_INVALID_AUTHENTICATION_EXCEPTION, correlationInfoFromWaitingRequest);
                    } else if (i2 == 2002) {
                        String string = extras.getString(AuthenticationConstants.Browser.RESPONSE_ERROR_CODE);
                        String string2 = extras.getString(AuthenticationConstants.Browser.RESPONSE_ERROR_MESSAGE);
                        ADALError aDALError = null;
                        Logger.m14615v("AcquireTokenRequest:onActivityResult", "Error info:" + string + " for requestId: " + i3 + " " + correlationInfoFromWaitingRequest, string2, null);
                        authenticationException = new AuthenticationException(ADALError.SERVER_INVALID_REQUEST, string + " " + string2 + correlationInfoFromWaitingRequest);
                    } else if (i2 != 2003) {
                        return;
                    } else {
                        AuthenticationRequest authenticationRequest = (AuthenticationRequest) extras.getSerializable(AuthenticationConstants.Browser.RESPONSE_REQUEST_INFO);
                        final String string3 = extras.getString(AuthenticationConstants.Browser.RESPONSE_FINAL_URL, "");
                        if (string3.isEmpty()) {
                            StringBuilder sb2 = new StringBuilder("Webview did not reach the redirectUrl. ");
                            if (authenticationRequest != null) {
                                sb2.append(authenticationRequest.getLogInfo());
                            }
                            sb2.append(correlationInfoFromWaitingRequest);
                            authenticationException = new AuthenticationException(ADALError.WEBVIEW_RETURNED_EMPTY_REDIRECT_URL, sb2.toString());
                            Logger.m14609e("AcquireTokenRequest:onActivityResult", "", authenticationException.getMessage(), authenticationException.getCode());
                        } else {
                            final CallbackHandler callbackHandler = new CallbackHandler(getHandler(), waitingRequest.getDelegate());
                            THREAD_EXECUTOR.execute(new Runnable() { // from class: com.microsoft.aad.adal.AcquireTokenRequest.2
                                @Override // java.lang.Runnable
                                public void run() {
                                    try {
                                        AuthenticationResult acquireTokenWithAuthCode = new AcquireTokenInteractiveRequest(AcquireTokenRequest.this.mContext, waitingRequest.getRequest(), AcquireTokenRequest.this.mTokenCacheAccessor).acquireTokenWithAuthCode(string3);
                                        waitingRequest.getAPIEvent().setWasApiCallSuccessful(true, null);
                                        waitingRequest.getAPIEvent().setCorrelationId(waitingRequest.getRequest().getCorrelationId().toString());
                                        waitingRequest.getAPIEvent().setIdToken(acquireTokenWithAuthCode.getIdToken());
                                        waitingRequest.getAPIEvent().stopTelemetryAndFlush();
                                        if (waitingRequest.getDelegate() != null) {
                                            Logger.m14615v("AcquireTokenRequest:onActivityResult", "Sending result to callback. ", waitingRequest.getRequest().getLogInfo(), null);
                                            callbackHandler.onSuccess(acquireTokenWithAuthCode);
                                        }
                                    } catch (AuthenticationException e) {
                                        StringBuilder sb3 = new StringBuilder(e.getMessage());
                                        if (e.getCause() != null) {
                                            sb3.append(e.getCause().getMessage());
                                        }
                                        String description = (e.getCode() == null ? ADALError.AUTHORIZATION_CODE_NOT_EXCHANGED_FOR_TOKEN : e.getCode()).getDescription();
                                        Logger.m14610e("AcquireTokenRequest:onActivityResult", description, sb3.toString() + ' ' + ExceptionExtensions.getExceptionMessage(e) + ' ' + Log.getStackTraceString(e), ADALError.AUTHORIZATION_CODE_NOT_EXCHANGED_FOR_TOKEN, null);
                                        AcquireTokenRequest.this.waitingRequestOnError(callbackHandler, waitingRequest, i2, null);
                                    }
                                }
                            });
                            return;
                        }
                    }
                    waitingRequestOnError(waitingRequest, i3, authenticationException);
                }
            } catch (AuthenticationException unused) {
                Logger.m14609e("AcquireTokenRequest:onActivityResult", "Failed to find waiting request. RequestId:" + i3, "", ADALError.ON_ACTIVITY_RESULT_INTENT_NULL);
            }
        }
    }

    public void refreshTokenWithoutCache(final String str, final AuthenticationRequest authenticationRequest, AuthenticationCallback<AuthenticationResult> authenticationCallback) {
        Logger.setCorrelationId(authenticationRequest.getCorrelationId());
        Logger.m14614v("AcquireTokenRequest:refreshTokenWithoutCache", "Refresh token without cache");
        final CallbackHandler callbackHandler = new CallbackHandler(getHandler(), authenticationCallback);
        THREAD_EXECUTOR.execute(new Runnable() { // from class: com.microsoft.aad.adal.AcquireTokenRequest.3
            @Override // java.lang.Runnable
            public void run() {
                try {
                    AcquireTokenRequest.this.validateAcquireTokenRequest(authenticationRequest);
                    AuthenticationResult acquireTokenWithRefreshToken = new AcquireTokenSilentHandler(AcquireTokenRequest.this.mContext, authenticationRequest, AcquireTokenRequest.this.mTokenCacheAccessor).acquireTokenWithRefreshToken(str);
                    AcquireTokenRequest.this.mAPIEvent.setWasApiCallSuccessful(true, null);
                    AcquireTokenRequest.this.mAPIEvent.setIdToken(acquireTokenWithRefreshToken.getIdToken());
                    callbackHandler.onSuccess(acquireTokenWithRefreshToken);
                } catch (AuthenticationException e) {
                    AcquireTokenRequest.this.mAPIEvent.setWasApiCallSuccessful(false, e);
                    callbackHandler.onError(e);
                } catch (Throwable th) {
                    AcquireTokenRequest.this.mAPIEvent.setCorrelationId(authenticationRequest.getCorrelationId().toString());
                    AcquireTokenRequest.this.mAPIEvent.stopTelemetryAndFlush();
                    throw th;
                }
                AcquireTokenRequest.this.mAPIEvent.setCorrelationId(authenticationRequest.getCorrelationId().toString());
                AcquireTokenRequest.this.mAPIEvent.stopTelemetryAndFlush();
            }
        });
    }
}
