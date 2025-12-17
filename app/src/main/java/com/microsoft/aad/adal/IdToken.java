package com.microsoft.aad.adal;

import android.util.Base64;
import java.io.UnsupportedEncodingException;
import java.util.Map;
import org.jose4j.jwt.ReservedClaimNames;
import org.json.JSONException;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes3.dex */
public class IdToken {
    private static final String TAG = "IdToken";
    private String mEmail;
    private String mFamilyName;
    private String mGivenName;
    private String mIdentityProvider;
    private String mObjectId;
    private String mPasswordChangeUrl;
    private long mPasswordExpiration;
    private String mSubject;
    private String mTenantId;
    private String mUpn;

    public IdToken(String str) throws AuthenticationException {
        Map<String, String> parseJWT = parseJWT(str);
        if (parseJWT == null || parseJWT.isEmpty()) {
            return;
        }
        this.mSubject = parseJWT.get(ReservedClaimNames.SUBJECT);
        this.mTenantId = parseJWT.get("tid");
        this.mUpn = parseJWT.get("upn");
        this.mEmail = parseJWT.get("email");
        this.mGivenName = parseJWT.get("name");
        this.mFamilyName = parseJWT.get("family_name");
        this.mIdentityProvider = parseJWT.get("idp");
        this.mObjectId = parseJWT.get("oid");
        String str2 = parseJWT.get("pwd_exp");
        if (!StringExtensions.isNullOrBlank(str2)) {
            this.mPasswordExpiration = Long.parseLong(str2);
        }
        this.mPasswordChangeUrl = parseJWT.get("pwd_url");
    }

    private String extractJWTBody(String str) throws AuthenticationException {
        int indexOf = str.indexOf(46);
        int i = indexOf + 1;
        int indexOf2 = str.indexOf(46, i);
        if (str.indexOf(46, indexOf2 + 1) == -1 && indexOf > 0 && indexOf2 > 0) {
            return str.substring(i, indexOf2);
        }
        throw new AuthenticationException(ADALError.IDTOKEN_PARSING_FAILURE, "Failed to extract the ClientID");
    }

    private Map<String, String> parseJWT(String str) throws AuthenticationException {
        try {
            return HashMapExtensions.jsonStringAsMap(new String(Base64.decode(extractJWTBody(str), 8), "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            Logger.m14610e("IdToken:parseJWT", "The encoding is not supported.", "", ADALError.ENCODING_IS_NOT_SUPPORTED, e);
            throw new AuthenticationException(ADALError.ENCODING_IS_NOT_SUPPORTED, e.getMessage(), e);
        } catch (JSONException e2) {
            Logger.m14610e("IdToken:parseJWT", "Failed to parse the decoded body into JsonObject.", "", ADALError.JSON_PARSE_ERROR, e2);
            throw new AuthenticationException(ADALError.JSON_PARSE_ERROR, e2.getMessage(), e2);
        }
    }

    public String getEmail() {
        return this.mEmail;
    }

    public String getFamilyName() {
        return this.mFamilyName;
    }

    public String getGivenName() {
        return this.mGivenName;
    }

    public String getIdentityProvider() {
        return this.mIdentityProvider;
    }

    public String getObjectId() {
        return this.mObjectId;
    }

    public String getPasswordChangeUrl() {
        return this.mPasswordChangeUrl;
    }

    public long getPasswordExpiration() {
        return this.mPasswordExpiration;
    }

    public String getSubject() {
        return this.mSubject;
    }

    public String getTenantId() {
        return this.mTenantId;
    }

    public String getUpn() {
        return this.mUpn;
    }
}
