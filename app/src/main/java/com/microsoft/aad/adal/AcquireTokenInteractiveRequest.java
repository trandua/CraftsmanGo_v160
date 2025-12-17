package com.microsoft.aad.adal;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import com.microsoft.aad.adal.AuthenticationConstants;
import java.io.IOException;
import java.net.MalformedURLException;

/* loaded from: classes3.dex */
final class AcquireTokenInteractiveRequest {
    private static final String TAG = "AcquireTokenInteractiveRequest";
    private final AuthenticationRequest mAuthRequest;
    private final Context mContext;
    private final TokenCacheAccessor mTokenCacheAccessor;

    /* JADX INFO: Access modifiers changed from: package-private */
    public AcquireTokenInteractiveRequest(Context context, AuthenticationRequest authenticationRequest, TokenCacheAccessor tokenCacheAccessor) {
        this.mContext = context;
        this.mTokenCacheAccessor = tokenCacheAccessor;
        this.mAuthRequest = authenticationRequest;
    }

    private Intent getAuthenticationActivityIntent() {
        Intent intent = new Intent();
        if (AuthenticationSettings.INSTANCE.getActivityPackageName() != null) {
            intent.setClassName(AuthenticationSettings.INSTANCE.getActivityPackageName(), AuthenticationActivity.class.getName());
        } else {
            intent.setClass(this.mContext, AuthenticationActivity.class);
        }
        intent.putExtra(AuthenticationConstants.Browser.REQUEST_MESSAGE, this.mAuthRequest);
        return intent;
    }

    private String getCorrelationInfo() {
        return String.format(" CorrelationId: %s", this.mAuthRequest.getCorrelationId().toString());
    }

    private boolean resolveIntent(Intent intent) {
        return this.mContext.getPackageManager().resolveActivity(intent, 0) != null;
    }

    private boolean startAuthenticationActivity(IWindowComponent iWindowComponent) {
        Intent authenticationActivityIntent = getAuthenticationActivityIntent();
        if (!resolveIntent(authenticationActivityIntent)) {
            Logger.m14609e("AcquireTokenInteractiveRequest:startAuthenticationActivity", "Intent is not resolved", "", ADALError.DEVELOPER_ACTIVITY_IS_NOT_RESOLVED);
            return false;
        }
        try {
            iWindowComponent.startActivityForResult(authenticationActivityIntent, 1001);
            return true;
        } catch (ActivityNotFoundException e) {
            Logger.m14610e("AcquireTokenInteractiveRequest:startAuthenticationActivity", "Activity login is not found after resolving intent", "", ADALError.DEVELOPER_ACTIVITY_IS_NOT_RESOLVED, e);
            return false;
        }
    }

    public void acquireToken(IWindowComponent iWindowComponent, AuthenticationDialog authenticationDialog) throws AuthenticationException {
        HttpWebRequest.throwIfNetworkNotAvailable(this.mContext);
        if (PromptBehavior.FORCE_PROMPT == this.mAuthRequest.getPrompt()) {
            Logger.m14614v("AcquireTokenInteractiveRequest:acquireToken", "FORCE_PROMPT is set for embedded flow, reset it as Always.");
            this.mAuthRequest.setPrompt(PromptBehavior.Always);
        }
        if (authenticationDialog != null) {
            authenticationDialog.show();
        } else if (!startAuthenticationActivity(iWindowComponent)) {
            throw new AuthenticationException(ADALError.DEVELOPER_ACTIVITY_IS_NOT_RESOLVED);
        }
    }

    public AuthenticationResult acquireTokenWithAuthCode(String str) throws AuthenticationException {
        TokenCacheAccessor tokenCacheAccessor;
        Logger.m14615v("AcquireTokenInteractiveRequest:acquireTokenWithAuthCode", "Start token acquisition with auth code.", this.mAuthRequest.getLogInfo(), null);
        try {
            AuthenticationResult token = new Oauth2(this.mAuthRequest, new WebRequestHandler()).getToken(str);
            Logger.m14614v("AcquireTokenInteractiveRequest:acquireTokenWithAuthCode", "OnActivityResult processed the result.");
            if (token == null) {
                Logger.m14609e("AcquireTokenInteractiveRequest:acquireTokenWithAuthCode", "Returned result with exchanging auth code for token is null" + getCorrelationInfo(), "", ADALError.AUTHORIZATION_CODE_NOT_EXCHANGED_FOR_TOKEN);
                throw new AuthenticationException(ADALError.AUTHORIZATION_CODE_NOT_EXCHANGED_FOR_TOKEN, getCorrelationInfo());
            } else if (StringExtensions.isNullOrBlank(token.getErrorCode())) {
                if (!StringExtensions.isNullOrBlank(token.getAccessToken()) && (tokenCacheAccessor = this.mTokenCacheAccessor) != null) {
                    try {
                        tokenCacheAccessor.updateTokenCache(this.mAuthRequest.getResource(), this.mAuthRequest.getClientId(), token);
                    } catch (MalformedURLException e) {
                        throw new AuthenticationException(ADALError.DEVELOPER_AUTHORITY_IS_NOT_VALID_URL, e.getMessage(), e);
                    }
                }
                return token;
            } else {
                Logger.m14609e("AcquireTokenInteractiveRequest:acquireTokenWithAuthCode", " ErrorCode:" + token.getErrorCode(), " ErrorDescription:" + token.getErrorDescription(), ADALError.AUTH_FAILED);
                throw new AuthenticationException(ADALError.AUTH_FAILED, " ErrorCode:" + token.getErrorCode());
            }
        } catch (AuthenticationException | IOException e2) {
            throw new AuthenticationException(ADALError.AUTHORIZATION_CODE_NOT_EXCHANGED_FOR_TOKEN, "Error in processing code to get token. " + getCorrelationInfo(), e2);
        }
    }
}
