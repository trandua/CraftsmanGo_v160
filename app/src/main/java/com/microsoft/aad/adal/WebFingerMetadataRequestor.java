package com.microsoft.aad.adal;

import com.google.gson.JsonSyntaxException;
import com.microsoft.aad.adal.AuthenticationConstants;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

/* loaded from: classes3.dex */
class WebFingerMetadataRequestor extends AbstractMetadataRequestor<WebFingerMetadata, WebFingerMetadataRequestParameters> {
    private static final String TAG = "WebFingerMetadataRequestor";

    static URL buildWebFingerUrl(URL url, DRSMetadata dRSMetadata) throws MalformedURLException {
        URL url2 = new URL(dRSMetadata.getIdentityProviderService().getPassiveAuthEndpoint());
        String str = AuthenticationConstants.Broker.REDIRECT_SSL_PREFIX + url2.getHost() + "/.well-known/webfinger?resource=" + url.toString();
        Logger.m14612i(TAG, "Validator will use WebFinger URL. ", "WebFinger URL: " + str);
        return new URL(str);
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // com.microsoft.aad.adal.AbstractMetadataRequestor
    public WebFingerMetadata parseMetadata(HttpWebResponse httpWebResponse) throws AuthenticationException {
        Logger.m14614v(TAG, "Parsing WebFinger response.");
        try {
            return (WebFingerMetadata) parser().fromJson(httpWebResponse.getBody(), WebFingerMetadata.class);
        } catch (JsonSyntaxException unused) {
            throw new AuthenticationException(ADALError.JSON_PARSE_ERROR);
        }
    }

    @Override // com.microsoft.aad.adal.AbstractMetadataRequestor
    public WebFingerMetadata requestMetadata(WebFingerMetadataRequestParameters webFingerMetadataRequestParameters) throws AuthenticationException {
        URL domain = webFingerMetadataRequestParameters.getDomain();
        DRSMetadata drsMetadata = webFingerMetadataRequestParameters.getDrsMetadata();
        Logger.m14612i(TAG, "Validating authority for auth endpoint. ", "Auth endpoint: " + domain.toString());
        try {
            HttpWebResponse sendGet = getWebrequestHandler().sendGet(buildWebFingerUrl(domain, drsMetadata), new HashMap());
            if (200 == sendGet.getStatusCode()) {
                return parseMetadata(sendGet);
            }
            throw new AuthenticationException(ADALError.DEVELOPER_AUTHORITY_IS_NOT_VALID_INSTANCE);
        } catch (IOException e) {
            throw new AuthenticationException(ADALError.IO_EXCEPTION, "Unexpected error", e);
        }
    }
}
