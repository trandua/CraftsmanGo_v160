package com.microsoft.aad.adal;

import com.microsoft.aad.adal.AuthenticationConstants;
import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;

/* loaded from: classes3.dex */
public class TokenCacheItem implements Serializable {
    private static final String TAG = "TokenCacheItem";
    private static final long serialVersionUID = 1;
    private String mAccessToken;
    private String mAuthority;
    private String mClientId;
    private Date mExpiresOn;
    private Date mExtendedExpiresOn;
    private String mFamilyClientId;
    private boolean mIsMultiResourceRefreshToken;
    private String mRawIdToken;
    private String mRefreshtoken;
    private String mResource;
    private String mSpeRing;
    private String mTenantId;
    private UserInfo mUserInfo;

    public TokenCacheItem() {
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public TokenCacheItem(TokenCacheItem tokenCacheItem) {
        this.mAuthority = tokenCacheItem.getAuthority();
        this.mResource = tokenCacheItem.getResource();
        this.mClientId = tokenCacheItem.getClientId();
        this.mAccessToken = tokenCacheItem.getAccessToken();
        this.mRefreshtoken = tokenCacheItem.getRefreshToken();
        this.mRawIdToken = tokenCacheItem.getRawIdToken();
        this.mUserInfo = tokenCacheItem.getUserInfo();
        this.mExpiresOn = tokenCacheItem.getExpiresOn();
        this.mIsMultiResourceRefreshToken = tokenCacheItem.getIsMultiResourceRefreshToken();
        this.mTenantId = tokenCacheItem.getTenantId();
        this.mFamilyClientId = tokenCacheItem.getFamilyClientId();
        this.mExtendedExpiresOn = tokenCacheItem.getExtendedExpiresOn();
        this.mSpeRing = tokenCacheItem.getSpeRing();
    }

    private TokenCacheItem(String str, AuthenticationResult authenticationResult) {
        if (authenticationResult == null) {
            throw new IllegalArgumentException("authenticationResult");
        }
        if (!StringExtensions.isNullOrBlank(str)) {
            this.mAuthority = str;
            this.mExpiresOn = authenticationResult.getExpiresOn();
            this.mIsMultiResourceRefreshToken = authenticationResult.getIsMultiResourceRefreshToken();
            this.mTenantId = authenticationResult.getTenantId();
            this.mUserInfo = authenticationResult.getUserInfo();
            this.mRawIdToken = authenticationResult.getIdToken();
            this.mRefreshtoken = authenticationResult.getRefreshToken();
            this.mFamilyClientId = authenticationResult.getFamilyClientId();
            this.mExtendedExpiresOn = authenticationResult.getExtendedExpiresOn();
            if (authenticationResult.getCliTelemInfo() != null) {
                this.mSpeRing = authenticationResult.getCliTelemInfo().getSpeRing();
                return;
            }
            return;
        }
        throw new IllegalArgumentException(AuthenticationConstants.OAuth2.AUTHORITY);
    }

    public static TokenCacheItem createFRRTTokenCacheItem(String str, AuthenticationResult authenticationResult) {
        return new TokenCacheItem(str, authenticationResult);
    }

    public static TokenCacheItem createMRRTTokenCacheItem(String str, String str2, AuthenticationResult authenticationResult) {
        TokenCacheItem tokenCacheItem = new TokenCacheItem(str, authenticationResult);
        tokenCacheItem.setClientId(str2);
        return tokenCacheItem;
    }

    public static TokenCacheItem createRegularTokenCacheItem(String str, String str2, String str3, AuthenticationResult authenticationResult) {
        TokenCacheItem tokenCacheItem = new TokenCacheItem(str, authenticationResult);
        tokenCacheItem.setClientId(str3);
        tokenCacheItem.setResource(str2);
        tokenCacheItem.setAccessToken(authenticationResult.getAccessToken());
        return tokenCacheItem;
    }

    public static boolean isTokenExpired(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(13, AuthenticationSettings.INSTANCE.getExpirationBuffer());
        Date time = calendar.getTime();
        Logger.m14612i(TAG, "Check token expiration time.", "expiresOn:" + date + " timeWithBuffer:" + calendar.getTime() + " Buffer:" + AuthenticationSettings.INSTANCE.getExpirationBuffer());
        return date != null && date.before(time);
    }

    public String getAccessToken() {
        return this.mAccessToken;
    }

    public String getAuthority() {
        return this.mAuthority;
    }

    public String getClientId() {
        return this.mClientId;
    }

    public Date getExpiresOn() {
        return Utility.getImmutableDateObject(this.mExpiresOn);
    }

    public final Date getExtendedExpiresOn() {
        return Utility.getImmutableDateObject(this.mExtendedExpiresOn);
    }

    public final String getFamilyClientId() {
        return this.mFamilyClientId;
    }

    public boolean getIsMultiResourceRefreshToken() {
        return this.mIsMultiResourceRefreshToken;
    }

    public String getRawIdToken() {
        return this.mRawIdToken;
    }

    public String getRefreshToken() {
        return this.mRefreshtoken;
    }

    public String getResource() {
        return this.mResource;
    }

    public String getSpeRing() {
        return this.mSpeRing;
    }

    public String getTenantId() {
        return this.mTenantId;
    }

    public TokenEntryType getTokenEntryType() {
        return !StringExtensions.isNullOrBlank(getResource()) ? TokenEntryType.REGULAR_TOKEN_ENTRY : StringExtensions.isNullOrBlank(getClientId()) ? TokenEntryType.FRT_TOKEN_ENTRY : TokenEntryType.MRRT_TOKEN_ENTRY;
    }

    public UserInfo getUserInfo() {
        return this.mUserInfo;
    }

    public final boolean isExtendedLifetimeValid() {
        if (this.mExtendedExpiresOn == null || StringExtensions.isNullOrBlank(this.mAccessToken)) {
            return false;
        }
        return !isTokenExpired(this.mExtendedExpiresOn);
    }

    public boolean isFamilyToken() {
        return !StringExtensions.isNullOrBlank(this.mFamilyClientId);
    }

    public void setAccessToken(String str) {
        this.mAccessToken = str;
    }

    public void setAuthority(String str) {
        this.mAuthority = str;
    }

    public void setClientId(String str) {
        this.mClientId = str;
    }

    public void setExpiresOn(Date date) {
        this.mExpiresOn = Utility.getImmutableDateObject(date);
    }

    public final void setExtendedExpiresOn(Date date) {
        this.mExtendedExpiresOn = Utility.getImmutableDateObject(date);
    }

    public final void setFamilyClientId(String str) {
        this.mFamilyClientId = str;
    }

    public void setIsMultiResourceRefreshToken(boolean z) {
        this.mIsMultiResourceRefreshToken = z;
    }

    public void setRawIdToken(String str) {
        this.mRawIdToken = str;
    }

    public void setRefreshToken(String str) {
        this.mRefreshtoken = str;
    }

    public void setResource(String str) {
        this.mResource = str;
    }

    public void setSpeRing(String str) {
        this.mSpeRing = str;
    }

    public void setTenantId(String str) {
        this.mTenantId = str;
    }

    public void setUserInfo(UserInfo userInfo) {
        this.mUserInfo = userInfo;
    }
}
