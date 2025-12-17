package com.microsoft.aad.adal;

import com.google.gson.JsonSyntaxException;
import com.microsoft.aad.adal.AuthenticationConstants;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.HashMap;

/* loaded from: classes3.dex */
final class DRSMetadataRequestor extends AbstractMetadataRequestor<DRSMetadata, String> {
    private static final String CLOUD_RESOLVER_DOMAIN = "windows.net/";
    private static final String DRS_URL_PREFIX = "https://enterpriseregistration.";
    private static final String TAG = "DRSMetadataRequestor";

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes3.dex */
    public enum Type {
        ON_PREM,
        CLOUD
    }

    private DRSMetadata requestCloud(String str) throws AuthenticationException {
        Logger.m14614v(TAG, "Requesting DRS discovery (cloud)");
        try {
            return requestDrsDiscoveryInternal(Type.CLOUD, str);
        } catch (UnknownHostException unused) {
            throw new AuthenticationException(ADALError.DRS_DISCOVERY_FAILED_UNKNOWN_HOST);
        }
    }

    private DRSMetadata requestDrsDiscoveryInternal(Type type, String str) throws AuthenticationException, UnknownHostException {
        try {
            URL url = new URL(buildRequestUrlByType(type, str));
            HashMap hashMap = new HashMap();
            hashMap.put(WebRequestHandler.HEADER_ACCEPT, WebRequestHandler.HEADER_ACCEPT_JSON);
            if (getCorrelationId() != null) {
                hashMap.put(AuthenticationConstants.AAD.CLIENT_REQUEST_ID, getCorrelationId().toString());
            }
            try {
                HttpWebResponse sendGet = getWebrequestHandler().sendGet(url, hashMap);
                int statusCode = sendGet.getStatusCode();
                if (200 == statusCode) {
                    return parseMetadata(sendGet);
                }
                ADALError aDALError = ADALError.DRS_FAILED_SERVER_ERROR;
                throw new AuthenticationException(aDALError, "Unexpected error code: [" + statusCode + "]");
            } catch (UnknownHostException e) {
                throw e;
            } catch (IOException unused) {
                throw new AuthenticationException(ADALError.IO_EXCEPTION);
            }
        } catch (MalformedURLException unused2) {
            throw new AuthenticationException(ADALError.DRS_METADATA_URL_INVALID);
        }
    }

    private DRSMetadata requestOnPrem(String str) throws UnknownHostException, AuthenticationException {
        Logger.m14614v(TAG, "Requesting DRS discovery (on-prem)");
        return requestDrsDiscoveryInternal(Type.ON_PREM, str);
    }

    public String buildRequestUrlByType(Type type, String str) {
        StringBuilder sb = new StringBuilder(DRS_URL_PREFIX);
        if (Type.CLOUD == type) {
            sb.append(CLOUD_RESOLVER_DOMAIN);
            sb.append(str);
        } else if (Type.ON_PREM == type) {
            sb.append(str);
        }
        sb.append("/enrollmentserver/contract?api-version=1.0");
        String sb2 = sb.toString();
        Logger.m14609e(TAG, "Request will use DRS url. ", "URL: " + sb2, null);
        return sb2;
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // com.microsoft.aad.adal.AbstractMetadataRequestor
    public DRSMetadata parseMetadata(HttpWebResponse httpWebResponse) throws AuthenticationException {
        Logger.m14614v(TAG, "Parsing DRS metadata response");
        try {
            return (DRSMetadata) parser().fromJson(httpWebResponse.getBody(), DRSMetadata.class);
        } catch (JsonSyntaxException unused) {
            throw new AuthenticationException(ADALError.JSON_PARSE_ERROR);
        }
    }

    @Override // com.microsoft.aad.adal.AbstractMetadataRequestor
    public DRSMetadata requestMetadata(String str) throws AuthenticationException {
        try {
            return requestOnPrem(str);
        } catch (UnknownHostException unused) {
            return requestCloud(str);
        }
    }
}
