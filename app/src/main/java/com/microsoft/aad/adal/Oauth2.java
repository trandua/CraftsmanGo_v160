package com.microsoft.aad.adal;

import android.net.Uri;
import android.os.Build;
import android.text.TextUtils;
import android.util.Base64;
//import com.appboy.Constants;
import com.microsoft.aad.adal.AuthenticationConstants;
import com.microsoft.aad.adal.TelemetryUtils;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.jose4j.jwk.RsaJsonWebKey;
import org.json.JSONException;
import org.json.JSONObject;

/* loaded from: classes3.dex */
class Oauth2 {
    private static final String DEFAULT_AUTHORIZE_ENDPOINT = "/oauth2/authorize";
    private static final String DEFAULT_TOKEN_ENDPOINT = "/oauth2/token";
    private static final int DELAY_TIME_PERIOD = 1000;
    private static final String HTTPS_PROTOCOL_STRING = "https";
    private static final int MAX_RESILIENCY_ERROR_CODE = 599;
    private static final String TAG = "Oauth";
    private IJWSBuilder mJWSBuilder;
    private AuthenticationRequest mRequest;
    private boolean mRetryOnce;
    private String mTokenEndpoint;
    private IWebRequestHandler mWebRequestHandler;

    /* JADX INFO: Access modifiers changed from: package-private */
    public Oauth2(AuthenticationRequest authenticationRequest) {
        new JWSBuilder();
        this.mRetryOnce = true;
        this.mRequest = authenticationRequest;
        this.mWebRequestHandler = null;
        this.mJWSBuilder = null;
        setTokenEndpoint(this.mRequest.getAuthority() + DEFAULT_TOKEN_ENDPOINT);
    }

    public Oauth2(AuthenticationRequest authenticationRequest, IWebRequestHandler iWebRequestHandler) {
        new JWSBuilder();
        this.mRetryOnce = true;
        this.mRequest = authenticationRequest;
        this.mWebRequestHandler = iWebRequestHandler;
        this.mJWSBuilder = null;
        setTokenEndpoint(this.mRequest.getAuthority() + DEFAULT_TOKEN_ENDPOINT);
    }

    public Oauth2(AuthenticationRequest authenticationRequest, IWebRequestHandler iWebRequestHandler, IJWSBuilder iJWSBuilder) {
        new JWSBuilder();
        this.mRetryOnce = true;
        this.mRequest = authenticationRequest;
        this.mWebRequestHandler = iWebRequestHandler;
        this.mJWSBuilder = iJWSBuilder;
        setTokenEndpoint(this.mRequest.getAuthority() + DEFAULT_TOKEN_ENDPOINT);
    }

    public String getAuthorizationEndpoint() {
        return this.mRequest.getAuthority() + DEFAULT_AUTHORIZE_ENDPOINT;
    }

    public String getTokenEndpoint() {
        return this.mTokenEndpoint;
    }

    public String getAuthorizationEndpointQueryParameters() throws UnsupportedEncodingException {
        Uri.Builder builder = new Uri.Builder();
        builder.appendQueryParameter(AuthenticationConstants.OAuth2.RESPONSE_TYPE, AuthenticationConstants.OAuth2.CODE).appendQueryParameter(AuthenticationConstants.OAuth2.CLIENT_ID, URLEncoder.encode(this.mRequest.getClientId(), "UTF-8")).appendQueryParameter(AuthenticationConstants.AAD.RESOURCE, URLEncoder.encode(this.mRequest.getResource(), "UTF-8")).appendQueryParameter(AuthenticationConstants.OAuth2.REDIRECT_URI, URLEncoder.encode(this.mRequest.getRedirectUri(), "UTF-8")).appendQueryParameter("state", encodeProtocolState());
        if (!StringExtensions.isNullOrBlank(this.mRequest.getLoginHint())) {
            builder.appendQueryParameter(AuthenticationConstants.AAD.LOGIN_HINT, URLEncoder.encode(this.mRequest.getLoginHint(), "UTF-8"));
        }
        builder.appendQueryParameter(AuthenticationConstants.AAD.ADAL_ID_PLATFORM, AuthenticationConstants.AAD.ADAL_ID_PLATFORM_VALUE).appendQueryParameter(AuthenticationConstants.AAD.ADAL_ID_VERSION, URLEncoder.encode(AuthenticationContext.getVersionName(), "UTF-8")).appendQueryParameter(AuthenticationConstants.AAD.ADAL_ID_OS_VER, URLEncoder.encode(String.valueOf(Build.VERSION.SDK_INT), "UTF-8")).appendQueryParameter(AuthenticationConstants.AAD.ADAL_ID_DM, URLEncoder.encode(Build.MODEL, "UTF-8"));
        if (this.mRequest.getCorrelationId() != null) {
            builder.appendQueryParameter(AuthenticationConstants.AAD.CLIENT_REQUEST_ID, URLEncoder.encode(this.mRequest.getCorrelationId().toString(), "UTF-8"));
        }
        if (this.mRequest.getPrompt() == PromptBehavior.Always) {
            builder.appendQueryParameter(AuthenticationConstants.AAD.QUERY_PROMPT, URLEncoder.encode("login", "UTF-8"));
        } else if (this.mRequest.getPrompt() == PromptBehavior.REFRESH_SESSION) {
            builder.appendQueryParameter(AuthenticationConstants.AAD.QUERY_PROMPT, URLEncoder.encode(AuthenticationConstants.AAD.QUERY_PROMPT_REFRESH_SESSION_VALUE, "UTF-8"));
        }
        String extraQueryParamsAuthentication = this.mRequest.getExtraQueryParamsAuthentication();
        if (StringExtensions.isNullOrBlank(extraQueryParamsAuthentication) || !extraQueryParamsAuthentication.contains("haschrome")) {
            builder.appendQueryParameter("haschrome", "1");
        }
        if (!StringExtensions.isNullOrBlank(this.mRequest.getClaimsChallenge())) {
            builder.appendQueryParameter("claims", this.mRequest.getClaimsChallenge());
        }
        String query = builder.build().getQuery();
        if (StringExtensions.isNullOrBlank(extraQueryParamsAuthentication)) {
            return query;
        }
        if (!extraQueryParamsAuthentication.startsWith("&")) {
            extraQueryParamsAuthentication = "&" + extraQueryParamsAuthentication;
        }
        return query + extraQueryParamsAuthentication;
    }

    public String getCodeRequestUrl() throws UnsupportedEncodingException {
        return String.format("%s?%s", getAuthorizationEndpoint(), getAuthorizationEndpointQueryParameters());
    }

    public String buildTokenRequestMessage(String str) throws UnsupportedEncodingException {
        Logger.m14614v(TAG, "Building request message for redeeming token with auth code.");
        return String.format("%s=%s&%s=%s&%s=%s&%s=%s", AuthenticationConstants.OAuth2.GRANT_TYPE, StringExtensions.urlFormEncode(AuthenticationConstants.OAuth2.AUTHORIZATION_CODE), AuthenticationConstants.OAuth2.CODE, StringExtensions.urlFormEncode(str), AuthenticationConstants.OAuth2.CLIENT_ID, StringExtensions.urlFormEncode(this.mRequest.getClientId()), AuthenticationConstants.OAuth2.REDIRECT_URI, StringExtensions.urlFormEncode(this.mRequest.getRedirectUri()));
    }

    public String buildRefreshTokenRequestMessage(String str) throws UnsupportedEncodingException {
        Logger.m14614v(TAG, "Building request message for redeeming token with refresh token.");
        String format = String.format("%s=%s&%s=%s&%s=%s", AuthenticationConstants.OAuth2.GRANT_TYPE, StringExtensions.urlFormEncode(AuthenticationConstants.OAuth2.REFRESH_TOKEN), AuthenticationConstants.OAuth2.REFRESH_TOKEN, StringExtensions.urlFormEncode(str), AuthenticationConstants.OAuth2.CLIENT_ID, StringExtensions.urlFormEncode(this.mRequest.getClientId()));
        return StringExtensions.isNullOrBlank(this.mRequest.getResource()) ? format : String.format("%s&%s=%s", format, AuthenticationConstants.AAD.RESOURCE, StringExtensions.urlFormEncode(this.mRequest.getResource()));
    }

    public AuthenticationResult processUIResponseParams(Map<String, String> map) throws AuthenticationException {
        UserInfo userInfo;
        String str;
        String str2;
        String str3;
        UserInfo userInfo2;
        if (map.containsKey(AuthenticationConstants.OAuth2.ERROR)) {
            String str4 = map.get(AuthenticationConstants.AAD.CORRELATION_ID);
            if (!StringExtensions.isNullOrBlank(str4)) {
                try {
                    Logger.setCorrelationId(UUID.fromString(str4));
                } catch (IllegalArgumentException unused) {
                    Logger.m14614v(TAG, "CorrelationId is malformed: " + str4);
                }
            }
            Logger.m14612i(TAG, "OAuth2 error:" + map.get(AuthenticationConstants.OAuth2.ERROR), " Description:" + map.get(AuthenticationConstants.OAuth2.ERROR_DESCRIPTION));
            return new AuthenticationResult(map.get(AuthenticationConstants.OAuth2.ERROR), map.get(AuthenticationConstants.OAuth2.ERROR_DESCRIPTION), map.get(AuthenticationConstants.OAuth2.ERROR_CODES));
        } else if (map.containsKey(AuthenticationConstants.OAuth2.CODE)) {
            AuthenticationResult authenticationResult = new AuthenticationResult(map.get(AuthenticationConstants.OAuth2.CODE));
            String str5 = map.get("cloud_instance_host_name");
            if (StringExtensions.isNullOrBlank(str5)) {
                return authenticationResult;
            }
            String uri = new Uri.Builder().scheme("https").authority(str5).path(StringExtensions.getUrl(this.mRequest.getAuthority()).getPath()).build().toString();
            setTokenEndpoint(uri + DEFAULT_TOKEN_ENDPOINT);
            authenticationResult.setAuthority(uri);
            return authenticationResult;
        } else {
            if (map.containsKey(AuthenticationConstants.OAuth2.ACCESS_TOKEN)) {
                String str6 = map.get(AuthenticationConstants.OAuth2.EXPIRES_IN);
                GregorianCalendar gregorianCalendar = new GregorianCalendar();
                int i = AuthenticationConstants.DEFAULT_EXPIRATION_TIME_SEC;
                gregorianCalendar.add(13, (str6 == null || str6.isEmpty()) ? AuthenticationConstants.DEFAULT_EXPIRATION_TIME_SEC : Integer.parseInt(str6));
                String str7 = map.get(AuthenticationConstants.OAuth2.REFRESH_TOKEN);
                boolean z = map.containsKey(AuthenticationConstants.AAD.RESOURCE) && !StringExtensions.isNullOrBlank(str7);
                if (map.containsKey("id_token")) {
                    String str8 = map.get("id_token");
                    if (!StringExtensions.isNullOrBlank(str8)) {
                        Logger.m14614v(TAG, "Id token was returned, parsing id token.");
                        IdToken idToken = new IdToken(str8);
                        str3 = idToken.getTenantId();
                        userInfo2 = new UserInfo(idToken);
                    } else {
                        Logger.m14614v(TAG, "IdToken was not returned from token request.");
                        str3 = null;
                        userInfo2 = null;
                    }
                    str2 = str8;
                    str = str3;
                    userInfo = userInfo2;
                } else {
                    userInfo = null;
                    str = null;
                    str2 = null;
                }
                String str9 = map.containsKey("foci") ? map.get("foci") : null;
                AuthenticationResult authenticationResult2 = new AuthenticationResult(map.get(AuthenticationConstants.OAuth2.ACCESS_TOKEN), str7, gregorianCalendar.getTime(), z, userInfo, str, str2, null);
                if (map.containsKey("ext_expires_in")) {
                    String str10 = map.get("ext_expires_in");
                    GregorianCalendar gregorianCalendar2 = new GregorianCalendar();
                    if (!StringExtensions.isNullOrBlank(str10)) {
                        i = Integer.parseInt(str10);
                    }
                    gregorianCalendar2.add(13, i);
                    authenticationResult2.setExtendedExpiresOn(gregorianCalendar2.getTime());
                }
                authenticationResult2.setFamilyClientId(str9);
                return authenticationResult2;
            }
            return null;
        }
    }

    private static void extractJsonObjects(Map<String, String> map, String str) throws JSONException {
        JSONObject jSONObject = new JSONObject(str);
        Iterator<String> keys = jSONObject.keys();
        while (keys.hasNext()) {
            String next = keys.next();
            map.put(next, jSONObject.getString(next));
        }
    }

    public AuthenticationResult refreshToken(String str) throws IOException, AuthenticationException {
        if (this.mWebRequestHandler != null) {
            try {
                String buildRefreshTokenRequestMessage = buildRefreshTokenRequestMessage(str);
                Map<String, String> requestHeaders = getRequestHeaders();
                requestHeaders.put(AuthenticationConstants.Broker.CHALLENGE_TLS_INCAPABLE, AuthenticationConstants.Broker.CHALLENGE_TLS_INCAPABLE_VERSION);
                Logger.m14614v(TAG, "Sending request to redeem token with refresh token.");
                return postMessage(buildRefreshTokenRequestMessage, requestHeaders);
            } catch (UnsupportedEncodingException e) {
                Logger.m14610e(TAG, ADALError.ENCODING_IS_NOT_SUPPORTED.getDescription(), e.getMessage(), ADALError.ENCODING_IS_NOT_SUPPORTED, e);
                return null;
            }
        }
        Logger.m14614v(TAG, "Web request is not set correctly.");
        throw new IllegalArgumentException("webRequestHandler is null.");
    }

    public AuthenticationResult getToken(String str) throws IOException, AuthenticationException {
        if (!StringExtensions.isNullOrBlank(str)) {
            HashMap<String, String> urlParameters = StringExtensions.getUrlParameters(str);
            String decodeProtocolState = decodeProtocolState(urlParameters.get("state"));
            if (!StringExtensions.isNullOrBlank(decodeProtocolState)) {
                Uri parse = Uri.parse("http://state/path?" + decodeProtocolState);
                String queryParameter = parse.getQueryParameter("a");
                String queryParameter2 = parse.getQueryParameter(RsaJsonWebKey.PRIME_FACTOR_OTHER_MEMBER_NAME);
                if (StringExtensions.isNullOrBlank(queryParameter) || StringExtensions.isNullOrBlank(queryParameter2) || !queryParameter2.equalsIgnoreCase(this.mRequest.getResource())) {
                    throw new AuthenticationException(ADALError.AUTH_FAILED_BAD_STATE);
                }
                AuthenticationResult processUIResponseParams = processUIResponseParams(urlParameters);
                if (processUIResponseParams == null || processUIResponseParams.getCode() == null || processUIResponseParams.getCode().isEmpty()) {
                    return processUIResponseParams;
                }
                AuthenticationResult tokenForCode = getTokenForCode(processUIResponseParams.getCode());
                if (!StringExtensions.isNullOrBlank(processUIResponseParams.getAuthority())) {
                    tokenForCode.setAuthority(processUIResponseParams.getAuthority());
                } else {
                    tokenForCode.setAuthority(this.mRequest.getAuthority());
                }
                return tokenForCode;
            }
            throw new AuthenticationException(ADALError.AUTH_FAILED_NO_STATE);
        }
        throw new IllegalArgumentException("authorizationUrl");
    }

    public AuthenticationResult getTokenForCode(String str) throws IOException, AuthenticationException {
        if (this.mWebRequestHandler != null) {
            try {
                String buildTokenRequestMessage = buildTokenRequestMessage(str);
                Map<String, String> requestHeaders = getRequestHeaders();
                Logger.m14614v("Oauth:getTokenForCode", "Sending request to redeem token with auth code.");
                return postMessage(buildTokenRequestMessage, requestHeaders);
            } catch (UnsupportedEncodingException e) {
                Logger.m14610e("Oauth:getTokenForCode", ADALError.ENCODING_IS_NOT_SUPPORTED.getDescription(), e.getMessage(), ADALError.ENCODING_IS_NOT_SUPPORTED, e);
                return null;
            }
        }
        throw new IllegalArgumentException("webRequestHandler");
    }

    private AuthenticationResult postMessage(String str, Map<String, String> map) throws IOException, AuthenticationException {
        AuthenticationResult authenticationResult;
        String body;
        HttpEvent startHttpEvent = startHttpEvent();
        URL url = StringExtensions.getUrl(getTokenEndpoint());
        if (url != null) {
            startHttpEvent.setHttpPath(url);
            try {
                this.mWebRequestHandler.setRequestCorrelationId(this.mRequest.getCorrelationId());
                ClientMetrics.INSTANCE.beginClientMetricsRecord(url, this.mRequest.getCorrelationId(), map);
                HttpWebResponse sendPost = this.mWebRequestHandler.sendPost(url, map, str.getBytes("UTF-8"), "application/x-www-form-urlencoded");
                startHttpEvent.setResponseCode(sendPost.getStatusCode());
                startHttpEvent.setCorrelationId(this.mRequest.getCorrelationId().toString());
                stopHttpEvent(startHttpEvent);
                if (sendPost.getStatusCode() == 401) {
                    if (sendPost.getResponseHeaders() != null && sendPost.getResponseHeaders().containsKey("WWW-Authenticate")) {
                        String str2 = sendPost.getResponseHeaders().get("WWW-Authenticate").get(0);
                        Logger.m14612i("Oauth:postMessage", "Device certificate challenge request. ", "Challenge header: " + str2);
                        if (StringExtensions.isNullOrBlank(str2)) {
                            throw new AuthenticationException(ADALError.DEVICE_CERTIFICATE_REQUEST_INVALID, "Challenge header is empty", sendPost);
                        }
                        if (StringExtensions.hasPrefixInHeader(str2, AuthenticationConstants.Broker.CHALLENGE_RESPONSE_TYPE)) {
                            HttpEvent startHttpEvent2 = startHttpEvent();
                            startHttpEvent2.setHttpPath(url);
                            Logger.m14614v("Oauth:postMessage", "Received pkeyAuth device challenge.");
                            ChallengeResponseBuilder challengeResponseBuilder = new ChallengeResponseBuilder(this.mJWSBuilder);
                            Logger.m14614v("Oauth:postMessage", "Processing device challenge.");
                            map.put("Authorization", challengeResponseBuilder.getChallengeResponseFromHeader(str2, url.toString()).getAuthorizationHeaderValue());
                            Logger.m14614v("Oauth:postMessage", "Sending request with challenge response.");
                            sendPost = this.mWebRequestHandler.sendPost(url, map, str.getBytes("UTF-8"), "application/x-www-form-urlencoded");
                            startHttpEvent2.setResponseCode(sendPost.getStatusCode());
                            startHttpEvent2.setCorrelationId(this.mRequest.getCorrelationId().toString());
                            stopHttpEvent(startHttpEvent2);
                        }
                    }
                    Logger.m14614v("Oauth:postMessage", "401 http status code is returned without authorization header.");
                }
                boolean isEmpty = TextUtils.isEmpty(sendPost.getBody());
                if (isEmpty) {
                    authenticationResult = null;
                } else {
                    Logger.m14614v("Oauth:postMessage", "Token request does not have exception.");
                    try {
                        authenticationResult = processTokenResponse(sendPost, startHttpEvent);
                        ClientMetrics.INSTANCE.setLastError(null);
                    } catch (ServerRespondingWithRetryableException e) {
                        AuthenticationResult retry = retry(str, map);
                        if (retry != null) {
                            ClientMetrics.INSTANCE.endClientMetricsRecord("token", this.mRequest.getCorrelationId());
                            return retry;
                        } else if (this.mRequest.getIsExtendedLifetimeEnabled()) {
                            Logger.m14614v("Oauth:postMessage", "WebResponse is not a success due to: " + sendPost.getStatusCode());
                            throw e;
                        } else {
                            Logger.m14614v("Oauth:postMessage", "WebResponse is not a success due to: " + sendPost.getStatusCode());
                            throw new AuthenticationException(ADALError.SERVER_ERROR, "WebResponse is not a success due to: " + sendPost.getStatusCode(), sendPost);
                        }
                    }
                }
                if (authenticationResult == null) {
                    if (isEmpty) {
                        body = "Status code:" + sendPost.getStatusCode();
                    } else {
                        body = sendPost.getBody();
                    }
                    Logger.m14614v("Oauth:postMessage", ADALError.SERVER_ERROR.getDescription());
                    throw new AuthenticationException(ADALError.SERVER_ERROR, body, sendPost);
                }
                ClientMetrics.INSTANCE.setLastErrorCodes(authenticationResult.getErrorCodes());
                ClientMetrics.INSTANCE.endClientMetricsRecord("token", this.mRequest.getCorrelationId());
                return authenticationResult;
            } catch (UnsupportedEncodingException e2) {
                ClientMetrics.INSTANCE.setLastError(null);
                Logger.m14610e("Oauth:postMessage", ADALError.ENCODING_IS_NOT_SUPPORTED.getDescription(), e2.getMessage(), ADALError.ENCODING_IS_NOT_SUPPORTED, e2);
                throw e2;
            } catch (SocketTimeoutException e3) {
                AuthenticationResult retry2 = retry(str, map);
                if (retry2 != null) {
                    ClientMetrics.INSTANCE.endClientMetricsRecord("token", this.mRequest.getCorrelationId());
                    return retry2;
                }
                ClientMetrics.INSTANCE.setLastError(null);
                if (this.mRequest.getIsExtendedLifetimeEnabled()) {
                    Logger.m14610e("Oauth:postMessage", ADALError.SERVER_ERROR.getDescription(), e3.getMessage(), ADALError.SERVER_ERROR, e3);
                    throw new ServerRespondingWithRetryableException(e3.getMessage(), e3);
                }
                Logger.m14610e("Oauth:postMessage", ADALError.SERVER_ERROR.getDescription(), e3.getMessage(), ADALError.SERVER_ERROR, e3);
                throw e3;
            } catch (IOException e4) {
                ClientMetrics.INSTANCE.setLastError(null);
                Logger.m14610e("Oauth:postMessage", ADALError.SERVER_ERROR.getDescription(), e4.getMessage(), ADALError.SERVER_ERROR, e4);
                throw e4;
            } catch (Throwable th) {
                ClientMetrics.INSTANCE.endClientMetricsRecord("token", this.mRequest.getCorrelationId());
                throw th;
            }
        }
        stopHttpEvent(startHttpEvent);
        throw new AuthenticationException(ADALError.DEVELOPER_AUTHORITY_IS_NOT_VALID_URL);
    }

    private AuthenticationResult retry(String str, Map<String, String> map) throws IOException, AuthenticationException {
        if (this.mRetryOnce) {
            this.mRetryOnce = false;
            try {
                Thread.sleep(1000L);
            } catch (InterruptedException unused) {
                Logger.m14614v("Oauth:retry", "The thread is interrupted while it is sleeping. ");
            }
            Logger.m14614v("Oauth:retry", "Try again...");
            return postMessage(str, map);
        }
        return null;
    }

    public static String decodeProtocolState(String str) throws UnsupportedEncodingException {
        if (StringExtensions.isNullOrBlank(str)) {
            return null;
        }
        return new String(Base64.decode(str, 9), "UTF-8");
    }

    public String encodeProtocolState() throws UnsupportedEncodingException {
        return Base64.encodeToString(String.format("a=%s&r=%s", this.mRequest.getAuthority(), this.mRequest.getResource()).getBytes("UTF-8"), 9);
    }

    private Map<String, String> getRequestHeaders() {
        HashMap hashMap = new HashMap();
        hashMap.put(WebRequestHandler.HEADER_ACCEPT, WebRequestHandler.HEADER_ACCEPT_JSON);
        return hashMap;
    }

    private AuthenticationResult processTokenResponse(HttpWebResponse httpWebResponse, HttpEvent httpEvent) throws AuthenticationException {
        String str;
        TelemetryUtils.CliTelemInfo parseXMsCliTelemHeader;
        List<String> list;
        List<String> list2;
        String str2 = null;
        if (httpWebResponse.getResponseHeaders() != null) {
            str = (!httpWebResponse.getResponseHeaders().containsKey(AuthenticationConstants.AAD.CLIENT_REQUEST_ID) || (list2 = httpWebResponse.getResponseHeaders().get(AuthenticationConstants.AAD.CLIENT_REQUEST_ID)) == null || list2.size() <= 0) ? null : list2.get(0);
            if (httpWebResponse.getResponseHeaders().containsKey(AuthenticationConstants.AAD.REQUEST_ID_HEADER) && (list = httpWebResponse.getResponseHeaders().get(AuthenticationConstants.AAD.REQUEST_ID_HEADER)) != null && list.size() > 0) {
                Logger.m14614v("Oauth:processTokenResponse", "Set request id header. x-ms-request-id: " + list.get(0));
                httpEvent.setRequestIdHeader(list.get(0));
            }
            if (httpWebResponse.getResponseHeaders().get("x-ms-clitelem") != null && !httpWebResponse.getResponseHeaders().get("x-ms-clitelem").isEmpty() && (parseXMsCliTelemHeader = TelemetryUtils.parseXMsCliTelemHeader(httpWebResponse.getResponseHeaders().get("x-ms-clitelem").get(0))) != null) {
                httpEvent.setXMsCliTelemData(parseXMsCliTelemHeader);
                str2 = parseXMsCliTelemHeader.getSpeRing();
            }
        } else {
            str = null;
        }
        int statusCode = httpWebResponse.getStatusCode();
        if (statusCode != 200 && statusCode != 400 && statusCode != 401) {
            if (statusCode < 500 || statusCode > MAX_RESILIENCY_ERROR_CODE) {
                throw new AuthenticationException(ADALError.SERVER_ERROR, "Unexpected server response " + statusCode + " " + httpWebResponse.getBody(), httpWebResponse);
            }
            throw new ServerRespondingWithRetryableException("Server Error " + statusCode + " " + httpWebResponse.getBody(), httpWebResponse);
        }
        try {
            AuthenticationResult parseJsonResponse = parseJsonResponse(httpWebResponse.getBody());
            if (parseJsonResponse != null) {
                if (parseJsonResponse.getErrorCode() != null) {
                    parseJsonResponse.setHttpResponse(httpWebResponse);
                }
                TelemetryUtils.CliTelemInfo cliTelemInfo = new TelemetryUtils.CliTelemInfo();
                cliTelemInfo.setSpeRing(str2);
                parseJsonResponse.setCliTelemInfo(cliTelemInfo);
                httpEvent.setOauthErrorCode(parseJsonResponse.getErrorCode());
            }
            if (str != null && !str.isEmpty()) {
                try {
                    if (!UUID.fromString(str).equals(this.mRequest.getCorrelationId())) {
                        Logger.m14617w("Oauth:processTokenResponse", "CorrelationId is not matching", "", ADALError.CORRELATION_ID_NOT_MATCHING_REQUEST_RESPONSE);
                    }
                    Logger.m14614v("Oauth:processTokenResponse", "Response correlationId:" + str);
                } catch (IllegalArgumentException e) {
                    Logger.m14610e("Oauth:processTokenResponse", "Wrong format of the correlation ID:" + str, "", ADALError.CORRELATION_ID_FORMAT, e);
                }
            }
            return parseJsonResponse;
        } catch (JSONException e2) {
            throw new AuthenticationException(ADALError.SERVER_INVALID_JSON_RESPONSE, "Can't parse server response. " + httpWebResponse.getBody(), httpWebResponse, e2);
        }
    }

    private AuthenticationResult parseJsonResponse(String str) throws JSONException, AuthenticationException {
        HashMap hashMap = new HashMap();
        extractJsonObjects(hashMap, str);
        return processUIResponseParams(hashMap);
    }

    private HttpEvent startHttpEvent() {
        HttpEvent httpEvent = new HttpEvent("Microsoft.ADAL.http_event");
        httpEvent.setRequestId(this.mRequest.getTelemetryRequestId());
        httpEvent.setMethod("Microsoft.ADAL.post");
        Telemetry.getInstance().startEvent(this.mRequest.getTelemetryRequestId(), "Microsoft.ADAL.http_event");
        return httpEvent;
    }

    private void stopHttpEvent(HttpEvent httpEvent) {
        Telemetry.getInstance().stopEvent(this.mRequest.getTelemetryRequestId(), httpEvent, "Microsoft.ADAL.http_event");
    }

    private void setTokenEndpoint(String str) {
        this.mTokenEndpoint = str;
    }
}
