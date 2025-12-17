package com.microsoft.aad.adal;

import android.content.Intent;
import android.text.TextUtils;
import com.microsoft.aad.adal.AuthenticationConstants;
import com.microsoft.aad.adal.ChallengeResponseBuilder;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;

/* loaded from: classes3.dex */
public class WebviewHelper {
    private static final String TAG = "WebviewHelper";
    private final Oauth2 mOauth;
    private final AuthenticationRequest mRequest;
    private final Intent mRequestIntent;

    /* loaded from: classes3.dex */
    public static class PreKeyAuthInfo {
        private final HashMap<String, String> mHttpHeaders;
        private final String mLoadUrl;

        public PreKeyAuthInfo(HashMap<String, String> hashMap, String str) {
            this.mHttpHeaders = hashMap;
            this.mLoadUrl = str;
        }

        public HashMap<String, String> getHttpHeaders() {
            return this.mHttpHeaders;
        }

        public String getLoadUrl() {
            return this.mLoadUrl;
        }
    }

    public WebviewHelper(Intent intent) {
        this.mRequestIntent = intent;
        AuthenticationRequest authenticationRequestFromIntent = getAuthenticationRequestFromIntent(intent);
        this.mRequest = authenticationRequestFromIntent;
        this.mOauth = new Oauth2(authenticationRequestFromIntent);
    }

    private AuthenticationRequest getAuthenticationRequestFromIntent(Intent intent) {
        Serializable serializableExtra = intent.getSerializableExtra(AuthenticationConstants.Browser.REQUEST_MESSAGE);
        if (serializableExtra instanceof AuthenticationRequest) {
            return (AuthenticationRequest) serializableExtra;
        }
        return null;
    }

    public PreKeyAuthInfo getPreKeyAuthInfo(String str) throws UnsupportedEncodingException, AuthenticationException {
        ChallengeResponseBuilder.ChallengeResponse challengeResponseFromUri = new ChallengeResponseBuilder(new JWSBuilder()).getChallengeResponseFromUri(str);
        HashMap hashMap = new HashMap();
        hashMap.put("Authorization", challengeResponseFromUri.getAuthorizationHeaderValue());
        String submitUrl = challengeResponseFromUri.getSubmitUrl();
        HashMap<String, String> urlParameters = StringExtensions.getUrlParameters(challengeResponseFromUri.getSubmitUrl());
        Logger.m14612i(TAG, "Get submit url. ", "SubmitUrl:" + challengeResponseFromUri.getSubmitUrl());
        if (!urlParameters.containsKey(AuthenticationConstants.OAuth2.CLIENT_ID)) {
            submitUrl = submitUrl + "?" + this.mOauth.getAuthorizationEndpointQueryParameters();
        }
        return new PreKeyAuthInfo(hashMap, submitUrl);
    }

    public String getRedirectUrl() {
        return this.mRequest.getRedirectUri();
    }

    public Intent getResultIntent(String str) {
        Intent intent = this.mRequestIntent;
        if (intent != null) {
            AuthenticationRequest authenticationRequestFromIntent = getAuthenticationRequestFromIntent(intent);
            Intent intent2 = new Intent();
            intent2.putExtra(AuthenticationConstants.Browser.RESPONSE_FINAL_URL, str);
            intent2.putExtra(AuthenticationConstants.Browser.RESPONSE_REQUEST_INFO, authenticationRequestFromIntent);
            intent2.putExtra(AuthenticationConstants.Browser.REQUEST_ID, authenticationRequestFromIntent.getRequestId());
            return intent2;
        }
        throw new IllegalArgumentException("requestIntent is null");
    }

    public String getStartUrl() throws UnsupportedEncodingException {
        return this.mOauth.getCodeRequestUrl();
    }

    public void validateRequestIntent() {
        AuthenticationRequest authenticationRequest = this.mRequest;
        if (authenticationRequest == null) {
            Logger.m14614v(TAG, "Request item is null, so it returns to caller");
            throw new IllegalArgumentException("Request is null");
        } else if (TextUtils.isEmpty(authenticationRequest.getAuthority())) {
            throw new IllegalArgumentException("Authority is null");
        } else {
            if (TextUtils.isEmpty(this.mRequest.getResource())) {
                throw new IllegalArgumentException("Resource is null");
            }
            if (TextUtils.isEmpty(this.mRequest.getClientId())) {
                throw new IllegalArgumentException("ClientId is null");
            }
            if (TextUtils.isEmpty(this.mRequest.getRedirectUri())) {
                throw new IllegalArgumentException("RedirectUri is null");
            }
        }
    }
}
