package com.microsoft.aad.adal;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/* loaded from: classes3.dex */
final class AuthorityValidationMetadataCache {
    private static final String ALIASES = "aliases";
    static final String META_DATA = "metadata";
    private static final String PREFERRED_CACHE = "preferred_cache";
    private static final String PREFERRED_NETWORK = "preferred_network";
    private static final String TAG = "AuthorityValidationMetadataCache";
    static final String TENANT_DISCOVERY_ENDPOINT = "tenant_discovery_endpoint";
    private static ConcurrentMap<String, InstanceDiscoveryMetadata> sAadAuthorityHostMetadata = new ConcurrentHashMap();

    private AuthorityValidationMetadataCache() {
    }

    static void clearAuthorityValidationCache() {
        sAadAuthorityHostMetadata.clear();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static boolean containsAuthorityHost(URL url) {
        return sAadAuthorityHostMetadata.containsKey(url.getHost().toLowerCase(Locale.US));
    }

    static Map<String, InstanceDiscoveryMetadata> getAuthorityValidationMetadataCache() {
        return Collections.unmodifiableMap(sAadAuthorityHostMetadata);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static InstanceDiscoveryMetadata getCachedInstanceDiscoveryMetadata(URL url) {
        return sAadAuthorityHostMetadata.get(url.getHost().toLowerCase(Locale.US));
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static boolean isAuthorityValidated(URL url) {
        return containsAuthorityHost(url) && getCachedInstanceDiscoveryMetadata(url).isValidated();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void processInstanceDiscoveryMetadata(URL url, Map<String, String> map) throws JSONException {
        ConcurrentMap<String, InstanceDiscoveryMetadata> concurrentMap;
        InstanceDiscoveryMetadata instanceDiscoveryMetadata;
        boolean containsKey = map.containsKey(TENANT_DISCOVERY_ENDPOINT);
        String str = map.get(META_DATA);
        String lowerCase = url.getHost().toLowerCase(Locale.US);
        if (!containsKey) {
            concurrentMap = sAadAuthorityHostMetadata;
            instanceDiscoveryMetadata = new InstanceDiscoveryMetadata(false);
        } else if (StringExtensions.isNullOrBlank(str)) {
            Logger.m14614v("AuthorityValidationMetadataCache:processInstanceDiscoveryMetadata", "No metadata returned from instance discovery.");
            concurrentMap = sAadAuthorityHostMetadata;
            instanceDiscoveryMetadata = new InstanceDiscoveryMetadata(lowerCase, lowerCase);
        } else {
            processInstanceDiscoveryResponse(str);
            return;
        }
        concurrentMap.put(lowerCase, instanceDiscoveryMetadata);
    }

    private static void processInstanceDiscoveryResponse(String str) throws JSONException {
        JSONArray jSONArray = new JSONArray(str);
        for (int i = 0; i < jSONArray.length(); i++) {
            InstanceDiscoveryMetadata processSingleJsonArray = processSingleJsonArray(new JSONObject(jSONArray.get(i).toString()));
            for (String str2 : processSingleJsonArray.getAliases()) {
                sAadAuthorityHostMetadata.put(str2.toLowerCase(Locale.US), processSingleJsonArray);
            }
        }
    }

    private static InstanceDiscoveryMetadata processSingleJsonArray(JSONObject jSONObject) throws JSONException {
        String string = jSONObject.getString(PREFERRED_NETWORK);
        String string2 = jSONObject.getString(PREFERRED_CACHE);
        JSONArray jSONArray = jSONObject.getJSONArray(ALIASES);
        ArrayList arrayList = new ArrayList();
        for (int i = 0; i < jSONArray.length(); i++) {
            arrayList.add(jSONArray.getString(i));
        }
        return new InstanceDiscoveryMetadata(string, string2, arrayList);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void updateInstanceDiscoveryMap(String str, InstanceDiscoveryMetadata instanceDiscoveryMetadata) {
        sAadAuthorityHostMetadata.put(str.toLowerCase(Locale.US), instanceDiscoveryMetadata);
    }
}
