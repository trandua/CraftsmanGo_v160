package com.microsoft.aad.adal;

import com.microsoft.aad.adal.TelemetryUtils;
import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import org.json.JSONException;

/* loaded from: classes3.dex */
public class AuthenticationResult implements Serializable {
    private static final long serialVersionUID = 2243372613182536368L;
    private String mAccessToken;
    private String mAuthority;
    private TelemetryUtils.CliTelemInfo mCliTelemInfo;
    private String mCode;
    private String mErrorCode;
    private String mErrorCodes;
    private String mErrorDescription;
    private Date mExpiresOn;
    private Date mExtendedExpiresOn;
    private String mFamilyClientId;
    private HashMap<String, String> mHttpResponseBody;
    private HashMap<String, List<String>> mHttpResponseHeaders;
    private String mIdToken;
    private boolean mInitialRequest;
    private boolean mIsExtendedLifeTimeToken;
    private boolean mIsMultiResourceRefreshToken;
    private String mRefreshToken;
    private int mServiceStatusCode;
    private AuthenticationStatus mStatus;
    private String mTenantId;
    private String mTokenType;
    private UserInfo mUserInfo;

    /* loaded from: classes3.dex */
    public enum AuthenticationStatus {
        Cancelled,
        Failed,
        Succeeded
    }

    AuthenticationResult() {
        this.mStatus = AuthenticationStatus.Failed;
        this.mIsExtendedLifeTimeToken = false;
        this.mHttpResponseBody = null;
        this.mServiceStatusCode = -1;
        this.mHttpResponseHeaders = null;
        this.mCode = null;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public AuthenticationResult(String str) {
        this.mStatus = AuthenticationStatus.Failed;
        this.mIsExtendedLifeTimeToken = false;
        this.mHttpResponseBody = null;
        this.mServiceStatusCode = -1;
        this.mHttpResponseHeaders = null;
        this.mCode = str;
        this.mStatus = AuthenticationStatus.Succeeded;
        this.mAccessToken = null;
        this.mRefreshToken = null;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public AuthenticationResult(String str, String str2, String str3) {
        this.mStatus = AuthenticationStatus.Failed;
        this.mIsExtendedLifeTimeToken = false;
        this.mHttpResponseBody = null;
        this.mServiceStatusCode = -1;
        this.mHttpResponseHeaders = null;
        this.mErrorCode = str;
        this.mErrorDescription = str2;
        this.mErrorCodes = str3;
        this.mStatus = AuthenticationStatus.Failed;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public AuthenticationResult(String str, String str2, Date date, boolean z, UserInfo userInfo, String str3, String str4, Date date2) {
        this.mStatus = AuthenticationStatus.Failed;
        this.mIsExtendedLifeTimeToken = false;
        this.mHttpResponseBody = null;
        this.mServiceStatusCode = -1;
        this.mHttpResponseHeaders = null;
        this.mCode = null;
        this.mAccessToken = str;
        this.mRefreshToken = str2;
        this.mExpiresOn = date;
        this.mIsMultiResourceRefreshToken = z;
        this.mStatus = AuthenticationStatus.Succeeded;
        this.mUserInfo = userInfo;
        this.mTenantId = str3;
        this.mIdToken = str4;
        this.mExtendedExpiresOn = date2;
    }

    AuthenticationResult(String str, String str2, Date date, boolean z, Date date2) {
        this.mStatus = AuthenticationStatus.Failed;
        this.mIsExtendedLifeTimeToken = false;
        this.mHttpResponseBody = null;
        this.mServiceStatusCode = -1;
        this.mHttpResponseHeaders = null;
        this.mCode = null;
        this.mAccessToken = str;
        this.mRefreshToken = str2;
        this.mExpiresOn = date;
        this.mIsMultiResourceRefreshToken = z;
        this.mStatus = AuthenticationStatus.Succeeded;
        this.mExtendedExpiresOn = date2;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static AuthenticationResult createExtendedLifeTimeResult(TokenCacheItem tokenCacheItem) {
        AuthenticationResult createResult = createResult(tokenCacheItem);
        createResult.setExpiresOn(createResult.getExtendedExpiresOn());
        createResult.setIsExtendedLifeTimeToken(true);
        return createResult;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static AuthenticationResult createResult(TokenCacheItem tokenCacheItem) {
        if (tokenCacheItem != null) {
            return new AuthenticationResult(tokenCacheItem.getAccessToken(), tokenCacheItem.getRefreshToken(), tokenCacheItem.getExpiresOn(), tokenCacheItem.getIsMultiResourceRefreshToken(), tokenCacheItem.getUserInfo(), tokenCacheItem.getTenantId(), tokenCacheItem.getRawIdToken(), tokenCacheItem.getExtendedExpiresOn());
        }
        AuthenticationResult authenticationResult = new AuthenticationResult();
        authenticationResult.mStatus = AuthenticationStatus.Failed;
        return authenticationResult;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static AuthenticationResult createResultForInitialRequest() {
        AuthenticationResult authenticationResult = new AuthenticationResult();
        authenticationResult.mInitialRequest = true;
        return authenticationResult;
    }

    public String createAuthorizationHeader() {
        return "Bearer " + getAccessToken();
    }

    public String getAccessToken() {
        return this.mAccessToken;
    }

    public String getAccessTokenType() {
        return this.mTokenType;
    }

    public final String getAuthority() {
        return this.mAuthority;
    }

    public final TelemetryUtils.CliTelemInfo getCliTelemInfo() {
        return this.mCliTelemInfo;
    }

    public String getCode() {
        return this.mCode;
    }

    public String getErrorCode() {
        return this.mErrorCode;
    }

    public String[] getErrorCodes() {
        String str = this.mErrorCodes;
        if (str != null) {
            return str.replaceAll("[\\[\\]]", "").split("([^,]),");
        }
        return null;
    }

    public String getErrorDescription() {
        return this.mErrorDescription;
    }

    public String getErrorLogInfo() {
        return " ErrorCode:" + getErrorCode();
    }

    public Date getExpiresOn() {
        return Utility.getImmutableDateObject(this.mExpiresOn);
    }

    public final Date getExtendedExpiresOn() {
        return this.mExtendedExpiresOn;
    }

    public final String getFamilyClientId() {
        return this.mFamilyClientId;
    }

    public HashMap<String, String> getHttpResponseBody() {
        return this.mHttpResponseBody;
    }

    public HashMap<String, List<String>> getHttpResponseHeaders() {
        return this.mHttpResponseHeaders;
    }

    public String getIdToken() {
        return this.mIdToken;
    }

    public boolean getIsMultiResourceRefreshToken() {
        return this.mIsMultiResourceRefreshToken;
    }

    public String getRefreshToken() {
        return this.mRefreshToken;
    }

    public int getServiceStatusCode() {
        return this.mServiceStatusCode;
    }

    public AuthenticationStatus getStatus() {
        return this.mStatus;
    }

    public String getTenantId() {
        return this.mTenantId;
    }

    public UserInfo getUserInfo() {
        return this.mUserInfo;
    }

    public boolean isExpired() {
        return TokenCacheItem.isTokenExpired(this.mIsExtendedLifeTimeToken ? getExtendedExpiresOn() : getExpiresOn());
    }

    public boolean isExtendedLifeTimeToken() {
        return this.mIsExtendedLifeTimeToken;
    }

    public boolean isInitialRequest() {
        return this.mInitialRequest;
    }

    public final void setAuthority(String str) {
        if (StringExtensions.isNullOrBlank(str)) {
            return;
        }
        this.mAuthority = str;
    }

    public final void setCliTelemInfo(TelemetryUtils.CliTelemInfo cliTelemInfo) {
        this.mCliTelemInfo = cliTelemInfo;
    }

    public void setCode(String str) {
        this.mCode = str;
    }

    public final void setExpiresOn(Date date) {
        this.mExpiresOn = date;
    }

    public final void setExtendedExpiresOn(Date date) {
        this.mExtendedExpiresOn = date;
    }

    public final void setFamilyClientId(String str) {
        this.mFamilyClientId = str;
    }

    public void setHttpResponse(HttpWebResponse httpWebResponse) {
        if (httpWebResponse != null) {
            this.mServiceStatusCode = httpWebResponse.getStatusCode();
            if (httpWebResponse.getResponseHeaders() != null) {
                this.mHttpResponseHeaders = new HashMap<>(httpWebResponse.getResponseHeaders());
            }
            if (httpWebResponse.getBody() != null) {
                try {
                    this.mHttpResponseBody = new HashMap<>(HashMapExtensions.getJsonResponse(httpWebResponse));
                } catch (JSONException e) {
                    Logger.m14609e("AuthenticationException", "Json exception", ExceptionExtensions.getExceptionMessage(e), ADALError.SERVER_INVALID_JSON_RESPONSE);
                }
            }
        }
    }

    public void setHttpResponseBody(HashMap<String, String> hashMap) {
        this.mHttpResponseBody = hashMap;
    }

    public void setHttpResponseHeaders(HashMap<String, List<String>> hashMap) {
        this.mHttpResponseHeaders = hashMap;
    }

    public void setIdToken(String str) {
        this.mIdToken = str;
    }

    public final void setIsExtendedLifeTimeToken(boolean z) {
        this.mIsExtendedLifeTimeToken = z;
    }

    public void setRefreshToken(String str) {
        this.mRefreshToken = str;
    }

    public void setServiceStatusCode(int i) {
        this.mServiceStatusCode = i;
    }

    public void setTenantId(String str) {
        this.mTenantId = str;
    }

    public void setUserInfo(UserInfo userInfo) {
        this.mUserInfo = userInfo;
    }
}
