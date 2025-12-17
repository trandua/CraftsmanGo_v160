package com.microsoft.aad.adal;

import com.microsoft.aad.adal.AuthenticationConstants;
import java.io.Serializable;
import java.util.Locale;
import org.jose4j.jwk.EllipticCurveJsonWebKey;

/* loaded from: classes3.dex */
public final class CacheKey implements Serializable {
    static final String FRT_ENTRY_PREFIX = "foci-";
    private static final long serialVersionUID = 8067972995583126404L;
    private String mAuthority;
    private String mClientId;
    private String mFamilyClientId;
    private boolean mIsMultipleResourceRefreshToken;
    private String mResource;
    private String mUserId;

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes3.dex */
    public static class C53661 {
        static final int[] $SwitchMap$com$microsoft$aad$adal$TokenEntryType;

        C53661() {
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

    private CacheKey() {
    }

    public static String createCacheKey(TokenCacheItem tokenCacheItem) throws AuthenticationException {
        if (tokenCacheItem != null) {
            String userId = tokenCacheItem.getUserInfo() != null ? tokenCacheItem.getUserInfo().getUserId() : null;
            int i = C53661.$SwitchMap$com$microsoft$aad$adal$TokenEntryType[tokenCacheItem.getTokenEntryType().ordinal()];
            if (i == 1) {
                return createCacheKeyForRTEntry(tokenCacheItem.getAuthority(), tokenCacheItem.getResource(), tokenCacheItem.getClientId(), userId);
            }
            if (i == 2) {
                return createCacheKeyForMRRT(tokenCacheItem.getAuthority(), tokenCacheItem.getClientId(), userId);
            }
            if (i == 3) {
                return createCacheKeyForFRT(tokenCacheItem.getAuthority(), tokenCacheItem.getFamilyClientId(), userId);
            }
            throw new AuthenticationException(ADALError.INVALID_TOKEN_CACHE_ITEM, "Cannot create cachekey from given token item");
        }
        throw new IllegalArgumentException("TokenCacheItem");
    }

    public static String createCacheKey(String str, String str2, String str3, boolean z, String str4, String str5) {
        if (str != null) {
            if (str3 == null && str5 == null) {
                throw new IllegalArgumentException("both clientId and familyClientId are null");
            }
            CacheKey cacheKey = new CacheKey();
            if (!z) {
                if (str2 != null) {
                    cacheKey.mResource = str2;
                } else {
                    throw new IllegalArgumentException(AuthenticationConstants.AAD.RESOURCE);
                }
            }
            String lowerCase = str.toLowerCase(Locale.US);
            cacheKey.mAuthority = lowerCase;
            if (lowerCase.endsWith("/")) {
                String str6 = cacheKey.mAuthority;
                cacheKey.mAuthority = (String) str6.subSequence(0, str6.length() - 1);
            }
            if (str3 != null) {
                cacheKey.mClientId = str3.toLowerCase(Locale.US);
            }
            if (str5 != null) {
                cacheKey.mFamilyClientId = (FRT_ENTRY_PREFIX + str5).toLowerCase(Locale.US);
            }
            cacheKey.mIsMultipleResourceRefreshToken = z;
            if (!StringExtensions.isNullOrBlank(str4)) {
                cacheKey.mUserId = str4.toLowerCase(Locale.US);
            }
            return cacheKey.toString();
        }
        throw new IllegalArgumentException(AuthenticationConstants.OAuth2.AUTHORITY);
    }

    public static String createCacheKeyForFRT(String str, String str2, String str3) {
        return createCacheKey(str, null, null, true, str3, str2);
    }

    public static String createCacheKeyForMRRT(String str, String str2, String str3) {
        return createCacheKey(str, null, str2, true, str3, null);
    }

    public static String createCacheKeyForRTEntry(String str, String str2, String str3, String str4) {
        return createCacheKey(str, str2, str3, false, str4, null);
    }

    public String getAuthority() {
        return this.mAuthority;
    }

    public String getClientId() {
        return this.mClientId;
    }

    public boolean getIsMultipleResourceRefreshToken() {
        return this.mIsMultipleResourceRefreshToken;
    }

    public String getResource() {
        return this.mResource;
    }

    public String getUserId() {
        return this.mUserId;
    }

    public String toString() {
        boolean isNullOrBlank = StringExtensions.isNullOrBlank(this.mFamilyClientId);
        String str = EllipticCurveJsonWebKey.Y_MEMBER_NAME;
        if (isNullOrBlank) {
            Locale locale = Locale.US;
            String str2 = this.mAuthority;
            String str3 = this.mResource;
            String str4 = this.mClientId;
            if (!this.mIsMultipleResourceRefreshToken) {
                str = "n";
            }
            return String.format(locale, "%s$%s$%s$%s$%s", str2, str3, str4, str, this.mUserId);
        }
        Locale locale2 = Locale.US;
        String str5 = this.mAuthority;
        String str6 = this.mResource;
        String str7 = this.mClientId;
        if (!this.mIsMultipleResourceRefreshToken) {
            str = "n";
        }
        return String.format(locale2, "%s$%s$%s$%s$%s$%s", str5, str6, str7, str, this.mUserId, this.mFamilyClientId);
    }
}
