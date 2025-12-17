package com.microsoft.aad.adal;

import android.content.Context;
import android.net.Uri;
import com.microsoft.aad.adal.AuthenticationConstants;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.locks.ReentrantLock;
import org.json.JSONException;

/* loaded from: classes3.dex */
final class Discovery {
    private static final Set<String> AAD_WHITELISTED_HOSTS = Collections.synchronizedSet(new HashSet());
    private static final Map<String, Set<URI>> ADFS_VALIDATED_AUTHORITIES = Collections.synchronizedMap(new HashMap());
    private static final String API_VERSION_KEY = "api-version";
    private static final String API_VERSION_VALUE = "1.1";
    private static final String AUTHORIZATION_COMMON_ENDPOINT = "/common/oauth2/authorize";
    private static final String AUTHORIZATION_ENDPOINT_KEY = "authorization_endpoint";
    private static final String INSTANCE_DISCOVERY_SUFFIX = "common/discovery/instance";
    private static final String TAG = "Discovery";
    private static final String TRUSTED_QUERY_INSTANCE = "login.microsoftonline.com";
    private static volatile ReentrantLock sInstanceDiscoveryNetworkRequestLock;
    private Context mContext;
    private UUID mCorrelationId;
    private final IWebRequestHandler mWebrequestHandler = new WebRequestHandler();

    public Discovery(Context context) {
        initValidList();
        this.mContext = context;
    }

    private URL buildQueryString(String str, String str2) throws MalformedURLException {
        Uri.Builder builder = new Uri.Builder();
        builder.appendEncodedPath(INSTANCE_DISCOVERY_SUFFIX).appendQueryParameter(API_VERSION_KEY, API_VERSION_VALUE).appendQueryParameter(AUTHORIZATION_ENDPOINT_KEY, str2);
        return new URL(builder.build().toString());
    }

    private String getAuthorizationCommonEndpoint(URL url) {
        return new Uri.Builder().authority(url.getHost()).appendPath(AUTHORIZATION_COMMON_ENDPOINT).build().toString();
    }

    private static ReentrantLock getLock() {
        if (sInstanceDiscoveryNetworkRequestLock == null) {
            synchronized (Discovery.class) {
                if (sInstanceDiscoveryNetworkRequestLock == null) {
                    sInstanceDiscoveryNetworkRequestLock = new ReentrantLock();
                }
            }
        }
        return sInstanceDiscoveryNetworkRequestLock;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static Set<String> getValidHosts() {
        return AAD_WHITELISTED_HOSTS;
    }

    private void initValidList() {
        Set<String> set = AAD_WHITELISTED_HOSTS;
        if (set.isEmpty()) {
            set.add("login.windows.net");
            set.add(TRUSTED_QUERY_INSTANCE);
            set.add("login.chinacloudapi.cn");
            set.add("login.microsoftonline.de");
            set.add("login-us.microsoftonline.com");
            set.add("login.microsoftonline.us");
        }
    }

    private Map<String, String> parseResponse(HttpWebResponse httpWebResponse) throws JSONException {
        return HashMapExtensions.getJsonResponse(httpWebResponse);
    }

    private void performInstanceDiscovery(URL url, String str) throws AuthenticationException {
        if (AuthorityValidationMetadataCache.containsAuthorityHost(url)) {
            return;
        }
        HttpWebRequest.throwIfNetworkNotAvailable(this.mContext);
        try {
            AuthorityValidationMetadataCache.processInstanceDiscoveryMetadata(url, sendRequest(buildQueryString(str, getAuthorizationCommonEndpoint(url))));
            if (!AuthorityValidationMetadataCache.containsAuthorityHost(url)) {
                ArrayList arrayList = new ArrayList();
                arrayList.add(url.getHost());
                AuthorityValidationMetadataCache.updateInstanceDiscoveryMap(url.getHost(), new InstanceDiscoveryMetadata(url.getHost(), url.getHost(), arrayList));
            }
            if (AuthorityValidationMetadataCache.isAuthorityValidated(url)) {
                return;
            }
            throw new AuthenticationException(ADALError.DEVELOPER_AUTHORITY_IS_NOT_VALID_INSTANCE);
        } catch (IOException | JSONException e) {
            Logger.m14610e("Discovery:performInstanceDiscovery", "Error when validating authority. ", "", ADALError.DEVELOPER_AUTHORITY_IS_NOT_VALID_URL, e);
            throw new AuthenticationException(ADALError.DEVELOPER_AUTHORITY_IS_NOT_VALID_INSTANCE, e.getMessage(), e);
        }
    }

    private Map<String, String> sendRequest(URL url) throws IOException, JSONException, AuthenticationException {
        Logger.m14615v(TAG, "Sending discovery request to query url. ", "queryUrl: " + url, null);
        HashMap hashMap = new HashMap();
        hashMap.put(WebRequestHandler.HEADER_ACCEPT, WebRequestHandler.HEADER_ACCEPT_JSON);
        UUID uuid = this.mCorrelationId;
        if (uuid != null) {
            hashMap.put(AuthenticationConstants.AAD.CLIENT_REQUEST_ID, uuid.toString());
            hashMap.put(AuthenticationConstants.AAD.RETURN_CLIENT_REQUEST_ID, "true");
        }
        try {
            ClientMetrics.INSTANCE.beginClientMetricsRecord(url, this.mCorrelationId, hashMap);
            HttpWebResponse sendGet = this.mWebrequestHandler.sendGet(url, hashMap);
            String str = null;
            ClientMetrics.INSTANCE.setLastError(null);
            Map<String, String> parseResponse = parseResponse(sendGet);
            if (parseResponse.containsKey(AuthenticationConstants.OAuth2.ERROR_CODES)) {
                String str2 = parseResponse.get(AuthenticationConstants.OAuth2.ERROR_CODES);
                ClientMetrics.INSTANCE.setLastError(str2);
                throw new AuthenticationException(ADALError.DEVELOPER_AUTHORITY_IS_NOT_VALID_INSTANCE, "Fail to valid authority with errors: " + str2);
            }
            return parseResponse;
        } finally {
            ClientMetrics.INSTANCE.endClientMetricsRecord(ClientMetricsEndpointType.INSTANCE_DISCOVERY, this.mCorrelationId);
        }
    }

    private static void validateADFS(URL url, String str) throws AuthenticationException {
        try {
            URI uri = url.toURI();
            Map<String, Set<URI>> map = ADFS_VALIDATED_AUTHORITIES;
            if (map.get(str) != null && map.get(str).contains(uri)) {
                return;
            }
            if (ADFSWebFingerValidator.realmIsTrusted(uri, new WebFingerMetadataRequestor().requestMetadata(new WebFingerMetadataRequestParameters(url, new DRSMetadataRequestor().requestMetadata(str))))) {
                if (map.get(str) == null) {
                    map.put(str, new HashSet());
                }
                map.get(str).add(uri);
                return;
            }
            throw new AuthenticationException(ADALError.DEVELOPER_AUTHORITY_IS_NOT_VALID_INSTANCE);
        } catch (URISyntaxException unused) {
            throw new AuthenticationException(ADALError.DEVELOPER_AUTHORITY_IS_NOT_VALID_URL, "Authority URL/URI must be RFC 2396 compliant to use AD FS validation");
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void verifyAuthorityValidInstance(URL url) throws AuthenticationException {
        if (url == null || StringExtensions.isNullOrBlank(url.getHost()) || !StringExtensions.isNullOrBlank(url.getQuery()) || !StringExtensions.isNullOrBlank(url.getRef()) || StringExtensions.isNullOrBlank(url.getPath())) {
            throw new AuthenticationException(ADALError.DEVELOPER_AUTHORITY_IS_NOT_VALID_INSTANCE);
        }
    }

    public void setCorrelationId(UUID uuid) {
        this.mCorrelationId = uuid;
    }

    public void validateAuthority(URL url) throws AuthenticationException {
        verifyAuthorityValidInstance(url);
        if (AuthorityValidationMetadataCache.containsAuthorityHost(url)) {
            return;
        }
        String lowerCase = url.getHost().toLowerCase(Locale.US);
        if (!AAD_WHITELISTED_HOSTS.contains(url.getHost().toLowerCase(Locale.US))) {
            lowerCase = TRUSTED_QUERY_INSTANCE;
        }
        try {
            sInstanceDiscoveryNetworkRequestLock = getLock();
            sInstanceDiscoveryNetworkRequestLock.lock();
            performInstanceDiscovery(url, lowerCase);
        } finally {
            sInstanceDiscoveryNetworkRequestLock.unlock();
        }
    }

    public void validateAuthorityADFS(URL url, String str) throws AuthenticationException {
        if (!StringExtensions.isNullOrBlank(str)) {
            validateADFS(url, str);
            return;
        }
        throw new IllegalArgumentException("Cannot validate AD FS Authority with domain [null]");
    }
}
