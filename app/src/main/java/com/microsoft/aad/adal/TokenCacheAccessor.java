package com.microsoft.aad.adal;

//import com.ironsource.mediationsdk.utils.IronSourceConstants;
import com.microsoft.aad.adal.AuthenticationConstants;
import com.microsoft.aad.adal.AuthenticationResult;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/* loaded from: classes3.dex */
class TokenCacheAccessor {
    private static final String TAG = "TokenCacheAccessor";
    private String mAuthority;
    private final String mTelemetryRequestId;
    private final ITokenCacheStore mTokenCacheStore;

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes3.dex */
    public static class C53751 {
        static final int[] $SwitchMap$com$microsoft$aad$adal$TokenEntryType;

        C53751() {
        }

        static {
            int[] iArr = new int[TokenEntryType.values().length];
            $SwitchMap$com$microsoft$aad$adal$TokenEntryType = iArr;
            try {
                iArr[TokenEntryType.REGULAR_TOKEN_ENTRY.ordinal()] = 1;
            } catch (NoSuchFieldError unused) {
            }
            try {
                $SwitchMap$com$microsoft$aad$adal$TokenEntryType[TokenEntryType.MRRT_TOKEN_ENTRY.ordinal()] = 2;
            } catch (NoSuchFieldError unused2) {
            }
            try {
                $SwitchMap$com$microsoft$aad$adal$TokenEntryType[TokenEntryType.FRT_TOKEN_ENTRY.ordinal()] = 3;
            } catch (NoSuchFieldError unused3) {
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public TokenCacheAccessor(ITokenCacheStore iTokenCacheStore, String str, String str2) {
        if (iTokenCacheStore == null) {
            throw new IllegalArgumentException("tokenCacheStore");
        }
        if (StringExtensions.isNullOrBlank(str)) {
            throw new IllegalArgumentException(AuthenticationConstants.OAuth2.AUTHORITY);
        }
        if (!StringExtensions.isNullOrBlank(str2)) {
            this.mTokenCacheStore = iTokenCacheStore;
            this.mAuthority = str;
            this.mTelemetryRequestId = str2;
            return;
        }
        throw new IllegalArgumentException("requestId");
    }

    private String constructAuthorityUrl(String str) throws MalformedURLException {
        URL url = new URL(this.mAuthority);
        return url.getHost().equalsIgnoreCase(str) ? this.mAuthority : Utility.constructAuthorityUrl(url, str).toString();
    }

    private String getCacheKey(String str, String str2, String str3, String str4, String str5, TokenEntryType tokenEntryType) {
        int i = C53751.$SwitchMap$com$microsoft$aad$adal$TokenEntryType[tokenEntryType.ordinal()];
        if (i == 1) {
            return CacheKey.createCacheKeyForRTEntry(str, str2, str3, str4);
        }
        if (i == 2) {
            return CacheKey.createCacheKeyForMRRT(str, str3, str4);
        }
        if (i != 3) {
            return null;
        }
        return CacheKey.createCacheKeyForFRT(str, str5, str4);
    }

    private InstanceDiscoveryMetadata getInstanceDiscoveryMetadata() throws MalformedURLException {
        return AuthorityValidationMetadataCache.getCachedInstanceDiscoveryMetadata(new URL(this.mAuthority));
    }

    private List<String> getKeyListToRemoveForFRT(TokenCacheItem tokenCacheItem) {
        ArrayList arrayList = new ArrayList();
        if (tokenCacheItem.getUserInfo() != null) {
            arrayList.add(CacheKey.createCacheKeyForFRT(this.mAuthority, tokenCacheItem.getFamilyClientId(), tokenCacheItem.getUserInfo().getDisplayableId()));
            arrayList.add(CacheKey.createCacheKeyForFRT(this.mAuthority, tokenCacheItem.getFamilyClientId(), tokenCacheItem.getUserInfo().getUserId()));
        }
        return arrayList;
    }

    private List<String> getKeyListToRemoveForMRRT(TokenCacheItem tokenCacheItem) {
        ArrayList arrayList = new ArrayList();
        arrayList.add(CacheKey.createCacheKeyForMRRT(this.mAuthority, tokenCacheItem.getClientId(), null));
        if (tokenCacheItem.getUserInfo() != null) {
            arrayList.add(CacheKey.createCacheKeyForMRRT(this.mAuthority, tokenCacheItem.getClientId(), tokenCacheItem.getUserInfo().getDisplayableId()));
            arrayList.add(CacheKey.createCacheKeyForMRRT(this.mAuthority, tokenCacheItem.getClientId(), tokenCacheItem.getUserInfo().getUserId()));
        }
        return arrayList;
    }

    private List<String> getKeyListToRemoveForRT(TokenCacheItem tokenCacheItem) {
        ArrayList arrayList = new ArrayList();
        arrayList.add(CacheKey.createCacheKeyForRTEntry(this.mAuthority, tokenCacheItem.getResource(), tokenCacheItem.getClientId(), null));
        if (tokenCacheItem.getUserInfo() != null) {
            arrayList.add(CacheKey.createCacheKeyForRTEntry(this.mAuthority, tokenCacheItem.getResource(), tokenCacheItem.getClientId(), tokenCacheItem.getUserInfo().getDisplayableId()));
            arrayList.add(CacheKey.createCacheKeyForRTEntry(this.mAuthority, tokenCacheItem.getResource(), tokenCacheItem.getClientId(), tokenCacheItem.getUserInfo().getUserId()));
        }
        return arrayList;
    }

    private TokenCacheItem getTokenCacheItemFromAliasedHost(String str, String str2, String str3, String str4, TokenEntryType tokenEntryType) throws MalformedURLException {
        TokenCacheItem item;
        InstanceDiscoveryMetadata instanceDiscoveryMetadata = getInstanceDiscoveryMetadata();
        if (instanceDiscoveryMetadata == null) {
            return null;
        }
        for (String str5 : instanceDiscoveryMetadata.getAliases()) {
            String constructAuthorityUrl = constructAuthorityUrl(str5);
            if (!constructAuthorityUrl.equalsIgnoreCase(this.mAuthority) && !constructAuthorityUrl.equalsIgnoreCase(getAuthorityUrlWithPreferredCache()) && (item = this.mTokenCacheStore.getItem(getCacheKey(constructAuthorityUrl, str, str2, str4, str3, tokenEntryType))) != null) {
                return item;
            }
        }
        return null;
    }

    private TokenCacheItem getTokenCacheItemFromPassedInAuthority(String str, String str2, String str3, String str4, TokenEntryType tokenEntryType) throws MalformedURLException {
        if (getAuthorityUrlWithPreferredCache().equalsIgnoreCase(this.mAuthority)) {
            return null;
        }
        return this.mTokenCacheStore.getItem(getCacheKey(this.mAuthority, str, str2, str4, str3, tokenEntryType));
    }

    private boolean isUserMisMatch(String str, TokenCacheItem tokenCacheItem) {
        return (StringExtensions.isNullOrBlank(str) || tokenCacheItem.getUserInfo() == null || str.equalsIgnoreCase(tokenCacheItem.getUserInfo().getDisplayableId()) || str.equalsIgnoreCase(tokenCacheItem.getUserInfo().getUserId())) ? false : true;
    }

    private void logReturnedToken(AuthenticationResult authenticationResult) {
        if (authenticationResult == null || authenticationResult.getAccessToken() == null) {
            return;
        }
        Logger.m14612i(TAG, "Access tokenID and refresh tokenID returned. ", null);
    }

    private TokenCacheItem performAdditionalCacheLookup(String str, String str2, String str3, String str4, TokenEntryType tokenEntryType) throws MalformedURLException {
        TokenCacheItem tokenCacheItemFromPassedInAuthority = getTokenCacheItemFromPassedInAuthority(str, str2, str3, str4, tokenEntryType);
        return tokenCacheItemFromPassedInAuthority == null ? getTokenCacheItemFromAliasedHost(str, str2, str3, str4, tokenEntryType) : tokenCacheItemFromPassedInAuthority;
    }

    private void setItemToCacheForUser(String str, String str2, AuthenticationResult authenticationResult, String str3) throws MalformedURLException {
        logReturnedToken(authenticationResult);
        Logger.m14614v("TokenCacheAccessor:setItemToCacheForUser", "Save regular token into cache.");
        CacheEvent cacheEvent = new CacheEvent("Microsoft.ADAL.token_cache_write");
        cacheEvent.setRequestId(this.mTelemetryRequestId);
        Telemetry.getInstance().startEvent(this.mTelemetryRequestId, "Microsoft.ADAL.token_cache_write");
        if (!StringExtensions.isNullOrBlank(authenticationResult.getAuthority())) {
            this.mAuthority = authenticationResult.getAuthority();
        }
        this.mTokenCacheStore.setItem(CacheKey.createCacheKeyForRTEntry(getAuthorityUrlWithPreferredCache(), str, str2, str3), TokenCacheItem.createRegularTokenCacheItem(getAuthorityUrlWithPreferredCache(), str, str2, authenticationResult));
        cacheEvent.setTokenTypeRT(true);
        if (authenticationResult.getIsMultiResourceRefreshToken()) {
            Logger.m14614v("TokenCacheAccessor:setItemToCacheForUser", "Save Multi Resource Refresh token to cache.");
            this.mTokenCacheStore.setItem(CacheKey.createCacheKeyForMRRT(getAuthorityUrlWithPreferredCache(), str2, str3), TokenCacheItem.createMRRTTokenCacheItem(getAuthorityUrlWithPreferredCache(), str2, authenticationResult));
            cacheEvent.setTokenTypeMRRT(true);
        }
        if (!StringExtensions.isNullOrBlank(authenticationResult.getFamilyClientId()) && !StringExtensions.isNullOrBlank(str3)) {
            Logger.m14614v("TokenCacheAccessor:setItemToCacheForUser", "Save Family Refresh token into cache.");
            this.mTokenCacheStore.setItem(CacheKey.createCacheKeyForFRT(getAuthorityUrlWithPreferredCache(), authenticationResult.getFamilyClientId(), str3), TokenCacheItem.createFRRTTokenCacheItem(getAuthorityUrlWithPreferredCache(), authenticationResult));
            cacheEvent.setTokenTypeFRT(true);
        }
        Telemetry.getInstance().stopEvent(this.mTelemetryRequestId, cacheEvent, "Microsoft.ADAL.token_cache_write");
    }

    private CacheEvent startCacheTelemetryRequest(String str) {
        CacheEvent cacheEvent = new CacheEvent("Microsoft.ADAL.token_cache_lookup");
        cacheEvent.setTokenType(str);
        cacheEvent.setRequestId(this.mTelemetryRequestId);
        Telemetry.getInstance().startEvent(this.mTelemetryRequestId, "Microsoft.ADAL.token_cache_lookup");
        return cacheEvent;
    }

    private void throwIfMultipleATExisted(String str, String str2, String str3) throws AuthenticationException {
        if (StringExtensions.isNullOrBlank(str3) && isMultipleRTsMatchingGivenAppAndResource(str, str2)) {
            throw new AuthenticationException(ADALError.AUTH_FAILED_USER_MISMATCH, "No user is provided and multiple access tokens exist for the given app and resource.");
        }
    }

    public TokenCacheItem getATFromCache(String str, String str2, String str3) throws AuthenticationException {
        try {
            TokenCacheItem regularRefreshTokenCacheItem = getRegularRefreshTokenCacheItem(str, str2, str3);
            if (regularRefreshTokenCacheItem != null) {
                throwIfMultipleATExisted(str2, str, str3);
                if (!StringExtensions.isNullOrBlank(regularRefreshTokenCacheItem.getAccessToken()) && !TokenCacheItem.isTokenExpired(regularRefreshTokenCacheItem.getExpiresOn()) && isUserMisMatch(str3, regularRefreshTokenCacheItem)) {
                    throw new AuthenticationException(ADALError.AUTH_FAILED_USER_MISMATCH);
                }
                return regularRefreshTokenCacheItem;
            }
            Logger.m14614v("TokenCacheAccessor:getATFromCache", "No access token exists.");
            return null;
        } catch (MalformedURLException e) {
            throw new AuthenticationException(ADALError.DEVELOPER_AUTHORITY_IS_NOT_VALID_URL, e.getMessage(), e);
        }
    }

    public String getAuthorityUrlWithPreferredCache() throws MalformedURLException {
        InstanceDiscoveryMetadata instanceDiscoveryMetadata = getInstanceDiscoveryMetadata();
        return (instanceDiscoveryMetadata == null || !instanceDiscoveryMetadata.isValidated()) ? this.mAuthority : constructAuthorityUrl(instanceDiscoveryMetadata.getPreferredCache());
    }

    public TokenCacheItem getFRTItem(String str, String str2) throws MalformedURLException {
        CacheEvent startCacheTelemetryRequest = startCacheTelemetryRequest("Microsoft.ADAL.frt");
        if (StringExtensions.isNullOrBlank(str2)) {
            Telemetry.getInstance().stopEvent(this.mTelemetryRequestId, startCacheTelemetryRequest, "Microsoft.ADAL.token_cache_lookup");
            return null;
        }
        TokenCacheItem item = this.mTokenCacheStore.getItem(CacheKey.createCacheKeyForFRT(getAuthorityUrlWithPreferredCache(), str, str2));
        if (item == null) {
            item = performAdditionalCacheLookup(null, null, str, str2, TokenEntryType.FRT_TOKEN_ENTRY);
        }
        if (item != null) {
            startCacheTelemetryRequest.setTokenTypeFRT(true);
        }
        Telemetry.getInstance().stopEvent(this.mTelemetryRequestId, startCacheTelemetryRequest, "Microsoft.ADAL.token_cache_lookup");
        return item;
    }

    public TokenCacheItem getMRRTItem(String str, String str2) throws MalformedURLException {
        CacheEvent startCacheTelemetryRequest = startCacheTelemetryRequest("Microsoft.ADAL.mrrt");
        TokenCacheItem item = this.mTokenCacheStore.getItem(CacheKey.createCacheKeyForMRRT(getAuthorityUrlWithPreferredCache(), str, str2));
        if (item == null) {
            item = performAdditionalCacheLookup(null, str, null, str2, TokenEntryType.MRRT_TOKEN_ENTRY);
        }
        if (item != null) {
            startCacheTelemetryRequest.setTokenTypeMRRT(true);
            startCacheTelemetryRequest.setTokenTypeFRT(item.isFamilyToken());
        }
        Telemetry.getInstance().stopEvent(this.mTelemetryRequestId, startCacheTelemetryRequest, "Microsoft.ADAL.token_cache_lookup");
        return item;
    }

    public TokenCacheItem getRegularRefreshTokenCacheItem(String str, String str2, String str3) throws MalformedURLException {
        CacheEvent startCacheTelemetryRequest = startCacheTelemetryRequest("Microsoft.ADAL.rt");
        TokenCacheItem item = this.mTokenCacheStore.getItem(CacheKey.createCacheKeyForRTEntry(getAuthorityUrlWithPreferredCache(), str, str2, str3));
        if (item == null) {
            item = performAdditionalCacheLookup(str, str2, null, str3, TokenEntryType.REGULAR_TOKEN_ENTRY);
        }
        if (item != null) {
            startCacheTelemetryRequest.setTokenTypeRT(true);
            startCacheTelemetryRequest.setSpeRing(item.getSpeRing());
        }
        Telemetry.getInstance().stopEvent(this.mTelemetryRequestId, startCacheTelemetryRequest, "Microsoft.ADAL.token_cache_lookup");
        return item;
    }

    public TokenCacheItem getStaleToken(AuthenticationRequest authenticationRequest) throws AuthenticationException {
        try {
            TokenCacheItem regularRefreshTokenCacheItem = getRegularRefreshTokenCacheItem(authenticationRequest.getResource(), authenticationRequest.getClientId(), authenticationRequest.getUserFromRequest());
            if (regularRefreshTokenCacheItem != null && !StringExtensions.isNullOrBlank(regularRefreshTokenCacheItem.getAccessToken()) && regularRefreshTokenCacheItem.getExtendedExpiresOn() != null && !TokenCacheItem.isTokenExpired(regularRefreshTokenCacheItem.getExtendedExpiresOn())) {
                throwIfMultipleATExisted(authenticationRequest.getClientId(), authenticationRequest.getResource(), authenticationRequest.getUserFromRequest());
                Logger.m14612i("TokenCacheAccessor:getStaleToken", "The stale access token is returned.", "");
                return regularRefreshTokenCacheItem;
            }
            Logger.m14612i("TokenCacheAccessor:getStaleToken", "The stale access token is not found.", "");
            return null;
        } catch (MalformedURLException e) {
            throw new AuthenticationException(ADALError.DEVELOPER_AUTHORITY_IS_NOT_VALID_URL, e.getMessage(), e);
        }
    }

    public boolean isMultipleMRRTsMatchingGivenApp(String str) {
        Iterator<TokenCacheItem> all = this.mTokenCacheStore.getAll();
        ArrayList arrayList = new ArrayList();
        while (all.hasNext()) {
            TokenCacheItem next = all.next();
            if (next.getAuthority().equalsIgnoreCase(this.mAuthority) && next.getClientId().equalsIgnoreCase(str) && (next.getIsMultiResourceRefreshToken() || StringExtensions.isNullOrBlank(next.getResource()))) {
                arrayList.add(next);
            }
        }
        return arrayList.size() > 1;
    }

    public boolean isMultipleRTsMatchingGivenAppAndResource(String str, String str2) {
        Iterator<TokenCacheItem> all = this.mTokenCacheStore.getAll();
        ArrayList arrayList = new ArrayList();
        while (all.hasNext()) {
            TokenCacheItem next = all.next();
            if (next.getAuthority().equalsIgnoreCase(this.mAuthority) && str.equalsIgnoreCase(next.getClientId()) && str2.equalsIgnoreCase(next.getResource()) && !next.getIsMultiResourceRefreshToken()) {
                arrayList.add(next);
            }
        }
        return arrayList.size() > 1;
    }

    public void removeTokenCacheItem(TokenCacheItem tokenCacheItem, String str) throws AuthenticationException {
        List<String> keyListToRemoveForFRT;
        CacheEvent cacheEvent = new CacheEvent("Microsoft.ADAL.token_cache_delete");
        cacheEvent.setRequestId(this.mTelemetryRequestId);
        Telemetry.getInstance().startEvent(this.mTelemetryRequestId, "Microsoft.ADAL.token_cache_delete");
        int i = C53751.$SwitchMap$com$microsoft$aad$adal$TokenEntryType[tokenCacheItem.getTokenEntryType().ordinal()];
        if (i == 1) {
            cacheEvent.setTokenTypeRT(true);
            Logger.m14614v("TokenCacheAccessor:removeTokenCacheItem", "Regular RT was used to get access token, remove entries for regular RT entries.");
            keyListToRemoveForFRT = getKeyListToRemoveForRT(tokenCacheItem);
        } else if (i == 2) {
            cacheEvent.setTokenTypeMRRT(true);
            Logger.m14614v("TokenCacheAccessor:removeTokenCacheItem", "MRRT was used to get access token, remove entries for both MRRT entries and regular RT entries.");
            List<String> keyListToRemoveForMRRT = getKeyListToRemoveForMRRT(tokenCacheItem);
            TokenCacheItem tokenCacheItem2 = new TokenCacheItem(tokenCacheItem);
            tokenCacheItem2.setResource(str);
            keyListToRemoveForMRRT.addAll(getKeyListToRemoveForRT(tokenCacheItem2));
            keyListToRemoveForFRT = keyListToRemoveForMRRT;
        } else if (i == 3) {
            cacheEvent.setTokenTypeFRT(true);
            Logger.m14614v("TokenCacheAccessor:removeTokenCacheItem", "FRT was used to get access token, remove entries for FRT entries.");
            keyListToRemoveForFRT = getKeyListToRemoveForFRT(tokenCacheItem);
        } else {
            throw new AuthenticationException(ADALError.INVALID_TOKEN_CACHE_ITEM);
        }
        for (String str2 : keyListToRemoveForFRT) {
            this.mTokenCacheStore.removeItem(str2);
        }
        Telemetry.getInstance().stopEvent(this.mTelemetryRequestId, cacheEvent, "Microsoft.ADAL.token_cache_delete");
    }

    public void updateCachedItemWithResult(String str, String str2, AuthenticationResult authenticationResult, TokenCacheItem tokenCacheItem) throws AuthenticationException {
        if (authenticationResult == null) {
            Logger.m14614v("TokenCacheAccessor:updateCachedItemWithResult", "AuthenticationResult is null, cannot update cache.");
//            throw new IllegalArgumentException(IronSourceConstants.EVENTS_RESULT);
        } else if (authenticationResult.getStatus() == AuthenticationResult.AuthenticationStatus.Succeeded) {
            Logger.m14614v("TokenCacheAccessor:updateCachedItemWithResult", "Save returned AuthenticationResult into cache.");
            if (tokenCacheItem != null && tokenCacheItem.getUserInfo() != null && authenticationResult.getUserInfo() == null) {
                authenticationResult.setUserInfo(tokenCacheItem.getUserInfo());
                authenticationResult.setIdToken(tokenCacheItem.getRawIdToken());
                authenticationResult.setTenantId(tokenCacheItem.getTenantId());
            }
            try {
                updateTokenCache(str, str2, authenticationResult);
            } catch (MalformedURLException e) {
                throw new AuthenticationException(ADALError.DEVELOPER_AUTHORITY_IS_NOT_VALID_URL, e.getMessage(), e);
            }
        } else if ("invalid_grant".equalsIgnoreCase(authenticationResult.getErrorCode())) {
            Logger.m14614v("TokenCacheAccessor:updateCachedItemWithResult", "Received INVALID_GRANT error code, remove existing cache entry.");
            removeTokenCacheItem(tokenCacheItem, str);
        }
    }

    public void updateTokenCache(String str, String str2, AuthenticationResult authenticationResult) throws MalformedURLException {
        if (authenticationResult == null || StringExtensions.isNullOrBlank(authenticationResult.getAccessToken())) {
            return;
        }
        if (authenticationResult.getUserInfo() != null) {
            if (!StringExtensions.isNullOrBlank(authenticationResult.getUserInfo().getDisplayableId())) {
                setItemToCacheForUser(str, str2, authenticationResult, authenticationResult.getUserInfo().getDisplayableId());
            }
            if (!StringExtensions.isNullOrBlank(authenticationResult.getUserInfo().getUserId())) {
                setItemToCacheForUser(str, str2, authenticationResult, authenticationResult.getUserInfo().getUserId());
            }
        }
        setItemToCacheForUser(str, str2, authenticationResult, null);
    }
}
