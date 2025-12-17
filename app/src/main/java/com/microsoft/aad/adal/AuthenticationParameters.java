package com.microsoft.aad.adal;

import android.content.Context;
import android.os.Handler;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Pattern;

/* loaded from: classes3.dex */
public class AuthenticationParameters {
    public static final String AUTHENTICATE_HEADER = "WWW-Authenticate";
    public static final String AUTHORITY_KEY = "authorization_uri";
    public static final String AUTH_HEADER_INVALID_FORMAT = "Invalid authentication header format";
    public static final String AUTH_HEADER_MISSING = "WWW-Authenticate header was expected in the response";
    public static final String AUTH_HEADER_MISSING_AUTHORITY = "WWW-Authenticate header is missing authorization_uri.";
    public static final String AUTH_HEADER_WRONG_STATUS = "Unauthorized http response (status code 401) was expected";
    public static final String BEARER = "bearer";
    public static final String RESOURCE_KEY = "resource_id";
    private static final String TAG = "AuthenticationParameters";
    private static ExecutorService sThreadExecutor = Executors.newSingleThreadExecutor();
    public static IWebRequestHandler sWebRequest = new WebRequestHandler();
    private String mAuthority;
    private String mResource;

    /* loaded from: classes3.dex */
    public interface AuthenticationParamCallback {
        void onCompleted(Exception exc, AuthenticationParameters authenticationParameters);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes3.dex */
    public static class Challenge {
        private static final String REGEX_SPLIT_UNQUOTED_COMMA = ",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)";
        private static final String REGEX_SPLIT_UNQUOTED_EQUALS = "=(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)";
        private static final String REGEX_STRING_TOKEN_WITH_SCHEME = "^([^\\s|^=]+)[\\s|\\t]+([^=]*=[^=]*)+$";
        private static final String REGEX_UNQUOTED_LOOKAHEAD = "(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)";
        private static final String SUFFIX_COMMA = ", ";
        private Map<String, String> mParameters;
        private String mScheme;

        private Challenge(String str, Map<String, String> map) {
            this.mScheme = str;
            this.mParameters = map;
        }

        private static boolean containsScheme(String str) throws ResourceAuthenticationChallengeException {
            if (!StringExtensions.isNullOrBlank(str)) {
                Logger.m14612i("AuthenticationParameters:containsScheme", "Testing token contains scheme", "input[" + str + "]");
                boolean matches = Pattern.compile(REGEX_STRING_TOKEN_WITH_SCHEME).matcher(str).matches();
                Logger.m14612i("AuthenticationParameters:containsScheme", "Testing String contains scheme", "Matches? [" + matches + "]");
                return matches;
            }
            Logger.m14616w("AuthenticationParameters:containsScheme", "Null/blank potential scheme token");
            throw new ResourceAuthenticationChallengeException(AuthenticationParameters.AUTH_HEADER_INVALID_FORMAT);
        }

        private static List<String> extractTokensContainingScheme(String[] strArr) throws ResourceAuthenticationChallengeException {
            ArrayList arrayList = new ArrayList();
            for (String str : strArr) {
                if (containsScheme(str)) {
                    arrayList.add(str);
                }
            }
            return arrayList;
        }

        static Challenge parseChallenge(String str) throws ResourceAuthenticationChallengeException {
            if (!StringExtensions.isNullOrBlank(str)) {
                String parseScheme = parseScheme(str);
                Logger.m14612i("AuthenticationParameters:parseChallenge", "Parsing scheme", "Scheme value [" + parseScheme + "]");
                Logger.m14612i("AuthenticationParameters:parseChallenge", "Removing scheme from source challenge", "[" + str + "]");
                Logger.m14614v("AuthenticationParameters:parseChallenge", "Parsing challenge substr. Total length: " + str.length() + " Scheme index: " + parseScheme.length() + 1);
                return new Challenge(parseScheme, parseParams(str.substring(parseScheme.length() + 1)));
            }
            Logger.m14616w("AuthenticationParameters:parseChallenge", "Cannot parse null/empty challenge.");
            throw new ResourceAuthenticationChallengeException(AuthenticationParameters.AUTH_HEADER_MISSING);
        }

        static List<Challenge> parseChallenges(String str) throws ResourceAuthenticationChallengeException {
            if (!StringExtensions.isNullOrBlank(str)) {
                ArrayList arrayList = new ArrayList();
                try {
                    Logger.m14612i("AuthenticationParameters:parseChallenges", "Separating challenges...", "input[" + str + "]");
                    for (String str2 : separateChallenges(str)) {
                        arrayList.add(parseChallenge(str2));
                    }
                    return arrayList;
                } catch (ResourceAuthenticationChallengeException e) {
                    Logger.m14617w("AuthenticationParameters:parseChallenges", "Encountered error during parsing...", e.getMessage(), null);
                    throw e;
                } catch (Exception e2) {
                    Logger.m14617w("AuthenticationParameters:parseChallenges", "Encountered error during parsing...", e2.getMessage(), null);
                    throw new ResourceAuthenticationChallengeException(AuthenticationParameters.AUTH_HEADER_INVALID_FORMAT);
                }
            }
            Logger.m14616w("AuthenticationParameters:parseChallenges", "Cannot parse empty/blank challenges.");
            throw new ResourceAuthenticationChallengeException(AuthenticationParameters.AUTH_HEADER_MISSING);
        }

        private static Map<String, String> parseParams(String str) throws ResourceAuthenticationChallengeException {
            if (!StringExtensions.isNullOrBlank(str)) {
                HashMap hashMap = new HashMap();
                Logger.m14612i("AuthenticationParameters:parseParams", "Splitting on unquoted commas...", "in-value [" + str + "]");
                int i = -1;
                String[] split = str.split(REGEX_SPLIT_UNQUOTED_COMMA, -1);
                Logger.m14612i("AuthenticationParameters:parseParams", "Splitting on unquoted commas...", "out-value [" + Arrays.toString(split) + "]");
                int length = split.length;
                char c = 0;
                int i2 = 0;
                while (i2 < length) {
                    String str2 = split[i2];
                    Logger.m14612i("AuthenticationParameters:parseParams", "Splitting on unquoted equals...", "in-value [" + str2 + "]");
                    String[] split2 = str2.split(REGEX_SPLIT_UNQUOTED_EQUALS, i);
                    Logger.m14612i("AuthenticationParameters:parseParams", "Splitting on unquoted equals...", "out-value [" + Arrays.toString(split2) + "]");
                    if (split2.length == 2) {
                        Logger.m14614v("AuthenticationParameters:parseParams", "Trimming split-string whitespace");
                        String trim = split2[c].trim();
                        String trim2 = split2[1].trim();
                        Logger.m14612i("AuthenticationParameters:parseParams", "", "key[" + trim + "]");
                        Logger.m14612i("AuthenticationParameters:parseParams", "", "value[" + trim2 + "]");
                        if (hashMap.containsKey(trim)) {
                            Logger.m14617w(AuthenticationParameters.TAG, "Key/value pair list contains redundant key. ", "Redundant key: " + trim, ADALError.DEVELOPER_BEARER_HEADER_MULTIPLE_ITEMS);
                        }
                        Logger.m14612i("AuthenticationParameters:parseParams", "", "put(" + trim + SUFFIX_COMMA + trim2 + ")");
                        hashMap.put(trim, trim2);
                        i2++;
                        c = 0;
                        i = -1;
                    } else {
                        Logger.m14616w("AuthenticationParameters:parseParams", "Splitting on equals yielded mismatched key/value.");
                        throw new ResourceAuthenticationChallengeException(AuthenticationParameters.AUTH_HEADER_INVALID_FORMAT);
                    }
                }
                if (hashMap.isEmpty()) {
                    Logger.m14616w("AuthenticationParameters:parseParams", "Parsed params were empty.");
                    throw new ResourceAuthenticationChallengeException(AuthenticationParameters.AUTH_HEADER_INVALID_FORMAT);
                }
                return hashMap;
            }
            Logger.m14616w("AuthenticationParameters:parseParams", "ChallengeSansScheme was null/empty");
            throw new ResourceAuthenticationChallengeException(AuthenticationParameters.AUTH_HEADER_INVALID_FORMAT);
        }

        private static String parseScheme(String str) throws ResourceAuthenticationChallengeException {
            if (!StringExtensions.isNullOrBlank(str)) {
                int indexOf = str.indexOf(32);
                int indexOf2 = str.indexOf(9);
                if (indexOf >= 0 || indexOf2 >= 0) {
                    Logger.m14614v("AuthenticationParameters:parseScheme", "Parsing scheme with indices: indexOfFirstSpace[" + indexOf + "] indexOfFirstTab[" + indexOf2 + "]");
                    if (indexOf <= -1 || (indexOf >= indexOf2 && indexOf2 >= 0)) {
                        if (indexOf2 > -1 && (indexOf2 < indexOf || indexOf < 0)) {
                            return str.substring(0, indexOf2);
                        }
                        Logger.m14616w("AuthenticationParameters:parseScheme", "Unexpected/malformed/missing scheme.");
                        throw new ResourceAuthenticationChallengeException(AuthenticationParameters.AUTH_HEADER_INVALID_FORMAT);
                    }
                    return str.substring(0, indexOf);
                }
                Logger.m14616w("AuthenticationParameters:parseScheme", "Couldn't locate space/tab char - returning input String");
                return str;
            }
            Logger.m14616w("AuthenticationParameters:parseScheme", "Cannot parse an empty/blank challenge");
            throw new ResourceAuthenticationChallengeException(AuthenticationParameters.AUTH_HEADER_MISSING);
        }

        private static void sanitizeParsedSuffixes(String[] strArr) {
            for (int i = 0; i < strArr.length; i++) {
                if (strArr[i].endsWith(SUFFIX_COMMA)) {
                    String str = strArr[i];
                    strArr[i] = str.substring(0, str.length() - 2);
                }
            }
        }

        private static void sanitizeWhitespace(String[] strArr) {
            Logger.m14614v("AuthenticationParameters:sanitizeWhitespace", "Sanitizing whitespace");
            for (int i = 0; i < strArr.length; i++) {
                strArr[i] = strArr[i].trim();
            }
        }

        private static List<String> separateChallenges(String str) throws ResourceAuthenticationChallengeException {
            if (!StringExtensions.isNullOrBlank(str)) {
                Logger.m14612i("AuthenticationParameters:separateChallenges", "Splitting input String on unquoted commas", "input[" + str + "]");
                String[] split = str.split(REGEX_SPLIT_UNQUOTED_COMMA, -1);
                Logger.m14612i("AuthenticationParameters:separateChallenges", "Splitting input String on unquoted commas", "output[" + Arrays.toString(split) + "]");
                sanitizeWhitespace(split);
                List<String> extractTokensContainingScheme = extractTokensContainingScheme(split);
                int size = extractTokensContainingScheme.size();
                String[] strArr = new String[size];
                for (int i = 0; i < size; i++) {
                    strArr[i] = "";
                }
                writeParsedChallenges(split, extractTokensContainingScheme, strArr);
                sanitizeParsedSuffixes(strArr);
                return Arrays.asList(strArr);
            }
            Logger.m14616w("AuthenticationParameters:separateChallenges", "Input String was null");
            throw new ResourceAuthenticationChallengeException(AuthenticationParameters.AUTH_HEADER_INVALID_FORMAT);
        }

        private static void writeParsedChallenges(String[] strArr, List<String> list, String[] strArr2) {
            int i = -1;
            for (String str : strArr) {
                if (list.contains(str)) {
                    i++;
                    strArr2[i] = str + SUFFIX_COMMA;
                } else {
                    strArr2[i] = strArr2[i] + str + SUFFIX_COMMA;
                }
            }
        }

        public Map<String, String> getParameters() {
            return this.mParameters;
        }

        public String getScheme() {
            return this.mScheme;
        }
    }

    public AuthenticationParameters() {
    }

    AuthenticationParameters(String str, String str2) {
        this.mAuthority = str;
        this.mResource = str2;
    }

    public static void createFromResourceUrl(Context context, final URL url, final AuthenticationParamCallback authenticationParamCallback) {
        if (authenticationParamCallback != null) {
            Logger.m14614v(TAG, "createFromResourceUrl");
            final Handler handler = new Handler(context.getMainLooper());
            sThreadExecutor.submit(new Runnable() { // from class: com.microsoft.aad.adal.AuthenticationParameters.1
                @Override // java.lang.Runnable
                public void run() {
                    HashMap hashMap = new HashMap();
                    hashMap.put(WebRequestHandler.HEADER_ACCEPT, WebRequestHandler.HEADER_ACCEPT_JSON);
                    try {
                        try {
                            onCompleted(null, AuthenticationParameters.parseResponse(AuthenticationParameters.sWebRequest.sendGet(url, hashMap)));
                        } catch (ResourceAuthenticationChallengeException e) {
                            onCompleted(e, null);
                        }
                    } catch (IOException e2) {
                        onCompleted(e2, null);
                    }
                }

                public void onCompleted(final Exception exc, final AuthenticationParameters authenticationParameters) {
                    handler.post(new Runnable() { // from class: com.microsoft.aad.adal.AuthenticationParameters.1.1
                        @Override // java.lang.Runnable
                        public void run() {
                            authenticationParamCallback.onCompleted(exc, authenticationParameters);
                        }
                    });
                }
            });
            return;
        }
        throw new IllegalArgumentException("callback");
    }

    public static AuthenticationParameters createFromResponseAuthenticateHeader(String str) throws ResourceAuthenticationChallengeException {
        Challenge challenge;
        if (!StringExtensions.isNullOrBlank(str)) {
            Logger.m14614v("AuthenticationParameters:createFromResponseAuthenticateHeader", "Parsing challenges - BEGIN");
            List<Challenge> parseChallenges = Challenge.parseChallenges(str);
            Logger.m14614v("AuthenticationParameters:createFromResponseAuthenticateHeader", "Parsing challenge - END");
            Logger.m14614v("AuthenticationParameters:createFromResponseAuthenticateHeader", "Looking for Bearer challenge.");
            Iterator<Challenge> it = parseChallenges.iterator();
            while (true) {
                if (!it.hasNext()) {
                    challenge = null;
                    break;
                }
                Challenge next = it.next();
                if (BEARER.equalsIgnoreCase(next.getScheme())) {
                    Logger.m14614v("AuthenticationParameters:createFromResponseAuthenticateHeader", "Found Bearer challenge.");
                    challenge = next;
                    break;
                }
            }
            if (challenge != null) {
                Map<String, String> parameters = challenge.getParameters();
                String str2 = parameters.get("authorization_uri");
                String str3 = parameters.get(RESOURCE_KEY);
                Logger.m14612i("AuthenticationParameters:createFromResponseAuthenticateHeader", "Bearer authority", "[" + str2 + "]");
                Logger.m14612i("AuthenticationParameters:createFromResponseAuthenticateHeader", "Bearer resource", "[" + str3 + "]");
                if (!StringExtensions.isNullOrBlank(str2)) {
                    Logger.m14614v("AuthenticationParameters:createFromResponseAuthenticateHeader", "Parsing leading/trailing \"\"'s (authority)");
                    String replaceAll = str2.replaceAll("^\"|\"$", "");
                    Logger.m14612i("AuthenticationParameters:createFromResponseAuthenticateHeader", "Sanitized authority value", "[" + replaceAll + "]");
                    if (!StringExtensions.isNullOrBlank(replaceAll)) {
                        if (!StringExtensions.isNullOrBlank(str3)) {
                            Logger.m14614v("AuthenticationParameters:createFromResponseAuthenticateHeader", "Parsing leading/trailing \"\"'s (resource)");
                            str3 = str3.replaceAll("^\"|\"$", "");
                            Logger.m14612i("AuthenticationParameters:createFromResponseAuthenticateHeader", "Sanitized resource value", "[" + replaceAll + "]");
                        }
                        return new AuthenticationParameters(replaceAll, str3);
                    }
                    Logger.m14616w("AuthenticationParameters:createFromResponseAuthenticateHeader", "Sanitized authority is null/empty.");
                    throw new ResourceAuthenticationChallengeException(AUTH_HEADER_MISSING_AUTHORITY);
                }
                Logger.m14616w("AuthenticationParameters:createFromResponseAuthenticateHeader", "Null/empty authority.");
                throw new ResourceAuthenticationChallengeException(AUTH_HEADER_MISSING_AUTHORITY);
            }
            Logger.m14616w("AuthenticationParameters:createFromResponseAuthenticateHeader", "Did not locate Bearer challenge.");
            throw new ResourceAuthenticationChallengeException(AUTH_HEADER_INVALID_FORMAT);
        }
        Logger.m14616w("AuthenticationParameters:createFromResponseAuthenticateHeader", "authenticateHeader was null/empty.");
        throw new ResourceAuthenticationChallengeException(AUTH_HEADER_MISSING);
    }

    public static AuthenticationParameters parseResponse(HttpWebResponse httpWebResponse) throws ResourceAuthenticationChallengeException {
        List<String> list;
        if (httpWebResponse.getStatusCode() == 401) {
            Map<String, List<String>> responseHeaders = httpWebResponse.getResponseHeaders();
            if (responseHeaders != null && responseHeaders.containsKey("WWW-Authenticate") && (list = responseHeaders.get("WWW-Authenticate")) != null && list.size() > 0) {
                return createFromResponseAuthenticateHeader(list.get(0));
            }
            throw new ResourceAuthenticationChallengeException(AUTH_HEADER_MISSING);
        }
        throw new ResourceAuthenticationChallengeException(AUTH_HEADER_WRONG_STATUS);
    }

    public String getAuthority() {
        return this.mAuthority;
    }

    public String getResource() {
        return this.mResource;
    }
}
