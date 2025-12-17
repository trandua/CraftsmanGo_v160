package com.microsoft.aad.adal;

import java.io.Serializable;
import java.util.UUID;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes3.dex */
public class AuthenticationRequest implements Serializable {
    private static final int DELIM_NOT_FOUND = -1;
    private static final String UPN_DOMAIN_SUFFIX_DELIM = "@";
    private static final long serialVersionUID = 1;
    private String mAuthority;
    private String mBrokerAccountName;
    private String mClaimsChallenge;
    private String mClientId;
    private UUID mCorrelationId;
    private String mExtraQueryParamsAuthentication;
    private UserIdentifierType mIdentifierType;
    private transient InstanceDiscoveryMetadata mInstanceDiscoveryMetadata;
    private boolean mIsExtendedLifetimeEnabled;
    private String mLoginHint;
    private PromptBehavior mPrompt;
    private String mRedirectUri;
    private int mRequestId;
    private String mResource;
    private boolean mSilent;
    private String mTelemetryRequestId;
    private String mUserId;
    private String mVersion;

    /* loaded from: classes3.dex */
    enum UserIdentifierType {
        UniqueId,
        LoginHint,
        NoUser
    }

    public AuthenticationRequest() {
        this.mRequestId = 0;
        this.mAuthority = null;
        this.mRedirectUri = null;
        this.mResource = null;
        this.mClientId = null;
        this.mLoginHint = null;
        this.mUserId = null;
        this.mBrokerAccountName = null;
        this.mSilent = false;
        this.mVersion = null;
        this.mIsExtendedLifetimeEnabled = false;
        this.mIdentifierType = UserIdentifierType.NoUser;
    }

    public AuthenticationRequest(String str, String str2, String str3, String str4, String str5, PromptBehavior promptBehavior, String str6, UUID uuid, boolean z, String str7) {
        this.mRequestId = 0;
        this.mUserId = null;
        this.mSilent = false;
        this.mVersion = null;
        this.mIsExtendedLifetimeEnabled = false;
        this.mAuthority = str;
        this.mResource = str2;
        this.mClientId = str3;
        this.mRedirectUri = str4;
        this.mLoginHint = str5;
        this.mBrokerAccountName = str5;
        this.mPrompt = promptBehavior;
        this.mExtraQueryParamsAuthentication = str6;
        this.mCorrelationId = uuid;
        this.mIdentifierType = UserIdentifierType.NoUser;
        this.mIsExtendedLifetimeEnabled = z;
        this.mClaimsChallenge = str7;
    }

    public AuthenticationRequest(String str, String str2, String str3, String str4, String str5, UUID uuid, boolean z) {
        this.mRequestId = 0;
        this.mUserId = null;
        this.mSilent = false;
        this.mVersion = null;
        this.mAuthority = str;
        this.mResource = str2;
        this.mClientId = str3;
        this.mRedirectUri = str4;
        this.mLoginHint = str5;
        this.mBrokerAccountName = str5;
        this.mCorrelationId = uuid;
        this.mIsExtendedLifetimeEnabled = z;
    }

    public AuthenticationRequest(String str, String str2, String str3, String str4, String str5, boolean z) {
        this.mRequestId = 0;
        this.mUserId = null;
        this.mSilent = false;
        this.mVersion = null;
        this.mAuthority = str;
        this.mResource = str2;
        this.mClientId = str3;
        this.mRedirectUri = str4;
        this.mLoginHint = str5;
        this.mBrokerAccountName = str5;
        this.mIsExtendedLifetimeEnabled = z;
    }

    public AuthenticationRequest(String str, String str2, String str3, String str4, UUID uuid, boolean z) {
        this.mRequestId = 0;
        this.mRedirectUri = null;
        this.mLoginHint = null;
        this.mBrokerAccountName = null;
        this.mSilent = false;
        this.mVersion = null;
        this.mAuthority = str;
        this.mResource = str2;
        this.mClientId = str3;
        this.mUserId = str4;
        this.mCorrelationId = uuid;
        this.mIsExtendedLifetimeEnabled = z;
    }

    public AuthenticationRequest(String str, String str2, String str3, UUID uuid, boolean z) {
        this.mRequestId = 0;
        this.mRedirectUri = null;
        this.mLoginHint = null;
        this.mUserId = null;
        this.mBrokerAccountName = null;
        this.mSilent = false;
        this.mVersion = null;
        this.mAuthority = str;
        this.mClientId = str3;
        this.mResource = str2;
        this.mCorrelationId = uuid;
        this.mIsExtendedLifetimeEnabled = z;
    }

    public AuthenticationRequest(String str, String str2, String str3, boolean z) {
        this.mRequestId = 0;
        this.mRedirectUri = null;
        this.mLoginHint = null;
        this.mUserId = null;
        this.mBrokerAccountName = null;
        this.mSilent = false;
        this.mVersion = null;
        this.mAuthority = str;
        this.mResource = str2;
        this.mClientId = str3;
        this.mIsExtendedLifetimeEnabled = z;
    }

    public String getAuthority() {
        return this.mAuthority;
    }

    public String getBrokerAccountName() {
        return this.mBrokerAccountName;
    }

    public String getClaimsChallenge() {
        return this.mClaimsChallenge;
    }

    public String getClientId() {
        return this.mClientId;
    }

    public UUID getCorrelationId() {
        return this.mCorrelationId;
    }

    public String getExtraQueryParamsAuthentication() {
        return this.mExtraQueryParamsAuthentication;
    }

    public InstanceDiscoveryMetadata getInstanceDiscoveryMetadata() {
        return this.mInstanceDiscoveryMetadata;
    }

    public boolean getIsExtendedLifetimeEnabled() {
        return this.mIsExtendedLifetimeEnabled;
    }

    public String getLogInfo() {
        return String.format("Request authority:%s clientid:%s", this.mAuthority, this.mClientId);
    }

    public String getLoginHint() {
        return this.mLoginHint;
    }

    public PromptBehavior getPrompt() {
        return this.mPrompt;
    }

    public String getRedirectUri() {
        return this.mRedirectUri;
    }

    public int getRequestId() {
        return this.mRequestId;
    }

    public String getResource() {
        return this.mResource;
    }

    public String getTelemetryRequestId() {
        return this.mTelemetryRequestId;
    }

    public String getUpnSuffix() {
        int lastIndexOf;
        String loginHint = getLoginHint();
        if (loginHint == null || -1 == (lastIndexOf = loginHint.lastIndexOf(UPN_DOMAIN_SUFFIX_DELIM))) {
            return null;
        }
        return loginHint.substring(lastIndexOf + 1);
    }

    public String getUserFromRequest() {
        if (UserIdentifierType.LoginHint == this.mIdentifierType) {
            return this.mLoginHint;
        }
        if (UserIdentifierType.UniqueId == this.mIdentifierType) {
            return this.mUserId;
        }
        return null;
    }

    public String getUserId() {
        return this.mUserId;
    }

    public UserIdentifierType getUserIdentifierType() {
        return this.mIdentifierType;
    }

    public String getVersion() {
        return this.mVersion;
    }

    public boolean isSilent() {
        return this.mSilent;
    }

    public void setAuthority(String str) {
        this.mAuthority = str;
    }

    public void setBrokerAccountName(String str) {
        this.mBrokerAccountName = str;
    }

    public void setClaimsChallenge(String str) {
        this.mClaimsChallenge = str;
    }

    public void setInstanceDiscoveryMetadata(InstanceDiscoveryMetadata instanceDiscoveryMetadata) {
        this.mInstanceDiscoveryMetadata = instanceDiscoveryMetadata;
    }

    public void setLoginHint(String str) {
        this.mLoginHint = str;
    }

    public void setPrompt(PromptBehavior promptBehavior) {
        this.mPrompt = promptBehavior;
    }

    public void setRequestId(int i) {
        this.mRequestId = i;
    }

    public void setSilent(boolean z) {
        this.mSilent = z;
    }

    public void setTelemetryRequestId(String str) {
        this.mTelemetryRequestId = str;
    }

    public void setUserId(String str) {
        this.mUserId = str;
    }

    public void setUserIdentifierType(UserIdentifierType userIdentifierType) {
        this.mIdentifierType = userIdentifierType;
    }

    public void setVersion(String str) {
        this.mVersion = str;
    }
}
