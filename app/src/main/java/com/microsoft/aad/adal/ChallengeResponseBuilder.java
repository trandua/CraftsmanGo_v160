package com.microsoft.aad.adal;

import com.microsoft.aad.adal.AuthenticationConstants;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.security.interfaces.RSAPrivateKey;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/* loaded from: classes3.dex */
class ChallengeResponseBuilder {
    private static final String TAG = "ChallengeResponseBuilder";
    private final IJWSBuilder mJWSBuilder;

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes3.dex */
    public enum RequestField {
        Nonce,
        CertAuthorities,
        Version,
        SubmitUrl,
        Context,
        CertThumbprint
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes3.dex */
    public class ChallengeRequest {
        public List<String> mCertAuthorities;
        public String mContext = "";
        public String mNonce = "";
        public String mSubmitUrl = "";
        public String mThumbprint = "";
        public String mVersion = null;
        final ChallengeResponseBuilder this$0;

        ChallengeRequest(ChallengeResponseBuilder challengeResponseBuilder) {
            this.this$0 = challengeResponseBuilder;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes3.dex */
    public class ChallengeResponse {
        public String mAuthorizationHeaderValue;
        public String mSubmitUrl;
        final ChallengeResponseBuilder this$0;

        ChallengeResponse(ChallengeResponseBuilder challengeResponseBuilder) {
            this.this$0 = challengeResponseBuilder;
        }

        public String getAuthorizationHeaderValue() {
            return this.mAuthorizationHeaderValue;
        }

        public String getSubmitUrl() {
            return this.mSubmitUrl;
        }

        public void setAuthorizationHeaderValue(String str) {
            this.mAuthorizationHeaderValue = str;
        }

        public void setSubmitUrl(String str) {
            this.mSubmitUrl = str;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public ChallengeResponseBuilder(IJWSBuilder iJWSBuilder) {
        this.mJWSBuilder = iJWSBuilder;
    }

    private ChallengeRequest getChallengeRequest(String str) throws AuthenticationException {
        if (!StringExtensions.isNullOrBlank(str)) {
            ChallengeRequest challengeRequest = new ChallengeRequest(this);
            HashMap<String, String> urlParameters = StringExtensions.getUrlParameters(str);
            validateChallengeRequest(urlParameters, true);
            challengeRequest.mNonce = urlParameters.get(RequestField.Nonce.name());
            if (StringExtensions.isNullOrBlank(challengeRequest.mNonce)) {
                challengeRequest.mNonce = urlParameters.get(RequestField.Nonce.name().toLowerCase(Locale.US));
            }
            String str2 = urlParameters.get(RequestField.CertAuthorities.name());
            Logger.m14615v("ChallengeResponseBuilder:getChallengeRequest", "Get cert authorities. ", "Authorities: " + str2, null);
            challengeRequest.mCertAuthorities = StringExtensions.getStringTokens(str2, AuthenticationConstants.Broker.CHALLENGE_REQUEST_CERT_AUTH_DELIMETER);
            challengeRequest.mVersion = urlParameters.get(RequestField.Version.name());
            challengeRequest.mSubmitUrl = urlParameters.get(RequestField.SubmitUrl.name());
            challengeRequest.mContext = urlParameters.get(RequestField.Context.name());
            return challengeRequest;
        }
        throw new AuthenticationServerProtocolException("redirectUri");
    }

    private ChallengeRequest getChallengeRequestFromHeader(String str) throws UnsupportedEncodingException, AuthenticationException {
        if (StringExtensions.isNullOrBlank(str)) {
            throw new AuthenticationServerProtocolException("headerValue");
        }
        if (StringExtensions.hasPrefixInHeader(str, AuthenticationConstants.Broker.CHALLENGE_RESPONSE_TYPE)) {
            ChallengeRequest challengeRequest = new ChallengeRequest(this);
            String substring = str.substring(8);
            ArrayList<String> splitWithQuotes = StringExtensions.splitWithQuotes(substring, ',');
            HashMap hashMap = new HashMap();
            Iterator<String> it = splitWithQuotes.iterator();
            while (it.hasNext()) {
                ArrayList<String> splitWithQuotes2 = StringExtensions.splitWithQuotes(it.next(), '=');
                if (splitWithQuotes2.size() == 2 && !StringExtensions.isNullOrBlank(splitWithQuotes2.get(0)) && !StringExtensions.isNullOrBlank(splitWithQuotes2.get(1))) {
                    hashMap.put(StringExtensions.urlFormDecode(splitWithQuotes2.get(0)).trim(), StringExtensions.removeQuoteInHeaderValue(StringExtensions.urlFormDecode(splitWithQuotes2.get(1)).trim()));
                } else if (splitWithQuotes2.size() != 1 || StringExtensions.isNullOrBlank(splitWithQuotes2.get(0))) {
                    throw new AuthenticationException(ADALError.DEVICE_CERTIFICATE_REQUEST_INVALID, substring);
                } else {
                    hashMap.put(StringExtensions.urlFormDecode(splitWithQuotes2.get(0)).trim(), StringExtensions.urlFormDecode(""));
                }
            }
            validateChallengeRequest(hashMap, false);
            challengeRequest.mNonce = (String) hashMap.get(RequestField.Nonce.name());
            if (StringExtensions.isNullOrBlank(challengeRequest.mNonce)) {
                challengeRequest.mNonce = (String) hashMap.get(RequestField.Nonce.name().toLowerCase(Locale.US));
            }
            if (!isWorkplaceJoined()) {
                Logger.m14614v("ChallengeResponseBuilder:getChallengeRequestFromHeader", "Device is not workplace joined. ");
            } else if (!StringExtensions.isNullOrBlank((String) hashMap.get(RequestField.CertThumbprint.name()))) {
                Logger.m14614v("ChallengeResponseBuilder:getChallengeRequestFromHeader", "CertThumbprint exists in the device auth challenge.");
                challengeRequest.mThumbprint = (String) hashMap.get(RequestField.CertThumbprint.name());
            } else if (hashMap.containsKey(RequestField.CertAuthorities.name())) {
                Logger.m14614v("ChallengeResponseBuilder:getChallengeRequestFromHeader", "CertAuthorities exists in the device auth challenge.");
                challengeRequest.mCertAuthorities = StringExtensions.getStringTokens((String) hashMap.get(RequestField.CertAuthorities.name()), AuthenticationConstants.Broker.CHALLENGE_REQUEST_CERT_AUTH_DELIMETER);
            } else {
                throw new AuthenticationException(ADALError.DEVICE_CERTIFICATE_REQUEST_INVALID, "Both certThumbprint and certauthorities are not present");
            }
            challengeRequest.mVersion = (String) hashMap.get(RequestField.Version.name());
            challengeRequest.mContext = (String) hashMap.get(RequestField.Context.name());
            return challengeRequest;
        }
        throw new AuthenticationException(ADALError.DEVICE_CERTIFICATE_REQUEST_INVALID, str);
    }

    /* JADX WARN: Multi-variable type inference failed */
    private ChallengeResponse getDeviceCertResponse(ChallengeRequest challengeRequest) throws AuthenticationException {
        ChallengeResponse noDeviceCertResponse = getNoDeviceCertResponse(challengeRequest);
        noDeviceCertResponse.mSubmitUrl = challengeRequest.mSubmitUrl;
        Class<?> deviceCertificateProxy = AuthenticationSettings.INSTANCE.getDeviceCertificateProxy();
        if (deviceCertificateProxy != null) {
            IDeviceCertificate wPJAPIInstance = getWPJAPIInstance((Class<IDeviceCertificate>) deviceCertificateProxy);
            if (wPJAPIInstance.isValidIssuer(challengeRequest.mCertAuthorities) || (wPJAPIInstance.getThumbPrint() != null && wPJAPIInstance.getThumbPrint().equalsIgnoreCase(challengeRequest.mThumbprint))) {
                RSAPrivateKey rSAPrivateKey = wPJAPIInstance.getRSAPrivateKey();
                if (rSAPrivateKey != null) {
                    noDeviceCertResponse.mAuthorizationHeaderValue = String.format("%s AuthToken=\"%s\",Context=\"%s\",Version=\"%s\"", AuthenticationConstants.Broker.CHALLENGE_RESPONSE_TYPE, this.mJWSBuilder.generateSignedJWT(challengeRequest.mNonce, challengeRequest.mSubmitUrl, rSAPrivateKey, wPJAPIInstance.getRSAPublicKey(), wPJAPIInstance.getCertificate()), challengeRequest.mContext, challengeRequest.mVersion);
                    Logger.m14615v(TAG, "Receive challenge response. ", "Challenge response:" + noDeviceCertResponse.mAuthorizationHeaderValue, null);
                } else {
                    throw new AuthenticationException(ADALError.KEY_CHAIN_PRIVATE_KEY_EXCEPTION);
                }
            }
        }
        return noDeviceCertResponse;
    }

    private ChallengeResponse getNoDeviceCertResponse(ChallengeRequest challengeRequest) {
        ChallengeResponse challengeResponse = new ChallengeResponse(this);
        challengeResponse.mSubmitUrl = challengeRequest.mSubmitUrl;
        challengeResponse.mAuthorizationHeaderValue = String.format("%s Context=\"%s\",Version=\"%s\"", AuthenticationConstants.Broker.CHALLENGE_RESPONSE_TYPE, challengeRequest.mContext, challengeRequest.mVersion);
        return challengeResponse;
    }

    private IDeviceCertificate getWPJAPIInstance(Class<IDeviceCertificate> cls) throws AuthenticationException {
        try {
            Object[] objArr = null;
            return cls.getDeclaredConstructor(new Class[0]).newInstance(null);
        } catch (IllegalAccessException | IllegalArgumentException | InstantiationException | NoSuchMethodException | InvocationTargetException e) {
            throw new AuthenticationException(ADALError.DEVICE_CERTIFICATE_API_EXCEPTION, "WPJ Api constructor is not defined", e);
        }
    }

    private boolean isWorkplaceJoined() {
        return AuthenticationSettings.INSTANCE.getDeviceCertificateProxy() != null;
    }

    private void validateChallengeRequest(Map<String, String> map, boolean z) throws AuthenticationException {
        if (!map.containsKey(RequestField.Nonce.name()) && !map.containsKey(RequestField.Nonce.name().toLowerCase(Locale.US))) {
            throw new AuthenticationException(ADALError.DEVICE_CERTIFICATE_REQUEST_INVALID, "Nonce");
        }
        if (!map.containsKey(RequestField.Version.name())) {
            throw new AuthenticationException(ADALError.DEVICE_CERTIFICATE_REQUEST_INVALID, "Version");
        }
        if (z && !map.containsKey(RequestField.SubmitUrl.name())) {
            throw new AuthenticationException(ADALError.DEVICE_CERTIFICATE_REQUEST_INVALID, "SubmitUrl");
        }
        if (!map.containsKey(RequestField.Context.name())) {
            throw new AuthenticationException(ADALError.DEVICE_CERTIFICATE_REQUEST_INVALID, AuthenticationConstants.Broker.CHALLENGE_RESPONSE_CONTEXT);
        }
        if (z && !map.containsKey(RequestField.CertAuthorities.name())) {
            throw new AuthenticationException(ADALError.DEVICE_CERTIFICATE_REQUEST_INVALID, "CertAuthorities");
        }
    }

    public ChallengeResponse getChallengeResponseFromHeader(String str, String str2) throws UnsupportedEncodingException, AuthenticationException {
        ChallengeRequest challengeRequestFromHeader = getChallengeRequestFromHeader(str);
        challengeRequestFromHeader.mSubmitUrl = str2;
        return getDeviceCertResponse(challengeRequestFromHeader);
    }

    public ChallengeResponse getChallengeResponseFromUri(String str) throws AuthenticationException {
        return getDeviceCertResponse(getChallengeRequest(str));
    }
}
