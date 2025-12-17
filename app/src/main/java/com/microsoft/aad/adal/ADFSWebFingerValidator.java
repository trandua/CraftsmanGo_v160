package com.microsoft.aad.adal;

import java.net.URI;
import java.net.URISyntaxException;

/* loaded from: classes3.dex */
final class ADFSWebFingerValidator {
    private static final String TAG = "ADFSWebFingerValidator";
    private static final URI TRUSTED_REALM_REL;

    static {
        try {
            TRUSTED_REALM_REL = new URI("http://schemas.microsoft.com/rel/trusted-realm");
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException(e);
        }
    }

    private ADFSWebFingerValidator() {
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static boolean realmIsTrusted(URI uri, WebFingerMetadata webFingerMetadata) {
        if (uri != null) {
            if (webFingerMetadata != null) {
                Logger.m14615v("ADFSWebFingerValidator:realmIsTrusted", "Verifying trust authority. ", uri.toString() + webFingerMetadata.toString(), null);
                if (webFingerMetadata.getLinks() == null) {
                    return false;
                }
                for (Link link : webFingerMetadata.getLinks()) {
                    try {
                        URI uri2 = new URI(link.getHref());
                        URI uri3 = new URI(link.getRel());
                        if (uri2.getScheme().equalsIgnoreCase(uri.getScheme()) && uri2.getAuthority().equalsIgnoreCase(uri.getAuthority()) && uri3.equals(TRUSTED_REALM_REL)) {
                            return true;
                        }
                    } catch (URISyntaxException unused) {
                    }
                }
                return false;
            }
            throw new IllegalArgumentException("WebFingerMetadata cannot be null");
        }
        throw new IllegalArgumentException("Authority cannot be null");
    }
}
