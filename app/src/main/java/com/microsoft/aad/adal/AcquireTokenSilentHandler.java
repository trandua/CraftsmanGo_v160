package com.microsoft.aad.adal;

import android.content.Context;
import android.util.Log;
import java.io.IOException;
import java.net.MalformedURLException;

/* loaded from: classes3.dex */
public class AcquireTokenSilentHandler {
    private static final String TAG = "AcquireTokenSilentHandler";
    private boolean mAttemptedWithMRRT = false;
    private final AuthenticationRequest mAuthRequest;
    private final Context mContext;
    private TokenCacheItem mMrrtTokenCacheItem;
    private final TokenCacheAccessor mTokenCacheAccessor;
    private IWebRequestHandler mWebRequestHandler;

    /* JADX INFO: Access modifiers changed from: package-private */
    public AcquireTokenSilentHandler(Context context, AuthenticationRequest authenticationRequest, TokenCacheAccessor tokenCacheAccessor) {
        this.mWebRequestHandler = null;
        if (context == null) {
            throw new IllegalArgumentException("context");
        }
        if (authenticationRequest != null) {
            this.mContext = context;
            this.mAuthRequest = authenticationRequest;
            this.mTokenCacheAccessor = tokenCacheAccessor;
            this.mWebRequestHandler = new WebRequestHandler();
            return;
        }
        throw new IllegalArgumentException("authRequest");
    }

    public AuthenticationResult getAccessToken() throws AuthenticationException {
        TokenCacheAccessor tokenCacheAccessor = this.mTokenCacheAccessor;
        if (tokenCacheAccessor == null) {
            return null;
        }
        TokenCacheItem aTFromCache = tokenCacheAccessor.getATFromCache(this.mAuthRequest.getResource(), this.mAuthRequest.getClientId(), this.mAuthRequest.getUserFromRequest());
        if (aTFromCache == null) {
            Logger.m14614v("AcquireTokenSilentHandler:getAccessToken", "No valid access token exists, try with refresh token.");
            return tryRT();
        }
        Logger.m14614v("AcquireTokenSilentHandler:getAccessToken", "Return AT from cache.");
        return AuthenticationResult.createResult(aTFromCache);
    }

    public AuthenticationResult acquireTokenWithRefreshToken(String str) throws AuthenticationException {
        Logger.m14615v("AcquireTokenSilentHandler:acquireTokenWithRefreshToken", "Try to get new access token with the found refresh token.", this.mAuthRequest.getLogInfo(), null);
        HttpWebRequest.throwIfNetworkNotAvailable(this.mContext);
        try {
            AuthenticationResult refreshToken = new Oauth2(this.mAuthRequest, this.mWebRequestHandler, new JWSBuilder()).refreshToken(str);
            if (refreshToken != null && StringExtensions.isNullOrBlank(refreshToken.getRefreshToken())) {
                Logger.m14612i("AcquireTokenSilentHandler:acquireTokenWithRefreshToken", "Refresh token is not returned or empty", "");
                refreshToken.setRefreshToken(str);
            }
            return refreshToken;
        } catch (ServerRespondingWithRetryableException e) {
            Logger.m14612i("AcquireTokenSilentHandler:acquireTokenWithRefreshToken", "The server is not responding after the retry with error code: " + e.getCode(), "");
            TokenCacheItem staleToken = this.mTokenCacheAccessor.getStaleToken(this.mAuthRequest);
            if (staleToken != null) {
                AuthenticationResult createExtendedLifeTimeResult = AuthenticationResult.createExtendedLifeTimeResult(staleToken);
                Logger.m14612i("AcquireTokenSilentHandler:acquireTokenWithRefreshToken", "The result with stale access token is returned.", "");
                return createExtendedLifeTimeResult;
            }
//            Logger.m14610e("AcquireTokenSilentHandler:acquireTokenWithRefreshToken", "Error in refresh token for request. ", "Request: " + this.mAuthRequest.getLogInfo() + " " + ExceptionExtensions.getExceptionMessage(e) + " " + Log.getStackTraceString(e), ADALError.AUTH_FAILED_NO_TOKEN, null);
            throw new AuthenticationException(ADALError.AUTH_FAILED_NO_TOKEN, ExceptionExtensions.getExceptionMessage(e), new AuthenticationException(ADALError.SERVER_ERROR, e.getMessage(), e));
        } catch (AuthenticationException e2) {
//            e = e2;
//            Logger.m14610e("AcquireTokenSilentHandler:acquireTokenWithRefreshToken", "Error in refresh token for request.", "Request: " + this.mAuthRequest.getLogInfo() + " " + ExceptionExtensions.getExceptionMessage(e2) + " " + Log.getStackTraceString(e), ADALError.AUTH_FAILED_NO_TOKEN, null);
            throw new AuthenticationException(ADALError.AUTH_FAILED_NO_TOKEN, ExceptionExtensions.getExceptionMessage(e2), new AuthenticationException(ADALError.SERVER_ERROR, e2.getMessage(), e2));
        } catch (IOException e3) {
//            e = e3;
//            Logger.m14610e("AcquireTokenSilentHandler:acquireTokenWithRefreshToken", "Error in refresh token for request.", "Request: " + this.mAuthRequest.getLogInfo() + " " + ExceptionExtensions.getExceptionMessage(e3) + " " + Log.getStackTraceString(e), ADALError.AUTH_FAILED_NO_TOKEN, null);
            throw new AuthenticationException(ADALError.AUTH_FAILED_NO_TOKEN, ExceptionExtensions.getExceptionMessage(e3), new AuthenticationException(ADALError.SERVER_ERROR, e3.getMessage(), e3));
        }
    }

    public void setWebRequestHandler(IWebRequestHandler iWebRequestHandler) {
        this.mWebRequestHandler = iWebRequestHandler;
    }

    private AuthenticationResult tryRT() throws AuthenticationException {
        try {
            TokenCacheItem regularRefreshTokenCacheItem = this.mTokenCacheAccessor.getRegularRefreshTokenCacheItem(this.mAuthRequest.getResource(), this.mAuthRequest.getClientId(), this.mAuthRequest.getUserFromRequest());
            if (regularRefreshTokenCacheItem == null) {
                Logger.m14614v("AcquireTokenSilentHandler:tryRT", "Regular token cache entry does not exist, try with MRRT.");
                return tryMRRT();
            }
            if (!regularRefreshTokenCacheItem.getIsMultiResourceRefreshToken() && !isMRRTEntryExisted()) {
                if (StringExtensions.isNullOrBlank(this.mAuthRequest.getUserFromRequest()) && this.mTokenCacheAccessor.isMultipleRTsMatchingGivenAppAndResource(this.mAuthRequest.getClientId(), this.mAuthRequest.getResource())) {
                    throw new AuthenticationException(ADALError.AUTH_FAILED_USER_MISMATCH, "Multiple refresh tokens exists for the given client id and resource");
                }
                Logger.m14614v("AcquireTokenSilentHandler:tryRT", "Send request to use regular RT for new AT.");
                return acquireTokenWithCachedItem(regularRefreshTokenCacheItem);
            }
            Logger.m14614v("AcquireTokenSilentHandler:tryRT", regularRefreshTokenCacheItem.getIsMultiResourceRefreshToken() ? "Found RT and it's also a MRRT, retry with MRRT" : "RT is found and there is a MRRT entry existed, try with MRRT");
            return tryMRRT();
        } catch (MalformedURLException e) {
            throw new AuthenticationException(ADALError.DEVELOPER_AUTHORITY_IS_NOT_VALID_URL, e.getMessage(), e);
        }
    }

    private AuthenticationResult tryMRRT() throws AuthenticationException {
        try {
            TokenCacheItem mRRTItem = this.mTokenCacheAccessor.getMRRTItem(this.mAuthRequest.getClientId(), this.mAuthRequest.getUserFromRequest());
            this.mMrrtTokenCacheItem = mRRTItem;
            if (mRRTItem == null) {
                Logger.m14614v("AcquireTokenSilentHandler:tryMRRT", "MRRT token does not exist, try with FRT");
                return tryFRT("1", null);
            } else if (mRRTItem.isFamilyToken()) {
                Logger.m14614v("AcquireTokenSilentHandler:tryMRRT", "MRRT item exists but it's also a FRT, try with FRT.");
                return tryFRT(this.mMrrtTokenCacheItem.getFamilyClientId(), null);
            } else {
                AuthenticationResult useMRRT = useMRRT();
                if (isTokenRequestFailed(useMRRT)) {
                    useMRRT = tryFRT(StringExtensions.isNullOrBlank(this.mMrrtTokenCacheItem.getFamilyClientId()) ? "1" : this.mMrrtTokenCacheItem.getFamilyClientId(), useMRRT);
                }
                if (StringExtensions.isNullOrBlank(this.mAuthRequest.getUserFromRequest()) && this.mTokenCacheAccessor.isMultipleMRRTsMatchingGivenApp(this.mAuthRequest.getClientId())) {
                    throw new AuthenticationException(ADALError.AUTH_FAILED_USER_MISMATCH, "No User provided and multiple MRRTs exist for the given client id");
                }
                return useMRRT;
            }
        } catch (MalformedURLException e) {
            throw new AuthenticationException(ADALError.DEVELOPER_AUTHORITY_IS_NOT_VALID_URL, e.getMessage(), e);
        }
    }

    private AuthenticationResult tryFRT(String str, AuthenticationResult authenticationResult) throws AuthenticationException {
        AuthenticationResult useMRRT;
        try {
            TokenCacheItem fRTItem = this.mTokenCacheAccessor.getFRTItem(str, this.mAuthRequest.getUserFromRequest());
            if (fRTItem != null) {
                Logger.m14614v("AcquireTokenSilentHandler:tryFRT", "Send request to use FRT for new AT.");
                AuthenticationResult acquireTokenWithCachedItem = acquireTokenWithCachedItem(fRTItem);
                if (isTokenRequestFailed(acquireTokenWithCachedItem) && !this.mAttemptedWithMRRT && (useMRRT = useMRRT()) != null) {
                    return useMRRT;
                }
                return acquireTokenWithCachedItem;
            } else if (this.mAttemptedWithMRRT) {
                return authenticationResult;
            } else {
                Logger.m14614v("AcquireTokenSilentHandler:tryFRT", "FRT cache item does not exist, fall back to try MRRT.");
                return useMRRT();
            }
        } catch (MalformedURLException e) {
            throw new AuthenticationException(ADALError.DEVELOPER_AUTHORITY_IS_NOT_VALID_URL, e.getMessage(), e);
        }
    }

    private AuthenticationResult useMRRT() throws AuthenticationException {
        Logger.m14614v("AcquireTokenSilentHandler:useMRRT", "Send request to use MRRT for new AT.");
        this.mAttemptedWithMRRT = true;
        TokenCacheItem tokenCacheItem = this.mMrrtTokenCacheItem;
        if (tokenCacheItem != null) {
            return acquireTokenWithCachedItem(tokenCacheItem);
        }
        Logger.m14614v("AcquireTokenSilentHandler:useMRRT", "MRRT does not exist, cannot proceed with MRRT for new AT.");
        return null;
    }

    private AuthenticationResult acquireTokenWithCachedItem(TokenCacheItem tokenCacheItem) throws AuthenticationException {
        if (StringExtensions.isNullOrBlank(tokenCacheItem.getRefreshToken())) {
            Logger.m14615v("AcquireTokenSilentHandler:acquireTokenWithCachedItem", "Token cache item contains empty refresh token, cannot continue refresh token request", this.mAuthRequest.getLogInfo(), null);
            return null;
        }
        AuthenticationResult acquireTokenWithRefreshToken = acquireTokenWithRefreshToken(tokenCacheItem.getRefreshToken());
        if (acquireTokenWithRefreshToken != null && !acquireTokenWithRefreshToken.isExtendedLifeTimeToken()) {
            this.mTokenCacheAccessor.updateCachedItemWithResult(this.mAuthRequest.getResource(), this.mAuthRequest.getClientId(), acquireTokenWithRefreshToken, tokenCacheItem);
        }
        return acquireTokenWithRefreshToken;
    }

    private boolean isMRRTEntryExisted() throws AuthenticationException {
        try {
            TokenCacheItem mRRTItem = this.mTokenCacheAccessor.getMRRTItem(this.mAuthRequest.getClientId(), this.mAuthRequest.getUserFromRequest());
            if (mRRTItem != null) {
                if (!StringExtensions.isNullOrBlank(mRRTItem.getRefreshToken())) {
                    return true;
                }
            }
            return false;
        } catch (MalformedURLException e) {
            throw new AuthenticationException(ADALError.DEVELOPER_AUTHORITY_IS_NOT_VALID_URL, e.getMessage(), e);
        }
    }

    private boolean isTokenRequestFailed(AuthenticationResult authenticationResult) {
        return (authenticationResult == null || StringExtensions.isNullOrBlank(authenticationResult.getErrorCode())) ? false : true;
    }
}
