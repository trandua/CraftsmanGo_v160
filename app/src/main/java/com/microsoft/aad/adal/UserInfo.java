package com.microsoft.aad.adal;

import android.net.Uri;
import android.os.Bundle;
import com.microsoft.aad.adal.AuthenticationConstants;
import java.io.Serializable;
import java.util.Date;
import java.util.GregorianCalendar;

/* loaded from: classes3.dex */
public class UserInfo implements Serializable {
    private static final long serialVersionUID = 8790127561636702672L;
    private String mDisplayableId;
    private String mFamilyName;
    private String mGivenName;
    private String mIdentityProvider;
    private transient Uri mPasswordChangeUrl;
    private transient Date mPasswordExpiresOn;
    private String mUniqueId;

    public UserInfo() {
    }

    public UserInfo(String str) {
        this.mDisplayableId = str;
    }

    public UserInfo(String str, String str2, String str3, String str4, String str5) {
        this.mUniqueId = str;
        this.mGivenName = str2;
        this.mFamilyName = str3;
        this.mIdentityProvider = str4;
        this.mDisplayableId = str5;
    }

    public UserInfo(IdToken idToken) {
        this.mUniqueId = null;
        this.mDisplayableId = null;
        if (!StringExtensions.isNullOrBlank(idToken.getObjectId())) {
            this.mUniqueId = idToken.getObjectId();
        } else if (!StringExtensions.isNullOrBlank(idToken.getSubject())) {
            this.mUniqueId = idToken.getSubject();
        }
        if (!StringExtensions.isNullOrBlank(idToken.getUpn())) {
            this.mDisplayableId = idToken.getUpn();
        } else if (!StringExtensions.isNullOrBlank(idToken.getEmail())) {
            this.mDisplayableId = idToken.getEmail();
        }
        this.mGivenName = idToken.getGivenName();
        this.mFamilyName = idToken.getFamilyName();
        this.mIdentityProvider = idToken.getIdentityProvider();
        if (idToken.getPasswordExpiration() > 0) {
            GregorianCalendar gregorianCalendar = new GregorianCalendar();
            gregorianCalendar.add(13, (int) idToken.getPasswordExpiration());
            this.mPasswordExpiresOn = gregorianCalendar.getTime();
        }
        this.mPasswordChangeUrl = null;
        if (StringExtensions.isNullOrBlank(idToken.getPasswordChangeUrl())) {
            return;
        }
        this.mPasswordChangeUrl = Uri.parse(idToken.getPasswordChangeUrl());
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static UserInfo getUserInfoFromBrokerResult(Bundle bundle) {
        return new UserInfo(bundle.getString(AuthenticationConstants.Broker.ACCOUNT_USERINFO_USERID), bundle.getString(AuthenticationConstants.Broker.ACCOUNT_USERINFO_GIVEN_NAME), bundle.getString(AuthenticationConstants.Broker.ACCOUNT_USERINFO_FAMILY_NAME), bundle.getString(AuthenticationConstants.Broker.ACCOUNT_USERINFO_IDENTITY_PROVIDER), bundle.getString(AuthenticationConstants.Broker.ACCOUNT_USERINFO_USERID_DISPLAYABLE));
    }

    public String getUserId() {
        return this.mUniqueId;
    }

    public void setUserId(String str) {
        this.mUniqueId = str;
    }

    public String getGivenName() {
        return this.mGivenName;
    }

    public String getFamilyName() {
        return this.mFamilyName;
    }

    public String getIdentityProvider() {
        return this.mIdentityProvider;
    }

    public String getDisplayableId() {
        return this.mDisplayableId;
    }

    public void setDisplayableId(String str) {
        this.mDisplayableId = str;
    }

    public Uri getPasswordChangeUrl() {
        return this.mPasswordChangeUrl;
    }

    public Date getPasswordExpiresOn() {
        return Utility.getImmutableDateObject(this.mPasswordExpiresOn);
    }
}
